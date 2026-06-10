-- ============================================================
-- Flyway Migration Script
-- Version: V20260603001
-- Description: inv_assembly_detail组装主从表建表DDL
-- Author: AI
-- Date: 2026-06-03
-- Task: P0-003-007-011-001-001
-- ============================================================

-- ============================================================
-- inv_assembly 组装主表
-- ============================================================
CREATE TABLE IF NOT EXISTS inv_assembly (
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    -- 业务字段
    order_no            VARCHAR(50)     NOT NULL,
    order_date          DATE            NOT NULL,
    warehouse_id        BIGINT          NOT NULL,
    handling_dept_id    BIGINT,
    handler_id          BIGINT,
    assembly_date       DATE,
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

COMMENT ON TABLE inv_assembly IS '组装主表';
COMMENT ON COLUMN inv_assembly.id IS '主键ID';
COMMENT ON COLUMN inv_assembly.tenant_id IS '租户ID';
COMMENT ON COLUMN inv_assembly.order_no IS '单据编号';
COMMENT ON COLUMN inv_assembly.order_date IS '单据日期';
COMMENT ON COLUMN inv_assembly.warehouse_id IS '仓库ID';
COMMENT ON COLUMN inv_assembly.handling_dept_id IS '经办部门ID';
COMMENT ON COLUMN inv_assembly.handler_id IS '经办人ID';
COMMENT ON COLUMN inv_assembly.assembly_date IS '组装日期';
COMMENT ON COLUMN inv_assembly.total_qty IS '总数量';
COMMENT ON COLUMN inv_assembly.total_amount IS '总金额';
COMMENT ON COLUMN inv_assembly.status IS '单据状态：0=草稿/1=已审核/2=已完成';
COMMENT ON COLUMN inv_assembly.approver_id IS '审核人ID';
COMMENT ON COLUMN inv_assembly.approve_date IS '审核日期';
COMMENT ON COLUMN inv_assembly.remark IS '备注';
COMMENT ON COLUMN inv_assembly.created_at IS '创建时间';
COMMENT ON COLUMN inv_assembly.updated_at IS '更新时间';
COMMENT ON COLUMN inv_assembly.created_by IS '创建人ID';
COMMENT ON COLUMN inv_assembly.updated_by IS '修改人ID';
COMMENT ON COLUMN inv_assembly.is_deleted IS '是否删除';
COMMENT ON COLUMN inv_assembly.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN inv_assembly.owner_id IS '数据负责人ID';
COMMENT ON COLUMN inv_assembly.version IS '版本号';

-- ============================================================
-- inv_assembly_detail 组装从表
-- ============================================================
CREATE TABLE IF NOT EXISTS inv_assembly_detail (
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
    qty                 DECIMAL(18,8)   DEFAULT 0,
    price               DECIMAL(18,8)   DEFAULT 0,
    amount              DECIMAL(18,8)   DEFAULT 0,
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

COMMENT ON TABLE inv_assembly_detail IS '组装从表';
COMMENT ON COLUMN inv_assembly_detail.id IS '主键ID';
COMMENT ON COLUMN inv_assembly_detail.tenant_id IS '租户ID';
COMMENT ON COLUMN inv_assembly_detail.order_id IS '主表ID';
COMMENT ON COLUMN inv_assembly_detail.line_no IS '行号';
COMMENT ON COLUMN inv_assembly_detail.code IS '明细编码';
COMMENT ON COLUMN inv_assembly_detail.product_id IS '商品ID';
COMMENT ON COLUMN inv_assembly_detail.warehouse_id IS '仓库ID';
COMMENT ON COLUMN inv_assembly_detail.location_id IS '库位ID';
COMMENT ON COLUMN inv_assembly_detail.batch_no IS '批次号';
COMMENT ON COLUMN inv_assembly_detail.qty IS '数量';
COMMENT ON COLUMN inv_assembly_detail.price IS '单价';
COMMENT ON COLUMN inv_assembly_detail.amount IS '金额';
COMMENT ON COLUMN inv_assembly_detail.product_code IS '商品编码快照';
COMMENT ON COLUMN inv_assembly_detail.product_name IS '商品名称快照';
COMMENT ON COLUMN inv_assembly_detail.model IS '型号快照';
COMMENT ON COLUMN inv_assembly_detail.spec IS '规格快照';
COMMENT ON COLUMN inv_assembly_detail.brand IS '品牌快照';
COMMENT ON COLUMN inv_assembly_detail.unit_id IS '单位ID快照';
COMMENT ON COLUMN inv_assembly_detail.unit IS '单位名称快照';
COMMENT ON COLUMN inv_assembly_detail.is_multi_unit IS '是否多单位快照';
COMMENT ON COLUMN inv_assembly_detail.conversion_rate IS '转换比例快照';
COMMENT ON COLUMN inv_assembly_detail.base_unit_id IS '基础单位ID快照';
COMMENT ON COLUMN inv_assembly_detail.base_qty IS '换算数量';
COMMENT ON COLUMN inv_assembly_detail.remark IS '备注';
COMMENT ON COLUMN inv_assembly_detail.created_at IS '创建时间';
COMMENT ON COLUMN inv_assembly_detail.updated_at IS '更新时间';
COMMENT ON COLUMN inv_assembly_detail.created_by IS '创建人ID';
COMMENT ON COLUMN inv_assembly_detail.updated_by IS '修改人ID';
COMMENT ON COLUMN inv_assembly_detail.is_deleted IS '是否删除';
COMMENT ON COLUMN inv_assembly_detail.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN inv_assembly_detail.owner_id IS '数据负责人ID';
COMMENT ON COLUMN inv_assembly_detail.version IS '版本号';
