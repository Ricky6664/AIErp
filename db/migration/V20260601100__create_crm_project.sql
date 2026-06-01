-- ============================================================
-- Flyway Migration Script
-- Version: V20260601100
-- Description: crm_project 客户项目表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- Task: P0-003-005-011-001-001
-- ============================================================

-- ============================================================
-- crm_project 客户项目表
-- 存储客户项目信息（客户ID/项目编码/项目名称/阶段/日期/项目负责人等）
-- ============================================================
CREATE TABLE IF NOT EXISTS crm_project (
    id                  BIGSERIAL       PRIMARY KEY,
    tenant_id           BIGINT          NOT NULL,
    -- 业务字段
    customer_id         BIGINT          NOT NULL,
    code                VARCHAR(50)     NOT NULL,
    project_name        VARCHAR(200)    NOT NULL,
    stage               VARCHAR(50)     NOT NULL,
    start_date          DATE,
    end_date            DATE,
    project_manager_id  BIGINT,
    description         VARCHAR(500),
    -- 单据主表字段
    order_no            VARCHAR(50),
    order_date          DATE,
    status              SMALLINT        NOT NULL DEFAULT 0,
    remark              VARCHAR(500),
    -- 商品快照字段
    product_id          BIGINT,
    product_code        VARCHAR(50),
    product_name        VARCHAR(200),
    model               VARCHAR(100),
    spec                VARCHAR(100),
    brand               VARCHAR(100),
    unit_id             BIGINT,
    unit                VARCHAR(50),
    qty                 DECIMAL(18,8),
    is_multi_unit       BOOLEAN         NOT NULL DEFAULT FALSE,
    conversion_rate     DECIMAL(18,8),
    base_unit_id        BIGINT,
    base_qty            DECIMAL(18,8),
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

COMMENT ON TABLE crm_project IS '客户项目表';
COMMENT ON COLUMN crm_project.id IS '主键ID';
COMMENT ON COLUMN crm_project.tenant_id IS '租户ID';
COMMENT ON COLUMN crm_project.customer_id IS '客户ID';
COMMENT ON COLUMN crm_project.code IS '项目编码';
COMMENT ON COLUMN crm_project.project_name IS '项目名称';
COMMENT ON COLUMN crm_project.stage IS '阶段（初步接洽/需求分析/方案报价/商务谈判/执行中/已完成/已关闭）';
COMMENT ON COLUMN crm_project.start_date IS '项目开始日期';
COMMENT ON COLUMN crm_project.end_date IS '项目结束日期';
COMMENT ON COLUMN crm_project.project_manager_id IS '项目负责人ID';
COMMENT ON COLUMN crm_project.description IS '项目描述';
COMMENT ON COLUMN crm_project.order_no IS '关联单据号';
COMMENT ON COLUMN crm_project.order_date IS '关联单据日期';
COMMENT ON COLUMN crm_project.status IS '状态（0-草稿/1-已审核/2-已完成/3-已作废）';
COMMENT ON COLUMN crm_project.remark IS '备注';
COMMENT ON COLUMN crm_project.product_id IS '商品ID';
COMMENT ON COLUMN crm_project.product_code IS '商品编码';
COMMENT ON COLUMN crm_project.product_name IS '商品名称';
COMMENT ON COLUMN crm_project.model IS '型号';
COMMENT ON COLUMN crm_project.spec IS '规格';
COMMENT ON COLUMN crm_project.brand IS '品牌';
COMMENT ON COLUMN crm_project.unit_id IS '单位ID';
COMMENT ON COLUMN crm_project.unit IS '单位名称';
COMMENT ON COLUMN crm_project.qty IS '数量';
COMMENT ON COLUMN crm_project.is_multi_unit IS '是否多单位';
COMMENT ON COLUMN crm_project.conversion_rate IS '换算率';
COMMENT ON COLUMN crm_project.base_unit_id IS '基本单位ID';
COMMENT ON COLUMN crm_project.base_qty IS '基本数量';
COMMENT ON COLUMN crm_project.created_at IS '创建时间';
COMMENT ON COLUMN crm_project.updated_at IS '更新时间';
COMMENT ON COLUMN crm_project.created_by IS '创建人ID';
COMMENT ON COLUMN crm_project.updated_by IS '修改人ID';
COMMENT ON COLUMN crm_project.is_deleted IS '是否删除';
COMMENT ON COLUMN crm_project.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN crm_project.owner_id IS '数据负责人ID';
COMMENT ON COLUMN crm_project.version IS '版本号';
