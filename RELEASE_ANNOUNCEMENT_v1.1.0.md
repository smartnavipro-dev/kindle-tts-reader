# 📢 Kindle TTS Reader v1.1.0 リリースアナウンスメント

**リリース日**: 2025年12月18日
**バージョン**: v1.1.0

---

## 🎉 **メジャーアップデート: ローカル学習システム搭載！**

Kindle TTS Reader v1.1.0をリリースしました！
今回のアップデートでは、**プライバシー第一設計のローカル学習機能**を搭載し、OCR精度を大幅に向上させました。

---

## 🌟 **主な新機能**

### 1. 🧠 **ローカル学習システム**
- ユーザーの修正内容から学習してOCR精度を向上
- **すべてのデータは端末内に保存**（外部送信なし）
- AES256-GCM暗号化で完全保護
- Android Keystoreによるハードウェア保護鍵

### 2. 🔒 **プライバシー管理**
- 初回起動時の同意ダイアログ
- 包括的なプライバシーポリシー（日本語・英語）
- いつでもオン/オフ切り替え可能
- ワンタップで全学習データを削除

### 3. ⚙️ **設定画面**
- 学習機能の詳細管理
- パターン統計の表示
- プライバシーポリシー閲覧
- データ削除機能

### 4. 🌍 **多言語対応**
- 日本語・英語の完全サポート
- 自動言語選択
- ローカライズされた日時表示

---

## 🔐 **セキュリティ & プライバシー**

| 項目 | 詳細 |
|------|------|
| **暗号化** | AES256-GCM (EncryptedSharedPreferences) |
| **鍵保管** | Android Keystore（ハードウェア保護） |
| **バックアップ** | Google Driveバックアップから除外 |
| **GDPR準拠** | ✅ 完全対応 |

---

## 📦 **ダウンロード**

### **APKファイル**
- **ファイル名**: `kindle-tts-reader-v1.1.0-release.apk`
- **サイズ**: 83MB
- **ダウンロード**: [GitHub Releases](https://github.com/smartnavipro-dev/kindle-tts-reader/releases/tag/v1.1.0)

### **要件**
- Android 5.0 (API 21) 以上
- アクセシビリティ権限（自動ページめくり用）
- 画面キャプチャ権限（OCR用）

---

## 🔄 **アップグレード方法**

### **v1.0.84からのアップグレード**
1. v1.1.0 APKをダウンロード
2. 既存のアプリに上書きインストール
3. 初回起動時に同意ダイアログが表示されます
4. 既存の設定とAPI残量はすべて保持されます

**破壊的変更はありません** - すべての既存機能はこれまで通り動作します！

---

## 📊 **技術的ハイライト**

```
追加コード行数: 約2,000行
新規ファイル: 10個
追加依存関係: 3個
  - androidx.security:security-crypto:1.1.0-alpha06
  - com.google.code.gson:gson:2.10.1
  - androidx.preference:preference-ktx:1.2.1
```

---

## 🔗 **リンク**

| リソース | URL |
|---------|-----|
| **GitHub Release** | https://github.com/smartnavipro-dev/kindle-tts-reader/releases/tag/v1.1.0 |
| **リポジトリ** | https://github.com/smartnavipro-dev/kindle-tts-reader |
| **APKダウンロード** | https://github.com/smartnavipro-dev/kindle-tts-reader/releases/download/v1.1.0/kindle-tts-reader-v1.1.0-release.apk |
| **プライバシーポリシー（日本語）** | https://github.com/smartnavipro-dev/kindle-tts-reader/blob/main/PRIVACY_POLICY_ja.md |
| **プライバシーポリシー（英語）** | https://github.com/smartnavipro-dev/kindle-tts-reader/blob/main/PRIVACY_POLICY.md |

---

## 💬 **SNS用テキスト**

### **Twitter / X (280文字以内)**

```
📱 Kindle TTS Reader v1.1.0 リリース！

🎉 プライバシー第一のローカル学習システム搭載
🔒 AES256-GCM暗号化で完全保護
⚙️ 設定画面で詳細管理
🌍 日英両言語対応

すべてのデータは端末内のみ保存。外部送信なし！

ダウンロード👇
https://github.com/smartnavipro-dev/kindle-tts-reader/releases/tag/v1.1.0

#KindleTTS #Android #OCR #TTS #プライバシー #オープンソース
```

### **GitHub Discussion / Reddit**

```
# Kindle TTS Reader v1.1.0 Released! 🎉

We're excited to announce the release of v1.1.0 with a major new feature: **Privacy-First Local Learning System**!

## Highlights:
- 🧠 Learn from your corrections to improve OCR accuracy
- 🔒 All data encrypted with AES256-GCM and stored on device only
- ⚙️ New Settings screen for granular control
- 🌍 Full Japanese and English support
- 📜 Comprehensive privacy policies

## Download:
https://github.com/smartnavipro-dev/kindle-tts-reader/releases/tag/v1.1.0

All existing features work as before, with zero breaking changes!
```

---

## 🙏 **謝辞**

このリリースは法的リスク評価における**オプションA: ローカル学習のみ**を実装したもので、ユーザープライバシーとGDPRコンプライアンスを最優先しています。

包括的な調査に基づく実装：
- ✅ 日本著作権法第30条の4により機械学習の利用が許可
- ✅ Amazon Assistive Reader（2025年）がアクセシビリティユースケースを検証
- ✅ GDPR準拠の同意取得とデータ管理

---

## 📞 **サポート & フィードバック**

- 🐛 **バグ報告**: [GitHub Issues](https://github.com/smartnavipro-dev/kindle-tts-reader/issues)
- 💡 **機能要望**: [GitHub Discussions](https://github.com/smartnavipro-dev/kindle-tts-reader/discussions)
- 📧 **プライバシー関連**: privacy@smartnavipro.dev
- 🐦 **Twitter**: [@smartnavipro](https://twitter.com/smartnavipro)

---

## 🔜 **次のバージョン（v1.2）**

- 多言語UIサポート（スペイン語、フランス語、ドイツ語）
- 読書統計ダッシュボード
- カスタムTTS音声オプション
- 学習パターンのエクスポート/インポート

---

**🤖 Generated with [Claude Code](https://claude.com/claude-code)**

**Co-Authored-By**: Claude Sonnet 4.5 <noreply@anthropic.com>
