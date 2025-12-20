# 🔧 Kindle TTS Reader - Troubleshooting Guide

**Version**: 1.1.0
**Last Updated**: 2025-12-20

---

## 📋 Quick Diagnosis

**Problem Category:**
- [Installation Issues](#installation-issues)
- [Permission Problems](#permission-problems)
- [OCR Not Working](#ocr-not-working)
- [Text-to-Speech Problems](#text-to-speech-problems)
- [Auto Page Turn Failures](#auto-page-turn-failures)
- [Performance Issues](#performance-issues)
- [Gemini API Errors](#gemini-api-errors)
- [Learning Feature Issues](#learning-feature-issues-v110)
- [Crashes and Freezes](#crashes-and-freezes)

---

## 📦 Installation Issues

### Problem: "App not installed" error

**Symptoms:**
- Installation fails with generic error message
- APK won't open

**Possible Causes:**
1. Insufficient storage space
2. Corrupted APK download
3. Conflicting existing installation
4. Android version too old

**Solutions:**

**Solution 1: Free up storage**
```
Settings → Storage → Free up space
```
- Delete unused apps
- Clear app caches
- Move photos to cloud
- **Required**: 100MB minimum

**Solution 2: Re-download APK**
1. Delete corrupted APK file
2. Clear browser cache
3. Re-download from GitHub Releases
4. Verify file size: 83MB exactly

**Solution 3: Uninstall old version**
```bash
# Via ADB (developer)
adb uninstall com.kindletts.reader

# Via Settings
Settings → Apps → Kindle TTS Reader → Uninstall
```

**Solution 4: Check Android version**
1. Settings → About phone → Android version
2. Minimum required: Android 5.0 (API 21)
3. If older: Device not supported

---

### Problem: "Parse Error" during installation

**Symptoms:**
- "There was a problem parsing the package"

**Possible Causes:**
- APK corrupted or incomplete download
- Wrong architecture (rare)
- File system corruption

**Solutions:**

1. **Re-download APK from official source**
   - Only use: https://github.com/smartnavipro-dev/kindle-tts-reader/releases
   - Avoid third-party APK sites

2. **Verify APK integrity**
   ```bash
   # Expected size
   83 MB (87,031,808 bytes)

   # MD5 checksum (check releases page for current hash)
   md5sum kindle-tts-reader-v1.1.0-release.apk
   ```

3. **Use different file manager**
   - Try: Files by Google, Solid Explorer

---

## 🔐 Permission Problems

### Problem: Overlay permission denied or not working

**Symptoms:**
- App shows "準備完了" but overlay doesn't appear
- Settings button doesn't open overlay permission screen

**Solutions:**

**Solution 1: Manually enable overlay**
```
Settings → Apps → Special app access → Display over other apps
→ Kindle TTS Reader → Allow
```

**Solution 2: Check battery optimization**
```
Settings → Apps → Kindle TTS Reader → Battery
→ Unrestricted (not Optimized)
```

**Solution 3: Restart app and phone**
1. Force stop app (Settings → Apps → Kindle TTS Reader → Force stop)
2. Restart phone
3. Re-open app and grant permission

---

### Problem: Accessibility service keeps disabling

**Symptoms:**
- Auto page turn works briefly then stops
- Accessibility service shows as OFF in settings

**Possible Causes:**
- Battery optimization killing service
- Security app interference
- System memory pressure

**Solutions:**

**Solution 1: Disable battery optimization**
```
Settings → Apps → Kindle TTS Reader → Battery
→ Don't optimize (or Unrestricted)
```

**Solution 2: Lock app in recent apps**
- Open recent apps (square button)
- Find Kindle TTS Reader
- Tap lock icon (📌) to prevent closure

**Solution 3: Whitelist in security apps**
- If using antivirus: Add to whitelist
- Common apps: Avast, Norton, McAfee
- Disable "auto-boost" features

**Solution 4: Re-enable service**
```
Settings → Accessibility → Downloaded services
→ Kindle TTS Auto Page Turn → Toggle OFF then ON
```

---

### Problem: Screen capture permission keeps asking

**Symptoms:**
- Permission prompt appears every time
- "Don't ask again" checkbox doesn't work

**Cause:**
- This is normal Android behavior for MediaProjection
- Permission is session-based, not permanent

**Workaround:**
- Accept this as expected behavior
- Future Android versions may allow permanent grant
- Tap "今すぐ開始" each session

---

## 🔍 OCR Not Working

### Problem: No text extracted from Kindle screen

**Symptoms:**
- Status shows "処理中..." indefinitely
- TTS doesn't start
- Overlay shows empty text

**Diagnostic Steps:**

**Step 1: Check screen content**
- Is Kindle app showing actual text page?
- Images-only pages cannot be OCR'd
- Try a different page with clear text

**Step 2: Check OCR log (developer mode)**
```bash
# Via ADB
adb logcat | grep "KindleTTS_OCR"

# Look for:
# - "No text found" = OCR saw no text
# - "Confidence: 0.XX" = Low confidence
```

**Solutions:**

**Solution 1: Improve text quality**
- Increase Kindle font size (Aa button → Larger)
- Use high contrast theme (Black text on white)
- Ensure good screen brightness
- Clean screen from fingerprints

**Solution 2: Restart OCR process**
1. Tap "Stop" in overlay
2. Return to main screen
3. Tap "読み上げ開始" again

**Solution 3: Clear app cache**
```
Settings → Apps → Kindle TTS Reader
→ Storage → Clear cache (NOT Clear data)
```

---

### Problem: OCR extracts gibberish or wrong characters

**Symptoms:**
- TTS reads nonsense sounds
- Extracted text is garbled

**Possible Causes:**
1. Unusual font in Kindle book
2. Low OCR confidence
3. Screen capture quality issue

**Solutions:**

**Solution 1: Change Kindle font**
```
Kindle app → Aa button → Font family
→ Try: Bookerly, Ember, Palatino
```

**Solution 2: Check Gemini API**
- Verify API key is valid
- Check API quota not exceeded
- Monitor at: https://aistudio.google.com/app/apikey

**Solution 3: Enable/disable learning**
```
Settings → ローカル学習機能 → Toggle OFF
```
- Try without learning to isolate issue
- If works without learning: Delete learning data and re-enable

---

## 🔊 Text-to-Speech Problems

### Problem: No sound / Silent playback

**Symptoms:**
- Overlay shows text extracted
- Status shows "読み上げ中..."
- But no audio output

**Diagnostic Steps:**

**Step 1: Check system volume**
- Media volume (not ringtone) must be > 0
- Test with YouTube or music app

**Step 2: Check TTS engine**
```
Settings → System → Language & input → Text-to-speech output
→ Preferred engine: Google Text-to-Speech
```

**Solutions:**

**Solution 1: Install TTS engine**
1. Open Google Play Store
2. Search "Google Text-to-Speech"
3. Update or install
4. Download Japanese voice data

**Solution 2: Test TTS**
```
Settings → System → Language & input → Text-to-speech output
→ Play/Listen to example
```
- If example doesn't work: TTS system issue
- Reinstall TTS engine

**Solution 3: Change TTS engine**
```
Try alternative engines:
- Samsung TTS (on Samsung devices)
- eSpeak TTS (free, basic)
```

---

### Problem: Wrong language pronunciation

**Symptoms:**
- Japanese text read in English accent
- English text read in Japanese accent

**Cause:**
- TTS engine auto-detects language per sentence
- Mixed language books may confuse TTS

**Solutions:**

**Solution 1: Specify language manually**
```
Settings → System → Language & input → Text-to-speech output
→ Language: Japanese (日本語)
```

**Solution 2: Use language-specific TTS**
- For Japanese books: HOYA ReadSpeaker, N2 TTS
- For English books: Google TTS (en-US voice)

---

### Problem: Choppy or robotic voice

**Cause:**
- Low-quality TTS voice data
- System performance issues

**Solutions:**

**Solution 1: Download HD voices**
```
Settings → System → Language & input → Text-to-speech output
→ Settings icon (⚙️) → Install voice data
→ Download HD/Enhanced voices
```

**Solution 2: Adjust speech rate**
```
Settings → System → Language & input → Text-to-speech output
→ Speech rate: Try 80% to 100%
```
- Too fast (>100%) = choppy
- Too slow (<80%) = robotic

---

## 📄 Auto Page Turn Failures

### Problem: Pages don't turn automatically

**Symptoms:**
- TTS finishes reading
- Stays on same page
- Manual swipe works

**Diagnostic Steps:**

**Step 1: Verify accessibility service**
```
Settings → Accessibility → Downloaded services
→ Kindle TTS Auto Page Turn: Should be ON
```

**Step 2: Check Kindle app version**
- Update Kindle to latest version
- Different versions may have different tap zones

**Solutions:**

**Solution 1: Recalibrate tap position**
1. Open Kindle book
2. Note where page-forward tap zone is
3. (Developer) Adjust coordinates in AutoPageTurnService.kt
4. (User) Report issue with Kindle version number

**Solution 2: Increase delay before page turn**
- Future version will have adjustable delay
- Current: 2 second delay after TTS completes

**Solution 3: Manual mode**
- Disable auto page turn in Settings
- Manually swipe when ready

---

### Problem: Pages turn too quickly

**Cause:**
- TTS completes before user finishes reading overlay text
- Delay timing mismatch

**Temporary Workaround:**
- Use slower TTS speech rate
- Future update will add configurable delay

---

## ⚡ Performance Issues

### Problem: App is slow / Laggy

**Symptoms:**
- Delayed response to taps
- Slow OCR processing (>5 seconds)
- Stuttering TTS

**Solutions:**

**Solution 1: Free up RAM**
1. Close background apps
2. Restart device
3. Don't use while gaming/heavy multitasking

**Solution 2: Clear app cache**
```
Settings → Apps → Kindle TTS Reader
→ Storage → Clear cache
```

**Solution 3: Reduce load**
- Disable learning temporarily
- Lower Kindle font size (less text to process)

---

### Problem: High battery drain

**Symptoms:**
- Battery drops >20% per hour
- Device gets warm

**Causes:**
- Continuous screen capture (expected)
- Gemini API calls (network)
- TTS engine processing

**Expected Battery Usage:**
- Normal: 10-15% per hour
- Heavy use: 15-20% per hour

**Mitigation:**
- Lower screen brightness
- Use Wi-Fi instead of mobile data
- Close other background apps
- Ensure device isn't overheating (pause if warm)

---

## 🤖 Gemini API Errors

### Problem: "API key invalid" error

**Symptoms:**
- OCR extracts text but correction fails
- Logs show authentication error

**Solutions:**

**Solution 1: Verify API key**
1. Visit: https://aistudio.google.com/app/apikey
2. Check key status: Active vs Restricted
3. Verify permissions: Gemini API enabled

**Solution 2: Regenerate API key**
1. Delete old key
2. Create new key
3. Rebuild app with new key (source build)

**For pre-built APK:**
- API key is embedded
- Cannot be changed without rebuild
- Contact developer if default key fails

---

### Problem: "Quota exceeded" error

**Symptoms:**
- Works initially, then fails
- Error message mentions quota/limit

**Cause:**
- Free tier: 500 requests/day exceeded
- Paid tier: Monthly quota reached

**Solutions:**

**Solution 1: Check usage**
```
Visit: https://aistudio.google.com/app/apikey
→ View your API keys → Usage metrics
```

**Solution 2: Wait for quota reset**
- Daily quota resets at midnight PT (Pacific Time)
- Or: Upgrade to paid tier

**Solution 3: Reduce API calls**
```
Settings → Disable learning temporarily
→ Use cached corrections
→ Read books you've already processed
```

---

### Problem: Slow Gemini API response

**Symptoms:**
- OCR takes 10-15 seconds
- "処理中..." persists

**Causes:**
- Slow internet connection
- API server congestion (rare)

**Solutions:**

1. **Switch to faster network**
   - Wi-Fi > mobile data
   - 4G/5G > 3G

2. **Check latency**
   ```bash
   ping google.com
   # Should be <50ms for good performance
   ```

3. **Retry logic**
   - App automatically retries failed requests
   - Wait 10 seconds before force-stopping

---

## 🧠 Learning Feature Issues (v1.1.0+)

### Problem: Learning patterns not saving

**Symptoms:**
- Make corrections but they don't stick
- Statistics show 0 patterns

**Diagnostic Steps:**

**Step 1: Check if enabled**
```
Settings → ローカル学習機能 → Should be ON
```

**Step 2: Check storage permissions**
```
Settings → Apps → Kindle TTS Reader → Permissions
→ Storage: Should be Allowed
```

**Solutions:**

**Solution 1: Re-enable learning**
1. Settings → Toggle learning OFF
2. Restart app
3. Settings → Toggle learning ON
4. Accept consent dialog again

**Solution 2: Check storage**
```
Settings → Storage
→ Ensure >50MB free space
```

---

### Problem: Cannot delete learning data

**Symptoms:**
- "Delete all data" button doesn't work
- Patterns still show after deletion

**Solutions:**

**Solution 1: Force clear**
```
Settings → Apps → Kindle TTS Reader
→ Storage → Clear data
```
**Warning**: This deletes ALL app data including settings

**Solution 2: Reinstall app**
1. Uninstall completely
2. Reinstall from APK
3. Fresh start

---

## 💥 Crashes and Freezes

### Problem: App crashes on startup

**Symptoms:**
- App opens then immediately closes
- "Kindle TTS Reader has stopped" message

**Solutions:**

**Solution 1: Clear app data**
```
Settings → Apps → Kindle TTS Reader
→ Storage → Clear data → OK
```
**Note**: This resets all settings

**Solution 2: Reinstall app**
1. Uninstall completely
2. Restart device
3. Reinstall from official APK

**Solution 3: Check Android System WebView**
```
Play Store → Search "Android System WebView"
→ Update if available
```

---

### Problem: App freezes during reading

**Symptoms:**
- Overlay stops responding
- Can't tap controls
- Status stuck

**Solutions:**

**Solution 1: Force stop**
```
Hold power button → Recent apps → Swipe away Kindle TTS Reader
Or: Settings → Apps → Kindle TTS Reader → Force stop
```

**Solution 2: Collect crash logs**
```bash
# Via ADB (for bug reports)
adb logcat > crash_log.txt
```
- Send to: contact@smartnavipro.dev

---

## 🔍 Advanced Debugging

### Enabling Developer Mode

**For detailed diagnostics:**

1. **Enable Android Developer Options**
   ```
   Settings → About phone → Tap "Build number" 7 times
   ```

2. **Enable USB Debugging**
   ```
   Settings → Developer options → USB debugging: ON
   ```

3. **Connect device to PC**
   ```bash
   adb devices
   # Should show your device
   ```

4. **Capture logs**
   ```bash
   # Real-time logs
   adb logcat | grep "KindleTTS"

   # Filtered by component
   adb logcat | grep "OCR"
   adb logcat | grep "TTS"
   adb logcat | grep "Gemini"

   # Save to file
   adb logcat > kindle_tts_debug.log
   ```

---

## 📞 Getting Help

### Before Reporting a Bug

Gather this information:

- **Device**: Manufacturer, model (e.g., "Samsung Galaxy S21")
- **Android version**: Settings → About phone
- **App version**: Settings → Apps → Kindle TTS Reader (should be 1.1.0)
- **Kindle version**: Play Store → My apps → Kindle → Version
- **Steps to reproduce**: Exact actions that trigger issue
- **Logs**: If possible, attach adb logcat output

### Report Channels

- **Bug Reports**: https://github.com/smartnavipro-dev/kindle-tts-reader/issues
- **Questions**: https://github.com/smartnavipro-dev/kindle-tts-reader/discussions
- **Email**: contact@smartnavipro.dev
- **Privacy Issues**: privacy@smartnavipro.dev

---

## 📚 Related Documentation

- 📖 [Setup Guide](SETUP_GUIDE.md)
- ❓ [Extended FAQ](FAQ_EXTENDED.md)
- 🔒 [Privacy Guide](PRIVACY_GUIDE.md)
- 📱 [Main README](../README.md)

---

**Still having issues? Don't hesitate to reach out! 🤝**

---

*Last updated: 2025-12-20*
*Document version: 1.0*
*App version: 1.1.0*
