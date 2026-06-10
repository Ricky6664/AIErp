-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601057
-- Description: prod_product_barcode 商品条码表回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- 回滚：删除 prod_product_barcode 表
DROP TABLE IF EXISTS prod_product_barcode CASCADE;

-- 清除 flyway_schema_history 中该版本的迁移记录
-- DELETE FROM flyway_schema_history WHERE version = '20260601057';
