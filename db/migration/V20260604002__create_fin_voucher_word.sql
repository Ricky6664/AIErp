-- ============================================================
-- Flyway Migration Script
-- Version: V20260604002
-- Description: fin_voucher_word凭证字表建表DDL
-- Author: AI Generated
-- Date: 2026-06-03
-- ============================================================

-- ============================================================
-- fin_voucher_word 凭证字表
-- ============================================================
CREATE TABLE IF NOT EXISTS fin_voucher_word (
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    -- 业务字段
    word_code           VARCHAR(20)     NOT NULL,
    word_name           VARCHAR(50)     NOT NULL,
    sort_order          INT             DEFAULT 0,
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

COMMENT ON TABLE fin_voucher_word IS '凭证字表';
COMMENT ON COLUMN fin_voucher_word.id IS '主键ID';
COMMENT ON COLUMN fin_voucher_word.tenant_id IS '租户ID';
COMMENT ON COLUMN fin_voucher_word.word_code IS '凭证字编码';
COMMENT ON COLUMN fin_voucher_word.word_name IS '凭证字名称';
COMMENT ON COLUMN fin_voucher_word.sort_order IS '排序号';
COMMENT ON COLUMN fin_voucher_word.created_at IS '创建时间';
COMMENT ON COLUMN fin_voucher_word.updated_at IS '更新时间';
COMMENT ON COLUMN fin_voucher_word.created_by IS '创建人ID';
COMMENT ON COLUMN fin_voucher_word.updated_by IS '修改人ID';
COMMENT ON COLUMN fin_voucher_word.is_deleted IS '是否删除';
COMMENT ON COLUMN fin_voucher_word.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN fin_voucher_word.owner_id IS '数据负责人ID';
COMMENT ON COLUMN fin_voucher_word.version IS '版本号';
