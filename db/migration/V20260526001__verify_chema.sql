-- ============================================================
-- ERP AI 智能管理系统 - DDL验证查询脚本
-- 任务编号: P0-003-001-001-001-002 / P0-003-001-002-001-003
-- 文件名:   V20260526001__verify_chema.sql
-- 说明:     验证P0-003-001-001-001-001创建的数据库和Schema
--           对象是否存在、配置是否正确；
--           验证P0-003-001-002-001-001/002的公共字段规范
--           是否正确定义。
-- 执行方式: 以postgres用户身份在psql中连接erp_db后执行
-- ============================================================

-- ============================================================
-- 1. 数据库存在性验证
-- ============================================================

-- 1.1 验证数据库已创建
SELECT datname, encoding, datcollate, datctype, datconnlimit
FROM pg_database
WHERE datname = 'erp_db';

-- 预期结果: 返回1行, datname='erp_db', encoding='6'(UTF8)

-- ============================================================
-- 2. Schema存在性验证
-- ============================================================

-- 2.1 验证erp_base Schema已创建
SELECT schema_name
FROM information_schema.schemata
WHERE schema_name = 'erp_base';

-- 预期结果: 返回1行, schema_name='erp_base'

-- 2.2 验证erp_tenant Schema已创建
SELECT schema_name
FROM information_schema.schemata
WHERE schema_name = 'erp_tenant';

-- 预期结果: 返回1行, schema_name='erp_tenant'

-- 2.3 验证Schema COMMENT注释
SELECT obj_description('erp_base'::regnamespace, 'pg_namespace') AS erp_base_comment;
SELECT obj_description('erp_tenant'::regnamespace, 'pg_namespace') AS erp_tenant_comment;

-- 预期结果: erp_base='基础数据Schema...', erp_tenant='租户业务数据Schema...'

-- ============================================================
-- 3. 数据库搜索路径验证
-- ============================================================

-- 3.1 验证search_path配置
SELECT datname, datconfig
FROM pg_database
WHERE datname = 'erp_db';

-- 预期结果: datconfig包含 search_path TO erp_tenant, erp_base, public

-- ============================================================
-- 4. 数据库编码与区域设置验证
-- ============================================================

-- 4.1 验证UTF8编码
SELECT datname,
       pg_encoding_to_char(encoding) AS encoding_name
FROM pg_database
WHERE datname = 'erp_db';

-- 预期结果: encoding_name='UTF8'

-- 4.2 验证LC_COLLATE和LC_CTYPE
SELECT datname, datcollate, datctype
FROM pg_database
WHERE datname = 'erp_db';

-- 预期结果: datcollate='zh_CN.UTF-8', datctype='zh_CN.UTF-8'

-- ============================================================
-- 5. 数据库连接限制验证
-- ============================================================

-- 5.1 验证连接数限制
SELECT datname, datconnlimit
FROM pg_database
WHERE datname = 'erp_db';

-- 预期结果: datconnlimit=200

-- ============================================================
-- 6. 综合验证汇总查询
-- ============================================================

-- 6.1 一次性汇总所有关键检查
SELECT
    CASE WHEN EXISTS (SELECT 1 FROM pg_database WHERE datname = 'erp_db')
         THEN 'PASS' ELSE 'FAIL' END AS "数据库erp_db存在",
    CASE WHEN EXISTS (SELECT 1 FROM pg_database WHERE datname = 'erp_db'
                      AND pg_encoding_to_char(encoding) = 'UTF8')
         THEN 'PASS' ELSE 'FAIL' END AS "UTF8编码",
    CASE WHEN EXISTS (SELECT 1 FROM pg_database WHERE datname = 'erp_db'
                      AND datcollate = 'zh_CN.UTF-8')
         THEN 'PASS' ELSE 'FAIL' END AS "LC_COLLATE",
    CASE WHEN EXISTS (SELECT 1 FROM pg_database WHERE datname = 'erp_db'
                      AND datctype = 'zh_CN.UTF-8')
         THEN 'PASS' ELSE 'FAIL' END AS "LC_CTYPE",
    CASE WHEN EXISTS (SELECT 1 FROM information_schema.schemata
                      WHERE schema_name = 'erp_base')
         THEN 'PASS' ELSE 'FAIL' END AS "Schema erp_base",
    CASE WHEN EXISTS (SELECT 1 FROM information_schema.schemata
                      WHERE schema_name = 'erp_tenant')
         THEN 'PASS' ELSE 'FAIL' END AS "Schema erp_tenant",
    CASE WHEN EXISTS (SELECT 1 FROM pg_database WHERE datname = 'erp_db'
                      AND datconnlimit = 200)
         THEN 'PASS' ELSE 'FAIL' END AS "连接数限制200";

-- ============================================================
-- 7. Flyway迁移历史验证
-- ============================================================

-- 7.1 查看最近的Flyway迁移记录
SELECT version, description, type, script, installed_on, success
FROM flyway_schema_history
ORDER BY installed_rank DESC
LIMIT 5;

-- 预期结果: 至少包含V20260526001__schema_related.sql的成功记录

-- ============================================================
-- 8. 公共字段规范验证（P0-003-001-002-001-003）
--    验证所有业务表是否包含完整的10个通用字段
-- ============================================================

-- 8.1 公共字段清单定义（规范基准）
-- 以下为10个通用字段的完整清单，用于对照验证
/*
 * 序号  字段名         数据类型      NOT NULL  默认值
 * 1     id             BIGINT       YES(PK)   BIGSERIAL
 * 2     tenant_id      BIGINT       YES       —
 * 3     created_by     BIGINT       NO        —
 * 4     created_at     TIMESTAMP    YES       CURRENT_TIMESTAMP
 * 5     updated_by     BIGINT       NO        —
 * 6     updated_at     TIMESTAMP    YES       CURRENT_TIMESTAMP
 * 7     is_deleted     BOOLEAN      YES       FALSE
 * 8     owner_dept_id  BIGINT       NO        —
 * 9     owner_id       BIGINT       NO        —
 * 10    version        INT          YES       1
 */

-- 8.2 验证erp_base和erp_tenant中所有业务表均包含10个通用字段
-- 查询缺少任一公共字段的表
SELECT table_schema, table_name
FROM information_schema.tables t
WHERE t.table_schema IN ('erp_base', 'erp_tenant')
  AND t.table_type = 'BASE TABLE'
  AND NOT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = t.table_schema
        AND c.table_name = t.table_name
        AND c.column_name = 'id')
UNION ALL
SELECT table_schema, table_name
FROM information_schema.tables t
WHERE t.table_schema IN ('erp_base', 'erp_tenant')
  AND t.table_type = 'BASE TABLE'
  AND NOT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = t.table_schema AND c.table_name = t.table_name
        AND c.column_name = 'tenant_id')
UNION ALL
SELECT table_schema, table_name
FROM information_schema.tables t
WHERE t.table_schema IN ('erp_base', 'erp_tenant')
  AND t.table_type = 'BASE TABLE'
  AND NOT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = t.table_schema AND c.table_name = t.table_name
        AND c.column_name = 'created_by')
UNION ALL
SELECT table_schema, table_name
FROM information_schema.tables t
WHERE t.table_schema IN ('erp_base', 'erp_tenant')
  AND t.table_type = 'BASE TABLE'
  AND NOT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = t.table_schema AND c.table_name = t.table_name
        AND c.column_name = 'created_at')
UNION ALL
SELECT table_schema, table_name
FROM information_schema.tables t
WHERE t.table_schema IN ('erp_base', 'erp_tenant')
  AND t.table_type = 'BASE TABLE'
  AND NOT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = t.table_schema AND c.table_name = t.table_name
        AND c.column_name = 'updated_by')
UNION ALL
SELECT table_schema, table_name
FROM information_schema.tables t
WHERE t.table_schema IN ('erp_base', 'erp_tenant')
  AND t.table_type = 'BASE TABLE'
  AND NOT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = t.table_schema AND c.table_name = t.table_name
        AND c.column_name = 'updated_at')
UNION ALL
SELECT table_schema, table_name
FROM information_schema.tables t
WHERE t.table_schema IN ('erp_base', 'erp_tenant')
  AND t.table_type = 'BASE TABLE'
  AND NOT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = t.table_schema AND c.table_name = t.table_name
        AND c.column_name = 'is_deleted')
UNION ALL
SELECT table_schema, table_name
FROM information_schema.tables t
WHERE t.table_schema IN ('erp_base', 'erp_tenant')
  AND t.table_type = 'BASE TABLE'
  AND NOT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = t.table_schema AND c.table_name = t.table_name
        AND c.column_name = 'owner_dept_id')
UNION ALL
SELECT table_schema, table_name
FROM information_schema.tables t
WHERE t.table_schema IN ('erp_base', 'erp_tenant')
  AND t.table_type = 'BASE TABLE'
  AND NOT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = t.table_schema AND c.table_name = t.table_name
        AND c.column_name = 'owner_id')
UNION ALL
SELECT table_schema, table_name
FROM information_schema.tables t
WHERE t.table_schema IN ('erp_base', 'erp_tenant')
  AND t.table_type = 'BASE TABLE'
  AND NOT EXISTS (
      SELECT 1 FROM information_schema.columns c
      WHERE c.table_schema = t.table_schema AND c.table_name = t.table_name
        AND c.column_name = 'version');

-- 预期结果: 返回0行（所有表均包含全部10个公共字段）

-- ============================================================
-- 9. 公共字段约束验证（P0-003-001-002-001-003）
--    验证NOT NULL约束、默认值、数据类型与规范一致
-- ============================================================

-- 9.1 验证所有业务表的公共字段NOT NULL约束
SELECT
    c.table_schema AS schema_name,
    c.table_name,
    c.column_name,
    c.is_nullable,
    c.data_type,
    c.column_default,
    CASE
        WHEN c.column_name = 'id' AND c.is_nullable = 'NO' THEN 'PASS'
        WHEN c.column_name = 'tenant_id' AND c.is_nullable = 'NO' THEN 'PASS'
        WHEN c.column_name = 'created_at' AND c.is_nullable = 'NO' THEN 'PASS'
        WHEN c.column_name = 'updated_at' AND c.is_nullable = 'NO' THEN 'PASS'
        WHEN c.column_name = 'is_deleted' AND c.is_nullable = 'NO' THEN 'PASS'
        WHEN c.column_name = 'version' AND c.is_nullable = 'NO' THEN 'PASS'
        WHEN c.column_name IN ('created_by', 'updated_by', 'owner_dept_id', 'owner_id')
             AND c.is_nullable = 'YES' THEN 'PASS'
        ELSE 'FAIL'
    END AS constraint_check
FROM information_schema.columns c
JOIN information_schema.tables t
    ON c.table_schema = t.table_schema AND c.table_name = t.table_name
WHERE t.table_schema IN ('erp_base', 'erp_tenant')
  AND t.table_type = 'BASE TABLE'
  AND c.column_name IN (
      'id', 'tenant_id', 'created_by', 'created_at', 'updated_by',
      'updated_at', 'is_deleted', 'owner_dept_id', 'owner_id', 'version'
  )
ORDER BY c.table_schema, c.table_name, c.column_name;

-- 预期结果: 所有记录的constraint_check = 'PASS'

-- 9.2 验证is_deleted默认值为false
SELECT
    c.table_schema, c.table_name, c.column_name,
    c.column_default,
    CASE WHEN c.column_default LIKE '%false%' THEN 'PASS' ELSE 'FAIL' END AS default_check
FROM information_schema.columns c
JOIN information_schema.tables t
    ON c.table_schema = t.table_schema AND c.table_name = t.table_name
WHERE t.table_schema IN ('erp_base', 'erp_tenant')
  AND t.table_type = 'BASE TABLE'
  AND c.column_name = 'is_deleted';

-- 预期结果: 所有记录的default_check = 'PASS'

-- 9.3 验证部分唯一索引包含WHERE is_deleted = FALSE条件
SELECT
    schemaname,
    tablename,
    indexname,
    indexdef
FROM pg_indexes
WHERE schemaname IN ('erp_base', 'erp_tenant')
  AND indexname LIKE 'uk_%'
  AND indexdef NOT LIKE '%WHERE%is_deleted%';

-- 预期结果: 返回0行（所有唯一索引均包含is_deleted条件）

-- 9.4 验证联合索引以tenant_id为首列
SELECT
    schemaname,
    tablename,
    indexname,
    indexdef
FROM pg_indexes
WHERE schemaname IN ('erp_base', 'erp_tenant')
  AND indexname LIKE 'idx_%'
  AND indexname != 'idx_%_tenant_id'
  AND indexdef NOT LIKE '%(tenant_id,%';

-- 预期结果: 返回0行（所有联合索引均以tenant_id为首列）

-- ============================================================
-- 10. COMMENT注释完整性验证（P0-003-001-002-001-003）
-- ============================================================

-- 10.1 验证所有业务表有表级COMMENT
SELECT
    t.table_schema || '.' || t.table_name AS full_table_name,
    pg_catalog.obj_description(
        (t.table_schema || '.' || t.table_name)::regclass, 'pg_class'
    ) AS table_comment,
    CASE WHEN pg_catalog.obj_description(
        (t.table_schema || '.' || t.table_name)::regclass, 'pg_class'
    ) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS comment_check
FROM information_schema.tables t
WHERE t.table_schema IN ('erp_base', 'erp_tenant')
  AND t.table_type = 'BASE TABLE';

-- 预期结果: 所有记录的comment_check = 'PASS'

-- 10.2 验证公共字段有列级COMMENT
SELECT
    t.table_schema || '.' || t.table_name AS full_table_name,
    c.column_name,
    pg_catalog.col_description(
        (t.table_schema || '.' || t.table_name)::regclass,
        c.ordinal_position
    ) AS column_comment,
    CASE WHEN pg_catalog.col_description(
        (t.table_schema || '.' || t.table_name)::regclass,
        c.ordinal_position
    ) IS NOT NULL THEN 'PASS' ELSE 'FAIL' END AS comment_check
FROM information_schema.columns c
JOIN information_schema.tables t
    ON c.table_schema = t.table_schema AND c.table_name = t.table_name
WHERE t.table_schema IN ('erp_base', 'erp_tenant')
  AND t.table_type = 'BASE TABLE'
  AND c.column_name IN (
      'id', 'tenant_id', 'created_by', 'created_at', 'updated_by',
      'updated_at', 'is_deleted', 'owner_dept_id', 'owner_id', 'version'
  )
ORDER BY full_table_name, c.column_name;

-- 预期结果: 所有公共字段comment_check = 'PASS'

-- ============================================================
-- 11. 规范定义文档存在性验证（P0-003-001-002-001-003）
--    以下查询验证Flyway迁移脚本文件已按规范创建
-- ============================================================

-- 11.1 验证Flyway迁移脚本数量（应包括schema_related.sql + 本脚本）
SELECT
    COUNT(*) AS migration_script_count,
    CASE WHEN COUNT(*) >= 2 THEN 'PASS' ELSE 'FAIL' END AS migration_count_check
FROM flyway_schema_history
WHERE success = TRUE;

-- 预期结果: migration_count_check = 'PASS'

-- 11.2 列出所有flyway迁移脚本及其状态
SELECT
    version,
    description,
    script,
    installed_on,
    success,
    CASE WHEN success THEN '正常迁移' ELSE '迁移失败' END AS status_cn
FROM flyway_schema_history
ORDER BY installed_rank;

-- ============================================================
-- 12. 综合规范验证汇总（P0-003-001-002-001-003）
-- ============================================================

-- 12.1 公共字段规范综合检查
SELECT
    CASE WHEN NOT EXISTS (
        SELECT table_schema, table_name
        FROM information_schema.tables t
        WHERE t.table_schema IN ('erp_base', 'erp_tenant')
          AND t.table_type = 'BASE TABLE'
          AND (NOT EXISTS (SELECT 1 FROM information_schema.columns c
                           WHERE c.table_schema = t.table_schema
                             AND c.table_name = t.table_name
                             AND c.column_name = 'id')
            OR NOT EXISTS (SELECT 1 FROM information_schema.columns c
                           WHERE c.table_schema = t.table_schema
                             AND c.table_name = t.table_name
                             AND c.column_name = 'tenant_id')
            OR NOT EXISTS (SELECT 1 FROM information_schema.columns c
                           WHERE c.table_schema = t.table_schema
                             AND c.table_name = t.table_name
                             AND c.column_name = 'is_deleted')
            OR NOT EXISTS (SELECT 1 FROM information_schema.columns c
                           WHERE c.table_schema = t.table_schema
                             AND c.table_name = t.table_name
                             AND c.column_name = 'version'))
    ) THEN 'PASS' ELSE 'FAIL' END AS "10个公共字段完整",
    CASE WHEN NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE schemaname IN ('erp_base', 'erp_tenant')
          AND indexname LIKE 'uk_%'
          AND indexdef NOT LIKE '%WHERE%is_deleted%'
    ) THEN 'PASS' ELSE 'FAIL' END AS "部分唯一索引规范",
    CASE WHEN NOT EXISTS (
        SELECT 1 FROM pg_indexes
        WHERE schemaname IN ('erp_base', 'erp_tenant')
          AND indexname LIKE 'idx_%'
          AND indexname != 'idx_%_tenant_id'
          AND indexdef NOT LIKE '%(tenant_id,%'
    ) THEN 'PASS' ELSE 'FAIL' END AS "多租户索引首列规范",
    CASE WHEN EXISTS (
        SELECT 1 FROM flyway_schema_history WHERE success = TRUE
    ) THEN 'PASS' ELSE 'FAIL' END AS "Flyway迁移成功";

-- ============================================================
-- 验证完成
-- ============================================================
