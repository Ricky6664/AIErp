-- ============================================================
-- Flyway Migration Script
-- Version: V20260601084
-- Description: crm_customer_address客户地址表DDL验证查询脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-005-001-003
-- ============================================================

-- ============================================================
-- 1. 表存在性验证
-- ============================================================
SELECT 'TABLE_EXISTS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'crm_customer_address table should exist in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'crm_customer_address';

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
WHERE table_name = 'crm_customer_address'
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
    SELECT column_name FROM information_schema.columns WHERE table_name = 'crm_customer_address'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- ============================================================
-- 4. 字段数量验证（期望 38：8业务 + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json + 10 通用）
-- ============================================================
SELECT 'FIELD_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 38 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 38 columns (8 business + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json + 10 generic)' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'crm_customer_address';

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
WHERE table_name = 'crm_customer_address'
  AND column_name IN ('ext_num1', 'ext_num2', 'ext_num3', 'ext_num4', 'ext_num5');

-- ============================================================
-- 6. 主键约束验证
-- ============================================================
SELECT 'PK_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'crm_customer_address should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'crm_customer_address' AND c.contype = 'p';

-- ============================================================
-- 7. 全部索引列表验证
-- ============================================================
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'crm_customer_address'
ORDER BY indexname;

-- ============================================================
-- 8. 多租户联合索引验证（tenant_id为首列）
-- ============================================================
SELECT 'TENANT_INDEX_CHECK' AS check_name,
       CASE WHEN count(*) >= 4 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected at least 4 tenant_id-leading indexes' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'crm_customer_address'
  AND indexdef ~ 'USING btree \(tenant_id';

-- ============================================================
-- 9. COMMENT注释完整性验证
-- ============================================================
SELECT 'TABLE_COMMENT_CHECK' AS check_name,
       CASE WHEN obj_description('crm_customer_address'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('crm_customer_address'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('crm_customer_address'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'crm_customer_address'
ORDER BY c.ordinal_position;

-- ============================================================
-- 10. NOT NULL约束验证（关键字段）
-- ============================================================
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'customer_id', 'address_type', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'customer_id', 'address_type', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'crm_customer_address'
  AND column_name IN ('id', 'tenant_id', 'customer_id', 'address_type', 'created_at', 'updated_at', 'is_deleted', 'version');

-- ============================================================
-- 11. Flyway迁移记录验证
-- ============================================================
SELECT version, description, script, installed_on, success
FROM flyway_schema_history
WHERE script LIKE '%crm_customer_address%'
ORDER BY installed_rank DESC;

-- ============================================================
-- 12. 外键约束检查（不应存在，应用层维护关联）
-- ============================================================
SELECT 'FK_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'crm_customer_address' AND c.contype = 'f';

-- ============================================================
-- 13. 索引数量验证（期望 11：1 PK + 10 普通索引）
-- ============================================================
SELECT 'INDEX_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 11 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 11 indexes (1 PK + 10 standard)' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'crm_customer_address';

-- ============================================================
-- 14. 部分唯一索引检查（crm_customer_address无业务唯一字段，不应有唯一索引）
-- ============================================================
SELECT 'UNIQUE_INDEX_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'INFO' END AS result,
       'crm_customer_address has no business unique field, no partial unique index expected' AS description
FROM pg_indexes
WHERE tablename = 'crm_customer_address'
  AND indexname LIKE 'uk_%';

-- ============================================================
-- 15. 索引列名一致性验证（索引引用列必须存在于DDL中）
-- ============================================================
SELECT 'COLUMN_CUSTOMER_ID_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'customer_id column must exist (indexes reference customer_id)' AS description
FROM information_schema.columns
WHERE table_name = 'crm_customer_address' AND column_name = 'customer_id';

SELECT 'COLUMN_ADDRESS_TYPE_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'address_type column must exist (index references address_type)' AS description
FROM information_schema.columns
WHERE table_name = 'crm_customer_address' AND column_name = 'address_type';
