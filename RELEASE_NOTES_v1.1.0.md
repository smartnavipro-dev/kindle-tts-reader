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

---
---
---

# 変更履歴 - v1.1.0 (2025-12-18)

## 🎉 メジャー機能: ローカル学習システム

このリリースでは、**プライバシー優先のローカル学習機能**を導入しました。ユーザーの修正内容から学習してOCR精度を向上させます—すべてのデータは端末内に安全に保存されます。

---

## ✨ 新機能

### 📚 ローカル学習システム
- **プライバシー優先設計**: すべての学習データはAES256-GCM暗号化で端末内にのみ保存
- **スマートパターンマッチング**: レーベンシュタイン距離アルゴリズムによる95%類似度判定
- **LRUキャッシュ**: 自動メモリ管理による高速検索（最大100パターン）
- **スレッドセーフ操作**: ReentrantReadWriteLockによるデータ整合性保証

### 🔒 プライバシーと同意
- **初回起動時の同意ダイアログ**: データ収集の明確な説明
- **プライバシーポリシー閲覧**: 日本語・英語で全文表示
- **詳細なコントロール**: 学習機能をいつでも有効/無効化可能
- **データ削除**: ワンタップですべての学習データを削除

### ⚙️ 設定画面
- **学習機能トグル**: ローカル学習の有効/無効を切り替え
- **統計表示**: 学習パターン数と最終更新日時を表示
- **プライバシーポリシーアクセス**: 設定から素早くアクセス
- **データ管理**: 確認ダイアログ付きで全学習データを削除

### 🌍 多言語対応
- 日本語・英語の完全サポート
- 端末ロケールに基づく自動言語選択
- 日時フォーマットのローカライズ

---

## 🔐 セキュリティ強化

| 項目 | 実装内容 |
|------|---------|
| **暗号化** | AES256-GCM (EncryptedSharedPreferences) |
| **鍵保管** | Android Keystore（ハードウェア保護） |
| **バックアップ** | Google Driveバックアップから除外 |
| **フォールバック** | root化端末では非暗号化SharedPreferences使用 |

---

## 📋 技術詳細

### 新規コンポーネント
- `PrivacyPreferences`: ユーザー同意状態の管理
- `PrivacyConsentDialog`: Material Design 3準拠の同意ダイアログ
- `PrivacyPolicyActivity`: Markdownからプライバシーポリシーを表示
- `LocalCorrectionManager`: パターンマッチング機能を持つ学習エンジン
- `SettingsActivity`: PreferenceFragmentベースの設定画面

### 追加された依存関係
```gradle
implementation 'androidx.security:security-crypto:1.1.0-alpha06'
implementation 'com.google.code.gson:gson:2.10.1'
implementation 'androidx.preference:preference-ktx:1.2.1'
```

### データ構造
学習パターンはJSON形式で保存：
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

## 🎨 UI変更

### MainActivity
- アクセシビリティボタンの横に**「設定」ボタン**を追加
- 初回起動時にプライバシー同意ダイアログを表示
- 下部ボタンレイアウトを改善（50/50分割）

### 設定画面
- 学習機能セクションとトグルスイッチ
- パターン数と最終更新日時の表示
- プライバシーポリシー閲覧機能
- 確認ダイアログ付きデータ削除機能

---

## 📝 プライバシーポリシー

**重要**: このリリースには日本語・英語の包括的なプライバシーポリシーが含まれています：
- `/PRIVACY_POLICY.md`（英語）
- `/PRIVACY_POLICY_ja.md`（日本語）

主要ポイント：
- ✅ データは端末内のみに保存
- ✅ 外部サーバーには送信しません
- ✅ 第三者と共有しません
- ✅ いつでも削除可能
- ⚠️ Gemini API（OCR補正）はOCRテキストをGoogleに送信（学習データは送信しません）

---

## 🚀 パフォーマンス

- **パターン検索**: HashMapによる完全一致はO(1)
- **類似検索**: レーベンシュタイン距離による曖昧マッチングはO(n)
- **メモリ使用量**: LRUキャッシュでメモリ内パターンを100件に制限
- **ストレージ**: アプリプライベートストレージに暗号化JSONファイルとして保存

---

## 🐛 バグ修正

このリリースではなし（新機能のみ）。

---

## 📦 インストール

### 要件
- Android 5.0（API 21）以上
- アクセシビリティ権限（自動ページめくり用）
- 画面キャプチャ権限（OCR用）

### v1.0.84からのアップグレード
1. v1.1.0 APKをインストール（既存のインストールに上書き）
2. 初回起動時に同意（または拒否）
3. 既存の設定とAPI残量はすべて保持されます

---

## 🔄 移行ノート

### v1.0.84 → v1.1.0
- **破壊的変更なし**: すべての既存機能はこれまで通り動作します
- **新規SharedPreferences**: `privacy_prefs`と`user_corrections_encrypted`
- **プライバシーダイアログ**: アップグレード後の初回起動時のみ表示
- **学習機能はデフォルトで無効**: 設定で明示的に有効化する必要があります

---

## 📖 ドキュメント

- [プライバシーポリシー（英語）](PRIVACY_POLICY.md)
- [プライバシーポリシー（日本語）](PRIVACY_POLICY_ja.md)
- [法的リスク評価](LEGAL_RISK_ASSESSMENT.md)
- [実装決定ガイド](IMPLEMENTATION_DECISION.md)
- [プライバシーUI設計](docs/PRIVACY_UI_DESIGN.md)

---

## 🙏 謝辞

このリリースは法的リスク評価における**オプションA: ローカル学習のみ**を実装したもので、ユーザープライバシーとGDPRコンプライアンスを最優先しています。

包括的な調査に基づく実装：
- ✅ 日本著作権法第30条の4により機械学習の利用が許可
- ✅ Amazon Assistive Reader（2025年）がアクセシビリティユースケースを検証
- ✅ GDPR準拠の同意取得とデータ管理

---

## 📊 統計

| 指標 | 数値 |
|------|------|
| **追加コード行数** | 約2,000行 |
| **新規作成ファイル** | 10個 |
| **追加依存関係** | 3個 |
| **多言語対応** | 英語 + 日本語 |
| **ビルド時間** | 約1分21秒 |

---

## 🤖 [Claude Code](https://claude.com/claude-code)で生成

**Co-Authored-By**: Claude Sonnet 4.5 <noreply@anthropic.com>

---

## 🔗 リンク

- **GitHubリポジトリ**: [smartnavipro-dev/kindle-tts-reader](https://github.com/smartnavipro-dev/kindle-tts-reader)
- **問題報告**: [GitHub Issues](https://github.com/smartnavipro-dev/kindle-tts-reader/issues)
- **プライバシー連絡先**: privacy@smartnavipro.dev

---

**完全な変更履歴**: [v1.0.84...v1.1.0](https://github.com/smartnavipro-dev/kindle-tts-reader/compare/v1.0.84...v1.1.0)
