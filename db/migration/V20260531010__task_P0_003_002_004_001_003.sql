-- ============================================================
-- Flyway Verification Script
-- Version: V20260531010
-- Description: 验证系统管理表DDL（P0-003-002-004-001-003）
--   sys_param/sys_dict_type/sys_dict_data/sys_code_rule/
--   sys_code_rule_segment/sys_operation_log/sys_data_view/
--   sys_data_view_field/sys_notice/sys_doc_config
-- Author: AI Generated
-- Date: 2026-05-31
-- ============================================================

-- ============================================================
-- Part 1: 表存在性验证
-- ============================================================

-- 1.1 验证10张系统管理表已创建
SELECT 'TABLE_COUNT_CHECK' AS check_name,
       CASE WHEN COUNT(*) = 10 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 10 tables, found ' || COUNT(*) AS detail
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename IN (
      'sys_param', 'sys_dict_type', 'sys_dict_data',
      'sys_code_rule', 'sys_code_rule_segment', 'sys_operation_log',
      'sys_data_view', 'sys_data_view_field', 'sys_notice', 'sys_doc_config'
  );

-- 1.2 逐表确认存在
SELECT tablename AS table_name
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename IN (
      'sys_param', 'sys_dict_type', 'sys_dict_data',
      'sys_code_rule', 'sys_code_rule_segment', 'sys_operation_log',
      'sys_data_view', 'sys_data_view_field', 'sys_notice', 'sys_doc_config'
  )
ORDER BY tablename;

-- ============================================================
-- Part 2: 通用字段完整性验证（每表10个通用必含字段）
-- ============================================================

WITH required_fields AS (
    SELECT unnest(ARRAY[
        'id', 'tenant_id', 'created_at', 'updated_at',
        'created_by', 'updated_by', 'is_deleted',
        'owner_dept_id', 'owner_id', 'version'
    ]) AS column_name
),
target_tables AS (
    SELECT unnest(ARRAY[
        'sys_param', 'sys_dict_type', 'sys_dict_data',
        'sys_code_rule', 'sys_code_rule_segment', 'sys_operation_log',
        'sys_data_view', 'sys_data_view_field', 'sys_notice', 'sys_doc_config'
    ]) AS table_name
),
actual_columns AS (
    SELECT c.table_name, c.column_name
    FROM information_schema.columns c
    JOIN target_tables t ON c.table_name = t.table_name
    WHERE c.table_schema = 'public'
)
SELECT t.table_name,
       r.column_name AS required_field,
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
      'sys_param', 'sys_dict_type', 'sys_dict_data',
      'sys_code_rule', 'sys_code_rule_segment', 'sys_operation_log',
      'sys_data_view', 'sys_data_view_field', 'sys_notice', 'sys_doc_config'
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
      'sys_param', 'sys_dict_type', 'sys_dict_data',
      'sys_code_rule', 'sys_code_rule_segment', 'sys_operation_log',
      'sys_data_view', 'sys_data_view_field', 'sys_notice', 'sys_doc_config'
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
      'sys_param', 'sys_dict_type', 'sys_dict_data',
      'sys_code_rule', 'sys_code_rule_segment', 'sys_operation_log',
      'sys_data_view', 'sys_data_view_field', 'sys_notice', 'sys_doc_config'
  )
  AND column_name IN ('ext_num1', 'ext_num2', 'ext_num3', 'ext_num4', 'ext_num5')
ORDER BY table_name, column_name;

-- 3.4 验证version字段为INT NOT NULL DEFAULT 1
SELECT table_name, column_name, data_type, is_nullable, column_default,
       CASE WHEN data_type = 'integer' AND is_nullable = 'NO'
             AND column_default ILIKE '%1%' THEN 'PASS'
            ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name IN (
      'sys_param', 'sys_dict_type', 'sys_dict_data',
      'sys_code_rule', 'sys_code_rule_segment', 'sys_operation_log',
      'sys_data_view', 'sys_data_view_field', 'sys_notice', 'sys_doc_config'
  )
  AND column_name = 'version'
ORDER BY table_name;

-- 3.5 验证主键id为BIGINT (BIGSERIAL)
SELECT kcu.table_name, kcu.column_name, c.data_type,
       CASE WHEN kcu.column_name = 'id' AND c.data_type = 'bigint' THEN 'PASS'
            ELSE 'FAIL' END AS pk_check
FROM information_schema.key_column_usage kcu
JOIN information_schema.columns c
    ON c.table_name = kcu.table_name AND c.column_name = kcu.column_name
    AND c.table_schema = kcu.table_schema
WHERE kcu.table_schema = 'public'
  AND kcu.table_name IN (
      'sys_param', 'sys_dict_type', 'sys_dict_data',
      'sys_code_rule', 'sys_code_rule_segment', 'sys_operation_log',
      'sys_data_view', 'sys_data_view_field', 'sys_notice', 'sys_doc_config'
  )
  AND kcu.constraint_name LIKE '%pkey%'
ORDER BY kcu.table_name;

-- ============================================================
-- Part 4: 索引验证
-- ============================================================

-- 4.1 验证索引总数（预期17个业务索引 + 10个PK = 27）
SELECT 'INDEX_COUNT_CHECK' AS check_name,
       CASE WHEN COUNT(*) >= 27 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected >= 27 indexes (10 PK + 17 business), found ' || COUNT(*) AS detail
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename IN (
      'sys_param', 'sys_dict_type', 'sys_dict_data',
      'sys_code_rule', 'sys_code_rule_segment', 'sys_operation_log',
      'sys_data_view', 'sys_data_view_field', 'sys_notice', 'sys_doc_config'
  );

-- 4.2 列出所有索引
SELECT tablename AS table_name,
       indexname AS index_name,
       indexdef AS index_definition
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename IN (
      'sys_param', 'sys_dict_type', 'sys_dict_data',
      'sys_code_rule', 'sys_code_rule_segment', 'sys_operation_log',
      'sys_data_view', 'sys_data_view_field', 'sys_notice', 'sys_doc_config'
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
      'sys_param', 'sys_dict_type', 'sys_dict_data',
      'sys_code_rule', 'sys_code_rule_segment', 'sys_operation_log',
      'sys_data_view', 'sys_data_view_field', 'sys_notice', 'sys_doc_config'
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
      'sys_param', 'sys_dict_type', 'sys_dict_data',
      'sys_code_rule', 'sys_code_rule_segment', 'sys_operation_log',
      'sys_data_view', 'sys_data_view_field', 'sys_notice', 'sys_doc_config'
  )
  AND indexname LIKE '%idx_%'
ORDER BY tablename, indexname;

-- 4.5 验证索引命名规范
SELECT tablename AS table_name,
       indexname AS index_name,
       CASE
           WHEN indexname LIKE 'uk_%' OR indexname LIKE 'idx_%' OR indexname LIKE '%pkey%'
               OR indexname LIKE '%pk%' THEN 'PASS'
           ELSE 'WARN: non-standard naming'
       END AS naming_check
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename IN (
      'sys_param', 'sys_dict_type', 'sys_dict_data',
      'sys_code_rule', 'sys_code_rule_segment', 'sys_operation_log',
      'sys_data_view', 'sys_data_view_field', 'sys_notice', 'sys_doc_config'
  )
ORDER BY tablename, indexname;

-- ============================================================
-- Part 5: COMMENT注释完整性验证
-- ============================================================

-- 5.1 验证表级COMMENT
SELECT c.relname AS table_name,
       obj_description(c.oid, 'pg_class') AS table_comment,
       CASE WHEN obj_description(c.oid, 'pg_class') IS NOT NULL THEN 'PASS'
            ELSE 'FAIL: no table comment'
       END AS table_comment_check
FROM pg_class c
JOIN pg_namespace n ON n.oid = c.relnamespace
WHERE c.relkind = 'r'
  AND n.nspname = 'public'
  AND c.relname IN (
      'sys_param', 'sys_dict_type', 'sys_dict_data',
      'sys_code_rule', 'sys_code_rule_segment', 'sys_operation_log',
      'sys_data_view', 'sys_data_view_field', 'sys_notice', 'sys_doc_config'
  )
ORDER BY c.relname;

-- 5.2 验证字段级COMMENT覆盖率
SELECT c.relname AS table_name,
       COUNT(a.attname) AS total_columns,
       COUNT(d.description) AS columns_with_comment,
       CASE WHEN COUNT(a.attname) = COUNT(d.description) THEN 'PASS: 100% coverage'
            ELSE 'FAIL: ' || (COUNT(a.attname) - COUNT(d.description)) || ' column(s) missing comment'
       END AS comment_coverage_check
FROM pg_class c
JOIN pg_namespace n ON n.oid = c.relnamespace
JOIN pg_attribute a ON a.attrelid = c.oid
LEFT JOIN pg_description d ON d.objoid = c.oid AND d.objsubid = a.attnum
WHERE c.relkind = 'r'
  AND n.nspname = 'public'
  AND c.relname IN (
      'sys_param', 'sys_dict_type', 'sys_dict_data',
      'sys_code_rule', 'sys_code_rule_segment', 'sys_operation_log',
      'sys_data_view', 'sys_data_view_field', 'sys_notice', 'sys_doc_config'
  )
  AND a.attnum > 0
  AND a.attisdropped = false
GROUP BY c.relname
ORDER BY c.relname;

-- ============================================================
-- Part 6: 外键约束检查（禁止数据库级外键）
-- ============================================================

SELECT 'FK_CHECK' AS check_name,
       CASE WHEN COUNT(*) = 0 THEN 'PASS: No foreign keys (application-managed)'
            ELSE 'WARN: Found ' || COUNT(*) || ' foreign key(s)'
       END AS result
FROM information_schema.table_constraints
WHERE constraint_schema = 'public'
  AND constraint_type = 'FOREIGN KEY'
  AND table_name IN (
      'sys_param', 'sys_dict_type', 'sys_dict_data',
      'sys_code_rule', 'sys_code_rule_segment', 'sys_operation_log',
      'sys_data_view', 'sys_data_view_field', 'sys_notice', 'sys_doc_config'
  );

-- ============================================================
-- Part 7: Flyway迁移记录验证
-- ============================================================

-- 7.1 验证Flyway迁移历史（最近5条）
SELECT version, description, script, installed_on, success
FROM flyway_schema_history
ORDER BY installed_rank DESC
LIMIT 5;

-- 7.2 验证系统管理表相关迁移成功
SELECT version, description,
       CASE WHEN success THEN 'PASS' ELSE 'FAIL' END AS status
FROM flyway_schema_history
WHERE version IN ('20260531008', '20260531009')
ORDER BY version;

-- ============================================================
-- Part 8: 综合验证摘要
-- ============================================================
SELECT 'VERIFICATION SUMMARY' AS section,
       'P0-003-002-004-001-003: Verify Parts 1-7 above. All checks should show PASS.' AS summary;
