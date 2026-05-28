# 全局规范-Git工作流与协作规范

> 本文档定义Git分支策略、合并规则、提交规范、PR流程、版本标签及代码审查标准，所有开发人员（含AI Agent）必须严格遵守。

---

## 1. 分支策略

### 1.1 五类分支定义

| 分支类型 | 命名格式 | 生命周期 | 说明 |
|---------|---------|---------|------|
| 主分支 | `main` | 永久 | 生产就绪代码，受保护，禁止直接推送 |
| 开发分支 | `develop` | 永久 | 开发集成分支，所有功能合并目标 |
| 发布分支 | `release/{version}` | 临时 | 发布准备分支，仅允许bugfix提交 |
| 功能分支 | `feature/{module}-{description}` | 临时 | 功能开发分支，从develop创建 |
| 热修复分支 | `hotfix/{description}` | 临时 | 紧急修复分支，从main创建 |

### 1.2 分支命名规则

| 分支类型 | 命名示例 | 说明 |
|---------|---------|------|
| feature | `feature/sale-order-export` | 模块名-功能描述，短横线分隔 |
| feature | `feature/inventory-batch-import` | 多词描述用短横线连接 |
| release | `release/1.2.0` | SemVer版本号 |
| hotfix | `hotfix/login-token-expiry` | 简短描述问题 |

### 1.3 分支创建规则

| 规则项 | 要求 |
|--------|------|
| feature分支来源 | 必须从`develop`最新代码创建 |
| release分支来源 | 必须从`develop`创建 |
| hotfix分支来源 | 必须从`main`最新tag或HEAD创建 |
| 分支创建前 | 必须先`git pull --rebase`确保源分支为最新 |
| 单人单分支 | 禁止多人共用同一feature分支 |
| 过期清理 | 已合并的feature/release/hotfix分支在合并后7天内删除 |

---

## 2. 合并方向规则

### 2.1 合并流向图

```
feature/{module}-{desc}
    │
    │ PR + Squash Merge（1 reviewer）
    ▼
develop
    │
    │ Merge Commit
    ▼
release/{version}
    │
    │ Merge Commit + Tag
    ▼
main ──────────────────┐
    │                  │
    │ Cherry-pick      │ Merge（hotfix回合）
    │ hotfix           │
    ▼                  │
develop               │
                       │
hotfix/{desc} ─────────┘
    │
    │ 双Merge：→ main + → develop
```

### 2.2 合并规则明细

| 合并方向 | 合并方式 | 审批要求 | 说明 |
|---------|---------|---------|------|
| feature → develop | Squash Merge | 1 reviewer + CI通过 | 功能分支所有提交压缩为一个提交 |
| develop → release | Merge Commit | 技术负责人审批 | 保留完整提交历史 |
| release → main | Merge Commit | 2 reviewers + CI通过 | 合并后必须打Tag |
| main → develop（hotfix回合） | Cherry-pick | 1 reviewer | 仅挑选hotfix相关提交 |
| hotfix → main | Merge Commit | 2 reviewers + CI通过 | 紧急修复合入生产 |
| hotfix → develop | Merge Commit | 1 reviewer | hotfix同步到开发分支 |

### 2.3 合并禁止项

| 禁止行为 | 说明 |
|---------|------|
| 直接推送到`main` | 必须通过PR合并 |
| 直接推送到`develop` | 必须通过PR合并 |
| Force Push到受保护分支 | `main`和`develop`禁止force push |
| 删除受保护分支 | `main`和`develop`禁止删除 |
| 未解决冲突时合并 | 合并前必须解决所有冲突 |
| Rebase已推送的公共分支 | 禁止rebase `main`/`develop`/`release` |

---

## 3. Commit Message规范（Conventional Commits）

### 3.1 格式定义

```
<type>(<scope>): <description>

[可选 body]

[可选 footer]
```

### 3.2 Type类型

| Type | 说明 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat(sale): 新增销售订单批量导出功能` |
| `fix` | Bug修复 | `fix(inventory): 修复库存查询分页参数丢失问题` |
| `refactor` | 重构（非新功能、非修复） | `refactor(auth): 重构Token刷新逻辑` |
| `docs` | 文档变更 | `docs(sale): 更新销售订单API文档` |
| `test` | 测试相关 | `test(purchase): 补充采购入库单元测试` |
| `chore` | 构建/工具链变更 | `chore: 升级Spring Boot至3.4.2` |
| `style` | 代码格式（不影响逻辑） | `style(system): 统一缩进格式` |
| `perf` | 性能优化 | `perf(finance): 优化报表查询SQL` |
| `ci` | CI/CD配置变更 | `ci: 添加SonarQube扫描步骤` |
| `revert` | 回滚提交 | `revert: 回滚feat(sale):批量导出` |

### 3.3 Scope模块列表

| Scope | 对应模块 |
|-------|---------|
| `system` | 系统管理（用户/角色/菜单/字典/参数） |
| `auth` | 认证授权（登录/Token/权限） |
| `sale` | 销售管理（订单/合同/报价/退货） |
| `purchase` | 采购管理（订单/合同/询价/退货） |
| `inventory` | 库存管理（入库/出库/调拨/盘点） |
| `finance` | 财务管理（应收/应付/总账/报表） |
| `hr` | 人力资源（员工/考勤/薪资/招聘） |
| `oa` | 办公自动化（审批/公告/日程/文档） |
| `produce` | 生产管理（BOM/工单/排产） |
| `subcontract` | 委外管理 |
| `report` | 报表引擎 |
| `integration` | 第三方集成 |
| `engine` | 通用引擎（编码/审核/流转/审批） |
| `infra` | 基础设施（缓存/消息/日志） |

### 3.4 Commit Message示例

```
feat(sale): 新增销售订单批量审核功能

- 支持勾选多条订单一次性审核
- 增加审核前置校验（客户信用额度、库存可用量）
- 审核结果通过WebSocket实时推送前端

关联任务: TASK-1234
```

```
fix(inventory): 修复批次号重复导致出库失败

Redis序号递增未加租户隔离，跨租户并发时产生相同批次号。
修复方案：Redis Key增加tenantId前缀。

修复: INVENTORY-BUG-0525
```

### 3.5 Commit规范强制执行

| 规则项 | 要求 |
|--------|------|
| 提交前检查 | 使用`commitlint`在pre-commit hook中校验格式 |
| 描述语言 | 中文描述，简洁明确，不超过72个字符 |
| 禁止无意义提交 | 禁止`update`、`fix bug`、`修改`等模糊描述 |
| 关联任务 | 有任务编号时必须在footer标注 |
| 原子提交 | 一个提交只做一件事，禁止混合不相关变更 |

---

## 4. PR/MR流程

### 4.1 PR标题格式

```
[P{priority}-{module}] {description}
```

| 优先级 | 标识 | 说明 |
|--------|------|------|
| P0 | `[P0-sale]` | 核心必做，阻塞发布 |
| P1 | `[P1-inventory]` | 重要功能 |
| P2 | `[P2-report]` | 常规优化 |
| P3 | `[P3-system]` | 低优先级改进 |

**示例**：
- `[P0-sale] 销售订单审核流程重构`
- `[P1-inventory] 库存预警规则配置化`
- `[P2-report] 优化利润报表查询性能`

### 4.2 PR Body模板

```markdown
## What（做了什么）
简要描述本次PR的变更内容。

## Why（为什么做）
说明变更原因、解决的问题或满足的需求。

## How（怎么做的）
说明实现方案、关键设计决策。

## 关联任务
TASK-XXXX

## 变更类型
- [ ] 新功能（feat）
- [ ] Bug修复（fix）
- [ ] 重构（refactor）
- [ ] 性能优化（perf）
- [ ] 其他

## 影响范围
- [ ] 数据库变更（DDL/DML）
- [ ] API接口变更
- [ ] 配置文件变更
- [ ] 前端UI变更

## 测试说明
- [ ] 单元测试通过
- [ ] 集成测试通过
- [ ] 手动验证通过

## 截图（如适用）
（前端变更请附截图）
```

### 4.3 PR合并前置条件

| 条件 | 要求 | 检查方式 |
|------|------|---------|
| CI检查通过 | 构建成功、测试通过、代码扫描通过 | 自动检查（CI Pipeline） |
| 代码审查通过 | 达到所需reviewer数量且全部Approve | GitHub/GitLab审批 |
| 无合并冲突 | 与目标分支无冲突 | 自动检测 |
| 分支已更新 | feature分支已rebase/merge目标分支最新代码 | 手动确认 |
| 测试覆盖 | 新增代码测试覆盖率≥80% | JaCoCo/Vitest报告 |
| 文档更新 | API变更需同步更新文档 | Reviewer检查 |

### 4.4 PR生命周期

```
创建PR → CI自动检查 → 指定Reviewer → Review → 修改反馈 → Approve → Squash Merge → 删除feature分支
   │         │              │            │          │
   │         │              │            │          └── 修改后重新提交，Reviewer重新审查
   │         │              │            └── Request Changes / Approve / Comment
   │         │              └── 至少1名Reviewer（main分支需2名）
   │         └── 失败则修复后重新触发CI
   └── 确保PR标题/Body格式正确
```

---

## 5. CI Pipeline检查项

### 5.1 检查阶段与通过标准

| 阶段 | 工具 | 通过标准 | 超时 |
|------|------|---------|------|
| Commit Lint | commitlint | 所有提交信息格式合规 | 1min |
| 代码风格 | ESLint(前端) + Checkstyle(后端) | 0个Error，Warning<20 | 3min |
| 静态扫描 | SonarQube | 0个Critical/Blocker，安全漏洞=0 | 5min |
| 单元测试 | JUnit(后端) + Vitest(前端) | 通过率100%，覆盖率≥80% | 10min |
| 构建 | Maven(后端) + pnpm build(前端) | 构建成功，产物完整 | 10min |
| 依赖检查 | OWASP Dependency-Check | 0个高危漏洞 | 5min |

### 5.2 CI触发规则

| 事件 | 触发Pipeline | 说明 |
|------|-------------|------|
| feature分支push | 完整CI（Lint+Scan+Test+Build） | 每次提交触发 |
| PR创建/更新 | 完整CI + 合并检查 | PR合并门禁 |
| develop分支push | 完整CI + 自动部署测试环境 | 集成验证 |
| release分支创建 | 完整CI + 自动部署预生产环境 | 发布验证 |
| main分支push | 完整CI + 自动部署生产环境 | 生产发布 |

---

## 6. Tag版本管理（SemVer）

### 6.1 版本号格式

```
v{major}.{minor}.{patch}
```

| 版本段 | 递增规则 | 示例 |
|--------|---------|------|
| Major | 不兼容的API变更、数据库结构大改 | v1.0.0 → v2.0.0 |
| Minor | 新增功能，向后兼容 | v1.0.0 → v1.1.0 |
| Patch | Bug修复，不改变API | v1.0.0 → v1.0.1 |

### 6.2 Tag创建规则

| 规则项 | 要求 |
|--------|------|
| 创建时机 | release分支合并到main后**立即**创建Tag |
| 创建位置 | 在`main`分支的merge commit上打Tag |
| 命名格式 | `v{major}.{minor}.{patch}`，如`v1.2.3` |
| 签名 | 生产Tag必须使用GPG签名：`git tag -s v1.2.3` |
| 注释 | Tag必须附带Release Notes：`git tag -a v1.2.3 -m "Release 1.2.3"` |
| 禁止修改 | 已推送的Tag禁止删除或覆盖 |

### 6.3 Tag创建命令

```bash
# 切换到main分支并拉取最新
git checkout main
git pull origin main

# 创建签名Tag
git tag -s v1.2.0 -m "$(cat <<'EOF'
Release v1.2.0

## 新增功能
- 销售订单批量导出
- 库存预警规则配置化

## Bug修复
- 修复采购入库金额计算精度问题
- 修复权限校验绕过漏洞

## 性能优化
- 报表查询性能提升40%
EOF
)"

# 推送Tag
git push origin v1.2.0
```

### 6.4 预发布版本

| 类型 | 格式 | 说明 |
|------|------|------|
| Alpha | `v1.2.0-alpha.1` | 内部测试版本 |
| Beta | `v1.2.0-beta.1` | 功能完整，外部测试 |
| RC | `v1.2.0-rc.1` | 候选发布版本 |

---

## 7. 分支保护规则

### 7.1 main分支保护

| 保护项 | 配置 |
|--------|------|
| 禁止直接推送 | ✅ 启用 |
| 禁止Force Push | ✅ 启用 |
| 禁止删除 | ✅ 启用 |
| 必须通过PR合并 | ✅ 启用 |
| 最少Reviewer数 | 2 |
| CI必须通过 | ✅ 所有检查项通过 |
| 要求签名提交 | ✅ GPG签名 |
| 要求线性历史 | ✅ Squash Merge |
| Code Owner审查 | ✅ 变更涉及的文件需要Code Owner审批 |
| 管理员绕过 | ❌ 禁止（管理员也需遵守规则） |

### 7.2 develop分支保护

| 保护项 | 配置 |
|--------|------|
| 禁止直接推送 | ✅ 启用 |
| 禁止Force Push | ✅ 启用 |
| 禁止删除 | ✅ 启用 |
| 必须通过PR合并 | ✅ 启用 |
| 最少Reviewer数 | 1 |
| CI必须通过 | ✅ 所有检查项通过 |
| 要求签名提交 | ❌ 不强制 |
| 要求线性历史 | ✅ Squash Merge |
| 管理员绕过 | ❌ 禁止 |

### 7.3 release分支保护

| 保护项 | 配置 |
|--------|------|
| 禁止直接推送 | ✅ 启用（仅允许bugfix通过PR合并） |
| 禁止Force Push | ✅ 启用 |
| 禁止删除 | ✅ 启用（合并到main后由CI自动删除） |
| 最少Reviewer数 | 1（技术负责人） |

---

## 8. Code Review清单

### 8.1 审查检查项（10项）

| 序号 | 检查项 | 审查要点 |
|:----:|--------|---------|
| 1 | **功能正确性** | 代码是否实现了PR描述中声明的功能，边界条件是否处理 |
| 2 | **代码规范** | 命名是否符合规范，格式是否统一，是否遵循项目编码规范 |
| 3 | **安全性** | 是否存在SQL注入、XSS、越权访问等安全漏洞，敏感数据是否脱敏 |
| 4 | **性能** | 是否存在N+1查询、大对象拷贝、死循环风险，SQL是否走索引 |
| 5 | **异常处理** | 异常是否被正确捕获和处理，是否有合理的错误提示，是否吞掉异常 |
| 6 | **事务一致性** | 事务边界是否正确，分布式场景下数据一致性是否有保障 |
| 7 | **测试覆盖** | 新增代码是否有对应单元测试，边界条件和异常路径是否覆盖 |
| 8 | **日志规范** | 关键操作是否有日志记录，日志级别是否合理，敏感信息是否脱敏 |
| 9 | **API兼容性** | 接口变更是否向后兼容，字段增删是否影响已有调用方 |
| 10 | **文档同步** | API变更是否同步更新文档，复杂逻辑是否有注释说明 |

### 8.2 Review响应时效

| PR优先级 | 首次Review时效 | 合并时效 |
|---------|--------------|---------|
| P0-核心 | 2小时内 | 当日 |
| P1-重要 | 4小时内 | 24小时内 |
| P2-常规 | 1个工作日 | 3个工作日内 |
| P3-低优 | 2个工作日 | 5个工作日内 |

### 8.3 Review意见分级

| 级别 | 标识 | 说明 | 是否阻塞合并 |
|------|------|------|:----------:|
| 必须修改 | `[MUST]` | 存在Bug、安全漏洞、严重规范违反 | ✅ |
| 建议修改 | `[SUGGEST]` | 代码可改进但不阻塞，可后续处理 | ❌ |
| 讨论 | `[DISCUSS]` | 需要讨论方案优劣 | ❌ |
| 提问 | `[QUESTION]` | 需要作者解释设计意图 | ❌ |
| 点赞 | `[NICE]` | 优秀的代码实践 | ❌ |

---

## 9. 冲突解决流程

### 9.1 冲突解决标准步骤

```
步骤1: 在feature分支上解决冲突（禁止在develop/main上直接解决）
  git checkout feature/xxx
  git fetch origin
  git merge origin/develop    # 或 git rebase origin/develop

步骤2: 解决冲突文件
  - 打开冲突文件，定位 <<<<<<< / ======= / >>>>>>> 标记
  - 理解双方变更意图，选择正确的合并方案
  - 复杂冲突必须与相关开发者沟通确认
  - 删除所有冲突标记

步骤3: 验证
  - 本地编译通过
  - 本地测试通过
  - 确认冲突解决未引入新问题

步骤4: 提交并推送
  git add .
  git commit        # merge方式：自动生成merge commit
                    # rebase方式：git rebase --continue
  git push origin feature/xxx

步骤5: 通知Reviewer重新审查
```

### 9.2 冲突解决规则

| 规则项 | 要求 |
|--------|------|
| 解决位置 | 必须在feature分支上解决，禁止在develop/main上直接操作 |
| 沟通要求 | 涉及同一文件的冲突，必须与另一位开发者沟通确认 |
| 数据库冲突 | SQL迁移脚本冲突禁止手动合并，必须由DBA协调执行顺序 |
| 配置文件冲突 | `pom.xml`/`package.json`冲突需检查依赖版本兼容性 |
| 解决后验证 | 必须本地编译+测试通过后才推送 |
| 大冲突处理 | 冲突文件>5个时，建议组织线下会议同步解决 |

### 9.3 常见冲突场景及处理方式

| 场景 | 处理方式 |
|------|---------|
| 同一文件不同方法修改 | 保留双方修改，无实际冲突 |
| 同一方法不同修改 | 与另一位开发者沟通，确定最终方案 |
| 导入语句冲突 | 合并导入，按字母排序去重 |
| 数据库迁移脚本冲突 | 按时间戳重命名，确保执行顺序正确 |
| 配置文件冲突 | 保留双方新增配置项，冲突项由技术负责人裁定 |

---

## 10. AI Agent协作规则

### 10.1 AI Agent分支操作规范

| 规则项 | 要求 |
|--------|------|
| 分支创建 | AI Agent必须从`develop`最新代码创建feature分支 |
| 提交频率 | 每完成一个独立功能点提交一次，提交粒度适中 |
| 提交信息 | 严格遵循Conventional Commits格式 |
| 推送规则 | 仅推送自己的feature分支，禁止推送公共分支 |
| PR创建 | 完成后必须创建PR，标题和Body遵循模板 |
| 冲突处理 | 遇到冲突时在feature分支上解决，解决后重新请求Review |

### 10.2 AI Agent禁止操作

| 禁止行为 | 说明 |
|---------|------|
| 直接修改main/develop | 必须通过PR流程 |
| 使用`git push --force` | 禁止force push任何分支 |
| 使用`git reset --hard` | 避免丢失未提交变更 |
| 跳过pre-commit hook | 禁止使用`--no-verify`跳过检查 |
| 修改`.git/config` | 禁止修改Git全局配置 |
| 删除远程分支 | 分支清理由CI自动处理 |

---

## 11. 附录：Git常用操作速查

### 11.1 功能开发完整流程

```bash
# 1. 同步develop到最新
git checkout develop
git pull --rebase origin develop

# 2. 创建feature分支
git checkout -b feature/sale-batch-export

# 3. 开发并提交（多次）
git add src/main/java/com/erp/sale/service/SaleExportService.java
git commit -m "feat(sale): 新增销售订单批量导出服务"

# 4. 推送feature分支
git push -u origin feature/sale-batch-export

# 5. 创建PR（通过GitHub/GitLab Web界面）
# 标题: [P1-sale] 销售订单批量导出功能
# Body: 按模板填写

# 6. CI检查 + Code Review → 修改 → Approve → Squash Merge

# 7. 清理本地分支
git checkout develop
git pull --rebase origin develop
git branch -d feature/sale-batch-export
```

### 11.2 热修复流程

```bash
# 1. 从main创建hotfix分支
git checkout main
git pull --rebase origin main
git checkout -b hotfix/fix-token-expiry

# 2. 修复并提交
git add .
git commit -m "fix(auth): 修复Token过期时间计算错误"

# 3. 推送到远程
git push -u origin hotfix/fix-token-expiry

# 4. PR合并到main（2 reviewers）
# 5. 在main上打Tag
git checkout main
git pull origin main
git tag -s v1.2.1 -m "Hotfix: Token过期时间修复"
git push origin v1.2.1

# 6. Cherry-pick到develop
git checkout develop
git cherry-pick <hotfix-commit-hash>
git push origin develop

# 7. 清理hotfix分支
git branch -d hotfix/fix-token-expiry
```
