-- ============================================================
-- 用户组角色关联表 DDL
-- Task: P0-004-008-006-001-001
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_user_group_role (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL DEFAULT 0,
    group_id        BIGINT          NOT NULL,
    role_id         BIGINT          NOT NULL,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    is_deleted      SMALLINT        NOT NULL DEFAULT 0,
    version         INT             NOT NULL DEFAULT 0
);

COMMENT ON TABLE sys_user_group_role IS '用户组角色关联表';
COMMENT ON COLUMN sys_user_group_role.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_user_group_role.group_id IS '用户组ID';
COMMENT ON COLUMN sys_user_group_role.role_id IS '角色ID';
COMMENT ON COLUMN sys_user_group_role.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_user_group_role.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_user_group_role.is_deleted IS '是否删除';

CREATE UNIQUE INDEX uk_sys_user_group_role_active ON sys_user_group_role(tenant_id, group_id, role_id) WHERE is_deleted = 0;
CREATE INDEX idx_sys_user_group_role_tenant_group ON sys_user_group_role(tenant_id, group_id);
CREATE INDEX idx_sys_user_group_role_tenant_role ON sys_user_group_role(tenant_id, role_id);
