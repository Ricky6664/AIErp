-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601064
-- Description: 回滚prod_product_relation商品关联表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_prod_product_relation_code;
DROP INDEX IF EXISTS idx_prod_product_relation_tenant_code;
DROP INDEX IF EXISTS idx_prod_product_relation_tenant_status;
DROP INDEX IF EXISTS idx_prod_product_relation_product_id;
DROP INDEX IF EXISTS idx_prod_product_relation_related_product_id;
DROP INDEX IF EXISTS idx_prod_product_relation_relation_type;
DROP INDEX IF EXISTS idx_prod_product_relation_status;
DROP INDEX IF EXISTS idx_prod_product_relation_sort_order;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_product_relation') THEN
        ALTER INDEX pk_prod_product_relation RENAME TO prod_product_relation_pkey;
    END IF;
END $$;
