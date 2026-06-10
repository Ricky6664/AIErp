-- ============================================================
-- Flyway Migration Script
-- Description: 创建SSO配置表 auth_sso_config
-- Author: AI Generated
-- Date: 2026-06-05
-- ============================================================

CREATE TABLE IF NOT EXISTS auth_sso_config (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    sso_name        VARCHAR(200)    NOT NULL,
    type            VARCHAR(50)     NOT NULL,
    idp_url         VARCHAR(500),
    sp_entity_id    VARCHAR(200),
    sso_login_url   VARCHAR(500),
    sso_logout_url  VARCHAR(500),
    certificate     TEXT,
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

COMMENT ON TABLE auth_sso_config IS 'SSO配置表';
COMMENT ON COLUMN auth_sso_config.sso_name IS 'SSO配置名称';
COMMENT ON COLUMN auth_sso_config.type IS 'SSO类型: SAML/OAuth2/CAS';
COMMENT ON COLUMN auth_sso_config.idp_url IS 'IdP服务地址';
COMMENT ON COLUMN auth_sso_config.sp_entity_id IS 'SP实体ID';
COMMENT ON COLUMN auth_sso_config.sso_login_url IS 'SSO登录URL';
COMMENT ON COLUMN auth_sso_config.sso_logout_url IS 'SSO登出URL';
COMMENT ON COLUMN auth_sso_config.certificate IS 'X.509 PEM格式证书';
COMMENT ON COLUMN auth_sso_config.enabled IS '启用状态';

CREATE UNIQUE INDEX uk_sso_config_name ON auth_sso_config (sso_name) WHERE is_deleted = false;
CREATE INDEX idx_sso_config_type ON auth_sso_config (type) WHERE is_deleted = false;
CREATE INDEX idx_sso_config_enabled ON auth_sso_config (enabled) WHERE is_deleted = false;
