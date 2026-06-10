-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601067
-- Description: 回滚prod_product_tag商品标签关联表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_prod_product_tag_product_def;
DROP INDEX IF EXISTS idx_prod_product_tag_tenant_product;
DROP INDEX IF EXISTS idx_prod_product_tag_product_id;
DROP INDEX IF EXISTS idx_prod_product_tag_tag_def_id;
DROP INDEX IF EXISTS idx_prod_product_tag_sort_order;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_product_tag') THEN
        ALTER INDEX pk_prod_product_tag RENAME TO prod_product_tag_pkey;
    END IF;
END $$;
