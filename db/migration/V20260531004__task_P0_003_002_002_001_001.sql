-- ============================================================
-- Flyway Migration Script
-- Version: V20260531004
-- Description: 系统核心表建表DDL（sys_user/sys_role/sys_menu/
--   sys_user_role/sys_user_dept/sys_role_menu/
--   sys_role_data_scope/sys_role_field_permission/
--   sys_user_group/sys_user_group_member）
-- Author: AI Generated
-- Date: 2026-05-31
-- ============================================================

-- ============================================================
-- 1. sys_user 用户表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_user (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    username        VARCHAR(50)     NOT NULL,
    password_hash   VARCHAR(200)    NOT NULL,
    employee_id     BIGINT,
    real_name       VARCHAR(50),
    nickname        VARCHAR(50),
    avatar          VARCHAR(500),
    email           VARCHAR(100),
    mobile          VARCHAR(20),
    gender          VARCHAR(10),
    status          VARCHAR(30)     NOT NULL DEFAULT 'active',
    last_login_at   TIMESTAMP,
    last_login_ip   VARCHAR(50),
    pwd_reset_at    TIMESTAMP,
    is_locked       BOOLEAN         NOT NULL DEFAULT FALSE,
    locked_until    TIMESTAMP,
    -- 扩展字段
    ext_str1        VARCHAR(200),
    ext_str2        VARCHAR(200),
    ext_str3        VARCHAR(200),
    ext_str4        VARCHAR(200),
    ext_str5        VARCHAR(200),
    ext_str6        VARCHAR(200),
    ext_str7        VARCHAR(200),
    ext_str8        VARCHAR(200),
    ext_str9        VARCHAR(200),
    ext_str10       VARCHAR(200),
    ext_num1        DECIMAL(18,8),
    ext_num2        DECIMAL(18,8),
    ext_num3        DECIMAL(18,8),
    ext_num4        DECIMAL(18,8),
    ext_num5        DECIMAL(18,8),
    ext_date1       DATE,
    ext_date2       DATE,
    ext_date3       DATE,
    ext_bool1       BOOLEAN,
    ext_bool2       BOOLEAN,
    ext_bool3       BOOLEAN,
    ext_json        JSONB,
    -- 通用必含字段
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    version         INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.id IS '主键ID';
COMMENT ON COLUMN sys_user.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_user.username IS '用户名';
COMMENT ON COLUMN sys_user.password_hash IS '密码哈希（BCrypt加密）';
COMMENT ON COLUMN sys_user.employee_id IS '关联员工ID';
COMMENT ON COLUMN sys_user.real_name IS '真实姓名';
COMMENT ON COLUMN sys_user.nickname IS '昵称';
COMMENT ON COLUMN sys_user.avatar IS '头像URL';
COMMENT ON COLUMN sys_user.email IS '邮箱';
COMMENT ON COLUMN sys_user.mobile IS '手机号';
COMMENT ON COLUMN sys_user.gender IS '性别：male=男/female=女/unknown=未知';
COMMENT ON COLUMN sys_user.status IS '状态：active=正常/disabled=禁用/locked=锁定';
COMMENT ON COLUMN sys_user.last_login_at IS '最后登录时间';
COMMENT ON COLUMN sys_user.last_login_ip IS '最后登录IP';
COMMENT ON COLUMN sys_user.pwd_reset_at IS '密码最后重置时间';
COMMENT ON COLUMN sys_user.is_locked IS '是否锁定';
COMMENT ON COLUMN sys_user.locked_until IS '锁定截止时间';
COMMENT ON COLUMN sys_user.created_at IS '创建时间';
COMMENT ON COLUMN sys_user.updated_at IS '更新时间';
COMMENT ON COLUMN sys_user.created_by IS '创建人ID';
COMMENT ON COLUMN sys_user.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_user.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_user.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_user.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_user.version IS '版本号';

-- ============================================================
-- 2. sys_role 角色表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_role (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    role_code       VARCHAR(50)     NOT NULL,
    role_name       VARCHAR(50)     NOT NULL,
    role_desc       VARCHAR(200),
    data_scope      VARCHAR(30)     NOT NULL DEFAULT 'self',
    is_enabled      BOOLEAN         NOT NULL DEFAULT TRUE,
    sort_order      INT             DEFAULT 0,
    -- 扩展字段
    ext_str1        VARCHAR(200),
    ext_str2        VARCHAR(200),
    ext_str3        VARCHAR(200),
    ext_str4        VARCHAR(200),
    ext_str5        VARCHAR(200),
    ext_str6        VARCHAR(200),
    ext_str7        VARCHAR(200),
    ext_str8        VARCHAR(200),
    ext_str9        VARCHAR(200),
    ext_str10       VARCHAR(200),
    ext_num1        DECIMAL(18,8),
    ext_num2        DECIMAL(18,8),
    ext_num3        DECIMAL(18,8),
    ext_num4        DECIMAL(18,8),
    ext_num5        DECIMAL(18,8),
    ext_date1       DATE,
    ext_date2       DATE,
    ext_date3       DATE,
    ext_bool1       BOOLEAN,
    ext_bool2       BOOLEAN,
    ext_bool3       BOOLEAN,
    ext_json        JSONB,
    -- 通用必含字段
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    version         INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE sys_role IS '角色表';
COMMENT ON COLUMN sys_role.id IS '主键ID';
COMMENT ON COLUMN sys_role.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_role.role_code IS '角色编码';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';
COMMENT ON COLUMN sys_role.role_desc IS '角色描述';
COMMENT ON COLUMN sys_role.data_scope IS '数据权限范围：all=全部/dept=本部门/dept_and_child=本部门及下级/custom=自定义部门/self=仅本人';
COMMENT ON COLUMN sys_role.is_enabled IS '是否启用';
COMMENT ON COLUMN sys_role.sort_order IS '排序号';
COMMENT ON COLUMN sys_role.created_at IS '创建时间';
COMMENT ON COLUMN sys_role.updated_at IS '更新时间';
COMMENT ON COLUMN sys_role.created_by IS '创建人ID';
COMMENT ON COLUMN sys_role.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_role.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_role.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_role.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_role.version IS '版本号';

-- ============================================================
-- 3. sys_menu 菜单表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_menu (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    parent_id       BIGINT          DEFAULT 0,
    menu_name       VARCHAR(50)     NOT NULL,
    menu_type       VARCHAR(20)     NOT NULL,
    permission_code VARCHAR(100),
    route_path      VARCHAR(200),
    route_name      VARCHAR(50),
    component_path  VARCHAR(200),
    icon            VARCHAR(50),
    sort_order      INT             DEFAULT 0,
    is_visible      BOOLEAN         NOT NULL DEFAULT TRUE,
    is_enabled      BOOLEAN         NOT NULL DEFAULT TRUE,
    is_keep_alive   BOOLEAN         NOT NULL DEFAULT FALSE,
    is_external_link BOOLEAN        NOT NULL DEFAULT FALSE,
    external_url    VARCHAR(500),
    -- 扩展字段
    ext_str1        VARCHAR(200),
    ext_str2        VARCHAR(200),
    ext_str3        VARCHAR(200),
    ext_str4        VARCHAR(200),
    ext_str5        VARCHAR(200),
    ext_str6        VARCHAR(200),
    ext_str7        VARCHAR(200),
    ext_str8        VARCHAR(200),
    ext_str9        VARCHAR(200),
    ext_str10       VARCHAR(200),
    ext_num1        DECIMAL(18,8),
    ext_num2        DECIMAL(18,8),
    ext_num3        DECIMAL(18,8),
    ext_num4        DECIMAL(18,8),
    ext_num5        DECIMAL(18,8),
    ext_date1       DATE,
    ext_date2       DATE,
    ext_date3       DATE,
    ext_bool1       BOOLEAN,
    ext_bool2       BOOLEAN,
    ext_bool3       BOOLEAN,
    ext_json        JSONB,
    -- 通用必含字段
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    version         INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE sys_menu IS '菜单表';
COMMENT ON COLUMN sys_menu.id IS '主键ID';
COMMENT ON COLUMN sys_menu.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_menu.parent_id IS '父菜单ID（0=顶级菜单）';
COMMENT ON COLUMN sys_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN sys_menu.menu_type IS '菜单类型：dir=目录/menu=菜单/button=按钮';
COMMENT ON COLUMN sys_menu.permission_code IS '权限标识码';
COMMENT ON COLUMN sys_menu.route_path IS '路由路径';
COMMENT ON COLUMN sys_menu.route_name IS '路由名称';
COMMENT ON COLUMN sys_menu.component_path IS '组件路径';
COMMENT ON COLUMN sys_menu.icon IS '图标';
COMMENT ON COLUMN sys_menu.sort_order IS '排序号';
COMMENT ON COLUMN sys_menu.is_visible IS '是否可见';
COMMENT ON COLUMN sys_menu.is_enabled IS '是否启用';
COMMENT ON COLUMN sys_menu.is_keep_alive IS '是否缓存（keep-alive）';
COMMENT ON COLUMN sys_menu.is_external_link IS '是否外链';
COMMENT ON COLUMN sys_menu.external_url IS '外链URL';
COMMENT ON COLUMN sys_menu.created_at IS '创建时间';
COMMENT ON COLUMN sys_menu.updated_at IS '更新时间';
COMMENT ON COLUMN sys_menu.created_by IS '创建人ID';
COMMENT ON COLUMN sys_menu.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_menu.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_menu.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_menu.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_menu.version IS '版本号';

-- ============================================================
-- 4. sys_user_role 用户角色关联表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_user_role (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    user_id         BIGINT          NOT NULL,
    role_id         BIGINT          NOT NULL,
    -- 扩展字段
    ext_str1        VARCHAR(200),
    ext_str2        VARCHAR(200),
    ext_str3        VARCHAR(200),
    ext_str4        VARCHAR(200),
    ext_str5        VARCHAR(200),
    ext_str6        VARCHAR(200),
    ext_str7        VARCHAR(200),
    ext_str8        VARCHAR(200),
    ext_str9        VARCHAR(200),
    ext_str10       VARCHAR(200),
    ext_num1        DECIMAL(18,8),
    ext_num2        DECIMAL(18,8),
    ext_num3        DECIMAL(18,8),
    ext_num4        DECIMAL(18,8),
    ext_num5        DECIMAL(18,8),
    ext_date1       DATE,
    ext_date2       DATE,
    ext_date3       DATE,
    ext_bool1       BOOLEAN,
    ext_bool2       BOOLEAN,
    ext_bool3       BOOLEAN,
    ext_json        JSONB,
    -- 通用必含字段
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    version         INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE sys_user_role IS '用户角色关联表';
COMMENT ON COLUMN sys_user_role.id IS '主键ID';
COMMENT ON COLUMN sys_user_role.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_user_role.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_role.role_id IS '角色ID';
COMMENT ON COLUMN sys_user_role.created_at IS '创建时间';
COMMENT ON COLUMN sys_user_role.updated_at IS '更新时间';
COMMENT ON COLUMN sys_user_role.created_by IS '创建人ID';
COMMENT ON COLUMN sys_user_role.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_user_role.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_user_role.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_user_role.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_user_role.version IS '版本号';

-- ============================================================
-- 5. sys_user_dept 用户部门关联表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_user_dept (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    user_id         BIGINT          NOT NULL,
    dept_id         BIGINT          NOT NULL,
    -- 扩展字段
    ext_str1        VARCHAR(200),
    ext_str2        VARCHAR(200),
    ext_str3        VARCHAR(200),
    ext_str4        VARCHAR(200),
    ext_str5        VARCHAR(200),
    ext_str6        VARCHAR(200),
    ext_str7        VARCHAR(200),
    ext_str8        VARCHAR(200),
    ext_str9        VARCHAR(200),
    ext_str10       VARCHAR(200),
    ext_num1        DECIMAL(18,8),
    ext_num2        DECIMAL(18,8),
    ext_num3        DECIMAL(18,8),
    ext_num4        DECIMAL(18,8),
    ext_num5        DECIMAL(18,8),
    ext_date1       DATE,
    ext_date2       DATE,
    ext_date3       DATE,
    ext_bool1       BOOLEAN,
    ext_bool2       BOOLEAN,
    ext_bool3       BOOLEAN,
    ext_json        JSONB,
    -- 通用必含字段
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    version         INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE sys_user_dept IS '用户部门关联表';
COMMENT ON COLUMN sys_user_dept.id IS '主键ID';
COMMENT ON COLUMN sys_user_dept.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_user_dept.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_dept.dept_id IS '部门ID';
COMMENT ON COLUMN sys_user_dept.created_at IS '创建时间';
COMMENT ON COLUMN sys_user_dept.updated_at IS '更新时间';
COMMENT ON COLUMN sys_user_dept.created_by IS '创建人ID';
COMMENT ON COLUMN sys_user_dept.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_user_dept.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_user_dept.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_user_dept.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_user_dept.version IS '版本号';

-- ============================================================
-- 6. sys_role_menu 角色菜单关联表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_role_menu (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    role_id         BIGINT          NOT NULL,
    menu_id         BIGINT          NOT NULL,
    permission_type VARCHAR(30)     NOT NULL DEFAULT 'all',
    -- 扩展字段
    ext_str1        VARCHAR(200),
    ext_str2        VARCHAR(200),
    ext_str3        VARCHAR(200),
    ext_str4        VARCHAR(200),
    ext_str5        VARCHAR(200),
    ext_str6        VARCHAR(200),
    ext_str7        VARCHAR(200),
    ext_str8        VARCHAR(200),
    ext_str9        VARCHAR(200),
    ext_str10       VARCHAR(200),
    ext_num1        DECIMAL(18,8),
    ext_num2        DECIMAL(18,8),
    ext_num3        DECIMAL(18,8),
    ext_num4        DECIMAL(18,8),
    ext_num5        DECIMAL(18,8),
    ext_date1       DATE,
    ext_date2       DATE,
    ext_date3       DATE,
    ext_bool1       BOOLEAN,
    ext_bool2       BOOLEAN,
    ext_bool3       BOOLEAN,
    ext_json        JSONB,
    -- 通用必含字段
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    version         INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE sys_role_menu IS '角色菜单关联表';
COMMENT ON COLUMN sys_role_menu.id IS '主键ID';
COMMENT ON COLUMN sys_role_menu.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_role_menu.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_menu.menu_id IS '菜单ID';
COMMENT ON COLUMN sys_role_menu.permission_type IS '权限类型：all=全部/view=查看/edit=编辑/delete=删除';
COMMENT ON COLUMN sys_role_menu.created_at IS '创建时间';
COMMENT ON COLUMN sys_role_menu.updated_at IS '更新时间';
COMMENT ON COLUMN sys_role_menu.created_by IS '创建人ID';
COMMENT ON COLUMN sys_role_menu.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_role_menu.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_role_menu.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_role_menu.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_role_menu.version IS '版本号';

-- ============================================================
-- 7. sys_role_data_scope 角色数据权限范围表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_role_data_scope (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    role_id         BIGINT          NOT NULL,
    scope_type      VARCHAR(30)     NOT NULL,
    dept_ids        TEXT,
    -- 扩展字段
    ext_str1        VARCHAR(200),
    ext_str2        VARCHAR(200),
    ext_str3        VARCHAR(200),
    ext_str4        VARCHAR(200),
    ext_str5        VARCHAR(200),
    ext_str6        VARCHAR(200),
    ext_str7        VARCHAR(200),
    ext_str8        VARCHAR(200),
    ext_str9        VARCHAR(200),
    ext_str10       VARCHAR(200),
    ext_num1        DECIMAL(18,8),
    ext_num2        DECIMAL(18,8),
    ext_num3        DECIMAL(18,8),
    ext_num4        DECIMAL(18,8),
    ext_num5        DECIMAL(18,8),
    ext_date1       DATE,
    ext_date2       DATE,
    ext_date3       DATE,
    ext_bool1       BOOLEAN,
    ext_bool2       BOOLEAN,
    ext_bool3       BOOLEAN,
    ext_json        JSONB,
    -- 通用必含字段
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    version         INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE sys_role_data_scope IS '角色数据权限范围表';
COMMENT ON COLUMN sys_role_data_scope.id IS '主键ID';
COMMENT ON COLUMN sys_role_data_scope.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_role_data_scope.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_data_scope.scope_type IS '数据权限范围类型：all=全部/dept=本部门/dept_and_child=本部门及下级/custom=自定义部门/self=仅本人';
COMMENT ON COLUMN sys_role_data_scope.dept_ids IS '自定义部门ID列表（JSON数组或逗号分隔，scope_type=custom时有效）';
COMMENT ON COLUMN sys_role_data_scope.created_at IS '创建时间';
COMMENT ON COLUMN sys_role_data_scope.updated_at IS '更新时间';
COMMENT ON COLUMN sys_role_data_scope.created_by IS '创建人ID';
COMMENT ON COLUMN sys_role_data_scope.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_role_data_scope.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_role_data_scope.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_role_data_scope.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_role_data_scope.version IS '版本号';

-- ============================================================
-- 8. sys_role_field_permission 角色字段权限表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_role_field_permission (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    role_id         BIGINT          NOT NULL,
    table_name      VARCHAR(100)    NOT NULL,
    field_name      VARCHAR(100)    NOT NULL,
    permission_type VARCHAR(30)     NOT NULL DEFAULT 'visible',
    -- 扩展字段
    ext_str1        VARCHAR(200),
    ext_str2        VARCHAR(200),
    ext_str3        VARCHAR(200),
    ext_str4        VARCHAR(200),
    ext_str5        VARCHAR(200),
    ext_str6        VARCHAR(200),
    ext_str7        VARCHAR(200),
    ext_str8        VARCHAR(200),
    ext_str9        VARCHAR(200),
    ext_str10       VARCHAR(200),
    ext_num1        DECIMAL(18,8),
    ext_num2        DECIMAL(18,8),
    ext_num3        DECIMAL(18,8),
    ext_num4        DECIMAL(18,8),
    ext_num5        DECIMAL(18,8),
    ext_date1       DATE,
    ext_date2       DATE,
    ext_date3       DATE,
    ext_bool1       BOOLEAN,
    ext_bool2       BOOLEAN,
    ext_bool3       BOOLEAN,
    ext_json        JSONB,
    -- 通用必含字段
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    version         INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE sys_role_field_permission IS '角色字段权限表';
COMMENT ON COLUMN sys_role_field_permission.id IS '主键ID';
COMMENT ON COLUMN sys_role_field_permission.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_role_field_permission.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_field_permission.table_name IS '表名';
COMMENT ON COLUMN sys_role_field_permission.field_name IS '字段名';
COMMENT ON COLUMN sys_role_field_permission.permission_type IS '权限类型：visible=可见/hidden=隐藏/readonly=只读/editable=可编辑';
COMMENT ON COLUMN sys_role_field_permission.created_at IS '创建时间';
COMMENT ON COLUMN sys_role_field_permission.updated_at IS '更新时间';
COMMENT ON COLUMN sys_role_field_permission.created_by IS '创建人ID';
COMMENT ON COLUMN sys_role_field_permission.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_role_field_permission.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_role_field_permission.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_role_field_permission.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_role_field_permission.version IS '版本号';

-- ============================================================
-- 9. sys_user_group 用户组表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_user_group (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    group_code      VARCHAR(50)     NOT NULL,
    group_name      VARCHAR(50)     NOT NULL,
    group_desc      VARCHAR(200),
    is_enabled      BOOLEAN         NOT NULL DEFAULT TRUE,
    sort_order      INT             DEFAULT 0,
    -- 扩展字段
    ext_str1        VARCHAR(200),
    ext_str2        VARCHAR(200),
    ext_str3        VARCHAR(200),
    ext_str4        VARCHAR(200),
    ext_str5        VARCHAR(200),
    ext_str6        VARCHAR(200),
    ext_str7        VARCHAR(200),
    ext_str8        VARCHAR(200),
    ext_str9        VARCHAR(200),
    ext_str10       VARCHAR(200),
    ext_num1        DECIMAL(18,8),
    ext_num2        DECIMAL(18,8),
    ext_num3        DECIMAL(18,8),
    ext_num4        DECIMAL(18,8),
    ext_num5        DECIMAL(18,8),
    ext_date1       DATE,
    ext_date2       DATE,
    ext_date3       DATE,
    ext_bool1       BOOLEAN,
    ext_bool2       BOOLEAN,
    ext_bool3       BOOLEAN,
    ext_json        JSONB,
    -- 通用必含字段
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    version         INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE sys_user_group IS '用户组表';
COMMENT ON COLUMN sys_user_group.id IS '主键ID';
COMMENT ON COLUMN sys_user_group.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_user_group.group_code IS '用户组编码';
COMMENT ON COLUMN sys_user_group.group_name IS '用户组名称';
COMMENT ON COLUMN sys_user_group.group_desc IS '用户组描述';
COMMENT ON COLUMN sys_user_group.is_enabled IS '是否启用';
COMMENT ON COLUMN sys_user_group.sort_order IS '排序号';
COMMENT ON COLUMN sys_user_group.created_at IS '创建时间';
COMMENT ON COLUMN sys_user_group.updated_at IS '更新时间';
COMMENT ON COLUMN sys_user_group.created_by IS '创建人ID';
COMMENT ON COLUMN sys_user_group.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_user_group.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_user_group.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_user_group.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_user_group.version IS '版本号';

-- ============================================================
-- 10. sys_user_group_member 用户组成员表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_user_group_member (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    group_id        BIGINT          NOT NULL,
    user_id         BIGINT          NOT NULL,
    -- 扩展字段
    ext_str1        VARCHAR(200),
    ext_str2        VARCHAR(200),
    ext_str3        VARCHAR(200),
    ext_str4        VARCHAR(200),
    ext_str5        VARCHAR(200),
    ext_str6        VARCHAR(200),
    ext_str7        VARCHAR(200),
    ext_str8        VARCHAR(200),
    ext_str9        VARCHAR(200),
    ext_str10       VARCHAR(200),
    ext_num1        DECIMAL(18,8),
    ext_num2        DECIMAL(18,8),
    ext_num3        DECIMAL(18,8),
    ext_num4        DECIMAL(18,8),
    ext_num5        DECIMAL(18,8),
    ext_date1       DATE,
    ext_date2       DATE,
    ext_date3       DATE,
    ext_bool1       BOOLEAN,
    ext_bool2       BOOLEAN,
    ext_bool3       BOOLEAN,
    ext_json        JSONB,
    -- 通用必含字段
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    version         INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE sys_user_group_member IS '用户组成员表';
COMMENT ON COLUMN sys_user_group_member.id IS '主键ID';
COMMENT ON COLUMN sys_user_group_member.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_user_group_member.group_id IS '用户组ID';
COMMENT ON COLUMN sys_user_group_member.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_group_member.created_at IS '创建时间';
COMMENT ON COLUMN sys_user_group_member.updated_at IS '更新时间';
COMMENT ON COLUMN sys_user_group_member.created_by IS '创建人ID';
COMMENT ON COLUMN sys_user_group_member.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_user_group_member.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_user_group_member.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_user_group_member.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_user_group_member.version IS '版本号';
