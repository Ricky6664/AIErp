-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: inv_stock库存实时表索引与约束
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-007-003-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
-- ============================================================
ALTER TABLE inv_stock RENAME CONSTRAINT inv_stock_pkey TO pk_inv_stock;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_inv_stock_order_no ON inv_stock(order_no) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_inv_stock_tenant_order_no ON inv_stock(tenant_id, order_no);
CREATE INDEX idx_inv_stock_tenant_status ON inv_stock(tenant_id, status);
CREATE INDEX idx_inv_stock_tenant_deleted ON inv_stock(tenant_id, is_deleted);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================

-- 外键关联字段（product_id / warehouse_id）
CREATE INDEX idx_inv_stock_product ON inv_stock(product_id);
CREATE INDEX idx_inv_stock_warehouse ON inv_stock(warehouse_id);

-- 批次号查询
CREATE INDEX idx_inv_stock_batch_no ON inv_stock(batch_no);

-- 状态筛选
CREATE INDEX idx_inv_stock_status ON inv_stock(status);

-- 日期范围查询
CREATE INDEX idx_inv_stock_order_date ON inv_stock(order_date);

-- 商品快照字段查询（多租户）
CREATE INDEX idx_inv_stock_tenant_product_code ON inv_stock(tenant_id, product_code);
CREATE INDEX idx_inv_stock_tenant_product_name ON inv_stock(tenant_id, product_name);

-- ============================================================
-- 5. 通用字段索引
-- ============================================================

-- 创建人/修改人查询
CREATE INDEX idx_inv_stock_created_by ON inv_stock(created_by);
CREATE INDEX idx_inv_stock_updated_by ON inv_stock(updated_by);

-- 数据权限查询
CREATE INDEX idx_inv_stock_owner_dept ON inv_stock(owner_dept_id);
CREATE INDEX idx_inv_stock_owner ON inv_stock(owner_id);

-- 租户+创建时间范围查询
CREATE INDEX idx_inv_stock_created_at ON inv_stock(tenant_id, created_at);
