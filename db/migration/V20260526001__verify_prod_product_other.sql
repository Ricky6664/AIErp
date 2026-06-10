-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: prod_product_other商品其他信息表DDL验证查询脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-022-001-003
-- ============================================================

-- ============================================================
-- 1. 表存在性验证
-- ============================================================
SELECT 'TABLE_EXISTS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'prod_product_other table should exist in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'prod_product_other';

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
WHERE table_name = 'prod_product_other'
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
    SELECT column_name FROM information_schema.columns WHERE table_name = 'prod_product_other'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- ============================================================
-- 4. 字段数量验证（期望 24：14业务 + 10通用）
-- ============================================================
SELECT 'FIELD_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 24 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 24 columns (14 business + 10 common)' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'prod_product_other';

-- ============================================================
-- 5. 数值精度验证（gross_weight/net_weight/volume应为decimal(18,8)）
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
WHERE table_name = 'prod_product_other'
  AND column_name IN ('gross_weight', 'net_weight', 'volume');

-- ============================================================
-- 6. 主键约束验证
-- ============================================================
SELECT 'PK_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'prod_product_other should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'prod_product_other' AND c.contype = 'p';

-- ============================================================
-- 7. 部分唯一索引验证（WHERE is_deleted = false）
-- ============================================================
SELECT indexname,
       indexdef,
       CASE WHEN indexdef ILIKE '%WHERE%is_deleted%false%' THEN 'PASS' ELSE 'FAIL' END AS partial_unique_check
FROM pg_indexes
WHERE tablename = 'prod_product_other'
  AND indexname LIKE 'uk_%';

-- ============================================================
-- 8. 全部索引列表验证
-- ============================================================
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'prod_product_other'
ORDER BY indexname;

-- ============================================================
-- 9. 多租户联合索引验证（tenant_id为首列）
-- ============================================================
SELECT 'TENANT_INDEX_CHECK' AS check_name,
       CASE WHEN count(*) >= 5 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected at least 5 tenant_id-leading indexes' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'prod_product_other'
  AND indexdef ~ 'USING btree \(tenant_id';

-- ============================================================
-- 10. COMMENT注释完整性验证
-- ============================================================
SELECT 'TABLE_COMMENT_CHECK' AS check_name,
       CASE WHEN obj_description('prod_product_other'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('prod_product_other'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('prod_product_other'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'prod_product_other'
ORDER BY c.ordinal_position;

-- ============================================================
-- 11. NOT NULL约束验证（关键字段）
-- ============================================================
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'product_id', 'code', 'created_at', 'updated_at', 'is_deleted', 'version', 'status')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'product_id', 'code', 'created_at', 'updated_at', 'is_deleted', 'version', 'status')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_other'
  AND column_name IN ('id', 'tenant_id', 'product_id', 'code', 'created_at', 'updated_at', 'is_deleted', 'version', 'status');

-- ============================================================
-- 12. Flyway迁移记录验证
-- ============================================================
SELECT version, description, script, installed_on, success
FROM flyway_schema_history
WHERE script LIKE '%prod_product_other%'
ORDER BY installed_rank DESC;

-- ============================================================
-- 13. 外键约束检查（不应存在，应用层维护关联）
-- ============================================================
SELECT 'FK_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'prod_product_other' AND c.contype = 'f';

-- ============================================================
-- 14. 索引数量验证（期望 7：1 PK + 1 唯一 + 5 普通索引）
-- ============================================================
SELECT 'INDEX_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 7 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 7 indexes (1 PK + 1 unique + 5 standard)' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'prod_product_other';

-- ============================================================
-- 15. 列名一致性验证（索引引用列code必须匹配DDL定义）
-- ============================================================
SELECT 'COLUMN_CODE_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       '"code" column must exist (indexes reference "code")' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_other' AND column_name = 'code';

-- ============================================================
-- 16. SMALLINT字段类型验证（status字段）
-- ============================================================
SELECT column_name,
       data_type,
       CASE WHEN data_type = 'smallint' THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_other'
  AND column_name = 'status';
