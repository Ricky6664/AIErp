-- ============================================================
-- Flyway Migration Rollback Script
-- Description: 回滚 sys_data_permission_scheme 及 sys_data_permission_scheme_role 表
-- Author: AI Generated
-- Date: 2026-06-03
-- ============================================================

DROP TABLE IF EXISTS sys_data_permission_scheme_role CASCADE;
DROP TABLE IF EXISTS sys_data_permission_scheme CASCADE;
