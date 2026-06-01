-- ============================================================
-- Flyway Rollback Script
-- Version: V20260601092
-- Description: crm_customer_evaluation 客户评价表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-008-001-002
-- ============================================================

-- 主键约束恢复默认名
ALTER TABLE crm_customer_evaluation RENAME CONSTRAINT pk_crm_customer_evaluation TO crm_customer_evaluation_pkey;

-- 部分唯一索引
DROP INDEX IF EXISTS uk_crm_customer_evaluation_code;

-- 多租户联合索引
DROP INDEX IF EXISTS idx_crm_customer_evaluation_tenant_code;
DROP INDEX IF EXISTS idx_crm_customer_evaluation_tenant_status;

-- 业务查询索引
DROP INDEX IF EXISTS idx_crm_customer_evaluation_customer_id;
DROP INDEX IF EXISTS idx_crm_customer_evaluation_parent_id;
DROP INDEX IF EXISTS idx_crm_customer_evaluation_evaluator_id;
DROP INDEX IF EXISTS idx_crm_customer_evaluation_evaluation_date;
DROP INDEX IF EXISTS idx_crm_customer_evaluation_created_by;
DROP INDEX IF EXISTS idx_crm_customer_evaluation_updated_by;
DROP INDEX IF EXISTS idx_crm_customer_evaluation_owner_dept;
DROP INDEX IF EXISTS idx_crm_customer_evaluation_owner;
DROP INDEX IF EXISTS idx_crm_customer_evaluation_created_at;
