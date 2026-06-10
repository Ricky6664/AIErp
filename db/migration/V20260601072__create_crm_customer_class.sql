-- ============================================================
-- Flyway Migration Script
-- Version: V20260601072
-- Description: crm_customer_class客户分类表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- crm_customer_class 客户分类表
-- ============================================================
CREATE TABLE IF NOT EXISTS crm_customer_class (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    parent_id       BIGINT          DEFAULT 0,
    class_code      VARCHAR(50)     NOT NULL,
    class_name      VARCHAR(100)    NOT NULL,
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

COMMENT ON TABLE crm_customer_class IS '客户分类表';
COMMENT ON COLUMN crm_customer_class.id IS '主键ID';
COMMENT ON COLUMN crm_customer_class.tenant_id IS '租户ID';
COMMENT ON COLUMN crm_customer_class.parent_id IS '父分类ID';
COMMENT ON COLUMN crm_customer_class.class_code IS '分类编码';
COMMENT ON COLUMN crm_customer_class.class_name IS '分类名称';
COMMENT ON COLUMN crm_customer_class.created_at IS '创建时间';
COMMENT ON COLUMN crm_customer_class.updated_at IS '更新时间';
COMMENT ON COLUMN crm_customer_class.created_by IS '创建人ID';
COMMENT ON COLUMN crm_customer_class.updated_by IS '修改人ID';
COMMENT ON COLUMN crm_customer_class.is_deleted IS '是否删除';
COMMENT ON COLUMN crm_customer_class.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN crm_customer_class.owner_id IS '数据负责人ID';
COMMENT ON COLUMN crm_customer_class.version IS '版本号';
