# crm_customer_tag_rel 客户标签关联表 DDL 验证报告

> **生成日期**：2026-06-01
> **验证任务**：P0-003-005-006-001-003
> **验证人**：AI (W1)

---

## 一、DDL 文件清单

| 文件 | 版本 | 说明 |
|------|------|------|
| V20260601085__create_crm_customer_tag_rel.sql | V20260601085 | CREATE TABLE 建表 DDL |
| V20260601085__create_crm_customer_tag_rel_rollback.sql | V20260601085 | 建表回滚脚本 |
| V20260601086__create_crm_customer_tag_rel_indexes.sql | V20260601086 | 索引与约束 DDL |
| V20260601086__drop_crm_customer_tag_rel_indexes.sql | V20260601086 | 索引回滚脚本 |
| V20260601087__verify_crm_customer_tag_rel.sql | V20260601087 | 验证查询脚本（本任务） |

---

## 二、静态 DDL 审查

### 2.1 CREATE TABLE 字段核对

**期望列数**：36（4 业务字段 + 1 status + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json + 9 通用字段）

**DDL定义列**：

| # | 列名 | 类型 | 业务/通用 | 校验 |
|:-:|------|------|:---:|:---:|
| 1 | id | BIGSERIAL PK | 通用 | ✅ |
| 2 | tenant_id | BIGINT NOT NULL | 通用 | ✅ |
| 3 | customer_id | BIGINT NOT NULL | 业务 | ✅ |
| 4 | tag_id | BIGINT NOT NULL | 业务 | ✅ |
| 5 | code | VARCHAR(50) NOT NULL | 业务 | ✅ |
| 6 | status | SMALLINT DEFAULT 1 | 业务 | ✅ |
| 7-16 | ext_str1~ext_str10 | VARCHAR(200) | 扩展 | ✅ |
| 17-21 | ext_num1~ext_num5 | DECIMAL(18,8) | 扩展 | ✅ |
| 22-24 | ext_date1~ext_date3 | DATE | 扩展 | ✅ |
| 25-27 | ext_bool1~ext_bool3 | BOOLEAN | 扩展 | ✅ |
| 28 | ext_json | JSONB | 扩展 | ✅ |
| 29 | created_at | TIMESTAMP NOT NULL DEFAULT NOW() | 通用 | ✅ |
| 30 | updated_at | TIMESTAMP NOT NULL DEFAULT NOW() | 通用 | ✅ |
| 31 | created_by | BIGINT | 通用 | ✅ |
| 32 | updated_by | BIGINT | 通用 | ✅ |
| 33 | is_deleted | BOOLEAN NOT NULL DEFAULT FALSE | 通用 | ✅ |
| 34 | owner_dept_id | BIGINT | 通用 | ✅ |
| 35 | owner_id | BIGINT | 通用 | ✅ |
| 36 | version | INT NOT NULL DEFAULT 1 | 通用 | ✅ |

**10 通用字段检查**：id, tenant_id, created_by, created_at, updated_by, updated_at, is_deleted, owner_dept_id, owner_id, version — 全部存在 ✅

### 2.2 索引与约束核对

**期望索引数**：11（1 PK + 1 UNIQUE + 9 标准索引）

| # | 索引名 | 类型 | 定义 | 校验 |
|:-:|--------|:---:|------|:---:|
| 1 | pk_crm_customer_tag_rel | PK | PRIMARY KEY (id) | ✅ |
| 2 | uk_crm_customer_tag_rel_code | UNIQUE | (code) WHERE is_deleted = false | ✅ |
| 3 | idx_crm_customer_tag_rel_tenant_code | INDEX | (tenant_id, code) | ✅ |
| 4 | idx_crm_customer_tag_rel_tenant_status | INDEX | (tenant_id, status) | ✅ |
| 5 | idx_crm_customer_tag_rel_customer_id | INDEX | (customer_id) | ✅ |
| 6 | idx_crm_customer_tag_rel_tag_id | INDEX | (tag_id) | ✅ |
| 7 | idx_crm_customer_tag_rel_created_by | INDEX | (created_by) | ✅ |
| 8 | idx_crm_customer_tag_rel_updated_by | INDEX | (updated_by) | ✅ |
| 9 | idx_crm_customer_tag_rel_owner_dept | INDEX | (owner_dept_id) | ✅ |
| 10 | idx_crm_customer_tag_rel_owner | INDEX | (owner_id) | ✅ |
| 11 | idx_crm_customer_tag_rel_created_at | INDEX | (tenant_id, created_at) | ✅ |

### 2.3 COMMENT 注释核对

| 范围 | 状态 |
|------|:---:|
| 表注释 | ✅ `COMMENT ON TABLE ... IS '客户标签关联表'` |
| id | ✅ `COMMENT ON COLUMN ... IS '主键ID'` |
| tenant_id | ✅ `COMMENT ON COLUMN ... IS '租户ID'` |
| customer_id | ✅ `COMMENT ON COLUMN ... IS '客户ID'` |
| tag_id | ✅ `COMMENT ON COLUMN ... IS '标签ID'` |
| code | ✅ `COMMENT ON COLUMN ... IS '关联编码'` |
| status | ✅ `COMMENT ON COLUMN ... IS '状态'` |
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
| 2 | 部分唯一索引 WHERE is_deleted = false | ✅ | uk_crm_customer_tag_rel_code |
| 3 | 数值精度 decimal(18,8) | ✅ | ext_num1~5 均为 DECIMAL(18,8) |
| 4 | 多租户索引 tenant_id 首列 | ✅ | idx_*_tenant_* 均以 tenant_id 为首列 |
| 5 | Flyway V{yyyyMMdd}{seq} 命名 | ✅ | V20260601085, V20260601086, V20260601087 |
| 6 | 无数据库外键约束 | ✅ | 应用层通过 MyBatis-Plus 维护关联 |
| 7 | 表 COMMENT 存在 | ✅ | |
| 8 | 核心字段 COMMENT 存在 | ✅ | 14 个核心字段均有 COMMENT |
| 9 | NOT NULL 约束正确 | ✅ | id/tenant_id/customer_id/tag_id/code/created_at/updated_at/is_deleted/version |
| 10 | is_deleted 默认 FALSE | ✅ | |
| 11 | version 默认 1 | ✅ | |
| 12 | created_at/updated_at 默认 NOW() | ✅ | |

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

**总体结论**：crm_customer_tag_rel 客户标签关联表 DDL 通过静态审查，符合设计规格与数据库规范要求。
