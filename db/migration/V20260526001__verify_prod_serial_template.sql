-- ============================================================
-- Verification Script
-- Version: V20260526001
-- Description: prod_serial_template 序列号模板表 DDL 验证查询
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-021-001-003
-- ============================================================

-- ============================================================
-- 1. 表存在性验证
-- ============================================================
SELECT 'CHECK_TABLE_EXISTS' AS check_name,
       CASE WHEN COUNT(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'prod_serial_template table exists in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'prod_serial_template';

-- ============================================================
-- 2. 字段定义验证（18字段：10通用 + 8业务）
-- ============================================================
SELECT 'CHECK_COLUMN_COUNT' AS check_name,
       CASE WHEN COUNT(*) = 18 THEN 'PASS' ELSE 'FAIL: expected 18, got ' || COUNT(*)::TEXT END AS result,
       '18 columns total (10 standard + 8 business)' AS description
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'prod_serial_template';

-- 2.1 通用字段（10个）
SELECT 'CHECK_STANDARD_COLUMNS' AS check_name,
       CASE WHEN COUNT(*) = 10 THEN 'PASS' ELSE 'FAIL: expected 10, got ' || COUNT(*)::TEXT END AS result,
       '10 standard columns present' AS description
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'prod_serial_template'
  AND column_name IN ('id', 'tenant_id', 'created_at', 'updated_at', 'created_by', 'updated_by',
                      'is_deleted', 'owner_dept_id', 'owner_id', 'version');

-- 2.2 业务字段（8个）
SELECT 'CHECK_BUSINESS_COLUMNS' AS check_name,
       CASE WHEN COUNT(*) = 8 THEN 'PASS' ELSE 'FAIL: expected 8, got ' || COUNT(*)::TEXT END AS result,
       '8 business columns present' AS description
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'prod_serial_template'
  AND column_name IN ('code', 'name', 'prefix', 'serial_length', 'start_value',
                      'step_value', 'reset_cycle', 'status');

-- 2.3 逐字段类型验证
SELECT 'CHECK_COLUMN_' || column_name AS check_name,
       CASE
           WHEN data_type = expected_type
                OR (data_type = 'character varying' AND expected_type = 'varchar')
                OR (data_type = 'timestamp without time zone' AND expected_type = 'timestamp')
                OR (data_type = 'smallint' AND expected_type = 'smallint')
           THEN 'PASS'
           ELSE 'FAIL: expected ' || expected_type || ', got ' || data_type
       END AS result,
       column_name || ' data_type is ' || data_type AS description
FROM (
    VALUES
        ('id', 'bigint'),
        ('tenant_id', 'bigint'),
        ('code', 'varchar'),
        ('name', 'varchar'),
        ('prefix', 'varchar'),
        ('serial_length', 'integer'),
        ('start_value', 'integer'),
        ('step_value', 'integer'),
        ('reset_cycle', 'varchar'),
        ('status', 'smallint'),
        ('created_at', 'timestamp'),
        ('updated_at', 'timestamp'),
        ('created_by', 'bigint'),
        ('updated_by', 'bigint'),
        ('is_deleted', 'boolean'),
        ('owner_dept_id', 'bigint'),
        ('owner_id', 'bigint'),
        ('version', 'integer')
) AS expected(column_name, expected_type)
JOIN information_schema.columns c
    ON c.column_name = expected.column_name
    AND c.table_schema = 'public'
    AND c.table_name = 'prod_serial_template';

-- 2.4 必填字段NOT NULL验证
SELECT 'CHECK_NOTNULL_COLUMNS' AS check_name,
       CASE WHEN COUNT(*) >= 7 THEN 'PASS' ELSE 'FAIL: expected >=7 NOT NULL, got ' || COUNT(*)::TEXT END AS result,
       'Required columns have NOT NULL constraint' AS description
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'prod_serial_template'
  AND is_nullable = 'NO'
  AND column_name IN ('id', 'tenant_id', 'code', 'name', 'serial_length', 'start_value',
                      'step_value', 'status', 'created_at', 'updated_at', 'is_deleted', 'version');

-- ============================================================
-- 3. 索引验证
-- ============================================================
SELECT 'CHECK_INDEX_COUNT' AS check_name,
       CASE WHEN COUNT(*) >= 6 THEN 'PASS' ELSE 'FAIL: expected >=6 indexes, got ' || COUNT(*)::TEXT END AS result,
       'At least 6 indexes exist (PK + 1 UK + 4 B-Tree)' AS description
FROM pg_indexes
WHERE schemaname = 'public' AND tablename = 'prod_serial_template';

-- 3.1 主键索引
SELECT 'CHECK_PK_INDEX' AS check_name,
       CASE WHEN COUNT(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'PK index pk_prod_serial_template exists' AS description
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename = 'prod_serial_template'
  AND indexname = 'pk_prod_serial_template';

-- 3.2 部分唯一索引验证（必须含 WHERE is_deleted = false）
SELECT 'CHECK_UNIQUE_INDEX_WHERE' AS check_name,
       CASE WHEN COUNT(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Unique index uk_prod_serial_template_code with WHERE is_deleted=false exists' AS description
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename = 'prod_serial_template'
  AND indexname = 'uk_prod_serial_template_code'
  AND indexdef ILIKE '%WHERE is_deleted = false%';

-- 3.3 多租户联合索引（tenant_id为首列）
SELECT 'CHECK_TENANT_INDEX' AS check_name,
       CASE WHEN COUNT(*) = 4 THEN 'PASS' ELSE 'FAIL: expected 4 tenant-first indexes, got ' || COUNT(*)::TEXT END AS result,
       'All multi-tenant indexes have tenant_id as first column' AS description
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename = 'prod_serial_template'
  AND indexname IN ('idx_prod_serial_template_tenant_code', 'idx_prod_serial_template_tenant_status',
                    'idx_prod_serial_template_tenant_name', 'idx_prod_serial_template_tenant_created_at')
  AND indexdef ILIKE '%ON prod_serial_template USING btree (tenant_id%';

-- 3.4 所有预期索引存在
SELECT 'CHECK_INDEX_' || expected_name AS check_name,
       CASE WHEN pi.indexname IS NOT NULL THEN 'PASS' ELSE 'FAIL: index not found' END AS result,
       expected_name || ' exists' AS description
FROM (VALUES
    ('pk_prod_serial_template'),
    ('uk_prod_serial_template_code'),
    ('idx_prod_serial_template_tenant_code'),
    ('idx_prod_serial_template_tenant_status'),
    ('idx_prod_serial_template_tenant_name'),
    ('idx_prod_serial_template_tenant_created_at')
) AS expected(expected_name)
LEFT JOIN pg_indexes pi
    ON pi.indexname = expected.expected_name
    AND pi.schemaname = 'public'
    AND pi.tablename = 'prod_serial_template';

-- ============================================================
-- 4. 约束验证
-- ============================================================
-- 4.1 主键约束
SELECT 'CHECK_PK_CONSTRAINT' AS check_name,
       CASE WHEN COUNT(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Primary key constraint exists on prod_serial_template' AS description
FROM information_schema.table_constraints
WHERE table_schema = 'public'
  AND table_name = 'prod_serial_template'
  AND constraint_type = 'PRIMARY KEY';

-- 4.2 无外键约束
SELECT 'CHECK_NO_FOREIGN_KEYS' AS check_name,
       CASE WHEN COUNT(*) = 0 THEN 'PASS' ELSE 'FAIL: found ' || COUNT(*)::TEXT || ' foreign keys' END AS result,
       'No foreign key constraints (application-layer management)' AS description
FROM information_schema.table_constraints
WHERE table_schema = 'public'
  AND table_name = 'prod_serial_template'
  AND constraint_type = 'FOREIGN KEY';

-- ============================================================
-- 5. COMMENT 完整性验证
-- ============================================================
-- 5.1 表注释
SELECT 'CHECK_TABLE_COMMENT' AS check_name,
       CASE WHEN obj_description(c.oid) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result,
       'Table COMMENT on prod_serial_template: ' || COALESCE(obj_description(c.oid), 'MISSING') AS description
FROM pg_class c
JOIN pg_namespace n ON c.relnamespace = n.oid
WHERE n.nspname = 'public' AND c.relname = 'prod_serial_template' AND c.relkind = 'r';

-- 5.2 字段注释完整性
SELECT 'CHECK_COLUMN_COMMENTS' AS check_name,
       CASE WHEN COUNT(*) = 0 THEN 'PASS' ELSE 'FAIL: ' || COUNT(*)::TEXT || ' columns missing COMMENT' END AS result,
       'All columns have COMMENT' AS description
FROM information_schema.columns c
LEFT JOIN pg_catalog.pg_description d
    ON d.objoid = (SELECT oid FROM pg_class WHERE relname = 'prod_serial_template' AND relnamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'public'))
    AND d.objsubid = c.ordinal_position
WHERE c.table_schema = 'public'
  AND c.table_name = 'prod_serial_template'
  AND d.description IS NULL;

-- ============================================================
-- 6. Flyway 版本验证
-- ============================================================
SELECT 'CHECK_FLYWAY_VERSIONS' AS check_name,
       CASE WHEN COUNT(*) >= 2 THEN 'PASS' ELSE 'FAIL: expected >=2 migrations, got ' || COUNT(*)::TEXT END AS result,
       'Flyway migrations for prod_serial_template are recorded' AS description
FROM flyway_schema_history
WHERE description ILIKE '%prod_serial_template%';

-- 最近5条Flyway记录
SELECT version, description, type, installed_on, success
FROM flyway_schema_history
ORDER BY installed_rank DESC
LIMIT 5;

-- ============================================================
-- 7. 汇总
-- ============================================================
SELECT '===== VERIFICATION SUMMARY =====' AS summary;
