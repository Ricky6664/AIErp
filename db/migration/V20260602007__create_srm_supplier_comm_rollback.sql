-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260602007
-- Description: srm_supplier_comm 供应商联系人通讯表回滚
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-004-001-001
-- ============================================================

-- 回滚：删除srm_supplier_comm表及其所有依赖
DROP TABLE IF EXISTS srm_supplier_comm CASCADE;
