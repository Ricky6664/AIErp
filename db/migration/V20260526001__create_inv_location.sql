-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: inv_location库位管理表建表DDL
-- Author: AI Generated
-- Date: 2026-06-02
-- ============================================================

-- ============================================================
-- inv_location 库位管理表
-- ============================================================
CREATE TABLE IF NOT EXISTS inv_location (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    warehouse_id    BIGINT          NOT NULL,
    location_code   VARCHAR(50)     NOT NULL,
    location_name   VARCHAR(100)    NOT NULL,
    zone            VARCHAR(50),
    status          SMALLINT        NOT NULL DEFAULT 1,
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

COMMENT ON TABLE inv_location IS '库位管理表';
COMMENT ON COLUMN inv_location.id IS '主键ID';
COMMENT ON COLUMN inv_location.tenant_id IS '租户ID';
COMMENT ON COLUMN inv_location.warehouse_id IS '仓库ID';
COMMENT ON COLUMN inv_location.location_code IS '库位编码';
COMMENT ON COLUMN inv_location.location_name IS '库位名称';
COMMENT ON COLUMN inv_location.zone IS '库区';
COMMENT ON COLUMN inv_location.status IS '状态：1=启用/0=禁用';
COMMENT ON COLUMN inv_location.created_at IS '创建时间';
COMMENT ON COLUMN inv_location.updated_at IS '更新时间';
COMMENT ON COLUMN inv_location.created_by IS '创建人ID';
COMMENT ON COLUMN inv_location.updated_by IS '修改人ID';
COMMENT ON COLUMN inv_location.is_deleted IS '是否删除';
COMMENT ON COLUMN inv_location.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN inv_location.owner_id IS '数据负责人ID';
COMMENT ON COLUMN inv_location.version IS '版本号';
