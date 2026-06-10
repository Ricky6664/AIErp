-- ============================================================
-- Flyway Migration Script
-- Version: V20260601063
-- Description: prod_product_relation 商品关联表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-019-001-001
-- ============================================================

-- ============================================================
-- prod_product_relation 商品关联表
-- 商品间关联关系（替代品/配件/搭配推荐/关联销售等）
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_product_relation (
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    -- 业务字段
    product_id          BIGINT          NOT NULL,
    related_product_id  BIGINT          NOT NULL,
    code                VARCHAR(50)     NOT NULL,
    relation_type       VARCHAR(50),
    is_bidirectional    BOOLEAN         NOT NULL DEFAULT FALSE,
    sort_order          INT             NOT NULL DEFAULT 0,
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

COMMENT ON TABLE prod_product_relation IS '商品关联表';
COMMENT ON COLUMN prod_product_relation.id IS '主键ID';
COMMENT ON COLUMN prod_product_relation.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_product_relation.product_id IS '商品ID';
COMMENT ON COLUMN prod_product_relation.related_product_id IS '关联商品ID';
COMMENT ON COLUMN prod_product_relation.code IS '关联编码';
COMMENT ON COLUMN prod_product_relation.relation_type IS '关联类型（替代品/配件/搭配推荐/关联销售等）';
COMMENT ON COLUMN prod_product_relation.is_bidirectional IS '是否双向关联（0-单向/1-双向）';
COMMENT ON COLUMN prod_product_relation.sort_order IS '排序号';
COMMENT ON COLUMN prod_product_relation.status IS '状态（0-启用/1-停用）';
COMMENT ON COLUMN prod_product_relation.remark IS '备注';
COMMENT ON COLUMN prod_product_relation.created_at IS '创建时间';
COMMENT ON COLUMN prod_product_relation.updated_at IS '更新时间';
COMMENT ON COLUMN prod_product_relation.created_by IS '创建人ID';
COMMENT ON COLUMN prod_product_relation.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_product_relation.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_product_relation.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_product_relation.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_product_relation.version IS '版本号';
