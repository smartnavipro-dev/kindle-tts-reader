#!/bin/bash

# GitHub Release v1.0.46 自動作成スクリプト

REPO_OWNER="smartnavipro-dev"
REPO_NAME="kindle-tts-reader"
TAG_NAME="v1.0.46"
RELEASE_NAME="Release v1.0.46: Gemini 2.5 Flash Migration"
APK_FILE="/c/Users/chanc/kindle-tts-reader-v1.0.46-release.apk"

# リリース説明文
RELEASE_BODY=$(cat <<'EOF'
# 🚀 Kindle TTS Reader v1.0.46

**リリース日**: 2025年11月28日

## 🎯 主な変更内容

### Gemini 2.5 Flash への移行

このリリースでは、Google Gemini APIを最新の安定版に移行しました。

**主要な変更**:
- ✅ Gemini API SDK: \`0.1.2\` → \`0.9.0\`
- ✅ AIモデル: \`gemini-1.5-flash\` → \`gemini-2.5-flash\`
- ✅ 型安全性の改善（null安全性）

## 📊 パフォーマンス

| 項目 | v1.0.45 | v1.0.46 | 変化 |
|------|---------|---------|------|
| **ビルド時間** | 47秒 | 24秒 | ⚡ **-49%** |
| **APKサイズ** | 23MB | 38MB | +65% |
| **警告数** | 16件 | 15件 | -1件 |

## 🔧 技術的詳細

### SDK更新
\`\`\`gradle
implementation 'com.google.ai.client.generativeai:generativeai:0.9.0'
\`\`\`

### モデル変更
\`\`\`kotlin
modelName = "gemini-2.5-flash"
\`\`\`

## ⚠️ 注意事項

1. **APKサイズ増加**: SDK更新により 23MB → 38MB (+65%)
2. **SDK廃止予定**: \`generativeai:0.9.0\` は将来的にFirebase SDKへの移行を推奨
3. **互換性**: Android 5.0 (API 21) 以上

## 📦 ダウンロード

**APKファイル**: \`kindle-tts-reader-v1.0.46-release.apk\` (38MB)

### インストール方法

#### ADBでインストール
\`\`\`bash
adb install -r kindle-tts-reader-v1.0.46-release.apk
\`\`\`

#### アクセシビリティ権限の設定
\`\`\`bash
adb shell settings put secure enabled_accessibility_services \\
  com.kindletts.reader/com.kindletts.reader.AutoPageTurnService
adb shell settings put secure accessibility_enabled 1
\`\`\`

## 🔗 変更履歴

詳細は [RELEASE_NOTES_v1.0.46.md](https://github.com/smartnavipro-dev/kindle-tts-reader/blob/main/RELEASE_NOTES_v1.0.46.md) を参照してください。

## 📝 コミット

- **メインコミット**: [\`10be8d8\`](https://github.com/smartnavipro-dev/kindle-tts-reader/commit/10be8d8) - Release v1.0.46: Gemini 2.5 Flash migration
- **ドキュメント**: [\`9417f8f\`](https://github.com/smartnavipro-dev/kindle-tts-reader/commit/9417f8f) - docs: Add release notes for v1.0.46

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
EOF
)

echo "GitHub Release v1.0.46 を作成します..."
echo "リポジトリ: ${REPO_OWNER}/${REPO_NAME}"
echo "タグ: ${TAG_NAME}"
echo ""

# GitHub CLIの確認
if command -v gh &> /dev/null; then
    echo "GitHub CLIを使用してReleaseを作成..."

    # Releaseを作成
    gh release create "${TAG_NAME}" \
        --repo "${REPO_OWNER}/${REPO_NAME}" \
        --title "${RELEASE_NAME}" \
        --notes "${RELEASE_BODY}" \
        "${APK_FILE}"

    if [ $? -eq 0 ]; then
        echo "✅ Release作成成功！"
        echo "URL: https://github.com/${REPO_OWNER}/${REPO_NAME}/releases/tag/${TAG_NAME}"
    else
        echo "❌ Release作成失敗"
        exit 1
    fi
else
    echo "⚠️ GitHub CLIが見つかりません"
    echo ""
    echo "手動での作成手順:"
    echo "1. https://github.com/${REPO_OWNER}/${REPO_NAME}/releases/new にアクセス"
    echo "2. Tag: ${TAG_NAME} を選択"
    echo "3. Title: ${RELEASE_NAME}"
    echo "4. Description: RELEASE_DESCRIPTION_v1.0.46.txt の内容をコピー"
    echo "5. APKファイル (${APK_FILE}) をアップロード"
    echo "6. 'Publish release' をクリック"
    exit 1
fi
