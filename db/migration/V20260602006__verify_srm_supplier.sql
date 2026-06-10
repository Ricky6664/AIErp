-- ============================================================
-- Flyway Migration Script
-- Version: V20260602006
-- Description: srm_supplier供应商主表DDL验证查询脚本
-- Author: AI Generated
-- Date: 2026-06-02
-- ============================================================

-- ============================================================
-- 1. 表存在性验证
-- ============================================================
SELECT 'TABLE_EXISTS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'srm_supplier table should exist in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'srm_supplier';

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
WHERE table_name = 'srm_supplier'
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
    SELECT column_name FROM information_schema.columns WHERE table_name = 'srm_supplier'
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
       'Expected 37 columns (5 business + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json + 10 common)' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'srm_supplier';

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
WHERE table_name = 'srm_supplier'
  AND column_name IN ('ext_num1', 'ext_num2', 'ext_num3', 'ext_num4', 'ext_num5');

-- ============================================================
-- 6. 主键约束验证
-- ============================================================
SELECT 'PK_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'srm_supplier should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'srm_supplier' AND c.contype = 'p';

-- ============================================================
-- 7. 主键命名验证（应为pk_srm_supplier）
-- ============================================================
SELECT 'PK_NAME_CHECK' AS check_name,
       CASE WHEN conname = 'pk_srm_supplier' THEN 'PASS' ELSE 'FAIL' END AS result,
       'Primary key should be named pk_srm_supplier' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'srm_supplier' AND c.contype = 'p';

-- ============================================================
-- 8. 部分唯一索引验证（WHERE is_deleted = false）
-- ============================================================
SELECT indexname,
       indexdef,
       CASE WHEN indexdef ILIKE '%WHERE%is_deleted%false%' THEN 'PASS' ELSE 'FAIL' END AS partial_unique_check
FROM pg_indexes
WHERE tablename = 'srm_supplier'
  AND indexname LIKE 'uk_%';

-- ============================================================
-- 9. 全部索引列表验证
-- ============================================================
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'srm_supplier'
ORDER BY indexname;

-- ============================================================
-- 10. 索引数量验证（期望 11：1 PK + 1 部分唯一 + 3 tenant联合 + 6 业务查询）
-- ============================================================
SELECT 'INDEX_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 11 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 11 indexes (1 PK + 1 partial unique + 3 tenant composite + 6 business query)' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'srm_supplier';

-- ============================================================
-- 11. 多租户联合索引验证（tenant_id为首列）
-- ============================================================
SELECT 'TENANT_INDEX_CHECK' AS check_name,
       CASE WHEN count(*) >= 3 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected at least 3 tenant_id-leading indexes' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'srm_supplier'
  AND indexdef ~ 'USING btree \(tenant_id';

-- ============================================================
-- 12. COMMENT注释完整性验证
-- ============================================================
SELECT 'TABLE_COMMENT_CHECK' AS check_name,
       CASE WHEN obj_description('srm_supplier'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('srm_supplier'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('srm_supplier'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'srm_supplier'
ORDER BY c.ordinal_position;

-- ============================================================
-- 13. NOT NULL约束验证（关键字段）
-- ============================================================
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'supplier_code', 'supplier_name', 'status', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'supplier_code', 'supplier_name', 'status', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'srm_supplier'
  AND column_name IN ('id', 'tenant_id', 'supplier_code', 'supplier_name', 'status', 'created_at', 'updated_at', 'is_deleted', 'version');

-- ============================================================
-- 14. 业务字段存在性验证（supplier_code, supplier_name）
-- ============================================================
SELECT 'COLUMN_SUPPLIER_CODE_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'supplier_code column must exist (unique index + tenant index reference it)' AS description
FROM information_schema.columns
WHERE table_name = 'srm_supplier' AND column_name = 'supplier_code';

SELECT 'COLUMN_SUPPLIER_NAME_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'supplier_name column must exist' AS description
FROM information_schema.columns
WHERE table_name = 'srm_supplier' AND column_name = 'supplier_name';

-- 检查 class_id 列（业务查询索引引用）
SELECT 'COLUMN_CLASS_ID_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'class_id column must exist (business query index reference it)' AS description
FROM information_schema.columns
WHERE table_name = 'srm_supplier' AND column_name = 'class_id';

-- ============================================================
-- 15. Flyway迁移记录验证
-- ============================================================
SELECT version, description, script, installed_on, success
FROM flyway_schema_history
WHERE script LIKE '%srm_supplier%'
ORDER BY installed_rank DESC;

-- ============================================================
-- 16. 外键约束检查（不应存在，应用层维护关联）
-- ============================================================
SELECT 'FK_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'srm_supplier' AND c.contype = 'f';

-- ============================================================
-- 17. BOOLEAN字段默认值验证
-- ============================================================
SELECT 'IS_DELETED_DEFAULT_CHECK' AS check_name,
       CASE WHEN column_default ILIKE '%false%' THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'srm_supplier' AND column_name = 'is_deleted';

-- ============================================================
-- 18. SMALLINT字段类型验证（credit_level, status）
-- ============================================================
SELECT column_name,
       data_type,
       CASE WHEN data_type = 'smallint' THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'srm_supplier'
  AND column_name IN ('credit_level', 'status');

-- ============================================================
-- 19. status字段默认值验证
-- ============================================================
SELECT 'STATUS_DEFAULT_CHECK' AS check_name,
       CASE WHEN column_default LIKE '%1%' THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'srm_supplier' AND column_name = 'status';

-- ============================================================
-- 20. VARCHAR字段长度验证
-- ============================================================
SELECT column_name,
       character_maximum_length,
       CASE WHEN (column_name = 'supplier_code' AND character_maximum_length = 50)
              OR (column_name = 'supplier_name' AND character_maximum_length = 200)
              OR (column_name LIKE 'ext_str%' AND character_maximum_length = 200)
            THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'srm_supplier'
  AND column_name IN ('supplier_code', 'supplier_name', 'ext_str1', 'ext_str2', 'ext_str3', 'ext_str4', 'ext_str5', 'ext_str6', 'ext_str7', 'ext_str8', 'ext_str9', 'ext_str10');
