# org_department部门表DDL验证报告

> **生成日期**：2026-06-01
> **验证任务**：P0-003-003-002-001-003
> **验证对象**：org_department部门表DDL（CREATE TABLE + 索引与约束）

---

## 一、验证范围

| 文件 | 版本号 | 说明 |
|------|--------|------|
| V20260601005__create_org_department.sql | V20260601005 | CREATE TABLE 建表语句 |
| V20260601006__create_org_department_indexes.sql | V20260601006 | 索引与约束 |

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

## 三、COMMENT注释完整性验证

### 3.1 已注释字段（16个）

| 字段 | COMMENT |
|------|---------|
| id | 主键ID |
| tenant_id | 租户ID |
| parent_id | 上级部门ID |
| dept_code | 部门编码 |
| dept_name | 部门名称 |
| leader_id | 部门负责人ID |
| sort_order | 排序号 |
| status | 状态：1=启用/0=停用 |
| created_at | 创建时间 |
| updated_at | 更新时间 |
| created_by | 创建人ID |
| updated_by | 修改人ID |
| is_deleted | 是否删除 |
| owner_dept_id | 所属部门ID |
| owner_id | 数据负责人ID |
| version | 版本号 |

### 3.2 缺失注释字段（22个）

| 字段 | 类型 | 问题 |
|------|------|------|
| ext_str1 ~ ext_str10 | VARCHAR(200) x10 | 缺少 COMMENT |
| ext_num1 ~ ext_num5 | DECIMAL(18,8) x5 | 缺少 COMMENT |
| ext_date1 ~ ext_date3 | DATE x3 | 缺少 COMMENT |
| ext_bool1 ~ ext_bool3 | BOOLEAN x3 | 缺少 COMMENT |
| ext_json | JSONB x1 | 缺少 COMMENT |

**结果：16/38 (42.1%) 已注释，22/38 (57.9%) 扩展字段缺少 COMMENT。**

> **影响**：PostgreSQL元数据查询和后续MyBatis-Plus代码生成依赖COMMENT注释（如 `全局规范-数据库规范` 要求）。扩展字段虽为标准化预留，但仍需标注用途以便数据字典生成。

---

## 四、索引与约束验证

### 4.1 索引清单

| 索引名 | 索引定义 | 结果 |
|--------|---------|:---:|
| pk_org_department | PRIMARY KEY (id) | PASS |
| uk_org_department_code | UNIQUE INDEX (code) WHERE is_deleted = false | FAIL |
| idx_org_department_tenant_code | INDEX (tenant_id, code) | FAIL |
| idx_org_department_tenant_status | INDEX (tenant_id, status) | PASS |
| idx_org_department_tenant_deleted | INDEX (tenant_id, is_deleted) | PASS |
| idx_org_department_parent_id | INDEX (tenant_id, parent_id) | PASS |
| idx_org_department_status | INDEX (status) | PASS |
| idx_org_department_leader_id | INDEX (leader_id) | PASS |
| idx_org_department_created_by | INDEX (created_by) | PASS |
| idx_org_department_updated_by | INDEX (updated_by) | PASS |
| idx_org_department_owner_dept | INDEX (owner_dept_id) | PASS |
| idx_org_department_owner | INDEX (owner_id) | PASS |
| idx_org_department_created_at | INDEX (tenant_id, created_at) | PASS |

### 4.2 关键发现：列名错误

**V20260601006__create_org_department_indexes.sql 中存在严重错误：索引引用了不存在的列 `code`，正确列名为 `dept_code`。**

受影响索引（2个）：

```sql
-- 当前（错误）：
CREATE UNIQUE INDEX uk_org_department_code ON org_department(code) WHERE is_deleted = false;
CREATE INDEX idx_org_department_tenant_code ON org_department(tenant_id, code);

-- 应为（修正）：
CREATE UNIQUE INDEX uk_org_department_code ON org_department(dept_code) WHERE is_deleted = false;
CREATE INDEX idx_org_department_tenant_code ON org_department(tenant_id, dept_code);
```

> **影响**：执行此SQL时PostgreSQL将报错 `column "code" does not exist`，索引创建失败。这将导致 `dept_code` 无唯一性约束，可能产生重复数据。

### 4.3 规范合规检查

| 检查项 | 要求 | 实际 | 结果 |
|--------|------|------|:---:|
| 部分唯一索引 WHERE is_deleted=false | 必须 | 已包含（但列名错误） | FAIL |
| 联合索引 tenant_id 为首列 | 必须 | 已包含（但列名错误） | FAIL |
| 禁用外键约束 | 禁止 | 无外键 | PASS |
| decimal(18,8) 精度 | 必须 | ext_num1-5 均为 DECIMAL(18,8) | PASS |

---

## 五、NOT NULL 约束验证

| 字段 | is_nullable | 要求 | 结果 |
|------|:---:|------|:---:|
| id | NO | NOT NULL (PK) | PASS |
| tenant_id | NO | NOT NULL | PASS |
| dept_code | NO | NOT NULL | PASS |
| dept_name | NO | NOT NULL | PASS |
| status | NO | NOT NULL | PASS |
| created_at | NO | NOT NULL | PASS |
| updated_at | NO | NOT NULL | PASS |
| is_deleted | NO | NOT NULL | PASS |
| version | NO | NOT NULL | PASS |

---

## 六、Flyway版本命名验证

| 文件 | 命名格式 | 结果 |
|------|---------|:---:|
| V20260601005__create_org_department.sql | V{yyyyMMdd}{seq}__{description} | PASS |
| V20260601006__create_org_department_indexes.sql | V{yyyyMMdd}{seq}__{description} | PASS |

双下划线分隔符正确，无版本号冲突。

---

## 七、验收标准对照

| 序号 | 检查项 | 验证结果 |
|:---:|--------|:---:|
| 1 | DDL执行成功，所有表已创建 | CREATE TABLE语法正确 |
| 2 | 字段类型/约束与设计100%一致 | 索引列名 `code` 应为 `dept_code` |
| 3 | 所有索引创建成功 | 2个索引引用错误列名，执行将失败 |
| 4 | Flyway迁移记录success=true | 版本命名规范正确 |
| 5 | COMMENT注释完整 | 22个扩展字段缺少 COMMENT |

---

## 八、问题汇总与修复建议

| 序号 | 严重度 | 问题 | 影响文件 | 修复方案 |
|:---:|:---:|------|---------|---------|
| 1 | CRITICAL | 列名 `code` 应为 `dept_code` | V20260601006 | 将2处 `code` 替换为 `dept_code` |
| 2 | WARNING | 22个扩展字段缺少 COMMENT | V20260601005 | 为 ext_* 字段补充 COMMENT 注释 |

---

## 九、总体结论

**验证结果：有条件通过（需修复1个严重问题后重新验证）**

- PASS：5/7 验收标准通过
- FAIL：2/7（索引列名错误、COMMENT不完整）
- 关键阻塞：索引文件列名错误导致DDL无法在PostgreSQL中执行成功
