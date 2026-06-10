-- ============================================================
-- Flyway Migration Script
-- Version: V20260601077
-- Description: crm_customer 客户主表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- crm_customer 客户主表
-- ============================================================
CREATE TABLE IF NOT EXISTS crm_customer (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    customer_code   VARCHAR(50)     NOT NULL,
    customer_name   VARCHAR(200)    NOT NULL,
    class_id        BIGINT,
    industry        VARCHAR(100),
    credit_level    SMALLINT,
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

COMMENT ON TABLE crm_customer IS '客户主表';
COMMENT ON COLUMN crm_customer.id IS '主键ID';
COMMENT ON COLUMN crm_customer.tenant_id IS '租户ID';
COMMENT ON COLUMN crm_customer.customer_code IS '客户编码';
COMMENT ON COLUMN crm_customer.customer_name IS '客户名称';
COMMENT ON COLUMN crm_customer.class_id IS '客户分类ID';
COMMENT ON COLUMN crm_customer.industry IS '所属行业';
COMMENT ON COLUMN crm_customer.credit_level IS '信用等级';
COMMENT ON COLUMN crm_customer.status IS '状态';
COMMENT ON COLUMN crm_customer.created_at IS '创建时间';
COMMENT ON COLUMN crm_customer.updated_at IS '更新时间';
COMMENT ON COLUMN crm_customer.created_by IS '创建人ID';
COMMENT ON COLUMN crm_customer.updated_by IS '修改人ID';
COMMENT ON COLUMN crm_customer.is_deleted IS '是否删除';
COMMENT ON COLUMN crm_customer.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN crm_customer.owner_id IS '数据负责人ID';
COMMENT ON COLUMN crm_customer.version IS '版本号';
