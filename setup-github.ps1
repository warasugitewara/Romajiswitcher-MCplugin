# GitHub リポジトリ作成と初期化スクリプト（Windows用）
# 使用方法: PowerShellで実行してください
# Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope Process
# .\setup-github.ps1

$ErrorActionPreference = "Stop"

$REPO_NAME = "Romajiswitcher-MCplugin"
$REPO_DIR = "C:\Users\waras\RomajiSwitcher"

Write-Host "==========================================" -ForegroundColor Green
Write-Host "GitHub CLI 認証確認" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host ""

# 認証状態を確認
try {
    $auth_status = gh auth status 2>&1
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✅ 既に GitHub に認証済みです" -ForegroundColor Green
    } else {
        Write-Host "⚠️  GitHub 認証が必要です" -ForegroundColor Yellow
        Write-Host ""
        Write-Host "以下のコマンドを実行してください:" -ForegroundColor Yellow
        Write-Host "  gh auth login" -ForegroundColor Cyan
        Write-Host ""
        Write-Host "認証完了後、このスクリプトを再度実行してください" -ForegroundColor Yellow
        exit 1
    }
} catch {
    Write-Host "❌ GitHub CLI の確認に失敗しました: $_" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "==========================================" -ForegroundColor Green
Write-Host "リポジトリ作成中..." -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host ""

Set-Location $REPO_DIR

try {
    # リポジトリを作成
    Write-Host "gh repo create を実行中..." -ForegroundColor Cyan
    & gh repo create $REPO_NAME `
        --source=. `
        --remote=origin `
        --push `
        --public `
        --description "A lightweight Paper Minecraft plugin that converts romaji to Japanese characters in chat messages"
    
    if ($LASTEXITCODE -ne 0) {
        Write-Host "❌ リポジトリ作成に失敗しました" -ForegroundColor Red
        exit 1
    }
    
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host "リポジトリ URL 確認中..." -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
    
    $repo_url = & gh repo view $REPO_NAME --json url --jq '.url'
    Write-Host "✅ リポジトリ作成完了！" -ForegroundColor Green
    Write-Host "URL: $repo_url" -ForegroundColor Cyan
    
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host "リリース作成中..." -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host ""
    
    # JAR ファイルのパスを確認
    $jar_files = Get-ChildItem -Path "target\RomajiSwitcher-*.jar" -ErrorAction SilentlyContinue
    
    if ($jar_files.Count -gt 0) {
        $jar_file = $jar_files[0].FullName
        
        Write-Host "JAR ファイル: $jar_file" -ForegroundColor Cyan
        Write-Host "リリースを作成中..." -ForegroundColor Cyan
        
        $release_notes = @"
Initial release with complete Japanese romanization support

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
- `/romaji` - Toggle romaji conversion on/off

For full documentation, see README.md
"@
        
        & gh release create v1.0.0 `
            --title "RomajiSwitcher v1.0.0" `
            --notes $release_notes `
            $jar_file
        
        if ($LASTEXITCODE -ne 0) {
            Write-Host "❌ リリース作成に失敗しました" -ForegroundColor Red
            exit 1
        }
        
        Write-Host "✅ リリース作成完了！" -ForegroundColor Green
    } else {
        Write-Host "❌ エラー: JAR ファイルが見つかりません" -ForegroundColor Red
        exit 1
    }
    
    Write-Host ""
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host "✅ すべて完了しました！" -ForegroundColor Green
    Write-Host "==========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "リポジトリ: $repo_url" -ForegroundColor Cyan
    Write-Host "JAR ファイル: $jar_file" -ForegroundColor Cyan
    
} catch {
    Write-Host "❌ エラーが発生しました: $_" -ForegroundColor Red
    exit 1
}
