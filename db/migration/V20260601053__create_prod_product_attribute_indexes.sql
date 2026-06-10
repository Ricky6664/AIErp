-- ============================================================
-- Flyway Migration Script
-- Version: V20260601053
-- Description: prod_product_attribute 商品属性表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_product_attribute_pkey') THEN
        ALTER INDEX prod_product_attribute_pkey RENAME TO pk_prod_product_attribute;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_prod_product_attribute_code ON prod_product_attribute(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_product_attribute_tenant_code ON prod_product_attribute(tenant_id, code);
CREATE INDEX idx_prod_product_attribute_tenant_status ON prod_product_attribute(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_product_attribute_parent_id ON prod_product_attribute(tenant_id, parent_id);
CREATE INDEX idx_prod_product_attribute_tenant_sort ON prod_product_attribute(tenant_id, sort_order);
CREATE INDEX idx_prod_product_attribute_tenant_created_at ON prod_product_attribute(tenant_id, created_at);
