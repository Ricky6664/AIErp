-- ============================================================
-- Flyway Migration Script
-- Version: V20260602003
-- Description: srm_tag_definition SRM标签定义表DDL验证查询脚本
-- Author: AI Generated
-- Date: 2026-06-02
-- ============================================================

-- ============================================================
-- 1. 表存在性验证
-- ============================================================
SELECT 'TABLE_EXISTS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'srm_tag_definition table should exist in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'srm_tag_definition';

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
WHERE table_name = 'srm_tag_definition'
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
    SELECT column_name FROM information_schema.columns WHERE table_name = 'srm_tag_definition'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- ============================================================
-- 4. 字段数量验证（期望 37：6业务 + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json + 10 通用）
-- ============================================================
SELECT 'FIELD_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 37 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 37 columns (6 business + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json + 10 common)' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'srm_tag_definition';

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
WHERE table_name = 'srm_tag_definition'
  AND column_name IN ('ext_num1', 'ext_num2', 'ext_num3', 'ext_num4', 'ext_num5');

-- ============================================================
-- 6. 主键约束验证
-- ============================================================
SELECT 'PK_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'srm_tag_definition should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'srm_tag_definition' AND c.contype = 'p';

-- ============================================================
-- 7. 主键命名验证（应为pk_srm_tag_definition）
-- ============================================================
SELECT 'PK_NAME_CHECK' AS check_name,
       CASE WHEN conname = 'pk_srm_tag_definition' THEN 'PASS' ELSE 'FAIL' END AS result,
       'Primary key should be named pk_srm_tag_definition' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'srm_tag_definition' AND c.contype = 'p';

-- ============================================================
-- 8. 部分唯一索引验证（WHERE is_deleted = false）
-- ============================================================
SELECT indexname,
       indexdef,
       CASE WHEN indexdef ILIKE '%WHERE%is_deleted%false%' THEN 'PASS' ELSE 'FAIL' END AS partial_unique_check
FROM pg_indexes
WHERE tablename = 'srm_tag_definition'
  AND indexname LIKE 'uk_%';

-- ============================================================
-- 9. 全部索引列表验证
-- ============================================================
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'srm_tag_definition'
ORDER BY indexname;

-- ============================================================
-- 10. 索引数量验证（期望 12：1 PK + 1 部分唯一 + 10 普通索引）
-- ============================================================
SELECT 'INDEX_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 12 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 12 indexes (1 PK + 1 partial unique + 10 standard)' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'srm_tag_definition';

-- ============================================================
-- 11. 多租户联合索引验证（tenant_id为首列）
-- ============================================================
SELECT 'TENANT_INDEX_CHECK' AS check_name,
       CASE WHEN count(*) >= 2 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected at least 2 tenant_id-leading indexes' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'srm_tag_definition'
  AND indexdef ~ 'USING btree \(tenant_id';

-- ============================================================
-- 12. COMMENT注释完整性验证
-- ============================================================
SELECT 'TABLE_COMMENT_CHECK' AS check_name,
       CASE WHEN obj_description('srm_tag_definition'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('srm_tag_definition'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('srm_tag_definition'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'srm_tag_definition'
ORDER BY c.ordinal_position;

-- ============================================================
-- 13. NOT NULL约束验证（关键字段）
-- ============================================================
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'tag_name', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'tag_name', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'srm_tag_definition'
  AND column_name IN ('id', 'tenant_id', 'tag_name', 'created_at', 'updated_at', 'is_deleted', 'version');

-- ============================================================
-- 14. BOOLEAN字段默认值验证
-- ============================================================
SELECT 'IS_DELETED_DEFAULT_CHECK' AS check_name,
       CASE WHEN column_default ILIKE '%false%' THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'srm_tag_definition' AND column_name = 'is_deleted';

SELECT 'ENABLE_FLAG_DEFAULT_CHECK' AS check_name,
       CASE WHEN column_default ILIKE '%true%' THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'srm_tag_definition' AND column_name = 'enable_flag';

-- ============================================================
-- 15. Flyway迁移记录验证
-- ============================================================
SELECT version, description, script, installed_on, success
FROM flyway_schema_history
WHERE script LIKE '%srm_tag_definition%'
ORDER BY installed_rank DESC;

-- ============================================================
-- 16. 外键约束检查（不应存在，应用层维护关联）
-- ============================================================
SELECT 'FK_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'srm_tag_definition' AND c.contype = 'f';

-- ============================================================
-- 17. 列名一致性验证（索引引用的列必须存在于DDL中）
-- ============================================================
SELECT 'COLUMN_TAG_NAME_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'tag_name column must exist (partial unique index + tenant index reference it)' AS description
FROM information_schema.columns
WHERE table_name = 'srm_tag_definition' AND column_name = 'tag_name';

SELECT 'COLUMN_TAG_GROUP_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'srm_tag_definition' AND column_name = 'tag_group';

SELECT 'COLUMN_TAG_COLOR_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'srm_tag_definition' AND column_name = 'tag_color';
