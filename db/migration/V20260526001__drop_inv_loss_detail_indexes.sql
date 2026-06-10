-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260526001
-- Description: inv_loss_detail报损主从表索引回滚脚本
-- Author: AI
-- Date: 2026-06-02
-- Task: P0-003-007-009-001-002
-- ============================================================

-- ============================================================
-- 一、inv_loss_detail 报损从表索引回滚
-- ============================================================

DROP INDEX IF EXISTS idx_inv_loss_detail_created_at;
DROP INDEX IF EXISTS idx_inv_loss_detail_owner;
DROP INDEX IF EXISTS idx_inv_loss_detail_owner_dept;
DROP INDEX IF EXISTS idx_inv_loss_detail_updated_by;
DROP INDEX IF EXISTS idx_inv_loss_detail_created_by;
DROP INDEX IF EXISTS idx_inv_loss_detail_tenant_product_name;
DROP INDEX IF EXISTS idx_inv_loss_detail_tenant_product_code;
DROP INDEX IF EXISTS idx_inv_loss_detail_batch_no;
DROP INDEX IF EXISTS idx_inv_loss_detail_line_no;
DROP INDEX IF EXISTS idx_inv_loss_detail_location;
DROP INDEX IF EXISTS idx_inv_loss_detail_warehouse;
DROP INDEX IF EXISTS idx_inv_loss_detail_product;
DROP INDEX IF EXISTS idx_inv_loss_detail_order;
DROP INDEX IF EXISTS idx_inv_loss_detail_tenant_deleted;
DROP INDEX IF EXISTS idx_inv_loss_detail_tenant_code;
DROP INDEX IF EXISTS uk_inv_loss_detail_code;

-- ============================================================
-- 二、inv_loss 报损主表索引回滚
-- ============================================================

DROP INDEX IF EXISTS idx_inv_loss_created_at;
DROP INDEX IF EXISTS idx_inv_loss_owner;
DROP INDEX IF EXISTS idx_inv_loss_owner_dept;
DROP INDEX IF EXISTS idx_inv_loss_updated_by;
DROP INDEX IF EXISTS idx_inv_loss_created_by;
DROP INDEX IF EXISTS idx_inv_loss_approver;
DROP INDEX IF EXISTS idx_inv_loss_handler;
DROP INDEX IF EXISTS idx_inv_loss_handling_dept;
DROP INDEX IF EXISTS idx_inv_loss_loss_date;
DROP INDEX IF EXISTS idx_inv_loss_order_date;
DROP INDEX IF EXISTS idx_inv_loss_status;
DROP INDEX IF EXISTS idx_inv_loss_warehouse;
DROP INDEX IF EXISTS idx_inv_loss_tenant_deleted;
DROP INDEX IF EXISTS idx_inv_loss_tenant_status;
DROP INDEX IF EXISTS idx_inv_loss_tenant_order_no;
DROP INDEX IF EXISTS uk_inv_loss_order_no;
