-- ============================================================
-- Flyway Rollback Script
-- Version: V20260601076
-- Description: crm_tag_definition CRM标签定义表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS idx_crm_tag_definition_created_at;
DROP INDEX IF EXISTS idx_crm_tag_definition_owner;
DROP INDEX IF EXISTS idx_crm_tag_definition_owner_dept;
DROP INDEX IF EXISTS idx_crm_tag_definition_updated_by;
DROP INDEX IF EXISTS idx_crm_tag_definition_created_by;
DROP INDEX IF EXISTS idx_crm_tag_definition_sort_no;
DROP INDEX IF EXISTS idx_crm_tag_definition_enable_flag;
DROP INDEX IF EXISTS idx_crm_tag_definition_tag_group;
DROP INDEX IF EXISTS idx_crm_tag_definition_tenant_deleted;
DROP INDEX IF EXISTS idx_crm_tag_definition_tenant_tag_name;
DROP INDEX IF EXISTS uk_crm_tag_definition_tag_name;
