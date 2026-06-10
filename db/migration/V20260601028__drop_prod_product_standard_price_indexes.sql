-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601028
-- Description: 回滚prod_product_standard_price商品标准价表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_prod_product_standard_price_tenant_product;
DROP INDEX IF EXISTS idx_prod_product_standard_price_tenant_product;
DROP INDEX IF EXISTS idx_prod_product_standard_price_tenant_status;
DROP INDEX IF EXISTS idx_prod_product_standard_price_tenant_currency;
DROP INDEX IF EXISTS idx_prod_product_standard_price_product_id;
DROP INDEX IF EXISTS idx_prod_product_standard_price_unit_id;
DROP INDEX IF EXISTS idx_prod_product_standard_price_status;
DROP INDEX IF EXISTS idx_prod_product_standard_price_effective_date;
DROP INDEX IF EXISTS idx_prod_product_standard_price_is_default;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_product_standard_price') THEN
        ALTER INDEX pk_prod_product_standard_price RENAME TO prod_product_standard_price_pkey;
    END IF;
END $$;
