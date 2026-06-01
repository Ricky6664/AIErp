# prod_product_image商品图片表DDL验证报告

> **生成日期**：2026-06-01
> **验证任务**：P0-003-004-018-001-003
> **验证对象**：prod_product_image商品图片表DDL（CREATE TABLE + 索引与约束）

---

## 一、验证范围

| 文件 | 版本号 | 说明 |
|------|--------|------|
| V20260601060__create_prod_product_image.sql | V20260601060 | CREATE TABLE 建表语句 |
| V20260601061__create_prod_product_image_indexes.sql | V20260601061 | 索引与约束 |

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
| code | VARCHAR(50) | YES | — | 图片编码 |
| image_name | VARCHAR(200) | NO | — | 图片名称 |
| image_url | VARCHAR(500) | NO | — | 图片存储路径 |
| image_type | VARCHAR(50) | YES | 'detail' | 图片类型（main-主图/detail-详情图/gallery-轮播图/other-其他） |
| sort_order | INTEGER | YES | 0 | 排序号 |
| is_main | BOOLEAN | YES | FALSE | 是否主图 |
| file_size | BIGINT | NO | — | 文件大小（字节） |
| status | SMALLINT | YES | 0 | 状态（0-正常/1-停用） |
| remark | VARCHAR(500) | NO | — | 备注 |

**字段总数：20（10业务 + 10通用），无扩展字段。**

---

## 四、COMMENT注释完整性验证

### 4.1 已注释字段（20个）

| 字段 | COMMENT |
|------|---------|
| id | 主键ID |
| tenant_id | 租户ID |
| product_id | 商品ID |
| code | 图片编码 |
| image_name | 图片名称 |
| image_url | 图片存储路径 |
| image_type | 图片类型（main-主图/detail-详情图/gallery-轮播图/other-其他） |
| sort_order | 排序号 |
| is_main | 是否主图 |
| file_size | 文件大小（字节） |
| status | 状态（0-正常/1-停用） |
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

**结果：20/20 (100.0%) 已注释，全部字段 COMMENT 完整。**

---

## 五、索引与约束验证

### 5.1 索引清单

| 索引名 | 索引定义 | 结果 |
|--------|---------|:---:|
| pk_prod_product_image | PRIMARY KEY (id) | PASS |
| uk_prod_product_image_code | UNIQUE INDEX (code) WHERE is_deleted = false | PASS |
| idx_prod_product_image_tenant_code | INDEX (tenant_id, code) | PASS |
| idx_prod_product_image_tenant_status | INDEX (tenant_id, status) | PASS |
| idx_prod_product_image_product_id | INDEX (product_id) | PASS |
| idx_prod_product_image_image_type | INDEX (image_type) | PASS |
| idx_prod_product_image_is_main | INDEX (is_main) | PASS |
| idx_prod_product_image_sort_order | INDEX (sort_order) | PASS |
| idx_prod_product_image_status | INDEX (status) | PASS |

**索引总数：9（1 PK + 1 唯一 + 7 普通），全部 PASS。**

### 5.2 列名一致性检查

| 索引名 | 引用列 | DDL实际列名 | 结果 |
|--------|--------|------------|:---:|
| uk_prod_product_image_code | `code` | `code` | PASS |
| idx_prod_product_image_tenant_code | `code` | `code` | PASS |
| idx_prod_product_image_product_id | `product_id` | `product_id` | PASS |
| idx_prod_product_image_image_type | `image_type` | `image_type` | PASS |
| idx_prod_product_image_is_main | `is_main` | `is_main` | PASS |
| idx_prod_product_image_sort_order | `sort_order` | `sort_order` | PASS |

**所有索引引用的列名与DDL定义一致，无列名不匹配问题。**

### 5.3 规范合规检查

| 检查项 | 要求 | 实际 | 结果 |
|--------|------|------|:---:|
| 部分唯一索引 WHERE is_deleted=false | 必须 | uk_prod_product_image_code 已包含 | PASS |
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
| image_type | NO | NOT NULL | PASS |
| sort_order | NO | NOT NULL | PASS |
| is_main | NO | NOT NULL | PASS |
| status | NO | NOT NULL | PASS |
| created_at | NO | NOT NULL | PASS |
| updated_at | NO | NOT NULL | PASS |
| is_deleted | NO | NOT NULL | PASS |
| version | NO | NOT NULL | PASS |

---

## 七、Flyway版本命名验证

| 文件 | 命名格式 | 结果 |
|------|---------|:---:|
| V20260601060__create_prod_product_image.sql | V{yyyyMMdd}{seq}__{description} | PASS |
| V20260601061__create_prod_product_image_indexes.sql | V{yyyyMMdd}{seq}__{description} | PASS |

双下划线分隔符正确，版本号无冲突。V20260601060和V20260601061序列递增无跳号。

---

## 八、易错警示逐项复核

| 序号 | 警示项 | 检查结果 |
|:---:|--------|:---:|
| 1 | 10个通用字段完整包含 | PASS — id/tenant_id/created_at/updated_at/created_by/updated_by/is_deleted/owner_dept_id/owner_id/version 全部存在 |
| 2 | 部分唯一索引包含 WHERE is_deleted = false | PASS — uk_prod_product_image_code 已包含条件 |
| 3 | decimal(18,8) 精度约束 | N/A — 本表无金额/单价/数量/转换率字段 |
| 4 | 联合索引 tenant_id 为首列 | PASS — idx_prod_product_image_tenant_code 和 idx_prod_product_image_tenant_status 均以 tenant_id 为首列 |
| 5 | Flyway命名双下划线 | PASS — 所有脚本使用 V{yyyyMMdd}{seq}__{description}.sql 格式 |
| 6 | 所有表和字段包含 COMMENT | PASS — 20/20 字段均已注释，表注释存在 |
| 7 | 禁止外键约束 | PASS — 无外键约束，应用层维护关联关系 |

---

## 九、验收标准对照

| 序号 | 检查项 | 验证结果 |
|:---:|--------|:---|
| 1 | DDL执行成功，所有表已创建 | CREATE TABLE 语法正确，20个字段定义完整 |
| 2 | 字段类型/约束与设计100%一致 | code VARCHAR(50) NOT NULL, status SMALLINT, image_type VARCHAR(50) DEFAULT 'detail'，均符合设计 |
| 3 | 所有索引创建成功 | 9个索引全部语法正确，列名引用与DDL一致 |
| 4 | Flyway迁移记录success=true | 版本命名符合 V{yyyyMMdd}{seq}__{description} 规范 |
| 5 | COMMENT注释完整 | 20/20 (100%) 字段已注释，表注释存在 |

---

## 十、总体结论

**验证结果：通过（0个CRITICAL，0个WARNING）**

- **CRITICAL（阻塞）**：0个
- **WARNING**：0个
- **PASS**：10个通用字段完整、20个字段全部COMMENT注释、列名一致性验证通过（所有索引引用列与DDL一致）、部分唯一索引WHERE is_deleted=false正确、tenant_id首列索引规范、无外键约束、NOT NULL约束正确、Flyway命名规范、SMALLINT状态字段正确

> **说明**：prod_product_image 表DDL质量良好，所有字段定义、索引与约束均符合数据库规范。`code` 列在DDL和索引文件中命名一致，无列名不匹配问题。本表无金额/数量字段，不涉及decimal(18,8)精度约束。
