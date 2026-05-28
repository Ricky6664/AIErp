# P2-001-001-003-001-006 编写ExpenseBudgetVO/ExpenseBudgetListVO

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-001-001-003-001-006 |
| 任务名称 | 编写ExpenseBudgetVO/ExpenseBudgetListVO |
| 所属模块 | P2-001 |
| 优先级 | P2 |
| 任务类型 | Entity/DTO/VO数据模型 |

## 二、任务目标

定义ExpenseBudgetVO详情对象：完整业务字段+关联翻译(deptName/userName)；@JsonFormat日期格式化；字典翻译字段(statusName)；BigDecimal金额精度

## 三、前置依赖

### 3.1 前置任务

- P2-001-001-003-001 费用预算Entity/DTO/VO定义（父任务）
- P2-001-001-003-001-005 编写ExpenseBudgetQueryDTO（前序兄弟任务）

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

### 5.1 VO定义

```java
@Data
public class ExpenseBudgetVO {
    private Long id;
    private String code;
    private String name;
    private Integer status;
    @Schema(description = "状态名称") private String statusName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "部门名称") private String deptName;

    private BigDecimal amount;
    private List<ExpenseBudgetDetailVO> details;
}
```

### 5.2 转换规则
- @JsonFormat统一日期格式
- 字典值通过DictTranslateService翻译
- 关联实体通过ID查名称
## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/expense/vo/ExpenseBudgetVO.java | ExpenseBudgetVO |


## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | @JsonFormat日期格式 | JSON序列化 |
| 2 | 字典翻译字段 | 查看详情 |
| 3 | 关联翻译(deptName) | 查看详情 |
| 4 | BigDecimal精度 | 查返回值 |


## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE

> ⚠️ @JsonFormat时区GMT+8

> ⚠️ 字典翻译用DictTranslateService

> ⚠️ BigDecimal保留精度
