-- ============================================================
-- Flyway Migration Script (Rollback)
-- Version: V20260601022
-- Description: prod_product_safety_stock商品安全库存表索引回滚(DROP INDEX)
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_prod_product_safety_stock_tenant_product_warehouse;
DROP INDEX IF EXISTS idx_prod_product_safety_stock_tenant_product;
DROP INDEX IF EXISTS idx_prod_product_safety_stock_tenant_warehouse;
DROP INDEX IF EXISTS idx_prod_product_safety_stock_product_id;
DROP INDEX IF EXISTS idx_prod_product_safety_stock_warehouse_id;
