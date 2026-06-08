# 员工中心P03主从列表页 — 问题清单与修复方案

> **任务编号**：P0-012-002-002-001-002
> **发现日期**：2026-06-08
> **发现人**：W10

---

## 问题列表

### 问题 #1：部门筛选下拉无数据源（功能缺陷）

| 属性 | 值 |
|------|-----|
| **严重程度** | 🟡 中等 |
| **类型** | 功能缺陷 |
| **位置** | `erp-ai-web/src/views/hrm/employeecenter/index.vue:346` |
| **现象** | `deptOptions` 初始化为空数组 `ref([])`，从未调用 API 填充，部门筛选下拉永远为空 |
| **影响** | 用户无法按部门筛选员工 |

**修复方案**：

在 `<script setup>` 中添加部门列表加载逻辑：

```ts
import { getDepartmentListApi } from '@/api/modules/org-department'

async function loadDeptOptions(): Promise<void> {
  try {
    const res = await getDepartmentListApi()
    deptOptions.value = (res || []).map(d => ({ label: d.name, value: d.id }))
  } catch {
    // 静默失败，部门筛选不可用时不影响主流程
  }
}
```

在 `onMounted` 中调用 `loadDeptOptions()`。

---

### 问题 #2：统计卡片数据来源不准确（数据准确性问题）

| 属性 | 值 |
|------|-----|
| **严重程度** | 🟡 中等 |
| **类型** | 设计缺陷 |
| **位置** | `erp-ai-web/src/views/hrm/employeecenter/index.vue:391-402` |
| **现象** | `updateStats()` 从 `tableData.value`（当前页数据）计算统计值。`stats.active` 只统计当前页的在职人数，而非全库在职人数 |
| **影响** | 统计卡片显示不准确，翻页时数字会变化 |

**修复方案**：

两种可选方案：
1. **后端聚合**：调用专门的统计 API（如 `/api/hrm/workbench/stats`）获取全局统计
2. **前端修正**：在后端 `pageList` 响应中增加 `extra` 字段携带全局统计，前端从中取值

---

### 问题 #3：表单校验与提示未国际化（i18n 遗漏）

| 属性 | 值 |
|------|-----|
| **严重程度** | 🟢 低 |
| **类型** | i18n 遗漏 |
| **位置** | `erp-ai-web/src/views/hrm/employeecenter/index.vue:358-367` |
| **现象** | `formRules` 中的 `message` 字段使用硬编码中文（'工号不能为空'、'姓名不能为空' 等），ElMessage 提示也使用硬编码中文 |
| **影响** | 切换到英文后，表单校验提示仍显示中文 |

**修复方案**：

```ts
// formRules 示例
const formRules: FormRules = {
  employeeNo: [
    { required: true, message: i18n.t('hrm.employee.employeeNoRequired'), trigger: 'blur' },
    { max: 20, message: i18n.t('hrm.employee.employeeNoMaxLen'), trigger: 'blur' }
  ],
  name: [
    { required: true, message: i18n.t('hrm.employee.nameRequired'), trigger: 'blur' },
    { max: 50, message: i18n.t('hrm.employee.nameMaxLen'), trigger: 'blur' }
  ]
}
```

并在 i18n 文件中补充对应 key。

---

### 问题 #4：删除确认弹窗文本未国际化（i18n 遗漏）

| 属性 | 值 |
|------|-----|
| **严重程度** | 🟢 低 |
| **类型** | i18n 遗漏 |
| **位置** | `erp-ai-web/src/views/hrm/employeecenter/index.vue:455` |
| **现象** | `ElMessageBox.confirm('确认删除员工「${row.name}」？', '提示', ...)` 硬编码中文 |
| **影响** | 英文环境下删除确认弹窗仍显示中文 |

**修复方案**：使用 `$t()` 替换硬编码文本，动态拼接员工姓名。

---

### 问题 #5：编辑表单缺少部门/岗位字段（功能缺失）

| 属性 | 值 |
|------|-----|
| **严重程度** | 🟢 低 |
| **类型** | 功能缺失 |
| **位置** | `erp-ai-web/src/views/hrm/employeecenter/index.vue:250-287` |
| **现象** | 编辑/新增表单中没有 departmentId 和 positionId 的输入项。EmployeeCreateDTO 类型定义中虽有这两个字段，但表单未提供 UI |
| **影响** | 新增/编辑员工时无法设置部门和岗位，可能需要在其他页面单独维护 |

**修复方案**：在表单中添加部门（级联选择器/树选择器）和岗位（下拉选择器）两个字段。

---

## 汇总

| 严重程度 | 数量 | 问题编号 |
|---------|:---:|---------|
| 🔴 严重 | 0 | - |
| 🟡 中等 | 2 | #1, #2 |
| 🟢 低 | 3 | #3, #4, #5 |

## 建议

- 问题 #1（部门下拉无数据）应在下次迭代优先修复，影响核心筛选功能
- 问题 #2（统计卡片数据源）可延后到后端提供聚合 API 后修复
- 问题 #3、#4（i18n）在英文版上线前修复即可
- 问题 #5（缺少部门/岗位字段）根据产品需求决定是否添加
