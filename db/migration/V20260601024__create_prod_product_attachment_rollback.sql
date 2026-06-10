-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601024
-- Description: prod_product_attachment商品附件表回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- 回滚：删除prod_product_attachment表及其所有数据
DROP TABLE IF EXISTS prod_product_attachment CASCADE;
