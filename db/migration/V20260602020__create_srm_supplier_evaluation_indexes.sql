-- ============================================================
-- Flyway Migration Script
-- Version: V20260602020
-- Description: srm_supplier_evaluation供应商评价表索引与约束
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-008-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
-- ============================================================
ALTER TABLE srm_supplier_evaluation RENAME CONSTRAINT srm_supplier_evaluation_pkey TO pk_srm_supplier_evaluation;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_srm_supplier_evaluation_code ON srm_supplier_evaluation(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_srm_supplier_evaluation_tenant_code ON srm_supplier_evaluation(tenant_id, code);
CREATE INDEX idx_srm_supplier_evaluation_tenant_status ON srm_supplier_evaluation(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================

-- 外键关联字段索引
CREATE INDEX idx_srm_supplier_evaluation_supplier ON srm_supplier_evaluation(supplier_id);

-- 评价类型筛选
CREATE INDEX idx_srm_supplier_evaluation_type ON srm_supplier_evaluation(evaluation_type);

-- 评价人筛选
CREATE INDEX idx_srm_supplier_evaluation_evaluator ON srm_supplier_evaluation(evaluator_id);

-- 评价日期范围查询
CREATE INDEX idx_srm_supplier_evaluation_date ON srm_supplier_evaluation(evaluation_date);

-- 状态筛选
CREATE INDEX idx_srm_supplier_evaluation_status ON srm_supplier_evaluation(status);

-- 树形结构查询（parent_id递归）
CREATE INDEX idx_srm_supplier_evaluation_parent ON srm_supplier_evaluation(parent_id);

-- 通用字段索引
CREATE INDEX idx_srm_supplier_evaluation_created_by ON srm_supplier_evaluation(created_by);
CREATE INDEX idx_srm_supplier_evaluation_updated_by ON srm_supplier_evaluation(updated_by);
CREATE INDEX idx_srm_supplier_evaluation_owner_dept ON srm_supplier_evaluation(owner_dept_id);
CREATE INDEX idx_srm_supplier_evaluation_owner ON srm_supplier_evaluation(owner_id);

-- 日期范围查询（租户+创建时间）
CREATE INDEX idx_srm_supplier_evaluation_created_at ON srm_supplier_evaluation(tenant_id, created_at);
