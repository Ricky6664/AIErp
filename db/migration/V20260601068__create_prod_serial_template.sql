-- ============================================================
-- Flyway Migration Script
-- Version: V20260601068
-- Description: prod_serial_template 序列号模板表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-021-001-001
-- ============================================================

-- ============================================================
-- prod_serial_template 序列号模板表
-- 序列号生成规则模板（模板编码/名称/前缀/长度/起始值/步长/重置周期/状态）
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_serial_template (
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    -- 业务字段
    code                VARCHAR(50)     NOT NULL,
    name                VARCHAR(100)    NOT NULL,
    prefix              VARCHAR(50),
    serial_length       INT             NOT NULL DEFAULT 8,
    start_value         INT             NOT NULL DEFAULT 1,
    step_value          INT             NOT NULL DEFAULT 1,
    reset_cycle         VARCHAR(50),
    status              SMALLINT        NOT NULL DEFAULT 0,
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

COMMENT ON TABLE prod_serial_template IS '序列号模板表';
COMMENT ON COLUMN prod_serial_template.id IS '主键ID';
COMMENT ON COLUMN prod_serial_template.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_serial_template.code IS '模板编码';
COMMENT ON COLUMN prod_serial_template.name IS '模板名称';
COMMENT ON COLUMN prod_serial_template.prefix IS '前缀';
COMMENT ON COLUMN prod_serial_template.serial_length IS '序列号长度';
COMMENT ON COLUMN prod_serial_template.start_value IS '起始值';
COMMENT ON COLUMN prod_serial_template.step_value IS '步长';
COMMENT ON COLUMN prod_serial_template.reset_cycle IS '重置周期';
COMMENT ON COLUMN prod_serial_template.status IS '状态（0=禁用/1=启用）';
COMMENT ON COLUMN prod_serial_template.created_at IS '创建时间';
COMMENT ON COLUMN prod_serial_template.updated_at IS '更新时间';
COMMENT ON COLUMN prod_serial_template.created_by IS '创建人ID';
COMMENT ON COLUMN prod_serial_template.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_serial_template.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_serial_template.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_serial_template.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_serial_template.version IS '版本号';
