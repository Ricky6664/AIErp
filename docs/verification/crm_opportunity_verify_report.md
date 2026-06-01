# crm_opportunity 客户机会表 DDL 验证报告

> **验证任务**：P0-003-005-010-001-003
> **验证日期**：2026-06-01
> **验证人**：AI (Worker W1)
> **关联DDL**：V20260601097__create_crm_opportunity.sql / V20260601098__create_crm_opportunity_indexes.sql

---

## 一、DDL 静态分析

### 1.1 建表语句（V20260601097）

| 检查项 | 结果 | 说明 |
|--------|:--:|------|
| 表名规范 | PASS | `crm_opportunity` 符合 `{模块前缀}_{实体名}` 命名规范 |
| CREATE TABLE IF NOT EXISTS | PASS | 使用幂等建表语法 |
| 10个通用字段完整性 | PASS | id/tenant_id/created_by/created_at/updated_by/updated_at/is_deleted/owner_dept_id/owner_id/version 全部包含 |
| id 类型 BIGSERIAL | PASS | 自增主键 |
| tenant_id NOT NULL | PASS | 多租户隔离强制约束 |
| is_deleted DEFAULT false | PASS | 逻辑删除默认值 |
| version DEFAULT 1 | PASS | 乐观锁默认值 |
| created_at/updated_at DEFAULT NOW() | PASS | 时间戳自动填充 |
| 数值精度 decimal(18,8) | PASS | estimated_amount/qty/conversion_rate/base_qty 均为 decimal(18,8) |
| 列总数 | PASS | 59 列（10通用 + 10业务 + 4单据 + 13商品快照 + 22扩展） |

### 1.2 业务字段

| 字段 | 类型 | 非空 | 说明 |
|------|------|:---:|------|
| customer_id | BIGINT | YES | 客户ID |
| code | VARCHAR(50) | YES | 机会编码 |
| opportunity_name | VARCHAR(200) | YES | 机会名称 |
| sales_person_id | BIGINT | — | 销售员ID |
| estimated_amount | DECIMAL(18,8) | — | 预计金额 |
| estimated_close_date | DATE | — | 预计成交日期 |
| stage | VARCHAR(50) | YES | 阶段 |
| probability | INT | — | 成交概率(0-100) |
| source | VARCHAR(50) | — | 机会来源 |
| description | VARCHAR(500) | — | 描述 |

### 1.3 单据主表字段

| 字段 | 类型 | 说明 |
|------|------|------|
| order_no | VARCHAR(50) | 关联单据号 |
| order_date | DATE | 关联单据日期 |
| status | SMALLINT NOT NULL DEFAULT 0 | 状态 |
| remark | VARCHAR(500) | 备注 |

### 1.4 商品快照字段

| 字段 | 类型 | 说明 |
|------|------|------|
| product_id/product_code/product_name/model/spec/brand | BIGINT/VARCHAR | 商品基础信息 |
| unit_id/unit | BIGINT/VARCHAR(50) | 单位信息 |
| qty | DECIMAL(18,8) | 数量 |
| is_multi_unit | BOOLEAN NOT NULL DEFAULT FALSE | 多单位标志 |
| conversion_rate | DECIMAL(18,8) | 换算率 |
| base_unit_id/base_qty | BIGINT/DECIMAL(18,8) | 基本单位/数量 |

### 1.5 扩展字段

| 类型 | 数量 | 状态 |
|------|:---:|:---:|
| ext_str (VARCHAR(200)) | 10 | PASS |
| ext_num (DECIMAL(18,8)) | 5 | PASS |
| ext_date (DATE) | 3 | PASS |
| ext_bool (BOOLEAN) | 3 | PASS |
| ext_json (JSONB) | 1 | PASS |
| **合计** | **22** | **PASS** |

### 1.6 COMMENT 注释

| 检查项 | 结果 |
|--------|:--:|
| 表注释 | PASS — `COMMENT ON TABLE crm_opportunity IS '客户机会表'` |
| 字段注释覆盖率 | PASS — 59/59 字段均有 COMMENT |

---

## 二、索引与约束静态分析（V20260601098）

### 2.1 主键约束

| 检查项 | 结果 | 说明 |
|--------|:--:|------|
| PK 存在 | PASS | inline `PRIMARY KEY (id)` 在 CREATE TABLE 中 |
| PK 命名 | PASS | 通过 RENAME CONSTRAINT 统一命名为 `pk_crm_opportunity` |

### 2.2 部分唯一索引

| 索引名 | 列 | WHERE 条件 | 结果 |
|--------|----|-----------|:--:|
| uk_crm_opportunity_code | code | WHERE is_deleted = false | PASS |

### 2.3 多租户联合索引（tenant_id 首列）

| 索引名 | 列 | 结果 |
|--------|----|:--:|
| idx_crm_opportunity_tenant_code | (tenant_id, code) | PASS |
| idx_crm_opportunity_tenant_status | (tenant_id, status) | PASS |
| idx_crm_opportunity_tenant_deleted | (tenant_id, is_deleted) | PASS |
| idx_crm_opportunity_tenant_customer | (tenant_id, customer_id) | PASS |
| idx_crm_opportunity_tenant_stage | (tenant_id, stage) | PASS |
| idx_crm_opportunity_tenant_sales | (tenant_id, sales_person_id) | PASS |
| idx_crm_opportunity_close_date | (tenant_id, estimated_close_date) | PASS |
| idx_crm_opportunity_created_at | (tenant_id, created_at) | PASS |
| idx_crm_opportunity_tenant_order | (tenant_id, order_no) | PASS |
| **tenant_id 首列索引数** | **9** | **PASS** |

### 2.4 业务查询索引

| 索引名 | 列 | 结果 |
|--------|----|:--:|
| idx_crm_opportunity_customer_id | (customer_id) | PASS |
| idx_crm_opportunity_sales_person | (sales_person_id) | PASS |
| idx_crm_opportunity_created_by | (created_by) | PASS |
| idx_crm_opportunity_updated_by | (updated_by) | PASS |
| idx_crm_opportunity_owner_dept | (owner_dept_id) | PASS |
| idx_crm_opportunity_owner | (owner_id) | PASS |
| idx_crm_opportunity_status | (status) | PASS |
| idx_crm_opportunity_stage | (stage) | PASS |

### 2.5 索引统计

| 类型 | 数量 |
|------|:---:|
| 主键索引 | 1 |
| 部分唯一索引 (uk_) | 1 |
| 标准B-Tree索引 (idx_) | 17 |
| **合计** | **19** |

### 2.6 命名规范

| 检查项 | 结果 |
|--------|:--:|
| PK 命名 `pk_{table}` | PASS |
| UK 命名 `uk_{table}_{column}` | PASS |
| 普通索引 `idx_{table}_{column}` | PASS |
| 索引引用的列名与 DDL 一致 | PASS |

---

## 三、合规性检查

| 序号 | 检查项 | 规范来源 | 结果 |
|:---:|--------|---------|:--:|
| 1 | 10个通用字段完整 | 数据库规范 | PASS |
| 2 | 部分唯一索引含 WHERE is_deleted=false | 数据库规范 | PASS |
| 3 | 金额/数量字段 decimal(18,8) | 数据库规范 | PASS |
| 4 | 联合索引 tenant_id 为首列 | 数据库规范 | PASS |
| 5 | Flyway 命名 V{yyyyMMdd}{seq}__{description} | Flyway规范 | PASS |
| 6 | 所有表/字段含 COMMENT | 数据库规范 | PASS |
| 7 | 无数据库外键约束 | 数据库规范 | PASS |
| 8 | 禁用 ENUM 类型（用 VARCHAR + COMMENT 替代） | 数据库规范 | PASS |

---

## 四、验证结论

| 项目 | 状态 |
|------|:--:|
| CREATE TABLE DDL | PASS |
| 索引与约束 | PASS |
| COMMENT 注释 | PASS |
| 规范合规 | PASS |
| Flyway 命名 | PASS |

**总体结论：crm_opportunity 客户机会表 DDL 通过静态验证，可执行数据库部署。**

---

*报告由 AI (Worker W1) 自动生成于 2026-06-01*
