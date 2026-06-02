-- ============================================================
-- Flyway Rollback Script
-- Version: V20260526001
-- Description: inv_warehouse仓库定义表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-007-001-001-002
-- ============================================================

DROP INDEX IF EXISTS idx_inv_warehouse_created_at;
DROP INDEX IF EXISTS idx_inv_warehouse_owner;
DROP INDEX IF EXISTS idx_inv_warehouse_owner_dept;
DROP INDEX IF EXISTS idx_inv_warehouse_updated_by;
DROP INDEX IF EXISTS idx_inv_warehouse_created_by;
DROP INDEX IF EXISTS idx_inv_warehouse_name;
DROP INDEX IF EXISTS idx_inv_warehouse_status;
DROP INDEX IF EXISTS idx_inv_warehouse_type;
DROP INDEX IF EXISTS idx_inv_warehouse_company;
DROP INDEX IF EXISTS idx_inv_warehouse_tenant_status;
DROP INDEX IF EXISTS idx_inv_warehouse_tenant_code;
DROP INDEX IF EXISTS uk_inv_warehouse_code;
