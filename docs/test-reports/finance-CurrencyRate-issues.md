# 币种汇率 P04单一列表页 问题清单

> **验证任务**：P0-011-002-002-001-002
> **更新日期**：2026-06-07
> **验证人**：W6
> **覆盖范围**：P04 列表页 (currencyrate/index.vue + finance-currencyrate.ts)

---

## 问题列表

### ISS-1: 后端 CurrencyRateController 缺失

| 属性 | 值 |
|------|-----|
| 严重度 | 🔴 高 |
| 状态 | ⏳ 待后续 |
| 影响 | 前端 API 调用返回 404，页面核心功能无法运行时验证 |
| 说明 | 后端存在 Service/Mapper/Entity/DTO/VO 层，但缺少 Controller 层 |
| 修复 | 创建 CurrencyRateController，路径 `/api/finance/currency-rate`，提供 CRUD + 编码校验端点 |

### ISS-2: 缺少启用/停用操作

| 属性 | 值 |
|------|-----|
| 严重度 | 🟡 中 |
| 状态 | ⏳ 待后续 |
| 说明 | 任务规格要求操作列包含启用/停用切换，当前未实现 |
| 修复 | 添加 updateCurrencyRateStatusApi + el-switch 组件 + 后端 status 端点 |

### ISS-3: debounce timer 未在组件卸载时清理

| 属性 | 值 |
|------|-----|
| 严重度 | 🟢 低 |
| 状态 | ⏳ 待后续 |
| 文件 | `erp-ai-web/src/views/finance/currencyrate/index.vue:407` |
| 说明 | `debounceTimer` 在组件卸载时未 `clearTimeout`，极端情况下可能在组件销毁后触发 `handleSearch` |
| 修复 | 在 `onUnmounted` 中添加 `if (debounceTimer) clearTimeout(debounceTimer)` |

---

## 统计

| 严重度 | 数量 | 已修复 | 未修复 |
|:---:|:---:|:---:|:---:|
| 🔴 高 | 1 | 0 | 1 |
| 🟡 中 | 1 | 0 | 1 |
| 🟢 低 | 1 | 0 | 1 |
| **合计** | **3** | **0** | **3** |
