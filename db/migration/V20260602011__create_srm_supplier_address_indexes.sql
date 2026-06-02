-- ============================================================
-- Flyway Migration Script
-- Version: V20260602011
-- Description: srm_supplier_address供应商地址表索引与约束
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-005-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE srm_supplier_address RENAME CONSTRAINT srm_supplier_address_pkey TO pk_srm_supplier_address;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_srm_supplier_address_supplier_type ON srm_supplier_address(supplier_id, address_type) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_srm_supplier_address_tenant_supplier ON srm_supplier_address(tenant_id, supplier_id);
CREATE INDEX idx_srm_supplier_address_tenant_status ON srm_supplier_address(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================

-- 外键关联字段索引
CREATE INDEX idx_srm_supplier_address_supplier ON srm_supplier_address(supplier_id);

-- 状态筛选
CREATE INDEX idx_srm_supplier_address_status ON srm_supplier_address(status);

-- 通用字段索引
CREATE INDEX idx_srm_supplier_address_created_by ON srm_supplier_address(created_by);
CREATE INDEX idx_srm_supplier_address_updated_by ON srm_supplier_address(updated_by);
CREATE INDEX idx_srm_supplier_address_owner_dept ON srm_supplier_address(owner_dept_id);
CREATE INDEX idx_srm_supplier_address_owner ON srm_supplier_address(owner_id);

-- 日期范围查询
CREATE INDEX idx_srm_supplier_address_created_at ON srm_supplier_address(tenant_id, created_at);
