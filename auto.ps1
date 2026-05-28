# Auto-Continuous Launcher - loops claude sessions in the same window
# Each loop = 1 task = 1 fresh claude session. Loop runs forever until stop switch.
#
# Usage:
#   auto.bat              → W1 (single worker)
#   auto.bat W2           → W2 (from parallel.bat)
#   auto.bat W3 P0-008    → W3, pre-assigned to P0-008 (from start.bat)

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

# ---- Parse arguments ----
$workerId = 'W1'
$assignedModule = ''
if ($args.Count -ge 1 -and $args[0] -match '^W\d+$') { $workerId = $args[0] }
if ($args.Count -ge 2 -and $args[1] -match '^P\d-\d{3}$') { $assignedModule = $args[1] }

# ---- Helper: parse config.ini ----
function Read-Config {
    $configFile = Join-Path $scriptDir 'config.ini'
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

# ---- Initial checks ----
$config = Read-Config
$projectPath = if ($config['project.path']) { $config['project.path'] } else { $scriptDir }
$msgFile = Join-Path $scriptDir 'auto-prompt.md'

if (-not (Test-Path $msgFile)) {
    Write-Host "ERROR: auto-prompt.md not found" -ForegroundColor Red
    Read-Host "Press Enter to exit"; exit 1
}

$stopSingle = ($config['execution.single_stop'] -eq 'true')
$stopMulti  = ($config['execution.multi_stop'] -eq 'true')
if ($stopSingle -or $stopMulti) {
    Write-Host "[STOP] Stop switch is ON. Worker $workerId will not start." -ForegroundColor Yellow
    Read-Host "Press Enter to exit"; exit 0
}

# ---- Display banner ----
$taskNum = 0
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  ERP Auto Worker - $workerId (PID: $PID)" -ForegroundColor Cyan
Write-Host "  Project: $projectPath" -ForegroundColor White
if ($assignedModule) { Write-Host "  Pre-assigned: $assignedModule" -ForegroundColor Green }
Write-Host "  Loop: infinite - each task = 1 fresh Claude session" -ForegroundColor White
Write-Host "  Stop: set single_stop=true in config.ini" -ForegroundColor DarkGray
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

# ================================================================
# MAIN LOOP - runs forever until stop switch
# No failCount, no PID lock, no chain flags. Just a simple loop.
# ================================================================
while ($true) {
    $taskNum++

    # 1. Check stop switch (re-read config to catch mid-run changes)
    $config = Read-Config
    if ($config['execution.single_stop'] -eq 'true' -or $config['execution.multi_stop'] -eq 'true') {
        Write-Host ""
        Write-Host "  [STOP] Stop switch turned ON. Worker $workerId exiting." -ForegroundColor Yellow
        Write-Host ""
        break
    }

    # 2. Build prompt
    $msg = (Get-Content -Path $msgFile -Raw -Encoding UTF8).Trim()
    $msg = $msg -replace '\{WORKER_ID\}', $workerId

    if ($taskNum -eq 1 -and $assignedModule) {
        $msg += "`n`n---`n**[DISPATCHER PRE-ASSIGNMENT]**`nPre-assigned to module **$assignedModule**.`nClaim a task from this module only. All other rules still apply.`n**[/DISPATCHER PRE-ASSIGNMENT]**"
    }

    # 3. Launch claude
    Write-Host "  [START] Task session #$taskNum - $(Get-Date -Format 'HH:mm:ss')" -ForegroundColor Green

    # Use try/catch to prevent ANY error from killing the PowerShell process
    try {
        & claude $msg --dangerously-skip-permissions
    } catch {
        Write-Host "  [ERROR] Exception: $($_.Exception.Message)" -ForegroundColor Red
    }

    # 4. Claude exited - always continue the loop
    Write-Host ""
    Write-Host "  [DONE] Session #$taskNum finished at $(Get-Date -Format 'HH:mm:ss')" -ForegroundColor Cyan
    Write-Host "  [LOOP] Starting next session in 3 seconds..." -ForegroundColor Cyan
    Write-Host ""

    Start-Sleep -Seconds 3
}

Write-Host ""
Write-Host "  [EXIT] Worker $workerId stopped after $taskNum sessions." -ForegroundColor Yellow
Read-Host "  Press Enter to close this window"
