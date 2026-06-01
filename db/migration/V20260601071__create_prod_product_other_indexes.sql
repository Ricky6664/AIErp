-- ============================================================
-- Flyway Migration Script
-- Version: V20260601071
-- Description: prod_product_other 商品其他信息表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-022-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_product_other_pkey') THEN
        ALTER INDEX prod_product_other_pkey RENAME TO pk_prod_product_other;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_prod_product_other_code ON prod_product_other(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_product_other_tenant_code ON prod_product_other(tenant_id, code);
CREATE INDEX idx_prod_product_other_tenant_status ON prod_product_other(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_product_other_tenant_product_id ON prod_product_other(tenant_id, product_id);
CREATE INDEX idx_prod_product_other_tenant_barcode ON prod_product_other(tenant_id, barcode);
CREATE INDEX idx_prod_product_other_tenant_created_at ON prod_product_other(tenant_id, created_at);
