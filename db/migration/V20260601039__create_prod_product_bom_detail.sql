-- ============================================================
-- Flyway Migration Script
-- Version: V20260601039
-- Description: prod_product_bom_detail 商品BOM明细表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-011-001-001
-- ============================================================

-- ============================================================
-- prod_product_bom_detail 商品BOM明细表
-- BOM从表，记录BOM的组成子件商品明细
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_product_bom_detail (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    bom_id          BIGINT          NOT NULL,
    sub_product_id  BIGINT          NOT NULL,
    qty             DECIMAL(18,8)   NOT NULL,
    loss_rate       DECIMAL(18,8)   DEFAULT 0,
    -- 单据字段
    order_no        VARCHAR(50)     NOT NULL,
    order_date      DATE            NOT NULL,
    status          SMALLINT        NOT NULL DEFAULT 0,
    remark          VARCHAR(500),
    -- 商品快照字段
    product_id      BIGINT,
    product_code    VARCHAR(50),
    product_name    VARCHAR(200),
    model           VARCHAR(100),
    spec            VARCHAR(200),
    brand           VARCHAR(100),
    unit_id         BIGINT,
    unit            VARCHAR(50),
    is_multi_unit   BOOLEAN         DEFAULT FALSE,
    conversion_rate DECIMAL(18,8)   DEFAULT 1,
    base_unit_id    BIGINT,
    base_qty        DECIMAL(18,8)   DEFAULT 0,
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

COMMENT ON TABLE prod_product_bom_detail IS '商品BOM明细表';
COMMENT ON COLUMN prod_product_bom_detail.id IS '主键ID';
COMMENT ON COLUMN prod_product_bom_detail.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_product_bom_detail.bom_id IS 'BOM主表ID';
COMMENT ON COLUMN prod_product_bom_detail.sub_product_id IS '子件商品ID';
COMMENT ON COLUMN prod_product_bom_detail.qty IS '用量';
COMMENT ON COLUMN prod_product_bom_detail.loss_rate IS '损耗率';
COMMENT ON COLUMN prod_product_bom_detail.order_no IS '单据编号';
COMMENT ON COLUMN prod_product_bom_detail.order_date IS '单据日期';
COMMENT ON COLUMN prod_product_bom_detail.status IS '状态（0-草稿/1-已审核/2-已完成）';
COMMENT ON COLUMN prod_product_bom_detail.remark IS '备注';
COMMENT ON COLUMN prod_product_bom_detail.product_id IS '商品ID（快照）';
COMMENT ON COLUMN prod_product_bom_detail.product_code IS '商品编码（快照）';
COMMENT ON COLUMN prod_product_bom_detail.product_name IS '商品名称（快照）';
COMMENT ON COLUMN prod_product_bom_detail.model IS '型号（快照）';
COMMENT ON COLUMN prod_product_bom_detail.spec IS '规格（快照）';
COMMENT ON COLUMN prod_product_bom_detail.brand IS '品牌（快照）';
COMMENT ON COLUMN prod_product_bom_detail.unit_id IS '单位ID（快照）';
COMMENT ON COLUMN prod_product_bom_detail.unit IS '单位名称（快照）';
COMMENT ON COLUMN prod_product_bom_detail.is_multi_unit IS '是否多单位（快照）';
COMMENT ON COLUMN prod_product_bom_detail.conversion_rate IS '换算率（快照）';
COMMENT ON COLUMN prod_product_bom_detail.base_unit_id IS '基本单位ID（快照）';
COMMENT ON COLUMN prod_product_bom_detail.base_qty IS '基本单位数量（快照）';
COMMENT ON COLUMN prod_product_bom_detail.created_at IS '创建时间';
COMMENT ON COLUMN prod_product_bom_detail.updated_at IS '更新时间';
COMMENT ON COLUMN prod_product_bom_detail.created_by IS '创建人ID';
COMMENT ON COLUMN prod_product_bom_detail.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_product_bom_detail.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_product_bom_detail.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_product_bom_detail.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_product_bom_detail.version IS '版本号';
