-- ============================================================
-- Flyway Migration Script
-- Version: V20260602014
-- Description: srm_supplier_tag_rel供应商标签关联表索引与约束
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-006-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
-- ============================================================
ALTER TABLE srm_supplier_tag_rel RENAME CONSTRAINT srm_supplier_tag_rel_pkey TO pk_srm_supplier_tag_rel;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_srm_supplier_tag_rel_code ON srm_supplier_tag_rel(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_srm_supplier_tag_rel_tenant_code ON srm_supplier_tag_rel(tenant_id, code);
CREATE INDEX idx_srm_supplier_tag_rel_tenant_status ON srm_supplier_tag_rel(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================

-- 外键关联字段索引
CREATE INDEX idx_srm_supplier_tag_rel_supplier ON srm_supplier_tag_rel(supplier_id);
CREATE INDEX idx_srm_supplier_tag_rel_tag ON srm_supplier_tag_rel(tag_id);

-- 状态筛选
CREATE INDEX idx_srm_supplier_tag_rel_status ON srm_supplier_tag_rel(status);

-- 通用字段索引
CREATE INDEX idx_srm_supplier_tag_rel_created_by ON srm_supplier_tag_rel(created_by);
CREATE INDEX idx_srm_supplier_tag_rel_updated_by ON srm_supplier_tag_rel(updated_by);
CREATE INDEX idx_srm_supplier_tag_rel_owner_dept ON srm_supplier_tag_rel(owner_dept_id);
CREATE INDEX idx_srm_supplier_tag_rel_owner ON srm_supplier_tag_rel(owner_id);

-- 日期范围查询
CREATE INDEX idx_srm_supplier_tag_rel_created_at ON srm_supplier_tag_rel(tenant_id, created_at);
