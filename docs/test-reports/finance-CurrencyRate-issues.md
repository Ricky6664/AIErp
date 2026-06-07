# 币种汇率 问题清单与修复方案

> **验证任务**：P0-011-002-003-001-002（P07单一表单页验证）
> **更新日期**：2026-06-08
> **验证人**：W4
> **覆盖范围**：P07 表单页 + P04 列表页 (currencyrate 完整功能)

---

## 问题列表

### ISS-1: 🔴 后端 CurrencyRateController 缺失

| 属性 | 值 |
|------|-----|
| 严重度 | 🔴 严重 |
| 状态 | ❌ 未修复 |
| 首次发现 | P0-011-002-002-001-002 (W6) |
| 影响范围 | P04列表页 + P07表单页 全部 API 调用返回 404 |
| 根因 | 后端存在完整 Service/Mapper/Entity/DTO/VO 层，但缺少 REST Controller 暴露端点 |

**修复方案**：

创建 `src/main/java/com/erp/finance/controller/CurrencyRateController.java`，参考 `VoucherWordController.java` 模式：

```java
@RestController
@RequestMapping("/api/finance")
@RequiredArgsConstructor
@Tag(name = "币种汇率管理", description = "币种汇率CRUD接口")
public class CurrencyRateController {
    private final ICurrencyRateService currencyRateService;

    @GetMapping("/currency-rate")          // 分页列表
    @GetMapping("/currency-rate/{id}")     // 详情
    @PostMapping("/currency-rate")         // 新增
    @PutMapping("/currency-rate/{id}")     // 修改
    @DeleteMapping("/currency-rate/{id}")  // 删除
    @GetMapping("/currency-rate/check-code") // 编码唯一性校验
}
```

---

### ISS-2: 🔴 check-code 端点前后端均缺失

| 属性 | 值 |
|------|-----|
| 严重度 | 🔴 严重 |
| 状态 | ❌ 未修复 |
| 影响 | 表单"币种编码"异步唯一性校验静默失败（validator catch 放过，不阻塞提交） |
| 前端 | `checkCurrencyCodeApi` → GET `/finance/currency-rate/check-code?code=XXX` |
| 后端 | ICurrencyRateService 无 `checkCode` 方法，Controller 不存在 |

**修复方案**：

1. `ICurrencyRateService` 添加方法：`boolean checkCurrencyCodeUnique(String code);`
2. `CurrencyRateServiceImpl` 实现：调用现有 `validateCurrencyCodeUniqueness` 逻辑但返回 boolean
3. `CurrencyRateController` 添加端点：`@GetMapping("/currency-rate/check-code")`

---

### ISS-3: 🟡 ISS-1 修复后启用/停用功能仍需补充

| 属性 | 值 |
|------|-----|
| 严重度 | 🟡 中 |
| 状态 | ❌ 未修复 |
| 首次发现 | P0-011-002-002-001-002 (W6) |
| 影响 | 币种汇率无启用/停用切换，无法软删除管控 |

**修复方案**：

1. Entity 添加 `status` 字段 (1启用/0停用)
2. Service 添加 `updateStatus(Long id, Integer status)` 方法
3. Controller 添加 `@PutMapping("/currency-rate/{id}/status")` 端点
4. 前端表格操作列添加 `el-switch` 组件

---

### ISS-4: 🟢 debounceTimer 未在组件卸载时清理

| 属性 | 值 |
|------|-----|
| 严重度 | 🟢 低 |
| 状态 | ❌ 未修复 |
| 首次发现 | P0-011-002-002-001-002 (W6) |
| 文件 | `erp-ai-web/src/views/finance/currencyrate/index.vue:407` |
| 影响 | 极端情况：组件销毁后 debounce 回调触发 `handleSearch`，访问已销毁的响应式数据 |

**修复方案**：

```typescript
import { onUnmounted } from 'vue'

onUnmounted(() => {
  if (debounceTimer) clearTimeout(debounceTimer)
})
```

---

### ISS-5: 🟢 前端请求路径与后端实际路径需确认对齐

| 属性 | 值 |
|------|-----|
| 严重度 | 🟢 低 |
| 状态 | ⚠️ 待 Controller 创建后验证 |
| 说明 | 前端 API 调用 `/finance/currency-rate`，VITE_API_BASE_URL=`/api`，拼接后为 `/api/finance/currency-rate`。VoucherWordController 使用 `@RequestMapping("/api/finance")` + `@GetMapping("/voucher-word")` = `/api/finance/voucher-word`，模式一致。Controller 创建后需验证路径完全匹配。 |

---

## 统计

| 严重度 | 数量 | 已修复 | 未修复 |
|:---:|:---:|:---:|:---:|
| 🔴 严重 | 2 | 0 | 2 |
| 🟡 中 | 1 | 0 | 1 |
| 🟢 低 | 2 | 0 | 2 |
| **合计** | **5** | **0** | **5** |

---

## 修复优先级建议

1. **立即修复** ISS-1 + ISS-2：创建 CurrencyRateController（含 check-code 端点）— 阻塞全部功能
2. **后续修复** ISS-3：启用/停用功能 — 业务完整性
3. **低优先级** ISS-4 + ISS-5：代码健壮性优化
