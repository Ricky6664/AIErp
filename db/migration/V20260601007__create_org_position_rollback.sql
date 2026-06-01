-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601007
-- Description: org_position岗位表回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- 回滚：删除org_position表
DROP TABLE IF EXISTS org_position CASCADE;
