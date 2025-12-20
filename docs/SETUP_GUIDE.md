# 🚀 Kindle TTS Reader - Complete Setup Guide

**Version**: 1.1.0
**Last Updated**: 2025-12-20

---

## 📋 Table of Contents

1. [System Requirements](#system-requirements)
2. [Pre-Installation Checklist](#pre-installation-checklist)
3. [Step-by-Step Installation](#step-by-step-installation)
4. [Permission Setup](#permission-setup)
5. [Gemini API Configuration](#gemini-api-configuration)
6. [First-Time Usage](#first-time-usage)
7. [Settings Configuration](#settings-configuration)
8. [Verification](#verification)
9. [Next Steps](#next-steps)

---

## 📱 System Requirements

### Minimum Requirements
- **OS**: Android 5.0 (Lollipop, API 21) or higher
- **RAM**: 2GB minimum, 4GB recommended
- **Storage**: 100MB free space (50MB for app + 50MB for cache)
- **Screen**: 720x1280 pixels minimum resolution

### Recommended Requirements
- **OS**: Android 8.0 (Oreo, API 26) or higher
- **RAM**: 4GB or more
- **Storage**: 200MB free space
- **Internet**: Wi-Fi or mobile data for Gemini API (optional)

### Required Apps
- **Kindle App**: Latest version from Google Play Store
- **Books**: At least one book downloaded in Kindle app

---

## ✅ Pre-Installation Checklist

Before installing Kindle TTS Reader, ensure you have:

- [ ] Android device meets minimum requirements
- [ ] Kindle app installed and logged in
- [ ] At least one book downloaded in Kindle app
- [ ] Sufficient storage space available
- [ ] (Optional) Gemini API key ready for enhanced accuracy

---

## 📦 Step-by-Step Installation

### Method 1: Install Pre-built APK (Recommended)

#### Step 1: Download APK

1. Open your browser on Android device
2. Navigate to: https://github.com/smartnavipro-dev/kindle-tts-reader/releases/latest
3. Download `kindle-tts-reader-v1.1.0-release.apk` (83MB)

#### Step 2: Enable Unknown Sources

**For Android 8.0+:**
1. When prompted, tap "Settings"
2. Enable "Allow from this source"
3. Return to installation

**For Android 7.1 and below:**
1. Go to Settings → Security
2. Enable "Unknown sources"
3. Confirm the warning

#### Step 3: Install APK

1. Open Downloads folder or notification
2. Tap the downloaded APK file
3. Tap "Install"
4. Wait for installation to complete
5. Tap "Open" or find app in app drawer

---

### Method 2: Build from Source

#### Prerequisites
- Android Studio Arctic Fox or later
- JDK 11 or higher
- Git installed

#### Build Steps

```bash
# Clone repository
git clone https://github.com/smartnavipro-dev/kindle-tts-reader.git
cd kindle-tts-reader

# Build debug APK
./gradlew assembleDebug

# Build release APK (requires signing)
./gradlew assembleRelease

# APK location
# Debug: app/build/outputs/apk/debug/app-debug.apk
# Release: app/build/outputs/apk/release/app-release.apk
```

---

## 🔐 Permission Setup

Kindle TTS Reader requires three essential permissions. Grant them in order:

### Permission 1: Overlay Permission

**Why needed**: Display floating controls over Kindle app
**Risk level**: Low - Standard feature for floating apps

**Steps:**
1. Launch Kindle TTS Reader
2. Tap "画面キャプチャを有効化" (Enable Screen Capture) button
3. System will prompt: "Display over other apps"
4. Tap "Kindle TTS Reader" in the list
5. Toggle "Allow display over other apps" to ON
6. Return to app (back button)

**Verification:**
- Status text should show "準備完了" (Ready)
- Button should be enabled

---

### Permission 2: Accessibility Permission

**Why needed**: Automatic page turning in Kindle app
**Risk level**: Medium - Accessibility services have broad access

**Steps:**
1. Tap "アクセシビリティサービスを有効化" (Enable Accessibility Service)
2. Navigate to: Settings → Accessibility → Downloaded services
3. Find "Kindle TTS Auto Page Turn"
4. Toggle to ON
5. Read the permission warning
6. Tap "OK" or "Allow"
7. Return to app

**Verification:**
- Accessibility service indicator should appear in notification bar
- App status should confirm service is active

**Security Note:**
- This permission allows the app to simulate touch gestures
- Only used for page turning, not data collection
- Can be disabled anytime in Android Settings

---

### Permission 3: Screen Capture Permission

**Why needed**: Capture Kindle screen for OCR text extraction
**Risk level**: High - Can capture all on-screen content

**Steps:**
1. Tap "読み上げ開始" (Start Reading) button
2. Android will show "Start screen capture?" dialog
3. Check "Don't show again" (optional)
4. Tap "Start now" or "今すぐ開始"

**Verification:**
- Recording indicator appears in status bar
- App begins capturing and processing

**Privacy Note:**
- Only Kindle app screens are processed
- Images discarded immediately after OCR
- No screenshots saved to storage
- All processing happens on-device (except optional Gemini API)

---

## 🤖 Gemini API Configuration

### Do I Need Gemini API?

**Pre-built APK**: Already includes API key - works immediately
**Building from source**: You need your own API key

### Getting Your API Key

#### Step 1: Visit Google AI Studio
1. Open browser: https://aistudio.google.com/app/apikey
2. Sign in with Google account

#### Step 2: Create API Key
1. Click "Create API Key"
2. Select or create a project
3. Copy the generated key (starts with `AIza...`)
4. Save it securely

### Setting API Key (Source Build Only)

#### Option A: Build-time Configuration
```bash
# In project root directory
echo "GEMINI_API_KEY=AIzaSy..." >> local.properties

# Build with API key
./gradlew assembleRelease -PGEMINI_API_KEY="AIzaSy..."
```

#### Option B: Environment Variable
```bash
# Linux/macOS
export GEMINI_API_KEY="AIzaSy..."

# Windows PowerShell
$env:GEMINI_API_KEY="AIzaSy..."
```

### Cost Information

**Free Tier** (Perfect for personal use):
- 500 requests per day
- 250,000 tokens per minute
- No credit card required

**Paid Usage** (if exceeding free tier):
- ~$0.0003 per correction (0.03¢)
- Typical monthly cost: $0.90 for 3,000 corrections
- Monitor at: https://aistudio.google.com/app/apikey

---

## 🎬 First-Time Usage

### Privacy Consent (v1.1.0+)

On first launch, you'll see a privacy consent dialog:

**Dialog Content:**
- Explanation of local learning feature
- Data storage and encryption details
- Your rights (view, delete data anytime)

**Options:**
- **Accept**: Enable local learning for improved accuracy
- **Decline**: Use app without learning (still fully functional)

**You can change this later in Settings!**

---

### Testing the App

#### Step 1: Open a Kindle Book
1. Launch Kindle app
2. Open any book with text content
3. Position to a page you want to test

#### Step 2: Start Kindle TTS Reader
1. Open Kindle TTS Reader
2. Verify all permissions are granted (green checkmarks)
3. Tap "読み上げ開始" (Start Reading)

#### Step 3: Grant Screen Capture
1. Tap "Start now" in system dialog
2. App switches to Kindle automatically

#### Step 4: Verify Operation
1. Floating overlay should appear on Kindle screen
2. OCR processing begins (watch status text)
3. Text-to-speech starts reading
4. Pages turn automatically when speech completes

**Expected Timeline:**
- Initial capture: 1-2 seconds
- OCR processing: 2-3 seconds
- First speech: 3-5 seconds total
- Subsequent pages: 1-2 seconds (cached)

---

## ⚙️ Settings Configuration

### Accessing Settings

1. Open Kindle TTS Reader main screen
2. Tap "⚙️" (settings icon) in top-right corner
3. Settings screen opens

---

### Learning Feature Settings

#### Enable/Disable Local Learning

**Path**: Settings → ローカル学習機能

**Options:**
- **ON**: App learns from corrections (recommended)
- **OFF**: No learning, manual corrections only

**Effect:**
- When ON: Pattern matching improves over time
- When OFF: Uses only default OCR + Gemini API

---

#### View Learning Statistics

**Path**: Settings → 学習パターン統計

**Information Shown:**
- Total patterns learned
- Last update timestamp
- Storage size used

**Example:**
```
学習パターン数: 47
最終更新: 2025-12-20 14:30:25
ストレージ使用量: 12 KB
```

---

#### Delete All Learning Data

**Path**: Settings → すべてのデータを削除

**Warning**: This action cannot be undone!

**Steps:**
1. Tap "すべてのデータを削除" (Delete All Data)
2. Confirm deletion in dialog
3. All learning patterns permanently deleted
4. App returns to fresh install state

**When to use:**
- Clearing accumulated incorrect patterns
- Switching to different reading material type
- Privacy concerns (before selling/gifting device)

---

### Privacy Settings

#### View Privacy Policy

**Path**: Settings → プライバシーポリシー

**Languages Available:**
- Japanese (default)
- English (switch in dialog)

**Content:**
- Data collection practices
- Encryption details
- User rights (GDPR)
- Contact information

---

#### Data Location

All learning data stored at:
```
/data/data/com.kindletts.reader/shared_prefs/local_corrections
```

**Encryption**: AES256-GCM
**Key Storage**: Android Keystore (hardware-protected)
**Backup**: Excluded from Google Drive backups

---

## ✔️ Verification

### Checklist for Successful Setup

Run through this checklist to ensure everything works:

- [ ] App installs without errors
- [ ] All three permissions granted
- [ ] Privacy consent accepted (or declined knowingly)
- [ ] Kindle app has downloaded books
- [ ] Screen capture starts when reading begins
- [ ] OCR extracts text correctly (check overlay status)
- [ ] Text-to-speech speaks in correct language
- [ ] Pages turn automatically
- [ ] Overlay controls respond to touches
- [ ] Settings screen accessible
- [ ] Learning feature status reflects your choice

### Common Success Indicators

✅ **Good Signs:**
- Status text shows "読み上げ中..." (Reading...)
- Page numbers increment automatically
- Speech quality is natural and clear
- No lag between pages
- Learning patterns accumulate (if enabled)

❌ **Warning Signs:**
- Status stuck on "処理中..." (Processing...)
- Pages don't turn
- Silent playback
- Frequent crashes
- See [Troubleshooting Guide](TROUBLESHOOTING.md)

---

## 🎯 Next Steps

### Optimize Your Experience

1. **Adjust Reading Speed**
   - Settings → Text-to-Speech → Speech Rate
   - Try 0.8x to 1.5x range

2. **Fine-tune Page Turn Timing**
   - Experiment with different book layouts
   - Report issues for books that don't work

3. **Monitor API Usage**
   - Check Gemini API dashboard weekly
   - Stay within free tier limits

4. **Contribute Learning Patterns**
   - (Future feature) Export patterns for community

### Get Help

- 📖 **Troubleshooting**: [TROUBLESHOOTING.md](TROUBLESHOOTING.md)
- ❓ **FAQ**: [FAQ_EXTENDED.md](FAQ_EXTENDED.md)
- 🔒 **Privacy**: [PRIVACY_GUIDE.md](PRIVACY_GUIDE.md)
- 🐛 **Bug Reports**: [GitHub Issues](https://github.com/smartnavipro-dev/kindle-tts-reader/issues)
- 💬 **Community**: [GitHub Discussions](https://github.com/smartnavipro-dev/kindle-tts-reader/discussions)

---

## 📞 Support

Having trouble? We're here to help!

- **GitHub Issues**: https://github.com/smartnavipro-dev/kindle-tts-reader/issues
- **Email**: contact@smartnavipro.dev
- **Privacy Questions**: privacy@smartnavipro.dev

---

**Happy Reading! 📚🔊**

---

*Last updated: 2025-12-20*
*Document version: 1.0*
*App version: 1.1.0*
