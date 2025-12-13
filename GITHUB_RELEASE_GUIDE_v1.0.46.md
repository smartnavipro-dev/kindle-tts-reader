# GitHub Release v1.0.46 作成ガイド

## 📋 準備完了項目

✅ コードをGitHubにプッシュ済み
✅ v1.0.46 タグをプッシュ済み
✅ リリースAPKをビルド済み (`kindle-tts-reader-v1.0.46-release.apk`, 38MB)
✅ リリースノート作成済み (`RELEASE_NOTES_v1.0.46.md`)

---

## 🌐 GitHub Release作成手順

### 1. GitHubリポジトリにアクセス

**URL**: https://github.com/smartnavipro-dev/kindle-tts-reader

### 2. Releasesページに移動

1. リポジトリのトップページで **"Releases"** をクリック
2. **"Draft a new release"** ボタンをクリック

### 3. リリース情報を入力

#### タグの選択
- **Tag version**: `v1.0.46` (既にプッシュ済み)
- ドロップダウンから `v1.0.46` を選択

#### リリースタイトル
```
Release v1.0.46: Gemini 2.5 Flash Migration
```

#### リリース説明文

以下の内容をコピー＆ペースト：

```markdown
# 🚀 Kindle TTS Reader v1.0.46

**リリース日**: 2025年11月28日

## 🎯 主な変更内容

### Gemini 2.5 Flash への移行

このリリースでは、Google Gemini APIを最新の安定版に移行しました。

**主要な変更**:
- ✅ Gemini API SDK: `0.1.2` → `0.9.0`
- ✅ AIモデル: `gemini-1.5-flash` → `gemini-2.5-flash`
- ✅ 型安全性の改善（null安全性）

## 📊 パフォーマンス

| 項目 | v1.0.45 | v1.0.46 | 変化 |
|------|---------|---------|------|
| **ビルド時間** | 47秒 | 24秒 | ⚡ **-49%** |
| **APKサイズ** | 23MB | 38MB | +65% |
| **警告数** | 16件 | 15件 | -1件 |

## 🔧 技術的詳細

### SDK更新
```gradle
implementation 'com.google.ai.client.generativeai:generativeai:0.9.0'
```

### モデル変更
```kotlin
modelName = "gemini-2.5-flash"
```

## ⚠️ 注意事項

1. **APKサイズ増加**: SDK更新により 23MB → 38MB (+65%)
2. **SDK廃止予定**: `generativeai:0.9.0` は将来的にFirebase SDKへの移行を推奨
3. **互換性**: Android 5.0 (API 21) 以上

## 📦 ダウンロード

**APKファイル**: `kindle-tts-reader-v1.0.46-release.apk` (38MB)

### インストール方法

#### ADBでインストール
```bash
adb install -r kindle-tts-reader-v1.0.46-release.apk
```

#### アクセシビリティ権限の設定
```bash
adb shell settings put secure enabled_accessibility_services \
  com.kindletts.reader/com.kindletts.reader.AutoPageTurnService
adb shell settings put secure accessibility_enabled 1
```

## 🔗 変更履歴

詳細は [RELEASE_NOTES_v1.0.46.md](https://github.com/smartnavipro-dev/kindle-tts-reader/blob/main/RELEASE_NOTES_v1.0.46.md) を参照してください。

## 📝 コミット

- **メインコミット**: [`10be8d8`](https://github.com/smartnavipro-dev/kindle-tts-reader/commit/10be8d8) - Release v1.0.46: Gemini 2.5 Flash migration
- **ドキュメント**: [`9417f8f`](https://github.com/smartnavipro-dev/kindle-tts-reader/commit/9417f8f) - docs: Add release notes for v1.0.46

## 🌟 次のバージョン予定

### v1.0.47 (予定)
- APKサイズ最適化 (ProGuard設定)
- コードクリーンアップ
- 残り警告の修正

### v1.1.0 (長期)
- Firebase Vertex AI SDK への移行
- 新機能追加
- パフォーマンス最適化

---

**🤖 このリリースは [Claude Code](https://claude.com/claude-code) で生成されました**

**Co-Authored-By**: Claude <noreply@anthropic.com>
```

### 4. APKファイルをアップロード

1. **"Attach binaries by dropping them here or selecting them"** セクションを見つける
2. 以下のファイルをドラッグ＆ドロップまたは選択:
   - `C:\Users\chanc\kindle-tts-reader-v1.0.46-release.apk` (38MB)

### 5. リリース設定

#### オプション設定
- ☐ **Set as a pre-release** - チェックしない（正式リリースのため）
- ☐ **Set as the latest release** - チェックする（最新版として表示）
- ☐ **Create a discussion for this release** - お好みで

### 6. リリース公開

**"Publish release"** ボタンをクリック

---

## ✅ 確認事項

リリース公開後、以下を確認：

1. ✅ タグが正しく表示されているか（v1.0.46）
2. ✅ APKファイルがダウンロード可能か
3. ✅ リリースノートが正しく表示されているか
4. ✅ "Latest" バッジが付いているか

---

## 📱 リリースURL

公開後のURL:
```
https://github.com/smartnavipro-dev/kindle-tts-reader/releases/tag/v1.0.46
```

---

## 🔄 リリース後の作業

1. **README更新** (オプション)
   - 最新バージョンへのリンクを更新
   - ダウンロードバッジを更新

2. **ユーザーへの通知**
   - リリースノートを共有
   - 変更内容を周知

3. **次のバージョンへの準備**
   - v1.0.47の開発計画
   - フィードバックの収集

---

## 📞 サポート

問題がある場合:
- GitHub Issues: https://github.com/smartnavipro-dev/kindle-tts-reader/issues
- リリースノート: `RELEASE_NOTES_v1.0.46.md`

---

**作成日**: 2025年11月28日
**バージョン**: 1.0.46
**ビルド**: 49
