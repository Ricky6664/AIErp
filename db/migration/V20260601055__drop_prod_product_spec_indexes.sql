-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601055
-- Description: 回滚prod_product_spec商品规格表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_prod_product_spec_code;
DROP INDEX IF EXISTS idx_prod_product_spec_tenant_code;
DROP INDEX IF EXISTS idx_prod_product_spec_tenant_status;
DROP INDEX IF EXISTS idx_prod_product_spec_tenant_name;
DROP INDEX IF EXISTS idx_prod_product_spec_tenant_sort;
DROP INDEX IF EXISTS idx_prod_product_spec_tenant_created_at;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_product_spec') THEN
        ALTER INDEX pk_prod_product_spec RENAME TO prod_product_spec_pkey;
    END IF;
END $$;
