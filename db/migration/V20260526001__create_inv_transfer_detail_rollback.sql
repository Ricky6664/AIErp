-- ============================================================
-- Flyway Rollback Script
-- Version: V20260526001
-- Description: inv_transfer_detail调拨主从表回滚脚本
-- Author: AI
-- Date: 2026-06-02
-- ============================================================

DROP TABLE IF EXISTS inv_transfer_detail CASCADE;
DROP TABLE IF EXISTS inv_transfer CASCADE;
