# Parallel Worker Launcher - reads config.ini for settings, spawns N auto.bat workers
# Usage: parallel.bat [N]  (N overrides config.ini max_workers)

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

# ---- Parse config.ini ----
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

# ---- Read config values ----
$projectPath = if ($config['project.path']) { $config['project.path'] } else { $scriptDir }
$configMaxWorkers = if ($config['parallel.max_workers']) { [int]$config['parallel.max_workers'] } else { 4 }
$multiStop = ($config['execution.multi_stop'] -eq 'true')

# ---- Check multi_stop ----
if ($multiStop) {
    Write-Host ""
    Write-Host "============================================================" -ForegroundColor Yellow
    Write-Host "  [STOP] multi_stop = true" -ForegroundColor Yellow
    Write-Host "  多人模式停止开关已开启，无法启动新工人" -ForegroundColor Yellow
    Write-Host "  如需继续，请将 config.ini 中 multi_stop 改为 false" -ForegroundColor Yellow
    Write-Host "============================================================" -ForegroundColor Yellow
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 0
}

# ---- Determine worker count: CLI arg > config.ini ----
$workerCount = $configMaxWorkers
if ($args.Count -gt 0) {
    $parsed = 0
    if ([int]::TryParse($args[0], [ref]$parsed)) {
        $workerCount = $parsed
    }
}

# ---- Enforce limits ----
if ($workerCount -lt 1) { $workerCount = 1 }
$maxAllowed = 100
if ($workerCount -gt $maxAllowed) {
    Write-Host "WARNING: Max $maxAllowed workers allowed, clamping to $maxAllowed." -ForegroundColor Yellow
    $workerCount = $maxAllowed
}

# ---- Display plan ----
Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Parallel Worker Launcher" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Project: $projectPath" -ForegroundColor White
Write-Host "  Workers: $workerCount (config default: $configMaxWorkers)" -ForegroundColor White
Write-Host "  Task pool: tasks_catalog.md + tasks_active.md" -ForegroundColor White
Write-Host ""

# ---- Launch workers ----
$batPath = Join-Path $scriptDir 'auto.bat'

for ($i = 1; $i -le $workerCount; $i++) {
    $title = "ERP-Worker-$i"
    $workerId = "W$i"

    Start-Process cmd.exe -ArgumentList @(
        '/c',
        "title $title && cd /d `"$scriptDir`" && `"$batPath`" $workerId"
    )

    Write-Host "  [OK] Worker $i launched: $title (ID: $workerId)" -ForegroundColor Green
    Start-Sleep -Seconds 2
}

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  $workerCount workers launched!" -ForegroundColor Green
Write-Host "  Each worker has a unique ID (W1..W$workerCount)" -ForegroundColor White
Write-Host "  Workers auto-claim tasks via deterministic protocol" -ForegroundColor White
Write-Host "  All claiming is serialized via .catalog.lock" -ForegroundColor White
Write-Host "  Set multi_stop=true in config.ini to stop all" -ForegroundColor DarkGray
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""
Read-Host "Press Enter to close this launcher window"
