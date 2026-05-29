@echo off
chcp 65001 >nul 2>&1
cd /d "%~dp0"
set WORKER=%1
if "%WORKER%"=="" set WORKER=W1
set SESSION_ID=%WORKER%-%RANDOM%
title ERP Worker %SESSION_ID%
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0auto.ps1" --session %SESSION_ID% %WORKER%
