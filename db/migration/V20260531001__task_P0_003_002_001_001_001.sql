-- ============================================================
-- ERP AI 系统 - 公共字段基座 DDL 规范定义
-- 任务编号: P0-003-002-001-001-001
-- 文件名:   V20260531001__task_P0_003_002_001_001_001.sql
-- 说明:     定义所有业务表必须包含的10个通用字段规范，
--           包括字段DDL片段、默认值规则、约束规则、
--           索引模板和COMMENT注释模板。
--           本脚本为规范定义脚本，执行后会创建
--           erp_base.public_field_spec 规范记录表
--           用于存储公共字段元数据，供后续建表参考。
-- 执行方式: 连接到 erp_db 后以应用账号执行
-- 数据库:   PostgreSQL 15+
-- 作者:     AI Generated
-- 日期:     2026-05-31
-- ============================================================

-- ============================================================
-- 第1步：创建公共字段规范记录表
-- 说明:     存储10个通用字段的元数据定义（字段名、数据类型、
--           默认值、NOT NULL约束、注释），作为所有业务表建表
--           时的权威参考。后续建表脚本必须严格遵循此规范。
-- ============================================================

CREATE TABLE IF NOT EXISTS erp_base.public_field_spec (
    field_seq           SMALLINT        NOT NULL,
    field_name          VARCHAR(30)     NOT NULL,
    data_type           VARCHAR(50)     NOT NULL,
    default_value       VARCHAR(100),
    is_not_null         BOOLEAN         NOT NULL DEFAULT FALSE,
    is_primary_key      BOOLEAN         NOT NULL DEFAULT FALSE,
    field_comment       VARCHAR(100)    NOT NULL,
    design_note         VARCHAR(500),
    sort_order          SMALLINT        NOT NULL DEFAULT 0,
    CONSTRAINT pk_public_field_spec PRIMARY KEY (field_name)
);

COMMENT ON TABLE erp_base.public_field_spec IS '公共字段规范定义表 - 存储所有业务表必须包含的通用字段元数据';
COMMENT ON COLUMN erp_base.public_field_spec.field_seq IS '字段序号';
COMMENT ON COLUMN erp_base.public_field_spec.field_name IS '字段名（snake_case）';
COMMENT ON COLUMN erp_base.public_field_spec.data_type IS 'PostgreSQL数据类型';
COMMENT ON COLUMN erp_base.public_field_spec.default_value IS '默认值（NULL表示无默认值）';
COMMENT ON COLUMN erp_base.public_field_spec.is_not_null IS '是否NOT NULL约束';
COMMENT ON COLUMN erp_base.public_field_spec.is_primary_key IS '是否主键';
COMMENT ON COLUMN erp_base.public_field_spec.field_comment IS '字段中文注释';
COMMENT ON COLUMN erp_base.public_field_spec.design_note IS '设计要点说明';
COMMENT ON COLUMN erp_base.public_field_spec.sort_order IS '排序号';

-- ============================================================
-- 第2步：插入10个通用字段规范数据（幂等性保证）
-- ============================================================

INSERT INTO erp_base.public_field_spec
    (field_seq, field_name, data_type, default_value, is_not_null, is_primary_key, field_comment, design_note, sort_order)
VALUES
    (1,  'id',             'BIGSERIAL',    NULL,                      TRUE,  TRUE,  '主键ID',         '自增主键，唯一标识', 1),
    (2,  'tenant_id',      'BIGINT',       NULL,                      TRUE,  FALSE, '租户ID',         '多租户隔离必填，由MyBatis-Plus多租户插件自动注入；联合索引必须以tenant_id为首列', 2),
    (3,  'created_by',     'BIGINT',       NULL,                      FALSE, FALSE, '创建人ID',       '审计字段，记录创建人，由应用层自动填充，创建后永不修改', 3),
    (4,  'created_at',     'TIMESTAMP',    'CURRENT_TIMESTAMP',       TRUE,  FALSE, '创建时间',       '审计字段，自动填充，不可修改', 4),
    (5,  'updated_by',     'BIGINT',       NULL,                      FALSE, FALSE, '修改人ID',       '审计字段，每次UPDATE时由应用层自动刷新', 5),
    (6,  'updated_at',     'TIMESTAMP',    'CURRENT_TIMESTAMP',       TRUE,  FALSE, '更新时间',       '审计字段，每次UPDATE时自动刷新为当前时间', 6),
    (7,  'is_deleted',     'BOOLEAN',      'FALSE',                   TRUE,  FALSE, '是否删除',       '软删除标记，MyBatis-Plus全局逻辑删除插件自动注入 is_deleted=false；部分唯一索引必须包含 WHERE is_deleted = false', 7),
    (8,  'owner_dept_id',  'BIGINT',       NULL,                      FALSE, FALSE, '所属部门ID',     '数据权限字段，默认取创建人所属部门ID，可按需变更；数据权限取值链路：WHERE owner_dept_id IN (用户可见部门)', 8),
    (9,  'owner_id',       'BIGINT',       NULL,                      FALSE, FALSE, '数据负责人ID',   '数据权限字段，默认取created_by值，可按需变更；数据权限取值链路：WHERE owner_id IN (用户可见人员)；禁止使用created_by做数据权限判定', 9),
    (10, 'version',        'INT',          '1',                       TRUE,  FALSE, '版本号',         '乐观锁版本号，每次更新+1，MyBatis-Plus自动处理', 10)
ON CONFLICT (field_name) DO UPDATE SET
    field_seq      = EXCLUDED.field_seq,
    data_type      = EXCLUDED.data_type,
    default_value  = EXCLUDED.default_value,
    is_not_null    = EXCLUDED.is_not_null,
    is_primary_key = EXCLUDED.is_primary_key,
    field_comment  = EXCLUDED.field_comment,
    design_note    = EXCLUDED.design_note,
    sort_order     = EXCLUDED.sort_order;

-- ============================================================
-- 第3步：公共字段 DDL 片段模板（注释文档）
-- 说明:     以下为10个通用字段的标准DDL片段，
--           所有业务表 CREATE TABLE 必须完整复制此片段。
--           字段顺序固定，不可调整，统一放在业务字段之后。
-- ============================================================

/*
 * ===================================================================
 * 公共字段 DDL 片段模板（直接复制到各 CREATE TABLE 语句末尾）
 * ===================================================================
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
 * 注意事项：
 * 1. id 使用 BIGSERIAL（自增），自动创建序列，无需手动设值
 * 2. tenant_id 为 NOT NULL，由 MyBatis-Plus 多租户插件自动注入
 * 3. created_by / updated_by 由应用层赋值，DB层不设默认值
 * 4. created_at / updated_at 使用 DEFAULT CURRENT_TIMESTAMP 兜底
 * 5. is_deleted 必须 NOT NULL DEFAULT FALSE，避免三值逻辑（true/false/NULL）
 * 6. version 初始值 DEFAULT 1，MyBatis-Plus 乐观锁自动递增
 * 7. owner_dept_id / owner_id 用于数据权限判定，创建时可空
 */

-- ============================================================
-- 第4步：索引模板（注释文档）
-- 说明:     每个业务表创建后必须创建以下索引。
-- ============================================================

/*
 * ===================================================================
 * 索引模板（每个业务表 CREATE TABLE 后执行）
 * ===================================================================
 *
 * -- 租户索引（所有业务表必须）
 * CREATE INDEX idx_{table}_tenant_id ON {schema}.{table}(tenant_id);
 *
 * -- 部分唯一索引（业务唯一字段，仅对未删除数据强制唯一）
 * -- 格式：uk_{table}_{field}_active
 * CREATE UNIQUE INDEX uk_{table}_{field}_active
 *     ON {schema}.{table}(tenant_id, {field}) WHERE is_deleted = FALSE;
 *
 * -- 外键关联索引（优化 JOIN 查询）
 * CREATE INDEX idx_{table}_{fk_field} ON {schema}.{table}(tenant_id, {fk_field});
 *
 * 设计要点：
 * - tenant_id 作为联合索引首列，确保租户隔离查询性能
 * - 部分唯一索引 WHERE is_deleted = FALSE，仅约束未删除数据
 * - 外键字段建立索引优化JOIN性能
 * - 禁止使用 FOREIGN KEY 约束（应用层维护外键关系）
 */

-- ============================================================
-- 第5步：COMMENT 注释模板（注释文档）
-- 说明:     每个业务表的10个公共字段统一使用以下注释。
-- ============================================================

/*
 * ===================================================================
 * COMMENT 注释模板（每个业务表 CREATE TABLE 后执行）
 * ===================================================================
 *
 * COMMENT ON COLUMN {schema}.{table}.id IS '主键ID';
 * COMMENT ON COLUMN {schema}.{table}.tenant_id IS '租户ID';
 * COMMENT ON COLUMN {schema}.{table}.created_by IS '创建人ID';
 * COMMENT ON COLUMN {schema}.{table}.created_at IS '创建时间';
 * COMMENT ON COLUMN {schema}.{table}.updated_by IS '修改人ID';
 * COMMENT ON COLUMN {schema}.{table}.updated_at IS '更新时间';
 * COMMENT ON COLUMN {schema}.{table}.is_deleted IS '是否删除';
 * COMMENT ON COLUMN {schema}.{table}.owner_dept_id IS '所属部门ID';
 * COMMENT ON COLUMN {schema}.{table}.owner_id IS '数据负责人ID';
 * COMMENT ON COLUMN {schema}.{table}.version IS '版本号';
 */

-- ============================================================
-- 第6步：扩展字段模板（注释文档）
-- 说明:     所有业务表预留的自定义扩展字段。
-- ============================================================

/*
 * ===================================================================
 * 扩展字段 DDL 片段（所有业务表预留，放在业务字段之后、公共字段之前）
 * ===================================================================
 *
 *     ext_str1        VARCHAR(200),
 *     ext_str2        VARCHAR(200),
 *     ext_str3        VARCHAR(200),
 *     ext_str4        VARCHAR(200),
 *     ext_str5        VARCHAR(200),
 *     ext_str6        VARCHAR(200),
 *     ext_str7        VARCHAR(200),
 *     ext_str8        VARCHAR(200),
 *     ext_str9        VARCHAR(200),
 *     ext_str10       VARCHAR(200),
 *     ext_num1        DECIMAL(18,8),
 *     ext_num2        DECIMAL(18,8),
 *     ext_num3        DECIMAL(18,8),
 *     ext_num4        DECIMAL(18,8),
 *     ext_num5        DECIMAL(18,8),
 *     ext_date1       DATE,
 *     ext_date2       DATE,
 *     ext_date3       DATE,
 *     ext_bool1       BOOLEAN,
 *     ext_bool2       BOOLEAN,
 *     ext_bool3       BOOLEAN,
 *     ext_json        JSONB,
 *
 * 说明：
 * - ext_str1~10: 用户自定义字符串字段，通过字段配置界面设置中文标题和校验规则
 * - ext_num1~5:  用户自定义数值字段，DDL统一decimal(18,8)，显示精度由系统参数控制
 * - ext_date1~3: 用户自定义日期字段
 * - ext_bool1~3: 用户自定义布尔字段
 * - ext_json:    预留JSON扩展字段，存储动态结构化扩展数据
 */

-- ============================================================
-- 第7步：完整示例建表（注释文档）
-- 说明:     展示符合全套规范的完整 CREATE TABLE 示例。
--           包含：业务字段 + 扩展字段 + 公共字段 + 索引 + COMMENT
-- ============================================================

/*
 * ===================================================================
 * 完整建表示例（以 sys_config 为例）
 * ===================================================================
 *
 * CREATE TABLE erp_base.sys_config (
 *     -- 业务字段
 *     config_key      VARCHAR(100)    NOT NULL,
 *     config_value    VARCHAR(500),
 *     config_group    VARCHAR(50),
 *     description     VARCHAR(200),
 *     -- 扩展字段
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
 * -- 租户索引
 * CREATE INDEX idx_sys_config_tenant_id ON erp_base.sys_config(tenant_id);
 *
 * -- 部分唯一索引（业务唯一字段）
 * CREATE UNIQUE INDEX uk_sys_config_key_active
 *     ON erp_base.sys_config(tenant_id, config_key) WHERE is_deleted = FALSE;
 *
 * -- 表注释
 * COMMENT ON TABLE erp_base.sys_config IS '系统配置表';
 *
 * -- 业务字段注释
 * COMMENT ON COLUMN erp_base.sys_config.config_key IS '配置键';
 * COMMENT ON COLUMN erp_base.sys_config.config_value IS '配置值';
 * COMMENT ON COLUMN erp_base.sys_config.config_group IS '配置分组';
 * COMMENT ON COLUMN erp_base.sys_config.description IS '配置说明';
 *
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
-- 验证脚本（可选执行）
-- ============================================================

-- 验证规范记录表数据完整性：
-- SELECT field_seq, field_name, data_type, default_value,
--        is_not_null, field_comment
-- FROM erp_base.public_field_spec
-- ORDER BY sort_order;

-- 验证通用字段为10个：
-- SELECT COUNT(*) AS field_count FROM erp_base.public_field_spec;
-- 预期结果：field_count = 10

-- ============================================================
-- 回滚脚本（如需回滚）:
-- DROP TABLE IF EXISTS erp_base.public_field_spec CASCADE;
-- ============================================================
