# 会计科目树形列表页 — 问题清单与修复方案

> **任务编号**：P0-011-002-006-001-002
> **创建日期**：2026-06-08
> **创建工人**：W5

---

## 问题列表

### 问题 #1：AccountController 缺失（阻塞级）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🔴 阻塞 |
| 发现方式 | 代码审查 |
| 问题描述 | 前端调用 `/finance/account` 系列API，但后端不存在对应的AccountController |
| 影响范围 | 所有7个API端点（列表、详情、树、创建、更新、状态切换、删除）均不可用 |

**缺失的端点清单：**
| 方法 | 路径 | 前端调用 |
|------|------|---------|
| GET | /finance/account | getAccountPageApi |
| GET | /finance/account/tree | getAccountTreeApi |
| GET | /finance/account/{id} | getAccountByIdApi |
| POST | /finance/account | createAccountApi |
| PUT | /finance/account/{id} | updateAccountApi |
| PUT | /finance/account/{id}/status | updateAccountStatusApi |
| DELETE | /finance/account/{id} | deleteAccountApi |

**修复方案：**
1. 创建 `com.erp.module.finance.controller.AccountController`
2. 注入 IAccountService，映射上述7个端点
3. 在 IAccountService 中新增 `getTree()` 方法返回树形结构
4. 在 IAccountService 中新增 `updateStatus(Long id, Integer status)` 方法

---

### 问题 #2：路由未注册（阻塞级）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🔴 阻塞 |
| 发现方式 | 代码审查 |
| 问题描述 | `/finance/account` 路由未在 `static.ts` 中注册，页面无法导航访问 |
| 影响范围 | 用户无法通过URL访问会计科目列表页 |

**修复方案：**
在 `erp-ai-web/src/router/modules/static.ts` 添加：
```typescript
export const FINANCE_ACCOUNT: RouteRecordRaw = {
  path: '/finance/account',
  name: 'FinanceAccount',
  component: () => import('@/views/finance/account/index.vue'),
  meta: { title: '会计科目', icon: 'List', keepAlive: true }
}
```
同时将 FINANCE_ACCOUNT 导出并加入到静态路由数组中。

---

### 问题 #3：后端Service缺少 tree() 和 updateStatus() 方法

| 属性 | 值 |
|------|-----|
| 严重程度 | 🟡 中等 |
| 发现方式 | 前后端契约对比 |
| 问题描述 | IAccountService 接口缺少 `getTree()` 和 `updateStatus()` 方法 |

**修复方案：**
- 在 IAccountService 添加 `TreeVO getTree()` 方法，构建账户树形结构
- 在 IAccountService 添加 `void updateStatus(Long id, Integer status)` 方法
- 在 AccountServiceImpl 实现这两个方法

---

### 问题 #4：前端AccountVO缺少 status 字段（低优先级）

| 属性 | 值 |
|------|-----|
| 严重程度 | 🟢 低 |
| 发现方式 | 代码审查 |
| 问题描述 | 前端 `AccountVO` 接口未定义 `status` 字段，但页面中使用了 `row.status` |

**说明**：当前前端编译无错误（vue-tsc通过），`status` 可能通过后端返回额外字段隐式传递。建议在 `AccountVO` 中显式声明 `status: number` 以保证类型安全。

---

## 总结

| 阻塞项 | 2（Controller缺失 + 路由未注册） |
| 中等 | 1（Service方法缺失） |
| 低 | 1（VO类型声明） |
