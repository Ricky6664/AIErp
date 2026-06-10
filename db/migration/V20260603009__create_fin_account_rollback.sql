-- ============================================================
-- Flyway Rollback Script
-- Version: V20260603009
-- Description: fin_account会计科目表回滚脚本
-- Author: AI Generated
-- Date: 2026-06-03
-- ============================================================

-- 回滚：删除 fin_account 表
DROP TABLE IF EXISTS fin_account CASCADE;
