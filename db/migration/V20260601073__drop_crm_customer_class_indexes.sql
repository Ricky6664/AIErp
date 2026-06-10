-- ============================================================
-- Flyway Rollback Script
-- Version: V20260601073
-- Description: crm_customer_class客户分类表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS idx_crm_customer_class_created_at;
DROP INDEX IF EXISTS idx_crm_customer_class_owner;
DROP INDEX IF EXISTS idx_crm_customer_class_owner_dept;
DROP INDEX IF EXISTS idx_crm_customer_class_updated_by;
DROP INDEX IF EXISTS idx_crm_customer_class_created_by;
DROP INDEX IF EXISTS idx_crm_customer_class_parent_id;
DROP INDEX IF EXISTS idx_crm_customer_class_tenant_deleted;
DROP INDEX IF EXISTS idx_crm_customer_class_tenant_class_code;
DROP INDEX IF EXISTS uk_crm_customer_class_class_code;
