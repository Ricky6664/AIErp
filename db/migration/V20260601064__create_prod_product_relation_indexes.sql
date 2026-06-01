-- ============================================================
-- Flyway Migration Script
-- Version: V20260601064
-- Description: prod_product_relation 商品关联表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-019-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_product_relation_pkey') THEN
        ALTER INDEX prod_product_relation_pkey RENAME TO pk_prod_product_relation;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
--    业务规则：关联编码code全局唯一
-- ============================================================
CREATE UNIQUE INDEX uk_prod_product_relation_code ON prod_product_relation(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_product_relation_tenant_code ON prod_product_relation(tenant_id, code);
CREATE INDEX idx_prod_product_relation_tenant_status ON prod_product_relation(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_product_relation_product_id ON prod_product_relation(product_id);
CREATE INDEX idx_prod_product_relation_related_product_id ON prod_product_relation(related_product_id);
CREATE INDEX idx_prod_product_relation_relation_type ON prod_product_relation(relation_type);
CREATE INDEX idx_prod_product_relation_status ON prod_product_relation(status);
CREATE INDEX idx_prod_product_relation_sort_order ON prod_product_relation(sort_order);
