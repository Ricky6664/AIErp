-- ============================================================
-- Flyway Migration Script
-- Version: V20260601073
-- Description: crm_customer_class客户分类表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE crm_customer_class RENAME CONSTRAINT crm_customer_class_pkey TO pk_crm_customer_class;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_crm_customer_class_class_code ON crm_customer_class(class_code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_crm_customer_class_tenant_class_code ON crm_customer_class(tenant_id, class_code);
CREATE INDEX idx_crm_customer_class_tenant_deleted ON crm_customer_class(tenant_id, is_deleted);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
-- 树形结构查询
CREATE INDEX idx_crm_customer_class_parent_id ON crm_customer_class(parent_id);

-- 外键关联字段索引
CREATE INDEX idx_crm_customer_class_created_by ON crm_customer_class(created_by);
CREATE INDEX idx_crm_customer_class_updated_by ON crm_customer_class(updated_by);
CREATE INDEX idx_crm_customer_class_owner_dept ON crm_customer_class(owner_dept_id);
CREATE INDEX idx_crm_customer_class_owner ON crm_customer_class(owner_id);

-- 日期范围查询
CREATE INDEX idx_crm_customer_class_created_at ON crm_customer_class(tenant_id, created_at);
