-- ============================================================
-- Flyway Migration Script
-- Version: V20260601051
-- Description: prod_product_attribute 商品属性表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- prod_product_attribute 商品属性表
-- 商品自定义属性定义（属性名/排序号），支持树形层级
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_product_attribute (
    id                    BIGSERIAL       PRIMARY KEY,
    tenant_id             BIGINT          NOT NULL,
    -- 业务字段
    code                  VARCHAR(50)     NOT NULL,
    name                  VARCHAR(200)    NOT NULL,
    parent_id             BIGINT,
    sort_order            INT             NOT NULL DEFAULT 0,
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

COMMENT ON TABLE prod_product_attribute IS '商品属性表';
COMMENT ON COLUMN prod_product_attribute.id IS '主键ID';
COMMENT ON COLUMN prod_product_attribute.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_product_attribute.code IS '属性编码';
COMMENT ON COLUMN prod_product_attribute.name IS '属性名称';
COMMENT ON COLUMN prod_product_attribute.parent_id IS '父属性ID（树形结构）';
COMMENT ON COLUMN prod_product_attribute.sort_order IS '排序号';
COMMENT ON COLUMN prod_product_attribute.status IS '状态（0-启用/1-停用）';
COMMENT ON COLUMN prod_product_attribute.remark IS '备注';
COMMENT ON COLUMN prod_product_attribute.created_at IS '创建时间';
COMMENT ON COLUMN prod_product_attribute.updated_at IS '更新时间';
COMMENT ON COLUMN prod_product_attribute.created_by IS '创建人ID';
COMMENT ON COLUMN prod_product_attribute.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_product_attribute.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_product_attribute.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_product_attribute.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_product_attribute.version IS '版本号';
