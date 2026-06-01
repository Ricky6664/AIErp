-- ============================================================
-- Flyway Rollback Script
-- Version: V20260601089
-- Description: crm_customer_attachment 客户附件表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-007-001-002
-- ============================================================

-- 主键约束恢复默认名
ALTER TABLE crm_customer_attachment RENAME CONSTRAINT pk_crm_customer_attachment TO crm_customer_attachment_pkey;

-- 部分唯一索引
DROP INDEX IF EXISTS uk_crm_customer_attachment_code;

-- 多租户联合索引
DROP INDEX IF EXISTS idx_crm_customer_attachment_tenant_code;
DROP INDEX IF EXISTS idx_crm_customer_attachment_tenant_status;

-- 业务查询索引
DROP INDEX IF EXISTS idx_crm_customer_attachment_customer_id;
DROP INDEX IF EXISTS idx_crm_customer_attachment_created_by;
DROP INDEX IF EXISTS idx_crm_customer_attachment_updated_by;
DROP INDEX IF EXISTS idx_crm_customer_attachment_owner_dept;
DROP INDEX IF EXISTS idx_crm_customer_attachment_owner;
DROP INDEX IF EXISTS idx_crm_customer_attachment_created_at;
