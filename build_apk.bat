@echo off
echo Building Kindle TTS Reader APK...
echo.

cd /d "C:\Users\chanc\KindleTTSReader"

echo Current directory: %CD%
echo.

echo Starting Gradle build...
call gradlew.bat assembleDebug

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Build successful!
    echo APK location: app\build\outputs\apk\debug\app-debug.apk
    echo.
    pause
) else (
    echo.
    echo ❌ Build failed. Please check the error messages above.
    echo.
    pause
)