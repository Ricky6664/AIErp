# prod_product_attribute + prod_product_attribute_value 商品属性表DDL验证报告

> **生成日期**：2026-06-01
> **验证任务**：P0-003-004-015-001-003
> **验证对象**：prod_product_attribute商品属性表 + prod_product_attribute_value商品属性值表 DDL（CREATE TABLE + 索引与约束）

---

## 一、验证范围

| 文件 | 版本号 | 说明 |
|------|--------|------|
| V20260601051__create_prod_product_attribute.sql | V20260601051 | CREATE TABLE 建表语句（属性表） |
| V20260601052__create_prod_product_attribute_value.sql | V20260601052 | CREATE TABLE 建表语句（属性值表） |
| V20260601053__create_prod_product_attribute_indexes.sql | V20260601053 | 索引与约束 |

---

## 二、通用字段完整性验证

### 2.1 prod_product_attribute 通用字段

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

### 2.2 prod_product_attribute_value 通用字段

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

### 3.1 prod_product_attribute 业务字段

| 序号 | 字段名 | 类型 | 约束 | 默认值 | 说明 |
|:---:|--------|------|------|--------|------|
| 1 | code | VARCHAR(50) | NOT NULL | — | 属性编码 |
| 2 | name | VARCHAR(200) | NOT NULL | — | 属性名称 |
| 3 | parent_id | BIGINT | — | — | 父属性ID（树形结构） |
| 4 | sort_order | INT | NOT NULL | 0 | 排序号 |
| 5 | status | SMALLINT | NOT NULL | 0 | 状态（0-启用/1-停用） |
| 6 | remark | VARCHAR(500) | — | — | 备注 |

**字段总数：6业务 + 10通用 = 16列**

### 3.2 prod_product_attribute_value 业务字段

| 序号 | 字段名 | 类型 | 约束 | 默认值 | 说明 |
|:---:|--------|------|------|--------|------|
| 1 | attribute_id | BIGINT | NOT NULL | — | 属性ID（关联prod_product_attribute） |
| 2 | value | VARCHAR(200) | NOT NULL | — | 属性值 |
| 3 | sort_order | INT | NOT NULL | 0 | 排序号 |
| 4 | status | SMALLINT | NOT NULL | 0 | 状态（0-启用/1-停用） |
| 5 | remark | VARCHAR(500) | — | — | 备注 |

**字段总数：5业务 + 10通用 = 15列**

---

## 四、索引与约束验证

### 4.1 主键约束

| 表 | 主键名 | 字段 | 类型 | 结果 |
|----|--------|------|------|:---:|
| prod_product_attribute | pk_prod_product_attribute | id | BIGSERIAL | PASS |
| prod_product_attribute_value | (默认) | id | BIGSERIAL | PASS |

### 4.2 部分唯一索引（WHERE is_deleted = false）

| 表 | 索引名 | 定义 | 结果 |
|----|--------|------|:---:|
| prod_product_attribute | uk_prod_product_attribute_code | CREATE UNIQUE INDEX uk_prod_product_attribute_code ON prod_product_attribute(code) WHERE is_deleted = false | PASS |

**唯一索引包含 `WHERE is_deleted = false` 条件，避免boolean字段在联合唯一索引中的"一删一活"陷阱。**

### 4.3 多租户联合索引（tenant_id为首列）

| 序号 | 索引名 | 列 | 结果 |
|:---:|--------|-----|:---:|
| 1 | idx_prod_product_attribute_tenant_code | (tenant_id, code) | PASS |
| 2 | idx_prod_product_attribute_tenant_status | (tenant_id, status) | PASS |
| 3 | idx_prod_product_attribute_parent_id | (tenant_id, parent_id) | PASS |
| 4 | idx_prod_product_attribute_tenant_sort | (tenant_id, sort_order) | PASS |
| 5 | idx_prod_product_attribute_tenant_created_at | (tenant_id, created_at) | PASS |

**5个联合索引均以 tenant_id 为首列，满足多租户隔离查询性能要求。**

### 4.4 索引数量

| 表 | 期望 | 实际 | 结果 |
|----|:---:|:---:|:---:|
| prod_product_attribute | 7（1PK + 1唯一 + 5普通） | 7 | PASS |

---

## 五、COMMENT 注释完整性验证

### 5.1 prod_product_attribute

| 序号 | 对象 | 注释 | 结果 |
|:---:|------|------|:---:|
| — | TABLE | '商品属性表' | PASS |
| 1 | id | '主键ID' | PASS |
| 2 | tenant_id | '租户ID' | PASS |
| 3 | code | '属性编码' | PASS |
| 4 | name | '属性名称' | PASS |
| 5 | parent_id | '父属性ID（树形结构）' | PASS |
| 6 | sort_order | '排序号' | PASS |
| 7 | status | '状态（0-启用/1-停用）' | PASS |
| 8 | remark | '备注' | PASS |
| 9 | created_at | '创建时间' | PASS |
| 10 | updated_at | '更新时间' | PASS |
| 11 | created_by | '创建人ID' | PASS |
| 12 | updated_by | '修改人ID' | PASS |
| 13 | is_deleted | '是否删除' | PASS |
| 14 | owner_dept_id | '所属部门ID' | PASS |
| 15 | owner_id | '数据负责人ID' | PASS |
| 16 | version | '版本号' | PASS |

**表注释 + 16个字段注释，覆盖率 100%。**

### 5.2 prod_product_attribute_value

| 序号 | 对象 | 注释 | 结果 |
|:---:|------|------|:---:|
| — | TABLE | '商品属性值表' | PASS |
| 1 | id | '主键ID' | PASS |
| 2 | tenant_id | '租户ID' | PASS |
| 3 | attribute_id | '属性ID（关联prod_product_attribute）' | PASS |
| 4 | value | '属性值' | PASS |
| 5 | sort_order | '排序号' | PASS |
| 6 | status | '状态（0-启用/1-停用）' | PASS |
| 7 | remark | '备注' | PASS |
| 8 | created_at | '创建时间' | PASS |
| 9 | updated_at | '更新时间' | PASS |
| 10 | created_by | '创建人ID' | PASS |
| 11 | updated_by | '修改人ID' | PASS |
| 12 | is_deleted | '是否删除' | PASS |
| 13 | owner_dept_id | '所属部门ID' | PASS |
| 14 | owner_id | '数据负责人ID' | PASS |
| 15 | version | '版本号' | PASS |

**表注释 + 15个字段注释，覆盖率 100%。**

---

## 六、规范合规性检查

| 序号 | 检查项 | 要求 | 实际 | 结果 |
|:---:|--------|------|------|:---:|
| 1 | 主键类型 | BIGSERIAL | BIGSERIAL PRIMARY KEY | PASS |
| 2 | tenant_id NOT NULL | 强制 | NOT NULL | PASS |
| 3 | is_deleted DEFAULT false | 强制 | DEFAULT FALSE | PASS |
| 4 | version DEFAULT 1 | 强制 | DEFAULT 1 | PASS |
| 5 | 时间字段 DEFAULT NOW() | 强制 | DEFAULT NOW() | PASS |
| 6 | 部分唯一索引含 is_deleted | 强制 | WHERE is_deleted = false | PASS |
| 7 | 联合索引 tenant_id 首列 | 强制 | 全部满足 | PASS |
| 8 | 无外键约束 | 强制 | 无外键定义 | PASS |
| 9 | status 字段类型 SMALLINT | 指定 | SMALLINT | PASS |
| 10 | 无 decimal 字段 | 表无金额/数量字段 | 无 decimal 字段 | PASS |

---

## 七、易错警示逐项复核

| 序号 | 警示项 | 复核结果 |
|:---:|--------|:---:|
| 1 | 10个通用字段必须完整 | ✅ id/tenant_id/created_at/updated_at/created_by/updated_by/is_deleted/owner_dept_id/owner_id/version 全部存在 |
| 2 | 部分唯一索引必须包含 WHERE is_deleted = false | ✅ uk_prod_product_attribute_code 包含条件 |
| 3 | 联合索引以 tenant_id 为首列 | ✅ 5个联合索引全部以 tenant_id 为首列 |
| 4 | Flyway 命名规范 V{yyyyMMdd}{seq}__{description} | ✅ 版本号 V20260601051~053，双下划线 |
| 5 | 所有表和字段包含 COMMENT | ✅ 两张表 + 31个字段全部有注释 |
| 6 | 禁止数据库外键约束 | ✅ 无任何外键定义 |

---

## 八、验证结论

| 验证项 | 结果 |
|--------|:---:|
| 表结构完整性 | PASS |
| 通用字段规范 | PASS |
| 索引与约束 | PASS |
| COMMENT 注释 | PASS |
| 规范合规性 | PASS |
| 易错警示复核 | PASS |

**总体结论：✅ 全部通过。prod_product_attribute 和 prod_product_attribute_value 两张表的 DDL + 索引/约束全部符合设计规范，可以安全执行到 PostgreSQL 15+ 数据库。**
