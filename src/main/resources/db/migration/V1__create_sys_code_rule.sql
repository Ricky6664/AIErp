-- ============================================================
-- ERP AI 智能管理系统 - 编码规则配置表 DDL
-- 任务: P0-001-005-001-001-001, P0-001-005-001-002-001
-- 描述: 创建 sys_code_rule 主表和 sys_code_rule_segment 从表
-- 数据库: PostgreSQL 15+
-- ============================================================

-- 1. 编码规则主表
CREATE TABLE sys_code_rule (
    id              BIGINT          NOT NULL,
    rule_code       VARCHAR(50)     NOT NULL,
    rule_name       VARCHAR(100)    NOT NULL,
    module_code     VARCHAR(50),
    description     VARCHAR(500),
    separator       VARCHAR(10)     DEFAULT '-',
    current_value   BIGINT          DEFAULT 0,
    is_enabled      SMALLINT        DEFAULT 1,
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
    CONSTRAINT pk_sys_code_rule PRIMARY KEY (id)
);

-- 唯一索引（含 tenant_id，部分索引仅对未删除数据强制唯一）
CREATE UNIQUE INDEX uk_rule_module ON sys_code_rule(rule_code, tenant_id) WHERE is_deleted = FALSE;

COMMENT ON TABLE sys_code_rule IS '编码规则配置表';
COMMENT ON COLUMN sys_code_rule.id IS '主键ID';
COMMENT ON COLUMN sys_code_rule.rule_code IS '规则编码';
COMMENT ON COLUMN sys_code_rule.rule_name IS '规则名称';
COMMENT ON COLUMN sys_code_rule.module_code IS '所属模块编码';
COMMENT ON COLUMN sys_code_rule.description IS '规则描述';
COMMENT ON COLUMN sys_code_rule.separator IS '段分隔符';
COMMENT ON COLUMN sys_code_rule.current_value IS '当前序列值';
COMMENT ON COLUMN sys_code_rule.is_enabled IS '是否启用(1=是,0=否)';
COMMENT ON COLUMN sys_code_rule.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_code_rule.created_at IS '创建时间';
COMMENT ON COLUMN sys_code_rule.updated_at IS '更新时间';
COMMENT ON COLUMN sys_code_rule.created_by IS '创建人ID';
COMMENT ON COLUMN sys_code_rule.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_code_rule.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_code_rule.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_code_rule.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_code_rule.version IS '版本号';

-- 2. 编码规则段配置从表
CREATE TABLE sys_code_rule_segment (
    id              BIGINT          NOT NULL,
    rule_id         BIGINT          NOT NULL,
    segment_type    SMALLINT        NOT NULL,
    segment_order   INT             NOT NULL,
    segment_value   VARCHAR(200),
    segment_length  INT,
    segment_format  VARCHAR(50),
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
    CONSTRAINT pk_sys_code_rule_segment PRIMARY KEY (id),
    CONSTRAINT fk_segment_rule_id FOREIGN KEY (rule_id) REFERENCES sys_code_rule(id)
);

-- 外键关联索引
CREATE INDEX idx_segment_order ON sys_code_rule_segment(rule_id, segment_order);

COMMENT ON TABLE sys_code_rule_segment IS '编码规则段配置表';
COMMENT ON COLUMN sys_code_rule_segment.id IS '主键ID';
COMMENT ON COLUMN sys_code_rule_segment.rule_id IS '关联编码规则ID';
COMMENT ON COLUMN sys_code_rule_segment.segment_type IS '段类型(1=固定,2=日期,3=序列,4=变量)';
COMMENT ON COLUMN sys_code_rule_segment.segment_order IS '段排序';
COMMENT ON COLUMN sys_code_rule_segment.segment_value IS '段值(固定值/变量名)';
COMMENT ON COLUMN sys_code_rule_segment.segment_length IS '段长度';
COMMENT ON COLUMN sys_code_rule_segment.segment_format IS '段格式(日期格式等)';
COMMENT ON COLUMN sys_code_rule_segment.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_code_rule_segment.created_at IS '创建时间';
COMMENT ON COLUMN sys_code_rule_segment.updated_at IS '更新时间';
COMMENT ON COLUMN sys_code_rule_segment.created_by IS '创建人ID';
COMMENT ON COLUMN sys_code_rule_segment.updated_by IS '修改人ID';
COMMENT ON COLUMN sys_code_rule_segment.is_deleted IS '是否删除';
COMMENT ON COLUMN sys_code_rule_segment.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN sys_code_rule_segment.owner_id IS '数据负责人ID';
COMMENT ON COLUMN sys_code_rule_segment.version IS '版本号';

-- ============================================================
-- 回滚脚本（如需回滚，执行以下语句）:
-- DROP TABLE IF EXISTS sys_code_rule_segment CASCADE;
-- DROP TABLE IF EXISTS sys_code_rule CASCADE;
-- ============================================================
