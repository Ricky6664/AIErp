-- ============================================================
-- Flyway Migration Script
-- Version: V20260601006
-- Description: org_department部门表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE org_department RENAME CONSTRAINT org_department_pkey TO pk_org_department;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_org_department_code ON org_department(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_org_department_tenant_code ON org_department(tenant_id, code);
CREATE INDEX idx_org_department_tenant_status ON org_department(tenant_id, status);
CREATE INDEX idx_org_department_tenant_deleted ON org_department(tenant_id, is_deleted);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
-- 树形结构索引
CREATE INDEX idx_org_department_parent_id ON org_department(tenant_id, parent_id);

-- 状态筛选
CREATE INDEX idx_org_department_status ON org_department(status);

-- 外键关联字段索引
CREATE INDEX idx_org_department_leader_id ON org_department(leader_id);
CREATE INDEX idx_org_department_created_by ON org_department(created_by);
CREATE INDEX idx_org_department_updated_by ON org_department(updated_by);
CREATE INDEX idx_org_department_owner_dept ON org_department(owner_dept_id);
CREATE INDEX idx_org_department_owner ON org_department(owner_id);

-- 日期范围查询
CREATE INDEX idx_org_department_created_at ON org_department(tenant_id, created_at);
