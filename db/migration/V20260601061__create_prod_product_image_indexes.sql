-- ============================================================
-- Flyway Migration Script
-- Version: V20260601061
-- Description: prod_product_image 商品图片表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_product_image_pkey') THEN
        ALTER INDEX prod_product_image_pkey RENAME TO pk_prod_product_image;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
--    业务规则：图片编码code全局唯一
-- ============================================================
CREATE UNIQUE INDEX uk_prod_product_image_code ON prod_product_image(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_product_image_tenant_code ON prod_product_image(tenant_id, code);
CREATE INDEX idx_prod_product_image_tenant_status ON prod_product_image(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_product_image_product_id ON prod_product_image(product_id);
CREATE INDEX idx_prod_product_image_image_type ON prod_product_image(image_type);
CREATE INDEX idx_prod_product_image_is_main ON prod_product_image(is_main);
CREATE INDEX idx_prod_product_image_sort_order ON prod_product_image(sort_order);
CREATE INDEX idx_prod_product_image_status ON prod_product_image(status);
