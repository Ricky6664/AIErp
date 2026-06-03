-- ============================================================
-- Flyway Migration Script
-- Description: sys_data_permission_scheme 数据权限方案主表 + 方案角色关联表
-- Author: AI Generated
-- Date: 2026-06-03
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_data_permission_scheme (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    scheme_name     VARCHAR(100)    NOT NULL,
    scheme_code     VARCHAR(100)    NOT NULL,
    scheme_desc     VARCHAR(500),
    data_scope      VARCHAR(50)     NOT NULL DEFAULT 'all',
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

COMMENT ON TABLE sys_data_permission_scheme IS '数据权限方案主表';
COMMENT ON COLUMN sys_data_permission_scheme.scheme_name IS '方案名称';
COMMENT ON COLUMN sys_data_permission_scheme.scheme_code IS '方案编码(唯一)';
COMMENT ON COLUMN sys_data_permission_scheme.data_scope IS '数据范围: all/dept/dept_and_sub/self/custom';

CREATE UNIQUE INDEX uk_data_perm_scheme_code ON sys_data_permission_scheme (scheme_code) WHERE is_deleted = false;

-- 方案-角色关联表
CREATE TABLE IF NOT EXISTS sys_data_permission_scheme_role (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    scheme_id       BIGINT          NOT NULL,
    role_id         BIGINT          NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    version         INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE sys_data_permission_scheme_role IS '数据权限方案-角色关联表';
CREATE UNIQUE INDEX uk_data_perm_scheme_role ON sys_data_permission_scheme_role (scheme_id, role_id) WHERE is_deleted = false;
