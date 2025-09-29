@echo off
setlocal enabledelayedexpansion

echo =========================================
echo Kindle TTS Reader - 単体テストスイート
echo =========================================
echo.

set "ADB_PATH=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
set "PACKAGE_NAME=com.kindletts.reader"

cd /d "C:\Users\chanc\KindleTTSReader"

echo 【Test Suite 1: 権限テスト】
echo.
echo T1.1 画面キャプチャ権限テスト...
"%ADB_PATH%" shell am start -n %PACKAGE_NAME%/.MainActivity -e test_type "screen_capture_permission"
timeout /t 2

echo.
echo T1.2 オーバーレイ権限テスト...
"%ADB_PATH%" shell am start -n %PACKAGE_NAME%/.MainActivity -e test_type "overlay_permission"
timeout /t 2

echo.
echo T1.3 アクセシビリティ権限テスト...
"%ADB_PATH%" shell am start -n %PACKAGE_NAME%/.MainActivity -e test_type "accessibility_permission"
timeout /t 2

echo.
echo 【Test Suite 2: サービステスト】
echo.
echo T2.1 OverlayService起動テスト...
"%ADB_PATH%" shell am startservice -n %PACKAGE_NAME%/.OverlayService -e action "START_READING"
timeout /t 3

echo.
echo T2.2 AutoPageTurnService起動テスト...
"%ADB_PATH%" shell am startservice -n %PACKAGE_NAME%/.AutoPageTurnService -e action "NEXT_PAGE"
timeout /t 2

echo.
echo T2.3 サービス状態確認...
"%ADB_PATH%" shell dumpsys activity services %PACKAGE_NAME%

echo.
echo 【Test Suite 3: メモリリークテスト】
echo.
echo T3.1 初期メモリ状態...
"%ADB_PATH%" shell dumpsys meminfo %PACKAGE_NAME% | findstr "TOTAL"

echo.
echo T3.2 サービス開始後のメモリ...
"%ADB_PATH%" shell am startservice -n %PACKAGE_NAME%/.OverlayService
timeout /t 5
"%ADB_PATH%" shell dumpsys meminfo %PACKAGE_NAME% | findstr "TOTAL"

echo.
echo T3.3 サービス停止後のメモリ...
"%ADB_PATH%" shell am stopservice -n %PACKAGE_NAME%/.OverlayService
timeout /t 3
"%ADB_PATH%" shell dumpsys meminfo %PACKAGE_NAME% | findstr "TOTAL"

echo.
echo 【Test Suite 4: エラーハンドリングテスト】
echo.
echo T4.1 権限なしでの動作テスト...
"%ADB_PATH%" shell pm revoke %PACKAGE_NAME% android.permission.SYSTEM_ALERT_WINDOW 2>nul
"%ADB_PATH%" shell am start -n %PACKAGE_NAME%/.MainActivity
timeout /t 3

echo.
echo T4.2 不正なIntentテスト...
"%ADB_PATH%" shell am startservice -n %PACKAGE_NAME%/.OverlayService -e action "INVALID_ACTION"
timeout /t 2

echo.
echo T4.3 アプリ強制終了からの復旧テスト...
"%ADB_PATH%" shell am force-stop %PACKAGE_NAME%
timeout /t 2
"%ADB_PATH%" shell am start -n %PACKAGE_NAME%/.MainActivity

echo.
echo 【Test Suite 5: パフォーマンステスト】
echo.
echo T5.1 CPU使用率監視 (10秒間)...
"%ADB_PATH%" shell top -n 10 | findstr %PACKAGE_NAME%

echo.
echo T5.2 バッテリー使用量確認...
"%ADB_PATH%" shell dumpsys batterystats %PACKAGE_NAME%

echo.
echo ✅ 単体テストスイート完了
echo 詳細なログは logcat で確認してください
echo.
pause