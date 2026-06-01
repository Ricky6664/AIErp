# crm_customer_evaluation 客户评价表 DDL 验证报告

> **生成日期**：2026-06-01
> **验证任务**：P0-003-005-008-001-003
> **验证人**：AI (W1)

---

## 一、DDL 文件清单

| 文件 | 版本 | 说明 |
|------|------|------|
| V20260601091__create_crm_customer_evaluation.sql | V20260601091 | CREATE TABLE 建表 DDL |
| V20260601091__create_crm_customer_evaluation_rollback.sql | V20260601091 | 建表回滚脚本 |
| V20260601092__create_crm_customer_evaluation_indexes.sql | V20260601092 | 索引与约束 DDL |
| V20260601092__drop_crm_customer_evaluation_indexes.sql | V20260601092 | 索引回滚脚本 |
| V20260601093__verify_crm_customer_evaluation.sql | V20260601093 | 验证查询脚本（本任务） |

---

## 二、静态 DDL 审查

### 2.1 CREATE TABLE 字段核对

**期望列数**：43（11 业务字段 + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json + 10 通用字段）

**DDL定义列**：

| # | 列名 | 类型 | 业务/通用 | 校验 |
|:-:|------|------|:---:|:---:|
| 1 | id | BIGSERIAL PK | 通用 | ✅ |
| 2 | tenant_id | BIGINT NOT NULL | 通用 | ✅ |
| 3 | customer_id | BIGINT NOT NULL | 业务 | ✅ |
| 4 | code | VARCHAR(50) NOT NULL | 业务 | ✅ |
| 5 | evaluation_name | VARCHAR(200) | 业务 | ✅ |
| 6 | evaluation_type | VARCHAR(50) NOT NULL DEFAULT 'general' | 业务 | ✅ |
| 7 | evaluation_score | DECIMAL(18,8) | 业务 | ✅ |
| 8 | evaluator_id | BIGINT | 业务 | ✅ |
| 9 | evaluation_date | DATE | 业务 | ✅ |
| 10 | evaluation_content | TEXT | 业务 | ✅ |
| 11 | parent_id | BIGINT | 业务 | ✅ |
| 12 | status | SMALLINT NOT NULL DEFAULT 0 | 业务 | ✅ |
| 13 | remark | VARCHAR(500) | 业务 | ✅ |
| 14-23 | ext_str1~ext_str10 | VARCHAR(200) | 扩展 | ✅ |
| 24-28 | ext_num1~ext_num5 | DECIMAL(18,8) | 扩展 | ✅ |
| 29-31 | ext_date1~ext_date3 | DATE | 扩展 | ✅ |
| 32-34 | ext_bool1~ext_bool3 | BOOLEAN | 扩展 | ✅ |
| 35 | ext_json | JSONB | 扩展 | ✅ |
| 36 | created_at | TIMESTAMP NOT NULL DEFAULT NOW() | 通用 | ✅ |
| 37 | updated_at | TIMESTAMP NOT NULL DEFAULT NOW() | 通用 | ✅ |
| 38 | created_by | BIGINT | 通用 | ✅ |
| 39 | updated_by | BIGINT | 通用 | ✅ |
| 40 | is_deleted | BOOLEAN NOT NULL DEFAULT FALSE | 通用 | ✅ |
| 41 | owner_dept_id | BIGINT | 通用 | ✅ |
| 42 | owner_id | BIGINT | 通用 | ✅ |
| 43 | version | INT NOT NULL DEFAULT 1 | 通用 | ✅ |

**10 通用字段检查**：id, tenant_id, created_by, created_at, updated_by, updated_at, is_deleted, owner_dept_id, owner_id, version — 全部存在 ✅

### 2.2 索引与约束核对

**期望索引数**：13（1 PK + 1 UNIQUE + 11 标准索引）

| # | 索引名 | 类型 | 定义 | 校验 |
|:-:|--------|:---:|------|:---:|
| 1 | pk_crm_customer_evaluation | PK | PRIMARY KEY (id) | ✅ |
| 2 | uk_crm_customer_evaluation_code | UNIQUE | (code) WHERE is_deleted = false | ✅ |
| 3 | idx_crm_customer_evaluation_tenant_code | INDEX | (tenant_id, code) | ✅ |
| 4 | idx_crm_customer_evaluation_tenant_status | INDEX | (tenant_id, status) | ✅ |
| 5 | idx_crm_customer_evaluation_customer_id | INDEX | (customer_id) | ✅ |
| 6 | idx_crm_customer_evaluation_parent_id | INDEX | (parent_id) | ✅ |
| 7 | idx_crm_customer_evaluation_evaluator_id | INDEX | (evaluator_id) | ✅ |
| 8 | idx_crm_customer_evaluation_evaluation_date | INDEX | (evaluation_date) | ✅ |
| 9 | idx_crm_customer_evaluation_created_by | INDEX | (created_by) | ✅ |
| 10 | idx_crm_customer_evaluation_updated_by | INDEX | (updated_by) | ✅ |
| 11 | idx_crm_customer_evaluation_owner_dept | INDEX | (owner_dept_id) | ✅ |
| 12 | idx_crm_customer_evaluation_owner | INDEX | (owner_id) | ✅ |
| 13 | idx_crm_customer_evaluation_created_at | INDEX | (tenant_id, created_at) | ✅ |

### 2.3 COMMENT 注释核对

| 范围 | 状态 |
|------|:---:|
| 表注释 | ✅ `COMMENT ON TABLE ... IS '客户评价表'` |
| id | ✅ `COMMENT ON COLUMN ... IS '主键ID'` |
| tenant_id | ✅ `COMMENT ON COLUMN ... IS '租户ID'` |
| customer_id | ✅ `COMMENT ON COLUMN ... IS '客户ID'` |
| code | ✅ `COMMENT ON COLUMN ... IS '评价编码'` |
| evaluation_name | ✅ `COMMENT ON COLUMN ... IS '评价名称'` |
| evaluation_type | ✅ `COMMENT ON COLUMN ... IS '评价类型（credit-信用评价/quality-质量评价/cooperation-合作评价/general-综合）'` |
| evaluation_score | ✅ `COMMENT ON COLUMN ... IS '评价得分'` |
| evaluator_id | ✅ `COMMENT ON COLUMN ... IS '评价人ID'` |
| evaluation_date | ✅ `COMMENT ON COLUMN ... IS '评价日期'` |
| evaluation_content | ✅ `COMMENT ON COLUMN ... IS '评价内容'` |
| parent_id | ✅ `COMMENT ON COLUMN ... IS '上级评价ID（树形结构）'` |
| status | ✅ `COMMENT ON COLUMN ... IS '状态（0-草稿/1-已审核/2-已完成/3-已作废）'` |
| remark | ✅ `COMMENT ON COLUMN ... IS '备注'` |
| created_at | ✅ `COMMENT ON COLUMN ... IS '创建时间'` |
| updated_at | ✅ `COMMENT ON COLUMN ... IS '更新时间'` |
| created_by | ✅ `COMMENT ON COLUMN ... IS '创建人ID'` |
| updated_by | ✅ `COMMENT ON COLUMN ... IS '修改人ID'` |
| is_deleted | ✅ `COMMENT ON COLUMN ... IS '是否删除'` |
| owner_dept_id | ✅ `COMMENT ON COLUMN ... IS '所属部门ID'` |
| owner_id | ✅ `COMMENT ON COLUMN ... IS '数据负责人ID'` |
| version | ✅ `COMMENT ON COLUMN ... IS '版本号'` |
| ext_str/num/date/bool/json 系列 | ⚠️ 扩展字段无单独 COMMENT（可接受，扩展字段无固定业务含义） |

---

## 三、规范合规检查

| # | 检查项 | 结果 | 说明 |
|:-:|--------|:---:|------|
| 1 | 10 通用字段完整 | ✅ | id/tenant_id/created_by/created_at/updated_by/updated_at/is_deleted/owner_dept_id/owner_id/version |
| 2 | 部分唯一索引 WHERE is_deleted = false | ✅ | uk_crm_customer_evaluation_code |
| 3 | 数值精度 decimal(18,8) | ✅ | evaluation_score + ext_num1~5 均为 DECIMAL(18,8) |
| 4 | 多租户索引 tenant_id 首列 | ✅ | idx_*_tenant_code, idx_*_tenant_status, idx_*_created_at 均以 tenant_id 为首列 |
| 5 | Flyway V{yyyyMMdd}{seq} 命名 | ✅ | V20260601091, V20260601092, V20260601093 |
| 6 | 无数据库外键约束 | ✅ | 应用层通过 MyBatis-Plus 维护关联 |
| 7 | 表 COMMENT 存在 | ✅ | |
| 8 | 核心字段 COMMENT 存在 | ✅ | 21 个核心字段均有 COMMENT |
| 9 | NOT NULL 约束正确 | ✅ | id/tenant_id/customer_id/code/evaluation_type/status/created_at/updated_at/is_deleted/version |
| 10 | is_deleted 默认 FALSE | ✅ | |
| 11 | version 默认 1 | ✅ | |
| 12 | created_at/updated_at 默认 NOW() | ✅ | |
| 13 | evaluation_type 默认 'general' | ✅ | |
| 14 | status 默认 0 | ✅ | |
| 15 | 回滚脚本存在 | ✅ | DROP TABLE + DROP INDEX 回滚脚本齐全 |
| 16 | 索引命名规范（uk_/idx_ 前缀） | ✅ | 所有索引前缀正确 |
| 17 | parent_id 树形结构索引 | ✅ | 支持树形评价结构查询 |
| 18 | 扩展字段结构完整 | ✅ | 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json |

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

**总体结论**：crm_customer_evaluation 客户评价表 DDL 通过静态审查，符合设计规格与数据库规范要求。
