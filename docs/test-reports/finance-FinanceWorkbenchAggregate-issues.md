# FinanceWorkbenchAggregate 问题清单与修复方案

> **任务编号**：P0-011-001-006-001-002
> **日期**：2026-06-07

---

## 问题列表

| 编号 | 级别 | 问题描述 | 影响 | 修复方案 |
|:---:|:---:|---------|------|---------|
| I-01 | 建议 | `countByTenant` 方法使用 if-else 链判断 Class 类型，扩展性差 | 新增实体需修改此方法 | 改用 Map<Class<?>, Function> 或为每个实体写独立 count 方法 |
| I-02 | 建议 | `getMonthlyTrend` 将全部 Account 加载到内存再按月分组计数 | 科目数据量大时内存占用高 | 改用 SQL: `SELECT DATE_FORMAT(create_time,'%Y-%m'), COUNT(*) FROM fin_account WHERE tenant_id=? AND create_time>=? GROUP BY 1` |
| I-03 | 建议 | `getCurrentTenantId()` 中 `catch (Exception e)` 范围过宽 | 可能掩盖未预期的运行时异常 | 改为捕获 `NullPointerException | NumberFormatException` |
| I-04 | 信息 | 缺少 Controller 层，Service 未暴露为 REST API | 前端无法调用此聚合接口 | 待 Controller 任务补充 `/api/finance/workbench` GET 端点 |

---

## 修复优先级

- I-01、I-02、I-03：低优先级，不影响功能正确性，可在后续重构中处理
- I-04：依赖后续任务（Controller开发），不在本任务范围内

---

## 总结

共发现4项问题，其中0项阻塞、3项改善建议、1项依赖后续任务。当前代码可正常编译运行，功能逻辑正确。
