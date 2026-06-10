# 财务基础设置工作台 问题清单与修复方案

> **验证任务**：P0-011-002-001-001-002  
> **验证人员**：W6（初审 W4，2026-06-07；复审 W6，2026-06-08）

## 问题列表

| 序号 | 严重程度 | 问题描述 | 状态 |
|:---:|:-------:|---------|:---:|
| 1 | 🔴 阻塞 | 缺少 `/api/finance/workbench` Controller 端点 | ✅ 已修复 |
| 2 | 🟡 建议 | 饼图空数据 placeholder 硬编码中文 '暂无数据' | ⚠️ 可忽略 |
| 3 | 🟡 建议 | loadData 中 ElMessage.error 硬编码中文 '加载工作台数据失败' | ⚠️ 可忽略 |

## 问题 1：缺少 Controller 端点（已修复）

### 问题描述

- 后端存在 `FinanceWorkbenchAggregateServiceImpl.getWorkbenchData()` 方法
- 前端 API 调用 `GET /api/finance/workbench`
- W4 初审时没有 Controller 将 HTTP 路径映射到该方法
- 运行时前端请求将返回 404

### 修复方案

已创建 `FinanceWorkbenchController.java`（`src/main/java/com/erp/finance/controller/FinanceWorkbenchController.java`）：

```java
@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
@Tag(name = "财务基础设置工作台")
public class FinanceWorkbenchController {
    private final FinanceWorkbenchAggregateServiceImpl workbenchService;

    @GetMapping("/workbench")
    public RT<FinanceWorkbenchAggregateVO> workbench() {
        return RT.ok(workbenchService.getWorkbenchData());
    }
}
```

### 验证结果（W6 复审）

- Controller 文件存在，`mvn compile` → BUILD SUCCESS
- 路径匹配：前端 `/api/finance/workbench` === 后端 `@RequestMapping("/api/finance")` + `@GetMapping("/workbench")`
- VO 字段与前端 `FinanceWorkbenchVO` 接口 9 个字段一一对应

## 问题 2：饼图 placeholder 硬编码中文（可忽略）

### 问题描述

`renderDistChart()` 中饼图无数据时的兜底文案为硬编码中文：

```typescript
data: pieData.length > 0 ? pieData : [{ name: '暂无数据', value: 0 }]
```

### 建议

改为 `$t('common.noData')`。该占位仅在 accountTypeDistribution 为空对象时显示（非正常场景），影响极小，不阻塞验证。

## 问题 3：ElMessage.error 硬编码中文（可忽略）

### 问题描述

`loadData()` catch 块中错误提示为硬编码中文：

```typescript
ElMessage.error('加载工作台数据失败')
```

### 建议

改为 `ElMessage.error($t('finance.workbench.loadError'))` 并添加对应 i18n 词条。该错误仅在 API 完全不可用时触发，且 el-result 组件已提供中文错误提示，影响极小，不阻塞验证。
