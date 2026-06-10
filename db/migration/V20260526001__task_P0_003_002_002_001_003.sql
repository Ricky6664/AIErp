-- ============================================================
-- Flyway Verification Script
-- Version: V20260526001
-- Description: 验证系统核心表DDL（P0-003-002-002-001-003）
--   - 确认10张系统核心表已创建
--   - 验证字段定义、约束、索引
--   - 验证COMMENT完整性
--   - 验证Flyway迁移记录
-- Author: AI Generated
-- Date: 2026-05-31
-- ============================================================

-- ============================================================
-- Part 1: 表存在性验证
-- ============================================================

-- 1.1 验证10张系统核心表已创建
SELECT 'TABLE_COUNT_CHECK' AS check_name,
       CASE WHEN COUNT(*) = 10 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 10 tables, found ' || COUNT(*) AS detail
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename IN (
      'sys_user', 'sys_role', 'sys_menu',
      'sys_user_role', 'sys_user_dept', 'sys_role_menu',
      'sys_role_data_scope', 'sys_role_field_permission',
      'sys_user_group', 'sys_user_group_member'
  );

-- 1.2 逐表确认存在
SELECT tablename AS table_name,
       CASE WHEN hasindexes THEN 'YES' ELSE 'NO' END AS has_indexes,
       CASE WHEN hasrules THEN 'YES' ELSE 'NO' END AS has_rules,
       CASE WHEN hastriggers THEN 'YES' ELSE 'NO' END AS has_triggers
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename IN (
      'sys_user', 'sys_role', 'sys_menu',
      'sys_user_role', 'sys_user_dept', 'sys_role_menu',
      'sys_role_data_scope', 'sys_role_field_permission',
      'sys_user_group', 'sys_user_group_member'
  )
ORDER BY tablename;

-- ============================================================
-- Part 2: 字段完整性验证（每表10个通用必含字段）
-- ============================================================

-- 2.1 验证所有系统核心表包含10个通用必含字段
WITH required_fields AS (
    SELECT unnest(ARRAY[
        'id', 'tenant_id', 'created_at', 'updated_at',
        'created_by', 'updated_by', 'is_deleted',
        'owner_dept_id', 'owner_id', 'version'
    ]) AS column_name
),
target_tables AS (
    SELECT unnest(ARRAY[
        'sys_user', 'sys_role', 'sys_menu',
        'sys_user_role', 'sys_user_dept', 'sys_role_menu',
        'sys_role_data_scope', 'sys_role_field_permission',
        'sys_user_group', 'sys_user_group_member'
    ]) AS table_name
),
actual_columns AS (
    SELECT c.table_name, c.column_name
    FROM information_schema.columns c
    JOIN target_tables t ON c.table_name = t.table_name
    WHERE c.table_schema = 'public'
)
SELECT t.table_name,
       r.column_name AS missing_field,
       CASE WHEN a.column_name IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM target_tables t
CROSS JOIN required_fields r
LEFT JOIN actual_columns a ON a.table_name = t.table_name AND a.column_name = r.column_name
ORDER BY t.table_name, r.column_name;

-- ============================================================
-- Part 3: 字段类型与约束验证
-- ============================================================

-- 3.1 验证tenant_id字段为BIGINT NOT NULL
SELECT table_name, column_name, data_type, is_nullable,
       CASE WHEN data_type = 'bigint' AND is_nullable = 'NO' THEN 'PASS'
            ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name IN (
      'sys_user', 'sys_role', 'sys_menu',
      'sys_user_role', 'sys_user_dept', 'sys_role_menu',
      'sys_role_data_scope', 'sys_role_field_permission',
      'sys_user_group', 'sys_user_group_member'
  )
  AND column_name = 'tenant_id'
ORDER BY table_name;

-- 3.2 验证is_deleted字段为BOOLEAN NOT NULL DEFAULT false
SELECT table_name, column_name, data_type, is_nullable, column_default,
       CASE WHEN data_type = 'boolean' AND is_nullable = 'NO'
             AND column_default ILIKE '%false%' THEN 'PASS'
            ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name IN (
      'sys_user', 'sys_role', 'sys_menu',
      'sys_user_role', 'sys_user_dept', 'sys_role_menu',
      'sys_role_data_scope', 'sys_role_field_permission',
      'sys_user_group', 'sys_user_group_member'
  )
  AND column_name = 'is_deleted'
ORDER BY table_name;

-- 3.3 验证数值字段使用decimal(18,8)
SELECT table_name, column_name, data_type, numeric_precision, numeric_scale,
       CASE WHEN data_type = 'numeric' AND numeric_precision = 18 AND numeric_scale = 8
            THEN 'PASS' ELSE 'WARN' END AS result
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name IN (
      'sys_user', 'sys_role', 'sys_menu',
      'sys_user_role', 'sys_user_dept', 'sys_role_menu',
      'sys_role_data_scope', 'sys_role_field_permission',
      'sys_user_group', 'sys_user_group_member'
  )
  AND column_name IN ('ext_num1', 'ext_num2', 'ext_num3', 'ext_num4', 'ext_num5')
ORDER BY table_name, column_name;

-- ============================================================
-- Part 4: 索引验证
-- ============================================================

-- 4.1 验证索引总数
SELECT 'INDEX_COUNT_CHECK' AS check_name,
       CASE WHEN COUNT(*) >= 27 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected >= 27 indexes, found ' || COUNT(*) AS detail
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename IN (
      'sys_user', 'sys_role', 'sys_menu',
      'sys_user_role', 'sys_user_dept', 'sys_role_menu',
      'sys_role_data_scope', 'sys_role_field_permission',
      'sys_user_group', 'sys_user_group_member'
  );

-- 4.2 列出所有索引（验证命名规范与WHERE条件）
SELECT tablename AS table_name,
       indexname AS index_name,
       indexdef AS index_definition
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename IN (
      'sys_user', 'sys_role', 'sys_menu',
      'sys_user_role', 'sys_user_dept', 'sys_role_menu',
      'sys_role_data_scope', 'sys_role_field_permission',
      'sys_user_group', 'sys_user_group_member'
  )
ORDER BY tablename, indexname;

-- 4.3 验证唯一索引包含WHERE is_deleted = false条件
SELECT tablename AS table_name,
       indexname AS index_name,
       CASE WHEN indexdef ILIKE '%CREATE UNIQUE INDEX%'
             AND indexdef ILIKE '%WHERE is_deleted = false%'
            THEN 'PASS' ELSE 'WARN' END AS partial_unique_check
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename IN (
      'sys_user', 'sys_role', 'sys_menu',
      'sys_user_role', 'sys_user_dept', 'sys_role_menu',
      'sys_role_data_scope', 'sys_role_field_permission',
      'sys_user_group', 'sys_user_group_member'
  )
  AND indexdef ILIKE '%CREATE UNIQUE INDEX%'
ORDER BY tablename, indexname;

-- 4.4 验证联合索引以tenant_id为首列
SELECT tablename AS table_name,
       indexname AS index_name,
       CASE WHEN indexdef ~ '\(tenant_id[,\)]' THEN 'PASS'
            ELSE 'CHECK_MANUAL' END AS tenant_first_check
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename IN (
      'sys_user', 'sys_role', 'sys_menu',
      'sys_user_role', 'sys_user_dept', 'sys_role_menu',
      'sys_role_data_scope', 'sys_role_field_permission',
      'sys_user_group', 'sys_user_group_member'
  )
  AND indexname LIKE '%idx_%'
ORDER BY tablename, indexname;

-- ============================================================
-- Part 5: COMMENT注释完整性验证
-- ============================================================

-- 5.1 验证表级COMMENT
SELECT t.tablename AS table_name,
       CASE WHEN d.description IS NOT NULL THEN 'PASS' ELSE 'FAIL - MISSING TABLE COMMENT' END AS table_comment
FROM pg_tables t
LEFT JOIN pg_description d ON d.objoid = (quote_ident(t.schemaname) || '.' || quote_ident(t.tablename))::regclass
                          AND d.objsubid = 0
WHERE t.schemaname = 'public'
  AND t.tablename IN (
      'sys_user', 'sys_role', 'sys_menu',
      'sys_user_role', 'sys_user_dept', 'sys_role_menu',
      'sys_role_data_scope', 'sys_role_field_permission',
      'sys_user_group', 'sys_user_group_member'
  )
ORDER BY t.tablename;

-- 5.2 验证字段级COMMENT覆盖率
SELECT c.table_name,
       COUNT(*) AS total_columns,
       COUNT(pgd.description) AS columns_with_comment,
       CASE WHEN COUNT(*) = COUNT(pgd.description) THEN 'PASS (100%)'
            ELSE 'FAIL - ' || (COUNT(*) - COUNT(pgd.description)) || ' columns missing comment'
       END AS comment_coverage
FROM information_schema.columns c
LEFT JOIN pg_description pgd ON pgd.objoid = (quote_ident(c.table_schema) || '.' || quote_ident(c.table_name))::regclass
                             AND pgd.objsubid = c.ordinal_position
WHERE c.table_schema = 'public'
  AND c.table_name IN (
      'sys_user', 'sys_role', 'sys_menu',
      'sys_user_role', 'sys_user_dept', 'sys_role_menu',
      'sys_role_data_scope', 'sys_role_field_permission',
      'sys_user_group', 'sys_user_group_member'
  )
GROUP BY c.table_name
ORDER BY c.table_name;

-- ============================================================
-- Part 6: 外键约束检查（禁止外键）
-- ============================================================

-- 6.1 确认无数据库级外键约束
SELECT 'FK_CHECK' AS check_name,
       CASE WHEN COUNT(*) = 0 THEN 'PASS - No foreign keys (application-managed)'
            ELSE 'WARN - Found ' || COUNT(*) || ' foreign key(s)'
       END AS result
FROM information_schema.table_constraints
WHERE constraint_schema = 'public'
  AND constraint_type = 'FOREIGN KEY'
  AND table_name IN (
      'sys_user', 'sys_role', 'sys_menu',
      'sys_user_role', 'sys_user_dept', 'sys_role_menu',
      'sys_role_data_scope', 'sys_role_field_permission',
      'sys_user_group', 'sys_user_group_member'
  );

-- ============================================================
-- Part 7: Flyway迁移记录验证
-- ============================================================

-- 7.1 验证Flyway迁移历史（最近5条）
SELECT version, description, script, installed_on, success
FROM flyway_schema_history
ORDER BY installed_rank DESC
LIMIT 5;

-- 7.2 验证V20260531004和V20260531005迁移成功
SELECT version, description,
       CASE WHEN success THEN 'PASS' ELSE 'FAIL' END AS status
FROM flyway_schema_history
WHERE version IN ('20260531004', '20260531005')
ORDER BY version;

-- ============================================================
-- Part 8: 综合验证摘要
-- ============================================================
SELECT 'VERIFICATION SUMMARY' AS section,
       'Run Parts 1-7 and review all PASS/FAIL results above.' AS summary;
