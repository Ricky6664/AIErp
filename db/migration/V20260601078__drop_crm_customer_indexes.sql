-- ============================================================
-- Flyway Rollback Script
-- Version: V20260601078
-- Description: crm_customer 客户主表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

DROP INDEX IF EXISTS idx_crm_customer_created_at;
DROP INDEX IF EXISTS idx_crm_customer_owner;
DROP INDEX IF EXISTS idx_crm_customer_owner_dept;
DROP INDEX IF EXISTS idx_crm_customer_updated_by;
DROP INDEX IF EXISTS idx_crm_customer_created_by;
DROP INDEX IF EXISTS idx_crm_customer_class_id;
DROP INDEX IF EXISTS idx_crm_customer_tenant_status;
DROP INDEX IF EXISTS idx_crm_customer_tenant_code;
DROP INDEX IF EXISTS uk_crm_customer_code;
