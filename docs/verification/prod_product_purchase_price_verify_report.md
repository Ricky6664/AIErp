# prod_product_purchase_price商品购价核定表DDL验证报告

> **生成日期**：2026-06-01
> **验证任务**：P0-003-004-008-001-003
> **验证对象**：prod_product_purchase_price商品购价核定表DDL（CREATE TABLE + 索引与约束）

---

## 一、验证范围

| 文件 | 版本号 | 说明 |
|------|--------|------|
| V20260601030__create_prod_product_purchase_price.sql | V20260601030 | CREATE TABLE 建表语句 |
| V20260601031__create_prod_product_purchase_price_indexes.sql | V20260601031 | 索引与约束 |

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
| supplier_id | BIGINT | — | — | 供应商ID |
| purchase_price | DECIMAL(18,8) | YES | — | 采购价格 |
| tax_inclusive_price | DECIMAL(18,8) | — | — | 含税单价 |
| tax_exclusive_price | DECIMAL(18,8) | — | — | 不含税单价 |
| tax_rate | DECIMAL(18,8) | — | — | 税率(%) |
| currency_code | VARCHAR(10) | YES | 'CNY' | 币种代码 |
| unit_id | BIGINT | — | — | 单位ID |
| is_default | BOOLEAN | YES | FALSE | 是否默认价格（0-否/1-是） |
| effective_date | DATE | — | — | 生效日期 |
| expiry_date | DATE | — | — | 失效日期 |
| status | SMALLINT | YES | 0 | 状态（0-草稿/1-已生效/2-已失效） |
| remark | VARCHAR(500) | — | — | 备注 |

**字段总数：23（13业务 + 10通用）**

---

## 四、COMMENT注释完整性验证

### 4.1 表注释

| 表名 | COMMENT |
|------|---------|
| prod_product_purchase_price | 商品购价核定表 |

### 4.2 已注释字段（23个）

| 字段 | COMMENT |
|------|---------|
| id | 主键ID |
| tenant_id | 租户ID |
| product_id | 商品ID |
| supplier_id | 供应商ID |
| purchase_price | 采购价格 |
| tax_inclusive_price | 含税单价 |
| tax_exclusive_price | 不含税单价 |
| tax_rate | 税率(%) |
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

**结果：23/23 (100%) 已注释，所有字段注释完整。表注释也已添加：'商品购价核定表'。**

---

## 五、索引与约束验证

### 5.1 索引清单

| 索引名 | 索引定义 | 结果 |
|--------|---------|:---:|
| pk_prod_product_purchase_price | PRIMARY KEY (id) | PASS |
| uk_prod_product_purchase_price_tenant_product_supplier | UNIQUE INDEX (tenant_id, product_id, supplier_id) WHERE is_deleted = false | PASS |
| idx_prod_product_purchase_price_tenant_product | INDEX (tenant_id, product_id) | PASS |
| idx_prod_product_purchase_price_tenant_status | INDEX (tenant_id, status) | PASS |
| idx_prod_product_purchase_price_tenant_currency | INDEX (tenant_id, currency_code) | PASS |
| idx_prod_product_purchase_price_tenant_supplier | INDEX (tenant_id, supplier_id) | PASS |
| idx_prod_product_purchase_price_product_id | INDEX (product_id) | PASS |
| idx_prod_product_purchase_price_supplier_id | INDEX (supplier_id) | PASS |
| idx_prod_product_purchase_price_unit_id | INDEX (unit_id) | PASS |
| idx_prod_product_purchase_price_status | INDEX (status) | PASS |
| idx_prod_product_purchase_price_effective_date | INDEX (effective_date) | PASS |
| idx_prod_product_purchase_price_is_default | INDEX (is_default) | PASS |

**索引总数：12（1 PK + 1 UK + 10 普通索引），全部索引引用列与DDL一致。**

### 5.2 列名一致性检查

| 索引名 | 引用列 | DDL实际列名 | 结果 |
|--------|--------|------------|:---:|
| uk_prod_product_purchase_price_tenant_product_supplier | tenant_id, product_id, supplier_id | tenant_id, product_id, supplier_id | PASS |
| idx_prod_product_purchase_price_tenant_product | tenant_id, product_id | tenant_id, product_id | PASS |
| idx_prod_product_purchase_price_tenant_status | tenant_id, status | tenant_id, status | PASS |
| idx_prod_product_purchase_price_tenant_currency | tenant_id, currency_code | tenant_id, currency_code | PASS |
| idx_prod_product_purchase_price_tenant_supplier | tenant_id, supplier_id | tenant_id, supplier_id | PASS |
| idx_prod_product_purchase_price_product_id | product_id | product_id | PASS |
| idx_prod_product_purchase_price_supplier_id | supplier_id | supplier_id | PASS |
| idx_prod_product_purchase_price_unit_id | unit_id | unit_id | PASS |
| idx_prod_product_purchase_price_status | status | status | PASS |
| idx_prod_product_purchase_price_effective_date | effective_date | effective_date | PASS |
| idx_prod_product_purchase_price_is_default | is_default | is_default | PASS |

**无列名不一致问题。**

### 5.3 规范合规检查

| 检查项 | 要求 | 实际 | 结果 |
|--------|------|------|:---:|
| 部分唯一索引 WHERE is_deleted=false | 必须 | uk_prod_product_purchase_price_tenant_product_supplier 已包含 | PASS |
| 联合索引 tenant_id 为首列 | 必须 | 4个tenant_id首列索引 | PASS |
| 禁用外键约束 | 禁止 | 无外键 | PASS |
| DECIMAL(18,8)字段精度正确 | 条件 | 4个金额/单价/税率字段均使用DECIMAL(18,8) | PASS |
| 部分唯一索引同一租户同一商品同一供应商仅一条购价记录 | 业务规则 | uk ON (tenant_id, product_id, supplier_id) WHERE is_deleted=false | PASS |
| 主键索引命名为规范格式 pk_{表名} | 规范 | pk_prod_product_purchase_price（已重命名） | PASS |

---

## 六、NOT NULL 约束验证

| 字段 | is_nullable | 要求 | 结果 |
|------|:---:|------|:---:|
| id | NO | NOT NULL (PK) | PASS |
| tenant_id | NO | NOT NULL | PASS |
| product_id | NO | NOT NULL | PASS |
| purchase_price | NO | NOT NULL | PASS |
| currency_code | NO | NOT NULL | PASS |
| is_default | NO | NOT NULL | PASS |
| status | NO | NOT NULL | PASS |
| created_at | NO | NOT NULL | PASS |
| updated_at | NO | NOT NULL | PASS |
| is_deleted | NO | NOT NULL | PASS |
| version | NO | NOT NULL | PASS |

---

## 七、DECIMAL(18,8)精度字段专项验证

| 字段 | 类型 | 精度(P) | 小数位(S) | 业务语义 | 结果 |
|------|------|:---:|:---:|------|:---:|
| purchase_price | DECIMAL(18,8) | 18 | 8 | 采购价格（金额类） | PASS |
| tax_inclusive_price | DECIMAL(18,8) | 18 | 8 | 含税单价（单价类） | PASS |
| tax_exclusive_price | DECIMAL(18,8) | 18 | 8 | 不含税单价（单价类） | PASS |
| tax_rate | DECIMAL(18,8) | 18 | 8 | 税率（百分比类） | PASS |

**4个金额/单价/税率字段均使用DECIMAL(18,8)，物理精度统一，显示精度由系统参数控制。**

---

## 八、Flyway版本命名验证

| 文件 | 命名格式 | 结果 |
|------|---------|:---:|
| V20260601030__create_prod_product_purchase_price.sql | V{yyyyMMdd}{seq}__{description} | PASS |
| V20260601031__create_prod_product_purchase_price_indexes.sql | V{yyyyMMdd}{seq}__{description} | PASS |
| V20260601032__verify_prod_product_purchase_price.sql | V{yyyyMMdd}{seq}__{description} | PASS |

双下划线分隔符正确，版本号无冲突。V20260601030→V20260601031→V20260601032序列递增无跳号。

---

## 九、易错警示逐项检查

| 序号 | 警示项 | 检查结果 |
|:---:|--------|:---|
| 1 | 通用字段10个必须完整包含 | PASS — 10个字段全部定义，类型与默认值正确 |
| 2 | 部分唯一索引必须包含WHERE is_deleted=false | PASS — uk包含`WHERE is_deleted = false` |
| 3 | 金额/单价字段统一使用decimal(18,8) | PASS — 4个字段均为DECIMAL(18,8) |
| 4 | 联合索引必须以tenant_id为首列 | PASS — 4个多租户索引均以tenant_id为首列 |
| 5 | Flyway命名使用双下划线 | PASS — 所有文件均使用`__`双下划线 |
| 6 | 所有表和字段必须包含COMMENT | PASS — 23/23 (100%) |
| 7 | 禁止使用数据库外键约束 | PASS — 无外键定义 |

---

## 十、验收标准对照

| 序号 | 检查项 | 验证结果 |
|:---:|--------|:---|
| 1 | DDL执行成功，所有表已创建 | CREATE TABLE语法正确，23个字段定义完整 |
| 2 | 字段类型/约束与设计100%一致 | 13个业务字段 + 10个通用字段，类型/约束均符合设计规范 |
| 3 | 所有索引创建成功 | 12个索引全部引用现有列，无列名不一致 |
| 4 | Flyway迁移记录success=true | 版本命名符合V{yyyyMMdd}{seq}__{description}规范 |
| 5 | COMMENT注释完整 | 23/23 (100%) 所有字段均已注释，表注释已添加 |

---

## 十一、与同类表的对比

| 维度 | prod_product_purchase_price | prod_product_standard_price | prod_product_attachment |
|------|:---:|:---:|:---:|
| 字段总数 | 23 | 22 | 19 |
| 业务字段 | 13 | 12 | 9 |
| decimal(18,8) 字段 | 4 | 4 | 0 |
| COMMENT 覆盖率 | 100% | 100% | 100% |
| 列名不一致 | 0 | 0 | 0 |
| 索引总数 | 12 | 10 | 8 |
| 多租户索引数 | 4 | 3 | 3 |

**prod_product_purchase_price 相比 prod_product_standard_price 多了 supplier_id 字段和第4个多租户索引（tenant_supplier），唯一索引为三列组合 (tenant_id, product_id, supplier_id) 确保同一租户下同一商品同一供应商仅一条购价记录。该表设计合理，DDL与索引定义完全一致。**

---

## 十二、总体结论

**验证结果：通过（0个CRITICAL，0个WARNING）**

- **CRITICAL（阻塞）**：0个
- **WARNING**：0个
- **PASS**：10个通用字段完整、13个业务字段定义正确、12个索引全部有效且列名一致、所有COMMENT注释完整(100%)、Flyway命名规范、无外键约束、NOT NULL约束正确、4个tenant_id首列索引规范、部分唯一索引WHERE条件正确（三列组合唯一满足购价核定业务规则）、DECIMAL(18,8)精度统一、VARCHAR字段长度符合规范、SMALLINT状态字段类型正确、默认值设置正确
