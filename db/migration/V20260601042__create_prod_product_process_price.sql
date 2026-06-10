-- ============================================================
-- Flyway Migration Script
-- Version: V20260601042
-- Description: prod_product_process_price 商品工序价格表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-012-001-001
-- ============================================================

-- ============================================================
-- prod_product_process_price 商品工序价格表
-- 商品工序主从表，存储商品关联的制造工序步骤及其定价信息
-- 支持工序树形层级结构（parent_id自关联）
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_product_process_price (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    code            VARCHAR(50)     NOT NULL,
    name            VARCHAR(200)    NOT NULL,
    product_id      BIGINT          NOT NULL,
    parent_id       BIGINT,
    process_type    VARCHAR(30),
    seq_no          INT             NOT NULL DEFAULT 0,
    standard_hours  DECIMAL(18,8),
    standard_cost   DECIMAL(18,8),
    unit_price      DECIMAL(18,8),
    price_type      VARCHAR(30),
    currency_code   VARCHAR(10)     NOT NULL DEFAULT 'CNY',
    effective_date  DATE,
    expiry_date     DATE,
    is_default      BOOLEAN         NOT NULL DEFAULT FALSE,
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

COMMENT ON TABLE prod_product_process_price IS '商品工序价格表';
COMMENT ON COLUMN prod_product_process_price.id IS '主键ID';
COMMENT ON COLUMN prod_product_process_price.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_product_process_price.code IS '工序编码';
COMMENT ON COLUMN prod_product_process_price.name IS '工序名称';
COMMENT ON COLUMN prod_product_process_price.product_id IS '商品ID';
COMMENT ON COLUMN prod_product_process_price.parent_id IS '父工序ID（树形层级结构）';
COMMENT ON COLUMN prod_product_process_price.process_type IS '工序类型（internal=内部工序/outsource=委外工序）';
COMMENT ON COLUMN prod_product_process_price.seq_no IS '工序序号';
COMMENT ON COLUMN prod_product_process_price.standard_hours IS '标准工时';
COMMENT ON COLUMN prod_product_process_price.standard_cost IS '标准成本';
COMMENT ON COLUMN prod_product_process_price.unit_price IS '工序单价';
COMMENT ON COLUMN prod_product_process_price.price_type IS '价格类型';
COMMENT ON COLUMN prod_product_process_price.currency_code IS '币种代码';
COMMENT ON COLUMN prod_product_process_price.effective_date IS '生效日期';
COMMENT ON COLUMN prod_product_process_price.expiry_date IS '失效日期';
COMMENT ON COLUMN prod_product_process_price.is_default IS '是否默认（0-否/1-是）';
COMMENT ON COLUMN prod_product_process_price.status IS '状态（0-草稿/1-已生效/2-已失效）';
COMMENT ON COLUMN prod_product_process_price.description IS '工序描述';
COMMENT ON COLUMN prod_product_process_price.remark IS '备注';
COMMENT ON COLUMN prod_product_process_price.created_at IS '创建时间';
COMMENT ON COLUMN prod_product_process_price.updated_at IS '更新时间';
COMMENT ON COLUMN prod_product_process_price.created_by IS '创建人ID';
COMMENT ON COLUMN prod_product_process_price.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_product_process_price.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_product_process_price.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_product_process_price.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_product_process_price.version IS '版本号';
