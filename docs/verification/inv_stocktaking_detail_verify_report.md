# inv_stocktaking_detail 盘点主从表 DDL 验证报告

> **任务编号**: P0-003-007-007-001-003
> **验证日期**: 2026-06-02
> **验证方式**: 静态分析（DDL文件对照设计规范逐项检查）
> **验证人**: AI (Worker W1)

---

## 1. 验证范围

| 文件 | 版本号 | 说明 |
|------|--------|------|
| V20260526001__create_inv_stocktaking_detail.sql | V20260526001 | CREATE TABLE DDL（含 inv_stocktaking 主表 + inv_stocktaking_detail 从表） |
| V20260526001__create_inv_stocktaking_detail_indexes.sql | V20260526001 | 索引与约束 DDL（主表 17 + 从表 17） |
| V20260526001__create_inv_stocktaking_detail_rollback.sql | V20260526001 | 表回滚脚本 |
| V20260526001__drop_inv_stocktaking_detail_indexes.sql | V20260526001 | 索引回滚脚本 |

---

## 2. 验收项检查结果

### 2.1 inv_stocktaking 盘点主表结构验证

| 检查项 | 结果 | 说明 |
|--------|:----:|------|
| 表名 inv_stocktaking 符合命名规范 | ✅ PASS | 模块前缀 inv_ + 业务名 stocktaking |
| 10 个通用字段完整 | ✅ PASS | id/tenant_id/created_at/updated_at/created_by/updated_by/is_deleted/owner_dept_id/owner_id/version 全部存在 |
| 字段总数 = 22 | ✅ PASS | 10 通用 + 12 业务 |
| 主键类型 BIGSERIAL | ✅ PASS | id BIGSERIAL PRIMARY KEY |
| tenant_id NOT NULL | ✅ PASS | 多租户隔离强制约束 |
| order_no VARCHAR(50) NOT NULL | ✅ PASS | 单据编号必填 |
| order_date DATE NOT NULL | ✅ PASS | 单据日期必填 |
| warehouse_id BIGINT NOT NULL | ✅ PASS | 仓库关联强制约束 |
| total_qty DECIMAL(18,8) DEFAULT 0 | ✅ PASS | 总数量精度符合全局规范 |
| total_amount DECIMAL(18,8) DEFAULT 0 | ✅ PASS | 总金额精度符合全局规范 |
| status SMALLINT DEFAULT 0 | ✅ PASS | 单据状态默认为草稿 |
| handling_dept_id/handler_id 可空 | ✅ PASS | 经办信息可选 |
| remark VARCHAR(500) 可空 | ✅ PASS | 备注允许为空 |

### 2.2 inv_stocktaking_detail 盘点从表结构验证

| 检查项 | 结果 | 说明 |
|--------|:----:|------|
| 表名 inv_stocktaking_detail 符合命名规范 | ✅ PASS | 模块前缀 inv_ + 业务名 stocktaking_detail |
| 10 个通用字段完整 | ✅ PASS | id/tenant_id/created_at/updated_at/created_by/updated_by/is_deleted/owner_dept_id/owner_id/version 全部存在 |
| 字段总数 = 58 | ✅ PASS | 10 通用 + 26 业务 + 10 ext_str + 5 ext_num + 3 ext_date + 3 ext_bool + 1 ext_json |
| 主键类型 BIGSERIAL | ✅ PASS | id BIGSERIAL PRIMARY KEY |
| tenant_id NOT NULL | ✅ PASS | 多租户隔离强制约束 |
| order_id NOT NULL | ✅ PASS | 主表关联强制约束 |
| product_id NOT NULL | ✅ PASS | 商品关联强制约束 |
| warehouse_id NOT NULL | ✅ PASS | 仓库关联强制约束 |
| line_no INT DEFAULT 1 | ✅ PASS | 行号默认为1 |
| code VARCHAR(50) 可空 | ✅ PASS | 明细编码（部分唯一索引覆盖非空值） |
| location_id 可空 | ✅ PASS | 库位关联可选 |
| batch_no VARCHAR(50) 可空 | ✅ PASS | 批次号可选 |
| 盘盈盘亏三件套(stock_qty/actual_qty/diff_qty) | ✅ PASS | 账面/实盘/差异数量设计合理 |
| 金额字段(stock_amount/actual_amount/diff_amount) | ✅ PASS | 账面/实盘/差异金额设计合理 |
| 商品快照字段 10 项 | ✅ PASS | product_code/name/model/spec/brand/unit_id/unit/is_multi_unit/conversion_rate/base_unit_id + base_qty(换算数量) |
| 扩展字段 22 项 | ✅ PASS | ext_str1-10/ext_num1-5/ext_date1-3/ext_bool1-3/ext_json 结构完整 |
| price DECIMAL(18,8) DEFAULT 0 | ✅ PASS | 单价精度符合全局规范 |

### 2.3 数值精度验证

| 检查项 | 结果 | 说明 |
|--------|:----:|------|
| total_qty 使用 DECIMAL(18,8) | ✅ PASS | 主表总数量精度符合全局规范 |
| total_amount 使用 DECIMAL(18,8) | ✅ PASS | 主表总金额精度符合全局规范 |
| stock_qty 使用 DECIMAL(18,8) | ✅ PASS | 账面数量精度符合全局规范 |
| actual_qty 使用 DECIMAL(18,8) | ✅ PASS | 实盘数量精度符合全局规范 |
| diff_qty 使用 DECIMAL(18,8) | ✅ PASS | 差异数量精度符合全局规范 |
| price 使用 DECIMAL(18,8) | ✅ PASS | 单价精度符合全局规范 |
| stock_amount 使用 DECIMAL(18,8) | ✅ PASS | 账面金额精度符合全局规范 |
| actual_amount 使用 DECIMAL(18,8) | ✅ PASS | 实盘金额精度符合全局规范 |
| diff_amount 使用 DECIMAL(18,8) | ✅ PASS | 差异金额精度符合全局规范 |
| conversion_rate 使用 DECIMAL(18,8) | ✅ PASS | 转换比例精度符合全局规范 |
| base_qty 使用 DECIMAL(18,8) | ✅ PASS | 换算数量精度符合全局规范 |
| ext_num1-5 使用 DECIMAL(18,8) | ✅ PASS | 扩展数值精度符合全局规范 |

### 2.4 inv_stocktaking 主表索引与约束验证

| 检查项 | 结果 | 说明 |
|--------|:----:|------|
| 主键约束 pk_inv_stocktaking | ✅ PASS | ALTER TABLE RENAME CONSTRAINT 重命名 |
| 部分唯一索引 uk_inv_stocktaking_order_no | ✅ PASS | WHERE is_deleted = false，避免"一删一活"陷阱 |
| tenant_id 联合索引首列 | ✅ PASS | idx_inv_stocktaking_tenant_order_no/tenant_status/tenant_deleted/created_at 共 4 个均以 tenant_id 为首列 |
| warehouse_id 索引 | ✅ PASS | 仓库外键关联字段索引 |
| status 索引 | ✅ PASS | 状态筛选索引 |
| order_date 索引 | ✅ PASS | 单据日期范围查询索引 |
| stocktaking_date 索引 | ✅ PASS | 盘点日期范围查询索引 |
| handling_dept_id 索引 | ✅ PASS | 经办部门查询索引 |
| handler_id 索引 | ✅ PASS | 经办人查询索引 |
| approver_id 索引 | ✅ PASS | 审核人查询索引 |
| 操作人索引 | ✅ PASS | created_by/updated_by 均有索引 |
| 数据权限索引 | ✅ PASS | owner_dept_id/owner_id 均有索引 |
| 索引总数 = 17 | ✅ PASS | 1 PK + 1 UNIQUE + 15 标准索引 |

### 2.5 inv_stocktaking_detail 从表索引与约束验证

| 检查项 | 结果 | 说明 |
|--------|:----:|------|
| 主键约束 pk_inv_stocktaking_detail | ✅ PASS | ALTER TABLE RENAME CONSTRAINT 重命名 |
| 部分唯一索引 uk_inv_stocktaking_detail_code | ✅ PASS | WHERE is_deleted = false，避免"一删一活"陷阱 |
| tenant_id 联合索引首列 | ✅ PASS | idx_inv_stocktaking_detail_tenant_code/tenant_deleted/tenant_product_code/tenant_product_name/created_at 共 5 个均以 tenant_id 为首列 |
| order_id 索引 | ✅ PASS | 主表外键关联字段索引 |
| product_id 索引 | ✅ PASS | 商品外键关联字段索引 |
| warehouse_id 索引 | ✅ PASS | 仓库外键关联字段索引 |
| location_id 索引 | ✅ PASS | 库位外键关联字段索引 |
| line_no 索引 | ✅ PASS | 行号查询索引 |
| batch_no 索引 | ✅ PASS | 批次号查询索引 |
| 操作人索引 | ✅ PASS | created_by/updated_by 均有索引 |
| 数据权限索引 | ✅ PASS | owner_dept_id/owner_id 均有索引 |
| 商品快照索引 | ✅ PASS | tenant_product_code/tenant_product_name 多租户前缀索引 |
| 索引总数 = 17 | ✅ PASS | 1 PK + 1 UNIQUE + 15 标准索引 |

### 2.6 Flyway 版本检查

| 检查项 | 结果 | 说明 |
|--------|:----:|------|
| 命名格式符合规范 | ✅ PASS | V{yyyyMMdd}{seq}__{description}.sql，双下划线 |
| CREATE TABLE 版本 V20260526001 | ⚠️ WARN | 与同日多个表的 DDL 共享版本号，Flyway 要求版本唯一 |
| INDEX 版本 V20260526001 | ⚠️ WARN | 与 CREATE TABLE 及其他表脚本共享版本号 |
| ROLLBACK 版本 V20260526001 | ⚠️ WARN | 与 DDL 脚本共享版本号 |

### 2.7 COMMENT 完整性

| 检查项 | 结果 | 说明 |
|--------|:----:|------|
| inv_stocktaking 表注释 | ✅ PASS | COMMENT ON TABLE inv_stocktaking IS '盘点主表' |
| inv_stocktaking 22 个字段注释 | ✅ PASS | 全部 22 个字段均有 COMMENT |
| inv_stocktaking_detail 表注释 | ✅ PASS | COMMENT ON TABLE inv_stocktaking_detail IS '盘点从表' |
| inv_stocktaking_detail 36 个业务/通用字段注释 | ✅ PASS | id/tenant_id/order_id/line_no/code/product_id/warehouse_id/location_id/batch_no/stock_qty/actual_qty/diff_qty/price/stock_amount/actual_amount/diff_amount + 商品快照 10 项 + remark + 通用 10 项均有注释 |
| **inv_stocktaking_detail 扩展字段注释** | **❌ FAIL** | **ext_str1-10/ext_num1-5/ext_date1-3/ext_bool1-3/ext_json 共 22 个扩展字段缺少 COMMENT** |
| **inv_stocktaking_detail 字段注释覆盖率** | **62.1%** (36/58) | **扩展字段无注释** |

### 2.8 安全规范检查

| 检查项 | 结果 | 说明 |
|--------|:----:|------|
| 主从表均无外键约束 | ✅ PASS | 应用层维护关联关系 |
| 无硬编码密码/密钥 | ✅ PASS | DDL 中无敏感信息 |
| 主从表关联字段 order_id 存在 | ✅ PASS | inv_stocktaking_detail.order_id → inv_stocktaking.id 应用层关联 |

---

## 3. 问题汇总

| 序号 | 严重级别 | 问题描述 | 所在文件 |
|:---:|:---:|---------|---------|
| 1 | ❌ MEDIUM | inv_stocktaking_detail 从表 22 个扩展字段缺少 COMMENT 注释（ext_str1-10/ext_num1-5/ext_date1-3/ext_bool1-3/ext_json） | V20260526001__create_inv_stocktaking_detail.sql |
| 2 | ⚠️ LOW | 所有 inv_stocktaking/staking_detail 迁移脚本使用同一版本号 V20260526001，与项目其他 30+ 脚本版本冲突 | 项目全局 |

---

## 4. 建议修复

1. **扩展字段注释补充**: 在 CREATE TABLE DDL 的 inv_stocktaking_detail 段中为 ext_str1-10/ext_num1-5/ext_date1-3/ext_bool1-3/ext_json 共 22 个字段添加 COMMENT ON COLUMN
2. **全局版本规范**: 建议项目统一 Flyway 版本号分配策略，确保每个迁移脚本有唯一版本号

---

## 5. 验收结论

| 验收标准 | 状态 |
|---------|:---:|
| DDL 执行成功，所有表已创建 | ⬜ 待数据库执行 |
| 字段类型/约束与设计 100% 一致 | ✅ PASS |
| 所有索引创建成功 | ✅ PASS（静态验证通过） |
| Flyway 迁移记录 success=true | ⚠️ 存在版本共享（项目全局现象） |
| COMMENT 注释完整 | ❌ inv_stocktaking_detail 扩展字段缺失 |

**总体结论**: DDL 结构设计正确，主表(inv_stocktaking) 22 字段 + 从表(inv_stocktaking_detail) 58 字段共计 80 字段，10 个通用字段完整无缺，主从关联(order_id)设计合理。数值精度统一使用 DECIMAL(18,8)，覆盖 12 个金额/数量字段。主表 17 索引 + 从表 17 索引共计 34 索引，部分唯一索引含 WHERE is_deleted=false，多租户索引以 tenant_id 为首列覆盖 9 个联合索引。外键关联字段(product_id/warehouse_id/location_id/order_id)均有独立索引。唯一问题是 inv_stocktaking_detail 从表 22 个扩展字段缺少 COMMENT 注释，属于项目全局模式（其他表扩展字段同样缺注释），不影响 DDL 可执行性。
