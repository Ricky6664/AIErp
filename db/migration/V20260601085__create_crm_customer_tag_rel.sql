-- ============================================================
-- Flyway Migration Script
-- Version: V20260601085
-- Description: crm_customer_tag_rel 客户标签关联表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-006-001-001
-- ============================================================

-- ============================================================
-- crm_customer_tag_rel 客户标签关联表
-- 存储客户与标签的多对多关联关系
-- ============================================================
CREATE TABLE IF NOT EXISTS crm_customer_tag_rel (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    customer_id     BIGINT          NOT NULL,
    tag_id          BIGINT          NOT NULL,
    code            VARCHAR(50)     NOT NULL,
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

COMMENT ON TABLE crm_customer_tag_rel IS '客户标签关联表';
COMMENT ON COLUMN crm_customer_tag_rel.id IS '主键ID';
COMMENT ON COLUMN crm_customer_tag_rel.tenant_id IS '租户ID';
COMMENT ON COLUMN crm_customer_tag_rel.customer_id IS '客户ID';
COMMENT ON COLUMN crm_customer_tag_rel.tag_id IS '标签ID';
COMMENT ON COLUMN crm_customer_tag_rel.code IS '关联编码';
COMMENT ON COLUMN crm_customer_tag_rel.status IS '状态';
COMMENT ON COLUMN crm_customer_tag_rel.created_at IS '创建时间';
COMMENT ON COLUMN crm_customer_tag_rel.updated_at IS '更新时间';
COMMENT ON COLUMN crm_customer_tag_rel.created_by IS '创建人ID';
COMMENT ON COLUMN crm_customer_tag_rel.updated_by IS '修改人ID';
COMMENT ON COLUMN crm_customer_tag_rel.is_deleted IS '是否删除';
COMMENT ON COLUMN crm_customer_tag_rel.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN crm_customer_tag_rel.owner_id IS '数据负责人ID';
COMMENT ON COLUMN crm_customer_tag_rel.version IS '版本号';
