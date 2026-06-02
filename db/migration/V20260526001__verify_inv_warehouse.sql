-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: inv_warehouse仓库定义表DDL验证查询脚本
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-007-001-001-003
-- ============================================================

-- ============================================================
-- 1. 表存在性验证
-- ============================================================
SELECT 'TABLE_EXISTS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'inv_warehouse table should exist in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'inv_warehouse';

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
WHERE table_name = 'inv_warehouse'
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
    SELECT column_name FROM information_schema.columns WHERE table_name = 'inv_warehouse'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- ============================================================
-- 4. 字段数量验证（期望 37：5业务 + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json + 10 通用）
-- ============================================================
SELECT 'FIELD_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 37 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 37 columns (5 business + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json + 10 generic)' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'inv_warehouse';

-- ============================================================
-- 5. 数值精度验证（ext_num字段应为decimal(18,8)）
-- ============================================================
SELECT column_name,
       data_type,
       numeric_precision,
       numeric_scale,
       CASE WHEN data_type = 'numeric'
             AND numeric_precision = 18
             AND numeric_scale = 8
            THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'inv_warehouse'
  AND column_name IN ('ext_num1', 'ext_num2', 'ext_num3', 'ext_num4', 'ext_num5');

-- ============================================================
-- 6. 主键约束验证
-- ============================================================
SELECT 'PK_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'inv_warehouse should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'inv_warehouse' AND c.contype = 'p';

-- ============================================================
-- 7. 部分唯一索引验证（WHERE is_deleted = false）
-- ============================================================
SELECT indexname,
       indexdef,
       CASE WHEN indexdef ILIKE '%WHERE%is_deleted%false%' THEN 'PASS' ELSE 'FAIL' END AS partial_unique_check
FROM pg_indexes
WHERE tablename = 'inv_warehouse'
  AND indexname LIKE 'uk_%';

-- ============================================================
-- 8. 全部索引列表验证
-- ============================================================
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'inv_warehouse'
ORDER BY indexname;

-- ============================================================
-- 9. 索引数量验证（期望 13：1 PK + 1 唯一 + 11 普通索引）
-- ============================================================
SELECT 'INDEX_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 13 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 13 indexes (1 PK + 1 unique + 11 standard)' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'inv_warehouse';

-- ============================================================
-- 10. 多租户联合索引验证（tenant_id为首列）
-- ============================================================
SELECT 'TENANT_INDEX_CHECK' AS check_name,
       CASE WHEN count(*) >= 2 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected at least 2 tenant_id-leading indexes' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'inv_warehouse'
  AND indexdef ~ 'USING btree \(tenant_id';

-- ============================================================
-- 11. COMMENT注释完整性验证
-- ============================================================
SELECT 'TABLE_COMMENT_CHECK' AS check_name,
       CASE WHEN obj_description('inv_warehouse'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('inv_warehouse'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('inv_warehouse'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'inv_warehouse'
ORDER BY c.ordinal_position;

-- ============================================================
-- 12. NOT NULL约束验证（关键字段）
-- ============================================================
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'warehouse_code', 'warehouse_name', 'status', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'warehouse_code', 'warehouse_name', 'status', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'inv_warehouse'
  AND column_name IN ('id', 'tenant_id', 'warehouse_code', 'warehouse_name', 'status', 'created_at', 'updated_at', 'is_deleted', 'version');

-- ============================================================
-- 13. 外键约束检查（不应存在，应用层维护关联）
-- ============================================================
SELECT 'FK_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'inv_warehouse' AND c.contype = 'f';

-- ============================================================
-- 14. 列名一致性验证（索引引用列必须存在于DDL中）
-- ============================================================
SELECT 'COLUMN_WAREHOUSE_CODE_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'warehouse_code column must exist (unique index references warehouse_code)' AS description
FROM information_schema.columns
WHERE table_name = 'inv_warehouse' AND column_name = 'warehouse_code';

SELECT 'COLUMN_TENANT_ID_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'tenant_id column must exist (tenant indexes reference tenant_id)' AS description
FROM information_schema.columns
WHERE table_name = 'inv_warehouse' AND column_name = 'tenant_id';

-- ============================================================
-- 15. Flyway迁移记录验证（如果Flyway已初始化）
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'flyway_schema_history') THEN
        EXECUTE 'SELECT version, description, script, installed_on, success FROM flyway_schema_history WHERE script LIKE ''%inv_warehouse%'' ORDER BY installed_rank DESC';
    ELSE
        RAISE NOTICE 'Flyway schema history not initialized — manual DDL execution assumed';
    END IF;
END $$;
