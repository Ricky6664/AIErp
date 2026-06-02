-- ============================================================
-- Flyway Rollback Script
-- Version: V20260526001
-- Description: inv_stocktaking_detail盘点主从表索引回滚脚本
-- Author: AI
-- Date: 2026-06-02
-- Task: P0-003-007-007-001-002
-- ============================================================

-- ============================================================
-- 一、inv_stocktaking_detail 盘点从表索引回滚
-- ============================================================

DROP INDEX IF EXISTS idx_inv_stocktaking_detail_created_at;
DROP INDEX IF EXISTS idx_inv_stocktaking_detail_owner;
DROP INDEX IF EXISTS idx_inv_stocktaking_detail_owner_dept;
DROP INDEX IF EXISTS idx_inv_stocktaking_detail_updated_by;
DROP INDEX IF EXISTS idx_inv_stocktaking_detail_created_by;
DROP INDEX IF EXISTS idx_inv_stocktaking_detail_tenant_product_name;
DROP INDEX IF EXISTS idx_inv_stocktaking_detail_tenant_product_code;
DROP INDEX IF EXISTS idx_inv_stocktaking_detail_batch_no;
DROP INDEX IF EXISTS idx_inv_stocktaking_detail_line_no;
DROP INDEX IF EXISTS idx_inv_stocktaking_detail_location;
DROP INDEX IF EXISTS idx_inv_stocktaking_detail_warehouse;
DROP INDEX IF EXISTS idx_inv_stocktaking_detail_product;
DROP INDEX IF EXISTS idx_inv_stocktaking_detail_order;
DROP INDEX IF EXISTS idx_inv_stocktaking_detail_tenant_deleted;
DROP INDEX IF EXISTS idx_inv_stocktaking_detail_tenant_code;
DROP INDEX IF EXISTS uk_inv_stocktaking_detail_code;

-- ============================================================
-- 二、inv_stocktaking 盘点主表索引回滚
-- ============================================================

DROP INDEX IF EXISTS idx_inv_stocktaking_created_at;
DROP INDEX IF EXISTS idx_inv_stocktaking_owner;
DROP INDEX IF EXISTS idx_inv_stocktaking_owner_dept;
DROP INDEX IF EXISTS idx_inv_stocktaking_updated_by;
DROP INDEX IF EXISTS idx_inv_stocktaking_created_by;
DROP INDEX IF EXISTS idx_inv_stocktaking_approver;
DROP INDEX IF EXISTS idx_inv_stocktaking_handler;
DROP INDEX IF EXISTS idx_inv_stocktaking_handling_dept;
DROP INDEX IF EXISTS idx_inv_stocktaking_stocktaking_date;
DROP INDEX IF EXISTS idx_inv_stocktaking_order_date;
DROP INDEX IF EXISTS idx_inv_stocktaking_status;
DROP INDEX IF EXISTS idx_inv_stocktaking_warehouse;
DROP INDEX IF EXISTS idx_inv_stocktaking_tenant_deleted;
DROP INDEX IF EXISTS idx_inv_stocktaking_tenant_status;
DROP INDEX IF EXISTS idx_inv_stocktaking_tenant_order_no;
DROP INDEX IF EXISTS uk_inv_stocktaking_order_no;
