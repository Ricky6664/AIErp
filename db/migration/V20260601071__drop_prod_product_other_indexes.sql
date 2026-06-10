-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601071
-- Description: 回滚prod_product_other商品其他信息表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-022-001-002
-- ============================================================

DROP INDEX IF EXISTS uk_prod_product_other_code;
DROP INDEX IF EXISTS idx_prod_product_other_tenant_code;
DROP INDEX IF EXISTS idx_prod_product_other_tenant_status;
DROP INDEX IF EXISTS idx_prod_product_other_tenant_product_id;
DROP INDEX IF EXISTS idx_prod_product_other_tenant_barcode;
DROP INDEX IF EXISTS idx_prod_product_other_tenant_created_at;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_product_other') THEN
        ALTER INDEX pk_prod_product_other RENAME TO prod_product_other_pkey;
    END IF;
END $$;
