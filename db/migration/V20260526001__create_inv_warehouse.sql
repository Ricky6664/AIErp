-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: inv_warehouse仓库定义表建表DDL
-- Author: AI Generated
-- Date: 2026-06-02
-- ============================================================

-- ============================================================
-- inv_warehouse 仓库定义表
-- ============================================================
CREATE TABLE IF NOT EXISTS inv_warehouse (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    warehouse_code  VARCHAR(50)     NOT NULL,
    warehouse_name  VARCHAR(100)    NOT NULL,
    warehouse_type  SMALLINT,
    company_id      BIGINT,
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

COMMENT ON TABLE inv_warehouse IS '仓库定义表';
COMMENT ON COLUMN inv_warehouse.id IS '主键ID';
COMMENT ON COLUMN inv_warehouse.tenant_id IS '租户ID';
COMMENT ON COLUMN inv_warehouse.warehouse_code IS '仓库编码';
COMMENT ON COLUMN inv_warehouse.warehouse_name IS '仓库名称';
COMMENT ON COLUMN inv_warehouse.warehouse_type IS '仓库类型：1=普通仓/2=虚拟仓/3=冻结仓';
COMMENT ON COLUMN inv_warehouse.company_id IS '公司ID';
COMMENT ON COLUMN inv_warehouse.status IS '状态：1=启用/0=禁用';
COMMENT ON COLUMN inv_warehouse.created_at IS '创建时间';
COMMENT ON COLUMN inv_warehouse.updated_at IS '更新时间';
COMMENT ON COLUMN inv_warehouse.created_by IS '创建人ID';
COMMENT ON COLUMN inv_warehouse.updated_by IS '修改人ID';
COMMENT ON COLUMN inv_warehouse.is_deleted IS '是否删除';
COMMENT ON COLUMN inv_warehouse.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN inv_warehouse.owner_id IS '数据负责人ID';
COMMENT ON COLUMN inv_warehouse.version IS '版本号';
