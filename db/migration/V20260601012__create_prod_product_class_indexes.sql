-- ============================================================
-- Flyway Migration Script
-- Version: V20260601012
-- Description: prod_product_class商品分类表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 补充业务字段（status列——任务规格5.3要求创建tenant_status联合索引）
-- ============================================================
ALTER TABLE prod_product_class ADD COLUMN IF NOT EXISTS status VARCHAR(30) DEFAULT 'active';
COMMENT ON COLUMN prod_product_class.status IS '状态：active=启用/inactive=停用';

-- ============================================================
-- 2. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_product_class_pkey') THEN
        ALTER INDEX prod_product_class_pkey RENAME TO pk_prod_product_class;
    END IF;
END $$;

-- ============================================================
-- 3. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_prod_product_class_code ON prod_product_class(code) WHERE is_deleted = false;

-- ============================================================
-- 4. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_product_class_tenant_code ON prod_product_class(tenant_id, code);
CREATE INDEX idx_prod_product_class_tenant_status ON prod_product_class(tenant_id, status);

-- ============================================================
-- 5. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_product_class_parent_id ON prod_product_class(tenant_id, parent_id);
CREATE INDEX idx_prod_product_class_tenant_sort ON prod_product_class(tenant_id, sort_order);
CREATE INDEX idx_prod_product_class_tenant_created_at ON prod_product_class(tenant_id, created_at);
