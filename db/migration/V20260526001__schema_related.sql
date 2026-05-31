-- ============================================================
-- ERP AI 智能管理系统 - 数据库与Schema创建脚本
-- 任务编号: P0-003-001-001-001-001
-- 文件名:   V20260526001__schema_related.sql
-- 说明:     创建ERP系统PostgreSQL数据库，指定UTF8编码、
--           LC_COLLATE/LC_CTYPE区域设置、默认表空间，
--           并创建业务Schema(多租户隔离Schema)
-- 执行方式: 以postgres超级用户身份在psql中执行此脚本
-- ============================================================

-- ============================================================
-- 第1步：创建ERP数据库
-- 注意：CREATE DATABASE不能在事务块中执行，需单独运行
-- 如果数据库已存在则跳过（幂等性保证）
-- ============================================================

-- 检查数据库是否已存在，不存在则创建
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_database WHERE datname = 'erp_db') THEN
        CREATE DATABASE erp_db
            WITH
                ENCODING       = 'UTF8'
                LC_COLLATE     = 'zh_CN.UTF-8'
                LC_CTYPE       = 'zh_CN.UTF-8'
                TEMPLATE       = template0
                TABLESPACE     = pg_default
                CONNECTION LIMIT = 200;
    END IF;
END
$$;

-- 数据库注释（需在创建后单独执行）
-- 如果数据库刚创建，需先断开当前连接再重新连接到erp_db执行以下语句
-- COMMENT ON DATABASE erp_db IS 'ERP AI 智能管理系统主数据库';

-- ============================================================
-- 第2步：创建业务Schema（多租户隔离）
-- 以下语句需在连接到 erp_db 后执行
-- ============================================================

-- 公共Schema：存放系统级配置、字典、参数等跨租户共享数据
CREATE SCHEMA IF NOT EXISTS erp_base;
COMMENT ON SCHEMA erp_base IS '基础数据Schema - 存放系统级配置、字典、参数、编码规则等租户共享的基础数据';

-- 租户业务Schema：存放各租户的业务数据表（约200张表）
CREATE SCHEMA IF NOT EXISTS erp_tenant;
COMMENT ON SCHEMA erp_tenant IS '租户业务数据Schema - 存放各租户的业务数据表，通过tenant_id字段实现行级多租户隔离';

-- ============================================================
-- 第3步：设置Schema搜索路径
-- ============================================================
-- 设置默认搜索路径，优先搜索租户Schema
ALTER DATABASE erp_db SET search_path TO erp_tenant, erp_base, public;

-- ============================================================
-- 第4步：Schema权限说明
-- ============================================================
-- erp_base: 应用服务账号具有读写权限，所有租户共享
-- erp_tenant: 应用服务账号具有读写权限，通过tenant_id行级隔离
-- public: 保留PostgreSQL默认，不存放业务数据

-- ============================================================
-- 验证脚本（可选执行）
-- ============================================================
-- 验证数据库创建
-- SELECT datname, encoding, datcollate, datctype, datconnlimit
-- FROM pg_database WHERE datname = 'erp_db';

-- 验证Schema创建
-- SELECT schema_name FROM information_schema.schemata
-- WHERE schema_name IN ('erp_base', 'erp_tenant');
