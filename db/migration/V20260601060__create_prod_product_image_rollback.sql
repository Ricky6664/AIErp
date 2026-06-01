-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601060
-- Description: prod_product_image商品图片表回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- 回滚：删除prod_product_image表
DROP TABLE IF EXISTS prod_product_image CASCADE;
