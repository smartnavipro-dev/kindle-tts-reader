# GitHub Release Re-creation Script for v1.1.0
# This script deletes the existing release and creates a new one with updated notes

$ErrorActionPreference = "Stop"

# Configuration
$owner = "smartnavipro-dev"
$repo = "kindle-tts-reader"
$tag = "v1.1.0"
$releaseName = "v1.1.0: Local Learning System with Privacy-First Design"
$apkPath = "kindle-tts-reader-v1.1.0-release.apk"
$releaseNotesPath = "RELEASE_NOTES_v1.1.0.md"

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "GitHub Release Re-creator for $tag" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check for GitHub token
Write-Host "Checking for GitHub token..." -ForegroundColor Yellow
$token = $env:GITHUB_TOKEN
if (-not $token) {
    Write-Host "GitHub token not found in environment variable GITHUB_TOKEN" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "To create a GitHub token:" -ForegroundColor Cyan
    Write-Host "1. Go to https://github.com/settings/tokens" -ForegroundColor White
    Write-Host "2. Click 'Generate new token (classic)'" -ForegroundColor White
    Write-Host "3. Select scope: 'repo' (Full control of private repositories)" -ForegroundColor White
    Write-Host "4. Copy the token and paste it below" -ForegroundColor White
    Write-Host ""
    $token = Read-Host "Enter your GitHub Personal Access Token"

    if (-not $token) {
        Write-Host "Error: Token is required" -ForegroundColor Red
        exit 1
    }
}
Write-Host "✓ GitHub token found" -ForegroundColor Green

# Read release notes
Write-Host "`nReading release notes..." -ForegroundColor Yellow
if (-Not (Test-Path $releaseNotesPath)) {
    Write-Host "Error: Release notes file not found: $releaseNotesPath" -ForegroundColor Red
    exit 1
}
$releaseBody = Get-Content $releaseNotesPath -Raw -Encoding UTF8
Write-Host "✓ Release notes loaded ($(($releaseBody.Length / 1KB).ToString('F2')) KB)" -ForegroundColor Green

# Check if APK exists
Write-Host "`nChecking APK file..." -ForegroundColor Yellow
if (-Not (Test-Path $apkPath)) {
    Write-Host "Error: APK file not found: $apkPath" -ForegroundColor Red
    exit 1
}
$apkSize = (Get-Item $apkPath).Length / 1MB
Write-Host "✓ APK found: $apkPath ($([math]::Round($apkSize, 2)) MB)" -ForegroundColor Green

# Setup API headers
$headers = @{
    "Authorization" = "Bearer $token"
    "Accept" = "application/vnd.github+json"
    "X-GitHub-Api-Version" = "2022-11-28"
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "STEP 1: Delete existing release" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

try {
    # Get existing release by tag
    Write-Host "Fetching existing release information..." -ForegroundColor Yellow
    $getReleaseUrl = "https://api.github.com/repos/$owner/$repo/releases/tags/$tag"

    try {
        $existingRelease = Invoke-RestMethod -Uri $getReleaseUrl -Headers $headers -Method Get
        $releaseId = $existingRelease.id
        Write-Host "✓ Found existing release (ID: $releaseId)" -ForegroundColor Green

        # Delete the release
        Write-Host "Deleting existing release..." -ForegroundColor Yellow
        $deleteUrl = "https://api.github.com/repos/$owner/$repo/releases/$releaseId"
        Invoke-RestMethod -Uri $deleteUrl -Headers $headers -Method Delete | Out-Null
        Write-Host "✓ Release deleted successfully" -ForegroundColor Green

    } catch {
        if ($_.Exception.Response.StatusCode -eq 404) {
            Write-Host "✓ No existing release found (this is OK)" -ForegroundColor Green
        } else {
            throw
        }
    }

} catch {
    Write-Host "Error during release deletion: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Response: $($_.ErrorDetails.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "STEP 2: Create new release" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

try {
    Write-Host "Creating new release..." -ForegroundColor Yellow

    $createReleaseUrl = "https://api.github.com/repos/$owner/$repo/releases"
    $releaseData = @{
        tag_name = $tag
        name = $releaseName
        body = $releaseBody
        draft = $false
        prerelease = $false
    } | ConvertTo-Json -Depth 10

    $newRelease = Invoke-RestMethod -Uri $createReleaseUrl -Headers $headers -Method Post -Body $releaseData -ContentType "application/json"
    $newReleaseId = $newRelease.id
    $uploadUrl = $newRelease.upload_url -replace '\{\?name,label\}', ''

    Write-Host "✓ Release created successfully (ID: $newReleaseId)" -ForegroundColor Green

} catch {
    Write-Host "Error during release creation: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Response: $($_.ErrorDetails.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "STEP 3: Upload APK file" -ForegroundColor Yellow
Write-Host "========================================" -ForegroundColor Cyan

try {
    Write-Host "Uploading APK ($([math]::Round($apkSize, 2)) MB)..." -ForegroundColor Yellow
    Write-Host "This may take a few minutes..." -ForegroundColor Yellow

    $apkFileName = [System.IO.Path]::GetFileName($apkPath)
    $uploadUrlWithName = "$uploadUrl?name=$apkFileName"

    $apkBytes = [System.IO.File]::ReadAllBytes((Resolve-Path $apkPath))

    $uploadHeaders = @{
        "Authorization" = "Bearer $token"
        "Accept" = "application/vnd.github+json"
        "Content-Type" = "application/vnd.android.package-archive"
    }

    $uploadResponse = Invoke-RestMethod -Uri $uploadUrlWithName -Headers $uploadHeaders -Method Post -Body $apkBytes

    Write-Host "✓ APK uploaded successfully" -ForegroundColor Green
    Write-Host "  Download URL: $($uploadResponse.browser_download_url)" -ForegroundColor Cyan

} catch {
    Write-Host "Error during APK upload: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Response: $($_.ErrorDetails.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "You can manually upload the APK at:" -ForegroundColor Yellow
    Write-Host "https://github.com/$owner/$repo/releases/edit/$tag" -ForegroundColor Cyan
    # Don't exit - release was created successfully
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "SUCCESS!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Release v$tag has been successfully recreated!" -ForegroundColor Green
Write-Host ""
Write-Host "View your release at:" -ForegroundColor Yellow
Write-Host "https://github.com/$owner/$repo/releases/tag/$tag" -ForegroundColor Cyan
Write-Host ""

# Open the release page in browser
Write-Host "Opening release page in browser..." -ForegroundColor Yellow
Start-Process "https://github.com/$owner/$repo/releases/tag/$tag"

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "SUMMARY" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Repository: $owner/$repo" -ForegroundColor White
Write-Host "Tag: $tag" -ForegroundColor White
Write-Host "Release ID: $newReleaseId" -ForegroundColor White
Write-Host "APK: $apkPath ($([math]::Round($apkSize, 2)) MB)" -ForegroundColor White
Write-Host "Release Notes: $releaseNotesPath (English + Japanese)" -ForegroundColor White
Write-Host ""
Write-Host "✓ All done!" -ForegroundColor Green
Write-Host ""
