-- ============================================================
-- Flyway Rollback Script
-- Version: V20260601008
-- Description: org_position岗位表索引回滚
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS uk_org_position_code;
DROP INDEX IF EXISTS idx_org_position_tenant_code;
DROP INDEX IF EXISTS idx_org_position_tenant_status;
DROP INDEX IF EXISTS idx_org_position_tenant_deleted;
DROP INDEX IF EXISTS idx_org_position_dept_id;
DROP INDEX IF EXISTS idx_org_position_sort_order;
DROP INDEX IF EXISTS idx_org_position_status;
DROP INDEX IF EXISTS idx_org_position_created_by;
DROP INDEX IF EXISTS idx_org_position_updated_by;
DROP INDEX IF EXISTS idx_org_position_owner_dept;
DROP INDEX IF EXISTS idx_org_position_owner;
DROP INDEX IF EXISTS idx_org_position_created_at;
