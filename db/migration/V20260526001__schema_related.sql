-- ============================================================
-- ERP AI 智能管理系统 - 数据库与Schema创建脚本
-- 任务编号: P0-003-001-001-001-001
-- 文件名:   V20260526001__schema_related.sql
-- 说明:     创建ERP系统PostgreSQL数据库，指定UTF8编码、
--           LC_COLLATE/LC_CTYPE区域设置、默认表空间，
--           并创建业务Schema(多租户隔离Schema)
-- 执行方式: 以postgres超级用户身份在psql中执行此脚本
-- ============================================================

-- ============================================================
-- 第1步：创建ERP数据库
-- 注意：CREATE DATABASE不能在事务块中执行，需单独运行
-- 如果数据库已存在则跳过（幂等性保证）
-- ============================================================

-- 检查数据库是否已存在，不存在则创建
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'erp_db') THEN
        CREATE DATABASE erp_db
            WITH
                ENCODING       = 'UTF8'
                LC_COLLATE     = 'zh_CN.UTF-8'
                LC_CTYPE       = 'zh_CN.UTF-8'
                TEMPLATE       = template0
                TABLESPACE     = pg_default
                CONNECTION LIMIT = 200;
    END IF;
END
$$;

-- 数据库注释（需在创建后单独执行）
-- 如果数据库刚创建，需先断开当前连接再重新连接到erp_db执行以下语句
-- COMMENT ON DATABASE erp_db IS 'ERP AI 智能管理系统主数据库';

-- ============================================================
-- 第2步：创建业务Schema（多租户隔离）
-- 以下语句需在连接到 erp_db 后执行
-- ============================================================

-- 公共Schema：存放系统级配置、字典、参数等跨租户共享数据
CREATE SCHEMA IF NOT EXISTS erp_base;
COMMENT ON SCHEMA erp_base IS '基础数据Schema - 存放系统级配置、字典、参数、编码规则等租户共享的基础数据';

-- 租户业务Schema：存放各租户的业务数据表（约200张表）
CREATE SCHEMA IF NOT EXISTS erp_tenant;
COMMENT ON SCHEMA erp_tenant IS '租户业务数据Schema - 存放各租户的业务数据表，通过tenant_id字段实现行级多租户隔离';

-- ============================================================
-- 第3步：设置Schema搜索路径
-- ============================================================
-- 设置默认搜索路径，优先搜索租户Schema
ALTER DATABASE erp_db SET search_path TO erp_tenant, erp_base, public;

-- ============================================================
-- 第4步：Schema权限说明
-- ============================================================
-- erp_base: 应用服务账号具有读写权限，所有租户共享
-- erp_tenant: 应用服务账号具有读写权限，通过tenant_id行级隔离
-- public: 保留PostgreSQL默认，不存放业务数据

-- ============================================================
-- 第5步：公共字段基座定义（通用字段模板）
-- 任务编号: P0-003-001-002-001-001
-- 说明:     所有业务表必须包含以下10个通用字段，
--           此为字段模板定义，各建表DDL必须严格遵循。
--           未来所有CREATE TABLE语句必须完整包含此10个字段。
-- ============================================================

/*
 * 公共字段基座规范（10个通用字段，所有业务表强制包含）
 *
 * 字段清单：
 * ┌─────────────────┬────────────┬──────────────────────────────────┐
 * │ 字段名           │ 数据类型    │ 说明                             │
 * ├─────────────────┼────────────┼──────────────────────────────────┤
 * │ id              │ BIGINT     │ 主键ID，自增，唯一标识            │
 * │ tenant_id       │ BIGINT     │ 租户ID，多租户隔离，NOT NULL      │
 * │ created_by      │ BIGINT     │ 创建人ID，审计字段，不可修改       │
 * │ created_at      │ TIMESTAMP  │ 创建时间，DEFAULT CURRENT_TIMESTAMP│
 * │ updated_by      │ BIGINT     │ 修改人ID，每次更新自动刷新         │
 * │ updated_at      │ TIMESTAMP  │ 更新时间，DEFAULT CURRENT_TIMESTAMP│
 * │ is_deleted      │ BOOLEAN    │ 软删除标记，DEFAULT false          │
 * │ owner_dept_id   │ BIGINT     │ 所属部门ID，数据权限用，可按需变更 │
 * │ owner_id        │ BIGINT     │ 数据负责人ID，数据权限用，可按需变更│
 * │ version         │ INT        │ 乐观锁版本号，每次更新+1，DEFAULT 1│
 * └─────────────────┴────────────┴──────────────────────────────────┘
 *
 * DDL片段模板（直接复制到各CREATE TABLE语句末尾）：
 *
 *     id              BIGSERIAL       PRIMARY KEY,
 *     tenant_id       BIGINT          NOT NULL,
 *     created_by      BIGINT,
 *     created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
 *     updated_by      BIGINT,
 *     updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
 *     is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
 *     owner_dept_id   BIGINT,
 *     owner_id        BIGINT,
 *     version         INT             NOT NULL DEFAULT 1
 *
 * 设计要点：
 * - created_by与owner_id分离：created_by记录历史创建人（审计字段，永不修改）；
 *   owner_id记录当前数据负责人（数据权限判定字段，可按需变更）。
 * - 数据权限取值链路：WHERE owner_dept_id IN (用户可见部门) AND/OR
 *   owner_id IN (用户可见人员)。禁止使用created_by做数据权限判定。
 * - 软删除：MyBatis-Plus全局逻辑删除插件自动注入 is_deleted=false。
 * - 部分唯一索引：CREATE UNIQUE INDEX ... WHERE is_deleted = false。
 * - 多租户：联合索引必须以tenant_id为首列。
 * - 乐观锁：MyBatis-Plus自动处理version字段的更新检查。
 */

-- 公共字段索引模板（每个业务表创建后执行）
-- CREATE INDEX idx_{table}_tenant_id ON {table}(tenant_id);
-- 业务唯一约束示例：
-- CREATE UNIQUE INDEX uk_{table}_{field}_active ON {table}(tenant_id, {field}) WHERE is_deleted = FALSE;

-- ============================================================
-- 第6步：公共字段默认值规范
-- 任务编号: P0-003-001-002-001-002
-- 说明:     定义10个通用字段的默认值规则，所有业务表
--           CREATE TABLE必须严格遵循以下默认值配置。
--           此处定义为"基座规范"，后续建表DDL按此模板执行。
-- ============================================================

/*
 * 公共字段默认值规范（10个通用字段，所有业务表强制遵循）
 *
 * ┌─────────────────┬──────────────────────────────────────────────────────┐
 * │ 字段名           │ 默认值规则                                            │
 * ├─────────────────┼──────────────────────────────────────────────────────┤
 * │ id              │ BIGSERIAL（自增），无需手动设默认值                     │
 * │ tenant_id       │ NOT NULL，无默认值，由应用层MyBatis-Plus多租户插件注入  │
 * │ created_by      │ 无默认值，由应用层自动填充当前登录用户ID（审计字段）     │
 * │ created_at      │ NOT NULL DEFAULT CURRENT_TIMESTAMP，自动填充，不可修改  │
 * │ updated_by      │ 无默认值，由应用层每次UPDATE自动刷新当前登录用户ID      │
 * │ updated_at      │ NOT NULL DEFAULT CURRENT_TIMESTAMP，每次UPDATE自动刷新  │
 * │ is_deleted      │ NOT NULL DEFAULT FALSE，软删除标记                     │
 * │ owner_dept_id   │ 无默认值，由应用层默认取创建人所属部门ID，可按需变更     │
 * │ owner_id        │ 无默认值，由应用层默认取created_by值，可按需变更        │
 * │ version         │ NOT NULL DEFAULT 1，乐观锁版本号，每次更新+1            │
 * └─────────────────┴──────────────────────────────────────────────────────┘
 *
 * DDL字段片段（含完整默认值与NOT NULL约束，直接复制到各CREATE TABLE末尾）：
 *
 *     id              BIGSERIAL       PRIMARY KEY,
 *     tenant_id       BIGINT          NOT NULL,
 *     created_by      BIGINT,
 *     created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
 *     updated_by      BIGINT,
 *     updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
 *     is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
 *     owner_dept_id   BIGINT,
 *     owner_id        BIGINT,
 *     version         INT             NOT NULL DEFAULT 1
 *
 * 默认值设计原则：
 * - 时间字段(created_at/updated_at)：DB层设CURRENT_TIMESTAMP兜底，应用层设精确值
 * - 布尔字段(is_deleted)：必须NOT NULL DEFAULT FALSE，避免三值逻辑
 * - 版本号(version)：DEFAULT 1，MyBatis-Plus乐观锁自动+1
 * - 人员字段(created_by/updated_by/owner_dept_id/owner_id)：由应用层赋值，DB层不设默认值
 * - 租户字段(tenant_id)：NOT NULL，由MyBatis-Plus多租户插件自动注入，DB层不设默认值
 */

-- ============================================================
-- 第7步：公共字段约束规范
-- 任务编号: P0-003-001-002-001-002
-- 说明:     定义10个通用字段的约束规则，包括NOT NULL约束、
--           部分唯一索引约束、多租户联合索引约束。
--           所有业务表建表时必须遵循以下约束规则。
-- ============================================================

/*
 * 公共字段约束规范（所有业务表强制遵循）
 *
 * 一、NOT NULL约束（不可为空，DDL必须声明NOT NULL）：
 * ┌─────────────────┬──────────┬──────────────────────────────┐
 * │ 字段名           │ NOT NULL │ 原因                          │
 * ├─────────────────┼──────────┼──────────────────────────────┤
 * │ id              │ 是(PK)   │ 主键，自增，天然NOT NULL       │
 * │ tenant_id       │ 是       │ 多租户隔离必填，插件自动注入   │
 * │ created_at      │ 是       │ 审计必填，DEFAULT兜底          │
 * │ updated_at      │ 是       │ 审计必填，DEFAULT兜底          │
 * │ is_deleted      │ 是       │ 软删除标记，DEFAULT FALSE兜底  │
 * │ version         │ 是       │ 乐观锁，DEFAULT 1兜底          │
 * └─────────────────┴──────────┴──────────────────────────────┘
 *
 * 二、可空字段（有默认业务逻辑，允许NULL）：
 * ┌─────────────────┬────────────────────────────────────────┐
 * │ 字段名           │ 可空原因                                │
 * ├─────────────────┼────────────────────────────────────────┤
 * │ created_by      │ 系统自动填充时可能未登录（如定时任务）    │
 * │ updated_by      │ 首次创建时尚未有修改操作                  │
 * │ owner_dept_id   │ 创建时部门可能未确定                      │
 * │ owner_id        │ 创建时负责人可能未分配                    │
 * └─────────────────┴────────────────────────────────────────┘
 *
 * 三、部分唯一索引约束（软删除场景下的唯一性保证）：
 *
 * 规则：业务唯一性字段必须使用部分唯一索引，仅对未删除数据强制唯一。
 * 格式：CREATE UNIQUE INDEX uk_{table}_{field}_active
 *       ON {table}(tenant_id, {field}) WHERE is_deleted = FALSE;
 *
 * 设计原因：is_deleted为BOOLEAN类型，若直接纳入联合唯一索引
 * (tenant_id, code, is_deleted)，则同一code只能存在一条已删+一条未删记录，
 * 无法支持多次删除后重建同code记录。使用部分唯一索引
 * WHERE is_deleted = FALSE 仅约束未删除数据，已删除数据不参与唯一性校验。
 *
 * 四、多租户联合索引约束：
 *
 * 规则：所有联合索引必须以tenant_id为首列。
 * 格式：CREATE INDEX idx_{table}_{field} ON {table}(tenant_id, {field});
 *
 * 设计原因：确保租户隔离查询性能，所有业务查询默认带tenant_id条件。
 *
 * 五、外键约束策略：
 *
 * 规则：应用层维护外键关系，数据库层不创建FOREIGN KEY约束。
 * 设计原因：
 * - 多租户场景下外键约束需包含tenant_id，PostgreSQL不支持跨表的复合外键
 * - 软删除场景下外键约束无法处理is_deleted逻辑
 * - 大数据量下外键约束影响写入性能
 * - 应用层通过Service层校验数据完整性
 */

-- ============================================================
-- 第8步：示例建表（含完整默认值与约束）
-- 说明:     以下为符合本规范的业务表示例，展示完整的字段定义、
--           默认值、NOT NULL约束、索引及COMMENT注释。
--           供后续所有建表DDL参考。
-- ============================================================

/*
 * 示例：系统配置表（sys_config）
 *
 * CREATE TABLE erp_base.sys_config (
 *     -- 业务字段
 *     config_key      VARCHAR(100)    NOT NULL,
 *     config_value    VARCHAR(500),
 *     config_group    VARCHAR(50),
 *     description     VARCHAR(200),
 *     -- 扩展字段（所有业务表预留）
 *     ext_str1        VARCHAR(200),
 *     ext_str2        VARCHAR(200),
 *     ext_num1        DECIMAL(18,8),
 *     ext_json        JSONB,
 *     -- 公共字段（完整10个，含默认值与约束）
 *     id              BIGSERIAL       PRIMARY KEY,
 *     tenant_id       BIGINT          NOT NULL,
 *     created_by      BIGINT,
 *     created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
 *     updated_by      BIGINT,
 *     updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
 *     is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
 *     owner_dept_id   BIGINT,
 *     owner_id        BIGINT,
 *     version         INT             NOT NULL DEFAULT 1
 * );
 *
 * -- 租户索引（所有业务表必须）
 * CREATE INDEX idx_sys_config_tenant_id ON erp_base.sys_config(tenant_id);
 *
 * -- 部分唯一索引（业务唯一字段，WHERE is_deleted = FALSE）
 * CREATE UNIQUE INDEX uk_sys_config_key_active
 *     ON erp_base.sys_config(tenant_id, config_key) WHERE is_deleted = FALSE;
 *
 * -- 表注释
 * COMMENT ON TABLE erp_base.sys_config IS '系统配置表';
 * COMMENT ON COLUMN erp_base.sys_config.config_key IS '配置键';
 * COMMENT ON COLUMN erp_base.sys_config.config_value IS '配置值';
 * COMMENT ON COLUMN erp_base.sys_config.config_group IS '配置分组';
 * COMMENT ON COLUMN erp_base.sys_config.description IS '配置说明';
 * -- 公共字段注释（所有表统一）
 * COMMENT ON COLUMN erp_base.sys_config.id IS '主键ID';
 * COMMENT ON COLUMN erp_base.sys_config.tenant_id IS '租户ID';
 * COMMENT ON COLUMN erp_base.sys_config.created_by IS '创建人ID';
 * COMMENT ON COLUMN erp_base.sys_config.created_at IS '创建时间';
 * COMMENT ON COLUMN erp_base.sys_config.updated_by IS '修改人ID';
 * COMMENT ON COLUMN erp_base.sys_config.updated_at IS '更新时间';
 * COMMENT ON COLUMN erp_base.sys_config.is_deleted IS '是否删除';
 * COMMENT ON COLUMN erp_base.sys_config.owner_dept_id IS '所属部门ID';
 * COMMENT ON COLUMN erp_base.sys_config.owner_id IS '数据负责人ID';
 * COMMENT ON COLUMN erp_base.sys_config.version IS '版本号';
 */

-- ============================================================
-- 第9步：DML幂等性示例
-- 说明:     数据初始化脚本（INSERT/UPSERT）必须使用ON CONFLICT
--           保证幂等性，支持重复执行不报错不重复插入。
--           以下为DML幂等性写法模板。
-- ============================================================

/*
 * DML幂等性规范：
 *
 * 1. INSERT ... ON CONFLICT DO NOTHING
 *    适用场景：主键或唯一索引冲突时跳过，不更新已有数据。
 *
 * 2. INSERT ... ON CONFLICT DO UPDATE SET ...
 *    适用场景：主键或唯一索引冲突时更新指定字段（upsert）。
 *
 * 示例1：初始化预置数据（DO NOTHING，首次插入后不再变更）：
 *
 * INSERT INTO erp_base.sys_config
 *     (tenant_id, config_key, config_value, config_group, description,
 *      created_at, updated_at, is_deleted, version)
 * VALUES
 *     (0, 'system.name', 'ERP AI 智能管理系统', 'system', '系统名称',
 *      CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, 1)
 * ON CONFLICT (tenant_id, config_key) WHERE is_deleted = FALSE
 * DO NOTHING;
 *
 * 示例2：更新或插入（DO UPDATE，数据变更时同步更新）：
 *
 * INSERT INTO erp_base.sys_config
 *     (tenant_id, config_key, config_value, config_group, description,
 *      created_at, updated_at, is_deleted, version)
 * VALUES
 *     (0, 'system.version', '1.0.0', 'system', '系统版本号',
 *      CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, FALSE, 1)
 * ON CONFLICT (tenant_id, config_key) WHERE is_deleted = FALSE
 * DO UPDATE SET
 *     config_value = EXCLUDED.config_value,
 *     description  = EXCLUDED.description,
 *     updated_at   = CURRENT_TIMESTAMP,
 *     version      = erp_base.sys_config.version + 1;
 *
 * 注意事项：
 * - ON CONFLICT子句必须使用与部分唯一索引一致的条件
 * - DO NOTHING用于纯初始化数据（不需更新）
 * - DO UPDATE用于需要同步更新的数据（用小版本号或时间戳）
 * - 更新时必须更新updated_at和version字段
 */

-- ============================================================
-- 验证脚本（可选执行）
-- ============================================================
-- 验证数据库创建
-- SELECT datname, encoding, datcollate, datctype, datconnlimit
-- FROM pg_database WHERE datname = 'erp_db';

-- 验证Schema创建
-- SELECT schema_name FROM information_schema.schemata
-- WHERE schema_name IN ('erp_base', 'erp_tenant');
