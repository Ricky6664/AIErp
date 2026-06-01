-- ============================================================
-- Flyway Verification Script
-- Version: V20260526001
-- Description: 验证CRM标签定义表DDL（P0-003-005-002-001-003）
--   - 确认crm_tag_definition表已创建
--   - 验证字段定义、约束、索引
--   - 验证COMMENT完整性
--   - 验证Flyway迁移记录
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- Part 1: 表存在性验证
-- ============================================================
SELECT 'TABLE_EXISTS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'crm_tag_definition table should exist in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'crm_tag_definition';

-- ============================================================
-- Part 2: 字段定义验证（列出所有字段及其类型/可空性）
-- ============================================================
SELECT column_name,
       data_type,
       character_maximum_length,
       numeric_precision,
       numeric_scale,
       is_nullable,
       column_default
FROM information_schema.columns
WHERE table_name = 'crm_tag_definition'
ORDER BY ordinal_position;

-- ============================================================
-- Part 3: 10个通用字段存在性验证
-- ============================================================
WITH required_fields AS (
    SELECT unnest(ARRAY[
        'id', 'tenant_id', 'created_by', 'created_at',
        'updated_by', 'updated_at', 'is_deleted',
        'owner_dept_id', 'owner_id', 'version'
    ]) AS field_name
),
actual_fields AS (
    SELECT column_name FROM information_schema.columns WHERE table_name = 'crm_tag_definition'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- ============================================================
-- Part 4: 字段数量验证（期望 >= 28：5业务 + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json + 10 通用 - 9 重叠?）
-- 实际列数: id, tenant_id, tag_name, tag_group, tag_color, sort_no, enable_flag,
--   ext_str1-10(10), ext_num1-5(5), ext_date1-3(3), ext_bool1-3(3), ext_json(1),
--   created_at, updated_at, created_by, updated_by, is_deleted, owner_dept_id, owner_id, version(8) = 38
-- ============================================================
SELECT 'FIELD_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) >= 38 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected at least 38 columns' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'crm_tag_definition';

-- ============================================================
-- Part 5: 数值精度验证（ext_num字段应为decimal(18,8)）
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
WHERE table_name = 'crm_tag_definition'
  AND column_name IN ('ext_num1', 'ext_num2', 'ext_num3', 'ext_num4', 'ext_num5');

-- ============================================================
-- Part 6: 主键约束验证
-- ============================================================
SELECT 'PK_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'crm_tag_definition should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'crm_tag_definition' AND c.contype = 'p';

-- ============================================================
-- Part 7: 部分唯一索引验证（WHERE is_deleted = false）
-- ============================================================
SELECT indexname,
       indexdef,
       CASE WHEN indexdef ILIKE '%CREATE UNIQUE INDEX%'
             AND indexdef ILIKE '%WHERE is_deleted = false%'
            THEN 'PASS' ELSE 'FAIL' END AS partial_unique_check
FROM pg_indexes
WHERE tablename = 'crm_tag_definition'
  AND indexname LIKE 'uk_%';

-- ============================================================
-- Part 8: 全部索引列表验证
-- ============================================================
SELECT 'INDEX_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) >= 12 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected >= 12 indexes, found ' || count(*) AS detail
FROM pg_indexes
WHERE tablename = 'crm_tag_definition';

SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'crm_tag_definition'
ORDER BY indexname;

-- ============================================================
-- Part 9: 多租户联合索引验证（tenant_id为首列）
-- ============================================================
SELECT indexname,
       indexdef,
       CASE WHEN indexdef ~ '\(tenant_id[,\)]' THEN 'PASS'
            ELSE 'CHECK_MANUAL' END AS tenant_first_check
FROM pg_indexes
WHERE tablename = 'crm_tag_definition'
  AND indexname LIKE '%idx_%'
ORDER BY indexname;

-- ============================================================
-- Part 10: NOT NULL约束验证（关键字段）
-- ============================================================
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'tag_name', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'tag_name', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'crm_tag_definition'
  AND column_name IN ('id', 'tenant_id', 'tag_name', 'created_at', 'updated_at', 'is_deleted', 'version');

-- ============================================================
-- Part 11: COMMENT注释完整性验证
-- ============================================================
SELECT 'TABLE_COMMENT_CHECK' AS check_name,
       CASE WHEN obj_description('crm_tag_definition'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('crm_tag_definition'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('crm_tag_definition'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'crm_tag_definition'
ORDER BY c.ordinal_position;

-- ============================================================
-- Part 12: 外键约束检查（禁止外键，应用层维护关联）
-- ============================================================
SELECT 'FK_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'crm_tag_definition' AND c.contype = 'f';

-- ============================================================
-- Part 13: Flyway迁移记录验证
-- ============================================================
SELECT version, description, script, installed_on, success
FROM flyway_schema_history
WHERE script LIKE '%crm_tag_definition%'
ORDER BY installed_rank DESC;

-- 验证V20260601075和V20260601076迁移成功
SELECT version, description,
       CASE WHEN success THEN 'PASS' ELSE 'FAIL' END AS status
FROM flyway_schema_history
WHERE version IN ('20260601075', '20260601076')
ORDER BY version;

-- ============================================================
-- Part 14: 综合验证摘要
-- ============================================================
SELECT 'VERIFICATION SUMMARY' AS section,
       'Run Parts 1-13 and review all PASS/FAIL results above.' AS summary;
