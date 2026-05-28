# P0-014-001-004 Controller层测试模板

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-014-001-004 |
| 任务名称 | Controller层测试模板 |
| 所属模块 | P0-014 |
| 优先级 | P0 |
| 任务类型 | Controller接口层 |

## 二、任务目标


开发Controller层测试模板：@WebMvcTest + MockMvc + @MockBean Service + JSON断言(JsonPath) + 异常场景覆盖(400/401/403/404/500) + 覆盖率目标>=70%，覆盖CRUD全接口

## 三、前置依赖

### 3.1 前置任务

- P0-014-001-003 Service层测试模板（@ExtendWith(MockitoExtension)+Mock依赖+given/when/then模式+覆盖率目标≥80%）（前序兄弟任务）

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

### 5.1 Controller层测试模板
- @WebMvcTest + MockMvc + @MockBean + JSON断言(jsonPath)

### 5.2 覆盖场景
- CRUD + 参数校验失败(400) + 未认证(401) + 无权限(403) + 资源不存在(404) + 业务异常(500)

### 5.3 覆盖率目标：行覆盖率 >= 70%

## 六、交付物清单



| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/test/java/com/erp/template/ControllerTestTemplate.java | Controller层测试模板类 |

## 七、验收标准



| 序号 | 检查项 | 验证方法 |
|:---:|--------|--------|
| 1 | @WebMvcTest + MockMvc正确 | 测试成功 |
| 2 | jsonPath断言正确 | 验证 |
| 3 | 异常场景覆盖 | 代码Review + DevTools验证 |
| 4 | 行>=70% | JaCoCo报告 |

## 八、易错警示


> ⚠️ @ExtendWith(MockitoExtension.class)不能与@SpringBootTest同时使用，前者是纯Mock后者启动完整容器

> ⚠️ given/when/then三段式必须使用注释明确划分，禁止混写

> ⚠️ Mock设置(given)必须在调用被测方法之前，verify必须在调用之后

> ⚠️ 异常断言使用assertThatThrownBy而非try-catch，避免测试方法吞掉异常导致假通过

> ⚠️ 覆盖率目标为最低阈值，核心业务逻辑应追求100%分支覆盖
