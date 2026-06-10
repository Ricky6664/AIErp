-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601015
-- Description: 回滚prod_product商品主表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_prod_product_code;
DROP INDEX IF EXISTS idx_prod_product_tenant_code;
DROP INDEX IF EXISTS idx_prod_product_tenant_status;
DROP INDEX IF EXISTS idx_prod_product_tenant_class_id;
DROP INDEX IF EXISTS idx_prod_product_tenant_base_unit_id;
DROP INDEX IF EXISTS idx_prod_product_tenant_created_at;
DROP INDEX IF EXISTS idx_prod_product_tenant_name;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_product') THEN
        ALTER INDEX pk_prod_product RENAME TO prod_product_pkey;
    END IF;
END $$;
