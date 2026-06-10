-- ============================================================
-- Flyway Migration Script
-- Version: V20260601098
-- Description: crm_opportunity 客户机会表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-010-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE crm_opportunity RENAME CONSTRAINT crm_opportunity_pkey TO pk_crm_opportunity;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_crm_opportunity_code ON crm_opportunity(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_crm_opportunity_tenant_code ON crm_opportunity(tenant_id, code);
CREATE INDEX idx_crm_opportunity_tenant_status ON crm_opportunity(tenant_id, status);
CREATE INDEX idx_crm_opportunity_tenant_deleted ON crm_opportunity(tenant_id, is_deleted);
CREATE INDEX idx_crm_opportunity_tenant_customer ON crm_opportunity(tenant_id, customer_id);
CREATE INDEX idx_crm_opportunity_tenant_stage ON crm_opportunity(tenant_id, stage);
CREATE INDEX idx_crm_opportunity_tenant_sales ON crm_opportunity(tenant_id, sales_person_id);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
-- 外键关联字段索引
CREATE INDEX idx_crm_opportunity_customer_id ON crm_opportunity(customer_id);
CREATE INDEX idx_crm_opportunity_sales_person ON crm_opportunity(sales_person_id);
CREATE INDEX idx_crm_opportunity_created_by ON crm_opportunity(created_by);
CREATE INDEX idx_crm_opportunity_updated_by ON crm_opportunity(updated_by);
CREATE INDEX idx_crm_opportunity_owner_dept ON crm_opportunity(owner_dept_id);
CREATE INDEX idx_crm_opportunity_owner ON crm_opportunity(owner_id);

-- 状态筛选索引
CREATE INDEX idx_crm_opportunity_status ON crm_opportunity(status);
CREATE INDEX idx_crm_opportunity_stage ON crm_opportunity(stage);

-- 日期范围查询索引
CREATE INDEX idx_crm_opportunity_close_date ON crm_opportunity(tenant_id, estimated_close_date);
CREATE INDEX idx_crm_opportunity_created_at ON crm_opportunity(tenant_id, created_at);

-- 单据号查询索引
CREATE INDEX idx_crm_opportunity_tenant_order ON crm_opportunity(tenant_id, order_no);
