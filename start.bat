@echo off
cd /d "%~dp0"

if not exist .env (
    echo.
    echo Missing .env file.
    echo Copy .env.example to .env and add your Supabase password first.
    echo.
    exit /b 1
)

echo.
echo Starting FitLens backend...
echo   API via nginx:  http://localhost/api
echo   API direct:     http://localhost:8080/api
echo.
echo Mobile app ^(separate terminal^):
echo   cd mobile
echo   npm start
echo.
echo Press Ctrl+C to stop.
echo.

docker compose up --build
