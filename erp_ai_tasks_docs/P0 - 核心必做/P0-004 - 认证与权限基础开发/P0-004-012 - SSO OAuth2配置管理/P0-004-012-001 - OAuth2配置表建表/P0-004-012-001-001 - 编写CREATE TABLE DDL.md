# P0-004-012-001-001 编写CREATE TABLE DDL

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-004-012-001-001 |
| 任务名称 | 编写CREATE TABLE DDL |
| 所属模块 | P0-004 |
| 优先级 | P0 |
| 任务类型 | DDL/数据库建表 |

## 二、任务目标

编写SSO OAuth2模块的数据库DDL脚本，定义表结构（字段名/类型/约束/默认值/注释）、索引、外键约束，确保符合PostgreSQL语法和项目数据库规范

## 三、前置依赖

### 3.1 前置任务

- P0-004-012-001 OAuth2配置表建表（父任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用


| 规范文档名 | 引用原因 |
|-----------|---------|
| 全局规范-项目架构与开发约束 | 项目架构、技术约束与任务依赖关系 |
| 全局规范-后端代码规范 | 后端代码开发规范约束 |
| 全局规范-数据库规范 | 数据库设计与DDL规范约束 |
| 全局规范-末端任务文档编写规范 | 末端任务文档结构与内容规范要求 |
| 全局规范-AI开发执行手册 | AI辅助开发流程与执行规范要求 |
## 五、详细开发规格


> **📦 本任务模块上下文**（来源：P0-004模块开发指南）
> - 本模块涉及数据表：sys_user, sys_dept, sys_user_dept, sys_user_role, sys_user_group, sys_user_group_member, sys_user_group_role, sys_user_password_history, sys_role, sys_role_menu
> - 本模块涉及API：/api/auth/login, /api/auth/logout, /api/auth/token/refresh, /api/auth/token/verify, /api/auth/user-info
> - 本模块业务规则：Sa-Token集成：所有登录认证必须通过Sa-Token框架实现，Token签发/注销/校验/续期均使用Sa-Token API，不允许自行实现Token机制。; 密码安全：用户密码必须使用BCrypt加密存储，不允许明文存储或可逆加密。密码策略配置（最小8位、大小写+数字+特殊字符四选三）由auth_password_policy表驱动。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。
### 5.1 表结构设计
按设计文档定义表名、字段、数据类型。
### 5.2 必含公共字段
id(BIGINT PK), tenant_id, created_by, created_time, updated_by, updated_time, is_deleted, version
### 5.3 索引设计
主键索引 + 业务唯一索引(含is_deleted) + 外键关联索引 + 常用查询字段索引
### 5.4 Flyway迁移脚本
命名: V{日期}{序号}__{描述}.sql，含回滚注释

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | Flyway迁移脚本(Vn__*.sql) | DDL语句+索引+约束 |
| 2 | DDL验证脚本 | 回滚脚本 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | DDL语句执行成功，表已创建 | 人工检查/自动化测试 |
| 2 | 字段类型/长度/约束与设计一致 | 人工检查/自动化测试 |
| 3 | 索引创建成功，查询性能达标 | 人工检查/自动化测试 |
| 4 | Flyway迁移版本号无冲突 | 人工检查/自动化测试 |
| 5 | 回滚脚本可正常执行 | 人工检查/自动化测试 |

## 八、易错警示

> ⚠️ PostgreSQL的varchar长度要合理设置，不要一律使用varchar(255)

> ⚠️ 所有表必须包含tenant_id(bigint)、is_deleted(int DEFAULT 0)、create_time、update_time、create_by、update_by基础字段

> ⚠️ 索引命名规范：idx_{表名}_{字段名}，唯一索引：uk_{表名}_{字段名}
