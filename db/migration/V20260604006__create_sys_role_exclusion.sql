-- ============================================================
-- Flyway Migration Script
-- Version: V20260604006
-- Description: sys_role_exclusion角色互斥表建表DDL
-- Author: AI Generated
-- Date: 2026-06-03
-- ============================================================

-- ============================================================
-- sys_role_exclusion 角色互斥表
-- 同一用户不可同时拥有互斥角色（如采购员与审批人、出纳与会计）
-- ============================================================
CREATE TABLE IF NOT EXISTS sys_role_exclusion (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    role_a          BIGINT          NOT NULL,
    role_b          BIGINT          NOT NULL,
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

-- ============================================================
-- 注释
-- ============================================================
COMMENT ON TABLE sys_role_exclusion IS '角色互斥表';
COMMENT ON COLUMN sys_role_exclusion.id IS '主键ID';
COMMENT ON COLUMN sys_role_exclusion.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_role_exclusion.role_a IS '角色A';
COMMENT ON COLUMN sys_role_exclusion.role_b IS '角色B';
COMMENT ON COLUMN sys_role_exclusion.created_at IS '创建时间';
COMMENT ON COLUMN sys_role_exclusion.updated_at IS '更新时间';
COMMENT ON COLUMN sys_role_exclusion.created_by IS '创建人ID';
COMMENT ON COLUMN sys_role_exclusion.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_role_exclusion.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_role_exclusion.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_role_exclusion.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_role_exclusion.version IS '版本号(乐观锁)';

-- ============================================================
-- 索引与约束
-- ============================================================
-- 唯一约束：同一对互斥关系不可重复（含软删除），不区分A/B顺序
CREATE UNIQUE INDEX uk_sys_role_exclusion ON sys_role_exclusion (LEAST(role_a, role_b), GREATEST(role_a, role_b)) WHERE is_deleted = false;

-- 外键关联索引
CREATE INDEX idx_sys_role_exclusion_role_a ON sys_role_exclusion (role_a);
CREATE INDEX idx_sys_role_exclusion_role_b ON sys_role_exclusion (role_b);

-- 按角色查询互斥关系
CREATE INDEX idx_sys_role_exclusion_role_tenant ON sys_role_exclusion (tenant_id, role_a) WHERE is_deleted = false;
