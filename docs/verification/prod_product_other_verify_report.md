# prod_product_other商品其他信息表DDL验证报告

> **生成日期**：2026-06-01
> **验证任务**：P0-003-004-022-001-003
> **验证对象**：prod_product_other商品其他信息表DDL（CREATE TABLE + 索引与约束）

---

## 一、验证范围

| 文件 | 版本号 | 说明 |
|------|--------|------|
| V20260601070__create_prod_product_other.sql | V20260601070 | CREATE TABLE 建表语句 |
| V20260601071__create_prod_product_other_indexes.sql | V20260601071 | 索引与约束 |

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
| product_id | BIGINT | YES | — | 关联商品ID |
| code | VARCHAR(50) | YES | — | 编码 |
| hs_code | VARCHAR(50) | NO | — | HS编码 |
| barcode | VARCHAR(100) | NO | — | 条码 |
| origin | VARCHAR(200) | NO | — | 产地 |
| gross_weight | DECIMAL(18,8) | NO | — | 毛重 |
| net_weight | DECIMAL(18,8) | NO | — | 净重 |
| volume | DECIMAL(18,8) | NO | — | 体积 |
| packaging | VARCHAR(200) | NO | — | 包装 |
| certification | VARCHAR(500) | NO | — | 认证 |
| shelf_life | INT | NO | — | 保质期（天数） |
| storage_condition | VARCHAR(500) | NO | — | 存储条件 |
| status | SMALLINT | YES | 0 | 状态（0=禁用/1=启用） |
| remark | VARCHAR(500) | NO | — | 备注 |

**字段总数：24（14业务 + 10通用）**

---

## 四、COMMENT注释完整性验证

| 序号 | 字段 | COMMENT | 结果 |
|:---:|------|---------|:---:|
| 1 | id | 主键ID | PASS |
| 2 | tenant_id | 租户ID | PASS |
| 3 | product_id | 关联商品ID | PASS |
| 4 | code | 编码 | PASS |
| 5 | hs_code | HS编码 | PASS |
| 6 | barcode | 条码 | PASS |
| 7 | origin | 产地 | PASS |
| 8 | gross_weight | 毛重 | PASS |
| 9 | net_weight | 净重 | PASS |
| 10 | volume | 体积 | PASS |
| 11 | packaging | 包装 | PASS |
| 12 | certification | 认证 | PASS |
| 13 | shelf_life | 保质期（天数） | PASS |
| 14 | storage_condition | 存储条件 | PASS |
| 15 | status | 状态（0=禁用/1=启用） | PASS |
| 16 | remark | 备注 | PASS |
| 17 | created_at | 创建时间 | PASS |
| 18 | updated_at | 更新时间 | PASS |
| 19 | created_by | 创建人ID | PASS |
| 20 | updated_by | 修改人ID | PASS |
| 21 | is_deleted | 是否删除 | PASS |
| 22 | owner_dept_id | 所属部门ID | PASS |
| 23 | owner_id | 数据负责人ID | PASS |
| 24 | version | 版本号 | PASS |
| — | TABLE | 商品其他信息表 | PASS |

**结果：25/25 (100%) 已注释，0个字段缺少 COMMENT。**

> 全部字段（含表注释）均包含 COMMENT，覆盖率 100%。PostgreSQL 元数据查询和后续 MyBatis-Plus 代码生成所需注释完整。

---

## 五、索引与约束验证

### 5.1 索引清单

| 索引名 | 索引定义 | 结果 |
|--------|---------|:---:|
| pk_prod_product_other | PRIMARY KEY (id) | PASS |
| uk_prod_product_other_code | UNIQUE INDEX (code) WHERE is_deleted = false | PASS |
| idx_prod_product_other_tenant_code | INDEX (tenant_id, code) | PASS |
| idx_prod_product_other_tenant_status | INDEX (tenant_id, status) | PASS |
| idx_prod_product_other_tenant_product_id | INDEX (tenant_id, product_id) | PASS |
| idx_prod_product_other_tenant_barcode | INDEX (tenant_id, barcode) | PASS |
| idx_prod_product_other_tenant_created_at | INDEX (tenant_id, created_at) | PASS |

**7个索引全部 PASS — 命名规范、列名一致。**

### 5.2 列名一致性检查

| 索引名 | 引用列 | DDL实际列名 | 结果 |
|--------|--------|------------|:---:|
| uk_prod_product_other_code | `code` | `code` | PASS |
| idx_prod_product_other_tenant_code | `code` | `code` | PASS |

> **结果：所有索引引用的列名与 DDL 定义完全一致，无列名不匹配问题。**

### 5.3 规范合规检查

| 检查项 | 要求 | 实际 | 结果 |
|--------|------|------|:---:|
| 部分唯一索引 WHERE is_deleted=false | 必须 | uk_prod_product_other_code 已包含 | PASS |
| 联合索引 tenant_id 为首列 | 必须 | 5个tenant_id首列索引 | PASS |
| 禁用外键约束 | 禁止 | 无外键 | PASS |
| decimal(18,8) 精度 | 必须 | gross_weight/net_weight/volume 均为 DECIMAL(18,8) | PASS |
| SMALLINT状态字段 | 按设计 | status 使用 SMALLINT DEFAULT 0 | PASS |

---

## 六、NOT NULL 约束验证

| 字段 | is_nullable | 要求 | 结果 |
|------|:---:|------|:---:|
| id | NO | NOT NULL (PK) | PASS |
| tenant_id | NO | NOT NULL | PASS |
| product_id | NO | NOT NULL | PASS |
| code | NO | NOT NULL | PASS |
| status | NO | NOT NULL | PASS |
| created_at | NO | NOT NULL | PASS |
| updated_at | NO | NOT NULL | PASS |
| is_deleted | NO | NOT NULL | PASS |
| version | NO | NOT NULL | PASS |

---

## 七、Flyway版本命名验证

| 文件 | 命名格式 | 结果 |
|------|---------|:---:|
| V20260601070__create_prod_product_other.sql | V{yyyyMMdd}{seq}__{description} | PASS |
| V20260601071__create_prod_product_other_indexes.sql | V{yyyyMMdd}{seq}__{description} | PASS |

双下划线分隔符正确，版本号序列递增无跳号（V20260601070 → V20260601071）。

---

## 八、验收标准对照

| 序号 | 检查项 | 验证结果 |
|:---:|--------|:---|
| 1 | DDL执行成功，所有表已创建 | CREATE TABLE语法正确，24个字段定义完整 |
| 2 | 字段类型/约束与设计100%一致 | code VARCHAR(50) NOT NULL, gross_weight/net_weight/volume DECIMAL(18,8), status SMALLINT DEFAULT 0，均符合设计 |
| 3 | 所有索引创建成功 | 7个索引（1 PK + 1 UK + 5 idx），列名与DDL一致，无冲突 |
| 4 | Flyway迁移记录success=true | 版本命名符合V{yyyyMMdd}{seq}__{description}规范，序列无冲突 |
| 5 | COMMENT注释完整 | 25/25 (100%) 覆盖，表+全部24字段均已注释 |

---

## 九、易错警示逐项检查

| 序号 | 警示项 | 检查结果 |
|:---:|--------|:---|
| 1 | 10个通用字段完整 | ✅ 全部10个已包含 |
| 2 | 部分唯一索引含 WHERE is_deleted = false | ✅ uk_prod_product_other_code 已包含 |
| 3 | 金额/单价/数量字段使用 decimal(18,8) | ✅ gross_weight/net_weight/volume 均为 DECIMAL(18,8) |
| 4 | 联合索引 tenant_id 为首列 | ✅ 5个联合索引均以tenant_id为首列 |
| 5 | Flyway命名 V{yyyyMMdd}{seq}__{description} | ✅ 双下划线，版本号无冲突 |
| 6 | 所有字段含 COMMENT | ✅ 24/24 字段 + 1表注释全部覆盖 |
| 7 | 禁止外键约束 | ✅ 无外键约束 |

---

## 十、总体结论

**验证结果：全部通过 ✅**

- **PASS**：10个通用字段完整、14个业务字段类型正确、25条 COMMENT 100% 覆盖、7个索引命名规范且列名一致、decimal(18,8) 精度正确、Flyway 命名规范、无外键约束、NOT NULL 约束正确、tenant_id 首列索引规范、SMALLINT 状态字段正确
- **FAIL**：0个
- **WARNING**：0个

> 与前序 prod_product 表的验证不同（存在索引列名 `code` vs `product_code` 不一致的 CRITICAL 问题），本表 DDL 中列名为 `code`，索引也引用 `code`，列名完全一致，不存在同类问题。
