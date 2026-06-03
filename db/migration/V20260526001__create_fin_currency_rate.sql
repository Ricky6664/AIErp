-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: fin_currency_rate币种汇率表建表DDL
-- Author: AI Generated
-- Date: 2026-06-03
-- ============================================================

-- ============================================================
-- fin_currency_rate 币种汇率表
-- ============================================================
CREATE TABLE IF NOT EXISTS fin_currency_rate (
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    -- 业务字段
    from_currency_id    BIGINT          NOT NULL,
    to_currency_id      BIGINT          NOT NULL,
    rate                DECIMAL(18,8)   NOT NULL,
    effective_date      DATE            NOT NULL,
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

COMMENT ON TABLE fin_currency_rate IS '币种汇率表';
COMMENT ON COLUMN fin_currency_rate.id IS '主键ID';
COMMENT ON COLUMN fin_currency_rate.tenant_id IS '租户ID';
COMMENT ON COLUMN fin_currency_rate.from_currency_id IS '源币种ID';
COMMENT ON COLUMN fin_currency_rate.to_currency_id IS '目标币种ID';
COMMENT ON COLUMN fin_currency_rate.rate IS '汇率值';
COMMENT ON COLUMN fin_currency_rate.effective_date IS '生效日期';
COMMENT ON COLUMN fin_currency_rate.created_at IS '创建时间';
COMMENT ON COLUMN fin_currency_rate.updated_at IS '更新时间';
COMMENT ON COLUMN fin_currency_rate.created_by IS '创建人ID';
COMMENT ON COLUMN fin_currency_rate.updated_by IS '修改人ID';
COMMENT ON COLUMN fin_currency_rate.is_deleted IS '是否删除';
COMMENT ON COLUMN fin_currency_rate.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN fin_currency_rate.owner_id IS '数据负责人ID';
COMMENT ON COLUMN fin_currency_rate.version IS '版本号';
