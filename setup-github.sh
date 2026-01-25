#!/bin/bash
# GitHub リポジトリ作成と初期化スクリプト
# 使用方法: ./setup-github.sh

set -e

REPO_NAME="Romajiswitcher-MCplugin"
REPO_DIR="C:\Users\waras\RomajiSwitcher"

echo "=========================================="
echo "GitHub CLI 認証"
echo "=========================================="
echo ""
echo "GitHub認証をまだ行っていない場合は、以下を実行してください："
echo "  gh auth login"
echo ""
echo "その後、Enterキーを押して続行してください..."
read -p ""

echo ""
echo "=========================================="
echo "リポジトリ作成中..."
echo "=========================================="

cd "$REPO_DIR"

# リポジトリを作成してプッシュ
gh repo create Romajiswitcher-MCplugin \
  --source=. \
  --remote=origin \
  --push \
  --public \
  --description "A lightweight Paper Minecraft plugin that converts romaji to Japanese characters in chat messages"

echo ""
echo "=========================================="
echo "リポジトリ URL 確認中..."
echo "=========================================="

REPO_URL=$(gh repo view Romajiswitcher-MCplugin --json url --jq .url)
echo "✅ リポジトリ作成完了！"
echo "URL: $REPO_URL"

echo ""
echo "=========================================="
echo "リリース作成中..."
echo "=========================================="

# JAR ファイルのパスを確認
JAR_FILE=$(find target -name "RomajiSwitcher-*.jar" | head -1)

if [ -f "$JAR_FILE" ]; then
  gh release create v1.0.0 \
    --title "RomajiSwitcher v1.0.0" \
    --notes "Initial release with complete Japanese romanization support

## Features
- ✅ Full Hepburn and Kunrei romanization support
- ✅ Long vowels (aa, ii, uu, ee, oo, ei, ou)
- ✅ Small kana (l/x prefix support)
- ✅ Sokuon (促音) - doubled consonants
- ✅ Per-player persistent settings (JSON storage)
- ✅ Compatible with Translator series and EssentialsX

## Installation
1. Download RomajiSwitcher-1.0.0.jar
2. Place in your Paper server's plugins folder
3. Restart the server

## Usage
- \`/romaji\` - Toggle romaji conversion on/off

For full documentation, see README.md" \
    "$JAR_FILE"
    
  echo "✅ リリース作成完了！"
else
  echo "❌ エラー: JAR ファイルが見つかりません"
  exit 1
fi

echo ""
echo "=========================================="
echo "✅ すべて完了しました！"
echo "=========================================="
echo ""
echo "リポジトリ: $REPO_URL"
echo "JAR ファイル: $JAR_FILE"
