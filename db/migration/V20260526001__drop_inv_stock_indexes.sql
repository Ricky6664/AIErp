-- ============================================================
-- Flyway Rollback Script
-- Version: V20260526001
-- Description: inv_stock库存实时表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-007-003-001-002
-- ============================================================

DROP INDEX IF EXISTS idx_inv_stock_created_at;
DROP INDEX IF EXISTS idx_inv_stock_owner;
DROP INDEX IF EXISTS idx_inv_stock_owner_dept;
DROP INDEX IF EXISTS idx_inv_stock_updated_by;
DROP INDEX IF EXISTS idx_inv_stock_created_by;
DROP INDEX IF EXISTS idx_inv_stock_tenant_product_name;
DROP INDEX IF EXISTS idx_inv_stock_tenant_product_code;
DROP INDEX IF EXISTS idx_inv_stock_order_date;
DROP INDEX IF EXISTS idx_inv_stock_status;
DROP INDEX IF EXISTS idx_inv_stock_batch_no;
DROP INDEX IF EXISTS idx_inv_stock_warehouse;
DROP INDEX IF EXISTS idx_inv_stock_product;
DROP INDEX IF EXISTS idx_inv_stock_tenant_deleted;
DROP INDEX IF EXISTS idx_inv_stock_tenant_status;
DROP INDEX IF EXISTS idx_inv_stock_tenant_order_no;
DROP INDEX IF EXISTS uk_inv_stock_order_no;
