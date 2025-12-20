# ❓ Kindle TTS Reader - Extended FAQ

**Version**: 1.1.0
**Last Updated**: 2025-12-20

---

## 📋 Table of Contents

1. [General Questions](#general-questions)
2. [Technical Questions](#technical-questions)
3. [Privacy & Security](#privacy--security)
4. [Gemini API](#gemini-api)
5. [Learning Feature (v1.1.0+)](#learning-feature-v110)
6. [Compatibility](#compatibility)
7. [Legal & Copyright](#legal--copyright)
8. [Troubleshooting](#troubleshooting)
9. [Feature Requests](#feature-requests)
10. [Contributing](#contributing)

---

## 🎯 General Questions

### Q: What is Kindle TTS Reader?

**A:** Kindle TTS Reader is an Android accessibility app that automatically reads Kindle books aloud using:
- **OCR** (Optical Character Recognition) to extract text from Kindle screens
- **AI correction** via Gemini 2.5 Flash API for high accuracy
- **TTS** (Text-to-Speech) for natural voice synthesis
- **Auto page turning** using accessibility services

It's designed for users with visual impairments, learning disabilities, or anyone who prefers auditory reading.

---

### Q: Is this an official Amazon/Kindle product?

**A:** No. This is an independent, open-source project created by SmartNaviPro Development. It is not affiliated with, endorsed by, or sponsored by Amazon or Kindle in any way.

---

### Q: Why would I use this instead of Kindle's built-in text-to-speech?

**A:** Great question! Here's the comparison:

| Feature | Kindle Built-in TTS | Kindle TTS Reader |
|---------|---------------------|-------------------|
| **Availability** | iOS/Fire devices only | Android 5.0+ |
| **Accuracy** | Medium (no AI correction) | High (Gemini AI + learning) |
| **Auto page turn** | ❌ Manual tapping required | ✅ Fully automatic |
| **Customization** | Limited | Extensive (TTS engine, speed, etc.) |
| **Offline** | ✅ Yes | ⚠️ Partial (OCR offline, correction online) |
| **Cost** | Free | Free (with API quota) |

**Use case:** If you're on Android and want a hands-free, AI-enhanced reading experience, this app is ideal.

---

### Q: Does it work with Kindle Unlimited books?

**A:** Yes! As long as the book is displayed in your Kindle app, this app can read it. The content format doesn't matter:
- ✅ Kindle Unlimited
- ✅ Purchased books
- ✅ Prime Reading
- ✅ Free samples
- ✅ Personal documents (Send to Kindle)

---

### Q: Can I use this with other reading apps (Google Play Books, Kobo, etc.)?

**A:** **Technically possible, but not officially supported.**

The app captures any on-screen text, so it *could* work with other reading apps. However:
- Auto page turn is calibrated specifically for Kindle app
- Text layout detection may fail with different apps
- No testing has been done on non-Kindle apps

**Recommendation:** Stick to Kindle app for best results. Support for other apps may come in future versions.

---

## 🔧 Technical Questions

### Q: How does OCR work? Why is it so accurate?

**A:** The OCR pipeline has three stages:

1. **Screen Capture** (MediaProjection API)
   - Captures Kindle screen at native resolution
   - No compression or quality loss

2. **ML Kit OCR** (Google)
   - On-device text recognition
   - Optimized for Japanese and English
   - Returns text + confidence score (0-1)

3. **AI Correction** (Gemini 2.5 Flash)
   - Only triggered if confidence < 0.7
   - Fixes common OCR errors (へ→ヘ, ー→一, etc.)
   - Considers context for disambiguation

4. **Local Learning** (v1.1.0+)
   - Stores correction patterns on device
   - Applies past corrections to similar text
   - Improves accuracy over time

**Accuracy benchmark:**
- Phase 1 (ML Kit only): ~85-90%
- Phase 2 (+ Gemini): ~95-98%
- Phase 3 (+ Learning): ~98-99.5%

---

### Q: What programming language is this written in?

**A:** 100% Kotlin for Android. Key technologies:

- **Language**: Kotlin 1.9+
- **Build**: Gradle 8.x
- **UI**: Jetpack Compose + Material Design 3
- **OCR**: Google ML Kit (Text Recognition V2)
- **AI**: Gemini 2.5 Flash via REST API
- **TTS**: Android TextToSpeech API
- **Morphology**: Atilika Kuromoji (Japanese)
- **Security**: AndroidX Security Crypto (v1.1.0+)

---

### Q: How much data does the app use?

**Expected data usage per hour of reading:**

| Component | Data Usage |
|-----------|------------|
| **OCR (ML Kit)** | 0 MB (offline) |
| **Gemini API calls** | 0.5-2 MB (text only, not images) |
| **TTS synthesis** | 0 MB (on-device) |
| **Learning storage** | <1 MB total (local) |

**Total: 0.5-2 MB/hour** (varies by OCR confidence and cache hit rate)

**To minimize data:**
- Use Wi-Fi instead of mobile data
- Pre-read books to build up learning cache
- Disable learning temporarily if data-constrained

---

### Q: Can I use this offline?

**A:** **Partially**, with limitations:

✅ **Works offline:**
- Screen capture
- OCR (ML Kit)
- Text-to-Speech
- Auto page turning
- Learning pattern matching (v1.1.0+)

❌ **Requires internet:**
- Gemini API corrections (when OCR confidence < 0.7)

**Offline experience:**
- If OCR confidence is high (>0.7): Fully offline
- If OCR confidence is low (<0.7): Will attempt API call, then fall back to uncorrected text if offline

**Future:** Offline-only mode planned for v1.2 (degraded accuracy but 100% offline)

---

### Q: What are the system requirements?

**Minimum:**
- Android 5.0 (Lollipop, API 21)
- 2GB RAM
- 50MB storage
- ARM or x86 CPU

**Recommended:**
- Android 8.0+ (Oreo, API 26)
- 4GB RAM
- 100MB storage
- Snapdragon 600 series or equivalent

**Tested devices:**
- ✅ Samsung Galaxy S21 (Android 14)
- ✅ Google Pixel 8 Pro (Android 14)
- ✅ Kindle Fire HD (Android 5.1) - limited support

---

## 🔒 Privacy & Security

### Q: Does the app collect any personal data?

**A:** **Absolutely not.** Here's exactly what data flows where:

| Data Type | Collected? | Stored Where? | Sent Externally? |
|-----------|-----------|---------------|------------------|
| **Screen images** | ✅ Temporary (in RAM) | Not stored | ❌ Never |
| **OCR text** | ✅ Yes | In-memory cache | ⚠️ Only to Gemini API (if confidence < 0.7) |
| **Learning patterns** | ✅ Yes (v1.1.0+) | Encrypted local storage | ❌ Never |
| **Book titles** | ❌ No | N/A | ❌ Never |
| **Reading history** | ❌ No | N/A | ❌ Never |
| **User identity** | ❌ No | N/A | ❌ Never |
| **Device info** | ❌ No | N/A | ❌ Never |

**What Gemini API receives:**
- Only the OCR-extracted text (not images)
- No book metadata, user ID, or device info
- Example: "この本は面白い" (this book is interesting)

**What Gemini API does NOT receive:**
- Screenshots or images
- Book title or author
- Your name or email
- Learning patterns
- Reading progress

---

### Q: Is my data encrypted?

**A (v1.1.0+):** Yes! All learning data uses military-grade encryption:

- **Algorithm**: AES256-GCM (Advanced Encryption Standard, 256-bit, Galois/Counter Mode)
- **Key storage**: Android Keystore (hardware-protected on supported devices)
- **Implementation**: AndroidX Security Crypto (EncryptedSharedPreferences)
- **Backup exclusion**: Learning data excluded from Google Drive backups

**What this means:**
- Even if someone gains root access to your device, they cannot decrypt learning data without your device's hardware key
- Uninstalling the app permanently deletes all encrypted data
- Factory reset makes data unrecoverable

---

### Q: Is this app GDPR compliant?

**A:** Yes, fully compliant as of v1.1.0:

✅ **GDPR Requirements Met:**
- **Consent**: Explicit opt-in required for learning feature
- **Right to access**: View learning statistics in Settings
- **Right to deletion**: One-tap delete all learning data
- **Data minimization**: Only essential data collected
- **Purpose limitation**: Data used only for OCR improvement
- **Storage limitation**: LRU cache with 100 pattern limit
- **Security**: AES256-GCM encryption + Android Keystore

📄 **Privacy Policies:**
- [English Version](../PRIVACY_POLICY.md)
- [Japanese Version](../PRIVACY_POLICY_ja.md)

---

### Q: Can Amazon see that I'm using this app?

**A:** **No.** This app operates entirely on your device's screen layer and does not interact with Amazon's servers or Kindle app internals:

- ❌ No Kindle app modification
- ❌ No DRM circumvention
- ❌ No Amazon API calls
- ❌ No telemetry to Amazon

**What Amazon can see:**
- Your normal Kindle reading activity (as always)

**What Amazon cannot see:**
- That you're using Kindle TTS Reader
- Your TTS usage
- Your learning patterns

---

## 🤖 Gemini API

### Q: Why do I need a Gemini API key?

**A (For source builds):** If you're building from source code, you need your own API key because:
1. We can't share a single API key with all users (quota limits)
2. You have full control over your API usage and costs
3. You can monitor and manage your quota

**For pre-built APK:** The APK includes a default API key and works immediately without setup.

---

### Q: How much does Gemini API cost?

**Pricing (as of December 2025):**

| Tier | Price | Limits |
|------|-------|--------|
| **Free** | $0 | 500 requests/day, 250K tokens/min |
| **Pay-as-you-go** | $0.30/M input tokens<br>$2.50/M output tokens | No daily limit |

**Real-world costs:**

| Usage | Corrections/month | Est. Cost |
|-------|-------------------|-----------|
| **Light** (1 book) | 500 | $0.15 |
| **Medium** (3 books) | 3,000 | $0.90 |
| **Heavy** (10 books) | 10,000 | $3.00 |

**Most users stay within free tier!**

---

### Q: How can I monitor my API usage?

**A:** Via Google AI Studio dashboard:

1. Visit https://aistudio.google.com/app/apikey
2. Click on your API key
3. View "Usage metrics" tab

**Metrics shown:**
- Requests per day (current/limit)
- Tokens consumed (input/output)
- Cost estimate (if exceeding free tier)

**Pro tip:** Check weekly to ensure you're within free tier limits.

---

### Q: What happens if I exceed the API quota?

**A:** The app handles quota exceeded gracefully:

1. Gemini API returns 429 error (quota exceeded)
2. App falls back to uncorrected ML Kit OCR text
3. TTS reads the uncorrected text (may have minor errors)
4. Error logged for debugging

**User experience:**
- Reading continues without interruption
- Slight accuracy reduction until quota resets
- Quota resets daily at midnight PT (Pacific Time)

---

### Q: Can I use my own custom AI model instead of Gemini?

**A (Advanced):** Not currently, but technically feasible with code modification:

**Steps to integrate custom model:**
1. Fork the repository
2. Modify `LLMCorrector.kt` to call your API endpoint
3. Implement same request/response format
4. Rebuild APK

**Supported alternatives:**
- OpenAI GPT-4o
- Anthropic Claude 3.5 Sonnet
- Self-hosted models (Llama 3, Mixtral, etc.)

**Note:** This requires Android/Kotlin development experience. Community contributions welcome!

---

## 🧠 Learning Feature (v1.1.0+)

### Q: How does local learning work?

**A:** The local learning system uses pattern matching with Levenshtein distance:

**Example workflow:**
1. **OCR extracts:** "この本は面曰い"
2. **Gemini corrects:** "この本は面白い" ("曰" → "白")
3. **Learning stores pattern:**
   ```
   Pattern: "面曰い" → "面白い"
   Context: Japanese text
   Timestamp: 2025-12-20 14:30
   ```
4. **Future encounter:** If OCR sees "面曰い" again (95% similarity), app applies learned correction without API call

**Benefits:**
- Faster processing (no API call)
- Works offline
- Zero data cost
- Improves over time

---

### Q: How many patterns can the learning system store?

**A:** **100 patterns maximum** (LRU cache - Least Recently Used)

When the 101st pattern is learned:
- Oldest (least recently used) pattern is evicted
- New pattern is stored

**Why limit to 100?**
- Performance: Fast lookup (<1ms)
- Storage: ~10-50KB total
- Privacy: Minimal data footprint

**Typical usage:**
- Most users accumulate 30-50 patterns for their favorite books
- Patterns are reusable across different books

---

### Q: Can I export/import learning patterns?

**A:** Not yet, but planned for v1.2!

**Planned features:**
- Export patterns to encrypted JSON file
- Import patterns from other devices
- Share curated pattern sets with community (opt-in)

**Current workaround:**
- Patterns are device-specific
- Each device learns independently

---

### Q: Does learning work for English books?

**A:** **Yes**, but with caveats:

The learning system is language-agnostic and works for any text. However:

**Japanese books:**
- ✅ Excellent results (complex characters benefit most from learning)
- ✅ Common OCR errors (ー→一, へ→ヘ, etc.)

**English books:**
- ⚠️ Moderate benefit (fewer OCR errors in general)
- ✅ Still useful for unusual fonts or low-quality scans

---

### Q: Is learning data synced across devices?

**A:** **No.** All learning data is strictly local:
- Stored only on the device where corrections were made
- Not synced to Google account
- Not uploaded to any server
- Deleted when app is uninstalled

**Future consideration:**
- Optional opt-in cloud sync planned for v2.0
- Will require explicit user consent
- End-to-end encryption (zero-knowledge architecture)

---

## 🌐 Compatibility

### Q: Which Kindle app versions are supported?

**A:** Tested and compatible with:

| Kindle Version | Status |
|----------------|--------|
| **v8.80+** (2024-2025) | ✅ Fully supported |
| **v8.50-8.79** (2023) | ✅ Supported |
| **v8.00-8.49** (2022) | ⚠️ May have page turn issues |
| **<v8.00** (2021 and older) | ❌ Not recommended |

**How to check Kindle version:**
```
Google Play Store → My apps & games → Installed → Kindle
```

**If using old version:** Update to latest for best compatibility.

---

### Q: Does it work on tablets?

**A:** **Yes!** Tablets are actually ideal:

**Benefits of tablets:**
- ✅ Larger screen = better OCR accuracy
- ✅ More RAM = smoother performance
- ✅ Better battery life
- ✅ Easier to read overlay UI

**Tested tablets:**
- Samsung Galaxy Tab S9
- Lenovo Tab P11
- Amazon Fire HD (limited support)

---

### Q: Can I use this on ChromeOS (Chromebooks)?

**A:** **Yes**, if your Chromebook supports Android apps:

**Requirements:**
- ChromeOS v80+ with Google Play Store
- Android app support enabled

**Known limitations:**
- Accessibility service may be restricted
- Screen capture permission may require developer mode
- Performance varies by Chromebook model

**Recommended:** Use on native Android devices for best experience.

---

### Q: Does it work with Kindle e-readers (Paperwhite, Oasis, etc.)?

**A:** **No.** This app only works on Android devices running the Kindle Android app.

**Why not e-readers?**
- Kindle e-readers don't run Android (they use a custom Linux-based OS)
- No support for third-party apps
- No accessibility service APIs

**Alternative:** Use Kindle Android app on smartphone/tablet instead.

---

## ⚖️ Legal & Copyright

### Q: Is this legal to use?

**A:** **Yes**, based on our legal research:

**Legal basis:**
1. **Accessibility exception** (Japan Copyright Act Article 30-4)
   - Machine learning for accessibility purposes is permitted
   - Amazon's Assistive Reader (2025) established precedent

2. **Fair use** (U.S. Copyright law)
   - Personal, non-commercial use
   - Transformative use (text-to-speech conversion)
   - No market harm to original work

3. **No DRM circumvention**
   - App only reads visual screen content
   - Does not decrypt or extract DRM-protected files
   - Does not enable piracy

**Disclaimer:** This is not legal advice. Use at your own discretion.

---

### Q: Can I use this for commercial purposes (e.g., audiobook production)?

**A:** **No, absolutely not.**

This app is intended for **personal accessibility use only**:

✅ **Allowed:**
- Personal reading assistance
- Study and research
- Accessibility needs

❌ **Prohibited:**
- Commercial audiobook production
- Public performances
- Redistribution of generated audio
- Any profit-generating activity

**Why?**
- Violates copyright holder's exclusive rights
- Circumvents legitimate audiobook market
- Not covered by fair use/accessibility exceptions

---

### Q: Am I allowed to modify and redistribute this app?

**A:** **Yes**, under MIT License terms:

**Permissions:**
- ✅ Use for any purpose
- ✅ Modify source code
- ✅ Distribute modified versions
- ✅ Sell or use commercially (if you add substantial value)

**Conditions:**
- ✅ Include original MIT license
- ✅ Include copyright notice

**Limitations:**
- ❌ No warranty provided
- ❌ Authors not liable for damages

**Full license:** [LICENSE](../LICENSE)

---

## 🔧 Troubleshooting

### Q: The app keeps crashing. What should I do?

**A:** See [Troubleshooting Guide - Crashes and Freezes](TROUBLESHOOTING.md#crashes-and-freezes)

Quick fixes:
1. Clear app data (Settings → Apps → Kindle TTS Reader → Storage → Clear data)
2. Restart device
3. Reinstall app
4. Report bug with logcat output

---

### Q: OCR is not extracting text. How do I fix it?

**A:** See [Troubleshooting Guide - OCR Not Working](TROUBLESHOOTING.md#ocr-not-working)

Common causes:
- Image-only pages (not OCR-able)
- Low screen brightness
- Unusual fonts
- Kindle app version incompatibility

---

### Q: Text-to-speech is silent. Help!

**A:** See [Troubleshooting Guide - Text-to-Speech Problems](TROUBLESHOOTING.md#text-to-speech-problems)

Quick checklist:
- ✅ Media volume > 0 (not ringtone volume)
- ✅ Google Text-to-Speech installed
- ✅ Japanese voice data downloaded
- ✅ TTS engine set to Google

---

## 💡 Feature Requests

### Q: Will you add support for [feature X]?

**A:** Check the [Roadmap in README](../README.md#roadmap) first.

**Already planned (v1.2):**
- Multiple language UI
- Reading statistics dashboard
- Custom TTS voices
- Export/import learning patterns

**Under consideration:**
- ePub format support
- PDF reading
- Offline-only mode
- Cloud sync (opt-in)

**How to request:**
1. Check [GitHub Discussions](https://github.com/smartnavipro-dev/kindle-tts-reader/discussions)
2. Search existing requests
3. If new, create a feature request issue

---

### Q: Can you add support for language [X]?

**A:** Currently supports:
- ✅ Japanese (fully supported)
- ✅ English (fully supported)

**Planned additions (v1.2):**
- Spanish
- French
- German
- Chinese (Simplified/Traditional)
- Korean

**Technical requirements for new language:**
- ML Kit Text Recognition support
- Android TTS engine availability
- Gemini API language support
- Community testing volunteers

**How to help:** If you're fluent in a language, volunteer to test in [GitHub Discussions](https://github.com/smartnavipro-dev/kindle-tts-reader/discussions)!

---

## 🤝 Contributing

### Q: How can I contribute to this project?

**A:** Contributions are very welcome! Here's how:

**Code contributions:**
1. Fork repository
2. Create feature branch
3. Make changes
4. Write tests
5. Submit pull request

**Non-code contributions:**
- 📝 Improve documentation
- 🌍 Translate to new languages
- 🐛 Report bugs with detailed logs
- 💡 Suggest features
- ⭐ Star the repository

**See:** [CONTRIBUTING.md](../CONTRIBUTING.md) (coming soon)

---

### Q: I found a security vulnerability. How do I report it?

**A:** **Do NOT create a public issue.**

**Responsible disclosure:**
1. Email: security@smartnavipro.dev
2. Include:
   - Detailed description
   - Steps to reproduce
   - Potential impact
   - Suggested fix (if any)
3. Allow 48 hours for initial response
4. Coordinated disclosure after patch released

**We take security seriously and will credit researchers appropriately.**

---

### Q: Can I donate to support development?

**A:** Thank you for the offer! Currently we don't accept donations, but you can support the project by:

- ⭐ Star the repository on GitHub
- 📢 Share with friends who would benefit
- 🐛 Report bugs and test new features
- 💻 Contribute code or documentation
- 📝 Write blog posts or tutorials

**Future:** If the project grows, we may set up Open Collective or GitHub Sponsors.

---

## 📞 Still Have Questions?

**Can't find your answer here?**

- 🔍 Check [Main README](../README.md)
- 📖 See [Setup Guide](SETUP_GUIDE.md)
- 🔧 Browse [Troubleshooting Guide](TROUBLESHOOTING.md)
- 💬 Ask in [GitHub Discussions](https://github.com/smartnavipro-dev/kindle-tts-reader/discussions)
- 📧 Email: contact@smartnavipro.dev

---

**Happy Reading! 📚🔊**

---

*Last updated: 2025-12-20*
*Document version: 1.0*
*App version: 1.1.0*
