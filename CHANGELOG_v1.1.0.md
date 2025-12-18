# Changelog - v1.1.0 (2025-12-18)

## 🎉 Major Feature: Local Learning System

This release introduces a **privacy-first local learning feature** that improves OCR accuracy by learning from your corrections—all stored securely on your device.

---

## ✨ New Features

### 📚 Local Learning System
- **Privacy-First Design**: All learning data stored locally on your device with AES256-GCM encryption
- **Smart Pattern Matching**: 95% similarity threshold using Levenshtein distance algorithm
- **LRU Cache**: Fast lookup with automatic memory management (100 pattern limit)
- **Thread-Safe Operations**: ReentrantReadWriteLock ensures data integrity

### 🔒 Privacy & Consent
- **First Launch Consent Dialog**: Clear explanation of data collection
- **Privacy Policy Viewer**: Full policy available in English and Japanese
- **Granular Controls**: Enable/disable learning feature anytime
- **Data Deletion**: One-tap deletion of all learning data

### ⚙️ Settings Screen
- **Learning Feature Toggle**: Enable or disable local learning
- **Statistics Display**: View learned pattern count and last updated time
- **Privacy Policy Access**: Quick access from settings
- **Data Management**: Delete all learning data with confirmation

### 🌍 Localization
- Full Japanese and English support
- Automatic language selection based on device locale
- Localized date/time formatting

---

## 🔐 Security Improvements

| Feature | Implementation |
|---------|---------------|
| **Encryption** | AES256-GCM (EncryptedSharedPreferences) |
| **Key Storage** | Android Keystore (hardware-protected) |
| **Backup** | Excluded from Google Drive backup |
| **Fallback** | Non-encrypted SharedPreferences on rooted devices |

---

## 📋 Technical Details

### New Components
- `PrivacyPreferences`: Manages user consent state
- `PrivacyConsentDialog`: Material Design 3 consent dialog
- `PrivacyPolicyActivity`: Displays privacy policy from Markdown
- `LocalCorrectionManager`: Core learning engine with pattern matching
- `SettingsActivity`: PreferenceFragment-based settings screen

### Dependencies Added
```gradle
implementation 'androidx.security:security-crypto:1.1.0-alpha06'
implementation 'com.google.code.gson:gson:2.10.1'
implementation 'androidx.preference:preference-ktx:1.2.1'
```

### Data Structure
Learning patterns stored as JSON:
```json
{
  "ocrText": "認識されたテキスト",
  "correctedText": "修正後のテキスト",
  "useCount": 5,
  "lastUsedAt": 1703001234567,
  "createdAt": 1702900000000
}
```

---

## 🎨 UI Changes

### MainActivity
- Added **"Settings"** button next to Accessibility button
- Privacy consent dialog on first launch
- Improved bottom button layout (50/50 split)

### Settings Screen
- Learning feature section with toggle
- Pattern count and last updated display
- Privacy policy viewer
- Delete data with confirmation dialog

---

## 📝 Privacy Policy

**Important**: This release includes comprehensive privacy policies in both English and Japanese:
- `/PRIVACY_POLICY.md` (English)
- `/PRIVACY_POLICY_ja.md` (Japanese)

Key points:
- ✅ Data stored on device only
- ✅ NOT sent to external servers
- ✅ NOT shared with third parties
- ✅ Can be deleted anytime
- ⚠️ Gemini API (OCR correction) sends OCR text to Google (learning data NOT sent)

---

## 🚀 Performance

- **Pattern Lookup**: O(1) for exact matches via HashMap
- **Similarity Search**: O(n) with Levenshtein distance for fuzzy matching
- **Memory Usage**: LRU cache limits in-memory patterns to 100
- **Storage**: Encrypted JSON file in app private storage

---

## 🐛 Bug Fixes

None in this release (new feature only).

---

## 📦 Installation

### Requirements
- Android 5.0 (API 21) or higher
- Accessibility permission (for auto page turning)
- Screen capture permission (for OCR)

### Upgrade from v1.0.84
1. Install v1.1.0 APK (over existing installation)
2. Grant consent on first launch (or decline)
3. All previous settings and API quota preserved

---

## 🔄 Migration Notes

### From v1.0.84 → v1.1.0
- **No breaking changes**: All existing features work as before
- **New SharedPreferences**: `privacy_prefs` and `user_corrections_encrypted`
- **Privacy dialog**: Shows only on first launch after upgrade
- **Learning disabled by default**: Must explicitly enable in settings

---

## 📖 Documentation

- [Privacy Policy (EN)](PRIVACY_POLICY.md)
- [Privacy Policy (JA)](PRIVACY_POLICY_ja.md)
- [Legal Risk Assessment](LEGAL_RISK_ASSESSMENT.md)
- [Implementation Decision Guide](IMPLEMENTATION_DECISION.md)
- [Privacy UI Design](docs/PRIVACY_UI_DESIGN.md)

---

## 🙏 Acknowledgments

This release implements **Option A: Local Learning Only** from the legal risk assessment, prioritizing user privacy and GDPR compliance.

Based on comprehensive research:
- ✅ Japanese Copyright Act Article 30-4 permits ML use
- ✅ Amazon Assistive Reader (2025) validates accessibility use case
- ✅ GDPR-compliant consent and data management

---

## 📊 Statistics

| Metric | Value |
|--------|-------|
| **Lines of Code Added** | ~2,000 |
| **New Files Created** | 10 |
| **Dependencies Added** | 3 |
| **Localization Support** | English + Japanese |
| **Build Time** | ~1 minute 21 seconds |

---

## 🤖 Generated with [Claude Code](https://claude.com/claude-code)

**Co-Authored-By**: Claude Sonnet 4.5 <noreply@anthropic.com>

---

## 🔗 Links

- **GitHub Repository**: [smartnavipro-dev/kindle-tts-reader](https://github.com/smartnavipro-dev/kindle-tts-reader)
- **Report Issues**: [GitHub Issues](https://github.com/smartnavipro-dev/kindle-tts-reader/issues)
- **Privacy Contact**: privacy@smartnavipro.dev

---

**Full Changelog**: [v1.0.84...v1.1.0](https://github.com/smartnavipro-dev/kindle-tts-reader/compare/v1.0.84...v1.1.0)
