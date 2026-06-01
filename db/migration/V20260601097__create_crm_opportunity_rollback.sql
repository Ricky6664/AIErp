-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601097
-- Description: crm_opportunity 客户机会表回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-010-001-001
-- ============================================================

-- 回滚：删除 crm_opportunity 表及其关联对象
-- 警告：此操作将删除表及全部数据，不可恢复！

DROP TABLE IF EXISTS crm_opportunity CASCADE;
