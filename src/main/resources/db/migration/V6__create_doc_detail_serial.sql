-- ============================================================
-- ERP AI 智能管理系统 - 序列号子表 DDL
-- 任务: P0-001-009-003-001-001
-- 描述: 创建 doc_detail_serial 序列号管理子表
-- 数据库: PostgreSQL 15+
-- ============================================================

CREATE TABLE doc_detail_serial (
    id                  BIGINT          NOT NULL,
    detail_id           BIGINT          NOT NULL,
    serial_no           VARCHAR(200)    NOT NULL,
    status              INTEGER         NOT NULL DEFAULT 1,
    activation_time     TIMESTAMP,
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
    CONSTRAINT pk_doc_detail_serial PRIMARY KEY (id)
);

-- 外键关联索引（detail_id 关联到业务单据明细主表）
CREATE INDEX idx_dds_detail_id ON doc_detail_serial(detail_id) WHERE is_deleted = FALSE;

-- 序列号查询索引
CREATE INDEX idx_dds_serial_no ON doc_detail_serial(serial_no) WHERE is_deleted = FALSE;

-- 租户级查询索引
CREATE INDEX idx_dds_tenant ON doc_detail_serial(tenant_id) WHERE is_deleted = FALSE;

-- 唯一索引：同一租户内序列号唯一
CREATE UNIQUE INDEX uk_serial_no ON doc_detail_serial(serial_no, tenant_id) WHERE is_deleted = FALSE;

COMMENT ON TABLE doc_detail_serial IS '序列号管理子表';
COMMENT ON COLUMN doc_detail_serial.id IS '主键ID';
COMMENT ON COLUMN doc_detail_serial.detail_id IS '明细ID（关联业务单据明细主表）';
COMMENT ON COLUMN doc_detail_serial.serial_no IS '序列号';
COMMENT ON COLUMN doc_detail_serial.status IS '序列号状态：1在库、2出库、3报废';
COMMENT ON COLUMN doc_detail_serial.activation_time IS '激活时间';
COMMENT ON COLUMN doc_detail_serial.tenant_id IS '租户ID';
COMMENT ON COLUMN doc_detail_serial.created_at IS '创建时间';
COMMENT ON COLUMN doc_detail_serial.updated_at IS '更新时间';
COMMENT ON COLUMN doc_detail_serial.created_by IS '创建人ID';
COMMENT ON COLUMN doc_detail_serial.updated_by IS '修改人ID';
COMMENT ON COLUMN doc_detail_serial.is_deleted IS '是否删除';
COMMENT ON COLUMN doc_detail_serial.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN doc_detail_serial.owner_id IS '数据负责人ID';
COMMENT ON COLUMN doc_detail_serial.version IS '版本号';

COMMENT ON CONSTRAINT pk_doc_detail_serial ON doc_detail_serial IS '主键约束：雪花算法BIGINT';
COMMENT ON INDEX idx_dds_detail_id IS '外键关联索引：明细ID（仅未删除数据）';
COMMENT ON INDEX idx_dds_serial_no IS '查询索引：序列号（仅未删除数据）';
COMMENT ON INDEX idx_dds_tenant IS '租户级查询索引（仅未删除数据）';
COMMENT ON INDEX uk_serial_no IS '唯一索引：序列号+租户ID（仅未删除数据）';

-- ============================================================
-- 创建索引（P0-001-009-003-001-001）
-- 索引清单:
--   1. pk_doc_detail_serial — 主键约束 (id)
--   2. idx_dds_detail_id — 外键关联索引 (detail_id)
--   3. idx_dds_serial_no — 序列号查询索引 (serial_no)
--   4. idx_dds_tenant — 租户查询索引 (tenant_id)
--   5. uk_serial_no — 唯一索引 (serial_no, tenant_id)
-- 约束说明：
--   - 唯一索引包含 tenant_id 以支持多租户隔离
--   - 所有索引使用部分索引（WHERE is_deleted = FALSE）排除软删除数据
--   - ALTER TABLE ADD CONSTRAINT 由下级任务 P0-001-009-003-001-002 完成
-- ============================================================

-- ============================================================
-- ALTER TABLE ADD CONSTRAINT（P0-001-009-003-001-002）
-- 外键说明：
--   - detail_id 为逻辑外键，关联各业务单据明细表（采购/销售/库存等）
--   - 因父表分属不同模块且尚不存在，外键关联以索引 idx_dds_detail_id 实现
--   - 业务层通过 Service 保证引用完整性
-- 检查约束：
--   1. chk_dds_status — 序列号状态仅允许 1在库、2出库、3报废
--   2. chk_dds_serial_no — 序列号不允许空字符串
-- ============================================================

ALTER TABLE doc_detail_serial ADD CONSTRAINT chk_dds_status CHECK (status BETWEEN 1 AND 3);
ALTER TABLE doc_detail_serial ADD CONSTRAINT chk_dds_serial_no CHECK (serial_no <> '');

COMMENT ON CONSTRAINT chk_dds_status ON doc_detail_serial IS '序列号状态检查约束：1在库、2出库、3报废';
COMMENT ON CONSTRAINT chk_dds_serial_no ON doc_detail_serial IS '序列号非空检查约束';

--
-- 回滚脚本（如需回滚，执行以下语句）:
-- ALTER TABLE doc_detail_serial DROP CONSTRAINT IF EXISTS chk_dds_status;
-- ALTER TABLE doc_detail_serial DROP CONSTRAINT IF EXISTS chk_dds_serial_no;
-- DROP TABLE IF EXISTS doc_detail_serial CASCADE;
-- ============================================================
