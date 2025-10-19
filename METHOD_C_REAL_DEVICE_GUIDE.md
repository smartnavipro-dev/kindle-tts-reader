# 方式C: 実機テスト - 完全実行ガイド

**作成日**: 2025年10月11日
**目的**: 実際のAndroid端末での完全な動作検証
**ステータス**: 実行準備完了

---

## 📋 テスト目的

実際のAndroid端末で Kindle TTS Reader の全機能を検証し、実環境でのパフォーマンス、安定性、ユーザビリティを評価します。

---

## 📱 推奨実機環境

### 最小要件

- **OS**: Android 5.0 (Lollipop, API 21) 以上
- **RAM**: 2GB 以上
- **ストレージ**: 空き容量 200MB 以上（アプリ + Kindle + 書籍）
- **画面解像度**: 720p (HD) 以上

### 推奨環境

- **OS**: Android 10.0 以上
- **RAM**: 4GB 以上
- **ストレージ**: 空き容量 500MB 以上
- **画面解像度**: 1080p (Full HD) 以上
- **プロセッサ**: Snapdragon 600シリーズ以上 または 同等品

### テスト済みデバイス

以下のデバイスでの動作確認を推奨:
- Google Pixel シリーズ (Pixel 4 以降)
- Samsung Galaxy シリーズ (S10 以降)
- OnePlus 6 以降
- Xiaomi Mi 9 以降

---

## 🔧 実機の準備

### ステップ1: USB デバッグを有効化

#### Android 10 以降の場合

1. **設定** → **端末情報** を開く
2. **ビルド番号** を7回連続タップ
3. 「開発者になりました」というメッセージが表示される
4. **設定** → **システム** → **開発者向けオプション**
5. **USBデバッグ** を有効化

#### Android 9 以前の場合

1. **設定** → **端末情報**
2. **ビルド番号** を7回タップ
3. **設定** → **開発者向けオプション**
4. **USBデバッグ** を有効化

---

### ステップ2: 実機をPCに接続

```bash
# USBケーブルで実機を接続

# 接続確認
adb devices

# 期待される出力例
List of devices attached
ABC123DEF456    device
```

⚠️ **初回接続時**: 実機の画面に「USBデバッグを許可しますか？」ダイアログが表示されるので、**「常に許可する」** にチェックして **「OK」** をタップ

---

### ステップ3: デバイスIDの確認

```bash
# デバイスIDを取得
adb devices -l

# 出力例
List of devices attached
ABC123DEF456    device product:coral model:Pixel_4 device:coral transport_id:1

# 以降のコマンドで ABC123DEF456 を使用
```

---

## 📦 APKインストール

### リリースAPKの準備

事前にリリースビルドAPKを作成しておく必要があります。

#### APKのビルド（ビルド済みでない場合）

```bash
cd C:\Users\chanc\KindleTTSReader

# リリースビルド
call gradlew.bat clean assembleRelease

# APKパス確認
dir app\build\outputs\apk\release\app-release.apk
```

### 実機にインストール

```bash
# デバイスID指定でインストール
adb -s ABC123DEF456 install -r "C:\Users\chanc\KindleTTSReader\app\build\outputs\apk\release\app-release.apk"

# インストール確認
adb -s ABC123DEF456 shell pm list packages | grep kindletts

# 期待される出力
package:com.kindletts.reader
```

---

## 🔑 権限設定

### 1. オーバーレイ権限（画面上に重ねて表示）

#### 自動付与（推奨）

```bash
adb -s ABC123DEF456 shell appops set com.kindletts.reader SYSTEM_ALERT_WINDOW allow
```

#### 手動設定

1. **設定** → **アプリ** → **Kindle TTS Reader**
2. **権限** → **他のアプリの上に重ねて表示**
3. **許可** に設定

---

### 2. 通知権限（Android 13 以降）

```bash
# Android 13+ の場合
adb -s ABC123DEF456 shell pm grant com.kindletts.reader android.permission.POST_NOTIFICATIONS
```

---

### 3. アクセシビリティサービス

⚠️ **重要**: この権限は手動設定が必須です。

#### 手動設定手順

1. **設定** → **ユーザー補助**
2. **ダウンロードしたアプリ** → **Kindle TTS Reader**
3. **サービスを有効にする** をONにする
4. 警告ダイアログで **「許可」** をタップ

#### 設定確認

```bash
adb -s ABC123DEF456 shell settings get secure enabled_accessibility_services

# 出力に com.kindletts.reader/com.kindletts.reader.AutoPageTurnService が含まれていればOK
```

---

## 📚 Kindleアプリのインストール

### オプション1: Google Play Storeから直接インストール（推奨）

1. 実機でGoogle Play Storeを開く
2. 「Kindle」を検索
3. インストール

### オプション2: APKからインストール

[METHOD_A_KINDLE_APK_GUIDE.md](METHOD_A_KINDLE_APK_GUIDE.md) を参照してKindle APKを入手し:

```bash
adb -s ABC123DEF456 install kindle-8.132.0.100.apk
```

---

## 🧪 完全な使用シナリオテスト

### シナリオ1: 基本動作確認（所要時間: 5分）

#### 目的
アプリの基本機能が正常に動作することを確認

#### 手順

1. **Kindle TTS Readerを起動**
   ```bash
   adb -s ABC123DEF456 shell am start -n com.kindletts.reader/.MainActivity
   ```

2. **ステータス確認**
   - 画面に「準備完了」と表示されるか
   - 権限ボタンに ✓ マークが表示されるか

3. **Kindleアプリで書籍を開く**
   - Kindle起動
   - 任意の書籍を開いて1ページ目を表示

4. **読み上げ開始**
   - Kindle TTS Readerに戻る
   - 「読み上げ開始」ボタンをタップ
   - 画面キャプチャ権限ダイアログで「今すぐ開始」をタップ

5. **OCR + TTS動作確認**
   - Kindleアプリに戻る
   - フローティングボタンの「キャプチャ」アイコンをタップ
   - テキストが認識され、読み上げが始まるか確認

6. **停止**
   - フローティングボタンの「停止」アイコンをタップ

#### 合格基準

- [  ] アプリ起動成功
- [  ] 全権限付与確認
- [  ] OCR認識成功
- [  ] TTS読み上げ成功
- [  ] クラッシュなし

---

### シナリオ2: 連続ページ読み上げ（所要時間: 15分）

#### 目的
自動ページめくり機能の動作検証

#### 手順

1. **Kindleで書籍の最初から開始**

2. **自動ページめくりを有効化**
   - Kindle TTS Reader設定で「自動ページめくり」をON

3. **読み上げ開始**
   - 10ページ連続読み上げを実行

4. **動作観察**
   - 各ページで正しくOCRされているか
   - ページめくりが自動的に行われるか
   - ページめくり後、次のページのOCRが実行されるか

#### 合格基準

- [  ] 10ページ連続動作成功
- [  ] ページめくり成功率 >90%
- [  ] OCR精度（各ページ）>70%
- [  ] クラッシュなし

#### ログ取得

```bash
# リアルタイム監視
adb -s ABC123DEF456 logcat -s "KindleTTS:*"

# ログ保存
adb -s ABC123DEF456 logcat -d > real_device_continuous_test.txt
```

---

### シナリオ3: 長時間動作テスト（所要時間: 30分）

#### 目的
長時間使用時の安定性、メモリリーク、バッテリー消費の検証

#### 手順

1. **初期状態記録**
   ```bash
   # バッテリー残量
   adb -s ABC123DEF456 shell dumpsys battery

   # メモリ使用量
   adb -s ABC123DEF456 shell dumpsys meminfo com.kindletts.reader
   ```

2. **1章分（30-50ページ）を読み上げ**
   - 自動ページめくり有効
   - バックグラウンドで動作させる（他のアプリを使用）

3. **定期的な状態確認（10分ごと）**
   ```bash
   # メモリ使用量の推移
   adb -s ABC123DEF456 shell dumpsys meminfo com.kindletts.reader | grep -E "TOTAL|Native|Dalvik"

   # バッテリー温度
   adb -s ABC123DEF456 shell dumpsys battery | grep temperature
   ```

4. **完了後の状態記録**

#### 合格基準

- [  ] 30分間クラッシュなし
- [  ] メモリ使用量増加 <50MB
- [  ] バッテリー温度上昇 <5°C
- [  ] バッテリー消費 <15%
- [  ] UI応答性維持

---

### シナリオ4: 設定変更テスト（所要時間: 10分）

#### 目的
各種設定の動作確認

#### テスト項目

1. **読み上げ速度変更**
   - 0.5倍、1.0倍、1.5倍、2.0倍で各々テスト
   - 速度が正しく反映されるか確認

2. **自動ページめくりON/OFF**
   - ON: ページめくりが自動で行われる
   - OFF: ページめくりが行われない

3. **一時停止・再開**
   - 一時停止 → TTSが停止
   - 再開 → 続きから読み上げ開始

4. **前のページ/次のページボタン**
   - 手動ページめくり動作確認

#### 合格基準

- [  ] 全設定が正しく反映される
- [  ] 設定変更後もクラッシュなし

---

## 📊 パフォーマンス測定

### CPU使用率

```bash
# CPU使用率を監視
adb -s ABC123DEF456 shell top -m 10 | grep kindletts
```

### メモリ使用量

```bash
# 詳細メモリ情報
adb -s ABC123DEF456 shell dumpsys meminfo com.kindletts.reader

# 簡易表示
adb -s ABC123DEF456 shell dumpsys meminfo com.kindletts.reader | grep -E "TOTAL PSS|Native|Dalvik|Graphics"
```

### バッテリー消費

```bash
# バッテリー統計
adb -s ABC123DEF456 shell dumpsys batterystats com.kindletts.reader

# 消費電力（mAh）
adb -s ABC123DEF456 shell dumpsys batterystats | grep -A 10 "Estimated power use"
```

### 処理時間測定

ログから各処理の所要時間を抽出:

```bash
adb -s ABC123DEF456 logcat -d | grep -E "OCR|performOCR|TTS" | grep -E "success|completed"
```

---

## 🐛 実機特有のトラブルシューティング

### 問題1: USBデバッグが接続できない

**症状**: `adb devices` でデバイスが表示されない

**原因**: USBドライバ未インストール、不正なUSBケーブル

**対処**:
1. 別のUSBケーブルを試す（データ転送対応のもの）
2. USBデバッグモードで接続を許可
3. ADB USBドライバを再インストール
4. PCを再起動

---

### 問題2: 実機でOCR精度が極端に低い

**症状**: エミュレータでは動作するが、実機では認識率が低い

**原因**: 実機のDPI設定、画面解像度の違い

**対処**:
```bash
# 実機の画面情報確認
adb -s ABC123DEF456 shell wm size
adb -s ABC123DEF456 shell wm density

# 必要に応じてOverlayService.ktの画面キャプチャ解像度を調整
```

---

### 問題3: アクセシビリティサービスが無効化される

**症状**: テスト中に自動ページめくりが動作しなくなる

**原因**: 一部のAndroid端末では、省電力モードやバックグラウンド制限によりサービスが停止される

**対処**:
1. **設定** → **バッテリー** → **バッテリー最適化**
2. Kindle TTS Readerを「最適化しない」に設定
3. **設定** → **アプリ** → **Kindle TTS Reader**
4. 「バックグラウンドでの実行」を許可

---

### 問題4: フローティングボタンが表示されない

**症状**: 読み上げ開始後、フローティングコントロールが表示されない

**原因**: オーバーレイ権限が正しく付与されていない

**対処**:
```bash
# 権限再設定
adb -s ABC123DEF456 shell appops set com.kindletts.reader SYSTEM_ALERT_WINDOW allow

# アプリ再起動
adb -s ABC123DEF456 shell am force-stop com.kindletts.reader
adb -s ABC123DEF456 shell am start -n com.kindletts.reader/.MainActivity
```

---

## 📝 実機テスト結果テンプレート

```markdown
### 方式C: 実機テスト結果

**実行日時**: YYYY-MM-DD HH:MM
**デバイス**: [メーカー モデル名]
**Android バージョン**: X.X (API XX)
**RAM**: X GB
**画面解像度**: XXXX x XXXX

#### テストシナリオ実行結果

**シナリオ1: 基本動作**
- 実行: [成功/失敗]
- OCR動作: [成功/失敗]
- TTS動作: [成功/失敗]

**シナリオ2: 連続ページ読み上げ**
- 実行ページ数: XX ページ
- ページめくり成功率: XX%
- OCR平均精度: XX%

**シナリオ3: 長時間動作**
- 動作時間: XX 分
- クラッシュ: [有/無]
- メモリリーク: [有/無]
- バッテリー消費: XX%

**シナリオ4: 設定変更**
- 速度変更: [正常/異常]
- 一時停止・再開: [正常/異常]
- ページめくりON/OFF: [正常/異常]

#### パフォーマンス測定

- 平均CPU使用率: XX%
- 平均メモリ使用量: XXX MB
- OCR処理時間: X.X 秒/ページ
- バッテリー消費率: XX%/時間

#### 総合評価

[合格/不合格]: [理由]

#### 観察された問題

```
[問題の詳細]
```

#### 改善提案

[提案内容]
```

---

## ✅ 実機テスト完了チェックリスト

### 準備

- [  ] 実機にUSBデバッグを有効化
- [  ] adb接続確認
- [  ] Kindle TTS Reader インストール
- [  ] Kindleアプリ インストール
- [  ] 全権限付与完了

### シナリオ実行

- [  ] シナリオ1: 基本動作確認
- [  ] シナリオ2: 連続ページ読み上げ
- [  ] シナリオ3: 長時間動作テスト
- [  ] シナリオ4: 設定変更テスト

### パフォーマンス

- [  ] CPU使用率測定
- [  ] メモリ使用量測定
- [  ] バッテリー消費測定
- [  ] 処理時間測定

### ドキュメント

- [  ] ログ保存
- [  ] テスト結果記録
- [  ] 問題点文書化
- [  ] 改善提案作成

---

## 🔗 関連ドキュメント

- [包括的テスト計画](COMPREHENSIVE_TEST_PLAN.md) - 全テスト方式の概要
- [方式A: Kindle APKテスト](METHOD_A_KINDLE_APK_GUIDE.md) - Kindleアプリ統合テスト
- [方式B: 最適化画像テスト](METHOD_B_MANUAL_TEST_GUIDE.md) - 制御環境でのOCRテスト
- [開発ログ](DEVELOPMENT_LOG.md) - プロジェクト全体の記録

---

## 💡 実機テストの重要性

実機テストは、以下の理由から非常に重要です:

1. **実際のハードウェア環境**: エミュレータでは再現できない実機特有の挙動を検証
2. **パフォーマンス測定**: 実際のCPU/GPU/メモリ使用状況を正確に測定
3. **バッテリー消費**: 実用上重要なバッテリー消費を評価
4. **ユーザー体験**: 実際のタッチ操作、画面サイズでのUI/UX検証
5. **デバイス多様性**: さまざまなメーカー/モデルでの互換性確認

---

**作成者**: Claude Code
**最終更新**: 2025年10月11日
**ステータス**: 実行準備完了
