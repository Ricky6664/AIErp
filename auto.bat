@echo off
chcp 65001 >nul 2>&1
cd /d "%~dp0"
title ERP Worker
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0auto.ps1"
