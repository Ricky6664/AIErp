-- ============================================================
-- Flyway Migration Script
-- Version: V20260602016
-- Description: srm_supplier_attachment 供应商附件表建表DDL
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-007-001-001
-- ============================================================

-- ============================================================
-- srm_supplier_attachment 供应商附件表
-- 存储供应商关联的附件信息（合同/资质/证照等）
-- ============================================================
CREATE TABLE IF NOT EXISTS srm_supplier_attachment (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    supplier_id     BIGINT          NOT NULL,
    code            VARCHAR(50)     NOT NULL,
    attachment_name VARCHAR(200)    NOT NULL,
    attachment_type VARCHAR(50)     NOT NULL DEFAULT 'other',
    attachment_url  VARCHAR(500),
    file_size       BIGINT,
    sort_order      INTEGER         NOT NULL DEFAULT 0,
    status          SMALLINT        NOT NULL DEFAULT 0,
    remark          VARCHAR(500),
    -- 扩展字段
    ext_str1        VARCHAR(200),
    ext_str2        VARCHAR(200),
    ext_str3        VARCHAR(200),
    ext_str4        VARCHAR(200),
    ext_str5        VARCHAR(200),
    ext_str6        VARCHAR(200),
    ext_str7        VARCHAR(200),
    ext_str8        VARCHAR(200),
    ext_str9        VARCHAR(200),
    ext_str10       VARCHAR(200),
    ext_num1        DECIMAL(18,8),
    ext_num2        DECIMAL(18,8),
    ext_num3        DECIMAL(18,8),
    ext_num4        DECIMAL(18,8),
    ext_num5        DECIMAL(18,8),
    ext_date1       DATE,
    ext_date2       DATE,
    ext_date3       DATE,
    ext_bool1       BOOLEAN,
    ext_bool2       BOOLEAN,
    ext_bool3       BOOLEAN,
    ext_json        JSONB,
    -- 通用必含字段
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    version         INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE srm_supplier_attachment IS '供应商附件表';
COMMENT ON COLUMN srm_supplier_attachment.id IS '主键ID';
COMMENT ON COLUMN srm_supplier_attachment.tenant_id IS '租户ID';
COMMENT ON COLUMN srm_supplier_attachment.supplier_id IS '供应商ID';
COMMENT ON COLUMN srm_supplier_attachment.code IS '附件编码';
COMMENT ON COLUMN srm_supplier_attachment.attachment_name IS '附件名称';
COMMENT ON COLUMN srm_supplier_attachment.attachment_type IS '附件类型（contract-合同/certificate-证照/qualification-资质/other-其他）';
COMMENT ON COLUMN srm_supplier_attachment.attachment_url IS '附件存储路径';
COMMENT ON COLUMN srm_supplier_attachment.file_size IS '文件大小（字节）';
COMMENT ON COLUMN srm_supplier_attachment.sort_order IS '排序号';
COMMENT ON COLUMN srm_supplier_attachment.status IS '状态（0-正常/1-停用）';
COMMENT ON COLUMN srm_supplier_attachment.remark IS '备注';
COMMENT ON COLUMN srm_supplier_attachment.created_at IS '创建时间';
COMMENT ON COLUMN srm_supplier_attachment.updated_at IS '更新时间';
COMMENT ON COLUMN srm_supplier_attachment.created_by IS '创建人ID';
COMMENT ON COLUMN srm_supplier_attachment.updated_by IS '修改人ID';
COMMENT ON COLUMN srm_supplier_attachment.is_deleted IS '是否删除';
COMMENT ON COLUMN srm_supplier_attachment.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN srm_supplier_attachment.owner_id IS '数据负责人ID';
COMMENT ON COLUMN srm_supplier_attachment.version IS '版本号';
