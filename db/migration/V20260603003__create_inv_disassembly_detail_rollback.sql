-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260603003
-- Description: inv_disassembly_detail拆卸主从表建表回滚脚本
-- Author: AI
-- Date: 2026-06-03
-- Task: P0-003-007-012-001-001
-- ============================================================

-- 回滚：删除拆卸从表
DROP TABLE IF EXISTS inv_disassembly_detail CASCADE;

-- 回滚：删除拆卸主表
DROP TABLE IF EXISTS inv_disassembly CASCADE;
