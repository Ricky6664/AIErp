-- ============================================================
-- Flyway Rollback Script
-- Version: V20260602017
-- Description: srm_supplier_attachment供应商附件表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-007-001-002
-- ============================================================

DROP INDEX IF EXISTS idx_srm_supplier_attachment_created_at;
DROP INDEX IF EXISTS idx_srm_supplier_attachment_owner;
DROP INDEX IF EXISTS idx_srm_supplier_attachment_owner_dept;
DROP INDEX IF EXISTS idx_srm_supplier_attachment_updated_by;
DROP INDEX IF EXISTS idx_srm_supplier_attachment_created_by;
DROP INDEX IF EXISTS idx_srm_supplier_attachment_status;
DROP INDEX IF EXISTS idx_srm_supplier_attachment_type;
DROP INDEX IF EXISTS idx_srm_supplier_attachment_supplier;
DROP INDEX IF EXISTS idx_srm_supplier_attachment_tenant_status;
DROP INDEX IF EXISTS idx_srm_supplier_attachment_tenant_code;
DROP INDEX IF EXISTS uk_srm_supplier_attachment_code;
