# Auto-Continuous Launcher - 1 task per session, new window per task

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
foreach ($f in @($relayFile, $sessionFile, $watchdogPs1)) {
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

# ---- Record CMD parent PID + CommandLine for safe verification ----
$cmdPid = 0
$cmdLine = ''
try {
    $myProc = Get-CimInstance Win32_Process -Filter "ProcessId = $PID" -ErrorAction SilentlyContinue
    if ($myProc -and $myProc.ParentProcessId) {
        $cmdPid = $myProc.ParentProcessId
        $parentProc = Get-CimInstance Win32_Process -Filter "ProcessId = $cmdPid" -ErrorAction SilentlyContinue
        if ($parentProc) { $cmdLine = $parentProc.CommandLine }
    }
} catch {}
Log "Parent CMD PID: $cmdPid | CommandLine: $cmdLine"

# ---- Build prompt ----
$msg = (Get-Content -Path $msgFile -Raw -Encoding UTF8).Trim()
$msg = $msg -replace '\{WORKER_ID\}', $workerId
$msg = $msg -replace '\{SCRIPT_DIR\}', $scriptDir
$msg = $msg -replace '\{SESSION_ID\}', $sessionId

# ---- Write session ID ----
[System.IO.File]::WriteAllText($sessionFile, $sessionId, [System.Text.Encoding]::UTF8)
Log "Session ID written: $sessionId"

# ============================================================
# WATCHDOG: detect .relay → kill old window → spawn new window
# ============================================================
# Write watchdog script to file
$wdLines = [System.Collections.ArrayList]::new()
[void]$wdLines.Add('$ErrorActionPreference = ''Continue''')
[void]$wdLines.Add("`$relayFile = '$relayFile'")
[void]$wdLines.Add("`$batFile = '$batFile'")
[void]$wdLines.Add("`$workerId = '$workerId'")
[void]$wdLines.Add("`$logFile = '$watchdogLog'")
[void]$wdLines.Add("`$scriptDir = '$scriptDir'")
[void]$wdLines.Add("`$expectedSession = '$sessionId'")
[void]$wdLines.Add("`$cmdPid = $cmdPid")
[void]$wdLines.Add("`$expectedCmdLine = '$cmdLine'")
[void]$wdLines.Add("`$watchdogPidFile = '$oldWatchdogPidFile'")
[void]$wdLines.Add('')
[void]$wdLines.Add('try { [System.IO.File]::WriteAllText($watchdogPidFile, "$PID", [System.Text.Encoding]::UTF8) } catch {}')
[void]$wdLines.Add('')
[void]$wdLines.Add('function WLog($m) {')
[void]$wdLines.Add('    $line = "[$(Get-Date -Format ''yyyy-MM-dd HH:mm:ss'')] [watchdog] $m"')
[void]$wdLines.Add('    try { [System.IO.File]::AppendAllText($logFile, "$line`r`n", [System.Text.Encoding]::UTF8) } catch {}')
[void]$wdLines.Add('}')
[void]$wdLines.Add('')
[void]$wdLines.Add('WLog "Started (PID: $PID), CMD PID: $cmdPid, expecting: $expectedSession"')
[void]$wdLines.Add('')
[void]$wdLines.Add('$maxWait = 14400')
[void]$wdLines.Add('for ($s = 0; $s -lt $maxWait; $s++) {')
[void]$wdLines.Add('    Start-Sleep -Seconds 1')
[void]$wdLines.Add('    if (-not (Test-Path $relayFile)) { continue }')
[void]$wdLines.Add('    $content = ''''')
[void]$wdLines.Add('    try { $content = [System.IO.File]::ReadAllText($relayFile, [System.Text.Encoding]::UTF8).Trim() } catch {}')
[void]$wdLines.Add('    if (-not ($content -like "*$expectedSession*")) {')
[void]$wdLines.Add('        WLog "Mismatch: content does NOT contain ''$expectedSession''. Deleting."')
[void]$wdLines.Add('        Remove-Item $relayFile -Force -ErrorAction SilentlyContinue')
[void]$wdLines.Add('        continue')
[void]$wdLines.Add('    }')
[void]$wdLines.Add('    WLog "MATCH! .relay contains session $expectedSession. Waiting 10s..."')
[void]$wdLines.Add('    Start-Sleep -Seconds 10')
[void]$wdLines.Add('    Remove-Item $relayFile -Force -ErrorAction SilentlyContinue')
[void]$wdLines.Add('')
[void]$wdLines.Add('    # ---- SAFETY: Verify CMD PID is still our auto.bat process ----')
[void]$wdLines.Add('    $safeToKill = $false')
[void]$wdLines.Add('    try {')
[void]$wdLines.Add('        $verifyProc = Get-CimInstance Win32_Process -Filter "ProcessId = $cmdPid" -ErrorAction SilentlyContinue')
[void]$wdLines.Add('        if ($verifyProc -and $verifyProc.Name -match ''cmd'' -and $verifyProc.CommandLine -like ''*auto.bat*'') {')
[void]$wdLines.Add('            $safeToKill = $true')
[void]$wdLines.Add('            WLog "Safety check PASSED: CMD PID $cmdPid is auto.bat"')
[void]$wdLines.Add('        } else {')
[void]$wdLines.Add('            WLog "SAFETY: CMD PID $cmdPid is NOT our auto.bat! Skipping kill."')
[void]$wdLines.Add('            if ($verifyProc) { WLog "Found: $($verifyProc.Name) | $($verifyProc.CommandLine)" }')
[void]$wdLines.Add('            else { WLog "CMD PID $cmdPid no longer exists" }')
[void]$wdLines.Add('        }')
[void]$wdLines.Add('    } catch {')
[void]$wdLines.Add('        WLog "SAFETY: Verification failed: $_"')
[void]$wdLines.Add('    }')
[void]$wdLines.Add('    if (-not $safeToKill) {')
[void]$wdLines.Add('        WLog "Spawning new window without killing (unsafe)..."')
[void]$wdLines.Add('        try {')
[void]$wdLines.Add('            $proc = Start-Process -FilePath $batFile -ArgumentList $workerId -WorkingDirectory $scriptDir -PassThru')
[void]$wdLines.Add('            WLog "New window launched (PID: $($proc.Id))"')
[void]$wdLines.Add('        } catch { WLog "ERROR spawning: $_" }')
[void]$wdLines.Add('        try { Remove-Item $watchdogPidFile -Force -ErrorAction SilentlyContinue } catch {}')
[void]$wdLines.Add('        WLog "Done (skip kill). Exiting."')
[void]$wdLines.Add('        exit 0')
[void]$wdLines.Add('    }')
[void]$wdLines.Add('')
[void]$wdLines.Add('    # Spawn new window FIRST (before killing, so we don''t die with the old tree)')
[void]$wdLines.Add('    WLog "Spawning new window FIRST..."')
[void]$wdLines.Add('    try {')
[void]$wdLines.Add('        $proc = Start-Process -FilePath $batFile -ArgumentList $workerId -WorkingDirectory $scriptDir -PassThru')
[void]$wdLines.Add('        WLog "New window launched (PID: $($proc.Id))"')
[void]$wdLines.Add('    } catch {')
[void]$wdLines.Add('        WLog "ERROR spawning: $_"')
[void]$wdLines.Add('    }')
[void]$wdLines.Add('')
[void]$wdLines.Add('    # ---- Kill ALL descendants of CMD, then CMD itself ----')
[void]$wdLines.Add('    WLog "Killing process tree under CMD PID $cmdPid..."')
[void]$wdLines.Add('    $myOwnPid = $PID')
[void]$wdLines.Add('    $toKill = [System.Collections.ArrayList]::new()')
[void]$wdLines.Add('    $queue = [System.Collections.ArrayList]::new()')
[void]$wdLines.Add('    [void]$queue.Add($cmdPid)')
[void]$wdLines.Add('    $visited = @{}')
[void]$wdLines.Add('    while ($queue.Count -gt 0) {')
[void]$wdLines.Add('        $current = $queue[0]')
[void]$wdLines.Add('        $queue.RemoveAt(0)')
[void]$wdLines.Add('        if ($visited.ContainsKey($current)) { continue }')
[void]$wdLines.Add('        $visited[$current] = $true')
[void]$wdLines.Add('        if ($current -eq $myOwnPid) {')
[void]$wdLines.Add('            WLog "Skipping own PID $myOwnPid (watchdog)"')
[void]$wdLines.Add('            continue')
[void]$wdLines.Add('        }')
[void]$wdLines.Add('        [void]$toKill.Add($current)')
[void]$wdLines.Add('        try {')
[void]$wdLines.Add('            $children = Get-CimInstance Win32_Process -Filter "ParentProcessId = $current" -ErrorAction SilentlyContinue')
[void]$wdLines.Add('            foreach ($child in $children) {')
[void]$wdLines.Add('                $childPid = [int]$child.ProcessId')
[void]$wdLines.Add('                if (-not $visited.ContainsKey($childPid)) {')
[void]$wdLines.Add('                    [void]$queue.Add($childPid)')
[void]$wdLines.Add('                }')
[void]$wdLines.Add('            }')
[void]$wdLines.Add('        } catch {}')
[void]$wdLines.Add('    }')
[void]$wdLines.Add('    WLog "Process tree PIDs to kill: $($toKill -join '', '')"')
[void]$wdLines.Add('')
[void]$wdLines.Add('    # Kill from leaves to root (reverse BFS order)')
[void]$wdLines.Add('    for ($k = $toKill.Count - 1; $k -ge 0; $k--) {')
[void]$wdLines.Add('        $targetPid = $toKill[$k]')
[void]$wdLines.Add('        try {')
[void]$wdLines.Add('            $p = Get-Process -Id $targetPid -ErrorAction SilentlyContinue')
[void]$wdLines.Add('            if ($p) {')
[void]$wdLines.Add('                $pName = $p.ProcessName')
[void]$wdLines.Add('                Stop-Process -Id $targetPid -Force -ErrorAction SilentlyContinue')
[void]$wdLines.Add('                WLog "Killed PID $targetPid ($pName)"')
[void]$wdLines.Add('            }')
[void]$wdLines.Add('        } catch {')
[void]$wdLines.Add('            WLog "Could not kill PID $targetPid : $_"')
[void]$wdLines.Add('        }')
[void]$wdLines.Add('    }')
[void]$wdLines.Add('')
[void]$wdLines.Add('    # Verify CMD is dead, fallback to taskkill if needed')
[void]$wdLines.Add('    Start-Sleep -Seconds 1')
[void]$wdLines.Add('    try {')
[void]$wdLines.Add('        $cmdAlive = Get-Process -Id $cmdPid -ErrorAction SilentlyContinue')
[void]$wdLines.Add('        if ($cmdAlive) {')
[void]$wdLines.Add('            WLog "CMD PID $cmdPid still alive! Using taskkill /F /T..."')
[void]$wdLines.Add('            $null = & cmd /c "taskkill /F /T /PID $cmdPid" 2>&1')
[void]$wdLines.Add('            Start-Sleep -Seconds 1')
[void]$wdLines.Add('            $cmdAlive2 = Get-Process -Id $cmdPid -ErrorAction SilentlyContinue')
[void]$wdLines.Add('            if ($cmdAlive2) { WLog "CRITICAL: CMD PID $cmdPid STILL alive!" }')
[void]$wdLines.Add('            else { WLog "CMD PID $cmdPid killed by taskkill" }')
[void]$wdLines.Add('        } else {')
[void]$wdLines.Add('            WLog "CMD PID $cmdPid confirmed dead"')
[void]$wdLines.Add('        }')
[void]$wdLines.Add('    } catch {}')
[void]$wdLines.Add('')
[void]$wdLines.Add('    try { Remove-Item $watchdogPidFile -Force -ErrorAction SilentlyContinue } catch {}')
[void]$wdLines.Add('    WLog "Done. Exiting."')
[void]$wdLines.Add('    exit 0')
[void]$wdLines.Add('}')
[void]$wdLines.Add('WLog "Timeout (4h). Exiting."')

[System.IO.File]::WriteAllText($watchdogPs1, ($wdLines -join "`r`n"), [System.Text.Encoding]::UTF8)

# Start watchdog hidden
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

# ---- If still alive, wait for watchdog to handle everything ----
Log "Waiting for watchdog to handle cleanup..."
Start-Sleep -Seconds 15

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
if (Test-Path $watchdogPs1) { try { Remove-Item $watchdogPs1 -Force } catch {} }
if (Test-Path $sessionFile) { try { Remove-Item $sessionFile -Force } catch {} }

Log "===== $workerId done ====="
exit 0
