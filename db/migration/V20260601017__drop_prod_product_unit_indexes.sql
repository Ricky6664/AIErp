-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601017
-- Description: 回滚prod_product_unit商品多单位表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_prod_product_unit_product_unit;
DROP INDEX IF EXISTS idx_prod_product_unit_tenant_product;
DROP INDEX IF EXISTS idx_prod_product_unit_tenant_unit;
DROP INDEX IF EXISTS idx_prod_product_unit_tenant_base;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_product_unit') THEN
        ALTER INDEX pk_prod_product_unit RENAME TO prod_product_unit_pkey;
    END IF;
END $$;
