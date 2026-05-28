# P0-014-001-003 Service层测试模板（@ExtendWith(MockitoExtension)+Mock依赖+given/when/then模式+覆盖率目标≥80%）

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-014-001-003 |
| 任务名称 | Service层测试模板（@ExtendWith(MockitoExtension)+Mock依赖+given/when/then模式+覆盖率目标≥80%） |
| 所属模块 | P0-014 |
| 优先级 | P0 |
| 任务类型 | Service服务层 |

## 二、任务目标


开发Service层测试模板：@ExtendWith(MockitoExtension.class) + @Mock依赖注入 + @InjectMocks被测类 + given/when/then三段式结构 + 覆盖率目标行覆盖>=80%分支覆盖>=70%，包含正常/异常/边界场景

## 三、前置依赖

### 3.1 前置任务

- P0-014-001-002 单元测试基类开发（前序兄弟任务）

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

### 5.1 Service层测试模板
- @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks
- given/when/then三段式 + AssertJ流式断言 + Mockito verify

### 5.2 覆盖场景
- 正常路径 + 异常路径 + 边界条件

### 5.3 覆盖率目标
- 行覆盖率 >= 80%, 分支覆盖率 >= 70%

## 六、交付物清单



| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/test/java/com/erp/template/ServiceTestTemplate.java | Service层测试模板类 |
| 2 | src/test/java/com/erp/template/ServiceTestTemplateTest.java | 模板自验测试 |

## 七、验收标准



| 序号 | 检查项 | 验证方法 |
|:---:|--------|--------|
| 1 | @ExtendWith(MockitoExtension)正确 | 代码Review + DevTools验证 |
| 2 | @Mock + @InjectMocks正确 | 测试无NPE |
| 3 | given/when/then完整 | 代码Review + DevTools验证 |
| 4 | 行>=80%,分支>=70% | JaCoCo报告 |
| 5 | 正常+异常+边界全覆盖 | 用例检查 |

## 八、易错警示


> ⚠️ @ExtendWith(MockitoExtension.class)不能与@SpringBootTest同时使用，前者是纯Mock后者启动完整容器

> ⚠️ given/when/then三段式必须使用注释明确划分，禁止混写

> ⚠️ Mock设置(given)必须在调用被测方法之前，verify必须在调用之后

> ⚠️ 异常断言使用assertThatThrownBy而非try-catch，避免测试方法吞掉异常导致假通过

> ⚠️ 覆盖率目标为最低阈值，核心业务逻辑应追求100%分支覆盖
