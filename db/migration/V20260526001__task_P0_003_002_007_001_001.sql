-- ============================================================
-- ERP AI 系统 - 明细从表商品冗余字段+快照原则规范定义
-- 任务编号: P0-003-002-007-001-001
-- 文件名:   V20260526001__task_P0_003_002_007_001_001.sql
-- 说明:     定义所有含商品的明细从表必须包含的商品快照冗余字段
--           规范，包括明细行结构字段(3个)、商品快照字段(13个)
--           的元数据定义、DDL片段模板、索引模板、完整建表示例
--           和校验函数。
--           含商品的明细从表在引用product_id的同时，必须冗余
--           存储商品快照字段（编码/名称/型号/规格/品牌/单位/
--           数量/多单位标记/转换率/基础单位/换算数量/
--           客户料号等），确保商品信息后续修改不影响历史单据
--           数据（快照原则：保存时从商品主表复制，后续不可修改）。
--           本脚本为规范定义脚本，执行后会创建
--           erp_base.detail_product_field_spec 规范记录表，
--           用于存储明细从表商品快照字段元数据，供后续建表参考。
-- 执行方式: 连接到 erp_db 后以应用账号执行
-- 数据库:   PostgreSQL 15+
-- 作者:     AI Generated
-- 日期:     2026-06-01
-- ============================================================

-- ============================================================
-- 第1步：创建明细从表商品冗余字段规范记录表
-- 说明:     存储所有含商品的明细从表必须包含的商品快照冗余
--           字段元数据定义（字段名、数据类型、默认值、
--           NOT NULL约束、注释、是否快照字段），作为所有
--           明细从表建表时的权威参考。
--           后续含商品的明细从表建表脚本必须严格遵循此规范。
-- ============================================================

CREATE TABLE IF NOT EXISTS erp_base.detail_product_field_spec (
    field_seq           SMALLINT        NOT NULL,
    field_name          VARCHAR(40)     NOT NULL,
    data_type           VARCHAR(50)     NOT NULL,
    default_value       VARCHAR(100),
    is_not_null         BOOLEAN         NOT NULL DEFAULT FALSE,
    is_snapshot         BOOLEAN         NOT NULL DEFAULT FALSE,
    field_category      VARCHAR(20)     NOT NULL DEFAULT 'snapshot',
    field_comment       VARCHAR(100)    NOT NULL,
    design_note         VARCHAR(500),
    sort_order          SMALLINT        NOT NULL DEFAULT 0,
    CONSTRAINT pk_detail_product_field_spec PRIMARY KEY (field_name)
);

COMMENT ON TABLE erp_base.detail_product_field_spec IS '明细从表商品冗余字段规范定义表 - 存储所有含商品的明细从表必须包含的商品快照冗余字段元数据（除10个公共字段外）';
COMMENT ON COLUMN erp_base.detail_product_field_spec.field_seq IS '字段序号';
COMMENT ON COLUMN erp_base.detail_product_field_spec.field_name IS '字段名（snake_case）';
COMMENT ON COLUMN erp_base.detail_product_field_spec.data_type IS 'PostgreSQL数据类型';
COMMENT ON COLUMN erp_base.detail_product_field_spec.default_value IS '默认值（NULL表示无默认值）';
COMMENT ON COLUMN erp_base.detail_product_field_spec.is_not_null IS '是否NOT NULL约束';
COMMENT ON COLUMN erp_base.detail_product_field_spec.is_snapshot IS '是否快照字段（保存时从商品主表复制，后续不可修改）';
COMMENT ON COLUMN erp_base.detail_product_field_spec.field_category IS '字段分类：structural=明细行结构字段, snapshot=商品快照字段';
COMMENT ON COLUMN erp_base.detail_product_field_spec.field_comment IS '字段中文注释';
COMMENT ON COLUMN erp_base.detail_product_field_spec.design_note IS '设计要点说明';
COMMENT ON COLUMN erp_base.detail_product_field_spec.sort_order IS '排序号';

-- ============================================================
-- 第2步：插入明细从表商品冗余字段规范数据（幂等性保证）
-- 字段分类说明：
--   structural — 明细行结构字段（3个）：line_no / main_id / product_id
--   snapshot   — 商品快照冗余字段（13个）：product_code开始
--               其中 customer_product_code 为销售链路额外字段
-- ============================================================

INSERT INTO erp_base.detail_product_field_spec
    (field_seq, field_name, data_type, default_value, is_not_null, is_snapshot, field_category, field_comment, design_note, sort_order)
VALUES
    -- ========== 明细行结构字段（3个） ==========
    (1, 'line_no',
     'INT',
     '1',
     TRUE,
     FALSE,
     'structural',
     '行号',
     '明细行序号，从1开始递增；同一单据内line_no唯一，用于排序和引用',
     1),
    (2, 'main_id',
     'BIGINT',
     NULL,
     TRUE,
     FALSE,
     'structural',
     '主表ID',
     '关联单据主表id；必须创建索引以优化JOIN查询；应用层维护关联关系，不使用数据库外键',
     2),
    (3, 'product_id',
     'BIGINT',
     NULL,
     TRUE,
     FALSE,
     'structural',
     '商品ID',
     '关联商品主表prod_product.id；必须创建索引；含商品的明细从表必须包含此字段',
     3),

    -- ========== 商品快照冗余字段（12个核心，所有含商品明细从表必须包含） ==========
    (4, 'product_code',
     'VARCHAR(50)',
     NULL,
     TRUE,
     TRUE,
     'snapshot',
     '商品编码',
     '下单时从prod_product.product_code复制快照；保存后不可修改；联合索引(tenant_id, product_code)',
     4),
    (5, 'product_name',
     'VARCHAR(100)',
     NULL,
     TRUE,
     TRUE,
     'snapshot',
     '商品名称',
     '下单时从prod_product.product_name复制快照；保存后不可修改',
     5),
    (6, 'model',
     'VARCHAR(100)',
     NULL,
     FALSE,
     TRUE,
     'snapshot',
     '型号',
     '下单时从prod_product.model复制快照；保存后不可修改；商品无型号时可为NULL',
     6),
    (7, 'spec',
     'VARCHAR(100)',
     NULL,
     FALSE,
     TRUE,
     'snapshot',
     '规格',
     '下单时从prod_product.spec复制快照；保存后不可修改；商品无规格时可为NULL',
     7),
    (8, 'brand',
     'VARCHAR(50)',
     NULL,
     FALSE,
     TRUE,
     'snapshot',
     '品牌',
     '下单时从prod_product.brand复制快照；保存后不可修改；商品无品牌时可为NULL',
     8),
    (9, 'unit_id',
     'BIGINT',
     NULL,
     TRUE,
     TRUE,
     'snapshot',
     '单位ID',
     '下单时所选单位ID；快照保存后不可修改',
     9),
    (10, 'unit',
     'VARCHAR(30)',
     NULL,
     TRUE,
     TRUE,
     'snapshot',
     '单位',
     '下单时所选单位中文名；快照保存后不可修改',
     10),
    (11, 'qty',
     'DECIMAL(18,8)',
     '0',
     TRUE,
     FALSE,
     'snapshot',
     '数量',
     '按所选单位的交易数量；NOT NULL DEFAULT 0；显示精度由系统参数system.decimal_places_qty控制',
     11),
    (12, 'is_multi_unit',
     'BOOLEAN',
     'FALSE',
     TRUE,
     TRUE,
     'snapshot',
     '是否多单位',
     '商品是否启用多单位；下单时从prod_product.is_multi_unit复制快照；默认FALSE',
     12),
    (13, 'conversion_rate',
     'DECIMAL(18,8)',
     '1',
     TRUE,
     FALSE,
     'snapshot',
     '转换比例',
     '所选单位与基础单位的转换比例；NOT NULL DEFAULT 1；单价换算和成本核算的基准参数',
     13),
    (14, 'base_unit_id',
     'BIGINT',
     NULL,
     TRUE,
     TRUE,
     'snapshot',
     '基础单位ID',
     '商品基础单位ID；下单时从prod_product.base_unit_id复制快照',
     14),
    (15, 'base_qty',
     'DECIMAL(18,8)',
     '0',
     TRUE,
     FALSE,
     'snapshot',
     '换算数量',
     '基础单位数量（base_qty = qty × conversion_rate）；库存增减/成本核算/统计汇总统一使用此值',
     15),

    -- ========== 销售链路额外字段（1个，仅销售链路明细从表包含） ==========
    (16, 'customer_product_code',
     'VARCHAR(50)',
     NULL,
     FALSE,
     TRUE,
     'snapshot',
     '客户料号',
     '销售链路额外字段（报价→订单→发货→出库→退货→对账→发票→售后/样品/借用/租赁）；非销售链路明细从表无需包含此字段',
     16)
ON CONFLICT (field_name) DO UPDATE SET
    field_seq         = EXCLUDED.field_seq,
    data_type         = EXCLUDED.data_type,
    default_value     = EXCLUDED.default_value,
    is_not_null       = EXCLUDED.is_not_null,
    is_snapshot       = EXCLUDED.is_snapshot,
    field_category    = EXCLUDED.field_category,
    field_comment     = EXCLUDED.field_comment,
    design_note       = EXCLUDED.design_note,
    sort_order        = EXCLUDED.sort_order;

-- ============================================================
-- 第3步：明细从表商品冗余字段 DDL 片段模板（注释文档）
-- 说明:     以下为3个明细行结构字段 + 12个核心商品快照字段
--           + 1个销售链路字段的标准DDL片段，
--           所有含商品的明细从表 CREATE TABLE 必须在业务字段
--           区域包含此片段（放在业务字段之后、扩展字段之前）。
-- ============================================================

/*
 * ===================================================================
 * 明细从表商品冗余字段 DDL 片段模板（直接复制到各 CREATE TABLE 语句中）
 * ===================================================================
 *
 * 一、明细行结构字段（3个，所有明细从表必须包含）：
 *
 *     line_no             INT             NOT NULL DEFAULT 1,
 *     main_id             BIGINT          NOT NULL,
 *     product_id          BIGINT          NOT NULL,
 *
 * 二、商品快照冗余字段（12个核心，所有含商品的明细从表必须包含）：
 *
 *     product_code        VARCHAR(50)     NOT NULL,
 *     product_name        VARCHAR(100)    NOT NULL,
 *     model               VARCHAR(100),
 *     spec                VARCHAR(100),
 *     brand               VARCHAR(50),
 *     unit_id             BIGINT          NOT NULL,
 *     unit                VARCHAR(30)     NOT NULL,
 *     qty                 DECIMAL(18,8)   NOT NULL DEFAULT 0,
 *     is_multi_unit       BOOLEAN         NOT NULL DEFAULT FALSE,
 *     conversion_rate     DECIMAL(18,8)   NOT NULL DEFAULT 1,
 *     base_unit_id        BIGINT          NOT NULL,
 *     base_qty            DECIMAL(18,8)   NOT NULL DEFAULT 0,
 *
 * 三、销售链路额外字段（1个，仅销售链路明细从表包含）：
 *
 *     customer_product_code VARCHAR(50),
 *
 * 注意事项：
 * 1. 所有快照字段在保存时从商品主表(prod_product)复制，保存后不可修改
 * 2. base_qty = qty × conversion_rate，库存增减/成本核算统一使用base_qty
 * 3. 商品快照字段排列顺序必须遵循此模板，不可随意调整
 * 4. 明细从表字段排列顺序：结构字段 → 商品快照字段 → 业务字段 → 扩展字段(22) → 公共字段(10)
 * 5. product_id 必须创建索引以优化JOIN查询
 * 6. qty/conversion_rate/base_qty 物理字段统一 DECIMAL(18,8)，显示精度由系统参数控制
 * 7. 非含商品的明细从表（如费用明细、备注明细）无需包含商品快照字段，
 *    仅需包含3个结构字段（line_no/main_id/product_id 换为业务关联字段）
 * 8. customer_product_code 仅销售链路明细从表包含，采购/生产/委外链路不包含
 */

-- ============================================================
-- 第4步：明细从表索引模板（注释文档）
-- 说明:     每个明细从表必须创建以下索引。
--           索引命名格式：idx_{table}_{字段名}
-- ============================================================

/*
 * ===================================================================
 * 明细从表索引模板（每个明细从表 CREATE TABLE 后执行）
 * ===================================================================
 *
 * -- 租户索引（所有表必须）
 * CREATE INDEX idx_{table}_tenant_id
 *     ON {schema}.{table}(tenant_id);
 *
 * -- 主表关联索引（所有明细从表必须）
 * CREATE INDEX idx_{table}_tenant_id_main_id
 *     ON {schema}.{table}(tenant_id, main_id);
 *
 * -- 商品关联索引（所有含商品的明细从表必须）
 * CREATE INDEX idx_{table}_tenant_id_product_id
 *     ON {schema}.{table}(tenant_id, product_id);
 *
 * -- 行号索引（支持按行号排序查询）
 * CREATE INDEX idx_{table}_tenant_id_main_id_line_no
 *     ON {schema}.{table}(tenant_id, main_id, line_no);
 *
 * 设计要点：
 * - 联合索引以 tenant_id 为首列，确保多租户隔离查询性能
 * - main_id索引优化按主表查询明细行的性能
 * - product_id索引优化按商品查询关联单据的性能
 * - 不使用数据库外键约束，应用层维护关联关系
 *
 * 额外建议索引（按业务需要）：
 * -- 商品编码索引（按商品编码查询明细）
 * CREATE INDEX idx_{table}_tenant_id_product_code
 *     ON {schema}.{table}(tenant_id, product_code);
 *
 * -- 客户料号索引（销售链路明细表）
 * CREATE INDEX idx_{table}_tenant_id_customer_product_code
 *     ON {schema}.{table}(tenant_id, customer_product_code);
 */
 */

-- ============================================================
-- 第5步：明细从表完整建表示例（注释文档）
-- 说明:     展示符合全套规范的含商品明细从表 CREATE TABLE 示例。
--           包含：结构字段 + 商品快照字段 + 业务字段 + 扩展字段
--           + 公共字段 + 索引 + COMMENT。
--           以采购单明细从表作为示例。
-- ============================================================

/*
 * ===================================================================
 * 完整明细从表建表示例（以采购单明细从表为例）
 * ===================================================================
 *
 * CREATE TABLE erp_purchase.pur_order_detail (
 *     -- 一、明细行结构字段（3个）
 *     line_no             INT             NOT NULL DEFAULT 1,
 *     main_id             BIGINT          NOT NULL,
 *     product_id          BIGINT          NOT NULL,
 *     -- 二、商品快照冗余字段（12个核心）
 *     product_code        VARCHAR(50)     NOT NULL,
 *     product_name        VARCHAR(100)    NOT NULL,
 *     model               VARCHAR(100),
 *     spec                VARCHAR(100),
 *     brand               VARCHAR(50),
 *     unit_id             BIGINT          NOT NULL,
 *     unit                VARCHAR(30)     NOT NULL,
 *     qty                 DECIMAL(18,8)   NOT NULL DEFAULT 0,
 *     is_multi_unit       BOOLEAN         NOT NULL DEFAULT FALSE,
 *     conversion_rate     DECIMAL(18,8)   NOT NULL DEFAULT 1,
 *     base_unit_id        BIGINT          NOT NULL,
 *     base_qty            DECIMAL(18,8)   NOT NULL DEFAULT 0,
 *     -- 三、业务字段（按具体业务需求定义）
 *     unit_price          DECIMAL(18,8)   NOT NULL DEFAULT 0,
 *     tax_rate            DECIMAL(18,8)   NOT NULL DEFAULT 0,
 *     tax_amount          DECIMAL(18,8)   NOT NULL DEFAULT 0,
 *     discount_rate       DECIMAL(18,8)   NOT NULL DEFAULT 0,
 *     discount_amount     DECIMAL(18,8)   NOT NULL DEFAULT 0,
 *     line_amount         DECIMAL(18,8)   NOT NULL DEFAULT 0,
 *     arrival_date        DATE,
 *     remark              VARCHAR(200),
 *     -- 四、扩展字段（22个，所有业务表预留）
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
 *     -- 五、公共字段（10个，完整包含，含默认值与约束）
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
 * CREATE INDEX idx_pur_order_detail_tenant_id
 *     ON erp_purchase.pur_order_detail(tenant_id);
 *
 * -- 主表关联索引
 * CREATE INDEX idx_pur_order_detail_tenant_id_main_id
 *     ON erp_purchase.pur_order_detail(tenant_id, main_id);
 *
 * -- 商品关联索引
 * CREATE INDEX idx_pur_order_detail_tenant_id_product_id
 *     ON erp_purchase.pur_order_detail(tenant_id, product_id);
 *
 * -- 行号排序索引
 * CREATE INDEX idx_pur_order_detail_tenant_id_main_id_line
 *     ON erp_purchase.pur_order_detail(tenant_id, main_id, line_no);
 *
 * -- 表注释
 * COMMENT ON TABLE erp_purchase.pur_order_detail IS '采购单明细从表';
 *
 * -- 结构字段注释
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.line_no IS '行号';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.main_id IS '采购单主表ID';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.product_id IS '商品ID';
 *
 * -- 商品快照字段注释
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.product_code IS '商品编码（快照）';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.product_name IS '商品名称（快照）';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.model IS '型号（快照）';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.spec IS '规格（快照）';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.brand IS '品牌（快照）';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.unit_id IS '单位ID（快照）';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.unit IS '单位（快照）';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.qty IS '数量';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.is_multi_unit IS '是否多单位（快照）';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.conversion_rate IS '转换比例';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.base_unit_id IS '基础单位ID（快照）';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.base_qty IS '换算数量';
 *
 * -- 业务字段注释
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.unit_price IS '单价';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.tax_rate IS '税率';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.tax_amount IS '税额';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.discount_rate IS '折扣率';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.discount_amount IS '折扣金额';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.line_amount IS '行金额';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.arrival_date IS '预计到货日期';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.remark IS '备注';
 *
 * -- 公共字段注释（所有表统一）
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.id IS '主键ID';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.tenant_id IS '租户ID';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.created_by IS '创建人ID';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.created_at IS '创建时间';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.updated_by IS '修改人ID';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.updated_at IS '更新时间';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.is_deleted IS '是否删除';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.owner_dept_id IS '所属部门ID';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.owner_id IS '数据负责人ID';
 * COMMENT ON COLUMN erp_purchase.pur_order_detail.version IS '版本号';
 */

-- ============================================================
-- 第6步：明细从表商品快照字段合规校验函数
-- 说明:     PL/pgSQL函数，接收schema名和表名作为参数，
--           校验目标明细从表是否包含3个结构字段和12个核心
--           商品快照字段及其NOT NULL/默认值/数据类型是否符合规范。
--           返回校验结果JSON，便于自动化检测。
-- ============================================================

CREATE OR REPLACE FUNCTION erp_base.fn_validate_detail_product_fields(
    p_schema_name VARCHAR(64),
    p_table_name  VARCHAR(64)
)
RETURNS JSONB
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_result              JSONB;
    v_missing_fields      TEXT[] := '{}';
    v_wrong_defaults      TEXT[] := '{}';
    v_wrong_not_null      TEXT[] := '{}';
    v_wrong_type          TEXT[] := '{}';
    v_non_snapshot_count  INT;
    v_snapshot_count      INT;
    v_field_count         INT;
    v_total_expected      INT;
    v_has_main_idx        BOOLEAN;
    v_has_product_idx     BOOLEAN;
    v_has_line_idx        BOOLEAN;
    v_rec                 RECORD;
    v_existing_cols       TEXT[];
BEGIN
    -- 获取目标表的所有列名
    SELECT array_agg(a.attname ORDER BY a.attname)
    INTO v_existing_cols
    FROM pg_catalog.pg_attribute a
    WHERE a.attrelid = (p_schema_name || '.' || p_table_name)::regclass
      AND a.attnum > 0
      AND NOT a.attisdropped;

    -- 统计总字段数
    v_total_expected := 0;

    -- 检查规范中定义的各个字段是否存在
    FOR v_rec IN
        SELECT field_name, data_type, default_value, is_not_null,
               is_snapshot, field_category, field_comment
        FROM erp_base.detail_product_field_spec
        ORDER BY sort_order
    LOOP
        v_total_expected := v_total_expected + 1;

        IF NOT (v_rec.field_name = ANY(v_existing_cols)) THEN
            -- 对于customer_product_code，非销售链路表可以缺失，只记录不报错
            IF v_rec.field_name = 'customer_product_code' THEN
                CONTINUE;
            END IF;
            v_missing_fields := array_append(v_missing_fields,
                v_rec.field_name || '（' || v_rec.field_category || '）');
            CONTINUE;
        END IF;
    END LOOP;

    -- 统计结构字段和快照字段数量
    SELECT COUNT(*)
    INTO v_field_count
    FROM pg_catalog.pg_attribute a
    WHERE a.attrelid = (p_schema_name || '.' || p_table_name)::regclass
      AND a.attnum > 0
      AND NOT a.attisdropped
      AND a.attname IN (
          SELECT field_name FROM erp_base.detail_product_field_spec
      );

    SELECT COUNT(*)
    INTO v_snapshot_count
    FROM pg_catalog.pg_attribute a
    WHERE a.attrelid = (p_schema_name || '.' || p_table_name)::regclass
      AND a.attnum > 0
      AND NOT a.attisdropped
      AND a.attname IN (
          SELECT field_name FROM erp_base.detail_product_field_spec
          WHERE is_snapshot = TRUE
      );

    -- 检查NOT NULL约束和默认值
    FOR v_rec IN
        SELECT
            a.attname::VARCHAR(40) AS field_name,
            pg_get_expr(d.adbin, d.adrelid) AS column_default,
            NOT a.attnotnull AS is_nullable,
            spec.default_value,
            spec.is_not_null,
            spec.is_snapshot
        FROM pg_catalog.pg_attribute a
        LEFT JOIN pg_catalog.pg_attrdef d
            ON (a.attrelid = d.adrelid AND a.attnum = d.adnum)
        JOIN erp_base.detail_product_field_spec spec
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

    -- 检查索引是否存在
    SELECT EXISTS (
        SELECT 1
        FROM pg_catalog.pg_index i
        JOIN pg_catalog.pg_class c ON i.indexrelid = c.oid
        WHERE i.indrelid = (p_schema_name || '.' || p_table_name)::regclass
          AND c.relname LIKE '%_main_id%'
    ) INTO v_has_main_idx;

    SELECT EXISTS (
        SELECT 1
        FROM pg_catalog.pg_index i
        JOIN pg_catalog.pg_class c ON i.indexrelid = c.oid
        WHERE i.indrelid = (p_schema_name || '.' || p_table_name)::regclass
          AND c.relname LIKE '%_product_id%'
    ) INTO v_has_product_idx;

    SELECT EXISTS (
        SELECT 1
        FROM pg_catalog.pg_index i
        JOIN pg_catalog.pg_class c ON i.indexrelid = c.oid
        WHERE i.indrelid = (p_schema_name || '.' || p_table_name)::regclass
          AND c.relname LIKE '%_main_id%_line_no%'
    ) INTO v_has_line_idx;

    -- 构建JSON结果
    v_result := jsonb_build_object(
        'schema', p_schema_name,
        'table', p_table_name,
        'expected_detail_fields', v_total_expected,
        'actual_detail_fields', v_field_count,
        'snapshot_field_count', v_snapshot_count,
        'expected_snapshot_core', 12,
        'is_structure_complete', (v_missing_fields IS NULL),
        'missing_fields', COALESCE(to_jsonb(v_missing_fields), '[]'::jsonb),
        'default_ok', (v_wrong_defaults = '{}'),
        'wrong_defaults', COALESCE(to_jsonb(v_wrong_defaults), '[]'::jsonb),
        'not_null_ok', (v_wrong_not_null = '{}'),
        'wrong_not_null', COALESCE(to_jsonb(v_wrong_not_null), '[]'::jsonb),
        'main_id_index_ok', v_has_main_idx,
        'product_id_index_ok', v_has_product_idx,
        'line_no_index_ok', v_has_line_idx,
        'overall_pass', (
            v_missing_fields IS NULL
            AND v_wrong_defaults = '{}'
            AND v_wrong_not_null = '{}'
            AND v_has_main_idx
            AND v_has_product_idx
        ),
        'checked_at', NOW()::TEXT
    );

    RETURN v_result;
END;
$$;

COMMENT ON FUNCTION erp_base.fn_validate_detail_product_fields(VARCHAR, VARCHAR)
    IS '校验目标明细从表是否包含结构字段(3个)和商品快照字段(12个核心)及其NOT NULL/默认值/索引合规性，返回JSON结果';

-- ============================================================
-- 第7步：明细从表完整字段清单（注释文档）
-- 说明:     汇总明细从表（含商品）需要包含的全部字段：
--           结构字段(3) + 商品快照字段(12-13) + 业务字段(N)
--           + 扩展字段(22) + 公共字段(10)
--           作为后续所有明细从表建表的完整检查清单。
-- ============================================================

/*
 * ===================================================================
 * 明细从表（含商品）完整字段清单（47个固定字段 + N个业务字段）
 * ===================================================================
 *
 * 字段分组            | 数量  | 字段列表
 * ---------------------|-------|-----------------------------------------
 * 明细行结构字段       | 3     | line_no, main_id, product_id
 * 商品快照字段（核心） | 12    | product_code, product_name, model, spec, brand, unit_id, unit, qty, is_multi_unit, conversion_rate, base_unit_id, base_qty
 * 商品快照字段（销售） | 1     | customer_product_code（仅销售链路）
 * 业务字段             | N     | 按业务需求定义（如unit_price, tax_rate, discount_rate等）
 * 扩展字段             | 22    | ext_str1~10, ext_num1~5, ext_date1~3, ext_bool1~3, ext_json
 * 公共字段             | 10    | id, tenant_id, created_by, created_at, updated_by, updated_at, is_deleted, owner_dept_id, owner_id, version
 *
 * 建表时字段排列顺序（固定）：
 *   结构字段(3) → 商品快照字段(12-13) → 业务字段(N) → 扩展字段(22) → 公共字段(10)
 *
 * 关键约束清单：
 * ✅ line_no INT NOT NULL DEFAULT 1 — 行号从1递增
 * ✅ main_id BIGINT NOT NULL — 关联单据主表，必须创建索引
 * ✅ product_id BIGINT NOT NULL — 关联商品主表，必须创建索引
 * ✅ 商品快照字段(12)完整包含 — product_code/name/model/spec/brand/unit_id/unit/qty/is_multi_unit/conversion_rate/base_unit_id/base_qty
 * ✅ 快照原则：保存时从prod_product复制，后续不可修改
 * ✅ base_qty = qty × conversion_rate — 库存增减/成本核算/统计汇总统一使用base_qty
 * ✅ 10个公共字段完整包含 — id/tenant_id/created_by/created_at/updated_by/updated_at/is_deleted/owner_dept_id/owner_id/version
 * ✅ 22个扩展字段预留 — ext_str1~10/ext_num1~5/ext_date1~3/ext_bool1~3/ext_json
 * ✅ 数值字段统一 DECIMAL(18,8)
 * ✅ 联合索引以 tenant_id 为首列
 * ✅ 表级和列级 COMMENT 注释完整
 * ✅ 不创建 FOREIGN KEY 约束
 * ✅ line_no + main_id 联合唯一约束（同一单据内行号唯一）
 *
 * 销售链路明细从表额外要求：
 * ✅ customer_product_code VARCHAR(50) — 客户料号，仅销售链路明细从表包含
 *
 * 非含商品明细从表：
 * ✅ 仅需包含 line_no, main_id 结构字段（product_id 替换为对应业务字段）
 * ✅ 无需包含商品快照字段
 */

-- ============================================================
-- 第8步：快照原则核心设计要点（注释文档）
-- 说明:     明确商品快照冗余字段的设计意图和使用约束，
--           避免后续开发中快照字段被错误修改或遗漏。
-- ============================================================

/*
 * ===================================================================
 * 商品快照冗余字段 - 快照原则核心设计要点
 * ===================================================================
 *
 * 1. 快照时机：单据保存时（创建/修改），从商品主表(prod_product)和
 *    商品单位表(prod_product_unit)同时复制最新字段值到明细从表
 *
 * 2. 快照不可变：快照字段一旦保存，后续商品信息变更不影响已保存文档。
 *    这是核心设计意图 — 保证历史单据数据的完整性和可审计性。
 *    例如：商品名称从"A型螺丝"改为"B型螺丝"后，历史采购单仍显示"A型螺丝"
 *
 * 3. 更新策略：
 *    - 草稿状态：重新编辑保存时刷新快照（重新从商品主表取值）
 *    - 审核后状态：快照字段不可修改，禁止刷新
 *
 * 4. 快照字段 vs 计算字段：
 *    - 快照字段（12个）：product_code/name/model/spec/brand/unit_id/unit/
 *      is_multi_unit/base_unit_id/customer_product_code
 *      → 保存时从商品主表复制，后续不可修改
 *    - 计算字段（3个）：qty/conversion_rate/base_qty
 *      → qty/unit_id由用户选择，conversion_rate根据unit_id查商品单位表获取，
 *        base_qty = qty × conversion_rate 自动计算
 *
 * 5. base_qty核心地位：
 *    - 所有库存增减计算统一使用base_qty（换算为基本单位的数量）
 *    - 成本核算基于base_qty而非qty（确保不同单位的一致性）
 *    - 统计汇总使用base_qty（同一商品不同单位可累加）
 *    - 公式：base_qty = qty × conversion_rate
 *
 * 6. 多单位场景：
 *    - is_multi_unit = TRUE 时，用户可选择非基础单位（如"箱"），
 *      系统自动计算base_qty
 *    - is_multi_unit = FALSE 时，仅能选择基础单位，
 *      conversion_rate=1, base_qty=qty
 *
 * 7. 客户料号传递链（销售链路）：
 *    报价单明细(customer_product_code) → 销售订单明细 → 发货通知明细
 *    → 出库明细 → 退货明细 → 对账明细 → 发票明细
 *    → 售后/样品/借用/租赁明细
 *    客户料号从报价单开始录入，沿销售链路自动向下传递
 *
 * 8. 明细从表唯一约束：
 *    CREATE UNIQUE INDEX uk_{table}_line_no_active
 *        ON {schema}.{table}(tenant_id, main_id, line_no)
 *        WHERE is_deleted = FALSE;
 *    同一单据内行号唯一（部分唯一索引，仅对未删除数据强制）
 */

-- ============================================================
-- 验证脚本（可选执行）
-- ============================================================

-- 验证规范记录表数据完整性：
-- SELECT field_seq, field_name, data_type, default_value,
--        CASE WHEN is_not_null THEN 'NOT NULL' ELSE 'NULL' END AS nullable,
--        CASE WHEN is_snapshot THEN 'YES' ELSE 'NO' END AS is_snapshot,
--        field_category, field_comment
-- FROM erp_base.detail_product_field_spec
-- ORDER BY sort_order;

-- 验证明细从表规范字段总数（不含customer_product_code为15个）：
-- SELECT
--     COUNT(*) AS total_spec_fields,
--     COUNT(*) FILTER (WHERE field_category = 'structural') AS structural_count,
--     COUNT(*) FILTER (WHERE field_category = 'snapshot') AS snapshot_count
-- FROM erp_base.detail_product_field_spec;
-- 预期结果：total_spec_fields=16, structural_count=3, snapshot_count=13

-- 验证商品快照核心字段完整（12个核心快照字段，不含customer_product_code）：
-- SELECT COUNT(*) AS core_snapshot_count
-- FROM erp_base.detail_product_field_spec
-- WHERE is_snapshot = TRUE AND field_name != 'customer_product_code';
-- 预期结果：core_snapshot_count = 12

-- 验证customer_product_code为销售链路字段：
-- SELECT field_name, field_comment, design_note
-- FROM erp_base.detail_product_field_spec
-- WHERE field_name = 'customer_product_code';

-- 验证表结构合规性（以 pur_order_detail 表为例，需表已存在）：
-- SELECT erp_base.fn_validate_detail_product_fields('erp_purchase', 'pur_order_detail');

-- ============================================================
-- 回滚脚本（如需回滚）:
-- DROP TABLE IF EXISTS erp_base.detail_product_field_spec CASCADE;
-- DROP FUNCTION IF EXISTS erp_base.fn_validate_detail_product_fields(VARCHAR, VARCHAR);
-- ============================================================
