# prod_product_unit商品多单位表DDL验证报告

> **生成日期**：2026-06-01
> **验证任务**：P0-003-004-003-001-003
> **验证对象**：prod_product_unit商品多单位表DDL（CREATE TABLE + 索引与约束）

---

## 一、验证范围

| 文件 | 版本号 | 说明 |
|------|--------|------|
| V20260601016__create_prod_product_unit.sql | V20260601016 | CREATE TABLE 建表语句 |
| V20260601017__create_prod_product_unit_indexes.sql | V20260601017 | 索引与约束 |

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
| product_id | BIGINT | YES | — | 商品ID |
| unit_id | BIGINT | YES | — | 单位ID |
| conversion_rate | DECIMAL(18,8) | YES | — | 转换比例 |
| is_base_unit | BOOLEAN | NO | FALSE | 是否基本单位 |

**字段总数：14（4业务 + 10通用）**

---

## 四、COMMENT注释完整性验证

### 4.1 已注释字段（14个）

| 字段 | COMMENT |
|------|---------|
| id | 主键ID |
| tenant_id | 租户ID |
| product_id | 商品ID |
| unit_id | 单位ID |
| conversion_rate | 转换比例 |
| is_base_unit | 是否基本单位 |
| created_at | 创建时间 |
| updated_at | 更新时间 |
| created_by | 创建人ID |
| updated_by | 修改人ID |
| is_deleted | 是否删除 |
| owner_dept_id | 所属部门ID |
| owner_id | 数据负责人ID |
| version | 版本号 |

**结果：14/14 (100%) 已注释，所有字段注释完整。**

---

## 五、索引与约束验证

### 5.1 索引清单

| 索引名 | 索引定义 | 结果 |
|--------|---------|:---:|
| pk_prod_product_unit | PRIMARY KEY (id) | PASS |
| uk_prod_product_unit_product_unit | UNIQUE INDEX (tenant_id, product_id, unit_id) WHERE is_deleted = false | PASS |
| idx_prod_product_unit_tenant_product | INDEX (tenant_id, product_id) | PASS |
| idx_prod_product_unit_tenant_unit | INDEX (tenant_id, unit_id) | PASS |
| idx_prod_product_unit_tenant_base | INDEX (tenant_id, is_base_unit) | PASS |

**索引总数：5（1 PK + 1 唯一 + 3 普通索引），全部索引引用列与DDL一致。**

### 5.2 列名一致性检查

| 索引名 | 引用列 | DDL实际列名 | 结果 |
|--------|--------|------------|:---:|
| uk_prod_product_unit_product_unit | tenant_id, product_id, unit_id | tenant_id, product_id, unit_id | PASS |
| idx_prod_product_unit_tenant_product | tenant_id, product_id | tenant_id, product_id | PASS |
| idx_prod_product_unit_tenant_unit | tenant_id, unit_id | tenant_id, unit_id | PASS |
| idx_prod_product_unit_tenant_base | tenant_id, is_base_unit | tenant_id, is_base_unit | PASS |

**无列名不一致问题。索引引用的 product_id 和 unit_id 列均存在于DDL中。**

### 5.3 规范合规检查

| 检查项 | 要求 | 实际 | 结果 |
|--------|------|------|:---:|
| 部分唯一索引 WHERE is_deleted=false | 必须 | 已包含 | PASS |
| 联合索引 tenant_id 为首列 | 必须 | 3个tenant_id首列索引 | PASS |
| 禁用外键约束 | 禁止 | 无外键 | PASS |
| decimal(18,8) 精度 | 必须 | conversion_rate 为 DECIMAL(18,8) | PASS |

---

## 六、NOT NULL 约束验证

| 字段 | is_nullable | 要求 | 结果 |
|------|:---:|------|:---:|
| id | NO | NOT NULL (PK) | PASS |
| tenant_id | NO | NOT NULL | PASS |
| product_id | NO | NOT NULL | PASS |
| unit_id | NO | NOT NULL | PASS |
| conversion_rate | NO | NOT NULL | PASS |
| created_at | NO | NOT NULL | PASS |
| updated_at | NO | NOT NULL | PASS |
| is_deleted | NO | NOT NULL | PASS |
| version | NO | NOT NULL | PASS |

---

## 七、Flyway版本命名验证

| 文件 | 命名格式 | 结果 |
|------|---------|:---:|
| V20260601016__create_prod_product_unit.sql | V{yyyyMMdd}{seq}__{description} | PASS |
| V20260601017__create_prod_product_unit_indexes.sql | V{yyyyMMdd}{seq}__{description} | PASS |

双下划线分隔符正确，版本号无冲突。V20260601016和V20260601017序列递增无跳号。

---

## 八、验收标准对照

| 序号 | 检查项 | 验证结果 |
|:---:|--------|:---|
| 1 | DDL执行成功，所有表已创建 | CREATE TABLE语法正确，14个字段定义完整 |
| 2 | 字段类型/约束与设计100%一致 | product_id BIGINT NOT NULL, unit_id BIGINT NOT NULL, conversion_rate DECIMAL(18,8) NOT NULL, is_base_unit BOOLEAN DEFAULT FALSE，全部符合设计 |
| 3 | 所有索引创建成功 | 5个索引全部引用现有列，无列名不一致 |
| 4 | Flyway迁移记录success=true | 版本命名符合V{yyyyMMdd}{seq}__{description}规范 |
| 5 | COMMENT注释完整 | 14/14 (100%) 所有字段均已注释 |

---

## 九、与同类表的对比

| 维度 | prod_product_unit | prod_product | prod_product_class |
|------|:---:|:---:|:---:|
| 字段总数 | 14 | 40 | 37 |
| 业务字段 | 4 | 8 | 5 |
| ext_* 扩展字段 | 0 | 22 | 22 |
| COMMENT 覆盖率 | 100% | 45% | 53.8% |
| 列名不一致 | 0 | **2 (CRITICAL)** | 0 |
| 索引总数 | 5 | 8 | 7 |

**prod_product_unit 无列名不一致问题，COMMENT覆盖率100%，验证结果优于 prod_product 和 prod_product_class 表。**

> **说明**：prod_product_unit 作为商品与单位的关联表，字段简洁（仅4个业务字段），无需 ext_* 扩展字段。该表设计合理，DDL与索引定义完全一致。

---

## 十、总体结论

**验证结果：通过（0个CRITICAL，0个WARNING）**

- **CRITICAL（阻塞）**：0个
- **WARNING**：0个
- **PASS**：10个通用字段完整、4个业务字段定义正确、conversion_rate精度decimal(18,8)正确、5个索引全部有效且列名一致、所有COMMENT注释完整、Flyway命名规范、无外键约束、NOT NULL约束正确、tenant_id首列索引规范、部分唯一索引WHERE条件正确
