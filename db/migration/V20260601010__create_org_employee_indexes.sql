-- ============================================================
-- Flyway Migration Script
-- Version: V20260601010
-- Description: org_employee员工表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 主键约束（命名规范化）
-- 建表DDL中id已声明PRIMARY KEY，系统自动生成约束名org_employee_pkey
-- 此处重命名为规范名称pk_org_employee
-- ============================================================
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'org_employee_pkey' AND conrelid = 'org_employee'::regclass
    ) THEN
        ALTER TABLE org_employee RENAME CONSTRAINT org_employee_pkey TO pk_org_employee;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false）
-- 避免boolean类型is_deleted在联合唯一索引中的"一删一活"陷阱
-- ============================================================
CREATE UNIQUE INDEX uk_org_employee_code ON org_employee(employee_code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_org_employee_tenant_code ON org_employee(tenant_id, employee_code);
CREATE INDEX idx_org_employee_tenant_status ON org_employee(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
-- 外键关联字段索引
CREATE INDEX idx_org_employee_dept_id ON org_employee(dept_id);
CREATE INDEX idx_org_employee_position_id ON org_employee(position_id);

-- 常用查询字段索引
CREATE INDEX idx_org_employee_entry_date ON org_employee(entry_date);
CREATE INDEX idx_org_employee_name ON org_employee(employee_name);
