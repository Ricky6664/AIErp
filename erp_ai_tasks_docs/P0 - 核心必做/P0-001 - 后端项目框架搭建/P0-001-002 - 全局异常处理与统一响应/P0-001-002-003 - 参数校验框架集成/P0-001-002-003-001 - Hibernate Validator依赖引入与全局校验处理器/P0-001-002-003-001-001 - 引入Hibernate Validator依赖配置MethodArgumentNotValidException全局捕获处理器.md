# P0-001-002-003-001-001 引入Hibernate Validator依赖+配置MethodArgumentNotValidException全局捕获处理器

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-001-002-003-001-001 |
| 任务名称 | 引入Hibernate Validator依赖+配置MethodArgumentNotValidException全局捕获处理器 |
| 所属模块 | P0-001 |
| 优先级 | P0 |
| 任务类型 | 配置/基础设施 |

## 二、任务目标

引入spring-boot-starter-validation依赖、在GlobalExceptionHandler中添加MethodArgumentNotValidException与ConstraintViolationException处理器：提取FieldError列表转换为List<ValidationError>、返回RT.fail(30001, "参数校验失败").data(errorList)、Controller方法参数使用@Valid/@Validated注解触发校验

## 三、前置依赖

### 3.1 前置任务

- P0-001-002-003-001 Hibernate Validator依赖引入与全局校验处理器（父任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用

| 规范文档名 | 引用原因 |
|-----------|---------|
| 全局规范-项目架构与开发约束 | 项目架构、技术约束与任务依赖关系 |
| 全局规范-后端代码规范 | 后端代码开发规范约束 |
| 全局规范-末端任务文档编写规范 | 末端任务文档结构与编写规范约束 |
| 全局规范-AI开发执行手册 | AI开发执行流程与规范约束 |

## 五、详细开发规格


> **📦 本任务模块上下文**
> - 任务类型：框架基础设施开发
> - 技术栈：JDK 17, Spring Boot 3.4.x, MyBatis-Plus 3.5.5, PostgreSQL 15+, Redis 7.x
>
> 💡 开发本任务时，请严格遵循上述技术约束。

### 5.1 详细规格
引入spring-boot-starter-validation依赖、在GlobalExceptionHandler中添加MethodArgumentNotValidException与ConstraintViolationException处理器：提取FieldError列表转换为List<ValidationError>、返回RT.fail(30001, "参数校验失败").data(errorList)、Controller方法参数使用@Valid/@Validated注解触发校验

### 5.2 实现要求
1. 严格按照上述规格实现，不要遗漏任何配置项、字段或方法
2. 代码结构清晰、命名规范、注释完整
3. 使用项目统一的基础类(BaseEntity/RT/PageResult等)
4. 配置类必须加@Component/@Configuration注解

### 5.3 验证要点
1. 编译无错误、无警告
2. 单元测试覆盖核心逻辑
3. 与上下游模块集成正确
4. 符合全局代码规范与架构约束

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | pom.xml | validation依赖 |
| 2 | src/main/java/com/erp/common/exception/GlobalExceptionHandler.java | 校验异常处理 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | @Valid/@Validated触发校验 | 传空字段验证 |
| 2 | 校验失败返回code=30001与FieldError列表 | 检查响应 |
| 3 | 自定义校验注解(@Phone/@IdCard)正确实现 | 传错误值验证 |

## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码

> ⚠️ 注意多租户隔离(tenant_id)

> ⚠️ 确保逻辑删除字段(is_deleted)正确处理
