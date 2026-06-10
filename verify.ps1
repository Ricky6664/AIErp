# ERP 自动验证修复系统 - 调度脚本
param([string]$Mode = 'full')

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$logFile = Join-Path $scriptDir 'verify.log'

function Log($msg) {
    $line = "[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] [verify] $msg"
    try { [System.IO.File]::AppendAllText($logFile, "$line`r`n", [System.Text.Encoding]::UTF8) } catch {}
    Write-Host "  $line"
}

function Read-VerifyConfig {
    $configFile = Join-Path $scriptDir 'verify-config.ini'
    $config = @{}
    $section = ''
    if (Test-Path $configFile) {
        foreach ($line in Get-Content $configFile -Encoding UTF8) {
            $line = $line.Trim()
            if ($line -eq '' -or $line.StartsWith(';')) { continue }
            if ($line -match '^\[(.+)\]$') { $section = $Matches[1]; continue }
            if ($line -match '^([^=]+)=(.*)$') {
                $config["$section.$($Matches[1].Trim())"] = $Matches[2].Trim()
            }
        }
    }
    return $config
}

# ---- Banner ----
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  ERP Verify System - Mode: $Mode" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Log "Verify started | Mode: $Mode | PID: $PID"

# ---- Validate prerequisites ----
$config = Read-VerifyConfig

if (-not (Test-Path (Join-Path $scriptDir 'verify-prompt.md'))) {
    Write-Host "ERROR: verify-prompt.md not found" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# ---- Launch Claude Code ----
$promptFile = Join-Path $scriptDir 'verify-prompt.md'
$msg = (Get-Content -Path $promptFile -Raw -Encoding UTF8).Trim()
$msg = $msg -replace '\{MODE\}', $Mode

Log "Launching Claude Code for verification..."
& claude $msg --dangerously-skip-permissions
$exitCode = $LASTEXITCODE
Log "Claude exited (code: $exitCode)"

Log "===== Verify session done ====="
