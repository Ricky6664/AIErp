-- ============================================================
-- Flyway Migration Script
-- Version: V20260601095
-- Description: crm_customer_finance 客户财务配置表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-009-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE crm_customer_finance RENAME CONSTRAINT crm_customer_finance_pkey TO pk_crm_customer_finance;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_crm_customer_finance_code ON crm_customer_finance(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_crm_customer_finance_tenant_code ON crm_customer_finance(tenant_id, code);
CREATE INDEX idx_crm_customer_finance_tenant_status ON crm_customer_finance(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
-- 客户外键关联
CREATE INDEX idx_crm_customer_finance_customer_id ON crm_customer_finance(customer_id);

-- 商品外键关联
CREATE INDEX idx_crm_customer_finance_product_id ON crm_customer_finance(product_id);

-- 单据日期范围查询（多租户隔离）
CREATE INDEX idx_crm_customer_finance_order_date ON crm_customer_finance(tenant_id, order_date);

-- 通用字段索引
CREATE INDEX idx_crm_customer_finance_created_by ON crm_customer_finance(created_by);
CREATE INDEX idx_crm_customer_finance_updated_by ON crm_customer_finance(updated_by);
CREATE INDEX idx_crm_customer_finance_owner_dept ON crm_customer_finance(owner_dept_id);
CREATE INDEX idx_crm_customer_finance_owner ON crm_customer_finance(owner_id);

-- 创建时间范围查询（tenant_id首列）
CREATE INDEX idx_crm_customer_finance_created_at ON crm_customer_finance(tenant_id, created_at);
