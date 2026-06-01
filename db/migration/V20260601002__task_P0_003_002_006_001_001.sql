-- ============================================================
-- ERP AI 系统 - 单据主表特有字段规范定义
-- 任务编号: P0-003-002-006-001-001
-- 文件名:   V20260601002__task_P0_003_002_006_001_001.sql
-- 说明:     定义所有单据主表（采购单/销售单/生产单/委外单/
--           应收应付单/凭证/审批单/期初单等）必须包含的
--           2个特有字段规范：bill_no（单据号）和 bill_status
--           （单据状态），包括字段DDL片段、部分唯一索引模板、
--           枚举值注释模板和COMMENT注释模板。
--           本脚本为规范定义脚本，执行后会创建
--           erp_base.bill_main_field_spec 规范记录表，
--           用于存储单据主表特有字段元数据，供后续建表参考。
-- 执行方式: 连接到 erp_db 后以应用账号执行
-- 数据库:   PostgreSQL 15+
-- 作者:     AI Generated
-- 日期:     2026-06-01
-- ============================================================

-- ============================================================
-- 第1步：创建单据主表特有字段规范记录表
-- 说明:     存储所有单据主表必须包含的特有字段元数据定义
--           （字段名、数据类型、默认值、NOT NULL约束、注释），
--           作为所有单据主表建表时的权威参考。
--           后续建表脚本必须严格遵循此规范。
-- ============================================================

CREATE TABLE IF NOT EXISTS erp_base.bill_main_field_spec (
    field_seq           SMALLINT        NOT NULL,
    field_name          VARCHAR(30)     NOT NULL,
    data_type           VARCHAR(50)     NOT NULL,
    default_value       VARCHAR(100),
    is_not_null         BOOLEAN         NOT NULL DEFAULT FALSE,
    is_unique_active    BOOLEAN         NOT NULL DEFAULT FALSE,
    field_comment       VARCHAR(100)    NOT NULL,
    enum_values         VARCHAR(500),
    design_note         VARCHAR(500),
    sort_order          SMALLINT        NOT NULL DEFAULT 0,
    CONSTRAINT pk_bill_main_field_spec PRIMARY KEY (field_name)
);

COMMENT ON TABLE erp_base.bill_main_field_spec IS '单据主表特有字段规范定义表 - 存储所有单据主表必须包含的特有字段元数据（除10个公共字段外）';
COMMENT ON COLUMN erp_base.bill_main_field_spec.field_seq IS '字段序号';
COMMENT ON COLUMN erp_base.bill_main_field_spec.field_name IS '字段名（snake_case）';
COMMENT ON COLUMN erp_base.bill_main_field_spec.data_type IS 'PostgreSQL数据类型';
COMMENT ON COLUMN erp_base.bill_main_field_spec.default_value IS '默认值（NULL表示无默认值）';
COMMENT ON COLUMN erp_base.bill_main_field_spec.is_not_null IS '是否NOT NULL约束';
COMMENT ON COLUMN erp_base.bill_main_field_spec.is_unique_active IS '是否创建部分唯一索引（WHERE is_deleted=false）';
COMMENT ON COLUMN erp_base.bill_main_field_spec.field_comment IS '字段中文注释';
COMMENT ON COLUMN erp_base.bill_main_field_spec.enum_values IS '枚举值定义（SMALLINT枚举字段时填写，格式：值=含义,值=含义,...）';
COMMENT ON COLUMN erp_base.bill_main_field_spec.design_note IS '设计要点说明';
COMMENT ON COLUMN erp_base.bill_main_field_spec.sort_order IS '排序号';

-- ============================================================
-- 第2步：插入单据主表特有字段规范数据（幂等性保证）
-- ============================================================

INSERT INTO erp_base.bill_main_field_spec
    (field_seq, field_name, data_type, default_value, is_not_null, is_unique_active, field_comment, enum_values, design_note, sort_order)
VALUES
    (1, 'bill_no',
     'VARCHAR(32)',
     NULL,
     TRUE,
     TRUE,
     '单据编号',
     NULL,
     '单据号唯一标识，格式由编码规则引擎（sys_code_rule）生成；部分唯一索引必须包含 WHERE is_deleted = false 和 tenant_id；支持同编号多次删除后重建',
     1),
    (2, 'bill_status',
     'SMALLINT',
     '0',
     TRUE,
     FALSE,
     '单据状态',
     '0=草稿,1=待审核,2=已审核,3=已关闭,4=已作废',
     '状态流转：草稿(0)→待审核(1)→已审核(2)→已关闭(3)；任意状态可跳转至已作废(4)；SMALLINT类型性能优于VARCHAR枚举；显示文本由前端国际化渲染',
     2)
ON CONFLICT (field_name) DO UPDATE SET
    field_seq         = EXCLUDED.field_seq,
    data_type         = EXCLUDED.data_type,
    default_value     = EXCLUDED.default_value,
    is_not_null       = EXCLUDED.is_not_null,
    is_unique_active  = EXCLUDED.is_unique_active,
    field_comment     = EXCLUDED.field_comment,
    enum_values       = EXCLUDED.enum_values,
    design_note       = EXCLUDED.design_note,
    sort_order        = EXCLUDED.sort_order;

-- ============================================================
-- 第3步：单据主表特有字段 DDL 片段模板（注释文档）
-- 说明:     以下为2个单据主表特有字段的标准DDL片段，
--           所有单据主表 CREATE TABLE 必须在业务字段区域
--           包含此片段（放在业务字段之后、扩展字段之前）。
-- ============================================================

/*
 * ===================================================================
 * 单据主表特有字段 DDL 片段模板（直接复制到各 CREATE TABLE 语句中）
 * ===================================================================
 *
 *     bill_no         VARCHAR(32)     NOT NULL,
 *     bill_status     SMALLINT        NOT NULL DEFAULT 0,
 *
 * 注意事项：
 * 1. bill_no 为 NOT NULL，单据号在保存草稿时由编码规则引擎自动生成
 * 2. bill_no 必须配合部分唯一索引使用（见下方索引模板）
 * 3. bill_status NOT NULL DEFAULT 0，新建单据默认为草稿状态
 * 4. bill_status 枚举值：0=草稿, 1=待审核, 2=已审核, 3=已关闭, 4=已作废
 * 5. 状态变更由应用层业务逻辑控制，数据库层仅存储不校验流转规则
 * 6. 单据主表特有字段放在 业务字段 → 扩展字段 → 公共字段 之间
 */

-- ============================================================
-- 第4步：单据主表部分唯一索引模板（注释文档）
-- 说明:     每个单据主表必须为 bill_no 创建部分唯一索引。
--           索引命名格式：uk_{table}_bill_no_active
-- ============================================================

/*
 * ===================================================================
 * 单据主表部分唯一索引模板（每个单据主表 CREATE TABLE 后执行）
 * ===================================================================
 *
 * -- 单据号部分唯一索引（仅对未删除数据强制唯一，支持同编号删除重建）
 * CREATE UNIQUE INDEX uk_{table}_bill_no_active
 *     ON {schema}.{table}(tenant_id, bill_no)
 *     WHERE is_deleted = FALSE;
 *
 * 设计要点：
 * - 联合索引以 tenant_id 为首列，确保多租户隔离查询性能
 * - WHERE is_deleted = FALSE 确保仅对未删除数据强制唯一性
 * - 支持同编号多次删除后重建的经典场景（逻辑删除的唯一索引策略）
 * - 索引命名规范：uk_{表名}_{字段名}_active
 */

-- ============================================================
-- 第5步：bill_status 枚举注释模板（注释文档）
-- 说明:     每个单据主表必须为其 bill_status 字段添加
--           包含完整枚举值定义的 COMMENT。
-- ============================================================

/*
 * ===================================================================
 * bill_status 枚举值定义（所有单据主表统一）
 * ===================================================================
 *
 * | 值 | 状态名称 | 说明                                   |
 * |----|---------|----------------------------------------|
 * | 0  | 草稿    | 新建单据初始状态，可编辑、可删除         |
 * | 1  | 待审核  | 提交审核中，不可编辑，可撤回至草稿       |
 * | 2  | 已审核  | 审核通过，可执行后续业务操作             |
 * | 3  | 已关闭  | 业务流程完结，不可再操作                 |
 * | 4  | 已作废  | 单据作废，不可再操作，任意状态可跳转     |
 *
 * 状态流转规则（应用层校验，非DB约束）：
 *   草稿(0) ──提交──→ 待审核(1) ──通过──→ 已审核(2) ──完结──→ 已关闭(3)
 *     │                  │                   │
 *     └──────作废──────→ 已作废(4) ←─────────┘
 *     任意状态均可跳转至已作废
 *
 * COMMENT模板：
 * COMMENT ON COLUMN {schema}.{table}.bill_status IS
 *     '单据状态：0=草稿/1=待审核/2=已审核/3=已关闭/4=已作废';
 */

-- ============================================================
-- 第6步：单据主表完整建表示例（注释文档）
-- 说明:     展示符合全套规范的单据主表 CREATE TABLE 示例。
--           包含：业务字段 + 单据特有字段 + 扩展字段 +
--           公共字段 + 索引 + COMMENT。
-- ============================================================

/*
 * ===================================================================
 * 完整单据主表建表示例（以采购单主表为例）
 * ===================================================================
 *
 * CREATE TABLE erp_purchase.pur_order (
 *     -- 一、业务字段
 *     supplier_id         BIGINT          NOT NULL,
 *     order_date          DATE            NOT NULL,
 *     expected_arrive_date DATE,
 *     total_amount        DECIMAL(18,8)   NOT NULL DEFAULT 0,
 *     discount_amount     DECIMAL(18,8)   NOT NULL DEFAULT 0,
 *     net_amount          DECIMAL(18,8)   NOT NULL DEFAULT 0,
 *     currency_code       VARCHAR(10)     DEFAULT 'CNY',
 *     exchange_rate       DECIMAL(18,8)   DEFAULT 1,
 *     remark              VARCHAR(500),
 *     -- 二、单据主表特有字段（2个，所有单据主表必须包含）
 *     bill_no             VARCHAR(32)     NOT NULL,
 *     bill_status         SMALLINT        NOT NULL DEFAULT 0,
 *     -- 三、扩展字段（22个，所有业务表预留）
 *     ext_str1            VARCHAR(200),
 *     ext_str2            VARCHAR(200),
 *     ext_str3            VARCHAR(200),
 *     ext_str4            VARCHAR(200),
 *     ext_str5            VARCHAR(200),
 *     ext_str6            VARCHAR(200),
 *     ext_str7            VARCHAR(200),
 *     ext_str8            VARCHAR(200),
 *     ext_str9            VARCHAR(200),
 *     ext_str10           VARCHAR(200),
 *     ext_num1            DECIMAL(18,8),
 *     ext_num2            DECIMAL(18,8),
 *     ext_num3            DECIMAL(18,8),
 *     ext_num4            DECIMAL(18,8),
 *     ext_num5            DECIMAL(18,8),
 *     ext_date1           DATE,
 *     ext_date2           DATE,
 *     ext_date3           DATE,
 *     ext_bool1           BOOLEAN,
 *     ext_bool2           BOOLEAN,
 *     ext_bool3           BOOLEAN,
 *     ext_json            JSONB,
 *     -- 四、公共字段（10个，完整包含，含默认值与约束）
 *     id                  BIGSERIAL       PRIMARY KEY,
 *     tenant_id           BIGINT          NOT NULL,
 *     created_by          BIGINT,
 *     created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
 *     updated_by          BIGINT,
 *     updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
 *     is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
 *     owner_dept_id       BIGINT,
 *     owner_id            BIGINT,
 *     version             INT             NOT NULL DEFAULT 1
 * );
 *
 * -- 租户索引
 * CREATE INDEX idx_pur_order_tenant_id ON erp_purchase.pur_order(tenant_id);
 *
 * -- 单据号部分唯一索引（仅未删除记录）
 * CREATE UNIQUE INDEX uk_pur_order_bill_no_active
 *     ON erp_purchase.pur_order(tenant_id, bill_no)
 *     WHERE is_deleted = FALSE;
 *
 * -- 供应商关联索引
 * CREATE INDEX idx_pur_order_tenant_id_supplier_id
 *     ON erp_purchase.pur_order(tenant_id, supplier_id);
 *
 * -- 下单日期索引
 * CREATE INDEX idx_pur_order_tenant_id_order_date
 *     ON erp_purchase.pur_order(tenant_id, order_date);
 *
 * -- 表注释
 * COMMENT ON TABLE erp_purchase.pur_order IS '采购单主表';
 *
 * -- 业务字段注释
 * COMMENT ON COLUMN erp_purchase.pur_order.supplier_id IS '供应商ID';
 * COMMENT ON COLUMN erp_purchase.pur_order.order_date IS '下单日期';
 * COMMENT ON COLUMN erp_purchase.pur_order.expected_arrive_date IS '预计到货日期';
 * COMMENT ON COLUMN erp_purchase.pur_order.total_amount IS '总金额';
 * COMMENT ON COLUMN erp_purchase.pur_order.discount_amount IS '折扣金额';
 * COMMENT ON COLUMN erp_purchase.pur_order.net_amount IS '净金额';
 * COMMENT ON COLUMN erp_purchase.pur_order.currency_code IS '币种代码';
 * COMMENT ON COLUMN erp_purchase.pur_order.exchange_rate IS '汇率';
 * COMMENT ON COLUMN erp_purchase.pur_order.remark IS '备注';
 *
 * -- 单据特有字段注释
 * COMMENT ON COLUMN erp_purchase.pur_order.bill_no IS '采购单号';
 * COMMENT ON COLUMN erp_purchase.pur_order.bill_status IS '单据状态：0=草稿/1=待审核/2=已审核/3=已关闭/4=已作废';
 *
 * -- 公共字段注释（所有表统一）
 * COMMENT ON COLUMN erp_purchase.pur_order.id IS '主键ID';
 * COMMENT ON COLUMN erp_purchase.pur_order.tenant_id IS '租户ID';
 * COMMENT ON COLUMN erp_purchase.pur_order.created_by IS '创建人ID';
 * COMMENT ON COLUMN erp_purchase.pur_order.created_at IS '创建时间';
 * COMMENT ON COLUMN erp_purchase.pur_order.updated_by IS '修改人ID';
 * COMMENT ON COLUMN erp_purchase.pur_order.updated_at IS '更新时间';
 * COMMENT ON COLUMN erp_purchase.pur_order.is_deleted IS '是否删除';
 * COMMENT ON COLUMN erp_purchase.pur_order.owner_dept_id IS '所属部门ID';
 * COMMENT ON COLUMN erp_purchase.pur_order.owner_id IS '数据负责人ID';
 * COMMENT ON COLUMN erp_purchase.pur_order.version IS '版本号';
 */

-- ============================================================
-- 第7步：单据主表字段结构合规校验函数
-- 说明:     PL/pgSQL函数，接收schema名和表名作为参数，
--           校验目标单据主表是否包含 bill_no 和 bill_status
--           两个特有字段及其NOT NULL约束和默认值是否符合规范。
--           返回校验结果JSON，便于自动化检测。
-- ============================================================

CREATE OR REPLACE FUNCTION erp_base.fn_validate_bill_fields(
    p_schema_name VARCHAR(64),
    p_table_name  VARCHAR(64)
)
RETURNS JSONB
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_result            JSONB;
    v_missing_fields    TEXT[] := '{}';
    v_wrong_defaults    TEXT[] := '{}';
    v_wrong_not_null    TEXT[] := '{}';
    v_wrong_unique_idx  TEXT[] := '{}';
    v_field_count       INT;
    v_has_bill_no_idx   BOOLEAN;
    v_rec               RECORD;
    v_expected          RECORD;
BEGIN
    -- 检查bill_no和bill_status字段是否存在
    SELECT array_agg(field_name ORDER BY field_name)
    INTO v_missing_fields
    FROM erp_base.bill_main_field_spec
    WHERE field_name NOT IN (
        SELECT a.attname
        FROM pg_catalog.pg_attribute a
        WHERE a.attrelid = (p_schema_name || '.' || p_table_name)::regclass
          AND a.attnum > 0
          AND NOT a.attisdropped
    );

    -- 统计已存在的单据特有字段数
    SELECT COUNT(*)
    INTO v_field_count
    FROM pg_catalog.pg_attribute a
    WHERE a.attrelid = (p_schema_name || '.' || p_table_name)::regclass
      AND a.attnum > 0
      AND NOT a.attisdropped
      AND a.attname IN ('bill_no', 'bill_status');

    -- 检查NOT NULL约束和默认值
    FOR v_rec IN
        SELECT
            a.attname::VARCHAR(30) AS field_name,
            pg_get_expr(d.adbin, d.adrelid) AS column_default,
            NOT a.attnotnull AS is_nullable,
            spec.default_value,
            spec.is_not_null,
            spec.is_unique_active
        FROM pg_catalog.pg_attribute a
        LEFT JOIN pg_catalog.pg_attrdef d
            ON (a.attrelid = d.adrelid AND a.attnum = d.adnum)
        JOIN erp_base.bill_main_field_spec spec
            ON a.attname = spec.field_name
        WHERE a.attrelid = (p_schema_name || '.' || p_table_name)::regclass
          AND a.attnum > 0
          AND NOT a.attisdropped
    LOOP
        -- 检查NOT NULL约束
        IF v_rec.is_not_null = TRUE AND v_rec.is_nullable = TRUE THEN
            v_wrong_not_null := array_append(v_wrong_not_null,
                v_rec.field_name || '（规范要求NOT NULL，实际可空）');
        END IF;

        -- 检查默认值
        IF v_rec.default_value IS NOT NULL THEN
            IF v_rec.column_default IS NULL THEN
                v_wrong_defaults := array_append(v_wrong_defaults,
                    v_rec.field_name || '（规范默认值: ' || v_rec.default_value || '，实际无默认值）');
            END IF;
        END IF;
    END LOOP;

    -- 检查bill_no部分唯一索引是否存在
    SELECT EXISTS (
        SELECT 1
        FROM pg_catalog.pg_index i
        JOIN pg_catalog.pg_class c ON i.indexrelid = c.oid
        WHERE i.indrelid = (p_schema_name || '.' || p_table_name)::regclass
          AND c.relname LIKE 'uk_%_bill_no_active'
          AND i.indisunique = TRUE
    ) INTO v_has_bill_no_idx;

    IF NOT v_has_bill_no_idx THEN
        v_wrong_unique_idx := array_append(v_wrong_unique_idx,
            'bill_no部分唯一索引缺失：需创建 CREATE UNIQUE INDEX uk_{table}_bill_no_active ON {schema}.{table}(tenant_id, bill_no) WHERE is_deleted = FALSE');
    END IF;

    -- 构建JSON结果
    v_result := jsonb_build_object(
        'schema', p_schema_name,
        'table', p_table_name,
        'bill_field_count', v_field_count,
        'expected_count', 2,
        'is_complete', (v_missing_fields IS NULL),
        'missing_fields', COALESCE(to_jsonb(v_missing_fields), '[]'::jsonb),
        'default_ok', (v_wrong_defaults = '{}'),
        'wrong_defaults', COALESCE(to_jsonb(v_wrong_defaults), '[]'::jsonb),
        'not_null_ok', (v_wrong_not_null = '{}'),
        'wrong_not_null', COALESCE(to_jsonb(v_wrong_not_null), '[]'::jsonb),
        'unique_index_ok', v_has_bill_no_idx,
        'missing_unique_index', COALESCE(to_jsonb(v_wrong_unique_idx), '[]'::jsonb),
        'overall_pass', (
            v_missing_fields IS NULL
            AND v_wrong_defaults = '{}'
            AND v_wrong_not_null = '{}'
            AND v_has_bill_no_idx
        ),
        'checked_at', NOW()::TEXT
    );

    RETURN v_result;
END;
$$;

COMMENT ON FUNCTION erp_base.fn_validate_bill_fields(VARCHAR, VARCHAR)
    IS '校验目标单据主表是否包含bill_no和bill_status两个特有字段及其NOT NULL/默认值/部分唯一索引合规性，返回JSON结果';

-- ============================================================
-- 第8步：单据主表完整字段清单（注释文档）
-- 说明:     汇总单据主表需要包含的全部字段：
--           业务字段 + 单据特有字段(2) + 扩展字段(22) + 公共字段(10)
--           作为后续所有单据主表建表的完整检查清单。
-- ============================================================

/*
 * ===================================================================
 * 单据主表完整字段清单（34个固定字段 + N个业务字段）
 * ===================================================================
 *
 * 字段分组          | 数量 | 字段列表
 * -------------------|------|-----------------------------------------
 * 业务字段           | N    | 按业务需求定义，如 supplier_id / order_date
 * 单据特有字段       | 2    | bill_no, bill_status
 * 扩展字段           | 22   | ext_str1~10, ext_num1~5, ext_date1~3, ext_bool1~3, ext_json
 * 公共字段           | 10   | id, tenant_id, created_by, created_at, updated_by, updated_at, is_deleted, owner_dept_id, owner_id, version
 *
 * 建表时字段排列顺序（固定）：
 *   业务字段 → 单据特有字段(2) → 扩展字段(22) → 公共字段(10)
 *
 * 关键约束清单：
 * ✅ bill_no VARCHAR(32) NOT NULL — 单据号，由编码规则引擎生成
 * ✅ bill_status SMALLINT NOT NULL DEFAULT 0 — 新建单据默认为草稿
 * ✅ bill_no 部分唯一索引 — CREATE UNIQUE INDEX ... (tenant_id, bill_no) WHERE is_deleted = FALSE
 * ✅ 10个公共字段完整包含 — id/tenant_id/created_by/created_at/updated_by/updated_at/is_deleted/owner_dept_id/owner_id/version
 * ✅ 22个扩展字段预留 — ext_str1~10/ext_num1~5/ext_date1~3/ext_bool1~3/ext_json
 * ✅ 数值字段统一 DECIMAL(18,8)
 * ✅ 联合索引以 tenant_id 为首列
 * ✅ 表级和列级 COMMENT 注释完整（含bill_status枚举值）
 * ✅ 不创建 FOREIGN KEY 约束
 */

-- ============================================================
-- 验证脚本（可选执行）
-- ============================================================

-- 验证规范记录表数据完整性：
-- SELECT field_seq, field_name, data_type, default_value,
--        CASE WHEN is_not_null THEN 'NOT NULL' ELSE 'NULL' END AS nullable,
--        CASE WHEN is_unique_active THEN 'YES' ELSE 'NO' END AS need_unique_idx,
--        field_comment, enum_values
-- FROM erp_base.bill_main_field_spec
-- ORDER BY sort_order;

-- 验证单据特有字段为2个：
-- SELECT COUNT(*) AS bill_field_count FROM erp_base.bill_main_field_spec;
-- 预期结果：bill_field_count = 2

-- 验证bill_status枚举值完整性：
-- SELECT field_name, enum_values
-- FROM erp_base.bill_main_field_spec
-- WHERE enum_values IS NOT NULL;
-- 预期结果：0=草稿,1=待审核,2=已审核,3=已关闭,4=已作废

-- 验证表结构合规性（以 pur_order 表为例，需表已存在）：
-- SELECT erp_base.fn_validate_bill_fields('erp_purchase', 'pur_order');

-- ============================================================
-- 回滚脚本（如需回滚）:
-- DROP TABLE IF EXISTS erp_base.bill_main_field_spec CASCADE;
-- DROP FUNCTION IF EXISTS erp_base.fn_validate_bill_fields(VARCHAR, VARCHAR);
-- ============================================================
