# Auto-Continuous Launcher - 1 task per session, new window per task
# Session ID system: auto.bat generates unique ID → auto.ps1 writes .session-id
# → Claude reads .session-id → Claude writes .relay with that ID on completion
# → Watchdog validates ID → spawns new auto.bat → kills old window

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$logFile = Join-Path $scriptDir 'auto.log'

# ---- Logger ----
function Log($msg) {
    $line = "[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] $msg"
    try { [System.IO.File]::AppendAllText($logFile, "$line`r`n", [System.Text.Encoding]::UTF8) } catch {}
    try { Write-Host "  $line" } catch {}
}

# ---- Parse arguments ----
$workerId = 'W1'
$sessionId = ''
$i = 0
while ($i -lt $args.Count) {
    if ($args[$i] -eq '--session' -and ($i + 1) -lt $args.Count) {
        $sessionId = $args[$i + 1]
        $i += 2
    } elseif ($args[$i] -match '^W\d+$') {
        $workerId = $args[$i]
        $i++
    } elseif ($args[$i] -match '^P\d-\d{3}$') {
        # assigned module - skip for now
        $i++
    } else {
        $i++
    }
}

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

# ---- Paths ----
$msgFile = Join-Path $scriptDir 'auto-prompt.md'
$batFile = Join-Path $scriptDir 'auto.bat'
$relayFile = Join-Path $scriptDir '.relay'
$sessionFile = Join-Path $scriptDir '.session-id'
$watchdogLog = Join-Path $scriptDir 'watchdog.log'
$watchdogPs1 = Join-Path $scriptDir '.watchdog.ps1'

# ---- Initial checks ----
$config = Read-Config

if (-not (Test-Path $msgFile)) {
    Write-Host "ERROR: auto-prompt.md not found" -ForegroundColor Red
    Read-Host "Press Enter to exit"
    exit 1
}

if ($config['execution.single_stop'] -eq 'true' -or $config['execution.multi_stop'] -eq 'true') {
    Write-Host "[STOP] Stop switch is ON. Worker $workerId will not start." -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 1
}

# ---- Kill old watchdog ----
$oldWatchdogPidFile = Join-Path $scriptDir '.watchdog.pid'
if (Test-Path $oldWatchdogPidFile) {
    $oldPid = (Get-Content $oldWatchdogPidFile -Raw -ErrorAction SilentlyContinue).Trim()
    if ($oldPid) {
        try {
            $oldProc = Get-Process -Id $oldPid -ErrorAction SilentlyContinue
            if ($oldProc) {
                Stop-Process -Id $oldPid -Force -ErrorAction SilentlyContinue
                Log "Killed old watchdog (PID: $oldPid)"
            }
        } catch {}
    }
    Remove-Item $oldWatchdogPidFile -Force -ErrorAction SilentlyContinue
}

# ---- Clean stale files ----
foreach ($f in @($relayFile, $sessionFile, $watchdogPs1, $cmdPidFile)) {
    if (Test-Path $f) { Remove-Item $f -Force -ErrorAction SilentlyContinue }
}

# ---- Display banner ----
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  ERP Auto Worker - $workerId" -ForegroundColor Cyan
Write-Host "  Session: $sessionId" -ForegroundColor Cyan
Write-Host "  PS PID: $PID" -ForegroundColor DarkGray
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

Log "===== $workerId started | Session: $sessionId | PID: $PID ====="

# ---- Build prompt ----
$msg = (Get-Content -Path $msgFile -Raw -Encoding UTF8).Trim()
$msg = $msg -replace '\{WORKER_ID\}', $workerId
$msg = $msg -replace '\{SCRIPT_DIR\}', $scriptDir
$msg = $msg -replace '\{SESSION_ID\}', $sessionId

# ---- Write session ID for Claude to read ----
[System.IO.File]::WriteAllText($sessionFile, $sessionId, [System.Text.Encoding]::UTF8)
Log "Session ID written to .session-id: $sessionId"

# ---- Write current PID for watchdog to kill later ----
$pidFile = Join-Path $scriptDir '.worker.pid'
[System.IO.File]::WriteAllText($pidFile, "$PID", [System.Text.Encoding]::UTF8)

# ---- Record parent CMD PID (before Claude can mess with window title) ----
$cmdPid = 0
try {
    $myProc = Get-CimInstance Win32_Process -Filter "ProcessId = $PID" -ErrorAction SilentlyContinue
    if ($myProc -and $myProc.ParentProcessId) {
        $cmdPid = $myProc.ParentProcessId
    }
} catch {}
$cmdPidFile = Join-Path $scriptDir '.cmd.pid'
[System.IO.File]::WriteAllText($cmdPidFile, "$cmdPid", [System.Text.Encoding]::UTF8)
Log "Parent CMD PID: $cmdPid"

# ============================================================
# Launch WATCHDOG (hidden PowerShell process)
# Watchdog polls .relay, validates session ID, spawns new window
# ============================================================
$watchdogScript = @"
`$ErrorActionPreference = 'Continue'
`$relayFile = '$relayFile'
`$batFile = '$batFile'
`$workerId = '$workerId'
`$logFile = '$watchdogLog'
`$scriptDir = '$scriptDir'
`$expectedSession = '$sessionId'
`$psPid = $PID
`$watchdogPidFile = '$oldWatchdogPidFile'
`$cmdPidFile = '$cmdPidFile'

# Write our PID
try { [System.IO.File]::WriteAllText(`$watchdogPidFile, "`$PID", [System.Text.Encoding]::UTF8) } catch {}

function WLog(`$m) {
    `$line = "[`$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] [watchdog] `$m"
    try { [System.IO.File]::AppendAllText(`$logFile, "`$line`r`n", [System.Text.Encoding]::UTF8) } catch {}
}

WLog "Started (PID: `$PID), PS parent PID: `$psPid, expecting session: `$expectedSession"

`$maxWait = 14400
for (`$s = 0; `$s -lt `$maxWait; `$s++) {
    Start-Sleep -Seconds 1

    if (-not (Test-Path `$relayFile)) { continue }

    # Read .relay content
    `$content = ''
    try { `$content = [System.IO.File]::ReadAllText(`$relayFile, [System.Text.Encoding]::UTF8).Trim() } catch {}

    # Validate session ID
    if (`$content -ne `$expectedSession) {
        WLog "Session mismatch: got '`$content', expected '`$expectedSession'. Deleting."
        Remove-Item `$relayFile -Force -ErrorAction SilentlyContinue
        continue
    }

    # Match!
    WLog "MATCH! Session `$expectedSession completed."

    Remove-Item `$relayFile -Force -ErrorAction SilentlyContinue

    # Clean up markers
    try { Remove-Item `$watchdogPidFile -Force -ErrorAction SilentlyContinue } catch {}

    # Wait a few seconds for old window to finish outputting
    WLog "Waiting 8 seconds for old window to finish..."
    Start-Sleep -Seconds 8

    # Find parent CMD PID (recorded by auto.ps1 at startup, before Claude could change title)
    `$parentPid = 0
    try {
        `$recordedCmdPid = [System.IO.File]::ReadAllText(`$cmdPidFile, [System.Text.Encoding]::UTF8).Trim()
        if (`$recordedCmdPid -and `$recordedCmdPid -ne '0') {
            # Verify this CMD is still alive and its title matches our session
            `$cmdProc = Get-Process -Id `$recordedCmdPid -ErrorAction SilentlyContinue
            if (`$cmdProc) {
                # Double check via WMI to be extra safe
                `$wmiProc = Get-CimInstance Win32_Process -Filter "ProcessId = `$recordedCmdPid" -ErrorAction SilentlyContinue
                if (`$wmiProc -and `$wmiProc.Name -match 'cmd') {
                    `$parentPid = [int]`$recordedCmdPid
                    WLog "Verified CMD PID: `$parentPid (Name: `$(`$wmiProc.Name), CommandLine: `$(`$wmiProc.CommandLine))"
                } else {
                    WLog "Recorded PID `$recordedCmdPid is not a CMD process anymore"
                }
            } else {
                WLog "Recorded CMD PID `$recordedCmdPid no longer running"
            }
        }
    } catch {
        WLog "Could not verify parent CMD: `$_"
    }

    # Spawn new auto.bat (new window with new session ID)
    try {
        `$proc = Start-Process -FilePath `$batFile -ArgumentList `$workerId -WorkingDirectory `$scriptDir -PassThru
        WLog "New window launched (PID: `$(`$proc.Id))"
    } catch {
        WLog "ERROR spawning new window: `$_"
    }

    # Kill old auto.ps1 process
    try {
        Stop-Process -Id `$psPid -Force -ErrorAction SilentlyContinue
        WLog "Killed old PS process (PID: `$psPid)"
    } catch {}

    # Kill old CMD window
    if (`$parentPid -gt 0) {
        try {
            Stop-Process -Id `$parentPid -Force -ErrorAction SilentlyContinue
            WLog "Killed old CMD window (PID: `$parentPid)"
        } catch {
            WLog "Could not kill old CMD: `$_"
        }
    }

    WLog "Done. Exiting watchdog."
    exit 0
}

WLog "Timeout (4h). Exiting."
"@

[System.IO.File]::WriteAllText($watchdogPs1, $watchdogScript, [System.Text.Encoding]::UTF8)

# Start watchdog COMPLETELY hidden (no visible window at all)
$psi = New-Object System.Diagnostics.ProcessStartInfo
$psi.FileName = "powershell.exe"
$psi.Arguments = "-NoProfile -ExecutionPolicy Bypass -File `"$watchdogPs1`""
$psi.WorkingDirectory = $scriptDir
$psi.CreateNoWindow = $true
$psi.UseShellExecute = $false
$psi.WindowStyle = [System.Diagnostics.ProcessWindowStyle]::Hidden
$watchdogProc = [System.Diagnostics.Process]::Start($psi)

Log "Watchdog started hidden (PID: $($watchdogProc.Id))"

# ============================================================
# Launch Claude
# ============================================================
Log "Launching Claude..."

& claude $msg --dangerously-skip-permissions

$exitCode = $LASTEXITCODE
Log "Claude exited (code: $exitCode)"

# If auto.ps1 is still alive (rare), wait for watchdog
Start-Sleep -Seconds 5
if (-not $watchdogProc.HasExited) {
    if (Test-Path $relayFile) {
        Log ".relay exists, waiting for watchdog..."
        $watchdogProc.WaitForExit(30000)
    }
    if (-not $watchdogProc.HasExited) {
        Log "No .relay, killing watchdog"
        try { Stop-Process -Id $watchdogProc.Id -Force } catch {}
    }
}

# Clean up
foreach ($f in @($watchdogPs1, $pidFile, $sessionFile, $cmdPidFile)) {
    if (Test-Path $f) { try { Remove-Item $f -Force } catch {} }
}

Log "===== $workerId done ====="
exit 0
