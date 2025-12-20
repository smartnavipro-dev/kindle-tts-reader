# GitHub Personal Access Token セットアップガイド

## 📋 必要な理由

GitHub APIを使用してリリースを自動作成するには、Personal Access Token（PAT）が必要です。

---

## 🔑 トークンの作成手順

### **ステップ1: GitHubの設定ページにアクセス**

1. ブラウザで以下のURLを開く:
   ```
   https://github.com/settings/tokens
   ```

2. または、GitHub画面の右上のプロフィールアイコン → **Settings** → 左サイドバーの **Developer settings** → **Personal access tokens** → **Tokens (classic)**

---

### **ステップ2: 新しいトークンを生成**

1. **「Generate new token」**ボタンをクリック
2. プルダウンから **「Generate new token (classic)」**を選択

---

### **ステップ3: トークンの設定**

#### **Note（メモ）**
```
Kindle TTS Reader Release Automation
```

#### **Expiration（有効期限）**
- **推奨**: 30 days（30日間）
- または: No expiration（無期限）※セキュリティリスクあり

#### **Select scopes（権限の選択）**
✅ **repo** - Full control of private repositories
  - ✅ repo:status
  - ✅ repo_deployment
  - ✅ public_repo
  - ✅ repo:invite
  - ✅ security_events

**注意**: `repo`のチェックボックスを1つ選択すると、サブ項目すべてが自動選択されます。

---

### **ステップ4: トークンを生成**

1. ページ下部の **「Generate token」**ボタンをクリック
2. **トークンが表示されます**（緑色の背景に`ghp_`で始まる文字列）

⚠️ **重要**: このトークンは**1度だけ**表示されます。必ずコピーして安全な場所に保存してください！

---

## 💾 トークンの保存方法

### **方法1: 環境変数に設定（推奨）**

PowerShellで以下を実行:
```powershell
# 現在のセッションのみ有効
$env:GITHUB_TOKEN = "ghp_your_token_here"

# 永続的に設定（システム全体）
[System.Environment]::SetEnvironmentVariable("GITHUB_TOKEN", "ghp_your_token_here", "User")
```

### **方法2: スクリプト実行時に入力**

環境変数を設定しない場合、スクリプト実行時にプロンプトでトークンを入力します。

---

## 🚀 スクリプトの実行

トークンを取得したら、以下のコマンドを実行:

```powershell
cd C:\Users\chanc\KindleTTSReader
powershell.exe -ExecutionPolicy Bypass -File recreate_release_v1.1.0.ps1
```

---

## 🔒 セキュリティ注意事項

- ✅ トークンは**絶対に公開リポジトリにコミットしない**
- ✅ トークンを他人と共有しない
- ✅ 使用後は定期的にトークンを再生成する
- ✅ 不要になったトークンは削除する（https://github.com/settings/tokens）

---

## ❓ トラブルシューティング

### **エラー: "Bad credentials"**
→ トークンが無効または期限切れです。新しいトークンを生成してください。

### **エラー: "Resource not accessible by integration"**
→ `repo` スコープが選択されていません。トークンを再生成してください。

### **エラー: "Not Found"**
→ リポジトリ名またはオーナー名が間違っています。スクリプト内の設定を確認してください。

---

## 🔗 参考リンク

- [GitHub公式ドキュメント: Personal Access Tokens](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token)
- [GitHub REST API: Releases](https://docs.github.com/en/rest/releases/releases)

---

**作成日**: 2025-12-20
**対象スクリプト**: `recreate_release_v1.1.0.ps1`
