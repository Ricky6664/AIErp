# 并行开发规则（任务清单 + 工人池）

> **所属主文档**：`CLAUDE.md` §10
> **版本**：V4.0
> **最后修订**：2026-05-28
> **配套文档**：`tasks_dependency.md`（预计算依赖 + 并行分组 + 文件冲突矩阵）、`tasks_catalog.md`（全量任务清单）

---

> 并行开发分两个层级：**任务内并行**（单任务使用子Agent拆分工作）和**模块间并行**（多个工人窗口从任务池同时认领不同模块）。两者独立，可叠加使用。

---

## 1. 任务内并行（Intra-task Parallelism）

**适用场景**：单个末端任务内部包含多个独立子步骤（如同时创建 Entity + DTO + VO，或同时编写 Service 的多个方法）。

```
执行方式：
  主会话使用 Claude 的 Agent 工具 spawn 子Agent，每个子Agent 负责一个独立子步骤

约束：
  1. 子Agent 之间不得修改同一文件
  2. 主会话负责汇总子Agent 结果、统一编译验证、统一提交
  3. 子Agent 不直接操作 tasks_active.md 或 git
  4. 子Agent 完成后由主会话执行 CLAUDE.md §6 自检协议

典型用法：
  - DDL + Entity 同时编写（文件不交叉）
  - 多个独立的 Service 方法同时实现
  - 前端页面 + 国际化词条同时编写
```

---

## 2. 模块间并行（Inter-module Parallelism）

**适用场景**：`tasks_dependency.md` 中「可并行模块」列明确标注的模块对，且前置依赖全部 ✅。

**核心机制**：`parallel.bat N` 启动 N 个 `auto.bat` 工人窗口，每个工人独立从 `tasks_catalog.md` 按游标取任务到 `tasks_active.md` 并认领执行。

```
架构：
  tasks_catalog.md（全量清单：4716 叶子任务，带位置编号）
  tasks_dependency.md（依赖关系 + 并行分组 + 冲突矩阵）
       │
       │ 按 fetch_cursor 取任务 + 查依赖确认可认领
       ▼
  tasks_active.md（活跃任务池）
       │
  ┌────┼────┐────┐
  ▼    ▼    ▼    ▼
 工人  工人  工人  工人  (auto.bat × N)
  W1   W2   W3   W4
  │    │    │    │
  认领P0-008  认领P0-009  认领P0-010  认领P0-011
  执行...     执行...     执行...     执行...
  完成→标记✅  完成→标记✅  完成→认领下一个  ...

每个工人内部：
  auto.bat → auto.ps1 进入 while 循环 → claude 读取 auto-prompt.md →
  续接🔄任务 或 认领⬜任务 →
  执行叶子任务 → commit → claude 退出 → auto.ps1 循环自动启动下一个 → 循环
```

---

## 3. 并行开发严格规则（必须 100% 遵守）

> **以下规则为强制执行，违反任何一条即判定为执行失败。**

```
规则 1 - 窗口数量上限：
  最大并行窗口数由 `config.ini` 的 `max_workers` 配置决定，绝对不能超过 100 个。
  根据电脑性能配置窗口数，超过 100 会被限制为 100。
  超过会导致内存爆炸、文件冲突、AI 失控。

规则 2 - 文件/目录隔离（绝对红线）：
  每个窗口的任务只能修改自己分配到的目录和文件。
  绝对不能有两个窗口修改同一个文件、同一个文件夹。
  错误：窗口1改 /src/views/order，窗口2也改 /src/views/order
  正确：窗口1改 /src/views/purchase，窗口2改 /src/views/sale

规则 3 - 禁止同时修改共享文件：
  禁止两个窗口同时修改：
  - 同一个 .vue / .java / .ts / .js 文件
  - 同一个路由文件（router/*.ts）
  - 同一个接口定义文件
  - 同一个配置文件（application.yml、pom.xml、package.json）
  - 同一个 XML 映射文件
  - tasks_active.md、tasks_completed.md（通过锁机制串行化）

规则 4 - 无依赖才可并行：
  任务B依赖任务A的代码 → 必须等A完全结束才能开始B，不能并行。
  判断依据：tasks_dependency.md 中该模块的「严格前置依赖」列。
  补充校验：任务文档 Section 3（前置依赖）。

规则 5 - 执行顺序必须遵循层级：
  1) 基础架构/工具类 → 2) Entity/DTO → 3) Service/Mapper → 4) Controller/API → 5) 前端页面 → 6) 权限/配置
  底层没写完，上层不能开始。

规则 6 - Git 提交串行化：
  - 一个任务结束 → 自检通过 → 获取 .parallel.lock → commit → push → 释放锁
  - 禁止并行任务同时 commit、push
  - 每个任务一个独立 commit，信息清晰：feat(模块号): 任务名称
  - 锁超时 120 秒自动释放（防止窗口崩溃导致死锁）

规则 7 - 目录隔离规范：
  前端任务：只动 /src/views/xxx、/src/api/xxx、/src/i18n/xxx（xxx为对应模块）
  后端任务：只动对应模块的 /controller、/service、/mapper、/entity
  配置任务：只动 /config、/router（配置任务不可并行，必须串行）
  数据库任务：只动 /sql、/docs（DDL 任务不可并行，必须串行）

规则 8 - 启动前强制检查（每一项都必须满足）：
  □ tasks_dependency.md 中该模块的可并行模块列是否包含当前其他窗口正在执行的模块？
    → 不包含则不能并行
  □ 是否会和现有任务修改同一文件？→ 是则不能并行
  □ 是否会和现有任务修改同一目录？→ 是则不能并行
  □ tasks_dependency.md 文件冲突矩阵中是否标记了冲突？→ 是则不能并行
  □ 当前窗口是否已达到 config.ini 的 max_workers 上限？→ 是则必须排队
  □ 是否涉及共享配置文件修改？→ 是则不能并行
  任何一项不满足 → 不能并行，必须排队串行执行
```

---

## 4. 并行任务识别条件

| 并行类型 | 条件 | 安全前提 | 推荐窗口数 |
|---------|------|---------|:---------:|
| 跨模块并行 | tasks_dependency.md 可并行模块列互相包含 | 文件冲突矩阵无冲突 | 2~4 |
| 前后端并行 | 后端 API 已完成，前端开发页面 | 文件路径完全不交叉 | 2 |
| 同模块不同子域并行 | 同一模块内不同业务子域 | 不修改同一文件/目录 | 2~3 |
| 同层级不同实体并行 | 多个独立实体的 CRUD | 实体间无关联关系 | 2~4 |

**不可并行的场景（必须串行）**：

| 场景 | 原因 | 参考 |
|------|------|------|
| DDL 建表任务 | 共享 SQL 文件和数据库 schema | tasks_dependency.md §三 |
| 路由/配置文件修改 | 共享全局配置文件 | tasks_dependency.md §三 |
| 有前置依赖关系的任务 | 违反规则 4 | tasks_dependency.md §一 |
| 公共组件/工具类修改 | 影响所有模块 | tasks_dependency.md §三 |
| pom.xml / package.json 修改 | 共享依赖配置 | tasks_dependency.md §三 |
| 文件冲突矩阵标记的模块对 | 可能修改同一文件 | tasks_dependency.md §四 |

---

## 5. 并行执行流程

```
Step 1：启动工人
  用户运行 parallel.bat N（N = 2~4）
  → parallel.ps1 启动 N 个 auto.bat 窗口（/c 模式，完成后自动关闭）
  → 每个窗口启动 Claude 读取 auto-prompt.md

Step 2：工人自动取任务
  每个工人独立读取 tasks_active.md
  → 有⬜/🔄任务 → 直接认领执行
  → 无可执行任务 → 获取 .catalog.lock → 从 tasks_catalog.md 按 fetch_cursor 取任务
    → 查 tasks_dependency.md 确认依赖已满足 → 写入 tasks_active.md → 释放锁

Step 3：工人循环执行
  每个工人：执行叶子任务 → 自检 → commit → claude 退出 → auto.ps1 循环启动下一个 → 循环
  → 续接当前任务（🔄）或认领新任务（⬜）→ 循环

Step 4：工人自动补充
  快的工人完成任务后立即认领下一个
  始终保持最多 N 个窗口在跑，零闲置
  旧窗口自动关闭（/c 模式），新窗口接力执行

Step 5：工人自然退出
  fetch_cursor 到末尾 + tasks_active 无⬜/🔄 → 工人退出（不启动替补）
  tasks_active 还有🔄（其他工人还在跑）→ 启动替补工人后退出
```

---

## 6. 锁机制

```
catalog 锁（取任务/清理已完成时使用）：
  锁文件：项目根目录 .catalog.lock
  锁内容：窗口ID + fetch_cursor 位置 + 时间戳
  获取流程：
    1. 检查 .catalog.lock 是否存在
    2. 不存在 → 创建锁文件
    3. 已存在 → 检查时间戳
       - 超过 120 秒 → 死锁，删除旧锁重试
       - 未超时 → 等待 3 秒重试，最多 40 次
    4. 获取锁 → 修改 tasks_active.md（更新 fetch_cursor + 任务列表）→ 释放锁

Git 提交锁（commit/push 时使用）：
  锁文件：项目根目录 .parallel.lock
  用途：防止多工人同时 commit/push 造成冲突
  流程同上

注意：
  - tasks_active.md 的 fetch_cursor 更新 → 通过 .catalog.lock 串行化
  - git commit/push → 通过 .parallel.lock 串行化
  - tasks_catalog.md 和 tasks_dependency.md 为只读，无需锁
  - 代码编写阶段的隔离靠目录/文件隔离规则（§3 规则 2/3）
```

---

## 7. 多工人协作机制

```
共享文档：
  tasks_catalog.md    — 全量任务清单（只读，工人不修改）
  tasks_dependency.md — 依赖关系 + 并行分组（只读，工人不修改）
  tasks_active.md     — 执行状态 + fetch_cursor + 活跃叶子任务（读写，通过锁串行化）
  tasks_completed.md  — 完成归档（只追加）

隔离方式：
  - tasks_active.md 的 fetch_cursor 更新 → 通过 .catalog.lock 串行化
  - git commit/push → 通过 .parallel.lock 串行化
  - tasks_active.md 中🔄任务标记了工人窗口ID，防止重复认领
  - 无需单独的工人进度文件，tasks_active.md 已包含全部执行状态
```

---

## 8. 启动脚本说明

```
项目根目录文件：
  config.ini         — 统一配置（路径、停止开关、窗口数、补充策略、锁参数）
  auto.bat           — 工人启动器（单工人入口，也是多工人的每个窗口入口）
  auto.ps1           — 解析 config.ini → 检查停止开关 → 读 auto-prompt.md → 启动 claude
  auto-prompt.md     — 工人循环提示词（两级查找 + 停止开关 + 游标补充逻辑）

  parallel.bat       — 多工人启动器（接收数字参数 N，不设则读 config.ini）
  parallel.ps1       — 解析 config.ini → 检查停止开关 → 启动 N 个 auto.bat 窗口（/c 模式）

  tasks_catalog.md    — 全量叶子任务清单（带位置编号 + fetch_cursor 参考）
  tasks_dependency.md — 预计算依赖关系 + 并行分组 + 文件冲突矩阵
  tasks_active.md     — 活跃任务（叶子级 + fetch_cursor，工人直接认领）
  tasks_completed.md  — 完成归档（只写不读）
  .catalog.lock       — catalog 读取 / active-tasks 写入锁
  .parallel.lock      — Git 提交锁

启动方式：
  单工人：auto.bat（双击或命令行）
  多工人：parallel.bat 3（启动 3 个工人窗口）
  多工人：parallel.bat（不带参数，使用 config.ini 的 max_workers）
  停止：config.ini 中 single_stop=true 或 multi_stop=true

窗口生命周期：
  parallel.ps1 使用 cmd /c 启动窗口（命令完成后自动关闭）
  每个任务完成后 claude 退出 → auto.ps1 循环自动启动下一个 claude 会话
  窗口始终保持打开，claude 会话在窗口内轮转，停止时窗口自动关闭
  始终保持 N 个活跃窗口，停止时全部自动关闭
```
