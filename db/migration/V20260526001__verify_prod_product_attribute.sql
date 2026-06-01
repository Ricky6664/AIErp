-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: prod_product_attribute + prod_product_attribute_value 商品属性表DDL验证查询脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 表存在性验证（prod_product_attribute）
-- ============================================================
SELECT 'TABLE_EXISTS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'prod_product_attribute table should exist in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'prod_product_attribute';

-- ============================================================
-- 2. 表存在性验证（prod_product_attribute_value）
-- ============================================================
SELECT 'TABLE_EXISTS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'prod_product_attribute_value table should exist in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'prod_product_attribute_value';

-- ============================================================
-- 3. 字段定义验证（prod_product_attribute）
-- ============================================================
SELECT column_name,
       data_type,
       character_maximum_length,
       numeric_precision,
       numeric_scale,
       is_nullable,
       column_default
FROM information_schema.columns
WHERE table_name = 'prod_product_attribute'
ORDER BY ordinal_position;

-- ============================================================
-- 4. 字段定义验证（prod_product_attribute_value）
-- ============================================================
SELECT column_name,
       data_type,
       character_maximum_length,
       numeric_precision,
       numeric_scale,
       is_nullable,
       column_default
FROM information_schema.columns
WHERE table_name = 'prod_product_attribute_value'
ORDER BY ordinal_position;

-- ============================================================
-- 5. 10个通用字段存在性验证（prod_product_attribute）
-- ============================================================
WITH required_fields AS (
    SELECT unnest(ARRAY[
        'id', 'tenant_id', 'created_by', 'created_at',
        'updated_by', 'updated_at', 'is_deleted',
        'owner_dept_id', 'owner_id', 'version'
    ]) AS field_name
),
actual_fields AS (
    SELECT column_name FROM information_schema.columns WHERE table_name = 'prod_product_attribute'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- ============================================================
-- 6. 10个通用字段存在性验证（prod_product_attribute_value）
-- ============================================================
WITH required_fields AS (
    SELECT unnest(ARRAY[
        'id', 'tenant_id', 'created_by', 'created_at',
        'updated_by', 'updated_at', 'is_deleted',
        'owner_dept_id', 'owner_id', 'version'
    ]) AS field_name
),
actual_fields AS (
    SELECT column_name FROM information_schema.columns WHERE table_name = 'prod_product_attribute_value'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- ============================================================
-- 7. 字段数量验证（prod_product_attribute：期望 16 = 6业务 + 10通用）
-- ============================================================
SELECT 'FIELD_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 16 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 16 columns (6 business + 10 common)' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'prod_product_attribute';

-- ============================================================
-- 8. 字段数量验证（prod_product_attribute_value：期望 15 = 5业务 + 10通用）
-- ============================================================
SELECT 'FIELD_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 15 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 15 columns (5 business + 10 common)' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'prod_product_attribute_value';

-- ============================================================
-- 9. 主键约束验证（prod_product_attribute）
-- ============================================================
SELECT 'PK_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'prod_product_attribute should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'prod_product_attribute' AND c.contype = 'p';

-- ============================================================
-- 10. 主键约束验证（prod_product_attribute_value）
-- ============================================================
SELECT 'PK_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'prod_product_attribute_value should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'prod_product_attribute_value' AND c.contype = 'p';

-- ============================================================
-- 11. 部分唯一索引验证（WHERE is_deleted = false）
-- ============================================================
SELECT indexname,
       indexdef,
       CASE WHEN indexdef ILIKE '%WHERE%is_deleted%false%' THEN 'PASS' ELSE 'FAIL' END AS partial_unique_check
FROM pg_indexes
WHERE tablename = 'prod_product_attribute'
  AND indexname LIKE 'uk_%';

-- ============================================================
-- 12. 全部索引列表验证（prod_product_attribute）
-- ============================================================
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'prod_product_attribute'
ORDER BY indexname;

-- ============================================================
-- 13. 全部索引列表验证（prod_product_attribute_value）
-- ============================================================
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'prod_product_attribute_value'
ORDER BY indexname;

-- ============================================================
-- 14. 多租户联合索引验证（tenant_id为首列，prod_product_attribute）
-- ============================================================
SELECT 'TENANT_INDEX_CHECK' AS check_name,
       CASE WHEN count(*) >= 4 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected at least 4 tenant_id-leading indexes' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'prod_product_attribute'
  AND indexdef ~ 'USING btree \(tenant_id';

-- ============================================================
-- 15. 索引数量验证（prod_product_attribute：期望 7 = 1 PK + 1 唯一 + 5 普通）
-- ============================================================
SELECT 'INDEX_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 7 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 7 indexes (1 PK + 1 unique + 5 standard)' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'prod_product_attribute';

-- ============================================================
-- 16. COMMENT注释完整性验证（prod_product_attribute）
-- ============================================================
SELECT 'TABLE_COMMENT_CHECK' AS check_name,
       CASE WHEN obj_description('prod_product_attribute'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('prod_product_attribute'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('prod_product_attribute'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'prod_product_attribute'
ORDER BY c.ordinal_position;

-- ============================================================
-- 17. COMMENT注释完整性验证（prod_product_attribute_value）
-- ============================================================
SELECT 'TABLE_COMMENT_CHECK' AS check_name,
       CASE WHEN obj_description('prod_product_attribute_value'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('prod_product_attribute_value'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('prod_product_attribute_value'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'prod_product_attribute_value'
ORDER BY c.ordinal_position;

-- ============================================================
-- 18. NOT NULL约束验证（关键字段，prod_product_attribute）
-- ============================================================
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'code', 'name', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'code', 'name', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_attribute'
  AND column_name IN ('id', 'tenant_id', 'code', 'name', 'created_at', 'updated_at', 'is_deleted', 'version');

-- ============================================================
-- 19. NOT NULL约束验证（关键字段，prod_product_attribute_value）
-- ============================================================
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'attribute_id', 'value', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'attribute_id', 'value', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_attribute_value'
  AND column_name IN ('id', 'tenant_id', 'attribute_id', 'value', 'created_at', 'updated_at', 'is_deleted', 'version');

-- ============================================================
-- 20. Flyway迁移记录验证
-- ============================================================
SELECT version, description, script, installed_on, success
FROM flyway_schema_history
WHERE script LIKE '%prod_product_attribute%'
ORDER BY installed_rank DESC;

-- ============================================================
-- 21. 外键约束检查（不应存在，应用层维护关联）
-- ============================================================
SELECT 'FK_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname IN ('prod_product_attribute', 'prod_product_attribute_value') AND c.contype = 'f';

-- ============================================================
-- 22. SMALLINT字段类型验证（status字段）
-- ============================================================
SELECT column_name,
       data_type,
       table_name,
       CASE WHEN data_type = 'smallint' THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name IN ('prod_product_attribute', 'prod_product_attribute_value')
  AND column_name = 'status';

-- ============================================================
-- 23. 列名一致性验证（索引引用列必须存在于DDL中）
-- ============================================================
SELECT 'COLUMN_CODE_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'code column must exist for index uk_prod_product_attribute_code' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_attribute' AND column_name = 'code';

SELECT 'COLUMN_PARENT_ID_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'parent_id column must exist for index idx_prod_product_attribute_parent_id' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_attribute' AND column_name = 'parent_id';

SELECT 'COLUMN_ATTRIBUTE_ID_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'attribute_id column must exist in prod_product_attribute_value' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_attribute_value' AND column_name = 'attribute_id';
