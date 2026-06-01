-- ============================================================
-- Flyway Migration Script
-- Version: V20260601086
-- Description: crm_customer_tag_rel 客户标签关联表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-006-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE crm_customer_tag_rel RENAME CONSTRAINT crm_customer_tag_rel_pkey TO pk_crm_customer_tag_rel;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_crm_customer_tag_rel_code ON crm_customer_tag_rel(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_crm_customer_tag_rel_tenant_code ON crm_customer_tag_rel(tenant_id, code);
CREATE INDEX idx_crm_customer_tag_rel_tenant_status ON crm_customer_tag_rel(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
-- 客户外键关联
CREATE INDEX idx_crm_customer_tag_rel_customer_id ON crm_customer_tag_rel(customer_id);

-- 标签外键关联
CREATE INDEX idx_crm_customer_tag_rel_tag_id ON crm_customer_tag_rel(tag_id);

-- 通用字段索引
CREATE INDEX idx_crm_customer_tag_rel_created_by ON crm_customer_tag_rel(created_by);
CREATE INDEX idx_crm_customer_tag_rel_updated_by ON crm_customer_tag_rel(updated_by);
CREATE INDEX idx_crm_customer_tag_rel_owner_dept ON crm_customer_tag_rel(owner_dept_id);
CREATE INDEX idx_crm_customer_tag_rel_owner ON crm_customer_tag_rel(owner_id);

-- 日期范围查询（tenant_id首列）
CREATE INDEX idx_crm_customer_tag_rel_created_at ON crm_customer_tag_rel(tenant_id, created_at);
