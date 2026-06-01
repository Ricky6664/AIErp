-- ============================================================
-- Flyway Migration Script
-- Version: V20260601089
-- Description: crm_customer_attachment 客户附件表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-007-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE crm_customer_attachment RENAME CONSTRAINT crm_customer_attachment_pkey TO pk_crm_customer_attachment;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_crm_customer_attachment_code ON crm_customer_attachment(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_crm_customer_attachment_tenant_code ON crm_customer_attachment(tenant_id, code);
CREATE INDEX idx_crm_customer_attachment_tenant_status ON crm_customer_attachment(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
-- 客户外键关联
CREATE INDEX idx_crm_customer_attachment_customer_id ON crm_customer_attachment(customer_id);

-- 通用字段索引
CREATE INDEX idx_crm_customer_attachment_created_by ON crm_customer_attachment(created_by);
CREATE INDEX idx_crm_customer_attachment_updated_by ON crm_customer_attachment(updated_by);
CREATE INDEX idx_crm_customer_attachment_owner_dept ON crm_customer_attachment(owner_dept_id);
CREATE INDEX idx_crm_customer_attachment_owner ON crm_customer_attachment(owner_id);

-- 日期范围查询（tenant_id首列）
CREATE INDEX idx_crm_customer_attachment_created_at ON crm_customer_attachment(tenant_id, created_at);
