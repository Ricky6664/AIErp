-- ============================================================
-- Flyway Rollback Script
-- Version: V20260602014
-- Description: srm_supplier_tag_rel供应商标签关联表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-006-001-002
-- ============================================================

DROP INDEX IF EXISTS idx_srm_supplier_tag_rel_created_at;
DROP INDEX IF EXISTS idx_srm_supplier_tag_rel_owner;
DROP INDEX IF EXISTS idx_srm_supplier_tag_rel_owner_dept;
DROP INDEX IF EXISTS idx_srm_supplier_tag_rel_updated_by;
DROP INDEX IF EXISTS idx_srm_supplier_tag_rel_created_by;
DROP INDEX IF EXISTS idx_srm_supplier_tag_rel_status;
DROP INDEX IF EXISTS idx_srm_supplier_tag_rel_tag;
DROP INDEX IF EXISTS idx_srm_supplier_tag_rel_supplier;
DROP INDEX IF EXISTS idx_srm_supplier_tag_rel_tenant_status;
DROP INDEX IF EXISTS idx_srm_supplier_tag_rel_tenant_code;
DROP INDEX IF EXISTS uk_srm_supplier_tag_rel_code;
