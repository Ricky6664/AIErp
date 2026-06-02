-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: inv_transfer_detail调拨主从表索引与约束
-- Author: AI
-- Date: 2026-06-02
-- Task: P0-003-007-008-001-002
-- ============================================================

-- ============================================================
-- 一、inv_transfer 调拨主表
-- ============================================================

-- 1.1 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
ALTER TABLE inv_transfer RENAME CONSTRAINT inv_transfer_pkey TO pk_inv_transfer;

-- 1.2 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
CREATE UNIQUE INDEX uk_inv_transfer_order_no ON inv_transfer(order_no) WHERE is_deleted = false;

-- 1.3 多租户联合索引（tenant_id为首列）
CREATE INDEX idx_inv_transfer_tenant_order_no ON inv_transfer(tenant_id, order_no);
CREATE INDEX idx_inv_transfer_tenant_status ON inv_transfer(tenant_id, status);
CREATE INDEX idx_inv_transfer_tenant_deleted ON inv_transfer(tenant_id, is_deleted);

-- 1.4 业务查询索引

-- 外键关联字段（调出/调入仓库）
CREATE INDEX idx_inv_transfer_from_warehouse ON inv_transfer(from_warehouse_id);
CREATE INDEX idx_inv_transfer_to_warehouse ON inv_transfer(to_warehouse_id);

-- 单据状态筛选
CREATE INDEX idx_inv_transfer_status ON inv_transfer(status);

-- 单据日期范围查询
CREATE INDEX idx_inv_transfer_order_date ON inv_transfer(order_date);

-- 调拨日期范围查询
CREATE INDEX idx_inv_transfer_transfer_date ON inv_transfer(transfer_date);

-- 经办部门/经办人/审核人查询
CREATE INDEX idx_inv_transfer_handling_dept ON inv_transfer(handling_dept_id);
CREATE INDEX idx_inv_transfer_handler ON inv_transfer(handler_id);
CREATE INDEX idx_inv_transfer_approver ON inv_transfer(approver_id);

-- 1.5 通用字段索引

-- 创建人/修改人查询
CREATE INDEX idx_inv_transfer_created_by ON inv_transfer(created_by);
CREATE INDEX idx_inv_transfer_updated_by ON inv_transfer(updated_by);

-- 数据权限查询
CREATE INDEX idx_inv_transfer_owner_dept ON inv_transfer(owner_dept_id);
CREATE INDEX idx_inv_transfer_owner ON inv_transfer(owner_id);

-- 租户+创建时间范围查询
CREATE INDEX idx_inv_transfer_created_at ON inv_transfer(tenant_id, created_at);


-- ============================================================
-- 二、inv_transfer_detail 调拨从表
-- ============================================================

-- 2.1 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
ALTER TABLE inv_transfer_detail RENAME CONSTRAINT inv_transfer_detail_pkey TO pk_inv_transfer_detail;

-- 2.2 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
--     code字段在is_deleted=false范围内保证唯一
CREATE UNIQUE INDEX uk_inv_transfer_detail_code ON inv_transfer_detail(code) WHERE is_deleted = false;

-- 2.3 多租户联合索引（tenant_id为首列）
CREATE INDEX idx_inv_transfer_detail_tenant_code ON inv_transfer_detail(tenant_id, code);
CREATE INDEX idx_inv_transfer_detail_tenant_deleted ON inv_transfer_detail(tenant_id, is_deleted);

-- 2.4 业务查询索引

-- 外键关联字段（order_id / product_id / 调出调入仓库 / 调出调入库位）
CREATE INDEX idx_inv_transfer_detail_order ON inv_transfer_detail(order_id);
CREATE INDEX idx_inv_transfer_detail_product ON inv_transfer_detail(product_id);
CREATE INDEX idx_inv_transfer_detail_from_warehouse ON inv_transfer_detail(from_warehouse_id);
CREATE INDEX idx_inv_transfer_detail_to_warehouse ON inv_transfer_detail(to_warehouse_id);
CREATE INDEX idx_inv_transfer_detail_from_location ON inv_transfer_detail(from_location_id);
CREATE INDEX idx_inv_transfer_detail_to_location ON inv_transfer_detail(to_location_id);

-- 行号查询
CREATE INDEX idx_inv_transfer_detail_line_no ON inv_transfer_detail(line_no);

-- 批次号查询
CREATE INDEX idx_inv_transfer_detail_batch_no ON inv_transfer_detail(batch_no);

-- 商品快照字段查询（多租户）
CREATE INDEX idx_inv_transfer_detail_tenant_product_code ON inv_transfer_detail(tenant_id, product_code);
CREATE INDEX idx_inv_transfer_detail_tenant_product_name ON inv_transfer_detail(tenant_id, product_name);

-- 2.5 通用字段索引

-- 创建人/修改人查询
CREATE INDEX idx_inv_transfer_detail_created_by ON inv_transfer_detail(created_by);
CREATE INDEX idx_inv_transfer_detail_updated_by ON inv_transfer_detail(updated_by);

-- 数据权限查询
CREATE INDEX idx_inv_transfer_detail_owner_dept ON inv_transfer_detail(owner_dept_id);
CREATE INDEX idx_inv_transfer_detail_owner ON inv_transfer_detail(owner_id);

-- 租户+创建时间范围查询
CREATE INDEX idx_inv_transfer_detail_created_at ON inv_transfer_detail(tenant_id, created_at);
