# 薪资管理P03主从列表页 - 问题清单与修复方案

> **关联测试报告**: `hrm-Salary-frontend-test.md`
> **验证日期**: 2026-06-09
> **严重级别**: 🔴 阻塞 / 🟡 警告 / 🟢 建议

---

## 问题 #1: 路由未注册 🔴

**严重级别**: 🔴 阻塞
**发现位置**: `erp-ai-web/src/router/modules/static.ts`
**问题描述**: `/hrm/salary` 路由未在静态路由模块中注册，页面无法通过浏览器访问。

**修复方案**: 在 `static.ts` 中添加路由定义（参考已有的 HRM_ATTENDANCE 路由）:

```typescript
// 薪资管理主从列表页
export const HRM_SALARY: RouteRecordRaw = {
  path: '/hrm/salary',
  name: 'HrmSalary',
  component: () => import('@/views/hrm/salary/index.vue'),
  meta: { title: '薪资管理', icon: 'Money', keepAlive: true }
}
```

然后将 `HRM_SALARY` 加入 `staticRoutes` 导出数组。

---

## 问题 #2: 后端缺少 SalaryController 🔴

**严重级别**: 🔴 阻塞
**发现位置**: `src/main/java/com/erp/hrm/controller/`
**问题描述**: 后端已有 `SalaryServiceImpl`（含 CRUD 逻辑）、`SalaryEntity`、`SalaryVO`、`SalaryQueryDTO` 等完整 Service 层代码，但缺少 `SalaryController.java`。前端所有 API 调用将返回 404。

**修复方案**: 创建 `SalaryController.java`：

```java
@RestController
@RequestMapping("/api/hrm/salary")
@RequiredArgsConstructor
public class SalaryController {
    private final ISalaryService salaryService;

    @GetMapping
    @SaCheckPermission("hrm:salary:list")
    @Operation(summary = "分页查询薪资记录")
    public Result<PageResult<SalaryVO>> page(SalaryQueryDTO query) {
        return Result.success(salaryService.pageList(query));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("hrm:salary:view")
    @Operation(summary = "查询薪资记录详情")
    public Result<SalaryVO> getById(@PathVariable Long id) {
        return Result.success(salaryService.getById(id));
    }

    @PostMapping
    @SaCheckPermission("hrm:salary:add")
    @Operation(summary = "新增薪资记录")
    public Result<SalaryVO> create(@RequestBody @Valid SalaryCreateDTO dto) {
        return Result.success(salaryService.create(dto));
    }

    @PutMapping("/{id}")
    @SaCheckPermission("hrm:salary:edit")
    @Operation(summary = "修改薪资记录")
    public Result<SalaryVO> update(@PathVariable Long id, @RequestBody @Valid SalaryUpdateDTO dto) {
        return Result.success(salaryService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("hrm:salary:delete")
    @Operation(summary = "删除薪资记录")
    public Result<Void> delete(@PathVariable Long id) {
        salaryService.delete(id);
        return Result.success();
    }
}
```

---

## 问题 #3: 国际化词条缺失 🔴

**严重级别**: 🔴 阻塞
**发现位置**: `erp-ai-web/src/i18n/locales/zh-CN/common.ts` 和 `en-US/common.ts`
**问题描述**: 页面使用了大量 `$t('hrm.salary.*')` 国际化调用，但 i18n 文件中没有 `hrm.salary` 节点。页面将显示原始 key 如 "hrm.salary.totalRecords" 而非中文文本。

**修复方案**: 在 `zh-CN/common.ts` 的 `hrm` 对象中添加 `salary` 节点:

```typescript
salary: {
  totalRecords: '薪资记录数',
  totalBaseSalary: '基本工资合计',
  totalNetSalary: '实发工资合计',
  employees: '涉及员工',
  employeeId: '员工ID',
  employeeIdPlaceholder: '请输入员工ID',
  salaryMonth: '薪资月份',
  salaryMonthPlaceholder: '请输入月份',
  salaryMonthFormat: '格式: YYYY-MM',
  baseSalary: '基本工资',
  baseSalaryPlaceholder: '请输入基本工资',
  allowance: '津贴',
  allowancePlaceholder: '请输入津贴',
  deduction: '扣款',
  deductionPlaceholder: '请输入扣款',
  netSalary: '实发工资',
  netSalaryPreview: '实发工资(预览)',
  grossSalary: '应发工资',
  createTime: '创建时间',
  add: '新增薪资',
  addTitle: '新增薪资记录',
  editTitle: '编辑薪资记录',
  deleteConfirm: '确认删除该薪资记录？',
  recordCount: '共 {total} 条记录',
  detailTitle: '薪资 # {id} 明细',
  selectHint: '请选择薪资记录查看详情',
  clickRowHint: '点击左侧表格行查看薪资明细',
  tabBreakdown: '薪资明细',
  tabSummary: '汇总统计'
}
```

同步在 `en-US/common.ts` 中添加对应英文翻译。

---

## 问题 #4: 菜单配置缺失 🟡

**严重级别**: 🟡 警告
**发现位置**: 菜单配置
**问题描述**: 薪资管理页面未在左侧导航菜单中配置入口，用户无法通过菜单导航到该页面。

**修复方案**: 在菜单配置中添加 `hrm.salary` 菜单项，绑定路由 `/hrm/salary`，并配置权限标识 `hrm:salary:list`。

---

## 问题 #5: 权限指令缺失 🟡

**严重级别**: 🟡 警告
**发现位置**: `erp-ai-web/src/views/hrm/salary/index.vue`
**问题描述**: 页面及操作按钮未添加 `v-permission` 权限指令，无法进行权限控制。

**修复方案**: 
- 统计卡片区: 无需权限指令（只读数据）
- 新增按钮: 添加 `v-permission="'hrm:salary:add'"`
- 编辑按钮: 添加 `v-permission="'hrm:salary:edit'"`
- 删除按钮: 添加 `v-permission="'hrm:salary:delete'"`

---

## 问题 #6: 表格列与规格不一致 🟡

**严重级别**: 🟡 警告
**发现位置**: `erp-ai-web/src/views/hrm/salary/index.vue` (Vxe Table 列定义)
**问题描述**: 任务规格 (Section 5.2) 定义的列与代码实现不完全匹配：

| 规格列 | 代码列 | 匹配 |
|--------|--------|:---:|
| 员工姓名 | employeeId | ❌ 规格要求显示姓名，代码显示ID |
| 年度 | — | ❌ 缺失 |
| 月份 | salaryMonth | ✅ |
| 基本工资 | baseSalary | ✅ |
| 应发工资 | — | ❌ 缺失（仅在从表中有 gross） |
| 实发工资 | netSalary | ✅ |
| 发放状态 | — | ❌ 缺失 |
| 发放日期 | — | ❌ 缺失 |

**修复方案**: 
1. 前端需要联查员工姓名（可调用 `/api/hrm/employee/{id}` API 或让后端 JOIN 返回姓名）
2. 添加发放状态列（需确认 SalaryEntity 是否有此字段）
3. 如后端有对应字段，补充缺失列

---

## 修复优先级建议

| 序号 | 问题 | 严重度 | 修复顺序 |
|:---:|------|:---:|:---:|
| 1 | 后端SalaryController | 🔴 | 1 |
| 2 | 路由注册 | 🔴 | 2 |
| 3 | 国际化词条 | 🔴 | 3 |
| 4 | 表格列对齐 | 🟡 | 4 |
| 5 | 菜单配置 | 🟡 | 5 |
| 6 | 权限指令 | 🟡 | 6 |
