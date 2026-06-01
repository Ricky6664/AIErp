-- ============================================================
-- Flyway Migration Script
-- Version: V20260601003
-- Description: org_company公司表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- org_company 公司表
-- ============================================================
CREATE TABLE IF NOT EXISTS org_company (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    company_code    VARCHAR(50)     NOT NULL,
    company_name    VARCHAR(200)    NOT NULL,
    credit_code     VARCHAR(50),
    legal_person    VARCHAR(50),
    registered_capital DECIMAL(18,8),
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

COMMENT ON TABLE org_company IS '公司表';
COMMENT ON COLUMN org_company.id IS '主键ID';
COMMENT ON COLUMN org_company.tenant_id IS '租户ID';
COMMENT ON COLUMN org_company.company_code IS '公司编码';
COMMENT ON COLUMN org_company.company_name IS '公司名称';
COMMENT ON COLUMN org_company.credit_code IS '统一社会信用代码';
COMMENT ON COLUMN org_company.legal_person IS '法定代表人';
COMMENT ON COLUMN org_company.registered_capital IS '注册资本';
COMMENT ON COLUMN org_company.status IS '状态：1=正常/0=禁用';
COMMENT ON COLUMN org_company.created_at IS '创建时间';
COMMENT ON COLUMN org_company.updated_at IS '更新时间';
COMMENT ON COLUMN org_company.created_by IS '创建人ID';
COMMENT ON COLUMN org_company.updated_by IS '修改人ID';
COMMENT ON COLUMN org_company.is_deleted IS '是否删除';
COMMENT ON COLUMN org_company.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN org_company.owner_id IS '数据负责人ID';
COMMENT ON COLUMN org_company.version IS '版本号';
