-- ============================================================
-- Flyway Migration Script
-- Version: V20260602023
-- Description: srm_supplier_finance供应商财务配置表索引与约束
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-009-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
-- ============================================================
ALTER TABLE srm_supplier_finance RENAME CONSTRAINT srm_supplier_finance_pkey TO pk_srm_supplier_finance;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_srm_supplier_finance_code ON srm_supplier_finance(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_srm_supplier_finance_tenant_code ON srm_supplier_finance(tenant_id, code);
CREATE INDEX idx_srm_supplier_finance_tenant_status ON srm_supplier_finance(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================

-- 外键关联字段索引
CREATE INDEX idx_srm_supplier_finance_supplier ON srm_supplier_finance(supplier_id);

-- 税号查询
CREATE INDEX idx_srm_supplier_finance_tax_no ON srm_supplier_finance(tax_no);

-- 状态筛选
CREATE INDEX idx_srm_supplier_finance_status ON srm_supplier_finance(status);

-- 关联单据日期范围查询
CREATE INDEX idx_srm_supplier_finance_order_date ON srm_supplier_finance(order_date);

-- 通用字段索引
CREATE INDEX idx_srm_supplier_finance_created_by ON srm_supplier_finance(created_by);
CREATE INDEX idx_srm_supplier_finance_updated_by ON srm_supplier_finance(updated_by);
CREATE INDEX idx_srm_supplier_finance_owner_dept ON srm_supplier_finance(owner_dept_id);
CREATE INDEX idx_srm_supplier_finance_owner ON srm_supplier_finance(owner_id);

-- 日期范围查询（租户+创建时间）
CREATE INDEX idx_srm_supplier_finance_created_at ON srm_supplier_finance(tenant_id, created_at);
