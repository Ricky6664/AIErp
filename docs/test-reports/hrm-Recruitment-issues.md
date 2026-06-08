# 招聘管理P04单一列表页 — 问题清单与修复方案

> **任务编号**：P0-012-002-005-001-002
> **验证日期**：2026-06-08
> **验证工人**：W10

---

## 问题清单

### 问题1：RecruitmentController缺失 🔴 阻塞

**类型**：Backend / Missing

**描述**：后端无 `RecruitmentController.java`。Service层(`IRecruitmentService` + `RecruitmentServiceImpl`)、Mapper层(`RecruitmentMapper`)、Entity/DTO/VO完整，但缺少Controller暴露REST端点。前端所有API调用将返回404。

**影响范围**：全部6个API端点不可用

**修复方案**：
```java
// 创建 src/main/java/com/erp/hrm/controller/RecruitmentController.java
@RestController
@RequestMapping("/api/hrm/recruitment")
@RequiredArgsConstructor
@Tag(name = "招聘管理")
public class RecruitmentController {
    private final IRecruitmentService recruitmentService;

    @GetMapping
    @Operation(summary = "分页查询招聘列表")
    public RT<PageResult<RecruitmentVO>> pageList(@Valid RecruitmentQueryDTO query) {
        return RT.ok(recruitmentService.pageList(query));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询招聘详情")
    public RT<RecruitmentVO> getById(@PathVariable Long id) {
        return RT.ok(recruitmentService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增招聘")
    public RT<RecruitmentVO> create(@Valid @RequestBody RecruitmentCreateDTO dto) {
        return RT.ok(recruitmentService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新招聘")
    public RT<RecruitmentVO> update(@PathVariable Long id, @Valid @RequestBody RecruitmentUpdateDTO dto) {
        return RT.ok(recruitmentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除招聘")
    public RT<Void> delete(@PathVariable Long id) {
        recruitmentService.delete(id);
        return RT.ok();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新招聘状态")
    public RT<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        // 需要在Service层新增updateStatus方法
        recruitmentService.updateStatus(id, body.get("status"));
        return RT.ok();
    }
}
```

**参考文件**：`src/main/java/com/erp/hrm/controller/EmployeeController.java`（同模块参考模板）

---

### 问题2：前后端查询DTO字段不匹配 🔴 阻塞

**类型**：API / Contract mismatch

**描述**：前端 `RecruitmentQueryDTO` 与后端 `RecruitmentQueryDTO` 字段名不一致。

| 前端发送字段 | 后端接收字段 | 说明 |
|-------------|-------------|------|
| `positionName` (String) | `positionId` (Long) | 类型和名称都不匹配 |
| `departmentName` (String) | `departmentId` (Long) | 类型和名称都不匹配 |
| — | `keyword` (String) | 前端未发送但后端支持关键词搜索 |

**修复方案**：
- **方案A（推荐）**：修改后端 `RecruitmentQueryDTO`，将 `departmentId`/`positionId`/`keyword` 改为 `departmentName`/`positionName`/`keyword`，并修改ServiceImpl中对应的LambdaQueryWrapper条件
- **方案B**：修改前端，发送 `departmentId`/`positionId`/`keyword`，前端需增加部门/岗位下拉选择器

**影响文件**：
- `src/main/java/com/erp/hrm/dto/RecruitmentQueryDTO.java`
- `src/main/java/com/erp/hrm/service/impl/RecruitmentServiceImpl.java` (pageList方法)
- 或 `erp-ai-web/src/api/modules/hrm-recruitment.ts`
- 或 `erp-ai-web/src/views/hrm/recruitment/index.vue`

---

### 问题3：Service层缺少updateStatus方法 🔴 阻塞

**类型**：Backend / Missing method

**描述**：前端 `updateRecruitmentStatusApi` 调用 `PUT /api/hrm/recruitment/{id}/status`，但 `IRecruitmentService` 接口和 `RecruitmentServiceImpl` 实现类中均无 `updateStatus` 方法。

**修复方案**：在 `IRecruitmentService` 中添加方法声明，在 `RecruitmentServiceImpl` 中添加实现：
```java
@Transactional(rollbackFor = Exception.class)
void updateStatus(Long id, String status);
```

**影响文件**：
- `src/main/java/com/erp/hrm/service/IRecruitmentService.java`
- `src/main/java/com/erp/hrm/service/impl/RecruitmentServiceImpl.java`

---

### 问题4：状态切换仅处理2态 🟡 中等

**类型**：Frontend / Logic

**描述**：`handleToggleStatus` 函数仅处理 `recruiting` ↔ `cancelled` 切换。当状态为 `completed` 时，会被错误切换为 `recruiting`（不符合业务预期：已完成状态通常不可逆）。

**当前逻辑**：
```typescript
const newStatus = row.recruitStatus === 'recruiting' ? 'cancelled' : 'recruiting'
```

**修复方案**：增加 `completed` 状态保护：
```typescript
if (row.recruitStatus === 'completed') {
  ElMessage.warning('已完成的招聘不可修改状态')
  return
}
const newStatus = row.recruitStatus === 'recruiting' ? 'cancelled' : 'recruiting'
```

**影响文件**：`erp-ai-web/src/views/hrm/recruitment/index.vue`

---

### 问题5：状态选项label未国际化 🟡 中等

**类型**：Frontend / i18n

**描述**：`statusOptions` 数组中 label 硬编码中文，未使用 `$t()` 国际化函数：
```typescript
const statusOptions = ref([
  { value: 'recruiting', label: '招聘中' },     // 应为 computed + $t()
  { value: 'completed', label: '已完成' },      // 同上
  { value: 'cancelled', label: '已取消' }       // 同上
])
```

**修复方案**：改为 computed 属性使用 computed + useI18n：
```typescript
import { useI18n } from 'vue-i18n'
const { t } = useI18n()
const statusOptions = computed(() => [
  { value: 'recruiting', label: t('hrm.recruitment.statusRecruiting') },
  { value: 'completed', label: t('hrm.recruitment.statusCompleted') },
  { value: 'cancelled', label: t('hrm.recruitment.statusCancelled') }
])
```

**影响文件**：`erp-ai-web/src/views/hrm/recruitment/index.vue`

---

## 修复优先级

| 优先级 | 问题编号 | 说明 |
|:---:|:---:|------|
| P0 | 问题1 | 创建RecruitmentController |
| P0 | 问题2 | 统一前后端查询DTO字段 |
| P0 | 问题3 | Service层新增updateStatus方法 |
| P1 | 问题4 | 状态切换增加completed保护 |
| P2 | 问题5 | 状态选项label国际化 |

**P0问题修复后可实现前后端联调通过。**
