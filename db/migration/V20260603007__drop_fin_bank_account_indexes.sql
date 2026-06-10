-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260603007
-- Description: fin_bank_account银行账户表索引回滚脚本
-- Author: AI
-- Date: 2026-06-03
-- Task: P0-003-008-002-001-002
-- ============================================================

-- ============================================================
-- 一、通用字段索引回滚
-- ============================================================

DROP INDEX IF EXISTS idx_fin_bank_account_created_at;
DROP INDEX IF EXISTS idx_fin_bank_account_owner;
DROP INDEX IF EXISTS idx_fin_bank_account_owner_dept;
DROP INDEX IF EXISTS idx_fin_bank_account_updated_by;
DROP INDEX IF EXISTS idx_fin_bank_account_created_by;

-- ============================================================
-- 二、业务查询索引回滚
-- ============================================================

DROP INDEX IF EXISTS idx_fin_bank_account_bank_name;
DROP INDEX IF EXISTS idx_fin_bank_account_currency;
DROP INDEX IF EXISTS idx_fin_bank_account_company;

-- ============================================================
-- 三、多租户联合索引回滚
-- ============================================================

DROP INDEX IF EXISTS idx_fin_bank_account_tenant_deleted;
DROP INDEX IF EXISTS idx_fin_bank_account_tenant_account_no;

-- ============================================================
-- 四、部分唯一索引回滚
-- ============================================================

DROP INDEX IF EXISTS uk_fin_bank_account_account_no;
