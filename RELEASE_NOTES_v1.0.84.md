# Kindle TTS Reader v1.0.84 リリースノート

**リリース日**: 2025年12月18日
**ビルド時間**: 1分2秒
**APKサイズ**: 82MB

---

## 🎯 主な変更内容

### API Quota Management 完全実装

**背景**:
- Gemini API の無料枠は1日1,500リクエスト制限
- ユーザーが使用量を把握できる仕組みが必要
- クォータ超過を事前に防止する機能が求められていた

**主要な変更**:

#### Phase 1: QuotaManager Core
```kotlin
// 永続化ストレージによるクォータ管理
- SharedPreferences による永続化
- 24時間自動リセット機能
- スレッドセーフな実装
```

**効果**: アプリ再起動後もクォータ情報を保持

#### Phase 2: MainActivity UI統合
```kotlin
// リアルタイムクォータ表示
- パーセンテージ表示 (例: 45%)
- プログレスバーでの視覚化
- 色遷移によるステータス表示
  - 緑 (0-50%): 余裕あり
  - 黄 (50-80%): 注意
  - 赤 (80-100%): 警告
```

**効果**: ユーザーが一目で使用状況を把握可能

#### Phase 3: WorkManager通知
```kotlin
// バックグラウンド監視
- 15分間隔でクォータチェック
- クォータリセット時に通知
- 80%超過時に警告通知
```

**効果**: アプリを開いていなくてもクォータ管理が可能

#### Phase 4: UI/UX改善
```kotlin
// スムーズなアニメーション
- フェードイン/アウト効果
- 色遷移アニメーション (300ms)
- タップ可能な設定画面への導線
```

**効果**: 直感的で洗練されたユーザー体験

---

## 📊 パフォーマンス

### ビルド時間
| バージョン | ビルド時間 | 変化 |
|-----------|-----------|------|
| v1.0.83 | 58秒 | - |
| v1.0.84 | 62秒 | +7% |

**理由**: WorkManager依存関係の追加

### APKサイズ
| バージョン | サイズ | 変化 |
|-----------|--------|------|
| v1.0.83 | 80MB | - |
| v1.0.84 | 82MB | +2.5% |

**理由**: WorkManager ライブラリの追加

### コンパイル警告
| バージョン | 警告数 | 変化 |
|-----------|-------|------|
| v1.0.83 | 24件 | - |
| v1.0.84 | 24件 | 変更なし |

---

## 🔧 技術的詳細

### 依存関係の変更

#### 追加されたライブラリ
```gradle
// WorkManager for background quota monitoring
implementation 'androidx.work:work-runtime-ktx:2.9.0'
```

### 新規ファイル

#### 1. **QuotaManager.kt**
クォータ管理のコアロジック
- 使用回数の追跡
- 24時間リセット機能
- SharedPreferences永続化

#### 2. **QuotaResetWorker.kt**
バックグラウンド監視ワーカー
- 15分間隔のチェック
- 通知の送信
- リセット処理

#### 3. **QuotaSettingsActivity.kt**
設定画面
- 詳細なクォータ情報表示
- リセット日時の確認
- 手動リセット機能

#### 4. **activity_quota_settings.xml**
設定画面レイアウト
- マテリアルデザイン準拠
- カード形式の情報表示

---

## ✅ 検証結果

### ビルドテスト
- ✅ **ビルド成功**: 62秒
- ✅ **エラー**: 0件
- ✅ **警告**: 24件 (すべて非致命的)

### セキュリティチェック
- ✅ **APIキー漏洩**: なし
- ✅ **.gitignore**: 正しく設定済み
- ✅ **local.properties**: Git除外済み
- ✅ **ドキュメント**: プレースホルダー修正完了

### 機能テスト
- ✅ **クォータ表示**: 正常動作
- ✅ **色遷移**: スムーズ
- ✅ **通知機能**: 動作確認済み
- ✅ **永続化**: 再起動後も保持

---

## 🆕 新機能

### 1. リアルタイムクォータ表示
メイン画面にAPI使用状況を常時表示
- パーセンテージ表示
- プログレスバー
- 色による視覚的フィードバック

### 2. クォータ設定画面
詳細なクォータ情報を確認可能
- 現在の使用回数
- 総クォータ数
- リセット予定時刻
- 手動リセットボタン

### 3. バックグラウンド通知
アプリを閉じていても監視
- クォータリセット通知
- 80%超過警告
- 15分間隔の自動チェック

### 4. 自動リセット機能
24時間ごとに自動リセット
- 最終リセット時刻を保存
- 次回リセット時刻を計算
- タイムゾーン対応

---

## 🔒 セキュリティ改善

### .gitignore 完全化
```gitignore
# APIキー保護
local.properties

# ビルドログ除外
build_*.log

# Claude Code設定除外
.claude/

# スクリーンショット除外
screenshots/*.png
screenshots/*.jpg
```

### APIキー分離
- local.propertiesに完全分離
- BuildConfigで安全に参照
- Gitリポジトリから完全除外

---

## 📦 ダウンロード

### リリースAPK
- **ファイル名**: `kindle-tts-reader-v1.0.84-release.apk`
- **サイズ**: 82MB
- **署名**: ✅ リリースキーで署名済み

### GitHubからダウンロード
```bash
# GitHub Releaseページからダウンロード
https://github.com/smartnavipro-dev/kindle-tts-reader/releases/tag/v1.0.84
```

### インストール方法

#### 実機にインストール
```bash
adb install -r kindle-tts-reader-v1.0.84-release.apk
```

#### アクセシビリティサービスを有効化
```bash
adb shell settings put secure enabled_accessibility_services \
  com.kindletts.reader/com.kindletts.reader.AutoPageTurnService
adb shell settings put secure accessibility_enabled 1
```

---

## 📝 アップグレードガイド

### v1.0.83 → v1.0.84

1. **バックアップ** (推奨)
   - アプリデータのバックアップは不要 (互換性あり)
   - クォータデータは自動的に移行

2. **インストール**
   ```bash
   adb install -r kindle-tts-reader-v1.0.84-release.apk
   ```

3. **新機能の確認**
   - メイン画面のクォータ表示を確認
   - 設定画面でクォータ情報を確認
   - 通知権限を許可（初回のみ）

4. **動作確認**
   - アプリを起動
   - クォータ表示が正しいか確認
   - 設定画面を開いて詳細情報を確認

---

## 🚀 今後の予定

### v1.0.85 (予定)
1. **クォータ履歴機能**
   - 日別・週別の使用統計
   - グラフ表示
   - エクスポート機能

2. **カスタマイズ機能**
   - クォータ閾値の変更
   - 通知タイミングの調整
   - 色テーマのカスタマイズ

### v1.1.0 (長期)
1. **プレミアム機能**
   - 複数APIキー対応
   - ローカルOCR補正（オフライン）
   - 高度な統計機能

2. **UI/UX改善**
   - ダークモード対応
   - 多言語対応（英語・中国語）
   - アクセシビリティ改善

---

## ⚠️ 既知の問題

### 1. 初回通知権限
**問題**: Android 13+で通知権限の許可が必要
**影響**: 初回起動時にのみ発生
**対策**: アプリ起動時に権限リクエストダイアログを表示

### 2. WorkManager遅延
**問題**: バックグラウンドチェックが最大15分遅延する可能性
**原因**: Androidのバッテリー最適化
**影響**: 軽微（通知が若干遅れる程度）
**対策**: システム設定で「バッテリー最適化」を無効化（任意）

---

## 🔗 参考リンク

### Gemini API関連
- [Gemini API Pricing](https://ai.google.dev/pricing)
- [Gemini API Quotas](https://ai.google.dev/gemini-api/docs/quota)
- [Free Tier Limits](https://ai.google.dev/pricing#1_5flash-free)

### Android WorkManager
- [WorkManager Guide](https://developer.android.com/topic/libraries/architecture/workmanager)
- [Scheduling Work](https://developer.android.com/topic/libraries/architecture/workmanager/how-to/define-work)
- [Background Work Best Practices](https://developer.android.com/topic/performance/background-work)

---

## 💬 フィードバック

バグ報告や機能リクエストは、GitHub Issuesまでお願いします。
- 🐛 [Bug Reports](https://github.com/smartnavipro-dev/kindle-tts-reader/issues)
- 💡 [Feature Requests](https://github.com/smartnavipro-dev/kindle-tts-reader/discussions)

---

## 📋 変更履歴サマリー

```
v1.0.84 (2025-12-18)
├── Phase 1: QuotaManager Core (永続化, 24時間リセット)
├── Phase 2: MainActivity UI統合 (パーセント表示, アニメーション)
├── Phase 3: WorkManager通知 (15分間隔チェック)
├── Phase 4: UI/UX改善 (フェードアニメーション, 色遷移)
├── セキュリティ: .gitignore完全化, API key分離
├── Build: 58s → 62s (+7%)
├── APK: 80MB → 82MB (+2.5%)
└── Dependencies: +WorkManager 2.9.0
```

---

## 📄 ライセンス

MIT License

---

## 👨‍💻 貢献者

**開発**: Claude Sonnet 4.5 (AI Development Assistant)
**プロジェクト**: SmartNaviPro Development
**リリース日**: 2025年12月18日

---

**🤖 このリリースノートは [Claude Code](https://claude.com/claude-code) で生成されました**

**Co-Authored-By**: Claude Sonnet 4.5 <noreply@anthropic.com>
