-- ============================================================
-- Flyway Migration Script
-- Version: V20260602005
-- Description: srm_supplier供应商主表索引与约束
-- Author: AI Generated
-- Date: 2026-06-02
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE srm_supplier RENAME CONSTRAINT srm_supplier_pkey TO pk_srm_supplier;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_srm_supplier_supplier_code ON srm_supplier(supplier_code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_srm_supplier_tenant_supplier_code ON srm_supplier(tenant_id, supplier_code);
CREATE INDEX idx_srm_supplier_tenant_status ON srm_supplier(tenant_id, status);
CREATE INDEX idx_srm_supplier_tenant_deleted ON srm_supplier(tenant_id, is_deleted);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
-- 外键关联字段索引
CREATE INDEX idx_srm_supplier_class_id ON srm_supplier(class_id);
CREATE INDEX idx_srm_supplier_created_by ON srm_supplier(created_by);
CREATE INDEX idx_srm_supplier_updated_by ON srm_supplier(updated_by);
CREATE INDEX idx_srm_supplier_owner_dept ON srm_supplier(owner_dept_id);
CREATE INDEX idx_srm_supplier_owner ON srm_supplier(owner_id);

-- 日期范围查询
CREATE INDEX idx_srm_supplier_tenant_created_at ON srm_supplier(tenant_id, created_at);
