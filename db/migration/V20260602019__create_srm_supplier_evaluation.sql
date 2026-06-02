-- ============================================================
-- Flyway Migration Script
-- Version: V20260602019
-- Description: srm_supplier_evaluation 供应商评价表建表DDL
-- Author: AI Generated
-- Date: 2026-06-02
-- Task: P0-003-006-008-001-001
-- ============================================================

-- ============================================================
-- srm_supplier_evaluation 供应商评价表
-- 存储供应商评价信息（质量评价/交付评价/服务评价/价格评价等）
-- ============================================================
CREATE TABLE IF NOT EXISTS srm_supplier_evaluation (
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    -- 业务字段
    supplier_id         BIGINT          NOT NULL,
    code                VARCHAR(50)     NOT NULL,
    evaluation_name     VARCHAR(200),
    evaluation_type     VARCHAR(50)     NOT NULL DEFAULT 'general',
    evaluation_score    DECIMAL(18,8),
    evaluator_id        BIGINT,
    evaluation_date     DATE,
    evaluation_content  TEXT,
    parent_id           BIGINT,
    status              SMALLINT        NOT NULL DEFAULT 0,
    remark              VARCHAR(500),
    -- 扩展字段
    ext_str1            VARCHAR(200),
    ext_str2            VARCHAR(200),
    ext_str3            VARCHAR(200),
    ext_str4            VARCHAR(200),
    ext_str5            VARCHAR(200),
    ext_str6            VARCHAR(200),
    ext_str7            VARCHAR(200),
    ext_str8            VARCHAR(200),
    ext_str9            VARCHAR(200),
    ext_str10           VARCHAR(200),
    ext_num1            DECIMAL(18,8),
    ext_num2            DECIMAL(18,8),
    ext_num3            DECIMAL(18,8),
    ext_num4            DECIMAL(18,8),
    ext_num5            DECIMAL(18,8),
    ext_date1           DATE,
    ext_date2           DATE,
    ext_date3           DATE,
    ext_bool1           BOOLEAN,
    ext_bool2           BOOLEAN,
    ext_bool3           BOOLEAN,
    ext_json            JSONB,
    -- 通用必含字段
    created_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by          BIGINT,
    updated_by          BIGINT,
    is_deleted          BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id       BIGINT,
    owner_id            BIGINT,
    version             INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE srm_supplier_evaluation IS '供应商评价表';
COMMENT ON COLUMN srm_supplier_evaluation.id IS '主键ID';
COMMENT ON COLUMN srm_supplier_evaluation.tenant_id IS '租户ID';
COMMENT ON COLUMN srm_supplier_evaluation.supplier_id IS '供应商ID';
COMMENT ON COLUMN srm_supplier_evaluation.code IS '评价编码';
COMMENT ON COLUMN srm_supplier_evaluation.evaluation_name IS '评价名称';
COMMENT ON COLUMN srm_supplier_evaluation.evaluation_type IS '评价类型（quality-质量评价/delivery-交付评价/service-服务评价/price-价格评价/general-综合）';
COMMENT ON COLUMN srm_supplier_evaluation.evaluation_score IS '评价得分';
COMMENT ON COLUMN srm_supplier_evaluation.evaluator_id IS '评价人ID';
COMMENT ON COLUMN srm_supplier_evaluation.evaluation_date IS '评价日期';
COMMENT ON COLUMN srm_supplier_evaluation.evaluation_content IS '评价内容';
COMMENT ON COLUMN srm_supplier_evaluation.parent_id IS '上级评价ID（树形结构）';
COMMENT ON COLUMN srm_supplier_evaluation.status IS '状态（0-草稿/1-已审核/2-已完成/3-已作废）';
COMMENT ON COLUMN srm_supplier_evaluation.remark IS '备注';
COMMENT ON COLUMN srm_supplier_evaluation.created_at IS '创建时间';
COMMENT ON COLUMN srm_supplier_evaluation.updated_at IS '更新时间';
COMMENT ON COLUMN srm_supplier_evaluation.created_by IS '创建人ID';
COMMENT ON COLUMN srm_supplier_evaluation.updated_by IS '修改人ID';
COMMENT ON COLUMN srm_supplier_evaluation.is_deleted IS '是否删除';
COMMENT ON COLUMN srm_supplier_evaluation.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN srm_supplier_evaluation.owner_id IS '数据负责人ID';
COMMENT ON COLUMN srm_supplier_evaluation.version IS '版本号';
