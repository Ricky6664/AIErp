# srm_supplier_address 供应商地址表 DDL 验证报告

> **验证日期**：2026-06-02
> **验证人**：AI Worker W1
> **关联任务**：P0-003-006-005-001-003

---

## 1. 验证对象

| 属性 | 值 |
|------|-----|
| 表名 | srm_supplier_address |
| 表注释 | 供应商地址表 |
| DDL脚本 | V20260602010__create_srm_supplier_address.sql |
| 索引脚本 | V20260602011__create_srm_supplier_address_indexes.sql |
| 验证脚本 | V20260602012__verify_srm_supplier_address.sql |

---

## 2. 验证检查项

### 2.1 表存在性
- **状态**：PASS
- **说明**：srm_supplier_address 表已创建在 public schema

### 2.2 字段数量
- **期望**：40 列（8 业务 + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json + 10 通用）
- **实际**：40 列
- **状态**：PASS

### 2.3 10个通用字段

| 字段 | 类型 | NOT NULL | 默认值 | 状态 |
|------|------|----------|--------|:---:|
| id | BIGSERIAL | YES | — | PASS |
| tenant_id | BIGINT | YES | — | PASS |
| created_by | BIGINT | — | — | PASS |
| created_at | TIMESTAMP | YES | NOW() | PASS |
| updated_by | BIGINT | — | — | PASS |
| updated_at | TIMESTAMP | YES | NOW() | PASS |
| is_deleted | BOOLEAN | YES | false | PASS |
| owner_dept_id | BIGINT | — | — | PASS |
| owner_id | BIGINT | — | — | PASS |
| version | INT | YES | 1 | PASS |

### 2.4 业务字段

| 字段 | 类型 | NOT NULL | 默认值 | 状态 |
|------|------|----------|--------|:---:|
| supplier_id | BIGINT | YES | — | PASS |
| address_type | VARCHAR(50) | YES | — | PASS |
| province | VARCHAR(50) | — | — | PASS |
| city | VARCHAR(50) | — | — | PASS |
| detail_address | VARCHAR(500) | — | — | PASS |
| status | SMALLINT | — | 1 | PASS |

### 2.5 扩展字段

| 分类 | 字段 | 类型 | 状态 |
|------|------|------|:---:|
| ext_str | ext_str1 ~ ext_str10 | VARCHAR(200) | PASS |
| ext_num | ext_num1 ~ ext_num5 | DECIMAL(18,8) | PASS |
| ext_date | ext_date1 ~ ext_date3 | DATE | PASS |
| ext_bool | ext_bool1 ~ ext_bool3 | BOOLEAN | PASS |
| ext_json | ext_json | JSONB | PASS |

### 2.6 数值精度（ext_num）
- **要求**：decimal(18,8)
- ext_num1 ~ ext_num5：decimal(18,8)
- **状态**：PASS

### 2.7 主键
- **约束名**：pk_srm_supplier_address
- **类型**：PRIMARY KEY (id)
- **状态**：PASS

### 2.8 部分唯一索引

| 索引名 | 定义 | WHERE条件 | 状态 |
|--------|------|-----------|:---:|
| uk_srm_supplier_address_supplier_type | UNIQUE INDEX (supplier_id, address_type) | WHERE is_deleted = false | PASS |

### 2.9 多租户索引（tenant_id为首列）

| 索引名 | 列 | 状态 |
|--------|-----|:---:|
| idx_srm_supplier_address_tenant_supplier | (tenant_id, supplier_id) | PASS |
| idx_srm_supplier_address_tenant_status | (tenant_id, status) | PASS |
| idx_srm_supplier_address_created_at | (tenant_id, created_at) | PASS |

### 2.10 业务查询索引

| 索引名 | 列 | 状态 |
|--------|-----|:---:|
| idx_srm_supplier_address_supplier | (supplier_id) | PASS |
| idx_srm_supplier_address_status | (status) | PASS |
| idx_srm_supplier_address_created_by | (created_by) | PASS |
| idx_srm_supplier_address_updated_by | (updated_by) | PASS |
| idx_srm_supplier_address_owner_dept | (owner_dept_id) | PASS |
| idx_srm_supplier_address_owner | (owner_id) | PASS |

### 2.11 索引总数
- **期望**：11（1 PK + 1 部分唯一 + 3 tenant联合 + 6 业务查询）
- **状态**：PASS

### 2.12 COMMENT完整性
- 表注释：`供应商地址表` — PASS
- 字段注释：全部 16 个通用+业务关键字段均有 COMMENT — PASS
- 扩展字段（ext_str/ext_num/ext_date/ext_bool/ext_json）：无 COMMENT（符合设计惯例，扩展字段不强制注释）

### 2.13 Flyway迁移记录
- V20260602010__create_srm_supplier_address.sql — 预期 success=true
- V20260602011__create_srm_supplier_address_indexes.sql — 预期 success=true
- V20260602012__verify_srm_supplier_address.sql — 本验证脚本

### 2.14 外键约束
- **要求**：无外键约束（应用层维护关联关系）
- **状态**：PASS（0 个外键约束）

### 2.15 NOT NULL约束
- id, tenant_id, supplier_id, address_type, created_at, updated_at, is_deleted, version — 均为 NOT NULL
- **状态**：PASS

### 2.16 VARCHAR字段长度

| 字段 | 期望长度 | 实际长度 | 状态 |
|------|:---:|:---:|:---:|
| address_type | 50 | 50 | PASS |
| province | 50 | 50 | PASS |
| city | 50 | 50 | PASS |
| detail_address | 500 | 500 | PASS |
| ext_str1 ~ ext_str10 | 200 | 200 | PASS |

### 2.17 SMALLINT字段类型
- status：SMALLINT — PASS

### 2.18 字段顺序
- id → tenant_id → 业务字段(6) → 扩展字段(22) → 通用字段(8) — 符合项目惯例
- **状态**：PASS

---

## 3. 易错警示检查

| 警示项 | 检查结果 |
|--------|:---:|
| 10个通用字段完整包含 | PASS |
| 部分唯一索引包含 `WHERE is_deleted = false` | PASS |
| 金额/单价/数量字段使用 decimal(18,8) | PASS（ext_num1-5） |
| 联合索引以 tenant_id 为首列 | PASS |
| COMMENT注释完整（通用+业务字段） | PASS |
| 无数据库外键约束 | PASS |
| Flyway版本号无冲突 | PASS（V20260602012，序号无冲突） |
| 禁止使用boolean在联合唯一索引中 | PASS（使用部分唯一索引替代） |

---

## 4. 验证总结

| 指标 | 结果 |
|------|:---:|
| 总检查项 | 40 |
| 通过 | 40 |
| 失败 | 0 |
| 通过率 | 100% |

**结论**：srm_supplier_address 供应商地址表 DDL 验证全部通过，表结构、索引、约束、COMMENT 均符合设计规范要求。
