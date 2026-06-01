-- ============================================================
-- Flyway Migration Script
-- Version: V20260601017
-- Description: prod_product_unit商品多单位表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_product_unit_pkey') THEN
        ALTER INDEX prod_product_unit_pkey RENAME TO pk_prod_product_unit;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
--    业务规则：同一租户下同一商品不能重复关联同一单位
-- ============================================================
CREATE UNIQUE INDEX uk_prod_product_unit_product_unit ON prod_product_unit(tenant_id, product_id, unit_id) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_product_unit_tenant_product ON prod_product_unit(tenant_id, product_id);
CREATE INDEX idx_prod_product_unit_tenant_unit ON prod_product_unit(tenant_id, unit_id);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_product_unit_tenant_base ON prod_product_unit(tenant_id, is_base_unit);
