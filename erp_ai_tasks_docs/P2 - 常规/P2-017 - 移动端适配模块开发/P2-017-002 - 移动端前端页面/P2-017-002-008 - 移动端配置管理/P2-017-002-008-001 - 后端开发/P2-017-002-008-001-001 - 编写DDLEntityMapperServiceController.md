# P2-017-002-008-001-001 编写DDL+Entity/Mapper/Service/Controller

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-017-002-008-001-001 |
| 任务名称 | 编写DDL+Entity/Mapper/Service/Controller |
| 所属模块 | P2-017 |
| 优先级 | P2 |
| 任务类型 | DDL/数据库建表 |

## 二、任务目标

编写DDL+Entity/Mapper/Service/Controller的Flyway迁移脚本：定义完整字段（含10个通用必含字段id/tenant_id/created_at/updated_at/created_by/updated_by/is_deleted/owner_dept_id/owner_id/version）+ 业务字段 + 索引（含uk唯一索引WHERE is_deleted=false + idx查询索引），PostgreSQL 15+语法，脚本命名V{版本号}__create_{表名}.sql

## 三、前置依赖

### 3.1 前置任务

- P2-017-002-008-001 后端开发（父任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用


| 规范文档名 | 引用原因 |
|-----------|---------|
| 全局规范-项目架构与开发约束 | 项目架构、技术约束与任务依赖关系 |
| 全局规范-末端任务文档编写规范 | 末端任务文档标准化模板与质量要求 |
| 全局规范-AI开发执行手册 | AI任务解读与执行流程规范 |
| 全局规范-后端代码规范 | 后端代码开发规范约束 |
| 全局规范-数据库规范 | 数据库字段映射与模型规范约束 |
## 五、详细开发规格

> **📦 本任务模块上下文**（来源：P2-017模块开发指南）
> - 本模块涉及数据表：参见模块开发指南
> - 本模块涉及API：/api/mobile/menu, /api/mobile/approval, /api/mobile/push, /api/mobile/business, /api/mobile/report
> - 本模块业务规则：本模块包含移动端专属约束，基于UniApp跨平台框架（iOS/Android/H5）开发：; 跨平台适配：移动端页面基于UniApp开发，需同时适配iOS、Android与H5三种平台；移动端首页采用H5适配方案，包含快捷功能入口、待办提醒与KPI卡片。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 完整字段定义

| 序号 | 字段名 | 数据类型 | 约束 | 默认值 | 中文标题 |
|:---:|--------|---------|------|--------|---------|
| 1 | id | BIGINT | PRIMARY KEY | 雪花算法 | 主键ID |
| 2 | tenant_id | BIGINT | NOT NULL | - | 租户ID |
| 3 | created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 创建时间 |
| 4 | updated_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 更新时间 |
| 5 | created_by | BIGINT | NOT NULL | - | 创建人 |
| 6 | updated_by | BIGINT | NOT NULL | - | 更新人 |
| 7 | is_deleted | BOOLEAN | NOT NULL | false | 逻辑删除 |
| 8 | owner_dept_id | BIGINT | - | - | 所属部门 |
| 9 | owner_id | BIGINT | - | - | 所属人 |
| 10 | version | INT | NOT NULL | 1 | 乐观锁版本 |
| 11+ | (按编写DDL+Entity/Mapper/Service/Controller补充业务字段) | - | - | - | - |

### 5.2 索引设计

| 索引名 | 类型 | 字段 | 说明 |
|--------|------|------|------|
| pk_mobile_device | 主键 | id | 主键索引 |
| uk_mobile_device_code | 唯一(部分) | code WHERE is_deleted=false | 编码唯一 |
| idx_mobile_device_tenant | 普通 | tenant_id, status | 租户查询 |

### 5.3 DDL模板

```sql
-- Flyway迁移脚本: V{版本号}__create_mobile_device.sql
CREATE TABLE mobile_device (
    id BIGINT PRIMARY KEY,
    tenant_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT NOT NULL,
    updated_by BIGINT NOT NULL,
    is_deleted BOOLEAN NOT NULL DEFAULT false,
    owner_dept_id BIGINT,
    owner_id BIGINT,
    version INT NOT NULL DEFAULT 1
    -- 业务字段按编写DDL+Entity/Mapper/Service/Controller补充
);
CREATE UNIQUE INDEX uk_mobile_device_code ON mobile_device(code) WHERE is_deleted = false;
CREATE INDEX idx_mobile_device_tenant ON mobile_device(tenant_id);
COMMENT ON TABLE mobile_device IS '移动端表';
```

### 5.4 验证

1. DDL脚本在PostgreSQL 15+执行成功
2. 表结构与字段定义完全一致
3. 索引创建成功（通过\di验证）
4. Flyway版本号无冲突

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | db/migration/V{version}__create_mobile.sql | 建表DDL+索引+注释 |
| 2 | db/rollback/V{version}__rollback_mobile.sql | 回滚脚本(DROP TABLE) |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | DDL脚本在PostgreSQL 15+执行成功无报错 | 执行Flyway迁移脚本 |
| 2 | 表字段与文档5.1定义完全一致（通过\d 表名验证） | psql \d命令 |
| 3 | uk唯一索引含WHERE is_deleted=false条件 | \di查看索引定义 |
| 4 | Flyway flyway_schema_history无版本号冲突 | 查询flyway_schema_history表 |
| 5 | COMMENT ON TABLE/COLUMN注释完整 | \d+ 查看注释 |

## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id条件

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE而非DELETE

> ⚠️ Flyway版本号格式V{YYYYMMDD}{NNN}，提交前检查无冲突

> ⚠️ UNIQUE索引必须带WHERE is_deleted=false条件，否则软删除记录会冲突

> ⚠️ 数值字段精度——金额用decimal(18,2)、数量用decimal(18,8)、比率用decimal(8,4)

> ⚠️ [移动端模块] 响应式设计断点：320px(手机)/768px(平板)/1024px(桌面)，使用CSS媒体查询适配

> ⚠️ [移动端模块] 离线数据同步时需处理冲突（乐观锁version校验），冲突时提示用户选择保留版本

> ⚠️ [移动端模块] 推送通知发送需记录推送日志（成功/失败/点击），失败时需重试机制（最多3次）
