# 凭证字 P04 单一列表页 — 问题清单与修复方案

> **测试日期**: 2026-06-08
> **测试人**: AI (W5)
> **任务编号**: P0-011-002-008-001-002

---

## 问题汇总

| 编号 | 严重程度 | 问题描述 | 状态 |
|:---:|:---:|------|:---:|
| IS-01 | CRITICAL | 缺少 VoucherWordController | ✅ 已修复 |
| IS-02 | MEDIUM | 缺少 updateStatus 方法 | ✅ 已修复 |
| IS-03 | MINOR | 统计卡片数据仅限当前页 | 🔧 建议改进 |

---

## IS-01: 缺少 VoucherWordController

**严重程度**: CRITICAL
**影响范围**: 所有前端 API 调用返回 404
**根因**: P0-011-002-008-001-001 任务未创建 Controller 层
**修复**: 创建 `src/main/java/com/erp/finance/controller/VoucherWordController.java`
- 暴露 6 个 REST 端点
- 遵循 FinanceWorkbenchController 的代码风格
- 使用 `IVoucherWordService` 接口注入
- 路由路径与前端 API 模块完全对齐

---

## IS-02: 缺少 updateStatus 方法

**严重程度**: MEDIUM
**影响范围**: 状态切换功能不可用
**根因**: Service 接口和实现中未定义 `updateStatus` 方法，但前端调用 `PUT /voucher-word/{id}/status`
**修复**:
1. `IVoucherWordService.java` — 添加 `void updateStatus(Long id, Integer status)` 方法声明
2. `VoucherWordServiceImpl.java` — 添加实现，复用已有的 `validateStatusTransition` 校验逻辑

---

## IS-03: 统计卡片数据仅限当前页

**严重程度**: MINOR
**影响范围**: 分页场景下统计数字不准确
**当前行为**: `updateStats(list, total)` 从当前页数据计算 enabled/disabled 计数
**预期行为**: 统计数据应反映全量数据
**建议方案**:
- 方案 A: 后端新增 `GET /api/finance/voucher-word/stats` 返回全量统计
- 方案 B: 前端在 pageSize 较大时关闭统计卡片
- 方案 C: 统计卡片只显示总数（已可用），移除启用/停用分项统计

**状态**: 🔧 待后续迭代修复
