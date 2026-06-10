-- ============================================================
-- Flyway Migration Script
-- Version: V20260603002
-- Description: inv_assembly_detail组装主从表索引与约束
-- Author: AI
-- Date: 2026-06-03
-- Task: P0-003-007-011-001-002
-- ============================================================

-- ============================================================
-- 一、inv_assembly 组装主表
-- ============================================================

-- 1.1 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
ALTER TABLE inv_assembly RENAME CONSTRAINT inv_assembly_pkey TO pk_inv_assembly;

-- 1.2 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
CREATE UNIQUE INDEX uk_inv_assembly_order_no ON inv_assembly(order_no) WHERE is_deleted = false;

-- 1.3 多租户联合索引（tenant_id为首列）
CREATE INDEX idx_inv_assembly_tenant_order_no ON inv_assembly(tenant_id, order_no);
CREATE INDEX idx_inv_assembly_tenant_status ON inv_assembly(tenant_id, status);
CREATE INDEX idx_inv_assembly_tenant_deleted ON inv_assembly(tenant_id, is_deleted);

-- 1.4 业务查询索引

-- 仓库关联查询
CREATE INDEX idx_inv_assembly_warehouse ON inv_assembly(warehouse_id);

-- 单据状态筛选
CREATE INDEX idx_inv_assembly_status ON inv_assembly(status);

-- 单据日期范围查询
CREATE INDEX idx_inv_assembly_order_date ON inv_assembly(order_date);

-- 组装日期范围查询
CREATE INDEX idx_inv_assembly_assembly_date ON inv_assembly(assembly_date);

-- 经办部门/经办人/审核人查询
CREATE INDEX idx_inv_assembly_handling_dept ON inv_assembly(handling_dept_id);
CREATE INDEX idx_inv_assembly_handler ON inv_assembly(handler_id);
CREATE INDEX idx_inv_assembly_approver ON inv_assembly(approver_id);

-- 1.5 通用字段索引

-- 创建人/修改人查询
CREATE INDEX idx_inv_assembly_created_by ON inv_assembly(created_by);
CREATE INDEX idx_inv_assembly_updated_by ON inv_assembly(updated_by);

-- 数据权限查询
CREATE INDEX idx_inv_assembly_owner_dept ON inv_assembly(owner_dept_id);
CREATE INDEX idx_inv_assembly_owner ON inv_assembly(owner_id);

-- 租户+创建时间范围查询
CREATE INDEX idx_inv_assembly_created_at ON inv_assembly(tenant_id, created_at);


-- ============================================================
-- 二、inv_assembly_detail 组装从表
-- ============================================================

-- 2.1 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
ALTER TABLE inv_assembly_detail RENAME CONSTRAINT inv_assembly_detail_pkey TO pk_inv_assembly_detail;

-- 2.2 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
--     code字段在is_deleted=false范围内保证唯一
CREATE UNIQUE INDEX uk_inv_assembly_detail_code ON inv_assembly_detail(code) WHERE is_deleted = false;

-- 2.3 多租户联合索引（tenant_id为首列）
CREATE INDEX idx_inv_assembly_detail_tenant_code ON inv_assembly_detail(tenant_id, code);
CREATE INDEX idx_inv_assembly_detail_tenant_deleted ON inv_assembly_detail(tenant_id, is_deleted);

-- 2.4 业务查询索引

-- 外键关联字段（order_id / product_id / warehouse_id / location_id）
CREATE INDEX idx_inv_assembly_detail_order ON inv_assembly_detail(order_id);
CREATE INDEX idx_inv_assembly_detail_product ON inv_assembly_detail(product_id);
CREATE INDEX idx_inv_assembly_detail_warehouse ON inv_assembly_detail(warehouse_id);
CREATE INDEX idx_inv_assembly_detail_location ON inv_assembly_detail(location_id);

-- 行号查询
CREATE INDEX idx_inv_assembly_detail_line_no ON inv_assembly_detail(line_no);

-- 批次号查询
CREATE INDEX idx_inv_assembly_detail_batch_no ON inv_assembly_detail(batch_no);

-- 商品快照字段查询（多租户）
CREATE INDEX idx_inv_assembly_detail_tenant_product_code ON inv_assembly_detail(tenant_id, product_code);
CREATE INDEX idx_inv_assembly_detail_tenant_product_name ON inv_assembly_detail(tenant_id, product_name);

-- 2.5 通用字段索引

-- 创建人/修改人查询
CREATE INDEX idx_inv_assembly_detail_created_by ON inv_assembly_detail(created_by);
CREATE INDEX idx_inv_assembly_detail_updated_by ON inv_assembly_detail(updated_by);

-- 数据权限查询
CREATE INDEX idx_inv_assembly_detail_owner_dept ON inv_assembly_detail(owner_dept_id);
CREATE INDEX idx_inv_assembly_detail_owner ON inv_assembly_detail(owner_id);

-- 租户+创建时间范围查询
CREATE INDEX idx_inv_assembly_detail_created_at ON inv_assembly_detail(tenant_id, created_at);
