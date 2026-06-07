# 财务基础设置工作台 问题清单与修复方案

> **验证任务**：P0-011-002-001-001-002  
> **验证人员**：W4  
> **验证时间**：2026-06-07

## 问题列表

| 序号 | 严重程度 | 问题描述 | 状态 |
|:---:|:-------:|---------|:---:|
| 1 | 🔴 阻塞 | 缺少 `/api/finance/workbench` Controller 端点 | ✅ 已修复 |
| 2 | 🟡 建议 | 饼图空数据时显示的 placeholder 值缺少国际化 | ⚠️ 可忽略 |

## 问题 1：缺少 Controller 端点（已修复）

### 问题描述

- 后端存在 `FinanceWorkbenchAggregateServiceImpl.getWorkbenchData()` 方法
- 前端 API 调用 `GET /api/finance/workbench`
- 但没有 Controller 将 HTTP 路径映射到该方法
- 运行时前端请求将返回 404

### 修复方案

创建 `FinanceWorkbenchController.java`：

```java
// 文件：src/main/java/com/erp/finance/controller/FinanceWorkbenchController.java
@RestController
@RequestMapping("/api/finance")
public class FinanceWorkbenchController {
    private final FinanceWorkbenchAggregateServiceImpl workbenchService;

    @GetMapping("/workbench")
    public RT<FinanceWorkbenchAggregateVO> workbench() {
        return RT.ok(workbenchService.getWorkbenchData());
    }
}
```

### 验证结果

- `mvn compile` → BUILD SUCCESS
- 路径匹配：前端 `/api/finance/workbench` === 后端 `@RequestMapping("/api/finance")` + `@GetMapping("/workbench")`

## 问题 2：饼图 placeholder 缺少国际化（可忽略）

### 问题描述

`renderDistChart()` 方法中饼图无数据时的兜底文案 `'暂无数据'` 为硬编码中文：

```typescript
data: pieData.length > 0 ? pieData : [{ name: '暂无数据', value: 0 }]
```

### 建议

改为 `$t('common.noData')` 或类似国际化 key。但该占位仅在后端完全无数据时显示（非正常场景），影响极小，不阻塞验证。
