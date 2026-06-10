-- ============================================================
-- Flyway Migration Script
-- Version: V20260531006
-- Description: 认证相关表建表DDL（sys_login_log/sys_oper_log）
-- Author: AI Generated
-- Date: 2026-05-31
-- ============================================================

-- ============================================================
-- 1. sys_login_log 登录日志表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_login_log (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    username        VARCHAR(50)     NOT NULL,
    login_type      VARCHAR(30)     NOT NULL DEFAULT 'password',
    ip_address      VARCHAR(50),
    user_agent      VARCHAR(500),
    login_status    VARCHAR(30)     NOT NULL DEFAULT 'success',
    fail_reason     VARCHAR(200),
    login_at        TIMESTAMP       NOT NULL DEFAULT NOW(),
    logout_at       TIMESTAMP,
    session_id      VARCHAR(100),
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

COMMENT ON TABLE sys_login_log IS '登录日志表';
COMMENT ON COLUMN sys_login_log.id IS '主键ID';
COMMENT ON COLUMN sys_login_log.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_login_log.username IS '登录用户名';
COMMENT ON COLUMN sys_login_log.login_type IS '登录方式：password=密码登录/sms=短信验证码/oauth2=第三方登录/sso=单点登录';
COMMENT ON COLUMN sys_login_log.ip_address IS '登录IP地址';
COMMENT ON COLUMN sys_login_log.user_agent IS '用户代理（浏览器UA信息）';
COMMENT ON COLUMN sys_login_log.login_status IS '登录状态：success=成功/fail=失败/locked=锁定拒绝';
COMMENT ON COLUMN sys_login_log.fail_reason IS '失败原因（登录失败时记录）';
COMMENT ON COLUMN sys_login_log.login_at IS '登录时间';
COMMENT ON COLUMN sys_login_log.logout_at IS '登出时间';
COMMENT ON COLUMN sys_login_log.session_id IS '会话ID';
COMMENT ON COLUMN sys_login_log.created_at IS '创建时间';
COMMENT ON COLUMN sys_login_log.updated_at IS '更新时间';
COMMENT ON COLUMN sys_login_log.created_by IS '创建人ID';
COMMENT ON COLUMN sys_login_log.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_login_log.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_login_log.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_login_log.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_login_log.version IS '版本号';

-- ============================================================
-- 2. sys_oper_log 操作日志表
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_oper_log (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    oper_type       VARCHAR(30)     NOT NULL,
    oper_module     VARCHAR(50),
    oper_desc       VARCHAR(500),
    request_method  VARCHAR(10),
    request_url     VARCHAR(500),
    request_params  TEXT,
    response_result TEXT,
    oper_ip         VARCHAR(50),
    oper_location   VARCHAR(100),
    oper_user_id    BIGINT,
    oper_user_name  VARCHAR(50),
    oper_at         TIMESTAMP       NOT NULL DEFAULT NOW(),
    cost_time_ms    BIGINT,
    oper_status     VARCHAR(30)     NOT NULL DEFAULT 'success',
    error_msg       TEXT,
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

COMMENT ON TABLE sys_oper_log IS '操作日志表';
COMMENT ON COLUMN sys_oper_log.id IS '主键ID';
COMMENT ON COLUMN sys_oper_log.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_oper_log.oper_type IS '操作类型：add=新增/update=修改/delete=删除/query=查询/export=导出/import=导入/login=登录/logout=登出/other=其他';
COMMENT ON COLUMN sys_oper_log.oper_module IS '操作模块';
COMMENT ON COLUMN sys_oper_log.oper_desc IS '操作描述';
COMMENT ON COLUMN sys_oper_log.request_method IS '请求方式：GET/POST/PUT/DELETE';
COMMENT ON COLUMN sys_oper_log.request_url IS '请求URL';
COMMENT ON COLUMN sys_oper_log.request_params IS '请求参数（敏感字段自动脱敏）';
COMMENT ON COLUMN sys_oper_log.response_result IS '响应结果摘要';
COMMENT ON COLUMN sys_oper_log.oper_ip IS '操作IP地址';
COMMENT ON COLUMN sys_oper_log.oper_location IS '操作地点（根据IP解析）';
COMMENT ON COLUMN sys_oper_log.oper_user_id IS '操作用户ID';
COMMENT ON COLUMN sys_oper_log.oper_user_name IS '操作用户名称';
COMMENT ON COLUMN sys_oper_log.oper_at IS '操作时间';
COMMENT ON COLUMN sys_oper_log.cost_time_ms IS '操作耗时（毫秒）';
COMMENT ON COLUMN sys_oper_log.oper_status IS '操作状态：success=成功/fail=失败';
COMMENT ON COLUMN sys_oper_log.error_msg IS '错误信息（操作失败时记录）';
COMMENT ON COLUMN sys_oper_log.created_at IS '创建时间';
COMMENT ON COLUMN sys_oper_log.updated_at IS '更新时间';
COMMENT ON COLUMN sys_oper_log.created_by IS '创建人ID';
COMMENT ON COLUMN sys_oper_log.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_oper_log.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_oper_log.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_oper_log.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_oper_log.version IS '版本号';
