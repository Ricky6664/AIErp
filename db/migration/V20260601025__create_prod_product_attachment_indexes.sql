-- ============================================================
-- Flyway Migration Script
-- Version: V20260601025
-- Description: prod_product_attachment商品附件表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_product_attachment_pkey') THEN
        ALTER INDEX prod_product_attachment_pkey RENAME TO pk_prod_product_attachment;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_prod_product_attachment_code ON prod_product_attachment(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_product_attachment_tenant_code ON prod_product_attachment(tenant_id, code);
CREATE INDEX idx_prod_product_attachment_tenant_status ON prod_product_attachment(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_product_attachment_tenant_product_id ON prod_product_attachment(tenant_id, product_id);
CREATE INDEX idx_prod_product_attachment_tenant_attach_type ON prod_product_attachment(tenant_id, attachment_type);
CREATE INDEX idx_prod_product_attachment_tenant_attach_name ON prod_product_attachment(tenant_id, attachment_name);
CREATE INDEX idx_prod_product_attachment_tenant_sort_order ON prod_product_attachment(tenant_id, sort_order);
