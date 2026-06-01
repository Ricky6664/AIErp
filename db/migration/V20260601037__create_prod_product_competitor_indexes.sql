-- ============================================================
-- Flyway Migration Script
-- Version: V20260601037
-- Description: prod_product_competitor 商品竞品表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_product_competitor_pkey') THEN
        ALTER INDEX prod_product_competitor_pkey RENAME TO pk_prod_product_competitor;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
--    业务规则：同一租户下同一商品不能有重名竞品记录
-- ============================================================
CREATE UNIQUE INDEX uk_prod_product_competitor_tenant_product_name ON prod_product_competitor(tenant_id, product_id, competitor_name) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_product_competitor_tenant_product ON prod_product_competitor(tenant_id, product_id);
CREATE INDEX idx_prod_product_competitor_tenant_status ON prod_product_competitor(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_product_competitor_product_id ON prod_product_competitor(product_id);
CREATE INDEX idx_prod_product_competitor_unit_id ON prod_product_competitor(unit_id);
CREATE INDEX idx_prod_product_competitor_status ON prod_product_competitor(status);
CREATE INDEX idx_prod_product_competitor_effective_date ON prod_product_competitor(effective_date);
CREATE INDEX idx_prod_product_competitor_expiry_date ON prod_product_competitor(expiry_date);
CREATE INDEX idx_prod_product_competitor_competitor_name ON prod_product_competitor(competitor_name);
