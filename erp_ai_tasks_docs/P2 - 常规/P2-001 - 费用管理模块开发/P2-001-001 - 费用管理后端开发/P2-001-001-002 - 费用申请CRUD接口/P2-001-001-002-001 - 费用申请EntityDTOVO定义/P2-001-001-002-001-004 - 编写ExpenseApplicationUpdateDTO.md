# P2-001-001-002-001-004 编写ExpenseApplicationUpdateDTO

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-001-001-002-001-004 |
| 任务名称 | 编写ExpenseApplicationUpdateDTO |
| 所属模块 | P2-001 |
| 优先级 | P2 |
| 任务类型 | Entity/DTO/VO数据模型 |

## 二、任务目标

定义ExpenseApplicationUpdateDTO：继承CreateDTO复用校验；新增@NotNull id字段(主键)；@NotNull version字段(乐观锁)；Service层校验version一致性

## 三、前置依赖

### 3.1 前置任务

- P2-001-001-002-001 费用申请Entity/DTO/VO定义（父任务）
- P2-001-001-002-001-003 编写ExpenseApplicationCreateDTO（前序兄弟任务）

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

### 5.1 UpdateDTO定义

```java
@Data
public class ExpenseApplicationUpdateDTO extends ExpenseApplicationCreateDTO {
    @NotNull(message = "ID不能为空")
    private Long id;

    @NotNull(message = "版本号不能为空")
    private Integer version;
}
```

### 5.2 设计
- 继承CreateDTO复用校验
- 新增id+version必填
- Service层校验version一致性
## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/expense/dto/ExpenseApplicationUpdateDTO.java | ExpenseApplication更新DTO |


## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | 继承CreateDTO | IDE检查 |
| 2 | id @NotNull | 无ID测试 |
| 3 | version @NotNull | 无version测试 |
| 4 | 乐观锁冲突测试 | 并发更新 |


## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE

> ⚠️ 必须包含id+version字段

> ⚠️ 继承CreateDTO复用校验
