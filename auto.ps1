# Auto-Continuous Launcher - reads config.ini, loops claude sessions
# Each loop iteration = 1 leaf task = 1 fresh claude session
# Window stays open, claude sessions rotate within the same window

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

# ---- Helper: parse config.ini ----
function Read-Config {
    $configFile = Join-Path $scriptDir 'config.ini'
    $config = @{}
    $currentSection = ''

    if (Test-Path $configFile) {
        foreach ($line in Get-Content $configFile -Encoding UTF8) {
            $line = $line.Trim()
            if ($line -eq '' -or $line.StartsWith(';')) { continue }
            if ($line -match '^\[(.+)\]$') {
                $currentSection = $Matches[1]
                continue
            }
            if ($line -match '^([^=]+)=(.*)$') {
                $key = "$currentSection.$($Matches[1].Trim())"
                $value = $Matches[2].Trim()
                $config[$key] = $value
            }
        }
    } else {
        Write-Host "WARNING: config.ini not found, using defaults" -ForegroundColor Yellow
    }
    return $config
}

# ---- Helper: check stop switches ----
function Test-StopSwitch {
    param($config)
    $singleStop = ($config['execution.single_stop'] -eq 'true')
    $multiStop  = ($config['execution.multi_stop'] -eq 'true')
    return ($singleStop -or $multiStop)
}

# ---- Initial config read ----
$config = Read-Config
$projectPath = if ($config['project.path']) { $config['project.path'] } else { $scriptDir }

# ---- Check stop switches at startup ----
if (Test-StopSwitch $config) {
    Write-Host ""
    Write-Host "============================================================" -ForegroundColor Yellow
    Write-Host "  [STOP] Stop switch is ON" -ForegroundColor Yellow
    Write-Host "  single_stop = $($config['execution.single_stop'])" -ForegroundColor Yellow
    Write-Host "  multi_stop  = $($config['execution.multi_stop'])" -ForegroundColor Yellow
    Write-Host "  Worker will not start. Set to false to resume." -ForegroundColor Yellow
    Write-Host "============================================================" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 0
}

# ---- Read auto-prompt.md ----
$msgFile = Join-Path $scriptDir 'auto-prompt.md'

if (-not (Test-Path $msgFile)) {
    Write-Host "ERROR: auto-prompt.md not found" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

$msg = (Get-Content -Path $msgFile -Raw -Encoding UTF8).Trim()

if ([string]::IsNullOrWhiteSpace($msg)) {
    Write-Host "ERROR: auto-prompt.md is empty" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

# ---- Display status ----
$taskNum = 0

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  ERP Auto Worker (Loop Mode)" -ForegroundColor Cyan
Write-Host "  Project: $projectPath" -ForegroundColor White
Write-Host "  Each task runs in a fresh Claude session" -ForegroundColor White
Write-Host "  Window auto-closes when stop switch is ON" -ForegroundColor White
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

# ---- Main loop: one claude session per task ----
while ($true) {
    $taskNum++

    # Re-read config each iteration (catch mid-run stop switch changes)
    $config = Read-Config
    if (Test-StopSwitch $config) {
        Write-Host ""
        Write-Host "============================================================" -ForegroundColor Yellow
        Write-Host "  [STOP] Stop switch turned ON after task #$($taskNum - 1)" -ForegroundColor Yellow
        Write-Host "  Worker exiting. Set to false and restart to resume." -ForegroundColor Yellow
        Write-Host "============================================================" -ForegroundColor Yellow
        Write-Host ""
        break
    }

    Write-Host "------------------------------------------------------------" -ForegroundColor DarkGray
    Write-Host "  Task session #$taskNum - $(Get-Date -Format 'HH:mm:ss')" -ForegroundColor Green
    Write-Host "------------------------------------------------------------" -ForegroundColor DarkGray

    # Launch claude (blocking call - waits for task to complete)
    & claude $msg --dangerously-skip-permissions

    Write-Host ""
    Write-Host "  [DONE] Task session #$taskNum completed, starting next..." -ForegroundColor Green
    Write-Host ""

    # Brief pause to allow filesystem to settle
    Start-Sleep -Seconds 2
}
