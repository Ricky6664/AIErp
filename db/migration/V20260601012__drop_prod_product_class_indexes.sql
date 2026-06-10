-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601012
-- Description: 回滚prod_product_class商品分类表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_prod_product_class_code;
DROP INDEX IF EXISTS idx_prod_product_class_tenant_code;
DROP INDEX IF EXISTS idx_prod_product_class_tenant_status;
DROP INDEX IF EXISTS idx_prod_product_class_parent_id;
DROP INDEX IF EXISTS idx_prod_product_class_tenant_sort;
DROP INDEX IF EXISTS idx_prod_product_class_tenant_created_at;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_product_class') THEN
        ALTER INDEX pk_prod_product_class RENAME TO prod_product_class_pkey;
    END IF;
END $$;

ALTER TABLE prod_product_class DROP COLUMN IF EXISTS status;
