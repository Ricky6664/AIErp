-- ============================================================
-- Flyway Migration Script
-- Version: V20260601028
-- Description: prod_product_standard_price商品标准价表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_product_standard_price_pkey') THEN
        ALTER INDEX prod_product_standard_price_pkey RENAME TO pk_prod_product_standard_price;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
--    业务规则：同一租户下同一商品只能有一条标准价记录
-- ============================================================
CREATE UNIQUE INDEX uk_prod_product_standard_price_tenant_product ON prod_product_standard_price(tenant_id, product_id) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_product_standard_price_tenant_product ON prod_product_standard_price(tenant_id, product_id);
CREATE INDEX idx_prod_product_standard_price_tenant_status ON prod_product_standard_price(tenant_id, status);
CREATE INDEX idx_prod_product_standard_price_tenant_currency ON prod_product_standard_price(tenant_id, currency_code);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_product_standard_price_product_id ON prod_product_standard_price(product_id);
CREATE INDEX idx_prod_product_standard_price_unit_id ON prod_product_standard_price(unit_id);
CREATE INDEX idx_prod_product_standard_price_status ON prod_product_standard_price(status);
CREATE INDEX idx_prod_product_standard_price_effective_date ON prod_product_standard_price(effective_date);
CREATE INDEX idx_prod_product_standard_price_is_default ON prod_product_standard_price(is_default);
