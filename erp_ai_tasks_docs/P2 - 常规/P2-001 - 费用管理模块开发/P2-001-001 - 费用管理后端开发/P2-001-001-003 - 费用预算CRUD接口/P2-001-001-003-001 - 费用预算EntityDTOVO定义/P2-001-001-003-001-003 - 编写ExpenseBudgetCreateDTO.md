# P2-001-001-003-001-003 编写ExpenseBudgetCreateDTO

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-001-001-003-001-003 |
| 任务名称 | 编写ExpenseBudgetCreateDTO |
| 所属模块 | P2-001 |
| 优先级 | P2 |
| 任务类型 | Entity/DTO/VO数据模型 |

## 二、任务目标

定义ExpenseBudgetCreateDTO：@NotBlank标注必填字符串(编号/名称)；@NotNull标注必填引用字段(部门ID/币种ID)；@Size限制字段长度；@DecimalMin金额≥0；嵌套明细列表@Valid+@NotEmpty级联校验；@DateTimeFormat日期格式

## 三、前置依赖

### 3.1 前置任务

- P2-001-001-003-001 费用预算Entity/DTO/VO定义（父任务）
- P2-001-001-003-001-002 编写ExpenseBudgetDetailEntity类（前序兄弟任务）

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

## 五、详细开发规格

> **📦 本任务模块上下文**（来源：P2-001模块开发指南）
> - 本模块涉及数据表：参见模块开发指南
> - 本模块涉及API：/api/expense/claim, /api/expense/application, /api/expense/budget, /api/expense/budget/execution
> - 本模块业务规则：本模块无模块级专属约束，遵循全局规范。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 CreateDTO定义

```java
@Data
public class ExpenseBudgetCreateDTO {
    @NotBlank(message = "编号不能为空")
    @Size(max = 50, message = "编号长度不能超过50")
    private String code;

    @NotBlank(message = "名称不能为空")
    private String name;

    @NotNull(message = "部门不能为空")
    private Long deptId;

    @NotNull(message = "日期不能为空")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate bizDate;

    @DecimalMin(value = "0", message = "金额不能为负")
    private BigDecimal amount;

    @Valid @NotEmpty(message = "明细不能为空")
    private List<ExpenseBudgetDetailDTO> details;

    private String remark;
}
```

### 5.2 校验规则
- 字符串@NotBlank（含trim）
- 引用ID@NotNull
- 金额@DecimalMin(0)
- 嵌套@Valid+@NotEmpty级联校验
## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/expense/dto/ExpenseBudgetCreateDTO.java | ExpenseBudget创建DTO |


## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | 必填@NotBlank/@NotNull完整 | 缺必填测试 |
| 2 | @Size长度与DB一致 | 对照DDL |
| 3 | @DecimalMin金额≥0 | 负数测试 |
| 4 | 嵌套@Valid+@NotEmpty | 空明细测试 |
| 5 | @DateTimeFormat日期 | 日期解析测试 |


## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE

> ⚠️ @NotBlank(字符串)与@NotNull(对象)不要混用

> ⚠️ @Size长度与数据库一致

> ⚠️ 嵌套@Valid级联校验勿忘
