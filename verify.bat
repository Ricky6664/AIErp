@echo off
chcp 65001 >nul 2>&1
cd /d "%~dp0"

set MODE=%1
if "%MODE%"=="" set MODE=full
if "%MODE%"=="cron" goto :run
if "%MODE%"=="full" goto :run
if "%MODE%"=="resume" goto :run
echo Usage: verify.bat [full^|cron^|resume]
echo   full   - 一次性全量扫描（7个Stage全部执行）
echo   cron   - 定时回归（跳过Stage 5.5）
echo   resume - 中断续跑，从上次断点继续
exit /b 1

:run
title ERP Verify - %MODE%
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0verify.ps1" -Mode %MODE%
