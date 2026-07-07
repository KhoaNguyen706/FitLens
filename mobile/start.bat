@echo off
cd /d "%~dp0"

set "PATH=C:\Program Files\nodejs;%APPDATA%\npm;%PATH%"

where npm.cmd >nul 2>&1
if errorlevel 1 (
    echo.
    echo Node.js not found. Install it first:
    echo   winget install OpenJS.NodeJS.LTS
    echo.
    echo Then close and reopen this terminal, or run this script again.
    echo.
    exit /b 1
)

if not exist node_modules (
    echo Installing dependencies...
    call npm.cmd install
    if errorlevel 1 exit /b 1
)

echo.
echo Starting FitLens mobile ^(Expo^)...
echo Scan the QR code with Expo Go on your phone.
echo.
call npm.cmd start
