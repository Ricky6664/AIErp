-- ============================================================
-- Flyway Migration Script
-- Version: V20260601052
-- Description: prod_product_attribute_value 商品属性值表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- prod_product_attribute_value 商品属性值表
-- 商品属性值（属性ID/属性值/排序号）
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_product_attribute_value (
    id                    BIGSERIAL       PRIMARY KEY,
    tenant_id             BIGINT          NOT NULL,
    -- 业务字段
    attribute_id          BIGINT          NOT NULL,
    value                 VARCHAR(200)    NOT NULL,
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

COMMENT ON TABLE prod_product_attribute_value IS '商品属性值表';
COMMENT ON COLUMN prod_product_attribute_value.id IS '主键ID';
COMMENT ON COLUMN prod_product_attribute_value.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_product_attribute_value.attribute_id IS '属性ID（关联prod_product_attribute）';
COMMENT ON COLUMN prod_product_attribute_value.value IS '属性值';
COMMENT ON COLUMN prod_product_attribute_value.sort_order IS '排序号';
COMMENT ON COLUMN prod_product_attribute_value.status IS '状态（0-启用/1-停用）';
COMMENT ON COLUMN prod_product_attribute_value.remark IS '备注';
COMMENT ON COLUMN prod_product_attribute_value.created_at IS '创建时间';
COMMENT ON COLUMN prod_product_attribute_value.updated_at IS '更新时间';
COMMENT ON COLUMN prod_product_attribute_value.created_by IS '创建人ID';
COMMENT ON COLUMN prod_product_attribute_value.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_product_attribute_value.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_product_attribute_value.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_product_attribute_value.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_product_attribute_value.version IS '版本号';
