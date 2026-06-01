-- ============================================================
-- Flyway Migration Script
-- Version: V20260601092
-- Description: crm_customer_evaluation 客户评价表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-008-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE crm_customer_evaluation RENAME CONSTRAINT crm_customer_evaluation_pkey TO pk_crm_customer_evaluation;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_crm_customer_evaluation_code ON crm_customer_evaluation(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_crm_customer_evaluation_tenant_code ON crm_customer_evaluation(tenant_id, code);
CREATE INDEX idx_crm_customer_evaluation_tenant_status ON crm_customer_evaluation(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
-- 客户外键关联
CREATE INDEX idx_crm_customer_evaluation_customer_id ON crm_customer_evaluation(customer_id);

-- 树形结构查询（parent_id）
CREATE INDEX idx_crm_customer_evaluation_parent_id ON crm_customer_evaluation(parent_id);

-- 评价人查询
CREATE INDEX idx_crm_customer_evaluation_evaluator_id ON crm_customer_evaluation(evaluator_id);

-- 评价日期范围查询
CREATE INDEX idx_crm_customer_evaluation_evaluation_date ON crm_customer_evaluation(evaluation_date);

-- 通用字段索引
CREATE INDEX idx_crm_customer_evaluation_created_by ON crm_customer_evaluation(created_by);
CREATE INDEX idx_crm_customer_evaluation_updated_by ON crm_customer_evaluation(updated_by);
CREATE INDEX idx_crm_customer_evaluation_owner_dept ON crm_customer_evaluation(owner_dept_id);
CREATE INDEX idx_crm_customer_evaluation_owner ON crm_customer_evaluation(owner_id);

-- 创建时间范围查询（tenant_id首列）
CREATE INDEX idx_crm_customer_evaluation_created_at ON crm_customer_evaluation(tenant_id, created_at);
