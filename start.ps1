# Smart Parallel Dispatcher
# 分析当前可并行的模块 → 只启动需要的窗口数 → 每个窗口带预分配模块
#
# 解决的核心问题：
#   旧模式 parallel.bat 同时打开N个窗口让它们抢任务 → 有竞态风险
#   新模式 start.bat 先分析再按需开窗口 → 零竞态，每个窗口知道自己该做什么
#
# 用法：start.bat [N]  (N 可选，覆盖 config.ini 的 max_workers)

# ---- Global error trap: prevent window from closing on any error ----
trap {
    Write-Host ""
    Write-Host "============================================================" -ForegroundColor Red
    Write-Host "  [FATAL ERROR] Unhandled exception:" -ForegroundColor Red
    Write-Host "  $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "  Location: $($_.InvocationInfo.PositionMessage)" -ForegroundColor Red
    Write-Host "============================================================" -ForegroundColor Red
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

$ErrorActionPreference = 'Stop'

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Smart Parallel Dispatcher" -ForegroundColor Cyan
Write-Host "  分析可并行模块 → 按需启动窗口 → 预分配任务" -ForegroundColor White
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

# ================================================================
# Helper: parse config.ini
# ================================================================
function Read-Config {
    $configFile = Join-Path $scriptDir 'config.ini'
    $config = @{}
    $currentSection = ''
    if (Test-Path $configFile) {
        foreach ($line in Get-Content $configFile -Encoding UTF8) {
            $line = $line.Trim()
            if ($line -eq '' -or $line.StartsWith(';')) { continue }
            if ($line -match '^\[(.+)\]$') { $currentSection = $Matches[1]; continue }
            if ($line -match '^([^=]+)=(.*)$') {
                $key = "$currentSection.$($Matches[1].Trim())"
                $config[$key] = $Matches[2].Trim()
            }
        }
    }
    return $config
}

# ================================================================
# Helper: parse tasks_dependency.md module table
# ================================================================
function Read-ModuleTable {
    $depFile = Join-Path $scriptDir 'tasks_dependency.md'
    $modules = @{}
    $content = Get-Content $depFile -Encoding UTF8
    $inTable = $false

    foreach ($line in $content) {
        $line = $line.Trim()

        # Detect table header
        if ($line -match '^\|.*模块.*名称.*严格前置依赖') {
            $inTable = $true
            continue
        }

        # Skip separator
        if ($line -match '^\|[-:|]+\|$') { continue }

        # End of table (empty line or new section)
        if ($inTable -and ($line -eq '' -or ($line -match '^##' -and $line -notmatch '^\|'))) {
            $inTable = $false
            continue
        }

        # Parse table row
        if ($inTable -and $line -match '^\|') {
            $cols = $line -split '\|' | ForEach-Object { $_.Trim() }
            # Remove empty first/last from split
            $cols = $cols | Where-Object { $_ -ne '' }

            if ($cols.Count -ge 6) {
                $moduleId = $cols[0]
                if ($moduleId -match '^P\d-\d{3}$') {
                    $name = $cols[1]

                    # Parse strict dependencies
                    $depStr = $cols[2]
                    $deps = @()
                    if ($depStr -ne '无') {
                        if ($depStr -match 'P0全部') {
                            # P1 modules depend on all 14 P0 modules
                            $deps = @('P0-001','P0-002','P0-003','P0-004','P0-005',
                                      'P0-006','P0-007','P0-008','P0-009','P0-010',
                                      'P0-011','P0-012','P0-013','P0-014')
                        }
                        else {
                            $found = [regex]::Matches($depStr, 'P\d-\d{3}')
                            $deps = @($found | ForEach-Object { $_.Value } | Sort-Object -Unique)
                        }
                    }

                    # Parse parallel modules
                    $parStr = $cols[4]
                    $parallelWith = @()
                    if ($parStr -and $parStr -ne '无') {
                        $found = [regex]::Matches($parStr, 'P\d-\d{3}')
                        $parallelWith = @($found | ForEach-Object { $_.Value })
                    }

                    # Parse execution mode
                    $modeStr = $cols[5]
                    $mode = if ($modeStr -match 'SERIAL') { 'SERIAL' } else { 'PARALLEL' }

                    $modules[$moduleId] = @{
                        Name = $name
                        Deps = $deps
                        ParallelWith = $parallelWith
                        Mode = $mode
                    }
                }
            }
        }
    }
    return $modules
}

# ================================================================
# Helper: parse task counts from tasks_catalog.md
# ================================================================
function Read-TaskCounts {
    $catFile = Join-Path $scriptDir 'tasks_catalog.md'
    $taskCounts = @{}
    $content = Get-Content $catFile -Encoding UTF8

    foreach ($line in $content) {
        if ($line -match '^###\s+(P\d-\d{3})\s+.*?(\d+)\s*任务') {
            $taskCounts[$Matches[1]] = [int]$Matches[2]
        }
    }
    return $taskCounts
}

# ================================================================
# Helper: parse completed modules from tasks_completed.md
# ================================================================
function Read-CompletedModules {
    $compFile = Join-Path $scriptDir 'tasks_completed.md'
    $completed = @()

    if (Test-Path $compFile) {
        $content = Get-Content $compFile -Encoding UTF8
        foreach ($line in $content) {
            # Match module completion markers like "### 模块完成: P0-001 ✅"
            if ($line -match '模块完成[：:]\s*(P\d-\d{3})') {
                $completed += $Matches[1]
            }
        }
    }
    return ($completed | Sort-Object -Unique)
}

# ================================================================
# Helper: parse active modules from tasks_active.md
# ================================================================
function Read-ActiveModules {
    $actFile = Join-Path $scriptDir 'tasks_active.md'
    $active = @{}

    if (Test-Path $actFile) {
        $rawLines = Get-Content $actFile -Encoding UTF8

        # ---- Strip HTML comment blocks (<!-- ... -->) ----
        # Templates/examples inside comments must NOT be parsed as real data
        $content = @()
        $inComment = $false
        foreach ($raw in $rawLines) {
            if ($raw -match '<!--') { $inComment = $true }
            if (-not $inComment) { $content += $raw }
            if ($raw -match '-->') { $inComment = $false }
        }

        $inTable = $false
        $currentModule = ''

        foreach ($line in $content) {
            $line = $line.Trim()

            # Module header: ### P0-001 - 后端项目框架搭建（SERIAL）
            if ($line -match '^###\s+(P\d-\d{3})') {
                $currentModule = $Matches[1]
                continue
            }

            # Table separator
            if ($line -match '^\|[-:|]+\|$') { $inTable = $true; continue }

            # Task row: | P0-001-xxx | task name | L0 | 🔄 | W1 |
            if ($inTable -and $currentModule -and $line -match '^\|') {
                $cols = $line -split '\|' | ForEach-Object { $_.Trim() }
                $cols = $cols | Where-Object { $_ -ne '' }
                if ($cols.Count -ge 4) {
                    $status = $cols[3]
                    $worker = if ($cols.Count -ge 5) { $cols[4] } else { '' }
                    if ($status -eq '🔄' -or $status -eq '⬜') {
                        if (-not $active.ContainsKey($currentModule)) {
                            $active[$currentModule] = @{ Workers = @(); TaskCount = 0 }
                        }
                        $active[$currentModule].TaskCount++
                        if ($worker -and $worker -ne '') {
                            $active[$currentModule].Workers += $worker
                        }
                    }
                }
            }

            # Section header resets
            if ($line -match '^## [一二三四五六七]') {
                $currentModule = ''
                $inTable = $false
            }
        }
    }
    return $active
}

# ================================================================
# Helper: parse active claims registry from tasks_active.md
# ================================================================
function Read-ActiveClaims {
    $actFile = Join-Path $scriptDir 'tasks_active.md'
    $claims = @()

    if (Test-Path $actFile) {
        $rawLines = Get-Content $actFile -Encoding UTF8

        # ---- Strip HTML comment blocks (<!-- ... -->) ----
        $content = @()
        $inComment = $false
        foreach ($raw in $rawLines) {
            if ($raw -match '<!--') { $inComment = $true }
            if (-not $inComment) { $content += $raw }
            if ($raw -match '-->') { $inComment = $false }
        }

        $inRegistry = $false

        foreach ($line in $content) {
            $line = $line.Trim()
            if ($line -match '活跃认领注册表') { $inRegistry = $true; continue }
            if ($line -match '^## [五六七]' -and $inRegistry) { $inRegistry = $false; continue }
            if ($inRegistry -and $line -match '^\|' -and $line -notmatch '^\|[-:|]+\|$' -and $line -notmatch '任务编号' -and $line -notmatch '空') {
                $cols = $line -split '\|' | ForEach-Object { $_.Trim() }
                $cols = $cols | Where-Object { $_ -ne '' }
                if ($cols.Count -ge 2) {
                    $claims += @{ TaskId = $cols[0]; Worker = $cols[1] }
                }
            }
        }
    }
    return $claims
}

# ================================================================
# MAIN LOGIC
# ================================================================

# 1. Read config
$config = Read-Config
$maxWorkers = if ($config['parallel.max_workers']) { [int]$config['parallel.max_workers'] } else { 4 }
$multiStop = ($config['execution.multi_stop'] -eq 'true')
$projectPath = if ($config['project.path']) { $config['project.path'] } else { $scriptDir }

# CLI override
if ($args.Count -gt 0) {
    $parsed = 0
    if ([int]::TryParse($args[0], [ref]$parsed)) { $maxWorkers = $parsed }
}
if ($maxWorkers -lt 1) { $maxWorkers = 1 }
if ($maxWorkers -gt 100) { $maxWorkers = 100 }

# Check stop switch
if ($multiStop) {
    Write-Host "  [STOP] multi_stop = true, 无法启动" -ForegroundColor Yellow
    Write-Host "  请将 config.ini 中 multi_stop 改为 false" -ForegroundColor Yellow
    Read-Host "Press Enter to exit"
    exit 0
}

# 2. Parse all data sources
Write-Host "  [1/5] 读取模块依赖表..." -ForegroundColor DarkGray
$allModules = Read-ModuleTable
Write-Host "        已解析 $($allModules.Count) 个模块" -ForegroundColor White

Write-Host "  [2/5] 读取任务清单..." -ForegroundColor DarkGray
$taskCounts = Read-TaskCounts
$totalTasks = ($taskCounts.Values | Measure-Object -Sum).Sum
Write-Host "        总任务数: $totalTasks" -ForegroundColor White

Write-Host "  [3/5] 读取已完成模块..." -ForegroundColor DarkGray
$completedModules = Read-CompletedModules
Write-Host "        已完成: $(if ($completedModules.Count -eq 0) { '无（项目从零开始）' } else { $completedModules -join ', ' })" -ForegroundColor White

Write-Host "  [4/5] 读取活跃任务..." -ForegroundColor DarkGray
$activeModules = Read-ActiveModules
$activeClaims = Read-ActiveClaims
$activeWorkerCount = ($activeClaims | Select-Object -Property Worker -Unique).Count
Write-Host "        活跃模块: $(if ($activeModules.Count -eq 0) { '无' } else { ($activeModules.Keys | Sort-Object) -join ', ' })" -ForegroundColor White
Write-Host "        活跃工人: $activeWorkerCount" -ForegroundColor White

# 3. Find eligible modules (all deps completed)
Write-Host "  [5/5] 分析可并行模块..." -ForegroundColor DarkGray
$eligible = @()
$skipped = @()

foreach ($mod in ($allModules.Keys | Sort-Object)) {
    $info = $allModules[$mod]
    $deps = $info.Deps

    # C1: All strict dependencies must be completed
    $depsMet = $true
    $missingDeps = @()
    foreach ($dep in $deps) {
        if ($dep -notin $completedModules) {
            $depsMet = $false
            $missingDeps += $dep
        }
    }

    if (-not $depsMet) {
        $skipped += @{ Module = $mod; Reason = "依赖未满足: $($missingDeps -join ', ')" }
        continue
    }

    # C0: P0 global gate — P1/P2 modules cannot start until ALL 14 P0 modules complete
    if ($mod -match '^P[12]-\d{3}$') {
        $allP0Modules = @('P0-001','P0-002','P0-003','P0-004','P0-005','P0-006','P0-007',
                          'P0-008','P0-009','P0-010','P0-011','P0-012','P0-013','P0-014')
        $p0Incomplete = @($allP0Modules | Where-Object { $_ -notin $completedModules })
        if ($p0Incomplete.Count -gt 0) {
            $skipped += @{ Module = $mod; Reason = "P0全局门禁: P0未全部完成，缺少 $($p0Incomplete.Count) 个模块: $($p0Incomplete -join ', ')" }
            continue
        }
    }

    # Already fully completed
    if ($mod -in $completedModules) {
        continue
    }

    # Check if already active
    $isActive = $activeModules.ContainsKey($mod)

    # C2: SERIAL module already active → cannot add workers
    if ($info.Mode -eq 'SERIAL' -and $isActive) {
        $skipped += @{ Module = $mod; Reason = "SERIAL 模式，已有工人执行中" }
        continue
    }

    $eligible += @{
        Module = $mod
        Name = $info.Name
        Mode = $info.Mode
        Active = $isActive
        TaskCount = if ($taskCounts.ContainsKey($mod)) { $taskCounts[$mod] } else { 0 }
    }
}

# 4. Calculate worker count per eligible module (round-robin fair allocation)
$totalNeeded = 0
$capacity = $maxWorkers - $activeWorkerCount

# Phase 1: SERIAL modules get exactly 1 worker each
foreach ($e in $eligible) {
    if ($e.Mode -eq 'SERIAL') {
        if ($capacity -gt 0) {
            $e['Workers'] = 1
            $totalNeeded++
            $capacity--
        } else {
            $e['Workers'] = 0
        }
    }
}

# Phase 2: PARALLEL modules — give 1 worker each first (round-robin)
$parallelModules = @($eligible | Where-Object { $_.Mode -eq 'PARALLEL' })
foreach ($e in $parallelModules) {
    if ($capacity -gt 0) {
        $e['Workers'] = 1
        $totalNeeded++
        $capacity--
    } else {
        $e['Workers'] = 0
    }
}

# Phase 3: Distribute remaining capacity to PARALLEL modules (proportional to task count)
while ($capacity -gt 0) {
    $distributed = $false
    foreach ($e in $parallelModules) {
        if ($capacity -le 0) { break }
        if ($e.Workers -lt $e.TaskCount) {
            $e['Workers']++
            $totalNeeded++
            $capacity--
            $distributed = $true
        }
    }
    if (-not $distributed) { break }  # All PARALLEL modules saturated
}

# Total = active workers + new workers
$totalWorkers = $activeWorkerCount + $totalNeeded
if ($totalWorkers -gt $maxWorkers) {
    # Trim from the end (lowest priority eligible modules)
    $excess = $totalWorkers - $maxWorkers
    for ($i = $eligible.Count - 1; $i -ge 0 -and $excess -gt 0; $i--) {
        $trim = [Math]::Min($eligible[$i].Workers, $excess)
        $eligible[$i].Workers -= $trim
        $excess -= $trim
        $totalNeeded -= $trim
    }
    # Remove modules with 0 workers
    $eligible = @($eligible | Where-Object { $_.Workers -gt 0 })
    $totalWorkers = $activeWorkerCount + $totalNeeded
}

# 5. Display analysis result
Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Analysis Result" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  max_workers (config): $maxWorkers" -ForegroundColor White
Write-Host "  Active workers:       $activeWorkerCount" -ForegroundColor White
Write-Host "  Eligible modules:     $($eligible.Count)" -ForegroundColor White
Write-Host ""

if ($eligible.Count -eq 0) {
    Write-Host "  [!] No eligible modules found." -ForegroundColor Yellow
    if ($skipped.Count -gt 0) {
        Write-Host ""
        Write-Host "  Skipped modules:" -ForegroundColor Yellow
        foreach ($s in $skipped) {
            Write-Host "    $($s.Module): $($s.Reason)" -ForegroundColor DarkYellow
        }
    }
    Write-Host ""
    Write-Host "  Possible reasons:" -ForegroundColor Yellow
    Write-Host "    - All modules completed (project done)" -ForegroundColor DarkGray
    Write-Host "    - No module has all dependencies satisfied" -ForegroundColor DarkGray
    Write-Host "    - All eligible SERIAL modules already have workers" -ForegroundColor DarkGray
    Write-Host ""
    Read-Host "  Press Enter to exit"
    exit 0
}

Write-Host "  Launch plan:" -ForegroundColor Green
Write-Host "  +------------+------+--------------------------------+" -ForegroundColor DarkGray
Write-Host "  | Module     | Mode | Workers                        |" -ForegroundColor DarkGray
Write-Host "  +------------+------+--------------------------------+" -ForegroundColor DarkGray
foreach ($e in $eligible) {
    $activeTag = if ($e.Active) { " (already running)" } else { "" }
    $wStr = "$($e.Workers)$activeTag"
    Write-Host ("  | {0,-10} | {1,-4} | {2,-30} |" -f $e.Module, $e.Mode, $wStr) -ForegroundColor White
}
Write-Host "  +------------+------+--------------------------------+" -ForegroundColor DarkGray
Write-Host "  Total new workers to launch: $totalNeeded" -ForegroundColor Green
Write-Host "  Total workers after launch:  $totalWorkers / $maxWorkers" -ForegroundColor Green
Write-Host ""

# 6. Launch workers
$batPath = Join-Path $scriptDir 'auto.bat'
$workerIndex = $activeWorkerCount  # Continue numbering from active workers

foreach ($e in $eligible) {
    if ($e.Active) {
        Write-Host "  [SKIP] $($e.Module) already has active workers" -ForegroundColor DarkGray
        continue
    }

    for ($w = 1; $w -le $e.Workers; $w++) {
        $workerIndex++
        $workerId = "W$workerIndex"
        $title = "ERP-Worker-$workerId"

        Start-Process cmd.exe -ArgumentList @(
            '/c',
            "title $title && cd /d `"$scriptDir`" && `"$batPath`" $workerId $($e.Module)"
        )

        Write-Host "  [OK] $workerId launched: $title -> $($e.Module) ($($e.Mode))" -ForegroundColor Green
        Start-Sleep -Seconds 2
    }
}

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  $totalNeeded workers launched!" -ForegroundColor Green
Write-Host "  Each worker has a pre-assigned module (no race condition)" -ForegroundColor White
Write-Host "  After pre-assigned task, workers auto-claim next task" -ForegroundColor White
Write-Host "  Set multi_stop=true in config.ini to stop all" -ForegroundColor DarkGray
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""
Read-Host "Press Enter to close this dispatcher window"
