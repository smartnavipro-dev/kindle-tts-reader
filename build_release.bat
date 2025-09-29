@echo off
echo =========================================
echo Kindle TTS Reader - リリースビルド作成
echo =========================================
echo.

cd /d "C:\Users\chanc\KindleTTSReader"

echo 【1】プロジェクトクリーン...
call gradlew.bat clean
echo.

echo 【2】リリースビルド開始...
call gradlew.bat assembleRelease

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ リリースビルド成功！
    echo.
    echo 📦 出力ファイル:
    echo app\build\outputs\apk\release\app-release-unsigned.apk
    echo.
    echo 【注意】
    echo このAPKは署名されていません。
    echo Google Play にアップロードする場合は署名が必要です。
    echo.

    echo 【3】APKサイズ確認...
    dir app\build\outputs\apk\release\app-release-unsigned.apk
    echo.

) else (
    echo.
    echo ❌ リリースビルド失敗
    echo エラーログを確認してください
)

echo.
pause