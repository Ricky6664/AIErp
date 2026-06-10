# inv_stock_location 库位库存表 DDL 验证报告

> **任务编号**: P0-003-007-004-001-003
> **验证日期**: 2026-06-02
> **验证方式**: 静态分析（DDL文件对照设计规范逐项检查）
> **验证人**: AI (Worker W1)

---

## 1. 验证范围

| 文件 | 版本号 | 说明 |
|------|--------|------|
| V20260526001__create_inv_stock_location.sql | V20260526001 | CREATE TABLE DDL |
| V20260526001__create_inv_stock_location_indexes.sql | V20260526001 | 索引与约束 DDL |
| V20260526001__create_inv_stock_location_rollback.sql | V20260526001 | 回滚脚本 |
| V20260526001__drop_inv_stock_location_indexes.sql | V20260526001 | 索引回滚脚本 |

---

## 2. 验收项检查结果

### 2.1 表结构验证

| 检查项 | 结果 | 说明 |
|--------|:----:|------|
| 表名 inv_stock_location 符合命名规范 | ✅ PASS | 模块前缀 inv_ + 业务名 stock_location |
| 10 个通用字段完整 | ✅ PASS | id/tenant_id/created_at/updated_at/created_by/updated_by/is_deleted/owner_dept_id/owner_id/version 全部存在 |
| 字段总数 = 53 | ✅ PASS | 10 通用 + 21 业务 + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json |
| 主键类型 BIGSERIAL | ✅ PASS | id BIGSERIAL PRIMARY KEY |
| tenant_id NOT NULL | ✅ PASS | 多租户隔离强制约束 |
| product_id NOT NULL | ✅ PASS | 商品关联强制约束 |
| warehouse_id NOT NULL | ✅ PASS | 仓库关联强制约束 |
| location_id NOT NULL | ✅ PASS | 库位关联强制约束 |
| order_no VARCHAR(50) NOT NULL | ✅ PASS | 单据编号必填 |
| order_date DATE NOT NULL | ✅ PASS | 单据日期必填 |
| batch_no VARCHAR(50) 可空 | ✅ PASS | 批次号允许为空（非批次品允许同库位多记录） |
| status SMALLINT DEFAULT 0 | ✅ PASS | 单据状态默认为草稿 |
| remark VARCHAR(500) 可空 | ✅ PASS | 备注允许为空 |

### 2.2 数值精度验证

| 检查项 | 结果 | 说明 |
|--------|:----:|------|
| qty 使用 DECIMAL(18,8) | ✅ PASS | 库位库存数量精度符合全局规范 |
| amount 使用 DECIMAL(18,8) | ✅ PASS | 库位库存金额精度符合全局规范 |
| conversion_rate 使用 DECIMAL(18,8) | ✅ PASS | 转换比例精度符合全局规范 |
| base_qty 使用 DECIMAL(18,8) | ✅ PASS | 换算数量精度符合全局规范 |
| ext_num1-5 使用 DECIMAL(18,8) | ✅ PASS | 扩展数值精度符合全局规范 |

### 2.3 索引与约束验证

| 检查项 | 结果 | 说明 |
|--------|:----:|------|
| 主键约束 pk_inv_stock_location | ✅ PASS | ALTER TABLE RENAME CONSTRAINT 重命名 |
| 部分唯一索引 uk_inv_stock_location_unique | ✅ PASS | WHERE is_deleted = false，覆盖(tenant_id, warehouse_id, location_id, product_id, batch_no)，batch_no可为NULL（PostgreSQL中NULL≠NULL） |
| tenant_id 联合索引首列 | ✅ PASS | uk_inv_stock_location_unique/tenant_status/tenant_deleted/tenant_order_no/tenant_product_code/tenant_product_name/created_at 共 7 个均以 tenant_id 为首列 |
| product_id 索引 | ✅ PASS | 商品外键关联字段索引 |
| warehouse_id 索引 | ✅ PASS | 仓库外键关联字段索引 |
| location_id 索引 | ✅ PASS | 库位外键关联字段索引 |
| batch_no 索引 | ✅ PASS | 批次号查询索引 |
| status 索引 | ✅ PASS | 状态筛选索引 |
| order_date 索引 | ✅ PASS | 日期范围查询索引 |
| order_no 多租户索引 | ✅ PASS | tenant_id + order_no 联合索引 |
| product_code/product_name 多租户索引 | ✅ PASS | 商品快照字段多租户查询索引 |
| 操作人索引 | ✅ PASS | created_by/updated_by 均有索引 |
| 数据权限索引 | ✅ PASS | owner_dept_id/owner_id 均有索引 |
| 索引总数 = 18 | ✅ PASS | 1 PK + 1 UNIQUE + 16 标准索引 |

### 2.4 Flyway 版本检查

| 检查项 | 结果 | 说明 |
|--------|:----:|------|
| 命名格式符合规范 | ✅ PASS | V{yyyyMMdd}{seq}__{description}.sql，双下划线 |
| CREATE TABLE 版本 V20260526001 | ⚠️ WARN | 与同日多个表的 DDL 共享版本号，Flyway 要求版本唯一 |
| INDEX 版本 V20260526001 | ⚠️ WARN | 与 CREATE TABLE 及其他表脚本共享版本号 |

### 2.5 COMMENT 完整性

| 检查项 | 结果 | 说明 |
|--------|:----:|------|
| 表注释 | ✅ PASS | COMMENT ON TABLE inv_stock_location IS '库位库存表' |
| 10 个通用字段注释 | ✅ PASS | id/tenant_id/created_at/updated_at/created_by/updated_by/is_deleted/owner_dept_id/owner_id/version 均有注释 |
| 21 个业务字段注释 | ✅ PASS | product_id/warehouse_id/location_id/batch_no/qty/amount/order_no/order_date/status/remark 及商品快照字段均有注释 |
| **扩展字段注释** | **❌ FAIL** | **ext_str1-10/ext_num1-5/ext_date1-3/ext_bool1-3/ext_json 共 22 个扩展字段缺少 COMMENT** |

### 2.6 安全规范检查

| 检查项 | 结果 | 说明 |
|--------|:----:|------|
| 无外键约束 | ✅ PASS | 应用层维护关联关系 |
| 无硬编码密码/密钥 | ✅ PASS | DDL 中无敏感信息 |

---

## 3. 问题汇总

| 序号 | 严重级别 | 问题描述 | 所在文件 |
|:---:|:---:|---------|---------|
| 1 | ❌ MEDIUM | 22 个扩展字段缺少 COMMENT 注释（ext_str1-10/ext_num1-5/ext_date1-3/ext_bool1-3/ext_json） | V20260526001__create_inv_stock_location.sql |
| 2 | ⚠️ LOW | 所有 inv_stock_location 迁移脚本使用同一版本号 V20260526001，与项目其他 30+ 脚本版本冲突 | 项目全局 |

---

## 4. 建议修复

1. **扩展字段注释补充**: 在 CREATE TABLE DDL 中为 ext_str1-10/ext_num1-5/ext_date1-3/ext_bool1-3/ext_json 共 22 个字段添加 COMMENT ON COLUMN
2. **全局版本规范**: 建议项目统一 Flyway 版本号分配策略，确保每个迁移脚本有唯一版本号

---

## 5. 验收结论

| 验收标准 | 状态 |
|---------|:---:|
| DDL 执行成功，所有表已创建 | ⬜ 待数据库执行 |
| 字段类型/约束与设计 100% 一致 | ✅ PASS |
| 所有索引创建成功 | ✅ PASS（静态验证通过） |
| Flyway 迁移记录 success=true | ⚠️ 存在版本共享（项目全局现象） |
| COMMENT 注释完整 | ❌ 扩展字段缺失 |

**总体结论**: DDL 结构设计正确，10 个通用字段完整，53 个字段数量精确，索引设计严谨（部分唯一索引含 WHERE is_deleted=false，覆盖 5 个业务字段，batch_no 可空利用 PostgreSQL NULL≠NULL 特性避免非批次品冲突；7 个 tenant_id 为首列的联合索引覆盖多租户查询场景；product_id/warehouse_id/location_id/batch_no/status/order_date 均有独立索引），数值精度统一使用 DECIMAL(18,8)，无外键约束。存在 1 个需修复问题：22 个扩展字段缺少 COMMENT 注释。
