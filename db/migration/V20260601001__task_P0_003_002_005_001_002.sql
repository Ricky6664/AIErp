-- ============================================================
-- Flyway Migration Script
-- Version: V20260601001
-- Description: 移动端菜单表索引与约束（sys_mobile_menu）
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-002-005-001-002
-- ============================================================

-- ============================================================
-- 1. 普通索引（租户隔离首列）
-- ============================================================

-- 租户隔离基础索引
CREATE INDEX IF NOT EXISTS idx_sys_mobile_menu_tenant_id
    ON sys_mobile_menu(tenant_id);

-- 树形层级查询：tenant_id + parent_id
CREATE INDEX IF NOT EXISTS idx_sys_mobile_menu_tenant_id_parent_id
    ON sys_mobile_menu(tenant_id, parent_id);

-- 菜单类型过滤：tenant_id + menu_type
CREATE INDEX IF NOT EXISTS idx_sys_mobile_menu_tenant_id_menu_type
    ON sys_mobile_menu(tenant_id, menu_type);

-- 排序查询：tenant_id + sort_order
CREATE INDEX IF NOT EXISTS idx_sys_mobile_menu_tenant_id_sort_order
    ON sys_mobile_menu(tenant_id, sort_order);

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false）
-- ============================================================

-- 权限标识码唯一（租户内未删除记录唯一）
CREATE UNIQUE INDEX IF NOT EXISTS uk_sys_mobile_menu_tenant_id_permission_code_active
    ON sys_mobile_menu(tenant_id, permission_code)
    WHERE is_deleted = false;

-- ============================================================
-- 3. 注释
-- ============================================================

COMMENT ON INDEX idx_sys_mobile_menu_tenant_id IS '租户隔离索引';
COMMENT ON INDEX idx_sys_mobile_menu_tenant_id_parent_id IS '树形层级查询联合索引';
COMMENT ON INDEX idx_sys_mobile_menu_tenant_id_menu_type IS '菜单类型过滤联合索引';
COMMENT ON INDEX idx_sys_mobile_menu_tenant_id_sort_order IS '排序联合索引';
COMMENT ON INDEX uk_sys_mobile_menu_tenant_id_permission_code_active IS '权限标识码部分唯一索引（仅未删除）';
