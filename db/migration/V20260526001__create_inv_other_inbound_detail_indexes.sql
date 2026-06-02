-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: inv_other_inbound_detail其他入库主从表索引与约束
-- Author: AI
-- Date: 2026-06-02
-- Task: P0-003-007-006-001-002
-- ============================================================

-- ============================================================
-- 一、inv_other_inbound 其他入库主表
-- ============================================================

-- 1.1 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
ALTER TABLE inv_other_inbound RENAME CONSTRAINT inv_other_inbound_pkey TO pk_inv_other_inbound;

-- 1.2 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
CREATE UNIQUE INDEX uk_inv_other_inbound_order_no ON inv_other_inbound(order_no) WHERE is_deleted = false;

-- 1.3 多租户联合索引（tenant_id为首列）
CREATE INDEX idx_inv_other_inbound_tenant_order_no ON inv_other_inbound(tenant_id, order_no);
CREATE INDEX idx_inv_other_inbound_tenant_status ON inv_other_inbound(tenant_id, status);
CREATE INDEX idx_inv_other_inbound_tenant_deleted ON inv_other_inbound(tenant_id, is_deleted);

-- 1.4 业务查询索引

-- 外键关联字段（warehouse_id）
CREATE INDEX idx_inv_other_inbound_warehouse ON inv_other_inbound(warehouse_id);

-- 来源类型
CREATE INDEX idx_inv_other_inbound_source_type ON inv_other_inbound(source_type);

-- 单据状态筛选
CREATE INDEX idx_inv_other_inbound_status ON inv_other_inbound(status);

-- 单据日期范围查询
CREATE INDEX idx_inv_other_inbound_order_date ON inv_other_inbound(order_date);

-- 入库日期范围查询
CREATE INDEX idx_inv_other_inbound_inbound_date ON inv_other_inbound(inbound_date);

-- 经办部门/经办人/审核人查询
CREATE INDEX idx_inv_other_inbound_handling_dept ON inv_other_inbound(handling_dept_id);
CREATE INDEX idx_inv_other_inbound_handler ON inv_other_inbound(handler_id);
CREATE INDEX idx_inv_other_inbound_approver ON inv_other_inbound(approver_id);

-- 单据编号+来源单号精确查询
CREATE INDEX idx_inv_other_inbound_source_order_no ON inv_other_inbound(source_order_no);

-- 1.5 通用字段索引

-- 创建人/修改人查询
CREATE INDEX idx_inv_other_inbound_created_by ON inv_other_inbound(created_by);
CREATE INDEX idx_inv_other_inbound_updated_by ON inv_other_inbound(updated_by);

-- 数据权限查询
CREATE INDEX idx_inv_other_inbound_owner_dept ON inv_other_inbound(owner_dept_id);
CREATE INDEX idx_inv_other_inbound_owner ON inv_other_inbound(owner_id);

-- 租户+创建时间范围查询
CREATE INDEX idx_inv_other_inbound_created_at ON inv_other_inbound(tenant_id, created_at);


-- ============================================================
-- 二、inv_other_inbound_detail 其他入库从表
-- ============================================================

-- 2.1 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
ALTER TABLE inv_other_inbound_detail RENAME CONSTRAINT inv_other_inbound_detail_pkey TO pk_inv_other_inbound_detail;

-- 2.2 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
--     code字段在is_deleted=false范围内保证唯一
CREATE UNIQUE INDEX uk_inv_other_inbound_detail_code ON inv_other_inbound_detail(code) WHERE is_deleted = false;

-- 2.3 多租户联合索引（tenant_id为首列）
CREATE INDEX idx_inv_other_inbound_detail_tenant_code ON inv_other_inbound_detail(tenant_id, code);
CREATE INDEX idx_inv_other_inbound_detail_tenant_deleted ON inv_other_inbound_detail(tenant_id, is_deleted);

-- 2.4 业务查询索引

-- 外键关联字段（order_id / product_id / warehouse_id / location_id）
CREATE INDEX idx_inv_other_inbound_detail_order ON inv_other_inbound_detail(order_id);
CREATE INDEX idx_inv_other_inbound_detail_product ON inv_other_inbound_detail(product_id);
CREATE INDEX idx_inv_other_inbound_detail_warehouse ON inv_other_inbound_detail(warehouse_id);
CREATE INDEX idx_inv_other_inbound_detail_location ON inv_other_inbound_detail(location_id);

-- 行号查询
CREATE INDEX idx_inv_other_inbound_detail_line_no ON inv_other_inbound_detail(line_no);

-- 批次号查询
CREATE INDEX idx_inv_other_inbound_detail_batch_no ON inv_other_inbound_detail(batch_no);

-- 商品快照字段查询（多租户）
CREATE INDEX idx_inv_other_inbound_detail_tenant_product_code ON inv_other_inbound_detail(tenant_id, product_code);
CREATE INDEX idx_inv_other_inbound_detail_tenant_product_name ON inv_other_inbound_detail(tenant_id, product_name);

-- 2.5 通用字段索引

-- 创建人/修改人查询
CREATE INDEX idx_inv_other_inbound_detail_created_by ON inv_other_inbound_detail(created_by);
CREATE INDEX idx_inv_other_inbound_detail_updated_by ON inv_other_inbound_detail(updated_by);

-- 数据权限查询
CREATE INDEX idx_inv_other_inbound_detail_owner_dept ON inv_other_inbound_detail(owner_dept_id);
CREATE INDEX idx_inv_other_inbound_detail_owner ON inv_other_inbound_detail(owner_id);

-- 租户+创建时间范围查询
CREATE INDEX idx_inv_other_inbound_detail_created_at ON inv_other_inbound_detail(tenant_id, created_at);
