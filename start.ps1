# FitLens — start backend (Spring Boot + nginx) with one command.
# Requires: Docker Desktop running, .env file in this folder.

Set-Location $PSScriptRoot

if (-not (Test-Path .env)) {
    Write-Host ""
    Write-Host "Missing .env file." -ForegroundColor Yellow
    Write-Host "Copy .env.example to .env and add your Supabase password first." -ForegroundColor Yellow
    Write-Host ""
    exit 1
}

Write-Host ""
Write-Host "Starting FitLens backend..." -ForegroundColor Cyan
Write-Host "  API (via nginx):  http://localhost/api" -ForegroundColor Green
Write-Host "  API (direct):     http://localhost:8080/api" -ForegroundColor DarkGray
Write-Host "  nginx health:     http://localhost/nginx-health" -ForegroundColor DarkGray
Write-Host ""
Write-Host "Mobile app (separate terminal):" -ForegroundColor Cyan
Write-Host "  cd mobile" -ForegroundColor White
Write-Host "  npm start" -ForegroundColor White
Write-Host ""
Write-Host "Press Ctrl+C to stop." -ForegroundColor DarkGray
Write-Host ""

docker compose up --build
