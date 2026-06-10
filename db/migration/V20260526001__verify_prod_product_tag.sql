-- ============================================================
-- Verification Script
-- Version: V20260526001
-- Description: prod_product_tag 商品标签关联表 DDL 验证查询
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-020-001-003
-- ============================================================

-- ============================================================
-- 1. 表存在性验证
-- ============================================================
SELECT 'CHECK_TABLE_EXISTS' AS check_name,
       CASE WHEN COUNT(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'prod_product_tag table exists in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'prod_product_tag';

-- ============================================================
-- 2. 字段定义验证（14字段：10通用 + 4业务）
-- ============================================================
SELECT 'CHECK_COLUMN_COUNT' AS check_name,
       CASE WHEN COUNT(*) = 14 THEN 'PASS' ELSE 'FAIL: expected 14, got ' || COUNT(*)::TEXT END AS result,
       '14 columns total (10 standard + 4 business)' AS description
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'prod_product_tag';

-- 2.1 通用字段（10个）
SELECT 'CHECK_STANDARD_COLUMNS' AS check_name,
       CASE WHEN COUNT(*) = 10 THEN 'PASS' ELSE 'FAIL: expected 10, got ' || COUNT(*)::TEXT END AS result,
       '10 standard columns present' AS description
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'prod_product_tag'
  AND column_name IN ('id', 'tenant_id', 'created_at', 'updated_at', 'created_by', 'updated_by',
                      'is_deleted', 'owner_dept_id', 'owner_id', 'version');

-- 2.2 业务字段（4个）
SELECT 'CHECK_BUSINESS_COLUMNS' AS check_name,
       CASE WHEN COUNT(*) = 4 THEN 'PASS' ELSE 'FAIL: expected 4, got ' || COUNT(*)::TEXT END AS result,
       '4 business columns present' AS description
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'prod_product_tag'
  AND column_name IN ('product_id', 'tag_definition_id', 'tag_value', 'sort_order');

-- 2.3 逐字段类型验证
SELECT 'CHECK_COLUMN_' || column_name AS check_name,
       CASE
           WHEN data_type = expected_type
                OR (data_type = 'character varying' AND expected_type = 'varchar')
                OR (data_type = 'timestamp without time zone' AND expected_type = 'timestamp')
           THEN 'PASS'
           ELSE 'FAIL: expected ' || expected_type || ', got ' || data_type
       END AS result,
       column_name || ' data_type is ' || data_type AS description
FROM (
    VALUES
        ('id', 'bigint'),
        ('tenant_id', 'bigint'),
        ('product_id', 'bigint'),
        ('tag_definition_id', 'bigint'),
        ('tag_value', 'varchar'),
        ('sort_order', 'integer'),
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
    AND c.table_name = 'prod_product_tag';

-- 2.4 必填字段NOT NULL验证
SELECT 'CHECK_NOTNULL_COLUMNS' AS check_name,
       CASE WHEN COUNT(*) = 6 THEN 'PASS' ELSE 'FAIL: expected 6 NOT NULL, got ' || COUNT(*)::TEXT END AS result,
       '6 columns with NOT NULL' AS description
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'prod_product_tag'
  AND is_nullable = 'NO'
  AND column_name IN ('id', 'tenant_id', 'product_id', 'sort_order', 'created_at', 'updated_at', 'is_deleted', 'version');

-- ============================================================
-- 3. 索引验证
-- ============================================================
SELECT 'CHECK_INDEX_COUNT' AS check_name,
       CASE WHEN COUNT(*) >= 6 THEN 'PASS' ELSE 'FAIL: expected >=6 indexes, got ' || COUNT(*)::TEXT END AS result,
       'At least 6 indexes exist (PK + 1 UK + 4 B-Tree)' AS description
FROM pg_indexes
WHERE schemaname = 'public' AND tablename = 'prod_product_tag';

-- 3.1 主键索引
SELECT 'CHECK_PK_INDEX' AS check_name,
       CASE WHEN COUNT(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'PK index pk_prod_product_tag exists' AS description
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename = 'prod_product_tag'
  AND indexname = 'pk_prod_product_tag';

-- 3.2 部分唯一索引验证（必须含 WHERE is_deleted = false）
SELECT 'CHECK_UNIQUE_INDEX_WHERE' AS check_name,
       CASE WHEN COUNT(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Unique index uk_prod_product_tag_product_def with WHERE is_deleted=false exists' AS description
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename = 'prod_product_tag'
  AND indexname = 'uk_prod_product_tag_product_def'
  AND indexdef ILIKE '%WHERE is_deleted = false%';

-- 3.3 多租户联合索引（tenant_id为首列）
SELECT 'CHECK_TENANT_INDEX' AS check_name,
       CASE WHEN COUNT(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Multi-tenant index idx_prod_product_tag_tenant_product with tenant_id as first column' AS description
FROM pg_indexes
WHERE schemaname = 'public'
  AND tablename = 'prod_product_tag'
  AND indexname = 'idx_prod_product_tag_tenant_product'
  AND indexdef ILIKE '%ON prod_product_tag USING btree (tenant_id%';

-- 3.4 所有预期索引存在
SELECT 'CHECK_INDEX_' || expected_name AS check_name,
       CASE WHEN pi.indexname IS NOT NULL THEN 'PASS' ELSE 'FAIL: index not found' END AS result,
       expected_name || ' exists' AS description
FROM (VALUES
    ('pk_prod_product_tag'),
    ('uk_prod_product_tag_product_def'),
    ('idx_prod_product_tag_tenant_product'),
    ('idx_prod_product_tag_product_id'),
    ('idx_prod_product_tag_tag_def_id'),
    ('idx_prod_product_tag_sort_order')
) AS expected(expected_name)
LEFT JOIN pg_indexes pi
    ON pi.indexname = expected.expected_name
    AND pi.schemaname = 'public'
    AND pi.tablename = 'prod_product_tag';

-- ============================================================
-- 4. 约束验证
-- ============================================================
-- 4.1 主键约束
SELECT 'CHECK_PK_CONSTRAINT' AS check_name,
       CASE WHEN COUNT(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Primary key constraint exists on prod_product_tag' AS description
FROM information_schema.table_constraints
WHERE table_schema = 'public'
  AND table_name = 'prod_product_tag'
  AND constraint_type = 'PRIMARY KEY';

-- 4.2 无外键约束
SELECT 'CHECK_NO_FOREIGN_KEYS' AS check_name,
       CASE WHEN COUNT(*) = 0 THEN 'PASS' ELSE 'FAIL: found ' || COUNT(*)::TEXT || ' foreign keys' END AS result,
       'No foreign key constraints (application-layer management)' AS description
FROM information_schema.table_constraints
WHERE table_schema = 'public'
  AND table_name = 'prod_product_tag'
  AND constraint_type = 'FOREIGN KEY';

-- ============================================================
-- 5. COMMENT 完整性验证
-- ============================================================
-- 5.1 表注释
SELECT 'CHECK_TABLE_COMMENT' AS check_name,
       CASE WHEN obj_description(c.oid) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result,
       'Table COMMENT on prod_product_tag: ' || COALESCE(obj_description(c.oid), 'MISSING') AS description
FROM pg_class c
JOIN pg_namespace n ON c.relnamespace = n.oid
WHERE n.nspname = 'public' AND c.relname = 'prod_product_tag' AND c.relkind = 'r';

-- 5.2 字段注释完整性
SELECT 'CHECK_COLUMN_COMMENTS' AS check_name,
       CASE WHEN COUNT(*) = 0 THEN 'PASS' ELSE 'FAIL: ' || COUNT(*)::TEXT || ' columns missing COMMENT' END AS result,
       'All columns have COMMENT' AS description
FROM information_schema.columns c
LEFT JOIN pg_catalog.pg_description d
    ON d.objoid = (SELECT oid FROM pg_class WHERE relname = 'prod_product_tag' AND relnamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'public'))
    AND d.objsubid = c.ordinal_position
WHERE c.table_schema = 'public'
  AND c.table_name = 'prod_product_tag'
  AND d.description IS NULL;

-- ============================================================
-- 6. Flyway 版本验证
-- ============================================================
SELECT 'CHECK_FLYWAY_VERSIONS' AS check_name,
       CASE WHEN COUNT(*) >= 2 THEN 'PASS' ELSE 'FAIL: expected >=2 migrations, got ' || COUNT(*)::TEXT END AS result,
       'Flyway migrations for prod_product_tag are recorded' AS description
FROM flyway_schema_history
WHERE description ILIKE '%prod_product_tag%';

-- 最近5条Flyway记录
SELECT version, description, type, installed_on, success
FROM flyway_schema_history
ORDER BY installed_rank DESC
LIMIT 5;

-- ============================================================
-- 7. 汇总
-- ============================================================
SELECT '===== VERIFICATION SUMMARY =====' AS summary;
