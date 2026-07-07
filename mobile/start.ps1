Set-Location $PSScriptRoot

$nodeDir = "C:\Program Files\nodejs"
$npm = Join-Path $nodeDir "npm.cmd"
$env:Path = "$nodeDir;$env:APPDATA\npm;" + $env:Path

if (-not (Test-Path $npm)) {
    Write-Host ""
    Write-Host "Node.js not found. Install it first:" -ForegroundColor Yellow
    Write-Host "  winget install OpenJS.NodeJS.LTS" -ForegroundColor White
    Write-Host ""
    exit 1
}

if (-not (Test-Path node_modules)) {
    Write-Host "Installing dependencies..." -ForegroundColor Cyan
    & $npm install
    if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
}

Write-Host ""
Write-Host "Starting FitLens mobile (Expo)..." -ForegroundColor Cyan
Write-Host "Scan the QR code with Expo Go on your phone." -ForegroundColor Green
Write-Host ""

# Use npm.cmd — PowerShell blocks npm.ps1 when script execution is restricted.
& $npm start
