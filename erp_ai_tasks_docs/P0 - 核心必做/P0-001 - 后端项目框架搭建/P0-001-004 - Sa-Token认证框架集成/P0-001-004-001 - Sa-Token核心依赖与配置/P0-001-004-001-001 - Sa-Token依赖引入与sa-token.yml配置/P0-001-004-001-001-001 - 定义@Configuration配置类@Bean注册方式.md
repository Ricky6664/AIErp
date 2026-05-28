# P0-001-004-001-001-001 定义@Configuration配置类+@Bean注册方式

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-001-004-001-001-001 |
| 任务名称 | 定义@Configuration配置类+@Bean注册方式 |
| 所属模块 | P0-001 |
| 优先级 | P0 |
| 任务类型 | 配置/基础设施 |

## 二、任务目标

配置Sa-Token(SaTokenConfig @Configuration)：@Bean注册SaServletFilter拦截/api/**(排除/api/auth/login、/api/auth/logout、/doc.html、/v3/api-docs/**)、未登录返回RT.fail(ErrorCode.UNAUTHORIZED)、配置SaTokenConfigure路由拦截+注解鉴权双机制

## 三、前置依赖

### 3.1 前置任务

- P0-001-004-001-001 Sa-Token依赖引入与sa-token.yml配置（父任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用


| 规范文档名 | 引用原因 |
|-----------|---------|
| 全局规范-项目架构与开发约束 | 项目架构、技术约束与任务依赖关系 |
| 全局规范-后端代码规范 | 后端代码开发规范约束 |
| 全局规范-部署架构规范 | 部署与环境配置规范约束 |
| 全局规范-末端任务文档编写规范 | 末端任务文档结构与编写规范约束 |
| 全局规范-AI开发执行手册 | AI开发执行流程与规范约束 |

## 五、详细开发规格


> **📦 本任务模块上下文**
> - 任务类型：框架基础设施开发
> - 技术栈：JDK 17, Spring Boot 3.4.x, MyBatis-Plus 3.5.5, PostgreSQL 15+, Redis 7.x
>
> 💡 开发本任务时，请严格遵循上述技术约束。

### 5.1 详细规格
配置Sa-Token(SaTokenConfig @Configuration)：@Bean注册SaServletFilter拦截/api/**(排除/api/auth/login、/api/auth/logout、/doc.html、/v3/api-docs/**)、未登录返回RT.fail(ErrorCode.UNAUTHORIZED)、配置SaTokenConfigure路由拦截+注解鉴权双机制

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
| 1 | src/main/java/com/erp/config/SaTokenConfig.java | Sa-Token配置类 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | 关键配置项(doc.html)正确加载 | /actuator/env验证 |
| 2 | 配置语法正确，启动无报错 | 启动验证 |
| 3 | 敏感信息无硬编码 | 检查配置值 |

## 八、易错警示

> ⚠️ Sa-Token Token有效期单位是秒不是毫秒，2592000=30天

> ⚠️ active-timeout是临时有效期(无操作过期)，不是总有效期

> ⚠️ sa-token-redis-jackson版本必须与sa-token一致，否则序列化失败
