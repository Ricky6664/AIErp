-- ============================================================
-- Flyway Migration Script
-- Version: V20260601026
-- Description: prod_product_attachment商品附件表DDL验证查询脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- 1. 表存在性验证
-- ============================================================
SELECT 'TABLE_EXISTS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'prod_product_attachment table should exist in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'prod_product_attachment';

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
WHERE table_name = 'prod_product_attachment'
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
    SELECT column_name FROM information_schema.columns WHERE table_name = 'prod_product_attachment'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- ============================================================
-- 4. 字段数量验证（期望 19：9业务 + 10通用）
-- ============================================================
SELECT 'FIELD_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 19 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 19 columns (9 business + 10 common)' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'prod_product_attachment';

-- ============================================================
-- 5. 主键约束验证
-- ============================================================
SELECT 'PK_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'prod_product_attachment should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'prod_product_attachment' AND c.contype = 'p';

-- ============================================================
-- 6. 部分唯一索引验证（WHERE is_deleted = false）
-- ============================================================
SELECT indexname,
       indexdef,
       CASE WHEN indexdef ILIKE '%WHERE%is_deleted%false%' THEN 'PASS' ELSE 'FAIL' END AS partial_unique_check
FROM pg_indexes
WHERE tablename = 'prod_product_attachment'
  AND indexname LIKE 'uk_%';

-- ============================================================
-- 7. 全部索引列表验证
-- ============================================================
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'prod_product_attachment'
ORDER BY indexname;

-- ============================================================
-- 8. 多租户联合索引验证（tenant_id为首列）
-- ============================================================
SELECT 'TENANT_INDEX_CHECK' AS check_name,
       CASE WHEN count(*) >= 5 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected at least 5 tenant_id-leading indexes' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'prod_product_attachment'
  AND indexdef ~ 'USING btree \(tenant_id';

-- ============================================================
-- 9. COMMENT注释完整性验证
-- ============================================================
SELECT 'TABLE_COMMENT_CHECK' AS check_name,
       CASE WHEN obj_description('prod_product_attachment'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('prod_product_attachment'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('prod_product_attachment'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'prod_product_attachment'
ORDER BY c.ordinal_position;

-- ============================================================
-- 10. NOT NULL约束验证（关键字段）
-- ============================================================
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'product_id', 'code', 'attachment_name', 'created_at', 'updated_at', 'is_deleted', 'version', 'sort_order', 'status')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'product_id', 'code', 'attachment_name', 'created_at', 'updated_at', 'is_deleted', 'version', 'sort_order', 'status')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_attachment'
  AND column_name IN ('id', 'tenant_id', 'product_id', 'code', 'attachment_name', 'created_at', 'updated_at', 'is_deleted', 'version', 'sort_order', 'status');

-- ============================================================
-- 11. Flyway迁移记录验证
-- ============================================================
SELECT version, description, script, installed_on, success
FROM flyway_schema_history
WHERE script LIKE '%prod_product_attachment%'
ORDER BY installed_rank DESC;

-- ============================================================
-- 12. 外键约束检查（不应存在，应用层维护关联）
-- ============================================================
SELECT 'FK_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'prod_product_attachment' AND c.contype = 'f';

-- ============================================================
-- 13. 索引数量验证（期望 8：1 PK + 1 UNIQUE + 6 普通索引）
-- ============================================================
SELECT 'INDEX_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 8 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 8 indexes (1 PK + 1 unique + 6 standard)' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'prod_product_attachment';

-- ============================================================
-- 14. 列名一致性验证（索引引用列必须存在于DDL中）
-- ============================================================
SELECT 'COLUMN_CODE_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       '"code" column must exist (unique index uk_prod_product_attachment_code references it)' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_attachment' AND column_name = 'code';

SELECT 'COLUMN_ATTACHMENT_TYPE_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       '"attachment_type" column must exist (index idx_prod_product_attachment_tenant_attach_type references it)' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_attachment' AND column_name = 'attachment_type';

-- ============================================================
-- 15. SMALLINT字段类型验证（status字段）
-- ============================================================
SELECT column_name,
       data_type,
       CASE WHEN data_type = 'smallint' THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_attachment'
  AND column_name = 'status';

-- ============================================================
-- 16. VARCHAR字段长度验证
-- ============================================================
SELECT column_name,
       data_type,
       character_maximum_length,
       CASE WHEN column_name = 'code' AND character_maximum_length = 50 THEN 'PASS'
            WHEN column_name = 'attachment_name' AND character_maximum_length = 200 THEN 'PASS'
            WHEN column_name = 'attachment_type' AND character_maximum_length = 50 THEN 'PASS'
            WHEN column_name = 'attachment_url' AND character_maximum_length = 500 THEN 'PASS'
            WHEN column_name = 'remark' AND character_maximum_length = 500 THEN 'PASS'
            ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_attachment'
  AND column_name IN ('code', 'attachment_name', 'attachment_type', 'attachment_url', 'remark');

-- ============================================================
-- 17. 默认值验证（关键字段）
-- ============================================================
SELECT column_name,
       column_default,
       CASE WHEN column_name = 'attachment_type' AND column_default LIKE '%other%' THEN 'PASS'
            WHEN column_name = 'sort_order' AND column_default LIKE '%0%' THEN 'PASS'
            WHEN column_name = 'status' AND column_default LIKE '%0%' THEN 'PASS'
            WHEN column_name = 'is_deleted' AND column_default ILIKE '%false%' THEN 'PASS'
            WHEN column_name = 'version' AND column_default LIKE '%1%' THEN 'PASS'
            WHEN column_name = 'created_at' AND column_default ILIKE '%now%' THEN 'PASS'
            WHEN column_name = 'updated_at' AND column_default ILIKE '%now%' THEN 'PASS'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_attachment'
  AND column_name IN ('attachment_type', 'sort_order', 'status', 'is_deleted', 'version', 'created_at', 'updated_at');
