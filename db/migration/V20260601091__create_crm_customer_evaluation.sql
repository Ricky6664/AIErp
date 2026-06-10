-- ============================================================
-- Flyway Migration Script
-- Version: V20260601091
-- Description: crm_customer_evaluation 客户评价表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-008-001-001
-- ============================================================

-- ============================================================
-- crm_customer_evaluation 客户评价表
-- 存储客户评价信息（信用评价/合作评价/质量评价等）
-- ============================================================
CREATE TABLE IF NOT EXISTS crm_customer_evaluation (
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    -- 业务字段
    customer_id         BIGINT          NOT NULL,
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

COMMENT ON TABLE crm_customer_evaluation IS '客户评价表';
COMMENT ON COLUMN crm_customer_evaluation.id IS '主键ID';
COMMENT ON COLUMN crm_customer_evaluation.tenant_id IS '租户ID';
COMMENT ON COLUMN crm_customer_evaluation.customer_id IS '客户ID';
COMMENT ON COLUMN crm_customer_evaluation.code IS '评价编码';
COMMENT ON COLUMN crm_customer_evaluation.evaluation_name IS '评价名称';
COMMENT ON COLUMN crm_customer_evaluation.evaluation_type IS '评价类型（credit-信用评价/quality-质量评价/cooperation-合作评价/general-综合）';
COMMENT ON COLUMN crm_customer_evaluation.evaluation_score IS '评价得分';
COMMENT ON COLUMN crm_customer_evaluation.evaluator_id IS '评价人ID';
COMMENT ON COLUMN crm_customer_evaluation.evaluation_date IS '评价日期';
COMMENT ON COLUMN crm_customer_evaluation.evaluation_content IS '评价内容';
COMMENT ON COLUMN crm_customer_evaluation.parent_id IS '上级评价ID（树形结构）';
COMMENT ON COLUMN crm_customer_evaluation.status IS '状态（0-草稿/1-已审核/2-已完成/3-已作废）';
COMMENT ON COLUMN crm_customer_evaluation.remark IS '备注';
COMMENT ON COLUMN crm_customer_evaluation.created_at IS '创建时间';
COMMENT ON COLUMN crm_customer_evaluation.updated_at IS '更新时间';
COMMENT ON COLUMN crm_customer_evaluation.created_by IS '创建人ID';
COMMENT ON COLUMN crm_customer_evaluation.updated_by IS '修改人ID';
COMMENT ON COLUMN crm_customer_evaluation.is_deleted IS '是否删除';
COMMENT ON COLUMN crm_customer_evaluation.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN crm_customer_evaluation.owner_id IS '数据负责人ID';
COMMENT ON COLUMN crm_customer_evaluation.version IS '版本号';
