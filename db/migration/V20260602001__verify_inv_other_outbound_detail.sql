-- ============================================================
-- Flyway Migration Script
-- Version: V20260602001
-- Description: inv_other_outbound / inv_other_outbound_detail 其他出库主从表DDL验证查询脚本
-- Author: AI
-- Date: 2026-06-02
-- Task: P0-003-007-005-001-003
-- ============================================================

-- ============================================================
-- 1. 表存在性验证（主表 + 从表）
-- ============================================================
SELECT 'TABLE_EXISTS_CHECK' AS check_name,
       CASE WHEN count(*) = 2 THEN 'PASS' ELSE 'FAIL' END AS result,
       'inv_other_outbound and inv_other_outbound_detail should both exist' AS description,
       count(*) AS actual_count
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename IN ('inv_other_outbound', 'inv_other_outbound_detail');

-- ============================================================
-- 2. inv_other_outbound 主表字段定义验证
-- ============================================================
SELECT column_name,
       data_type,
       character_maximum_length,
       numeric_precision,
       numeric_scale,
       is_nullable,
       column_default
FROM information_schema.columns
WHERE table_name = 'inv_other_outbound'
ORDER BY ordinal_position;

-- ============================================================
-- 3. inv_other_outbound_detail 从表字段定义验证
-- ============================================================
SELECT column_name,
       data_type,
       character_maximum_length,
       numeric_precision,
       numeric_scale,
       is_nullable,
       column_default
FROM information_schema.columns
WHERE table_name = 'inv_other_outbound_detail'
ORDER BY ordinal_position;

-- ============================================================
-- 4. 主表 10 个通用字段存在性验证
-- ============================================================
WITH required_fields AS (
    SELECT unnest(ARRAY[
        'id', 'tenant_id', 'created_by', 'created_at',
        'updated_by', 'updated_at', 'is_deleted',
        'owner_dept_id', 'owner_id', 'version'
    ]) AS field_name
),
actual_fields AS (
    SELECT column_name FROM information_schema.columns WHERE table_name = 'inv_other_outbound'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- ============================================================
-- 5. 从表 10 个通用字段存在性验证
-- ============================================================
WITH required_fields AS (
    SELECT unnest(ARRAY[
        'id', 'tenant_id', 'created_by', 'created_at',
        'updated_by', 'updated_at', 'is_deleted',
        'owner_dept_id', 'owner_id', 'version'
    ]) AS field_name
),
actual_fields AS (
    SELECT column_name FROM information_schema.columns WHERE table_name = 'inv_other_outbound_detail'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- ============================================================
-- 6. 主表字段数量验证（期望 24 = 14业务 + 10通用）
-- ============================================================
SELECT 'FIELD_COUNT_MAIN_CHECK' AS check_name,
       CASE WHEN count(*) = 24 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 24 columns for inv_other_outbound (14 business + 10 common)' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'inv_other_outbound';

-- ============================================================
-- 7. 从表字段数量验证（期望 54 = 22业务 + 22扩展 + 10通用）
-- ============================================================
SELECT 'FIELD_COUNT_DETAIL_CHECK' AS check_name,
       CASE WHEN count(*) = 54 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 54 columns for inv_other_outbound_detail (22 biz + 22 ext + 10 common)' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'inv_other_outbound_detail';

-- ============================================================
-- 8. 数值精度验证（主表 total_qty / total_amount 应为 decimal(18,8)）
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
WHERE table_name = 'inv_other_outbound'
  AND column_name IN ('total_qty', 'total_amount');

-- ============================================================
-- 9. 数值精度验证（从表 qty/price/amount/base_qty/ext_num 字段应为 decimal(18,8)）
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
WHERE table_name = 'inv_other_outbound_detail'
  AND column_name IN ('qty', 'price', 'amount', 'conversion_rate', 'base_qty',
                      'ext_num1', 'ext_num2', 'ext_num3', 'ext_num4', 'ext_num5');

-- ============================================================
-- 10. 主表主键约束验证
-- ============================================================
SELECT 'PK_MAIN_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'inv_other_outbound should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'inv_other_outbound' AND c.contype = 'p';

-- ============================================================
-- 11. 从表主键约束验证
-- ============================================================
SELECT 'PK_DETAIL_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'inv_other_outbound_detail should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'inv_other_outbound_detail' AND c.contype = 'p';

-- ============================================================
-- 12. 从表部分唯一索引验证（WHERE is_deleted = false）
-- ============================================================
SELECT indexname,
       indexdef,
       CASE WHEN indexdef ILIKE '%WHERE%is_deleted%false%' THEN 'PASS' ELSE 'FAIL' END AS partial_unique_check
FROM pg_indexes
WHERE tablename = 'inv_other_outbound_detail'
  AND indexname LIKE 'uk_%';

-- ============================================================
-- 13. 从表全部索引列表验证
-- ============================================================
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'inv_other_outbound_detail'
ORDER BY indexname;

-- ============================================================
-- 14. 从表多租户联合索引验证（tenant_id为首列）
-- ============================================================
SELECT 'TENANT_INDEX_DETAIL_CHECK' AS check_name,
       CASE WHEN count(*) >= 4 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected at least 4 tenant_id-leading indexes on inv_other_outbound_detail' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'inv_other_outbound_detail'
  AND indexdef ~ 'USING btree \(tenant_id';

-- ============================================================
-- 15. 主表 COMMENT 注释完整性验证
-- ============================================================
SELECT 'TABLE_COMMENT_MAIN_CHECK' AS check_name,
       CASE WHEN obj_description('inv_other_outbound'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('inv_other_outbound'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('inv_other_outbound'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'inv_other_outbound'
ORDER BY c.ordinal_position;

-- ============================================================
-- 16. 从表 COMMENT 注释完整性验证
-- ============================================================
SELECT 'TABLE_COMMENT_DETAIL_CHECK' AS check_name,
       CASE WHEN obj_description('inv_other_outbound_detail'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('inv_other_outbound_detail'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('inv_other_outbound_detail'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'inv_other_outbound_detail'
ORDER BY c.ordinal_position;

-- ============================================================
-- 17. 主表 NOT NULL 约束验证（关键字段）
-- ============================================================
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'order_no', 'order_date', 'warehouse_id', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'order_no', 'order_date', 'warehouse_id', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'inv_other_outbound'
  AND column_name IN ('id', 'tenant_id', 'order_no', 'order_date', 'warehouse_id', 'created_at', 'updated_at', 'is_deleted', 'version');

-- ============================================================
-- 18. 从表 NOT NULL 约束验证（关键字段）
-- ============================================================
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'order_id', 'product_id', 'warehouse_id', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'order_id', 'product_id', 'warehouse_id', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'inv_other_outbound_detail'
  AND column_name IN ('id', 'tenant_id', 'order_id', 'product_id', 'warehouse_id', 'created_at', 'updated_at', 'is_deleted', 'version');

-- ============================================================
-- 19. 外键约束检查（两表均不应存在，应用层维护关联）
-- ============================================================
SELECT 'FK_MAIN_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist on inv_other_outbound (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'inv_other_outbound' AND c.contype = 'f';

SELECT 'FK_DETAIL_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist on inv_other_outbound_detail (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'inv_other_outbound_detail' AND c.contype = 'f';

-- ============================================================
-- 20. 从表索引数量验证（期望 17 = 1 PK + 1 唯一 + 15 普通索引）
-- ============================================================
SELECT 'INDEX_COUNT_DETAIL_CHECK' AS check_name,
       CASE WHEN count(*) = 17 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 17 indexes on inv_other_outbound_detail (1 PK + 1 unique + 15 standard)' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'inv_other_outbound_detail';

-- ============================================================
-- 21. 列名一致性验证（索引引用列必须存在于DDL中）
-- ============================================================
SELECT 'COLUMN_CODE_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'code column must exist in inv_other_outbound_detail (unique index references it)' AS description
FROM information_schema.columns
WHERE table_name = 'inv_other_outbound_detail' AND column_name = 'code';

SELECT 'COLUMN_ORDER_ID_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'order_id column must exist in inv_other_outbound_detail (index references it)' AS description
FROM information_schema.columns
WHERE table_name = 'inv_other_outbound_detail' AND column_name = 'order_id';

-- ============================================================
-- 22. Flyway 迁移记录验证
-- ============================================================
SELECT version, description, script, installed_on, success
FROM flyway_schema_history
WHERE script LIKE '%outbound%' OR script LIKE '%inv_other%'
ORDER BY installed_rank DESC;
