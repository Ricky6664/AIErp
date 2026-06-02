-- ============================================================
-- Flyway Migration Script
-- Version: V20260526001
-- Description: inv_transfer_detail调拨主从表DDL验证查询脚本
-- Author: AI
-- Date: 2026-06-02
-- Task: P0-003-007-008-001-003
-- ============================================================

-- ============================================================
-- 1. 表存在性验证
-- ============================================================

-- 1.1 确认 inv_transfer 调拨主表已创建
SELECT COUNT(*) AS inv_transfer_table_exists
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'inv_transfer';
-- EXPECT: 1

-- 1.2 确认 inv_transfer_detail 调拨从表已创建
SELECT COUNT(*) AS inv_transfer_detail_table_exists
FROM pg_tables
WHERE schemaname = 'public' AND tablename = 'inv_transfer_detail';
-- EXPECT: 1

-- ============================================================
-- 2. 字段定义验证
-- ============================================================

-- 2.1 inv_transfer 主表字段定义
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_name = 'inv_transfer'
ORDER BY ordinal_position;

-- 2.2 inv_transfer_detail 从表字段定义
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_name = 'inv_transfer_detail'
ORDER BY ordinal_position;

-- ============================================================
-- 3. 索引验证
-- ============================================================

-- 3.1 inv_transfer 主表索引
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'inv_transfer'
ORDER BY indexname;

-- 3.2 inv_transfer_detail 从表索引
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'inv_transfer_detail'
ORDER BY indexname;

-- ============================================================
-- 4. 约束验证
-- ============================================================

-- 4.1 主键约束验证
SELECT tc.constraint_name, tc.constraint_type, kc.column_name
FROM information_schema.table_constraints tc
JOIN information_schema.key_column_usage kc
    ON tc.constraint_name = kc.constraint_name
WHERE tc.table_name IN ('inv_transfer', 'inv_transfer_detail')
ORDER BY tc.table_name, tc.constraint_type;

-- 4.2 NOT NULL 约束验证
SELECT table_name, column_name, is_nullable
FROM information_schema.columns
WHERE table_name IN ('inv_transfer', 'inv_transfer_detail')
  AND column_name IN ('tenant_id', 'order_no', 'order_date', 'from_warehouse_id', 'to_warehouse_id', 'id', 'order_id', 'product_id')
ORDER BY table_name, column_name;

-- ============================================================
-- 5. COMMENT 完整性验证
-- ============================================================

-- 5.1 表注释验证
SELECT c.relname AS table_name, pgd.description AS table_comment
FROM pg_catalog.pg_statio_all_tables AS st
INNER JOIN pg_catalog.pg_description pgd ON pgd.objoid = st.relid
INNER JOIN pg_catalog.pg_class c ON pgd.objoid = c.oid AND pgd.objsubid = 0
WHERE st.schemaname = 'public' AND c.relname IN ('inv_transfer', 'inv_transfer_detail')
ORDER BY c.relname;

-- 5.2 字段注释完整性（无注释的字段）
SELECT c.relname AS table_name, a.attname AS column_name
FROM pg_catalog.pg_class c
JOIN pg_catalog.pg_attribute a ON a.attrelid = c.oid
LEFT JOIN pg_catalog.pg_description d ON d.objoid = c.oid AND d.objsubid = a.attnum
WHERE c.relname IN ('inv_transfer', 'inv_transfer_detail')
  AND a.attnum > 0
  AND a.attisdropped = false
  AND d.description IS NULL
ORDER BY c.relname, a.attname;
-- EXPECT: 全部 ext_str/ext_num/ext_date/ext_bool/ext_json 扩展字段可能缺失

-- 5.3 字段注释覆盖率
SELECT
    c.relname AS table_name,
    COUNT(*) FILTER (WHERE d.description IS NOT NULL) AS columns_with_comment,
    COUNT(*) FILTER (WHERE d.description IS NULL) AS columns_without_comment,
    ROUND(100.0 * COUNT(*) FILTER (WHERE d.description IS NOT NULL) / COUNT(*), 1) AS comment_coverage_pct
FROM pg_catalog.pg_class c
JOIN pg_catalog.pg_attribute a ON a.attrelid = c.oid
LEFT JOIN pg_catalog.pg_description d ON d.objoid = c.oid AND d.objsubid = a.attnum
WHERE c.relname IN ('inv_transfer', 'inv_transfer_detail')
  AND a.attnum > 0
  AND a.attisdropped = false
GROUP BY c.relname
ORDER BY c.relname;

-- ============================================================
-- 6. Flyway 版本验证
-- ============================================================

SELECT version, description, type, script, installed_by, success
FROM flyway_schema_history
ORDER BY installed_rank DESC
LIMIT 5;
