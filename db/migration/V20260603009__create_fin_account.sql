-- ============================================================
-- Flyway Migration Script
-- Version: V20260603009
-- Description: fin_account会计科目表建表DDL
-- Author: AI Generated
-- Date: 2026-06-03
-- ============================================================

-- ============================================================
-- fin_account 会计科目表
-- ============================================================
CREATE TABLE IF NOT EXISTS fin_account (
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    -- 业务字段
    parent_id           BIGINT          DEFAULT 0,
    account_code        VARCHAR(50)     NOT NULL,
    account_name        VARCHAR(200)    NOT NULL,
    account_type        SMALLINT        NOT NULL,
    balance_direction   SMALLINT        NOT NULL,
    -- 扩展字段
    ext_str1            VARCHAR(200),
    ext_str2            VARCHAR(200),
    ext_str3            VARCHAR(200),
    ext_str4            VARCHAR(200),
    ext_str5            VARCHAR(200),
    ext_str6            VARCHAR(200),
    ext_str7            VARCHAR(200),
    ext_str8            VARCHAR(200),
    ext_str9            VARCHAR(200),
    ext_str10           VARCHAR(200),
    ext_num1            DECIMAL(18,8),
    ext_num2            DECIMAL(18,8),
    ext_num3            DECIMAL(18,8),
    ext_num4            DECIMAL(18,8),
    ext_num5            DECIMAL(18,8),
    ext_date1           DATE,
    ext_date2           DATE,
    ext_date3           DATE,
    ext_bool1           BOOLEAN,
    ext_bool2           BOOLEAN,
    ext_bool3           BOOLEAN,
    ext_json            JSONB,
    -- 通用必含字段
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by          BIGINT,
    updated_by          BIGINT,
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id       BIGINT,
    owner_id            BIGINT,
    version             INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE fin_account IS '会计科目表';
COMMENT ON COLUMN fin_account.id IS '主键ID';
COMMENT ON COLUMN fin_account.tenant_id IS '租户ID';
COMMENT ON COLUMN fin_account.parent_id IS '上级科目ID';
COMMENT ON COLUMN fin_account.account_code IS '科目编码';
COMMENT ON COLUMN fin_account.account_name IS '科目名称';
COMMENT ON COLUMN fin_account.account_type IS '科目类型';
COMMENT ON COLUMN fin_account.balance_direction IS '余额方向';
COMMENT ON COLUMN fin_account.created_at IS '创建时间';
COMMENT ON COLUMN fin_account.updated_at IS '更新时间';
COMMENT ON COLUMN fin_account.created_by IS '创建人ID';
COMMENT ON COLUMN fin_account.updated_by IS '修改人ID';
COMMENT ON COLUMN fin_account.is_deleted IS '是否删除';
COMMENT ON COLUMN fin_account.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN fin_account.owner_id IS '数据负责人ID';
COMMENT ON COLUMN fin_account.version IS '版本号';
