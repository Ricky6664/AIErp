-- ============================================================
-- Flyway Rollback Script
-- Version: V20260601006
-- Description: org_department部门表索引回滚
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_org_department_code;
DROP INDEX IF EXISTS idx_org_department_tenant_code;
DROP INDEX IF EXISTS idx_org_department_tenant_status;
DROP INDEX IF EXISTS idx_org_department_tenant_deleted;
DROP INDEX IF EXISTS idx_org_department_parent_id;
DROP INDEX IF EXISTS idx_org_department_status;
DROP INDEX IF EXISTS idx_org_department_leader_id;
DROP INDEX IF EXISTS idx_org_department_created_by;
DROP INDEX IF EXISTS idx_org_department_updated_by;
DROP INDEX IF EXISTS idx_org_department_owner_dept;
DROP INDEX IF EXISTS idx_org_department_owner;
DROP INDEX IF EXISTS idx_org_department_created_at;
