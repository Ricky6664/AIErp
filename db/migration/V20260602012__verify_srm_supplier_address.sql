-- ============================================================
-- Flyway Verification Script
-- Version: V20260602012
-- Description: srm_supplier_address 供应商地址表DDL验证查询
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-005-001-003
-- ============================================================

-- ============================================================
-- 1. 表存在性验证
-- ============================================================
SELECT tablename
FROM pg_tables
WHERE schemaname = 'public'
  AND tablename = 'srm_supplier_address';

-- ============================================================
-- 2. 字段定义验证（全部字段+类型+可空性）
-- ============================================================
SELECT column_name,
       data_type,
       is_nullable,
       character_maximum_length,
       numeric_precision,
       numeric_scale,
       column_default
FROM information_schema.columns
WHERE table_name = 'srm_supplier_address'
ORDER BY ordinal_position;

-- ============================================================
-- 3. 通用字段完整性验证（10个必含字段）
-- ============================================================
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_name = 'srm_supplier_address'
  AND column_name IN (
      'id', 'tenant_id', 'created_by', 'created_at',
      'updated_by', 'updated_at', 'is_deleted',
      'owner_dept_id', 'owner_id', 'version'
  )
ORDER BY ordinal_position;

-- ============================================================
-- 4. 索引验证
-- ============================================================
SELECT indexname, indexdef
FROM pg_indexes
WHERE tablename = 'srm_supplier_address'
ORDER BY indexname;

-- ============================================================
-- 5. 约束验证（主键+唯一+CHECK+外键）
-- ============================================================
SELECT con.conname  AS constraint_name,
       con.contype AS constraint_type,
       pg_get_constraintdef(con.oid) AS constraint_def
FROM pg_constraint con
JOIN pg_class rel ON rel.oid = con.conrelid
WHERE rel.relname = 'srm_supplier_address'
ORDER BY con.contype, con.conname;

-- ============================================================
-- 6. COMMENT注释完整性验证
-- ============================================================
SELECT 'TABLE' AS obj_type,
       obj_description(c.oid, 'pg_class') AS comment
FROM pg_class c
WHERE c.relname = 'srm_supplier_address'

UNION ALL

SELECT 'COLUMN:' || a.attname AS obj_type,
       col_description(a.attrelid, a.attnum) AS comment
FROM pg_attribute a
WHERE a.attrelid = 'srm_supplier_address'::regclass
  AND a.attnum > 0
  AND NOT a.attisdropped
ORDER BY obj_type;

-- ============================================================
-- 7. Flyway版本历史验证
-- ============================================================
SELECT version,
       description,
       script,
       installed_rank,
       success,
       installed_on
FROM flyway_schema_history
WHERE script LIKE '%srm_supplier_address%'
ORDER BY installed_rank;

-- ============================================================
-- 8. 数值精度验证（ext_num字段必须为decimal(18,8)）
-- ============================================================
SELECT column_name,
       data_type,
       numeric_precision,
       numeric_scale
FROM information_schema.columns
WHERE table_name = 'srm_supplier_address'
  AND column_name LIKE 'ext_num%'
ORDER BY ordinal_position;

-- ============================================================
-- 9. 部分唯一索引WHERE条件验证
-- ============================================================
SELECT indexname,
       indexdef
FROM pg_indexes
WHERE tablename = 'srm_supplier_address'
  AND indexname LIKE 'uk_%'
ORDER BY indexname;

-- ============================================================
-- 10. 多租户索引首列验证
-- ============================================================
SELECT indexname,
       indexdef
FROM pg_indexes
WHERE tablename = 'srm_supplier_address'
  AND indexname LIKE 'idx_%'
ORDER BY indexname;

-- ============================================================
-- 11. 综合验证结果汇总
-- ============================================================
DO $$
DECLARE
    v_table_count   INT;
    v_col_count     INT;
    v_idx_count     INT;
    v_cmt_count     INT;
    v_common_count  INT;
    v_decimal_count INT;
    v_tenant_idx    INT;
BEGIN
    -- 表存在
    SELECT COUNT(*) INTO v_table_count
    FROM pg_tables
    WHERE tablename = 'srm_supplier_address';

    -- 字段总数
    SELECT COUNT(*) INTO v_col_count
    FROM information_schema.columns
    WHERE table_name = 'srm_supplier_address';

    -- 索引总数
    SELECT COUNT(*) INTO v_idx_count
    FROM pg_indexes
    WHERE tablename = 'srm_supplier_address';

    -- 注释完整字段数
    SELECT COUNT(*) INTO v_cmt_count
    FROM pg_attribute a
    WHERE a.attrelid = 'srm_supplier_address'::regclass
      AND a.attnum > 0
      AND NOT a.attisdropped
      AND col_description(a.attrelid, a.attnum) IS NOT NULL;

    -- 通用字段数
    SELECT COUNT(*) INTO v_common_count
    FROM information_schema.columns
    WHERE table_name = 'srm_supplier_address'
      AND column_name IN ('id','tenant_id','created_by','created_at','updated_by','updated_at','is_deleted','owner_dept_id','owner_id','version');

    -- decimal(18,8)字段数
    SELECT COUNT(*) INTO v_decimal_count
    FROM information_schema.columns
    WHERE table_name = 'srm_supplier_address'
      AND data_type = 'numeric'
      AND numeric_precision = 18
      AND numeric_scale = 8;

    -- tenant_id为首列的联合索引数
    SELECT COUNT(*) INTO v_tenant_idx
    FROM pg_indexes
    WHERE tablename = 'srm_supplier_address'
      AND indexdef LIKE '%ON srm_supplier_address USING btree (tenant_id%';

    RAISE NOTICE '===== srm_supplier_address DDL验证结果汇总 =====';
    RAISE NOTICE '表存在: % (期望: 1)', v_table_count;
    RAISE NOTICE '字段总数: % (期望: 40)', v_col_count;
    RAISE NOTICE '索引总数: % (期望: 11)', v_idx_count;
    RAISE NOTICE '已注释字段: % (期望: 16)', v_cmt_count;
    RAISE NOTICE '通用字段: % (期望: 10)', v_common_count;
    RAISE NOTICE 'decimal(18,8)字段: % (期望: 5)', v_decimal_count;
    RAISE NOTICE 'tenant_id首列索引: % (期望: >=3)', v_tenant_idx;

    -- 全部通过检查
    IF v_table_count = 1
       AND v_col_count = 40
       AND v_idx_count >= 11
       AND v_common_count = 10
       AND v_decimal_count = 5
    THEN
        RAISE NOTICE '===== 验证结论: 全部通过 =====';
    ELSE
        RAISE WARNING '===== 验证结论: 存在差异，请检查 =====';
    END IF;
END $$;
