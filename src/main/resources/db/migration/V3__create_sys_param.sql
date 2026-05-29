-- ============================================================
-- ERP AI 智能管理系统 - 系统参数表 DDL
-- 任务: P0-001-007-001-001-001
-- 描述: 创建 sys_param 表，存储系统各项参数配置
-- 数据库: PostgreSQL 15+
-- ============================================================

CREATE TABLE sys_param (
    id              BIGINT          NOT NULL,
    param_category  VARCHAR(50)     NOT NULL,
    param_key       VARCHAR(100)    NOT NULL,
    param_value     TEXT,
    value_type      SMALLINT        NOT NULL DEFAULT 1,
    description     VARCHAR(500),
    sort_order      INT             DEFAULT 0,
    is_system       SMALLINT        DEFAULT 0,
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
    CONSTRAINT pk_sys_param PRIMARY KEY (id)
);

-- 唯一索引（含 tenant_id，部分索引仅对未删除数据强制唯一）
CREATE UNIQUE INDEX uk_category_key ON sys_param(param_category, param_key, tenant_id) WHERE is_deleted = FALSE;

-- 分类查询索引
CREATE INDEX idx_category ON sys_param(param_category) WHERE is_deleted = FALSE;

-- 租户级查询索引
CREATE INDEX idx_sys_param_tenant ON sys_param(tenant_id) WHERE is_deleted = FALSE;

COMMENT ON TABLE sys_param IS '系统参数配置表';
COMMENT ON COLUMN sys_param.id IS '主键ID';
COMMENT ON COLUMN sys_param.param_category IS '参数分类';
COMMENT ON COLUMN sys_param.param_key IS '参数键';
COMMENT ON COLUMN sys_param.param_value IS '参数值';
COMMENT ON COLUMN sys_param.value_type IS '值类型(1=字符串,2=数字,3=布尔,4=JSON,5=日期)';
COMMENT ON COLUMN sys_param.description IS '参数描述';
COMMENT ON COLUMN sys_param.sort_order IS '排序';
COMMENT ON COLUMN sys_param.is_system IS '是否系统参数(1=是,0=否,系统参数不可删除)';
COMMENT ON COLUMN sys_param.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_param.created_at IS '创建时间';
COMMENT ON COLUMN sys_param.updated_at IS '更新时间';
COMMENT ON COLUMN sys_param.created_by IS '创建人ID';
COMMENT ON COLUMN sys_param.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_param.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_param.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_param.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_param.version IS '版本号';

-- ============================================================
-- 回滚脚本（如需回滚，执行以下语句）:
-- DROP TABLE IF EXISTS sys_param CASCADE;
-- ============================================================
