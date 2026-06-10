-- ============================================================
-- Flyway Migration Script
-- Version: V20260601004
-- Description: org_company公司表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE org_company RENAME CONSTRAINT org_company_pkey TO pk_org_company;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_org_company_code ON org_company(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_org_company_tenant_code ON org_company(tenant_id, code);
CREATE INDEX idx_org_company_tenant_status ON org_company(tenant_id, status);
CREATE INDEX idx_org_company_tenant_deleted ON org_company(tenant_id, is_deleted);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
-- 状态筛选
CREATE INDEX idx_org_company_status ON org_company(status);

-- 外键关联字段索引
CREATE INDEX idx_org_company_created_by ON org_company(created_by);
CREATE INDEX idx_org_company_updated_by ON org_company(updated_by);
CREATE INDEX idx_org_company_owner_dept ON org_company(owner_dept_id);
CREATE INDEX idx_org_company_owner ON org_company(owner_id);

-- 日期范围查询
CREATE INDEX idx_org_company_created_at ON org_company(tenant_id, created_at);
