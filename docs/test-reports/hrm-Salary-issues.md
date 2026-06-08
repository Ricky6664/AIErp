# 薪资管理 P06 主从表单页 — 问题清单与修复方案

> **关联任务**：P0-012-002-008-001-002
> **验证日期**：2026-06-09
> **严重级别**：🔴 阻塞 / 🟡 警告 / 🟢 建议

---

## 问题 #1: netSalaryPreview 类型错误 🔴→✅ 已修复

**严重级别**：🔴 阻塞
**发现位置**：`erp-ai-web/src/views/hrm/salary/index.vue:566`
**问题描述**：`netSalaryPreview` computed 通过 `.toFixed(2)` 返回 `string`，但 `formatCurrency()` 函数签名为 `(value: number | undefined | null): string`，导致 TypeScript 编译错误 TS2345。

**错误信息**：
```
src/views/hrm/salary/index.vue(366,50): error TS2345: Argument of type 'string' is not assignable to parameter of type 'number'.
```

**修复方案**（已实施）：
将 `netSalaryPreview` 的 `return` 语句从 `...toFixed(2)` 改为直接返回 `number`，由 `formatCurrency()` 统一格式化：
```diff
- return (base + overtime + bonusVal + allowanceVal + detailAdd - deduct - detailDeduct).toFixed(2)
+ return base + overtime + bonusVal + allowanceVal + detailAdd - deduct - detailDeduct
```

**状态**：✅ 已修复并验证编译通过

---

## 问题 #2: tableRef/detailTableRef TS6133 误报 🟢

**严重级别**：🟢 建议（非功能问题）
**发现位置**：`erp-ai-web/src/views/hrm/salary/index.vue:481-482`
**问题描述**：vue-tsc 报告 `tableRef` 和 `detailTableRef` "已声明但从未读取"。

**原因**：这两个 ref 在 `<script setup>` 中声明，在 `<template>` 中通过 `ref="tableRef"` / `ref="detailTableRef"` 使用。vue-tsc 无法检测 template 中的 ref 绑定使用。

**修复方案**：无需修复。属 vue-tsc 已知局限，不影响运行时行为。可考虑在 `tsconfig.json` 中关闭 `noUnusedLocals` 或升级 vue-tsc。

**状态**：🟢 已知问题，不阻塞

---

## 修复总结

| # | 问题 | 严重度 | 状态 |
|:---:|------|:---:|:---:|
| 1 | netSalaryPreview 类型错误 | 🔴 | ✅ 已修复 |
| 2 | tableRef/detailTableRef TS6133 误报 | 🟢 | 已知局限 |
