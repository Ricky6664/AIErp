# prod_product_class商品分类表DDL验证报告

> **生成日期**：2026-06-01
> **验证任务**：P0-003-004-001-001-003
> **验证对象**：prod_product_class商品分类表DDL（CREATE TABLE + 索引与约束）

---

## 一、验证范围

| 文件 | 版本号 | 说明 |
|------|--------|------|
| V20260601011__create_prod_product_class.sql | V20260601011 | CREATE TABLE 建表语句 |
| V20260601012__create_prod_product_class_indexes.sql | V20260601012 | 索引与约束 |

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
| sort_order | INT | NO | 0 | 排序号 |
| status | VARCHAR(30) | NO | 'active' | 状态（V20260601012添加） |

### 3.2 扩展字段（22个）

| 字段组 | 数量 | 类型 |
|--------|:---:|------|
| ext_str1 ~ ext_str10 | 10 | VARCHAR(200) |
| ext_num1 ~ ext_num5 | 5 | DECIMAL(18,8) |
| ext_date1 ~ ext_date3 | 3 | DATE |
| ext_bool1 ~ ext_bool3 | 3 | BOOLEAN |
| ext_json | 1 | JSONB |

**字段总数：37（5业务 + 1 status + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json + 9通用[id/tenant_id已计入]）**

> 注：通用字段10个中 id 和 tenant_id 已包含在计数字段中，其余 8 个通用字段（created_by/created_at/updated_by/updated_at/is_deleted/owner_dept_id/owner_id/version）追加后总计 37 列。

---

## 四、COMMENT注释完整性验证

### 4.1 已注释字段（15个）

| 字段 | COMMENT |
|------|---------|
| id | 主键ID |
| tenant_id | 租户ID |
| parent_id | 父分类ID |
| class_code | 分类编码 |
| class_name | 分类名称 |
| sort_order | 排序号 |
| status | 状态：active=启用/inactive=停用 |
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

**结果：15/37 (40.5%) 已注释，22/37 (59.5%) 扩展字段缺少 COMMENT。**

> **影响**：PostgreSQL元数据查询和后续MyBatis-Plus代码生成依赖COMMENT注释（如 `全局规范-数据库规范` 要求）。扩展字段虽为标准化预留，但仍需标注用途以便数据字典生成。

---

## 五、索引与约束验证

### 5.1 索引清单

| 索引名 | 索引定义 | 结果 |
|--------|---------|:---:|
| pk_prod_product_class | PRIMARY KEY (id) | PASS |
| uk_prod_product_class_code | UNIQUE INDEX (code) WHERE is_deleted = false | **FAIL** |
| idx_prod_product_class_tenant_code | INDEX (tenant_id, code) | **FAIL** |
| idx_prod_product_class_tenant_status | INDEX (tenant_id, status) | PASS |
| idx_prod_product_class_parent_id | INDEX (tenant_id, parent_id) | PASS |
| idx_prod_product_class_tenant_sort | INDEX (tenant_id, sort_order) | PASS |
| idx_prod_product_class_tenant_created_at | INDEX (tenant_id, created_at) | PASS |

**2个索引FAIL — 列名不一致（详见 §5.2）。**

### 5.2 列名一致性检查（关键问题）

| 索引名 | 引用列 | DDL实际列名 | 结果 |
|--------|--------|------------|:---:|
| uk_prod_product_class_code | `code` | `class_code` | **FAIL** |
| idx_prod_product_class_tenant_code | `code` | `class_code` | **FAIL** |

> **严重性：CRITICAL** — `V20260601012__create_prod_product_class_indexes.sql` 中引用了不存在的 `code` 列（DDL中定义为 `class_code`）。执行这两个索引创建语句将导致 PostgreSQL 报错 `column "code" does not exist`，Flyway迁移将失败。

### 5.3 规范合规检查

| 检查项 | 要求 | 实际 | 结果 |
|--------|------|------|:---:|
| 部分唯一索引 WHERE is_deleted=false | 必须 | 已包含 | PASS |
| 联合索引 tenant_id 为首列 | 必须 | 4个tenant_id首列索引 | PASS |
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
| status | NO | NOT NULL | PASS |
| created_at | NO | NOT NULL | PASS |
| updated_at | NO | NOT NULL | PASS |
| is_deleted | NO | NOT NULL | PASS |
| version | NO | NOT NULL | PASS |

---

## 七、Flyway版本命名验证

| 文件 | 命名格式 | 结果 |
|------|---------|:---:|
| V20260601011__create_prod_product_class.sql | V{yyyyMMdd}{seq}__{description} | PASS |
| V20260601012__create_prod_product_class_indexes.sql | V{yyyyMMdd}{seq}__{description} | PASS |

双下划线分隔符正确，无版本号冲突。V20260601011和V20260601012序列递增无跳号。

---

## 八、验收标准对照

| 序号 | 检查项 | 验证结果 |
|:---:|--------|:---|
| 1 | DDL执行成功，所有表已创建 | CREATE TABLE语法正确，37个字段定义完整 |
| 2 | 字段类型/约束与设计100%一致 | class_code VARCHAR(50) NOT NULL, class_name VARCHAR(100) NOT NULL，均符合设计 |
| 3 | 所有索引创建成功 | **2个索引引用了不存在的 `code` 列，将在执行时失败** |
| 4 | Flyway迁移记录success=true | 版本命名符合V{yyyyMMdd}{seq}__{description}规范 |
| 5 | COMMENT注释完整 | 22个扩展字段缺少 COMMENT |

---

## 九、问题汇总与修复建议

| 序号 | 严重度 | 问题 | 影响文件 | 修复方案 |
|:---:|:---:|------|---------|---------|
| 1 | **CRITICAL** | 索引引用 `code` 列，但DDL中实际列名为 `class_code` | V20260601012 | 将 `code` 改为 `class_code`：`uk_prod_product_class_code ON prod_product_class(class_code)` 和 `idx_prod_product_class_tenant_code ON prod_product_class(tenant_id, class_code)` |
| 2 | WARNING | 22个扩展字段缺少 COMMENT | V20260601011 | 为 ext_* 字段补充 COMMENT 注释 |
| 3 | INFO | 索引名 `uk_prod_product_class_code` 和 `idx_prod_product_class_tenant_code` 含 `code`，修正列名后建议同步更新索引名为 `*_class_code` 以保持一致 | V20260601012 | 可选：重命名为 `uk_prod_product_class_class_code` / `idx_prod_product_class_tenant_class_code`，或保留现有名称（列名修正后索引即可正常工作） |

> **说明**：问题1（列名不匹配）是阻塞性问题，必须修复后才能执行Flyway迁移。问题2（扩展字段缺少COMMENT）与org_company/org_department/org_position/org_employee表一致，属于统一模式。

---

## 十、总体结论

**验证结果：不通过（1个CRITICAL阻塞性问题 + 1个WARNING建议改进项）**

- **CRITICAL（阻塞）**：1个 — 索引文件引用不存在的 `code` 列，DDL实际列名为 `class_code`，将导致Flyway迁移失败
- **WARNING**：1个 — 22个扩展字段缺少 COMMENT（与其他表一致的统一模式）
- **PASS**：10个通用字段完整、decimal精度正确、Flyway命名规范、无外键约束、NOT NULL约束正确、tenant_id首列索引规范
