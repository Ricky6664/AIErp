-- ============================================================
-- ERP AI 系统 - 公共字段默认值与约束规范定义
-- 任务编号: P0-003-002-001-001-002
-- 文件名:   V20260531002__task_P0_003_002_001_001_002.sql
-- 说明:     定义所有业务表必须遵循的10个通用字段默认值规则和
--           约束规则，包括默认值规范表、约束规范表以及PL/pgSQL
--           表结构校验函数，确保所有业务建表DDL严格合规。
-- 执行方式: 连接到 erp_db 后以应用账号执行
-- 数据库:   PostgreSQL 15+
-- 作者:     AI Generated
-- 日期:     2026-05-31
-- ============================================================

-- ============================================================
-- 第1步：创建公共字段默认值规范表
-- 说明:     存储10个通用字段的默认值规则定义，包括字段名、
--           默认值表达式、是否允许NULL覆盖、默认值策略说明。
--           作为所有业务表建表时字段默认值的权威参考。
-- ============================================================

CREATE TABLE IF NOT EXISTS erp_base.public_default_value_spec (
    field_name          VARCHAR(30)     NOT NULL,
    default_expression  VARCHAR(100),
    not_null_constraint BOOLEAN         NOT NULL DEFAULT FALSE,
    default_strategy    VARCHAR(200)    NOT NULL,
    usage_note          VARCHAR(500),
    sort_order          SMALLINT        NOT NULL DEFAULT 0,
    CONSTRAINT pk_public_default_value_spec PRIMARY KEY (field_name)
);

COMMENT ON TABLE erp_base.public_default_value_spec IS '公共字段默认值规范表 - 存储10个通用字段的默认值规则';
COMMENT ON COLUMN erp_base.public_default_value_spec.field_name IS '字段名（snake_case）';
COMMENT ON COLUMN erp_base.public_default_value_spec.default_expression IS '默认值表达式（NULL表示DB层不设默认值，由应用层赋值）';
COMMENT ON COLUMN erp_base.public_default_value_spec.not_null_constraint IS '是否NOT NULL约束';
COMMENT ON COLUMN erp_base.public_default_value_spec.default_strategy IS '默认值策略说明';
COMMENT ON COLUMN erp_base.public_default_value_spec.usage_note IS '使用注意事项';
COMMENT ON COLUMN erp_base.public_default_value_spec.sort_order IS '排序号';

-- ============================================================
-- 第2步：插入10个通用字段的默认值规范数据（幂等性保证）
-- ============================================================

INSERT INTO erp_base.public_default_value_spec
    (field_name, default_expression, not_null_constraint, default_strategy, usage_note, sort_order)
VALUES
    ('id',
     NULL,
     TRUE,
     'BIGSERIAL自增，序列自动生成，无需手动设默认值',
     '禁止手动指定id值；序列命名格式：{table}_id_seq',
     1),
    ('tenant_id',
     NULL,
     TRUE,
     'NOT NULL，DB层不设默认值，由MyBatis-Plus多租户插件自动注入',
     '联合索引必须以tenant_id为首列；所有业务查询默认带tenant_id条件',
     2),
    ('created_by',
     NULL,
     FALSE,
     'DB层不设默认值，由应用层自动填充当前登录用户ID（审计字段）',
     '首次创建后永不修改；批量导入或定时任务场景允许为NULL',
     3),
    ('created_at',
     'CURRENT_TIMESTAMP',
     TRUE,
     'NOT NULL DEFAULT CURRENT_TIMESTAMP，DB层兜底，应用层设精确值',
     '审计字段，创建后不可修改；UPDATE语句禁止修改此字段',
     4),
    ('updated_by',
     NULL,
     FALSE,
     'DB层不设默认值，由应用层每次UPDATE自动刷新当前登录用户ID',
     '首次创建时为NULL（无修改操作）；每次UPDATE由应用层自动填充',
     5),
    ('updated_at',
     'CURRENT_TIMESTAMP',
     TRUE,
     'NOT NULL DEFAULT CURRENT_TIMESTAMP，DB层兜底，应用层每次UPDATE刷新',
     '每次UPDATE必须自动刷新为当前时间；应用层需配合@TableField(update=\"NOW()\")',
     6),
    ('is_deleted',
     'FALSE',
     TRUE,
     'NOT NULL DEFAULT FALSE，避免三值逻辑(true/false/NULL)',
     'MyBatis-Plus全局逻辑删除插件自动注入WHERE is_deleted=false；部分唯一索引必须包含WHERE is_deleted=FALSE条件',
     7),
    ('owner_dept_id',
     NULL,
     FALSE,
     'DB层不设默认值，由应用层默认取创建人所属部门ID，可按需变更',
     '数据权限判定字段：WHERE owner_dept_id IN (用户可见部门)；创建时允许为NULL，后续由业务逻辑填充',
     8),
    ('owner_id',
     NULL,
     FALSE,
     'DB层不设默认值，由应用层默认取created_by值，可按需变更',
     '数据权限判定字段：WHERE owner_id IN (用户可见人员)；禁止使用created_by做数据权限判定',
     9),
    ('version',
     '1',
     TRUE,
     'NOT NULL DEFAULT 1，乐观锁版本号，每次UPDATE由MyBatis-Plus自动+1',
     'MyBatis-Plus乐观锁插件自动处理；应用层禁止手动修改此字段',
     10)
ON CONFLICT (field_name) DO UPDATE SET
    default_expression  = EXCLUDED.default_expression,
    not_null_constraint = EXCLUDED.not_null_constraint,
    default_strategy    = EXCLUDED.default_strategy,
    usage_note          = EXCLUDED.usage_note,
    sort_order          = EXCLUDED.sort_order;

-- ============================================================
-- 第3步：创建公共字段约束规范表
-- 说明:     存储约束类型定义（NOT NULL约束、部分唯一索引约束、
--           多租户联合索引约束、COMMENT注释要求）及适用规则。
--           作为所有业务表建表时约束配置的权威参考。
-- ============================================================

CREATE TABLE IF NOT EXISTS erp_base.public_constraint_spec (
    constraint_code     VARCHAR(50)     NOT NULL,
    constraint_type     VARCHAR(30)     NOT NULL,
    constraint_name     VARCHAR(100)    NOT NULL,
    applies_to_fields   VARCHAR(500),
    constraint_sql_template VARCHAR(500),
    is_mandatory        BOOLEAN         NOT NULL DEFAULT TRUE,
    design_rationale    VARCHAR(500),
    sort_order          SMALLINT        NOT NULL DEFAULT 0,
    CONSTRAINT pk_public_constraint_spec PRIMARY KEY (constraint_code)
);

COMMENT ON TABLE erp_base.public_constraint_spec IS '公共字段约束规范表 - 存储所有业务表必须遵循的约束规则';
COMMENT ON COLUMN erp_base.public_constraint_spec.constraint_code IS '约束编码（唯一标识）';
COMMENT ON COLUMN erp_base.public_constraint_spec.constraint_type IS '约束类型（NOT_NULL / UNIQUE_INDEX / TENANT_INDEX / COMMENT / FK_POLICY）';
COMMENT ON COLUMN erp_base.public_constraint_spec.constraint_name IS '约束名称（中文）';
COMMENT ON COLUMN erp_base.public_constraint_spec.applies_to_fields IS '适用的公共字段（逗号分隔）';
COMMENT ON COLUMN erp_base.public_constraint_spec.constraint_sql_template IS '约束SQL模板';
COMMENT ON COLUMN erp_base.public_constraint_spec.is_mandatory IS '是否强制约束';
COMMENT ON COLUMN erp_base.public_constraint_spec.design_rationale IS '设计理由';
COMMENT ON COLUMN erp_base.public_constraint_spec.sort_order IS '排序号';

-- ============================================================
-- 第4步：插入约束规范数据（幂等性保证）
-- ============================================================

INSERT INTO erp_base.public_constraint_spec
    (constraint_code, constraint_type, constraint_name, applies_to_fields, constraint_sql_template, is_mandatory, design_rationale, sort_order)
VALUES
    ('C001',
     'NOT_NULL',
     '必填字段NOT NULL约束',
     'id, tenant_id, created_at, updated_at, is_deleted, version',
     '字段声明必须包含 NOT NULL',
     TRUE,
     '6个字段必须有值：id(PK自增)、tenant_id(多租户隔离)、created_at/updated_at(审计必备)、is_deleted(避免三值逻辑)、version(乐观锁必备)',
     1),
    ('C002',
     'NOT_NULL',
     '可空字段（应用层处理）',
     'created_by, updated_by, owner_dept_id, owner_id',
     '字段声明不设 NOT NULL',
     TRUE,
     '4个字段允许NULL：created_by(定时任务无用户上下文)、updated_by(首次创建无修改)、owner_dept_id/owner_id(创建时可能未确定)',
     2),
    ('C003',
     'TENANT_INDEX',
     '租户索引约束',
     'tenant_id',
     'CREATE INDEX idx_{table}_tenant_id ON {schema}.{table}(tenant_id)',
     TRUE,
     '所有业务表必须创建租户索引，确保租户隔离查询性能',
     3),
    ('C004',
     'UNIQUE_INDEX',
     '部分唯一索引约束（软删除场景）',
     '业务唯一字段',
     'CREATE UNIQUE INDEX uk_{table}_{field}_active ON {schema}.{table}(tenant_id, {field}) WHERE is_deleted = FALSE',
     TRUE,
     '仅对未删除数据强制唯一性；支持同code多次删除后重建；is_deleted为BOOLEAN类型，纳入联合唯一索引会导致无法多次删除重建',
     4),
    ('C005',
     'COMMENT',
     '表注释约束',
     '全表',
     'COMMENT ON TABLE {schema}.{table} IS ''{description}''',
     TRUE,
     '所有表必须包含COMMENT ON TABLE注释',
     5),
    ('C006',
     'COMMENT',
     '列注释约束',
     '全字段',
     'COMMENT ON COLUMN {schema}.{table}.{column} IS ''{description}''',
     TRUE,
     '所有字段必须包含COMMENT ON COLUMN注释',
     6),
    ('C007',
     'FK_POLICY',
     '外键策略约束',
     '关联字段',
     '应用层维护外键关系，数据库层不创建FOREIGN KEY约束',
     TRUE,
     '多租户场景外键需含tenant_id（PG不支持跨表复合外键）；软删除场景外键无法处理is_deleted；大数据量影响写入性能',
     7),
    ('C008',
     'TENANT_FIRST',
     '联合索引多租户首列约束',
     'tenant_id',
     '所有联合索引必须以tenant_id为首列：CREATE INDEX idx_{table}_{cols} ON {schema}.{table}(tenant_id, {cols})',
     TRUE,
     '确保所有租户隔离查询能有效利用索引；PostgreSQL B-tree索引最左匹配原则',
     8),
    ('C009',
     'DECIMAL_PRECISION',
     '数值精度约束',
     '金额/单价/数量/转换率字段',
     '数值字段统一使用 DECIMAL(18,8)',
     TRUE,
     '统一精度保证计算一致性；18位总长度+8位小数精度覆盖业务所有场景；显示精度由系统参数动态控制',
     9),
    ('C010',
     'EXT_FIELD_TEMPLATE',
     '扩展字段预留约束',
     'ext_*系列22个预留字段',
     '每个业务表必须包含22个扩展字段：ext_str1~10(VARCHAR(200))、ext_num1~5(DECIMAL(18,8))、ext_date1~3(DATE)、ext_bool1~3(BOOLEAN)、ext_json(JSONB)',
     TRUE,
     '预留扩展字段支持租户自定义扩展；通过字段配置界面设置中文标题和校验规则',
     10)
ON CONFLICT (constraint_code) DO UPDATE SET
    constraint_type       = EXCLUDED.constraint_type,
    constraint_name       = EXCLUDED.constraint_name,
    applies_to_fields     = EXCLUDED.applies_to_fields,
    constraint_sql_template = EXCLUDED.constraint_sql_template,
    is_mandatory          = EXCLUDED.is_mandatory,
    design_rationale      = EXCLUDED.design_rationale,
    sort_order            = EXCLUDED.sort_order;

-- ============================================================
-- 第5步：创建表结构合规校验函数
-- 说明:     PL/pgSQL函数，接收schema名和表名作为参数，
--           校验目标表是否包含全部10个通用字段及其默认值
--           和NOT NULL约束是否符合规范定义。
--           返回校验结果JSON，便于自动化检测。
-- ============================================================

CREATE OR REPLACE FUNCTION erp_base.fn_validate_common_fields(
    p_schema_name VARCHAR(64),
    p_table_name  VARCHAR(64)
)
RETURNS JSONB
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_result          JSONB;
    v_missing_fields  TEXT[] := '{}';
    v_wrong_defaults  TEXT[] := '{}';
    v_wrong_not_null  TEXT[] := '{}';
    v_field_count     INT;
    v_rec             RECORD;
    v_expected        RECORD;
BEGIN
    -- 收集目标表已存在的公共字段信息
    CREATE TEMP TABLE IF NOT EXISTS _tmp_validate_fields (
        field_name       VARCHAR(30),
        has_default      BOOLEAN,
        column_default   TEXT,
        is_nullable      BOOLEAN,
        data_type        VARCHAR(50)
    ) ON COMMIT DROP;

    TRUNCATE _tmp_validate_fields;

    INSERT INTO _tmp_validate_fields (field_name, has_default, column_default, is_nullable, data_type)
    SELECT
        a.attname::VARCHAR(30),
        (pg_get_expr(d.adbin, d.adrelid) IS NOT NULL),
        pg_get_expr(d.adbin, d.adrelid),
        NOT a.attnotnull,
        pg_catalog.format_type(a.atttypid, a.atttypmod)
    FROM pg_catalog.pg_attribute a
    LEFT JOIN pg_catalog.pg_attrdef d ON (a.attrelid = d.adrelid AND a.attnum = d.adnum)
    WHERE a.attrelid = (p_schema_name || '.' || p_table_name)::regclass
      AND a.attnum > 0
      AND NOT a.attisdropped
      AND a.attname IN (
          'id', 'tenant_id', 'created_by', 'created_at', 'updated_by',
          'updated_at', 'is_deleted', 'owner_dept_id', 'owner_id', 'version'
      );

    -- 检查缺失字段
    SELECT array_agg(field_name ORDER BY field_name)
    INTO v_missing_fields
    FROM (
        SELECT field_name FROM erp_base.public_default_value_spec
        WHERE field_name NOT IN (
            SELECT tf.field_name FROM _tmp_validate_fields tf
        )
    ) sub;

    -- 统计已存在的公共字段数
    SELECT COUNT(*) INTO v_field_count FROM _tmp_validate_fields;

    -- 检查默认值违规（遍历已存在的字段，与规范对比）
    FOR v_rec IN
        SELECT tf.field_name, tf.column_default, tf.is_nullable, tf.data_type,
               spec.default_expression, spec.not_null_constraint
        FROM _tmp_validate_fields tf
        JOIN erp_base.public_default_value_spec spec ON tf.field_name = spec.field_name
    LOOP
        -- 检查 NOT NULL 约束
        IF v_rec.not_null_constraint = TRUE AND v_rec.is_nullable = TRUE THEN
            v_wrong_not_null := array_append(v_wrong_not_null,
                v_rec.field_name || '（规范要求NOT NULL，实际可空）');
        ELSIF v_rec.not_null_constraint = FALSE AND v_rec.is_nullable = FALSE THEN
            v_wrong_not_null := array_append(v_wrong_not_null,
                v_rec.field_name || '（规范允许可空，实际NOT NULL）');
        END IF;

        -- 检查默认值（仅对规范有默认值的字段检查）
        IF v_rec.default_expression IS NOT NULL THEN
            IF v_rec.column_default IS NULL THEN
                v_wrong_defaults := array_append(v_wrong_defaults,
                    v_rec.field_name || '（规范默认值: ' || v_rec.default_expression || '，实际无默认值）');
            END IF;
        END IF;
    END LOOP;

    -- 构建JSON结果
    v_result := jsonb_build_object(
        'schema', p_schema_name,
        'table', p_table_name,
        'common_field_count', v_field_count,
        'expected_count', 10,
        'is_complete', (v_missing_fields IS NULL),
        'missing_fields', COALESCE(to_jsonb(v_missing_fields), '[]'::jsonb),
        'default_ok', (v_wrong_defaults = '{}'),
        'wrong_defaults', COALESCE(to_jsonb(v_wrong_defaults), '[]'::jsonb),
        'not_null_ok', (v_wrong_not_null = '{}'),
        'wrong_not_null', COALESCE(to_jsonb(v_wrong_not_null), '[]'::jsonb),
        'overall_pass', (v_missing_fields IS NULL AND v_wrong_defaults = '{}' AND v_wrong_not_null = '{}'),
        'checked_at', NOW()::TEXT
    );

    RETURN v_result;
END;
$$;

COMMENT ON FUNCTION erp_base.fn_validate_common_fields(VARCHAR, VARCHAR)
    IS '校验目标表是否包含全部10个通用字段及其默认值/NOT NULL约束合规性，返回JSON结果';

-- ============================================================
-- 第6步：创建建表DDL默认值与约束合规检查函数
-- 说明:     批量检查指定Schema下所有业务表的公共字段合规性。
--           用于CI/CD流水线或代码审查时的自动化检测。
-- ============================================================

CREATE OR REPLACE FUNCTION erp_base.fn_batch_validate_schema(
    p_schema_name VARCHAR(64)
)
RETURNS TABLE(
    table_name       VARCHAR(128),
    common_fields    INT,
    is_complete      BOOLEAN,
    default_ok       BOOLEAN,
    not_null_ok      BOOLEAN,
    overall_pass     BOOLEAN,
    detail           JSONB
)
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_table_rec RECORD;
    v_result    JSONB;
BEGIN
    FOR v_table_rec IN
        SELECT tablename
        FROM pg_catalog.pg_tables
        WHERE schemaname = p_schema_name
          AND tablename NOT IN ('public_field_spec', 'public_default_value_spec', 'public_constraint_spec')
          AND tablename NOT LIKE '\_%'  -- 排除以_开头的内部表
        ORDER BY tablename
    LOOP
        v_result := erp_base.fn_validate_common_fields(p_schema_name, v_table_rec.tablename);
        table_name    := v_table_rec.tablename;
        common_fields := (v_result->>'common_field_count')::INT;
        is_complete   := (v_result->>'is_complete')::BOOLEAN;
        default_ok    := (v_result->>'default_ok')::BOOLEAN;
        not_null_ok   := (v_result->>'not_null_ok')::BOOLEAN;
        overall_pass  := (v_result->>'overall_pass')::BOOLEAN;
        detail        := v_result;
        RETURN NEXT;
    END LOOP;
END;
$$;

COMMENT ON FUNCTION erp_base.fn_batch_validate_schema(VARCHAR)
    IS '批量校验指定Schema下所有业务表的公共字段合规性，逐表返回校验结果';

-- ============================================================
-- 第7步：完整建表DDL模板（含默认值与约束）
-- 说明:     展示符合本规范全部要求的完整建表DDL示例，
--           包含字段默认值、NOT NULL约束、索引和COMMENT注释。
--           作为后续所有建表DDL的标准参考模板。
-- ============================================================

/*
 * ===================================================================
 * 完整建表DDL标准模板（所有业务表严格遵循）
 * ===================================================================
 *
 * CREATE TABLE {schema}.{table} (
 *     -- 一、业务字段
 *     {business_field_1}  {data_type}  [{NOT NULL}] [DEFAULT {value}],
 *     {business_field_2}  {data_type}  [{NOT NULL}] [DEFAULT {value}],
 *     ...
 *     -- 二、扩展字段（22个，所有业务表预留）
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
 *     -- 三、公共字段（10个，完整包含，含默认值与NOT NULL约束，顺序固定）
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
 * -- 四、租户索引（所有业务表必须）
 * CREATE INDEX idx_{table}_tenant_id ON {schema}.{table}(tenant_id);
 *
 * -- 五、部分唯一索引（业务唯一字段，必须含WHERE is_deleted = FALSE）
 * CREATE UNIQUE INDEX uk_{table}_{field}_active
 *     ON {schema}.{table}(tenant_id, {field}) WHERE is_deleted = FALSE;
 *
 * -- 六、表注释（必须）
 * COMMENT ON TABLE {schema}.{table} IS '{表中文描述}';
 *
 * -- 七、业务字段注释（必须，每列）
 * COMMENT ON COLUMN {schema}.{table}.{column} IS '{字段中文描述}';
 *
 * -- 八、公共字段注释（必须，10个，统一模板）
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
 *
 * 关键约束清单：
 * ✅ 10个公共字段完整包含，顺序固定
 * ✅ 6个NOT NULL字段: id(PK), tenant_id, created_at, updated_at, is_deleted, version
 * ✅ 3个DEFAULT值: created_at→CURRENT_TIMESTAMP, updated_at→CURRENT_TIMESTAMP, is_deleted→FALSE, version→1
 * ✅ 部分唯一索引含 WHERE is_deleted = FALSE
 * ✅ 联合索引以tenant_id为首列
 * ✅ 数值字段统一 DECIMAL(18,8)
 * ✅ 表级和列级COMMENT注释完整
 * ✅ 不创建FOREIGN KEY约束
 * ✅ 22个扩展字段预留
 */

-- ============================================================
-- 验证脚本（可选执行）
-- ============================================================

-- 验证默认值规范表数据完整性：
-- SELECT sort_order, field_name, default_expression,
--        CASE WHEN not_null_constraint THEN 'NOT NULL' ELSE 'NULL' END AS nullable,
--        default_strategy
-- FROM erp_base.public_default_value_spec
-- ORDER BY sort_order;

-- 验证约束规范表数据完整性：
-- SELECT sort_order, constraint_code, constraint_type, constraint_name, is_mandatory
-- FROM erp_base.public_constraint_spec
-- ORDER BY sort_order;

-- 验证默认值规范表共10条记录：
-- SELECT COUNT(*) AS field_count FROM erp_base.public_default_value_spec;
-- 预期结果：field_count = 10

-- 验证约束规范表共10条记录：
-- SELECT COUNT(*) AS constraint_count FROM erp_base.public_constraint_spec;
-- 预期结果：constraint_count = 10

-- 验证表结构合规性（以 public_field_spec 表为例）：
-- SELECT erp_base.fn_validate_common_fields('erp_base', 'public_field_spec');

-- ============================================================
-- 回滚脚本（如需回滚）:
-- DROP TABLE IF EXISTS erp_base.public_default_value_spec CASCADE;
-- DROP TABLE IF EXISTS erp_base.public_constraint_spec CASCADE;
-- DROP FUNCTION IF EXISTS erp_base.fn_validate_common_fields(VARCHAR, VARCHAR);
-- DROP FUNCTION IF EXISTS erp_base.fn_batch_validate_schema(VARCHAR);
-- ============================================================
