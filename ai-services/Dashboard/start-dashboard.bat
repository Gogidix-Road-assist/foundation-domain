@echo off
REM AI Services Monitoring Dashboard Startup Script for Windows

echo ==================================
echo AI Services Monitoring Dashboard
echo ==================================
echo.

REM Check if Node.js is installed
where node >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Error: Node.js is not installed
    echo Please install Node.js from https://nodejs.org/
    pause
    exit /b 1
)

echo Node version:
node --version
echo NPM version:
npm --version
echo.

REM Navigate to dashboard directory
cd /d "%~dp0"

REM Check if node_modules exists
if not exist "node_modules" (
    echo Installing dependencies...
    call npm install
    echo.
)

REM Check if .env file exists
if not exist ".env" (
    echo Creating .env file...
    (
        echo # AI Services Dashboard Configuration
        echo.
        echo API_BASE_URL=http://localhost:8080
        echo WS_URL=ws://localhost:8080/ws
        echo REFRESH_INTERVAL=30000
    ) > .env
    echo .env file created
    echo.
)

REM Start the development server
echo Starting AI Services Monitoring Dashboard...
echo Dashboard will be available at: http://localhost:3000
echo.
echo Press Ctrl+C to stop the server
echo.

call npm run dev

pause