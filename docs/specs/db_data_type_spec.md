# 数据类型规范

> 版本：V1.0
> 创建日期：2026-06-01
> 用途：定义数据库字段数据类型约定、精度规范、默认值策略，作为所有DDL任务的开发约束基准。

---

## 1. 数据类型总览

| 数据类型 | 物理定义 | 适用场景 | 说明 |
|---------|---------|---------|------|
| bigint | `bigint` | 主键ID、外键、租户ID、操作人ID | 长整数，自增主键使用 BIGSERIAL |
| integer | `int` | 排序号、版本号、小范围计数 | 4字节整数 |
| smallint | `smallint` | 状态字段、类型字段 | 2字节整数，预留扩展 |
| varchar(N) | `varchar(N)` | 名称、编码、备注、编号 | 可变长度字符串，N 为最大字符数 |
| text | `text` | 长文本、富文本内容、JSON字符串 | 无长度限制 |
| decimal(18,8) | `decimal(18,8)` | 金额、单价、数量、转换率、税率、百分比 | 物理精度 18 位总长、8 位小数 |
| date | `date` | 业务日期（单据日期、生产日期、有效期） | 仅日期，无时间 |
| timestamp | `timestamp` | 创建时间、更新时间、审核时间 | 日期+时间 |
| boolean | `boolean` | 布尔标记（is_ 前缀字段） | true/false |
| jsonb | `jsonb` | 扩展JSON、配置存储、动态结构 | 二进制JSON，支持索引 |

---

## 2. 数值精度规范

### 2.1 核心原则

**数据库物理字段统一使用 `decimal(18,8)`，实际显示精度由系统参数动态控制。**

此设计确保：
- 数据库层面有足够精度存储，不会因精度不足丢失数据
- 显示精度（输入框小数位数、列表展示位数）由系统参数决定，无需改DDL
- 前端录入组件的 `precision` 属性从系统参数读取
- 后端校验时按系统参数值截断/四舍五入

### 2.2 物理精度定义

| 字段类别 | DDL类型 | 物理精度 | 控制参数 |
|---------|---------|:---:|---------|
| 金额（amount） | `decimal(18,8)` | 18位总长, 8位小数 | `system.decimal_places_amount`（默认2） |
| 单价（price） | `decimal(18,8)` | 18位总长, 8位小数 | `sale.decimal_places_price`（默认4） |
| 数量（qty/base_qty） | `decimal(18,8)` | 18位总长, 8位小数 | `system.decimal_places_qty`（默认6） |
| 转换率（conversion_rate） | `decimal(18,8)` | 18位总长, 8位小数 | `system.decimal_places_qty`（默认6） |
| 税率（tax_rate） | `decimal(18,8)` | 18位总长, 8位小数 | `system.decimal_places_rate`（默认4） |
| 百分比（percentage） | `decimal(18,8)` | 18位总长, 8位小数 | `system.decimal_places_rate`（默认4） |
| 扩展数值（ext_num*） | `decimal(18,8)` | 18位总长, 8位小数 | 用户自定义 |

### 2.3 精度参数化架构

```
系统参数表 (sys_param)
  ├── system.decimal_places_amount  → 控制金额类字段显示精度
  ├── system.decimal_places_qty     → 控制数量/转换率类字段显示精度
  ├── system.decimal_places_price   → 控制单价类字段显示精度
  └── system.decimal_places_rate    → 控制税率/百分比类字段显示精度

      ↓ 前端读取参数

前端录入组件（el-input-number / el-input）
  └── :precision="decimalPlacesAmount"  ← 从参数动态读取

      ↓ 后端校验

后端 Service 层
  └── 使用 @DecimalPlaces 或手工按参数截断
```

### 2.4 文档标注约定

在任务文档和表设计文档中，金额/单价/数量字段可能标注为 `decimal(18,6)` 或 `decimal(18,4)` 等，这仅表示该字段的**业务精度语义**（金额常用2位、数量常用6位、单价常用4位），实际建表 DDL **统一使用 `decimal(18,8)`**。

---

## 3. 字符串类型规范

| 场景 | 数据类型 | 典型长度 | 示例 |
|------|---------|:---:|------|
| 编码（code） | varchar(50) | 50 | product_code, customer_code |
| 名称（name） | varchar(100) | 100 | product_name, warehouse_name |
| 短名称 | varchar(50) | 50 | unit, brand |
| 编号（no） | varchar(50) | 50 | order_no, bill_no |
| 备注 | varchar(500) | 500 | remark |
| 扩展字符串 | varchar(200) | 200 | ext_str1 ~ ext_str10 |
| 长文本 | text | — | description, content |
| URL/路径 | varchar(500) | 500 | file_url, avatar_url |
| 手机号 | varchar(20) | 20 | phone, mobile |
| 邮箱 | varchar(100) | 100 | email |
| 地址 | varchar(300) | 300 | address |
| IP地址 | varchar(45) | 45 | ip_address（支持IPv6） |

---

## 4. 整数类型规范

| 场景 | 数据类型 | 说明 |
|------|---------|------|
| 主键ID | bigint / BIGSERIAL | 所有表主键统一 |
| 外键 | bigint | 与主键类型一致 |
| 租户ID | bigint | — |
| 操作人ID | bigint | created_by, updated_by |
| 部门ID | bigint | owner_dept_id |
| 排序号 | int | sort, sort_order |
| 版本号 | int | version，乐观锁 |
| 状态/类型 | smallint | status, type，预留扩展 |
| 计数 | int | 小范围计数 |

---

## 5. 状态与类型字段规范

### 5.1 使用 SMALLINT 而非 ENUM

状态和类型字段**统一使用 `smallint`**，**禁止使用 PostgreSQL ENUM 类型**。

**原因：**
- ENUM 类型扩展需要 `ALTER TYPE ... ADD VALUE`，有性能开销且不可在事务内执行
- SMALLINT 扩展只需修改注释，无需变更表结构
- SMALLINT 与 Java Integer 映射简单，MyBatis-Plus 原生支持
- 状态值的含义通过 COMMENT 注释明确说明

### 5.2 状态字段 COMMENT 规范

```sql
-- 状态字段注释格式：字段含义：值1=含义1/值2=含义2/值3=含义3
COMMENT ON COLUMN sale_order.status IS '单据状态：0=草稿/1=待审核/2=已审核/3=已关闭/4=已作废';
```

---

## 6. 布尔类型规范

| 规则 | 说明 | 示例 |
|------|------|------|
| 数据类型 | `boolean` | PostgreSQL 原生布尔 |
| 命名前缀 | `is_` | `is_deleted`、`is_enabled` |
| 默认值 | `DEFAULT false` | 大多数场景默认 false |
| 禁止行为 | 禁止用 smallint(0/1) 替代 boolean | 使用原生 boolean |

**常用布尔字段：**

| 字段名 | 默认值 | 说明 |
|--------|--------|------|
| is_deleted | false | 软删除标记 |
| is_enabled | true | 启用/禁用 |
| is_multi_unit | false | 是否多单位 |
| is_batch_manage | false | 是否批次管理 |
| is_serial_manage | false | 是否序列号管理 |
| is_location_manage | false | 是否库位管理 |
| is_shelf_life | false | 是否效期管理 |
| is_inventory | false | 是否参与库存计算 |
| is_default | false | 是否默认 |

---

## 7. 时间类型规范

| 场景 | 数据类型 | 说明 |
|------|---------|------|
| 创建时间 | `timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP` | 自动填充 |
| 更新时间 | `timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP` | 自动更新 |
| 业务日期 | `date` | 单据日期、生产日期、有效期 |
| 审核时间 | `timestamp` | 可空，审核后填充 |
| 删除时间 | `timestamp` | 可空，软删除后填充 |

> 时间类型统一使用 `timestamp`（不带时区），应用层统一使用 UTC 时间，前端展示时按用户时区转换。

---

## 8. JSON 类型规范

| 场景 | 数据类型 | 说明 |
|------|---------|------|
| 扩展JSON | `jsonb` | ext_json 字段 |
| 配置存储 | `jsonb` | 动态配置项 |
| 快照数据 | `jsonb` | 非结构化快照 |

> 统一使用 `jsonb`（二进制JSON），不使用 `json`。`jsonb` 支持索引，查询性能更优。

---

## 9. CHECK 约束规范

对数值字段添加 CHECK 约束，确保数据合法性：

```sql
-- 金额/数量/单价/税率 非负
ALTER TABLE sale_order_detail
  ADD CONSTRAINT ck_sale_order_detail_qty CHECK (qty >= 0);

ALTER TABLE sale_order_detail
  ADD CONSTRAINT ck_sale_order_detail_unit_price CHECK (unit_price >= 0);

-- 税率范围 0-100
ALTER TABLE sale_order_detail
  ADD CONSTRAINT ck_sale_order_detail_tax_rate CHECK (tax_rate >= 0 AND tax_rate <= 100);
```

| CHECK 规则 | 适用字段 |
|-----------|---------|
| >= 0 | 金额、数量、单价、转换率 |
| >= 0 AND <= 100 | 税率 |
| >= 0 AND <= 1 | 折扣率 |

---

## 10. 主键策略

| 策略 | 说明 |
|------|------|
| 数据类型 | `BIGSERIAL`（等价于 `bigint` + 自增序列） |
| 命名 | 统一使用 `id` |
| 是否业务主键 | 否，所有表使用代理主键，业务唯一性通过唯一索引保证 |
