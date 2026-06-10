-- ============================================================
-- Flyway Rollback Script
-- Version: V20260603001
-- Description: inv_assembly_detail组装主从表回滚DDL
-- Author: AI
-- Date: 2026-06-03
-- Task: P0-003-007-011-001-001
-- ============================================================

DROP TABLE IF EXISTS inv_assembly_detail CASCADE;
DROP TABLE IF EXISTS inv_assembly CASCADE;
