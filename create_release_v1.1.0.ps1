# GitHub Release Creation Script for v1.1.0
# This script creates a GitHub release using the GitHub API

$ErrorActionPreference = "Stop"

# Configuration
$owner = "smartnavipro-dev"
$repo = "kindle-tts-reader"
$tag = "v1.1.0"
$releaseName = "v1.1.0: Local Learning System with Privacy-First Design"
$apkPath = "kindle-tts-reader-v1.1.0-release.apk"
$releaseNotesPath = "RELEASE_NOTES_v1.1.0.md"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "GitHub Release Creator for $tag" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if git credentials are configured
Write-Host "Checking git configuration..." -ForegroundColor Yellow
$gitUser = git config user.name
$gitEmail = git config user.email
Write-Host "Git User: $gitUser <$gitEmail>" -ForegroundColor Green

# Read release notes
Write-Host "`nReading release notes..." -ForegroundColor Yellow
if (-Not (Test-Path $releaseNotesPath)) {
    Write-Host "Error: Release notes file not found: $releaseNotesPath" -ForegroundColor Red
    exit 1
}
$releaseBody = Get-Content $releaseNotesPath -Raw

# Check if APK exists
Write-Host "Checking APK file..." -ForegroundColor Yellow
if (-Not (Test-Path $apkPath)) {
    Write-Host "Error: APK file not found: $apkPath" -ForegroundColor Red
    exit 1
}
$apkSize = (Get-Item $apkPath).Length / 1MB
Write-Host "APK found: $apkPath ($([math]::Round($apkSize, 2)) MB)" -ForegroundColor Green

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "MANUAL STEPS REQUIRED" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "GitHub CLI (gh) is not available. Please create the release manually:" -ForegroundColor Yellow
Write-Host ""
Write-Host "1. Go to: https://github.com/$owner/$repo/releases/new" -ForegroundColor White
Write-Host ""
Write-Host "2. Fill in the following information:" -ForegroundColor White
Write-Host "   - Tag: $tag" -ForegroundColor Cyan
Write-Host "   - Release title: $releaseName" -ForegroundColor Cyan
Write-Host "   - Description: Copy from $releaseNotesPath" -ForegroundColor Cyan
Write-Host ""
Write-Host "3. Upload the APK file:" -ForegroundColor White
Write-Host "   - File: $apkPath" -ForegroundColor Cyan
Write-Host "   - Size: $([math]::Round($apkSize, 2)) MB" -ForegroundColor Cyan
Write-Host ""
Write-Host "4. Click 'Publish release'" -ForegroundColor White
Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Copy release notes to clipboard if possible
Write-Host "Attempting to copy release notes to clipboard..." -ForegroundColor Yellow
try {
    Set-Clipboard -Value $releaseBody
    Write-Host "✅ Release notes copied to clipboard!" -ForegroundColor Green
    Write-Host "   You can now paste them directly into the GitHub release form." -ForegroundColor Green
} catch {
    Write-Host "⚠️  Could not copy to clipboard. You'll need to copy manually." -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Opening GitHub releases page in browser..." -ForegroundColor Yellow
Start-Process "https://github.com/$owner/$repo/releases/new?tag=$tag"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Repository: $owner/$repo" -ForegroundColor White
Write-Host "Tag: $tag" -ForegroundColor White
Write-Host "APK: $apkPath ($([math]::Round($apkSize, 2)) MB)" -ForegroundColor White
Write-Host "Release Notes: $releaseNotesPath" -ForegroundColor White
Write-Host ""
Write-Host "✅ Ready to create release!" -ForegroundColor Green
Write-Host ""
