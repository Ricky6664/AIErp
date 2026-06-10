-- ============================================================
-- Flyway Migration Script
-- Version: V20260602022
-- Description: srm_supplier_finance 供应商财务配置表建表DDL
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-009-001-001
-- ============================================================

-- ============================================================
-- srm_supplier_finance 供应商财务配置表
-- 存储供应商财务配置信息（税号/信用额度/付款条件等）
-- ============================================================
CREATE TABLE IF NOT EXISTS srm_supplier_finance (
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    -- 业务字段
    supplier_id         BIGINT          NOT NULL,
    code                VARCHAR(50)     NOT NULL,
    tax_no              VARCHAR(50),
    credit_limit        DECIMAL(18,8),
    payment_terms       VARCHAR(100),
    -- 单据主表字段
    order_no            VARCHAR(50),
    order_date          DATE,
    status              SMALLINT        NOT NULL DEFAULT 0,
    remark              VARCHAR(500),
    -- 商品快照字段
    product_id          BIGINT,
    product_code        VARCHAR(50),
    product_name        VARCHAR(200),
    model               VARCHAR(100),
    spec                VARCHAR(100),
    brand               VARCHAR(100),
    unit_id             BIGINT,
    unit                VARCHAR(50),
    qty                 DECIMAL(18,8),
    is_multi_unit       BOOLEAN         NOT NULL DEFAULT FALSE,
    conversion_rate     DECIMAL(18,8),
    base_unit_id        BIGINT,
    base_qty            DECIMAL(18,8),
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

COMMENT ON TABLE srm_supplier_finance IS '供应商财务配置表';
COMMENT ON COLUMN srm_supplier_finance.id IS '主键ID';
COMMENT ON COLUMN srm_supplier_finance.tenant_id IS '租户ID';
COMMENT ON COLUMN srm_supplier_finance.supplier_id IS '供应商ID';
COMMENT ON COLUMN srm_supplier_finance.code IS '财务配置编码';
COMMENT ON COLUMN srm_supplier_finance.tax_no IS '税号';
COMMENT ON COLUMN srm_supplier_finance.credit_limit IS '信用额度';
COMMENT ON COLUMN srm_supplier_finance.payment_terms IS '付款条件';
COMMENT ON COLUMN srm_supplier_finance.order_no IS '关联单据号';
COMMENT ON COLUMN srm_supplier_finance.order_date IS '关联单据日期';
COMMENT ON COLUMN srm_supplier_finance.status IS '状态（0-草稿/1-已审核/2-已完成/3-已作废）';
COMMENT ON COLUMN srm_supplier_finance.remark IS '备注';
COMMENT ON COLUMN srm_supplier_finance.product_id IS '商品ID';
COMMENT ON COLUMN srm_supplier_finance.product_code IS '商品编码';
COMMENT ON COLUMN srm_supplier_finance.product_name IS '商品名称';
COMMENT ON COLUMN srm_supplier_finance.model IS '型号';
COMMENT ON COLUMN srm_supplier_finance.spec IS '规格';
COMMENT ON COLUMN srm_supplier_finance.brand IS '品牌';
COMMENT ON COLUMN srm_supplier_finance.unit_id IS '单位ID';
COMMENT ON COLUMN srm_supplier_finance.unit IS '单位名称';
COMMENT ON COLUMN srm_supplier_finance.qty IS '数量';
COMMENT ON COLUMN srm_supplier_finance.is_multi_unit IS '是否多单位';
COMMENT ON COLUMN srm_supplier_finance.conversion_rate IS '换算率';
COMMENT ON COLUMN srm_supplier_finance.base_unit_id IS '基本单位ID';
COMMENT ON COLUMN srm_supplier_finance.base_qty IS '基本数量';
COMMENT ON COLUMN srm_supplier_finance.created_at IS '创建时间';
COMMENT ON COLUMN srm_supplier_finance.updated_at IS '更新时间';
COMMENT ON COLUMN srm_supplier_finance.created_by IS '创建人ID';
COMMENT ON COLUMN srm_supplier_finance.updated_by IS '修改人ID';
COMMENT ON COLUMN srm_supplier_finance.is_deleted IS '是否删除';
COMMENT ON COLUMN srm_supplier_finance.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN srm_supplier_finance.owner_id IS '数据负责人ID';
COMMENT ON COLUMN srm_supplier_finance.version IS '版本号';
