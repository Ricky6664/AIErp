# 币种汇率 — 问题清单与修复方案

> **最新任务编号**: P0-011-002-003-001-002
> **更新日期**: 2026-06-07
> **验证人**: W3
> **覆盖范围**: P04 列表页 + P07 单一表单页

---

## 问题列表

### ISS-1: 路由未注册 [已修复]

| 属性 | 值 |
|------|-----|
| 严重度 | 🔴 高 |
| 状态 | ✅ 已修复 (W6, P0-011-002-002-001-002) |
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

### ISS-3: 编辑/新增按钮仅为占位 [已修复]

| 属性 | 值 |
|------|-----|
| 严重度 | 🟡 中 |
| 状态 | ✅ 已修复 (W4, P0-011-002-003-001-001) |
| 文件 | `erp-ai-web/src/views/finance/currencyrate/index.vue` |
| 描述 | 原 `handleEdit`/`handleAdd` 仅执行 `ElMessage.info` 占位提示 |
| 修复 | P07 单一表单页已实现完整的 el-dialog 弹窗表单：<br>- 6个表单字段(编码/名称/基准币种/汇率/日期/类型)<br>- 新增模式 POST + 编辑模式 GET回显 + PUT提交<br>- 表单校验(必填/长度/异步唯一性检查)<br>- 提交 loading 防重复、成功后关闭弹窗刷新列表 |

### ISS-4: 后端 CurrencyRateController 缺失 [未修复]

| 属性 | 值 |
|------|-----|
| 严重度 | 🔴 高 |
| 状态 | ⏳ 待后续 |
| 描述 | 后端存在完整的 Service/Mapper/Entity/DTO/VO 层（11个文件），但缺少 Controller |
| 影响 | 前端所有 API 调用返回 404，页面核心功能无法进行运行时验证 |
| 修复方案 | 创建 `CurrencyRateController.java`，路径 `/api/finance/currency-rate`，提供以下端点：<br>- GET /api/finance/currency-rate (分页查询)<br>- GET /api/finance/currency-rate/{id} (详情)<br>- POST /api/finance/currency-rate (新增)<br>- PUT /api/finance/currency-rate/{id} (更新)<br>- DELETE /api/finance/currency-rate/{id} (删除)<br>- GET /api/finance/currency-rate/check-code (编码唯一性校验) |
| 规范参考 | RESTful 路径规范、API接口规范 |

---

## P07 表单页专项检查（本次验证新增）

无新增问题。代码层面所有功能均已正确实现：

- ✅ 表单字段完整(6个字段, 含校验)
- ✅ 新增/编辑双模式
- ✅ 异步唯一性校验
- ✅ 日期上限约束(当前+30天)
- ✅ 汇率精度6位
- ✅ 提交loading防重复
- ✅ 弹窗关闭清理
- ✅ API层: CurrencyRateSaveDTO, createCurrencyRateApi, updateCurrencyRateApi, checkCurrencyCodeApi

---

## 统计

| 严重度 | 数量 | 已修复 | 未修复 |
|:---:|:---:|:---:|:---:|
| 🔴 高 | 2 | 1 (ISS-1) | 1 (ISS-4) |
| 🟡 中 | 2 | 1 (ISS-3) | 1 (ISS-2) |
| **合计** | **4** | **2** | **2** |
