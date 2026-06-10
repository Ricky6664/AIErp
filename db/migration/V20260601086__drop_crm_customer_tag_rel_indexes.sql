-- ============================================================
-- Flyway Rollback Script
-- Version: V20260601086
-- Description: crm_customer_tag_rel 客户标签关联表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-006-001-002
-- ============================================================

-- 主键约束恢复默认名
ALTER TABLE crm_customer_tag_rel RENAME CONSTRAINT pk_crm_customer_tag_rel TO crm_customer_tag_rel_pkey;

-- 部分唯一索引
DROP INDEX IF EXISTS uk_crm_customer_tag_rel_code;

-- 多租户联合索引
DROP INDEX IF EXISTS idx_crm_customer_tag_rel_tenant_code;
DROP INDEX IF EXISTS idx_crm_customer_tag_rel_tenant_status;

-- 业务查询索引
DROP INDEX IF EXISTS idx_crm_customer_tag_rel_customer_id;
DROP INDEX IF EXISTS idx_crm_customer_tag_rel_tag_id;

-- 通用字段索引
DROP INDEX IF EXISTS idx_crm_customer_tag_rel_created_by;
DROP INDEX IF EXISTS idx_crm_customer_tag_rel_updated_by;
DROP INDEX IF EXISTS idx_crm_customer_tag_rel_owner_dept;
DROP INDEX IF EXISTS idx_crm_customer_tag_rel_owner;

-- 日期范围索引
DROP INDEX IF EXISTS idx_crm_customer_tag_rel_created_at;
