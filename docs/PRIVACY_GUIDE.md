# 🔒 Kindle TTS Reader - Privacy & Data Management Guide

**Version**: 1.1.0
**Last Updated**: 2025-12-20

---

## 📋 Table of Contents

1. [Privacy Overview](#privacy-overview)
2. [Data Collection](#data-collection)
3. [Local Learning System (v1.1.0+)](#local-learning-system-v110)
4. [Encryption & Security](#encryption--security)
5. [Your Privacy Rights](#your-privacy-rights)
6. [Managing Your Data](#managing-your-data)
7. [Third-Party Services](#third-party-services)
8. [GDPR Compliance](#gdpr-compliance)
9. [Children's Privacy](#childrens-privacy)
10. [Changes to Privacy Practices](#changes-to-privacy-practices)

---

## 🎯 Privacy Overview

### Our Commitment

**Kindle TTS Reader is built with privacy-first principles:**

- ✅ **No user accounts** - No registration or login required
- ✅ **No analytics** - Zero tracking of usage patterns
- ✅ **No advertising** - No ads, no ad networks, no behavioral profiling
- ✅ **Local processing** - Most operations happen on your device
- ✅ **Transparent** - Fully open-source code for auditing
- ✅ **User control** - You decide what data to share

---

### Privacy Philosophy

We believe that:

1. **Your reading is private** - Book titles, reading progress, and habits are yours alone
2. **Encryption is mandatory** - All stored data must be encrypted
3. **Minimal data** - Collect only what's absolutely necessary
4. **Deletable** - You can delete all data with one tap
5. **Transparent** - No hidden data collection

---

## 📊 Data Collection

### What We Collect

| Data Type | Collected? | Purpose | Storage | Shared? |
|-----------|-----------|---------|---------|---------|
| **Screen captures** | ✅ Temporary | OCR text extraction | RAM only (not saved) | ❌ Never |
| **OCR text** | ✅ Yes | Text-to-speech | Memory cache | ⚠️ Gemini API only (if confidence < 0.7) |
| **Learning patterns** | ✅ Optional (v1.1.0+) | Improve OCR accuracy | Encrypted local storage | ❌ Never |
| **Book metadata** | ❌ No | N/A | N/A | ❌ Never |
| **Reading history** | ❌ No | N/A | N/A | ❌ Never |
| **Personal info** | ❌ No | N/A | N/A | ❌ Never |
| **Device info** | ❌ No | N/A | N/A | ❌ Never |
| **Crash logs** | ❌ No | N/A | N/A | ❌ Never |

---

### What We DON'T Collect

**Absolutely no collection of:**

- 📚 Book titles or authors
- 📖 Reading progress or bookmarks
- 👤 Name, email, or user identity
- 📱 Device identifiers (IMEI, MAC address, etc.)
- 📍 Location data
- 📞 Contacts or calendar
- 💳 Payment information
- 🌐 Browsing history

---

### How Data Flows

**Screen Capture → OCR → TTS Workflow:**

```
1. Screen Capture (MediaProjection API)
   ↓
   Image stored in RAM temporarily
   ↓
2. OCR Processing (ML Kit)
   ↓
   Text extracted + confidence score
   ↓
3. Confidence Check
   ├─ High (≥0.7): Use directly → TTS
   └─ Low (<0.7): Send text to Gemini API → Corrected text → TTS
   ↓
4. Learning Pattern (if enabled)
   ↓
   Store correction locally (encrypted)
   ↓
5. Memory Cleared
   ↓
   Image and temp data discarded
```

**Key points:**
- Images never leave device
- Only low-confidence text sent to API
- Learning data stored locally only
- Temporary data cleared after use

---

## 🧠 Local Learning System (v1.1.0+)

### What is Local Learning?

The local learning system improves OCR accuracy by remembering corrections you've approved. It works entirely on your device without internet.

---

### How It Works

**Example scenario:**

1. **First encounter:**
   - OCR extracts: "この本は面曰い"
   - Gemini corrects: "この本は面白い"
   - You confirm correction
   - **Pattern stored locally**: "面曰い" → "面白い"

2. **Future encounters:**
   - OCR extracts: "面曰い" again
   - App recognizes pattern (95% similarity)
   - **Auto-corrects without API call**: "面白い"
   - Faster + offline + free

---

### Learning Data Details

**What is stored:**

```json
{
  "originalText": "面曰い",
  "correctedText": "面白い",
  "language": "ja",
  "confidence": 0.95,
  "timestamp": 1703088625000,
  "useCount": 3
}
```

**What is NOT stored:**
- Book titles
- Full page content
- Your identity
- Reading context

**Storage location:**
```
/data/data/com.kindletts.reader/shared_prefs/local_corrections
```

**Encryption:** AES256-GCM with Android Keystore

---

### Privacy Safeguards

✅ **Consent required** - Explicit opt-in on first use
✅ **Encrypted storage** - AES256-GCM with hardware keys
✅ **LRU cache** - Maximum 100 patterns (oldest auto-deleted)
✅ **Device-only** - Never synced or uploaded
✅ **Backup excluded** - Not included in Google Drive backups
✅ **One-tap deletion** - Instantly erase all patterns

---

## 🔐 Encryption & Security

### Encryption Standards

**Algorithm:** AES256-GCM (Advanced Encryption Standard, 256-bit, Galois/Counter Mode)

**Key features:**
- Military-grade encryption
- Authenticated encryption (prevents tampering)
- Industry standard (NIST approved)

---

### Key Management

**Android Keystore** - Hardware-backed secure key storage

**How it works:**
1. Encryption key generated on device
2. Key stored in Android Keystore (TEE - Trusted Execution Environment)
3. Key never accessible to app code or user
4. Key deleted on app uninstall

**Security level:**
- ✅ Protected from root access
- ✅ Protected from physical extraction
- ✅ Destroyed on factory reset
- ✅ Hardware-backed on supported devices (e.g., Titan M chip)

---

### Data Protection

**EncryptedSharedPreferences** - AndroidX Security Crypto library

**Protection against:**
- ❌ Root access snooping
- ❌ Malicious apps reading files
- ❌ USB debugging extraction
- ❌ ADB backup attacks
- ❌ File system browser access

**Even if attacker gains:**
- Root privileges
- Physical device access
- File system dumps

**They cannot decrypt without:**
- Device-specific hardware key
- Secure boot chain validation

---

### Security Best Practices

**App follows:**

1. **Principle of Least Privilege**
   - Only requests essential permissions
   - No unnecessary network access
   - No background location

2. **Secure Coding**
   - Input validation
   - SQL injection prevention (not applicable - no SQL)
   - XSS protection (not applicable - no web views with user input)

3. **Regular Updates**
   - Security patches applied promptly
   - Dependency updates monitored
   - Vulnerability scanning

4. **Open Source**
   - Full code transparency
   - Community security audits
   - Bug bounty program (planned)

---

## 🛡️ Your Privacy Rights

### GDPR Rights (EU Users)

Under GDPR (General Data Protection Regulation), you have:

1. **Right to Access**
   - View all learning patterns in Settings
   - See statistics (count, size, last update)

2. **Right to Rectification**
   - Learning patterns auto-update with new corrections
   - You can disable learning to prevent new patterns

3. **Right to Erasure** ("Right to be Forgotten")
   - One-tap deletion of all learning data
   - Complete app data reset via Android settings

4. **Right to Data Portability**
   - Export learning patterns (v1.2 planned feature)
   - Import patterns to new device (v1.2 planned)

5. **Right to Restrict Processing**
   - Disable learning feature in Settings
   - App continues to function without learning

6. **Right to Object**
   - Decline consent on first use
   - Withdraw consent anytime in Settings

7. **Right to Human Review**
   - Not applicable (no automated decision-making affecting you)

---

### CCPA Rights (California Users)

Under CCPA (California Consumer Privacy Act), you have:

1. **Right to Know**
   - This privacy guide discloses all data practices
   - View learning data in Settings

2. **Right to Delete**
   - Delete all data via Settings or app uninstall

3. **Right to Opt-Out of Sale**
   - Not applicable - **we never sell your data**

4. **Right to Non-Discrimination**
   - App works fully whether you enable learning or not
   - No features locked behind data sharing

---

## ⚙️ Managing Your Data

### Viewing Learning Data

**Access Statistics:**

1. Open Kindle TTS Reader
2. Tap ⚙️ (Settings icon)
3. Navigate to **学習パターン統計** (Learning Pattern Statistics)

**Information shown:**
```
学習パターン数: 47
最終更新: 2025-12-20 14:30:25
ストレージ使用量: 12 KB
```

---

### Enabling/Disabling Learning

**Toggle Learning Feature:**

**Path:** Settings → ローカル学習機能 → Toggle switch

**When enabled (ON):**
- ✅ Learns from corrections
- ✅ Improves accuracy over time
- ✅ Faster processing (cached patterns)
- ✅ Reduces API calls

**When disabled (OFF):**
- ✅ No pattern storage
- ✅ Still fully functional
- ✅ Uses ML Kit + Gemini API only
- ⚠️ Slightly slower (more API calls)

---

### Deleting Learning Data

**Complete Data Deletion:**

**Path:** Settings → すべてのデータを削除 (Delete All Data) → Confirm

**What happens:**
1. All 100 patterns permanently deleted
2. Encryption keys destroyed
3. Storage space freed
4. Cannot be undone

**When to delete:**
- Selling or gifting device
- Accumulated too many incorrect patterns
- Privacy concerns
- Starting fresh with new book genre

---

### Resetting All App Data

**Nuclear option - Deletes EVERYTHING:**

**Path:** Android Settings → Apps → Kindle TTS Reader → Storage → Clear Data

**What gets deleted:**
- Learning patterns
- App settings
- Consent preferences
- Cached corrections
- All temporary data

**What survives:**
- Nothing - complete reset

**Use case:**
- Troubleshooting severe bugs
- Complete privacy wipe
- Preparing device for sale

---

## 🌐 Third-Party Services

### Gemini API (Google)

**Purpose:** AI-powered OCR text correction

**Data sent:**
- ✅ OCR-extracted text only (when confidence < 0.7)
- ❌ NOT sent: Images, book metadata, user identity

**Example request:**
```json
{
  "contents": [{
    "parts": [{"text": "Please correct: この本は面曰い"}]
  }]
}
```

**Google's data handling:**
- Governed by [Google Cloud Terms](https://cloud.google.com/terms)
- API requests logged for billing/diagnostics
- Not used to train public models (per Google Cloud Data Processing Agreement)
- Retained for 30 days, then auto-deleted

**How to opt-out:**
- Use offline-only mode (v1.2 planned)
- Currently: Learning patterns reduce API calls

---

### Google ML Kit (On-Device)

**Purpose:** OCR text recognition

**Data sent:**
- ❌ Nothing - completely offline

**Processing:**
- 100% on-device
- No network requests
- No telemetry to Google

**Privacy guarantee:**
- Images never leave device
- Text processed locally

---

### Android System Services

**TextToSpeech (TTS) API:**
- On-device speech synthesis
- No data sent to Google
- Uses locally downloaded voice packs

**AccessibilityService API:**
- System-level permission
- Used only for page turning gestures
- No data collection

**MediaProjection API:**
- Screen capture permission
- Temporary in-memory capture
- No screenshots saved

---

## ✅ GDPR Compliance

### Compliance Checklist

Kindle TTS Reader v1.1.0 fully complies with GDPR:

- ✅ **Lawful basis:** User consent (Article 6)
- ✅ **Consent mechanism:** Explicit opt-in dialog
- ✅ **Data minimization:** Only essential data collected (Article 5)
- ✅ **Purpose limitation:** Data used only for OCR improvement (Article 5)
- ✅ **Storage limitation:** LRU cache with 100 pattern max (Article 5)
- ✅ **Accuracy:** Auto-updates with new corrections (Article 5)
- ✅ **Integrity and confidentiality:** AES256-GCM encryption (Article 32)
- ✅ **Accountability:** This privacy policy + GDPR-specific policies (Article 5)
- ✅ **Transparency:** Clear consent dialog + privacy policies (Articles 12-14)
- ✅ **Data subject rights:** Full access, deletion, portability (Articles 15-20)
- ✅ **Data protection by design:** Privacy-first architecture (Article 25)
- ✅ **No data transfers:** All processing on-device (Chapter V not applicable)

---

### Consent Management

**Initial consent (v1.1.0+):**

On first app launch, users see:

```
┌─────────────────────────────────────────┐
│   ローカル学習機能について                  │
│                                         │
│   このアプリはOCR精度を向上させるため、      │
│   あなたの修正内容から学習できます。         │
│                                         │
│   ✅ すべてのデータは端末内に保存           │
│   ✅ AES256-GCM暗号化で完全保護            │
│   ✅ いつでもオン/オフ切り替え可能           │
│   ✅ ワンタップで全データ削除可能            │
│                                         │
│   [同意する]  [同意しない]                │
└─────────────────────────────────────────┘
```

**Characteristics of valid consent:**
- ✅ **Freely given** - App works without consent
- ✅ **Specific** - Clear purpose stated
- ✅ **Informed** - Full disclosure provided
- ✅ **Unambiguous** - Explicit action required (button tap)
- ✅ **Withdrawable** - Can disable in Settings anytime

---

## 👶 Children's Privacy

### COPPA Compliance (US)

**COPPA** (Children's Online Privacy Protection Act) applies to users under 13.

**Our position:**
- App does not knowingly collect data from children under 13
- No age verification implemented (app doesn't collect age)
- Parents/guardians should supervise children's use

**Recommendation:**
- Parents should review this privacy guide
- Enable parental controls on Android device
- Monitor app usage for young children

---

### GDPR Child Protection (EU)

Under GDPR Article 8:

- Children under 16 (or 13-16 depending on member state) need parental consent for data processing
- We recommend parental supervision for users under 16
- Parents can exercise data rights on child's behalf

---

## 🔄 Changes to Privacy Practices

### Notification Policy

**If privacy practices change materially:**

1. **In-app notification** - Alert on next app launch
2. **Privacy policy update** - New version published
3. **Consent re-collection** - If required for new features
4. **Changelog** - Detailed in release notes

**Minor changes** (e.g., clarifications, typos):
- Updated privacy policy only
- No active notification

---

### Version History

| Version | Date | Changes |
|---------|------|---------|
| **1.1.0** | 2025-12-18 | Added local learning system with encryption |
| **1.0.0** | 2025-11-XX | Initial release - Basic OCR + TTS |

---

## 📞 Privacy Questions & Contact

### Data Protection Officer

**Email:** privacy@smartnavipro.dev

**Response time:** Within 48 hours for privacy inquiries

---

### Privacy Concerns

**If you have concerns about:**
- Data collection practices
- Security vulnerabilities
- GDPR compliance
- Consent management

**Contact us:**
- 📧 Email: privacy@smartnavipro.dev
- 🐛 GitHub Issues (for non-sensitive matters): https://github.com/smartnavipro-dev/kindle-tts-reader/issues
- 🔒 Security issues: security@smartnavipro.dev (confidential)

---

### Regulatory Authorities

**EU users:**
- Your local data protection authority
- Full list: https://edpb.europa.eu/about-edpb/board/members_en

**California users:**
- California Attorney General: https://oag.ca.gov/privacy

---

## 📚 Related Resources

- 📄 [Full Privacy Policy](../PRIVACY_POLICY.md)
- 📄 [プライバシーポリシー (日本語)](../PRIVACY_POLICY_ja.md)
- 📖 [Setup Guide](SETUP_GUIDE.md)
- ❓ [Extended FAQ](FAQ_EXTENDED.md)
- 🔧 [Troubleshooting Guide](TROUBLESHOOTING.md)

---

## ✅ Privacy Commitment

**We promise:**

1. **Transparency** - No hidden data collection
2. **Control** - You own your data
3. **Security** - Military-grade encryption
4. **Minimalism** - Collect only what's needed
5. **Respect** - Your privacy is non-negotiable

**We will never:**

1. ❌ Sell your data
2. ❌ Share without consent
3. ❌ Collect unnecessarily
4. ❌ Use dark patterns
5. ❌ Hide our practices

---

**Your privacy, your control. 🔒**

---

*Last updated: 2025-12-20*
*Document version: 1.0*
*App version: 1.1.0*
*GDPR compliance verified: 2025-12-20*
