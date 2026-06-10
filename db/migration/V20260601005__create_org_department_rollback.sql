-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601005
-- Description: org_department部门表回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- 回滚：删除org_department表
DROP TABLE IF EXISTS org_department CASCADE;
