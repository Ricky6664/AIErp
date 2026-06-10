-- ============================================================
-- Flyway Migration Script
-- Version: V20260602024
-- Description: srm_supplier_finance供应商财务配置表DDL验证查询脚本
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-009-001-003
-- ============================================================

-- ============================================================
-- 1. 表存在性验证
-- ============================================================
SELECT 'TABLE_EXISTS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'srm_supplier_finance table should exist in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'srm_supplier_finance';

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
WHERE table_name = 'srm_supplier_finance'
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
    SELECT column_name FROM information_schema.columns WHERE table_name = 'srm_supplier_finance'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- ============================================================
-- 4. 字段数量验证（期望 54 列）
-- ============================================================
SELECT 'FIELD_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 54 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 54 columns' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'srm_supplier_finance';

-- ============================================================
-- 5. 数值精度验证（金额/数量字段应为decimal(18,8)）
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
WHERE table_name = 'srm_supplier_finance'
  AND column_name IN ('credit_limit', 'qty', 'conversion_rate', 'base_qty',
                       'ext_num1', 'ext_num2', 'ext_num3', 'ext_num4', 'ext_num5');

-- ============================================================
-- 6. 主键约束验证
-- ============================================================
SELECT 'PK_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'srm_supplier_finance should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'srm_supplier_finance' AND c.contype = 'p';

-- ============================================================
-- 7. 主键命名验证（应重命名为pk_srm_supplier_finance）
-- ============================================================
SELECT 'PK_NAME_CHECK' AS check_name,
       CASE WHEN conname = 'pk_srm_supplier_finance' THEN 'PASS' ELSE 'FAIL' END AS result,
       'Primary key should be named pk_srm_supplier_finance' AS description,
       conname AS actual_name
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'srm_supplier_finance' AND c.contype = 'p';

-- ============================================================
-- 8. 部分唯一索引验证（WHERE is_deleted = false）
-- ============================================================
SELECT indexname,
       indexdef,
       CASE WHEN indexdef ILIKE '%WHERE%is_deleted%false%' THEN 'PASS' ELSE 'FAIL' END AS partial_unique_check
FROM pg_indexes
WHERE tablename = 'srm_supplier_finance'
  AND indexname LIKE 'uk_%';

-- ============================================================
-- 9. 全部索引列表验证
-- ============================================================
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'srm_supplier_finance'
ORDER BY indexname;

-- ============================================================
-- 10. 索引数量验证（期望 13：1 PK + 1 唯一 + 11 普通）
-- ============================================================
SELECT 'INDEX_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 13 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 13 indexes (1 PK + 1 unique + 11 standard)' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'srm_supplier_finance';

-- ============================================================
-- 11. 多租户联合索引验证（tenant_id为首列）
-- ============================================================
SELECT 'TENANT_INDEX_CHECK' AS check_name,
       CASE WHEN count(*) >= 3 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected at least 3 tenant_id-leading indexes' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'srm_supplier_finance'
  AND indexdef ~ 'USING btree \(tenant_id';

-- ============================================================
-- 12. COMMENT注释完整性验证
-- ============================================================
SELECT 'TABLE_COMMENT_CHECK' AS check_name,
       CASE WHEN obj_description('srm_supplier_finance'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('srm_supplier_finance'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('srm_supplier_finance'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'srm_supplier_finance'
ORDER BY c.ordinal_position;

-- ============================================================
-- 13. NOT NULL约束验证（关键字段）
-- ============================================================
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'supplier_id', 'code', 'status', 'is_multi_unit', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'supplier_id', 'code', 'status', 'is_multi_unit', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'srm_supplier_finance'
  AND column_name IN ('id', 'tenant_id', 'supplier_id', 'code', 'status', 'is_multi_unit', 'created_at', 'updated_at', 'is_deleted', 'version');

-- ============================================================
-- 14. Flyway迁移记录验证
-- ============================================================
SELECT version, description, script, installed_on, success
FROM flyway_schema_history
WHERE script LIKE '%srm_supplier_finance%'
ORDER BY installed_rank DESC;

-- ============================================================
-- 15. 外键约束检查（不应存在，应用层维护关联）
-- ============================================================
SELECT 'FK_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'srm_supplier_finance' AND c.contype = 'f';

-- ============================================================
-- 16. 索引列名一致性验证（索引引用的列必须存在于表中）
-- ============================================================
WITH index_cols AS (
    SELECT DISTINCT unnest(string_to_array(
        regexp_replace(
            regexp_replace(indexdef, '.* USING btree \((.*)\).*', '\1'),
            '\s+', '', 'g'
        ), ','
    )) AS col_name
    FROM pg_indexes
    WHERE tablename = 'srm_supplier_finance'
      AND indexdef ~ 'USING btree'
),
table_cols AS (
    SELECT column_name FROM information_schema.columns WHERE table_name = 'srm_supplier_finance'
)
SELECT ic.col_name AS missing_column_in_index,
       'FAIL' AS result
FROM index_cols ic
LEFT JOIN table_cols tc ON lower(ic.col_name) = lower(tc.column_name)
WHERE tc.column_name IS NULL;

-- ============================================================
-- 17. 排序规则验证（BIGSERIAL主键、BOOLEAN默认值）
-- ============================================================
SELECT column_name,
       data_type,
       column_default,
       CASE WHEN column_name = 'id' AND data_type = 'bigint' AND column_default LIKE 'nextval%' THEN 'PASS'
            WHEN column_name = 'is_deleted' AND data_type = 'boolean' AND column_default = 'false' THEN 'PASS'
            WHEN column_name = 'version' AND data_type = 'integer' AND column_default = '1' THEN 'PASS'
            WHEN column_name = 'status' AND data_type = 'smallint' AND column_default = '0' THEN 'PASS'
            WHEN column_name = 'is_multi_unit' AND data_type = 'boolean' AND column_default = 'false' THEN 'PASS'
            WHEN column_name = 'created_at' AND column_default ILIKE '%now()%' THEN 'PASS'
            WHEN column_name = 'updated_at' AND column_default ILIKE '%now()%' THEN 'PASS'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'srm_supplier_finance'
  AND column_name IN ('id', 'is_deleted', 'version', 'status', 'is_multi_unit', 'created_at', 'updated_at');
