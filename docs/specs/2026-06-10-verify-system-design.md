# P0 自动验证修复系统 — 设计规格

> **版本**：V1.0
> **创建日期**：2026-06-10
> **状态**：已批准，执行中

---

## 1. 概述

为已完成开发的 14 个 P0 模块建立独立的自动验证修复系统。系统独立于现有 `auto.bat` 开发流水线，通过 CLI 管道式架构覆盖编译验证、后端测试、前端测试、E2E 测试、代码合规扫描和前端设计重构，发现问题后自动修复并创建 PR。

## 2. 核心决策

| 维度 | 决策 |
|------|------|
| 架构 | CLI 管道式 7 Stage |
| 修复策略 | 全自动修复 + PR，每 Stage 最多重试 3 次 |
| 运行方式 | 一次性全量扫描 + 每天凌晨定时回归 |
| 测试层级 | 后端 mvn test + 前端 vitest + E2E Playwright |
| 前端重构 | 4 轮渐进，Round 1 公共组件为阻断门禁 |
| 隔离方式 | 独立脚本/配置/追踪/分支，不占开发窗口 |

## 3. 目录结构

```
AiErp/
├── verify.bat                    # Windows 入口
├── verify.ps1                    # 核心调度脚本
├── verify-config.ini             # 验证系统配置
├── verify-tasks.md               # 验证任务追踪（类似 tasks_active.md）
├── reports/                      # 验证报告输出目录
│   └── YYYY-MM-DD-verify-001/
├── tests-e2e/                    # E2E 测试目录
│   ├── playwright.config.ts
│   ├── package.json
│   ├── fixtures/
│   ├── specs/
│   │   ├── auth/
│   │   ├── org/
│   │   ├── product/
│   │   ├── crm/
│   │   ├── warehouse/
│   │   ├── finance/
│   │   └── hrm/
│   └── screenshots/
└── erp-ai-web/src/__tests__/     # 前端组件测试（扩展）
    ├── components/
    └── views/
```

## 4. Stage 设计

### 4.1 Stage 1 — 编译验证

- 后端: `mvn clean compile -DskipTests`
- 前端: `pnpm install && pnpm build`
- 预计 2~5 分钟
- 修复: 缺失 import、类型不匹配、清理残留产物

### 4.2 Stage 2 — 后端单元测试

- 分批执行: 基础层(P0-001~005) → 业务A(P0-006~009) → 业务B(P0-010~012) → 部署测试(P0-013~014)
- 覆盖率: JaCoCo，目标 ≥ 60%
- 修复: 断言不匹配、NPE、数据库约束冲突

### 4.3 Stage 3 — 前端组件测试

- 框架: Vitest + @vue/test-utils
- 覆盖: 公共组件 + 每模块 1~2 个代表性页面
- 预计 5~10 分钟

### 4.4 Stage 4 — E2E 页面测试

- 框架: Playwright (Chromium headless)
- 覆盖: 每 P0 模块 1 条核心流程（约 14~16 条）
- 先启动后端服务，测试完停止
- 失败自动截图

### 4.5 Stage 5 — 代码合规扫描

- 命名规范、注解完整性、安全审计、import 规范、事务注解、API 规范
- 扫描用正则 + AST 解析

### 4.6 Stage 5.5 — 前端设计重构（仅 full 模式）

4 轮渐进推进:
- **Round 1** (7文件): 公共基础组件 — 阻断门禁
- **Round 2** (6文件): 核心入口页面
- **Round 3** (14文件): 每模块 1 个代表性页面，逐模块推进
- **Round 4** (50+文件): 剩余页面，每 5 个一批

约束: 不改功能逻辑、不改路由/权限、不改 i18n key、只在需求范围内调整

### 4.7 Stage 6 — 汇总 & PR

- 生成 summary.md
- `git checkout -b fix/verify-YYYYMMDD-HHmmss`
- `git push && gh pr create`
- 输出 PR 链接

## 5. 修复闭环

每个 Stage 内部:
```
执行验证 → 收集输出 → 判断结果
  ├── PASS → 写入报告 → 下一 Stage
  └── FAIL → 解析错误 → Agent修复 → 重跑 → 最多3次
       └── 3次仍失败 → 标记 ⚠️ 人工修复
```

修复 Agent 按 Stage 分:
- fix-compile, fix-backend-test, fix-frontend-test, fix-e2e, fix-compliance

## 6. 执行调度

- `verify.bat full` — 一次性全量 7 Stage
- `verify.bat cron` — 定时回归 Stage 1~4 + Stage 5（跳过 5.5）
- `verify.bat resume` — 中断续跑

## 7. 配置 (verify-config.ini)

```ini
[schedule]      cron=0 3 * * *, enabled=true
[scope]         full_scan_mode=full, cron_mode=quick
[modules]       target_modules=all
[fix]           max_retry=3, stage_timeout=30, auto_pr=true
[report]        output_dir=reports, keep_recent=10
```

## 8. 与开发系统隔离

| 维度 | 开发系统 | 验证系统 |
|------|---------|---------|
| 入口 | auto.bat | verify.bat |
| 调度 | auto.ps1 | verify.ps1 |
| 配置 | config.ini | verify-config.ini |
| 追踪 | tasks_active.md | verify-tasks.md |
| 报告 | tasks_completed.md | reports/ |
| 分支 | feature/P*-XXX | fix/verify-* |
| 模式 | 持续循环 | 一次性/定时 |
