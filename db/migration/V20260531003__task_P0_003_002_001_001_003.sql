-- ============================================================
-- ERP AI 系统 - 公共字段基座规范验证脚本
-- 任务编号: P0-003-002-001-001-003
-- 文件名:   V20260531003__task_P0_003_002_001_001_003.sql
-- 说明:     验证 P0-003-002-001-001-001（公共字段DDL）和
--           P0-003-002-001-001-002（默认值与约束）的执行结果，
--           包括表结构完整性、字段定义正确性、约束合规性、
--           COMMENT注释完整性和Flyway迁移状态。
-- 执行方式: 连接到 erp_dev 后以应用账号执行
-- 数据库:   PostgreSQL 15+
-- 作者:     AI Generated
-- 日期:     2026-05-31
-- ============================================================

-- ============================================================
-- 第1步：验证规范记录表 erp_base.public_field_spec
-- ============================================================

-- 1.1 验证表存在
SELECT CASE WHEN EXISTS (
    SELECT 1 FROM pg_catalog.pg_tables
    WHERE schemaname = 'erp_base' AND tablename = 'public_field_spec'
) THEN 'PASS' ELSE 'FAIL' END AS "public_field_spec表存在";

-- 1.2 验证表字段定义
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_schema = 'erp_base' AND table_name = 'public_field_spec'
ORDER BY ordinal_position;

-- 预期: 返回8列 (field_seq, field_name, data_type, default_value,
--        is_not_null, is_primary_key, field_comment, design_note, sort_order)

-- 1.3 验证主键约束
SELECT
    kcu.column_name AS pk_column,
    CASE WHEN kcu.column_name = 'field_name' THEN 'PASS' ELSE 'FAIL' END AS pk_check
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kcu
    ON tc.constraint_name = kcu.constraint_name
    AND tc.table_schema = kcu.table_schema
WHERE tc.constraint_type = 'PRIMARY KEY'
  AND tc.table_schema = 'erp_base'
  AND tc.table_name = 'public_field_spec';

-- 1.4 验证10个字段数据完整
SELECT
    COUNT(*) AS field_count,
    CASE WHEN COUNT(*) = 10 THEN 'PASS' ELSE 'FAIL' END AS count_check
FROM erp_base.public_field_spec;

-- 预期: field_count = 10

-- 1.5 验证10个必含字段名
SELECT field_seq, field_name, data_type, is_not_null, is_primary_key, field_comment
FROM erp_base.public_field_spec
ORDER BY sort_order;

-- 预期: 返回10行，包含 id/tenant_id/created_by/created_at/updated_by/
--        updated_at/is_deleted/owner_dept_id/owner_id/version

-- ============================================================
-- 第2步：验证默认值规范表 erp_base.public_default_value_spec
-- ============================================================

-- 2.1 验证表存在
SELECT CASE WHEN EXISTS (
    SELECT 1 FROM pg_catalog.pg_tables
    WHERE schemaname = 'erp_base' AND tablename = 'public_default_value_spec'
) THEN 'PASS' ELSE 'FAIL' END AS "public_default_value_spec表存在";

-- 2.2 验证字段定义
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_schema = 'erp_base' AND table_name = 'public_default_value_spec'
ORDER BY ordinal_position;

-- 预期: 5列 (field_name, default_expression, not_null_constraint,
--        default_strategy, usage_note, sort_order)

-- 2.3 验证10个字段数据完整
SELECT
    COUNT(*) AS field_count,
    CASE WHEN COUNT(*) = 10 THEN 'PASS' ELSE 'FAIL' END AS count_check
FROM erp_base.public_default_value_spec;

-- 预期: field_count = 10

-- 2.4 验证NOT NULL字段的默认值策略
SELECT
    field_name,
    not_null_constraint,
    default_expression,
    CASE
        WHEN field_name IN ('id', 'tenant_id', 'created_at', 'updated_at', 'is_deleted', 'version')
             AND not_null_constraint = TRUE THEN 'PASS'
        WHEN field_name IN ('created_by', 'updated_by', 'owner_dept_id', 'owner_id')
             AND not_null_constraint = FALSE THEN 'PASS'
        ELSE 'FAIL'
    END AS not_null_check
FROM erp_base.public_default_value_spec
ORDER BY sort_order;

-- 预期: 所有 not_null_check = 'PASS'

-- ============================================================
-- 第3步：验证约束规范表 erp_base.public_constraint_spec
-- ============================================================

-- 3.1 验证表存在
SELECT CASE WHEN EXISTS (
    SELECT 1 FROM pg_catalog.pg_tables
    WHERE schemaname = 'erp_base' AND tablename = 'public_constraint_spec'
) THEN 'PASS' ELSE 'FAIL' END AS "public_constraint_spec表存在";

-- 3.2 验证字段定义
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_schema = 'erp_base' AND table_name = 'public_constraint_spec'
ORDER BY ordinal_position;

-- 预期: 7+ 列 (constraint_code, constraint_type, constraint_name,
--        applies_to_fields, constraint_sql_template, is_mandatory,
--        design_rationale, sort_order)

-- 3.3 验证10条约束数据完整
SELECT
    COUNT(*) AS constraint_count,
    CASE WHEN COUNT(*) = 10 THEN 'PASS' ELSE 'FAIL' END AS count_check
FROM erp_base.public_constraint_spec;

-- 预期: constraint_count = 10

-- 3.4 验证约束编码唯一性
SELECT
    constraint_code,
    COUNT(*) AS dup_count,
    CASE WHEN COUNT(*) = 1 THEN 'PASS' ELSE 'FAIL' END AS unique_check
FROM erp_base.public_constraint_spec
GROUP BY constraint_code
HAVING COUNT(*) > 1;

-- 预期: 返回0行

-- 3.5 验证强制约束(is_mandatory=TRUE)完整性
SELECT
    constraint_code,
    constraint_name,
    is_mandatory,
    CASE WHEN is_mandatory = TRUE THEN 'PASS' ELSE 'FAIL' END AS mandatory_check
FROM erp_base.public_constraint_spec
ORDER BY sort_order;

-- 预期: 所有 mandatory_check = 'PASS'

-- ============================================================
-- 第4步：验证表级和列级COMMENT注释完整性
-- ============================================================

-- 4.1 验证表级COMMENT
SELECT
    tbl.table_name,
    pg_catalog.obj_description(
        (tbl.table_schema || '.' || tbl.table_name)::regclass, 'pg_class'
    ) AS table_comment,
    CASE WHEN pg_catalog.obj_description(
        (tbl.table_schema || '.' || tbl.table_name)::regclass, 'pg_class'
    ) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS table_comment_check
FROM (
    VALUES
        ('erp_base', 'public_field_spec'),
        ('erp_base', 'public_default_value_spec'),
        ('erp_base', 'public_constraint_spec')
) AS tbl(table_schema, table_name);

-- 预期: 所有 table_comment_check = 'PASS'

-- 4.2 验证public_field_spec列级COMMENT
SELECT
    c.column_name,
    pg_catalog.col_description(
        'erp_base.public_field_spec'::regclass, c.ordinal_position
    ) AS column_comment,
    CASE WHEN pg_catalog.col_description(
        'erp_base.public_field_spec'::regclass, c.ordinal_position
    ) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS col_comment_check
FROM information_schema.columns c
WHERE c.table_schema = 'erp_base'
  AND c.table_name = 'public_field_spec'
ORDER BY c.ordinal_position;

-- 预期: 所有 col_comment_check = 'PASS'

-- 4.3 验证public_default_value_spec列级COMMENT
SELECT
    c.column_name,
    pg_catalog.col_description(
        'erp_base.public_default_value_spec'::regclass, c.ordinal_position
    ) AS column_comment,
    CASE WHEN pg_catalog.col_description(
        'erp_base.public_default_value_spec'::regclass, c.ordinal_position
    ) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS col_comment_check
FROM information_schema.columns c
WHERE c.table_schema = 'erp_base'
  AND c.table_name = 'public_default_value_spec'
ORDER BY c.ordinal_position;

-- 预期: 所有 col_comment_check = 'PASS'

-- 4.4 验证public_constraint_spec列级COMMENT
SELECT
    c.column_name,
    pg_catalog.col_description(
        'erp_base.public_constraint_spec'::regclass, c.ordinal_position
    ) AS column_comment,
    CASE WHEN pg_catalog.col_description(
        'erp_base.public_constraint_spec'::regclass, c.ordinal_position
    ) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS col_comment_check
FROM information_schema.columns c
WHERE c.table_schema = 'erp_base'
  AND c.table_name = 'public_constraint_spec'
ORDER BY c.ordinal_position;

-- 预期: 所有 col_comment_check = 'PASS'

-- ============================================================
-- 第5步：验证PL/pgSQL校验函数
-- ============================================================

-- 5.1 验证 fn_validate_common_fields 函数存在
SELECT CASE WHEN EXISTS (
    SELECT 1 FROM pg_catalog.pg_proc p
    JOIN pg_catalog.pg_namespace n ON p.pronamespace = n.oid
    WHERE n.nspname = 'erp_base'
      AND p.proname = 'fn_validate_common_fields'
      AND pg_catalog.pg_get_function_arguments(p.oid) = 'p_schema_name character varying, p_table_name character varying'
) THEN 'PASS' ELSE 'FAIL' END AS "fn_validate_common_fields存在";

-- 5.2 验证 fn_batch_validate_schema 函数存在
SELECT CASE WHEN EXISTS (
    SELECT 1 FROM pg_catalog.pg_proc p
    JOIN pg_catalog.pg_namespace n ON p.pronamespace = n.oid
    WHERE n.nspname = 'erp_base'
      AND p.proname = 'fn_batch_validate_schema'
      AND pg_catalog.pg_get_function_arguments(p.oid) = 'p_schema_name character varying'
) THEN 'PASS' ELSE 'FAIL' END AS "fn_batch_validate_schema存在";

-- 5.3 验证函数COMMENT
SELECT
    p.proname AS function_name,
    pg_catalog.obj_description(p.oid, 'pg_proc') AS function_comment,
    CASE WHEN pg_catalog.obj_description(p.oid, 'pg_proc') IS NOT NULL
         THEN 'PASS' ELSE 'FAIL' END AS func_comment_check
FROM pg_catalog.pg_proc p
JOIN pg_catalog.pg_namespace n ON p.pronamespace = n.oid
WHERE n.nspname = 'erp_base'
  AND p.proname IN ('fn_validate_common_fields', 'fn_batch_validate_schema')
ORDER BY p.proname;

-- 预期: 所有 func_comment_check = 'PASS'

-- 5.4 实际调用校验函数进行自验证
-- 用规范表自身作为测试目标
SELECT erp_base.fn_validate_common_fields('erp_base', 'public_field_spec') AS validation_result;

-- 预期: overall_pass = true (规范表自身应符合规范)

-- ============================================================
-- 第6步：验证Flyway迁移历史
-- ============================================================

-- 6.1 查看所有迁移记录
SELECT
    installed_rank,
    version,
    description,
    script,
    checksum,
    installed_on,
    success,
    CASE WHEN success THEN 'PASS' ELSE 'FAIL' END AS migration_status
FROM flyway_schema_history
ORDER BY installed_rank;

-- 6.2 验证本模块迁移脚本数量
SELECT
    COUNT(*) AS migration_count,
    CASE WHEN COUNT(*) >= 2 THEN 'PASS' ELSE 'FAIL' END AS count_check
FROM flyway_schema_history
WHERE version IN ('20260531001', '20260531002', '20260531003')
  AND success = TRUE;

-- 预期: count >= 2 (至少包含前序两个任务脚本)

-- 6.3 验证无失败迁移
SELECT
    COUNT(*) AS failed_count,
    CASE WHEN COUNT(*) = 0 THEN 'PASS' ELSE 'FAIL' END AS no_failure_check
FROM flyway_schema_history
WHERE success = FALSE;

-- 预期: failed_count = 0

-- 6.4 验证无版本号冲突
SELECT
    version,
    COUNT(*) AS version_count,
    CASE WHEN COUNT(*) > 1 THEN 'FAIL' ELSE 'PASS' END AS version_conflict_check
FROM flyway_schema_history
GROUP BY version
HAVING COUNT(*) > 1;

-- 预期: 返回0行

-- ============================================================
-- 第7步：综合验证汇总
-- ============================================================

SELECT
    '===== P0-003-002-001-001-003 规范验证汇总 =====' AS header,
    CASE WHEN EXISTS (
        SELECT 1 FROM pg_catalog.pg_tables
        WHERE schemaname = 'erp_base' AND tablename = 'public_field_spec'
    ) THEN 'PASS' ELSE 'FAIL' END AS "1.public_field_spec表存在",
    CASE WHEN (SELECT COUNT(*) FROM erp_base.public_field_spec) = 10
         THEN 'PASS' ELSE 'FAIL' END AS "2.通用字段10个完整",
    CASE WHEN EXISTS (
        SELECT 1 FROM pg_catalog.pg_tables
        WHERE schemaname = 'erp_base' AND tablename = 'public_default_value_spec'
    ) THEN 'PASS' ELSE 'FAIL' END AS "3.public_default_value_spec表存在",
    CASE WHEN (SELECT COUNT(*) FROM erp_base.public_default_value_spec) = 10
         THEN 'PASS' ELSE 'FAIL' END AS "4.默认值规范10条完整",
    CASE WHEN EXISTS (
        SELECT 1 FROM pg_catalog.pg_tables
        WHERE schemaname = 'erp_base' AND tablename = 'public_constraint_spec'
    ) THEN 'PASS' ELSE 'FAIL' END AS "5.public_constraint_spec表存在",
    CASE WHEN (SELECT COUNT(*) FROM erp_base.public_constraint_spec) = 10
         THEN 'PASS' ELSE 'FAIL' END AS "6.约束规范10条完整",
    CASE WHEN EXISTS (
        SELECT 1 FROM pg_catalog.pg_proc p
        JOIN pg_catalog.pg_namespace n ON p.pronamespace = n.oid
        WHERE n.nspname = 'erp_base' AND p.proname = 'fn_validate_common_fields'
    ) THEN 'PASS' ELSE 'FAIL' END AS "7.校验函数fn_validate存在",
    CASE WHEN EXISTS (
        SELECT 1 FROM pg_catalog.pg_proc p
        JOIN pg_catalog.pg_namespace n ON p.pronamespace = n.oid
        WHERE n.nspname = 'erp_base' AND p.proname = 'fn_batch_validate_schema'
    ) THEN 'PASS' ELSE 'FAIL' END AS "8.批量校验函数fn_batch存在",
    CASE WHEN NOT EXISTS (
        SELECT 1 FROM flyway_schema_history WHERE success = FALSE
    ) THEN 'PASS' ELSE 'FAIL' END AS "9.无失败Flyway迁移",
    CASE WHEN NOT EXISTS (
        SELECT version FROM flyway_schema_history
        GROUP BY version HAVING COUNT(*) > 1
    ) THEN 'PASS' ELSE 'FAIL' END AS "10.无版本号冲突";

-- ============================================================
-- 验证完成
-- ============================================================
