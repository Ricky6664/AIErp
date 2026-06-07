# 银行账户 P04 列表页 — 问题清单与修复方案

> **验证任务**: P0-011-002-004-001-002
> **验证日期**: 2026-06-07
> **关联报告**: finance-BankAccount-frontend-test.md

---

## 问题列表

### 问题 #1: 缺少 BankAccountController（严重）

| 属性 | 值 |
|------|-----|
| 严重级别 | 🔴 严重 |
| 影响范围 | 前端所有 API 调用 404 |
| 根因 | 前置任务 P0-011-002-004-001-001 仅交付了前端代码，未包含后端 Controller |

**现象**:
- 前端 `finance-bankaccount.ts` 调用 `GET/POST/PUT/DELETE /finance/bank-account` 
- 后端无对应 Controller，端点不存在
- 页面加载即报 404，列表为空

**修复方案**:
创建 `BankAccountController.java`，路径建议：
`src/main/java/com/erp/module/finance/controller/BankAccountController.java`

需暴露的端点：
```
GET    /api/finance/bank-account?pageNum=&pageSize=&accountName=&bankName=&status=
GET    /api/finance/bank-account/{id}
POST   /api/finance/bank-account
PUT    /api/finance/bank-account/{id}
PUT    /api/finance/bank-account/{id}/status
DELETE /api/finance/bank-account/{id}
```

**关联任务**: 需在 P0-011 模块中新增 Controller 任务，或由后续任务补充。

---

### 问题 #2: 表格缺少"币种"列（中等）

| 属性 | 值 |
|------|-----|
| 严重级别 | 🟡 中等 |
| 影响范围 | 用户无法看到账户关联的币种信息 |

**现象**:
- 任务规格 §5.2 明确要求表格列包含"币种"
- 实际 vxe-table 列配置无 currencyId/币种 列
- 用户无法在列表中区分不同币种账户

**修复方案**:
在 vxe-table 中增加币种列，需将 currencyId 映射为币种名称：
```html
<vxe-column field="currencyName" title="币种" width="100" align="center" />
```
数据加载后需关联查询币种名称（或后端 VO 中 join 返回 currencyName）。

---

### 问题 #3: 币种选项硬编码（中等）

| 属性 | 值 |
|------|-----|
| 严重级别 | 🟡 中等 |
| 影响范围 | 新增/编辑表单中币种下拉选项 |

**现象**:
- `index.vue:290-297` — currencyOptions 硬编码了 6 种币种
- 如果后端币种数据变更，前端不会同步更新
- 与前序币种汇率模块不联动

**修复方案**:
改为从 API 获取币种列表（可复用币种汇率模块的接口）：
```typescript
import { getCurrencyOptionsApi } from '@/api/modules/finance-currencyrate'
const currencyOptions = ref([])
onMounted(async () => {
  currencyOptions.value = await getCurrencyOptionsApi()
})
```

---

### 问题 #4: 操作按钮缺少权限指令（中等）

| 属性 | 值 |
|------|-----|
| 严重级别 | 🟡 中等 |
| 影响范围 | 未控制操作按钮的权限可见性 |

**现象**:
- 新增、编辑、删除、启用/停用按钮均未使用 `v-permission` 指令
- 无权限用户仍可看到操作按钮（点击后后端会拒绝，但体验不佳）

**修复方案**:
为操作按钮添加权限指令，参考全局规范-权限体系规范：
```html
<el-button v-permission="['finance:bankaccount:add']" ...>新增</el-button>
<el-button v-permission="['finance:bankaccount:edit']" ...>编辑</el-button>
<el-button v-permission="['finance:bankaccount:delete']" ...>删除</el-button>
```

---

### 问题 #5: 缺少国际化 `$t()` 包裹（低）

| 属性 | 值 |
|------|-----|
| 严重级别 | 🟢 低 |
| 影响范围 | 多语言环境下文本无法切换 |

**现象**:
- 页面所有中文文本均硬编码（标签、placeholder、提示消息）
- 未使用 `$t()` 国际化函数

**修复方案**:
1. 在国际化文件中添加对应 key
2. 将模板中的中文替换为 `{{ $t('finance.bankaccount.xxx') }}`
3. 脚本中的 ElMessage 使用 `i18n.t()` 

---

### 问题 #6: 统计卡片仅反映当前页数据（低）

| 属性 | 值 |
|------|-----|
| 严重级别 | 🟢 低 |
| 影响范围 | 统计数字不准确（仅显示当前页的启用/停用数） |

**现象**:
- `updateStats` 函数（line 451-455）统计的是 `list`（当前页数据）
- 总记录数使用了分页 total，但启用/停用数只统计当前页
- 翻页后启用/停用统计数字会变化

**修复方案**:
方案A: 后端在分页结果中额外返回 `enabledCount`/`disabledCount`
方案B: 单独调用统计接口获取全量启用/停用计数

---

## 修复优先级

| 优先级 | 问题 | 建议处理方式 |
|:---:|------|------|
| 1 | #1 缺少 Controller | 创建新任务或由后续 Controller 任务覆盖 |
| 2 | #2 缺少币种列 | 前端补充列配置 |
| 3 | #3 币种硬编码 | 改为 API 获取 |
| 4 | #4 缺少权限指令 | 按权限规范添加 |
| 5 | #5 缺少国际化 | 后续国际化任务统一处理 |
| 6 | #6 统计不准确 | 后续优化 |
