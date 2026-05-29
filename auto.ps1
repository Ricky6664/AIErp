# Auto-Continuous Launcher - 1 task per session
# auto.ps1 runs ONE Claude session then exits. auto.bat handles the loop.

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
$msgFile = Join-Path $scriptDir 'auto-prompt.md'

if (-not (Test-Path $msgFile)) {
    Write-Host "ERROR: auto-prompt.md not found" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

if ($config['execution.single_stop'] -eq 'true' -or $config['execution.multi_stop'] -eq 'true') {
    Write-Host "[STOP] Stop switch is ON. Worker $workerId will not start." -ForegroundColor Yellow
    exit 1
}

# ---- Display banner ----
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  ERP Auto Worker - $workerId (PID: $PID)" -ForegroundColor Cyan
if ($assignedModule) { Write-Host "  Pre-assigned: $assignedModule" -ForegroundColor Green }
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

# ---- Build prompt ----
$msg = (Get-Content -Path $msgFile -Raw -Encoding UTF8).Trim()
$msg = $msg -replace '\{WORKER_ID\}', $workerId

if ($assignedModule) {
    $msg += "`n`n---`n**[DISPATCHER PRE-ASSIGNMENT]**`nPre-assigned to module **$assignedModule**.`nClaim a task from this module only. All other rules still apply.`n**[/DISPATCHER PRE-ASSIGNMENT]**"
}

# ---- Launch Claude ----
Write-Host "  [START] Task session - $(Get-Date -Format 'HH:mm:ss')" -ForegroundColor Green
Write-Host "  [PID] PowerShell PID: $PID" -ForegroundColor DarkGray

& claude $msg --dangerously-skip-permissions

$exitCode = $LASTEXITCODE
Write-Host ""
Write-Host "  [DONE] Claude exited at $(Get-Date -Format 'HH:mm:ss') with code $exitCode" -ForegroundColor Cyan
Write-Host "  [INFO] Claude should have launched the next window before exiting." -ForegroundColor DarkGray

exit 0
