# 凭证字P07单一表单页 - 问题清单与修复方案

> **任务编号**：P0-011-002-009-001-002
> **验证日期**：2026-06-08
> **验证人员**：W4 (AI Worker)

---

## 问题清单

### 问题 #1 — 任务文档路径与实际项目目录不一致（文档问题，非代码Bug）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🟡 低（不影响功能） |
| 类型 | 文档不一致 |
| 状态 | ⚠️ 已知，无需修复代码 |

**描述**：
任务文档 `P0-011-002-009-001-001` Section 6 交付物清单中指定的文件路径为：
- `erp-ui/src/views/finance/voucherword/index.vue`
- `erp-ui/src/api/finance/voucherword.ts`

但实际项目前端目录名为 `erp-ai-web`，实际文件路径为：
- `erp-ai-web/src/views/finance/voucherword/index.vue`
- `erp-ai-web/src/api/modules/finance-voucherword.ts`

**修复方案**：更新任务文档中的路径引用，将 `erp-ui` 改为 `erp-ai-web`，将 `api/finance/` 改为 `api/modules/finance-`。此为文档维护任务，建议在后续文档整理阶段统一处理。

---

### 问题 #2 — 项目存在预存TypeScript编译错误（预存问题，非本次引入）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🟡 低（不影响凭证字页面） |
| 类型 | 预存编译错误 |
| 状态 | ⚠️ 已知，需其他任务修复 |

**描述**：
执行 `pnpm build` 时 `vue-tsc -b` 发现以下文件存在类型错误：
- `src/components/action-bar/HeaderToolbar.vue` — button type 类型不匹配
- `src/components/detail-table/HeaderToolbar.vue` — button type 类型不匹配
- `src/components/edit-table/index.vue` — Columns type 类型不匹配
- `src/components/basic-input/__tests__/ErpFieldRenderer.test.ts` — 多处类型断言错误
- `src/components/page-base/__tests__/PageP05TreeList.test.ts` — wrapper props 类型错误
- `src/components/page-base/__tests__/PageP06MasterForm.test.ts` — wrapper props 类型错误

**影响范围**：以上错误均在 `components/` 目录下的预存文件中，与凭证字页面（`views/finance/voucherword/`）和凭证字 API 层（`api/modules/finance-voucherword.ts`）完全无关。

**修复方案**：需要独立的代码修复任务处理这些预存问题。凭证字页面代码本身通过类型检查。

---

## 问题汇总

| 序号 | 问题 | 严重程度 | 是否需要修复 | 修复归属 |
|:---:|------|:---:|:---:|------|
| 1 | 任务文档路径不一致 | 🟡 低 | 否（文档维护） | 文档任务 |
| 2 | 预存TypeScript编译错误 | 🟡 低 | 是（非本次范围） | 独立修复任务 |

**结论**：凭证字P07页面代码无功能问题、无类型错误、API契约对齐正确。发现的问题均非本次任务引入，不影响功能验收。
