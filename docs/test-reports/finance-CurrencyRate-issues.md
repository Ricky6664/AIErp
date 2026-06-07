# 币种汇率P04列表页 — 问题清单与修复方案

> **任务编号**: P0-011-002-002-001-002
> **创建日期**: 2026-06-07
> **验证人**: W6

---

## 问题列表

### ISS-1: 路由未注册 [已修复]

| 属性 | 值 |
|------|-----|
| 严重度 | 🔴 高 |
| 状态 | ✅ 已修复 |
| 文件 | `erp-ai-web/src/router/modules/static.ts` |
| 描述 | 路由 `/finance/currencyrate` 未注册，导致页面无法通过URL访问 |
| 修复 | 添加 `FINANCE_CURRENCYRATE` 路由常量并注册到 `staticRoutes` 数组 |

### ISS-2: 缺少启用/停用操作 [未修复]

| 属性 | 值 |
|------|-----|
| 严重度 | 🟡 中 |
| 状态 | ⏳ 待后续 |
| 文件 | `erp-ai-web/src/views/finance/currencyrate/index.vue` |
| 描述 | 任务规格要求操作列包含"启用/停用"切换，当前代码未实现 |
| 修复方案 | 1. 在 `finance-currencyrate.ts` 添加 `updateCurrencyRateStatusApi`<br>2. 在操作列添加 `el-switch` 或按钮<br>3. 后端需配合提供 `PUT /finance/currency-rate/{id}/status` 端点 |
| 建议承接 | 可在 P0-011-002-003 表单页任务中一并补充 |

### ISS-3: 编辑按钮仅为占位 [设计如此]

| 属性 | 值 |
|------|-----|
| 严重度 | 🟡 中 |
| 状态 | ⏳ 待后续(P0-011-002-003) |
| 文件 | `erp-ai-web/src/views/finance/currencyrate/index.vue` |
| 描述 | `handleEdit` 仅执行 `ElMessage.info` 提示，未弹出编辑表单 |
| 说明 | 编辑表单页由 P0-011-002-003 任务承接，此为预期行为 |

### ISS-4: 后端CurrencyRateController缺失 [未修复]

| 属性 | 值 |
|------|-----|
| 严重度 | 🔴 高 |
| 状态 | ⏳ 待后续 |
| 描述 | 后端存在完整的 Service/Mapper/Entity/DTO/VO 层（11个文件），但缺少 Controller |
| 影响 | 前端所有API调用返回404，页面核心功能不可用 |
| 修复方案 | 创建 `CurrencyRateController.java`，路径 `/api/finance/currency-rate` |
| 规范参考 | RESTful 路径规范、API接口规范 |

---

## 统计

| 严重度 | 数量 | 已修复 | 未修复 |
|:---:|:---:|:---:|:---:|
| 🔴 高 | 2 | 1 | 1 |
| 🟡 中 | 2 | 0 | 2 |
| **合计** | **4** | **1** | **3** |
