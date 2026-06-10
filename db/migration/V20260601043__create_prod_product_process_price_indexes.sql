-- ============================================================
-- Flyway Migration Script
-- Version: V20260601043
-- Description: prod_product_process_price 商品工序价格表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-012-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_product_process_price_pkey') THEN
        ALTER INDEX prod_product_process_price_pkey RENAME TO pk_prod_product_process_price;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
--    业务规则：同一租户下工序编码唯一
-- ============================================================
CREATE UNIQUE INDEX uk_prod_product_process_price_tenant_code ON prod_product_process_price(tenant_id, code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_product_process_price_tenant_code ON prod_product_process_price(tenant_id, code);
CREATE INDEX idx_prod_product_process_price_tenant_status ON prod_product_process_price(tenant_id, status);
CREATE INDEX idx_prod_product_process_price_tenant_product ON prod_product_process_price(tenant_id, product_id);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_product_process_price_product_id ON prod_product_process_price(product_id);
CREATE INDEX idx_prod_product_process_price_parent_id ON prod_product_process_price(parent_id);
CREATE INDEX idx_prod_product_process_price_status ON prod_product_process_price(status);
CREATE INDEX idx_prod_product_process_price_effective_date ON prod_product_process_price(effective_date);
