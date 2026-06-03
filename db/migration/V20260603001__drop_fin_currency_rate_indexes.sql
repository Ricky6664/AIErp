-- ============================================================
-- Flyway Rollback Script
-- Version: V20260603001
-- Description: fin_currency_rate币种汇率表索引回滚（DROP INDEX）
-- Author: AI Generated
-- Date: 2026-06-03
-- ============================================================

-- ============================================================
-- 回滚说明：按创建逆序删除索引，最后恢复主键约束原名
-- ============================================================

-- 4. 业务查询索引回滚
DROP INDEX IF EXISTS idx_fin_currency_rate_owner;
DROP INDEX IF EXISTS idx_fin_currency_rate_owner_dept;
DROP INDEX IF EXISTS idx_fin_currency_rate_updated_by;
DROP INDEX IF EXISTS idx_fin_currency_rate_created_by;
DROP INDEX IF EXISTS idx_fin_currency_rate_created_at;
DROP INDEX IF EXISTS idx_fin_currency_rate_effective_date;
DROP INDEX IF EXISTS idx_fin_currency_rate_to_currency;
DROP INDEX IF EXISTS idx_fin_currency_rate_from_currency;

-- 3. 多租户联合索引回滚
DROP INDEX IF EXISTS idx_fin_currency_rate_tenant_deleted;
DROP INDEX IF EXISTS idx_fin_currency_rate_tenant_effective_date;
DROP INDEX IF EXISTS idx_fin_currency_rate_tenant_to_currency;
DROP INDEX IF EXISTS idx_fin_currency_rate_tenant_from_currency;

-- 2. 部分唯一索引回滚
DROP INDEX IF EXISTS uk_fin_currency_rate_currency_effective;

-- 1. 主键约束恢复原名
ALTER TABLE fin_currency_rate RENAME CONSTRAINT pk_fin_currency_rate TO fin_currency_rate_pkey;
