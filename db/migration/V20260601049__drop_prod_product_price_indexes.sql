-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601049
-- Description: 回滚prod_product_price商品价格表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_prod_product_price_code;
DROP INDEX IF EXISTS idx_prod_product_price_tenant_code;
DROP INDEX IF EXISTS idx_prod_product_price_tenant_status;
DROP INDEX IF EXISTS idx_prod_product_price_product_id;
DROP INDEX IF EXISTS idx_prod_product_price_unit_id;
DROP INDEX IF EXISTS idx_prod_product_price_status;
DROP INDEX IF EXISTS idx_prod_product_price_price_type;
DROP INDEX IF EXISTS idx_prod_product_price_currency_code;
DROP INDEX IF EXISTS idx_prod_product_price_is_default;
DROP INDEX IF EXISTS idx_prod_product_price_effective_date;
DROP INDEX IF EXISTS idx_prod_product_price_expiry_date;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_product_price') THEN
        ALTER INDEX pk_prod_product_price RENAME TO prod_product_price_pkey;
    END IF;
END $$;
