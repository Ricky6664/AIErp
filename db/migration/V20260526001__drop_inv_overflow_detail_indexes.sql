-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260526001
-- Description: inv_overflow_detail报溢主从表索引回滚脚本
-- Author: AI
-- Date: 2026-06-03
-- Task: P0-003-007-010-001-002
-- ============================================================

-- ============================================================
-- 一、inv_overflow_detail 报溢从表索引回滚
-- ============================================================

DROP INDEX IF EXISTS idx_inv_overflow_detail_created_at;
DROP INDEX IF EXISTS idx_inv_overflow_detail_owner;
DROP INDEX IF EXISTS idx_inv_overflow_detail_owner_dept;
DROP INDEX IF EXISTS idx_inv_overflow_detail_updated_by;
DROP INDEX IF EXISTS idx_inv_overflow_detail_created_by;
DROP INDEX IF EXISTS idx_inv_overflow_detail_tenant_product_name;
DROP INDEX IF EXISTS idx_inv_overflow_detail_tenant_product_code;
DROP INDEX IF EXISTS idx_inv_overflow_detail_batch_no;
DROP INDEX IF EXISTS idx_inv_overflow_detail_line_no;
DROP INDEX IF EXISTS idx_inv_overflow_detail_location;
DROP INDEX IF EXISTS idx_inv_overflow_detail_warehouse;
DROP INDEX IF EXISTS idx_inv_overflow_detail_product;
DROP INDEX IF EXISTS idx_inv_overflow_detail_order;
DROP INDEX IF EXISTS idx_inv_overflow_detail_tenant_deleted;
DROP INDEX IF EXISTS idx_inv_overflow_detail_tenant_code;
DROP INDEX IF EXISTS uk_inv_overflow_detail_code;

-- ============================================================
-- 二、inv_overflow 报溢主表索引回滚
-- ============================================================

DROP INDEX IF EXISTS idx_inv_overflow_created_at;
DROP INDEX IF EXISTS idx_inv_overflow_owner;
DROP INDEX IF EXISTS idx_inv_overflow_owner_dept;
DROP INDEX IF EXISTS idx_inv_overflow_updated_by;
DROP INDEX IF EXISTS idx_inv_overflow_created_by;
DROP INDEX IF EXISTS idx_inv_overflow_approver;
DROP INDEX IF EXISTS idx_inv_overflow_handler;
DROP INDEX IF EXISTS idx_inv_overflow_handling_dept;
DROP INDEX IF EXISTS idx_inv_overflow_overflow_date;
DROP INDEX IF EXISTS idx_inv_overflow_order_date;
DROP INDEX IF EXISTS idx_inv_overflow_status;
DROP INDEX IF EXISTS idx_inv_overflow_warehouse;
DROP INDEX IF EXISTS idx_inv_overflow_tenant_deleted;
DROP INDEX IF EXISTS idx_inv_overflow_tenant_status;
DROP INDEX IF EXISTS idx_inv_overflow_tenant_order_no;
DROP INDEX IF EXISTS uk_inv_overflow_order_no;
