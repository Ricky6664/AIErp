-- ============================================================
-- Flyway Migration Script
-- Version: V20260602001
-- Description: inv_location库位管理表索引与约束
-- Author: AI Generated
-- Date: 2026-06-02
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE inv_location RENAME CONSTRAINT inv_location_pkey TO pk_inv_location;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_inv_location_code ON inv_location(location_code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_inv_location_tenant_code ON inv_location(tenant_id, location_code);
CREATE INDEX idx_inv_location_tenant_status ON inv_location(tenant_id, status);
CREATE INDEX idx_inv_location_tenant_deleted ON inv_location(tenant_id, is_deleted);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
-- 外键关联字段（warehouse_id）
CREATE INDEX idx_inv_location_warehouse ON inv_location(warehouse_id);

-- 状态筛选
CREATE INDEX idx_inv_location_status ON inv_location(status);

-- 库区查询（多租户）
CREATE INDEX idx_inv_location_tenant_zone ON inv_location(tenant_id, zone);

-- 操作人查询
CREATE INDEX idx_inv_location_created_by ON inv_location(created_by);
CREATE INDEX idx_inv_location_updated_by ON inv_location(updated_by);

-- 数据权限字段
CREATE INDEX idx_inv_location_owner_dept ON inv_location(owner_dept_id);
CREATE INDEX idx_inv_location_owner ON inv_location(owner_id);

-- 日期范围查询（多租户）
CREATE INDEX idx_inv_location_created_at ON inv_location(tenant_id, created_at);
