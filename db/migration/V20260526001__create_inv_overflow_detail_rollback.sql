-- ============================================================
-- Flyway Rollback Script
-- Version: V20260526001
-- Description: inv_overflow_detail报溢主从表回滚DDL
-- Author: AI
-- Date: 2026-06-03
-- Task: P0-003-007-010-001-001
-- ============================================================

DROP TABLE IF EXISTS inv_overflow_detail CASCADE;
DROP TABLE IF EXISTS inv_overflow CASCADE;
