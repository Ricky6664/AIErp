-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: inv_overflow_detail报溢主从表索引与约束
-- Author: AI
-- Date: 2026-06-03
-- Task: P0-003-007-010-001-002
-- ============================================================

-- ============================================================
-- 一、inv_overflow 报溢主表
-- ============================================================

-- 1.1 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
ALTER TABLE inv_overflow RENAME CONSTRAINT inv_overflow_pkey TO pk_inv_overflow;

-- 1.2 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
CREATE UNIQUE INDEX uk_inv_overflow_order_no ON inv_overflow(order_no) WHERE is_deleted = false;

-- 1.3 多租户联合索引（tenant_id为首列）
CREATE INDEX idx_inv_overflow_tenant_order_no ON inv_overflow(tenant_id, order_no);
CREATE INDEX idx_inv_overflow_tenant_status ON inv_overflow(tenant_id, status);
CREATE INDEX idx_inv_overflow_tenant_deleted ON inv_overflow(tenant_id, is_deleted);

-- 1.4 业务查询索引

-- 仓库关联查询
CREATE INDEX idx_inv_overflow_warehouse ON inv_overflow(warehouse_id);

-- 单据状态筛选
CREATE INDEX idx_inv_overflow_status ON inv_overflow(status);

-- 单据日期范围查询
CREATE INDEX idx_inv_overflow_order_date ON inv_overflow(order_date);

-- 报溢日期范围查询
CREATE INDEX idx_inv_overflow_overflow_date ON inv_overflow(overflow_date);

-- 经办部门/经办人/审核人查询
CREATE INDEX idx_inv_overflow_handling_dept ON inv_overflow(handling_dept_id);
CREATE INDEX idx_inv_overflow_handler ON inv_overflow(handler_id);
CREATE INDEX idx_inv_overflow_approver ON inv_overflow(approver_id);

-- 1.5 通用字段索引

-- 创建人/修改人查询
CREATE INDEX idx_inv_overflow_created_by ON inv_overflow(created_by);
CREATE INDEX idx_inv_overflow_updated_by ON inv_overflow(updated_by);

-- 数据权限查询
CREATE INDEX idx_inv_overflow_owner_dept ON inv_overflow(owner_dept_id);
CREATE INDEX idx_inv_overflow_owner ON inv_overflow(owner_id);

-- 租户+创建时间范围查询
CREATE INDEX idx_inv_overflow_created_at ON inv_overflow(tenant_id, created_at);


-- ============================================================
-- 二、inv_overflow_detail 报溢从表
-- ============================================================

-- 2.1 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
ALTER TABLE inv_overflow_detail RENAME CONSTRAINT inv_overflow_detail_pkey TO pk_inv_overflow_detail;

-- 2.2 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
--     code字段在is_deleted=false范围内保证唯一
CREATE UNIQUE INDEX uk_inv_overflow_detail_code ON inv_overflow_detail(code) WHERE is_deleted = false;

-- 2.3 多租户联合索引（tenant_id为首列）
CREATE INDEX idx_inv_overflow_detail_tenant_code ON inv_overflow_detail(tenant_id, code);
CREATE INDEX idx_inv_overflow_detail_tenant_deleted ON inv_overflow_detail(tenant_id, is_deleted);

-- 2.4 业务查询索引

-- 外键关联字段（order_id / product_id / warehouse_id / location_id）
CREATE INDEX idx_inv_overflow_detail_order ON inv_overflow_detail(order_id);
CREATE INDEX idx_inv_overflow_detail_product ON inv_overflow_detail(product_id);
CREATE INDEX idx_inv_overflow_detail_warehouse ON inv_overflow_detail(warehouse_id);
CREATE INDEX idx_inv_overflow_detail_location ON inv_overflow_detail(location_id);

-- 行号查询
CREATE INDEX idx_inv_overflow_detail_line_no ON inv_overflow_detail(line_no);

-- 批次号查询
CREATE INDEX idx_inv_overflow_detail_batch_no ON inv_overflow_detail(batch_no);

-- 商品快照字段查询（多租户）
CREATE INDEX idx_inv_overflow_detail_tenant_product_code ON inv_overflow_detail(tenant_id, product_code);
CREATE INDEX idx_inv_overflow_detail_tenant_product_name ON inv_overflow_detail(tenant_id, product_name);

-- 2.5 通用字段索引

-- 创建人/修改人查询
CREATE INDEX idx_inv_overflow_detail_created_by ON inv_overflow_detail(created_by);
CREATE INDEX idx_inv_overflow_detail_updated_by ON inv_overflow_detail(updated_by);

-- 数据权限查询
CREATE INDEX idx_inv_overflow_detail_owner_dept ON inv_overflow_detail(owner_dept_id);
CREATE INDEX idx_inv_overflow_detail_owner ON inv_overflow_detail(owner_id);

-- 租户+创建时间范围查询
CREATE INDEX idx_inv_overflow_detail_created_at ON inv_overflow_detail(tenant_id, created_at);
