-- ============================================================
-- Flyway Migration Script
-- Version: V20260601060
-- Description: prod_product_image商品图片表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- prod_product_image 商品图片表
-- 商品关联图片（主图/详情图/轮播图等）
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_product_image (
    id               BIGSERIAL       PRIMARY KEY,
    tenant_id        BIGINT          NOT NULL,
    -- 业务字段
    product_id       BIGINT          NOT NULL,
    code             VARCHAR(50)     NOT NULL,
    image_name       VARCHAR(200),
    image_url        VARCHAR(500),
    image_type       VARCHAR(50)     NOT NULL DEFAULT 'detail',
    sort_order       INTEGER         NOT NULL DEFAULT 0,
    is_main          BOOLEAN         NOT NULL DEFAULT FALSE,
    file_size        BIGINT,
    status           SMALLINT        NOT NULL DEFAULT 0,
    remark           VARCHAR(500),
    -- 通用必含字段
    created_at       TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by       BIGINT,
    updated_by       BIGINT,
    is_deleted       BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id    BIGINT,
    owner_id         BIGINT,
    version          INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE prod_product_image IS '商品图片表';
COMMENT ON COLUMN prod_product_image.id IS '主键ID';
COMMENT ON COLUMN prod_product_image.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_product_image.product_id IS '商品ID';
COMMENT ON COLUMN prod_product_image.code IS '图片编码';
COMMENT ON COLUMN prod_product_image.image_name IS '图片名称';
COMMENT ON COLUMN prod_product_image.image_url IS '图片存储路径';
COMMENT ON COLUMN prod_product_image.image_type IS '图片类型（main-主图/detail-详情图/gallery-轮播图/other-其他）';
COMMENT ON COLUMN prod_product_image.sort_order IS '排序号';
COMMENT ON COLUMN prod_product_image.is_main IS '是否主图';
COMMENT ON COLUMN prod_product_image.file_size IS '文件大小（字节）';
COMMENT ON COLUMN prod_product_image.status IS '状态（0-正常/1-停用）';
COMMENT ON COLUMN prod_product_image.remark IS '备注';
COMMENT ON COLUMN prod_product_image.created_at IS '创建时间';
COMMENT ON COLUMN prod_product_image.updated_at IS '更新时间';
COMMENT ON COLUMN prod_product_image.created_by IS '创建人ID';
COMMENT ON COLUMN prod_product_image.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_product_image.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_product_image.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_product_image.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_product_image.version IS '版本号';
