-- ============================================================
-- Flyway Migration Script
-- Version: V20260601083
-- Description: crm_customer_address 客户地址表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-005-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE crm_customer_address RENAME CONSTRAINT crm_customer_address_pkey TO pk_crm_customer_address;

-- ============================================================
-- 2. 部分唯一索引
-- 注：crm_customer_address表无code等业务唯一字段（此表为客户地址子表），
-- 不创建部分唯一索引
-- ============================================================

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_crm_customer_address_tenant_customer ON crm_customer_address(tenant_id, customer_id);
CREATE INDEX idx_crm_customer_address_tenant_status ON crm_customer_address(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
-- 客户外键关联
CREATE INDEX idx_crm_customer_address_customer_id ON crm_customer_address(customer_id);

-- 地址类型查询（tenant_id首列）
CREATE INDEX idx_crm_customer_address_address_type ON crm_customer_address(tenant_id, address_type);

-- 城市查询
CREATE INDEX idx_crm_customer_address_city ON crm_customer_address(tenant_id, city);

-- 通用字段索引
CREATE INDEX idx_crm_customer_address_created_by ON crm_customer_address(created_by);
CREATE INDEX idx_crm_customer_address_updated_by ON crm_customer_address(updated_by);
CREATE INDEX idx_crm_customer_address_owner_dept ON crm_customer_address(owner_dept_id);
CREATE INDEX idx_crm_customer_address_owner ON crm_customer_address(owner_id);

-- 日期范围查询（tenant_id首列）
CREATE INDEX idx_crm_customer_address_created_at ON crm_customer_address(tenant_id, created_at);
