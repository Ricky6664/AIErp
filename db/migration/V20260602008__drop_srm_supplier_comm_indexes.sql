-- ============================================================
-- Flyway Rollback Script
-- Version: V20260602008
-- Description: srm_supplier_comm供应商联系人表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-004-001-002
-- ============================================================

DROP INDEX IF EXISTS idx_srm_supplier_comm_created_at;
DROP INDEX IF EXISTS idx_srm_supplier_comm_owner;
DROP INDEX IF EXISTS idx_srm_supplier_comm_owner_dept;
DROP INDEX IF EXISTS idx_srm_supplier_comm_updated_by;
DROP INDEX IF EXISTS idx_srm_supplier_comm_created_by;
DROP INDEX IF EXISTS idx_srm_supplier_comm_parent;
DROP INDEX IF EXISTS idx_srm_supplier_comm_status;
DROP INDEX IF EXISTS idx_srm_supplier_comm_supplier;
DROP INDEX IF EXISTS idx_srm_supplier_comm_tenant_status;
DROP INDEX IF EXISTS idx_srm_supplier_comm_tenant_code;
DROP INDEX IF EXISTS uk_srm_supplier_comm_code;
