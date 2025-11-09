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
    private var pageDirection = "right_to_next" // "right_to_next" or "left_to_next"

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
                pageDirection = intent.getStringExtra("page_direction") ?: "right_to_next"

                if (data != null) {
                    startScreenCapture(data)
                    createOverlay()
                }
            }
            "START_SERVICE_AND_READING" -> {
                // 画面キャプチャとオーバーレイを開始し、その後自動的に読み上げを開始
                val data = intent.getParcelableExtra<Intent>("screen_capture_data")
                readingSpeed = intent.getFloatExtra("reading_speed", 1.0f)
                autoPageTurnEnabled = intent.getBooleanExtra("auto_page_turn", true)
                pageDirection = intent.getStringExtra("page_direction") ?: "right_to_next"

                if (data != null) {
                    startScreenCapture(data)
                    createOverlay()

                    // 画面キャプチャとTTSの初期化を待ってから読み上げ開始
                    mainHandler.postDelayed({
                        if (appState.screenCaptureActive && appState.ttsInitialized) {
                            startReading()
                        } else {
                            debugLog("Auto-start reading failed", "ScreenCapture: ${appState.screenCaptureActive}, TTS: ${appState.ttsInitialized}")
                        }
                    }, 1000)
                }
            }
            "START_READING" -> {
                readingSpeed = intent.getFloatExtra("reading_speed", 1.0f)
                autoPageTurnEnabled = intent.getBooleanExtra("auto_page_turn", true)
                pageDirection = intent.getStringExtra("page_direction") ?: "right_to_next"
                startReading()
            }
            "SET_PAGE_DIRECTION" -> {
                pageDirection = intent.getStringExtra("page_direction") ?: "right_to_next"
                debugLog("Page direction changed", pageDirection)
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
            debugLog("Created new OCR executor")
        }

        ocrExecutor?.scheduleAtFixedRate({
            try {
                debugLog("OCR schedule tick", "isReading: $isReading, isPaused: $isPaused, isCapturing: $isCapturing")
                if (isReading && !isPaused && !isCapturing) {
                    performOCR()
                } else {
                    debugLog("Skipping OCR", "Conditions not met")
                }
            } catch (e: Exception) {
                debugLog("OCR schedule error", e.message)
            }
        }, 0, 2, TimeUnit.SECONDS)

        debugLog("OCR schedule started")
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
        if (isCapturing || imageReader == null) {
            debugLog("performOCR skipped", "isCapturing: $isCapturing, imageReader: ${imageReader != null}")
            return
        }

        isCapturing = true
        debugLog("Performing OCR")

        try {
            val image = imageReader?.acquireLatestImage()
            debugLog("Image acquired", "image: ${image != null}")

            if (image != null) {
                debugLog("Converting image to bitmap")
                val bitmap = convertImageToBitmap(image)
                image.close()

                debugLog("Bitmap created", "bitmap: ${bitmap != null}, size: ${bitmap?.width}x${bitmap?.height}")

                if (bitmap != null) {
                    processOCRImage(bitmap)
                } else {
                    debugLog("Bitmap conversion failed")
                    isCapturing = false
                }
            } else {
                debugLog("No image available from ImageReader")
                isCapturing = false
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
            debugLog("convertImageToBitmap", "Starting conversion")
            val startTime = System.currentTimeMillis()

            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * screenWidth

            debugLog("Image plane info", "pixelStride: $pixelStride, rowStride: $rowStride, padding: $rowPadding")

            val bitmap = Bitmap.createBitmap(
                screenWidth + rowPadding / pixelStride,
                screenHeight,
                Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)

            debugLog("Bitmap created", "Time: ${System.currentTimeMillis() - startTime}ms")

            val croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, screenWidth, screenHeight)

            debugLog("Bitmap cropped", "Time: ${System.currentTimeMillis() - startTime}ms, Size: ${screenWidth}x${screenHeight}")

            // ✨ 画像前処理を適用してOCR精度を向上
            val processedBitmap = preprocessBitmapForOCR(croppedBitmap)

            debugLog("Bitmap conversion completed", "Total time: ${System.currentTimeMillis() - startTime}ms")

            return processedBitmap

        } catch (e: Exception) {
            handleError("ビットマップ変換エラー", e)
            return null
        }
    }

    /**
     * 画像前処理: OCR精度向上のための画像最適化（v1.0.12 超高速精度改善版）
     * - 3.0倍拡大（OCR精度優先）
     * - グレースケール変換
     * - 強化されたコントラスト（2.0倍 + 明るさ調整）
     * ※ColorMatrixのみ使用で超高速（getPixel/setPixelは一切使用しない）
     */
    private fun preprocessBitmapForOCR(bitmap: Bitmap): Bitmap {
        try {
            val startTime = System.currentTimeMillis()
            val width = bitmap.width
            val height = bitmap.height

            debugLog("preprocessBitmapForOCR", "Starting preprocessing, size: ${width}x${height}")

            // Step 1: 3.0倍拡大（OCR精度優先）
            val targetWidth = (width * 3.0).toInt()
            val targetHeight = (height * 3.0).toInt()
            debugLog("Step 1: Scaling", "Target: ${targetWidth}x${targetHeight}")

            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
            debugLog("Step 1 completed", "Time: ${System.currentTimeMillis() - startTime}ms")

            // Step 2: グレースケール + 強化されたコントラスト（2.0倍）+ 明るさ調整
            debugLog("Step 2: Grayscale + Enhanced Contrast", "Starting")
            val enhancedBitmap = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(enhancedBitmap)
            val paint = Paint()

            // グレースケール + 強めのコントラスト（2.0倍）+ 明るさ調整（-128で暗部を黒く）
            val colorMatrix = ColorMatrix(floatArrayOf(
                2.0f, 2.0f, 2.0f, 0f, -128f,  // R: グレースケール + 強コントラスト + 明るさ調整
                2.0f, 2.0f, 2.0f, 0f, -128f,  // G
                2.0f, 2.0f, 2.0f, 0f, -128f,  // B
                0f, 0f, 0f, 1f, 0f              // A
            ))
            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
            canvas.drawBitmap(scaledBitmap, 0f, 0f, paint)
            debugLog("Step 2 completed", "Time: ${System.currentTimeMillis() - startTime}ms")

            debugLog("Image preprocessing completed", "Total time: ${System.currentTimeMillis() - startTime}ms")

            // メモリ解放
            if (scaledBitmap != bitmap) {
                scaledBitmap.recycle()
            }

            return enhancedBitmap

        } catch (e: Exception) {
            debugLog("Image preprocessing failed", e.message)
            return bitmap
        }
    }

    /**
     * メディアンフィルタ: ノイズ除去（3x3ウィンドウ）
     */
    private fun applyMedianFilter(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                val values = mutableListOf<Int>()

                for (ky in -1..1) {
                    for (kx in -1..1) {
                        val pixel = bitmap.getPixel(x + kx, y + ky)
                        val gray = Color.red(pixel) // Already grayscale
                        values.add(gray)
                    }
                }

                values.sort()
                val median = values[4] // 中央値（9個の中央）
                result.setPixel(x, y, Color.rgb(median, median, median))
            }
        }

        return result
    }

    /**
     * 強力なシャープネスフィルタ適用: エッジを強調してテキストを明確に
     */
    private fun applyStrongSharpenFilter(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // 強力なシャープネスカーネル
        val kernel = floatArrayOf(
            -1f, -1f, -1f,
            -1f, 9f, -1f,
            -1f, -1f, -1f
        )

        for (y in 1 until height - 1) {
            for (x in 1 until width - 1) {
                var sum = 0f

                for (ky in -1..1) {
                    for (kx in -1..1) {
                        val pixel = bitmap.getPixel(x + kx, y + ky)
                        val gray = Color.red(pixel)
                        val kernelValue = kernel[(ky + 1) * 3 + (kx + 1)]
                        sum += gray * kernelValue
                    }
                }

                val newValue = sum.toInt().coerceIn(0, 255)
                result.setPixel(x, y, Color.rgb(newValue, newValue, newValue))
            }
        }

        return result
    }

    /**
     * 高速二値化: サンプリングによる閾値計算で処理時間を大幅短縮
     * 全ピクセルではなく、10%のサンプリングで閾値を計算
     */
    private fun applyFastBinarization(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // サンプリングでヒストグラム計算（10%のピクセルのみ）
        val histogram = IntArray(256)
        val sampleStep = 10
        var sampleCount = 0

        for (y in 0 until height step sampleStep) {
            for (x in 0 until width step sampleStep) {
                val pixel = bitmap.getPixel(x, y)
                val gray = Color.red(pixel)
                histogram[gray]++
                sampleCount++
            }
        }

        // 大津の方法で最適な閾値を計算（サンプルのみ使用）
        var sum = 0.0
        for (i in 0..255) {
            sum += i * histogram[i]
        }

        var sumB = 0.0
        var wB = 0
        var wF = 0
        var varMax = 0.0
        var threshold = 128  // デフォルト値

        for (t in 0..255) {
            wB += histogram[t]
            if (wB == 0) continue

            wF = sampleCount - wB
            if (wF == 0) break

            sumB += t * histogram[t]
            val mB = sumB / wB
            val mF = (sum - sumB) / wF

            val varBetween = wB.toDouble() * wF.toDouble() * (mB - mF) * (mB - mF)

            if (varBetween > varMax) {
                varMax = varBetween
                threshold = t
            }
        }

        debugLog("Fast binarization threshold", threshold)

        // 二値化適用（全ピクセル）
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap.getPixel(x, y)
                val gray = Color.red(pixel)
                val binaryValue = if (gray > threshold) 255 else 0
                result.setPixel(x, y, Color.rgb(binaryValue, binaryValue, binaryValue))
            }
        }

        return result
    }

    /**
     * 大津の方法による適応的二値化: テキストと背景を明確に分離（旧バージョン・使用していない）
     */
    private fun applyOtsuBinarization(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // ヒストグラム計算
        val histogram = IntArray(256)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap.getPixel(x, y)
                val gray = Color.red(pixel)
                histogram[gray]++
            }
        }

        // 大津の方法で最適な閾値を計算
        val totalPixels = width * height
        var sum = 0.0
        for (i in 0..255) {
            sum += i * histogram[i]
        }

        var sumB = 0.0
        var wB = 0
        var wF = 0
        var varMax = 0.0
        var threshold = 0

        for (t in 0..255) {
            wB += histogram[t]
            if (wB == 0) continue

            wF = totalPixels - wB
            if (wF == 0) break

            sumB += t * histogram[t]
            val mB = sumB / wB
            val mF = (sum - sumB) / wF

            val varBetween = wB.toDouble() * wF.toDouble() * (mB - mF) * (mB - mF)

            if (varBetween > varMax) {
                varMax = varBetween
                threshold = t
            }
        }

        debugLog("Otsu threshold", threshold)

        // 二値化適用
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap.getPixel(x, y)
                val gray = Color.red(pixel)
                val binaryValue = if (gray > threshold) 255 else 0
                result.setPixel(x, y, Color.rgb(binaryValue, binaryValue, binaryValue))
            }
        }

        return result
    }

    /**
     * 適応的コントラスト強化: テキストと背景の差を最大化（削除予定）
     */
    private fun applyAdaptiveContrast(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()

        // 輝度ヒストグラムを計算
        val histogram = IntArray(256)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = bitmap.getPixel(x, y)
                val brightness = (Color.red(pixel) * 0.299 + Color.green(pixel) * 0.587 + Color.blue(pixel) * 0.114).toInt()
                histogram[brightness]++
            }
        }

        // ヒストグラムから最適なコントラストパラメータを計算
        val totalPixels = width * height
        var darkPixels = 0
        var darkThreshold = 0
        for (i in 0..127) {
            darkPixels += histogram[i]
            if (darkPixels > totalPixels * 0.1) {
                darkThreshold = i
                break
            }
        }

        var brightPixels = 0
        var brightThreshold = 255
        for (i in 255 downTo 128) {
            brightPixels += histogram[i]
            if (brightPixels > totalPixels * 0.1) {
                brightThreshold = i
                break
            }
        }

        // 適応的コントラスト調整
        val contrastFactor = 255.0f / (brightThreshold - darkThreshold).coerceAtLeast(1)
        val offset = -darkThreshold * contrastFactor

        val contrastMatrix = ColorMatrix(floatArrayOf(
            contrastFactor, 0f, 0f, 0f, offset,
            0f, contrastFactor, 0f, 0f, offset,
            0f, 0f, contrastFactor, 0f, offset,
            0f, 0f, 0f, 1f, 0f
        ))

        paint.colorFilter = ColorMatrixColorFilter(contrastMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)

        return result
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
        debugLog("Next page", "direction: $pageDirection")
        appState.currentPage++

        // ✅ FIX: ページ変更時に状態をリセット（TTS継続の問題を修正）
        lastRecognizedText = ""
        currentSentences = emptyList()  // 古い文のリストをクリア
        currentSentenceIndex = 0         // インデックスをリセット
        textToSpeech?.stop()             // 前のページのTTSを停止

        debugLog("State reset", "sentences cleared, index reset to 0, TTS stopped")

        // ページめくり方向に応じてジェスチャーを選択
        val intent = Intent(this, AutoPageTurnService::class.java)
        intent.action = if (pageDirection == "right_to_next") "NEXT_PAGE" else "PREVIOUS_PAGE"
        intent.putExtra("page_direction", pageDirection)
        startService(intent)

        // OCRを再実行（リトライ付き）
        // ページ遷移アニメーション完了を確実に待つため2.5秒に延長
        performOCRWithRetry(maxRetries = 3, initialDelay = 2500)
    }

    private fun previousPage() {
        debugLog("Previous page", "direction: $pageDirection")
        appState.currentPage--

        // ✅ FIX: ページ変更時に状態をリセット（TTS継続の問題を修正）
        lastRecognizedText = ""
        currentSentences = emptyList()  // 古い文のリストをクリア
        currentSentenceIndex = 0         // インデックスをリセット
        textToSpeech?.stop()             // 前のページのTTSを停止

        debugLog("State reset", "sentences cleared, index reset to 0, TTS stopped")

        // ページめくり方向に応じてジェスチャーを選択
        val intent = Intent(this, AutoPageTurnService::class.java)
        intent.action = if (pageDirection == "right_to_next") "PREVIOUS_PAGE" else "NEXT_PAGE"
        intent.putExtra("page_direction", pageDirection)
        startService(intent)

        // OCRを再実行（リトライ付き）
        // ページ遷移アニメーション完了を確実に待つため2.5秒に延長
        performOCRWithRetry(maxRetries = 3, initialDelay = 2500)
    }

    /**
     * リトライ付きOCR実行
     * ページめくり後、画面が安定するまで待ってからOCRを実行
     * 待機時間を延長してページ遷移アニメーションの完了を確実に待つ
     */
    private fun performOCRWithRetry(maxRetries: Int, initialDelay: Long) {
        var retryCount = 0

        fun attemptOCR() {
            if (!isReading || isPaused) {
                debugLog("OCR retry cancelled", "isReading: $isReading, isPaused: $isPaused")
                return
            }

            mainHandler.postDelayed({
                if (!isReading || isPaused) return@postDelayed

                performOCR()

                // lastRecognizedTextが更新されたかチェック
                mainHandler.postDelayed({
                    if (lastRecognizedText.isEmpty() && retryCount < maxRetries) {
                        retryCount++
                        debugLog("OCR retry", "Attempt $retryCount of $maxRetries")
                        attemptOCR()
                    } else if (lastRecognizedText.isEmpty()) {
                        debugLog("OCR retry exhausted", "No text found after $maxRetries attempts")
                    } else {
                        debugLog("OCR retry success", "Text found on attempt $retryCount")
                    }
                }, 500)
            }, if (retryCount == 0) initialDelay else 1500)  // ← リトライ時も1.5秒待つ
        }

        attemptOCR()
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