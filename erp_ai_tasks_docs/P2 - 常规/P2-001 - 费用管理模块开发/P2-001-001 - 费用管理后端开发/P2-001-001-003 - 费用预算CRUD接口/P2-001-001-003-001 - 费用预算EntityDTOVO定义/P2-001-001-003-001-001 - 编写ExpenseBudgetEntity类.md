# P2-001-001-003-001-001 编写ExpenseBudgetEntity类

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-001-001-003-001-001 |
| 任务名称 | 编写ExpenseBudgetEntity类 |
| 所属模块 | P2-001 |
| 优先级 | P2 |
| 任务类型 | Entity/DTO/VO数据模型 |

## 二、任务目标

定义ExpenseBudgetEntity类：@TableName("expense_expense_budget") + @TableId(type=IdType.ASSIGN_ID)雪花ID策略；业务字段使用@TableField显式映射；公共字段(create_by/create_time/update_by/update_time/tenant_id)通过MetaHandler自动填充；@TableLogic逻辑删除is_deleted默认0；@Version乐观锁version字段；implements Serializable

## 三、前置依赖

### 3.1 前置任务

- P2-001-001-003-001 费用预算Entity/DTO/VO定义（父任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用

| 规范文档名 | 引用原因 |
|-----------|---------|
| 全局规范-项目架构与开发约束 | 项目架构、技术约束与任务依赖关系 |
| 全局规范-末端任务文档编写规范 | 末端任务文档格式与内容规范约束 |
| 全局规范-AI开发执行手册 | AI辅助开发流程与执行标准 |
| 全局规范-后端代码规范 | 后端代码开发规范约束 |
| 全局规范-数据库规范 | 数据库字段映射与模型规范约束 |

## 五、详细开发规格

> **📦 本任务模块上下文**（来源：P2-001模块开发指南）
> - 本模块涉及数据表：参见模块开发指南
> - 本模块涉及API：/api/expense/claim, /api/expense/application, /api/expense/budget, /api/expense/budget/execution
> - 本模块业务规则：本模块无模块级专属约束，遵循全局规范。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 Entity类定义

```java
@Data
@TableName("expense_expense_budget")
public class ExpenseBudgetEntity implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    // 业务字段
    @TableField("code") private String code;        // 编号
    @TableField("name") private String name;        // 名称
    @TableField("status") private Integer status;   // 状态

    // 公共字段(MetaHandler自动填充)
    @TableField(fill = FieldFill.INSERT) private Long createBy;
    @TableField(fill = FieldFill.INSERT) private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE) private Long updateBy;
    @TableField(fill = FieldFill.INSERT_UPDATE) private LocalDateTime updateTime;
    @TableField("tenant_id") private Long tenantId;
    @TableLogic @TableField("is_deleted") private Integer isDeleted;
    @Version @TableField("version") private Integer version;
}
```

### 5.2 字段规范

- 所有字段使用@TableField显式声明列名
- 日期统一LocalDateTime，不用Date
- 金额BigDecimal(DECIMAL 18,4)
- 枚举Integer类型+字典翻译
- @TableLogic逻辑删除默认值0
- @Version乐观锁配合VersionMetaHandler

### 5.3 验证

1. @TableName值与DDL一致
2. 所有业务字段@TableField完整
3. 编译无警告
## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/expense/entity/ExpenseBudgetEntity.java | ExpenseBudget实体类 |


## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | @TableName("expense_expense_budget")正确 | 启动测试 |
| 2 | @TableId(type=ASSIGN_ID)策略 | 插入测试ID |
| 3 | 所有@TableField完整 | 与DDL对照 |
| 4 | @TableLogic逻辑删除默认0 | 查数据库 |
| 5 | @Version乐观锁 | 并发测试 |
| 6 | implements Serializable | IDE检查 |
| 7 | 日期LocalDateTime | IDE检查 |
| 8 | 编译无警告 | mvn compile |


## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE

> ⚠️ @TableField映射勿遗漏——所有业务字段显式声明

> ⚠️ LocalDateTime类型勿用Date

> ⚠️ implements Serializable勿忘

> ⚠️ 金额BigDecimal，禁用double/float

> ⚠️ @Version乐观锁配合VersionMetaHandler
