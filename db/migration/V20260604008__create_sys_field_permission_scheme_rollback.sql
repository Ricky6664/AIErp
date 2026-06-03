-- ============================================================
-- Flyway Migration Rollback Script
-- Description: 回滚 sys_field_permission_scheme / sys_field_permission_scheme_role / sys_field_permission_scheme_detail 表
-- Author: AI Generated
-- Date: 2026-06-03
-- ============================================================

DROP TABLE IF EXISTS sys_field_permission_scheme_detail CASCADE;
DROP TABLE IF EXISTS sys_field_permission_scheme_role CASCADE;
DROP TABLE IF EXISTS sys_field_permission_scheme CASCADE;
