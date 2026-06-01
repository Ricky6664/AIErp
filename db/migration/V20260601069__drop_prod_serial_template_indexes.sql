-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601069
-- Description: 回滚prod_serial_template序列号模板表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_prod_serial_template_code;
DROP INDEX IF EXISTS idx_prod_serial_template_tenant_code;
DROP INDEX IF EXISTS idx_prod_serial_template_tenant_status;
DROP INDEX IF EXISTS idx_prod_serial_template_tenant_name;
DROP INDEX IF EXISTS idx_prod_serial_template_tenant_created_at;

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_serial_template') THEN
        ALTER INDEX pk_prod_serial_template RENAME TO prod_serial_template_pkey;
    END IF;
END $$;
