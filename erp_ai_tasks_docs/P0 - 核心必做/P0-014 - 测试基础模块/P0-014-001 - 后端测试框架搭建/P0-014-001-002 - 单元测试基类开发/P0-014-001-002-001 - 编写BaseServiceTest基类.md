# P0-014-001-002-001 编写BaseServiceTest基类

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-014-001-002-001 |
| 任务名称 | 编写BaseServiceTest基类 |
| 所属模块 | P0-014 |
| 优先级 | P0 |
| 任务类型 | Service服务层 |

## 二、任务目标


开发BaseServiceTest测试基类：@SpringBootTest + Testcontainers PostgreSQL容器配置 + 通用Mock方法 + AssertJ断言工具集成 + @Transactional事务自动回滚，为所有Service层集成测试提供统一基类

## 三、前置依赖

### 3.1 前置任务

- P0-014-001-002 单元测试基类开发（父任务）

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

### 5.1 BaseServiceTest基类
- @SpringBootTest(webEnvironment=RANDOM_PORT) + @Testcontainers + @TestInstance(PER_CLASS) + @Transactional
- PostgreSQLContainer "postgres:15" + @DynamicPropertySource
- 通用断言：assertSuccess/assertBusinessError

### 5.2 关键特性
- @Transactional每个测试自动回滚
- Testcontainers每次启动独立PostgreSQL容器

## 六、交付物清单



| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/test/java/com/erp/testsupport/BaseServiceTest.java | Service层测试基类 |
| 2 | src/test/resources/application-test.yml | 测试数据库配置 |

## 七、验收标准



| 序号 | 检查项 | 验证方法 |
|:---:|--------|--------|
| 1 | BaseServiceTest定义完整 | 代码Review + DevTools验证 |
| 2 | Testcontainers配置正确 | 测试验证容器启动 |
| 3 | 通用断言可用 | 调用验证 |
| 4 | 事务自动回滚 | 数据库无残留 |
| 5 | 覆盖率>=80% | JaCoCo报告 |

## 八、易错警示


> ⚠️ BaseServiceTest的@Transactional会回滚每个测试，异步操作或新线程中的操作事务不会自动回滚

> ⚠️ Testcontainers容器启动较慢(10-30秒)，使用@TestInstance(PER_CLASS)避免每个测试方法重启容器

> ⚠️ TestDataFactory生成的实体id必须使用AtomicLong序列，避免并行测试时id冲突

> ⚠️ 测试数据TEST_前缀必须严格执行，否则数据泄漏难以排查
