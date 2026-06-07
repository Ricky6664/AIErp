# 问题清单 — P0-011-002-005-001-002 银行账户P07单一表单页

**执行时间**: 2026-06-08T00:30
**执行工人**: W6
**依赖任务**: P0-011-002-005-001-001 编写核心代码（✅ W6）

## 问题列表

### ISSUE-01: 币种选项硬编码在前端组件中

| 属性 | 值 |
|------|-----|
| 严重程度 | 中 |
| 影响范围 | 新增/编辑表单中的币种下拉选择器 |
| 根因 | currencyOptions 为静态常量，未从后端 `/finance/currency-rate` API 动态获取 |
| 位置 | `index.vue:291-298` |

**当前代码**:
```ts
const currencyOptions = [
  { label: 'CNY - 人民币', value: 1 },
  { label: 'USD - 美元', value: 2 },
  { label: 'EUR - 欧元', value: 3 },
  { label: 'JPY - 日元', value: 4 },
  { label: 'GBP - 英镑', value: 5 },
  { label: 'HKD - 港币', value: 6 }
]
```

**问题**: 币种数据与后端 `fin_currency_rate` 表不同步。若后端新增/删除币种，前端下拉列表不会同步更新。

**修复建议**: 在 `onMounted` 中调用币种列表API获取动态数据，或从 `finance-currencyrate` API 模块导入 `getCurrencyListApi`。

**状态**: ⚠️ 待修复

---

### ISSUE-02: 后端 BankAccountController 缺失

| 属性 | 值 |
|------|-----|
| 严重程度 | 高 |
| 影响范围 | 全部API端点 `/finance/bank-account/*` |
| 根因 | 项目 `src/main/java` 中未找到 BankAccountController 类，Controller层尚未实现 |
| 位置 | 不存在 |

**问题**: 前端 API 层（`finance-bankaccount.ts`）和后端 Service 层（`BankAccountServiceImpl`）已就绪，但缺少 Controller 桥接，导致 API 请求无法路由到 Service。当前可用后端类：`BankAccountEntity`, `BankAccountCreateDTO`, `BankAccountUpdateDTO`, `BankAccountQueryDTO`, `BankAccountVO`, `BankAccountMapper`, `IBankAccountService`, `BankAccountServiceImpl`, `BankAccountServiceTest`。

**修复建议**: 创建 `com.erp.module.finance.controller.BankAccountController`（或 `com.erp.finance.controller.BankAccountController`），实现标准CRUD端点：
- `GET /api/finance/bank-account` — 分页查询
- `GET /api/finance/bank-account/{id}` — 获取详情
- `POST /api/finance/bank-account` — 新增
- `PUT /api/finance/bank-account/{id}` — 编辑
- `PUT /api/finance/bank-account/{id}/status` — 状态切换
- `DELETE /api/finance/bank-account/{id}` — 删除
- `GET /api/finance/bank-account/check-account-no` — 账号查重

**状态**: ⚠️ 待修复（可能在其他任务中实现，如 P0-011-001-004-001-xxx）

---

### ISSUE-03: el-input clearable 清空可能不触发搜索

| 属性 | 值 |
|------|-----|
| 严重程度 | 低 |
| 影响范围 | 搜索表单中的账户名称、开户银行输入框 |
| 根因 | 仅监听 `@input` 事件，部分 Element Plus 版本中 clearable 清空不触发 input 事件 |
| 位置 | `index.vue:29-35, 38-44` |

**修复建议**: 在 `el-input` 上添加 `@clear="handleSearch"` 事件监听。

**状态**: ⚠️ 待修复

---

### ISSUE-04: 异步校验静默吞错

| 属性 | 值 |
|------|-----|
| 严重程度 | 低 |
| 影响范围 | 银行账号唯一性异步校验 `validateBankAccountNo` |
| 根因 | catch 块中直接 `callback()`（跳过校验）而不通知用户校验服务不可用 |
| 位置 | `index.vue:346-348` |

```ts
} catch {
  callback()  // 网络异常时静默跳过，用户无感知
}
```

**修复建议**: 考虑在 catch 中展示 warning 提示或在 console 记录，告知用户唯一性校验暂时不可用。当前静默处理在可用性优先的场景下可接受。

**状态**: ⚠️ 低优先级，当前行为可接受

---

### ISSUE-05: 编辑模式未对编辑ID做空值防护

| 属性 | 值 |
|------|-----|
| 严重程度 | 低 |
| 影响范围 | `handleSubmit()` 编辑提交分支 |
| 根因 | `isEdit && editId != null` 检查存在但 editId 为 0 时可绕过（id 通常从1开始，实际影响有限） |
| 位置 | `index.vue:381` |

**修复建议**: 使用 `editId.value != null && editId.value > 0` 或严格判断。

**状态**: ⚠️ 低优先级

---

## 汇总

| 严重程度 | 数量 |
|---------|:---:|
| 高 | 1 |
| 中 | 1 |
| 低 | 3 |
| 合计 | 5 |

## 补充说明

1. 前端代码结构清晰，TypeScript 类型完备，编译零错误
2. ISSUE-02（Controller缺失）为当前最高优先级问题，直接影响前后端联调；需确认是否在其他任务中覆盖
3. 其余4个问题均为轻度问题，不影响核心功能开发和编译
4. 表单校验（前端+后端DTO）、异常处理、loading防重复提交等关键机制均已正确实现
