# Auto-Continuous Launcher - reads config.ini, loops claude sessions
# Each loop iteration = 1 leaf task = 1 fresh claude session
# Window stays open, claude sessions rotate within the same window
# Protected by per-worker PID lock file - prevents duplicate instances
#   - Old process dead (crashed/closed) → new instance auto-takes over
#   - Old process alive → kills old instance, takes over (no more stale windows)
#
# Argument modes:
#   auto.bat                          → Single worker, W1, no assignment
#   auto.bat W2                       → Multi worker (from parallel.bat), W2, no assignment
#   auto.bat W3 P0-008                → Dispatcher (from start.bat), W3, assigned to P0-008
#
# After the first task (pre-assigned or claimed), subsequent iterations
# always use the normal claiming protocol (no pre-assignment).

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

# ---- Parse arguments ----
$workerId = 'W1'
$assignedModule = ''

if ($args.Count -ge 1) {
    if ($args[0] -match '^W\d+$') {
        $workerId = $args[0]
    }
}
if ($args.Count -ge 2) {
    if ($args[1] -match '^P\d-\d{3}$') {
        $assignedModule = $args[1]
    }
}

# ---- Per-worker PID lock: prevent duplicate instances ----
# Each worker ID (W1, W2...) has a lock file: .locks/worker_W1.pid
# The file stores the PowerShell process ID. On new launch:
#   - No lock file / PID dead → take over (new instance starts normally)
#   - PID alive → kill old process + take over (closes stale window automatically)
# parallel.bat/start.bat launch W1,W2,W3... → each has its own lock → no conflict.
$script:lockDir = Join-Path $scriptDir '.locks'
$script:lockFile = Join-Path $script:lockDir "worker_$workerId.pid"
$script:myPid = $PID
$script:lockAcquired = $false

function Acquire-WorkerLock {
    # Ensure lock directory exists
    if (-not (Test-Path $script:lockDir)) {
        try { New-Item -ItemType Directory -Path $script:lockDir -Force | Out-Null } catch {}
    }

    # Check for existing lock
    if (Test-Path $script:lockFile) {
        $oldPidStr = $null
        try { $oldPidStr = (Get-Content -Path $script:lockFile -Raw -Encoding UTF8).Trim() } catch {}

        if ($oldPidStr -and $oldPidStr -match '^\d+$') {
            $oldPid = [int]$oldPidStr

            # Don't kill ourselves
            if ($oldPid -ne $script:myPid) {
                $oldProc = $null
                try { $oldProc = Get-Process -Id $oldPid -ErrorAction SilentlyContinue } catch {}

                if ($oldProc) {
                    # Old instance is still alive - kill it (closes the stale window)
                    Write-Host ""
                    Write-Host "  [CLEANUP] Found stale worker $workerId (PID $oldPid)" -ForegroundColor Yellow
                    Write-Host "  Terminating old instance to take over..." -ForegroundColor Yellow

                    try {
                        # Kill the entire process tree (cmd.exe → powershell → claude)
                        Stop-Process -Id $oldPid -Force -ErrorAction SilentlyContinue
                        Start-Sleep -Seconds 2
                        Write-Host "  [OK] Old worker $workerId terminated." -ForegroundColor Green
                    } catch {
                        Write-Host "  [WARN] Could not kill PID $oldPid - taking over anyway" -ForegroundColor Yellow
                    }
                }
                # else: old PID is dead (crashed/exited) → stale lock file, just overwrite
            }
        }
    }

    # Write our PID
    try {
        Set-Content -Path $script:lockFile -Value $script:myPid -Encoding UTF8
        $script:lockAcquired = $true
        return $true
    } catch {
        Write-Host "  [WARN] Could not write lock file: $($_.Exception.Message)" -ForegroundColor Yellow
        # Continue anyway - lock file is best-effort
        $script:lockAcquired = $true
        return $true
    }
}

function Release-WorkerLock {
    if ($script:lockAcquired -and (Test-Path $script:lockFile)) {
        try {
            $storedPid = (Get-Content -Path $script:lockFile -Raw -Encoding UTF8).Trim()
            # Only delete if it's OUR lock (don't delete a newer instance's lock)
            if ($storedPid -eq [string]$script:myPid) {
                Remove-Item -Path $script:lockFile -Force -ErrorAction SilentlyContinue
            }
        } catch {}
    }
    $script:lockAcquired = $false
}

# Acquire lock immediately after parsing worker ID
Acquire-WorkerLock

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
    Write-Host "  Worker $workerId will not start. Set to false to resume." -ForegroundColor Yellow
    Write-Host "============================================================" -ForegroundColor Yellow
    Write-Host ""
    Release-WorkerLock
    Read-Host "Press Enter to exit"
    exit 0
}

# ---- Read auto-prompt.md ----
$msgFile = Join-Path $scriptDir 'auto-prompt.md'

if (-not (Test-Path $msgFile)) {
    Write-Host "ERROR: auto-prompt.md not found" -ForegroundColor Red
    Release-WorkerLock
    Read-Host "Press Enter to exit"
    exit 1
}

$msgTemplate = (Get-Content -Path $msgFile -Raw -Encoding UTF8).Trim()

if ([string]::IsNullOrWhiteSpace($msgTemplate)) {
    Write-Host "ERROR: auto-prompt.md is empty" -ForegroundColor Red
    Release-WorkerLock
    Read-Host "Press Enter to exit"
    exit 1
}

# ---- Display status ----
$taskNum = 0
$failCount = 0

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  ERP Auto Worker (Loop Mode)" -ForegroundColor Cyan
Write-Host "  Project:  $projectPath" -ForegroundColor White
Write-Host "  Worker:   $workerId (PID: $script:myPid)" -ForegroundColor White
if ($assignedModule) {
    Write-Host "  Assigned: $assignedModule (pre-assigned by dispatcher)" -ForegroundColor Green
} else {
    Write-Host "  Mode:     Claiming protocol (auto-claim from pool)" -ForegroundColor White
}
Write-Host "  Each task runs in a fresh Claude session" -ForegroundColor White
Write-Host "  Lock:     .locks/worker_$workerId.pid" -ForegroundColor DarkGray
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
        Write-Host "  Worker $workerId exiting. Set to false and restart to resume." -ForegroundColor Yellow
        Write-Host "============================================================" -ForegroundColor Yellow
        Write-Host ""
        Release-WorkerLock
        break
    }

    Write-Host "------------------------------------------------------------" -ForegroundColor DarkGray
    Write-Host "  Worker $workerId - Task session #$taskNum - $(Get-Date -Format 'HH:mm:ss')" -ForegroundColor Green
    if ($taskNum -eq 1 -and $assignedModule) {
        Write-Host "  Pre-assigned module: $assignedModule" -ForegroundColor Green
    }
    Write-Host "------------------------------------------------------------" -ForegroundColor DarkGray

    # Re-read prompt template (in case it was updated between tasks)
    $msgTemplate = (Get-Content -Path $msgFile -Raw -Encoding UTF8).Trim()

    # Replace worker ID placeholder
    $msg = $msgTemplate -replace '\{WORKER_ID\}', $workerId

    # For the first iteration only: if pre-assigned, add module instruction
    if ($taskNum -eq 1 -and $assignedModule) {
        $moduleInstruction = @"

---

**[DISPATCHER PRE-ASSIGNMENT]**
The smart dispatcher has pre-assigned you to module **$assignedModule**.
You MUST claim and execute a task from this module.
Follow the normal claiming protocol (Step 2), but restrict your candidate selection
to tasks belonging to module $assignedModule only.
All other protocol rules (execution layer ordering, file isolation, registration) still apply.
**[/DISPATCHER PRE-ASSIGNMENT]**
"@
        $msg = $msg + "`n`n" + $moduleInstruction
    }

    # Launch claude (blocking call - waits for task to complete)
    & claude $msg --dangerously-skip-permissions
    $claudeExitCode = $LASTEXITCODE

    # Check if claude command failed (e.g., not installed, crashed)
    if ($claudeExitCode -ne 0 -and $claudeExitCode -ne $null) {
        $failCount++
        Write-Host ""
        Write-Host "  [WARN] Claude exited with code $claudeExitCode (attempt $failCount/3)" -ForegroundColor Yellow

        if ($failCount -ge 3) {
            Write-Host ""
            Write-Host "  [ERROR] Claude failed 3 consecutive times. Worker $workerId stopping." -ForegroundColor Red
            Write-Host "  Possible causes:" -ForegroundColor DarkGray
            Write-Host "    - claude CLI not installed or not in PATH" -ForegroundColor DarkGray
            Write-Host "    - API key invalid or rate limited" -ForegroundColor DarkGray
            Write-Host "    - Network connectivity issues" -ForegroundColor DarkGray
            Write-Host ""
            Release-WorkerLock
            break
        }

        Start-Sleep -Seconds 5
        continue
    }

    # Reset fail counter on success
    $failCount = 0

    Write-Host ""
    Write-Host "  [DONE] Worker $workerId - Task session #$taskNum completed, starting next..." -ForegroundColor Green
    Write-Host ""

    # Brief pause to allow filesystem to settle
    Start-Sleep -Seconds 2

    # After first task, clear pre-assignment (subsequent tasks use normal claiming)
    # (assignedModule variable persists but $taskNum > 1 so the if-block won't trigger)
}

# Safety net: ensure lock is released on any exit path
Release-WorkerLock
