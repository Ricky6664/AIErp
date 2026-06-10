-- ============================================================
-- Flyway Rollback Script
-- Version: V20260602011
-- Description: srm_supplier_address供应商地址表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-005-001-002
-- ============================================================

DROP INDEX IF EXISTS idx_srm_supplier_address_created_at;
DROP INDEX IF EXISTS idx_srm_supplier_address_owner;
DROP INDEX IF EXISTS idx_srm_supplier_address_owner_dept;
DROP INDEX IF EXISTS idx_srm_supplier_address_updated_by;
DROP INDEX IF EXISTS idx_srm_supplier_address_created_by;
DROP INDEX IF EXISTS idx_srm_supplier_address_status;
DROP INDEX IF EXISTS idx_srm_supplier_address_supplier;
DROP INDEX IF EXISTS idx_srm_supplier_address_tenant_status;
DROP INDEX IF EXISTS idx_srm_supplier_address_tenant_supplier;
DROP INDEX IF EXISTS uk_srm_supplier_address_supplier_type;
