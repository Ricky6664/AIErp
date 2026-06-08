# HRM Workbench P02 问题清单与修复方案

> **任务编号**：P0-012-002-001-001-002
> **验证人**：W8
> **时间**：2026-06-08T20:15

---

## 发现的问题

### 问题 #1：路由未注册（已修复）

- **严重程度**：🔴 阻断
- **问题描述**：`erp-ai-web/src/router/modules/static.ts` 中缺少HRM工作台路由，导致页面无法访问
- **修复方案**：新增 `HRM_WORKBENCH` 路由导出，路径 `/hrm/workbench`，并注册到 `staticRoutes` 数组
- **修复文件**：`erp-ai-web/src/router/modules/static.ts`
- **状态**：✅ 已修复

### 问题 #2：i18n词条缺失（已修复）

- **严重程度**：🔴 阻断
- **问题描述**：页面模板使用 `$t('hrm.workbench.*')` 但 i18n 文件中无对应 `hrm.workbench` 词条，渲染时显示原始key
- **修复方案**：在 zh-CN 和 en-US 的 `common.ts` 中补充 `hrm.workbench` 完整词条(17条)
- **修复文件**：
  - `erp-ai-web/src/i18n/locales/zh-CN/common.ts`
  - `erp-ai-web/src/i18n/locales/en-US/common.ts`
- **状态**：✅ 已修复

### 问题 #3：硬编码中文字符串（已修复）

- **严重程度**：🟡 中等
- **问题描述**：script中6处硬编码中文字符串，切换英文时不变
- **影响范围**：
  - `ElMessage.error('加载工作台数据失败')` — 错误提示
  - `'新增员工'` — 折线图系列名
  - `'考勤记录'` — 折线图系列名
  - `'暂无数据'` — 饼图空数据
  - `'部门分布'` — 饼图系列名
  - `'招聘状态'` — 饼图系列名
- **修复方案**：引入 `useI18n()` composable，全部替换为 `t()` 调用
- **修复文件**：`erp-ai-web/src/views/hrm/hrmworkbench/index.vue`
- **状态**：✅ 已修复

### 问题 #4：areaStyle颜色处理脆弱（已知晓，不修复）

- **严重程度**：🟢 低
- **问题描述**：`renderLineChart` 函数使用字符串 `replace()` 生成渐变色，如 `color.replace('1)', '0.3)')`，对颜色格式变更敏感
- **建议**：后续可用 echarts 内置渐变色 API 替代
- **状态**：⚠️ 已知晓，当前不影响功能

---

## 修复统计

| 类别 | 数量 |
|------|:---:|
| 阻断问题 | 2 |
| 中等问题 | 1 |
| 低优先级 | 1 |
| 已修复 | 3 |
| 已知晓 | 1 |
