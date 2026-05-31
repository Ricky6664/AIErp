-- ============================================================
-- DDL Verification Script
-- Version: V20260526001
-- Description: 验证认证相关表(sys_login_log/sys_oper_log)DDL
-- Author: AI Generated
-- Date: 2026-05-31
-- ============================================================

-- ============================================================
-- 1. 表存在性验证
-- ============================================================
SELECT tablename, schemaname
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename IN ('sys_login_log', 'sys_oper_log')
ORDER BY tablename;
-- 预期结果: 2行（sys_login_log, sys_oper_log）

-- ============================================================
-- 2. sys_login_log 字段定义验证
-- ============================================================
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'sys_login_log'
ORDER BY ordinal_position;
-- 预期: 含10个通用字段(id/tenant_id/created_at/updated_at/created_by/updated_by/is_deleted/owner_dept_id/owner_id/version)
--       + 业务字段(username/login_type/ip_address/user_agent/login_status/fail_reason/login_at/logout_at/session_id)
--       + 扩展字段(ext_str1~10/ext_num1~5/ext_date1~3/ext_bool1~3/ext_json)

-- 2.1 验证通用字段完整性(10个必含字段)
SELECT
    CASE WHEN COUNT(*) = 10 THEN 'PASS: 10 common fields present'
         ELSE 'FAIL: missing ' || (10 - COUNT(*)) || ' common field(s)'
    END AS common_field_check
FROM (
    SELECT column_name
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'sys_login_log'
      AND column_name IN ('id','tenant_id','created_at','updated_at','created_by','updated_by','is_deleted','owner_dept_id','owner_id','version')
) t;

-- 2.2 验证数值字段精度 decimal(18,8)
SELECT column_name, data_type, numeric_precision, numeric_scale
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'sys_login_log'
  AND data_type = 'numeric'
ORDER BY ordinal_position;
-- 预期: ext_num1~5 均为 numeric(18,8)

-- ============================================================
-- 3. sys_oper_log 字段定义验证
-- ============================================================
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'sys_oper_log'
ORDER BY ordinal_position;

-- 3.1 验证通用字段完整性(10个必含字段)
SELECT
    CASE WHEN COUNT(*) = 10 THEN 'PASS: 10 common fields present'
         ELSE 'FAIL: missing ' || (10 - COUNT(*)) || ' common field(s)'
    END AS common_field_check
FROM (
    SELECT column_name
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'sys_oper_log'
      AND column_name IN ('id','tenant_id','created_at','updated_at','created_by','updated_by','is_deleted','owner_dept_id','owner_id','version')
) t;

-- 3.2 验证数值字段精度 decimal(18,8)
SELECT column_name, data_type, numeric_precision, numeric_scale
FROM information_schema.columns
WHERE table_schema = 'public'
  AND table_name = 'sys_oper_log'
  AND data_type = 'numeric'
ORDER BY ordinal_position;

-- ============================================================
-- 4. sys_login_log 索引验证
-- ============================================================
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'sys_login_log'
ORDER BY indexname;
-- 预期: 7个索引(1个PK + 6个业务索引), 所有联合索引起始列为tenant_id

-- 4.1 验证联合索引以tenant_id为首列
SELECT indexname,
       CASE WHEN indexdef ~ '\(tenant_id' THEN 'PASS'
            ELSE 'FAIL: index does not start with tenant_id'
       END AS tenant_first_check
FROM pg_indexes
WHERE tablename = 'sys_login_log'
  AND indexname LIKE 'idx_%';

-- ============================================================
-- 5. sys_oper_log 索引验证
-- ============================================================
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'sys_oper_log'
ORDER BY indexname;
-- 预期: 7个索引(1个PK + 6个业务索引), 所有联合索引起始列为tenant_id

-- 5.1 验证联合索引以tenant_id为首列
SELECT indexname,
       CASE WHEN indexdef ~ '\(tenant_id' THEN 'PASS'
            ELSE 'FAIL: index does not start with tenant_id'
       END AS tenant_first_check
FROM pg_indexes
WHERE tablename = 'sys_oper_log'
  AND indexname LIKE 'idx_%';

-- ============================================================
-- 6. COMMENT完整性验证
-- ============================================================
-- 6.1 表注释验证
SELECT
    c.relname AS table_name,
    obj_description(c.oid, 'pg_class') AS table_comment,
    CASE WHEN obj_description(c.oid, 'pg_class') IS NOT NULL THEN 'PASS'
         ELSE 'FAIL: no table comment'
    END AS table_comment_check
FROM pg_class c
JOIN pg_namespace n ON n.oid = c.relnamespace
WHERE c.relkind = 'r'
  AND n.nspname = 'public'
  AND c.relname IN ('sys_login_log', 'sys_oper_log');

-- 6.2 字段注释覆盖率
SELECT
    c.relname AS table_name,
    COUNT(a.attname) AS total_columns,
    COUNT(d.description) AS columns_with_comment,
    CASE WHEN COUNT(a.attname) = COUNT(d.description) THEN 'PASS: 100% comment coverage'
         ELSE 'FAIL: ' || (COUNT(a.attname) - COUNT(d.description)) || ' column(s) missing comment'
    END AS comment_coverage_check
FROM pg_class c
JOIN pg_namespace n ON n.oid = c.relnamespace
JOIN pg_attribute a ON a.attrelid = c.oid
LEFT JOIN pg_description d ON d.objoid = c.oid AND d.objsubid = a.attnum
WHERE c.relkind = 'r'
  AND n.nspname = 'public'
  AND c.relname IN ('sys_login_log', 'sys_oper_log')
  AND a.attnum > 0
  AND a.attisdropped = false
GROUP BY c.relname;

-- ============================================================
-- 7. Flyway迁移历史验证
-- ============================================================
SELECT version, description, script, installed_on, success
FROM flyway_schema_history
WHERE version IN ('20260531006', '20260531007')
ORDER BY installed_rank DESC;
-- 预期: 2条记录, success均为true

-- ============================================================
-- 8. 无外键约束验证
-- ============================================================
SELECT
    tc.table_name,
    tc.constraint_name,
    tc.constraint_type,
    CASE WHEN tc.constraint_type = 'FOREIGN KEY' THEN 'FAIL: foreign key found'
         ELSE 'PASS'
    END AS fk_check
FROM information_schema.table_constraints tc
WHERE tc.table_schema = 'public'
  AND tc.table_name IN ('sys_login_log', 'sys_oper_log')
  AND tc.constraint_type = 'FOREIGN KEY';
-- 预期: 0行(无外键约束)

-- ============================================================
-- 9. 主键验证
-- ============================================================
SELECT
    kcu.table_name,
    kcu.column_name,
    c.data_type,
    CASE WHEN kcu.column_name = 'id' AND c.data_type = 'bigint' THEN 'PASS'
         ELSE 'FAIL'
    END AS pk_check
FROM information_schema.key_column_usage kcu
JOIN information_schema.columns c
    ON c.table_name = kcu.table_name AND c.column_name = kcu.column_name
WHERE kcu.table_schema = 'public'
  AND kcu.table_name IN ('sys_login_log', 'sys_oper_log')
  AND kcu.constraint_name LIKE '%pkey%';
-- 预期: 2行, 均为id bigint主键
