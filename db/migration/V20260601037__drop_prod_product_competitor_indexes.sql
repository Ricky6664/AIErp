-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601037
-- Description: 回滚prod_product_competitor商品竞品表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_prod_product_competitor_tenant_product_name;
DROP INDEX IF EXISTS idx_prod_product_competitor_tenant_product;
DROP INDEX IF EXISTS idx_prod_product_competitor_tenant_status;
DROP INDEX IF EXISTS idx_prod_product_competitor_product_id;
DROP INDEX IF EXISTS idx_prod_product_competitor_unit_id;
DROP INDEX IF EXISTS idx_prod_product_competitor_status;
DROP INDEX IF EXISTS idx_prod_product_competitor_effective_date;
DROP INDEX IF EXISTS idx_prod_product_competitor_expiry_date;
DROP INDEX IF EXISTS idx_prod_product_competitor_competitor_name;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_product_competitor') THEN
        ALTER INDEX pk_prod_product_competitor RENAME TO prod_product_competitor_pkey;
    END IF;
END $$;
