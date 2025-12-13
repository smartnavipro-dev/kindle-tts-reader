# GitHub Release Creation via API
# This script creates a release using GitHub REST API

$ErrorActionPreference = "Stop"

$owner = "smartnavipro-dev"
$repo = "kindle-tts-reader"
$tag = "v1.0.55"
$releaseName = "v1.0.55: Gemini 2.5 Flash maxOutputTokens Optimization"
$apkPath = "kindle-tts-reader-v1.0.55.apk"
$releaseNotesPath = "RELEASE_NOTES_v1.0.55.md"

Write-Host "GitHub Release API Creator" -ForegroundColor Cyan
Write-Host ""

# Check for GitHub token in environment
$token = $env:GITHUB_TOKEN
if (-Not $token) {
    Write-Host "GITHUB_TOKEN environment variable not found." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "To create releases via API, you need a GitHub Personal Access Token." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Option 1: Set environment variable" -ForegroundColor White
    Write-Host '  $env:GITHUB_TOKEN = "your_token_here"' -ForegroundColor Cyan
    Write-Host '  Then run this script again' -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Option 2: Create release manually" -ForegroundColor White
    Write-Host "  Run: .\create_github_release.ps1" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "To get a token:" -ForegroundColor White
    Write-Host "  1. Visit: https://github.com/settings/tokens/new" -ForegroundColor Cyan
    Write-Host "  2. Select 'repo' scope" -ForegroundColor Cyan
    Write-Host "  3. Generate and copy the token" -ForegroundColor Cyan
    Write-Host ""

    # Fall back to manual process
    Write-Host "Falling back to manual process..." -ForegroundColor Yellow
    & ".\create_github_release.ps1"
    exit 0
}

Write-Host "✅ GitHub token found" -ForegroundColor Green

# Read release notes
$releaseBody = Get-Content $releaseNotesPath -Raw

# Create release
Write-Host "Creating GitHub release..." -ForegroundColor Yellow

$releaseData = @{
    tag_name = $tag
    name = $releaseName
    body = $releaseBody
    draft = $false
    prerelease = $false
} | ConvertTo-Json

$headers = @{
    Authorization = "Bearer $token"
    Accept = "application/vnd.github+json"
    "X-GitHub-Api-Version" = "2022-11-28"
}

try {
    $response = Invoke-RestMethod -Uri "https://api.github.com/repos/$owner/$repo/releases" `
        -Method Post `
        -Headers $headers `
        -Body $releaseData `
        -ContentType "application/json"

    Write-Host "✅ Release created successfully!" -ForegroundColor Green
    Write-Host "   URL: $($response.html_url)" -ForegroundColor Cyan

    # Upload APK
    Write-Host ""
    Write-Host "Uploading APK asset..." -ForegroundColor Yellow

    $uploadUrl = $response.upload_url -replace '\{\?.*\}', "?name=$apkPath"
    $apkBytes = [System.IO.File]::ReadAllBytes((Resolve-Path $apkPath))

    $uploadHeaders = @{
        Authorization = "Bearer $token"
        Accept = "application/vnd.github+json"
        "Content-Type" = "application/vnd.android.package-archive"
    }

    $uploadResponse = Invoke-RestMethod -Uri $uploadUrl `
        -Method Post `
        -Headers $uploadHeaders `
        -Body $apkBytes

    Write-Host "✅ APK uploaded successfully!" -ForegroundColor Green
    Write-Host "   Download URL: $($uploadResponse.browser_download_url)" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "✅ Release v1.0.55 published!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "View release: $($response.html_url)" -ForegroundColor White

} catch {
    Write-Host "❌ Error creating release:" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host ""
    Write-Host "Falling back to manual process..." -ForegroundColor Yellow
    & ".\create_github_release.ps1"
}
