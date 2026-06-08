# 员工档案P07单一表单页 — 问题清单与修复方案

> **任务编号**：P0-012-002-009-001-002
> **关联报告**：hrm-EmployeeArchive-frontend-test.md
> **创建日期**：2026-06-09

---

## 🔴 阻塞级

### B1. 后端 EmployeeArchive API 未实现

**描述**：前端 `hrm-archive.ts` 调用 `/api/hrm/employee-archive` 的6个接口（分页查询/详情/新增/更新/删除/状态切换），后端无任何对应 Controller/Service/Mapper/Entity。

**影响**：所有 API 调用返回 404。页面无法加载数据，CRUD 操作完全不可用。

**后端缺失清单**：
- `EmployeeArchiveEntity.java` — 实体类
- `EmployeeArchiveMapper.java` + XML — 数据访问层
- `IEmployeeArchiveService.java` + `EmployeeArchiveServiceImpl.java` — 业务逻辑层
- `EmployeeArchiveController.java` — REST 控制器
- `EmployeeArchiveCreateDTO.java` / `EmployeeArchiveUpdateDTO.java` / `EmployeeArchiveQueryDTO.java` / `EmployeeArchiveVO.java` — DTO/VO

**现有后端对照**：`EmployeeController` 映射路径为 `/api/hrm/employee`，与前端期望 `/api/hrm/employee-archive` 路径不匹配。

**修复方案**：
1. 创建 Flyway DDL 迁移脚本建立 `hrm_employee_archive` 表
2. 按层级顺序实现 Entity → Mapper → Service → Controller
3. Controller 使用 `@RequestMapping("/api/hrm/employee-archive")` 匹配前端 API 路径

---

### B2. 数据库表 hrm_employee_archive 未创建

**描述**：数据库迁移目录中无 `hrm_employee_archive` 表的 DDL 文件。

**影响**：即使后端代码实现，数据无法持久化。

**修复方案**：创建 Flyway 迁移脚本，字段参考前端 `EmployeeArchiveVO`：
- `id` BIGINT PRIMARY KEY
- `employee_id` BIGINT NOT NULL
- `employee_name` VARCHAR(50)
- `education` VARCHAR(20)
- `major` VARCHAR(50)
- `school` VARCHAR(100)
- `emergency_contact` VARCHAR(50)
- `emergency_phone` VARCHAR(20)
- `address` VARCHAR(200)
- `bank_card_number` VARCHAR(30)
- `bank_name` VARCHAR(100)
- `social_security_account` VARCHAR(30)
- `archive_date` DATE
- `status` INT DEFAULT 1
- `create_time` TIMESTAMP
- `update_time` TIMESTAMP

---

## 🟡 中等级

### M1. 统计卡片仅从当前页数据计算

**位置**：`index.vue` L344-355

**描述**：`stats.enabled`、`stats.disabled`、`stats.newThisMonth` 仅从 `tableData.value`（当前页最多20条）过滤计算，当数据超过一页时统计不准确。

```typescript
// 当前：仅从当前页计算
const enabled = tableData.value.filter((r) => r.status === 1).length

// 期望：从全局数据统计
```

**修复方案**（二选一）：
1. **推荐**：后端新增 `GET /api/hrm/employee-archive/stats` 聚合接口返回全局统计
2. 备选：前端单独拉取全量数据计算统计

---

## 🟢 低优先级

### L1. tableRef 声明但未读取

**位置**：`index.vue` L320

**描述**：
```typescript
const tableRef = ref()  // TS6133: declared but never read
```
模板中 `ref="tableRef"` 仅用于 vxe-table 组件实例绑定，脚本逻辑中未使用 `tableRef.value`。

**修复方案**：若不需要调用 table 实例方法（如 `validate()`），可删除该变量。模板 ref 绑定会自动创建组件实例引用。

---

### L2. searchForm 中 pageNum/pageSize 字段冗余

**位置**：`index.vue` L331-336

**描述**：`searchForm: EmployeeArchiveQueryDTO` 定义了 `pageNum: 1` 和 `pageSize: 20`，但 `loadTableData()` (L384-389) 实际使用 `pagination.current` 和 `pagination.size` 构造查询参数。

**修复方案**：从 `loadTableData` 内部 query 构造中移除 searchForm 的 pageNum/pageSize，或统一使用 searchForm 作为分页源（推荐保持 `pagination` 作为单一数据源）。

---

### L3. 表单校验覆盖不完整

**位置**：`index.vue` L375-378

**描述**：`formRules` 仅校验 `employeeId`(必填) 和 `emergencyPhone`(手机号正则)。建议补充：
- `archiveDate` 日期格式校验
- `bankCardNumber` 银行卡号格式校验
- `socialSecurityAccount` 社保账号格式校验

**修复方案**：
```typescript
archiveDate: [
  { pattern: /^\d{4}-\d{2}-\d{2}$/, message: '日期格式无效', trigger: 'blur' }
]
```

---

### L4. 路由缺少权限守卫

**位置**：`static.ts` L226-231

**描述**：路由 meta 未定义 `permission` 字段，页面无访问控制。

**修复方案**：根据权限体系规范添加 `meta.permission` 字段，如 `permission: 'hrm:archive:list'`。
