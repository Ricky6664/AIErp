-- ============================================================
-- Flyway Migration Script
-- Version: V20260601030
-- Description: prod_product_purchase_price 商品购价核定表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- prod_product_purchase_price 商品购价核定表
-- 商品采购价格核定（含税/不含税采购价）
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_product_purchase_price (
    id                    BIGSERIAL       PRIMARY KEY,
    tenant_id             BIGINT          NOT NULL,
    -- 业务字段
    product_id            BIGINT          NOT NULL,
    supplier_id           BIGINT,
    purchase_price        DECIMAL(18,8)   NOT NULL,
    tax_inclusive_price   DECIMAL(18,8),
    tax_exclusive_price   DECIMAL(18,8),
    tax_rate              DECIMAL(18,8),
    currency_code         VARCHAR(10)     NOT NULL DEFAULT 'CNY',
    unit_id               BIGINT,
    is_default            BOOLEAN         NOT NULL DEFAULT FALSE,
    effective_date        DATE,
    expiry_date           DATE,
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

COMMENT ON TABLE prod_product_purchase_price IS '商品购价核定表';
COMMENT ON COLUMN prod_product_purchase_price.id IS '主键ID';
COMMENT ON COLUMN prod_product_purchase_price.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_product_purchase_price.product_id IS '商品ID';
COMMENT ON COLUMN prod_product_purchase_price.supplier_id IS '供应商ID';
COMMENT ON COLUMN prod_product_purchase_price.purchase_price IS '采购价格';
COMMENT ON COLUMN prod_product_purchase_price.tax_inclusive_price IS '含税单价';
COMMENT ON COLUMN prod_product_purchase_price.tax_exclusive_price IS '不含税单价';
COMMENT ON COLUMN prod_product_purchase_price.tax_rate IS '税率(%)';
COMMENT ON COLUMN prod_product_purchase_price.currency_code IS '币种代码';
COMMENT ON COLUMN prod_product_purchase_price.unit_id IS '单位ID';
COMMENT ON COLUMN prod_product_purchase_price.is_default IS '是否默认价格（0-否/1-是）';
COMMENT ON COLUMN prod_product_purchase_price.effective_date IS '生效日期';
COMMENT ON COLUMN prod_product_purchase_price.expiry_date IS '失效日期';
COMMENT ON COLUMN prod_product_purchase_price.status IS '状态（0-草稿/1-已生效/2-已失效）';
COMMENT ON COLUMN prod_product_purchase_price.remark IS '备注';
COMMENT ON COLUMN prod_product_purchase_price.created_at IS '创建时间';
COMMENT ON COLUMN prod_product_purchase_price.updated_at IS '更新时间';
COMMENT ON COLUMN prod_product_purchase_price.created_by IS '创建人ID';
COMMENT ON COLUMN prod_product_purchase_price.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_product_purchase_price.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_product_purchase_price.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_product_purchase_price.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_product_purchase_price.version IS '版本号';
