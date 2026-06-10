-- ============================================================
-- Flyway Migration Script
-- Version: V20260531008
-- Description: 系统管理表建表DDL（sys_param/sys_dict_type/sys_dict_data/
--   sys_code_rule/sys_code_rule_segment/sys_operation_log/
--   sys_data_view/sys_data_view_field/sys_notice/sys_doc_config）
-- Author: AI Generated
-- Date: 2026-05-31
-- ============================================================

-- ============================================================
-- 1. sys_param 系统参数表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_param (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    param_code      VARCHAR(100)    NOT NULL,
    param_value     TEXT,
    param_group     VARCHAR(50),
    is_preset       BOOLEAN         NOT NULL DEFAULT FALSE,
    is_readonly     BOOLEAN         NOT NULL DEFAULT FALSE,
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

COMMENT ON TABLE sys_param IS '系统参数表';
COMMENT ON COLUMN sys_param.id IS '主键ID';
COMMENT ON COLUMN sys_param.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_param.param_code IS '参数键（系统参数唯一标识）';
COMMENT ON COLUMN sys_param.param_value IS '参数值';
COMMENT ON COLUMN sys_param.param_group IS '参数分类';
COMMENT ON COLUMN sys_param.is_preset IS '是否预置参数（预置参数不可删除）';
COMMENT ON COLUMN sys_param.is_readonly IS '是否只读（只读参数不可修改）';
COMMENT ON COLUMN sys_param.remark IS '备注说明';
COMMENT ON COLUMN sys_param.created_at IS '创建时间';
COMMENT ON COLUMN sys_param.updated_at IS '更新时间';
COMMENT ON COLUMN sys_param.created_by IS '创建人ID';
COMMENT ON COLUMN sys_param.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_param.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_param.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_param.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_param.version IS '版本号';

-- ============================================================
-- 2. sys_dict_type 字典类型表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_dict_type (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    dict_type_code  VARCHAR(50)     NOT NULL,
    dict_type_name  VARCHAR(100)    NOT NULL,
    status          VARCHAR(30)     NOT NULL DEFAULT 'enabled',
    sort_no         INT             NOT NULL DEFAULT 0,
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

COMMENT ON TABLE sys_dict_type IS '字典类型表';
COMMENT ON COLUMN sys_dict_type.id IS '主键ID';
COMMENT ON COLUMN sys_dict_type.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_dict_type.dict_type_code IS '字典类型编码';
COMMENT ON COLUMN sys_dict_type.dict_type_name IS '字典类型名称';
COMMENT ON COLUMN sys_dict_type.status IS '状态：enabled=启用/disabled=禁用';
COMMENT ON COLUMN sys_dict_type.sort_no IS '排序号';
COMMENT ON COLUMN sys_dict_type.remark IS '备注说明';
COMMENT ON COLUMN sys_dict_type.created_at IS '创建时间';
COMMENT ON COLUMN sys_dict_type.updated_at IS '更新时间';
COMMENT ON COLUMN sys_dict_type.created_by IS '创建人ID';
COMMENT ON COLUMN sys_dict_type.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_dict_type.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_dict_type.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_dict_type.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_dict_type.version IS '版本号';

-- ============================================================
-- 3. sys_dict_data 字典数据表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_dict_data (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    dict_type_id    BIGINT          NOT NULL,
    dict_label      VARCHAR(100)    NOT NULL,
    dict_value      VARCHAR(100)    NOT NULL,
    sort_no         INT             NOT NULL DEFAULT 0,
    status          VARCHAR(30)     NOT NULL DEFAULT 'enabled',
    is_default      BOOLEAN         NOT NULL DEFAULT FALSE,
    css_class       VARCHAR(50),
    list_class      VARCHAR(50),
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

COMMENT ON TABLE sys_dict_data IS '字典数据表';
COMMENT ON COLUMN sys_dict_data.id IS '主键ID';
COMMENT ON COLUMN sys_dict_data.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_dict_data.dict_type_id IS '字典类型ID（关联sys_dict_type.id）';
COMMENT ON COLUMN sys_dict_data.dict_label IS '字典标签（显示文本）';
COMMENT ON COLUMN sys_dict_data.dict_value IS '字典键值（存储值）';
COMMENT ON COLUMN sys_dict_data.sort_no IS '排序号';
COMMENT ON COLUMN sys_dict_data.status IS '状态：enabled=启用/disabled=禁用';
COMMENT ON COLUMN sys_dict_data.is_default IS '是否默认值';
COMMENT ON COLUMN sys_dict_data.css_class IS 'CSS样式类名';
COMMENT ON COLUMN sys_dict_data.list_class IS '列表样式类名';
COMMENT ON COLUMN sys_dict_data.remark IS '备注说明';
COMMENT ON COLUMN sys_dict_data.created_at IS '创建时间';
COMMENT ON COLUMN sys_dict_data.updated_at IS '更新时间';
COMMENT ON COLUMN sys_dict_data.created_by IS '创建人ID';
COMMENT ON COLUMN sys_dict_data.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_dict_data.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_dict_data.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_dict_data.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_dict_data.version IS '版本号';

-- ============================================================
-- 4. sys_code_rule 编码规则配置表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_code_rule (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    rule_code       VARCHAR(50)     NOT NULL,
    rule_name       VARCHAR(100),
    target_table    VARCHAR(100),
    target_field    VARCHAR(50),
    reset_cycle     VARCHAR(30),
    is_enabled      BOOLEAN         NOT NULL DEFAULT TRUE,
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

COMMENT ON TABLE sys_code_rule IS '编码规则配置表';
COMMENT ON COLUMN sys_code_rule.id IS '主键ID';
COMMENT ON COLUMN sys_code_rule.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_code_rule.rule_code IS '规则编码（唯一标识）';
COMMENT ON COLUMN sys_code_rule.rule_name IS '规则名称';
COMMENT ON COLUMN sys_code_rule.target_table IS '目标表名';
COMMENT ON COLUMN sys_code_rule.target_field IS '目标字段名';
COMMENT ON COLUMN sys_code_rule.reset_cycle IS '重置周期：none=不重置/day=每天/month=每月/year=每年/custom=自定义';
COMMENT ON COLUMN sys_code_rule.is_enabled IS '是否启用';
COMMENT ON COLUMN sys_code_rule.remark IS '备注说明';
COMMENT ON COLUMN sys_code_rule.created_at IS '创建时间';
COMMENT ON COLUMN sys_code_rule.updated_at IS '更新时间';
COMMENT ON COLUMN sys_code_rule.created_by IS '创建人ID';
COMMENT ON COLUMN sys_code_rule.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_code_rule.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_code_rule.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_code_rule.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_code_rule.version IS '版本号';

-- ============================================================
-- 5. sys_code_rule_segment 编码规则段配置表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_code_rule_segment (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    rule_id         BIGINT          NOT NULL,
    segment_type    VARCHAR(30)     NOT NULL,
    seq             INT             NOT NULL DEFAULT 0,
    fixed_value     VARCHAR(50),
    date_format     VARCHAR(30),
    serial_length   INT,
    step            INT             NOT NULL DEFAULT 1,
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

COMMENT ON TABLE sys_code_rule_segment IS '编码规则段配置表';
COMMENT ON COLUMN sys_code_rule_segment.id IS '主键ID';
COMMENT ON COLUMN sys_code_rule_segment.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_code_rule_segment.rule_id IS '编码规则ID（关联sys_code_rule.id）';
COMMENT ON COLUMN sys_code_rule_segment.segment_type IS '段类型：fixed=固定值/date=日期/serial=序列号/variable=自定义变量';
COMMENT ON COLUMN sys_code_rule_segment.seq IS '段顺序（从0开始）';
COMMENT ON COLUMN sys_code_rule_segment.fixed_value IS '固定值（segment_type=fixed时使用）';
COMMENT ON COLUMN sys_code_rule_segment.date_format IS '日期格式（segment_type=date时使用，如yyyyMMdd）';
COMMENT ON COLUMN sys_code_rule_segment.serial_length IS '序号位数（segment_type=serial时使用，左补零）';
COMMENT ON COLUMN sys_code_rule_segment.step IS '步长（segment_type=serial时使用，默认1）';
COMMENT ON COLUMN sys_code_rule_segment.remark IS '备注说明';
COMMENT ON COLUMN sys_code_rule_segment.created_at IS '创建时间';
COMMENT ON COLUMN sys_code_rule_segment.updated_at IS '更新时间';
COMMENT ON COLUMN sys_code_rule_segment.created_by IS '创建人ID';
COMMENT ON COLUMN sys_code_rule_segment.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_code_rule_segment.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_code_rule_segment.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_code_rule_segment.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_code_rule_segment.version IS '版本号';

-- ============================================================
-- 6. sys_operation_log 操作日志表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_operation_log (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    module          VARCHAR(50),
    operation_type  VARCHAR(30)     NOT NULL,
    request_params  TEXT,
    response        TEXT,
    duration        BIGINT,
    operator_id     BIGINT,
    operator_name   VARCHAR(50),
    oper_ip         VARCHAR(50),
    oper_at         TIMESTAMP       NOT NULL DEFAULT NOW(),
    oper_status     VARCHAR(30)     NOT NULL DEFAULT 'success',
    error_msg       TEXT,
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

COMMENT ON TABLE sys_operation_log IS '操作日志表';
COMMENT ON COLUMN sys_operation_log.id IS '主键ID';
COMMENT ON COLUMN sys_operation_log.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_operation_log.module IS '操作模块';
COMMENT ON COLUMN sys_operation_log.operation_type IS '操作类型：add=新增/update=修改/delete=删除/query=查询/export=导出/import=导入/other=其他';
COMMENT ON COLUMN sys_operation_log.request_params IS '请求参数（敏感字段自动脱敏）';
COMMENT ON COLUMN sys_operation_log.response IS '响应结果摘要';
COMMENT ON COLUMN sys_operation_log.duration IS '操作耗时（毫秒）';
COMMENT ON COLUMN sys_operation_log.operator_id IS '操作人ID';
COMMENT ON COLUMN sys_operation_log.operator_name IS '操作人名称';
COMMENT ON COLUMN sys_operation_log.oper_ip IS '操作IP地址';
COMMENT ON COLUMN sys_operation_log.oper_at IS '操作时间';
COMMENT ON COLUMN sys_operation_log.oper_status IS '操作状态：success=成功/fail=失败';
COMMENT ON COLUMN sys_operation_log.error_msg IS '错误信息（操作失败时记录）';
COMMENT ON COLUMN sys_operation_log.remark IS '备注说明';
COMMENT ON COLUMN sys_operation_log.created_at IS '创建时间';
COMMENT ON COLUMN sys_operation_log.updated_at IS '更新时间';
COMMENT ON COLUMN sys_operation_log.created_by IS '创建人ID';
COMMENT ON COLUMN sys_operation_log.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_operation_log.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_operation_log.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_operation_log.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_operation_log.version IS '版本号';

-- ============================================================
-- 7. sys_data_view 数据视图配置表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_data_view (
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    -- 业务字段
    view_code           VARCHAR(50)     NOT NULL,
    view_name           VARCHAR(100),
    main_table          VARCHAR(100),
    query_sql_template  TEXT,
    status              VARCHAR(30)     NOT NULL DEFAULT 'enabled',
    sort_no             INT             NOT NULL DEFAULT 0,
    remark              VARCHAR(500),
    -- 扩展字段
    ext_str1            VARCHAR(200),
    ext_str2            VARCHAR(200),
    ext_str3            VARCHAR(200),
    ext_str4            VARCHAR(200),
    ext_str5            VARCHAR(200),
    ext_str6            VARCHAR(200),
    ext_str7            VARCHAR(200),
    ext_str8            VARCHAR(200),
    ext_str9            VARCHAR(200),
    ext_str10           VARCHAR(200),
    ext_num1            DECIMAL(18,8),
    ext_num2            DECIMAL(18,8),
    ext_num3            DECIMAL(18,8),
    ext_num4            DECIMAL(18,8),
    ext_num5            DECIMAL(18,8),
    ext_date1           DATE,
    ext_date2           DATE,
    ext_date3           DATE,
    ext_bool1           BOOLEAN,
    ext_bool2           BOOLEAN,
    ext_bool3           BOOLEAN,
    ext_json            JSONB,
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

COMMENT ON TABLE sys_data_view IS '数据视图配置表';
COMMENT ON COLUMN sys_data_view.id IS '主键ID';
COMMENT ON COLUMN sys_data_view.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_data_view.view_code IS '视图编码（唯一标识）';
COMMENT ON COLUMN sys_data_view.view_name IS '视图名称';
COMMENT ON COLUMN sys_data_view.main_table IS '主表名';
COMMENT ON COLUMN sys_data_view.query_sql_template IS '查询SQL模板';
COMMENT ON COLUMN sys_data_view.status IS '状态：enabled=启用/disabled=禁用';
COMMENT ON COLUMN sys_data_view.sort_no IS '排序号';
COMMENT ON COLUMN sys_data_view.remark IS '备注说明';
COMMENT ON COLUMN sys_data_view.created_at IS '创建时间';
COMMENT ON COLUMN sys_data_view.updated_at IS '更新时间';
COMMENT ON COLUMN sys_data_view.created_by IS '创建人ID';
COMMENT ON COLUMN sys_data_view.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_data_view.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_data_view.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_data_view.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_data_view.version IS '版本号';

-- ============================================================
-- 8. sys_data_view_field 数据视图字段配置表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_data_view_field (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    view_id         BIGINT          NOT NULL,
    field_name      VARCHAR(50)     NOT NULL,
    label           VARCHAR(100),
    query_type      VARCHAR(30),
    sort_no         INT             NOT NULL DEFAULT 0,
    visible         BOOLEAN         NOT NULL DEFAULT TRUE,
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

COMMENT ON TABLE sys_data_view_field IS '数据视图字段配置表';
COMMENT ON COLUMN sys_data_view_field.id IS '主键ID';
COMMENT ON COLUMN sys_data_view_field.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_data_view_field.view_id IS '视图ID（关联sys_data_view.id）';
COMMENT ON COLUMN sys_data_view_field.field_name IS '字段名（对应数据库列名）';
COMMENT ON COLUMN sys_data_view_field.label IS '字段标签（显示名称）';
COMMENT ON COLUMN sys_data_view_field.query_type IS '查询方式：eq=等于/like=模糊/range=范围/in=包含/date_range=日期范围';
COMMENT ON COLUMN sys_data_view_field.sort_no IS '排序号';
COMMENT ON COLUMN sys_data_view_field.visible IS '是否显示';
COMMENT ON COLUMN sys_data_view_field.remark IS '备注说明';
COMMENT ON COLUMN sys_data_view_field.created_at IS '创建时间';
COMMENT ON COLUMN sys_data_view_field.updated_at IS '更新时间';
COMMENT ON COLUMN sys_data_view_field.created_by IS '创建人ID';
COMMENT ON COLUMN sys_data_view_field.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_data_view_field.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_data_view_field.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_data_view_field.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_data_view_field.version IS '版本号';

-- ============================================================
-- 9. sys_notice 系统公告表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_notice (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    title           VARCHAR(200)    NOT NULL,
    content         TEXT,
    type            VARCHAR(30)     NOT NULL DEFAULT 'notice',
    status          VARCHAR(30)     NOT NULL DEFAULT 'draft',
    publish_time    TIMESTAMP,
    publisher_id    BIGINT,
    is_top          BOOLEAN         NOT NULL DEFAULT FALSE,
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

COMMENT ON TABLE sys_notice IS '系统公告表';
COMMENT ON COLUMN sys_notice.id IS '主键ID';
COMMENT ON COLUMN sys_notice.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_notice.title IS '公告标题';
COMMENT ON COLUMN sys_notice.content IS '公告内容（支持富文本）';
COMMENT ON COLUMN sys_notice.type IS '公告类型：notice=通知/announcement=公告/alert=预警';
COMMENT ON COLUMN sys_notice.status IS '状态：draft=草稿/published=已发布/revoked=已撤回';
COMMENT ON COLUMN sys_notice.publish_time IS '发布时间';
COMMENT ON COLUMN sys_notice.publisher_id IS '发布人ID';
COMMENT ON COLUMN sys_notice.is_top IS '是否置顶';
COMMENT ON COLUMN sys_notice.remark IS '备注说明';
COMMENT ON COLUMN sys_notice.created_at IS '创建时间';
COMMENT ON COLUMN sys_notice.updated_at IS '更新时间';
COMMENT ON COLUMN sys_notice.created_by IS '创建人ID';
COMMENT ON COLUMN sys_notice.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_notice.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_notice.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_notice.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_notice.version IS '版本号';

-- ============================================================
-- 10. sys_doc_config 单据配置表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_doc_config (
    id                      BIGSERIAL       PRIMARY KEY,
    tenant_id               BIGINT          NOT NULL,
    -- 业务字段
    doc_type                VARCHAR(50)     NOT NULL,
    doc_name                VARCHAR(100),
    audit_flow_config       JSONB,
    print_template_config   JSONB,
    field_visibility        JSONB,
    status                  VARCHAR(30)     NOT NULL DEFAULT 'enabled',
    remark                  VARCHAR(500),
    -- 扩展字段
    ext_str1                VARCHAR(200),
    ext_str2                VARCHAR(200),
    ext_str3                VARCHAR(200),
    ext_str4                VARCHAR(200),
    ext_str5                VARCHAR(200),
    ext_str6                VARCHAR(200),
    ext_str7                VARCHAR(200),
    ext_str8                VARCHAR(200),
    ext_str9                VARCHAR(200),
    ext_str10               VARCHAR(200),
    ext_num1                DECIMAL(18,8),
    ext_num2                DECIMAL(18,8),
    ext_num3                DECIMAL(18,8),
    ext_num4                DECIMAL(18,8),
    ext_num5                DECIMAL(18,8),
    ext_date1               DATE,
    ext_date2               DATE,
    ext_date3               DATE,
    ext_bool1               BOOLEAN,
    ext_bool2               BOOLEAN,
    ext_bool3               BOOLEAN,
    ext_json                JSONB,
    -- 通用必含字段
    created_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by              BIGINT,
    updated_by              BIGINT,
    is_deleted              BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id           BIGINT,
    owner_id                BIGINT,
    version                 INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE sys_doc_config IS '单据配置表';
COMMENT ON COLUMN sys_doc_config.id IS '主键ID';
COMMENT ON COLUMN sys_doc_config.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_doc_config.doc_type IS '单据类型编码（如sale_order/purchase_order）';
COMMENT ON COLUMN sys_doc_config.doc_name IS '单据名称';
COMMENT ON COLUMN sys_doc_config.audit_flow_config IS '审核流程配置（JSONB：审批节点/审批人/审批条件）';
COMMENT ON COLUMN sys_doc_config.print_template_config IS '打印模板配置（JSONB：模板ID/纸张大小/页边距）';
COMMENT ON COLUMN sys_doc_config.field_visibility IS '字段显隐配置（JSONB：各状态下字段的显示/隐藏/只读）';
COMMENT ON COLUMN sys_doc_config.status IS '状态：enabled=启用/disabled=禁用';
COMMENT ON COLUMN sys_doc_config.remark IS '备注说明';
COMMENT ON COLUMN sys_doc_config.created_at IS '创建时间';
COMMENT ON COLUMN sys_doc_config.updated_at IS '更新时间';
COMMENT ON COLUMN sys_doc_config.created_by IS '创建人ID';
COMMENT ON COLUMN sys_doc_config.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_doc_config.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_doc_config.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_doc_config.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_doc_config.version IS '版本号';
