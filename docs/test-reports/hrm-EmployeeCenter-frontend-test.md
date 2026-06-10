# 员工中心P06主从表单页 — 前端验证报告

> **任务编号**：P0-012-002-003-001-002
> **验证日期**：2026-06-08
> **验证人**：W10
> **验证方法**：代码审查 + 前端类型检查 + API 对齐分析 + 后端代码审核

---

## 一、验证清单执行结果

| 序号 | 验证项 | 结果 | 备注 |
|:---:|--------|:---:|------|
| 1 | 页面路由访问 | ✅ 通过 | 路由 `/hrm/employeecenter` 已在 `static.ts` 注册，name=`HrmEmployeecenter` |
| 2 | 数据加载 | ⚠️ 部分通过 | API 调用正确，部门下拉已修复（`loadDeptOptions` 从 `/api/system/dept/tree` 加载），但统计卡片仍从当前页数据计算 |
| 3 | 筛选/搜索功能 | ✅ 通过 | 姓名搜索(300ms防抖)、部门筛选(新增数据源)、状态筛选均正常 |
| 4 | 操作交互 | ✅ 通过 | 新增/编辑/删除正确绑定，删除有二次确认，编辑回显含档案列表 |
| 5 | 数据回显(编辑) | ⚠️ 部分通过 | 基本信息 8 个字段正确回显；档案列表通过 `(detail as any).archives` 读取（后端无archives字段支持） |
| 6 | 表单校验 | ✅ 通过 | 新增身份证18位校验、手机号11位校验、邮箱格式校验，employeeNo/name 必填+max |
| 7 | 异常处理 | ✅ 通过 | 所有 API 调用有 try-catch，失败时 ElMessage.error |

---

## 二、编译验证

| 项目 | 结果 | 说明 |
|------|:---:|------|
| 后端 `mvn compile` | ✅ | 无错误（历史通过，本次无后端变更） |
| 前端 `vue-tsc --noEmit` | ✅ | 无类型错误 |

---

## 三、P06 新增功能覆盖逐项检查

### 3.1 编辑弹窗升级（900px P06 主从表单）
- 弹窗宽度从默认升级至 `900px` — ✅
- `close-on-click-modal="false"` 防误关闭 — ✅
- 关闭时 `resetForm()` 清理表单 + 档案列表 — ✅

### 3.2 新增表单字段
- 身份证号 `idCard`：input + maxlength=18 + 校验规则 `(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)` — ✅
- 部门 `departmentId`：下拉选择器 + deptOptions 数据源 — ✅（已修复加载逻辑）
- 岗位 `positionId`：input 输入框（非下拉选择器，待对接岗位 API）— ⚠️

### 3.3 员工档案从表（Vxe Table）
- 列定义：学历 / 专业 / 毕业院校 / 紧急联系人 / 银行卡号 — ✅
- 行内点击编辑：`edit-config="{ trigger: 'click', mode: 'cell' }"` + edit-render input — ✅
- 新增行按钮 `addArchiveRow()` — ✅
- 删除行按钮 `removeArchiveRow(index)` — ✅
- max-height=300 滚动 — ✅

### 3.4 主从联动提交
- `handleSubmit` 组装 `{ ...formData, archives: archiveList.value }` — ✅
- `loadEmployeeDetail` 回显时读取 `archives` — ⚠️（后端无此字段）

---

## 四、API 对齐检查

| 前端调用 | 后端接口 | 前端参数 | 后端DTO | 对齐 |
|---------|---------|---------|--------|:---:|
| `getEmployeePageApi` | `GET /api/hrm/employee` | EmployeeQueryDTO | EmployeeQueryDTO | ✅ |
| `getEmployeeByIdApi` | `GET /api/hrm/employee/{id}` | id: number | Long id | ✅ |
| `createEmployeeApi` | `POST /api/hrm/employee` | EmployeeCreateDTO + archives | EmployeeCreateDTO（**无archives字段**） | ❌ |
| `updateEmployeeApi` | `PUT /api/hrm/employee/{id}` | EmployeeUpdateDTO + archives | EmployeeUpdateDTO（**无archives字段**） | ❌ |
| `deleteEmployeeApi` | `DELETE /api/hrm/employee/{id}` | id: number | Long id | ✅ |

> **关键风险**：前端提交 `archives` 字段，后端 DTO 未定义此字段。根据 Jackson 默认配置，未知属性将导致 `UnrecognizedPropertyException`（若 `FAIL_ON_UNKNOWN_PROPERTIES=true`）或被静默忽略（默认行为）。无论何种情况，**员工档案从表数据将丢失**。

---

## 五、i18n 覆盖检查

| 检查项 | 结果 | 说明 |
|--------|:---:|------|
| P03 原有 key (30+) | ✅ | zh-CN + en-US 均已定义 |
| P06 新增 key (11个) | ✅ | 已补充：idCardPlaceholder, positionPlaceholder, baseInfo, archiveInfo, archiveList, addArchive, archiveEducation, archiveMajor, archiveSchool, archiveEmergencyContact, archiveBankCardNo |
| 表单校验 message | ❌ | formRules 仍使用硬编码中文（employeeNo/name/idCard/phone/email 的 message） |
| ElMessage 提示 | ❌ | '加载员工列表失败'/'删除成功'/'新增成功' 等硬编码中文 |
| 删除确认弹窗 | ❌ | `ElMessageBox.confirm` 文本硬编码中文 |

---

## 六、边界场景分析

| 场景 | 处理方式 | 评估 |
|------|---------|:---:|
| 空列表 | vxe-table 显示空状态 | ✅ |
| API 异常 | catch 块 ElMessage.error | ✅ |
| 删除确认 | ElMessageBox.confirm + try-catch | ✅ |
| 档案行为空 | addArchiveRow() 可新增 | ✅ |
| 身份证脱敏 | maskIdCard() 保留前6后4 | ✅ |
| 编辑回显空档案 | detail.archives 可能 undefined → `\|\| []` | ✅ |
| Jackson 未知属性 | 取决于 Spring 配置，可能报错或静默丢弃 | ⚠️ |

---

## 七、修复记录

本验证任务中修复了以下问题：
1. **P06 i18n 缺失**：补充 zh-CN 和 en-US 各 11 个 key
2. **部门下拉无数据**：新增 `loadDeptOptions()` 从 `/api/system/dept/tree` 加载部门列表（扁平化树结构）
