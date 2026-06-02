-- ============================================================
-- Flyway Migration Script
-- Version: V20260602002
-- Description: srm_tag_definition SRM标签定义表索引与约束
-- Author: AI Generated
-- Date: 2026-06-02
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE srm_tag_definition RENAME CONSTRAINT srm_tag_definition_pkey TO pk_srm_tag_definition;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_srm_tag_definition_tag_name ON srm_tag_definition(tag_name) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_srm_tag_definition_tenant_tag_name ON srm_tag_definition(tenant_id, tag_name);
CREATE INDEX idx_srm_tag_definition_tenant_deleted ON srm_tag_definition(tenant_id, is_deleted);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
CREATE INDEX idx_srm_tag_definition_tag_group ON srm_tag_definition(tag_group);
CREATE INDEX idx_srm_tag_definition_enable_flag ON srm_tag_definition(enable_flag);
CREATE INDEX idx_srm_tag_definition_sort_no ON srm_tag_definition(sort_no);

-- ============================================================
-- 5. 通用字段索引（外键关联、数据权限、审计查询）
-- ============================================================
CREATE INDEX idx_srm_tag_definition_created_by ON srm_tag_definition(created_by);
CREATE INDEX idx_srm_tag_definition_updated_by ON srm_tag_definition(updated_by);
CREATE INDEX idx_srm_tag_definition_owner_dept ON srm_tag_definition(owner_dept_id);
CREATE INDEX idx_srm_tag_definition_owner ON srm_tag_definition(owner_id);

-- 日期范围查询（tenant_id为首列）
CREATE INDEX idx_srm_tag_definition_created_at ON srm_tag_definition(tenant_id, created_at);
