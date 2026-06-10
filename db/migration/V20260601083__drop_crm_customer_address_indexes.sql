-- ============================================================
-- Flyway Rollback Script
-- Version: V20260601083
-- Description: crm_customer_address 客户地址表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-005-001-002
-- ============================================================

-- 主键约束恢复默认名
ALTER TABLE crm_customer_address RENAME CONSTRAINT pk_crm_customer_address TO crm_customer_address_pkey;

-- 多租户联合索引
DROP INDEX IF EXISTS idx_crm_customer_address_tenant_customer;
DROP INDEX IF EXISTS idx_crm_customer_address_tenant_status;

-- 业务查询索引
DROP INDEX IF EXISTS idx_crm_customer_address_customer_id;
DROP INDEX IF EXISTS idx_crm_customer_address_address_type;
DROP INDEX IF EXISTS idx_crm_customer_address_city;

-- 通用字段索引
DROP INDEX IF EXISTS idx_crm_customer_address_created_by;
DROP INDEX IF EXISTS idx_crm_customer_address_updated_by;
DROP INDEX IF EXISTS idx_crm_customer_address_owner_dept;
DROP INDEX IF EXISTS idx_crm_customer_address_owner;

-- 日期范围索引
DROP INDEX IF EXISTS idx_crm_customer_address_created_at;
