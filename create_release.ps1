# GitHub Release v1.0.46 自動作成スクリプト (PowerShell)

$ErrorActionPreference = "Stop"

# 設定
$repoOwner = "smartnavipro-dev"
$repoName = "kindle-tts-reader"
$tagName = "v1.0.46"
$releaseName = "Release v1.0.46: Gemini 2.5 Flash Migration"
$apkFile = "C:\Users\<user>\kindle-tts-reader-v1.0.46-release.apk"

# リリース説明文
$releaseBody = @"
# 🚀 Kindle TTS Reader v1.0.46

**リリース日**: 2025年11月28日

## 🎯 主な変更内容

### Gemini 2.5 Flash への移行

このリリースでは、Google Gemini APIを最新の安定版に移行しました。

**主要な変更**:
- ✅ Gemini API SDK: ``0.1.2`` → ``0.9.0``
- ✅ AIモデル: ``gemini-1.5-flash`` → ``gemini-2.5-flash``
- ✅ 型安全性の改善（null安全性）

## 📊 パフォーマンス

| 項目 | v1.0.45 | v1.0.46 | 変化 |
|------|---------|---------|------|
| **ビルド時間** | 47秒 | 24秒 | ⚡ **-49%** |
| **APKサイズ** | 23MB | 38MB | +65% |
| **警告数** | 16件 | 15件 | -1件 |

## 🔧 技術的詳細

### SDK更新
``````gradle
implementation 'com.google.ai.client.generativeai:generativeai:0.9.0'
``````

### モデル変更
``````kotlin
modelName = "gemini-2.5-flash"
``````

## ⚠️ 注意事項

1. **APKサイズ増加**: SDK更新により 23MB → 38MB (+65%)
2. **SDK廃止予定**: ``generativeai:0.9.0`` は将来的にFirebase SDKへの移行を推奨
3. **互換性**: Android 5.0 (API 21) 以上

## 📦 ダウンロード

**APKファイル**: ``kindle-tts-reader-v1.0.46-release.apk`` (38MB)

### インストール方法

#### ADBでインストール
``````bash
adb install -r kindle-tts-reader-v1.0.46-release.apk
``````

#### アクセシビリティ権限の設定
``````bash
adb shell settings put secure enabled_accessibility_services \
  com.kindletts.reader/com.kindletts.reader.AutoPageTurnService
adb shell settings put secure accessibility_enabled 1
``````

## 🔗 変更履歴

詳細は [RELEASE_NOTES_v1.0.46.md](https://github.com/smartnavipro-dev/kindle-tts-reader/blob/main/RELEASE_NOTES_v1.0.46.md) を参照してください。

## 📝 コミット

- **メインコミット**: [``10be8d8``](https://github.com/smartnavipro-dev/kindle-tts-reader/commit/10be8d8) - Release v1.0.46: Gemini 2.5 Flash migration
- **ドキュメント**: [``9417f8f``](https://github.com/smartnavipro-dev/kindle-tts-reader/commit/9417f8f) - docs: Add release notes for v1.0.46

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
"@

Write-Host "=" * 80
Write-Host "GitHub Release v1.0.46 作成"
Write-Host "=" * 80
Write-Host ""
Write-Host "リポジトリ: $repoOwner/$repoName"
Write-Host "タグ: $tagName"
Write-Host "APK: $apkFile"
Write-Host ""

# APKファイルの存在確認
if (-not (Test-Path $apkFile)) {
    Write-Host "❌ エラー: APKファイルが見つかりません: $apkFile" -ForegroundColor Red
    exit 1
}

$apkSize = (Get-Item $apkFile).Length / 1MB
Write-Host "✅ APKファイル確認: $([math]::Round($apkSize, 2)) MB" -ForegroundColor Green
Write-Host ""

# GitHub APIエンドポイント
$apiUrl = "https://api.github.com/repos/$repoOwner/$repoName/releases"

Write-Host "手動での作成が必要です:" -ForegroundColor Yellow
Write-Host ""
Write-Host "📝 手順:" -ForegroundColor Cyan
Write-Host "1. ブラウザで以下のURLを開く:"
Write-Host "   https://github.com/$repoOwner/$repoName/releases/new?tag=$tagName"
Write-Host ""
Write-Host "2. 'Release title' に入力:"
Write-Host "   $releaseName"
Write-Host ""
Write-Host "3. 'Describe this release' に以下をコピー&ペースト:"
Write-Host "   (RELEASE_DESCRIPTION_v1.0.46.txt の内容)"
Write-Host ""
Write-Host "4. APKファイルをドラッグ&ドロップ:"
Write-Host "   $apkFile"
Write-Host ""
Write-Host "5. '☑ Set as the latest release' をチェック"
Write-Host ""
Write-Host "6. 'Publish release' をクリック"
Write-Host ""
Write-Host "=" * 80
Write-Host ""

# ブラウザで開く
$url = "https://github.com/$repoOwner/$repoName/releases/new?tag=$tagName&title=$([uri]::EscapeDataString($releaseName))"
Write-Host "ブラウザを開いています..." -ForegroundColor Green
Start-Process $url

Write-Host ""
Write-Host "✅ 準備完了！上記の手順に従ってReleaseを作成してください。" -ForegroundColor Green
