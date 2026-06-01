-- ============================================================
-- Flyway Migration Script
-- Version: V20260601038
-- Description: prod_product_competitor商品竞品表DDL验证查询脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 表存在性验证
-- ============================================================
SELECT 'TABLE_EXISTS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'prod_product_competitor table should exist in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'prod_product_competitor';

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
WHERE table_name = 'prod_product_competitor'
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
    SELECT column_name FROM information_schema.columns WHERE table_name = 'prod_product_competitor'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- ============================================================
-- 4. 字段数量验证（期望 27：17业务 + 10通用）
-- ============================================================
SELECT 'FIELD_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 27 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 27 columns (17 business + 10 common)' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'prod_product_competitor';

-- ============================================================
-- 5. 主键约束验证
-- ============================================================
SELECT 'PK_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'prod_product_competitor should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'prod_product_competitor' AND c.contype = 'p';

-- ============================================================
-- 6. 部分唯一索引验证（WHERE is_deleted = false）
-- ============================================================
SELECT indexname,
       indexdef,
       CASE WHEN indexdef ILIKE '%WHERE%is_deleted%false%' THEN 'PASS' ELSE 'FAIL' END AS partial_unique_check
FROM pg_indexes
WHERE tablename = 'prod_product_competitor'
  AND indexname LIKE 'uk_%';

-- ============================================================
-- 7. 全部索引列表验证
-- ============================================================
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'prod_product_competitor'
ORDER BY indexname;

-- ============================================================
-- 8. 多租户联合索引验证（tenant_id为首列）
-- ============================================================
SELECT 'TENANT_INDEX_CHECK' AS check_name,
       CASE WHEN count(*) >= 2 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected at least 2 tenant_id-leading indexes' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'prod_product_competitor'
  AND indexdef ~ 'USING btree \(tenant_id';

-- ============================================================
-- 9. COMMENT注释完整性验证
-- ============================================================
SELECT 'TABLE_COMMENT_CHECK' AS check_name,
       CASE WHEN obj_description('prod_product_competitor'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('prod_product_competitor'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('prod_product_competitor'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'prod_product_competitor'
ORDER BY c.ordinal_position;

-- ============================================================
-- 10. NOT NULL约束验证（关键字段）
-- ============================================================
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'product_id', 'competitor_name', 'currency_code', 'status', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'product_id', 'competitor_name', 'currency_code', 'status', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_competitor'
  AND column_name IN ('id', 'tenant_id', 'product_id', 'competitor_name', 'currency_code', 'status', 'created_at', 'updated_at', 'is_deleted', 'version');

-- ============================================================
-- 11. Flyway迁移记录验证
-- ============================================================
SELECT version, description, script, installed_on, success
FROM flyway_schema_history
WHERE script LIKE '%prod_product_competitor%'
ORDER BY installed_rank DESC;

-- ============================================================
-- 12. 外键约束检查（不应存在，应用层维护关联）
-- ============================================================
SELECT 'FK_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'prod_product_competitor' AND c.contype = 'f';

-- ============================================================
-- 13. 索引数量验证（期望 10：1 PK + 1 UNIQUE + 8 普通索引）
-- ============================================================
SELECT 'INDEX_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 10 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 10 indexes (1 PK + 1 unique + 8 standard)' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'prod_product_competitor';

-- ============================================================
-- 14. 列名一致性验证（索引引用列必须存在于DDL中）
-- ============================================================
SELECT 'COLUMN_PRODUCT_ID_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       '"product_id" column must exist (unique index uk_prod_product_competitor_tenant_product_name references it)' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_competitor' AND column_name = 'product_id';

SELECT 'COLUMN_COMPETITOR_NAME_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       '"competitor_name" column must exist (unique index and standard index reference it)' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_competitor' AND column_name = 'competitor_name';

SELECT 'COLUMN_STATUS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       '"status" column must exist (indexes reference it)' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_competitor' AND column_name = 'status';

SELECT 'COLUMN_UNIT_ID_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       '"unit_id" column must exist (index references it)' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_competitor' AND column_name = 'unit_id';

SELECT 'COLUMN_EFFECTIVE_DATE_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       '"effective_date" column must exist (index references it)' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_competitor' AND column_name = 'effective_date';

SELECT 'COLUMN_EXPIRY_DATE_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       '"expiry_date" column must exist (index references it)' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_competitor' AND column_name = 'expiry_date';

-- ============================================================
-- 15. SMALLINT字段类型验证（status字段）
-- ============================================================
SELECT column_name,
       data_type,
       CASE WHEN data_type = 'smallint' THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_competitor'
  AND column_name = 'status';

-- ============================================================
-- 16. DECIMAL(18,8)字段精度验证（金额/单价/差异率字段）
-- ============================================================
SELECT column_name,
       data_type,
       numeric_precision,
       numeric_scale,
       CASE WHEN data_type = 'numeric'
             AND numeric_precision = 18
             AND numeric_scale = 8 THEN 'PASS'
            ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_competitor'
  AND column_name IN ('competitor_price', 'our_price', 'price_diff_rate');

-- ============================================================
-- 17. VARCHAR字段长度验证
-- ============================================================
SELECT column_name,
       data_type,
       character_maximum_length,
       CASE WHEN column_name = 'competitor_name' AND character_maximum_length = 200 THEN 'PASS'
            WHEN column_name = 'competitor_brand' AND character_maximum_length = 100 THEN 'PASS'
            WHEN column_name = 'competitor_model' AND character_maximum_length = 100 THEN 'PASS'
            WHEN column_name = 'competitor_spec' AND character_maximum_length = 200 THEN 'PASS'
            WHEN column_name = 'competitor_product_code' AND character_maximum_length = 50 THEN 'PASS'
            WHEN column_name = 'currency_code' AND character_maximum_length = 10 THEN 'PASS'
            WHEN column_name = 'competitor_url' AND character_maximum_length = 500 THEN 'PASS'
            WHEN column_name = 'source' AND character_maximum_length = 100 THEN 'PASS'
            WHEN column_name = 'remark' AND character_maximum_length = 500 THEN 'PASS'
            ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_competitor'
  AND column_name IN ('competitor_name', 'competitor_brand', 'competitor_model', 'competitor_spec', 'competitor_product_code', 'currency_code', 'competitor_url', 'source', 'remark');

-- ============================================================
-- 18. 默认值验证（关键字段）
-- ============================================================
SELECT column_name,
       column_default,
       CASE WHEN column_name = 'currency_code' AND column_default LIKE '%CNY%' THEN 'PASS'
            WHEN column_name = 'status' AND column_default LIKE '%0%' THEN 'PASS'
            WHEN column_name = 'is_deleted' AND column_default ILIKE '%false%' THEN 'PASS'
            WHEN column_name = 'version' AND column_default LIKE '%1%' THEN 'PASS'
            WHEN column_name = 'created_at' AND column_default ILIKE '%now%' THEN 'PASS'
            WHEN column_name = 'updated_at' AND column_default ILIKE '%now%' THEN 'PASS'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_competitor'
  AND column_name IN ('currency_code', 'status', 'is_deleted', 'version', 'created_at', 'updated_at');

-- ============================================================
-- 19. 业务字段存在性验证（17个业务字段）
-- ============================================================
WITH required_biz_fields AS (
    SELECT unnest(ARRAY[
        'product_id', 'competitor_name', 'competitor_brand', 'competitor_model',
        'competitor_spec', 'competitor_product_code', 'competitor_price', 'our_price',
        'price_diff_rate', 'currency_code', 'unit_id', 'competitor_url',
        'source', 'effective_date', 'expiry_date', 'status', 'remark'
    ]) AS field_name
),
actual_fields AS (
    SELECT column_name FROM information_schema.columns WHERE table_name = 'prod_product_competitor'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_biz_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- ============================================================
-- 20. 索引命名规范验证
-- ============================================================
SELECT indexname,
       CASE WHEN indexname ~ '^(pk_|uk_|idx_)prod_product_competitor' THEN 'PASS'
            ELSE 'FAIL' END AS naming_check
FROM pg_indexes
WHERE tablename = 'prod_product_competitor'
  AND indexname NOT LIKE '%pkey'
ORDER BY indexname;
