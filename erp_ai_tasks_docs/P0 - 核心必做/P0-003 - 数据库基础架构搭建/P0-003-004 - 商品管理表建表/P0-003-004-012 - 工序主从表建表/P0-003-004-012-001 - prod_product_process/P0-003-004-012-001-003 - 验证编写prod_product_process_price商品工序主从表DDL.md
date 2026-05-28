# P0-003-004-012-001-003 验证编写prod_product_process_price商品工序主从表DDL

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-003-004-012-001-003 |
| 任务名称 | 验证编写prod_product_process_price商品工序主从表DDL |
| 所属模块 | P0-003 |
| 优先级 | P0 |
| 任务类型 | DDL/数据库建表 |

## 二、任务目标


验证DDL脚本执行结果：确认CREATE TABLE/INDEX执行成功、字段类型约束与设计一致、Flyway版本号无冲突

## 三、前置依赖

### 3.1 前置任务

- P0-003-004-012-001 编写prod_product_process_price商品工序主从表DDL（父任务）
- P0-003-004-012-001-002 编写prod_product_process_price商品工序主从表索引与约束（前序兄弟任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用




| 规范文档名 | 引用原因 |
|-----------|--------|
| 全局规范-项目架构与开发约束 | 项目架构、技术约束与任务依赖关系 |
| 全局规范-末端任务文档编写规范 | 末端任务文档结构与内容规范 |
| 全局规范-AI开发执行手册 | AI开发执行流程与质量标准 |
| 全局规范-数据库规范 | 数据库设计与DDL规范约束 |

## 五、详细开发规格




> **本任务模块上下文**（来源：P0-003模块开发指南）
> - 本模块技术栈：PostgreSQL 15+ / Flyway迁移 / Schema多租户 / 公共字段基座规范
> - 通用字段强制约束：所有业务表必须包含10个通用字段(id(BIGINT PK), tenant_id(BIGINT NOT NULL), created_by(BIGINT), created_at(TIMESTAMP DEFAULT CURRENT_TIMESTAMP), updated_by(BIGINT), updated_at(TIMESTAMP DEFAULT CURRENT_TIMESTAMP), is_deleted(BOOLEAN DEFAULT false), owner_dept_id(BIGINT), owner_id(BIGINT), version(INT DEFAULT 1))
> - 数值精度约束：所有金额/单价/数量/转换率字段统一使用decimal(18,8)
> - 部分唯一索引约束：业务唯一性字段必须采用`CREATE UNIQUE INDEX ... WHERE is_deleted = false`
> - 多租户隔离：tenant_id字段不可为空，联合索引必须以tenant_id为首列
> - Flyway命名规范：V{yyyyMMdd}{seq}__{description}.sql

### 5.1 DDL执行验证
- 执行`SELECT tablename FROM pg_tables WHERE schemaname='public' AND tablename='prod_product_process_price'`确认表已创建
- 执行`SELECT column_name, data_type, is_nullable FROM information_schema.columns WHERE table_name='prod_product_process_price'`验证字段定义
- 执行`SELECT indexname, indexdef FROM pg_indexes WHERE tablename='prod_product_process_price'`验证索引

### 5.2 约束验证
- 主键约束验证、唯一约束验证、部分唯一索引WHERE条件包含`is_deleted = false`验证

### 5.3 Flyway版本验证
- 执行`SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank DESC LIMIT 5`

### 5.4 COMMENT完整性验证
- 表注释验证、字段注释完整性验证

## 六、交付物清单



| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | db/migration/V20260526001__verify_prod_product_process_price.sql | prod_product_process_price表DDL验证查询脚本 |
| 2 | docs/verification/prod_product_process_price_verify_report.md | 验证报告记录 |

## 七、验收标准



| 序号 | 检查项 | 验证方法 |
|:---:|--------|--------|
| 1 | DDL执行成功，所有表已创建 | pg_tables COUNT验证 |
| 2 | 字段类型/约束与设计100%一致 | 人工对比DDL与设计文档 |
| 3 | 所有索引创建成功 | pg_indexes COUNT验证 |
| 4 | Flyway迁移记录success=true | flyway_schema_history查询 |
| 5 | COMMENT注释完整 | 人工检查覆盖率 |

## 八、易错警示


> ⚠️ 通用字段10个必须完整包含(id/tenant_id/created_at/updated_at/created_by/updated_by/is_deleted/owner_dept_id/owner_id/version)，缺少任何一个将导致MyBatis-Plus自动填充和多租户插件异常

> ⚠️ 部分唯一索引必须包含`WHERE is_deleted = false`条件，避免boolean类型is_deleted在联合唯一索引中的"一删一活"经典陷阱

> ⚠️ 所有金额/单价/数量/转换率字段统一使用decimal(18,8)，禁止使用decimal(18,8)或DECIMAL(18,6)，显示精度由系统参数动态控制

> ⚠️ 联合索引必须以tenant_id为首列（如INDEX(tenant_id, code)），否则多租户隔离查询性能将严重下降

> ⚠️ Flyway迁移脚本命名必须使用V{yyyyMMdd}{seq}__{description}.sql（双下划线），版本号冲突将导致迁移失败

> ⚠️ 所有表和字段必须包含COMMENT注释，PostgreSQL元数据查询和后续代码生成依赖COMMENT

> ⚠️ 禁止使用数据库外键约束，应用层通过MyBatis-Plus维护关联关系，避免级联操作和性能问题
