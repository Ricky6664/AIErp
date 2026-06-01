-- ============================================================
-- Flyway Migration Script
-- Version: V20260601007
-- Description: org_position岗位表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- org_position 岗位表
-- ============================================================
CREATE TABLE IF NOT EXISTS org_position (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    position_code   VARCHAR(50)     NOT NULL,
    position_name   VARCHAR(100)    NOT NULL,
    dept_id         BIGINT,
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

COMMENT ON TABLE org_position IS '岗位表';
COMMENT ON COLUMN org_position.id IS '主键ID';
COMMENT ON COLUMN org_position.tenant_id IS '租户ID';
COMMENT ON COLUMN org_position.position_code IS '岗位编码';
COMMENT ON COLUMN org_position.position_name IS '岗位名称';
COMMENT ON COLUMN org_position.dept_id IS '所属部门ID';
COMMENT ON COLUMN org_position.sort_order IS '排序号';
COMMENT ON COLUMN org_position.status IS '状态：1=启用/0=停用';
COMMENT ON COLUMN org_position.created_at IS '创建时间';
COMMENT ON COLUMN org_position.updated_at IS '更新时间';
COMMENT ON COLUMN org_position.created_by IS '创建人ID';
COMMENT ON COLUMN org_position.updated_by IS '修改人ID';
COMMENT ON COLUMN org_position.is_deleted IS '是否删除';
COMMENT ON COLUMN org_position.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN org_position.owner_id IS '数据负责人ID';
COMMENT ON COLUMN org_position.version IS '版本号';
