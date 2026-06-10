-- ============================================================
-- Flyway Migration Script
-- Version: V20260601067
-- Description: prod_product_tag 商品标签关联表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-020-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_product_tag_pkey') THEN
        ALTER INDEX prod_product_tag_pkey RENAME TO pk_prod_product_tag;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
--    业务规则：同一商品(product_id)对同一标签定义(tag_definition_id)只能关联一次
--    PostgreSQL中NULL值在唯一索引中被视为不同值，允许多个NULL tag_definition_id
-- ============================================================
CREATE UNIQUE INDEX uk_prod_product_tag_product_def ON prod_product_tag(product_id, tag_definition_id) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_product_tag_tenant_product ON prod_product_tag(tenant_id, product_id);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_product_tag_product_id ON prod_product_tag(product_id);
CREATE INDEX idx_prod_product_tag_tag_def_id ON prod_product_tag(tag_definition_id);
CREATE INDEX idx_prod_product_tag_sort_order ON prod_product_tag(sort_order);
