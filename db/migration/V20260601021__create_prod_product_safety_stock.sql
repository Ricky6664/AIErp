-- ============================================================
-- Flyway Migration Script
-- Version: V20260601021
-- Description: prod_product_safety_stock商品安全库存表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- prod_product_safety_stock 商品安全库存表
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_product_safety_stock (
    id               BIGSERIAL       PRIMARY KEY,
    tenant_id        BIGINT          NOT NULL,
    -- 业务字段
    product_id       BIGINT          NOT NULL,
    warehouse_id     BIGINT,
    min_stock_qty    DECIMAL(18,8)   NOT NULL DEFAULT 0,
    max_stock_qty    DECIMAL(18,8)   NOT NULL DEFAULT 0,
    reorder_point    DECIMAL(18,8)   NOT NULL DEFAULT 0,
    -- 通用必含字段
    created_at       TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by       BIGINT,
    updated_by       BIGINT,
    is_deleted       BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id    BIGINT,
    owner_id         BIGINT,
    version          INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE prod_product_safety_stock IS '商品安全库存表';
COMMENT ON COLUMN prod_product_safety_stock.id IS '主键ID';
COMMENT ON COLUMN prod_product_safety_stock.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_product_safety_stock.product_id IS '商品ID';
COMMENT ON COLUMN prod_product_safety_stock.warehouse_id IS '仓库ID';
COMMENT ON COLUMN prod_product_safety_stock.min_stock_qty IS '最低库存量';
COMMENT ON COLUMN prod_product_safety_stock.max_stock_qty IS '最高库存量';
COMMENT ON COLUMN prod_product_safety_stock.reorder_point IS '再订购点';
COMMENT ON COLUMN prod_product_safety_stock.created_at IS '创建时间';
COMMENT ON COLUMN prod_product_safety_stock.updated_at IS '更新时间';
COMMENT ON COLUMN prod_product_safety_stock.created_by IS '创建人ID';
COMMENT ON COLUMN prod_product_safety_stock.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_product_safety_stock.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_product_safety_stock.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_product_safety_stock.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_product_safety_stock.version IS '版本号';
