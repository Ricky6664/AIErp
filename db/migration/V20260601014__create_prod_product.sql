-- ============================================================
-- Flyway Migration Script
-- Version: V20260601014
-- Description: prod_product商品主表建表DDL
-- Author: AI Generated
-- Date: 2026-06-01
-- ============================================================

-- ============================================================
-- prod_product 商品主表
-- ============================================================
CREATE TABLE IF NOT EXISTS prod_product (
    id              BIGSERIAL       PRIMARY KEY,
    tenant_id       BIGINT          NOT NULL,
    -- 业务字段
    product_code    VARCHAR(50)     NOT NULL,
    product_name    VARCHAR(200)    NOT NULL,
    model           VARCHAR(100),
    spec            VARCHAR(200),
    brand           VARCHAR(100),
    base_unit_id    BIGINT,
    class_id        BIGINT,
    status          SMALLINT        DEFAULT 1,
    -- 扩展字段
    ext_str1        VARCHAR(200),
    ext_str2        VARCHAR(200),
    ext_str3        VARCHAR(200),
    ext_str4        VARCHAR(200),
    ext_str5        VARCHAR(200),
    ext_str6        VARCHAR(200),
    ext_str7        VARCHAR(200),
    ext_str8        VARCHAR(200),
    ext_str9        VARCHAR(200),
    ext_str10       VARCHAR(200),
    ext_num1        DECIMAL(18,8),
    ext_num2        DECIMAL(18,8),
    ext_num3        DECIMAL(18,8),
    ext_num4        DECIMAL(18,8),
    ext_num5        DECIMAL(18,8),
    ext_date1       DATE,
    ext_date2       DATE,
    ext_date3       DATE,
    ext_bool1       BOOLEAN,
    ext_bool2       BOOLEAN,
    ext_bool3       BOOLEAN,
    ext_json        JSONB,
    -- 通用必含字段
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by      BIGINT,
    updated_by      BIGINT,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    owner_dept_id   BIGINT,
    owner_id        BIGINT,
    version         INT             NOT NULL DEFAULT 1
);

COMMENT ON TABLE prod_product IS '商品主表';
COMMENT ON COLUMN prod_product.id IS '主键ID';
COMMENT ON COLUMN prod_product.tenant_id IS '租户ID';
COMMENT ON COLUMN prod_product.product_code IS '商品编码';
COMMENT ON COLUMN prod_product.product_name IS '商品名称';
COMMENT ON COLUMN prod_product.model IS '型号';
COMMENT ON COLUMN prod_product.spec IS '规格';
COMMENT ON COLUMN prod_product.brand IS '品牌';
COMMENT ON COLUMN prod_product.base_unit_id IS '基本单位ID';
COMMENT ON COLUMN prod_product.class_id IS '商品分类ID';
COMMENT ON COLUMN prod_product.status IS '状态(1=启用/0=停用)';
COMMENT ON COLUMN prod_product.ext_str1 IS '扩展字符串1';
COMMENT ON COLUMN prod_product.ext_str2 IS '扩展字符串2';
COMMENT ON COLUMN prod_product.ext_str3 IS '扩展字符串3';
COMMENT ON COLUMN prod_product.ext_str4 IS '扩展字符串4';
COMMENT ON COLUMN prod_product.ext_str5 IS '扩展字符串5';
COMMENT ON COLUMN prod_product.ext_str6 IS '扩展字符串6';
COMMENT ON COLUMN prod_product.ext_str7 IS '扩展字符串7';
COMMENT ON COLUMN prod_product.ext_str8 IS '扩展字符串8';
COMMENT ON COLUMN prod_product.ext_str9 IS '扩展字符串9';
COMMENT ON COLUMN prod_product.ext_str10 IS '扩展字符串10';
COMMENT ON COLUMN prod_product.ext_num1 IS '扩展数值1';
COMMENT ON COLUMN prod_product.ext_num2 IS '扩展数值2';
COMMENT ON COLUMN prod_product.ext_num3 IS '扩展数值3';
COMMENT ON COLUMN prod_product.ext_num4 IS '扩展数值4';
COMMENT ON COLUMN prod_product.ext_num5 IS '扩展数值5';
COMMENT ON COLUMN prod_product.ext_date1 IS '扩展日期1';
COMMENT ON COLUMN prod_product.ext_date2 IS '扩展日期2';
COMMENT ON COLUMN prod_product.ext_date3 IS '扩展日期3';
COMMENT ON COLUMN prod_product.ext_bool1 IS '扩展布尔1';
COMMENT ON COLUMN prod_product.ext_bool2 IS '扩展布尔2';
COMMENT ON COLUMN prod_product.ext_bool3 IS '扩展布尔3';
COMMENT ON COLUMN prod_product.ext_json IS '扩展JSON';
COMMENT ON COLUMN prod_product.created_at IS '创建时间';
COMMENT ON COLUMN prod_product.updated_at IS '更新时间';
COMMENT ON COLUMN prod_product.created_by IS '创建人ID';
COMMENT ON COLUMN prod_product.updated_by IS '修改人ID';
COMMENT ON COLUMN prod_product.is_deleted IS '是否删除';
COMMENT ON COLUMN prod_product.owner_dept_id IS '所属部门ID';
COMMENT ON COLUMN prod_product.owner_id IS '数据负责人ID';
COMMENT ON COLUMN prod_product.version IS '版本号';
