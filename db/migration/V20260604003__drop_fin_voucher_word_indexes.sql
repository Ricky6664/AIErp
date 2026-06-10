-- ============================================================
-- Flyway Rollback Script
-- Version: V20260604003
-- Description: fin_voucher_word凭证字表索引回滚
-- Author: AI Generated
-- Date: 2026-06-03
-- ============================================================

DROP INDEX IF EXISTS idx_fin_voucher_word_created_at;
DROP INDEX IF EXISTS idx_fin_voucher_word_sort_order;
DROP INDEX IF EXISTS idx_fin_voucher_word_owner;
DROP INDEX IF EXISTS idx_fin_voucher_word_owner_dept;
DROP INDEX IF EXISTS idx_fin_voucher_word_updated_by;
DROP INDEX IF EXISTS idx_fin_voucher_word_created_by;
DROP INDEX IF EXISTS idx_fin_voucher_word_tenant_deleted;
DROP INDEX IF EXISTS idx_fin_voucher_word_tenant_code;
DROP INDEX IF EXISTS uk_fin_voucher_word_code;
