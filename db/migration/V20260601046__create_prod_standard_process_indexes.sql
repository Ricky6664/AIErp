-- ============================================================
-- Flyway Migration Script
-- Version: V20260601046
-- Description: prod_standard_process 标准工序表索引与约束
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-004-013-001-002
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名为规范名称
-- ============================================================
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'prod_standard_process_pkey') THEN
        ALTER INDEX prod_standard_process_pkey RENAME TO pk_prod_standard_process;
    END IF;
END $$;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免boolean字段联合唯一索引陷阱）
--    业务规则：工序编码code全局唯一（未删除记录）
-- ============================================================
CREATE UNIQUE INDEX uk_prod_standard_process_code ON prod_standard_process(code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_prod_standard_process_tenant_code ON prod_standard_process(tenant_id, code);
CREATE INDEX idx_prod_standard_process_tenant_status ON prod_standard_process(tenant_id, status);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_prod_standard_process_status ON prod_standard_process(status);
CREATE INDEX idx_prod_standard_process_created_at ON prod_standard_process(created_at);
CREATE INDEX idx_prod_standard_process_process_type ON prod_standard_process(process_type);
