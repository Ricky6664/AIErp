-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601058
-- Description: 回滚prod_product_barcode商品条码表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_prod_product_barcode_code;
DROP INDEX IF EXISTS idx_prod_product_barcode_tenant_code;
DROP INDEX IF EXISTS idx_prod_product_barcode_tenant_status;
DROP INDEX IF EXISTS idx_prod_product_barcode_product_id;
DROP INDEX IF EXISTS idx_prod_product_barcode_unit_id;
DROP INDEX IF EXISTS idx_prod_product_barcode_status;
DROP INDEX IF EXISTS idx_prod_product_barcode_is_default;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_product_barcode') THEN
        ALTER INDEX pk_prod_product_barcode RENAME TO prod_product_barcode_pkey;
    END IF;
END $$;
