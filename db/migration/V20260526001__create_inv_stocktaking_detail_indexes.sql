-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: inv_stocktaking_detail盘点主从表索引与约束
-- Author: AI
-- Date: 2026-06-02
-- Task: P0-003-007-007-001-002
-- ============================================================

-- ============================================================
-- 一、inv_stocktaking 盘点主表
-- ============================================================

-- 1.1 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
ALTER TABLE inv_stocktaking RENAME CONSTRAINT inv_stocktaking_pkey TO pk_inv_stocktaking;

-- 1.2 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
CREATE UNIQUE INDEX uk_inv_stocktaking_order_no ON inv_stocktaking(order_no) WHERE is_deleted = false;

-- 1.3 多租户联合索引（tenant_id为首列）
CREATE INDEX idx_inv_stocktaking_tenant_order_no ON inv_stocktaking(tenant_id, order_no);
CREATE INDEX idx_inv_stocktaking_tenant_status ON inv_stocktaking(tenant_id, status);
CREATE INDEX idx_inv_stocktaking_tenant_deleted ON inv_stocktaking(tenant_id, is_deleted);

-- 1.4 业务查询索引

-- 外键关联字段（warehouse_id）
CREATE INDEX idx_inv_stocktaking_warehouse ON inv_stocktaking(warehouse_id);

-- 单据状态筛选
CREATE INDEX idx_inv_stocktaking_status ON inv_stocktaking(status);

-- 单据日期范围查询
CREATE INDEX idx_inv_stocktaking_order_date ON inv_stocktaking(order_date);

-- 盘点日期范围查询
CREATE INDEX idx_inv_stocktaking_stocktaking_date ON inv_stocktaking(stocktaking_date);

-- 经办部门/经办人/审核人查询
CREATE INDEX idx_inv_stocktaking_handling_dept ON inv_stocktaking(handling_dept_id);
CREATE INDEX idx_inv_stocktaking_handler ON inv_stocktaking(handler_id);
CREATE INDEX idx_inv_stocktaking_approver ON inv_stocktaking(approver_id);

-- 1.5 通用字段索引

-- 创建人/修改人查询
CREATE INDEX idx_inv_stocktaking_created_by ON inv_stocktaking(created_by);
CREATE INDEX idx_inv_stocktaking_updated_by ON inv_stocktaking(updated_by);

-- 数据权限查询
CREATE INDEX idx_inv_stocktaking_owner_dept ON inv_stocktaking(owner_dept_id);
CREATE INDEX idx_inv_stocktaking_owner ON inv_stocktaking(owner_id);

-- 租户+创建时间范围查询
CREATE INDEX idx_inv_stocktaking_created_at ON inv_stocktaking(tenant_id, created_at);


-- ============================================================
-- 二、inv_stocktaking_detail 盘点从表
-- ============================================================

-- 2.1 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
ALTER TABLE inv_stocktaking_detail RENAME CONSTRAINT inv_stocktaking_detail_pkey TO pk_inv_stocktaking_detail;

-- 2.2 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
--     code字段在is_deleted=false范围内保证唯一
CREATE UNIQUE INDEX uk_inv_stocktaking_detail_code ON inv_stocktaking_detail(code) WHERE is_deleted = false;

-- 2.3 多租户联合索引（tenant_id为首列）
CREATE INDEX idx_inv_stocktaking_detail_tenant_code ON inv_stocktaking_detail(tenant_id, code);
CREATE INDEX idx_inv_stocktaking_detail_tenant_deleted ON inv_stocktaking_detail(tenant_id, is_deleted);

-- 2.4 业务查询索引

-- 外键关联字段（order_id / product_id / warehouse_id / location_id）
CREATE INDEX idx_inv_stocktaking_detail_order ON inv_stocktaking_detail(order_id);
CREATE INDEX idx_inv_stocktaking_detail_product ON inv_stocktaking_detail(product_id);
CREATE INDEX idx_inv_stocktaking_detail_warehouse ON inv_stocktaking_detail(warehouse_id);
CREATE INDEX idx_inv_stocktaking_detail_location ON inv_stocktaking_detail(location_id);

-- 行号查询
CREATE INDEX idx_inv_stocktaking_detail_line_no ON inv_stocktaking_detail(line_no);

-- 批次号查询
CREATE INDEX idx_inv_stocktaking_detail_batch_no ON inv_stocktaking_detail(batch_no);

-- 商品快照字段查询（多租户）
CREATE INDEX idx_inv_stocktaking_detail_tenant_product_code ON inv_stocktaking_detail(tenant_id, product_code);
CREATE INDEX idx_inv_stocktaking_detail_tenant_product_name ON inv_stocktaking_detail(tenant_id, product_name);

-- 2.5 通用字段索引

-- 创建人/修改人查询
CREATE INDEX idx_inv_stocktaking_detail_created_by ON inv_stocktaking_detail(created_by);
CREATE INDEX idx_inv_stocktaking_detail_updated_by ON inv_stocktaking_detail(updated_by);

-- 数据权限查询
CREATE INDEX idx_inv_stocktaking_detail_owner_dept ON inv_stocktaking_detail(owner_dept_id);
CREATE INDEX idx_inv_stocktaking_detail_owner ON inv_stocktaking_detail(owner_id);

-- 租户+创建时间范围查询
CREATE INDEX idx_inv_stocktaking_detail_created_at ON inv_stocktaking_detail(tenant_id, created_at);
