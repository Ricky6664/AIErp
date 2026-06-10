-- ============================================================
-- Flyway Rollback Script
-- Version: V20260602001
-- Description: inv_location库位管理表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-02
-- ============================================================

DROP INDEX IF EXISTS idx_inv_location_created_at;
DROP INDEX IF EXISTS idx_inv_location_owner;
DROP INDEX IF EXISTS idx_inv_location_owner_dept;
DROP INDEX IF EXISTS idx_inv_location_updated_by;
DROP INDEX IF EXISTS idx_inv_location_created_by;
DROP INDEX IF EXISTS idx_inv_location_tenant_zone;
DROP INDEX IF EXISTS idx_inv_location_status;
DROP INDEX IF EXISTS idx_inv_location_warehouse;
DROP INDEX IF EXISTS idx_inv_location_tenant_deleted;
DROP INDEX IF EXISTS idx_inv_location_tenant_status;
DROP INDEX IF EXISTS idx_inv_location_tenant_code;
DROP INDEX IF EXISTS uk_inv_location_code;
