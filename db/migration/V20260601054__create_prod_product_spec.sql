-- ============================================================
-- Flyway Migration Script
-- Version: V20260601054
-- Description: prod_product_spec 商品规格表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- prod_product_spec 商品规格表
-- 商品规格定义（规格名称/规格值/排序号）
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_product_spec (
    id                    BIGSERIAL       PRIMARY KEY,
    tenant_id             BIGINT          NOT NULL,
    -- 业务字段
    code                  VARCHAR(50)     NOT NULL,
    name                  VARCHAR(200)    NOT NULL,
    value                 VARCHAR(500)    NOT NULL,
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

COMMENT ON TABLE prod_product_spec IS '商品规格表';
COMMENT ON COLUMN prod_product_spec.id IS '主键ID';
COMMENT ON COLUMN prod_product_spec.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_product_spec.code IS '规格编码';
COMMENT ON COLUMN prod_product_spec.name IS '规格名称';
COMMENT ON COLUMN prod_product_spec.value IS '规格值';
COMMENT ON COLUMN prod_product_spec.sort_order IS '排序号';
COMMENT ON COLUMN prod_product_spec.status IS '状态（0-启用/1-停用）';
COMMENT ON COLUMN prod_product_spec.remark IS '备注';
COMMENT ON COLUMN prod_product_spec.created_at IS '创建时间';
COMMENT ON COLUMN prod_product_spec.updated_at IS '更新时间';
COMMENT ON COLUMN prod_product_spec.created_by IS '创建人ID';
COMMENT ON COLUMN prod_product_spec.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_product_spec.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_product_spec.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_product_spec.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_product_spec.version IS '版本号';
