-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601031
-- Description: 回滚prod_product_purchase_price商品购价核定表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_prod_product_purchase_price_tenant_product_supplier;
DROP INDEX IF EXISTS idx_prod_product_purchase_price_tenant_product;
DROP INDEX IF EXISTS idx_prod_product_purchase_price_tenant_status;
DROP INDEX IF EXISTS idx_prod_product_purchase_price_tenant_currency;
DROP INDEX IF EXISTS idx_prod_product_purchase_price_tenant_supplier;
DROP INDEX IF EXISTS idx_prod_product_purchase_price_product_id;
DROP INDEX IF EXISTS idx_prod_product_purchase_price_supplier_id;
DROP INDEX IF EXISTS idx_prod_product_purchase_price_unit_id;
DROP INDEX IF EXISTS idx_prod_product_purchase_price_status;
DROP INDEX IF EXISTS idx_prod_product_purchase_price_effective_date;
DROP INDEX IF EXISTS idx_prod_product_purchase_price_is_default;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_product_purchase_price') THEN
        ALTER INDEX pk_prod_product_purchase_price RENAME TO prod_product_purchase_price_pkey;
    END IF;
END $$;
