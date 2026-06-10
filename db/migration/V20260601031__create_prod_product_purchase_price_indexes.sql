-- ============================================================
-- Flyway Migration Script
-- Version: V20260601031
-- Description: prod_product_purchase_price商品购价核定表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_product_purchase_price_pkey') THEN
        ALTER INDEX prod_product_purchase_price_pkey RENAME TO pk_prod_product_purchase_price;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
--    业务规则：同一租户下同一商品同一供应商只能有一条购价记录
-- ============================================================
CREATE UNIQUE INDEX uk_prod_product_purchase_price_tenant_product_supplier ON prod_product_purchase_price(tenant_id, product_id, supplier_id) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_product_purchase_price_tenant_product ON prod_product_purchase_price(tenant_id, product_id);
CREATE INDEX idx_prod_product_purchase_price_tenant_status ON prod_product_purchase_price(tenant_id, status);
CREATE INDEX idx_prod_product_purchase_price_tenant_currency ON prod_product_purchase_price(tenant_id, currency_code);
CREATE INDEX idx_prod_product_purchase_price_tenant_supplier ON prod_product_purchase_price(tenant_id, supplier_id);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_product_purchase_price_product_id ON prod_product_purchase_price(product_id);
CREATE INDEX idx_prod_product_purchase_price_supplier_id ON prod_product_purchase_price(supplier_id);
CREATE INDEX idx_prod_product_purchase_price_unit_id ON prod_product_purchase_price(unit_id);
CREATE INDEX idx_prod_product_purchase_price_status ON prod_product_purchase_price(status);
CREATE INDEX idx_prod_product_purchase_price_effective_date ON prod_product_purchase_price(effective_date);
CREATE INDEX idx_prod_product_purchase_price_is_default ON prod_product_purchase_price(is_default);
