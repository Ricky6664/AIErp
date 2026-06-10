-- ============================================================
-- Flyway Migration Script
-- Version: V20260601016
-- Description: prod_product_unit商品多单位表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- prod_product_unit 商品多单位表
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_product_unit (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    product_id      BIGINT          NOT NULL,
    unit_id         BIGINT          NOT NULL,
    conversion_rate DECIMAL(18,8)   NOT NULL,
    is_base_unit    BOOLEAN         DEFAULT FALSE,
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

COMMENT ON TABLE prod_product_unit IS '商品多单位表';
COMMENT ON COLUMN prod_product_unit.id IS '主键ID';
COMMENT ON COLUMN prod_product_unit.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_product_unit.product_id IS '商品ID';
COMMENT ON COLUMN prod_product_unit.unit_id IS '单位ID';
COMMENT ON COLUMN prod_product_unit.conversion_rate IS '转换比例';
COMMENT ON COLUMN prod_product_unit.is_base_unit IS '是否基本单位';
COMMENT ON COLUMN prod_product_unit.created_at IS '创建时间';
COMMENT ON COLUMN prod_product_unit.updated_at IS '更新时间';
COMMENT ON COLUMN prod_product_unit.created_by IS '创建人ID';
COMMENT ON COLUMN prod_product_unit.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_product_unit.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_product_unit.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_product_unit.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_product_unit.version IS '版本号';
