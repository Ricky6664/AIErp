-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601030
-- Description: prod_product_purchase_price 商品购价核定表建表回滚
-- Author: AI Generated
-- Date: 2026-06-01
-- Usage: 仅用于开发环境回滚，生产环境禁止执行
-- ============================================================

-- 回滚：删除表及关联对象
DROP TABLE IF EXISTS prod_product_purchase_price CASCADE;
