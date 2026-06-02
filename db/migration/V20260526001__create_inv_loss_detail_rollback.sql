-- ============================================================
-- Flyway Rollback Script
-- Version: V20260526001
-- Description: inv_loss_detail报损主从表回滚DDL
-- Author: AI
-- Date: 2026-06-02
-- Task: P0-003-007-009-001-001
-- ============================================================

DROP TABLE IF EXISTS inv_loss_detail CASCADE;
DROP TABLE IF EXISTS inv_loss CASCADE;
