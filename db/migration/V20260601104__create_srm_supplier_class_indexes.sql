-- ============================================================
-- Flyway Migration Script
-- Version: V20260601104
-- Description: srm_supplier_class供应商分类表索引与约束
-- Author: AI Generated
-- Date: 2026-06-02
-- ============================================================

-- ============================================================
-- 1. 补充业务字段（status列——任务规格5.3要求创建tenant_status联合索引）
-- ============================================================
ALTER TABLE srm_supplier_class ADD COLUMN IF NOT EXISTS status VARCHAR(30) DEFAULT 'active';
COMMENT ON COLUMN srm_supplier_class.status IS '状态：active=启用/inactive=停用';

-- ============================================================
-- 2. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE srm_supplier_class RENAME CONSTRAINT srm_supplier_class_pkey TO pk_srm_supplier_class;

-- ============================================================
-- 3. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_srm_supplier_class_class_code ON srm_supplier_class(class_code) WHERE is_deleted = false;

-- ============================================================
-- 4. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_srm_supplier_class_tenant_class_code ON srm_supplier_class(tenant_id, class_code);
CREATE INDEX idx_srm_supplier_class_tenant_status ON srm_supplier_class(tenant_id, status);
CREATE INDEX idx_srm_supplier_class_tenant_deleted ON srm_supplier_class(tenant_id, is_deleted);

-- ============================================================
-- 5. 业务查询索引
-- ============================================================
-- 树形结构查询
CREATE INDEX idx_srm_supplier_class_parent_id ON srm_supplier_class(parent_id);

-- 外键关联字段索引
CREATE INDEX idx_srm_supplier_class_created_by ON srm_supplier_class(created_by);
CREATE INDEX idx_srm_supplier_class_updated_by ON srm_supplier_class(updated_by);
CREATE INDEX idx_srm_supplier_class_owner_dept ON srm_supplier_class(owner_dept_id);
CREATE INDEX idx_srm_supplier_class_owner ON srm_supplier_class(owner_id);

-- 日期范围查询
CREATE INDEX idx_srm_supplier_class_created_at ON srm_supplier_class(tenant_id, created_at);
