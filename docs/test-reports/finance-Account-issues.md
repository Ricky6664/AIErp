# 会计科目P07单一表单页 — 问题清单与修复方案

> **任务编号**：P0-011-002-007-001-002
> **创建日期**：2026-06-08
> **创建工人**：W6

---

## 问题列表

### 问题 #1：AccountController 缺失（阻塞级）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🔴 阻塞 |
| 发现方式 | 代码审查 |
| 问题描述 | 前端调用 `/finance/account` 系列API，但 `src/main/java/com/erp/module/finance/controller/` 目录下不存在 AccountController |
| 影响范围 | 7个API端点（列表、详情、树、创建、更新、状态切换、删除）全部不可用 |

**缺失的端点：**
| 方法 | 路径 | 前端调用 | Controller方法 |
|------|------|---------|:---:|
| GET | /finance/account | getAccountPageApi | ❌ |
| GET | /finance/account/tree | getAccountTreeApi | ❌ |
| GET | /finance/account/{id} | getAccountByIdApi | ❌ |
| POST | /finance/account | createAccountApi | ❌ |
| PUT | /finance/account/{id} | updateAccountApi | ❌ |
| PUT | /finance/account/{id}/status | updateAccountStatusApi | ❌ |
| DELETE | /finance/account/{id} | deleteAccountApi | ❌ |

**修复方案：**
创建 `src/main/java/com/erp/module/finance/controller/AccountController.java`，注入 `IAccountService`，映射上述7个端点。

---

### 问题 #2：路由未注册（阻塞级）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🔴 阻塞 |
| 发现方式 | 代码审查 |
| 问题描述 | `/finance/account` 路由未在 `erp-ai-web/src/router/modules/static.ts` 中注册 |
| 影响范围 | 用户无法通过URL导航到会计科目页面；菜单也无法自动生成 |

**修复方案：**
在 `static.ts` 添加：
```typescript
export const FINANCE_ACCOUNT: RouteRecordRaw = {
  path: '/finance/account',
  name: 'FinanceAccount',
  component: () => import('@/views/finance/account/index.vue'),
  meta: { title: '会计科目', icon: 'List', keepAlive: true }
}
```
将 `FINANCE_ACCOUNT` 添加到 `staticRoutes` 数组中。

---

### 问题 #3：IAccountService 缺少 getTree() 方法（中等）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🟡 中等 |
| 发现方式 | 前后端契约对比 |
| 问题描述 | 前端 `getAccountTreeApi()` 调用 `GET /finance/account/tree`，但 `IAccountService` 接口中无 `getTree()` 方法 |

**修复方案：**
- 在 `IAccountService` 添加 `List<AccountTreeVO> getTree()` 方法
- 在 `AccountServiceImpl` 实现：查询所有科目，使用已有 `TreeUtil.buildTree()` 构建树形结构

---

### 问题 #4：IAccountService 缺少 updateStatus() 方法（中等）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🟡 中等 |
| 发现方式 | 前后端契约对比 |
| 问题描述 | 前端 `updateAccountStatusApi()` 调用 `PUT /finance/account/{id}/status`，但 `IAccountService` 接口中无 `updateStatus()` 方法 |

**修复方案：**
- 在 `IAccountService` 添加 `void updateStatus(Long id, Integer status)` 方法
- 在 `AccountServiceImpl` 实现

---

### 问题 #5：前端 AccountVO 缺少 status 字段（低优先级）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🟢 低 |
| 发现方式 | 代码审查 |
| 问题描述 | `finance-account.ts` 中 `AccountVO` 接口未显式声明 `status: number` 字段，但页面使用 `row.status` |

**说明**：vue-tsc 编译通过（`row.status` 通过后端额外字段隐式传递），建议显式声明：
```typescript
export interface AccountVO {
  // ...existing fields...
  status: number   // 0=停用 1=启用
}
```

---

### 问题 #6：表单 isForeignCurrency / isAuxiliary 类型不匹配（低优先级）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🟢 低 |
| 发现方式 | 代码审查 |
| 问题描述 | `AccountSaveDTO` 定义 `isForeignCurrency: string`，但表单使用 `el-switch` 绑定 `boolean`，提交时需要手动转换为 `'true'/'false'` 字符串 |

**说明**：vue-tsc 编译通过，功能正常。建议将 `AccountSaveDTO` 中 `isForeignCurrency` 和 `isAuxiliary` 改为 `boolean` 类型以消除类型不匹配。

---

## 总结

| 严重度 | 数量 | 说明 |
|:---:|:---:|------|
| 🔴 阻塞 | 2 | Controller缺失 + 路由未注册 |
| 🟡 中等 | 2 | getTree() + updateStatus() 方法缺失 |
| 🟢 低 | 2 | VO类型声明 + 布尔类型不匹配 |
