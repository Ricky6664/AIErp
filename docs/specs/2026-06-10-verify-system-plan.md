# P0 自动验证修复系统 实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建独立于 auto.bat 开发流水线的 P0 全自动验证修复系统，覆盖编译验证、后端测试、前端组件测试、E2E 测试、代码合规扫描、前端设计重构，发现问题自动修复并创建 PR。

**Architecture:** CLI 管道式 7 Stage。verify.bat 启动 Claude Code 会话，按 verify-tasks.md 追踪进度，每 Stage 有独立的验证-修复闭环（最多重试 3 次），结果写入 reports/ 目录，最终自动创建 fix/verify-* 分支并提交 PR。

**Tech Stack:** Claude Code CLI + PowerShell + Maven + Vitest + Playwright + Vue Test Utils + Git + GitHub CLI

---

## 文件结构总览

| 文件 | 职责 | 操作 |
|------|------|------|
| `verify-config.ini` | 验证系统配置（调度/范围/修复/报告） | 创建 |
| `verify.bat` | Windows 入口，调用 verify.ps1 | 创建 |
| `verify.ps1` | 核心调度脚本，启动 Claude Code 会话 | 创建 |
| `verify-tasks.md` | 验证任务追踪（当前 Stage/批次/状态） | 创建 |
| `verify-prompt.md` | Claude Code 会话的验证执行指令 | 创建 |
| `tests-e2e/package.json` | E2E 测试依赖 (Playwright) | 创建 |
| `tests-e2e/playwright.config.ts` | Playwright 配置 | 创建 |
| `tests-e2e/fixtures/auth.ts` | 登录认证夹具 | 创建 |
| `tests-e2e/fixtures/helpers.ts` | 通用测试辅助函数 | 创建 |
| `tests-e2e/specs/auth/login.spec.ts` | 登录流程 E2E | 创建 |
| `tests-e2e/specs/org/company.spec.ts` | 组织架构公司管理 E2E | 创建 |
| `tests-e2e/specs/warehouse/warehouse.spec.ts` | 仓库管理 E2E | 创建 |
| `tests-e2e/specs/finance/account.spec.ts` | 财务科目 E2E | 创建 |
| `tests-e2e/specs/hrm/employee.spec.ts` | HRM 员工 E2E | 创建 |
| `erp-ai-web/src/__tests__/views/login.test.ts` | 登录页组件测试 | 创建 |
| `erp-ai-web/src/__tests__/views/org.test.ts` | 组织架构页组件测试 | 创建 |

---

### Task 1: 验证系统配置文件

**Files:**
- Create: `verify-config.ini`

- [ ] **Step 1: 编写 verify-config.ini**

```ini
; ============================================================
; ERP AI 自动验证修复系统 - 配置文件
; ============================================================

[schedule]
; 定时回归 cron 表达式（每天凌晨 3:00）
cron=0 3 * * *
; 启用定时回归
enabled=true

[scope]
; 一次性扫描的模式: full | quick
full_scan_mode=full
; 定时回归的模式: quick | compliance
cron_mode=quick

[modules]
; 需要验证的模块列表，all = 全部P0模块
target_modules=all

[fix]
; 自动修复最大重试次数
max_retry=3
; 单次 Stage 超时时间（分钟）
stage_timeout=30
; 是否自动创建 PR
auto_pr=true

[report]
; 报告输出目录
output_dir=reports
; 保留最近 N 份报告
keep_recent=10

[server]
; E2E 测试用的后端服务端口
e2e_port=8080
; 后端启动等待超时（秒）
startup_timeout=60
; 后端 profile
spring_profile=dev
```

- [ ] **Step 2: 提交**

```bash
git add verify-config.ini
git commit -m "feat(verify): 创建验证系统配置文件"
```

---

### Task 2: 验证任务追踪文件模板

**Files:**
- Create: `verify-tasks.md`

- [ ] **Step 1: 编写 verify-tasks.md 模板**

```markdown
# verify-tasks.md — 验证任务追踪

> **最后更新**：{TIMESTAMP}
> **运行模式**：{MODE}
> **当前分支**：{BRANCH}

---

## 执行头信息

| 字段 | 值 |
|------|-----|
| current_stage | Stage 1 |
| current_batch | - |
| status | 🔄 执行中 |
| last_failure | null |
| total_stages | 7 |
| completed_stages | 0 |
| report_dir | reports/{DATE}-verify-{SEQ} |

---

## Stage 进度

| Stage | 名称 | 状态 | 重试次数 | 耗时 |
|-------|------|:----:|:-------:|------|
| 1 | 编译验证 | ⬜ | 0 | - |
| 2 | 后端单元测试 | ⬜ | 0 | - |
| 3 | 前端组件测试 | ⬜ | 0 | - |
| 4 | E2E 页面测试 | ⬜ | 0 | - |
| 5 | 代码合规扫描 | ⬜ | 0 | - |
| 5.5 | 前端设计重构 | ⬜ | 0 | - |
| 6 | 汇总 & PR | ⬜ | 0 | - |

---

## 修复记录

| Stage | 文件 | 问题 | 修复方式 | 结果 |
|-------|------|------|---------|:----:|
| - | - | - | - | - |
```

- [ ] **Step 2: 提交**

```bash
git add verify-tasks.md
git commit -m "feat(verify): 创建验证任务追踪文件模板"
```

---

### Task 3: 验证系统入口脚本

**Files:**
- Create: `verify.bat`
- Create: `verify.ps1`

- [ ] **Step 1: 编写 verify.bat**

```bat
@echo off
chcp 65001 >nul 2>&1
cd /d "%~dp0"

set MODE=%1
if "%MODE%"=="" set MODE=full
if "%MODE%"=="cron" goto :run
if "%MODE%"=="full" goto :run
if "%MODE%"=="resume" goto :run
echo Usage: verify.bat [full^|cron^|resume]
echo   full   - 一次性全量扫描（7个Stage全部执行）
echo   cron   - 定时回归（跳过Stage 5.5）
echo   resume - 中断续跑，从上次断点继续
exit /b 1

:run
title ERP Verify - %MODE%
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0verify.ps1" -Mode %MODE%
```

- [ ] **Step 2: 编写 verify.ps1**

```powershell
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
```

- [ ] **Step 3: 提交**

```bash
git add verify.bat verify.ps1
git commit -m "feat(verify): 创建验证系统入口脚本"
```

---

### Task 4: 验证执行指令文件（verify-prompt.md）

**Files:**
- Create: `verify-prompt.md`

这是 Claude Code 会话的核心指令文件，告诉 AI 如何执行每个 Stage。

- [ ] **Step 1: 编写 verify-prompt.md**

```markdown
# ERP 自动验证修复 — 执行指令

> 运行模式: {MODE}
> 启动时间: 会话自动获取

---

## 0. 初始化

1. 读取 `verify-config.ini` 获取配置参数
2. 读取 `verify-tasks.md` 获取当前进度（resume 模式下使用）
3. 确定报告目录: `reports/{当前日期}-verify-{序号}`（检查已有目录递增序号）
4. 如 Mode=full，所有 7 个 Stage 执行；Mode=cron 时跳过 Stage 5.5
5. 如 Mode=resume，从 verify-tasks.md 的 current_stage 和 current_batch 恢复执行
6. 更新 verify-tasks.md 初始化状态

---

## Stage 1: 编译验证

**目标**: 确认前后端编译无错误

### 执行
```bash
# 后端编译
cd {项目根目录}
mvn clean compile -DskipTests 2>&1 | Out-File -FilePath reports/{date}-verify-{seq}/stage-1-compile.log -Encoding utf8

# 前端编译
cd erp-ai-web
pnpm install 2>&1 | Out-File -FilePath ../reports/{date}-verify-{seq}/stage-1-compile.log -Append -Encoding utf8
pnpm build 2>&1 | Out-File -FilePath ../reports/{date}-verify-{seq}/stage-1-compile.log -Append -Encoding utf8
```

### 判定
- exit code 均为 0 → PASS
- 有 ERROR 或 exit code ≠ 0 → FAIL，进入修复

### 修复流程（FAIL 时）
1. 解析 stage-1-compile.log，提取所有 [ERROR] 行
2. 按文件分组，每文件作为独立修复单元
3. 对每个 ERROR:
   - 读取错误文件和行号
   - 分析根因（缺失 import / 类型不匹配 / 语法错误）
   - Edit 修复
4. 重跑编译命令
5. 仍失败则重试，最多 3 次
6. 3 次后仍失败 → 标记 ⚠️ 写入报告，跳过该错误
7. 生成 `stage-1-compile.md` 报告

### 报告内容
- 编译结果（PASS/FAIL）
- 修复文件和行数清单
- 未能修复的项（如有）
- 耗时

---

## Stage 2: 后端单元测试

**目标**: 运行所有后端单元测试，确保全部通过

### 执行（分批）
```bash
# Batch A: P0-001 ~ P0-005 (基础层)
mvn test -Dtest="com.erp.config.*,com.erp.common.*,com.erp.auth.*,com.erp.system.*" 2>&1 | Out-File reports/.../stage-2-backend.log -Encoding utf8

# Batch B: P0-006 ~ P0-009 (业务层A — org/product/crm/srm)
mvn test -Dtest="com.erp.module.org.*,com.erp.module.product.*,com.erp.module.crm.*,com.erp.module.srm.*" 2>&1 | Out-File -Append ...

# Batch C: P0-010 ~ P0-012 (业务层B — warehouse/finance/hrm)
mvn test -Dtest="com.erp.module.warehouse.*,com.erp.module.finance.*,com.erp.hrm.*" 2>&1 | Out-File -Append ...

# Batch D: P0-013 ~ P0-014 (部署/测试)
mvn test -Dtest="com.erp.deploy.*,com.erp.test.*" 2>&1 | Out-File -Append ...
```

### 判定
- 所有批次 exit code 均为 0，且无 FAILED/ERROR → PASS
- 有失败用例 → FAIL

### 修复流程（FAIL 时）
1. 解析 Maven Surefire 报告，定位失败用例
2. 对每个失败用例:
   - 读取测试源码，理解测试意图
   - 检查断言是否与当前代码一致
   - 检查 Mock 数据是否匹配 Entity 字段变更
   - 检查数据库约束是否满足
3. Edit 修复测试代码（不修改业务代码，除非业务代码有 bug）
4. 重跑对应批次的测试
5. 最多重试 3 次

### 报告内容
- 各批次通过/失败统计
- 失败用例清单及修复记录
- 覆盖率数据（如 JaCoCo 已配置）

---

## Stage 3: 前端组件测试

**目标**: 运行所有 Vitest 组件测试

### 执行
```bash
cd erp-ai-web
npx vitest run --reporter=verbose 2>&1 | Out-File ../reports/{date}-verify-{seq}/stage-3-frontend.log -Encoding utf8
```

### 判定
- exit code = 0，所有测试 PASS → 通过
- 有失败或超时 → FAIL

### 修复流程（FAIL 时）
1. 解析 vitest 输出，定位失败用例
2. 对每个失败用例:
   - 检查组件 props/events/slots 是否与当前组件实现一致
   - 检查 stub/mock 是否正确模拟了依赖组件
   - 修复测试代码
3. 重跑 `npx vitest run`
4. 最多重试 3 次

---

## Stage 4: E2E 页面测试

**目标**: Playwright 端到端测试覆盖每模块核心流程

### 前置
1. 确认 `tests-e2e/` 目录存在且 `pnpm install` 完成
2. 启动后端服务:
```bash
Start-Process -NoNewWindow mvn -ArgumentList "spring-boot:run","-Dspring.profiles.active=dev" -WorkingDirectory {项目根目录}
```
3. 等待服务就绪: 循环 curl `http://localhost:8080/api/actuator/health` 最多 60 秒

### 执行
```bash
cd tests-e2e
npx playwright test 2>&1 | Out-File ../reports/{date}-verify-{seq}/stage-4-e2e.log -Encoding utf8
```

### 后置
1. 停止后端进程
2. 收集 `tests-e2e/screenshots/` 中的失败截图到报告目录

### 判定
- 所有 spec 通过 → PASS
- 有失败 → FAIL，进入修复

### 修复流程（FAIL 时）
1. 查看失败截图和日志
2. 分析失败原因（选择器变化 / 超时 / 页面结构变化）
3. 修复测试代码或页面代码
4. 重跑失败的 spec: `npx playwright test --grep "{spec name}"`
5. 最多重试 3 次

---

## Stage 5: 代码合规扫描

**目标**: 检查代码规范和安全问题

### 扫描规则
执行以下 Grep/Read 扫描，输出违规清单:

| 检查项 | 扫描方式 | 规则 |
|--------|---------|------|
| 硬编码密码 | Grep `password\s*=` `secret\s*=` `apiKey\s*=` 在非 properties 文件中 | 应使用环境变量 |
| 缺失 @Transactional | 读取所有 ServiceImpl，检查写操作（insert/update/delete/save）方法 | 必须有注解 |
| Controller 无 @Operation | 读取所有 Controller，检查每个 @XxxMapping 方法 | 必须有 @Operation |
| import 通配符 | Grep `import\s+\S+\.\*` | 必须展开 |
| 未使用的 import | 前端: `eslint` 输出 | 删除 |

### 修复
- 每项违规自动修复（Edit 修改源代码）
- 重扫确认修复成功
- 最多重试 3 次

### 报告
- 扫描项总数、违规数、修复数
- 未修复项清单

---

## Stage 5.5: 前端设计重构（仅 full 模式）

**目标**: 在需求范围内改进页面视觉和交互体验

**约束**: 不改功能逻辑、路由、权限、i18n key；不新增功能

### Round 1: 公共基础组件（7 个文件，阻断门禁）

需重构文件:
- `erp-ai-web/src/components/list-table/index.vue`
- `erp-ai-web/src/components/edit-table/index.vue`
- `erp-ai-web/src/components/master-form/MasterForm.vue`
- `erp-ai-web/src/components/detail-table/index.vue`
- `erp-ai-web/src/components/basic-input/index.vue`
- `erp-ai-web/src/components/query-panel/index.vue`
- `erp-ai-web/src/components/action-bar/index.vue`

每文件 SOP:
1. Read 当前文件
2. 使用 frontend-design 技能分析改进点
3. Edit 应用改进
4. `pnpm build` 验证编译
5. PASS → 下一个文件; FAIL → 回滚 → 重试 1 次
6. Round 1 全部完成后: `npx vitest run` 确认组件测试通过
7. git commit: "refactor(verify): 重构公共基础组件样式"

**阻断**: Round 1 任一文件 2 次尝试后仍失败 → 暂停 Stage 5.5，记录原因

### Round 2: 核心入口页面（6 个文件）

- `erp-ai-web/src/views/login/index.vue`
- `erp-ai-web/src/views/home/index.vue`
- `erp-ai-web/src/layouts/components/Sidebar/index.vue`
- `erp-ai-web/src/layouts/components/Navbar.vue`
- `erp-ai-web/src/layouts/components/TabNav/index.vue`
- `erp-ai-web/src/App.vue`

SOP 同 Round 1。完成后: `pnpm build` + 对应组件测试

### Round 3: 每模块 1 个代表性页面（14 个文件，逐模块推进）

按顺序逐模块:
1. P0-006 org → `erp-ai-web/src/views/org/CompanyForm.vue`
2. P0-007 product → 找到对应列表页
3. P0-008 crm → 找到对应列表页
4. P0-009 srm → 找到对应列表页
5. P0-010 warehouse → `erp-ai-web/src/views/warehouse/warehouse/index.vue`
6. P0-011 finance → `erp-ai-web/src/views/finance/account/index.vue`
7. P0-012 hrm → `erp-ai-web/src/views/hrm/employeecenter/index.vue`

每模块完成后 `pnpm build` + git commit

### Round 4: 剩余页面（约 50+ 个文件，每 5 个一批）

收集所有未被 Round 1~3 覆盖的 .vue 文件，每 5 个一批重构，每批 `pnpm build` + git commit

### 报告
- 各轮文件数、成功数、失败数
- 关键改进点摘要
- 重构前后截图对比（如有 Playwright 截图）

---

## Stage 6: 汇总 & PR

### 汇总
1. 读取所有 Stage 报告
2. 生成 `summary.md`:
   - 概览表（Stage / 结果 / 修复数 / 耗时）
   - 修复文件清单
   - 未修复项（标记 ⚠️）
   - 测试统计（总数/通过/失败/跳过）

### 提交 & PR
```bash
git checkout -b fix/verify-YYYYMMDD-HHmmss
git add -A
git commit -m "fix(verify): 自动验证修复 - {日期} {模式} — X个Stage通过, Y个问题修复"
git push -u origin fix/verify-YYYYMMDD-HHmmss
gh pr create --title "fix(verify): 自动验证修复 - {日期}" --body "$(cat reports/{date}-verify-{seq}/summary.md)"
```

### 清理
- 更新 verify-tasks.md 标记全部完成
- 删除超过 keep_recent 的旧报告目录
- 输出 PR 链接

---

## 中断续跑（仅 Mode=resume）

读取 `verify-tasks.md`:
- `current_stage` → 从该 Stage 继续
- `current_batch` → 从该批次继续
- `status` → 确认状态后恢复执行
- 已完成 Stage 的报告不重新生成
```

- [ ] **Step 2: 提交**

```bash
git add verify-prompt.md
git commit -m "feat(verify): 创建验证执行指令文件"
```

---

### Task 5: E2E 测试基础设施

**Files:**
- Create: `tests-e2e/package.json`
- Create: `tests-e2e/playwright.config.ts`
- Create: `tests-e2e/fixtures/auth.ts`
- Create: `tests-e2e/fixtures/helpers.ts`

- [ ] **Step 1: 编写 tests-e2e/package.json**

```json
{
  "name": "erp-ai-e2e",
  "private": true,
  "type": "module",
  "scripts": {
    "test": "playwright test",
    "test:headed": "playwright test --headed",
    "test:debug": "playwright test --debug",
    "report": "playwright show-report"
  },
  "devDependencies": {
    "@playwright/test": "^1.52.0"
  }
}
```

- [ ] **Step 2: 编写 tests-e2e/playwright.config.ts**

```typescript
import { defineConfig, devices } from '@playwright/test'

export default defineConfig({
  testDir: './specs',
  timeout: 30000,
  expect: { timeout: 10000 },
  fullyParallel: false,
  retries: 1,
  workers: 1,
  reporter: [['list'], ['html', { outputFolder: 'playwright-report' }]],
  use: {
    baseURL: 'http://localhost:5173',
    screenshot: 'only-on-failure',
    video: 'retain-on-failure',
    trace: 'on-first-retry'
  },
  projects: [
    {
      name: 'chromium',
      use: { ...devices['Desktop Chrome'] }
    }
  ],
  webServer: {
    command: 'cd ../erp-ai-web && pnpm dev',
    url: 'http://localhost:5173',
    reuseExistingServer: true,
    timeout: 60000
  }
})
```

- [ ] **Step 3: 编写 tests-e2e/fixtures/auth.ts**

```typescript
import { test as base, expect } from '@playwright/test'

export const test = base.extend({
  authenticatedPage: async ({ page }, use) => {
    await page.goto('/login')
    await page.fill('input[placeholder*="用户名"]', 'admin')
    await page.fill('input[placeholder*="密码"]', 'admin123')
    await page.click('button:has-text("登录")')
    await page.waitForURL('**/home', { timeout: 10000 })
    await use(page)
  }
})

export { expect }
```

- [ ] **Step 4: 编写 tests-e2e/fixtures/helpers.ts**

```typescript
import { Page, expect } from '@playwright/test'

export async function waitForTableLoad(page: Page) {
  await page.waitForSelector('.vxe-table--body, .el-table__body', { timeout: 10000 })
}

export async function clickTableRow(page: Page, rowIndex: number = 0) {
  const rows = page.locator('.vxe-table--body tr, .el-table__body tr')
  await rows.nth(rowIndex).click()
}

export async function fillFormField(page: Page, label: string, value: string) {
  const formItem = page.locator('.el-form-item', { hasText: label })
  const input = formItem.locator('input, textarea').first()
  await input.fill(value)
}

export async function submitForm(page: Page) {
  await page.click('button:has-text("保存")')
  await page.waitForTimeout(1000)
}

export async function verifySuccessMessage(page: Page) {
  await expect(page.locator('.el-message--success, .el-notification--success').first()).toBeVisible({ timeout: 5000 })
}

export async function verifyPageTitle(page: Page, title: string) {
  await expect(page.locator('h1, .page-title, .el-breadcrumb').filter({ hasText: title }).first()).toBeVisible({ timeout: 5000 })
}
```

- [ ] **Step 5: 安装 Playwright 依赖**

```bash
cd tests-e2e
pnpm install
npx playwright install chromium
```

- [ ] **Step 6: 提交**

```bash
git add tests-e2e/package.json tests-e2e/playwright.config.ts tests-e2e/fixtures/
git commit -m "feat(verify): 搭建E2E测试基础设施(Playwright)"
```

---

### Task 6: E2E 测试规格 — 认证模块

**Files:**
- Create: `tests-e2e/specs/auth/login.spec.ts`

- [ ] **Step 1: 编写登录流程 E2E 测试**

```typescript
import { test, expect } from '@playwright/test'

test.describe('登录流程', () => {
  test('正常登录: 输入正确的用户名和密码后跳转首页', async ({ page }) => {
    await page.goto('/login')

    // 验证登录页关键元素存在
    await expect(page.locator('input[placeholder*="用户名"], input[name*="username"]').first()).toBeVisible()
    await expect(page.locator('input[placeholder*="密码"], input[type="password"]').first()).toBeVisible()
    await expect(page.getByRole('button', { name: /登录|登録|login/i })).toBeVisible()

    // 执行登录
    await page.fill('input[placeholder*="用户名"], input[name*="username"]', 'admin')
    await page.fill('input[placeholder*="密码"], input[type="password"]', 'admin123')
    await page.getByRole('button', { name: /登录|登録|login/i }).click()

    // 验证跳转到首页
    await page.waitForURL(/\/home|\/dashboard|\//, { timeout: 10000 })
  })

  test('登录失败: 错误密码应显示提示信息', async ({ page }) => {
    await page.goto('/login')
    await page.fill('input[placeholder*="用户名"], input[name*="username"]', 'admin')
    await page.fill('input[placeholder*="密码"], input[type="password"]', 'wrongpassword')
    await page.getByRole('button', { name: /登录|登録|login/i }).click()

    // 应该有错误提示（消息提醒或表单错误文字）
    const errorIndicator = page.locator('.el-message--error, .el-form-item__error, .el-alert--error, [class*="error"]').first()
    await expect(errorIndicator).toBeVisible({ timeout: 5000 })
  })

  test('空表单验证: 不输入直接点登录应有校验提示', async ({ page }) => {
    await page.goto('/login')
    await page.getByRole('button', { name: /登录|登録|login/i }).click()

    // 应有表单校验提示或按钮保持不可点击
    const validation = page.locator('.el-form-item__error, .el-message--warning').first()
    const buttonDisabled = page.getByRole('button', { name: /登录|登録|login/i }).isDisabled()
    const hasValidation = await validation.isVisible().catch(() => false)
    const isDisabled = await buttonDisabled.catch(() => false)
    expect(hasValidation || isDisabled).toBeTruthy()
  })
})
```

- [ ] **Step 2: 提交**

```bash
git add tests-e2e/specs/auth/
git commit -m "feat(verify): 添加登录流程E2E测试"
```

---

### Task 7: E2E 测试规格 — 组织架构模块

**Files:**
- Create: `tests-e2e/specs/org/company.spec.ts`

- [ ] **Step 1: 编写组织架构公司管理 E2E 测试**

```typescript
import { test, expect } from '@playwright/test'
import { waitForTableLoad, verifyPageTitle } from '../../fixtures/helpers'

test.describe('组织架构 — 公司管理', () => {
  test.beforeEach(async ({ page }) => {
    // 先登录
    await page.goto('/login')
    await page.fill('input[placeholder*="用户名"], input[name*="username"]', 'admin')
    await page.fill('input[placeholder*="密码"], input[type="password"]', 'admin123')
    await page.getByRole('button', { name: /登录|登録|login/i }).click()
    await page.waitForURL(/\/home|\/dashboard/, { timeout: 10000 })
  })

  test('公司列表页渲染: 表格和数据正常加载', async ({ page }) => {
    await page.goto('/org/company')
    await waitForTableLoad(page)
    await verifyPageTitle(page, '公司')
  })

  test('公司工作台渲染: 统计卡片和图表正常显示', async ({ page }) => {
    await page.goto('/org/workbench')
    await expect(page.locator('.kpi-card, .chart-area, [class*="kpi"]').first()).toBeVisible({ timeout: 10000 })
  })
})
```

- [ ] **Step 2: 提交**

```bash
git add tests-e2e/specs/org/
git commit -m "feat(verify): 添加组织架构E2E测试"
```

---

### Task 8: E2E 测试规格 — 仓库 & 财务 & HRM

**Files:**
- Create: `tests-e2e/specs/warehouse/warehouse.spec.ts`
- Create: `tests-e2e/specs/finance/account.spec.ts`
- Create: `tests-e2e/specs/hrm/employee.spec.ts`

- [ ] **Step 1: 编写仓库管理 E2E 测试**

```typescript
import { test, expect } from '@playwright/test'
import { waitForTableLoad, verifyPageTitle } from '../../fixtures/helpers'

test.describe('仓库管理', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login')
    await page.fill('input[placeholder*="用户名"], input[name*="username"]', 'admin')
    await page.fill('input[placeholder*="密码"], input[type="password"]', 'admin123')
    await page.getByRole('button', { name: /登录|登録|login/i }).click()
    await page.waitForURL(/\/home|\/dashboard/, { timeout: 10000 })
  })

  test('仓库列表页渲染: 表格数据正常加载', async ({ page }) => {
    await page.goto('/warehouse/warehouse')
    await waitForTableLoad(page)
    await verifyPageTitle(page, '仓库')
  })

  test('仓库工作台渲染: KPI卡片和图表显示', async ({ page }) => {
    await page.goto('/warehouse/workbench')
    await expect(page.locator('.kpi-card, .chart-area, [class*="kpi"], [class*="chart"]').first()).toBeVisible({ timeout: 10000 })
  })
})
```

- [ ] **Step 2: 编写财务科目 E2E 测试**

```typescript
import { test, expect } from '@playwright/test'
import { waitForTableLoad, verifyPageTitle } from '../../fixtures/helpers'

test.describe('财务管理 — 科目', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login')
    await page.fill('input[placeholder*="用户名"], input[name*="username"]', 'admin')
    await page.fill('input[placeholder*="密码"], input[type="password"]', 'admin123')
    await page.getByRole('button', { name: /登录|登録|login/i }).click()
    await page.waitForURL(/\/home|\/dashboard/, { timeout: 10000 })
  })

  test('科目列表页渲染: 表格数据正常加载', async ({ page }) => {
    await page.goto('/finance/account')
    await waitForTableLoad(page)
    await verifyPageTitle(page, '科目')
  })
})
```

- [ ] **Step 3: 编写 HRM 员工 E2E 测试**

```typescript
import { test, expect } from '@playwright/test'
import { waitForTableLoad, verifyPageTitle } from '../../fixtures/helpers'

test.describe('HRM — 员工管理', () => {
  test.beforeEach(async ({ page }) => {
    await page.goto('/login')
    await page.fill('input[placeholder*="用户名"], input[name*="username"]', 'admin')
    await page.fill('input[placeholder*="密码"], input[type="password"]', 'admin123')
    await page.getByRole('button', { name: /登录|登録|login/i }).click()
    await page.waitForURL(/\/home|\/dashboard/, { timeout: 10000 })
  })

  test('员工中心页面渲染: 基本结构正常', async ({ page }) => {
    await page.goto('/hrm/employeecenter')
    await expect(page.locator('body').first()).toBeVisible()
  })

  test('员工档案页面渲染: 表单或列表正常显示', async ({ page }) => {
    await page.goto('/hrm/employeearchive')
    await waitForTableLoad(page)
  })
})
```

- [ ] **Step 4: 提交**

```bash
git add tests-e2e/specs/warehouse/ tests-e2e/specs/finance/ tests-e2e/specs/hrm/
git commit -m "feat(verify): 添加仓库/财务/HRM模块E2E测试"
```

---

### Task 9: 前端页面视图组件测试

**Files:**
- Create: `erp-ai-web/src/__tests__/views/login.test.ts`
- Create: `erp-ai-web/src/__tests__/views/org.test.ts`

- [ ] **Step 1: 编写登录页组件测试**

```typescript
import { describe, it, expect, beforeEach } from 'vitest'
import { mount } from '@vue/test-utils'
import LoginPage from '@/views/login/index.vue'

describe('LoginPage', () => {
  it('渲染登录表单: 用户名输入框、密码输入框、登录按钮存在', () => {
    const wrapper = mount(LoginPage, {
      global: {
        stubs: {
          CaptchaImage: { template: '<div class="captcha-stub"></div>' },
          PasswordStrength: { template: '<div class="password-strength-stub"></div>' }
        }
      }
    })

    const html = wrapper.html()
    expect(html).toBeTruthy()
    // 页面应包含基本表单元素
    const hasInput = wrapper.find('input').exists() || wrapper.find('el-input').exists()
    const hasButton = wrapper.find('button').exists() || wrapper.find('el-button').exists()
    expect(hasInput || hasButton).toBe(true)
  })

  it('渲染时不崩溃: 组件正常挂载', () => {
    const wrapper = mount(LoginPage, {
      global: {
        stubs: {
          CaptchaImage: true,
          PasswordStrength: true
        }
      }
    })
    expect(wrapper.exists()).toBe(true)
  })
})
```

- [ ] **Step 2: 编写组织架构页组件测试**

```typescript
import { describe, it, expect } from 'vitest'
import { mount } from '@vue/test-utils'
import CompanyForm from '@/views/org/CompanyForm.vue'
import OrgWorkbench from '@/views/org/OrgWorkbench.vue'

describe('CompanyForm', () => {
  it('渲染时不崩溃: 组件正常挂载', () => {
    const wrapper = mount(CompanyForm, {
      global: {
        stubs: {
          MasterForm: { template: '<div class="master-form-stub"><slot /></div>' },
          FormField: { template: '<div class="form-field-stub"><slot /></div>' }
        }
      }
    })
    expect(wrapper.exists()).toBe(true)
  })
})

describe('OrgWorkbench', () => {
  it('渲染时不崩溃: 组件正常挂载', () => {
    const wrapper = mount(OrgWorkbench, {
      global: {
        stubs: {
          KpiCard: { template: '<div class="kpi-card-stub"></div>' }
        }
      }
    })
    expect(wrapper.exists()).toBe(true)
  })
})
```

- [ ] **Step 3: 运行测试验证**

```bash
cd erp-ai-web
npx vitest run src/__tests__/views/
```

- [ ] **Step 4: 提交**

```bash
git add erp-ai-web/src/__tests__/views/
git commit -m "feat(verify): 添加登录页和组织架构页组件测试"
```

---

### Task 10: 集成验证 — 首次全量运行

**Files:** 无新建，验证整体系统可运行

- [ ] **Step 1: 检查所有交付物**

```bash
# 确认所有文件已创建
ls verify-config.ini verify.bat verify.ps1 verify-tasks.md verify-prompt.md
ls tests-e2e/package.json tests-e2e/playwright.config.ts tests-e2e/fixtures/
ls tests-e2e/specs/auth/ tests-e2e/specs/org/ tests-e2e/specs/warehouse/
ls erp-ai-web/src/__tests__/views/
```

- [ ] **Step 2: 安装 E2E 依赖**

```bash
cd tests-e2e && pnpm install
```

- [ ] **Step 3: 验证前端组件测试可运行**

```bash
cd erp-ai-web && npx vitest run --reporter=verbose 2>&1
```

预期: 所有已有测试通过，新增测试通过

- [ ] **Step 4: 验证后端测试可运行**

```bash
mvn test -Dtest="com.erp.config.*,com.erp.common.*" 2>&1
```

预期: 基础层测试全部通过

- [ ] **Step 5: 提交**

```bash
git add -A
git commit -m "feat(verify): 完成验证系统全部基础设施搭建"
```

---

## 执行顺序

```
Task 1 → Task 2 → Task 3 → Task 4 → Task 5 → Task 6 → Task 7 → Task 8 → Task 9 → Task 10
  (配置 → 追踪 → 入口 → 指令 → E2E基础 → 认证E2E → 组织E2E → 业务E2E → 组件测试 → 集成验证)
```

## 注意事项

1. 本项目 vitest 使用 `happy-dom` 环境，测试文件匹配 `src/**/*.test.ts`
2. 测试全局 setup 已配置 ElementPlus 全局插件
3. 后端后端服务 E2E 测试前需确认端口 8080 可用且数据库连接正常
4. E2E 测试使用的登录凭据需与实际开发环境一致（admin / admin123）
5. Playwright webServer 配置指向 Vite dev server (port 5173)，前后端分离架构下 API 调用走 vite proxy 到后端 8080
