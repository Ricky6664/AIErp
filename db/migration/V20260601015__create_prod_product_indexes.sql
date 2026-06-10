-- ============================================================
-- Flyway Migration Script
-- Version: V20260601015
-- Description: prod_product商品主表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_product_pkey') THEN
        ALTER INDEX prod_product_pkey RENAME TO pk_prod_product;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_prod_product_code ON prod_product(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_product_tenant_code ON prod_product(tenant_id, code);
CREATE INDEX idx_prod_product_tenant_status ON prod_product(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_product_tenant_class_id ON prod_product(tenant_id, class_id);
CREATE INDEX idx_prod_product_tenant_base_unit_id ON prod_product(tenant_id, base_unit_id);
CREATE INDEX idx_prod_product_tenant_created_at ON prod_product(tenant_id, created_at);
CREATE INDEX idx_prod_product_tenant_name ON prod_product(tenant_id, product_name);
