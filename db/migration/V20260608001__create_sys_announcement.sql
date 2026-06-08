-- ============================================================
-- Flyway Migration Script
-- Description: 创建系统公告表 sys_announcement
-- Author: AI Generated
-- Date: 2026-06-08
-- ============================================================

CREATE TABLE IF NOT EXISTS sys_announcement (
    id                 BIGSERIAL       PRIMARY KEY,
    tenant_id          BIGINT          NOT NULL,
    title              VARCHAR(200)    NOT NULL,
    content            TEXT,
    announcement_type  VARCHAR(50),
    publish_time       TIMESTAMP,
    is_top             BOOLEAN         NOT NULL DEFAULT FALSE,
    status             INT             NOT NULL DEFAULT 1,
    create_time        TIMESTAMP       NOT NULL DEFAULT NOW(),
    update_time        TIMESTAMP       NOT NULL DEFAULT NOW(),
    creator_id         BIGINT,
    updater_id         BIGINT,
    is_deleted         BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id      BIGINT,
    owner_id           BIGINT,
    version            INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE sys_announcement IS '系统公告表';
COMMENT ON COLUMN sys_announcement.title IS '公告标题';
COMMENT ON COLUMN sys_announcement.content IS '公告内容(富文本)';
COMMENT ON COLUMN sys_announcement.announcement_type IS '公告类型';
COMMENT ON COLUMN sys_announcement.publish_time IS '发布时间';
COMMENT ON COLUMN sys_announcement.is_top IS '是否置顶';
COMMENT ON COLUMN sys_announcement.status IS '状态: 1=草稿 2=已发布 3=已撤回';

-- 回滚: DROP TABLE IF EXISTS sys_announcement;
