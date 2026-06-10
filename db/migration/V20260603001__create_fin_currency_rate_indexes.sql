-- ============================================================
-- Flyway Migration Script
-- Version: V20260603001
-- Description: fin_currency_rate币种汇率表索引与约束
-- Author: AI Generated
-- Date: 2026-06-03
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE fin_currency_rate RENAME CONSTRAINT fin_currency_rate_pkey TO pk_fin_currency_rate;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_fin_currency_rate_currency_effective ON fin_currency_rate(from_currency_id, to_currency_id, effective_date) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_fin_currency_rate_tenant_from_currency ON fin_currency_rate(tenant_id, from_currency_id);
CREATE INDEX idx_fin_currency_rate_tenant_to_currency ON fin_currency_rate(tenant_id, to_currency_id);
CREATE INDEX idx_fin_currency_rate_tenant_effective_date ON fin_currency_rate(tenant_id, effective_date);
CREATE INDEX idx_fin_currency_rate_tenant_deleted ON fin_currency_rate(tenant_id, is_deleted);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
-- 外键关联字段索引
CREATE INDEX idx_fin_currency_rate_from_currency ON fin_currency_rate(from_currency_id);
CREATE INDEX idx_fin_currency_rate_to_currency ON fin_currency_rate(to_currency_id);

-- 日期范围查询
CREATE INDEX idx_fin_currency_rate_effective_date ON fin_currency_rate(effective_date);
CREATE INDEX idx_fin_currency_rate_created_at ON fin_currency_rate(tenant_id, created_at);

-- 操作人索引
CREATE INDEX idx_fin_currency_rate_created_by ON fin_currency_rate(created_by);
CREATE INDEX idx_fin_currency_rate_updated_by ON fin_currency_rate(updated_by);
CREATE INDEX idx_fin_currency_rate_owner_dept ON fin_currency_rate(owner_dept_id);
CREATE INDEX idx_fin_currency_rate_owner ON fin_currency_rate(owner_id);
