-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601088
-- Description: crm_customer_attachment 客户附件表回滚
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- 回滚：删除表（CASCADE 确保所有依赖一并删除）
DROP TABLE IF EXISTS crm_customer_attachment CASCADE;
