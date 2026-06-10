-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260526001
-- Description: inv_transfer_detail调拨主从表索引回滚脚本
-- Author: AI
-- Date: 2026-06-02
-- Task: P0-003-007-008-001-002
-- ============================================================

-- ============================================================
-- 一、inv_transfer_detail 调拨从表索引回滚
-- ============================================================

DROP INDEX IF EXISTS idx_inv_transfer_detail_created_at;
DROP INDEX IF EXISTS idx_inv_transfer_detail_owner;
DROP INDEX IF EXISTS idx_inv_transfer_detail_owner_dept;
DROP INDEX IF EXISTS idx_inv_transfer_detail_updated_by;
DROP INDEX IF EXISTS idx_inv_transfer_detail_created_by;
DROP INDEX IF EXISTS idx_inv_transfer_detail_tenant_product_name;
DROP INDEX IF EXISTS idx_inv_transfer_detail_tenant_product_code;
DROP INDEX IF EXISTS idx_inv_transfer_detail_batch_no;
DROP INDEX IF EXISTS idx_inv_transfer_detail_line_no;
DROP INDEX IF EXISTS idx_inv_transfer_detail_to_location;
DROP INDEX IF EXISTS idx_inv_transfer_detail_from_location;
DROP INDEX IF EXISTS idx_inv_transfer_detail_to_warehouse;
DROP INDEX IF EXISTS idx_inv_transfer_detail_from_warehouse;
DROP INDEX IF EXISTS idx_inv_transfer_detail_product;
DROP INDEX IF EXISTS idx_inv_transfer_detail_order;
DROP INDEX IF EXISTS idx_inv_transfer_detail_tenant_deleted;
DROP INDEX IF EXISTS idx_inv_transfer_detail_tenant_code;
DROP INDEX IF EXISTS uk_inv_transfer_detail_code;

-- ============================================================
-- 二、inv_transfer 调拨主表索引回滚
-- ============================================================

DROP INDEX IF EXISTS idx_inv_transfer_created_at;
DROP INDEX IF EXISTS idx_inv_transfer_owner;
DROP INDEX IF EXISTS idx_inv_transfer_owner_dept;
DROP INDEX IF EXISTS idx_inv_transfer_updated_by;
DROP INDEX IF EXISTS idx_inv_transfer_created_by;
DROP INDEX IF EXISTS idx_inv_transfer_approver;
DROP INDEX IF EXISTS idx_inv_transfer_handler;
DROP INDEX IF EXISTS idx_inv_transfer_handling_dept;
DROP INDEX IF EXISTS idx_inv_transfer_transfer_date;
DROP INDEX IF EXISTS idx_inv_transfer_order_date;
DROP INDEX IF EXISTS idx_inv_transfer_status;
DROP INDEX IF EXISTS idx_inv_transfer_to_warehouse;
DROP INDEX IF EXISTS idx_inv_transfer_from_warehouse;
DROP INDEX IF EXISTS idx_inv_transfer_tenant_deleted;
DROP INDEX IF EXISTS idx_inv_transfer_tenant_status;
DROP INDEX IF EXISTS idx_inv_transfer_tenant_order_no;
DROP INDEX IF EXISTS uk_inv_transfer_order_no;
