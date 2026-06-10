-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260601046
-- Description: prod_standard_process 标准工序表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-013-001-002
-- ============================================================

-- ============================================================
-- 1. 删除业务查询索引
-- ============================================================
DROP INDEX IF EXISTS idx_prod_standard_process_process_type;
DROP INDEX IF EXISTS idx_prod_standard_process_created_at;
DROP INDEX IF EXISTS idx_prod_standard_process_status;

-- ============================================================
-- 2. 删除多租户联合索引
-- ============================================================
DROP INDEX IF EXISTS idx_prod_standard_process_tenant_status;
DROP INDEX IF EXISTS idx_prod_standard_process_tenant_code;

-- ============================================================
-- 3. 删除部分唯一索引
-- ============================================================
DROP INDEX IF EXISTS uk_prod_standard_process_code;

-- ============================================================
-- 4. 主键约束恢复为默认名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'pk_prod_standard_process') THEN
        ALTER INDEX pk_prod_standard_process RENAME TO prod_standard_process_pkey;
    END IF;
END $$;
