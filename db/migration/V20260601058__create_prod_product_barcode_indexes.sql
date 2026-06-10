-- ============================================================
-- Flyway Migration Script
-- Version: V20260601058
-- Description: prod_product_barcode 商品条码表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_product_barcode_pkey') THEN
        ALTER INDEX prod_product_barcode_pkey RENAME TO pk_prod_product_barcode;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
--    业务规则：条码值code全局唯一
-- ============================================================
CREATE UNIQUE INDEX uk_prod_product_barcode_code ON prod_product_barcode(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_product_barcode_tenant_code ON prod_product_barcode(tenant_id, code);
CREATE INDEX idx_prod_product_barcode_tenant_status ON prod_product_barcode(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_product_barcode_product_id ON prod_product_barcode(product_id);
CREATE INDEX idx_prod_product_barcode_unit_id ON prod_product_barcode(unit_id);
CREATE INDEX idx_prod_product_barcode_status ON prod_product_barcode(status);
CREATE INDEX idx_prod_product_barcode_is_default ON prod_product_barcode(is_default);
