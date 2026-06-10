# ERP 自动验证修复 — 执行指令

> 运行模式: {MODE}
> 启动时间: 会话自动获取

---

## 0. 初始化

1. 读取 `verify-config.ini` 获取配置参数
2. 读取 `verify-tasks.md` 获取当前进度（resume 模式下使用）
3. 确定报告目录: `reports/{当前日期}-verify-{序号}`（检查已有目录递增序号）
4. 如 Mode=full，所有 7 个 Stage 执行；Mode=cron 时跳过 Stage 5.5
5. 如 Mode=resume，从 verify-tasks.md 的 current_stage 和 current_batch 恢复执行
6. 更新 verify-tasks.md 初始化状态

---

## Stage 1: 编译验证

**目标**: 确认前后端编译无错误

### 执行
```bash
# 后端编译
cd {项目根目录}
mvn clean compile -DskipTests 2>&1 | Out-File -FilePath reports/{date}-verify-{seq}/stage-1-compile.log -Encoding utf8

# 前端编译
cd erp-ai-web
pnpm install 2>&1 | Out-File -FilePath ../reports/{date}-verify-{seq}/stage-1-compile.log -Append -Encoding utf8
pnpm build 2>&1 | Out-File -FilePath ../reports/{date}-verify-{seq}/stage-1-compile.log -Append -Encoding utf8
```

### 判定
- exit code 均为 0 → PASS
- 有 ERROR 或 exit code ≠ 0 → FAIL，进入修复

### 修复流程（FAIL 时）
1. 解析 stage-1-compile.log，提取所有 [ERROR] 行
2. 按文件分组，每文件作为独立修复单元
3. 对每个 ERROR:
   - 读取错误文件和行号
   - 分析根因（缺失 import / 类型不匹配 / 语法错误）
   - Edit 修复
4. 重跑编译命令
5. 仍失败则重试，最多 3 次
6. 3 次后仍失败 → 标记 ⚠️ 写入报告，跳过该错误
7. 生成 `stage-1-compile.md` 报告

### 报告内容
- 编译结果（PASS/FAIL）
- 修复文件和行数清单
- 未能修复的项（如有）
- 耗时

---

## Stage 2: 后端单元测试

**目标**: 运行所有后端单元测试，确保全部通过

### 执行（分批）
```bash
# Batch A: 基础层 (P0-001 ~ P0-005)
mvn test -Dtest="com.erp.config.*,com.erp.common.*,com.erp.auth.*,com.erp.system.*" 2>&1 | Out-File reports/{date}-verify-{seq}/stage-2-backend.log -Encoding utf8

# Batch B: 业务层A (P0-006 ~ P0-009: org/product/crm/srm)
mvn test -Dtest="com.erp.module.org.*,com.erp.module.product.*,com.erp.module.crm.*,com.erp.module.srm.*" 2>&1 | Out-File -Append reports/{date}-verify-{seq}/stage-2-backend.log -Encoding utf8

# Batch C: 业务层B (P0-010 ~ P0-012: warehouse/finance/hrm)
mvn test -Dtest="com.erp.module.warehouse.*,com.erp.module.finance.*,com.erp.hrm.*" 2>&1 | Out-File -Append reports/{date}-verify-{seq}/stage-2-backend.log -Encoding utf8

# Batch D: 部署+测试 (P0-013 ~ P0-014)
mvn test -Dtest="com.erp.engine.*,com.erp.approval.*,com.erp.message.*,com.erp.flow.*" 2>&1 | Out-File -Append reports/{date}-verify-{seq}/stage-2-backend.log -Encoding utf8
```

### 判定
- 所有批次 exit code 均为 0，且无 FAILED/ERROR → PASS
- 有失败用例 → FAIL

### 修复流程（FAIL 时）
1. 解析 target/surefire-reports/ 下的测试报告，定位失败用例
2. 对每个失败用例:
   - 读取测试源码，理解测试意图
   - 检查断言是否与当前代码一致
   - 检查 Mock 数据是否匹配 Entity 字段变更
   - 检查数据库约束是否满足
3. Edit 修复测试代码（不修改业务代码，除非业务代码有 bug）
4. 重跑对应批次的测试
5. 最多重试 3 次

### 报告内容
- 各批次通过/失败统计
- 失败用例清单及修复记录
- 覆盖率数据（如 JaCoCo 已配置）

---

## Stage 3: 前端组件测试

**目标**: 运行所有 Vitest 组件测试

### 执行
```bash
cd erp-ai-web
npx vitest run --reporter=verbose 2>&1 | Out-File ../reports/{date}-verify-{seq}/stage-3-frontend.log -Encoding utf8
```

### 判定
- exit code = 0，所有测试 PASS → 通过
- 有失败或超时 → FAIL

### 修复流程（FAIL 时）
1. 解析 vitest 输出，定位失败用例
2. 对每个失败用例:
   - 检查组件 props/events/slots 是否与当前组件实现一致
   - 检查 stub/mock 是否正确模拟了依赖组件
   - 修复测试代码
3. 重跑 `npx vitest run`
4. 最多重试 3 次

---

## Stage 4: E2E 页面测试

**目标**: Playwright 端到端测试覆盖每模块核心流程

### 前置
1. 确认 `tests-e2e/` 目录存在且 `pnpm install` 完成
2. 启动后端服务:
```bash
Start-Process -NoNewWindow mvn -ArgumentList "spring-boot:run","-Dspring.profiles.active=dev" -WorkingDirectory {项目根目录}
```
3. 等待服务就绪: 循环 curl `http://localhost:8080/api/actuator/health` 最多 60 秒

### 执行
```bash
cd tests-e2e
npx playwright test 2>&1 | Out-File ../reports/{date}-verify-{seq}/stage-4-e2e.log -Encoding utf8
```

### 后置
1. 停止后端进程
2. 收集 `tests-e2e/screenshots/` 中的失败截图到报告目录

### 判定
- 所有 spec 通过 → PASS
- 有失败 → FAIL，进入修复

### 修复流程（FAIL 时）
1. 查看失败截图和日志
2. 分析失败原因（选择器变化 / 超时 / 页面结构变化）
3. 修复测试代码或页面代码
4. 重跑失败的 spec: `npx playwright test --grep "{spec name}"`
5. 最多重试 3 次

---

## Stage 5: 代码合规扫描

**目标**: 检查代码规范和安全问题

### 扫描规则
执行以下扫描，输出违规清单:

| 检查项 | 扫描方式 | 规则 |
|--------|---------|------|
| 硬编码密码 | Grep `password\s*=` `secret\s*=` `apiKey\s*=` 在非 properties 文件中 | 应使用环境变量 |
| 缺失 @Transactional | 读取所有 ServiceImpl，检查写操作（insert/update/delete/save）方法 | 必须有注解 |
| Controller 无 @Operation | 读取所有 Controller，检查每个 @XxxMapping 方法 | 必须有 @Operation |
| import 通配符 | Grep `import\s+\S+\.\*` | 必须展开 |
| 未使用的 import | 前端: `eslint` 输出 | 删除 |

### 修复
- 每项违规自动修复（Edit 修改源代码）
- 重扫确认修复成功
- 最多重试 3 次

### 报告
- 扫描项总数、违规数、修复数
- 未修复项清单

---

## Stage 5.5: 前端设计重构（仅 full 模式）

**目标**: 在需求范围内改进页面视觉和交互体验

**约束**: 不改功能逻辑、路由、权限、i18n key；不新增功能

### Round 1: 公共基础组件（7 个文件，阻断门禁）

需重构文件:
- `erp-ai-web/src/components/list-table/index.vue`
- `erp-ai-web/src/components/edit-table/index.vue`
- `erp-ai-web/src/components/master-form/MasterForm.vue`
- `erp-ai-web/src/components/detail-table/index.vue`
- `erp-ai-web/src/components/basic-input/index.vue`
- `erp-ai-web/src/components/query-panel/index.vue`
- `erp-ai-web/src/components/action-bar/index.vue`

每文件 SOP:
1. Read 当前文件
2. 使用 frontend-design 技能分析改进点
3. Edit 应用改进
4. `pnpm build` 验证编译
5. PASS → 下一个文件; FAIL → 回滚 → 重试 1 次
6. Round 1 全部完成后: `npx vitest run` 确认组件测试通过
7. git commit: "refactor(verify): 重构公共基础组件样式"

**阻断**: Round 1 任一文件 2 次尝试后仍失败 → 暂停 Stage 5.5，记录原因

### Round 2: 核心入口页面（6 个文件）

- `erp-ai-web/src/views/login/index.vue`
- `erp-ai-web/src/views/home/index.vue`
- `erp-ai-web/src/layouts/components/Sidebar/index.vue`
- `erp-ai-web/src/layouts/components/Navbar.vue`
- `erp-ai-web/src/layouts/components/TabNav/index.vue`
- `erp-ai-web/src/App.vue`

SOP 同 Round 1。完成后: `pnpm build` + 对应组件测试

### Round 3: 每模块 1 个代表性页面（逐模块推进）

按顺序逐模块:
1. P0-006 org → `erp-ai-web/src/views/org/CompanyForm.vue`
2. P0-007 product → 找到对应列表页
3. P0-008 crm → 找到对应列表页
4. P0-009 srm → 找到对应列表页
5. P0-010 warehouse → `erp-ai-web/src/views/warehouse/warehouse/index.vue`
6. P0-011 finance → `erp-ai-web/src/views/finance/account/index.vue`
7. P0-012 hrm → `erp-ai-web/src/views/hrm/employeecenter/index.vue`

每模块完成后 `pnpm build` + git commit

### Round 4: 剩余页面（约 50+ 个文件，每 5 个一批）

收集所有未被 Round 1~3 覆盖的 .vue 文件，每 5 个一批重构，每批 `pnpm build` + git commit

### 报告
- 各轮文件数、成功数、失败数
- 关键改进点摘要
- 重构前后截图对比（如有 Playwright 截图）

---

## Stage 6: 汇总 & PR

### 汇总
1. 读取所有 Stage 报告
2. 生成 `summary.md`:
   - 概览表（Stage / 结果 / 修复数 / 耗时）
   - 修复文件清单
   - 未修复项（标记 ⚠️）
   - 测试统计（总数/通过/失败/跳过）

### 提交 & PR
```bash
git checkout -b fix/verify-YYYYMMDD-HHmmss
git add -A
git commit -m "fix(verify): 自动验证修复 - {日期} {模式} — X个Stage通过, Y个问题修复"
git push -u origin fix/verify-YYYYMMDD-HHmmss
gh pr create --title "fix(verify): 自动验证修复 - {日期}" --body "$(cat reports/{date}-verify-{seq}/summary.md)"
```

### 清理
- 更新 verify-tasks.md 标记全部完成
- 删除超过 keep_recent 的旧报告目录
- 输出 PR 链接

---

## 中断续跑（仅 Mode=resume）

读取 `verify-tasks.md`:
- `current_stage` → 从该 Stage 继续
- `current_batch` → 从该批次继续
- `status` → 确认状态后恢复执行
- 已完成 Stage 的报告不重新生成
