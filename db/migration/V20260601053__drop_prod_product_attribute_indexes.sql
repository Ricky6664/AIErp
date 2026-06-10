-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601053
-- Description: 回滚prod_product_attribute商品属性表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_prod_product_attribute_code;
DROP INDEX IF EXISTS idx_prod_product_attribute_tenant_code;
DROP INDEX IF EXISTS idx_prod_product_attribute_tenant_status;
DROP INDEX IF EXISTS idx_prod_product_attribute_parent_id;
DROP INDEX IF EXISTS idx_prod_product_attribute_tenant_sort;
DROP INDEX IF EXISTS idx_prod_product_attribute_tenant_created_at;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_product_attribute') THEN
        ALTER INDEX pk_prod_product_attribute RENAME TO prod_product_attribute_pkey;
    END IF;
END $$;
