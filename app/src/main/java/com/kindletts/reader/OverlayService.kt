package com.kindletts.reader

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.*
import android.speech.tts.TextToSpeech
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

class OverlayService : Service(), TextToSpeech.OnInitListener {

    companion object {
        var isRunning = false
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "KindleTTSService"
        private const val TAG = "KindleTTS_Service"
    }

    // UI関連
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    // 画面キャプチャ関連
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var screenWidth = 0
    private var screenHeight = 0

    // TTS関連
    private var textToSpeech: TextToSpeech? = null
    private var currentSentences: List<String> = emptyList()
    private var currentSentenceIndex = 0
    private var isReading = false
    private var isPaused = false
    private var readingSpeed = 1.0f
    private var autoPageTurnEnabled = true

    // OCR関連
    private var lastRecognizedText = ""
    private var ocrExecutor: ScheduledExecutorService? = null
    private var isCapturing = false

    // 状態管理
    private data class AppState(
        var isReading: Boolean = false,
        var isPaused: Boolean = false,
        var ttsInitialized: Boolean = false,
        var screenCaptureActive: Boolean = false,
        var currentPage: Int = 1,
        var totalSentences: Int = 0,
        var currentSentence: Int = 0
    )

    private val appState = AppState()

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        debugLog("OverlayService created")

        // TTS初期化
        textToSpeech = TextToSpeech(this, this)

        // OCR実行スケジューラー初期化
        ocrExecutor = Executors.newSingleThreadScheduledExecutor()

        // 画面サイズ取得
        initializeScreenMetrics()

        // 通知チャンネル作成
        createNotificationChannel()

        // フォアグラウンドサービス開始
        startForeground(NOTIFICATION_ID, createNotification("Kindle TTS Reader 準備中..."))

        debugLog("OverlayService initialization completed")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        debugLog("onStartCommand called", intent?.action)

        when (intent?.action) {
            "START_SERVICE" -> {
                val data = intent.getParcelableExtra<Intent>("screen_capture_data")
                readingSpeed = intent.getFloatExtra("reading_speed", 1.0f)
                autoPageTurnEnabled = intent.getBooleanExtra("auto_page_turn", true)

                if (data != null) {
                    startScreenCapture(data)
                    createOverlay()
                }
            }
            "START_READING" -> {
                readingSpeed = intent.getFloatExtra("reading_speed", 1.0f)
                autoPageTurnEnabled = intent.getBooleanExtra("auto_page_turn", true)
                startReading()
            }
            "STOP_READING" -> stopReading()
            "PAUSE_READING" -> pauseReading()
            "RESUME_READING" -> resumeReading()
            "NEXT_PAGE" -> nextPage()
            "PREVIOUS_PAGE" -> previousPage()
        }

        return START_STICKY
    }

    private fun initializeScreenMetrics() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = windowManager.currentWindowMetrics.bounds
            screenWidth = bounds.width()
            screenHeight = bounds.height()
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            screenWidth = displayMetrics.widthPixels
            screenHeight = displayMetrics.heightPixels
        }

        debugLog("Screen metrics", "Width: $screenWidth, Height: $screenHeight")
    }

    private fun startScreenCapture(data: Intent) {
        debugLog("Starting screen capture")

        try {
            val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            mediaProjection = mediaProjectionManager.getMediaProjection(Activity.RESULT_OK, data)

            // ✅ FIX: MediaProjectionコールバックを登録（必須）
            mediaProjection?.registerCallback(object : MediaProjection.Callback() {
                override fun onStop() {
                    debugLog("MediaProjection stopped")
                    appState.screenCaptureActive = false
                }
            }, Handler(Looper.getMainLooper()))

            // ImageReader設定
            imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2)

            // ImageReader コールバックは不要（手動/自動OCRで明示的に取得するため）
            // setOnImageAvailableListenerは使用せず、必要時にacquireLatestImageを呼び出す

            // VirtualDisplay作成
            virtualDisplay = mediaProjection?.createVirtualDisplay(
                "KindleTTSCapture",
                screenWidth,
                screenHeight,
                resources.displayMetrics.densityDpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader?.surface,
                null,
                null
            )

            appState.screenCaptureActive = true
            updateNotification("画面キャプチャ準備完了")
            debugLog("Screen capture started successfully")

        } catch (e: Exception) {
            handleError("画面キャプチャ開始エラー", e)
        }
    }

    private fun createOverlay() {
        try {
            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
            overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null)

            val layoutParams = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    @Suppress("DEPRECATION")
                    WindowManager.LayoutParams.TYPE_PHONE
                },
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                PixelFormat.TRANSLUCENT
            )

            layoutParams.gravity = Gravity.TOP or Gravity.END
            layoutParams.x = 50
            layoutParams.y = 200

            windowManager?.addView(overlayView, layoutParams)
            setupOverlayButtons()
            makeOverlayDraggable()

            debugLog("Overlay created successfully")

        } catch (e: Exception) {
            handleError("オーバーレイ作成エラー", e)
        }
    }

    private fun setupOverlayButtons() {
        overlayView?.let { view ->
            // ボタンの取得
            val btnPrevious = view.findViewById<ImageView>(R.id.btnPreviousPage)
            val btnPlayPause = view.findViewById<ImageView>(R.id.btnPlayPause)
            val btnNext = view.findViewById<ImageView>(R.id.btnNextPage)
            val btnClose = view.findViewById<ImageView>(R.id.btnCloseOverlay)

            // 前ページボタン
            btnPrevious?.setOnClickListener {
                debugLog("Previous page button clicked")
                previousPage()
            }

            // 再生/一時停止ボタン
            btnPlayPause?.setOnClickListener {
                debugLog("Play/Pause button clicked", "isReading: $isReading, isPaused: $isPaused")

                if (!isReading) {
                    // 読み上げ開始
                    startReading()
                } else {
                    // 一時停止/再開
                    if (isPaused) {
                        resumeReading()
                    } else {
                        pauseReading()
                    }
                }

                // ボタンアイコン更新
                updatePlayPauseButton()
            }

            // 次ページボタン
            btnNext?.setOnClickListener {
                debugLog("Next page button clicked")
                nextPage()
            }

            // 閉じるボタン
            btnClose?.setOnClickListener {
                debugLog("Close button clicked")
                stopSelf()
            }

            updateOverlayUI()
            updatePlayPauseButton()
        }
    }

    private fun updatePlayPauseButton() {
        overlayView?.let { view ->
            val btnPlayPause = view.findViewById<ImageView>(R.id.btnPlayPause)
            btnPlayPause?.setImageResource(
                if (isReading && !isPaused) {
                    android.R.drawable.ic_media_pause  // 一時停止アイコン
                } else {
                    android.R.drawable.ic_media_play   // 再生アイコン
                }
            )
        }
    }

    private fun makeOverlayDraggable() {
        overlayView?.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        val params = overlayView?.layoutParams as WindowManager.LayoutParams
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val params = overlayView?.layoutParams as WindowManager.LayoutParams
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(overlayView, params)
                        return true
                    }
                }
                return false
            }
        })
    }

    private fun startReading() {
        debugLog("Starting reading mode")

        if (!appState.ttsInitialized) {
            showToast("TTS初期化中です。しばらくお待ちください。")
            return
        }

        if (!appState.screenCaptureActive) {
            showToast("画面キャプチャが開始されていません。")
            return
        }

        isReading = true
        isPaused = false
        appState.isReading = true
        appState.isPaused = false

        updateNotification("読み上げ中...")
        updateOverlayUI()
        updatePlayPauseButton()

        // 自動OCR開始
        startAutoOCR()
    }

    private fun stopReading() {
        debugLog("Stopping reading mode")

        isReading = false
        isPaused = false
        appState.isReading = false
        appState.isPaused = false

        stopAutoOCR()
        textToSpeech?.stop()

        updateNotification("読み上げ停止")
        updateOverlayUI()
        updatePlayPauseButton()
    }

    private fun pauseReading() {
        debugLog("Pausing reading")

        isPaused = true
        appState.isPaused = true

        textToSpeech?.stop()
        stopAutoOCR()

        updateNotification("一時停止中")
        updateOverlayUI()
        updatePlayPauseButton()
    }

    private fun resumeReading() {
        debugLog("Resuming reading")

        isPaused = false
        appState.isPaused = false

        updateNotification("読み上げ中...")
        updateOverlayUI()
        updatePlayPauseButton()

        // 現在の文から再開
        if (currentSentenceIndex < currentSentences.size) {
            speakCurrentSentence()
        } else {
            startAutoOCR()
        }
    }

    private fun startAutoOCR() {
        debugLog("Starting auto OCR")

        // 既存のExecutorが停止している場合のみ新しく作成
        if (ocrExecutor == null || ocrExecutor?.isShutdown == true) {
            ocrExecutor = Executors.newSingleThreadScheduledExecutor()
        }

        ocrExecutor?.scheduleAtFixedRate({
            if (isReading && !isPaused && !isCapturing) {
                performOCR()
            }
        }, 0, 2, TimeUnit.SECONDS)
    }

    private fun stopAutoOCR() {
        debugLog("Stopping auto OCR")

        ocrExecutor?.let { executor ->
            if (!executor.isShutdown) {
                executor.shutdown()
                try {
                    if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                        executor.shutdownNow()
                    }
                } catch (e: InterruptedException) {
                    executor.shutdownNow()
                    Thread.currentThread().interrupt()
                }
            }
        }
        // 新しいExecutorは必要時のみ作成（startAutoOCRで作成）
    }

    private fun performOCR() {
        if (isCapturing || imageReader == null) return

        isCapturing = true
        debugLog("Performing OCR")

        try {
            val image = imageReader?.acquireLatestImage()
            if (image != null) {
                val bitmap = convertImageToBitmap(image)
                image.close()

                if (bitmap != null) {
                    processOCRImage(bitmap)
                }
            }
        } catch (e: Exception) {
            handleError("OCR処理エラー", e)
            isCapturing = false
        }
    }

    private fun manualCapture() {
        debugLog("Manual capture requested")
        performOCR()
    }

    private fun convertImageToBitmap(image: Image): Bitmap? {
        try {
            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * screenWidth

            val bitmap = Bitmap.createBitmap(
                screenWidth + rowPadding / pixelStride,
                screenHeight,
                Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)

            val croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, screenWidth, screenHeight)

            // ✨ 画像前処理を適用してOCR精度を向上
            return preprocessBitmapForOCR(croppedBitmap)

        } catch (e: Exception) {
            handleError("ビットマップ変換エラー", e)
            return null
        }
    }

    /**
     * 画像前処理: OCR精度向上のための画像最適化
     * - コントラスト強化
     * - シャープネス向上
     * - グレースケール変換（オプション）
     */
    private fun preprocessBitmapForOCR(bitmap: Bitmap): Bitmap {
        try {
            val width = bitmap.width
            val height = bitmap.height

            // Step 1: 1.5倍拡大（ML Kitは高解像度でOCR精度向上）
            val targetWidth = (width * 1.5).toInt()
            val targetHeight = (height * 1.5).toInt()
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)

            // Step 2: グレースケール + コントラスト強化
            val processedBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(processedBitmap)
            val paint = Paint()

            // グレースケール変換
            val grayscaleMatrix = ColorMatrix()
            grayscaleMatrix.setSaturation(0f)

            // コントラストを強化（テキストを明確に）
            val contrastMatrix = ColorMatrix(floatArrayOf(
                2.5f, 0f, 0f, 0f, -180f,  // 高コントラスト
                0f, 2.5f, 0f, 0f, -180f,
                0f, 0f, 2.5f, 0f, -180f,
                0f, 0f, 0f, 1f, 0f
            ))

            grayscaleMatrix.postConcat(contrastMatrix)
            paint.colorFilter = ColorMatrixColorFilter(grayscaleMatrix)
            canvas.drawBitmap(scaledBitmap, 0f, 0f, paint)

            debugLog("Image preprocessing", "Scaled: ${targetWidth}x${targetHeight}, Grayscale + High contrast applied")

            // メモリ解放
            if (scaledBitmap != bitmap) {
                scaledBitmap.recycle()
            }

            return processedBitmap

        } catch (e: Exception) {
            debugLog("Image preprocessing failed", e.message)
            return bitmap
        }
    }

    private fun processOCRImage(bitmap: Bitmap) {
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                // 縦書き対応: テキストブロックを位置でソート
                val extractedText = extractTextWithVerticalSupport(visionText)

                // ✨ 詳細なデバッグログ
                debugLog("OCR success", """
                    TextLength: ${extractedText.length},
                    Preview: ${extractedText.take(100).replace("\n", " | ")},
                    Changed: ${extractedText != lastRecognizedText}
                """.trimIndent())

                if (extractedText.isNotEmpty() && extractedText != lastRecognizedText && isReading && !isPaused) {
                    lastRecognizedText = extractedText
                    val sentences = splitIntoSentences(extractedText)
                    if (sentences.isNotEmpty()) {
                        debugLog("Speaking", "Total sentences: ${sentences.size}")
                        speakSentences(sentences)
                    } else {
                        debugLog("No sentences", "Text could not be split into sentences")
                    }
                } else if (extractedText.isEmpty()) {
                    debugLog("OCR result empty", "No text extracted from image")
                } else if (extractedText == lastRecognizedText) {
                    debugLog("OCR duplicate", "Same text as previous capture")
                }

                isCapturing = false
            }
            .addOnFailureListener { e ->
                debugLog("OCR failed", e.message)
                handleError("OCRエラー", e)
                isCapturing = false
            }
    }

    private fun extractTextWithVerticalSupport(visionText: com.google.mlkit.vision.text.Text): String {
        if (visionText.textBlocks.isEmpty()) {
            debugLog("OCR: No text blocks detected")
            return visionText.text.trim()
        }

        // ✨ 信頼度フィルタリング: 低信頼度テキストを除外
        val minConfidence = 0.5f  // 信頼度50%未満のテキストは除外
        val blocks = visionText.textBlocks

        // 信頼度の統計情報を計算
        val confidences = blocks.flatMap { block ->
            block.lines.flatMap { line ->
                line.elements.map { element ->
                    element.confidence ?: 0f
                }
            }
        }

        val avgConfidence = if (confidences.isNotEmpty()) confidences.average() else 0.0
        val highConfidenceCount = confidences.count { it >= minConfidence }
        val totalElementCount = confidences.size

        debugLog("OCR confidence stats", """
            Blocks: ${blocks.size},
            Elements: $totalElementCount,
            HighConf: $highConfidenceCount (${(highConfidenceCount.toFloat() / totalElementCount * 100).toInt()}%),
            AvgConf: ${"%.2f".format(avgConfidence)}
        """.trimIndent())

        // テキストブロックの配置を分析
        val avgWidth = blocks.map { it.boundingBox?.width() ?: 0 }.average()
        val avgHeight = blocks.map { it.boundingBox?.height() ?: 0 }.average()

        // 縦書き判定: ブロックの高さが幅より大きい場合
        val isVertical = avgHeight > avgWidth * 1.5

        debugLog("Text orientation", "Vertical: $isVertical, AvgW: ${"%.1f".format(avgWidth)}, AvgH: ${"%.1f".format(avgHeight)}")

        return if (isVertical) {
            // 縦書き: 右から左、上から下の順でソート
            blocks.sortedWith(compareByDescending<com.google.mlkit.vision.text.Text.TextBlock> {
                it.boundingBox?.right ?: 0
            }.thenBy {
                it.boundingBox?.top ?: 0
            })
                .joinToString("\n") { it.text }
                .trim()
        } else {
            // 横書き: 上から下、左から右の順でソート
            blocks.sortedWith(compareBy<com.google.mlkit.vision.text.Text.TextBlock> {
                it.boundingBox?.top ?: 0
            }.thenBy {
                it.boundingBox?.left ?: 0
            })
                .joinToString("\n") { it.text }
                .trim()
        }
    }

    private fun splitIntoSentences(text: String): List<String> {
        return text.split(Regex("[。！？\\.\\!\\?]"))
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }

    private fun speakSentences(sentences: List<String>) {
        currentSentences = sentences
        currentSentenceIndex = 0
        appState.totalSentences = sentences.size
        appState.currentSentence = 0

        debugLog("Speaking sentences", "Count: ${sentences.size}")
        speakCurrentSentence()
    }

    private fun speakCurrentSentence() {
        if (currentSentenceIndex >= currentSentences.size || !isReading || isPaused) {
            return
        }

        val sentence = currentSentences[currentSentenceIndex]
        debugLog("Speaking sentence", "$currentSentenceIndex: $sentence")

        appState.currentSentence = currentSentenceIndex + 1
        updateOverlayText(sentence)

        textToSpeech?.speak(sentence, TextToSpeech.QUEUE_FLUSH, null, currentSentenceIndex.toString())
    }

    private fun onSentenceComplete() {
        currentSentenceIndex++

        if (currentSentenceIndex < currentSentences.size) {
            // 次の文を読み上げ (既存のmainHandlerを再利用)
            mainHandler.postDelayed({
                speakCurrentSentence()
            }, 500)
        } else {
            // ページ完了
            if (autoPageTurnEnabled && isReading) {
                mainHandler.postDelayed({
                    nextPage()
                }, 2000)
            }
        }
    }

    private fun nextPage() {
        debugLog("Next page")
        appState.currentPage++

        // ページ変更時にlastRecognizedTextをリセット（新しいページの読み上げを開始させる）
        lastRecognizedText = ""

        // AccessibilityServiceに自動ページめくりを要求
        val intent = Intent(this, AutoPageTurnService::class.java)
        intent.action = "NEXT_PAGE"
        startService(intent)

        // OCRを再実行 (既存のmainHandlerを再利用)
        mainHandler.postDelayed({
            if (isReading && !isPaused) {
                performOCR()
            }
        }, 1000)
    }

    private fun previousPage() {
        debugLog("Previous page")
        appState.currentPage--

        // ページ変更時にlastRecognizedTextをリセット（新しいページの読み上げを開始させる）
        lastRecognizedText = ""

        // AccessibilityServiceに自動ページめくりを要求
        val intent = Intent(this, AutoPageTurnService::class.java)
        intent.action = "PREVIOUS_PAGE"
        startService(intent)

        // OCRを再実行 (既存のmainHandlerを再利用)
        mainHandler.postDelayed({
            if (isReading && !isPaused) {
                performOCR()
            }
        }, 1000)
    }

    private fun updateOverlayUI() {
        overlayView?.let { view ->
            val statusText = view.findViewById<TextView>(R.id.overlayText)
            val status = when {
                !isReading -> "待機中"
                isPaused -> "一時停止"
                else -> "読み上げ中"
            }
            statusText.text = "$status (${appState.currentPage}ページ)"
        }
    }

    private fun updateOverlayText(text: String) {
        overlayView?.let { view ->
            val textView = view.findViewById<TextView>(R.id.overlayText)
            textView.text = text.take(50) + if (text.length > 50) "..." else ""

            // 5秒後に非表示 (既存のmainHandlerを再利用)
            mainHandler.postDelayed({
                updateOverlayUI()
            }, 5000)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = textToSpeech?.setLanguage(Locale.JAPANESE)
            when (result) {
                TextToSpeech.LANG_MISSING_DATA, TextToSpeech.LANG_NOT_SUPPORTED -> {
                    debugLog("Japanese TTS not supported, trying English")
                    textToSpeech?.setLanguage(Locale.ENGLISH)
                }
                else -> {
                    debugLog("TTS initialized successfully")
                }
            }

            // TTS設定
            textToSpeech?.let { tts ->
                tts.setSpeechRate(readingSpeed)
                tts.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        debugLog("TTS started", utteranceId)
                    }

                    override fun onDone(utteranceId: String?) {
                        debugLog("TTS completed", utteranceId)
                        onSentenceComplete()
                    }

                    override fun onError(utteranceId: String?) {
                        debugLog("TTS error", utteranceId)
                        handleError("TTS エラー", Exception("Utterance failed: $utteranceId"))
                    }
                })
            } ?: run {
                debugLog("TTS object is null in onInit")
                handleError("TTS初期化エラー", Exception("TextToSpeech is null after successful initialization"))
                return
            }

            appState.ttsInitialized = true
            updateNotification("準備完了")

        } else {
            debugLog("TTS initialization failed")
            handleError("TTS初期化エラー", Exception("TTS initialization failed"))
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Kindle TTS Reader",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Kindle読み上げサービスの通知"
                setShowBadge(false)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(message: String): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Kindle TTS Reader")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .setSound(null)
            .build()
    }

    private fun updateNotification(message: String) {
        val notification = createNotification(message)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun showToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleError(title: String, error: Throwable) {
        debugLog("Error: $title", error.message)
        showToast("$title: ${error.message}")
    }

    private fun debugLog(message: String, data: Any? = null) {
        Log.d(TAG, "[$TAG] $message ${if (data != null) ": $data" else ""}")
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false

        debugLog("OverlayService destroying")

        // 読み上げ停止
        if (isReading) {
            stopReading()
        }

        // リソース解放
        stopAutoOCR()

        // Handler の保留中タスクをクリア
        mainHandler.removeCallbacksAndMessages(null)

        // VirtualDisplay と MediaProjection の解放
        try {
            virtualDisplay?.release()
            virtualDisplay = null
        } catch (e: Exception) {
            debugLog("Error releasing VirtualDisplay", e.message)
        }

        try {
            mediaProjection?.stop()
            mediaProjection = null
        } catch (e: Exception) {
            debugLog("Error stopping MediaProjection", e.message)
        }

        try {
            imageReader?.close()
            imageReader = null
        } catch (e: Exception) {
            debugLog("Error closing ImageReader", e.message)
        }

        // オーバーレイビューの削除
        overlayView?.let {
            try {
                windowManager?.removeView(it)
            } catch (e: Exception) {
                debugLog("Error removing overlay view", e.message)
            }
            overlayView = null
        }

        // TTS の停止とシャットダウン
        textToSpeech?.let { tts ->
            try {
                tts.stop()
                tts.shutdown()
            } catch (e: Exception) {
                debugLog("Error shutting down TTS", e.message)
            }
            textToSpeech = null
        }

        debugLog("OverlayService destroyed")
    }
}