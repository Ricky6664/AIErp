# P2-014-001-001-001-001 定义Controller类+@RequestMapping路径+注入Service

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-014-001-001-001-001 |
| 任务名称 | 定义Controller类+@RequestMapping路径+注入Service |
| 所属模块 | P2-014 |
| 优先级 | P2 |
| 任务类型 | Service服务层 |

## 二、任务目标

定义OpsController类：@RestController+@RequestMapping("/api/ops/scheduled-task")类级路径，@Tag(name="运维管理") Knife4j分组注解，@Autowired注入OpsService，类级@SaCheckPermission权限前缀

## 三、前置依赖

### 3.1 前置任务

- P2-014-001-001-001 定时任务Entity/Mapper/Service/Controller开发（父任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用


| 规范文档名 | 引用原因 |
|-----------|---------|
| 全局规范-项目架构与开发约束 | 项目架构、技术约束与任务依赖关系 |
| 全局规范-末端任务文档编写规范 | 末端任务文档标准化模板与质量要求 |
| 全局规范-AI开发执行手册 | AI任务解读与执行流程规范 |
| 全局规范-后端代码规范 | 后端代码开发规范约束 |
| 全局规范-API接口规范 | API接口设计与RESTful规范约束 |
## 五、详细开发规格

> **📦 本任务模块上下文**（来源：P2-014模块开发指南）
> - 本模块涉及数据表：参见模块开发指南
> - 本模块涉及API：/api/ops/scheduled-task, /api/ops/task-log, /api/ops/backup, /api/ops/alert, /api/ops/server-monitor
> - 本模块业务规则：本模块包含运维级专属约束，覆盖监控、备份、恢复、告警四大运维领域：; 定时任务管理：集成XXL-JOB调度框架，支持Cron表达式配置与手动触发，任务执行日志自动记录。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 Controller类定义

```java
@RestController
@RequestMapping("/api/ops/scheduled-task")
@Tag(name = "运维管理")
@SaCheckPermission("ops")
public class OpsController {

    @Autowired
    private OpsService opsService;
}
```

### 5.2 类级注解

- @RestController：声明为REST控制器
- @RequestMapping("/api/ops/scheduled-task")：类级路径，符合 /api/{module}/{resource} 规范
- @Tag(name="运维管理")：Knife4j分组
- @SaCheckPermission：Sa-Token权限前缀

### 5.3 验证

1. @RequestMapping路径符合RESTful规范（复数形式）
2. @Tag/@SaCheckPermission注解完整
3. Service注入正确

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/ops/controller/OpsController.java | Controller类 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | @RequestMapping路径符合/api/{module}/{resource}规范 | 代码review |
| 2 | @Tag(name="xxx") Knife4j分组注解存在 | 访问Knife4j文档页 |
| 3 | Service通过@Autowired正确注入 | 代码review |
| 4 | @SaCheckPermission权限注解存在 | 代码review |
| 5 | 应用启动无报错 | mvn spring-boot:run |

## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id条件

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE而非DELETE

> ⚠️ 事务边界要准确——@Transactional加在Service方法而非Controller

> ⚠️ 避免大事务——查询操作尽量移出事务，仅写操作在事务内

> ⚠️ 异常统一抛出BusinessException——由GlobalExceptionHandler统一处理

> ⚠️ 编码生成调用要幂等——失败重试不重复生成编码

> ⚠️ [运维管理模块] 定时任务CRON表达式必须做合法性校验，防止无效表达式导致调度器异常

> ⚠️ [运维管理模块] 任务执行日志需设置保留天数，超期日志自动归档或清理防止日志表膨胀

> ⚠️ [运维管理模块] 监控数据采集使用异步线程池，采集失败时不得影响主业务
