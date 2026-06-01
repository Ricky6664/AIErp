-- ============================================================
-- Flyway Migration Script
-- Version: V20260601040
-- Description: prod_product_bom_detail 商品BOM明细表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-011-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_product_bom_detail_pkey') THEN
        ALTER INDEX prod_product_bom_detail_pkey RENAME TO pk_prod_product_bom_detail;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
--    业务规则：同一BOM下不能有重复的子件商品
-- ============================================================
CREATE UNIQUE INDEX uk_prod_product_bom_detail_tenant_bom_sub_product ON prod_product_bom_detail(tenant_id, bom_id, sub_product_id) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_product_bom_detail_tenant_bom ON prod_product_bom_detail(tenant_id, bom_id);
CREATE INDEX idx_prod_product_bom_detail_tenant_status ON prod_product_bom_detail(tenant_id, status);
CREATE INDEX idx_prod_product_bom_detail_tenant_order_date ON prod_product_bom_detail(tenant_id, order_date);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_product_bom_detail_bom_id ON prod_product_bom_detail(bom_id);
CREATE INDEX idx_prod_product_bom_detail_sub_product_id ON prod_product_bom_detail(sub_product_id);
CREATE INDEX idx_prod_product_bom_detail_order_no ON prod_product_bom_detail(order_no);
CREATE INDEX idx_prod_product_bom_detail_status ON prod_product_bom_detail(status);
CREATE INDEX idx_prod_product_bom_detail_order_date ON prod_product_bom_detail(order_date);
