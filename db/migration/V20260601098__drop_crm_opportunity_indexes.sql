-- ============================================================
-- Flyway Rollback Script
-- Version: V20260601098
-- Description: crm_opportunity 客户机会表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-010-001-002
-- ============================================================

DROP INDEX IF EXISTS idx_crm_opportunity_tenant_order;
DROP INDEX IF EXISTS idx_crm_opportunity_created_at;
DROP INDEX IF EXISTS idx_crm_opportunity_close_date;
DROP INDEX IF EXISTS idx_crm_opportunity_stage;
DROP INDEX IF EXISTS idx_crm_opportunity_status;
DROP INDEX IF EXISTS idx_crm_opportunity_owner;
DROP INDEX IF EXISTS idx_crm_opportunity_owner_dept;
DROP INDEX IF EXISTS idx_crm_opportunity_updated_by;
DROP INDEX IF EXISTS idx_crm_opportunity_created_by;
DROP INDEX IF EXISTS idx_crm_opportunity_sales_person;
DROP INDEX IF EXISTS idx_crm_opportunity_customer_id;
DROP INDEX IF EXISTS idx_crm_opportunity_tenant_sales;
DROP INDEX IF EXISTS idx_crm_opportunity_tenant_stage;
DROP INDEX IF EXISTS idx_crm_opportunity_tenant_customer;
DROP INDEX IF EXISTS idx_crm_opportunity_tenant_deleted;
DROP INDEX IF EXISTS idx_crm_opportunity_tenant_status;
DROP INDEX IF EXISTS idx_crm_opportunity_tenant_code;
DROP INDEX IF EXISTS uk_crm_opportunity_code;
