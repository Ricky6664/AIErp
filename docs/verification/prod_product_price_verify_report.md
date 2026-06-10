# prod_product_price 商品价格表DDL验证报告

> **生成日期**：2026-06-01
> **验证任务**：P0-003-004-014-001-003
> **验证对象**：prod_product_price 商品价格表DDL（CREATE TABLE + 索引与约束）

---

## 一、验证范围

| 文件 | 版本号 | 说明 |
|------|--------|------|
| V20260601048__create_prod_product_price.sql | V20260601048 | CREATE TABLE 建表语句 |
| V20260601049__create_prod_product_price_indexes.sql | V20260601049 | 索引与约束 |

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

### 3.1 业务字段（12个）

| 字段 | 类型 | NOT NULL | 默认值 | 说明 |
|------|------|:---:|--------|------|
| code | VARCHAR(50) | YES | — | 价格编码 |
| name | VARCHAR(200) | YES | — | 价格名称 |
| product_id | BIGINT | YES | — | 商品ID |
| price | DECIMAL(18,8) | YES | — | 价格 |
| price_type | VARCHAR(50) | — | — | 价格类型（wholesale-批发价/retail-零售价/vip-VIP价等） |
| currency_code | VARCHAR(10) | YES | 'CNY' | 币种代码 |
| unit_id | BIGINT | — | — | 单位ID |
| is_default | BOOLEAN | YES | FALSE | 是否默认价格（0-否/1-是） |
| effective_date | DATE | — | — | 生效日期 |
| expiry_date | DATE | — | — | 失效日期 |
| status | SMALLINT | YES | 0 | 状态（0-草稿/1-已生效/2-已失效） |
| remark | VARCHAR(500) | — | — | 备注 |

**字段总数：22（12业务 + 10通用）**

---

## 四、COMMENT注释完整性验证

### 4.1 表注释

| 表名 | COMMENT |
|------|---------|
| prod_product_price | 商品价格表 |

### 4.2 已注释字段（22个）

| 字段 | COMMENT |
|------|---------|
| id | 主键ID |
| tenant_id | 租户ID |
| code | 价格编码 |
| name | 价格名称 |
| product_id | 商品ID |
| price | 价格 |
| price_type | 价格类型（wholesale-批发价/retail-零售价/vip-VIP价等） |
| currency_code | 币种代码 |
| unit_id | 单位ID |
| is_default | 是否默认价格（0-否/1-是） |
| effective_date | 生效日期 |
| expiry_date | 失效日期 |
| status | 状态（0-草稿/1-已生效/2-已失效） |
| remark | 备注 |
| created_at | 创建时间 |
| updated_at | 更新时间 |
| created_by | 创建人ID |
| updated_by | 修改人ID |
| is_deleted | 是否删除 |
| owner_dept_id | 所属部门ID |
| owner_id | 数据负责人ID |
| version | 版本号 |

**注释覆盖率：22/22（100%）—— PASS**

---

## 五、数值精度验证（DECIMAL(18,8)）

| 字段 | 类型 | 精度 | 标度 | 结果 |
|------|------|:---:|:---:|:---:|
| price | DECIMAL(18,8) | 18 | 8 | PASS |

**所有金额/单价字段统一使用DECIMAL(18,8)，符合数值精度约束。**

---

## 六、NOT NULL约束验证

| 字段 | NOT NULL | 结果 |
|------|:---:|:---:|
| id | YES (PK) | PASS |
| tenant_id | YES | PASS |
| code | YES | PASS |
| name | YES | PASS |
| product_id | YES | PASS |
| price | YES | PASS |
| currency_code | YES | PASS |
| is_default | YES | PASS |
| status | YES | PASS |
| created_at | YES | PASS |
| updated_at | YES | PASS |
| is_deleted | YES | PASS |
| version | YES | PASS |

---

## 七、索引验证

### 7.1 索引清单（12个）

| 序号 | 索引名 | 类型 | 列 | 附加条件 | 命名规范 |
|:---:|--------|:---:|-----|------|:---:|
| 1 | pk_prod_product_price | PK | id | — | PASS |
| 2 | uk_prod_product_price_code | UNIQUE | code | WHERE is_deleted = false | PASS |
| 3 | idx_prod_product_price_tenant_code | INDEX | tenant_id, code | — | PASS |
| 4 | idx_prod_product_price_tenant_status | INDEX | tenant_id, status | — | PASS |
| 5 | idx_prod_product_price_product_id | INDEX | product_id | — | PASS |
| 6 | idx_prod_product_price_unit_id | INDEX | unit_id | — | PASS |
| 7 | idx_prod_product_price_status | INDEX | status | — | PASS |
| 8 | idx_prod_product_price_price_type | INDEX | price_type | — | PASS |
| 9 | idx_prod_product_price_currency_code | INDEX | currency_code | — | PASS |
| 10 | idx_prod_product_price_is_default | INDEX | is_default | — | PASS |
| 11 | idx_prod_product_price_effective_date | INDEX | effective_date | — | PASS |
| 12 | idx_prod_product_price_expiry_date | INDEX | expiry_date | — | PASS |

**索引总数：12（1 PK + 1 UK + 10 普通索引）—— PASS**

### 7.2 部分唯一索引验证

| 索引 | WHERE条件 | 结果 |
|------|------|:---:|
| uk_prod_product_price_code | WHERE is_deleted = false | PASS |

**部分唯一索引正确包含 `WHERE is_deleted = false` 条件，避免boolean字段联合唯一索引陷阱。**

### 7.3 多租户索引验证

| 索引 | 首列 | 结果 |
|------|:---:|:---:|
| idx_prod_product_price_tenant_code | tenant_id | PASS |
| idx_prod_product_price_tenant_status | tenant_id | PASS |

**联合索引均以tenant_id为首列，符合多租户隔离查询性能要求。**

---

## 八、外键约束检查

| 检查项 | 期望 | 实际 | 结果 |
|--------|:---:|:---:|:---:|
| 外键约束数量 | 0 | 0 | PASS |

**无数据库外键约束，关联关系由应用层维护 —— PASS**

---

## 九、易错警示逐项验证

| 序号 | 警示项 | 验证结果 |
|:---:|--------|:---:|
| 1 | 通用字段10个完整包含 | ✅ id/tenant_id/created_at/updated_at/created_by/updated_by/is_deleted/owner_dept_id/owner_id/version 全部存在 |
| 2 | 部分唯一索引含WHERE is_deleted=false | ✅ uk_prod_product_price_code 正确包含 |
| 3 | 金额/单价统一decimal(18,8) | ✅ price 为 DECIMAL(18,8) |
| 4 | 联合索引tenant_id为首列 | ✅ idx_prod_product_price_tenant_code 和 tenant_status |
| 5 | Flyway命名规范 | ✅ V20260601048 / V20260601049 双下划线 |
| 6 | 所有字段含COMMENT | ✅ 22/22字段全部注释 |
| 7 | 禁止外键约束 | ✅ 无外键约束 |

---

## 十、验证汇总

| 验证类别 | 检查项数 | 通过 | 失败 | 状态 |
|---------|:---:|:---:|:---:|:---:|
| 表存在性 | 1 | 1 | 0 | ✅ |
| 通用字段完整性 | 10 | 10 | 0 | ✅ |
| 字段总数 | 1 | 1 | 0 | ✅ |
| 主键约束 | 1 | 1 | 0 | ✅ |
| COMMENT注释 | 23 | 23 | 0 | ✅ |
| 数值精度 | 1 | 1 | 0 | ✅ |
| NOT NULL约束 | 13 | 13 | 0 | ✅ |
| 索引完整性 | 12 | 12 | 0 | ✅ |
| 部分唯一索引 | 1 | 1 | 0 | ✅ |
| 多租户索引 | 2 | 2 | 0 | ✅ |
| 外键约束 | 1 | 1 | 0 | ✅ |
| Flyway版本 | 3 | 3 | 0 | ✅ |
| 索引命名规范 | 12 | 12 | 0 | ✅ |
| **总计** | **81** | **81** | **0** | **✅ PASS** |

---

> **结论**：prod_product_price 商品价格表DDL验证全部通过，22个字段定义正确，12个索引创建规范，COMMENT注释完整，数值精度一致，部分唯一索引条件正确，多租户索引结构合规。
