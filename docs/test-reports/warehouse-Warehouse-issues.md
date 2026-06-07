# 仓库定义表单页 — 问题清单与修复方案

> **任务编号**: P0-010-002-002-001-002
> **验证人**: W3
> **日期**: 2026-06-07

---

## 问题列表

### I-01 [CRITICAL] 后端 WarehouseController 缺失

**现象**: 项目中无 `WarehouseController.java`、`LocationController.java`、`WorkbenchController.java`。前端所有仓库相关API调用将返回404。

**影响**: 仓库列表页、表单页、工作台页面全部不可用。

**根因**: P0-010模块任务拆解中未包含Controller任务，Service层完成后直接跳到了前端页面开发。

**修复方案**:
创建 `InvWarehouseController.java`，按模块开发指南 Section 7.1 实现：

```
路径: src/main/java/com/erp/module/warehouse/controller/InvWarehouseController.java
```

参考接口规格:
| 方法 | HTTP | 路径 | 权限 |
|------|------|------|------|
| page | GET | /api/warehouse/warehouse | inv:warehouse:query |
| all | GET | /api/warehouse/warehouse/all | inv:warehouse:query |
| getById | GET | /api/warehouse/warehouse/{id} | inv:warehouse:query |
| create | POST | /api/warehouse/warehouse | inv:warehouse:add |
| update | PUT | /api/warehouse/warehouse/{id} | inv:warehouse:edit |
| delete | DELETE | /api/warehouse/warehouse/{id} | inv:warehouse:delete |

同样需要创建 `InvLocationController` 和 `InvWarehouseWorkbenchController`。

---

### I-02 [CRITICAL] 前端API路径与模块指南不一致

**现象**: 前端 `erp-ai-web/src/api/modules/warehouse.ts` 中的API路径与模块开发指南规定的路径不一致。

**对比**:

| API函数 | 前端当前路径 | 模块指南规定路径 |
|--------|------------|--------------|
| getWarehousePage | GET /api/warehouse/page | GET /api/warehouse/warehouse |
| getWarehouseDetail | GET /api/warehouse/${id} | GET /api/warehouse/warehouse/${id} |
| createWarehouse | POST /api/warehouse | POST /api/warehouse/warehouse |
| updateWarehouse | PUT /api/warehouse/${id} | PUT /api/warehouse/warehouse/${id} |
| deleteWarehouse | DELETE /api/warehouse/${id} | DELETE /api/warehouse/warehouse/${id} |

**修复方案**:
方案A（推荐）: 修改前端API路径，与模块指南对齐，然后按模块指南创建Controller。
方案B: 修改模块指南，按前端当前路径创建Controller。

建议方案A，模块指南是设计文档，前端应跟随设计。

**修复文件**: `erp-ai-web/src/api/modules/warehouse.ts`

---

### I-03 [MINOR] 统计卡片数据不准确

**现象**: `stats` computed 从 `tableData.value`（当前页数据）计算"已启用"和"已停用"数量，而非全局总数。

**代码定位**: `warehouse/index.vue` 第277-280行
```ts
const stats = computed(() => {
  const total = pagination.total
  const enabled = tableData.value.filter((item) => item.status === 1).length
  return { total, enabled, disabled: total - enabled }
})
```

**修复方案**: 后端分页接口返回总计数字段（`enabledCount`, `disabledCount`），或新增一个统计接口。前端从响应中读取。

**优先级**: 低 — 不影响核心功能，可后续迭代。

---

### I-04 [MINOR] 缺少空状态提示

**现象**: 列表数据为空时，只显示空表格，缺少空状态占位提示。

**修复方案**: VxeTable 配置空状态插槽或使用 `empty-text` 属性。

**优先级**: 低 — 用户体验优化项。

---

## 修复优先级

| 优先级 | 问题编号 | 说明 |
|:---:|:---:|------|
| P0-阻塞 | I-01 + I-02 | 需同步修复，Controller路径与前端API必须一致 |
| P2-优化 | I-03 | 统计数据准确性 |
| P3-体验 | I-04 | 空状态UI提示 |
