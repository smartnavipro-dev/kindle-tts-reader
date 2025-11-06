package com.kindletts.reader

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kindletts.reader.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var binding: ActivityMainBinding
    private var textToSpeech: TextToSpeech? = null
    private lateinit var sharedPreferences: SharedPreferences

    // 状態管理
    private var isReading = false
    private var isPaused = false
    private var currentReadingSpeed = 1.0f
    private var autoPageTurnEnabled = true
    private var pageDirection = "right_to_next" // "right_to_next" or "left_to_next"

    // MediaProjection 関連
    private var mediaProjectionManager: MediaProjectionManager? = null

    // 権限リクエスト
    private val screenCapturePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    startOverlayServiceAndReading(data)
                }
            } else {
                showToast("画面キャプチャ権限が必要です")
            }
        }

    private val overlayPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                debugLog("Overlay permission granted")
                updatePermissionButtonStates()
            } else {
                showToast("オーバーレイ権限が必要です")
            }
        }

    private val accessibilitySettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            checkAccessibilityServiceEnabled()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        debugLog("MainActivity created")

        // SharedPreferences初期化
        sharedPreferences = getSharedPreferences("KindleTTSPrefs", Context.MODE_PRIVATE)

        // TTS初期化
        textToSpeech = TextToSpeech(this, this)

        // MediaProjectionManager初期化
        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        // UI初期化
        setupUI()
        loadSettings()
        updatePermissionButtonStates()
        updateUIState()

        debugLog("MainActivity initialization completed")
    }

    private fun setupUI() {
        // 読み上げコントロール
        binding.btnStartReading.setOnClickListener { toggleReading() }
        binding.btnPauseResume.setOnClickListener { togglePauseResume() }

        // ページコントロール
        binding.btnPrevPage.setOnClickListener { previousPage() }
        binding.btnNextPage.setOnClickListener { nextPage() }

        // 権限ボタン（アクセシビリティのみ表示）
        binding.btnAccessibility.setOnClickListener { openAccessibilitySettings() }

        // 設定コントロール
        binding.speedSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    currentReadingSpeed = (progress + 1) * 0.25f // 0.25倍から2.0倍まで
                    applyTTSSettings()
                    saveSettings()
                    debugLog("Reading speed changed to $currentReadingSpeed")
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        binding.autoPageTurnSwitch.setOnCheckedChangeListener { _, isChecked ->
            autoPageTurnEnabled = isChecked
            saveSettings()
            debugLog("Auto page turn: $autoPageTurnEnabled")
        }

        // ページめくり方向設定
        binding.pageDirectionGroup.setOnCheckedChangeListener { _, checkedId ->
            pageDirection = when (checkedId) {
                R.id.radioLeftToNext -> "left_to_next"
                else -> "right_to_next"
            }
            saveSettings()
            sendPageDirectionToService()
            debugLog("Page direction changed", pageDirection)
        }
    }

    private fun toggleReading() {
        // アクセシビリティ権限のみチェック（画面キャプチャは自動で要求）
        if (!isAccessibilityServiceEnabled()) {
            showPermissionDialog()
            return
        }

        if (isReading) {
            stopReading()
        } else {
            startReading()
        }
    }

    private fun startReading() {
        debugLog("Starting reading mode")

        // 画面キャプチャが開始されていない場合は自動的に要求
        if (!OverlayService.isRunning) {
            debugLog("OverlayService not running, requesting screen capture")
            requestScreenCapturePermission()
            return
        }

        isReading = true
        isPaused = false
        updateUIState()
        updateStatusText("読み上げ中...")

        // OverlayServiceに読み上げ開始を通知
        val intent = Intent(this, OverlayService::class.java)
        intent.action = "START_READING"
        intent.putExtra("reading_speed", currentReadingSpeed)
        intent.putExtra("auto_page_turn", autoPageTurnEnabled)
        intent.putExtra("page_direction", pageDirection)
        startService(intent)
    }

    private fun sendPageDirectionToService() {
        if (OverlayService.isRunning) {
            val intent = Intent(this, OverlayService::class.java)
            intent.action = "SET_PAGE_DIRECTION"
            intent.putExtra("page_direction", pageDirection)
            startService(intent)
        }
    }

    private fun stopReading() {
        debugLog("Stopping reading mode")

        isReading = false
        isPaused = false
        updateUIState()
        updateStatusText("準備完了")

        // OverlayServiceに読み上げ停止を通知
        val intent = Intent(this, OverlayService::class.java)
        intent.action = "STOP_READING"
        startService(intent)

        // TTSも停止
        textToSpeech?.stop()
    }

    private fun togglePauseResume() {
        if (!isReading) return

        if (isPaused) {
            debugLog("Resuming reading")
            isPaused = false
            updateStatusText("読み上げ中...")

            val intent = Intent(this, OverlayService::class.java)
            intent.action = "RESUME_READING"
            startService(intent)
        } else {
            debugLog("Pausing reading")
            isPaused = true
            updateStatusText("一時停止中")

            val intent = Intent(this, OverlayService::class.java)
            intent.action = "PAUSE_READING"
            startService(intent)

            textToSpeech?.stop()
        }

        updateUIState()
    }

    private fun previousPage() {
        debugLog("Previous page requested")

        val intent = Intent(this, OverlayService::class.java)
        intent.action = "PREVIOUS_PAGE"
        startService(intent)
    }

    private fun nextPage() {
        debugLog("Next page requested")

        val intent = Intent(this, OverlayService::class.java)
        intent.action = "NEXT_PAGE"
        startService(intent)
    }

    private fun requestScreenCapturePermission() {
        debugLog("Requesting screen capture permission")

        // オーバーレイ権限が無い場合は先にそれを要求
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            showToast("オーバーレイ権限を先に許可してください")
            requestOverlayPermission()
            return
        }

        // MediaProjection権限ダイアログを表示
        mediaProjectionManager?.let { manager ->
            val captureIntent = manager.createScreenCaptureIntent()
            screenCapturePermissionLauncher.launch(captureIntent)
        }
    }

    private fun requestOverlayPermission() {
        debugLog("Requesting overlay permission")

        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
        }
        overlayPermissionLauncher.launch(intent)
    }

    private fun openAccessibilitySettings() {
        debugLog("Opening accessibility settings")

        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        accessibilitySettingsLauncher.launch(intent)
    }

    private fun startOverlayServiceAndReading(data: Intent) {
        debugLog("Starting overlay service with screen capture data and auto-start reading")

        val intent = Intent(this, OverlayService::class.java)
        intent.action = "START_SERVICE_AND_READING"  // 新しいアクション
        intent.putExtra("screen_capture_data", data)
        intent.putExtra("reading_speed", currentReadingSpeed)
        intent.putExtra("auto_page_turn", autoPageTurnEnabled)
        intent.putExtra("page_direction", pageDirection)
        startService(intent)

        // UI状態を更新
        isReading = true
        isPaused = false

        // OverlayServiceが起動するまで少し待ってからUIを更新
        binding.root.postDelayed({
            updatePermissionButtonStates()
            updateUIState()
            updateStatusText("読み上げ中...")
        }, 500)
    }

    private fun checkAllPermissions(): Boolean {
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) &&
               isAccessibilityServiceEnabled()
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val expectedComponentName = "$packageName/${AutoPageTurnService::class.java.name}"
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: ""

        debugLog("Checking accessibility service - Expected: $expectedComponentName, Enabled: $enabledServices")

        // サービス名は {{...}} で囲まれることがあるため、より柔軟に検索
        return enabledServices.contains(expectedComponentName) ||
               enabledServices.contains("${packageName}/.AutoPageTurnService")
    }

    private fun checkAccessibilityServiceEnabled() {
        val isEnabled = isAccessibilityServiceEnabled()
        debugLog("Accessibility service enabled: $isEnabled")
        updatePermissionButtonStates()
    }

    private fun updatePermissionButtonStates() {
        // アクセシビリティ権限状態のみ表示（画面キャプチャは自動）
        val accessibilityEnabled = isAccessibilityServiceEnabled()
        binding.btnAccessibility.text = if (accessibilityEnabled) "アクセシビリティ権限 ✓" else "アクセシビリティ権限を許可"
        binding.btnAccessibility.isEnabled = !accessibilityEnabled

        debugLog("Permission states - Accessibility: $accessibilityEnabled")
    }

    private fun updateUIState() {
        binding.btnStartReading.text = if (isReading) "読み上げ停止" else "読み上げ開始"
        binding.btnPauseResume.text = if (isPaused) "再開" else "一時停止"
        binding.btnPauseResume.isEnabled = isReading

        binding.btnPrevPage.isEnabled = isReading
        binding.btnNextPage.isEnabled = isReading

        // アクセシビリティ権限があれば読み上げ開始可能（画面キャプチャは自動）
        val accessibilityEnabled = isAccessibilityServiceEnabled()
        binding.btnStartReading.isEnabled = accessibilityEnabled || isReading
    }

    private fun updateStatusText(status: String) {
        binding.statusText.text = status
        debugLog("Status updated: $status")
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setTitle("権限が必要です")
            .setMessage("Kindle読み上げ機能を使用するには、画面キャプチャ権限とアクセシビリティ権限が必要です。")
            .setPositiveButton("設定") { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                    requestOverlayPermission()
                } else if (!isAccessibilityServiceEnabled()) {
                    openAccessibilitySettings()
                }
            }
            .setNegativeButton("キャンセル", null)
            .show()
    }

    private fun loadSettings() {
        currentReadingSpeed = sharedPreferences.getFloat("reading_speed", 1.0f)
        autoPageTurnEnabled = sharedPreferences.getBoolean("auto_page_turn", true)
        pageDirection = sharedPreferences.getString("page_direction", "right_to_next") ?: "right_to_next"

        // UIに設定を反映
        val speedProgress = ((currentReadingSpeed / 0.25f) - 1).toInt().coerceIn(0, 8)
        binding.speedSeekBar.progress = speedProgress
        binding.autoPageTurnSwitch.isChecked = autoPageTurnEnabled

        // ページめくり方向設定を反映
        when (pageDirection) {
            "left_to_next" -> binding.radioLeftToNext.isChecked = true
            else -> binding.radioRightToNext.isChecked = true
        }

        debugLog("Settings loaded - Speed: $currentReadingSpeed, AutoPageTurn: $autoPageTurnEnabled, PageDirection: $pageDirection")
    }

    private fun saveSettings() {
        sharedPreferences.edit()
            .putFloat("reading_speed", currentReadingSpeed)
            .putBoolean("auto_page_turn", autoPageTurnEnabled)
            .putString("page_direction", pageDirection)
            .apply()

        debugLog("Settings saved")
    }

    private fun applyTTSSettings() {
        textToSpeech?.setSpeechRate(currentReadingSpeed)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.let { tts ->
                val result = tts.setLanguage(Locale.JAPANESE)
                when (result) {
                    TextToSpeech.LANG_MISSING_DATA, TextToSpeech.LANG_NOT_SUPPORTED -> {
                        debugLog("Japanese TTS not supported, trying English")
                        tts.setLanguage(Locale.ENGLISH)
                    }
                    else -> {
                        debugLog("TTS initialized successfully with Japanese")
                    }
                }
                applyTTSSettings()
                updateStatusText("準備完了")
            } ?: run {
                debugLog("TTS object is null after successful init")
                updateStatusText("TTS初期化エラー")
            }
        } else {
            debugLog("TTS initialization failed")
            updateStatusText("TTS初期化に失敗しました")
        }
    }

    override fun onResume() {
        super.onResume()
        updatePermissionButtonStates()
        checkAccessibilityServiceEnabled()
        updateUIState()  // ✅ FIX: Update UI state after checking permissions
    }

    override fun onDestroy() {
        super.onDestroy()

        if (isReading) {
            stopReading()
        }

        textToSpeech?.let { tts ->
            tts.stop()
            tts.shutdown()
        }

        debugLog("MainActivity destroyed")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun debugLog(message: String, data: Any? = null) {
        Log.d(TAG, "[$TAG] $message ${if (data != null) ": $data" else ""}")
    }

    companion object {
        private const val TAG = "KindleTTS_MainActivity"
    }
}