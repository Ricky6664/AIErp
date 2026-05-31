-- ============================================================
-- Flyway Migration Script
-- Version: V20260531007
-- Description: 认证相关表索引与约束（sys_login_log/sys_oper_log）
-- Author: AI Generated
-- Date: 2026-05-31
-- ============================================================

-- ============================================================
-- 1. sys_login_log 登录日志表 索引与约束
-- ============================================================
CREATE INDEX idx_sys_login_log_tenant_username ON sys_login_log(tenant_id, username);
CREATE INDEX idx_sys_login_log_tenant_login_at ON sys_login_log(tenant_id, login_at);
CREATE INDEX idx_sys_login_log_tenant_status ON sys_login_log(tenant_id, login_status);
CREATE INDEX idx_sys_login_log_tenant_session ON sys_login_log(tenant_id, session_id);
CREATE INDEX idx_sys_login_log_tenant_type ON sys_login_log(tenant_id, login_type);
CREATE INDEX idx_sys_login_log_tenant_id ON sys_login_log(tenant_id);

-- ============================================================
-- 2. sys_oper_log 操作日志表 索引与约束
-- ============================================================
CREATE INDEX idx_sys_oper_log_tenant_oper_at ON sys_oper_log(tenant_id, oper_at);
CREATE INDEX idx_sys_oper_log_tenant_user ON sys_oper_log(tenant_id, oper_user_id);
CREATE INDEX idx_sys_oper_log_tenant_type ON sys_oper_log(tenant_id, oper_type);
CREATE INDEX idx_sys_oper_log_tenant_module ON sys_oper_log(tenant_id, oper_module);
CREATE INDEX idx_sys_oper_log_tenant_status ON sys_oper_log(tenant_id, oper_status);
CREATE INDEX idx_sys_oper_log_tenant_id ON sys_oper_log(tenant_id);
