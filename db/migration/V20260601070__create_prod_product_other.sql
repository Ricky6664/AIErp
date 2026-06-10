-- ============================================================
-- Flyway Migration Script
-- Version: V20260601070
-- Description: prod_product_other 商品其他信息表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-022-001-001
-- ============================================================

-- ============================================================
-- prod_product_other 商品其他信息表
-- 商品扩展信息（HS编码/条码/产地/毛净重/体积/包装/认证/保质期/存储条件）
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_product_other (
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    -- 业务字段
    product_id          BIGINT          NOT NULL,
    code                VARCHAR(50)     NOT NULL,
    hs_code             VARCHAR(50),
    barcode             VARCHAR(100),
    origin              VARCHAR(200),
    gross_weight        DECIMAL(18,8),
    net_weight          DECIMAL(18,8),
    volume              DECIMAL(18,8),
    packaging           VARCHAR(200),
    certification       VARCHAR(500),
    shelf_life          INT,
    storage_condition   VARCHAR(500),
    status              SMALLINT        NOT NULL DEFAULT 0,
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

COMMENT ON TABLE prod_product_other IS '商品其他信息表';
COMMENT ON COLUMN prod_product_other.id IS '主键ID';
COMMENT ON COLUMN prod_product_other.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_product_other.product_id IS '关联商品ID';
COMMENT ON COLUMN prod_product_other.code IS '编码';
COMMENT ON COLUMN prod_product_other.hs_code IS 'HS编码';
COMMENT ON COLUMN prod_product_other.barcode IS '条码';
COMMENT ON COLUMN prod_product_other.origin IS '产地';
COMMENT ON COLUMN prod_product_other.gross_weight IS '毛重';
COMMENT ON COLUMN prod_product_other.net_weight IS '净重';
COMMENT ON COLUMN prod_product_other.volume IS '体积';
COMMENT ON COLUMN prod_product_other.packaging IS '包装';
COMMENT ON COLUMN prod_product_other.certification IS '认证';
COMMENT ON COLUMN prod_product_other.shelf_life IS '保质期（天数）';
COMMENT ON COLUMN prod_product_other.storage_condition IS '存储条件';
COMMENT ON COLUMN prod_product_other.status IS '状态（0=禁用/1=启用）';
COMMENT ON COLUMN prod_product_other.remark IS '备注';
COMMENT ON COLUMN prod_product_other.created_at IS '创建时间';
COMMENT ON COLUMN prod_product_other.updated_at IS '更新时间';
COMMENT ON COLUMN prod_product_other.created_by IS '创建人ID';
COMMENT ON COLUMN prod_product_other.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_product_other.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_product_other.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_product_other.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_product_other.version IS '版本号';
