# 📱 実機テスト実行ガイド - 詳細手順

## 🎯 実機テストの実行方法

### **Step 1: Android デバイスの準備**

#### 1.1 必要なもの
- Android スマートフォン/タブレット (Android 5.0以上)
- USBケーブル (デバイス ↔ PC接続用)
- Kindle アプリ (Google Play からインストール)

#### 1.2 開発者オプションの有効化
```
設定 → デバイス情報 → ビルド番号を7回タップ
→ "開発者になりました" と表示される
```

#### 1.3 USBデバッグの有効化
```
設定 → 開発者向けオプション → USBデバッグ をON
```

#### 1.4 PC接続の確認
```bash
# コマンドプロンプトで実行
C:\Users\chanc\AppData\Local\Android\Sdk\platform-tools\adb.exe devices
```

**期待する出力:**
```
List of devices attached
XXXXXXXXX	device
```

---

### **Step 2: APK インストール (自動)**

#### 2.1 自動インストールスクリプト実行
```bash
# 作成済みスクリプトを実行
C:\Users\chanc\KindleTTSReader\install_and_test.bat
```

#### 2.2 手動インストール (代替方法)
```bash
# APKを手動インストール
adb install "C:\Users\chanc\KindleTTSReader\app\build\outputs\apk\debug\app-debug.apk"
```

---

### **Step 3: アプリ起動とセットアップ**

#### 3.1 アプリ起動
```bash
# アプリを起動
adb shell am start -n com.kindletts.reader/.MainActivity
```

#### 3.2 権限設定 (重要!)

**📱 デバイス画面で以下を実行:**

##### A) オーバーレイ権限
```
1. Kindle TTS Reader で "画面キャプチャ権限" ボタンをタップ
2. 設定画面が開く → Kindle TTS Reader を探す
3. "他のアプリの上に表示" を ON にする
4. 戻るボタンでアプリに戻る
```

##### B) アクセシビリティ権限
```
1. "アクセシビリティ権限" ボタンをタップ
2. 設定 → ユーザー補助 画面が開く
3. "Kindle TTS Reader" を探してタップ
4. サービスを ON にする
5. 確認ダイアログで "OK" をタップ
```

---

### **Step 4: Kindle アプリの準備**

#### 4.1 Kindle アプリインストール
```
Google Play ストア → "Kindle" で検索 → インストール
```

#### 4.2 本をダウンロード
```
1. Kindle アプリを開く
2. Amazon アカウントでログイン
3. 適当な本をダウンロード (無料本でOK)
4. 本を開いて読める状態にする
```

---

### **Step 5: 実機テスト実行**

#### 5.1 基本動作テスト

**🎯 テスト手順:**
```
1. Kindle TTS Reader を起動
2. "読み上げ開始" ボタンをタップ
3. 画面キャプチャ権限を許可
4. Kindle アプリに切り替え
5. 本のページを開く
6. Kindle TTS Reader に戻る
7. "読み上げ開始" を再度タップ
```

**✅ 期待する動作:**
- オーバーレイUIが表示される
- OCRでテキストが認識される
- 音声読み上げが開始される
- 自動でページがめくられる

#### 5.2 詳細機能テスト

**A) OCR精度テスト**
```
1. 日本語テキストのページで実行
2. 英語テキストのページで実行
3. 図や画像が多いページで実行
```

**B) TTS品質テスト**
```
1. 音量・速度の調整確認
2. 読み上げの一時停止・再開
3. 異なる言語での読み上げ
```

**C) ページめくりテスト**
```
1. 自動ページめくりの動作
2. 手動の「次のページ」「前のページ」
3. Kindleアプリでの反応確認
```

---

### **Step 6: ログ監視とデバッグ**

#### 6.1 リアルタイムログ確認
```bash
# ログ監視開始 (別のコマンドプロンプトで)
adb logcat -s KindleTTS_Service KindleTTS_AutoPageTurn KindleTTS_Main
```

#### 6.2 重要なログメッセージ
```
✅ 正常動作:
KindleTTS_Service: OverlayService starting
KindleTTS_Service: OCR processing started
KindleTTS_Service: Text recognized: [認識されたテキスト]
KindleTTS_Service: TTS speaking started
KindleTTS_AutoPageTurn: Next page gesture completed

❌ エラー例:
KindleTTS_Service: Error: 権限が許可されていません
KindleTTS_AutoPageTurn: Gesture API not supported
```

---

### **Step 7: トラブルシューティング**

#### 7.1 よくある問題と解決法

**問題: アプリが起動しない**
```
原因: APKインストールエラー
解決: adb uninstall com.kindletts.reader 後に再インストール
```

**問題: 権限設定画面が開かない**
```
原因: Android バージョンの違い
解決: 設定 → アプリ → Kindle TTS Reader → 権限 で手動設定
```

**問題: OCRが動作しない**
```
原因: 画面キャプチャ権限なし
解決: オーバーレイ権限を確実に許可
```

**問題: 音声が出ない**
```
原因: TTS エンジン未設定
解決: 設定 → 言語と入力 → テキスト読み上げ で確認
```

**問題: ページめくりが効かない**
```
原因: アクセシビリティ権限なし or Kindle未対応
解決: ユーザー補助設定を確認、Kindleアプリバージョン確認
```

#### 7.2 高度なデバッグ

**メモリ使用量確認:**
```bash
adb shell dumpsys meminfo com.kindletts.reader
```

**CPU使用率確認:**
```bash
adb shell top -n 5 | grep kindletts
```

**権限状態確認:**
```bash
adb shell dumpsys package com.kindletts.reader | grep permission
```

---

### **Step 8: テスト結果の記録**

#### 8.1 テスト項目チェックリスト

**基本機能:**
- [ ] アプリ起動
- [ ] UI表示
- [ ] 権限取得
- [ ] サービス開始

**読み上げ機能:**
- [ ] 画面キャプチャ
- [ ] OCR認識
- [ ] TTS音声合成
- [ ] オーバーレイ表示

**ページめくり機能:**
- [ ] 自動ページめくり
- [ ] 手動ページ操作
- [ ] Kindle連携

**エラーハンドリング:**
- [ ] 権限なし動作
- [ ] Kindleアプリなし
- [ ] 長時間動作

#### 8.2 パフォーマンス測定
```
測定項目:
- 初回起動時間: ___ 秒
- OCR処理時間: ___ 秒
- メモリ使用量: ___ MB
- バッテリー消費: ___% / 時間
```

---

## 🚨 **重要な注意事項**

### ⚖️ **法的・倫理的考慮**
- **個人利用のみ**: 商用利用禁止
- **著作権遵守**: 適切な利用範囲内で
- **プライバシー**: 画面データは一時的使用のみ

### 🔒 **セキュリティ**
- アクセシビリティ権限は強力な権限です
- 使用後は権限を無効化することを推奨
- 信頼できない環境では使用を控える

### ⚡ **パフォーマンス**
- 長時間使用時はバッテリー消費に注意
- 他のアプリに影響する場合は使用を控える
- 定期的にアプリを再起動することを推奨

---

## 📞 **サポート情報**

### 🐛 **問題報告**
テスト中に問題が発生した場合:
1. エラーログをコピー
2. 発生状況を詳細に記録
3. デバイス情報を含めて報告

### 📚 **追加リソース**
- `TESTING_GUIDE.md`: 詳細テスト手順
- `COMPREHENSIVE_TEST_REPORT.md`: 品質分析結果
- `PROJECT_COMPLETION_REPORT.md`: プロジェクト全体概要

---

**🎯 これで実機テストを開始できます！**

上記手順に従って、段階的にテストを実行してください。問題が発生した場合は、ログを確認しながらトラブルシューティングを実施します。

---
🔧 Generated with [Claude Code](https://claude.ai/code)