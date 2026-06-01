-- ============================================================
-- Flyway Rollback Script
-- Version: V20260601004
-- Description: org_company公司表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS idx_org_company_created_at;
DROP INDEX IF EXISTS idx_org_company_owner;
DROP INDEX IF EXISTS idx_org_company_owner_dept;
DROP INDEX IF EXISTS idx_org_company_updated_by;
DROP INDEX IF EXISTS idx_org_company_created_by;
DROP INDEX IF EXISTS idx_org_company_status;
DROP INDEX IF EXISTS idx_org_company_tenant_deleted;
DROP INDEX IF EXISTS idx_org_company_tenant_status;
DROP INDEX IF EXISTS idx_org_company_tenant_code;
DROP INDEX IF EXISTS uk_org_company_code;
