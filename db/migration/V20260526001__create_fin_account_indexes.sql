-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: fin_account会计科目表索引与约束
-- Author: AI Generated
-- Date: 2026-06-03
-- Task: P0-003-008-003-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名为pk_前缀）
-- ============================================================
ALTER TABLE fin_account RENAME CONSTRAINT fin_account_pkey TO pk_fin_account;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_fin_account_code ON fin_account(account_code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_fin_account_tenant_code ON fin_account(tenant_id, account_code);
CREATE INDEX idx_fin_account_tenant_type ON fin_account(tenant_id, account_type);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================

-- 树形结构/外键关联查询
CREATE INDEX idx_fin_account_parent ON fin_account(parent_id);

-- 科目类型筛选
CREATE INDEX idx_fin_account_type ON fin_account(account_type);

-- 余额方向筛选
CREATE INDEX idx_fin_account_balance_direction ON fin_account(balance_direction);

-- 科目名称查询
CREATE INDEX idx_fin_account_name ON fin_account(account_name);

-- ============================================================
-- 5. 通用字段索引
-- ============================================================

-- 创建人/修改人查询
CREATE INDEX idx_fin_account_created_by ON fin_account(created_by);
CREATE INDEX idx_fin_account_updated_by ON fin_account(updated_by);

-- 数据权限查询
CREATE INDEX idx_fin_account_owner_dept ON fin_account(owner_dept_id);
CREATE INDEX idx_fin_account_owner ON fin_account(owner_id);

-- 租户+创建时间范围查询
CREATE INDEX idx_fin_account_created_at ON fin_account(tenant_id, created_at);
