-- ============================================================
-- Flyway Migration Script
-- Version: V20260601020
-- Description: prod_product_control商品控制策略表DDL验证查询脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 表存在性验证
-- ============================================================
SELECT 'TABLE_EXISTS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'prod_product_control table should exist in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'prod_product_control';

-- ============================================================
-- 2. 字段定义验证（列出所有字段及其类型/可空性）
-- ============================================================
SELECT column_name,
       data_type,
       character_maximum_length,
       numeric_precision,
       numeric_scale,
       is_nullable,
       column_default
FROM information_schema.columns
WHERE table_name = 'prod_product_control'
ORDER BY ordinal_position;

-- ============================================================
-- 3. 10个通用字段存在性验证
-- ============================================================
WITH required_fields AS (
    SELECT unnest(ARRAY[
        'id', 'tenant_id', 'created_by', 'created_at',
        'updated_by', 'updated_at', 'is_deleted',
        'owner_dept_id', 'owner_id', 'version'
    ]) AS field_name
),
actual_fields AS (
    SELECT column_name FROM information_schema.columns WHERE table_name = 'prod_product_control'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- ============================================================
-- 4. 字段数量验证（期望 14：4业务 + 10通用）
-- ============================================================
SELECT 'FIELD_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 14 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 14 columns (4 business + 10 common)' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'prod_product_control';

-- ============================================================
-- 5. BOOLEAN字段类型验证（is_inventory/is_batch_manage/is_serial_manage）
-- ============================================================
SELECT column_name,
       data_type,
       CASE WHEN data_type = 'boolean' THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_control'
  AND column_name IN ('is_inventory', 'is_batch_manage', 'is_serial_manage', 'is_deleted');

-- ============================================================
-- 6. 主键约束验证
-- ============================================================
SELECT 'PK_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'prod_product_control should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'prod_product_control' AND c.contype = 'p';

-- ============================================================
-- 7. 部分唯一索引验证（WHERE is_deleted = false）
-- ============================================================
SELECT indexname,
       indexdef,
       CASE WHEN indexdef ILIKE '%WHERE%is_deleted%false%' THEN 'PASS' ELSE 'FAIL' END AS partial_unique_check
FROM pg_indexes
WHERE tablename = 'prod_product_control'
  AND indexname LIKE 'uk_%';

-- ============================================================
-- 8. 全部索引列表验证
-- ============================================================
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'prod_product_control'
ORDER BY indexname;

-- ============================================================
-- 9. 多租户联合索引验证（tenant_id为首列）
-- ============================================================
SELECT 'TENANT_INDEX_CHECK' AS check_name,
       CASE WHEN count(*) >= 2 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected at least 2 tenant_id-leading indexes (excluding UNIQUE indexes)' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'prod_product_control'
  AND indexdef ~ 'USING btree \(tenant_id';

-- ============================================================
-- 10. COMMENT注释完整性验证
-- ============================================================
SELECT 'TABLE_COMMENT_CHECK' AS check_name,
       CASE WHEN obj_description('prod_product_control'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('prod_product_control'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('prod_product_control'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'prod_product_control'
ORDER BY c.ordinal_position;

-- ============================================================
-- 11. NOT NULL约束验证（关键字段）
-- ============================================================
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'product_id', 'is_inventory', 'is_batch_manage', 'is_serial_manage', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'product_id', 'is_inventory', 'is_batch_manage', 'is_serial_manage', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_control'
  AND column_name IN ('id', 'tenant_id', 'product_id', 'is_inventory', 'is_batch_manage', 'is_serial_manage', 'created_at', 'updated_at', 'is_deleted', 'version');

-- ============================================================
-- 12. Flyway迁移记录验证
-- ============================================================
SELECT version, description, script, installed_on, success
FROM flyway_schema_history
WHERE script LIKE '%prod_product_control%'
ORDER BY installed_rank DESC;

-- ============================================================
-- 13. 外键约束检查（不应存在，应用层维护关联）
-- ============================================================
SELECT 'FK_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'prod_product_control' AND c.contype = 'f';

-- ============================================================
-- 14. 索引数量验证（期望 5：1 PK + 1 唯一 + 3 普通索引）
-- ============================================================
SELECT 'INDEX_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 5 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 5 indexes (1 PK + 1 unique + 3 standard)' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'prod_product_control';

-- ============================================================
-- 15. 列名一致性验证（索引引用列必须存在于DDL中）
-- ============================================================
SELECT 'COLUMN_PRODUCT_ID_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'product_id column must exist (referenced by indexes)' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_control' AND column_name = 'product_id';

SELECT 'COLUMN_IS_INVENTORY_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'is_inventory column must exist (referenced by indexes)' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_control' AND column_name = 'is_inventory';
