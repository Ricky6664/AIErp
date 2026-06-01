-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601009
-- Description: org_employee员工表回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- 回滚：删除org_employee表
DROP TABLE IF EXISTS org_employee CASCADE;
