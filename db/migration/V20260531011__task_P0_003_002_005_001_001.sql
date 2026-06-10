-- ============================================================
-- Flyway Migration Script
-- Version: V20260531011
-- Description: 移动端相关表建表DDL（sys_mobile_menu）
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. sys_mobile_menu 移动端菜单表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_mobile_menu (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    parent_id       BIGINT          DEFAULT 0,
    menu_name       VARCHAR(100)    NOT NULL,
    menu_type       VARCHAR(20)     NOT NULL,
    permission_code VARCHAR(100),
    route_path      VARCHAR(200),
    icon            VARCHAR(50),
    sort_order      INT             DEFAULT 0,
    is_visible      BOOLEAN         NOT NULL DEFAULT TRUE,
    is_enabled      BOOLEAN         NOT NULL DEFAULT TRUE,
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

COMMENT ON TABLE sys_mobile_menu IS '移动端菜单表';
COMMENT ON COLUMN sys_mobile_menu.id IS '主键ID';
COMMENT ON COLUMN sys_mobile_menu.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_mobile_menu.parent_id IS '父菜单ID（0=顶级菜单）';
COMMENT ON COLUMN sys_mobile_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN sys_mobile_menu.menu_type IS '菜单类型：dir=目录/menu=菜单/button=按钮';
COMMENT ON COLUMN sys_mobile_menu.permission_code IS '权限标识码';
COMMENT ON COLUMN sys_mobile_menu.route_path IS '路由路径';
COMMENT ON COLUMN sys_mobile_menu.icon IS '图标';
COMMENT ON COLUMN sys_mobile_menu.sort_order IS '排序号';
COMMENT ON COLUMN sys_mobile_menu.is_visible IS '是否可见';
COMMENT ON COLUMN sys_mobile_menu.is_enabled IS '是否启用';
COMMENT ON COLUMN sys_mobile_menu.created_at IS '创建时间';
COMMENT ON COLUMN sys_mobile_menu.updated_at IS '更新时间';
COMMENT ON COLUMN sys_mobile_menu.created_by IS '创建人ID';
COMMENT ON COLUMN sys_mobile_menu.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_mobile_menu.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_mobile_menu.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_mobile_menu.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_mobile_menu.version IS '版本号';
