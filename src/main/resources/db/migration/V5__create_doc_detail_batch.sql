-- ============================================================
-- ERP AI 智能管理系统 - 批次子表 DDL
-- 任务: P0-001-009-002-001-001
-- 描述: 创建 doc_detail_batch 批次辅助属性子表
-- 数据库: PostgreSQL 15+
-- ============================================================

CREATE TABLE doc_detail_batch (
    id                  BIGINT          NOT NULL,
    detail_id           BIGINT          NOT NULL,
    batch_no            VARCHAR(100)    NOT NULL,
    production_date     DATE,
    expiry_date         DATE,
    supplier_batch_no   VARCHAR(100),
    quantity            DECIMAL(18,4)   NOT NULL DEFAULT 0,
    -- 通用必含字段
    tenant_id           BIGINT          NOT NULL,
    created_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by          BIGINT,
    updated_by          BIGINT,
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id       BIGINT,
    owner_id            BIGINT,
    version             INT             NOT NULL DEFAULT 0,
    CONSTRAINT pk_doc_detail_batch PRIMARY KEY (id)
);

-- 外键关联索引（detail_id 关联到业务单据明细主表）
CREATE INDEX idx_ddb_detail_id ON doc_detail_batch(detail_id) WHERE is_deleted = FALSE;

-- 批次号查询索引
CREATE INDEX idx_ddb_batch_no ON doc_detail_batch(batch_no) WHERE is_deleted = FALSE;

-- 供应商批号查询索引
CREATE INDEX idx_ddb_supplier_batch_no ON doc_detail_batch(supplier_batch_no) WHERE is_deleted = FALSE;

-- 租户级查询索引
CREATE INDEX idx_ddb_tenant ON doc_detail_batch(tenant_id) WHERE is_deleted = FALSE;

-- 唯一索引：同一单据明细行内不允许重复批次
CREATE UNIQUE INDEX uk_detail_batch ON doc_detail_batch(detail_id, batch_no, tenant_id) WHERE is_deleted = FALSE;

COMMENT ON TABLE doc_detail_batch IS '批次辅助属性子表';
COMMENT ON COLUMN doc_detail_batch.id IS '主键ID';
COMMENT ON COLUMN doc_detail_batch.detail_id IS '明细ID（关联业务单据明细主表）';
COMMENT ON COLUMN doc_detail_batch.batch_no IS '批次号';
COMMENT ON COLUMN doc_detail_batch.production_date IS '生产日期';
COMMENT ON COLUMN doc_detail_batch.expiry_date IS '有效期';
COMMENT ON COLUMN doc_detail_batch.supplier_batch_no IS '供应商批号';
COMMENT ON COLUMN doc_detail_batch.quantity IS '数量';
COMMENT ON COLUMN doc_detail_batch.tenant_id IS '租户ID';
COMMENT ON COLUMN doc_detail_batch.created_at IS '创建时间';
COMMENT ON COLUMN doc_detail_batch.updated_at IS '更新时间';
COMMENT ON COLUMN doc_detail_batch.created_by IS '创建人ID';
COMMENT ON COLUMN doc_detail_batch.updated_by IS '修改人ID';
COMMENT ON COLUMN doc_detail_batch.is_deleted IS '是否删除';
COMMENT ON COLUMN doc_detail_batch.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN doc_detail_batch.owner_id IS '数据负责人ID';
COMMENT ON COLUMN doc_detail_batch.version IS '版本号';

COMMENT ON CONSTRAINT pk_doc_detail_batch ON doc_detail_batch IS '主键约束：雪花算法BIGINT';
COMMENT ON INDEX idx_ddb_detail_id IS '外键关联索引：明细ID（仅未删除数据）';
COMMENT ON INDEX idx_ddb_batch_no IS '查询索引：批次号（仅未删除数据）';
COMMENT ON INDEX idx_ddb_supplier_batch_no IS '查询索引：供应商批号（仅未删除数据）';
COMMENT ON INDEX idx_ddb_tenant IS '租户级查询索引（仅未删除数据）';
COMMENT ON INDEX uk_detail_batch IS '唯一索引：明细ID+批次号+租户ID（仅未删除数据）';

-- ============================================================
-- 创建索引（P0-001-009-002-001-001）
-- 索引清单:
--   1. pk_doc_detail_batch — 主键约束 (id)
--   2. idx_ddb_detail_id — 外键关联索引 (detail_id)
--   3. idx_ddb_batch_no — 批次号查询索引 (batch_no)
--   4. idx_ddb_supplier_batch_no — 供应商批号查询索引 (supplier_batch_no)
--   5. idx_ddb_tenant — 租户查询索引 (tenant_id)
--   6. uk_detail_batch — 唯一索引 (detail_id, batch_no, tenant_id)
-- 约束说明：
--   - 唯一索引包含 tenant_id 以支持多租户隔离
--   - 所有索引使用部分索引（WHERE is_deleted = FALSE）排除软删除数据
--   - ALTER TABLE ADD CONSTRAINT 由下级任务 P0-001-009-002-001-002 完成
-- ============================================================
