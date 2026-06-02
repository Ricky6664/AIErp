-- ============================================================
-- Flyway Rollback Script
-- Version: V20260526001
-- Description: inv_other_inbound_detail其他入库主从表索引回滚脚本
-- Author: AI
-- Date: 2026-06-02
-- Task: P0-003-007-006-001-002
-- ============================================================

-- ============================================================
-- 一、inv_other_inbound_detail 其他入库从表索引回滚
-- ============================================================

DROP INDEX IF EXISTS idx_inv_other_inbound_detail_created_at;
DROP INDEX IF EXISTS idx_inv_other_inbound_detail_owner;
DROP INDEX IF EXISTS idx_inv_other_inbound_detail_owner_dept;
DROP INDEX IF EXISTS idx_inv_other_inbound_detail_updated_by;
DROP INDEX IF EXISTS idx_inv_other_inbound_detail_created_by;
DROP INDEX IF EXISTS idx_inv_other_inbound_detail_tenant_product_name;
DROP INDEX IF EXISTS idx_inv_other_inbound_detail_tenant_product_code;
DROP INDEX IF EXISTS idx_inv_other_inbound_detail_batch_no;
DROP INDEX IF EXISTS idx_inv_other_inbound_detail_line_no;
DROP INDEX IF EXISTS idx_inv_other_inbound_detail_location;
DROP INDEX IF EXISTS idx_inv_other_inbound_detail_warehouse;
DROP INDEX IF EXISTS idx_inv_other_inbound_detail_product;
DROP INDEX IF EXISTS idx_inv_other_inbound_detail_order;
DROP INDEX IF EXISTS idx_inv_other_inbound_detail_tenant_deleted;
DROP INDEX IF EXISTS idx_inv_other_inbound_detail_tenant_code;
DROP INDEX IF EXISTS uk_inv_other_inbound_detail_code;

-- ============================================================
-- 二、inv_other_inbound 其他入库主表索引回滚
-- ============================================================

DROP INDEX IF EXISTS idx_inv_other_inbound_created_at;
DROP INDEX IF EXISTS idx_inv_other_inbound_owner;
DROP INDEX IF EXISTS idx_inv_other_inbound_owner_dept;
DROP INDEX IF EXISTS idx_inv_other_inbound_updated_by;
DROP INDEX IF EXISTS idx_inv_other_inbound_created_by;
DROP INDEX IF EXISTS idx_inv_other_inbound_source_order_no;
DROP INDEX IF EXISTS idx_inv_other_inbound_approver;
DROP INDEX IF EXISTS idx_inv_other_inbound_handler;
DROP INDEX IF EXISTS idx_inv_other_inbound_handling_dept;
DROP INDEX IF EXISTS idx_inv_other_inbound_inbound_date;
DROP INDEX IF EXISTS idx_inv_other_inbound_order_date;
DROP INDEX IF EXISTS idx_inv_other_inbound_status;
DROP INDEX IF EXISTS idx_inv_other_inbound_source_type;
DROP INDEX IF EXISTS idx_inv_other_inbound_warehouse;
DROP INDEX IF EXISTS idx_inv_other_inbound_tenant_deleted;
DROP INDEX IF EXISTS idx_inv_other_inbound_tenant_status;
DROP INDEX IF EXISTS idx_inv_other_inbound_tenant_order_no;
DROP INDEX IF EXISTS uk_inv_other_inbound_order_no;
