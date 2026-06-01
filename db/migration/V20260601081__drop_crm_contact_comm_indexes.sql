-- ============================================================
-- Flyway Rollback Script
-- Version: V20260601081
-- Description: crm_contact_comm 联系人通讯表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-004-001-002
-- ============================================================

DROP INDEX IF EXISTS idx_crm_contact_comm_created_at;
DROP INDEX IF EXISTS idx_crm_contact_comm_owner;
DROP INDEX IF EXISTS idx_crm_contact_comm_owner_dept;
DROP INDEX IF EXISTS idx_crm_contact_comm_updated_by;
DROP INDEX IF EXISTS idx_crm_contact_comm_created_by;
DROP INDEX IF EXISTS idx_crm_contact_comm_comm_type;
DROP INDEX IF EXISTS idx_crm_contact_comm_parent_id;
DROP INDEX IF EXISTS idx_crm_contact_comm_contact_id;
DROP INDEX IF EXISTS idx_crm_contact_comm_tenant_status;
DROP INDEX IF EXISTS idx_crm_contact_comm_tenant_code;
DROP INDEX IF EXISTS uk_crm_contact_comm_code;
