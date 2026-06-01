-- ============================================================
-- Flyway Migration Script
-- Version: V20260601005
-- Description: org_department部门表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- org_department 部门表
-- ============================================================
CREATE TABLE IF NOT EXISTS org_department (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    parent_id       BIGINT          DEFAULT 0,
    dept_code       VARCHAR(50)     NOT NULL,
    dept_name       VARCHAR(100)    NOT NULL,
    leader_id       BIGINT,
    sort_order      INT             DEFAULT 0,
    status          SMALLINT        NOT NULL DEFAULT 1,
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

COMMENT ON TABLE org_department IS '部门表';
COMMENT ON COLUMN org_department.id IS '主键ID';
COMMENT ON COLUMN org_department.tenant_id IS '租户ID';
COMMENT ON COLUMN org_department.parent_id IS '上级部门ID';
COMMENT ON COLUMN org_department.dept_code IS '部门编码';
COMMENT ON COLUMN org_department.dept_name IS '部门名称';
COMMENT ON COLUMN org_department.leader_id IS '部门负责人ID';
COMMENT ON COLUMN org_department.sort_order IS '排序号';
COMMENT ON COLUMN org_department.status IS '状态：1=启用/0=停用';
COMMENT ON COLUMN org_department.created_at IS '创建时间';
COMMENT ON COLUMN org_department.updated_at IS '更新时间';
COMMENT ON COLUMN org_department.created_by IS '创建人ID';
COMMENT ON COLUMN org_department.updated_by IS '修改人ID';
COMMENT ON COLUMN org_department.is_deleted IS '是否删除';
COMMENT ON COLUMN org_department.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN org_department.owner_id IS '数据负责人ID';
COMMENT ON COLUMN org_department.version IS '版本号';
