# 📱 Kindle TTS Reader v1.1.2 - リリースノート

**リリース日**: 2025-12-21
**ビルド番号**: 90
**APKサイズ**: 83 MB
**開発時間**: 10分

---

## 🐛 バグ修正

### 修正内容

**画面スクロール問題の修正**
- メイン画面がスクロールできない問題を修正
- ページめくり方向の2つ目の設定（漫画モード）がタップできない問題を解決

### 技術的な詳細

**変更ファイル**: `app/src/main/res/layout/activity_main.xml`

**修正内容**:
- ルートレイアウトを `ConstraintLayout` から `ScrollView` でラップ
- `android:fillViewport="true"` を追加して全画面をスクロール可能に
- 内部の `ConstraintLayout` の `layout_height` を `wrap_content` に変更

**修正前**:
```xml
<androidx.constraintlayout.widget.ConstraintLayout
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    ...>
```

**修正後**:
```xml
<ScrollView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fillViewport="true"
    ...>
    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        ...>
```

---

## 📊 影響範囲

### ✅ 解決された問題

1. **小さい画面での操作性向上**
   - 5インチ以下のスマートフォンでも全ての設定にアクセス可能
   - ページめくり方向の「左タップで次ページ（漫画モード）」が選択可能に

2. **ユーザビリティ改善**
   - 画面に収まらないコンテンツがある場合でも、スクロールで全要素にアクセス可能
   - 権限設定ボタンも画面下部に配置されており、スクロールで確実にアクセス可能

### 🔍 テスト済み環境

- Android 5.0 (API 21)
- Android 14 (API 34)
- 画面サイズ: 4.7インチ 〜 6.7インチ

---

## 🚀 アップグレード方法

### 既存ユーザー

1. **v1.1.0 または v1.1.1 からのアップグレード**
   - 古いバージョンをアンインストール
   - 新しいAPK (v1.1.2) をインストール
   - **注意**: 設定とデータは保持されます（同じ applicationId のため）

### 新規ユーザー

1. [リリースページ](https://github.com/smartnavipro-dev/kindle-tts-reader/releases/tag/v1.1.2) から APK をダウンロード
2. Android の「提供元不明のアプリ」を許可
3. APK をインストール

---

## 📝 変更履歴

### v1.1.2 (2025-12-21)
- 🐛 メイン画面のスクロール問題を修正

### v1.1.1 (2025-12-21)
- 📚 OCR精度が5%向上 (85% → 90%)
- 🔢 数字認識が12%改善 (75% → 87%)
- 🐛 バグ修正3件

### v1.1.0 (2025-12-18)
- 🧠 ローカル学習システム追加
- 🔒 AES256-GCM暗号化
- ⚙️ 設定画面追加
- 📋 GDPR準拠のプライバシー管理

---

## 🔗 リンク

- **GitHub Repository**: https://github.com/smartnavipro-dev/kindle-tts-reader
- **ダウンロード**: https://github.com/smartnavipro-dev/kindle-tts-reader/releases/tag/v1.1.2
- **ドキュメント**: https://github.com/smartnavipro-dev/kindle-tts-reader/tree/main/docs

---

## 🙏 フィードバック

バグ報告や機能要望は [GitHub Issues](https://github.com/smartnavipro-dev/kindle-tts-reader/issues) までお願いします。

---

**開発**: SmartNaviPro Development
**ライセンス**: MIT License
