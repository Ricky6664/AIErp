-- ============================================================
-- Flyway Migration Script
-- Version: V20260602010
-- Description: srm_supplier_address 供应商地址表建表DDL
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-005-001-001
-- ============================================================

-- ============================================================
-- srm_supplier_address 供应商地址表
-- 存储供应商的收货/发货/开票等地址信息
-- ============================================================
CREATE TABLE IF NOT EXISTS srm_supplier_address (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    supplier_id     BIGINT          NOT NULL,
    address_type    VARCHAR(50)     NOT NULL,
    province        VARCHAR(50),
    city            VARCHAR(50),
    detail_address  VARCHAR(500),
    status          SMALLINT        DEFAULT 1,
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

COMMENT ON TABLE srm_supplier_address IS '供应商地址表';
COMMENT ON COLUMN srm_supplier_address.id IS '主键ID';
COMMENT ON COLUMN srm_supplier_address.tenant_id IS '租户ID';
COMMENT ON COLUMN srm_supplier_address.supplier_id IS '供应商ID';
COMMENT ON COLUMN srm_supplier_address.address_type IS '地址类型';
COMMENT ON COLUMN srm_supplier_address.province IS '省份';
COMMENT ON COLUMN srm_supplier_address.city IS '城市';
COMMENT ON COLUMN srm_supplier_address.detail_address IS '详细地址';
COMMENT ON COLUMN srm_supplier_address.status IS '状态';
COMMENT ON COLUMN srm_supplier_address.created_at IS '创建时间';
COMMENT ON COLUMN srm_supplier_address.updated_at IS '更新时间';
COMMENT ON COLUMN srm_supplier_address.created_by IS '创建人ID';
COMMENT ON COLUMN srm_supplier_address.updated_by IS '修改人ID';
COMMENT ON COLUMN srm_supplier_address.is_deleted IS '是否删除';
COMMENT ON COLUMN srm_supplier_address.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN srm_supplier_address.owner_id IS '数据负责人ID';
COMMENT ON COLUMN srm_supplier_address.version IS '版本号';
