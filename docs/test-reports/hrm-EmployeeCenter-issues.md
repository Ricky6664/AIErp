# 员工中心P06主从表单页 — 问题清单与修复方案

> **任务编号**：P0-012-002-003-001-002
> **发现日期**：2026-06-08
> **发现人**：W10

---

## 本次已修复

### ✅ 修复 #1：P06 新增 i18n key 缺失

| 属性 | 值 |
|------|-----|
| **严重程度** | 🟡 中等 |
| **类型** | i18n 遗漏 |
| **位置** | `erp-ai-web/src/i18n/locales/zh-CN/common.ts`、`en-US/common.ts` |
| **现象** | 11 个 P06 新增 key 在模板中使用但未定义（idCardPlaceholder, positionPlaceholder, baseInfo, archiveInfo, archiveList, addArchive, archiveEducation, archiveMajor, archiveSchool, archiveEmergencyContact, archiveBankCardNo），英文环境页面显示 key 路径而非实际文本 |
| **修复** | 已补充 zh-CN 和 en-US 各 11 个 key |

### ✅ 修复 #2：部门筛选下拉无数据源

| 属性 | 值 |
|------|-----|
| **严重程度** | 🟡 中等 |
| **类型** | 功能缺陷 |
| **位置** | `erp-ai-web/src/views/hrm/employeecenter/index.vue:476` |
| **现象** | `deptOptions` 初始化为空数组，从未调用 API 填充，部门筛选下拉永远为空 |
| **修复** | 新增 `loadDeptOptions()` 函数，调用 `getDeptTree()` API (`/api/system/dept/tree`)，扁平化树结构填充下拉选项；在 `onMounted` 中调用 |

---

## 待修复问题

### 问题 A：后端不支持员工档案从表数据（🔴严重）

| 属性 | 值 |
|------|-----|
| **严重程度** | 🔴 严重 |
| **类型** | 前后端不匹配 |
| **位置** | 前端 `hrm-employee.ts:54` + `employeecenter/index.vue:636`；后端 `EmployeeCreateDTO.java`、`EmployeeServiceImpl.java` |
| **现象** | 前端 `createEmployeeApi`/`updateEmployeeApi` 提交 `{ ...formData, archives: archiveList.value }`，但后端 `EmployeeCreateDTO` 和 `EmployeeUpdateDTO` 均无 `archives` 字段。Spring Boot 默认 Jackson 配置 `DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES=false` 时会静默丢弃 archives 数据；若为 `true` 则直接报 400 错误 |
| **影响** | **员工档案从表（学历/专业/毕业院校/紧急联系人/银行卡号）数据将永久丢失，无法保存到数据库** |

**修复方案**（建议下一个后端任务处理）：

1. 后端 `EmployeeCreateDTO` 新增 `private List<EmployeeArchiveDTO> archives` 字段
2. 创建 `EmployeeArchiveEntity`（对应 `hrm_employee_archive` 表）
3. 创建 `EmployeeArchiveMapper`
4. `EmployeeServiceImpl.create/update` 方法中添加 `@Transactional` 级联保存/更新 archives
5. `EmployeeVO` 新增 `private List<EmployeeArchiveVO> archives` 字段
6. `EmployeeServiceImpl.getById/pageList` 查询员工时连带查询 archives 列表

---

### 问题 B：统计卡片数据源不准确（数据准确性问题）

| 属性 | 值 |
|------|-----|
| **严重程度** | 🟡 中等 |
| **类型** | 设计缺陷 |
| **位置** | `erp-ai-web/src/views/hrm/employeecenter/index.vue:536-548` |
| **现象** | `updateStats()` 从 `tableData.value`（当前页数据）计算统计值。`stats.active` 只统计当前页在职人数，翻页时数字会变化 |
| **影响** | 统计卡片显示不准确，翻页时数字会变化 |
| **修复方案** | 后端 `pageList` 响应增加全局统计字段，或调用专门统计 API |

---

### 问题 C：表单校验与提示未国际化（i18n 遗漏）

| 属性 | 值 |
|------|-----|
| **严重程度** | 🟢 低 |
| **类型** | i18n 遗漏 |
| **位置** | `erp-ai-web/src/views/hrm/employeecenter/index.vue:494-511` |
| **现象** | `formRules` 中 message 使用硬编码中文（'工号不能为空'、'姓名最长20个字符'、'身份证号格式不正确' 等），ElMessage 提示（'加载员工列表失败'、'删除成功' 等）硬编码中文，删除确认弹窗文本硬编码中文 |
| **影响** | 英文环境下仍显示中文提示 |
| **修复方案** | formRules 使用 `i18n.t()` 函数式写法，ElMessage 使用 `i18n.t()` 获取文本 |

---

### 问题 D：岗位字段使用自由输入而非选择器

| 属性 | 值 |
|------|-----|
| **严重程度** | 🟢 低 |
| **类型** | 设计建议 |
| **位置** | `erp-ai-web/src/views/hrm/employeecenter/index.vue:329-334` |
| **现象** | `positionId` 使用 `el-input` 自由输入，而非下拉选择器。部门岗位关联校验（岗位需属于所选部门）无法在前端实现 |
| **影响** | 用户可输入不存在的岗位ID；业务规则"岗位需属于所选部门"无法在前端约束 |
| **修复方案** | 替换为 `el-select`，根据所选 `departmentId` 动态加载该部门的岗位列表 |

---

## 汇总

| 严重程度 | 数量 | 问题编号 |
|---------|:---:|---------|
| 🔴 严重 | 1 | A（后端缺失 archives 支持） |
| 🟡 中等 | 1 | B（统计卡片数据源） |
| 🟢 低 | 2 | C（i18n），D（岗位选择器） |
| ✅ 已修复 | 2 | #1（i18n缺失），#2（部门下拉） |
