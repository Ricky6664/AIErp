-- ============================================================
-- Flyway Migration Script
-- Version: V20260531005
-- Description: 系统核心表索引与约束（sys_user/sys_role/sys_menu/
--   sys_user_role/sys_user_dept/sys_role_menu/
--   sys_role_data_scope/sys_role_field_permission/
--   sys_user_group/sys_user_group_member）
-- Author: AI Generated
-- Date: 2026-05-31
-- ============================================================

-- ============================================================
-- 1. sys_user 用户表 索引与约束
-- ============================================================
CREATE UNIQUE INDEX uk_sys_user_username_active ON sys_user(tenant_id, username) WHERE is_deleted = false;
CREATE UNIQUE INDEX uk_sys_user_mobile_active ON sys_user(tenant_id, mobile) WHERE is_deleted = false AND mobile IS NOT NULL;
CREATE UNIQUE INDEX uk_sys_user_email_active ON sys_user(tenant_id, email) WHERE is_deleted = false AND email IS NOT NULL;
CREATE INDEX idx_sys_user_tenant_employee ON sys_user(tenant_id, employee_id);
CREATE INDEX idx_sys_user_tenant_status ON sys_user(tenant_id, status);
CREATE INDEX idx_sys_user_tenant_id ON sys_user(tenant_id);

-- ============================================================
-- 2. sys_role 角色表 索引与约束
-- ============================================================
CREATE UNIQUE INDEX uk_sys_role_code_active ON sys_role(tenant_id, role_code) WHERE is_deleted = false;
CREATE INDEX idx_sys_role_tenant_id ON sys_role(tenant_id);

-- ============================================================
-- 3. sys_menu 菜单表 索引与约束
-- ============================================================
CREATE UNIQUE INDEX uk_sys_menu_permission_active ON sys_menu(tenant_id, permission_code) WHERE is_deleted = false AND permission_code IS NOT NULL;
CREATE INDEX idx_sys_menu_tenant_parent ON sys_menu(tenant_id, parent_id);
CREATE INDEX idx_sys_menu_tenant_type ON sys_menu(tenant_id, menu_type);
CREATE INDEX idx_sys_menu_tenant_id ON sys_menu(tenant_id);

-- ============================================================
-- 4. sys_user_role 用户角色关联表 索引与约束
-- ============================================================
CREATE UNIQUE INDEX uk_sys_user_role_user_role_active ON sys_user_role(tenant_id, user_id, role_id) WHERE is_deleted = false;
CREATE INDEX idx_sys_user_role_tenant_user ON sys_user_role(tenant_id, user_id);
CREATE INDEX idx_sys_user_role_tenant_role ON sys_user_role(tenant_id, role_id);

-- ============================================================
-- 5. sys_user_dept 用户部门关联表 索引与约束
-- ============================================================
CREATE UNIQUE INDEX uk_sys_user_dept_user_dept_active ON sys_user_dept(tenant_id, user_id, dept_id) WHERE is_deleted = false;
CREATE INDEX idx_sys_user_dept_tenant_user ON sys_user_dept(tenant_id, user_id);
CREATE INDEX idx_sys_user_dept_tenant_dept ON sys_user_dept(tenant_id, dept_id);

-- ============================================================
-- 6. sys_role_menu 角色菜单关联表 索引与约束
-- ============================================================
CREATE UNIQUE INDEX uk_sys_role_menu_role_menu_active ON sys_role_menu(tenant_id, role_id, menu_id) WHERE is_deleted = false;
CREATE INDEX idx_sys_role_menu_tenant_role ON sys_role_menu(tenant_id, role_id);
CREATE INDEX idx_sys_role_menu_tenant_menu ON sys_role_menu(tenant_id, menu_id);

-- ============================================================
-- 7. sys_role_data_scope 角色数据权限范围表 索引与约束
-- ============================================================
CREATE UNIQUE INDEX uk_sys_role_data_scope_role_type_active ON sys_role_data_scope(tenant_id, role_id, scope_type) WHERE is_deleted = false;
CREATE INDEX idx_sys_role_data_scope_tenant_role ON sys_role_data_scope(tenant_id, role_id);

-- ============================================================
-- 8. sys_role_field_permission 角色字段权限表 索引与约束
-- ============================================================
CREATE UNIQUE INDEX uk_sys_role_field_perm_active ON sys_role_field_permission(tenant_id, role_id, table_name, field_name) WHERE is_deleted = false;
CREATE INDEX idx_sys_role_field_perm_tenant_role ON sys_role_field_permission(tenant_id, role_id);
CREATE INDEX idx_sys_role_field_perm_tenant_table ON sys_role_field_permission(tenant_id, table_name);

-- ============================================================
-- 9. sys_user_group 用户组表 索引与约束
-- ============================================================
CREATE UNIQUE INDEX uk_sys_user_group_code_active ON sys_user_group(tenant_id, group_code) WHERE is_deleted = false;
CREATE INDEX idx_sys_user_group_tenant_id ON sys_user_group(tenant_id);

-- ============================================================
-- 10. sys_user_group_member 用户组成员表 索引与约束
-- ============================================================
CREATE UNIQUE INDEX uk_sys_user_group_member_active ON sys_user_group_member(tenant_id, group_id, user_id) WHERE is_deleted = false;
CREATE INDEX idx_sys_user_group_member_tenant_group ON sys_user_group_member(tenant_id, group_id);
CREATE INDEX idx_sys_user_group_member_tenant_user ON sys_user_group_member(tenant_id, user_id);
