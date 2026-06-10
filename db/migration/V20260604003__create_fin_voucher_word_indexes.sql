-- ============================================================
-- Flyway Migration Script
-- Version: V20260604003
-- Description: fin_voucher_word凭证字表索引与约束
-- Author: AI Generated
-- Date: 2026-06-03
-- ============================================================

-- ============================================================
-- 1. 主键约束重命名（CREATE TABLE中已定义inline PK，此处统一命名）
-- ============================================================
ALTER TABLE fin_voucher_word RENAME CONSTRAINT fin_voucher_word_pkey TO pk_fin_voucher_word;

-- ============================================================
-- 2. 部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================
CREATE UNIQUE INDEX uk_fin_voucher_word_code ON fin_voucher_word(word_code) WHERE is_deleted = false;

-- ============================================================
-- 3. 多租户联合索引（tenant_id为首列）
-- ============================================================
CREATE INDEX idx_fin_voucher_word_tenant_code ON fin_voucher_word(tenant_id, word_code);
CREATE INDEX idx_fin_voucher_word_tenant_deleted ON fin_voucher_word(tenant_id, is_deleted);

-- ============================================================
-- 4. 业务查询索引
-- ============================================================
-- 外键关联字段索引
CREATE INDEX idx_fin_voucher_word_created_by ON fin_voucher_word(created_by);
CREATE INDEX idx_fin_voucher_word_updated_by ON fin_voucher_word(updated_by);
CREATE INDEX idx_fin_voucher_word_owner_dept ON fin_voucher_word(owner_dept_id);
CREATE INDEX idx_fin_voucher_word_owner ON fin_voucher_word(owner_id);

-- 排序字段索引（按租户+排序号）
CREATE INDEX idx_fin_voucher_word_sort_order ON fin_voucher_word(tenant_id, sort_order);

-- 日期范围查询（按租户+创建时间）
CREATE INDEX idx_fin_voucher_word_created_at ON fin_voucher_word(tenant_id, created_at);
