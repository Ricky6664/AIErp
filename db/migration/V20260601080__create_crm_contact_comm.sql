-- ============================================================
-- Flyway Migration Script
-- Version: V20260601080
-- Description: crm_contact_comm 联系人通讯表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-004-001-001
-- ============================================================

-- ============================================================
-- crm_contact_comm 联系人通讯表
-- 存储联系人的多种通讯方式（微信/QQ/电话/邮箱等）
-- 支持树形结构分组（parent_id）
-- ============================================================
CREATE TABLE IF NOT EXISTS crm_contact_comm (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    contact_id      BIGINT          NOT NULL,
    code            VARCHAR(50)     NOT NULL,
    name            VARCHAR(100)    NOT NULL,
    comm_type       VARCHAR(50),
    comm_value      VARCHAR(200),
    parent_id       BIGINT,
    sort_order      INT             DEFAULT 0,
    is_primary      BOOLEAN         DEFAULT FALSE,
    status          SMALLINT        DEFAULT 1,
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

COMMENT ON TABLE crm_contact_comm IS '联系人通讯表';
COMMENT ON COLUMN crm_contact_comm.id IS '主键ID';
COMMENT ON COLUMN crm_contact_comm.tenant_id IS '租户ID';
COMMENT ON COLUMN crm_contact_comm.contact_id IS '联系人ID';
COMMENT ON COLUMN crm_contact_comm.code IS '通讯方式编码';
COMMENT ON COLUMN crm_contact_comm.name IS '通讯方式名称';
COMMENT ON COLUMN crm_contact_comm.comm_type IS '通讯类型';
COMMENT ON COLUMN crm_contact_comm.comm_value IS '通讯号码/地址';
COMMENT ON COLUMN crm_contact_comm.parent_id IS '父级通讯方式ID';
COMMENT ON COLUMN crm_contact_comm.sort_order IS '排序号';
COMMENT ON COLUMN crm_contact_comm.is_primary IS '是否为主要联系方式';
COMMENT ON COLUMN crm_contact_comm.status IS '状态';
COMMENT ON COLUMN crm_contact_comm.created_at IS '创建时间';
COMMENT ON COLUMN crm_contact_comm.updated_at IS '更新时间';
COMMENT ON COLUMN crm_contact_comm.created_by IS '创建人ID';
COMMENT ON COLUMN crm_contact_comm.updated_by IS '修改人ID';
COMMENT ON COLUMN crm_contact_comm.is_deleted IS '是否删除';
COMMENT ON COLUMN crm_contact_comm.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN crm_contact_comm.owner_id IS '数据负责人ID';
COMMENT ON COLUMN crm_contact_comm.version IS '版本号';
