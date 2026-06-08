# HRM工作台聚合数据 — 问题清单与修复方案

> **任务编号**：P0-012-001-005-001-002
> **关联报告**：hrm-HrmWorkbenchAggregate-backend-test.md
> **创建日期**：2026-06-08

---

## 问题列表

### 问题 #1：缺少Controller端点 [严重]

**描述**：HrmWorkbenchAggregateServiceImpl已实现，但没有对应的REST Controller暴露HTTP端点，前端无法调用。

**影响**：工作台聚合API不可用，功能不可达。

**修复方案**：
```java
@RestController
@RequestMapping("/api/hrm/workbench")
public class HrmWorkbenchController {

    private final HrmWorkbenchAggregateServiceImpl workbenchService;

    @GetMapping("/aggregate")
    @SaCheckPermission("hrm:workbench:view")
    public R<HrmWorkbenchAggregateVO> getWorkbenchData() {
        return R.ok(workbenchService.getWorkbenchData());
    }
}
```

**涉及文件**：新增 `erp-hrm-module/.../controller/HrmWorkbenchController.java`

---

### 问题 #2：缺少Service接口 [中等]

**描述**：HrmWorkbenchAggregateServiceImpl未实现接口，与模块内其他Service（IEmployeeService、IAttendanceService等）风格不一致。

**影响**：Spring代理方式受限，不利于扩展和测试Mock。

**修复方案**：抽取接口 `IHrmWorkbenchAggregateService`，让Impl实现该接口。

---

### 问题 #3：缺少异常降级处理 [严重]

**描述**：`getWorkbenchData()` 方法无try-catch，任意子查询失败都会导致整个工作台接口500。任务文档明确要求"异常场景要有降级方案"。

**影响**：单体查询失败会级联导致整个工作台崩溃。

**修复方案**：
```java
try {
    vo.setTotalEmployees(countEmployees(tenantId, null));
} catch (Exception e) {
    log.error("查询员工总数失败", e);
    vo.setTotalEmployees(0L);
}
// 各KPI指标独立try-catch
```

---

### 问题 #4：全量加载后Java聚合 — 性能风险 [中等]

**描述**：以下方法先将全部匹配记录加载到JVM内存，再在Java中聚合：
- `countDistinctDepartments` — 加载全部员工后Java distinct计数
- `sumMonthlySalary` — 加载全部薪资记录后Java求和
- `getDepartmentDistribution` — 加载全部员工后Java分组
- `getEmployeeMonthlyTrend` — 加载6个月内全部员工后Java分组
- `getAttendanceMonthlyTrend` — 加载6个月内全部考勤后Java分组

**影响**：数据量大时（万级+）会导致OOM或响应超时。

**修复方案**：使用MyBatis-Plus自定义SQL或Mapper XML实现数据库端聚合：
- 部门去重计数：`SELECT COUNT(DISTINCT department_id) FROM hrm_employee WHERE tenant_id = ?`
- 薪资求和：`SELECT SUM(net_salary) FROM hrm_salary WHERE tenant_id = ? AND salary_month = ?`
- 分组统计：`SELECT department_id, COUNT(*) FROM hrm_employee WHERE tenant_id = ? GROUP BY department_id`

---

### 问题 #5：趋势数据使用错误的日期字段 [中等]

**描述**：
- `getEmployeeMonthlyTrend` 使用 `createTime`（记录创建时间）过滤，应使用 `entryDate`（入职日期）
- `getAttendanceMonthlyTrend` 使用 `createTime`（记录创建时间）过滤，应使用 `attendanceDate`（考勤日期）

**影响**：趋势图表显示的是"记录创建时间"的趋势，而非业务事件（入职/考勤）的真实趋势。

**修复方案**：
- Employee趋势：将 `.ge(EmployeeEntity::getCreateTime, sixMonthsAgo)` 改为 `.ge(EmployeeEntity::getEntryDate, sixMonthsAgo.toLocalDate())`
- Attendance趋势：将 `.ge(AttendanceEntity::getCreateTime, sixMonthsAgo)` 改为 `.ge(AttendanceEntity::getAttendanceDate, sixMonthsAgo.toLocalDate())`

---

### 问题 #6：Cache Key SpEL表达式不标准 [轻微]

**描述**：`key = "'hrm:' + #root.target.getCurrentTenantId()"` 直接在SpEL中调用目标对象方法，绕过Spring代理。

**影响**：当前场景下可工作，但脆弱（如果getCurrentTenantId改为依赖AOP则失效）。

**修复方案**：将tenantId作为方法参数传入：
```java
@Cacheable(value = "workbench", key = "'hrm:' + #tenantId", unless = "#result == null")
public HrmWorkbenchAggregateVO getWorkbenchData(Long tenantId) { ... }
```
或从Controller层获取tenantId后传入。

---

### 问题 #7：缺少@CacheEvict — 数据变更后缓存未清除 [中等]

**描述**：当员工、考勤、招聘、薪资数据发生变更时，工作台聚合缓存不会自动清除，导致数据不一致（最多5分钟TTL窗口）。

**影响**：用户在数据变更后刷新工作台，看到的是旧缓存数据。

**修复方案**：在EmployeeServiceImpl、AttendanceServiceImpl等数据变更方法上添加：
```java
@CacheEvict(value = "workbench", key = "'hrm:' + #entity.tenantId")
```
或使用 `@CacheEvict(value = "workbench", allEntries = true)` 清除所有租户缓存。

---

### 问题 #8：查询未设置超时时间 [轻微]

**描述**：任务文档要求"聚合SQL查询要设置超时时间(30s)"，但当前查询未配置超时。

**影响**：慢查询可能长时间占用数据库连接。

**修复方案**：在Mapper XML中为聚合查询设置 `timeout="30"` 或配置MyBatis-Plus `pageTimeout`。

---

## 问题统计

| 严重程度 | 数量 |
|---------|:---:|
| 严重 | 2 |
| 中等 | 4 |
| 轻微 | 2 |

**建议修复优先级**：#1（Controller）→ #3（降级）→ #4/#5（数据正确性与性能）→ #7（缓存一致性）→ #2/#6/#8（代码质量）
