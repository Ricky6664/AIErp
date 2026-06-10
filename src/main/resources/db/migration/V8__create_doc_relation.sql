-- ============================================================
-- ERP AI 智能管理系统 - 单据关联关系表 DDL
-- 任务: P1-004-001-005-001-001, P1-004-001-005-001-002
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
-- 创建索引（P1-004-001-005-001-002）
-- 索引清单:
--   1. pk_doc_relation — 主键约束 (id)
--   2. idx_dr_source_doc — 源单据追溯索引 (source_doc_type, source_doc_id)
--   3. idx_dr_target_doc — 目标单据追溯索引 (target_doc_type, target_doc_id)
--   4. idx_dr_source_detail — 源明细行追溯索引 (source_detail_id)
--   5. idx_dr_target_detail — 目标明细行追溯索引 (target_detail_id)
--   6. idx_dr_tenant — 租户级查询索引 (tenant_id)
-- 约束说明：
--   - 所有索引使用部分索引（WHERE is_deleted = FALSE）排除软删除数据
--   - tenant_id 由 MyBatis-Plus 拦截器自动添加，不在索引首位
-- ============================================================

-- 源单据追溯索引：正向查询上游→下游关联链路
CREATE INDEX idx_dr_source_doc ON doc_relation(source_doc_type, source_doc_id) WHERE is_deleted = FALSE;

-- 目标单据追溯索引：反向查询下游→上游关联链路
CREATE INDEX idx_dr_target_doc ON doc_relation(target_doc_type, target_doc_id) WHERE is_deleted = FALSE;

-- 源明细行追溯索引：行级关联追溯 + 数量回写 FOR UPDATE 行级锁
CREATE INDEX idx_dr_source_detail ON doc_relation(source_detail_id) WHERE is_deleted = FALSE;

-- 目标明细行追溯索引：行级反向追溯
CREATE INDEX idx_dr_target_detail ON doc_relation(target_detail_id) WHERE is_deleted = FALSE;

-- 租户级查询索引
CREATE INDEX idx_dr_tenant ON doc_relation(tenant_id) WHERE is_deleted = FALSE;

COMMENT ON INDEX idx_dr_source_doc IS '源单据追溯索引：正向查询关联链路（仅未删除数据）';
COMMENT ON INDEX idx_dr_target_doc IS '目标单据追溯索引：反向查询关联链路（仅未删除数据）';
COMMENT ON INDEX idx_dr_source_detail IS '源明细行追溯索引：行级关联+数量回写行级锁（仅未删除数据）';
COMMENT ON INDEX idx_dr_target_detail IS '目标明细行追溯索引：行级反向追溯（仅未删除数据）';
COMMENT ON INDEX idx_dr_tenant IS '租户级查询索引（仅未删除数据）';

-- ============================================================
-- ALTER TABLE ADD CONSTRAINT（P1-004-001-005-001-002）
-- 检查约束：
--   1. chk_dr_relation_type — 关联类型取值限定
--   2. chk_dr_relation_qty — 关联数量非负
-- 外键说明：
--   - source_doc_id / target_doc_id 为逻辑外键，关联各业务单据主表
--   - source_detail_id / target_detail_id 为逻辑外键，关联各业务单据明细表
--   - 因父表分属不同模块且尚不存在，外键关联以索引实现
--   - 业务层通过 Service 保证引用完整性
-- ============================================================

ALTER TABLE doc_relation ADD CONSTRAINT chk_dr_relation_type CHECK (relation_type IN ('import', 'push', 'copy'));
ALTER TABLE doc_relation ADD CONSTRAINT chk_dr_relation_qty CHECK (relation_qty >= 0);

COMMENT ON CONSTRAINT chk_dr_relation_type ON doc_relation IS '关联类型取值约束：import（引入）/ push（下推）/ copy（复制）';
COMMENT ON CONSTRAINT chk_dr_relation_qty ON doc_relation IS '关联数量非负检查约束';

-- ============================================================
-- 回滚脚本（如需回滚，执行以下语句）:
-- ALTER TABLE doc_relation DROP CONSTRAINT IF EXISTS chk_dr_relation_type;
-- ALTER TABLE doc_relation DROP CONSTRAINT IF EXISTS chk_dr_relation_qty;
-- DROP INDEX IF EXISTS idx_dr_source_doc;
-- DROP INDEX IF EXISTS idx_dr_target_doc;
-- DROP INDEX IF EXISTS idx_dr_source_detail;
-- DROP INDEX IF EXISTS idx_dr_target_detail;
-- DROP INDEX IF EXISTS idx_dr_tenant;
-- DROP TABLE IF EXISTS doc_relation CASCADE;
-- ============================================================
