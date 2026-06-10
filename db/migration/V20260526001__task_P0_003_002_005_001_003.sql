-- ============================================================
-- Flyway Verification Script
-- Version: V20260526001
-- Description: 验证移动端菜单表DDL（P0-003-002-005-001-003）
--   sys_mobile_menu
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- Part 1: 表存在性验证
-- ============================================================

-- 1.1 验证sys_mobile_menu表已创建
SELECT 'TABLE_COUNT_CHECK' AS check_name,
       CASE WHEN COUNT(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 1 table, found ' || COUNT(*) AS detail
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename = 'sys_mobile_menu';

-- 1.2 逐表确认存在
SELECT tablename AS table_name
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename = 'sys_mobile_menu';

-- ============================================================
-- Part 2: 通用字段完整性验证（10个通用必含字段）
-- ============================================================

WITH required_fields AS (
    SELECT unnest(ARRAY[
        'id', 'tenant_id', 'created_at', 'updated_at',
        'created_by', 'updated_by', 'is_deleted',
        'owner_dept_id', 'owner_id', 'version'
    ]) AS column_name
),
target_tables AS (
    SELECT 'sys_mobile_menu'::text AS table_name
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
ORDER BY r.column_name;

-- ============================================================
-- Part 3: 字段类型与约束验证
-- ============================================================

-- 3.1 验证tenant_id字段为BIGINT NOT NULL
SELECT table_name, column_name, data_type, is_nullable,
       CASE WHEN data_type = 'bigint' AND is_nullable = 'NO' THEN 'PASS'
            ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'sys_mobile_menu'
  AND column_name = 'tenant_id';

-- 3.2 验证is_deleted字段为BOOLEAN NOT NULL DEFAULT false
SELECT table_name, column_name, data_type, is_nullable, column_default,
       CASE WHEN data_type = 'boolean' AND is_nullable = 'NO'
             AND column_default ILIKE '%false%' THEN 'PASS'
            ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'sys_mobile_menu'
  AND column_name = 'is_deleted';

-- 3.3 验证数值字段使用decimal(18,8)
SELECT table_name, column_name, data_type, numeric_precision, numeric_scale,
       CASE WHEN data_type = 'numeric' AND numeric_precision = 18 AND numeric_scale = 8
            THEN 'PASS' ELSE 'WARN' END AS result
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'sys_mobile_menu'
  AND column_name IN ('ext_num1', 'ext_num2', 'ext_num3', 'ext_num4', 'ext_num5')
ORDER BY column_name;

-- 3.4 验证version字段为INT NOT NULL DEFAULT 1
SELECT table_name, column_name, data_type, is_nullable, column_default,
       CASE WHEN data_type = 'integer' AND is_nullable = 'NO'
             AND column_default ILIKE '%1%' THEN 'PASS'
            ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'sys_mobile_menu'
  AND column_name = 'version';

-- 3.5 验证主键id为BIGINT (BIGSERIAL)
SELECT kcu.table_name, kcu.column_name, c.data_type,
       CASE WHEN kcu.column_name = 'id' AND c.data_type = 'bigint' THEN 'PASS'
            ELSE 'FAIL' END AS pk_check
FROM information_schema.key_column_usage kcu
JOIN information_schema.columns c
    ON c.table_name = kcu.table_name AND c.column_name = kcu.column_name
    AND c.table_schema = kcu.table_schema
WHERE kcu.table_schema = 'public'
  AND kcu.table_name = 'sys_mobile_menu'
  AND kcu.constraint_name LIKE '%pkey%';

-- 3.6 验证业务字段类型（menu_name=character varying, permission_code=character varying）
SELECT column_name, data_type, character_maximum_length, is_nullable,
       CASE
           WHEN column_name = 'menu_name' AND data_type = 'character varying'
                AND character_maximum_length = 100 AND is_nullable = 'NO'
                THEN 'PASS'
           WHEN column_name = 'menu_type' AND data_type = 'character varying'
                AND character_maximum_length = 20 AND is_nullable = 'NO'
                THEN 'PASS'
           WHEN column_name = 'parent_id' AND data_type = 'bigint' THEN 'PASS'
           WHEN column_name = 'sort_order' AND data_type = 'integer' THEN 'PASS'
           WHEN column_name = 'is_visible' AND data_type = 'boolean'
                AND is_nullable = 'NO' THEN 'PASS'
           WHEN column_name = 'is_enabled' AND data_type = 'boolean'
                AND is_nullable = 'NO' THEN 'PASS'
           ELSE 'CHECK_MANUAL'
       END AS field_check
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'sys_mobile_menu'
  AND column_name IN ('parent_id', 'menu_name', 'menu_type', 'route_path',
                       'icon', 'sort_order', 'is_visible', 'is_enabled',
                       'permission_code')
ORDER BY column_name;

-- ============================================================
-- Part 4: 索引验证
-- ============================================================

-- 4.1 验证索引总数（预期5个业务索引 + 1个PK = 6）
SELECT 'INDEX_COUNT_CHECK' AS check_name,
       CASE WHEN COUNT(*) >= 6 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected >= 6 indexes (1 PK + 5 business), found ' || COUNT(*) AS detail
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename = 'sys_mobile_menu';

-- 4.2 列出所有索引
SELECT tablename AS table_name,
       indexname AS index_name,
       indexdef AS index_definition
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename = 'sys_mobile_menu'
ORDER BY indexname;

-- 4.3 验证唯一索引包含WHERE is_deleted = false条件
SELECT tablename AS table_name,
       indexname AS index_name,
       CASE WHEN indexdef ILIKE '%CREATE UNIQUE INDEX%'
             AND indexdef ILIKE '%WHERE is_deleted = false%'
            THEN 'PASS' ELSE 'WARN' END AS partial_unique_check
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename = 'sys_mobile_menu'
  AND indexdef ILIKE '%CREATE UNIQUE INDEX%'
ORDER BY indexname;

-- 4.4 验证联合索引以tenant_id为首列
SELECT tablename AS table_name,
       indexname AS index_name,
       CASE WHEN indexdef ~ '\(tenant_id[,\)]' THEN 'PASS'
            ELSE 'CHECK_MANUAL' END AS tenant_first_check
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename = 'sys_mobile_menu'
  AND indexname LIKE '%idx_%'
ORDER BY indexname;

-- 4.5 验证索引命名规范
SELECT tablename AS table_name,
       indexname AS index_name,
       CASE
           WHEN indexname LIKE 'uk_%' OR indexname LIKE 'idx_%'
                OR indexname LIKE '%pkey%' OR indexname LIKE '%pk%' THEN 'PASS'
           ELSE 'WARN: non-standard naming'
       END AS naming_check
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename = 'sys_mobile_menu'
ORDER BY indexname;

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
  AND c.relname = 'sys_mobile_menu';

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
  AND c.relname = 'sys_mobile_menu'
  AND a.attnum > 0
  AND a.attisdropped = false
GROUP BY c.relname;

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
  AND table_name = 'sys_mobile_menu';

-- ============================================================
-- Part 7: Flyway迁移记录验证
-- ============================================================

-- 7.1 验证Flyway迁移历史（最近5条）
SELECT version, description, script, installed_on, success
FROM flyway_schema_history
ORDER BY installed_rank DESC
LIMIT 5;

-- 7.2 验证移动端菜单表相关迁移成功
SELECT version, description,
       CASE WHEN success THEN 'PASS' ELSE 'FAIL' END AS status
FROM flyway_schema_history
WHERE version IN ('20260531011', '20260601001')
ORDER BY version;

-- ============================================================
-- Part 8: 综合验证摘要
-- ============================================================
SELECT 'VERIFICATION SUMMARY' AS section,
       'P0-003-002-005-001-003: Verify Parts 1-7 above for sys_mobile_menu. All checks should show PASS.' AS summary;
