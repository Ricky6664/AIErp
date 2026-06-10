-- ============================================================
-- Flyway Rollback Script
-- Version: V20260604006
-- Description: sys_role_exclusion角色互斥表回滚脚本
-- Author: AI Generated
-- Date: 2026-06-03
-- ============================================================

-- 删除索引
DROP INDEX IF EXISTS idx_sys_role_exclusion_role_tenant;
DROP INDEX IF EXISTS idx_sys_role_exclusion_role_b;
DROP INDEX IF EXISTS idx_sys_role_exclusion_role_a;
DROP INDEX IF EXISTS uk_sys_role_exclusion;

-- 删除表
DROP TABLE IF EXISTS sys_role_exclusion;
