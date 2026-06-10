# srm_supplier_attachment DDL 验证报告

| 属性 | 值 |
|------|-----|
| 验证任务 | P0-003-006-007-001-003 |
| 验证日期 | 2026-06-02 |
| 验证人 | AI (W1) |
| 相关DDL脚本 | V20260602016（建表）、V20260602017（索引约束） |

## 1. 表存在性

| 检查项 | 期望 | 实际 | 结果 |
|--------|:---:|:---:|:---:|
| pg_tables中存在srm_supplier_attachment | 1 | — | ⏳ 待DB执行 |

## 2. 字段定义

| 序号 | 字段名 | 期望类型 | 期望可空 | 实际 | 结果 |
|:---:|--------|---------|:---:|------|:---:|
| 1 | id | bigint | NO | — | ⏳ |
| 2 | tenant_id | bigint | NO | — | ⏳ |
| 3 | supplier_id | bigint | NO | — | ⏳ |
| 4 | code | varchar(50) | NO | — | ⏳ |
| 5 | attachment_name | varchar(200) | NO | — | ⏳ |
| 6 | attachment_type | varchar(50) | NO | — | ⏳ |
| 7 | attachment_url | varchar(500) | YES | — | ⏳ |
| 8 | file_size | bigint | YES | — | ⏳ |
| 9 | sort_order | integer | NO | — | ⏳ |
| 10 | status | smallint | NO | — | ⏳ |
| 11 | remark | varchar(500) | YES | — | ⏳ |
| 12-21 | ext_str1~10 | varchar(200) | YES | — | ⏳ |
| 22-26 | ext_num1~5 | decimal(18,8) | YES | — | ⏳ |
| 27-29 | ext_date1~3 | date | YES | — | ⏳ |
| 30-32 | ext_bool1~3 | boolean | YES | — | ⏳ |
| 33 | ext_json | jsonb | YES | — | ⏳ |
| 34 | created_at | timestamp | NO | — | ⏳ |
| 35 | updated_at | timestamp | NO | — | ⏳ |
| 36 | created_by | bigint | YES | — | ⏳ |
| 37 | updated_by | bigint | YES | — | ⏳ |
| 38 | is_deleted | boolean | NO | — | ⏳ |
| 39 | owner_dept_id | bigint | YES | — | ⏳ |
| 40 | owner_id | bigint | YES | — | ⏳ |
| 41 | version | integer | NO | — | ⏳ |

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
| pk_srm_supplier_attachment | PRIMARY KEY | id | ⏳ |
| uk_srm_supplier_attachment_code | UNIQUE WHERE is_deleted=false | code | ⏳ |
| idx_srm_supplier_attachment_tenant_code | BTREE | tenant_id, code | ⏳ |
| idx_srm_supplier_attachment_tenant_status | BTREE | tenant_id, status | ⏳ |
| idx_srm_supplier_attachment_supplier | BTREE | supplier_id | ⏳ |
| idx_srm_supplier_attachment_type | BTREE | attachment_type | ⏳ |
| idx_srm_supplier_attachment_status | BTREE | status | ⏳ |
| idx_srm_supplier_attachment_created_by | BTREE | created_by | ⏳ |
| idx_srm_supplier_attachment_updated_by | BTREE | updated_by | ⏳ |
| idx_srm_supplier_attachment_owner_dept | BTREE | owner_dept_id | ⏳ |
| idx_srm_supplier_attachment_owner | BTREE | owner_id | ⏳ |
| idx_srm_supplier_attachment_created_at | BTREE | tenant_id, created_at | ⏳ |

**索引数量：12个 ✅（DDL层面已确认）**

## 5. COMMENT注释

| 对象 | 注释内容 | 结果 |
|------|---------|:---:|
| TABLE | 供应商附件表 | ✅ |
| COLUMN id | 主键ID | ✅ |
| COLUMN tenant_id | 租户ID | ✅ |
| COLUMN supplier_id | 供应商ID | ✅ |
| COLUMN code | 附件编码 | ✅ |
| COLUMN attachment_name | 附件名称 | ✅ |
| COLUMN attachment_type | 附件类型（contract-合同/certificate-证照/qualification-资质/other-其他） | ✅ |
| COLUMN attachment_url | 附件存储路径 | ✅ |
| COLUMN file_size | 文件大小（字节） | ✅ |
| COLUMN sort_order | 排序号 | ✅ |
| COLUMN status | 状态（0-正常/1-停用） | ✅ |
| COLUMN remark | 备注 | ✅ |
| COLUMN created_at | 创建时间 | ✅ |
| COLUMN updated_at | 更新时间 | ✅ |
| COLUMN created_by | 创建人ID | ✅ |
| COLUMN updated_by | 修改人ID | ✅ |
| COLUMN is_deleted | 是否删除 | ✅ |
| COLUMN owner_dept_id | 所属部门ID | ✅ |
| COLUMN owner_id | 数据负责人ID | ✅ |
| COLUMN version | 版本号 | ✅ |

**注释覆盖率：19/19 核心字段 ✅（扩展字段ext_*按项目惯例无需逐个注释）**

## 6. 易错警示检查

| 序号 | 警示项 | 检查结果 |
|:---:|--------|:---:|
| 1 | 10个通用字段完整性 | ✅ 全部包含 |
| 2 | 部分唯一索引WHERE is_deleted=false | ✅ uk_srm_supplier_attachment_code |
| 3 | 数值字段decimal(18,8) | ✅ ext_num1~5均为DECIMAL(18,8) |
| 4 | 联合索引tenant_id首列 | ✅ 3个联合索引均以tenant_id为首列 |
| 5 | Flyway命名规范 | ✅ V20260602016、V20260602017 |
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
  - CREATE TABLE语句结构完整，41个字段定义正确
  - 10个通用字段完整包含，类型和约束符合规范
  - 12个索引（1PK + 1UNIQUE + 10BTREE）定义正确
  - 部分唯一索引正确使用 `WHERE is_deleted = false`
  - 多租户联合索引以tenant_id为首列
  - ext_num1~5均为decimal(18,8)精度
  - 19个核心字段+1个表注释完整
  - 无数据库外键约束
  - Flyway版本号V20260602016和V20260602017无冲突
- **数据库执行验证**：⏳ 待PostgreSQL环境执行验证SQL脚本
