-- ============================================================
-- Flyway Migration Script
-- Version: V20260601101
-- Description: crm_project 客户项目表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-011-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE crm_project RENAME CONSTRAINT crm_project_pkey TO pk_crm_project;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_crm_project_code ON crm_project(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_crm_project_tenant_code ON crm_project(tenant_id, code);
CREATE INDEX idx_crm_project_tenant_status ON crm_project(tenant_id, status);
CREATE INDEX idx_crm_project_tenant_deleted ON crm_project(tenant_id, is_deleted);
CREATE INDEX idx_crm_project_tenant_customer ON crm_project(tenant_id, customer_id);
CREATE INDEX idx_crm_project_tenant_stage ON crm_project(tenant_id, stage);
CREATE INDEX idx_crm_project_tenant_manager ON crm_project(tenant_id, project_manager_id);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
-- 外键关联字段索引
CREATE INDEX idx_crm_project_customer_id ON crm_project(customer_id);
CREATE INDEX idx_crm_project_project_manager ON crm_project(project_manager_id);
CREATE INDEX idx_crm_project_created_by ON crm_project(created_by);
CREATE INDEX idx_crm_project_updated_by ON crm_project(updated_by);
CREATE INDEX idx_crm_project_owner_dept ON crm_project(owner_dept_id);
CREATE INDEX idx_crm_project_owner ON crm_project(owner_id);

-- 状态筛选索引
CREATE INDEX idx_crm_project_status ON crm_project(status);
CREATE INDEX idx_crm_project_stage ON crm_project(stage);

-- 日期范围查询索引
CREATE INDEX idx_crm_project_start_date ON crm_project(tenant_id, start_date);
CREATE INDEX idx_crm_project_end_date ON crm_project(tenant_id, end_date);
CREATE INDEX idx_crm_project_created_at ON crm_project(tenant_id, created_at);

-- 单据号查询索引
CREATE INDEX idx_crm_project_tenant_order ON crm_project(tenant_id, order_no);
