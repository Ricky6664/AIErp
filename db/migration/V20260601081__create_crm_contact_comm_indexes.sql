-- ============================================================
-- Flyway Migration Script
-- Version: V20260601081
-- Description: crm_contact_comm 联系人通讯表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-004-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE crm_contact_comm RENAME CONSTRAINT crm_contact_comm_pkey TO pk_crm_contact_comm;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_crm_contact_comm_code ON crm_contact_comm(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_crm_contact_comm_tenant_code ON crm_contact_comm(tenant_id, code);
CREATE INDEX idx_crm_contact_comm_tenant_status ON crm_contact_comm(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
-- 联系人外键关联
CREATE INDEX idx_crm_contact_comm_contact_id ON crm_contact_comm(contact_id);

-- 树形结构（parent_id）
CREATE INDEX idx_crm_contact_comm_parent_id ON crm_contact_comm(parent_id);

-- 通讯类型查询
CREATE INDEX idx_crm_contact_comm_comm_type ON crm_contact_comm(tenant_id, comm_type);

-- 通用字段索引
CREATE INDEX idx_crm_contact_comm_created_by ON crm_contact_comm(created_by);
CREATE INDEX idx_crm_contact_comm_updated_by ON crm_contact_comm(updated_by);
CREATE INDEX idx_crm_contact_comm_owner_dept ON crm_contact_comm(owner_dept_id);
CREATE INDEX idx_crm_contact_comm_owner ON crm_contact_comm(owner_id);

-- 日期范围查询（tenant_id首列）
CREATE INDEX idx_crm_contact_comm_created_at ON crm_contact_comm(tenant_id, created_at);
