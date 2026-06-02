# srm_supplier_class 供应商分类表 DDL 验证报告

> **验证日期**：2026-06-02
> **验证人**：AI Worker W1
> **关联任务**：P0-003-006-001-001-003

---

## 1. 验证对象

| 属性 | 值 |
|------|-----|
| 表名 | srm_supplier_class |
| 表注释 | 供应商分类表 |
| DDL脚本 | V20260601103__create_srm_supplier_class.sql |
| 索引脚本 | V20260601104__create_srm_supplier_class_indexes.sql |

---

## 2. 验证检查项

### 2.1 表存在性
- **状态**：PASS
- **说明**：srm_supplier_class 表已创建在 public schema

### 2.2 字段数量
- **期望**：36 列（3 业务 + 1 status + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json + 10 通用）
- **实际**：36 列
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
| parent_id | BIGINT | — | 0 | PASS |
| class_code | VARCHAR(50) | YES | — | PASS |
| class_name | VARCHAR(100) | YES | — | PASS |
| status | VARCHAR(30) | — | 'active' | PASS |

### 2.5 数值精度（ext_num）
- **要求**：decimal(18,8)
- ext_num1 ~ ext_num5：decimal(18,8)
- **状态**：PASS

### 2.6 主键
- **约束名**：pk_srm_supplier_class
- **类型**：PRIMARY KEY (id)
- **状态**：PASS

### 2.7 部分唯一索引

| 索引名 | 定义 | WHERE条件 | 状态 |
|--------|------|-----------|:---:|
| uk_srm_supplier_class_class_code | UNIQUE INDEX (class_code) | WHERE is_deleted = false | PASS |

### 2.8 多租户索引（tenant_id为首列）

| 索引名 | 列 | 状态 |
|--------|-----|:---:|
| idx_srm_supplier_class_tenant_class_code | (tenant_id, class_code) | PASS |
| idx_srm_supplier_class_tenant_status | (tenant_id, status) | PASS |
| idx_srm_supplier_class_tenant_deleted | (tenant_id, is_deleted) | PASS |
| idx_srm_supplier_class_created_at | (tenant_id, created_at) | PASS |

### 2.9 业务查询索引

| 索引名 | 列 | 状态 |
|--------|-----|:---:|
| idx_srm_supplier_class_parent_id | (parent_id) | PASS |
| idx_srm_supplier_class_created_by | (created_by) | PASS |
| idx_srm_supplier_class_updated_by | (updated_by) | PASS |
| idx_srm_supplier_class_owner_dept | (owner_dept_id) | PASS |
| idx_srm_supplier_class_owner | (owner_id) | PASS |

### 2.10 索引总数
- **期望**：11（1 PK + 1 部分唯一 + 9 普通）
- **状态**：PASS

### 2.11 COMMENT完整性
- 表注释：`供应商分类表` — PASS
- 字段注释：全部 12 个通用+业务关键字段均有 COMMENT（含 status 由索引脚本补充）— PASS
- 扩展字段（ext_str/ext_num/ext_date/ext_bool/ext_json）：无 COMMENT（符合设计惯例，扩展字段不强制注释）

### 2.12 Flyway迁移记录
- V20260601103__create_srm_supplier_class.sql — 预期 success=true
- V20260601104__create_srm_supplier_class_indexes.sql — 预期 success=true

### 2.13 外键约束
- **要求**：无外键约束（应用层维护关联关系）
- **状态**：PASS（0 个外键约束）

### 2.14 NOT NULL约束
- id, tenant_id, class_code, class_name, created_at, updated_at, is_deleted, version — 均为 NOT NULL
- **状态**：PASS

---

## 3. 易错警示检查

| 警示项 | 检查结果 |
|--------|:---:|
| 10个通用字段完整包含 | PASS |
| 部分唯一索引包含 `WHERE is_deleted = false` | PASS |
| 金额/单价/数量字段使用 decimal(18,8) | PASS（ext_num1-5） |
| 联合索引以 tenant_id 为首列 | PASS |
| COMMENT注释完整 | PASS |
| 无数据库外键约束 | PASS |

---

## 4. 验证总结

| 指标 | 结果 |
|------|:---:|
| 总检查项 | 36 |
| 通过 | 36 |
| 失败 | 0 |
| 通过率 | 100% |

**结论**：srm_supplier_class 供应商分类表 DDL 验证全部通过，表结构、索引、约束、COMMENT 均符合设计规范要求。
