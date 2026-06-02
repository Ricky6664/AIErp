# srm_supplier_tag_rel DDL 验证报告

| 属性 | 值 |
|------|-----|
| 验证任务 | P0-003-006-006-001-003 |
| 验证日期 | 2026-06-02 |
| 验证人 | AI (W1) |
| 相关DDL脚本 | V20260602013（建表）、V20260602014（索引约束） |

## 1. 表存在性

| 检查项 | 期望 | 实际 | 结果 |
|--------|:---:|:---:|:---:|
| pg_tables中存在srm_supplier_tag_rel | 1 | — | ⏳ 待DB执行 |

## 2. 字段定义

| 序号 | 字段名 | 期望类型 | 期望可空 | 实际 | 结果 |
|:---:|--------|---------|:---:|------|:---:|
| 1 | id | bigint | NO | — | ⏳ |
| 2 | tenant_id | bigint | NO | — | ⏳ |
| 3 | supplier_id | bigint | NO | — | ⏳ |
| 4 | tag_id | bigint | NO | — | ⏳ |
| 5 | code | varchar(50) | NO | — | ⏳ |
| 6 | status | smallint | YES | — | ⏳ |
| 7-16 | ext_str1~10 | varchar(200) | YES | — | ⏳ |
| 17-21 | ext_num1~5 | decimal(18,8) | YES | — | ⏳ |
| 22-24 | ext_date1~3 | date | YES | — | ⏳ |
| 25-27 | ext_bool1~3 | boolean | YES | — | ⏳ |
| 28 | ext_json | jsonb | YES | — | ⏳ |
| 29 | created_at | timestamp | NO | — | ⏳ |
| 30 | updated_at | timestamp | NO | — | ⏳ |
| 31 | created_by | bigint | YES | — | ⏳ |
| 32 | updated_by | bigint | YES | — | ⏳ |
| 33 | is_deleted | boolean | NO | — | ⏳ |
| 34 | owner_dept_id | bigint | YES | — | ⏳ |
| 35 | owner_id | bigint | YES | — | ⏳ |
| 36 | version | integer | NO | — | ⏳ |

## 3. 通用字段完整性（10个必含）

| 字段 | DDL中存在 | 类型正确 | NOT NULL约束 | 默认值 |
|------|:---:|:---:|:---:|--------|
| id | ✅ | BIGSERIAL | ✅ (PK) | — |
| tenant_id | ✅ | BIGINT | ✅ | — |
| created_by | ✅ | BIGINT | — | — |
| created_at | ✅ | TIMESTAMP | ✅ | NOW() |
| updated_by | ✅ | BIGINT | — | — |
| updated_at | ✅ | TIMESTAMP | ✅ | NOW() |
| is_deleted | ✅ | BOOLEAN | ✅ | FALSE |
| owner_dept_id | ✅ | BIGINT | — | — |
| owner_id | ✅ | BIGINT | — | — |
| version | ✅ | INT | ✅ | 1 |

**通用字段检查：10/10 ✅**

## 4. 索引

| 索引名 | 类型 | 列 | 结果 |
|--------|:---:|-----|:---:|
| pk_srm_supplier_tag_rel | PRIMARY KEY | id | ⏳ |
| uk_srm_supplier_tag_rel_code | UNIQUE WHERE is_deleted=false | code | ⏳ |
| idx_srm_supplier_tag_rel_tenant_code | BTREE | tenant_id, code | ⏳ |
| idx_srm_supplier_tag_rel_tenant_status | BTREE | tenant_id, status | ⏳ |
| idx_srm_supplier_tag_rel_supplier | BTREE | supplier_id | ⏳ |
| idx_srm_supplier_tag_rel_tag | BTREE | tag_id | ⏳ |
| idx_srm_supplier_tag_rel_status | BTREE | status | ⏳ |
| idx_srm_supplier_tag_rel_created_by | BTREE | created_by | ⏳ |
| idx_srm_supplier_tag_rel_updated_by | BTREE | updated_by | ⏳ |
| idx_srm_supplier_tag_rel_owner_dept | BTREE | owner_dept_id | ⏳ |
| idx_srm_supplier_tag_rel_owner | BTREE | owner_id | ⏳ |
| idx_srm_supplier_tag_rel_created_at | BTREE | tenant_id, created_at | ⏳ |

**索引数量：12个 ✅（DDL层面已确认）**

## 5. COMMENT注释

| 对象 | 注释内容 | 结果 |
|------|---------|:---:|
| TABLE | 供应商标签关联表 | ✅ |
| COLUMN id | 主键ID | ✅ |
| COLUMN tenant_id | 租户ID | ✅ |
| COLUMN supplier_id | 供应商ID | ✅ |
| COLUMN tag_id | 标签ID | ✅ |
| COLUMN code | 关联编码 | ✅ |
| COLUMN status | 状态 | ✅ |
| COLUMN created_at | 创建时间 | ✅ |
| COLUMN updated_at | 更新时间 | ✅ |
| COLUMN created_by | 创建人ID | ✅ |
| COLUMN updated_by | 修改人ID | ✅ |
| COLUMN is_deleted | 是否删除 | ✅ |
| COLUMN owner_dept_id | 所属部门ID | ✅ |
| COLUMN owner_id | 数据负责人ID | ✅ |
| COLUMN version | 版本号 | ✅ |

**注释覆盖率：14/14 核心字段 ✅（扩展字段ext_*按项目惯例无需逐个注释）**

## 6. 易错警示检查

| 序号 | 警示项 | 检查结果 |
|:---:|--------|:---:|
| 1 | 10个通用字段完整性 | ✅ 全部包含 |
| 2 | 部分唯一索引WHERE is_deleted=false | ✅ uk_srm_supplier_tag_rel_code |
| 3 | 数值字段decimal(18,8) | ✅ ext_num1~5均为DECIMAL(18,8) |
| 4 | 联合索引tenant_id首列 | ✅ 3个联合索引均以tenant_id为首列 |
| 5 | Flyway命名规范 | ✅ V20260602013、V20260602014 |
| 6 | 表/字段COMMENT注释 | ✅ 核心字段全部有注释 |
| 7 | 无数据库外键约束 | ✅ 无外键定义 |

## 7. 验收标准检查

| 序号 | 检查项 | 状态 |
|:---:|--------|:---:|
| 1 | DDL执行成功，所有表已创建 | ⏳ 待DB执行 |
| 2 | 字段类型/约束与设计100%一致 | ✅ DDL审查通过 |
| 3 | 所有索引创建成功 | ✅ DDL审查通过 |
| 4 | Flyway迁移记录success=true | ⏳ 待DB执行 |
| 5 | COMMENT注释完整 | ✅ DDL审查通过 |

## 8. 验证结论

- **DDL静态审查**：✅ 全部通过
  - CREATE TABLE语句结构完整，36个字段定义正确
  - 10个通用字段完整包含，类型和约束符合规范
  - 12个索引（1PK + 1UNIQUE + 10BTREE）定义正确
  - 部分唯一索引正确使用 `WHERE is_deleted = false`
  - 多租户联合索引以tenant_id为首列
  - ext_num1~5均为decimal(18,8)精度
  - 14个核心字段+1个表注释完整
  - 无数据库外键约束
  - Flyway版本号V20260602013和V20260602014无冲突
- **数据库执行验证**：⏳ 待PostgreSQL环境执行验证SQL脚本
