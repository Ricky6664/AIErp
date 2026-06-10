-- ============================================================
-- Flyway Rollback Script
-- Version: V20260601101
-- Description: crm_project 客户项目表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-011-001-002
-- ============================================================

DROP INDEX IF EXISTS idx_crm_project_tenant_order;
DROP INDEX IF EXISTS idx_crm_project_created_at;
DROP INDEX IF EXISTS idx_crm_project_end_date;
DROP INDEX IF EXISTS idx_crm_project_start_date;
DROP INDEX IF EXISTS idx_crm_project_stage;
DROP INDEX IF EXISTS idx_crm_project_status;
DROP INDEX IF EXISTS idx_crm_project_owner;
DROP INDEX IF EXISTS idx_crm_project_owner_dept;
DROP INDEX IF EXISTS idx_crm_project_updated_by;
DROP INDEX IF EXISTS idx_crm_project_created_by;
DROP INDEX IF EXISTS idx_crm_project_project_manager;
DROP INDEX IF EXISTS idx_crm_project_customer_id;
DROP INDEX IF EXISTS idx_crm_project_tenant_manager;
DROP INDEX IF EXISTS idx_crm_project_tenant_stage;
DROP INDEX IF EXISTS idx_crm_project_tenant_customer;
DROP INDEX IF EXISTS idx_crm_project_tenant_deleted;
DROP INDEX IF EXISTS idx_crm_project_tenant_status;
DROP INDEX IF EXISTS idx_crm_project_tenant_code;
DROP INDEX IF EXISTS uk_crm_project_code;
