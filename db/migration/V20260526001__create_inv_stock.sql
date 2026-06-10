-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: inv_stock库存实时表建表DDL
-- Author: AI Generated
-- Date: 2026-06-02
-- ============================================================

-- ============================================================
-- inv_stock 库存实时表
-- ============================================================
CREATE TABLE IF NOT EXISTS inv_stock (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    product_id      BIGINT          NOT NULL,
    warehouse_id    BIGINT          NOT NULL,
    batch_no        VARCHAR(50),
    qty             DECIMAL(18,8)   DEFAULT 0,
    amount          DECIMAL(18,8)   DEFAULT 0,
    -- 单据字段
    order_no        VARCHAR(50)     NOT NULL,
    order_date      DATE            NOT NULL,
    status          SMALLINT        DEFAULT 0,
    remark          VARCHAR(500),
    -- 商品快照字段
    product_code    VARCHAR(50),
    product_name    VARCHAR(100),
    model           VARCHAR(100),
    spec            VARCHAR(100),
    brand           VARCHAR(50),
    unit_id         BIGINT,
    unit            VARCHAR(30),
    is_multi_unit   BOOLEAN,
    conversion_rate DECIMAL(18,8),
    base_unit_id    BIGINT,
    base_qty        DECIMAL(18,8),
    -- 扩展字段
    ext_str1        VARCHAR(200),
    ext_str2        VARCHAR(200),
    ext_str3        VARCHAR(200),
    ext_str4        VARCHAR(200),
    ext_str5        VARCHAR(200),
    ext_str6        VARCHAR(200),
    ext_str7        VARCHAR(200),
    ext_str8        VARCHAR(200),
    ext_str9        VARCHAR(200),
    ext_str10       VARCHAR(200),
    ext_num1        DECIMAL(18,8),
    ext_num2        DECIMAL(18,8),
    ext_num3        DECIMAL(18,8),
    ext_num4        DECIMAL(18,8),
    ext_num5        DECIMAL(18,8),
    ext_date1       DATE,
    ext_date2       DATE,
    ext_date3       DATE,
    ext_bool1       BOOLEAN,
    ext_bool2       BOOLEAN,
    ext_bool3       BOOLEAN,
    ext_json        JSONB,
    -- 通用必含字段
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    version         INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE inv_stock IS '库存实时表';
COMMENT ON COLUMN inv_stock.id IS '主键ID';
COMMENT ON COLUMN inv_stock.tenant_id IS '租户ID';
COMMENT ON COLUMN inv_stock.product_id IS '商品ID';
COMMENT ON COLUMN inv_stock.warehouse_id IS '仓库ID';
COMMENT ON COLUMN inv_stock.batch_no IS '批次号';
COMMENT ON COLUMN inv_stock.qty IS '库存数量';
COMMENT ON COLUMN inv_stock.amount IS '库存金额';
COMMENT ON COLUMN inv_stock.order_no IS '单据编号';
COMMENT ON COLUMN inv_stock.order_date IS '单据日期';
COMMENT ON COLUMN inv_stock.status IS '单据状态：0=草稿/1=已审核/2=已完成';
COMMENT ON COLUMN inv_stock.remark IS '备注';
COMMENT ON COLUMN inv_stock.product_code IS '商品编码快照';
COMMENT ON COLUMN inv_stock.product_name IS '商品名称快照';
COMMENT ON COLUMN inv_stock.model IS '型号快照';
COMMENT ON COLUMN inv_stock.spec IS '规格快照';
COMMENT ON COLUMN inv_stock.brand IS '品牌快照';
COMMENT ON COLUMN inv_stock.unit_id IS '单位ID快照';
COMMENT ON COLUMN inv_stock.unit IS '单位名称快照';
COMMENT ON COLUMN inv_stock.is_multi_unit IS '是否多单位快照';
COMMENT ON COLUMN inv_stock.conversion_rate IS '转换比例快照';
COMMENT ON COLUMN inv_stock.base_unit_id IS '基础单位ID快照';
COMMENT ON COLUMN inv_stock.base_qty IS '换算数量';
COMMENT ON COLUMN inv_stock.created_at IS '创建时间';
COMMENT ON COLUMN inv_stock.updated_at IS '更新时间';
COMMENT ON COLUMN inv_stock.created_by IS '创建人ID';
COMMENT ON COLUMN inv_stock.updated_by IS '修改人ID';
COMMENT ON COLUMN inv_stock.is_deleted IS '是否删除';
COMMENT ON COLUMN inv_stock.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN inv_stock.owner_id IS '数据负责人ID';
COMMENT ON COLUMN inv_stock.version IS '版本号';
