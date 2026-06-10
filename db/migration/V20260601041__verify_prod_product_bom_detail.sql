-- ============================================================
-- Flyway Migration Script
-- Version: V20260601041
-- Description: prod_product_bom_detail商品BOM明细表DDL验证查询脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-011-001-003
-- ============================================================

-- ============================================================
-- 1. 表存在性验证
-- ============================================================
SELECT 'TABLE_EXISTS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'prod_product_bom_detail table should exist in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'prod_product_bom_detail';

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
WHERE table_name = 'prod_product_bom_detail'
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
    SELECT column_name FROM information_schema.columns WHERE table_name = 'prod_product_bom_detail'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- ============================================================
-- 4. 字段数量验证（期望 30：20业务 + 10通用）
-- ============================================================
SELECT 'FIELD_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 30 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 30 columns (20 business + 10 common)' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'prod_product_bom_detail';

-- ============================================================
-- 5. 主键约束验证
-- ============================================================
SELECT 'PK_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'prod_product_bom_detail should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'prod_product_bom_detail' AND c.contype = 'p';

-- ============================================================
-- 6. 部分唯一索引验证（WHERE is_deleted = false）
-- ============================================================
SELECT indexname,
       indexdef,
       CASE WHEN indexdef ILIKE '%WHERE%is_deleted%false%' THEN 'PASS' ELSE 'FAIL' END AS partial_unique_check
FROM pg_indexes
WHERE tablename = 'prod_product_bom_detail'
  AND indexname LIKE 'uk_%';

-- ============================================================
-- 7. 全部索引列表验证
-- ============================================================
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'prod_product_bom_detail'
ORDER BY indexname;

-- ============================================================
-- 8. 多租户联合索引验证（tenant_id为首列）
-- ============================================================
SELECT 'TENANT_INDEX_CHECK' AS check_name,
       CASE WHEN count(*) >= 3 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected at least 3 tenant_id-leading indexes' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'prod_product_bom_detail'
  AND indexdef ~ 'USING btree \(tenant_id';

-- ============================================================
-- 9. COMMENT注释完整性验证
-- ============================================================
SELECT 'TABLE_COMMENT_CHECK' AS check_name,
       CASE WHEN obj_description('prod_product_bom_detail'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('prod_product_bom_detail'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('prod_product_bom_detail'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'prod_product_bom_detail'
ORDER BY c.ordinal_position;

-- ============================================================
-- 10. NOT NULL约束验证（关键字段）
-- ============================================================
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'bom_id', 'sub_product_id', 'qty', 'order_no', 'order_date', 'status', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'bom_id', 'sub_product_id', 'qty', 'order_no', 'order_date', 'status', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_bom_detail'
  AND column_name IN ('id', 'tenant_id', 'bom_id', 'sub_product_id', 'qty', 'order_no', 'order_date', 'status', 'created_at', 'updated_at', 'is_deleted', 'version');

-- ============================================================
-- 11. Flyway迁移记录验证
-- ============================================================
SELECT version, description, script, installed_on, success
FROM flyway_schema_history
WHERE script LIKE '%prod_product_bom_detail%'
ORDER BY installed_rank DESC;

-- ============================================================
-- 12. 外键约束检查（不应存在，应用层维护关联）
-- ============================================================
SELECT 'FK_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'prod_product_bom_detail' AND c.contype = 'f';

-- ============================================================
-- 13. 索引数量验证（期望 10：1 PK + 1 UNIQUE + 8 普通索引）
-- ============================================================
SELECT 'INDEX_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 10 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 10 indexes (1 PK + 1 unique + 8 standard)' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'prod_product_bom_detail';

-- ============================================================
-- 14. 列名一致性验证（索引引用列必须存在于DDL中）
-- ============================================================
SELECT 'COLUMN_BOM_ID_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       '"bom_id" column must exist (unique index uk_prod_product_bom_detail_tenant_bom_sub_product references it)' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_bom_detail' AND column_name = 'bom_id';

SELECT 'COLUMN_SUB_PRODUCT_ID_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       '"sub_product_id" column must exist (unique index and standard index reference it)' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_bom_detail' AND column_name = 'sub_product_id';

SELECT 'COLUMN_STATUS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       '"status" column must exist (indexes reference it)' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_bom_detail' AND column_name = 'status';

SELECT 'COLUMN_ORDER_NO_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       '"order_no" column must exist (index references it)' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_bom_detail' AND column_name = 'order_no';

SELECT 'COLUMN_ORDER_DATE_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       '"order_date" column must exist (index references it)' AS description
FROM information_schema.columns
WHERE table_name = 'prod_product_bom_detail' AND column_name = 'order_date';

-- ============================================================
-- 15. SMALLINT字段类型验证（status字段）
-- ============================================================
SELECT column_name,
       data_type,
       CASE WHEN data_type = 'smallint' THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_bom_detail'
  AND column_name = 'status';

-- ============================================================
-- 16. DECIMAL(18,8)字段精度验证（用量/损耗率/换算率/基本数量字段）
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
WHERE table_name = 'prod_product_bom_detail'
  AND column_name IN ('qty', 'loss_rate', 'conversion_rate', 'base_qty');

-- ============================================================
-- 17. VARCHAR字段长度验证
-- ============================================================
SELECT column_name,
       data_type,
       character_maximum_length,
       CASE WHEN column_name = 'order_no' AND character_maximum_length = 50 THEN 'PASS'
            WHEN column_name = 'product_code' AND character_maximum_length = 50 THEN 'PASS'
            WHEN column_name = 'product_name' AND character_maximum_length = 200 THEN 'PASS'
            WHEN column_name = 'model' AND character_maximum_length = 100 THEN 'PASS'
            WHEN column_name = 'spec' AND character_maximum_length = 200 THEN 'PASS'
            WHEN column_name = 'brand' AND character_maximum_length = 100 THEN 'PASS'
            WHEN column_name = 'unit' AND character_maximum_length = 50 THEN 'PASS'
            WHEN column_name = 'remark' AND character_maximum_length = 500 THEN 'PASS'
            ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_bom_detail'
  AND column_name IN ('order_no', 'product_code', 'product_name', 'model', 'spec', 'brand', 'unit', 'remark');

-- ============================================================
-- 18. 默认值验证（关键字段）
-- ============================================================
SELECT column_name,
       column_default,
       CASE WHEN column_name = 'status' AND column_default LIKE '%0%' THEN 'PASS'
            WHEN column_name = 'loss_rate' AND column_default LIKE '%0%' THEN 'PASS'
            WHEN column_name = 'is_multi_unit' AND column_default ILIKE '%false%' THEN 'PASS'
            WHEN column_name = 'conversion_rate' AND column_default LIKE '%1%' THEN 'PASS'
            WHEN column_name = 'base_qty' AND column_default LIKE '%0%' THEN 'PASS'
            WHEN column_name = 'is_deleted' AND column_default ILIKE '%false%' THEN 'PASS'
            WHEN column_name = 'version' AND column_default LIKE '%1%' THEN 'PASS'
            WHEN column_name = 'created_at' AND column_default ILIKE '%now%' THEN 'PASS'
            WHEN column_name = 'updated_at' AND column_default ILIKE '%now%' THEN 'PASS'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'prod_product_bom_detail'
  AND column_name IN ('status', 'loss_rate', 'is_multi_unit', 'conversion_rate', 'base_qty', 'is_deleted', 'version', 'created_at', 'updated_at');

-- ============================================================
-- 19. 业务字段存在性验证（20个业务字段）
-- ============================================================
WITH required_biz_fields AS (
    SELECT unnest(ARRAY[
        'bom_id', 'sub_product_id', 'qty', 'loss_rate',
        'order_no', 'order_date', 'status', 'remark',
        'product_id', 'product_code', 'product_name', 'model',
        'spec', 'brand', 'unit_id', 'unit',
        'is_multi_unit', 'conversion_rate', 'base_unit_id', 'base_qty'
    ]) AS field_name
),
actual_fields AS (
    SELECT column_name FROM information_schema.columns WHERE table_name = 'prod_product_bom_detail'
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
       CASE WHEN indexname ~ '^(pk_|uk_|idx_)prod_product_bom_detail' THEN 'PASS'
            ELSE 'FAIL' END AS naming_check
FROM pg_indexes
WHERE tablename = 'prod_product_bom_detail'
  AND indexname NOT LIKE '%pkey'
ORDER BY indexname;
