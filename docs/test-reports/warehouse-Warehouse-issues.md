# Warehouse 仓库定义列表页 — 问题清单与修复方案

> **验证任务**: P0-010-002-001-001-002
> **日期**: 2026-06-07
> **工人**: W3

---

## 问题列表

### ISSUE-01: 路由未注册 (阻塞)

| 属性 | 值 |
|------|-----|
| 严重级别 | CRITICAL |
| 影响范围 | 页面完全不可访问 |
| 文件 | `erp-ai-web/src/router/modules/static.ts` |
| 预期 | 路由 `/warehouse/warehouse` 注册 |

**修复方案**:
在 `static.ts` 中添加路由定义并加入 `staticRoutes` 数组:

```typescript
export const WAREHOUSE_LIST: RouteRecordRaw = {
  path: '/warehouse/warehouse',
  name: 'WarehouseList',
  component: () => import('@/views/warehouse/warehouse/index.vue'),
  meta: { title: '仓库定义', icon: 'Box', keepAlive: true }
}
```

---

### ISSUE-02: API 路径与规格不一致 (高风险)

| 属性 | 值 |
|------|-----|
| 严重级别 | HIGH |
| 影响范围 | 后端联调时 404 |
| 文件 | `erp-ai-web/src/api/modules/warehouse.ts` |
| 规格路径 | `GET /api/warehouse/warehouse` (Section 5.2) |
| 实现路径 | `GET /api/warehouse/page` (第13行) |

**修复方案**:
将 `warehouse.ts` 第13行的路径从 `/api/warehouse/page` 改为 `/api/warehouse/warehouse/page` 或与后端实际路径对齐。建议与后端确认实际 Controller 的 `@RequestMapping` 路径。

```typescript
// 方案A: 如后端为 /api/warehouse/warehouse
return request.get('/api/warehouse/warehouse', { params })

// 方案B: 如后端为 /api/warehouse + 方法级 page
return request.get('/api/warehouse/warehouse/page', { params })
```

---

### ISSUE-03: 缺少"负责人"列 (中风险)

| 属性 | 值 |
|------|-----|
| 严重级别 | MEDIUM |
| 影响范围 | 列表信息不完整 |
| 文件 | `erp-ai-web/src/views/warehouse/warehouse/index.vue` |
| 规格列 | 仓库编码, 仓库名称, 仓库类型, 地址, **负责人**, 联系电话, 状态 |
| 实现列 | 仓库编码, 仓库名称, 仓库类型, 地址, 联系电话, 状态, 创建时间 |

**说明**: 类型定义 `WarehouseListVO` 包含 `managerId` 字段，但表格中未展示负责人列。需确认是否有负责人姓名映射。

**修复方案**:
在 vxe-table 中添加负责人列（如后端提供负责人姓名字段）:

```html
<vxe-column field="managerName" title="负责人" width="120" />
```

---

### ISSUE-04: 统计卡片仅计算当前页 (低风险)

| 属性 | 值 |
|------|-----|
| 严重级别 | LOW |
| 影响范围 | 统计数字不准确 |
| 文件 | `erp-ai-web/src/views/warehouse/warehouse/index.vue` |
| 行号 | 161-165 |

**问题描述**: `stats` computed 通过 `tableData.value.filter()` 计算启用/停用数量，仅统计当前页数据而非全量。当数据超过一页时统计不准确。

**修复方案**: 从后端响应中获取全量统计数据，或在分页查询返回中包含状态统计字段。

---

## 已确认正常的项目

- TypeScript 类型定义完整正确 ✓
- 使用封装的 request 工具 ✓
- 防抖实现正确 (300ms) ✓
- 删除二次确认弹窗 ✓
- 异常处理覆盖完整 ✓
- Vxe Table 虚拟滚动配置 ✓
- Loading 状态管理 ✓
- 编辑/启停占位实现符合预期（等待 P0-010-002-002 任务）✓
