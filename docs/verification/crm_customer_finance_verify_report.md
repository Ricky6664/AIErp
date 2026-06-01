# crm_customer_finance 客户财务配置表 DDL 验证报告

> **生成日期**：2026-06-01
> **验证任务**：P0-003-005-009-001-003
> **验证人**：AI (W1)

---

## 一、DDL 文件清单

| 文件 | 版本 | 说明 |
|------|------|------|
| V20260601094__create_crm_customer_finance.sql | V20260601094 | CREATE TABLE 建表 DDL |
| V20260601094__create_crm_customer_finance_rollback.sql | V20260601094 | 建表回滚脚本 |
| V20260601095__create_crm_customer_finance_indexes.sql | V20260601095 | 索引与约束 DDL |
| V20260601095__drop_crm_customer_finance_indexes.sql | V20260601095 | 索引回滚脚本 |
| V20260601096__verify_crm_customer_finance.sql | V20260601096 | 验证查询脚本（本任务） |

---

## 二、静态 DDL 审查

### 2.1 CREATE TABLE 字段核对

**期望列数**：54（5 业务字段 + 4 单据主表字段 + 13 商品快照字段 + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json + 10 通用字段）

**DDL定义列**：

| # | 列名 | 类型 | 业务/通用 | 校验 |
|:-:|------|------|:---:|:---:|
| 1 | id | BIGSERIAL PK | 通用 | ✅ |
| 2 | tenant_id | BIGINT NOT NULL | 通用 | ✅ |
| 3 | customer_id | BIGINT NOT NULL | 业务 | ✅ |
| 4 | code | VARCHAR(50) NOT NULL | 业务 | ✅ |
| 5 | tax_no | VARCHAR(50) | 业务 | ✅ |
| 6 | credit_limit | DECIMAL(18,8) | 业务 | ✅ |
| 7 | payment_terms | VARCHAR(100) | 业务 | ✅ |
| 8 | order_no | VARCHAR(50) | 单据 | ✅ |
| 9 | order_date | DATE | 单据 | ✅ |
| 10 | status | SMALLINT NOT NULL DEFAULT 0 | 单据 | ✅ |
| 11 | remark | VARCHAR(500) | 单据 | ✅ |
| 12 | product_id | BIGINT | 商品快照 | ✅ |
| 13 | product_code | VARCHAR(50) | 商品快照 | ✅ |
| 14 | product_name | VARCHAR(200) | 商品快照 | ✅ |
| 15 | model | VARCHAR(100) | 商品快照 | ✅ |
| 16 | spec | VARCHAR(100) | 商品快照 | ✅ |
| 17 | brand | VARCHAR(100) | 商品快照 | ✅ |
| 18 | unit_id | BIGINT | 商品快照 | ✅ |
| 19 | unit | VARCHAR(50) | 商品快照 | ✅ |
| 20 | qty | DECIMAL(18,8) | 商品快照 | ✅ |
| 21 | is_multi_unit | BOOLEAN NOT NULL DEFAULT FALSE | 商品快照 | ✅ |
| 22 | conversion_rate | DECIMAL(18,8) | 商品快照 | ✅ |
| 23 | base_unit_id | BIGINT | 商品快照 | ✅ |
| 24 | base_qty | DECIMAL(18,8) | 商品快照 | ✅ |
| 25-34 | ext_str1~ext_str10 | VARCHAR(200) | 扩展 | ✅ |
| 35-39 | ext_num1~ext_num5 | DECIMAL(18,8) | 扩展 | ✅ |
| 40-42 | ext_date1~ext_date3 | DATE | 扩展 | ✅ |
| 43-45 | ext_bool1~ext_bool3 | BOOLEAN | 扩展 | ✅ |
| 46 | ext_json | JSONB | 扩展 | ✅ |
| 47 | created_at | TIMESTAMP NOT NULL DEFAULT NOW() | 通用 | ✅ |
| 48 | updated_at | TIMESTAMP NOT NULL DEFAULT NOW() | 通用 | ✅ |
| 49 | created_by | BIGINT | 通用 | ✅ |
| 50 | updated_by | BIGINT | 通用 | ✅ |
| 51 | is_deleted | BOOLEAN NOT NULL DEFAULT FALSE | 通用 | ✅ |
| 52 | owner_dept_id | BIGINT | 通用 | ✅ |
| 53 | owner_id | BIGINT | 通用 | ✅ |
| 54 | version | INT NOT NULL DEFAULT 1 | 通用 | ✅ |

**10 通用字段检查**：id, tenant_id, created_by, created_at, updated_by, updated_at, is_deleted, owner_dept_id, owner_id, version — 全部存在 ✅

### 2.2 索引与约束核对

**期望索引数**：12（1 PK + 1 UNIQUE + 10 标准索引）

| # | 索引名 | 类型 | 定义 | 校验 |
|:-:|--------|:---:|------|:---:|
| 1 | pk_crm_customer_finance | PK | PRIMARY KEY (id) | ✅ |
| 2 | uk_crm_customer_finance_code | UNIQUE | (code) WHERE is_deleted = false | ✅ |
| 3 | idx_crm_customer_finance_tenant_code | INDEX | (tenant_id, code) | ✅ |
| 4 | idx_crm_customer_finance_tenant_status | INDEX | (tenant_id, status) | ✅ |
| 5 | idx_crm_customer_finance_customer_id | INDEX | (customer_id) | ✅ |
| 6 | idx_crm_customer_finance_product_id | INDEX | (product_id) | ✅ |
| 7 | idx_crm_customer_finance_order_date | INDEX | (tenant_id, order_date) | ✅ |
| 8 | idx_crm_customer_finance_created_by | INDEX | (created_by) | ✅ |
| 9 | idx_crm_customer_finance_updated_by | INDEX | (updated_by) | ✅ |
| 10 | idx_crm_customer_finance_owner_dept | INDEX | (owner_dept_id) | ✅ |
| 11 | idx_crm_customer_finance_owner | INDEX | (owner_id) | ✅ |
| 12 | idx_crm_customer_finance_created_at | INDEX | (tenant_id, created_at) | ✅ |

### 2.3 COMMENT 注释核对

| 范围 | 状态 |
|------|:---:|
| 表注释 | ✅ `COMMENT ON TABLE ... IS '客户财务配置表'` |
| id | ✅ `COMMENT ON COLUMN ... IS '主键ID'` |
| tenant_id | ✅ `COMMENT ON COLUMN ... IS '租户ID'` |
| customer_id | ✅ `COMMENT ON COLUMN ... IS '客户ID'` |
| code | ✅ `COMMENT ON COLUMN ... IS '财务配置编码'` |
| tax_no | ✅ `COMMENT ON COLUMN ... IS '税号'` |
| credit_limit | ✅ `COMMENT ON COLUMN ... IS '信用额度'` |
| payment_terms | ✅ `COMMENT ON COLUMN ... IS '付款条件'` |
| order_no | ✅ `COMMENT ON COLUMN ... IS '关联单据号'` |
| order_date | ✅ `COMMENT ON COLUMN ... IS '关联单据日期'` |
| status | ✅ `COMMENT ON COLUMN ... IS '状态（0-草稿/1-已审核/2-已完成/3-已作废）'` |
| remark | ✅ `COMMENT ON COLUMN ... IS '备注'` |
| product_id ~ base_qty | ✅ 13 个商品快照字段均有 COMMENT |
| ext_str/num/date/bool/json 系列 | ⚠️ 扩展字段无单独 COMMENT（可接受，扩展字段无固定业务含义） |
| created_at | ✅ `COMMENT ON COLUMN ... IS '创建时间'` |
| updated_at | ✅ `COMMENT ON COLUMN ... IS '更新时间'` |
| created_by | ✅ `COMMENT ON COLUMN ... IS '创建人ID'` |
| updated_by | ✅ `COMMENT ON COLUMN ... IS '修改人ID'` |
| is_deleted | ✅ `COMMENT ON COLUMN ... IS '是否删除'` |
| owner_dept_id | ✅ `COMMENT ON COLUMN ... IS '所属部门ID'` |
| owner_id | ✅ `COMMENT ON COLUMN ... IS '数据负责人ID'` |
| version | ✅ `COMMENT ON COLUMN ... IS '版本号'` |

---

## 三、规范合规检查

| # | 检查项 | 结果 | 说明 |
|:-:|--------|:---:|------|
| 1 | 10 通用字段完整 | ✅ | id/tenant_id/created_by/created_at/updated_by/updated_at/is_deleted/owner_dept_id/owner_id/version |
| 2 | 部分唯一索引 WHERE is_deleted = false | ✅ | uk_crm_customer_finance_code |
| 3 | 数值精度 decimal(18,8) | ✅ | credit_limit + qty + conversion_rate + base_qty + ext_num1~5 均为 DECIMAL(18,8) |
| 4 | 多租户索引 tenant_id 首列 | ✅ | idx_*_tenant_code, idx_*_tenant_status, idx_*_order_date, idx_*_created_at 均以 tenant_id 为首列 |
| 5 | Flyway V{yyyyMMdd}{seq} 命名 | ✅ | V20260601094, V20260601095, V20260601096 |
| 6 | 无数据库外键约束 | ✅ | 应用层通过 MyBatis-Plus 维护关联 |
| 7 | 表 COMMENT 存在 | ✅ | |
| 8 | 核心字段 COMMENT 存在 | ✅ | 31 个核心字段均有 COMMENT |
| 9 | NOT NULL 约束正确 | ✅ | id/tenant_id/customer_id/code/status/is_multi_unit/created_at/updated_at/is_deleted/version |
| 10 | is_deleted 默认 FALSE | ✅ | |
| 11 | version 默认 1 | ✅ | |
| 12 | created_at/updated_at 默认 NOW() | ✅ | |
| 13 | status 默认 0 | ✅ | |
| 14 | is_multi_unit 默认 FALSE | ✅ | |
| 15 | 回滚脚本存在 | ✅ | DROP TABLE + DROP INDEX 回滚脚本齐全 |
| 16 | 索引命名规范（uk_/idx_ 前缀） | ✅ | 所有索引前缀正确 |
| 17 | 单据主表字段完整（order_no/order_date/status/remark） | ✅ | |
| 18 | 商品快照字段完整（13字段） | ✅ | product_id/code/name/model/spec/brand/unit_id/unit/qty/is_multi_unit/conversion_rate/base_unit_id/base_qty |
| 19 | 扩展字段结构完整 | ✅ | 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json |
| 20 | 外键关联字段索引（customer_id/product_id） | ✅ | |
| 21 | 通用字段审计索引（created_by/updated_by/owner_dept_id/owner_id） | ✅ | |

---

## 四、验收结论

| 验收项 | 状态 |
|--------|:---:|
| DDL 脚本存在且语法正确 | ✅ |
| 所有表字段类型/约束与设计一致 | ✅ |
| 所有索引定义正确 | ✅ |
| Flyway 版本号无冲突 | ✅ |
| COMMENT 注释核心字段完整 | ✅ |
| 易错警示全部规避 | ✅ |

**总体结论**：crm_customer_finance 客户财务配置表 DDL 通过静态审查，符合设计规格与数据库规范要求。
