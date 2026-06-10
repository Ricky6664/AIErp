-- ============================================================
-- Flyway Migration Script
-- Version: V20260601009
-- Description: org_employee员工表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- org_employee 员工表
-- ============================================================
CREATE TABLE IF NOT EXISTS org_employee (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    employee_code   VARCHAR(50)     NOT NULL,
    employee_name   VARCHAR(100)    NOT NULL,
    phone           VARCHAR(20),
    dept_id         BIGINT,
    position_id     BIGINT,
    entry_date      DATE,
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

COMMENT ON TABLE org_employee IS '员工表';
COMMENT ON COLUMN org_employee.id IS '主键ID';
COMMENT ON COLUMN org_employee.tenant_id IS '租户ID';
COMMENT ON COLUMN org_employee.employee_code IS '员工编码';
COMMENT ON COLUMN org_employee.employee_name IS '员工姓名';
COMMENT ON COLUMN org_employee.phone IS '手机号';
COMMENT ON COLUMN org_employee.dept_id IS '所属部门ID';
COMMENT ON COLUMN org_employee.position_id IS '所属岗位ID';
COMMENT ON COLUMN org_employee.entry_date IS '入职日期';
COMMENT ON COLUMN org_employee.status IS '状态：1=在职/0=离职';
COMMENT ON COLUMN org_employee.created_at IS '创建时间';
COMMENT ON COLUMN org_employee.updated_at IS '更新时间';
COMMENT ON COLUMN org_employee.created_by IS '创建人ID';
COMMENT ON COLUMN org_employee.updated_by IS '修改人ID';
COMMENT ON COLUMN org_employee.is_deleted IS '是否删除';
COMMENT ON COLUMN org_employee.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN org_employee.owner_id IS '数据负责人ID';
COMMENT ON COLUMN org_employee.version IS '版本号';
