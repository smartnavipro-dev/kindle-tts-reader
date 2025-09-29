@echo off
setlocal enabledelayedexpansion

echo =========================================
echo Kindle TTS Reader - 徹底的テスト実行
echo =========================================
echo.

set "ADB_PATH=%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
set "APK_PATH=app\build\outputs\apk\debug\app-debug.apk"
set "PACKAGE_NAME=com.kindletts.reader"

cd /d "C:\Users\chanc\KindleTTSReader"

echo 【Phase 1: 環境確認】
echo.
echo 1.1 ADB接続確認...
"%ADB_PATH%" devices -l
if %ERRORLEVEL% NEQ 0 (
    echo ❌ ADBが見つかりません
    goto :error
)

echo.
echo 1.2 APKファイル確認...
if not exist "%APK_PATH%" (
    echo ❌ APKファイルが見つかりません
    echo ビルドを実行してください: gradlew.bat assembleDebug
    goto :error
)

echo ✅ APKファイル確認済み

echo.
echo 【Phase 2: アプリケーションテスト】
echo.
echo 2.1 既存アプリのアンインストール...
"%ADB_PATH%" uninstall %PACKAGE_NAME% 2>nul

echo.
echo 2.2 新しいAPKのインストール...
"%ADB_PATH%" install "%APK_PATH%"
if %ERRORLEVEL% NEQ 0 (
    echo ❌ APKインストールに失敗しました
    goto :error
)

echo ✅ APKインストール成功

echo.
echo 2.3 アプリ情報の確認...
"%ADB_PATH%" shell dumpsys package %PACKAGE_NAME% | findstr "versionName"

echo.
echo 2.4 権限の確認...
"%ADB_PATH%" shell dumpsys package %PACKAGE_NAME% | findstr "permission"

echo.
echo 【Phase 3: 基本動作テスト】
echo.
echo 3.1 アプリ起動テスト...
"%ADB_PATH%" shell am start -n %PACKAGE_NAME%/.MainActivity
timeout /t 3

echo.
echo 3.2 プロセス確認...
"%ADB_PATH%" shell ps | findstr %PACKAGE_NAME%

echo.
echo 3.3 メモリ使用量確認...
"%ADB_PATH%" shell dumpsys meminfo %PACKAGE_NAME%

echo.
echo 【Phase 4: ログ監視開始】
echo.
echo ログ監視を開始します (Ctrl+C で停止)
echo 主要なログタグ: KindleTTS_Service, KindleTTS_AutoPageTurn, KindleTTS_Main
echo.

"%ADB_PATH%" logcat -s KindleTTS_Service KindleTTS_AutoPageTurn KindleTTS_Main AndroidRuntime System.err

goto :end

:error
echo.
echo ❌ テスト実行中にエラーが発生しました
echo.
pause
exit /b 1

:end
echo.
echo ✅ テスト完了
pause