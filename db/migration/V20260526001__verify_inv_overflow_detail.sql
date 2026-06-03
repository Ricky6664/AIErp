-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: inv_overflow_detail报溢主从表DDL验证查询脚本
-- Author: AI
-- Date: 2026-06-03
-- Task: P0-003-007-010-001-003
-- ============================================================

-- ============================================================
-- 一、inv_overflow 报溢主表验证
-- ============================================================

-- 1.1 表存在性验证
SELECT 'TABLE_INV_OVERFLOW_EXISTS' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'inv_overflow table should exist in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'inv_overflow';

-- 1.2 字段定义验证（列出所有字段及其类型/可空性）
SELECT column_name,
       data_type,
       character_maximum_length,
       numeric_precision,
       numeric_scale,
       is_nullable,
       column_default
FROM information_schema.columns
WHERE table_name = 'inv_overflow'
ORDER BY ordinal_position;

-- 1.3 10个通用字段存在性验证
WITH required_fields AS (
    SELECT unnest(ARRAY[
        'id', 'tenant_id', 'created_by', 'created_at',
        'updated_by', 'updated_at', 'is_deleted',
        'owner_dept_id', 'owner_id', 'version'
    ]) AS field_name
),
actual_fields AS (
    SELECT column_name FROM information_schema.columns WHERE table_name = 'inv_overflow'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- 1.4 字段数量验证（期望 22：12业务 + 10通用）
SELECT 'FIELD_COUNT_INV_OVERFLOW' AS check_name,
       CASE WHEN count(*) = 22 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 22 columns (12 business + 10 common)' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'inv_overflow';

-- 1.5 主键约束验证
SELECT 'PK_INV_OVERFLOW_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'inv_overflow should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'inv_overflow' AND c.contype = 'p';

-- 1.6 部分唯一索引验证（WHERE is_deleted = false）
SELECT indexname,
       indexdef,
       CASE WHEN indexdef ILIKE '%WHERE%is_deleted%false%' THEN 'PASS' ELSE 'FAIL' END AS partial_unique_check
FROM pg_indexes
WHERE tablename = 'inv_overflow'
  AND indexname LIKE 'uk_%';

-- 1.7 全部索引列表验证
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'inv_overflow'
ORDER BY indexname;

-- 1.8 多租户联合索引验证（tenant_id为首列）
SELECT 'TENANT_INDEX_INV_OVERFLOW' AS check_name,
       CASE WHEN count(*) >= 3 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected at least 3 tenant_id-leading indexes' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'inv_overflow'
  AND indexdef ~ 'USING btree \(tenant_id';

-- 1.9 COMMENT注释完整性验证
SELECT 'TABLE_COMMENT_INV_OVERFLOW' AS check_name,
       CASE WHEN obj_description('inv_overflow'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('inv_overflow'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('inv_overflow'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'inv_overflow'
ORDER BY c.ordinal_position;

-- 1.10 NOT NULL约束验证（关键字段）
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'order_no', 'order_date', 'warehouse_id', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'order_no', 'order_date', 'warehouse_id', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'inv_overflow'
  AND column_name IN ('id', 'tenant_id', 'order_no', 'order_date', 'warehouse_id', 'created_at', 'updated_at', 'is_deleted', 'version');

-- 1.11 外键约束检查（不应存在，应用层维护关联）
SELECT 'FK_INV_OVERFLOW_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'inv_overflow' AND c.contype = 'f';

-- 1.12 索引数量验证（期望 17：1 PK + 1 UNIQUE + 15 普通索引）
SELECT 'INDEX_COUNT_INV_OVERFLOW' AS check_name,
       CASE WHEN count(*) = 17 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 17 indexes (1 PK + 1 unique + 15 standard)' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'inv_overflow';

-- 1.13 SMALLINT字段类型验证（status字段）
SELECT column_name,
       data_type,
       CASE WHEN data_type = 'smallint' THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'inv_overflow'
  AND column_name = 'status';

-- 1.14 VARCHAR字段长度验证
SELECT column_name,
       data_type,
       character_maximum_length,
       CASE WHEN column_name = 'order_no' AND character_maximum_length = 50 THEN 'PASS'
            WHEN column_name = 'remark' AND character_maximum_length = 500 THEN 'PASS'
            ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'inv_overflow'
  AND column_name IN ('order_no', 'remark');

-- 1.15 默认值验证（关键字段）
SELECT column_name,
       column_default,
       CASE WHEN column_name = 'total_qty' AND column_default LIKE '%0%' THEN 'PASS'
            WHEN column_name = 'total_amount' AND column_default LIKE '%0%' THEN 'PASS'
            WHEN column_name = 'status' AND column_default LIKE '%0%' THEN 'PASS'
            WHEN column_name = 'is_deleted' AND column_default ILIKE '%false%' THEN 'PASS'
            WHEN column_name = 'version' AND column_default LIKE '%1%' THEN 'PASS'
            WHEN column_name = 'created_at' AND column_default ILIKE '%now%' THEN 'PASS'
            WHEN column_name = 'updated_at' AND column_default ILIKE '%now%' THEN 'PASS'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'inv_overflow'
  AND column_name IN ('total_qty', 'total_amount', 'status', 'is_deleted', 'version', 'created_at', 'updated_at');

-- 1.16 列名一致性验证（索引引用列必须存在于DDL中）
SELECT 'COLUMN_ORDER_NO_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       '"order_no" column must exist (unique index uk_inv_overflow_order_no references it)' AS description
FROM information_schema.columns
WHERE table_name = 'inv_overflow' AND column_name = 'order_no';

-- 1.17 DECIMAL(18,8)精度验证（金额/数量字段）
SELECT column_name,
       data_type,
       numeric_precision,
       numeric_scale,
       CASE WHEN data_type = 'numeric' AND numeric_precision = 18 AND numeric_scale = 8 THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'inv_overflow'
  AND column_name IN ('total_qty', 'total_amount');


-- ============================================================
-- 二、inv_overflow_detail 报溢从表验证
-- ============================================================

-- 2.1 表存在性验证
SELECT 'TABLE_INV_OVERFLOW_DETAIL_EXISTS' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'inv_overflow_detail table should exist in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'inv_overflow_detail';

-- 2.2 字段定义验证（列出所有字段及其类型/可空性）
SELECT column_name,
       data_type,
       character_maximum_length,
       numeric_precision,
       numeric_scale,
       is_nullable,
       column_default
FROM information_schema.columns
WHERE table_name = 'inv_overflow_detail'
ORDER BY ordinal_position;

-- 2.3 10个通用字段存在性验证
WITH required_fields AS (
    SELECT unnest(ARRAY[
        'id', 'tenant_id', 'created_by', 'created_at',
        'updated_by', 'updated_at', 'is_deleted',
        'owner_dept_id', 'owner_id', 'version'
    ]) AS field_name
),
actual_fields AS (
    SELECT column_name FROM information_schema.columns WHERE table_name = 'inv_overflow_detail'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- 2.4 字段数量验证（期望 54：10通用 + 11业务 + 11快照 + 22扩展）
SELECT 'FIELD_COUNT_INV_OVERFLOW_DETAIL' AS check_name,
       CASE WHEN count(*) = 54 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 54 columns (10 common + 11 business + 11 snapshot + 22 extension)' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'inv_overflow_detail';

-- 2.5 主键约束验证
SELECT 'PK_INV_OVERFLOW_DETAIL_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'inv_overflow_detail should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'inv_overflow_detail' AND c.contype = 'p';

-- 2.6 部分唯一索引验证（WHERE is_deleted = false）
SELECT indexname,
       indexdef,
       CASE WHEN indexdef ILIKE '%WHERE%is_deleted%false%' THEN 'PASS' ELSE 'FAIL' END AS partial_unique_check
FROM pg_indexes
WHERE tablename = 'inv_overflow_detail'
  AND indexname LIKE 'uk_%';

-- 2.7 全部索引列表验证
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'inv_overflow_detail'
ORDER BY indexname;

-- 2.8 多租户联合索引验证（tenant_id为首列）
SELECT 'TENANT_INDEX_INV_OVERFLOW_DETAIL' AS check_name,
       CASE WHEN count(*) >= 2 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected at least 2 tenant_id-leading indexes' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'inv_overflow_detail'
  AND indexdef ~ 'USING btree \(tenant_id';

-- 2.9 COMMENT注释完整性验证
SELECT 'TABLE_COMMENT_INV_OVERFLOW_DETAIL' AS check_name,
       CASE WHEN obj_description('inv_overflow_detail'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('inv_overflow_detail'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('inv_overflow_detail'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'inv_overflow_detail'
ORDER BY c.ordinal_position;

-- 2.10 NOT NULL约束验证（关键字段）
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'order_id', 'product_id', 'warehouse_id', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'order_id', 'product_id', 'warehouse_id', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'inv_overflow_detail'
  AND column_name IN ('id', 'tenant_id', 'order_id', 'product_id', 'warehouse_id', 'created_at', 'updated_at', 'is_deleted', 'version');

-- 2.11 外键约束检查（不应存在，应用层维护关联）
SELECT 'FK_INV_OVERFLOW_DETAIL_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'inv_overflow_detail' AND c.contype = 'f';

-- 2.12 索引数量验证（期望 17：1 PK + 1 UNIQUE + 15 普通索引）
SELECT 'INDEX_COUNT_INV_OVERFLOW_DETAIL' AS check_name,
       CASE WHEN count(*) = 17 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 17 indexes (1 PK + 1 unique + 15 standard)' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'inv_overflow_detail';

-- 2.13 VARCHAR字段长度验证
SELECT column_name,
       data_type,
       character_maximum_length,
       CASE WHEN column_name = 'code' AND character_maximum_length = 50 THEN 'PASS'
            WHEN column_name = 'batch_no' AND character_maximum_length = 50 THEN 'PASS'
            WHEN column_name = 'product_code' AND character_maximum_length = 50 THEN 'PASS'
            WHEN column_name = 'product_name' AND character_maximum_length = 100 THEN 'PASS'
            WHEN column_name = 'model' AND character_maximum_length = 100 THEN 'PASS'
            WHEN column_name = 'spec' AND character_maximum_length = 100 THEN 'PASS'
            WHEN column_name = 'brand' AND character_maximum_length = 50 THEN 'PASS'
            WHEN column_name = 'unit' AND character_maximum_length = 30 THEN 'PASS'
            WHEN column_name = 'remark' AND character_maximum_length = 500 THEN 'PASS'
            ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'inv_overflow_detail'
  AND column_name IN ('code', 'batch_no', 'product_code', 'product_name', 'model', 'spec', 'brand', 'unit', 'remark');

-- 2.14 默认值验证（关键字段）
SELECT column_name,
       column_default,
       CASE WHEN column_name = 'is_deleted' AND column_default ILIKE '%false%' THEN 'PASS'
            WHEN column_name = 'version' AND column_default LIKE '%1%' THEN 'PASS'
            WHEN column_name = 'created_at' AND column_default ILIKE '%now%' THEN 'PASS'
            WHEN column_name = 'updated_at' AND column_default ILIKE '%now%' THEN 'PASS'
            WHEN column_name = 'line_no' AND column_default LIKE '%1%' THEN 'PASS'
            WHEN column_name = 'qty' AND column_default LIKE '%0%' THEN 'PASS'
            WHEN column_name = 'price' AND column_default LIKE '%0%' THEN 'PASS'
            WHEN column_name = 'amount' AND column_default LIKE '%0%' THEN 'PASS'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'inv_overflow_detail'
  AND column_name IN ('line_no', 'qty', 'price', 'amount', 'is_deleted', 'version', 'created_at', 'updated_at');

-- 2.15 商品快照字段存在性验证
WITH required_snapshot_fields AS (
    SELECT unnest(ARRAY[
        'product_code', 'product_name', 'model', 'spec', 'brand',
        'unit_id', 'unit', 'is_multi_unit', 'conversion_rate', 'base_unit_id', 'base_qty'
    ]) AS field_name
),
actual_fields AS (
    SELECT column_name FROM information_schema.columns WHERE table_name = 'inv_overflow_detail'
)
SELECT rf.field_name AS missing_snapshot_field,
       'FAIL' AS result
FROM required_snapshot_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- 2.16 扩展字段存在性验证
WITH required_ext_fields AS (
    SELECT unnest(ARRAY[
        'ext_str1', 'ext_str2', 'ext_str3', 'ext_str4', 'ext_str5',
        'ext_str6', 'ext_str7', 'ext_str8', 'ext_str9', 'ext_str10',
        'ext_num1', 'ext_num2', 'ext_num3', 'ext_num4', 'ext_num5',
        'ext_date1', 'ext_date2', 'ext_date3',
        'ext_bool1', 'ext_bool2', 'ext_bool3',
        'ext_json'
    ]) AS field_name
),
actual_fields AS (
    SELECT column_name FROM information_schema.columns WHERE table_name = 'inv_overflow_detail'
)
SELECT rf.field_name AS missing_ext_field,
       'FAIL' AS result
FROM required_ext_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- 2.17 DECIMAL(18,8)精度验证（金额/数量字段）
SELECT column_name,
       data_type,
       numeric_precision,
       numeric_scale,
       CASE WHEN data_type = 'numeric' AND COALESCE(numeric_precision, 18) = 18 AND COALESCE(numeric_scale, 8) = 8 THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'inv_overflow_detail'
  AND column_name IN ('qty', 'price', 'amount', 'conversion_rate', 'base_qty');


-- ============================================================
-- 三、Flyway迁移记录验证
-- ============================================================
SELECT version, description, script, installed_on, success
FROM flyway_schema_history
WHERE script LIKE '%inv_overflow%'
ORDER BY installed_rank DESC;
