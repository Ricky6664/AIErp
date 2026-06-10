-- ============================================================
-- Flyway Migration Script
-- Version: V20260601027
-- Description: prod_product_standard_price 商品标准价表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- prod_product_standard_price 商品标准价表
-- 商品标准价格（含税/不含税标准售价）
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_product_standard_price (
    id                    BIGSERIAL       PRIMARY KEY,
    tenant_id             BIGINT          NOT NULL,
    -- 业务字段
    product_id            BIGINT          NOT NULL,
    standard_price        DECIMAL(18,8)   NOT NULL,
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

COMMENT ON TABLE prod_product_standard_price IS '商品标准价表';
COMMENT ON COLUMN prod_product_standard_price.id IS '主键ID';
COMMENT ON COLUMN prod_product_standard_price.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_product_standard_price.product_id IS '商品ID';
COMMENT ON COLUMN prod_product_standard_price.standard_price IS '标准价格';
COMMENT ON COLUMN prod_product_standard_price.tax_inclusive_price IS '含税单价';
COMMENT ON COLUMN prod_product_standard_price.tax_exclusive_price IS '不含税单价';
COMMENT ON COLUMN prod_product_standard_price.tax_rate IS '税率(%)';
COMMENT ON COLUMN prod_product_standard_price.currency_code IS '币种代码';
COMMENT ON COLUMN prod_product_standard_price.unit_id IS '单位ID';
COMMENT ON COLUMN prod_product_standard_price.is_default IS '是否默认价格（0-否/1-是）';
COMMENT ON COLUMN prod_product_standard_price.effective_date IS '生效日期';
COMMENT ON COLUMN prod_product_standard_price.expiry_date IS '失效日期';
COMMENT ON COLUMN prod_product_standard_price.status IS '状态（0-草稿/1-已生效/2-已失效）';
COMMENT ON COLUMN prod_product_standard_price.remark IS '备注';
COMMENT ON COLUMN prod_product_standard_price.created_at IS '创建时间';
COMMENT ON COLUMN prod_product_standard_price.updated_at IS '更新时间';
COMMENT ON COLUMN prod_product_standard_price.created_by IS '创建人ID';
COMMENT ON COLUMN prod_product_standard_price.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_product_standard_price.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_product_standard_price.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_product_standard_price.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_product_standard_price.version IS '版本号';
