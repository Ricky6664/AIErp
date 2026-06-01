-- ============================================================
-- Flyway Migration Script
-- Version: V20260601018
-- Description: prod_product_control商品控制策略表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- prod_product_control 商品控制策略表
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_product_control (
    id               BIGSERIAL       PRIMARY KEY,
    tenant_id        BIGINT          NOT NULL,
    -- 业务字段
    product_id       BIGINT          NOT NULL,
    is_inventory     BOOLEAN         NOT NULL DEFAULT TRUE,
    is_batch_manage  BOOLEAN         NOT NULL DEFAULT FALSE,
    is_serial_manage BOOLEAN         NOT NULL DEFAULT FALSE,
    -- 通用必含字段
    created_at       TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by       BIGINT,
    updated_by       BIGINT,
    is_deleted       BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id    BIGINT,
    owner_id         BIGINT,
    version          INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE prod_product_control IS '商品控制策略表';
COMMENT ON COLUMN prod_product_control.id IS '主键ID';
COMMENT ON COLUMN prod_product_control.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_product_control.product_id IS '商品ID';
COMMENT ON COLUMN prod_product_control.is_inventory IS '是否启用库存管理';
COMMENT ON COLUMN prod_product_control.is_batch_manage IS '是否启用批次管理';
COMMENT ON COLUMN prod_product_control.is_serial_manage IS '是否启用序列号管理';
COMMENT ON COLUMN prod_product_control.created_at IS '创建时间';
COMMENT ON COLUMN prod_product_control.updated_at IS '更新时间';
COMMENT ON COLUMN prod_product_control.created_by IS '创建人ID';
COMMENT ON COLUMN prod_product_control.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_product_control.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_product_control.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_product_control.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_product_control.version IS '版本号';
