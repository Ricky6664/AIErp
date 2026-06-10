-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601040
-- Description: prod_product_bom_detail 商品BOM明细表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-011-001-002
-- ============================================================

-- ============================================================
-- 1. 删除业务查询索引
-- ============================================================
DROP INDEX IF EXISTS idx_prod_product_bom_detail_order_date;
DROP INDEX IF EXISTS idx_prod_product_bom_detail_status;
DROP INDEX IF EXISTS idx_prod_product_bom_detail_order_no;
DROP INDEX IF EXISTS idx_prod_product_bom_detail_sub_product_id;
DROP INDEX IF EXISTS idx_prod_product_bom_detail_bom_id;

-- ============================================================
-- 2. 删除多租户联合索引
-- ============================================================
DROP INDEX IF EXISTS idx_prod_product_bom_detail_tenant_order_date;
DROP INDEX IF EXISTS idx_prod_product_bom_detail_tenant_status;
DROP INDEX IF EXISTS idx_prod_product_bom_detail_tenant_bom;

-- ============================================================
-- 3. 删除部分唯一索引
-- ============================================================
DROP INDEX IF EXISTS uk_prod_product_bom_detail_tenant_bom_sub_product;

-- ============================================================
-- 4. 主键约束恢复为默认名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_product_bom_detail') THEN
        ALTER INDEX pk_prod_product_bom_detail RENAME TO prod_product_bom_detail_pkey;
    END IF;
END $$;
