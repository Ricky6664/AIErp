-- ============================================================
-- Flyway Migration Script
-- Version: V20260601019
-- Description: prod_product_control商品控制策略表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_product_control_pkey') THEN
        ALTER INDEX prod_product_control_pkey RENAME TO pk_prod_product_control;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
--    业务规则：同一租户下同一商品只能有一条控制策略记录
-- ============================================================
CREATE UNIQUE INDEX uk_prod_product_control_tenant_product ON prod_product_control(tenant_id, product_id) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_product_control_tenant_product ON prod_product_control(tenant_id, product_id);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_product_control_product_id ON prod_product_control(product_id);
CREATE INDEX idx_prod_product_control_tenant_inventory ON prod_product_control(tenant_id, is_inventory);
