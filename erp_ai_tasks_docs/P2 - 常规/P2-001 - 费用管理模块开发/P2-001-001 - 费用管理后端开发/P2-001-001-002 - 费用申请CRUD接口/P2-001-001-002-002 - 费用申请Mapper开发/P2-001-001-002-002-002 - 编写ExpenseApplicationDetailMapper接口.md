# P2-001-001-002-002-002 编写ExpenseApplicationDetailMapper接口

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-001-001-002-002-002 |
| 任务名称 | 编写ExpenseApplicationDetailMapper接口 |
| 所属模块 | P2-001 |
| 优先级 | P2 |
| 任务类型 | Mapper数据访问层 |

## 二、任务目标

编写ExpenseApplicationDetailMapper接口：extends BaseMapperX<ExpenseApplicationDetailEntity>；@Mapper注解；自定义方法selectByCode/selectPageByCondition/selectByMainId/聚合统计方法

## 三、前置依赖

### 3.1 前置任务

- P2-001-001-002-002 费用申请Mapper开发（父任务）
- P2-001-001-002-002-001 编写ExpenseApplicationMapper接口（前序兄弟任务）

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

### 5.1 Mapper接口

```java
@Mapper
public interface ExpenseApplicationDetailMapper extends BaseMapperX<ExpenseApplicationDetailEntity> {
    ExpenseApplicationDetailEntity selectByCode(@Param("code") String code);
    Page<ExpenseApplicationDetailListVO> selectPageByCondition(Page page, @Param("query") ExpenseApplicationDetailQueryDTO query);
    List<ExpenseApplicationDetailEntity> selectByMainId(@Param("mainId") Long mainId);
    List<ExpenseApplicationDetailVO> selectSumByDeptPeriod(@Param("deptId") Long deptId, @Param("startDate") LocalDate start, @Param("endDate") LocalDate end);
}
```

### 5.2 规范
- 继承BaseMapperX(MyBatis-Plus扩展)
- @Mapper注解
- 命名：selectXxx/insertXxx/updateXxx/deleteXxx
- 复杂查询走XML
## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/expense/mapper/ExpenseApplicationDetailMapper.java | ExpenseApplicationDetailMapper接口 |


## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | extends BaseMapperX | IDE检查 |
| 2 | @Mapper注解 | 启动测试 |
| 3 | 命名规范selectXxx | IDE检查 |
| 4 | @Param参数完整 | 编译测试 |


## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE

> ⚠️ @Mapper让Spring扫描

> ⚠️ 命名规范selectXxx/insertXxx

> ⚠️ 多参数@Param
