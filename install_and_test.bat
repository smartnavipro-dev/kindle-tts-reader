@echo off
echo =========================================
echo Kindle TTS Reader - 実機テストスクリプト
echo =========================================
echo.

cd /d "C:\Users\chanc\KindleTTSReader"

echo 【1】接続デバイス確認...
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" devices
echo.

echo 【2】既存アプリのアンインストール...
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" uninstall com.kindletts.reader
echo.

echo 【3】新しいAPKのインストール...
"%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" install "app\build\outputs\apk\debug\app-debug.apk"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ インストール成功！
    echo.
    echo 【4】アプリ起動...
    "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" shell am start -n com.kindletts.reader/.MainActivity
    echo.
    echo 【5】ログ監視を開始します...
    echo Ctrl+C で停止できます
    echo.
    "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" logcat -s KindleTTS_Service KindleTTS_AutoPageTurn KindleTTS_Main
) else (
    echo.
    echo ❌ インストール失敗
    echo.
    echo トラブルシューティング:
    echo 1. USBデバッグが有効になっているか確認
    echo 2. デバイスが認識されているか確認
    echo 3. 不明なソースからのアプリインストールが許可されているか確認
)

echo.
pause