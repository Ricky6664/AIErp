-- ============================================================
-- Flyway Migration Script
-- Description: sys_field_permission_scheme 字段权限方案主表 + 方案角色关联表
-- Author: AI Generated
-- Date: 2026-06-03
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_field_permission_scheme (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    scheme_name     VARCHAR(100)    NOT NULL,
    scheme_code     VARCHAR(100)    NOT NULL,
    scheme_desc     VARCHAR(500),
    table_name      VARCHAR(100)    NOT NULL,
    ext_json        JSONB,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    version         INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE sys_field_permission_scheme IS '字段权限方案主表';
COMMENT ON COLUMN sys_field_permission_scheme.scheme_name IS '方案名称';
COMMENT ON COLUMN sys_field_permission_scheme.scheme_code IS '方案编码(唯一)';
COMMENT ON COLUMN sys_field_permission_scheme.table_name IS '目标表名';

CREATE UNIQUE INDEX uk_field_perm_scheme_code ON sys_field_permission_scheme (scheme_code) WHERE is_deleted = false;

-- 方案-角色关联表
CREATE TABLE IF NOT EXISTS sys_field_permission_scheme_role (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    scheme_id       BIGINT          NOT NULL,
    role_id         BIGINT          NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE sys_field_permission_scheme_role IS '字段权限方案-角色关联表';
CREATE UNIQUE INDEX uk_field_perm_scheme_role ON sys_field_permission_scheme_role (scheme_id, role_id) WHERE is_deleted = false;

-- 方案字段明细表
CREATE TABLE IF NOT EXISTS sys_field_permission_scheme_detail (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    scheme_id       BIGINT          NOT NULL,
    field_name      VARCHAR(100)    NOT NULL,
    permission_type VARCHAR(50)     NOT NULL DEFAULT 'visible',
    page_state      VARCHAR(50)     DEFAULT 'all',
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE sys_field_permission_scheme_detail IS '字段权限方案明细表';
COMMENT ON COLUMN sys_field_permission_scheme_detail.field_name IS '字段名';
COMMENT ON COLUMN sys_field_permission_scheme_detail.permission_type IS '权限类型: visible/editable/hidden';
COMMENT ON COLUMN sys_field_permission_scheme_detail.page_state IS '页面状态: add/edit/detail/all';

CREATE UNIQUE INDEX uk_field_perm_detail ON sys_field_permission_scheme_detail (scheme_id, field_name) WHERE is_deleted = false;
