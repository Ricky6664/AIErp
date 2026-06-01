-- ============================================================
-- Flyway Migration Script
-- Version: V20260601036
-- Description: prod_product_competitor 商品竞品表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- prod_product_competitor 商品竞品表
-- 竞品商品信息跟踪（竞品价格对比分析）
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_product_competitor (
    id                    BIGSERIAL       PRIMARY KEY,
    tenant_id             BIGINT          NOT NULL,
    -- 业务字段
    product_id            BIGINT          NOT NULL,
    competitor_name       VARCHAR(200)    NOT NULL,
    competitor_brand      VARCHAR(100),
    competitor_model      VARCHAR(100),
    competitor_spec       VARCHAR(200),
    competitor_product_code VARCHAR(50),
    competitor_price      DECIMAL(18,8),
    our_price             DECIMAL(18,8),
    price_diff_rate       DECIMAL(18,8),
    currency_code         VARCHAR(10)     NOT NULL DEFAULT 'CNY',
    unit_id               BIGINT,
    competitor_url        VARCHAR(500),
    source                VARCHAR(100),
    effective_date        DATE,
    expiry_date           DATE,
    status                SMALLINT        NOT NULL DEFAULT 0,
    remark                VARCHAR(500),
    -- 通用必含字段
    created_at            TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at            TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by            BIGINT,
    updated_by            BIGINT,
    is_deleted            BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id         BIGINT,
    owner_id              BIGINT,
    version               INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE prod_product_competitor IS '商品竞品表';
COMMENT ON COLUMN prod_product_competitor.id IS '主键ID';
COMMENT ON COLUMN prod_product_competitor.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_product_competitor.product_id IS '商品ID';
COMMENT ON COLUMN prod_product_competitor.competitor_name IS '竞品名称';
COMMENT ON COLUMN prod_product_competitor.competitor_brand IS '竞品品牌';
COMMENT ON COLUMN prod_product_competitor.competitor_model IS '竞品型号';
COMMENT ON COLUMN prod_product_competitor.competitor_spec IS '竞品规格';
COMMENT ON COLUMN prod_product_competitor.competitor_product_code IS '竞品商品编码';
COMMENT ON COLUMN prod_product_competitor.competitor_price IS '竞品价格';
COMMENT ON COLUMN prod_product_competitor.our_price IS '我方价格';
COMMENT ON COLUMN prod_product_competitor.price_diff_rate IS '价格差异率(%)';
COMMENT ON COLUMN prod_product_competitor.currency_code IS '币种代码';
COMMENT ON COLUMN prod_product_competitor.unit_id IS '单位ID';
COMMENT ON COLUMN prod_product_competitor.competitor_url IS '竞品链接';
COMMENT ON COLUMN prod_product_competitor.source IS '信息来源';
COMMENT ON COLUMN prod_product_competitor.effective_date IS '生效日期';
COMMENT ON COLUMN prod_product_competitor.expiry_date IS '失效日期';
COMMENT ON COLUMN prod_product_competitor.status IS '状态（0-草稿/1-已生效/2-已失效）';
COMMENT ON COLUMN prod_product_competitor.remark IS '备注';
COMMENT ON COLUMN prod_product_competitor.created_at IS '创建时间';
COMMENT ON COLUMN prod_product_competitor.updated_at IS '更新时间';
COMMENT ON COLUMN prod_product_competitor.created_by IS '创建人ID';
COMMENT ON COLUMN prod_product_competitor.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_product_competitor.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_product_competitor.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_product_competitor.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_product_competitor.version IS '版本号';
