# 全自动连续开发规则（任务清单 + 工人池）

> **所属主文档**：`CLAUDE.md` §8
> **版本**：V5.0
> **最后修订**：2026-05-28

---

## 1. 核心架构：任务清单 + 依赖表 + 工人池

```
设计原则：
  - tasks_catalog.md 是全量叶子任务清单（带位置编号 + fetch_cursor）
  - tasks_dependency.md 是预计算依赖关系（46 模块依赖 + 并行分组 + 冲突矩阵）
  - tasks_active.md 是活跃任务（叶子级，动态维护，含 fetch_cursor）
  - config.ini 是统一配置（路径、停止开关、窗口数、补充策略）
  - auto.bat + auto.ps1 是通用工人启动器，auto.ps1 通过 while 循环驱动多个 claude 会话
  - 每个叶子任务在独立 Claude 会话中完成（一任务一会话）
  - 工人完成后自动认领下一个，始终保持窗口不空闲

任务调度：
  tasks_catalog.md（全量清单 - 叶子级，带位置编号）
       │
       │  补充（active 可认领数 < fetch_threshold 时）
       │  按 fetch_cursor 位置读取 + 查 tasks_dependency.md 确认依赖
       ▼
  tasks_active.md（活跃任务 - 叶子级，动态）
       │
  ┌────┼────┐────┐
  ▼    ▼    ▼    ▼
 工人  工人  工人  工人  (auto.bat × N)
  认领 → 执行 → 完成 → 认领下一个 → 循环

两种启动方式：
  单工人：auto.bat（1 个窗口，逐个任务执行）
  多工人：parallel.bat N（N 个窗口，同时从任务池认领，上限由 config.ini 的 max_workers 控制，最大 100）
  参数来源：config.ini
```

---

## 2. 配置文件（config.ini）

```ini
[project]
path=D:\ClaudeCode\ErpProject\AiErp      ; 项目根目录
git_branch=develop                         ; Git 分支

[execution]
single_stop=false    ; 单人停止开关（true = 当前任务完成后停止）
multi_stop=false     ; 多人停止开关（true = 所有工人当前任务完成后退出）

[parallel]
max_workers=4        ; 默认工人窗口数（1~100，根据电脑性能配置）
fetch_batch_size=12  ; 每批从 catalog 补充的任务条数
fetch_threshold=5    ; active-tasks 可认领数低于此值时触发补充

[lock]
timeout_seconds=120  ; 锁超时（秒）
retry_interval_seconds=3  ; 重试间隔
max_retries=40       ; 最大重试次数
```

**停止开关使用方式**：
- 想暂停开发：将 `single_stop` 或 `multi_stop` 改为 `true`
- 当前任务完成后工人会自动停止，不启动新窗口
- 恢复开发：改回 `false`，运行 `auto.bat` 或 `parallel.bat` 即可继续
- `auto.ps1` 和 `parallel.ps1` 启动时检查开关，开启则直接退出
- `auto-prompt.md` 中每个任务完成后也检查开关

---

## 3. 项目根目录文件

```
auto.bat          — 单工人启动器（双击启动，调用 auto.ps1）
auto.ps1          — 读 config.ini + while 循环：每轮检查停止开关 + 启动 claude
auto-prompt.md    — 工人循环提示词（两级查找 + 停止开关 + 游标补充逻辑）
config.ini        — 统一配置（路径、开关、窗口数、补充策略、锁参数）

parallel.bat      — 多工人启动器（parallel.bat N 启动 N 个窗口）
parallel.ps1      — 读 config.ini + 检查停止开关 + 启动 N 个 auto.bat 窗口

tasks_catalog.md    — 全量叶子任务清单（带位置编号 + fetch_cursor 参考）
tasks_dependency.md — 预计算依赖关系 + 并行分组 + 文件冲突矩阵
tasks_active.md     — 执行状态 + fetch_cursor + 活跃叶子任务
tasks_completed.md  — 完成归档（只写不读）

启动流程：
  auto.bat → auto.ps1 进入 while 循环：
    每轮：检查停止开关 → 读取 auto-prompt.md → claude 执行任务 → claude 退出 → 循环
```

---

## 4. 工人循环流程（AI 严格执行）

```
Step 0: 读取配置 + 检查停止开关
  读取 config.ini
  single_stop=true 或 multi_stop=true → 输出提示 → 不启动新窗口 → 退出

Step 1: 两级查找任务
  1A: 优先从 tasks_active.md 查找
    - 找到🔄任务 → 续接执行 → 跳到 Step 2
    - 找到⬜任务 → 认领执行 → 跳到 Step 2
    - 无可执行任务 → 进入 Step 1B
  1B: 从 tasks_catalog.md 补充
    - active 可认领数 < fetch_threshold？
    - 是 → 获取 .catalog.lock
      → 读取 tasks_active.md 中的 fetch_cursor
      → 从 tasks_catalog.md 的 fetch_cursor 之后取 fetch_batch_size 条任务
      → 查 tasks_dependency.md 确认每条任务所属模块的依赖已 ✅
      → 依赖满足的任务写入 tasks_active.md（状态 ⬜）
      → 更新 fetch_cursor → 释放锁 → 回到 Step 1A
    - fetch_cursor 已到末尾且无可执行任务 → 跳到 Step 4

Step 2: 执行任务
  1. 在 tasks_active.md 标记🔄 + 窗口ID
  2. 加载上下文（CLAUDE.md §3）+ 执行（§5）+ 自检（§6）
  3. 获取 .parallel.lock → git commit → 释放锁
  4. tasks_active.md 标记✅
  5. 检查停止开关（再读 config.ini）
     true → 不启动新窗口 → 退出（auto.ps1 循环检测到后关闭窗口）
     false → 正常退出 claude（auto.ps1 循环自动启动下一个会话）

Step 3: 模块完成检查
  当前模块所有叶子都✅？
  是 → 获取 .catalog.lock
     → tasks_active.md 清理该模块的已完成任务
     → 释放锁
     → 追加 tasks_completed.md
     → 回到 Step 1
  否 → 回到 Step 1A 继续下一个叶子

Step 4: 无任务可执行
  存在🔄任务（其他工人还在跑）→ 正常退出（auto.ps1 循环会重试）
  不存在🔄任务 → 项目完成 → 不启动替补 → 退出
```

---

## 5. 补充逻辑（从 catalog 到 active）

```
触发条件：
  tasks_active.md 中 ⬜ + 🔄 任务总数 < fetch_threshold（config.ini）

补充流程：
  1. 获取 .catalog.lock
  2. 读取 tasks_active.md 中的 fetch_cursor（如 = 212）
  3. 从 tasks_catalog.md 的位置 #0213 开始，取 fetch_batch_size 条任务
  4. 对每条任务查 tasks_dependency.md 确认所属模块前置依赖全部 ✅
  5. 依赖满足的 → 写入 tasks_active.md（按模块分组，状态 ⬜）
  6. 依赖未满足的 → 跳过，继续取下一条
  7. 更新 fetch_cursor 为本批次取到的最后一个位置编号
  8. 释放 .catalog.lock

清理流程（模块完成时）：
  1. 获取 .catalog.lock
  2. tasks_active.md 中该模块所有叶子均为 ✅
  3. tasks_active.md 删除该模块的所有已完成叶子
  4. 释放 .catalog.lock
  5. 追加完成记录到 tasks_completed.md
```

---

## 6. 无缝接续机制

```
每次新会话启动时，AI 通过以下机制实现零信息丢失：
  1. tasks_active.md 精确记录 fetch_cursor + 叶子任务状态（⬜/🔄/✅）和工人窗口
  2. tasks_dependency.md 记录模块依赖关系和并行分组
  3. tasks_catalog.md 提供全量任务清单和位置编号
  4. 任务文档自包含（Section 1-8），无需历史上下文
  5. 层级规范 + 全局规范提供所有技术约束
  6. tasks_completed.md 提供历史参考
  7. config.ini 提供停止开关和运行参数
  8. 新会话是干净状态，不受前一会话的上下文干扰
```

---

## 7. 多工人协作机制

```
共享文档（所有工人共同读写，通过锁串行化）：
  tasks_catalog.md    — 全量任务清单（只读，工人不修改）
  tasks_dependency.md — 依赖关系（只读，工人不修改）
  tasks_active.md     — 执行状态 + fetch_cursor + 活跃叶子任务（读写）
  tasks_completed.md  — 完成归档（只追加）

隔离方式：
  - tasks_active.md 的 fetch_cursor 更新 → 通过 .catalog.lock 串行化
  - git commit/push → 通过 .parallel.lock 串行化
  - tasks_active.md 中的🔄任务标记了工人窗口ID，防止重复认领
  - 无需单独的工人进度文件，tasks_active.md 已包含全部执行状态
```

---

## 8. 优先级阶段切换

```
P0 全部完成进入 P1（或 P1 进入 P2）时：
  1. tasks_dependency.md 中已预计算好各级依赖
  2. P0 模块全部完成后，P1 模块的前置依赖自动满足
  3. 工人取 P1 任务时查 tasks_dependency.md 发现依赖已 ✅，自动认领
  4. 无需人工干预，任务池自动推进
  5. 在最后一个模块完成时打标签：git tag v{P级别}-complete
```

---

## 9. 项目全部完成

```
IF fetch_cursor = 总任务数 AND tasks_active.md 中无⬜且无🔄:
    1. 所有工人检测到无任务可执行 → 直接退出（不启动替补）
    2. 最后一个退出的工人：
       a. 确认 tasks_completed.md 归档完整
       b. 在 develop 上打标签：git tag v1.0-complete
       c. 输出项目完成总结
```
