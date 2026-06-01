-- ============================================================
-- Flyway Rollback Script
-- Version: V20260601095
-- Description: crm_customer_finance 客户财务配置表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-009-001-002
-- ============================================================

-- 主键约束恢复默认名
ALTER TABLE crm_customer_finance RENAME CONSTRAINT pk_crm_customer_finance TO crm_customer_finance_pkey;

-- 部分唯一索引
DROP INDEX IF EXISTS uk_crm_customer_finance_code;

-- 多租户联合索引
DROP INDEX IF EXISTS idx_crm_customer_finance_tenant_code;
DROP INDEX IF EXISTS idx_crm_customer_finance_tenant_status;

-- 业务查询索引
DROP INDEX IF EXISTS idx_crm_customer_finance_customer_id;
DROP INDEX IF EXISTS idx_crm_customer_finance_product_id;
DROP INDEX IF EXISTS idx_crm_customer_finance_order_date;
DROP INDEX IF EXISTS idx_crm_customer_finance_created_by;
DROP INDEX IF EXISTS idx_crm_customer_finance_updated_by;
DROP INDEX IF EXISTS idx_crm_customer_finance_owner_dept;
DROP INDEX IF EXISTS idx_crm_customer_finance_owner;
DROP INDEX IF EXISTS idx_crm_customer_finance_created_at;
