-- ============================================================
-- Flyway Rollback Script
-- Version: V20260602020
-- Description: srm_supplier_evaluation供应商评价表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-008-001-002
-- ============================================================

DROP INDEX IF EXISTS idx_srm_supplier_evaluation_created_at;
DROP INDEX IF EXISTS idx_srm_supplier_evaluation_owner;
DROP INDEX IF EXISTS idx_srm_supplier_evaluation_owner_dept;
DROP INDEX IF EXISTS idx_srm_supplier_evaluation_updated_by;
DROP INDEX IF EXISTS idx_srm_supplier_evaluation_created_by;
DROP INDEX IF EXISTS idx_srm_supplier_evaluation_parent;
DROP INDEX IF EXISTS idx_srm_supplier_evaluation_status;
DROP INDEX IF EXISTS idx_srm_supplier_evaluation_date;
DROP INDEX IF EXISTS idx_srm_supplier_evaluation_evaluator;
DROP INDEX IF EXISTS idx_srm_supplier_evaluation_type;
DROP INDEX IF EXISTS idx_srm_supplier_evaluation_supplier;
DROP INDEX IF EXISTS idx_srm_supplier_evaluation_tenant_status;
DROP INDEX IF EXISTS idx_srm_supplier_evaluation_tenant_code;
DROP INDEX IF EXISTS uk_srm_supplier_evaluation_code;
