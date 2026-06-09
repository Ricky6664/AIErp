-- ============================================================
-- ERP AI 智能管理系统 - 单据关联关系表 DDL
-- 任务: P1-004-001-005-001-001
-- 描述: 创建 doc_relation 单据关联关系表，记录单据间的引入/下推/复制关系
--       支撑单据追溯链路的递归查询和源单已推数量回写
-- 数据库: PostgreSQL 15+
-- ============================================================

CREATE TABLE doc_relation (
    id                BIGINT          NOT NULL,
    source_doc_type   VARCHAR(30)     NOT NULL,
    source_doc_id     BIGINT          NOT NULL,
    source_detail_id  BIGINT,
    target_doc_type   VARCHAR(30)     NOT NULL,
    target_doc_id     BIGINT          NOT NULL,
    target_detail_id  BIGINT,
    relation_type     VARCHAR(20)     NOT NULL,
    relation_qty      DECIMAL(18,6)   NOT NULL DEFAULT 0,
    -- 通用必含字段
    tenant_id         BIGINT          NOT NULL,
    created_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by        BIGINT,
    updated_by        BIGINT,
    is_deleted        BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id     BIGINT,
    owner_id          BIGINT,
    version           INT             NOT NULL DEFAULT 0,
    CONSTRAINT pk_doc_relation PRIMARY KEY (id)
);

COMMENT ON TABLE doc_relation IS '单据关联关系表';
COMMENT ON COLUMN doc_relation.id IS '主键ID';
COMMENT ON COLUMN doc_relation.source_doc_type IS '源单据类型（如sale_order/purchase_order）';
COMMENT ON COLUMN doc_relation.source_doc_id IS '源单据ID';
COMMENT ON COLUMN doc_relation.source_detail_id IS '源单据明细行ID（用于行级关联追溯）';
COMMENT ON COLUMN doc_relation.target_doc_type IS '目标单据类型';
COMMENT ON COLUMN doc_relation.target_doc_id IS '目标单据ID';
COMMENT ON COLUMN doc_relation.target_detail_id IS '目标单据明细行ID（用于行级关联追溯）';
COMMENT ON COLUMN doc_relation.relation_type IS '关联类型：import（引入）/ push（下推）/ copy（复制）';
COMMENT ON COLUMN doc_relation.relation_qty IS '关联数量（下推/引入数量，用于数量回写控制）';
COMMENT ON COLUMN doc_relation.tenant_id IS '租户ID';
COMMENT ON COLUMN doc_relation.created_at IS '创建时间';
COMMENT ON COLUMN doc_relation.updated_at IS '更新时间';
COMMENT ON COLUMN doc_relation.created_by IS '创建人ID';
COMMENT ON COLUMN doc_relation.updated_by IS '修改人ID';
COMMENT ON COLUMN doc_relation.is_deleted IS '是否删除';
COMMENT ON COLUMN doc_relation.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN doc_relation.owner_id IS '数据负责人ID';
COMMENT ON COLUMN doc_relation.version IS '版本号';

COMMENT ON CONSTRAINT pk_doc_relation ON doc_relation IS '主键约束：雪花算法BIGINT';

-- ============================================================
-- 索引与约束将在 P1-004-001-005-001-002 中添加
-- ============================================================
