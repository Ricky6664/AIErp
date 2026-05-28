# P0-014-001-001 JUnit 5 + Mockito测试依赖引入

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-014-001-001 |
| 任务名称 | JUnit 5 + Mockito测试依赖引入 |
| 所属模块 | P0-014 |
| 优先级 | P0 |
| 任务类型 | 配置/基础设施 |

## 二、任务目标


在pom.xml中引入后端测试框架依赖：junit-jupiter 5.10.x(测试核心)、mockito-core 5.x + mockito-junit-jupiter 5.x(Mock框架)、assertj-core 3.x(流式断言)、h2database 2.x(Mapper层内存数据库)、testcontainers-postgresql 1.19.x(集成测试容器)、spring-boot-starter-test、jacoco-maven-plugin 0.8.x(覆盖率报告)

## 三、前置依赖

### 3.1 前置任务

- 无特定前置任务依赖

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用




| 规范文档名 | 引用原因 |
|-----------|--------|
| 全局规范-项目架构与开发约束 | 项目架构、技术约束与任务依赖关系 |
| 全局规范-末端任务文档编写规范 | 末端任务文档结构与内容规范 |
| 全局规范-AI开发执行手册 | AI开发执行流程与质量标准 |
| 全局规范-测试开发规范 | 测试开发与验证规范约束 |

## 五、详细开发规格




> **本任务模块上下文**（来源：P0-014模块开发指南）
> - 后端测试框架：JUnit 5.10.x + Mockito 5.x + AssertJ 3.x + Testcontainers 1.19.x + H2 2.x + JaCoCo 0.8.x
> - 前端测试框架：Vitest 1.x + @vue/test-utils 2.x + happy-dom + @vitest/coverage-v8 + MSW 2.x
> - 覆盖率门禁：后端Service>=80%/Controller>=70%/Mapper>=60%，前端组件>=70%
> - 测试命名：类名<被测类>Test，方法名should_<行为>_when_<条件>，结构given/when/then三段式

### 5.1 pom.xml依赖声明
- junit-jupiter 5.10.3 (test scope)
- mockito-core 5.11.0 + mockito-junit-jupiter 5.11.0 (test scope)
- assertj-core 3.25.3 (test scope)
- h2 2.2.224 (test scope)
- testcontainers:postgresql 1.19.7 (test scope)
- spring-boot-starter-test (test scope)
- jacoco-maven-plugin 0.8.11

### 5.2 验证
- `mvn dependency:tree` 确认依赖引入
- `mvn test` 确认测试框架可正常执行

### 5.3 版本锁定
- 所有版本通过dependencyManagement统一管理

## 六、交付物清单



| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | pom.xml | JUnit 5/Mockito/AssertJ/H2/Testcontainers/JaCoCo依赖声明 |
| 2 | src/test/resources/application-test.yml | 测试环境配置 |

## 七、验收标准



| 序号 | 检查项 | 验证方法 |
|:---:|--------|--------|
| 1 | junit-jupiter 5.10.x引入正确 | mvn dependency:tree验证 |
| 2 | mockito 5.x引入正确 | mvn dependency:tree验证 |
| 3 | assertj + h2 + testcontainers引入正确 | 依赖树验证 |
| 4 | jacoco 0.8.x配置正确 | mvn jacoco:prepare-agent |
| 5 | 测试框架可正常执行 | mvn test无报错 |

## 八、易错警示


> ⚠️ 测试依赖版本必须与模块开发指南锁定版本一致，版本不一致可能导致API不兼容

> ⚠️ spring-boot-starter-test已包含JUnit 5，但需要排除junit-vintage-engine避免JUnit 4/5混用

> ⚠️ h2database版本必须与PostgreSQL兼容模式配置匹配，高版本H2有breaking changes

> ⚠️ testcontainers版本必须与Docker Engine版本兼容

> ⚠️ JaCoCo插件版本必须与JDK版本匹配，JDK 17+需要JaCoCo 0.8.7+
