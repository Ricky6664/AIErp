@echo off
chcp 65001 >nul 2>&1
cd /d "%~dp0"
title Smart Parallel Dispatcher
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0start.ps1" %*
if %errorlevel% neq 0 (
    echo.
    echo ============================================================
    echo   [ERROR] start.ps1 exited with code %errorlevel%
    echo ============================================================
    echo.
)
pause
