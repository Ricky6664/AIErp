-- ============================================================
-- Flyway Rollback Script
-- Version: V20260526001
-- Description: inv_other_outbound_detail其他出库从表索引回滚脚本
-- Author: AI
-- Date: 2026-06-02
-- Task: P0-003-007-005-001-002
-- ============================================================

DROP INDEX IF EXISTS idx_inv_other_outbound_detail_created_at;
DROP INDEX IF EXISTS idx_inv_other_outbound_detail_owner;
DROP INDEX IF EXISTS idx_inv_other_outbound_detail_owner_dept;
DROP INDEX IF EXISTS idx_inv_other_outbound_detail_updated_by;
DROP INDEX IF EXISTS idx_inv_other_outbound_detail_created_by;
DROP INDEX IF EXISTS idx_inv_other_outbound_detail_tenant_product_name;
DROP INDEX IF EXISTS idx_inv_other_outbound_detail_tenant_product_code;
DROP INDEX IF EXISTS idx_inv_other_outbound_detail_batch_no;
DROP INDEX IF EXISTS idx_inv_other_outbound_detail_line_no;
DROP INDEX IF EXISTS idx_inv_other_outbound_detail_location;
DROP INDEX IF EXISTS idx_inv_other_outbound_detail_warehouse;
DROP INDEX IF EXISTS idx_inv_other_outbound_detail_product;
DROP INDEX IF EXISTS idx_inv_other_outbound_detail_order;
DROP INDEX IF EXISTS idx_inv_other_outbound_detail_tenant_deleted;
DROP INDEX IF EXISTS idx_inv_other_outbound_detail_tenant_code;
DROP INDEX IF EXISTS uk_inv_other_outbound_detail_code;
