-- ============================================================
-- Flyway Migration Rollback Script
-- Version: V20260602005
-- Description: 回滚srm_supplier供应商主表索引与约束
-- Author: AI Generated
-- Date: 2026-06-02
-- ============================================================

-- ============================================================
-- 1. 删除业务查询索引
-- ============================================================
DROP INDEX IF EXISTS idx_srm_supplier_tenant_created_at;
DROP INDEX IF EXISTS idx_srm_supplier_owner;
DROP INDEX IF EXISTS idx_srm_supplier_owner_dept;
DROP INDEX IF EXISTS idx_srm_supplier_updated_by;
DROP INDEX IF EXISTS idx_srm_supplier_created_by;
DROP INDEX IF EXISTS idx_srm_supplier_class_id;

-- ============================================================
-- 2. 删除多租户联合索引
-- ============================================================
DROP INDEX IF EXISTS idx_srm_supplier_tenant_deleted;
DROP INDEX IF EXISTS idx_srm_supplier_tenant_status;
DROP INDEX IF EXISTS idx_srm_supplier_tenant_supplier_code;

-- ============================================================
-- 3. 删除部分唯一索引
-- ============================================================
DROP INDEX IF EXISTS uk_srm_supplier_supplier_code;

-- ============================================================
-- 4. 恢复主键约束名
-- ============================================================
ALTER TABLE srm_supplier RENAME CONSTRAINT pk_srm_supplier TO srm_supplier_pkey;
