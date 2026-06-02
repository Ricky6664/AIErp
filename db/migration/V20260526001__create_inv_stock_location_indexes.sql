-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: inv_stock_location库位库存表索引与约束
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-007-004-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
-- ============================================================
ALTER TABLE inv_stock_location RENAME CONSTRAINT inv_stock_location_pkey TO pk_inv_stock_location;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
--    库位库存以(tenant_id, warehouse_id, location_id, product_id, batch_no)为业务唯一键
--    batch_no可为NULL，PostgreSQL中NULL≠NULL，非批次品允许同库位多记录
-- ============================================================
CREATE UNIQUE INDEX uk_inv_stock_location_unique ON inv_stock_location(tenant_id, warehouse_id, location_id, product_id, batch_no) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_inv_stock_location_tenant_status ON inv_stock_location(tenant_id, status);
CREATE INDEX idx_inv_stock_location_tenant_deleted ON inv_stock_location(tenant_id, is_deleted);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================

-- 外键关联字段（product_id / warehouse_id / location_id）
CREATE INDEX idx_inv_stock_location_product ON inv_stock_location(product_id);
CREATE INDEX idx_inv_stock_location_warehouse ON inv_stock_location(warehouse_id);
CREATE INDEX idx_inv_stock_location_location ON inv_stock_location(location_id);

-- 批次号查询
CREATE INDEX idx_inv_stock_location_batch_no ON inv_stock_location(batch_no);

-- 状态筛选
CREATE INDEX idx_inv_stock_location_status ON inv_stock_location(status);

-- 日期范围查询
CREATE INDEX idx_inv_stock_location_order_date ON inv_stock_location(order_date);

-- 单据编号查询（多租户）
CREATE INDEX idx_inv_stock_location_tenant_order_no ON inv_stock_location(tenant_id, order_no);

-- 商品快照字段查询（多租户）
CREATE INDEX idx_inv_stock_location_tenant_product_code ON inv_stock_location(tenant_id, product_code);
CREATE INDEX idx_inv_stock_location_tenant_product_name ON inv_stock_location(tenant_id, product_name);

-- ============================================================
-- 5. 通用字段索引
-- ============================================================

-- 创建人/修改人查询
CREATE INDEX idx_inv_stock_location_created_by ON inv_stock_location(created_by);
CREATE INDEX idx_inv_stock_location_updated_by ON inv_stock_location(updated_by);

-- 数据权限查询
CREATE INDEX idx_inv_stock_location_owner_dept ON inv_stock_location(owner_dept_id);
CREATE INDEX idx_inv_stock_location_owner ON inv_stock_location(owner_id);

-- 租户+创建时间范围查询
CREATE INDEX idx_inv_stock_location_created_at ON inv_stock_location(tenant_id, created_at);
