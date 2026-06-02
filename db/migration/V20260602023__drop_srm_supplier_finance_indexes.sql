-- ============================================================
-- Flyway Rollback Script
-- Version: V20260602023
-- Description: srm_supplier_finance供应商财务配置表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-009-001-002
-- ============================================================

DROP INDEX IF EXISTS idx_srm_supplier_finance_created_at;
DROP INDEX IF EXISTS idx_srm_supplier_finance_owner;
DROP INDEX IF EXISTS idx_srm_supplier_finance_owner_dept;
DROP INDEX IF EXISTS idx_srm_supplier_finance_updated_by;
DROP INDEX IF EXISTS idx_srm_supplier_finance_created_by;
DROP INDEX IF EXISTS idx_srm_supplier_finance_order_date;
DROP INDEX IF EXISTS idx_srm_supplier_finance_status;
DROP INDEX IF EXISTS idx_srm_supplier_finance_tax_no;
DROP INDEX IF EXISTS idx_srm_supplier_finance_supplier;
DROP INDEX IF EXISTS idx_srm_supplier_finance_tenant_status;
DROP INDEX IF EXISTS idx_srm_supplier_finance_tenant_code;
DROP INDEX IF EXISTS uk_srm_supplier_finance_code;
