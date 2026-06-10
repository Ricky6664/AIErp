# 数据库命名规范

> 版本：V1.0
> 创建日期：2026-06-01
> 用途：定义数据库、Schema、表、字段、约束、索引、序列、函数、视图的命名规范，作为所有DDL任务的开发约束基准。

---

## 1. 数据库命名

| 规则 | 说明 | 示例 |
|------|------|------|
| 命名风格 | 全小写 + 下划线分隔（snake_case） | `erp_ai` |
| 字符集 | 仅允许小写字母、数字、下划线 | — |
| 长度限制 | 不超过 63 字符 | — |

---

## 2. Schema 命名

| 规则 | 说明 | 示例 |
|------|------|------|
| 业务 Schema | `{模块缩写}` | `sale`、`purchase`、`inv` |
| 公共 Schema | `public`（存放系统核心表） | `public` |
| 命名风格 | 全小写，无下划线 | `crm`、`srm`、`fin` |

---

## 3. 表命名

### 3.1 基础规则

| 规则 | 说明 | 示例 |
|------|------|------|
| 前缀格式 | `{模块前缀}_{业务名}` | `crm_customer`、`sale_order`、`inv_warehouse` |
| 命名风格 | 全小写 + 下划线分隔（snake_case） | `purchase_order_detail` |
| 长度限制 | 不超过 63 字符 | — |
| 禁止关键字 | 禁止使用 PostgreSQL 保留字 | 不使用 `order`、`group`、`user` 等 |

### 3.2 按表角色细分

| 角色 | 命名格式 | 示例 |
|------|---------|------|
| 主表 | `{模块}_{业务}` | `sale_order`、`purchase_order` |
| 从表（明细） | `{模块}_{业务}_detail` | `sale_order_detail`、`purchase_order_detail` |
| 关联表 | `{模块}_{业务}_{关联名}` | `crm_customer_contact`、`crm_customer_address` |
| 辅助属性子表 | `doc_detail_{属性名}` | `doc_detail_location`、`doc_detail_batch`、`doc_detail_serial` |
| 多对多关联表 | `{模块1}_{模块2}` 或 `{模块}_{实体1}_{实体2}` | `sys_user_role`、`sys_role_menu` |
| 快照表 | `{表名}_snapshot` | `sale_order_snapshot` |
| 日志表 | `{表名}_log` | `sys_operation_log` |

### 3.3 模块前缀对照

| 前缀 | 模块 | 前缀 | 模块 |
|------|------|------|------|
| sys | 系统核心 | sale | 销售管理 |
| org | 组织架构 | purchase | 采购管理 |
| prod | 商品管理 | mfg | 生产管理 |
| crm | 客户管理 | outs | 委外管理 |
| srm | 供应商管理 | ar | 应收账款 |
| inv | 仓库库存 | ap | 应付账款 |
| fin | 财务基础 | gl | 凭证账簿 |
| hrm | 人力资源管理 | oa | OA/运维 |
| ext | 扩展业务 | msg | 消息管理 |
| appr | 审批流程 | init | 期初管理 |

---

## 4. 字段命名

### 4.1 基础规则

| 规则 | 说明 | 示例 |
|------|------|------|
| 命名风格 | 全小写 + 下划线分隔（snake_case） | `bill_no`、`customer_id`、`created_at` |
| 长度限制 | 不超过 63 字符 | — |
| 禁止关键字 | 禁止使用 PostgreSQL 保留字 | 不使用 `order`、`group`、`user` 等 |

### 4.2 按字段角色细分

| 角色 | 命名格式 | 示例 |
|------|---------|------|
| 主键 | `id` | `id bigint` |
| 外键 | `{关联表}_id` | `customer_id`、`warehouse_id`、`product_id` |
| 布尔标记 | `is_` 前缀 | `is_deleted`、`is_batch_manage`、`is_enabled` |
| 时间戳 | `{动作}_at` | `created_at`、`updated_at`、`deleted_at`、`audited_at` |
| 操作人 | `{动作}_by` | `created_by`、`updated_by` |
| 编码 | `{业务}_code` 或 `code` | `product_code`、`customer_code` |
| 名称 | `{业务}_name` 或 `name` | `product_name`、`customer_name` |
| 数量 | `qty` / `base_qty` | `qty`（业务单位数量）、`base_qty`（基础单位换算数量） |
| 金额 | `{业务}_amount` | `total_amount`、`tax_amount`、`discount_amount` |
| 单价 | `{业务}_price` | `unit_price`、`tax_price` |
| 税率 | `tax_rate` | `tax_rate` |
| 备注 | `remark` | `remark` |
| 状态 | `status` | `status` |
| 排序 | `sort` 或 `sort_order` | `sort` |

### 4.3 扩展字段命名

| 字段名 | 数据类型 | 用途 |
|--------|---------|------|
| ext_str1 ~ ext_str10 | varchar(200) | 自定义字符串字段 |
| ext_num1 ~ ext_num5 | decimal(18,8) | 自定义数值字段 |
| ext_date1 ~ ext_date3 | date | 自定义日期字段 |
| ext_bool1 ~ ext_bool3 | boolean | 自定义布尔字段 |
| ext_json | jsonb | 自定义 JSON 扩展字段 |

---

## 5. 约束命名

| 约束类型 | 命名格式 | 示例 |
|---------|---------|------|
| 主键约束 | `pk_{表名}` | `pk_sale_order` |
| 唯一约束 | `uk_{表名}_{字段名}` | `uk_sale_order_order_no` |
| 检查约束 | `ck_{表名}_{字段名}` | `ck_sale_order_status` |
| 外键约束 | `fk_{表名}_{关联表}` | `fk_sale_order_customer` |
| 默认值约束 | 使用有意义的名称或由系统自动生成 | `df_sale_order_status` |

---

## 6. 索引命名

| 索引类型 | 命名格式 | 示例 |
|---------|---------|------|
| 主键索引 | `pk_{表名}` | `pk_sale_order` |
| 唯一索引 | `uk_{表名}_{字段名}` | `uk_sale_order_order_no` |
| 普通索引 | `idx_{表名}_{字段名}` | `idx_sale_order_tenant_id` |
| 联合索引 | `idx_{表名}_{字段1}_{字段2}` | `idx_sale_order_tenant_id_customer_id` |
| 部分唯一索引 | `uk_{表名}_{字段名}_active` | `uk_sale_order_order_no_active` |

---

## 7. 序列命名

| 规则 | 格式 | 示例 |
|------|------|------|
| 自增主键序列 | `{表名}_id_seq` | `sale_order_id_seq` |
| 业务编号序列 | `seq_{业务}_no` | `seq_sale_order_no` |

> 注：使用 `BIGSERIAL` 类型时，PostgreSQL 自动按 `{表名}_{字段名}_seq` 格式生成序列。

---

## 8. 函数与视图命名

| 对象类型 | 命名格式 | 示例 |
|---------|---------|------|
| 数据库函数 | `fn_{模块}_{功能}` | `fn_sale_calc_total` |
| 触发器函数 | `trg_{表名}_{时机}_{操作}` | `trg_sale_order_before_update` |
| 视图 | `v_{模块}_{用途}` | `v_sale_order_summary` |
| 物化视图 | `mv_{模块}_{用途}` | `mv_inv_stock_summary` |

---

## 9. Flyway 迁移脚本命名

### 9.1 命名格式

```
V{主版本}.{次版本}.{修订号}__{描述}.sql
```

| 命名元素 | 说明 | 示例 |
|---------|------|------|
| V | 版本化脚本前缀（大写V） | V |
| 主版本.次版本.修订号 | 语义化版本号 | 1.0.0、1.0.1 |
| __ | 双下划线分隔版本号与描述 | __ |
| 描述 | 蛇形命名（snake_case），简洁描述 | init_schema、add_sale_order |

### 9.2 脚本目录结构

```
db/migration/
├── V1.0.0__init_schema.sql
├── V1.0.1__init_data.sql
├── V1.0.2__create_org_tables.sql
├── V1.0.3__create_prod_tables.sql
├── V1.1.0__add_sale_order.sql
└── V1.1.1__alter_customer_add_ext.sql
```

### 9.3 脚本内容规范

- 文件编码：UTF-8
- 脚本开头注释说明变更内容、作者、日期
- DDL 语句使用标准 PostgreSQL 语法
- 每个 `CREATE TABLE` 必须包含全部通用必含字段（10个）
- 索引在 `CREATE TABLE` 之后单独创建
- 数据初始化脚本（INSERT）与结构脚本（CREATE TABLE）分开
- COMMENT 注释紧跟在对应 DDL 语句之后

---

## 10. 通用必含字段

所有业务表**必须**包含以下 10 个通用字段：

| 字段名 | 数据类型 | 默认值 | 说明 |
|--------|---------|--------|------|
| id | bigint | 自增 | 主键ID |
| tenant_id | bigint | NOT NULL | 租户ID，多租户隔离 |
| created_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 创建时间，自动填充，不可修改 |
| updated_at | timestamp | DEFAULT CURRENT_TIMESTAMP | 更新时间，自动更新，不可手动修改 |
| created_by | bigint | — | 创建人ID，自动填充，不可修改 |
| updated_by | bigint | — | 修改人ID，自动更新 |
| is_deleted | boolean | DEFAULT false | 软删除标记 |
| owner_dept_id | bigint | — | 所属部门ID，数据权限用 |
| owner_id | bigint | — | 数据负责人ID，数据权限用 |
| version | int | DEFAULT 1 | 乐观锁版本号，每次更新+1 |

---

## 11. 单据主表特有字段

所有单据类主表（销售订单、采购订单、出入库单等），除 10 个通用字段外，还必须包含：

| 字段名 | 数据类型 | 默认值 | 说明 |
|--------|---------|--------|------|
| order_no | varchar(50) | NOT NULL | 单据编号，由编码组件自动生成 |
| order_date | date | DEFAULT CURRENT_DATE | 单据日期 |
| status | varchar(30) | DEFAULT 'draft' | 单据状态，状态机驱动 |
| remark | varchar(500) | — | 备注 |

---

## 12. 明细从表商品冗余字段

所有含商品的明细从表（有 product_id 字段），除 product_id 外，还必须冗余存储以下快照字段：

| 字段名 | 数据类型 | 说明 |
|--------|---------|------|
| product_code | varchar(50) | 商品编码快照 |
| product_name | varchar(100) | 商品名称快照 |
| model | varchar(100) | 型号快照 |
| spec | varchar(100) | 规格快照 |
| brand | varchar(50) | 品牌快照 |
| unit_id | bigint | 所选单位ID |
| unit | varchar(30) | 单位中文名 |
| qty | decimal(18,6) | 按所选单位的数量 |
| is_multi_unit | boolean | 是否多单位 |
| conversion_rate | decimal(18,6) | 转换比例 |
| base_unit_id | bigint | 基础单位ID |
| base_qty | decimal(18,6) | 换算数量（基础单位），库存增减统一使用 |
