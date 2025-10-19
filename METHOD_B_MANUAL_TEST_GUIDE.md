# 方式B: 最適化画像OCRテスト - 手動実行ガイド

**作成日**: 2025年10月11日
**目的**: 制御された環境での正確なOCR精度測定
**ステータス**: ✅ 準備完了 - 手動実行待ち

---

## 📋 準備完了項目

- ✅ 最適化画像作成済み: `テストチェック用_final_optimized.jpg` (94%認識率期待値)
- ✅ エミュレータに転送済み: `/sdcard/Pictures/test_ocr_final.jpg`
- ✅ Kindle TTS Readerインストール済み
- ✅ 全権限付与完了 (画面キャプチャ ✓, アクセシビリティ ✓)
- ✅ TTS初期化完了 (日本語対応)
- ✅ エミュレータ起動中: Pixel 8 Pro (API 36)

---

## 🎯 テスト実行手順

### ステップ1: 最適化画像を全画面表示

エミュレータで最適化画像を開く:

```bash
adb -e shell am start -a android.intent.action.VIEW \
  -d "file:///sdcard/Pictures/test_ocr_final.jpg" \
  -t "image/*"
```

**期待結果**: 画像ビューアーが起動し、最適化されたテスト画像が表示される

---

### ステップ2: Kindle TTS Readerを起動

```bash
adb -e shell am start -n com.kindletts.reader/.MainActivity
```

**期待結果**:
- アプリが起動
- ステータス: "準備完了"
- 権限ボタン: "画面キャプチャ権限 ✓" "アクセシビリティ権限 ✓"

---

### ステップ3: 画面キャプチャ権限リクエスト（**手動操作必須**）

⚠️ **重要**: この手順は自動化できません。MediaProjection APIのセキュリティ制約により、ユーザーの明示的な承認が必要です。

**手順**:
1. エミュレータ画面で「読み上げ開始」ボタンをタップ
2. システムダイアログ「他のアプリの上に重ねて表示」が表示される
3. 「今すぐ開始」ボタンをタップして承認

**期待結果**:
- 画面キャプチャが開始される
- OverlayServiceが起動
- フローティングコントロールボタンが表示される

---

### ステップ4: OCR実行

**手順**:
1. エミュレータで画像ビューアーに戻る（戻るボタンまたはタスクスイッチャー）
2. 画像が全画面表示されていることを確認
3. フローティングボタンの「キャプチャ」アイコンをタップ

**自動実行する場合**（OverlayService起動後）:
```bash
# 読み上げ開始コマンドを送信
adb -e shell am startservice \
  -n com.kindletts.reader/.OverlayService \
  -a START_READING \
  --ef reading_speed 1.0 \
  --ez auto_page_turn true
```

---

### ステップ5: ログ監視とOCR結果取得

別のターミナルでログ監視:

```bash
# リアルタイムログ監視
adb -e logcat -s "KindleTTS_Service:D" "KindleTTS_MainActivity:D"

# OCR処理ログのみ
adb -e logcat -s "KindleTTS_Service:D" | grep -E "OCR|Text"

# TTS動作ログ
adb -e logcat -s "KindleTTS_Service:D" | grep "TTS"
```

**期待されるログ出力**:
```
KindleTTS_Service: Performing OCR
KindleTTS_Service: OCR success: Text length: 450
KindleTTS_Service: Speaking sentences: Count: 8
KindleTTS_Service: Speaking sentence 0: 私中学生だった一九七〇年代には...
KindleTTS_Service: TTS started: 0
KindleTTS_Service: TTS completed: 0
```

---

### ステップ6: OCR結果の抽出と検証

OCR結果をファイルに保存:

```bash
adb -e logcat -d > full_test_log.txt
```

OCR認識テキストを抽出:

```bash
# ログからOCR結果を抽出
grep "Speaking sentence" full_test_log.txt

# または特定の範囲を抽出
adb -e logcat -d -s "KindleTTS_Service:D" | \
  grep -A 1 "Speaking sentence" > ocr_recognized_text.txt
```

---

## 📊 認識精度の計算

### 正解テキスト（最初の8行）

```
私中学生だった一九七〇年代には、世界の経済は不調に陥っていた。平日はロウソ
クを灯して宿題をしたこと、二〇％以上のインフレに苦しめられたことを覚えている。
当時の印象が強烈だったため、経済が人々の生活にいかに重大な影響をおよぼすかとい
うことが心に焼きつけられた。
そんな私が経済学の学習を熱心に推奨するのは当然のなりゆきと言えるだろう。経済
学の基本的な知識を身につけるだけでも役に立つ。身近なところで言えば、GDP（国
内総生産）やQE(量的緩和)といった略語の意味を理解できるようになるし、経済が
成長する理由や停滞する理由を説明できるようになる。
```

### 認識率計算方法

1. **文字レベル一致率**:
   ```python
   correct_chars = 正解テキストの文字数
   recognized_chars = 認識されたテキストの文字数
   matching_chars = 一致した文字数

   accuracy = (matching_chars / correct_chars) * 100
   ```

2. **期待値**: 90%以上
3. **目標値**: 94%（最適化により達成見込み）

---

## ✅ テスト完了基準

### 成功条件

- [  ] OCR初期化成功
- [  ] 画面キャプチャ開始成功
- [  ] テキスト認識実行
- [  ] **認識精度90%以上達成**
- [  ] TTS読み上げ開始
- [  ] 音声出力確認
- [  ] エラーなし

### 失敗条件

- OCR初期化失敗
- 画面キャプチャエラー
- 認識精度 <90%
- TTS初期化エラー
- クラッシュ発生

---

## 🐛 トラブルシューティング

### 問題1: 画面キャプチャ権限ダイアログが表示されない

**原因**: 権限が既に付与されているか、OverlayServiceが起動済み

**対処**:
```bash
# サービスを停止して再起動
adb -e shell am force-stop com.kindletts.reader
adb -e shell am start -n com.kindletts.reader/.MainActivity
```

### 問題2: OCRが実行されない

**原因**: MediaProjectionが正しく初期化されていない

**対処**:
```bash
# ログでエラーを確認
adb -e logcat -d -s "KindleTTS_Service:D" | grep -i error

# サービス再起動
adb -e shell am stopservice com.kindletts.reader/.OverlayService
```

### 問題3: 認識精度が低い

**原因**: 画像が正しく表示されていない

**対処**:
1. 画像が全画面表示されているか確認
2. 画像ビューアーのズームレベル確認
3. 画像ファイルの転送状態確認

---

## 📝 テスト結果テンプレート

```markdown
### 方式B: 最適化画像OCRテスト結果

**実行日時**: YYYY-MM-DD HH:MM
**環境**: Pixel 8 Pro エミュレータ (API 36)
**画像**: test_ocr_final.jpg (4314x2898px, 2.4MB)

#### 結果サマリー
- OCR初期化: [成功/失敗]
- 画面キャプチャ: [成功/失敗]
- テキスト認識: [成功/失敗]
- 認識精度: XX.X%
- TTS動作: [成功/失敗]
- 総合評価: [合格/不合格]

#### OCR認識テキスト
```
[認識されたテキストをここに貼り付け]
```

#### 観察されたエラー
```
[エラーログをここに記録]
```

#### 備考
[その他の観察事項]
```

---

## 🔗 関連ドキュメント

- [包括的テスト計画](COMPREHENSIVE_TEST_PLAN.md) - 全テスト方式の概要
- [OCR画像最適化レポート](OCR_IMAGE_OPTIMIZATION_REPORT.md) - 画像前処理の詳細
- [開発ログ](DEVELOPMENT_LOG.md) - プロジェクト全体の進捗

---

## 💡 自動化の限界について

**MediaProjection APIの制約**:
- Androidセキュリティポリシーにより、MediaProjection（画面キャプチャ）の権限付与は**ユーザーの明示的な操作が必須**です
- adb shell input tapコマンドではシステムダイアログをタップできません
- UI Automator Framework も制限されます

**推奨アプローチ**:
1. 初回の権限付与は手動で実行
2. 以降のテストでは、権限が保持されている状態で自動化可能
3. または実機でのテスト自動化（Espresso / UI Automator）を検討

---

**作成者**: Claude Code
**最終更新**: 2025年10月11日
**ステータス**: 準備完了 - 手動実行待ち
