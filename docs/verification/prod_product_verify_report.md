# prod_product商品主表DDL验证报告

> **生成日期**：2026-06-01
> **验证任务**：P0-003-004-002-001-003
> **验证对象**：prod_product商品主表DDL（CREATE TABLE + 索引与约束）

---

## 一、验证范围

| 文件 | 版本号 | 说明 |
|------|--------|------|
| V20260601014__create_prod_product.sql | V20260601014 | CREATE TABLE 建表语句 |
| V20260601015__create_prod_product_indexes.sql | V20260601015 | 索引与约束 |

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
| product_code | VARCHAR(50) | YES | — | 商品编码 |
| product_name | VARCHAR(200) | YES | — | 商品名称 |
| model | VARCHAR(100) | NO | — | 型号 |
| spec | VARCHAR(200) | NO | — | 规格 |
| brand | VARCHAR(100) | NO | — | 品牌 |
| base_unit_id | BIGINT | NO | — | 基本单位ID |
| class_id | BIGINT | NO | — | 商品分类ID |
| status | SMALLINT | NO | 1 | 状态(1=启用/0=停用) |

### 3.2 扩展字段（22个）

| 字段组 | 数量 | 类型 |
|--------|:---:|------|
| ext_str1 ~ ext_str10 | 10 | VARCHAR(200) |
| ext_num1 ~ ext_num5 | 5 | DECIMAL(18,8) |
| ext_date1 ~ ext_date3 | 3 | DATE |
| ext_bool1 ~ ext_bool3 | 3 | BOOLEAN |
| ext_json | 1 | JSONB |

**字段总数：40（8业务 + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json + 10通用）**

---

## 四、COMMENT注释完整性验证

### 4.1 已注释字段（18个）

| 字段 | COMMENT |
|------|---------|
| id | 主键ID |
| tenant_id | 租户ID |
| product_code | 商品编码 |
| product_name | 商品名称 |
| model | 型号 |
| spec | 规格 |
| brand | 品牌 |
| base_unit_id | 基本单位ID |
| class_id | 商品分类ID |
| status | 状态(1=启用/0=停用) |
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

**结果：18/40 (45.0%) 已注释，22/40 (55.0%) 扩展字段缺少 COMMENT。**

> **影响**：PostgreSQL元数据查询和后续MyBatis-Plus代码生成依赖COMMENT注释。扩展字段虽为标准化预留，但仍需标注用途以便数据字典生成。此问题与 org_company / org_department / org_position / org_employee / prod_product_class 表一致，属于统一模式。

---

## 五、索引与约束验证

### 5.1 索引清单

| 索引名 | 索引定义 | 结果 |
|--------|---------|:---:|
| pk_prod_product | PRIMARY KEY (id) | PASS |
| uk_prod_product_code | UNIQUE INDEX (code) WHERE is_deleted = false | **FAIL** |
| idx_prod_product_tenant_code | INDEX (tenant_id, code) | **FAIL** |
| idx_prod_product_tenant_status | INDEX (tenant_id, status) | PASS |
| idx_prod_product_tenant_class_id | INDEX (tenant_id, class_id) | PASS |
| idx_prod_product_tenant_base_unit_id | INDEX (tenant_id, base_unit_id) | PASS |
| idx_prod_product_tenant_created_at | INDEX (tenant_id, created_at) | PASS |
| idx_prod_product_tenant_name | INDEX (tenant_id, product_name) | PASS |

**2个索引FAIL — 列名不一致（详见 §5.2）。**

### 5.2 列名一致性检查（关键问题）

| 索引名 | 引用列 | DDL实际列名 | 结果 |
|--------|--------|------------|:---:|
| uk_prod_product_code | `code` | `product_code` | **FAIL** |
| idx_prod_product_tenant_code | `code` | `product_code` | **FAIL** |

> **严重性：CRITICAL** — `V20260601015__create_prod_product_indexes.sql` 中引用了不存在的 `code` 列（DDL中定义为 `product_code`）。执行这两个索引创建语句将导致 PostgreSQL 报错 `column "code" does not exist`，Flyway迁移将失败。

### 5.3 规范合规检查

| 检查项 | 要求 | 实际 | 结果 |
|--------|------|------|:---:|
| 部分唯一索引 WHERE is_deleted=false | 必须 | 已包含 | PASS |
| 联合索引 tenant_id 为首列 | 必须 | 5个tenant_id首列索引 | PASS |
| 禁用外键约束 | 禁止 | 无外键 | PASS |
| decimal(18,8) 精度 | 必须 | ext_num1-5 均为 DECIMAL(18,8) | PASS |
| SMALLINT状态字段 | 按设计 | status 使用 SMALLINT | PASS |

---

## 六、NOT NULL 约束验证

| 字段 | is_nullable | 要求 | 结果 |
|------|:---:|------|:---:|
| id | NO | NOT NULL (PK) | PASS |
| tenant_id | NO | NOT NULL | PASS |
| product_code | NO | NOT NULL | PASS |
| product_name | NO | NOT NULL | PASS |
| created_at | NO | NOT NULL | PASS |
| updated_at | NO | NOT NULL | PASS |
| is_deleted | NO | NOT NULL | PASS |
| version | NO | NOT NULL | PASS |

---

## 七、Flyway版本命名验证

| 文件 | 命名格式 | 结果 |
|------|---------|:---:|
| V20260601014__create_prod_product.sql | V{yyyyMMdd}{seq}__{description} | PASS |
| V20260601015__create_prod_product_indexes.sql | V{yyyyMMdd}{seq}__{description} | PASS |

双下划线分隔符正确，版本号无冲突。V20260601014和V20260601015序列递增无跳号。

---

## 八、验收标准对照

| 序号 | 检查项 | 验证结果 |
|:---:|--------|:---|
| 1 | DDL执行成功，所有表已创建 | CREATE TABLE语法正确，40个字段定义完整 |
| 2 | 字段类型/约束与设计100%一致 | product_code VARCHAR(50) NOT NULL, product_name VARCHAR(200) NOT NULL，status SMALLINT，均符合设计 |
| 3 | 所有索引创建成功 | **2个索引引用了不存在的 `code` 列，将在执行时失败** |
| 4 | Flyway迁移记录success=true | 版本命名符合V{yyyyMMdd}{seq}__{description}规范 |
| 5 | COMMENT注释完整 | 22个扩展字段缺少 COMMENT |

---

## 九、问题汇总与修复建议

| 序号 | 严重度 | 问题 | 影响文件 | 修复方案 |
|:---:|:---:|------|---------|---------|
| 1 | **CRITICAL** | 索引引用 `code` 列，但DDL中实际列名为 `product_code` | V20260601015 | 将 `code` 改为 `product_code`：`uk_prod_product_code ON prod_product(product_code)` 和 `idx_prod_product_tenant_code ON prod_product(tenant_id, product_code)` |
| 2 | WARNING | 22个扩展字段缺少 COMMENT | V20260601014 | 为 ext_* 字段补充 COMMENT 注释 |
| 3 | INFO | 索引名 `uk_prod_product_code` 和 `idx_prod_product_tenant_code` 在当前命名下仍可工作（列名修正后）。若需完全一致可重命名为 `*_product_code` | V20260601015 | 可选：保留现有名称（列名修正后索引即可正常工作） |

> **说明**：问题1（列名不匹配）是阻塞性问题，必须修复后才能执行Flyway迁移。此问题与 prod_product_class 表的 `code` vs `class_code` 为同类错误模式，建议一并修复。

---

## 十、总体结论

**验证结果：不通过（1个CRITICAL阻塞性问题 + 1个WARNING建议改进项）**

- **CRITICAL（阻塞）**：1个 — 索引文件引用不存在的 `code` 列，DDL实际列名为 `product_code`，将导致Flyway迁移失败
- **WARNING**：1个 — 22个扩展字段缺少 COMMENT（与其他表一致的统一模式）
- **PASS**：10个通用字段完整、decimal精度正确、Flyway命名规范、无外键约束、NOT NULL约束正确、tenant_id首列索引规范、SMALLINT状态字段正确
