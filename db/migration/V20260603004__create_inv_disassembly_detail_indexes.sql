-- ============================================================
-- Flyway Migration Script
-- Version: V20260603004
-- Description: inv_disassembly_detail拆卸主从表索引与约束
-- Author: AI
-- Date: 2026-06-03
-- Task: P0-003-007-012-001-002
-- ============================================================

-- ============================================================
-- 一、inv_disassembly 拆卸主表
-- ============================================================

-- 1.1 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
ALTER TABLE inv_disassembly RENAME CONSTRAINT inv_disassembly_pkey TO pk_inv_disassembly;

-- 1.2 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
CREATE UNIQUE INDEX uk_inv_disassembly_order_no ON inv_disassembly(order_no) WHERE is_deleted = false;

-- 1.3 多租户联合索引（tenant_id为首列）
CREATE INDEX idx_inv_disassembly_tenant_order_no ON inv_disassembly(tenant_id, order_no);
CREATE INDEX idx_inv_disassembly_tenant_status ON inv_disassembly(tenant_id, status);
CREATE INDEX idx_inv_disassembly_tenant_deleted ON inv_disassembly(tenant_id, is_deleted);

-- 1.4 业务查询索引

-- 仓库关联查询
CREATE INDEX idx_inv_disassembly_warehouse ON inv_disassembly(warehouse_id);

-- 单据状态筛选
CREATE INDEX idx_inv_disassembly_status ON inv_disassembly(status);

-- 单据日期范围查询
CREATE INDEX idx_inv_disassembly_order_date ON inv_disassembly(order_date);

-- 拆卸日期范围查询
CREATE INDEX idx_inv_disassembly_disassembly_date ON inv_disassembly(disassembly_date);

-- 经办部门/经办人/审核人查询
CREATE INDEX idx_inv_disassembly_handling_dept ON inv_disassembly(handling_dept_id);
CREATE INDEX idx_inv_disassembly_handler ON inv_disassembly(handler_id);
CREATE INDEX idx_inv_disassembly_approver ON inv_disassembly(approver_id);

-- 1.5 通用字段索引

-- 创建人/修改人查询
CREATE INDEX idx_inv_disassembly_created_by ON inv_disassembly(created_by);
CREATE INDEX idx_inv_disassembly_updated_by ON inv_disassembly(updated_by);

-- 数据权限查询
CREATE INDEX idx_inv_disassembly_owner_dept ON inv_disassembly(owner_dept_id);
CREATE INDEX idx_inv_disassembly_owner ON inv_disassembly(owner_id);

-- 租户+创建时间范围查询
CREATE INDEX idx_inv_disassembly_created_at ON inv_disassembly(tenant_id, created_at);


-- ============================================================
-- 二、inv_disassembly_detail 拆卸从表
-- ============================================================

-- 2.1 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
ALTER TABLE inv_disassembly_detail RENAME CONSTRAINT inv_disassembly_detail_pkey TO pk_inv_disassembly_detail;

-- 2.2 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
--     code字段在is_deleted=false范围内保证唯一
CREATE UNIQUE INDEX uk_inv_disassembly_detail_code ON inv_disassembly_detail(code) WHERE is_deleted = false;

-- 2.3 多租户联合索引（tenant_id为首列）
CREATE INDEX idx_inv_disassembly_detail_tenant_code ON inv_disassembly_detail(tenant_id, code);
CREATE INDEX idx_inv_disassembly_detail_tenant_deleted ON inv_disassembly_detail(tenant_id, is_deleted);

-- 2.4 业务查询索引

-- 外键关联字段（order_id / product_id / warehouse_id / location_id）
CREATE INDEX idx_inv_disassembly_detail_order ON inv_disassembly_detail(order_id);
CREATE INDEX idx_inv_disassembly_detail_product ON inv_disassembly_detail(product_id);
CREATE INDEX idx_inv_disassembly_detail_warehouse ON inv_disassembly_detail(warehouse_id);
CREATE INDEX idx_inv_disassembly_detail_location ON inv_disassembly_detail(location_id);

-- 行号查询
CREATE INDEX idx_inv_disassembly_detail_line_no ON inv_disassembly_detail(line_no);

-- 批次号查询
CREATE INDEX idx_inv_disassembly_detail_batch_no ON inv_disassembly_detail(batch_no);

-- 商品快照字段查询（多租户）
CREATE INDEX idx_inv_disassembly_detail_tenant_product_code ON inv_disassembly_detail(tenant_id, product_code);
CREATE INDEX idx_inv_disassembly_detail_tenant_product_name ON inv_disassembly_detail(tenant_id, product_name);

-- 2.5 通用字段索引

-- 创建人/修改人查询
CREATE INDEX idx_inv_disassembly_detail_created_by ON inv_disassembly_detail(created_by);
CREATE INDEX idx_inv_disassembly_detail_updated_by ON inv_disassembly_detail(updated_by);

-- 数据权限查询
CREATE INDEX idx_inv_disassembly_detail_owner_dept ON inv_disassembly_detail(owner_dept_id);
CREATE INDEX idx_inv_disassembly_detail_owner ON inv_disassembly_detail(owner_id);

-- 租户+创建时间范围查询
CREATE INDEX idx_inv_disassembly_detail_created_at ON inv_disassembly_detail(tenant_id, created_at);
