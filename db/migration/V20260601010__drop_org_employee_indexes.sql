-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601010
-- Description: org_employee员工表索引回滚(DROP INDEX)
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- 业务查询索引
DROP INDEX IF EXISTS idx_org_employee_name;
DROP INDEX IF EXISTS idx_org_employee_entry_date;
DROP INDEX IF EXISTS idx_org_employee_position_id;
DROP INDEX IF EXISTS idx_org_employee_dept_id;

-- 多租户联合索引
DROP INDEX IF EXISTS idx_org_employee_tenant_status;
DROP INDEX IF EXISTS idx_org_employee_tenant_code;

-- 部分唯一索引
DROP INDEX IF EXISTS uk_org_employee_code;

-- 主键约束：仅恢复默认命名，不删除约束本身
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conname = 'pk_org_employee' AND conrelid = 'org_employee'::regclass
    ) THEN
        ALTER TABLE org_employee RENAME CONSTRAINT pk_org_employee TO org_employee_pkey;
    END IF;
END $$;
