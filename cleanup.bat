@echo off
chcp 65001 >nul 2>&1
cd /d "%~dp0"

echo.
echo ============================================================
echo   ERP Auto-Dev Cleanup
echo ============================================================
echo.

setlocal enabledelayedexpansion
set COUNT=0

for %%f in (.relay-* .session-id-* .watchdog-* .task-* .module-* .idle-* .catalog.lock .parallel.lock .relay .session-id .watchdog.ps1 .watchdog.pid) do (
    if exist "%%f" (
        del /q "%%f" 2>nul
        if not errorlevel 1 (
            echo   [DEL] %%f
            set /a COUNT=COUNT+1
        )
    )
)

echo.
if !COUNT!==0 (
    echo   No temp files to clean.
) else (
    echo   Cleaned !COUNT! temp files.
)
echo.
echo ============================================================
echo   Ready to run start.bat
echo ============================================================
echo.
pause
endlocal
