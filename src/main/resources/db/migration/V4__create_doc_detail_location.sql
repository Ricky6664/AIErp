-- ============================================================
-- ERP AI 智能管理系统 - 库位子表 DDL
-- 任务: P0-001-009-001-001-001
-- 描述: 创建 doc_detail_location 库位辅助属性子表
-- 数据库: PostgreSQL 15+
-- ============================================================

CREATE TABLE doc_detail_location (
    id              BIGINT          NOT NULL,
    detail_id       BIGINT          NOT NULL,
    location_code   VARCHAR(100)    NOT NULL,
    warehouse_code  VARCHAR(50),
    area_code       VARCHAR(50),
    shelf_code      VARCHAR(50),
    layer_code      VARCHAR(50),
    quantity        DECIMAL(18,4)   NOT NULL DEFAULT 0,
    is_default      SMALLINT        NOT NULL DEFAULT 0,
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
    CONSTRAINT pk_doc_detail_location PRIMARY KEY (id)
);

-- 外键关联索引（detail_id 关联到业务单据明细主表）
CREATE INDEX idx_ddl_detail_id ON doc_detail_location(detail_id) WHERE is_deleted = FALSE;

-- 库位编码查询索引
CREATE INDEX idx_ddl_location_code ON doc_detail_location(location_code) WHERE is_deleted = FALSE;

-- 租户级查询索引
CREATE INDEX idx_ddl_tenant ON doc_detail_location(tenant_id) WHERE is_deleted = FALSE;

-- 唯一索引：同一单据明细行内不允许重复库位
CREATE UNIQUE INDEX uk_ddl_detail_location ON doc_detail_location(detail_id, location_code, tenant_id) WHERE is_deleted = FALSE;

COMMENT ON TABLE doc_detail_location IS '库位辅助属性子表';
COMMENT ON COLUMN doc_detail_location.id IS '主键ID';
COMMENT ON COLUMN doc_detail_location.detail_id IS '明细ID（关联业务单据明细主表）';
COMMENT ON COLUMN doc_detail_location.location_code IS '库位编码';
COMMENT ON COLUMN doc_detail_location.warehouse_code IS '仓库编码';
COMMENT ON COLUMN doc_detail_location.area_code IS '区域编码';
COMMENT ON COLUMN doc_detail_location.shelf_code IS '货架编码';
COMMENT ON COLUMN doc_detail_location.layer_code IS '层级编码';
COMMENT ON COLUMN doc_detail_location.quantity IS '数量';
COMMENT ON COLUMN doc_detail_location.is_default IS '是否默认库位(1=是,0=否)';
COMMENT ON COLUMN doc_detail_location.tenant_id IS '租户ID';
COMMENT ON COLUMN doc_detail_location.created_at IS '创建时间';
COMMENT ON COLUMN doc_detail_location.updated_at IS '更新时间';
COMMENT ON COLUMN doc_detail_location.created_by IS '创建人ID';
COMMENT ON COLUMN doc_detail_location.updated_by IS '修改人ID';
COMMENT ON COLUMN doc_detail_location.is_deleted IS '是否删除';
COMMENT ON COLUMN doc_detail_location.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN doc_detail_location.owner_id IS '数据负责人ID';
COMMENT ON COLUMN doc_detail_location.version IS '版本号';

COMMENT ON CONSTRAINT pk_doc_detail_location ON doc_detail_location IS '主键约束：雪花算法BIGINT';
COMMENT ON INDEX idx_ddl_detail_id IS '外键关联索引：明细ID（仅未删除数据）';
COMMENT ON INDEX idx_ddl_location_code IS '查询索引：库位编码（仅未删除数据）';
COMMENT ON INDEX idx_ddl_tenant IS '租户级查询索引（仅未删除数据）';
COMMENT ON INDEX uk_ddl_detail_location IS '唯一索引：明细ID+库位编码+租户ID（仅未删除数据）';

-- ============================================================
-- 创建索引（P0-001-009-001-001-001）
-- 索引清单:
--   1. pk_doc_detail_location — 主键约束 (id)
--   2. idx_ddl_detail_id — 外键关联索引 (detail_id)
--   3. idx_ddl_location_code — 库位编码查询索引 (location_code)
--   4. idx_ddl_tenant — 租户查询索引 (tenant_id)
--   5. uk_ddl_detail_location — 唯一索引 (detail_id, location_code, tenant_id)
-- 约束说明：
--   - 唯一索引包含 tenant_id 以支持多租户隔离
--   - 所有索引使用部分索引（WHERE is_deleted = FALSE）排除软删除数据
-- ============================================================
--
-- 回滚脚本（如需回滚，执行以下语句）:
-- DROP TABLE IF EXISTS doc_detail_location CASCADE;
-- ============================================================
