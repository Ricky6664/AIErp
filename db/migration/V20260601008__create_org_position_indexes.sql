-- ============================================================
-- Flyway Migration Script
-- Version: V20260601008
-- Description: org_position岗位表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE org_position RENAME CONSTRAINT org_position_pkey TO pk_org_position;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_org_position_code ON org_position(position_code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_org_position_tenant_code ON org_position(tenant_id, position_code);
CREATE INDEX idx_org_position_tenant_status ON org_position(tenant_id, status);
CREATE INDEX idx_org_position_tenant_deleted ON org_position(tenant_id, is_deleted);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
-- 部门关联查询
CREATE INDEX idx_org_position_dept_id ON org_position(tenant_id, dept_id);

-- 排序字段索引
CREATE INDEX idx_org_position_sort_order ON org_position(tenant_id, sort_order);

-- 状态筛选
CREATE INDEX idx_org_position_status ON org_position(status);

-- 通用外键字段索引
CREATE INDEX idx_org_position_created_by ON org_position(created_by);
CREATE INDEX idx_org_position_updated_by ON org_position(updated_by);
CREATE INDEX idx_org_position_owner_dept ON org_position(owner_dept_id);
CREATE INDEX idx_org_position_owner ON org_position(owner_id);

-- 日期范围查询
CREATE INDEX idx_org_position_created_at ON org_position(tenant_id, created_at);
