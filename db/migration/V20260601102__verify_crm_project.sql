-- ============================================================
-- Flyway Migration Script
-- Version: V20260601102
-- Description: crm_project客户项目表DDL验证查询脚本
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-011-001-003
-- ============================================================

-- ============================================================
-- 1. 表存在性验证
-- ============================================================
SELECT 'TABLE_EXISTS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'crm_project table should exist in public schema' AS description
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'crm_project';

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
WHERE table_name = 'crm_project'
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
    SELECT column_name FROM information_schema.columns WHERE table_name = 'crm_project'
)
SELECT rf.field_name AS missing_field,
       'FAIL' AS result
FROM required_fields rf
LEFT JOIN actual_fields af ON rf.field_name = af.column_name
WHERE af.column_name IS NULL;

-- ============================================================
-- 4. 字段数量验证（期望 57：10通用 + 8业务 + 4单据 + 13商品快照 + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json）
-- ============================================================
SELECT 'FIELD_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 57 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 57 columns' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'crm_project';

-- ============================================================
-- 5. 数值精度验证（qty + conversion_rate + base_qty + ext_num 字段应为decimal(18,8)）
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
WHERE table_name = 'crm_project'
  AND column_name IN ('qty', 'conversion_rate', 'base_qty',
                       'ext_num1', 'ext_num2', 'ext_num3', 'ext_num4', 'ext_num5');

-- ============================================================
-- 6. 主键约束验证
-- ============================================================
SELECT 'PK_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'crm_project should have exactly 1 primary key' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'crm_project' AND c.contype = 'p';

-- ============================================================
-- 7. 主键约束命名验证
-- ============================================================
SELECT 'PK_NAME_CHECK' AS check_name,
       CASE WHEN c.conname = 'pk_crm_project' THEN 'PASS' ELSE 'FAIL' END AS result,
       'Primary key should be named pk_crm_project, actual: ' || c.conname AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'crm_project' AND c.contype = 'p';

-- ============================================================
-- 8. 部分唯一索引验证（WHERE is_deleted = false）
-- ============================================================
SELECT indexname,
       indexdef,
       CASE WHEN indexdef ILIKE '%WHERE%is_deleted%false%' THEN 'PASS' ELSE 'FAIL' END AS partial_unique_check
FROM pg_indexes
WHERE tablename = 'crm_project'
  AND indexname LIKE 'uk_%';

-- ============================================================
-- 9. 全部索引列表验证
-- ============================================================
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'crm_project'
ORDER BY indexname;

-- ============================================================
-- 10. 索引数量验证（期望 20：1 PK + 1 UNIQUE + 18 标准索引）
-- ============================================================
SELECT 'INDEX_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 20 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 20 indexes (1 PK + 1 unique + 18 standard)' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'crm_project';

-- ============================================================
-- 11. 多租户联合索引验证（tenant_id为首列）
-- ============================================================
SELECT 'TENANT_INDEX_CHECK' AS check_name,
       CASE WHEN count(*) >= 10 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected at least 10 tenant_id-leading indexes' AS description,
       count(*) AS actual_count
FROM pg_indexes
WHERE tablename = 'crm_project'
  AND indexdef ~ 'USING btree \(tenant_id';

-- ============================================================
-- 12. COMMENT注释完整性验证
-- ============================================================
SELECT 'TABLE_COMMENT_CHECK' AS check_name,
       CASE WHEN obj_description('crm_project'::regclass) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS result;

SELECT c.column_name,
       col_description('crm_project'::regclass, c.ordinal_position) AS comment,
       CASE WHEN col_description('crm_project'::regclass, c.ordinal_position) IS NULL THEN 'MISSING' ELSE 'OK' END AS status
FROM information_schema.columns c
WHERE c.table_name = 'crm_project'
ORDER BY c.ordinal_position;

-- ============================================================
-- 13. NOT NULL约束验证（关键字段）
-- ============================================================
SELECT column_name,
       is_nullable,
       CASE WHEN column_name IN ('id', 'tenant_id', 'customer_id', 'code', 'project_name', 'stage', 'status', 'is_multi_unit', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'NO' THEN 'PASS'
            WHEN column_name IN ('id', 'tenant_id', 'customer_id', 'code', 'project_name', 'stage', 'status', 'is_multi_unit', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND is_nullable = 'YES' THEN 'FAIL'
            ELSE 'OK' END AS result
FROM information_schema.columns
WHERE table_name = 'crm_project'
  AND column_name IN ('id', 'tenant_id', 'customer_id', 'code', 'project_name', 'stage', 'status', 'is_multi_unit', 'created_at', 'updated_at', 'is_deleted', 'version');

-- ============================================================
-- 14. Flyway迁移记录验证
-- ============================================================
SELECT version, description, script, installed_on, success
FROM flyway_schema_history
WHERE script LIKE '%crm_project%'
ORDER BY installed_rank DESC;

-- ============================================================
-- 15. 外键约束检查（不应存在，应用层维护关联）
-- ============================================================
SELECT 'FK_CHECK' AS check_name,
       CASE WHEN count(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'No foreign key constraints should exist (app-layer relationship)' AS description
FROM pg_constraint c
JOIN pg_class t ON c.conrelid = t.oid
WHERE t.relname = 'crm_project' AND c.contype = 'f';

-- ============================================================
-- 16. 列命名一致性验证（索引引用列名必须与DDL一致）
-- ============================================================
SELECT 'COLUMN_CODE_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'code column must exist (unique index references it)' AS description
FROM information_schema.columns
WHERE table_name = 'crm_project' AND column_name = 'code';

SELECT 'COLUMN_STATUS_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'status column must exist (indexes reference it)' AS description
FROM information_schema.columns
WHERE table_name = 'crm_project' AND column_name = 'status';

SELECT 'COLUMN_CUSTOMER_ID_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'customer_id column must exist (indexes reference it)' AS description
FROM information_schema.columns
WHERE table_name = 'crm_project' AND column_name = 'customer_id';

SELECT 'COLUMN_PROJECT_MANAGER_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'project_manager_id column must exist (indexes reference it)' AS description
FROM information_schema.columns
WHERE table_name = 'crm_project' AND column_name = 'project_manager_id';

SELECT 'COLUMN_STAGE_CHECK' AS check_name,
       CASE WHEN count(*) > 0 THEN 'PASS' ELSE 'FAIL' END AS result,
       'stage column must exist (indexes reference it)' AS description
FROM information_schema.columns
WHERE table_name = 'crm_project' AND column_name = 'stage';

-- ============================================================
-- 17. 扩展字段结构完整性验证
-- ============================================================
SELECT 'EXT_STR_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 10 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 10 ext_str fields' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'crm_project' AND column_name LIKE 'ext_str%';

SELECT 'EXT_NUM_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 5 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 5 ext_num fields' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'crm_project' AND column_name LIKE 'ext_num%';

SELECT 'EXT_DATE_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 3 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 3 ext_date fields' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'crm_project' AND column_name LIKE 'ext_date%';

SELECT 'EXT_BOOL_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 3 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 3 ext_bool fields' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'crm_project' AND column_name LIKE 'ext_bool%';

SELECT 'EXT_JSON_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 1 ext_json field' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'crm_project' AND column_name = 'ext_json';

-- ============================================================
-- 18. decimal(18,8)精度字段计数验证
-- ============================================================
SELECT 'DECIMAL_COUNT_CHECK' AS check_name,
       CASE WHEN count(*) = 8 THEN 'PASS' ELSE 'FAIL' END AS result,
       'Expected 8 decimal(18,8) fields (qty + conversion_rate + base_qty + ext_num1-ext_num5)' AS description,
       count(*) AS actual_count
FROM information_schema.columns
WHERE table_name = 'crm_project'
  AND data_type = 'numeric'
  AND numeric_precision = 18
  AND numeric_scale = 8;
