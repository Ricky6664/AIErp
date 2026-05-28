# 全自动连续开发规则（确定性并行引擎）

> **所属主文档**：`CLAUDE.md` §8
> **版本**：V6.0
> **最后修订**：2026-05-28
> **核心原则**：确定性 —— 在任何给定状态下，所有工人对"下一个应该认领的任务"得出完全相同的结论

---

## 1. 核心架构：确定性任务调度引擎

```
设计原则：
  - 确定性认领：通过七重约束检查 + 确定性排序，消除一切随机性和猜测
  - 锁保护串行化：所有认领操作在 .catalog.lock 保护下执行，同一时刻只有 1 个工人做决策
  - 三重注册表：活跃认领注册表 + 文件锁注册表 + 模块占用表，完整追踪并行状态
  - 工人 ID 系统：每个工人有唯一 ID（W1/W2/W3/...），用于注册表记录和冲突检测
  - 崩溃恢复：超时检测机制自动释放死锁资源

架构：
  tasks_catalog.md（全量清单：4716 叶子任务，带位置编号）
  tasks_dependency.md（依赖关系 + 执行层 + 执行模式 + 文件隔离 + 认领协议）
       │
       │  七重约束检查 + 确定性排序（§十二）
       ▼
  tasks_active.md（活跃任务池 + 三重注册表）
       │
  ┌────┼────┐────┐
  ▼    ▼    ▼    ▼
  W1   W2   W3   W4  (auto.bat × N, 各有唯一工人ID)
  │    │    │    │
  认领  认领  认领  认领   (在 .catalog.lock 保护下串行执行)
  执行  执行  执行  执行   (文件隔离，互不干扰)
  完成  完成  完成  完成   (更新注册表，释放资源)
  ↓    ↓    ↓    ↓
  认领下一个 → 循环（零闲置）

两种启动方式：
  单工人：auto.bat（1 个窗口，简化约束检查）
  多工人：parallel.bat N（N 个窗口，完整七重约束检查）
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

[registry]
claim_timeout_minutes=30     ; 认领超时时间（分钟），超过视为工人崩溃
cleanup_multiplier=3         ; 超时倍数（任务平均时间 × 此值 = 动态超时）
```

**停止开关使用方式**：
- 想暂停开发：将 `single_stop` 或 `multi_stop` 改为 `true`
- 当前任务完成后工人会自动停止，不启动新窗口
- 恢复开发：改回 `false`，运行 `auto.bat` 或 `parallel.bat` 即可继续

---

## 3. 项目根目录文件

```
auto.bat          — 单工人启动器（双击启动，调用 auto.ps1）
auto.ps1          — 读 config.ini + while 循环：每轮检查停止开关 + 分配工人ID + 启动 claude
auto-prompt.md    — 工人循环提示词（确定性认领协议 + 七重约束检查 + 注册表操作）
config.ini        — 统一配置（路径、开关、窗口数、补充策略、锁参数、注册表参数）

parallel.bat      — 多工人启动器（parallel.bat N 启动 N 个窗口）
parallel.ps1      — 读 config.ini + 检查停止开关 + 分配工人ID + 启动 N 个 auto.bat 窗口

tasks_catalog.md      — 全量叶子任务清单（带位置编号 + fetch_cursor 参考）
tasks_dependency.md   — 依赖关系 + 执行层 + 执行模式 + 文件隔离 + 确定性认领协议
tasks_active.md       — 执行状态 + fetch_cursor + 活跃叶子任务 + 三重注册表
tasks_completed.md    — 完成归档（只写不读）

启动流程：
  auto.bat → auto.ps1 进入 while 循环：
    每轮：检查停止开关 → 读取 auto-prompt.md → claude 执行任务 → claude 退出 → 循环

锁文件：
  .catalog.lock   — 认领操作锁（取任务 + 注册表更新 + 状态变更，全部在此锁保护下执行）
  .parallel.lock  — Git 提交锁（commit/push 串行化）
```

---

## 4. 工人循环流程（AI 严格执行）

> **核心变化**：认领操作在 .catalog.lock 保护下执行，确保同一时刻只有 1 个工人做决策。
> **权威文档**：`tasks_dependency.md` §十二（确定性认领协议）

```
Step 0: 初始化
  1. 读取 config.ini 获取配置和停止开关
  2. single_stop=true 或 multi_stop=true → 输出提示 → 退出
  3. 确认自己的工人 ID（从 auto.ps1 传入的 WORKER_ID 环境变量获取）
     单工人模式：WORKER_ID = W1
     多工人模式：WORKER_ID = W{N}（由 parallel.ps1 分配）

Step 1: 超时清理（每次循环开始时执行）
  1. 读取 tasks_active.md 的「活跃认领注册表」
  2. 检查每条认领记录的时间戳
  3. 如果某条记录的时间 > config.ini 的 claim_timeout_minutes → 视为工人崩溃
     → 清除该工人的所有注册表记录（认领表 + 文件锁表 + 模块占用表）
     → 将该工人的任务状态从 🔄 回退为 ⬜
  4. 此步骤无需锁（只读检查），但修改注册表需要 .catalog.lock

Step 2: 查找并认领任务（在 .catalog.lock 保护下执行）

  ┌─ 获取 .catalog.lock ─────────────────────────────────────────────┐
  │                                                                    │
  │  2A: 续接检查                                                      │
  │    读取 tasks_active.md                                            │
  │    找到🔄任务且工人列 = 自己的 WORKER_ID？                          │
  │    → 是 → 续接执行（跳到 Step 3，锁内直接释放）                     │
  │    → 否 → 进入 2B                                                  │
  │                                                                    │
  │  2B: 补充候选任务（如需要）                                         │
  │    统计 tasks_active.md 中 ⬜ + 🔄 总数                            │
  │    IF 总数 < fetch_threshold:                                      │
  │      从 tasks_catalog.md 的 fetch_cursor 之后取 fetch_batch_size 条 │
  │      将候选任务暂存（尚未写入 tasks_active.md）                     │
  │                                                                    │
  │  2C: 七重约束检查（逐一检查每个候选任务）                            │
  │    对每个候选任务 T，检查：                                         │
  │    C1: T 所属模块的前置依赖全部 ✅？                                │
  │        查 tasks_dependency.md §一 + tasks_completed.md              │
  │    C2: T 所属模块的执行模式允许认领？                               │
  │        SERIAL → 模块占用表中该模块无其他工人                        │
  │        PARALLEL → 无额外限制                                       │
  │        查 tasks_dependency.md §一 + tasks_active.md 模块占用表      │
  │    C3: T 所在执行层的前置层在该模块内全部 ✅？                       │
  │        查 tasks_dependency.md §九 + tasks_active.md 同模块任务状态  │
  │    C4: T 的文件作用域与文件锁注册表无交集？                          │
  │        查 tasks_dependency.md §十一 + tasks_active.md 文件锁注册表  │
  │    C5: T 不在活跃认领注册表中？                                     │
  │        查 tasks_active.md 活跃认领注册表                            │
  │    C6: 同模块内无跨层冲突？                                         │
  │        同模块的活跃任务中有更高层级的？→ 跳过当前低层任务             │
  │    C7: 共享文件未被其他工人锁定？                                   │
  │        查 tasks_dependency.md §十一.2 + 文件锁注册表                │
  │    所有检查通过 → 加入「合格候选列表」                              │
  │    任一检查不通过 → 跳过此任务，检查下一个                          │
  │                                                                    │
  │  2D: 确定性选择                                                    │
  │    对「合格候选列表」按以下规则排序：                               │
  │    1. P 级别升序（P0 > P1 > P2）                                   │
  │    2. 模块号升序（001 > 002 > 003）                                │
  │    3. 执行层升序（L0 > L1 > L2 > L3 > L4 > L5）                   │
  │    4. 目录位置升序（tasks_catalog.md 中编号小的优先）               │
  │    选择排序后的第一个任务（唯一确定）                               │
  │                                                                    │
  │  2E: 声明认领（全部在锁内完成）                                    │
  │    1. 将任务写入 tasks_active.md（如尚未写入）                      │
  │    2. 任务状态改为 🔄，填入工人 ID                                  │
  │    3.「活跃认领注册表」追加：任务编号 + 工人ID + 时间戳             │
  │    4.「文件锁注册表」追加：任务涉及的所有文件路径 + 任务编号 + 工人ID│
  │    5.「模块占用表」追加/更新：模块编号 + 工人ID + 执行模式          │
  │    6. 更新 fetch_cursor（如有新任务写入）                           │
  │                                                                    │
  │  2F: 释放 .catalog.lock                                            │
  │                                                                    │
  │  合格候选列表为空？                                                │
  │  → fetch_cursor 未到末尾 → 等待 10 秒后重新进入 Step 2             │
  │  → fetch_cursor 已到末尾 + 无⬜/🔄 → 跳到 Step 5                  │
  │                                                                    │
  └────────────────────────────────────────────────────────────────────┘

Step 3: 执行任务
  1. 按 CLAUDE.md §3 加载上下文（任务文档 Section 1-8 + 层级规范 + 全局规范）
  2. 执行开发任务（CLAUDE.md §5 开发规则）
  3. 自检（CLAUDE.md §6 自检协议）
  4. 自检通过后：
     a. 获取 .parallel.lock → git add -A && git commit → 释放 .parallel.lock
     b. 获取 .catalog.lock：
        - 任务状态改为 ✅
        - 从「活跃认领注册表」删除该任务
        - 从「文件锁注册表」删除该任务锁定的所有文件
        - 更新「模块占用表」（SERIAL → 删除占用；PARALLEL → 删除当前工人记录）
     c. 释放 .catalog.lock
     d. 追加完成记录到 tasks_completed.md
  5. 自检失败 → 标记 ❌ 阻塞 → 记录原因 → 释放所有注册表资源 → 跳到 Step 1

Step 4: 检查停止开关 + 循环
  1. 再读 config.ini
  2. single_stop=true 或 multi_stop=true → 退出（auto.ps1 循环会检测到并关闭窗口）
  3. 否则 → 回到 Step 1（零闲置，立即开始下一个任务）

Step 5: 无任务可执行
  1. 检查 tasks_active.md 中是否还有 🔄 状态的任务
  2. 有 → 其他工人还在执行 → 退出（auto.ps1 循环会重试）
  3. 无 → 项目全部完成：
     → 输出最终统计（已完成/总数/完成率）
     → 在 develop 上打标签：git tag v1.0-complete
     → 退出
```

---

## 5. 补充逻辑（从 catalog 到 active）

> **核心变化**：补充时不只检查模块依赖，还检查执行模式和执行层，确保补充的任务真正可执行。

```
触发条件：
  tasks_active.md 中 ⬜ + 🔄 任务总数 < fetch_threshold

补充流程（在 .catalog.lock 保护下执行）：
  1. 读取 tasks_active.md 中的 fetch_cursor（如 = 212）
  2. 从 tasks_catalog.md 的位置 #0213 开始，取 fetch_batch_size 条任务
  3. 对每条任务执行预筛选（轻量检查，不做完整七重检查）：
     a. 所属模块的前置依赖全部 ✅？
        → 查 tasks_dependency.md §一 + tasks_completed.md
        → 否 → 跳过，取下一条
     b. 所属模块的执行模式 = SERIAL 且已被其他工人占用？
        → 查 tasks_active.md 模块占用表
        → 是 → 跳过（该模块的任务全部跳过，直到占用释放）
     c. 通过预筛选 → 写入 tasks_active.md（状态 ⬜）
  4. 更新 fetch_cursor 为本批次扫描到的最后一个位置编号
     （注意：是扫描到的位置，不是写入的位置。跳过的任务也算扫描过）
  5. 释放 .catalog.lock

清理流程（模块完成时）：
  1. 获取 .catalog.lock
  2. 检查 tasks_active.md 中该模块所有叶子任务是否均为 ✅
  3. 是 → 删除该模块的所有已完成叶子
  4. 清理该模块的注册表记录（认领表 + 文件锁表 + 占用表）
  5. 释放 .catalog.lock
  6. 追加完成记录到 tasks_completed.md（模块级汇总）
```

---

## 6. 无缝接续机制

```
每次新会话启动时，AI 通过以下机制实现零信息丢失：
  1. tasks_active.md 精确记录：
     - fetch_cursor（任务游标位置）
     - 叶子任务状态（⬜/🔄/✅）
     - 活跃认领注册表（谁在做什么）
     - 文件锁注册表（哪些文件被锁）
     - 模块占用表（哪些模块被占用）
  2. tasks_dependency.md 记录：
     - 模块间依赖关系
     - 执行层框架（L0-L6）
     - 模块执行模式（SERIAL/PARALLEL）
     - 文件隔离规则
     - 确定性认领协议
  3. tasks_catalog.md 提供全量任务清单和位置编号
  4. 任务文档自包含（Section 1-8），无需历史上下文
  5. 层级规范 + 全局规范提供所有技术约束
  6. tasks_completed.md 提供历史参考（模块级完成记录）
  7. config.ini 提供停止开关、运行参数、注册表参数
  8. 新会话是干净状态，不受前一会话的上下文干扰
  9. 工人 ID 由 auto.ps1 传入，确保注册表记录一致
```

---

## 7. 多工人协作机制

```
共享文档（所有工人共同读写，通过锁串行化）：
  tasks_catalog.md      — 全量任务清单（只读，工人不修改）
  tasks_dependency.md   — 依赖关系 + 执行层 + 执行模式 + 认领协议（只读，工人不修改）
  tasks_active.md       — 执行状态 + fetch_cursor + 活跃叶子 + 三重注册表（读写，.catalog.lock 串行化）
  tasks_completed.md    — 完成归档（只追加）

三重注册表（tasks_active.md 中）：
  活跃认领注册表 — 记录每个🔄任务由哪个工人认领、认领时间
  文件锁注册表   — 记录每个🔄任务锁定了哪些文件路径
  模块占用表     — 记录每个活跃模块被哪些工人占用、执行模式

隔离方式：
  - 认领操作 → .catalog.lock 串行化（同一时刻只有 1 个工人做决策）
  - git commit/push → .parallel.lock 串行化
  - 代码编写 → 文件锁注册表保证文件隔离
  - SERIAL 模块 → 模块占用表保证单工人独占
  - 崩溃恢复 → 超时检测自动释放死锁资源

工人 ID 系统：
  parallel.ps1 启动时为每个窗口分配唯一 ID（W1, W2, W3, ...）
  通过环境变量 WORKER_ID 传递给 auto.ps1 → auto-prompt.md
  所有注册表操作都使用 WORKER_ID 标识工人
  单工人模式固定为 W1
```

---

## 8. 优先级阶段切换

```
P0 全部完成进入 P1（或 P1 进入 P2）时：
  1. tasks_dependency.md §一 中已预计算好各级依赖
  2. P0 模块全部完成后，P1 模块的前置依赖自动满足
  3. 工人取 P1 任务时，七重约束检查中 C1 自动通过
  4. 无需人工干预，任务池自动推进
  5. 在最后一个模块完成时打标签：git tag v{P级别}-complete

并行度自动调节：
  P0-S1 阶段（P0-001）：SERIAL 模式，只有 1 个工人工作，其他工人等待
  P0-S2 阶段（P0-002/003/004）：3 个 SERIAL 模块，最多 3 个工人并行
  P0-S3~S5（P0-005/006/007）：SERIAL 模式，单工人执行
  P0-S6（P0-008~011）：4 个 PARALLEL 模块，所有工人可并行
  ...以此类推
  工人无需知道当前处于哪个阶段，七重约束检查自动限制可认领的任务范围
```

---

## 9. 项目全部完成

```
IF fetch_cursor = 总任务数 AND tasks_active.md 中无⬜且无🔄:
    1. 所有工人检测到无任务可执行 → 直接退出
    2. 最后一个退出的工人：
       a. 确认 tasks_completed.md 归档完整
       b. 在 develop 上打标签：git tag v1.0-complete
       c. 输出项目完成总结
```

---

## 10. 变更日志

| 版本 | 日期 | 变更内容 |
|:---:|:---:|---------|
| V1.0-V4.0 | - | 历史版本 |
| V5.0 | 2026-05-28 | 任务清单+工人池架构 |
| V6.0 | 2026-05-28 | 全面重写为确定性并行引擎：新增七重约束检查、三重注册表、工人ID系统、超时清理机制、确定性任务选择算法；消除所有随机性和猜测 |
