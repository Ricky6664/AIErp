-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601061
-- Description: 回滚prod_product_image商品图片表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_prod_product_image_code;
DROP INDEX IF EXISTS idx_prod_product_image_tenant_code;
DROP INDEX IF EXISTS idx_prod_product_image_tenant_status;
DROP INDEX IF EXISTS idx_prod_product_image_product_id;
DROP INDEX IF EXISTS idx_prod_product_image_image_type;
DROP INDEX IF EXISTS idx_prod_product_image_is_main;
DROP INDEX IF EXISTS idx_prod_product_image_sort_order;
DROP INDEX IF EXISTS idx_prod_product_image_status;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_product_image') THEN
        ALTER INDEX pk_prod_product_image RENAME TO prod_product_image_pkey;
    END IF;
END $$;
