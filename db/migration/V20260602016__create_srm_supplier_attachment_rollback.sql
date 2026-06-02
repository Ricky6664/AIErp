-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260602016
-- Description: srm_supplier_attachment 供应商附件表回滚
-- Author: AI Generated
-- Date: 2026-06-02
-- ============================================================

-- 回滚：删除表（CASCADE 确保所有依赖一并删除）
DROP TABLE IF EXISTS srm_supplier_attachment CASCADE;
