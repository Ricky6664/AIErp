-- ============================================================
-- Flyway Migration Script
-- Version: V20260601078
-- Description: crm_customer 客户主表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE crm_customer RENAME CONSTRAINT crm_customer_pkey TO pk_crm_customer;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_crm_customer_code ON crm_customer(customer_code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_crm_customer_tenant_code ON crm_customer(tenant_id, customer_code);
CREATE INDEX idx_crm_customer_tenant_status ON crm_customer(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
-- 客户分类外键关联
CREATE INDEX idx_crm_customer_class_id ON crm_customer(class_id);

-- 通用字段索引
CREATE INDEX idx_crm_customer_created_by ON crm_customer(created_by);
CREATE INDEX idx_crm_customer_updated_by ON crm_customer(updated_by);
CREATE INDEX idx_crm_customer_owner_dept ON crm_customer(owner_dept_id);
CREATE INDEX idx_crm_customer_owner ON crm_customer(owner_id);

-- 日期范围查询（tenant_id首列）
CREATE INDEX idx_crm_customer_created_at ON crm_customer(tenant_id, created_at);
