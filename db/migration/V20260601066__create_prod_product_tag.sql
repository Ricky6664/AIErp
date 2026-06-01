-- ============================================================
-- Flyway Migration Script
-- Version: V20260601066
-- Description: prod_product_tag 商品标签关联表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-020-001-001
-- ============================================================

-- ============================================================
-- prod_product_tag 商品标签关联表
-- 商品与标签的关联（标签定义ID/标签值/排序号）
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_product_tag (
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    -- 业务字段
    product_id          BIGINT          NOT NULL,
    tag_definition_id   BIGINT,
    tag_value           VARCHAR(200),
    sort_order          INT             NOT NULL DEFAULT 0,
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

COMMENT ON TABLE prod_product_tag IS '商品标签关联表';
COMMENT ON COLUMN prod_product_tag.id IS '主键ID';
COMMENT ON COLUMN prod_product_tag.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_product_tag.product_id IS '商品ID';
COMMENT ON COLUMN prod_product_tag.tag_definition_id IS '标签定义ID';
COMMENT ON COLUMN prod_product_tag.tag_value IS '标签值';
COMMENT ON COLUMN prod_product_tag.sort_order IS '排序号';
COMMENT ON COLUMN prod_product_tag.created_at IS '创建时间';
COMMENT ON COLUMN prod_product_tag.updated_at IS '更新时间';
COMMENT ON COLUMN prod_product_tag.created_by IS '创建人ID';
COMMENT ON COLUMN prod_product_tag.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_product_tag.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_product_tag.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_product_tag.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_product_tag.version IS '版本号';
