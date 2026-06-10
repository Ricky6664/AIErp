-- ============================================================
-- Flyway Migration Script
-- Description: 创建OAuth2配置表 auth_oauth2_config
-- Author: AI Generated
-- Date: 2026-06-05
-- ============================================================

CREATE TABLE IF NOT EXISTS auth_oauth2_config (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    supplier_name   VARCHAR(200)    NOT NULL,
    type            VARCHAR(50)     NOT NULL,
    client_id       VARCHAR(500)    NOT NULL,
    client_secret   TEXT            NOT NULL,
    auth_url        VARCHAR(500),
    token_url       VARCHAR(500),
    user_info_url   VARCHAR(500),
    scope           VARCHAR(200),
    enabled         BOOLEAN         NOT NULL DEFAULT TRUE,
    create_time     TIMESTAMP       NOT NULL DEFAULT NOW(),
    update_time     TIMESTAMP       NOT NULL DEFAULT NOW(),
    creator_id      BIGINT,
    updater_id      BIGINT,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    version         INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE auth_oauth2_config IS 'OAuth2配置表';
COMMENT ON COLUMN auth_oauth2_config.supplier_name IS '供应商名称';
COMMENT ON COLUMN auth_oauth2_config.type IS 'OAuth2类型: wechat_work/dingtalk/feishu/github/google';
COMMENT ON COLUMN auth_oauth2_config.client_id IS 'Client ID';
COMMENT ON COLUMN auth_oauth2_config.client_secret IS 'Client Secret(AES-256-GCM加密存储)';
COMMENT ON COLUMN auth_oauth2_config.auth_url IS '授权URL';
COMMENT ON COLUMN auth_oauth2_config.token_url IS 'Token URL';
COMMENT ON COLUMN auth_oauth2_config.user_info_url IS '用户信息URL';
COMMENT ON COLUMN auth_oauth2_config.scope IS 'OAuth2 Scope';
COMMENT ON COLUMN auth_oauth2_config.enabled IS '启用状态';

CREATE UNIQUE INDEX uk_oauth2_config_supplier ON auth_oauth2_config (supplier_name) WHERE is_deleted = false;
CREATE INDEX idx_oauth2_config_type ON auth_oauth2_config (type) WHERE is_deleted = false;
CREATE INDEX idx_oauth2_config_enabled ON auth_oauth2_config (enabled) WHERE is_deleted = false;
