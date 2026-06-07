# 币种汇率 P04 列表页 — 问题清单与修复方案

> **任务编号**：P0-011-002-002-001-002
> **验证日期**：2026-06-07
> **前置任务**：P0-011-002-002-001-001（编写核心代码）

---

## 问题 #1 [CRITICAL] 缺少 CurrencyRateController

**现象**：后端有完整的 Service 层（`ICurrencyRateService` / `CurrencyRateServiceImpl`），但没有 REST Controller。前端 API 调用 `GET/DELETE /finance/currency-rate` 将返回 404。

**影响**：页面核心功能（数据加载、删除）完全不可用。

**定位**：需要在 `src/main/java/com/erp/module/finance/controller/` 下创建 `CurrencyRateController.java`。

**修复方案**：创建 Controller 暴露以下端点：
- `GET /finance/currency-rate` → 分页查询
- `GET /finance/currency-rate/{id}` → 单条查询
- `POST /finance/currency-rate` → 新增
- `PUT /finance/currency-rate/{id}` → 修改
- `DELETE /finance/currency-rate/{id}` → 删除

**关联文件**：
- 需创建：`src/main/java/com/erp/module/finance/controller/CurrencyRateController.java`
- 依赖：`ICurrencyRateService.java` (已存在)

---

## 问题 #2 [MAJOR] 缺少前端路由注册

**现象**：页面组件 `views/finance/currencyrate/index.vue` 已创建，但路由 `/finance/currencyrate` 未在 `router/modules/static.ts` 中注册。

**影响**：用户无法通过 URL 访问页面，菜单中也不会显示此入口。

**定位**：`erp-ai-web/src/router/modules/static.ts` 缺少路由定义。

**修复方案**：
```typescript
// 币种汇率列表页
export const CURRENCY_RATE_LIST: RouteRecordRaw = {
  path: '/finance/currencyrate',
  name: 'CurrencyRateList',
  component: () => import('@/views/finance/currencyrate/index.vue'),
  meta: { title: '币种汇率', icon: 'Money', keepAlive: true }
}
```
并将其加入 `staticRoutes` 数组。

**关联文件**：
- `erp-ai-web/src/router/modules/static.ts`

---

## 问题 #3 [MODERATE] 页面硬编码中文，未国际化

**现象**：页面中大量中文文本直接写在模板中，未使用 `$t()` 或 i18n key。

| 硬编码位置 | 行号 | 建议 i18n key |
|-----------|:---:|--------------|
| "总记录数" | 9 | `currencyrate.stats.total` |
| "今日新增" | 14 | `currencyrate.stats.todayCount` |
| "汇率类型数" | 20 | `currencyrate.stats.typeCount` |
| "币种名称" | 28 | `currencyrate.filter.currencyName` |
| "汇率类型" | 37 | `currencyrate.filter.rateType` |
| "固定汇率" / "浮动汇率" | 45-46 | `currencyrate.rateType.fixed` / `currencyrate.rateType.floating` |
| "查询" / "重置" | 50-51 | 已有全局 key 可复用 |
| "条记录" | 60 | 已有全局 key 可复用 |
| "新增币种汇率" | 61 | `currencyrate.action.add` |
| "币种编码" / "币种名称" / "基准币种" / "汇率" / "汇率日期" / "汇率类型" / "创建时间" | 74-91 | 列标题用 i18n |
| "操作" | 91 | 已有全局 key |
| "编辑" / "删除" | 93/101 | 已有全局 key |
| "确认删除该币种汇率？" | 95 | `currencyrate.confirm.delete` |

**修复方案**：使用 `$t()` 替换硬编码中文，在 `zh-CN/common.ts` 和 `en-US/common.ts` 中添加对应词条。

---

## 问题 #4 [MINOR] 统计卡片 typeCount 算法不准确

**现象**：`stats.typeCount` 通过当前页数据计算 (`new Set(list.map(item => item.rateType)).size`)，只能反映当前页的汇率类型数，不是全部数据的类型数。

**定位**：`index.vue:220-221`

**修复方案**：
- 方案 A：后端返回 `totalTypeCount` 字段
- 方案 B：单独调用统计接口获取全量类型数
- 当前方案可接受作为近似值，但建议在 pageSize 较大时使用

---

## 问题 #5 [INFO] 编辑/新增为占位实现

**现象**：`handleEdit` 和 `handleAdd` 仅为 `ElMessage.info` 提示，无实际功能。

**说明**：按任务规划，编辑/新增表单页应为后续任务（P0-011-002-002-002 或类似编号）实现，当前占位符合分步开发策略。

**待办**：后续表单页任务完成后，需回填 `handleEdit` 调用编辑弹窗或跳转编辑页。

---

## 总结

| 严重度 | 数量 | 说明 |
|--------|:---:|------|
| CRITICAL | 1 | 缺少后端 Controller |
| MAJOR | 1 | 缺少前端路由注册 |
| MODERATE | 1 | 未国际化 |
| MINOR | 1 | typeCount 统计不准确 |
| INFO | 1 | 编辑/新增占位 |

**建议**：优先修复问题 #1 和 #2，这两项修复后页面即可正常访问和数据交互。问题 #3-#5 可在后续迭代中逐步改进。
