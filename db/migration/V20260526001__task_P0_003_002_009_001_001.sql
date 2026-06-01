-- ============================================================
-- ERP AI 系统 - 明细表辅助属性存储规范 DDL
-- 任务编号: P0-003-002-009-001-001
-- 文件名:   V20260526001__task_P0_003_002_009_001_001.sql
-- 说明:     创建三个辅助属性子表（库位/批次/序列号），
--           为所有业务单据明细行提供统一的辅助属性存储。
--           三表独立管理、独立启用/关闭，互不耦合。
--           采用"一行明细对多行辅助记录"模式存储。
--           启用库位或批次时：同一detail_id的辅助记录qty之和
--           必须等于明细行主表qty值。
--           启用序列号时：序列号记录数必须等于主表qty值
--           （一条序列号对应一个商品个体）。
-- 执行方式: 连接到 erp_db 后以应用账号执行
-- 数据库:   PostgreSQL 15+
-- 作者:     AI Generated
-- 日期:     2026-06-01
-- ============================================================

-- ============================================================
-- 第1步：创建库位辅助属性子表 doc_detail_location
-- 说明:     存储明细行的库位拆分数据。
--           一个明细行可按不同库位拆分存放，
--           被所有业务单据明细行共享引用。
-- ============================================================

CREATE TABLE IF NOT EXISTS erp_base.doc_detail_location (
    -- 一、业务字段
    detail_id           BIGINT          NOT NULL,
    detail_type         VARCHAR(50)     NOT NULL,
    location_id         BIGINT          NOT NULL,
    qty                 DECIMAL(18,8)   NOT NULL DEFAULT 0,
    -- 二、扩展字段（22个，所有业务表预留）
    ext_str1            VARCHAR(200),
    ext_str2            VARCHAR(200),
    ext_str3            VARCHAR(200),
    ext_str4            VARCHAR(200),
    ext_str5            VARCHAR(200),
    ext_str6            VARCHAR(200),
    ext_str7            VARCHAR(200),
    ext_str8            VARCHAR(200),
    ext_str9            VARCHAR(200),
    ext_str10           VARCHAR(200),
    ext_num1            DECIMAL(18,8),
    ext_num2            DECIMAL(18,8),
    ext_num3            DECIMAL(18,8),
    ext_num4            DECIMAL(18,8),
    ext_num5            DECIMAL(18,8),
    ext_date1           DATE,
    ext_date2           DATE,
    ext_date3           DATE,
    ext_bool1           BOOLEAN,
    ext_bool2           BOOLEAN,
    ext_bool3           BOOLEAN,
    ext_json            JSONB,
    -- 三、公共字段（10个，完整包含，含默认值与约束）
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    created_by          BIGINT,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          BIGINT,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id       BIGINT,
    owner_id            BIGINT,
    version             INT             NOT NULL DEFAULT 1
);

-- 租户索引
CREATE INDEX IF NOT EXISTS idx_doc_detail_location_tenant_id
    ON erp_base.doc_detail_location(tenant_id);

-- 明细行关联查询索引（以tenant_id为首列，按detail_type+detail_id定位来源明细行）
CREATE INDEX IF NOT EXISTS idx_doc_detail_location_detail
    ON erp_base.doc_detail_location(tenant_id, detail_type, detail_id);

-- 库位关联索引
CREATE INDEX IF NOT EXISTS idx_doc_detail_location_location
    ON erp_base.doc_detail_location(tenant_id, location_id);

-- 同一明细行同一库位仅允许一条记录（部分唯一索引，忽略已删除）
CREATE UNIQUE INDEX IF NOT EXISTS uk_doc_detail_location_detail_loc_active
    ON erp_base.doc_detail_location(tenant_id, detail_type, detail_id, location_id)
    WHERE is_deleted = FALSE;

-- 表注释
COMMENT ON TABLE erp_base.doc_detail_location IS '库位辅助属性子表 - 明细行的库位拆分数据，被所有业务单据明细行共享引用';

-- 业务字段注释
COMMENT ON COLUMN erp_base.doc_detail_location.detail_id IS '来源明细行ID';
COMMENT ON COLUMN erp_base.doc_detail_location.detail_type IS '来源明细表类型（如sale_order_detail/purchase_order_detail等）';
COMMENT ON COLUMN erp_base.doc_detail_location.location_id IS '库位ID';
COMMENT ON COLUMN erp_base.doc_detail_location.qty IS '该库位存放数量（base_qty基础单位数量）';

-- 扩展字段注释
COMMENT ON COLUMN erp_base.doc_detail_location.ext_str1 IS '扩展字符串1';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_str2 IS '扩展字符串2';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_str3 IS '扩展字符串3';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_str4 IS '扩展字符串4';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_str5 IS '扩展字符串5';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_str6 IS '扩展字符串6';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_str7 IS '扩展字符串7';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_str8 IS '扩展字符串8';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_str9 IS '扩展字符串9';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_str10 IS '扩展字符串10';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_num1 IS '扩展数值1';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_num2 IS '扩展数值2';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_num3 IS '扩展数值3';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_num4 IS '扩展数值4';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_num5 IS '扩展数值5';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_date1 IS '扩展日期1';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_date2 IS '扩展日期2';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_date3 IS '扩展日期3';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_bool1 IS '扩展布尔1';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_bool2 IS '扩展布尔2';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_bool3 IS '扩展布尔3';
COMMENT ON COLUMN erp_base.doc_detail_location.ext_json IS '扩展JSON';

-- 公共字段注释
COMMENT ON COLUMN erp_base.doc_detail_location.id IS '主键ID';
COMMENT ON COLUMN erp_base.doc_detail_location.tenant_id IS '租户ID';
COMMENT ON COLUMN erp_base.doc_detail_location.created_by IS '创建人ID';
COMMENT ON COLUMN erp_base.doc_detail_location.created_at IS '创建时间';
COMMENT ON COLUMN erp_base.doc_detail_location.updated_by IS '修改人ID';
COMMENT ON COLUMN erp_base.doc_detail_location.updated_at IS '更新时间';
COMMENT ON COLUMN erp_base.doc_detail_location.is_deleted IS '是否删除';
COMMENT ON COLUMN erp_base.doc_detail_location.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN erp_base.doc_detail_location.owner_id IS '数据负责人ID';
COMMENT ON COLUMN erp_base.doc_detail_location.version IS '版本号';

-- ============================================================
-- 第2步：创建批次辅助属性子表 doc_detail_batch
-- 说明:     存储明细行的批次拆分数据。
--           一个明细行可按不同批次拆分，
--           被所有业务单据明细行共享引用。
-- ============================================================

CREATE TABLE IF NOT EXISTS erp_base.doc_detail_batch (
    -- 一、业务字段
    detail_id           BIGINT          NOT NULL,
    detail_type         VARCHAR(50)     NOT NULL,
    batch_no            VARCHAR(100)    NOT NULL,
    produced_date       DATE,
    expiry_date         DATE,
    qty                 DECIMAL(18,8)   NOT NULL DEFAULT 0,
    -- 二、扩展字段（22个，所有业务表预留）
    ext_str1            VARCHAR(200),
    ext_str2            VARCHAR(200),
    ext_str3            VARCHAR(200),
    ext_str4            VARCHAR(200),
    ext_str5            VARCHAR(200),
    ext_str6            VARCHAR(200),
    ext_str7            VARCHAR(200),
    ext_str8            VARCHAR(200),
    ext_str9            VARCHAR(200),
    ext_str10           VARCHAR(200),
    ext_num1            DECIMAL(18,8),
    ext_num2            DECIMAL(18,8),
    ext_num3            DECIMAL(18,8),
    ext_num4            DECIMAL(18,8),
    ext_num5            DECIMAL(18,8),
    ext_date1           DATE,
    ext_date2           DATE,
    ext_date3           DATE,
    ext_bool1           BOOLEAN,
    ext_bool2           BOOLEAN,
    ext_bool3           BOOLEAN,
    ext_json            JSONB,
    -- 三、公共字段（10个，完整包含，含默认值与约束）
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    created_by          BIGINT,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          BIGINT,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id       BIGINT,
    owner_id            BIGINT,
    version             INT             NOT NULL DEFAULT 1
);

-- 租户索引
CREATE INDEX IF NOT EXISTS idx_doc_detail_batch_tenant_id
    ON erp_base.doc_detail_batch(tenant_id);

-- 明细行关联查询索引
CREATE INDEX IF NOT EXISTS idx_doc_detail_batch_detail
    ON erp_base.doc_detail_batch(tenant_id, detail_type, detail_id);

-- 批次号查询索引
CREATE INDEX IF NOT EXISTS idx_doc_detail_batch_batch_no
    ON erp_base.doc_detail_batch(tenant_id, batch_no);

-- 同一明细行同批次仅允许一条记录（部分唯一索引，忽略已删除）
CREATE UNIQUE INDEX IF NOT EXISTS uk_doc_detail_batch_detail_batch_active
    ON erp_base.doc_detail_batch(tenant_id, detail_type, detail_id, batch_no)
    WHERE is_deleted = FALSE;

-- 表注释
COMMENT ON TABLE erp_base.doc_detail_batch IS '批次辅助属性子表 - 明细行的批次拆分数据，被所有业务单据明细行共享引用';

-- 业务字段注释
COMMENT ON COLUMN erp_base.doc_detail_batch.detail_id IS '来源明细行ID';
COMMENT ON COLUMN erp_base.doc_detail_batch.detail_type IS '来源明细表类型（如sale_order_detail/purchase_order_detail等）';
COMMENT ON COLUMN erp_base.doc_detail_batch.batch_no IS '批次号';
COMMENT ON COLUMN erp_base.doc_detail_batch.produced_date IS '生产日期';
COMMENT ON COLUMN erp_base.doc_detail_batch.expiry_date IS '有效期至';
COMMENT ON COLUMN erp_base.doc_detail_batch.qty IS '该批次数量（base_qty基础单位数量）';

-- 扩展字段注释
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_str1 IS '扩展字符串1';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_str2 IS '扩展字符串2';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_str3 IS '扩展字符串3';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_str4 IS '扩展字符串4';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_str5 IS '扩展字符串5';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_str6 IS '扩展字符串6';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_str7 IS '扩展字符串7';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_str8 IS '扩展字符串8';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_str9 IS '扩展字符串9';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_str10 IS '扩展字符串10';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_num1 IS '扩展数值1';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_num2 IS '扩展数值2';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_num3 IS '扩展数值3';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_num4 IS '扩展数值4';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_num5 IS '扩展数值5';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_date1 IS '扩展日期1';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_date2 IS '扩展日期2';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_date3 IS '扩展日期3';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_bool1 IS '扩展布尔1';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_bool2 IS '扩展布尔2';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_bool3 IS '扩展布尔3';
COMMENT ON COLUMN erp_base.doc_detail_batch.ext_json IS '扩展JSON';

-- 公共字段注释
COMMENT ON COLUMN erp_base.doc_detail_batch.id IS '主键ID';
COMMENT ON COLUMN erp_base.doc_detail_batch.tenant_id IS '租户ID';
COMMENT ON COLUMN erp_base.doc_detail_batch.created_by IS '创建人ID';
COMMENT ON COLUMN erp_base.doc_detail_batch.created_at IS '创建时间';
COMMENT ON COLUMN erp_base.doc_detail_batch.updated_by IS '修改人ID';
COMMENT ON COLUMN erp_base.doc_detail_batch.updated_at IS '更新时间';
COMMENT ON COLUMN erp_base.doc_detail_batch.is_deleted IS '是否删除';
COMMENT ON COLUMN erp_base.doc_detail_batch.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN erp_base.doc_detail_batch.owner_id IS '数据负责人ID';
COMMENT ON COLUMN erp_base.doc_detail_batch.version IS '版本号';

-- ============================================================
-- 第3步：创建序列号辅助属性子表 doc_detail_serial
-- 说明:     存储明细行的序列号数据。
--           一条序列号对应一个商品个体，
--           序列号记录数必须等于主表qty值。
--           被所有业务单据明细行共享引用。
-- ============================================================

CREATE TABLE IF NOT EXISTS erp_base.doc_detail_serial (
    -- 一、业务字段
    detail_id           BIGINT          NOT NULL,
    detail_type         VARCHAR(50)     NOT NULL,
    serial_no           VARCHAR(200)    NOT NULL,
    -- 二、扩展字段（22个，所有业务表预留）
    ext_str1            VARCHAR(200),
    ext_str2            VARCHAR(200),
    ext_str3            VARCHAR(200),
    ext_str4            VARCHAR(200),
    ext_str5            VARCHAR(200),
    ext_str6            VARCHAR(200),
    ext_str7            VARCHAR(200),
    ext_str8            VARCHAR(200),
    ext_str9            VARCHAR(200),
    ext_str10           VARCHAR(200),
    ext_num1            DECIMAL(18,8),
    ext_num2            DECIMAL(18,8),
    ext_num3            DECIMAL(18,8),
    ext_num4            DECIMAL(18,8),
    ext_num5            DECIMAL(18,8),
    ext_date1           DATE,
    ext_date2           DATE,
    ext_date3           DATE,
    ext_bool1           BOOLEAN,
    ext_bool2           BOOLEAN,
    ext_bool3           BOOLEAN,
    ext_json            JSONB,
    -- 三、公共字段（10个，完整包含，含默认值与约束）
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    created_by          BIGINT,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by          BIGINT,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id       BIGINT,
    owner_id            BIGINT,
    version             INT             NOT NULL DEFAULT 1
);

-- 租户索引
CREATE INDEX IF NOT EXISTS idx_doc_detail_serial_tenant_id
    ON erp_base.doc_detail_serial(tenant_id);

-- 明细行关联查询索引
CREATE INDEX IF NOT EXISTS idx_doc_detail_serial_detail
    ON erp_base.doc_detail_serial(tenant_id, detail_type, detail_id);

-- 序列号全局部分唯一索引（整个系统内同一序列号仅允许一条未删除记录）
CREATE UNIQUE INDEX IF NOT EXISTS uk_doc_detail_serial_no_active
    ON erp_base.doc_detail_serial(tenant_id, serial_no)
    WHERE is_deleted = FALSE;

-- 表注释
COMMENT ON TABLE erp_base.doc_detail_serial IS '序列号辅助属性子表 - 明细行的序列号数据，被所有业务单据明细行共享引用';

-- 业务字段注释
COMMENT ON COLUMN erp_base.doc_detail_serial.detail_id IS '来源明细行ID';
COMMENT ON COLUMN erp_base.doc_detail_serial.detail_type IS '来源明细表类型（如sale_order_detail/purchase_order_detail等）';
COMMENT ON COLUMN erp_base.doc_detail_serial.serial_no IS '序列号（一条序列号对应一个商品个体）';

-- 扩展字段注释
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_str1 IS '扩展字符串1';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_str2 IS '扩展字符串2';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_str3 IS '扩展字符串3';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_str4 IS '扩展字符串4';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_str5 IS '扩展字符串5';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_str6 IS '扩展字符串6';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_str7 IS '扩展字符串7';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_str8 IS '扩展字符串8';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_str9 IS '扩展字符串9';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_str10 IS '扩展字符串10';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_num1 IS '扩展数值1';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_num2 IS '扩展数值2';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_num3 IS '扩展数值3';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_num4 IS '扩展数值4';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_num5 IS '扩展数值5';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_date1 IS '扩展日期1';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_date2 IS '扩展日期2';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_date3 IS '扩展日期3';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_bool1 IS '扩展布尔1';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_bool2 IS '扩展布尔2';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_bool3 IS '扩展布尔3';
COMMENT ON COLUMN erp_base.doc_detail_serial.ext_json IS '扩展JSON';

-- 公共字段注释
COMMENT ON COLUMN erp_base.doc_detail_serial.id IS '主键ID';
COMMENT ON COLUMN erp_base.doc_detail_serial.tenant_id IS '租户ID';
COMMENT ON COLUMN erp_base.doc_detail_serial.created_by IS '创建人ID';
COMMENT ON COLUMN erp_base.doc_detail_serial.created_at IS '创建时间';
COMMENT ON COLUMN erp_base.doc_detail_serial.updated_by IS '修改人ID';
COMMENT ON COLUMN erp_base.doc_detail_serial.updated_at IS '更新时间';
COMMENT ON COLUMN erp_base.doc_detail_serial.is_deleted IS '是否删除';
COMMENT ON COLUMN erp_base.doc_detail_serial.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN erp_base.doc_detail_serial.owner_id IS '数据负责人ID';
COMMENT ON COLUMN erp_base.doc_detail_serial.version IS '版本号';

-- ============================================================
-- 第4步：创建辅助属性子表结构校验函数
-- 说明:     PL/pgSQL函数，校验指定辅助属性子表是否包含
--           所有规范要求的字段（10个公共字段 +
--           22个扩展字段 + 业务特有字段），返回JSON结果。
-- ============================================================

CREATE OR REPLACE FUNCTION erp_base.fn_validate_aux_table(
    p_schema_name  VARCHAR(64),
    p_table_name   VARCHAR(64),
    p_biz_fields   TEXT[]
)
RETURNS JSONB
LANGUAGE plpgsql
STABLE
AS $$
DECLARE
    v_expected_fields   TEXT[];
    v_actual_fields     TEXT[];
    v_missing_fields    TEXT[];
    v_result            JSONB;
BEGIN
    -- 构建预期字段列表：业务字段 + 22扩展字段 + 10公共字段
    v_expected_fields := p_biz_fields
        || ARRAY['ext_str1','ext_str2','ext_str3','ext_str4','ext_str5',
                 'ext_str6','ext_str7','ext_str8','ext_str9','ext_str10',
                 'ext_num1','ext_num2','ext_num3','ext_num4','ext_num5',
                 'ext_date1','ext_date2','ext_date3',
                 'ext_bool1','ext_bool2','ext_bool3','ext_json']
        || ARRAY['id','tenant_id','created_by','created_at',
                 'updated_by','updated_at','is_deleted',
                 'owner_dept_id','owner_id','version'];

    -- 获取实际字段列表
    SELECT array_agg(a.attname ORDER BY a.attnum)
    INTO v_actual_fields
    FROM pg_catalog.pg_attribute a
    WHERE a.attrelid = (p_schema_name || '.' || p_table_name)::regclass
      AND a.attnum > 0
      AND NOT a.attisdropped;

    -- 找出缺失字段
    SELECT array_agg(f ORDER BY f)
    INTO v_missing_fields
    FROM unnest(v_expected_fields) AS f
    WHERE f NOT IN (SELECT unnest(v_actual_fields));

    -- 构建JSON结果
    v_result := jsonb_build_object(
        'schema', p_schema_name,
        'table', p_table_name,
        'expected_count', array_length(v_expected_fields, 1),
        'actual_count', array_length(v_actual_fields, 1),
        'is_complete', (v_missing_fields IS NULL),
        'missing_fields', COALESCE(to_jsonb(v_missing_fields), '[]'::jsonb),
        'checked_at', NOW()::TEXT
    );

    RETURN v_result;
END;
$$;

COMMENT ON FUNCTION erp_base.fn_validate_aux_table(VARCHAR, VARCHAR, TEXT[])
    IS '校验辅助属性子表是否包含所有规范要求的字段（业务字段+22扩展+10公共），返回JSON结果';

-- ============================================================
-- 验证脚本（可选执行）
-- ============================================================

-- 验证 doc_detail_location 表结构：
-- SELECT erp_base.fn_validate_aux_table('erp_base', 'doc_detail_location',
--     ARRAY['detail_id','detail_type','location_id','qty']);

-- 验证 doc_detail_batch 表结构：
-- SELECT erp_base.fn_validate_aux_table('erp_base', 'doc_detail_batch',
--     ARRAY['detail_id','detail_type','batch_no','produced_date','expiry_date','qty']);

-- 验证 doc_detail_serial 表结构：
-- SELECT erp_base.fn_validate_aux_table('erp_base', 'doc_detail_serial',
--     ARRAY['detail_id','detail_type','serial_no']);

-- 验证部分唯一索引：
-- SELECT indexrelid::regclass, indpred
-- FROM pg_index
-- WHERE indrelid = 'erp_base.doc_detail_location'::regclass AND indisunique = true;
-- SELECT indexrelid::regclass, indpred
-- FROM pg_index
-- WHERE indrelid = 'erp_base.doc_detail_batch'::regclass AND indisunique = true;
-- SELECT indexrelid::regclass, indpred
-- FROM pg_index
-- WHERE indrelid = 'erp_base.doc_detail_serial'::regclass AND indisunique = true;

-- 验证COMMENT注释完整性：
-- SELECT c.relname AS table_name,
--        obj_description(c.oid, 'pg_class') AS table_comment
-- FROM pg_class c
-- WHERE c.relname IN ('doc_detail_location','doc_detail_batch','doc_detail_serial')
--   AND c.relkind = 'r';

-- ============================================================
-- 回滚脚本（如需回滚）:
-- DROP TABLE IF EXISTS erp_base.doc_detail_location CASCADE;
-- DROP TABLE IF EXISTS erp_base.doc_detail_batch CASCADE;
-- DROP TABLE IF EXISTS erp_base.doc_detail_serial CASCADE;
-- DROP FUNCTION IF EXISTS erp_base.fn_validate_aux_table(VARCHAR, VARCHAR, TEXT[]);
-- ============================================================
