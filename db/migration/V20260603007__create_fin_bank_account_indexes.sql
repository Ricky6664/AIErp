-- ============================================================
-- Flyway Migration Script
-- Version: V20260603007
-- Description: fin_bank_account银行账户表索引与约束
-- Author: AI
-- Date: 2026-06-03
-- Task: P0-003-008-002-001-002
-- ============================================================

-- ============================================================
-- 一、主键约束
-- ============================================================

-- 重命名内联主键为统一命名规范 pk_ 前缀
ALTER TABLE fin_bank_account RENAME CONSTRAINT fin_bank_account_pkey TO pk_fin_bank_account;

-- ============================================================
-- 二、部分唯一索引（WHERE is_deleted = false，避免"一删一活"陷阱）
-- ============================================================

CREATE UNIQUE INDEX uk_fin_bank_account_account_no ON fin_bank_account(account_no) WHERE is_deleted = false;

-- ============================================================
-- 三、多租户联合索引（tenant_id为首列）
-- ============================================================

CREATE INDEX idx_fin_bank_account_tenant_account_no ON fin_bank_account(tenant_id, account_no);
CREATE INDEX idx_fin_bank_account_tenant_deleted ON fin_bank_account(tenant_id, is_deleted);

-- ============================================================
-- 四、业务查询索引
-- ============================================================

-- 公司关联查询
CREATE INDEX idx_fin_bank_account_company ON fin_bank_account(company_id);

-- 币种关联查询
CREATE INDEX idx_fin_bank_account_currency ON fin_bank_account(currency_id);

-- 银行名称查询
CREATE INDEX idx_fin_bank_account_bank_name ON fin_bank_account(bank_name);

-- ============================================================
-- 五、通用字段索引
-- ============================================================

-- 创建人/修改人查询
CREATE INDEX idx_fin_bank_account_created_by ON fin_bank_account(created_by);
CREATE INDEX idx_fin_bank_account_updated_by ON fin_bank_account(updated_by);

-- 数据权限查询
CREATE INDEX idx_fin_bank_account_owner_dept ON fin_bank_account(owner_dept_id);
CREATE INDEX idx_fin_bank_account_owner ON fin_bank_account(owner_id);

-- 租户+创建时间范围查询
CREATE INDEX idx_fin_bank_account_created_at ON fin_bank_account(tenant_id, created_at);
