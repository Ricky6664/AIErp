-- ============================================================
-- Flyway Migration Script
-- Version: V20260601022
-- Description: prod_product_safety_stock商品安全库存表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_product_safety_stock_pkey') THEN
        ALTER INDEX prod_product_safety_stock_pkey RENAME TO pk_prod_product_safety_stock;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
--    业务规则：同一租户下同一商品在同一仓库只能有一条安全库存记录
-- ============================================================
CREATE UNIQUE INDEX uk_prod_product_safety_stock_tenant_product_warehouse ON prod_product_safety_stock(tenant_id, product_id, warehouse_id) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_product_safety_stock_tenant_product ON prod_product_safety_stock(tenant_id, product_id);
CREATE INDEX idx_prod_product_safety_stock_tenant_warehouse ON prod_product_safety_stock(tenant_id, warehouse_id);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_product_safety_stock_product_id ON prod_product_safety_stock(product_id);
CREATE INDEX idx_prod_product_safety_stock_warehouse_id ON prod_product_safety_stock(warehouse_id);
