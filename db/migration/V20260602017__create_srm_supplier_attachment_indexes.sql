-- ============================================================
-- Flyway Migration Script
-- Version: V20260602017
-- Description: srm_supplier_attachment供应商附件表索引与约束
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-007-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
-- ============================================================
ALTER TABLE srm_supplier_attachment RENAME CONSTRAINT srm_supplier_attachment_pkey TO pk_srm_supplier_attachment;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_srm_supplier_attachment_code ON srm_supplier_attachment(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_srm_supplier_attachment_tenant_code ON srm_supplier_attachment(tenant_id, code);
CREATE INDEX idx_srm_supplier_attachment_tenant_status ON srm_supplier_attachment(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================

-- 外键关联字段索引
CREATE INDEX idx_srm_supplier_attachment_supplier ON srm_supplier_attachment(supplier_id);

-- 附件类型筛选
CREATE INDEX idx_srm_supplier_attachment_type ON srm_supplier_attachment(attachment_type);

-- 状态筛选
CREATE INDEX idx_srm_supplier_attachment_status ON srm_supplier_attachment(status);

-- 通用字段索引
CREATE INDEX idx_srm_supplier_attachment_created_by ON srm_supplier_attachment(created_by);
CREATE INDEX idx_srm_supplier_attachment_updated_by ON srm_supplier_attachment(updated_by);
CREATE INDEX idx_srm_supplier_attachment_owner_dept ON srm_supplier_attachment(owner_dept_id);
CREATE INDEX idx_srm_supplier_attachment_owner ON srm_supplier_attachment(owner_id);

-- 日期范围查询
CREATE INDEX idx_srm_supplier_attachment_created_at ON srm_supplier_attachment(tenant_id, created_at);
