-- ============================================================
-- Flyway Migration Script
-- Version: V20260602008
-- Description: srm_supplier_comm供应商联系人表索引与约束
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-004-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE srm_supplier_comm RENAME CONSTRAINT srm_supplier_comm_pkey TO pk_srm_supplier_comm;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_srm_supplier_comm_code ON srm_supplier_comm(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_srm_supplier_comm_tenant_code ON srm_supplier_comm(tenant_id, code);
CREATE INDEX idx_srm_supplier_comm_tenant_status ON srm_supplier_comm(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================

-- 外键关联字段索引
CREATE INDEX idx_srm_supplier_comm_supplier ON srm_supplier_comm(supplier_id);

-- 状态筛选
CREATE INDEX idx_srm_supplier_comm_status ON srm_supplier_comm(status);

-- 树形结构索引
CREATE INDEX idx_srm_supplier_comm_parent ON srm_supplier_comm(parent_id);

-- 通用字段索引
CREATE INDEX idx_srm_supplier_comm_created_by ON srm_supplier_comm(created_by);
CREATE INDEX idx_srm_supplier_comm_updated_by ON srm_supplier_comm(updated_by);
CREATE INDEX idx_srm_supplier_comm_owner_dept ON srm_supplier_comm(owner_dept_id);
CREATE INDEX idx_srm_supplier_comm_owner ON srm_supplier_comm(owner_id);

-- 日期范围查询
CREATE INDEX idx_srm_supplier_comm_created_at ON srm_supplier_comm(tenant_id, created_at);
