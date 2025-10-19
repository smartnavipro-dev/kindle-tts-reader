# 方式A: Kindle APKテスト - 実行ガイド

**作成日**: 2025年10月11日
**目的**: 実際のKindleアプリでのOCR + TTS + 自動ページめくり統合テスト
**ステータス**: APK入手方法記録済み

---

## 📋 テスト目的

実際のKindleアプリ画面でOCR + TTS + 自動ページめくりの完全な統合テストを実施し、実用シナリオでの動作を検証します。

---

## 🔍 Kindle APK入手方法

### オプション1: APKMirrorから手動ダウンロード（推奨）

**最新バージョン情報** (2025年10月時点):
- **バージョン**: 8.132.0.100 (2.0.55111.0)
- **ファイルサイズ**: 111.3 MB
- **要件**: Android 9.0+ (API 28)、arm64-v8a
- **対応**: Android 15 (API 35)

**ダウンロード手順**:

1. ブラウザで APKMirror にアクセス:
   ```
   https://www.apkmirror.com/apk/amazon-mobile-llc/amazon-kindle/
   ```

2. 最新の arm64-v8a (Android 9.0+) バージョンを選択

3. ダウンロードページで **"Download APK"** ボタンをクリック

4. ダウンロード完了後、以下のディレクトリに配置:
   ```
   C:\Users\chanc\Downloads\kindle-8.132.0.100.apk
   ```

⚠️ **注意**: APKMirrorは直接curlダウンロードをブロックしているため、ブラウザからの手動ダウンロードが必要です。

---

### オプション2: Aurora Store経由（オープンソース代替）

Aurora StoreはGoogle Play Storeの匿名クライアントです。

**手順**:

1. Aurora Store APKをダウンロード:
   ```
   https://gitlab.com/AuroraOSS/AuroraStore/-/releases
   ```

2. Aurora Storeをエミュレータにインストール:
   ```bash
   adb -e install aurora-store-xxx.apk
   ```

3. Aurora Storeを起動し、"Kindle" を検索

4. Kindleアプリをダウンロード・インストール

---

### オプション3: Google Play Store（エミュレータにGoogle Services有効の場合）

エミュレータでGoogle Play Servicesが有効な場合:

1. Play Storeアプリを開く
2. "Kindle"を検索
3. インストール

---

## 📦 Kindle APKインストール手順

### 前提条件

- ✅ エミュレータ起動中
- ✅ Kindle APKダウンロード済み

### インストールコマンド

```bash
# APKをエミュレータにインストール
adb -e install -r "C:\Users\chanc\Downloads\kindle-8.132.0.100.apk"

# インストール確認
adb -e shell pm list packages | grep kindle
```

**期待される出力**:
```
package:com.amazon.kindle.ebook
```

---

## 🚀 Kindleアプリ初期設定

### ステップ1: Kindleアプリを起動

```bash
adb -e shell am start -n com.amazon.kindle.ebook/.StartupActivity
```

### ステップ2: 初期設定（手動）

エミュレータ画面で:

1. **言語選択**: 日本語を選択
2. **サインイン**:
   - オプションA: Amazonアカウントでログイン（推奨）
   - オプションB: 「スキップ」→ サンプル書籍のみ使用
3. **権限付与**: ストレージアクセス権限を許可

---

## 📚 サンプル書籍の準備

### 無料サンプル書籍の取得方法

#### 方法1: 青空文庫（著作権フリー）

青空文庫の作品はパブリックドメインで自由に使用可能です。

**推奨書籍**:
- 夏目漱石「こころ」
- 芥川龍之介「羅生門」
- 宮沢賢治「銀河鉄道の夜」

**取得手順**:
1. Kindleストアで「青空文庫」を検索
2. 無料書籍を選択してダウンロード

#### 方法2: Kindle Unlimited無料体験

Amazonアカウントでログインしている場合:

1. Kindle Unlimited対象書籍を検索
2. 「読み放題で読む」を選択
3. 30日間無料体験を開始

#### 方法3: 無料サンプルのダウンロード

ほぼ全ての Kindle書籍で無料サンプルが利用可能:

1. Kindleストアで任意の書籍を検索
2. 「サンプルをダウンロード」を選択
3. 最初の章がダウンロードされる

---

## 🧪 統合テスト実行手順

### 前提条件確認

- ✅ Kindleアプリインストール済み
- ✅ サンプル書籍ダウンロード済み
- ✅ Kindle TTS Readerインストール済み
- ✅ 全権限付与完了

### テスト実行ステップ

#### ステップ1: Kindleで書籍を開く

```bash
# Kindleアプリを起動
adb -e shell am start -n com.amazon.kindle.ebook/.StartupActivity
```

**手動操作**:
- ライブラリからテスト書籍を選択
- 書籍を開いて最初のページを表示

#### ステップ2: Kindle TTS Readerを起動

```bash
# Kindle TTS Readerを起動
adb -e shell am start -n com.kindletts.reader/.MainActivity
```

#### ステップ3: 画面キャプチャ権限を付与（初回のみ）

**手動操作**:
1. 「読み上げ開始」ボタンをタップ
2. 「他のアプリの上に重ねて表示」権限ダイアログで「今すぐ開始」をタップ
3. フローティングコントロールが表示されることを確認

#### ステップ4: OCR + TTS実行

**手動操作**:
1. Kindleアプリに戻る（タスクスイッチャーまたは戻るボタン）
2. 書籍ページが表示されていることを確認
3. フローティングボタンの「キャプチャ」アイコンをタップ
4. OCRが実行され、テキストが認識される
5. TTSで読み上げが開始される

#### ステップ5: 自動ページめくりのテスト

**期待される動作**:
1. 1ページ分の読み上げが完了
2. 2秒待機
3. 自動的に次のページへ移動
4. 新しいページのOCRが実行される
5. 読み上げが継続される

---

## 📊 テスト検証項目

### 基本機能テスト

- [  ] Kindleアプリ起動成功
- [  ] 書籍ダウンロード成功
- [  ] ページ表示正常
- [  ] OCR認識実行
- [  ] テキスト認識成功
- [  ] TTS読み上げ開始
- [  ] 音声出力確認
- [  ] 読み上げ完了

### ページめくりテスト

- [  ] 自動ページめくり動作
- [  ] ページめくり成功率 >90%
- [  ] 次ページOCR実行
- [  ] 連続動作（10ページ）

### パフォーマンステスト

- [  ] OCR処理時間 <3秒
- [  ] ページめくり応答時間 <1秒
- [  ] メモリ使用量安定
- [  ] クラッシュなし

---

## 📈 ログ監視とデバッグ

### リアルタイムログ監視

```bash
# 全Kindle TTSログ
adb -e logcat -s "KindleTTS_Service:D" "KindleTTS_MainActivity:D" "KindleTTS_AutoPageTurn:D"

# OCR処理ログのみ
adb -e logcat -s "KindleTTS_Service:D" | grep -E "OCR|Text"

# ページめくりログ
adb -e logcat -s "KindleTTS_AutoPageTurn:D"
```

### ログ保存

```bash
# 全ログを保存
adb -e logcat -d > kindle_integration_test_log.txt

# フィルタ済みログ
adb -e logcat -d -s "KindleTTS:*" > kindle_tts_test.txt
```

---

## 🐛 既知の課題と対処法

### 課題1: Kindle画面保護機能

**症状**: 一部の書籍でスクリーンショットが制限される

**原因**: DRM保護により、特定の書籍は画面キャプチャが制限されている

**対処**:
- DRM保護のない書籍（青空文庫等）を使用
- 無料サンプルを使用（一部DRM制限が緩い）

**確認方法**:
```bash
# スクリーンショット テスト
adb -e shell screencap -p > test_screenshot.png

# 画像が真っ黒の場合、DRM保護が有効
```

### 課題2: Kindle UI変更による自動ページめくり失敗

**症状**: ページめくりコマンドが動作しない

**原因**: Kindleアプリの更新により、UIコンポーネントIDが変更された

**対処**:
```bash
# UI階層をダンプして確認
adb -e shell uiautomator dump
adb -e shell cat /sdcard/window_dump.xml | grep -i "page"

# AutoPageTurnService.ktのタップ座標を更新
```

### 課題3: OCR精度が低い

**症状**: Kindleの独特なフォントやレイアウトでOCR精度が低下

**原因**:
- Kindleアプリ固有のフォントレンダリング
- ページ余白やヘッダー/フッターの影響

**対処**:
1. Kindle設定でフォントサイズを大きくする
2. 画面の明るさを最大にする
3. 背景色を白に設定

---

## 📝 テスト結果テンプレート

```markdown
### 方式A: Kindle APK統合テスト結果

**実行日時**: YYYY-MM-DD HH:MM
**環境**: Pixel 8 Pro エミュレータ (API 36)
**Kindleバージョン**: 8.132.0.100
**テスト書籍**: [書籍名]

#### 基本機能
- Kindle起動: [成功/失敗]
- OCR認識: [成功/失敗]
- TTS動作: [成功/失敗]

#### ページめくり
- 自動ページめくり: [成功/失敗]
- 連続動作: [ページ数]
- 成功率: XX%

#### パフォーマンス
- OCR処理時間: X.X秒/ページ
- メモリ使用量: XXX MB
- クラッシュ: [有/無]

#### 総合評価
[合格/不合格]: [理由]

#### 観察されたエラー
```
[エラーログ]
```

#### 改善提案
[提案内容]
```

---

## ✅ テスト完了基準

### 合格条件

- [  ] Kindleアプリで書籍を正常に表示
- [  ] OCR認識精度 >70%（Kindle画面特有の条件を考慮）
- [  ] TTS読み上げ正常動作
- [  ] 自動ページめくり成功率 >80%
- [  ] 10ページ連続動作成功
- [  ] クラッシュなし

### 不合格条件

- Kindleアプリ起動失敗
- OCR認識失敗
- ページめくり完全不動作
- 頻繁なクラッシュ

---

## 🔗 関連ドキュメント

- [包括的テスト計画](COMPREHENSIVE_TEST_PLAN.md)
- [方式B: 最適化画像テスト](METHOD_B_MANUAL_TEST_GUIDE.md)
- [開発ログ](DEVELOPMENT_LOG.md)

---

## 💡 次のステップ

### 即座に実行可能

1. ブラウザでAPKMirrorにアクセス
2. Kindle APKをダウンロード
3. エミュレータにインストール
4. 青空文庫の無料書籍をダウンロード
5. 統合テスト実行

### 将来の改善

- [ ] Kindle特有のフォント/レイアウトに最適化したOCR前処理
- [ ] DRM保護書籍への対応研究
- [ ] Kindleアプリバージョン互換性テスト
- [ ] より精密な自動ページめくりロジック

---

**作成者**: Claude Code
**最終更新**: 2025年10月11日
**ステータス**: APK入手待ち - 手動ダウンロード必要
