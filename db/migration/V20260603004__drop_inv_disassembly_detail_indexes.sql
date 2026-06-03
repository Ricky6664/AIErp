-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260603004
-- Description: inv_disassembly_detail拆卸主从表索引回滚脚本
-- Author: AI
-- Date: 2026-06-03
-- Task: P0-003-007-012-001-002
-- ============================================================

-- ============================================================
-- 一、inv_disassembly_detail 拆卸从表索引回滚
-- ============================================================

DROP INDEX IF EXISTS idx_inv_disassembly_detail_created_at;
DROP INDEX IF EXISTS idx_inv_disassembly_detail_owner;
DROP INDEX IF EXISTS idx_inv_disassembly_detail_owner_dept;
DROP INDEX IF EXISTS idx_inv_disassembly_detail_updated_by;
DROP INDEX IF EXISTS idx_inv_disassembly_detail_created_by;
DROP INDEX IF EXISTS idx_inv_disassembly_detail_tenant_product_name;
DROP INDEX IF EXISTS idx_inv_disassembly_detail_tenant_product_code;
DROP INDEX IF EXISTS idx_inv_disassembly_detail_batch_no;
DROP INDEX IF EXISTS idx_inv_disassembly_detail_line_no;
DROP INDEX IF EXISTS idx_inv_disassembly_detail_location;
DROP INDEX IF EXISTS idx_inv_disassembly_detail_warehouse;
DROP INDEX IF EXISTS idx_inv_disassembly_detail_product;
DROP INDEX IF EXISTS idx_inv_disassembly_detail_order;
DROP INDEX IF EXISTS idx_inv_disassembly_detail_tenant_deleted;
DROP INDEX IF EXISTS idx_inv_disassembly_detail_tenant_code;
DROP INDEX IF EXISTS uk_inv_disassembly_detail_code;

-- ============================================================
-- 二、inv_disassembly 拆卸主表索引回滚
-- ============================================================

DROP INDEX IF EXISTS idx_inv_disassembly_created_at;
DROP INDEX IF EXISTS idx_inv_disassembly_owner;
DROP INDEX IF EXISTS idx_inv_disassembly_owner_dept;
DROP INDEX IF EXISTS idx_inv_disassembly_updated_by;
DROP INDEX IF EXISTS idx_inv_disassembly_created_by;
DROP INDEX IF EXISTS idx_inv_disassembly_approver;
DROP INDEX IF EXISTS idx_inv_disassembly_handler;
DROP INDEX IF EXISTS idx_inv_disassembly_handling_dept;
DROP INDEX IF EXISTS idx_inv_disassembly_disassembly_date;
DROP INDEX IF EXISTS idx_inv_disassembly_order_date;
DROP INDEX IF EXISTS idx_inv_disassembly_status;
DROP INDEX IF EXISTS idx_inv_disassembly_warehouse;
DROP INDEX IF EXISTS idx_inv_disassembly_tenant_deleted;
DROP INDEX IF EXISTS idx_inv_disassembly_tenant_status;
DROP INDEX IF EXISTS idx_inv_disassembly_tenant_order_no;
DROP INDEX IF EXISTS uk_inv_disassembly_order_no;
