-- ============================================================
-- Flyway Rollback Script
-- Version: V20260526001
-- Description: inv_stocktaking_detail盘点主从表回滚脚本
-- Author: AI
-- Date: 2026-06-02
-- ============================================================

DROP TABLE IF EXISTS inv_stocktaking_detail CASCADE;
DROP TABLE IF EXISTS inv_stocktaking CASCADE;
