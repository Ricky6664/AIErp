# 索引设计规范

> 版本：V1.0
> 创建日期：2026-06-01
> 用途：定义数据库索引命名规范、创建策略、设计原则，作为所有DDL任务的开发约束基准。

---

## 1. 索引命名规范

| 索引类型 | 命名格式 | 示例 |
|---------|---------|------|
| 主键索引 | `pk_{表名}` | `pk_sale_order` |
| 唯一索引 | `uk_{表名}_{字段名}` | `uk_sale_order_order_no` |
| 普通索引 | `idx_{表名}_{字段名}` | `idx_sale_order_customer_id` |
| 联合索引 | `idx_{表名}_{字段1}_{字段2}_{字段3}` | `idx_sale_order_tenant_id_status` |
| 部分唯一索引 | `uk_{表名}_{字段名}_active` | `uk_sale_order_order_no_active` |
| 外键索引 | `idx_{表名}_{外键字段}` | `idx_sale_order_customer_id` |

### 1.1 命名约束

- 全小写 + 下划线分隔
- 字段名按在索引中的顺序排列
- 联合索引字段数不超过 5 个，超出则合并中间字段（如 `idx_{表}_{字段1}_{字段2}_etc`）
- 部分唯一索引使用 `_active` 后缀，表示仅对 `is_deleted = false` 的数据生效

---

## 2. 索引创建时机

| 索引类型 | 创建时机 | 优先级 |
|---------|---------|:---:|
| 主键索引 | `CREATE TABLE` 时通过 `PRIMARY KEY` 自动创建 | 必须 |
| 唯一索引 | 业务唯一性字段，在 `CREATE TABLE` 后单独创建 | 必须 |
| 租户索引 | `tenant_id` 字段，所有业务表必须创建 | 必须 |
| 外键索引 | 所有外键字段（`_id` 后缀字段） | 建议 |
| 查询条件索引 | 高频 WHERE 条件字段 | 按需 |
| 排序索引 | 高频 ORDER BY 字段 | 按需 |
| 联合索引 | 高频联合查询条件 | 按需 |

---

## 3. 多租户索引规范

### 3.1 核心原则

**所有业务表必须创建以 `tenant_id` 为首列的索引。**

```sql
-- 基础租户索引（所有业务表必须有）
CREATE INDEX idx_{表名}_tenant_id ON {表名}(tenant_id);
```

### 3.2 联合索引中的 tenant_id 位置

`tenant_id` 必须作为联合索引的**首列**，确保租户隔离查询性能：

```sql
-- 正确：tenant_id 为首列
CREATE UNIQUE INDEX uk_sale_order_order_no_active
  ON sale_order(tenant_id, order_no) WHERE is_deleted = false;

-- 错误：tenant_id 不是首列
CREATE UNIQUE INDEX uk_sale_order_order_no_active
  ON sale_order(order_no, tenant_id) WHERE is_deleted = false;
```

### 3.3 唯一约束必须包含 tenant_id

所有唯一约束必须包含 `tenant_id` 字段：

```sql
-- 正确：包含 tenant_id
CREATE UNIQUE INDEX uk_sale_order_order_no_active
  ON sale_order(tenant_id, order_no) WHERE is_deleted = false;

-- 错误：缺少 tenant_id（跨租户会冲突）
CREATE UNIQUE INDEX uk_sale_order_order_no_active
  ON sale_order(order_no) WHERE is_deleted = false;
```

---

## 4. 软删除部分唯一索引

### 4.1 设计原因

涉及业务唯一性校验的字段，必须使用**部分唯一索引**（Partial Unique Index），仅对未删除数据强制唯一：

```sql
CREATE UNIQUE INDEX uk_{表名}_{字段名}_active
  ON {表名}(tenant_id, {字段名})
  WHERE is_deleted = false;
```

### 4.2 为什么不能用联合唯一索引

经典陷阱：`UNIQUE(tenant_id, code, is_deleted)` 在 boolean 类型 is_deleted 字段上使用时，同一租户同一编码只能有一条 `is_deleted=true` 的记录和一条 `is_deleted=false` 的记录，第二条删除记录就会冲突。部分唯一索引解决了此问题。

### 4.3 适用场景

| 场景 | 索引定义 |
|------|---------|
| 编码唯一 | `CREATE UNIQUE INDEX uk_{表}_code_active ON {表}(tenant_id, code) WHERE is_deleted = false` |
| 名称唯一 | `CREATE UNIQUE INDEX uk_{表}_name_active ON {表}(tenant_id, name) WHERE is_deleted = false` |
| 单据编号唯一 | `CREATE UNIQUE INDEX uk_{表}_order_no_active ON {表}(tenant_id, order_no) WHERE is_deleted = false` |

---

## 5. 外键索引规范

所有外键字段（`{关联表}_id`）必须创建索引，优化 JOIN 查询性能：

```sql
CREATE INDEX idx_sale_order_customer_id ON sale_order(customer_id);
CREATE INDEX idx_sale_order_detail_order_id ON sale_order_detail(order_id);
CREATE INDEX idx_sale_order_detail_product_id ON sale_order_detail(product_id);
```

---

## 6. 常用索引模板

### 6.1 基础模板（每张业务表必建）

```sql
-- 租户索引
CREATE INDEX idx_{表名}_tenant_id ON {表名}(tenant_id);

-- 业务唯一约束（部分唯一索引）
CREATE UNIQUE INDEX uk_{表名}_{编码字段}_active
  ON {表名}(tenant_id, {编码字段}) WHERE is_deleted = false;
```

### 6.2 单据主表模板

```sql
-- 基础
CREATE INDEX idx_{表名}_tenant_id ON {表名}(tenant_id);

-- 单据编号唯一
CREATE UNIQUE INDEX uk_{表名}_order_no_active
  ON {表名}(tenant_id, order_no) WHERE is_deleted = false;

-- 查询常用
CREATE INDEX idx_{表名}_tenant_id_status ON {表名}(tenant_id, status);
CREATE INDEX idx_{表名}_tenant_id_order_date ON {表名}(tenant_id, order_date);
CREATE INDEX idx_{表名}_customer_id ON {表名}(customer_id);
```

### 6.3 明细从表模板

```sql
-- 基础
CREATE INDEX idx_{表名}_tenant_id ON {表名}(tenant_id);

-- 关联主表
CREATE INDEX idx_{表名}_order_id ON {表名}(order_id);

-- 关联商品
CREATE INDEX idx_{表名}_product_id ON {表名}(product_id);
```

### 6.4 树形表模板（分类/部门/菜单等）

```sql
-- 基础
CREATE INDEX idx_{表名}_tenant_id ON {表名}(tenant_id);

-- 父节点查询
CREATE INDEX idx_{表名}_parent_id ON {表名}(parent_id);

-- 编码唯一
CREATE UNIQUE INDEX uk_{表名}_code_active
  ON {表名}(tenant_id, code) WHERE is_deleted = false;
```

---

## 7. 索引设计原则

### 7.1 应该创建索引的场景

- WHERE 子句中频繁使用的列
- JOIN 关联的外键列
- ORDER BY 排序的列
- 业务唯一性约束的列
- 租户隔离的 tenant_id 列
- 多列经常一起查询的组合

### 7.2 不应创建索引的场景

- 表数据量很小（< 1000 行）
- 列的值分布极不均匀且查询不频繁
- 频繁更新的列（索引维护开销大）
- text/jsonb 类型不加 B-tree 索引（使用 GIN 索引）
- 布尔字段不单独建索引（选择性低）

### 7.3 索引数量控制

- 单表索引总数建议不超过 10 个
- 优先创建覆盖面广的联合索引，减少单列索引数量
- 定期通过 `pg_stat_user_indexes` 分析索引使用情况，清理未使用的索引

---

## 8. 索引创建语法要求

- 索引在 `CREATE TABLE` 语句**之后**单独创建，不嵌入 `CREATE TABLE` 内部
- 每个索引语句独占一行，清晰可读
- 索引名称必须显式指定，不使用 PostgreSQL 自动生成名称

```sql
-- 正确：独立创建，名称显式指定
CREATE TABLE sale_order (
    ...
);
CREATE INDEX idx_sale_order_tenant_id ON sale_order(tenant_id);
CREATE UNIQUE INDEX uk_sale_order_order_no_active ON sale_order(tenant_id, order_no) WHERE is_deleted = false;
```

---

## 9. 性能约束

| 指标 | 目标值 | 说明 |
|------|:---:|------|
| 单表查询响应 | < 100ms | 10万数据量 |
| 联合查询响应 | < 500ms | 含 JOIN |
| 索引命中率 | > 95% | `pg_stat_user_indexes.idx_scan` 监控 |
