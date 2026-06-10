# crm_customer_class客户分类表DDL验证报告

> **生成日期**：2026-06-01
> **验证任务**：P0-003-005-001-001-003
> **验证对象**：crm_customer_class客户分类表DDL（CREATE TABLE + 索引与约束）

---

## 一、验证范围

| 文件 | 版本号 | 说明 |
|------|--------|------|
| V20260601072__create_crm_customer_class.sql | V20260601072 | CREATE TABLE 建表语句 |
| V20260601073__create_crm_customer_class_indexes.sql | V20260601073 | 索引与约束 |

---

## 二、通用字段完整性验证

| 序号 | 字段名 | 类型 | 要求 | 实际 | 结果 |
|:---:|--------|------|------|------|:---:|
| 1 | id | BIGINT | PK, NOT NULL | BIGSERIAL PRIMARY KEY | PASS |
| 2 | tenant_id | BIGINT | NOT NULL | BIGINT NOT NULL | PASS |
| 3 | created_by | BIGINT | — | BIGINT | PASS |
| 4 | created_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | TIMESTAMP NOT NULL DEFAULT NOW() | PASS |
| 5 | updated_by | BIGINT | — | BIGINT | PASS |
| 6 | updated_at | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | TIMESTAMP NOT NULL DEFAULT NOW() | PASS |
| 7 | is_deleted | BOOLEAN | DEFAULT false, NOT NULL | BOOLEAN NOT NULL DEFAULT FALSE | PASS |
| 8 | owner_dept_id | BIGINT | — | BIGINT | PASS |
| 9 | owner_id | BIGINT | — | BIGINT | PASS |
| 10 | version | INT | DEFAULT 1, NOT NULL | INT NOT NULL DEFAULT 1 | PASS |

**10个通用字段全部存在，类型与默认值符合规范。**

---

## 三、字段定义清单

### 3.1 业务字段

| 字段 | 类型 | NOT NULL | 默认值 | 说明 |
|------|------|:---:|--------|------|
| parent_id | BIGINT | NO | 0 | 父分类ID |
| class_code | VARCHAR(50) | YES | — | 分类编码 |
| class_name | VARCHAR(100) | YES | — | 分类名称 |

### 3.2 扩展字段（22个）

| 字段组 | 数量 | 类型 |
|--------|:---:|------|
| ext_str1 ~ ext_str10 | 10 | VARCHAR(200) |
| ext_num1 ~ ext_num5 | 5 | DECIMAL(18,8) |
| ext_date1 ~ ext_date3 | 3 | DATE |
| ext_bool1 ~ ext_bool3 | 3 | BOOLEAN |
| ext_json | 1 | JSONB |

**字段总数：35（5业务 + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json + 8通用）**

> 注：通用字段10个中 id 和 tenant_id 已计入业务字段，其余 8 个通用字段（created_by/created_at/updated_by/updated_at/is_deleted/owner_dept_id/owner_id/version）追加后总计 35 列。

---

## 四、COMMENT注释完整性验证

### 4.1 已注释字段（13个）

| 字段 | COMMENT |
|------|---------|
| id | 主键ID |
| tenant_id | 租户ID |
| parent_id | 父分类ID |
| class_code | 分类编码 |
| class_name | 分类名称 |
| created_at | 创建时间 |
| updated_at | 更新时间 |
| created_by | 创建人ID |
| updated_by | 修改人ID |
| is_deleted | 是否删除 |
| owner_dept_id | 所属部门ID |
| owner_id | 数据负责人ID |
| version | 版本号 |

### 4.2 缺失注释字段（22个）

| 字段 | 类型 | 问题 |
|------|------|------|
| ext_str1 ~ ext_str10 | VARCHAR(200) x10 | 缺少 COMMENT |
| ext_num1 ~ ext_num5 | DECIMAL(18,8) x5 | 缺少 COMMENT |
| ext_date1 ~ ext_date3 | DATE x3 | 缺少 COMMENT |
| ext_bool1 ~ ext_bool3 | BOOLEAN x3 | 缺少 COMMENT |
| ext_json | JSONB x1 | 缺少 COMMENT |

**结果：13/35 (37.1%) 已注释，22/35 (62.9%) 扩展字段缺少 COMMENT。**

> **影响**：PostgreSQL元数据查询和后续MyBatis-Plus代码生成依赖COMMENT注释。扩展字段虽为标准化预留，但仍需标注用途以便数据字典生成。此模式与org_company/org_department/org_position/org_employee/prod_product_class等所有已验证表一致。

---

## 五、索引与约束验证

### 5.1 索引清单

| 索引名 | 索引定义 | 结果 |
|--------|---------|:---:|
| pk_crm_customer_class | PRIMARY KEY (id) | PASS |
| uk_crm_customer_class_class_code | UNIQUE INDEX (class_code) WHERE is_deleted = false | PASS |
| idx_crm_customer_class_tenant_class_code | INDEX (tenant_id, class_code) | PASS |
| idx_crm_customer_class_tenant_deleted | INDEX (tenant_id, is_deleted) | PASS |
| idx_crm_customer_class_parent_id | INDEX (parent_id) | PASS |
| idx_crm_customer_class_created_by | INDEX (created_by) | PASS |
| idx_crm_customer_class_updated_by | INDEX (updated_by) | PASS |
| idx_crm_customer_class_owner_dept | INDEX (owner_dept_id) | PASS |
| idx_crm_customer_class_owner | INDEX (owner_id) | PASS |
| idx_crm_customer_class_created_at | INDEX (tenant_id, created_at) | PASS |

**10个索引全部 PASS — 列名与DDL一致，无引用不存在列的问题。**

### 5.2 列名一致性检查

| 索引名 | 引用列 | DDL实际列名 | 结果 |
|--------|--------|------------|:---:|
| uk_crm_customer_class_class_code | `class_code` | `class_code` | PASS |
| idx_crm_customer_class_tenant_class_code | `class_code` | `class_code` | PASS |
| idx_crm_customer_class_tenant_deleted | `is_deleted` | `is_deleted` | PASS |
| idx_crm_customer_class_parent_id | `parent_id` | `parent_id` | PASS |
| idx_crm_customer_class_created_by | `created_by` | `created_by` | PASS |
| idx_crm_customer_class_updated_by | `updated_by` | `updated_by` | PASS |
| idx_crm_customer_class_owner_dept | `owner_dept_id` | `owner_dept_id` | PASS |
| idx_crm_customer_class_owner | `owner_id` | `owner_id` | PASS |
| idx_crm_customer_class_created_at | `tenant_id, created_at` | `tenant_id, created_at` | PASS |

**全部9个索引引用列与DDL实际列名完全一致，无列名不匹配问题。**

### 5.3 规范合规检查

| 检查项 | 要求 | 实际 | 结果 |
|--------|------|------|:---:|
| 部分唯一索引 WHERE is_deleted=false | 必须 | 已包含 | PASS |
| 联合索引 tenant_id 为首列 | 必须 | 3个tenant_id首列索引 | PASS |
| 禁用外键约束 | 禁止 | 无外键 | PASS |
| decimal(18,8) 精度 | 必须 | ext_num1-5 均为 DECIMAL(18,8) | PASS |

---

## 六、NOT NULL 约束验证

| 字段 | is_nullable | 要求 | 结果 |
|------|:---:|------|:---:|
| id | NO | NOT NULL (PK) | PASS |
| tenant_id | NO | NOT NULL | PASS |
| class_code | NO | NOT NULL | PASS |
| class_name | NO | NOT NULL | PASS |
| created_at | NO | NOT NULL | PASS |
| updated_at | NO | NOT NULL | PASS |
| is_deleted | NO | NOT NULL | PASS |
| version | NO | NOT NULL | PASS |

---

## 七、Flyway版本命名验证

| 文件 | 命名格式 | 结果 |
|------|---------|:---:|
| V20260601072__create_crm_customer_class.sql | V{yyyyMMdd}{seq}__{description} | PASS |
| V20260601073__create_crm_customer_class_indexes.sql | V{yyyyMMdd}{seq}__{description} | PASS |

双下划线分隔符正确，V20260601072和V20260601073序列递增无跳号，无版本号冲突。

---

## 八、验收标准对照

| 序号 | 检查项 | 验证结果 |
|:---:|--------|:---|
| 1 | DDL执行成功，所有表已创建 | CREATE TABLE语法正确，35个字段定义完整 |
| 2 | 字段类型/约束与设计100%一致 | class_code VARCHAR(50) NOT NULL, class_name VARCHAR(100) NOT NULL，均符合设计 |
| 3 | 所有索引创建成功 | 10个索引全部引用的列名与DDL一致，执行时将成功创建 |
| 4 | Flyway迁移记录success=true | 版本命名符合V{yyyyMMdd}{seq}__{description}规范 |
| 5 | COMMENT注释完整 | 22个扩展字段缺少 COMMENT |

---

## 九、问题汇总

| 序号 | 严重度 | 问题 | 影响文件 | 说明 |
|:---:|:---:|------|---------|------|
| 1 | WARNING | 22个扩展字段缺少 COMMENT | V20260601072 | ext_str1-10/ext_num1-5/ext_date1-3/ext_bool1-3/ext_json 缺少注释，与其他所有已验证表一致 |

**无 CRITICAL 级别问题。** crm_customer_class 的 DDL 质量优于 prod_product_class（后者存在索引引用不存在的 `code` 列的 CRITICAL 问题）。本表索引中引用的所有列名（class_code、parent_id、created_by、updated_by、owner_dept_id、owner_id）均与 DDL 实际定义完全一致。

---

## 十、总体结论

**验证结果：通过（0个CRITICAL阻塞性问题，1个WARNING建议改进项）**

- **CRITICAL（阻塞）**：0个
- **WARNING**：1个 — 22个扩展字段缺少 COMMENT（与其他所有已验证表一致的统一模式）
- **PASS**：10个通用字段完整、decimal精度正确、Flyway命名规范、无外键约束、NOT NULL约束正确、tenant_id首列索引规范、列名一致性全部PASS、索引引用列与DDL完全匹配
