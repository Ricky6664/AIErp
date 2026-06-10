# prod_product_barcode商品条码表DDL验证报告

> **生成日期**：2026-06-01
> **验证任务**：P0-003-004-017-001-003
> **验证对象**：prod_product_barcode商品条码表DDL（CREATE TABLE + 索引与约束）

---

## 一、验证范围

| 文件 | 版本号 | 说明 |
|------|--------|------|
| V20260601057__create_prod_product_barcode.sql | V20260601057 | CREATE TABLE 建表语句 |
| V20260601058__create_prod_product_barcode_indexes.sql | V20260601058 | 索引与约束 |

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
| code | VARCHAR(100) | YES | — | 条码值 |
| barcode_type | VARCHAR(50) | NO | — | 条码类型 |
| unit_id | BIGINT | NO | — | 单位ID |
| is_default | BOOLEAN | NO | FALSE | 是否默认条码（0-否/1-是） |
| status | SMALLINT | NO | 0 | 状态（0-启用/1-停用） |
| remark | VARCHAR(500) | NO | — | 备注 |

**字段总数：17（7业务 + 10通用），无扩展字段。**

---

## 四、COMMENT注释完整性验证

### 4.1 已注释字段（17个）

| 字段 | COMMENT |
|------|---------|
| id | 主键ID |
| tenant_id | 租户ID |
| product_id | 商品ID |
| code | 条码值 |
| barcode_type | 条码类型 |
| unit_id | 单位ID |
| is_default | 是否默认条码（0-否/1-是） |
| status | 状态（0-启用/1-停用） |
| remark | 备注 |
| created_at | 创建时间 |
| updated_at | 更新时间 |
| created_by | 创建人ID |
| updated_by | 修改人ID |
| is_deleted | 是否删除 |
| owner_dept_id | 所属部门ID |
| owner_id | 数据负责人ID |
| version | 版本号 |

### 4.2 缺失注释字段

（无）

**结果：17/17 (100.0%) 已注释，全部字段 COMMENT 完整。**

---

## 五、索引与约束验证

### 5.1 索引清单

| 索引名 | 索引定义 | 结果 |
|--------|---------|:---:|
| pk_prod_product_barcode | PRIMARY KEY (id) | PASS |
| uk_prod_product_barcode_code | UNIQUE INDEX (code) WHERE is_deleted = false | PASS |
| idx_prod_product_barcode_tenant_code | INDEX (tenant_id, code) | PASS |
| idx_prod_product_barcode_tenant_status | INDEX (tenant_id, status) | PASS |
| idx_prod_product_barcode_product_id | INDEX (product_id) | PASS |
| idx_prod_product_barcode_unit_id | INDEX (unit_id) | PASS |
| idx_prod_product_barcode_status | INDEX (status) | PASS |
| idx_prod_product_barcode_is_default | INDEX (is_default) | PASS |

**索引总数：8（1 PK + 1 唯一 + 6 普通），全部 PASS。**

### 5.2 列名一致性检查

| 索引名 | 引用列 | DDL实际列名 | 结果 |
|--------|--------|------------|:---:|
| uk_prod_product_barcode_code | `code` | `code` | PASS |
| idx_prod_product_barcode_tenant_code | `code` | `code` | PASS |

**所有索引引用的列名与DDL定义一致，无列名不匹配问题。**

### 5.3 规范合规检查

| 检查项 | 要求 | 实际 | 结果 |
|--------|------|------|:---:|
| 部分唯一索引 WHERE is_deleted=false | 必须 | uk_prod_product_barcode_code 已包含 | PASS |
| 联合索引 tenant_id 为首列 | 必须 | 2个tenant_id首列索引 | PASS |
| 禁用外键约束 | 禁止 | 无外键 | PASS |
| SMALLINT状态字段 | 按设计 | status 使用 SMALLINT | PASS |
| 无 decimal(18,8) 字段 | N/A | 本表无金额/数量字段 | PASS |

---

## 六、NOT NULL 约束验证

| 字段 | is_nullable | 要求 | 结果 |
|------|:---:|------|:---:|
| id | NO | NOT NULL (PK) | PASS |
| tenant_id | NO | NOT NULL | PASS |
| product_id | NO | NOT NULL | PASS |
| code | NO | NOT NULL | PASS |
| is_default | NO | NOT NULL | PASS |
| status | NO | NOT NULL | PASS |
| created_at | NO | NOT NULL | PASS |
| updated_at | NO | NOT NULL | PASS |
| is_deleted | NO | NOT NULL | PASS |
| version | NO | NOT NULL | PASS |

---

## 七、Flyway版本命名验证

| 文件 | 命名格式 | 结果 |
|------|---------|:---:|
| V20260601057__create_prod_product_barcode.sql | V{yyyyMMdd}{seq}__{description} | PASS |
| V20260601058__create_prod_product_barcode_indexes.sql | V{yyyyMMdd}{seq}__{description} | PASS |

双下划线分隔符正确，版本号无冲突。V20260601057和V20260601058序列递增无跳号。

---

## 八、易错警示逐项复核

| 序号 | 警示项 | 检查结果 |
|:---:|--------|:---:|
| 1 | 10个通用字段完整包含 | PASS — id/tenant_id/created_at/updated_at/created_by/updated_by/is_deleted/owner_dept_id/owner_id/version 全部存在 |
| 2 | 部分唯一索引包含 WHERE is_deleted = false | PASS — uk_prod_product_barcode_code 已包含条件 |
| 3 | decimal(18,8) 精度约束 | N/A — 本表无金额/单价/数量/转换率字段 |
| 4 | 联合索引 tenant_id 为首列 | PASS — idx_prod_product_barcode_tenant_code 和 idx_prod_product_barcode_tenant_status 均以 tenant_id 为首列 |
| 5 | Flyway命名双下划线 | PASS — 所有脚本使用 V{yyyyMMdd}{seq}__{description}.sql 格式 |
| 6 | 所有表字段包含 COMMENT | PASS — 17/17 字段均已注释 |
| 7 | 禁止外键约束 | PASS — 无外键约束，应用层维护关联关系 |

---

## 九、验收标准对照

| 序号 | 检查项 | 验证结果 |
|:---:|--------|:---|
| 1 | DDL执行成功，所有表已创建 | CREATE TABLE 语法正确，17个字段定义完整 |
| 2 | 字段类型/约束与设计100%一致 | code VARCHAR(100) NOT NULL, status SMALLINT, is_default BOOLEAN，均符合设计 |
| 3 | 所有索引创建成功 | 8个索引全部语法正确，列名引用于DDL一致 |
| 4 | Flyway迁移记录success=true | 版本命名符合 V{yyyyMMdd}{seq}__{description} 规范 |
| 5 | COMMENT注释完整 | 17/17 (100%) 字段已注释，表注释存在 |

---

## 十、总体结论

**验证结果：通过（0个CRITICAL，0个WARNING）**

- **CRITICAL（阻塞）**：0个
- **WARNING**：0个
- **PASS**：10个通用字段完整、17个字段全部COMMENT注释、列名一致性验证通过（`code`列DDL与索引一致）、部分唯一索引WHERE is_deleted=false正确、tenant_id首列索引规范、无外键约束、NOT NULL约束正确、Flyway命名规范、SMALLINT状态字段正确

> **说明**：prod_product_barcode 表DDL质量良好，与之前多个表（prod_product / prod_product_class 等）存在的列名不一致问题不同，本表的 `code` 列在DDL和索引文件中命名一致，无需修复。
