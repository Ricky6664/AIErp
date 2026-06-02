-- ============================================================
-- Flyway Rollback Script
-- Version: V20260601104
-- Description: srm_supplier_class供应商分类表索引回滚脚本
-- Author: AI Generated
-- Date: 2026-06-02
-- ============================================================

DROP INDEX IF EXISTS idx_srm_supplier_class_created_at;
DROP INDEX IF EXISTS idx_srm_supplier_class_owner;
DROP INDEX IF EXISTS idx_srm_supplier_class_owner_dept;
DROP INDEX IF EXISTS idx_srm_supplier_class_updated_by;
DROP INDEX IF EXISTS idx_srm_supplier_class_created_by;
DROP INDEX IF EXISTS idx_srm_supplier_class_parent_id;
DROP INDEX IF EXISTS idx_srm_supplier_class_tenant_deleted;
DROP INDEX IF EXISTS idx_srm_supplier_class_tenant_status;
DROP INDEX IF EXISTS idx_srm_supplier_class_tenant_class_code;
DROP INDEX IF EXISTS uk_srm_supplier_class_class_code;

-- 注意：status列和主键重命名不在回滚范围（Flyway正向迁移不可逆结构变更）
