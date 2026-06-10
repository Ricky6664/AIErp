-- ============================================================
-- Flyway Migration Script
-- Version: V20260601045
-- Description: prod_standard_process 标准工序表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-013-001-001
-- ============================================================

-- ============================================================
-- prod_standard_process 标准工序表
-- 标准工序库，存储标准制造工序定义，可被商品BOM和工艺路线引用
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_standard_process (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    code            VARCHAR(50)     NOT NULL,
    name            VARCHAR(200)    NOT NULL,
    process_type    VARCHAR(30),
    standard_hours  DECIMAL(18,8),
    standard_cost   DECIMAL(18,8),
    unit_price      DECIMAL(18,8),
    currency_code   VARCHAR(10)     NOT NULL DEFAULT 'CNY',
    status          SMALLINT        NOT NULL DEFAULT 0,
    description     VARCHAR(500),
    remark          VARCHAR(500),
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

COMMENT ON TABLE prod_standard_process IS '标准工序表';
COMMENT ON COLUMN prod_standard_process.id IS '主键ID';
COMMENT ON COLUMN prod_standard_process.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_standard_process.code IS '工序编码';
COMMENT ON COLUMN prod_standard_process.name IS '工序名称';
COMMENT ON COLUMN prod_standard_process.process_type IS '工序类型（internal=内部工序/outsource=委外工序）';
COMMENT ON COLUMN prod_standard_process.standard_hours IS '标准工时';
COMMENT ON COLUMN prod_standard_process.standard_cost IS '标准成本';
COMMENT ON COLUMN prod_standard_process.unit_price IS '工序单价';
COMMENT ON COLUMN prod_standard_process.currency_code IS '币种代码';
COMMENT ON COLUMN prod_standard_process.status IS '状态（0=草稿/1=启用/2=停用）';
COMMENT ON COLUMN prod_standard_process.description IS '工序描述';
COMMENT ON COLUMN prod_standard_process.remark IS '备注';
COMMENT ON COLUMN prod_standard_process.created_at IS '创建时间';
COMMENT ON COLUMN prod_standard_process.updated_at IS '更新时间';
COMMENT ON COLUMN prod_standard_process.created_by IS '创建人ID';
COMMENT ON COLUMN prod_standard_process.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_standard_process.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_standard_process.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_standard_process.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_standard_process.version IS '版本号';
