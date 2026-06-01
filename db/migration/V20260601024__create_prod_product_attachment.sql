-- ============================================================
-- Flyway Migration Script
-- Version: V20260601024
-- Description: prod_product_attachment商品附件表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- prod_product_attachment 商品附件表
-- 商品关联附件（图纸/证书/说明书等）
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_product_attachment (
    id               BIGSERIAL       PRIMARY KEY,
    tenant_id        BIGINT          NOT NULL,
    -- 业务字段
    product_id       BIGINT          NOT NULL,
    code             VARCHAR(50)     NOT NULL,
    attachment_name  VARCHAR(200)    NOT NULL,
    attachment_type  VARCHAR(50)     NOT NULL DEFAULT 'other',
    attachment_url   VARCHAR(500),
    file_size        BIGINT,
    sort_order       INTEGER         NOT NULL DEFAULT 0,
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

COMMENT ON TABLE prod_product_attachment IS '商品附件表';
COMMENT ON COLUMN prod_product_attachment.id IS '主键ID';
COMMENT ON COLUMN prod_product_attachment.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_product_attachment.product_id IS '商品ID';
COMMENT ON COLUMN prod_product_attachment.code IS '附件编码';
COMMENT ON COLUMN prod_product_attachment.attachment_name IS '附件名称';
COMMENT ON COLUMN prod_product_attachment.attachment_type IS '附件类型（drawing-图纸/certificate-证书/manual-说明书/other-其他）';
COMMENT ON COLUMN prod_product_attachment.attachment_url IS '附件存储路径';
COMMENT ON COLUMN prod_product_attachment.file_size IS '文件大小（字节）';
COMMENT ON COLUMN prod_product_attachment.sort_order IS '排序号';
COMMENT ON COLUMN prod_product_attachment.status IS '状态（0-正常/1-停用）';
COMMENT ON COLUMN prod_product_attachment.remark IS '备注';
COMMENT ON COLUMN prod_product_attachment.created_at IS '创建时间';
COMMENT ON COLUMN prod_product_attachment.updated_at IS '更新时间';
COMMENT ON COLUMN prod_product_attachment.created_by IS '创建人ID';
COMMENT ON COLUMN prod_product_attachment.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_product_attachment.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_product_attachment.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_product_attachment.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_product_attachment.version IS '版本号';
