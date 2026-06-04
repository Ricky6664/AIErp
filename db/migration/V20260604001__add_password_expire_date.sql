-- ============================================================
-- Flyway Migration Script
-- Version: V20260604001
-- Description: sys_user表新增password_expire_date字段（密码过期日期）
-- Author: AI
-- Date: 2026-06-04
-- ============================================================

ALTER TABLE IF EXISTS sys_user
    ADD COLUMN IF NOT EXISTS password_expire_date DATE;

COMMENT ON COLUMN sys_user.password_expire_date IS '密码过期日期，NULL表示永不过期';
