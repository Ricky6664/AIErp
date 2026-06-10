-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: inv_stocktaking_detail盘点主从表DDL验证查询脚本
-- Author: AI
-- Date: 2026-06-02
-- Task: P0-003-007-007-001-003
-- ============================================================

-- ============================================================
-- Part A: inv_stocktaking 盘点主表验证
-- ============================================================

-- A.1 表存在性验证
SELECT 'TABLE_EXISTS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'inv_stocktaking table should exist in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'inv_stocktaking';

-- A.2 字段定义验证（列出所有字段及其类型/可空性）
SELECT column_name,
       data_type,
       character_maximum_length,
       numeric_precision,
       numeric_scale,
       is_nullable,
       column_default
FROM information_schema.columns
WHERE table_name = 'inv_stocktaking'
ORDER BY ordinal_position;

-- A.3 10个通用字段存在性验证
WITH required_fields AS (
    SELECT unnest(ARRAY[
        'id', 'tenant_id', 'created_by', 'created_at',
        'updated_by', 'updated_at', 'is_deleted',
        'owner_dept_id', 'owner_id', 'version'
    ]) AS field_name
),
actual_fields AS (
    SELECT column_name FROM information_schema.columns WHERE table_name = 'inv_stocktaking'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- A.4 字段数量验证（期望 22：10 通用 + 12 业务）
SELECT 'FIELD_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 22 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 22 columns (10 generic + 12 business)' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'inv_stocktaking';

-- A.5 数值精度验证（金额/数量字段应为decimal(18,8)）
SELECT column_name,
       data_type,
       numeric_precision,
       numeric_scale,
       CASE WHEN data_type = 'numeric'
             AND numeric_precision = 18
             AND numeric_scale = 8
            THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'inv_stocktaking'
  AND column_name IN ('total_qty', 'total_amount');

-- A.6 主键约束验证
SELECT 'PK_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'inv_stocktaking should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'inv_stocktaking' AND c.contype = 'p';

-- A.7 部分唯一索引验证（WHERE is_deleted = false）
SELECT indexname,
       indexdef,
       CASE WHEN indexdef ILIKE '%WHERE%is_deleted%false%' THEN 'PASS' ELSE 'FAIL' END AS partial_unique_check
FROM pg_indexes
WHERE tablename = 'inv_stocktaking'
  AND indexname LIKE 'uk_%';

-- A.8 全部索引列表
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'inv_stocktaking'
ORDER BY indexname;

-- A.9 索引数量验证（期望 17：1 PK + 1 UNIQUE + 15 标准）
SELECT 'INDEX_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 17 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 17 indexes (1 PK + 1 unique + 15 standard)' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'inv_stocktaking';

-- A.10 多租户联合索引验证（tenant_id为首列）
SELECT 'TENANT_INDEX_CHECK' AS check_name,
       CASE WHEN count(*) >= 4 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected at least 4 tenant_id-leading indexes' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'inv_stocktaking'
  AND indexdef ~ 'USING btree \(tenant_id';

-- A.11 COMMENT注释完整性验证（主表）
SELECT 'TABLE_COMMENT_CHECK' AS check_name,
       CASE WHEN obj_description('inv_stocktaking'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('inv_stocktaking'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('inv_stocktaking'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'inv_stocktaking'
ORDER BY c.ordinal_position;

-- A.12 NOT NULL约束验证（关键字段）
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'order_no', 'order_date', 'warehouse_id', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'order_no', 'order_date', 'warehouse_id', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'inv_stocktaking'
  AND column_name IN ('id', 'tenant_id', 'order_no', 'order_date', 'warehouse_id', 'created_at', 'updated_at', 'is_deleted', 'version');

-- A.13 外键约束检查（不应存在）
SELECT 'FK_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'inv_stocktaking' AND c.contype = 'f';

-- A.14 关键业务字段存在性验证
SELECT 'COLUMN_WAREHOUSE_ID_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'warehouse_id column must exist' AS description
FROM information_schema.columns
WHERE table_name = 'inv_stocktaking' AND column_name = 'warehouse_id';

SELECT 'COLUMN_ORDER_NO_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'order_no column must exist (uk_inv_stocktaking_order_no references it)' AS description
FROM information_schema.columns
WHERE table_name = 'inv_stocktaking' AND column_name = 'order_no';

-- ============================================================
-- Part B: inv_stocktaking_detail 盘点从表验证
-- ============================================================

-- B.1 表存在性验证
SELECT 'TABLE_EXISTS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'inv_stocktaking_detail table should exist in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'inv_stocktaking_detail';

-- B.2 字段定义验证（列出所有字段及其类型/可空性）
SELECT column_name,
       data_type,
       character_maximum_length,
       numeric_precision,
       numeric_scale,
       is_nullable,
       column_default
FROM information_schema.columns
WHERE table_name = 'inv_stocktaking_detail'
ORDER BY ordinal_position;

-- B.3 10个通用字段存在性验证
WITH required_fields AS (
    SELECT unnest(ARRAY[
        'id', 'tenant_id', 'created_by', 'created_at',
        'updated_by', 'updated_at', 'is_deleted',
        'owner_dept_id', 'owner_id', 'version'
    ]) AS field_name
),
actual_fields AS (
    SELECT column_name FROM information_schema.columns WHERE table_name = 'inv_stocktaking_detail'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- B.4 字段数量验证（期望 58：10 通用 + 26 业务 + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json）
SELECT 'FIELD_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 58 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 58 columns (10 generic + 26 business + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json)' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'inv_stocktaking_detail';

-- B.5 数值精度验证（金额/数量/转换率字段应为decimal(18,8)）
SELECT column_name,
       data_type,
       numeric_precision,
       numeric_scale,
       CASE WHEN data_type = 'numeric'
             AND numeric_precision = 18
             AND numeric_scale = 8
            THEN 'PASS' ELSE 'FAIL' END AS result
FROM information_schema.columns
WHERE table_name = 'inv_stocktaking_detail'
  AND column_name IN ('stock_qty', 'actual_qty', 'diff_qty', 'price',
                       'stock_amount', 'actual_amount', 'diff_amount',
                       'conversion_rate', 'base_qty',
                       'ext_num1', 'ext_num2', 'ext_num3', 'ext_num4', 'ext_num5');

-- B.6 主键约束验证
SELECT 'PK_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'inv_stocktaking_detail should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'inv_stocktaking_detail' AND c.contype = 'p';

-- B.7 部分唯一索引验证（WHERE is_deleted = false）
SELECT indexname,
       indexdef,
       CASE WHEN indexdef ILIKE '%WHERE%is_deleted%false%' THEN 'PASS' ELSE 'FAIL' END AS partial_unique_check
FROM pg_indexes
WHERE tablename = 'inv_stocktaking_detail'
  AND indexname LIKE 'uk_%';

-- B.8 全部索引列表
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'inv_stocktaking_detail'
ORDER BY indexname;

-- B.9 索引数量验证（期望 17：1 PK + 1 UNIQUE + 15 标准）
SELECT 'INDEX_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 17 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 17 indexes (1 PK + 1 unique + 15 standard)' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'inv_stocktaking_detail';

-- B.10 多租户联合索引验证（tenant_id为首列）
SELECT 'TENANT_INDEX_CHECK' AS check_name,
       CASE WHEN count(*) >= 4 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected at least 4 tenant_id-leading indexes' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'inv_stocktaking_detail'
  AND indexdef ~ 'USING btree \(tenant_id';

-- B.11 COMMENT注释完整性验证（从表）
SELECT 'TABLE_COMMENT_CHECK' AS check_name,
       CASE WHEN obj_description('inv_stocktaking_detail'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('inv_stocktaking_detail'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('inv_stocktaking_detail'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'inv_stocktaking_detail'
ORDER BY c.ordinal_position;

-- B.12 NOT NULL约束验证（关键字段）
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'order_id', 'product_id', 'warehouse_id', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'order_id', 'product_id', 'warehouse_id', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'inv_stocktaking_detail'
  AND column_name IN ('id', 'tenant_id', 'order_id', 'product_id', 'warehouse_id', 'created_at', 'updated_at', 'is_deleted', 'version');

-- B.13 外键约束检查（不应存在）
SELECT 'FK_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'inv_stocktaking_detail' AND c.contype = 'f';

-- B.14 关键关联字段存在性验证
SELECT 'COLUMN_ORDER_ID_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'order_id column must exist (idx_inv_stocktaking_detail_order references it)' AS description
FROM information_schema.columns
WHERE table_name = 'inv_stocktaking_detail' AND column_name = 'order_id';

SELECT 'COLUMN_LOCATION_ID_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'location_id column must exist (idx_inv_stocktaking_detail_location references it)' AS description
FROM information_schema.columns
WHERE table_name = 'inv_stocktaking_detail' AND column_name = 'location_id';

SELECT 'COLUMN_CODE_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'code column must exist (uk_inv_stocktaking_detail_code references it)' AS description
FROM information_schema.columns
WHERE table_name = 'inv_stocktaking_detail' AND column_name = 'code';

-- ============================================================
-- Part C: Flyway迁移记录验证
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.tables WHERE table_name = 'flyway_schema_history') THEN
        EXECUTE 'SELECT version, description, script, installed_on, success FROM flyway_schema_history WHERE script LIKE ''%inv_stocktaking%'' ORDER BY installed_rank DESC';
    ELSE
        RAISE NOTICE 'Flyway schema history not initialized — manual DDL execution assumed';
    END IF;
END $$;

-- ============================================================
-- Part D: 主从表关联完整性检查
-- ============================================================
SELECT 'MASTER_DETAIL_CHECK' AS check_name,
       CASE WHEN m.cnt > 0 AND d.cnt > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Both master (inv_stocktaking) and detail (inv_stocktaking_detail) tables should exist' AS description
FROM
    (SELECT count(*) AS cnt FROM pg_tables WHERE schemaname = 'public' AND tablename = 'inv_stocktaking') m,
    (SELECT count(*) AS cnt FROM pg_tables WHERE schemaname = 'public' AND tablename = 'inv_stocktaking_detail') d;
