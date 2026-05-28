# P2-001-001-001-004-009 实现POST /pushFromApply从申请下推接口

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-001-001-001-004-009 |
| 任务名称 | 实现POST /pushFromApply从申请下推接口 |
| 所属模块 | P2-001 |
| 优先级 | P2 |
| 任务类型 | 综合开发任务 |

## 二、任务目标

实现POST /pushFromApply从申请下推接口：校验申请单已审核；防重复下推(apply_id唯一)；复制申请明细到报销明细；生成新报销单

## 三、前置依赖

### 3.1 前置任务

- P2-001-001-001-004 费用报销Controller开发（父任务）
- P2-001-001-001-004-008 实现POST /checkBudget预算校验接口（前序兄弟任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用

| 规范文档名 | 引用原因 |
|-----------|---------|
| 全局规范-项目架构与开发约束 | 项目架构、技术约束与任务依赖关系 |
| 全局规范-末端任务文档编写规范 | 末端任务文档格式与内容规范约束 |
| 全局规范-AI开发执行手册 | AI辅助开发流程与执行标准 |
| 全局规范-后端代码规范 | 后端代码开发规范约束 |
| 全局规范-API接口规范 | API接口设计与RESTful规范约束 |

## 五、详细开发规格

> **本任务模块上下文**
> - 涉及数据表：expense_budget(费用预算单), expense_budget_detail(预算明细), expense_application(费用申请单), expense_application_detail(申请明细), expense_reimbursement(费用报销单), expense_reimbursement_detail(报销明细)
> - 涉及API：/api/expense/claim/*, /api/expense/application/*, /api/expense/budget/*, /api/expense/budget/execution/*
### 5.1 接口
- URL: POST /api/expense/expense/pushFromApply
- 请求: {"applyId": 1234567890}

### 5.2 处理流程
1. 查询申请单+校验已审核
2. 防重复下推(apply_id唯一)
3. 复制明细到报销
4. 生成新编号
5. 设置apply_id关联
6. 返回新报销单
## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/expense/controller/ExpenseController.java | Controller接口方法 |


## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | 下推成功 | 新报销单 |
| 2 | 自动带明细 | 查明细 |
| 3 | 重复拒绝 | 再次下推 |
| 4 | apply_id关联 | 查DB |


## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE

> ⚠️ 编号CodeGeneratorService生成勿手动拼接

> ⚠️ 公共字段MetaHandler自动填充

> ⚠️ 明细mainId设置为主表ID
