-- ============================================================
-- Flyway Rollback Script
-- Version: V20260604005
-- Description: sys_role_inheritance角色继承关系表回滚脚本
-- Author: AI Generated
-- Date: 2026-06-03
-- ============================================================

-- 删除索引
DROP INDEX IF EXISTS idx_sys_role_inheritance_child_tenant;
DROP INDEX IF EXISTS idx_sys_role_inheritance_child;
DROP INDEX IF EXISTS idx_sys_role_inheritance_parent;
DROP INDEX IF EXISTS uk_sys_role_inheritance;

-- 删除表
DROP TABLE IF EXISTS sys_role_inheritance;
