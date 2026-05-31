-- ============================================================
-- ERP AI 智能管理系统 - DDL验证查询脚本
-- 任务编号: P0-003-001-001-001-002
-- 文件名:   V20260526001__verify_chema.sql
-- 说明:     验证P0-003-001-001-001-001创建的数据库和Schema
--           对象是否存在、配置是否正确
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
-- 验证完成
-- ============================================================
