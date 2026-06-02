-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: inv_other_outbound_detail其他出库从表索引与约束
-- Author: AI
-- Date: 2026-06-02
-- Task: P0-003-007-005-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
-- ============================================================
ALTER TABLE inv_other_outbound_detail RENAME CONSTRAINT inv_other_outbound_detail_pkey TO pk_inv_other_outbound_detail;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
--    code字段在is_deleted=false范围内保证唯一
-- ============================================================
CREATE UNIQUE INDEX uk_inv_other_outbound_detail_code ON inv_other_outbound_detail(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_inv_other_outbound_detail_tenant_code ON inv_other_outbound_detail(tenant_id, code);
CREATE INDEX idx_inv_other_outbound_detail_tenant_deleted ON inv_other_outbound_detail(tenant_id, is_deleted);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================

-- 外键关联字段（order_id / product_id / warehouse_id / location_id）
CREATE INDEX idx_inv_other_outbound_detail_order ON inv_other_outbound_detail(order_id);
CREATE INDEX idx_inv_other_outbound_detail_product ON inv_other_outbound_detail(product_id);
CREATE INDEX idx_inv_other_outbound_detail_warehouse ON inv_other_outbound_detail(warehouse_id);
CREATE INDEX idx_inv_other_outbound_detail_location ON inv_other_outbound_detail(location_id);

-- 行号查询
CREATE INDEX idx_inv_other_outbound_detail_line_no ON inv_other_outbound_detail(line_no);

-- 批次号查询
CREATE INDEX idx_inv_other_outbound_detail_batch_no ON inv_other_outbound_detail(batch_no);

-- 商品快照字段查询（多租户）
CREATE INDEX idx_inv_other_outbound_detail_tenant_product_code ON inv_other_outbound_detail(tenant_id, product_code);
CREATE INDEX idx_inv_other_outbound_detail_tenant_product_name ON inv_other_outbound_detail(tenant_id, product_name);

-- ============================================================
-- 5. 通用字段索引
-- ============================================================

-- 创建人/修改人查询
CREATE INDEX idx_inv_other_outbound_detail_created_by ON inv_other_outbound_detail(created_by);
CREATE INDEX idx_inv_other_outbound_detail_updated_by ON inv_other_outbound_detail(updated_by);

-- 数据权限查询
CREATE INDEX idx_inv_other_outbound_detail_owner_dept ON inv_other_outbound_detail(owner_dept_id);
CREATE INDEX idx_inv_other_outbound_detail_owner ON inv_other_outbound_detail(owner_id);

-- 租户+创建时间范围查询
CREATE INDEX idx_inv_other_outbound_detail_created_at ON inv_other_outbound_detail(tenant_id, created_at);
