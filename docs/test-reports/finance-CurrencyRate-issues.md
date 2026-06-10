# 币种汇率 问题清单与修复方案

> **验证任务**：P0-011-002-002-001-002（P04 列表页验证）
> **更新日期**：2026-06-08
> **验证人**：W5
> **覆盖范围**：P04 列表页 + P07 表单页 (currencyrate 完整功能)
> **历史验证**：W6 首次发现 → W4（P07验证）补充 → W5（本验证）确认

---

## 问题列表

### ISS-1: 🔴 后端 CurrencyRateController 缺失

| 属性 | 值 |
|------|-----|
| 严重度 | 🔴 严重 |
| 状态 | ❌ 未修复 |
| 首次发现 | P0-011-002-002-001-002 (W6) |
| 确认人 | W4, W5 |
| 影响范围 | P04列表页 + P07表单页 全部 6 个 API 返回 404 |
| 根因 | 后端存在完整 Service/Mapper/Entity/DTO/VO 层，但缺少 REST Controller |

**修复方案**：

创建 `src/main/java/com/erp/finance/controller/CurrencyRateController.java`：

```java
@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
@Tag(name = "币种汇率管理")
public class CurrencyRateController {
    private final ICurrencyRateService currencyRateService;

    @GetMapping("/currency-rate")
    @Operation(summary = "分页查询币种汇率")
    public Result<PageResult<CurrencyRateVO>> pageList(@Valid CurrencyRateQueryDTO query) {
        return Result.success(currencyRateService.pageList(query));
    }

    @GetMapping("/currency-rate/{id}")
    @Operation(summary = "查询币种汇率详情")
    public Result<CurrencyRateVO> getById(@PathVariable Long id) {
        return Result.success(currencyRateService.getById(id));
    }

    @PostMapping("/currency-rate")
    @Operation(summary = "新增币种汇率")
    public Result<CurrencyRateVO> create(@Valid @RequestBody CurrencyRateCreateDTO dto) {
        return Result.success(currencyRateService.create(dto));
    }

    @PutMapping("/currency-rate/{id}")
    @Operation(summary = "修改币种汇率")
    public Result<CurrencyRateVO> update(@PathVariable Long id, @Valid @RequestBody CurrencyRateUpdateDTO dto) {
        return Result.success(currencyRateService.update(id, dto));
    }

    @DeleteMapping("/currency-rate/{id}")
    @Operation(summary = "删除币种汇率")
    public Result<Void> delete(@PathVariable Long id) {
        currencyRateService.delete(id);
        return Result.success();
    }

    @GetMapping("/currency-rate/check-code")
    @Operation(summary = "校验币种编码唯一性")
    public Result<Boolean> checkCode(@RequestParam String code) {
        return Result.success(currencyRateService.checkCurrencyCodeUnique(code));
    }
}
```

---

### ISS-2: 🔴 check-code 端点前后端均缺失

| 属性 | 值 |
|------|-----|
| 严重度 | 🔴 严重 |
| 状态 | ❌ 未修复 |
| 影响 | 表单"币种编码"异步唯一性校验静默失败（validator catch 放过） |
| 前端 | `checkCurrencyCodeApi` → GET `/finance/currency-rate/check-code` |
| 后端 | ICurrencyRateService 无 `checkCode` 方法，无对应端点 |

**修复方案**：

1. `ICurrencyRateService` 添加：`boolean checkCurrencyCodeUnique(String code);`
2. `CurrencyRateServiceImpl` 实现该方法（复用已有 validateCurrencyCodeUniqueness 逻辑但返回 boolean）
3. Controller 添加 `@GetMapping("/currency-rate/check-code")` 端点

---

### ISS-3: 🟡 启用/停用功能缺失

| 属性 | 值 |
|------|-----|
| 严重度 | 🟡 中 |
| 状态 | ❌ 未修复 |
| 首次发现 | W6 |
| 影响 | 无法软删除管控币种汇率 |

**修复方案**：

1. `fin_currency_rate` 表添加 `status` 字段 (1启用/0停用)
2. Service 添加 `updateStatus(Long id, Integer status)` 方法
3. Controller 添加 `@PutMapping("/currency-rate/{id}/status")` 端点
4. 前端表格操作列添加 `el-switch` 状态切换组件

---

### ISS-4: 🟢 debounceTimer 未在组件卸载时清理

| 属性 | 值 |
|------|-----|
| 严重度 | 🟢 低 |
| 状态 | ❌ 未修复 |
| 文件 | `erp-ai-web/src/views/finance/currencyrate/index.vue:407` |
| 影响 | 极端情况：组件销毁后 debounce 回调触发，访问已销毁的响应式数据 |

**修复方案**：

```typescript
import { onUnmounted } from 'vue'

onUnmounted(() => {
  if (debounceTimer) clearTimeout(debounceTimer)
})
```

---

### ISS-5: 🟢 前端请求路径与后端待确认

| 属性 | 值 |
|------|-----|
| 严重度 | 🟢 低 |
| 状态 | ⚠️ 待 Controller 创建后验证 |
| 说明 | 前端路径 `/finance/currency-rate` + base URL `/api` = 完整路径 `/api/finance/currency-rate`，与 VoucherWordController 的 `/api/finance/voucher-word` 模式一致 |

---

## 统计

| 严重度 | 数量 | 已修复 | 未修复 |
|:---:|:---:|:---:|:---:|
| 🔴 严重 | 2 | 0 | 2 |
| 🟡 中 | 1 | 0 | 1 |
| 🟢 低 | 2 | 0 | 2 |
| **合计** | **5** | **0** | **5** |

---

## 修复优先级

1. **立即修复** ISS-1 + ISS-2：创建 CurrencyRateController（含 check-code）— 阻塞全部前后端功能
2. **后续修复** ISS-3：启用/停用功能 — 业务完整性
3. **低优先级** ISS-4 + ISS-5：代码健壮性优化
