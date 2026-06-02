-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: inv_warehouse仓库定义表索引与约束
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-007-001-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
-- ============================================================
ALTER TABLE inv_warehouse RENAME CONSTRAINT inv_warehouse_pkey TO pk_inv_warehouse;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_inv_warehouse_code ON inv_warehouse(warehouse_code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_inv_warehouse_tenant_code ON inv_warehouse(tenant_id, warehouse_code);
CREATE INDEX idx_inv_warehouse_tenant_status ON inv_warehouse(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================

-- 公司关联查询
CREATE INDEX idx_inv_warehouse_company ON inv_warehouse(company_id);

-- 仓库类型筛选
CREATE INDEX idx_inv_warehouse_type ON inv_warehouse(warehouse_type);

-- 状态筛选
CREATE INDEX idx_inv_warehouse_status ON inv_warehouse(status);

-- 仓库名称查询
CREATE INDEX idx_inv_warehouse_name ON inv_warehouse(warehouse_name);

-- ============================================================
-- 5. 通用字段索引
-- ============================================================

-- 创建人/修改人查询
CREATE INDEX idx_inv_warehouse_created_by ON inv_warehouse(created_by);
CREATE INDEX idx_inv_warehouse_updated_by ON inv_warehouse(updated_by);

-- 数据权限查询
CREATE INDEX idx_inv_warehouse_owner_dept ON inv_warehouse(owner_dept_id);
CREATE INDEX idx_inv_warehouse_owner ON inv_warehouse(owner_id);

-- 租户+创建时间范围查询
CREATE INDEX idx_inv_warehouse_created_at ON inv_warehouse(tenant_id, created_at);
