# P0-001-001-001-001-001 执行Spring Initializr生成项目

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-001-001-001-001-001 |
| 任务名称 | 执行Spring Initializr生成项目 |
| 所属模块 | P0-001 |
| 优先级 | P0 |
| 任务类型 | 综合开发任务 |

## 二、任务目标

访问start.spring.io生成Spring Boot 3.4.x项目：JDK 17、Maven、groupId=com.erp、artifactId=erp-ai、包名com.erp、打包方式Jar、依赖选择Spring Web+Spring Data JPA+Lombok

## 三、前置依赖

### 3.1 前置任务

- P0-001-001-001-001 Spring Initializr项目生成（父任务）

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
访问start.spring.io生成Spring Boot 3.4.x项目：JDK 17、Maven、groupId=com.erp、artifactId=erp-ai、包名com.erp、打包方式Jar、依赖选择Spring Web+Spring Data JPA+Lombok

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
| 1 | pom.xml | Maven项目配置文件 |
| 2 | src/main/java/com/erp/ErpAiApplication.java | 主启动类 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | Spring Boot 3.4.x项目结构正确 | mvn clean compile无错 |
| 2 | JDK 17 + Maven + 包名com.erp配置正确 | 检查pom.xml与src |
| 3 | 项目启动时间<10秒 | mvn spring-boot:run计时 |

## 八、易错警示

> ⚠️ Spring Boot版本必须3.4.x，不要用3.5.x(尚未稳定)

> ⚠️ JDK必须17+，Spring Boot 3.x不支持JDK 8/11

> ⚠️ 包名com.erp不要随意改，影响后续模块扫描
