-- ============================================================
-- ERP AI 智能管理系统 - 数据视图配置表 DDL
-- 任务: P0-001-006-001-001-001 / P0-001-006-001-001-002
-- 描述: 创建 sys_data_view 主表和 sys_data_view_field 从表，添加主键与索引约束
-- 数据库: PostgreSQL 15+
-- ============================================================

-- 1. 数据视图主表
CREATE TABLE sys_data_view (
    id              BIGINT          NOT NULL,
    view_code       VARCHAR(50)     NOT NULL,
    view_name       VARCHAR(100)    NOT NULL,
    source_table    VARCHAR(100),
    source_type     SMALLINT        NOT NULL,
    source_sql      TEXT,
    description     VARCHAR(500),
    -- 通用必含字段
    tenant_id       BIGINT          NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      BIGINT,
    updated_by      BIGINT,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    version         INT             NOT NULL DEFAULT 0,
    CONSTRAINT pk_sys_data_view PRIMARY KEY (id)
);

-- 唯一索引（含 tenant_id，部分索引仅对未删除数据强制唯一）
CREATE UNIQUE INDEX uk_view_code ON sys_data_view(view_code, tenant_id) WHERE is_deleted = FALSE;

-- 租户级查询索引
CREATE INDEX idx_sys_data_view_tenant ON sys_data_view(tenant_id) WHERE is_deleted = FALSE;

COMMENT ON TABLE sys_data_view IS '数据视图配置表';
COMMENT ON COLUMN sys_data_view.id IS '主键ID';
COMMENT ON COLUMN sys_data_view.view_code IS '视图编码';
COMMENT ON COLUMN sys_data_view.view_name IS '视图名称';
COMMENT ON COLUMN sys_data_view.source_table IS '源表名';
COMMENT ON COLUMN sys_data_view.source_type IS '来源类型(1=表,2=SQL)';
COMMENT ON COLUMN sys_data_view.source_sql IS '自定义SQL';
COMMENT ON COLUMN sys_data_view.description IS '视图描述';
COMMENT ON COLUMN sys_data_view.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_data_view.created_at IS '创建时间';
COMMENT ON COLUMN sys_data_view.updated_at IS '更新时间';
COMMENT ON COLUMN sys_data_view.created_by IS '创建人ID';
COMMENT ON COLUMN sys_data_view.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_data_view.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_data_view.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_data_view.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_data_view.version IS '版本号';

-- 2. 数据视图字段配置从表
CREATE TABLE sys_data_view_field (
    id              BIGINT          NOT NULL,
    view_id         BIGINT          NOT NULL,
    field_code      VARCHAR(50)     NOT NULL,
    field_name      VARCHAR(100)    NOT NULL,
    field_type      VARCHAR(30)     NOT NULL,
    field_order     INT             NOT NULL,
    is_searchable   BOOLEAN         DEFAULT FALSE,
    is_sortable     BOOLEAN         DEFAULT FALSE,
    is_visible      BOOLEAN         DEFAULT TRUE,
    search_type     VARCHAR(20),
    search_component VARCHAR(20),
    -- 通用必含字段
    tenant_id       BIGINT          NOT NULL,
    created_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by      BIGINT,
    updated_by      BIGINT,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    version         INT             NOT NULL DEFAULT 0,
    CONSTRAINT pk_sys_data_view_field PRIMARY KEY (id),
    CONSTRAINT fk_field_view_id FOREIGN KEY (view_id) REFERENCES sys_data_view(id)
);

-- 外键关联索引
CREATE INDEX idx_field_view_order ON sys_data_view_field(view_id, field_order);

-- 唯一部分索引（防止同视图同租户下字段编码重复）
CREATE UNIQUE INDEX uk_field_view_code ON sys_data_view_field(view_id, field_code, tenant_id) WHERE is_deleted = FALSE;

-- 租户级查询索引
CREATE INDEX idx_sys_data_view_field_tenant ON sys_data_view_field(tenant_id) WHERE is_deleted = FALSE;

COMMENT ON TABLE sys_data_view_field IS '数据视图字段配置表';
COMMENT ON COLUMN sys_data_view_field.id IS '主键ID';
COMMENT ON COLUMN sys_data_view_field.view_id IS '关联视图ID';
COMMENT ON COLUMN sys_data_view_field.field_code IS '字段编码';
COMMENT ON COLUMN sys_data_view_field.field_name IS '字段名称';
COMMENT ON COLUMN sys_data_view_field.field_type IS '字段类型(VARCHAR/INTEGER/DATE等)';
COMMENT ON COLUMN sys_data_view_field.field_order IS '字段排序';
COMMENT ON COLUMN sys_data_view_field.is_searchable IS '是否可搜索';
COMMENT ON COLUMN sys_data_view_field.is_sortable IS '是否可排序';
COMMENT ON COLUMN sys_data_view_field.is_visible IS '是否可见';
COMMENT ON COLUMN sys_data_view_field.search_type IS '搜索类型(eq/like/between)';
COMMENT ON COLUMN sys_data_view_field.search_component IS '搜索组件(input/select/date-range)';
COMMENT ON COLUMN sys_data_view_field.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_data_view_field.created_at IS '创建时间';
COMMENT ON COLUMN sys_data_view_field.updated_at IS '更新时间';
COMMENT ON COLUMN sys_data_view_field.created_by IS '创建人ID';
COMMENT ON COLUMN sys_data_view_field.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_data_view_field.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_data_view_field.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_data_view_field.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_data_view_field.version IS '版本号';

-- ============================================================
-- 回滚脚本（如需回滚，执行以下语句）:
-- DROP TABLE IF EXISTS sys_data_view_field CASCADE;
-- DROP TABLE IF EXISTS sys_data_view CASCADE;
-- ============================================================
