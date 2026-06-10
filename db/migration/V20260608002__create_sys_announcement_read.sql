-- ============================================================
-- Flyway Migration Script
-- Description: 创建公告已读记录表 sys_announcement_read
-- Author: AI Generated
-- Date: 2026-06-08
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_announcement_read (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    announcement_id BIGINT          NOT NULL,
    user_id         BIGINT          NOT NULL,
    read_time       TIMESTAMP       NOT NULL DEFAULT NOW(),
    create_time     TIMESTAMP       NOT NULL DEFAULT NOW()
);

COMMENT ON TABLE sys_announcement_read IS '公告已读记录表';
COMMENT ON COLUMN sys_announcement_read.announcement_id IS '公告ID';
COMMENT ON COLUMN sys_announcement_read.user_id IS '用户ID';
COMMENT ON COLUMN sys_announcement_read.read_time IS '阅读时间';

CREATE UNIQUE INDEX IF NOT EXISTS uk_announcement_read_user
    ON sys_announcement_read(announcement_id, user_id);

-- 回滚: DROP TABLE IF EXISTS sys_announcement_read;
