-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: inv_stocktaking_detail盘点主从表建表DDL
-- Author: AI
-- Date: 2026-06-02
-- Task: P0-003-007-007-001-001
-- ============================================================

-- ============================================================
-- inv_stocktaking 盘点主表
-- ============================================================
CREATE TABLE IF NOT EXISTS inv_stocktaking (
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    -- 业务字段
    order_no            VARCHAR(50)     NOT NULL,
    order_date          DATE            NOT NULL,
    warehouse_id        BIGINT          NOT NULL,
    handling_dept_id    BIGINT,
    handler_id          BIGINT,
    stocktaking_date    DATE,
    total_qty           DECIMAL(18,8)   DEFAULT 0,
    total_amount        DECIMAL(18,8)   DEFAULT 0,
    status              SMALLINT        DEFAULT 0,
    approver_id         BIGINT,
    approve_date        TIMESTAMP,
    remark              VARCHAR(500),
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

COMMENT ON TABLE inv_stocktaking IS '盘点主表';
COMMENT ON COLUMN inv_stocktaking.id IS '主键ID';
COMMENT ON COLUMN inv_stocktaking.tenant_id IS '租户ID';
COMMENT ON COLUMN inv_stocktaking.order_no IS '单据编号';
COMMENT ON COLUMN inv_stocktaking.order_date IS '单据日期';
COMMENT ON COLUMN inv_stocktaking.warehouse_id IS '仓库ID';
COMMENT ON COLUMN inv_stocktaking.handling_dept_id IS '经办部门ID';
COMMENT ON COLUMN inv_stocktaking.handler_id IS '经办人ID';
COMMENT ON COLUMN inv_stocktaking.stocktaking_date IS '盘点日期';
COMMENT ON COLUMN inv_stocktaking.total_qty IS '总数量';
COMMENT ON COLUMN inv_stocktaking.total_amount IS '总金额';
COMMENT ON COLUMN inv_stocktaking.status IS '单据状态：0=草稿/1=已审核/2=已完成';
COMMENT ON COLUMN inv_stocktaking.approver_id IS '审核人ID';
COMMENT ON COLUMN inv_stocktaking.approve_date IS '审核日期';
COMMENT ON COLUMN inv_stocktaking.remark IS '备注';
COMMENT ON COLUMN inv_stocktaking.created_at IS '创建时间';
COMMENT ON COLUMN inv_stocktaking.updated_at IS '更新时间';
COMMENT ON COLUMN inv_stocktaking.created_by IS '创建人ID';
COMMENT ON COLUMN inv_stocktaking.updated_by IS '修改人ID';
COMMENT ON COLUMN inv_stocktaking.is_deleted IS '是否删除';
COMMENT ON COLUMN inv_stocktaking.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN inv_stocktaking.owner_id IS '数据负责人ID';
COMMENT ON COLUMN inv_stocktaking.version IS '版本号';

-- ============================================================
-- inv_stocktaking_detail 盘点从表
-- ============================================================
CREATE TABLE IF NOT EXISTS inv_stocktaking_detail (
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    -- 业务字段
    order_id            BIGINT          NOT NULL,
    line_no             INT             DEFAULT 1,
    code                VARCHAR(50),
    product_id          BIGINT          NOT NULL,
    warehouse_id        BIGINT          NOT NULL,
    location_id         BIGINT,
    batch_no            VARCHAR(50),
    stock_qty           DECIMAL(18,8)   DEFAULT 0,
    actual_qty          DECIMAL(18,8)   DEFAULT 0,
    diff_qty            DECIMAL(18,8)   DEFAULT 0,
    price               DECIMAL(18,8)   DEFAULT 0,
    stock_amount        DECIMAL(18,8)   DEFAULT 0,
    actual_amount       DECIMAL(18,8)   DEFAULT 0,
    diff_amount         DECIMAL(18,8)   DEFAULT 0,
    -- 商品快照字段
    product_code        VARCHAR(50),
    product_name        VARCHAR(100),
    model               VARCHAR(100),
    spec                VARCHAR(100),
    brand               VARCHAR(50),
    unit_id             BIGINT,
    unit                VARCHAR(30),
    is_multi_unit       BOOLEAN,
    conversion_rate     DECIMAL(18,8),
    base_unit_id        BIGINT,
    base_qty            DECIMAL(18,8),
    remark              VARCHAR(500),
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

COMMENT ON TABLE inv_stocktaking_detail IS '盘点从表';
COMMENT ON COLUMN inv_stocktaking_detail.id IS '主键ID';
COMMENT ON COLUMN inv_stocktaking_detail.tenant_id IS '租户ID';
COMMENT ON COLUMN inv_stocktaking_detail.order_id IS '主表ID';
COMMENT ON COLUMN inv_stocktaking_detail.line_no IS '行号';
COMMENT ON COLUMN inv_stocktaking_detail.code IS '明细编码';
COMMENT ON COLUMN inv_stocktaking_detail.product_id IS '商品ID';
COMMENT ON COLUMN inv_stocktaking_detail.warehouse_id IS '仓库ID';
COMMENT ON COLUMN inv_stocktaking_detail.location_id IS '库位ID';
COMMENT ON COLUMN inv_stocktaking_detail.batch_no IS '批次号';
COMMENT ON COLUMN inv_stocktaking_detail.stock_qty IS '账面数量';
COMMENT ON COLUMN inv_stocktaking_detail.actual_qty IS '实盘数量';
COMMENT ON COLUMN inv_stocktaking_detail.diff_qty IS '差异数量';
COMMENT ON COLUMN inv_stocktaking_detail.price IS '单价';
COMMENT ON COLUMN inv_stocktaking_detail.stock_amount IS '账面金额';
COMMENT ON COLUMN inv_stocktaking_detail.actual_amount IS '实盘金额';
COMMENT ON COLUMN inv_stocktaking_detail.diff_amount IS '差异金额';
COMMENT ON COLUMN inv_stocktaking_detail.product_code IS '商品编码快照';
COMMENT ON COLUMN inv_stocktaking_detail.product_name IS '商品名称快照';
COMMENT ON COLUMN inv_stocktaking_detail.model IS '型号快照';
COMMENT ON COLUMN inv_stocktaking_detail.spec IS '规格快照';
COMMENT ON COLUMN inv_stocktaking_detail.brand IS '品牌快照';
COMMENT ON COLUMN inv_stocktaking_detail.unit_id IS '单位ID快照';
COMMENT ON COLUMN inv_stocktaking_detail.unit IS '单位名称快照';
COMMENT ON COLUMN inv_stocktaking_detail.is_multi_unit IS '是否多单位快照';
COMMENT ON COLUMN inv_stocktaking_detail.conversion_rate IS '转换比例快照';
COMMENT ON COLUMN inv_stocktaking_detail.base_unit_id IS '基础单位ID快照';
COMMENT ON COLUMN inv_stocktaking_detail.base_qty IS '换算数量';
COMMENT ON COLUMN inv_stocktaking_detail.remark IS '备注';
COMMENT ON COLUMN inv_stocktaking_detail.created_at IS '创建时间';
COMMENT ON COLUMN inv_stocktaking_detail.updated_at IS '更新时间';
COMMENT ON COLUMN inv_stocktaking_detail.created_by IS '创建人ID';
COMMENT ON COLUMN inv_stocktaking_detail.updated_by IS '修改人ID';
COMMENT ON COLUMN inv_stocktaking_detail.is_deleted IS '是否删除';
COMMENT ON COLUMN inv_stocktaking_detail.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN inv_stocktaking_detail.owner_id IS '数据负责人ID';
COMMENT ON COLUMN inv_stocktaking_detail.version IS '版本号';
