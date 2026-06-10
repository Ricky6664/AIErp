-- ============================================================
-- Flyway Verification Script
-- Version: V20260604001
-- Description: fin_account会计科目表DDL验证查询
-- Author: AI Generated
-- Date: 2026-06-03
-- Task: P0-003-008-003-001-003
-- ============================================================

-- ============================================================
-- 5.1 DDL执行验证
-- ============================================================

-- 5.1.1 确认表已创建
SELECT tablename
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename = 'fin_account';

-- 5.1.2 验证字段定义（列名、数据类型、是否可空）
SELECT column_name,
       data_type,
       is_nullable,
       character_maximum_length,
       numeric_precision,
       numeric_scale,
       column_default
FROM information_schema.columns
WHERE table_name = 'fin_account'
ORDER BY ordinal_position;

-- 5.1.3 验证索引
SELECT indexname,
       indexdef
FROM pg_indexes
WHERE tablename = 'fin_account'
ORDER BY indexname;

-- ============================================================
-- 5.2 约束验证
-- ============================================================

-- 5.2.1 主键约束验证
SELECT constraint_name,
       constraint_type,
       table_name
FROM information_schema.table_constraints
WHERE table_name = 'fin_account'
  AND constraint_type = 'PRIMARY KEY';

-- 5.2.2 部分唯一索引WHERE条件验证（必须含 is_deleted = false）
SELECT indexname,
       indexdef
FROM pg_indexes
WHERE tablename = 'fin_account'
  AND indexname LIKE 'uk_%'
  AND indexdef ILIKE '%WHERE%is_deleted%false%';

-- 5.2.3 所有索引COUNT验证
SELECT COUNT(*) AS index_count
FROM pg_indexes
WHERE tablename = 'fin_account';

-- ============================================================
-- 5.3 Flyway版本验证
-- ============================================================

SELECT version,
       description,
       success,
       installed_on
FROM flyway_schema_history
ORDER BY installed_rank DESC
LIMIT 5;

-- ============================================================
-- 5.4 COMMENT完整性验证
-- ============================================================

-- 5.4.1 表注释验证
SELECT obj_description('fin_account'::regclass, 'pg_class') AS table_comment;

-- 5.4.2 字段注释完整性验证
SELECT c.column_name,
       col_description('fin_account'::regclass, c.ordinal_position) AS column_comment
FROM information_schema.columns c
WHERE c.table_name = 'fin_account'
ORDER BY c.ordinal_position;

-- 5.4.3 缺失注释的字段（应为0行）
SELECT c.column_name
FROM information_schema.columns c
WHERE c.table_name = 'fin_account'
  AND col_description('fin_account'::regclass, c.ordinal_position) IS NULL;

-- ============================================================
-- 5.5 通用字段完整性验证（10个通用字段）
-- ============================================================

-- 验证10个通用字段全部存在（预期返回10行）
SELECT column_name
FROM information_schema.columns
WHERE table_name = 'fin_account'
  AND column_name IN (
    'id', 'tenant_id', 'created_by', 'created_at', 'updated_by',
    'updated_at', 'is_deleted', 'owner_dept_id', 'owner_id', 'version'
  );

-- ============================================================
-- 5.6 业务字段验证
-- ============================================================

-- 验证核心业务字段
SELECT column_name,
       data_type,
       is_nullable,
       character_maximum_length
FROM information_schema.columns
WHERE table_name = 'fin_account'
  AND column_name IN (
    'parent_id', 'account_code', 'account_name',
    'account_type', 'balance_direction'
  )
ORDER BY ordinal_position;
