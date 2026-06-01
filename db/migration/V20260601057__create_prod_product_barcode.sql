-- ============================================================
-- Flyway Migration Script
-- Version: V20260601057
-- Description: prod_product_barcode 商品条码表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- prod_product_barcode 商品条码表
-- 商品条码（条码值/条码类型/是否默认/单位ID关联）
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_product_barcode (
    id                    BIGSERIAL       PRIMARY KEY,
    tenant_id             BIGINT          NOT NULL,
    -- 业务字段
    product_id            BIGINT          NOT NULL,
    code                  VARCHAR(100)    NOT NULL,
    barcode_type          VARCHAR(50),
    unit_id               BIGINT,
    is_default            BOOLEAN         NOT NULL DEFAULT FALSE,
    status                SMALLINT        NOT NULL DEFAULT 0,
    remark                VARCHAR(500),
    -- 通用必含字段
    created_at            TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by            BIGINT,
    updated_by            BIGINT,
    is_deleted            BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id         BIGINT,
    owner_id              BIGINT,
    version               INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE prod_product_barcode IS '商品条码表';
COMMENT ON COLUMN prod_product_barcode.id IS '主键ID';
COMMENT ON COLUMN prod_product_barcode.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_product_barcode.product_id IS '商品ID';
COMMENT ON COLUMN prod_product_barcode.code IS '条码值';
COMMENT ON COLUMN prod_product_barcode.barcode_type IS '条码类型';
COMMENT ON COLUMN prod_product_barcode.unit_id IS '单位ID';
COMMENT ON COLUMN prod_product_barcode.is_default IS '是否默认条码（0-否/1-是）';
COMMENT ON COLUMN prod_product_barcode.status IS '状态（0-启用/1-停用）';
COMMENT ON COLUMN prod_product_barcode.remark IS '备注';
COMMENT ON COLUMN prod_product_barcode.created_at IS '创建时间';
COMMENT ON COLUMN prod_product_barcode.updated_at IS '更新时间';
COMMENT ON COLUMN prod_product_barcode.created_by IS '创建人ID';
COMMENT ON COLUMN prod_product_barcode.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_product_barcode.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_product_barcode.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_product_barcode.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_product_barcode.version IS '版本号';
