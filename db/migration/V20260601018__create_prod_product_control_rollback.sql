-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601018
-- Description: prod_product_control商品控制策略表建表DDL回滚
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- 回滚：删除prod_product_control表
DROP TABLE IF EXISTS prod_product_control CASCADE;
