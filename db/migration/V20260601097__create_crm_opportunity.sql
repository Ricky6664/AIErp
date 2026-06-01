-- ============================================================
-- Flyway Migration Script
-- Version: V20260601097
-- Description: crm_opportunity 客户机会表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-010-001-001
-- ============================================================

-- ============================================================
-- crm_opportunity 客户机会表
-- 存储客户销售机会信息（客户ID/机会名称/销售员/预计金额/阶段/成交概率等）
-- ============================================================
CREATE TABLE IF NOT EXISTS crm_opportunity (
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    -- 业务字段
    customer_id         BIGINT          NOT NULL,
    code                VARCHAR(50)     NOT NULL,
    opportunity_name    VARCHAR(200)    NOT NULL,
    sales_person_id     BIGINT,
    estimated_amount    DECIMAL(18,8),
    estimated_close_date DATE,
    stage               VARCHAR(50)     NOT NULL,
    probability         INT,
    source              VARCHAR(50),
    description         VARCHAR(500),
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

COMMENT ON TABLE crm_opportunity IS '客户机会表';
COMMENT ON COLUMN crm_opportunity.id IS '主键ID';
COMMENT ON COLUMN crm_opportunity.tenant_id IS '租户ID';
COMMENT ON COLUMN crm_opportunity.customer_id IS '客户ID';
COMMENT ON COLUMN crm_opportunity.code IS '机会编码';
COMMENT ON COLUMN crm_opportunity.opportunity_name IS '机会名称';
COMMENT ON COLUMN crm_opportunity.sales_person_id IS '销售员ID';
COMMENT ON COLUMN crm_opportunity.estimated_amount IS '预计金额';
COMMENT ON COLUMN crm_opportunity.estimated_close_date IS '预计成交日期';
COMMENT ON COLUMN crm_opportunity.stage IS '阶段（初步接触/需求确认/方案报价/商务谈判/赢单/输单）';
COMMENT ON COLUMN crm_opportunity.probability IS '成交概率（0-100）';
COMMENT ON COLUMN crm_opportunity.source IS '机会来源';
COMMENT ON COLUMN crm_opportunity.description IS '描述';
COMMENT ON COLUMN crm_opportunity.order_no IS '关联单据号';
COMMENT ON COLUMN crm_opportunity.order_date IS '关联单据日期';
COMMENT ON COLUMN crm_opportunity.status IS '状态（0-草稿/1-已审核/2-已完成/3-已作废）';
COMMENT ON COLUMN crm_opportunity.remark IS '备注';
COMMENT ON COLUMN crm_opportunity.product_id IS '商品ID';
COMMENT ON COLUMN crm_opportunity.product_code IS '商品编码';
COMMENT ON COLUMN crm_opportunity.product_name IS '商品名称';
COMMENT ON COLUMN crm_opportunity.model IS '型号';
COMMENT ON COLUMN crm_opportunity.spec IS '规格';
COMMENT ON COLUMN crm_opportunity.brand IS '品牌';
COMMENT ON COLUMN crm_opportunity.unit_id IS '单位ID';
COMMENT ON COLUMN crm_opportunity.unit IS '单位名称';
COMMENT ON COLUMN crm_opportunity.qty IS '数量';
COMMENT ON COLUMN crm_opportunity.is_multi_unit IS '是否多单位';
COMMENT ON COLUMN crm_opportunity.conversion_rate IS '换算率';
COMMENT ON COLUMN crm_opportunity.base_unit_id IS '基本单位ID';
COMMENT ON COLUMN crm_opportunity.base_qty IS '基本数量';
COMMENT ON COLUMN crm_opportunity.created_at IS '创建时间';
COMMENT ON COLUMN crm_opportunity.updated_at IS '更新时间';
COMMENT ON COLUMN crm_opportunity.created_by IS '创建人ID';
COMMENT ON COLUMN crm_opportunity.updated_by IS '修改人ID';
COMMENT ON COLUMN crm_opportunity.is_deleted IS '是否删除';
COMMENT ON COLUMN crm_opportunity.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN crm_opportunity.owner_id IS '数据负责人ID';
COMMENT ON COLUMN crm_opportunity.version IS '版本号';
