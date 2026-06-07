# 库位管理列表页 - 问题清单与修复方案

> **任务编号**：P0-010-002-003-001-002
> **验证日期**：2026-06-07
> **验证人员**：W3

---

## 问题列表

### 问题 #1：缺少 LocationController（严重）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🔴 严重 |
| 问题类型 | 后端缺失 |
| 发现方式 | 代码审查 |

**描述**：
前端 `location.ts` API 模块调用 `/api/warehouse/location/page`、`/api/warehouse/location/{id}` 等端点，但后端 `warehouse` 模块没有 `LocationController` 类。`ILocationService` 和 `LocationServiceImpl` 已完整实现所有业务逻辑，只是没有 REST 控制器将其暴露为 HTTP API。

**影响**：
- 库位列表页无法加载数据
- 新建/编辑/删除/状态切换操作全部不可用
- 仓库下拉列表也无法加载（见问题 #2）

**修复方案**：
创建 `src/main/java/com/erp/module/warehouse/controller/LocationController.java`，实现以下端点：

```java
@RestController
@RequestMapping("/api/warehouse/location")
public class LocationController {

    @Autowired
    private ILocationService locationService;

    @GetMapping("/page")
    public Result<PageResult<LocationVO>> page(LocationQueryDTO query) { ... }

    @GetMapping("/{id}")
    public Result<LocationVO> detail(@PathVariable Long id) { ... }

    @PostMapping
    public Result<LocationVO> create(@Valid @RequestBody LocationCreateDTO dto) { ... }

    @PutMapping("/{id}")
    public Result<LocationVO> update(@PathVariable Long id, @Valid @RequestBody LocationUpdateDTO dto) { ... }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) { ... }
}
```

---

### 问题 #2：缺少 WarehouseController（严重）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🔴 严重 |
| 问题类型 | 后端缺失 |
| 发现方式 | 代码审查 |

**描述**：
库位列表页的"所属仓库"下拉框调用 `getWarehousePage()` 加载仓库列表（`/api/warehouse/warehouse/page`）。但后端同样没有 `WarehouseController`。`IWarehouseService` 和 `WarehouseServiceImpl` 已完整实现。

**影响**：
- 库位表单中"所属仓库"下拉框无数据
- 搜索栏"所属仓库"筛选条件无效

**修复方案**：
创建 `src/main/java/com/erp/module/warehouse/controller/WarehouseController.java`，实现仓库 CRUD 端点。

---

### 问题 #3：缺少前端路由配置（高）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🟡 高 |
| 问题类型 | 前端缺失 |
| 发现方式 | 代码审查 |

**描述**：
库位列表页组件位于 `@/views/warehouse/location/index.vue`，但在 `router/modules/static.ts` 中没有对应的路由配置。无法通过 URL 访问该页面。

**修复方案**：
在 `router/modules/static.ts` 中添加路由配置：

```typescript
export const WAREHOUSE_LOCATION_LIST: RouteRecordRaw = {
  path: '/warehouse/location',
  name: 'WarehouseLocationList',
  component: () => import('@/views/warehouse/location/index.vue'),
  meta: { title: '库位管理', icon: 'Location', keepAlive: true }
}
```

并加入 `staticRoutes` 数组。

---

### 问题 #4：国际化未使用 $t()（低）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🟢 低 |
| 问题类型 | 代码规范 |
| 发现方式 | 代码审查 |

**描述**：
搜索表单 label 和部分 UI 文案使用中文硬编码而非 `$t()` 国际化函数。例如：
- `label="库位名称"` 应为 `label="$t('location.locationName')"`
- `label="所属仓库"` 应为 `label="$t('location.warehouseName')"`

其他同模块页面（如仓库列表页、工作台）也存在类似情况，可统一处理。

**修复方案**：
1. 在 i18n 配置中添加对应的中英文词条
2. 将硬编码文案替换为 `$t()` 调用

---

## 问题统计

| 严重程度 | 数量 |
|:---:|:---:|
| 🔴 严重 | 2 |
| 🟡 高 | 1 |
| 🟢 低 | 1 |
| **合计** | **4** |

---

## 修复优先级建议

1. **立即修复**：问题 #1 和 #2（LocationController + WarehouseController），这是页面功能的核心依赖
2. **尽快修复**：问题 #3（路由配置），Controller 修复后路由配置也需要就位才能访问页面
3. **后续优化**：问题 #4（国际化），不影响功能可用性
