# 库位管理表单页 — 问题清单

> **任务编号**: P0-010-002-004-001-002
> **验证人**: W5
> **验证日期**: 2026-06-07
> **严重程度**: CRITICAL > MAJOR > MINOR > INFO

---

## 问题列表

| 编号 | 问题描述 | 严重程度 | 影响范围 |
|:---:|---------|:---:|---------|
| F01 | **LocationController缺失** — 前端5个API端点无后端Controller。Service层已完整实现(ILocationService+LocationServiceImpl)，仅缺REST入口 | CRITICAL | 新建/编辑/删除/状态切换全部不可用 |
| F02 | **WarehouseController缺失** — 表单"所属仓库"下拉框依赖getWarehousePage()，但后端无WarehouseController | CRITICAL | 仓库下拉无数据，用户无法选择所属仓库 |
| F03 | **formData类型扩展不规范** — `LocationCreateDTO & { id?: number }` 联合类型可工作但不规范，建议定义独立表单DTO | MINOR | 不影响功能，增加类型理解成本 |
| F04 | **排序号无默认值提示** — formData.sortOrder默认0，用户可能不清楚取值范围(0-9999) | MINOR | 不影响功能，用户体验轻微影响 |

---

## 详细分析

### F01 — LocationController缺失（CRITICAL）

**现状**：
- Service层完整：`ILocationService.java` + `LocationServiceImpl.java`
- 数据层完整：`LocationEntity/Mapper/DTO/VO`
- 测试存在：`LocationServiceTest.java`
- 前端API模块就绪：`location.ts`（5个API函数）

**需新增的Controller端点**：
```
GET    /api/warehouse/location/page  → 分页查询
GET    /api/warehouse/location/{id}  → 查询详情
POST   /api/warehouse/location       → 新增库位
PUT    /api/warehouse/location/{id}  → 修改库位
DELETE /api/warehouse/location/{id}  → 删除库位
```

### F02 — WarehouseController缺失（CRITICAL）

库位表单中"所属仓库"下拉框调用`getWarehousePage()` → `GET /api/warehouse/warehouse/page`。该Controller同样缺失，导致下拉框无数据源。

### F03 — formData类型扩展（MINOR）

当前：`const formData = reactive<LocationCreateDTO & { id?: number }>({...})`

建议定义独立类型：
```typescript
interface LocationFormDTO extends LocationCreateDTO {
  id?: number
}
```

### F04 — 排序号提示（MINOR）

`el-input-number` 的 min/max 属性不提供用户可见提示。可考虑添加 placeholder 或 help 文本说明取值范围的业务含义。

---

## 问题统计

| 严重程度 | 数量 |
|:---:|:---:|
| 🔴 CRITICAL | 2 |
| 🟡 MAJOR | 0 |
| 🟢 MINOR | 2 |
| **合计** | **4** |

---

## 与列表页(W3)问题清单的关系

W3在P0-010-002-003-001-002(库位管理列表页验证)中已识别：
- LocationController缺失 ✅ 已记录
- WarehouseController缺失 ✅ 已记录
- 前端路由缺失 ✅ 已记录
- 国际化未使用$t() ✅ 已记录

本表单页验证新识别：
- formData类型扩展不规范（表单特有）
- 排序号无默认值提示（表单特有）

Controller缺失是表单页与列表页的共享阻塞项，需在L3 Controller任务中统一解决。
