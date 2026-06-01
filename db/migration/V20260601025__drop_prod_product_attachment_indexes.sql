-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601025
-- Description: 回滚prod_product_attachment商品附件表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_prod_product_attachment_code;
DROP INDEX IF EXISTS idx_prod_product_attachment_tenant_code;
DROP INDEX IF EXISTS idx_prod_product_attachment_tenant_status;
DROP INDEX IF EXISTS idx_prod_product_attachment_tenant_product_id;
DROP INDEX IF EXISTS idx_prod_product_attachment_tenant_attach_type;
DROP INDEX IF EXISTS idx_prod_product_attachment_tenant_attach_name;
DROP INDEX IF EXISTS idx_prod_product_attachment_tenant_sort_order;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_product_attachment') THEN
        ALTER INDEX pk_prod_product_attachment RENAME TO prod_product_attachment_pkey;
    END IF;
END $$;
