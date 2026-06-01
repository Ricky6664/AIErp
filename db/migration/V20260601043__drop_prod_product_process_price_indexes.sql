-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601043
-- Description: prod_product_process_price 商品工序价格表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-012-001-002
-- ============================================================

-- ============================================================
-- 1. 删除业务查询索引
-- ============================================================
DROP INDEX IF EXISTS idx_prod_product_process_price_effective_date;
DROP INDEX IF EXISTS idx_prod_product_process_price_status;
DROP INDEX IF EXISTS idx_prod_product_process_price_parent_id;
DROP INDEX IF EXISTS idx_prod_product_process_price_product_id;

-- ============================================================
-- 2. 删除多租户联合索引
-- ============================================================
DROP INDEX IF EXISTS idx_prod_product_process_price_tenant_product;
DROP INDEX IF EXISTS idx_prod_product_process_price_tenant_status;
DROP INDEX IF EXISTS idx_prod_product_process_price_tenant_code;

-- ============================================================
-- 3. 删除部分唯一索引
-- ============================================================
DROP INDEX IF EXISTS uk_prod_product_process_price_tenant_code;

-- ============================================================
-- 4. 主键约束恢复为默认名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_product_process_price') THEN
        ALTER INDEX pk_prod_product_process_price RENAME TO prod_product_process_price_pkey;
    END IF;
END $$;
