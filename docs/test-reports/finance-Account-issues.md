# 会计科目P07单一表单页 — 问题清单与修复方案

> **任务编号**：P0-011-002-007-001-002
> **创建日期**：2026-06-08
> **原始工人**：W6（初版问题识别）
> **修复工人**：W5（修复验证）
> **更新日期**：2026-06-08

---

## 问题列表

### 问题 #1：AccountController 缺失（阻塞级）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🔴 阻塞 |
| 状态 | ⬜ 未修复（后端任务范围） |
| 发现方式 | 代码审查 |
| 问题描述 | 前端调用 `/finance/account` 系列API，但后端 `AccountController` 不存在 |
| 影响范围 | 7个API端点全部404 |

**缺失的端点：**
| 方法 | 路径 | Controller方法 |
|------|------|:---:|
| GET | /finance/account | ❌ |
| GET | /finance/account/tree | ❌ |
| GET | /finance/account/{id} | ❌ |
| POST | /finance/account | ❌ |
| PUT | /finance/account/{id} | ❌ |
| PUT | /finance/account/{id}/status | ❌ |
| DELETE | /finance/account/{id} | ❌ |

**修复方案：** 创建 `AccountController.java`，注入 `IAccountService`，映射上述7个端点。

---

### 问题 #2：路由未注册（已修复 ✅）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🔴 阻塞 |
| 状态 | ✅ 已修复 (W5) |
| 发现方式 | 代码审查 |
| 问题描述 | `/finance/account` 路由未在 `static.ts` 中注册 |
| 修复内容 | 添加 `FINANCE_ACCOUNT` 路由到 `staticRoutes` 数组，路径 `/finance/account`，meta title '会计科目' |

---

### 问题 #3：IAccountService 缺少 getTree() 方法（中等）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🟡 中等 |
| 状态 | ⬜ 未修复（后端任务范围） |
| 发现方式 | 前后端契约对比 |
| 问题描述 | 前端 `getAccountTreeApi()` → `GET /finance/account/tree`，但 `IAccountService` 无 `getTree()` |

**修复方案：** 在 `IAccountService` 添加 `List<AccountTreeVO> getTree()` 并在 `AccountServiceImpl` 实现。

---

### 问题 #4：IAccountService 缺少 updateStatus() 方法（中等）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🟡 中等 |
| 状态 | ⬜ 未修复（后端任务范围） |
| 发现方式 | 前后端契约对比 |
| 问题描述 | 前端 `updateAccountStatusApi()` → `PUT /finance/account/{id}/status`，但 `IAccountService` 无 `updateStatus()` |

**修复方案：** 在 `IAccountService` 添加 `void updateStatus(Long id, Integer status)` 并实现。

---

### 问题 #5：前端 AccountVO 缺少 status 字段（已修复 ✅）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🟢 低 |
| 状态 | ✅ 已修复 (W5) |
| 发现方式 | TypeScript编译 |
| 修复内容 | `finance-account.ts` 的 `AccountVO` 接口添加 `status: number` 字段 |

---

### 问题 #6：表单 isForeignCurrency / isAuxiliary 类型不匹配（已修复 ✅）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🟢 低 |
| 状态 | ✅ 已修复 (W5) |
| 发现方式 | 代码审查 |
| 问题描述 | `AccountSaveDTO` 定义 `isForeignCurrency: string` 但表单 switch 绑定 `boolean`，代码中使用 `as unknown as string` 类型强转 |
| 修复内容 | `AccountSaveDTO` 中 `isForeignCurrency` 和 `isAuxiliary` 改为 `boolean | string`；移除 `initFormData()` 和 `loadEditData()` 中的 `as unknown as string` 类型断言 |

---

### 问题 #7：filter-node-method TS类型错误（已修复 ✅）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🔴 编译错误 |
| 状态 | ✅ 已修复 (W5) |
| 发现方式 | `pnpm build` 编译 |
| 问题描述 | `el-tree` 的 `filter-node-method` 接收 `TreeNodeData` 类型参数，但函数签名使用 `AccountTreeVO`，类型不兼容 |
| 修复内容 | 函数参数类型改为 `Record<string, unknown>`，内部通过属性名访问 |

---

### 问题 #8：表单 category 下拉绑定错误（已修复 ✅）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🟡 中等 |
| 状态 | ✅ 已修复 (W5) |
| 发现方式 | 代码审查 |
| 问题描述 | 科目类别下拉绑定 `formData.category`（string），但选项值为数字1-6；编辑时API返回category为字符串（如"资产类"）导致下拉无法正确选中 |
| 修复内容 | 下拉改为绑定 `formData.accountType`（number）；添加 `onAccountTypeChange()` 自动同步 `formData.category`（string）；表单校验 prop 从 `category` 改为 `accountType` |

---

## 总结

| 严重度 | 总数 | 已修复 | 未修复 | 说明 |
|:---:|:---:|:---:|:---:|------|
| 🔴 阻塞 | 3 | 2 | 1 | Controller缺失需后端任务修复 |
| 🟡 中等 | 3 | 1 | 2 | getTree/updateStatus需后端任务修复 |
| 🟢 低 | 2 | 2 | 0 | 全部修复 |
| **合计** | **8** | **5** | **3** | 3项遗留为后端任务范围 |
