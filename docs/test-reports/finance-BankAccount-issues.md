# 问题清单 — P0-011-002-004-001-002 银行账户列表页

**执行时间**: 2026-06-08T00:20
**执行工人**: W6

## 问题列表

### ISSUE-01: 统计卡片数据范围不准确

| 属性 | 值 |
|------|-----|
| 严重程度 | 低 |
| 影响范围 | 统计卡片（总记录数/已启用/已停用） |
| 根因 | `updateStats()` 使用当前页数据 `list` 计算 enabled/disabled 数量，而非全量数据 |
| 位置 | `index.vue:474-478` |

**当前代码**:
```ts
function updateStats(list: BankAccountVO[], total: number): void {
  stats.total = total
  stats.enabled = list.filter((item) => item.status === 1).length
  stats.disabled = list.filter((item) => item.status === 0).length
}
```

**问题**: 当数据超过一页时，`stats.enabled` 和 `stats.disabled` 只统计当前页数据。例如总共50条启用记录，当前页只有20条，则显示"已启用: 20"而非"已启用: 50"。

**修复建议**: 后端pageList接口返回额外统计字段（totalEnabled/totalDisabled），或新增 `/finance/bank-account/stats` 统计接口。前端调用统计接口获取全量汇总数据。

**状态**: ⚠️ 待修复

---

### ISSUE-02: 币种选项硬编码

| 属性 | 值 |
|------|-----|
| 严重程度 | 中 |
| 影响范围 | 新增/编辑表单中的币种下拉选择器 |
| 根因 | 币种列表硬编码在前端组件中，而非从后端币种汇率API获取 |
| 位置 | `index.vue:291-298` |

**当前代码**:
```ts
const currencyOptions = [
  { label: 'CNY - 人民币', value: 1 },
  { label: 'USD - 美元', value: 2 },
  ...
]
```

**修复建议**: 从 `finance-currencyrate` API 获取币种列表，或在 `onMounted` 中调用 `getCurrencyOptionsApi()` 动态加载。

**状态**: ⚠️ 待修复

---

### ISSUE-03: 银行账号格式校验缺失

| 属性 | 值 |
|------|-----|
| 严重程度 | 低 |
| 影响范围 | 新增/编辑表单银行账号字段 |
| 根因 | 前端仅校验唯一性（异步查重），未校验账号格式（如纯数字、长度范围） |
| 位置 | `index.vue:317-321` |

**修复建议**: 在 `formRules.bankAccountNo` 中添加 `pattern` 规则，如 `/^\d{10,30}$/`（根据实际业务规则调整）。

**状态**: ⚠️ 待修复

---

### ISSUE-04: clearable 清空时未触发搜索

| 属性 | 值 |
|------|-----|
| 严重程度 | 低 |
| 影响范围 | 搜索表单中的 el-input 清空操作 |
| 根因 | `@input` 事件在某些 Element Plus 版本中不会被 clearable 清空按钮触发 |
| 位置 | `index.vue:29-35, 38-44` |

**修复建议**: 在 `el-input` 上同时添加 `@clear="handleSearch"` 事件监听，确保清空后触发搜索。

**状态**: ⚠️ 待修复

---

## 汇总

| 严重程度 | 数量 |
|---------|:---:|
| 高 | 0 |
| 中 | 1 |
| 低 | 3 |
| 合计 | 4 |

## 补充说明

1. 后端未找到 BankAccountController 类，API 端点 `/finance/bank-account/*` 可能尚未注册。前端API模块和后端ServiceImpl已就绪，需确认Controller是否在其他任务中实现。
2. 前端编译（pnpm build）和后端编译（mvn compile）均通过，无编译错误。
3. 所有4个问题均不影响核心功能的编译和基本运行，建议在后续迭代中修复。
