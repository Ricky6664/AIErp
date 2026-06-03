-- ============================================================
-- Flyway Migration Script
-- Version: V20260604005
-- Description: sys_role_inheritance角色继承关系表建表DDL
-- Author: AI Generated
-- Date: 2026-06-03
-- ============================================================

-- ============================================================
-- sys_role_inheritance 角色继承关系表
-- 子角色自动继承父角色全部权限
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_role_inheritance (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    parent_role_id  BIGINT          NOT NULL,
    child_role_id   BIGINT          NOT NULL,
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

-- ============================================================
-- 注释
-- ============================================================
COMMENT ON TABLE sys_role_inheritance IS '角色继承关系表';
COMMENT ON COLUMN sys_role_inheritance.id IS '主键ID';
COMMENT ON COLUMN sys_role_inheritance.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_role_inheritance.parent_role_id IS '父角色ID';
COMMENT ON COLUMN sys_role_inheritance.child_role_id IS '子角色ID';
COMMENT ON COLUMN sys_role_inheritance.created_at IS '创建时间';
COMMENT ON COLUMN sys_role_inheritance.updated_at IS '更新时间';
COMMENT ON COLUMN sys_role_inheritance.created_by IS '创建人ID';
COMMENT ON COLUMN sys_role_inheritance.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_role_inheritance.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_role_inheritance.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_role_inheritance.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_role_inheritance.version IS '版本号(乐观锁)';

-- ============================================================
-- 索引与约束
-- ============================================================
-- 唯一约束：同一对父子角色关系不可重复（含软删除）
CREATE UNIQUE INDEX uk_sys_role_inheritance ON sys_role_inheritance (parent_role_id, child_role_id) WHERE is_deleted = false;

-- 外键关联索引
CREATE INDEX idx_sys_role_inheritance_parent ON sys_role_inheritance (parent_role_id);
CREATE INDEX idx_sys_role_inheritance_child ON sys_role_inheritance (child_role_id);

-- 按子角色查询继承的父角色
CREATE INDEX idx_sys_role_inheritance_child_tenant ON sys_role_inheritance (tenant_id, child_role_id) WHERE is_deleted = false;
