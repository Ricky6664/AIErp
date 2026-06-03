-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260603002
-- Description: inv_assembly_detail组装主从表索引回滚脚本
-- Author: AI
-- Date: 2026-06-03
-- Task: P0-003-007-011-001-002
-- ============================================================

-- ============================================================
-- 一、inv_assembly_detail 组装从表索引回滚
-- ============================================================

DROP INDEX IF EXISTS idx_inv_assembly_detail_created_at;
DROP INDEX IF EXISTS idx_inv_assembly_detail_owner;
DROP INDEX IF EXISTS idx_inv_assembly_detail_owner_dept;
DROP INDEX IF EXISTS idx_inv_assembly_detail_updated_by;
DROP INDEX IF EXISTS idx_inv_assembly_detail_created_by;
DROP INDEX IF EXISTS idx_inv_assembly_detail_tenant_product_name;
DROP INDEX IF EXISTS idx_inv_assembly_detail_tenant_product_code;
DROP INDEX IF EXISTS idx_inv_assembly_detail_batch_no;
DROP INDEX IF EXISTS idx_inv_assembly_detail_line_no;
DROP INDEX IF EXISTS idx_inv_assembly_detail_location;
DROP INDEX IF EXISTS idx_inv_assembly_detail_warehouse;
DROP INDEX IF EXISTS idx_inv_assembly_detail_product;
DROP INDEX IF EXISTS idx_inv_assembly_detail_order;
DROP INDEX IF EXISTS idx_inv_assembly_detail_tenant_deleted;
DROP INDEX IF EXISTS idx_inv_assembly_detail_tenant_code;
DROP INDEX IF EXISTS uk_inv_assembly_detail_code;

-- ============================================================
-- 二、inv_assembly 组装主表索引回滚
-- ============================================================

DROP INDEX IF EXISTS idx_inv_assembly_created_at;
DROP INDEX IF EXISTS idx_inv_assembly_owner;
DROP INDEX IF EXISTS idx_inv_assembly_owner_dept;
DROP INDEX IF EXISTS idx_inv_assembly_updated_by;
DROP INDEX IF EXISTS idx_inv_assembly_created_by;
DROP INDEX IF EXISTS idx_inv_assembly_approver;
DROP INDEX IF EXISTS idx_inv_assembly_handler;
DROP INDEX IF EXISTS idx_inv_assembly_handling_dept;
DROP INDEX IF EXISTS idx_inv_assembly_assembly_date;
DROP INDEX IF EXISTS idx_inv_assembly_order_date;
DROP INDEX IF EXISTS idx_inv_assembly_status;
DROP INDEX IF EXISTS idx_inv_assembly_warehouse;
DROP INDEX IF EXISTS idx_inv_assembly_tenant_deleted;
DROP INDEX IF EXISTS idx_inv_assembly_tenant_status;
DROP INDEX IF EXISTS idx_inv_assembly_tenant_order_no;
DROP INDEX IF EXISTS uk_inv_assembly_order_no;
