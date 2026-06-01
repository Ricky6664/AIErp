-- ============================================================
-- Flyway Migration Script (Rollback)
-- Version: V20260601019
-- Description: prod_product_control商品控制策略表索引回滚(DROP INDEX)
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_prod_product_control_tenant_product;
DROP INDEX IF EXISTS idx_prod_product_control_tenant_product;
DROP INDEX IF EXISTS idx_prod_product_control_product_id;
DROP INDEX IF EXISTS idx_prod_product_control_tenant_inventory;
