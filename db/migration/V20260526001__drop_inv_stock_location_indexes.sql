-- ============================================================
-- Flyway Rollback Script
-- Version: V20260526001
-- Description: inv_stock_location库位库存表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-007-004-001-002
-- ============================================================

DROP INDEX IF EXISTS idx_inv_stock_location_created_at;
DROP INDEX IF EXISTS idx_inv_stock_location_owner;
DROP INDEX IF EXISTS idx_inv_stock_location_owner_dept;
DROP INDEX IF EXISTS idx_inv_stock_location_updated_by;
DROP INDEX IF EXISTS idx_inv_stock_location_created_by;
DROP INDEX IF EXISTS idx_inv_stock_location_tenant_product_name;
DROP INDEX IF EXISTS idx_inv_stock_location_tenant_product_code;
DROP INDEX IF EXISTS idx_inv_stock_location_tenant_order_no;
DROP INDEX IF EXISTS idx_inv_stock_location_order_date;
DROP INDEX IF EXISTS idx_inv_stock_location_status;
DROP INDEX IF EXISTS idx_inv_stock_location_batch_no;
DROP INDEX IF EXISTS idx_inv_stock_location_location;
DROP INDEX IF EXISTS idx_inv_stock_location_warehouse;
DROP INDEX IF EXISTS idx_inv_stock_location_product;
DROP INDEX IF EXISTS idx_inv_stock_location_tenant_deleted;
DROP INDEX IF EXISTS idx_inv_stock_location_tenant_status;
DROP INDEX IF EXISTS uk_inv_stock_location_unique;
