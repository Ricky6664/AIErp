-- ============================================================
-- Flyway Rollback Script
-- Version: V20260526001
-- Description: fin_account会计科目表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-03
-- Task: P0-003-008-003-001-002
-- ============================================================

DROP INDEX IF EXISTS idx_fin_account_created_at;
DROP INDEX IF EXISTS idx_fin_account_owner;
DROP INDEX IF EXISTS idx_fin_account_owner_dept;
DROP INDEX IF EXISTS idx_fin_account_updated_by;
DROP INDEX IF EXISTS idx_fin_account_created_by;
DROP INDEX IF EXISTS idx_fin_account_name;
DROP INDEX IF EXISTS idx_fin_account_balance_direction;
DROP INDEX IF EXISTS idx_fin_account_type;
DROP INDEX IF EXISTS idx_fin_account_parent;
DROP INDEX IF EXISTS idx_fin_account_tenant_type;
DROP INDEX IF EXISTS idx_fin_account_tenant_code;
DROP INDEX IF EXISTS uk_fin_account_code;
