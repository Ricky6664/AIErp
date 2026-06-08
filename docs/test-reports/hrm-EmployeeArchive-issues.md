# 员工档案P04单一列表页 — 问题清单与修复方案

> **任务编号**：P0-012-002-004-001-002
> **关联报告**：hrm-EmployeeArchive-frontend-test.md
> **创建日期**：2026-06-08

---

## 🔴 阻塞级

### B1. 后端 EmployeeArchive API 未实现

**描述**：前端页面通过 `/api/hrm/employee-archive` 调用6个接口（分页查询/详情/新增/更新/删除/状态切换），但后端无任何对应代码。

**影响**：页面所有 API 调用返回 404，页面无法加载数据，所有 CRUD 操作不可用。

**后端缺失清单**：
- `EmployeeArchiveEntity.java` — 实体类
- `EmployeeArchiveMapper.java` + XML — 数据访问层
- `EmployeeArchiveService.java` + `EmployeeArchiveServiceImpl.java` — 业务逻辑层
- `EmployeeArchiveController.java` — 控制器（REST API）
- `EmployeeArchiveCreateDTO.java` / `EmployeeArchiveUpdateDTO.java` / `EmployeeArchiveQueryDTO.java` / `EmployeeArchiveVO.java` — DTO/VO

**修复方案**：按顺序执行对应的末端任务（DDL → Entity → Mapper → Service → Controller），完成后页面即可正常联调。

**关联任务**：P0-012 模块中 EmployeeArchive 相关后端任务（如存在）

---

### B2. 数据库表 hrm_employee_archive 未创建

**描述**：数据库迁移目录 `db/migration/` 中无 `hrm_employee_archive` 表的 DDL 文件。

**影响**：即使后端代码实现，也无法持久化数据。

**修复方案**：创建 Flyway 迁移脚本，建立 `hrm_employee_archive` 表（含字段：id, employee_name, education, major, school, emergency_contact, archive_date, status, create_time, update_time）。

---

## 🟡 中等级

### M1. 统计卡片仅从当前页计算

**位置**：`index.vue` L286-297

**描述**：`stats.enabled`、`stats.disabled`、`stats.newThisMonth` 均从 `tableData.value`（当前页数据）计算，不反映全局真实数据。

```typescript
// 当前实现：仅从当前页20条数据统计
const enabled = tableData.value.filter((r) => r.status === 1).length

// 期望：从全局数据统计（需要后端提供聚合接口或前端计算所有页）
```

**修复方案**（二选一）：
1. **推荐**：后端新增 `GET /api/hrm/employee-archive/stats` 聚合接口，返回全局统计数据
2. 备选：前端拉取全量数据（不分页）计算统计

---

## 🟢 低优先级

### L1. searchForm 中 pageNum/pageSize 字段冗余

**位置**：`index.vue` L273-278

**描述**：`searchForm` 定义了 `pageNum: 1` 和 `pageSize: 20`，但 `loadTableData()` 实际使用 `pagination.current` 和 `pagination.size` 构造查询参数，`searchForm` 中的分页字段从未使用。

**修复方案**：从 `EmployeeArchiveQueryDTO` 使用处移除 `pageNum`/`pageSize`，或在 `loadTableData` 中同步使用 `searchForm` 的分页字段（推荐前者，保持单一数据源）。

---

### L2. 表单校验覆盖不完整

**位置**：`index.vue` L311-316

**描述**：`formRules` 仅校验 `employeeName`（必填 + max:50）。建议对 `archiveDate` 添加日期格式校验，对 `emergencyContact` 添加手机号格式校验。

```typescript
// 建议补充
archiveDate: [
  { pattern: /^\d{4}-\d{2}-\d{2}$/, message: '日期格式无效', trigger: 'blur' }
],
emergencyContact: [
  { pattern: /^1[3-9]\d{9}$/, message: '手机号格式无效', trigger: 'blur' }
]
```

---

### L3. 路由缺少权限守卫

**位置**：`static.ts` L226-231

**描述**：路由 meta 未定义 `permission` 字段，页面无访问控制。

**修复方案**：根据权限体系规范添加 `meta.permission` 字段。
