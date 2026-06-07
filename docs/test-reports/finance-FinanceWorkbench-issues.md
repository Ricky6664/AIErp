# 财务基础设置工作台 — 问题清单与修复方案

> **任务编号**: P0-011-002-001-001-002
> **验证人**: W6
> **日期**: 2026-06-07

---

## 问题列表

### I-01 [CRITICAL] 后端 FinanceWorkbenchController 缺失

**现象**: 项目中有 `FinanceWorkbenchAggregateServiceImpl.java` 服务实现类，但没有对应的 Controller 类。前端 `getFinanceWorkbenchApi()` 调用 `GET /api/finance/workbench` 将返回404。

**影响**: 财务工作台页面完全不可用，数据无法加载，仅展示错误状态。

**根因**: P0-011模块任务拆解中，P0-011-001-006（财务基础设置工作台聚合数据接口）只包含Service层实现，未安排Controller层任务。

**修复方案**: 创建 `FinanceWorkbenchController.java`，调用 `FinanceWorkbenchAggregateServiceImpl.getWorkbenchData()`：

```
路径: src/main/java/com/erp/finance/controller/FinanceWorkbenchController.java
```

参考接口规格:
| 方法 | HTTP | 路径 | 说明 |
|------|------|------|------|
| getWorkbenchData | GET | /api/finance/workbench | 获取工作台聚合数据 |

Controller核心逻辑:
```java
@RestController
@RequestMapping("/api/finance")
public class FinanceWorkbenchController {
    private final FinanceWorkbenchAggregateServiceImpl workbenchService;

    @GetMapping("/workbench")
    public R<FinanceWorkbenchAggregateVO> getWorkbenchData() {
        return R.ok(workbenchService.getWorkbenchData());
    }
}
```

> 注：此修复需由后续任务或模块收尾阶段统一处理。

---

### I-02 [MEDIUM] 无防重复请求机制

**现象**: `loadData()` 函数在 loading 期间如果用户快速点击刷新按钮，会发起重复请求。虽然有 `loading` 状态显示，但未在代码层面拦截。

**影响**: 网络较慢时可能出现竞态条件，后到的旧数据覆盖先到的新数据。

**修复方案**:
在 `loadData()` 开头增加防重复检查：
```typescript
let loadingRequest: Promise<void> | null = null

async function loadData(): Promise<void> {
  if (loadingRequest) return  // 已有进行中的请求
  loading.value = true
  error.value = false
  try {
    loadingRequest = (async () => {
      data.value = await getFinanceWorkbenchApi()
      await nextTick()
      renderCharts()
    })()
    await loadingRequest
  } catch { ... }
  finally {
    loading.value = false
    loadingRequest = null
  }
}
```

**修复文件**: `erp-ai-web/src/views/finance/financeworkbench/index.vue`

**优先级**: 中 — 当前交互中不易触发（刷新按钮有loading态），但在慢网络下理论存在。

---

### I-03 [LOW] 缺少组件单元测试

**现象**: `erp-ai-web/src/views/finance/financeworkbench/` 目录下无 `__tests__/` 目录，缺少组件单元测试。

**影响**: 无自动化回归覆盖，未来修改可能引入未检测到的回归问题。

**修复方案**: 创建 Vitest 组件测试，覆盖核心用例：
```
路径: erp-ai-web/src/views/finance/financeworkbench/__tests__/index.test.ts
```

建议覆盖:
- 组件正常渲染
- API调用成功时数据展示
- API调用失败时错误状态展示
- 刷新按钮触发数据重载
- 图表实例创建和销毁

**优先级**: 低 — 组件逻辑简单，手动测试可覆盖；后续统一补充测试时处理。

---

### I-04 [LOW] el-result 组件依赖确认

**现象**: 模板中使用了 `<el-result>` 组件展示错误状态。该组件在 Element Plus 2.2.0+ 版本中可用。

**验证结果**: 项目中 Element Plus 版本满足要求，`el-result` 可用。无需修复。

**状态**: ✅ 已验证通过，非问题。

---

## 问题统计

| 级别 | 数量 | 状态 |
|------|:---:|------|
| CRITICAL | 1 | 需后续任务处理（Controller缺失） |
| MEDIUM | 1 | 建议修复（防重复请求） |
| LOW | 2 | 1个待补充测试 + 1个已验证通过 |
| **合计** | **4** | **1个需修复 + 1个建议 + 2个记录** |

---

## 总结

前端页面代码质量良好，核心问题是后端Controller缺失导致API不可用（与warehouse模块情况一致，是任务拆解层面的系统性问题）。建议在P0-011模块收尾阶段统一补齐所有Controller。
