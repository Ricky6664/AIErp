-- ============================================================
-- Flyway Migration Script
-- Version: V20260601069
-- Description: prod_serial_template 序列号模板表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-021-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_serial_template_pkey') THEN
        ALTER INDEX prod_serial_template_pkey RENAME TO pk_prod_serial_template;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_prod_serial_template_code ON prod_serial_template(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_serial_template_tenant_code ON prod_serial_template(tenant_id, code);
CREATE INDEX idx_prod_serial_template_tenant_status ON prod_serial_template(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_serial_template_tenant_name ON prod_serial_template(tenant_id, name);
CREATE INDEX idx_prod_serial_template_tenant_created_at ON prod_serial_template(tenant_id, created_at);
