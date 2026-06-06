# ERP AI 测试规范指南

> 版本：V1.0
> 创建日期：2026-06-06
> 适用范围：ERP AI 项目全部模块

---

## 1. 测试框架与版本

| 层级 | 框架 | 版本 |
|------|------|------|
| 后端测试引擎 | JUnit 5 | 5.10.x |
| 后端 Mock | Mockito | 5.x |
| 后端断言 | AssertJ | 3.x |
| 后端集成测试 | Testcontainers | 1.19.x |
| 后端内存数据库 | H2 | 2.x |
| 后端覆盖率 | JaCoCo | 0.8.x |
| 前端测试引擎 | Vitest | 1.x |
| 前端组件测试 | @vue/test-utils | 2.x |
| 前端 DOM 环境 | happy-dom | latest |
| 前端覆盖率 | @vitest/coverage-v8 | latest |
| 前端 API Mock | MSW | 2.x |

---

## 2. 测试命名规范

### 2.1 后端测试类命名

```
格式：<被测类名>Test
示例：
  UserService → UserServiceTest
  OrgCompanyController → OrgCompanyControllerTest
  ProductMapper → ProductMapperTest
```

### 2.2 后端测试方法命名

```
格式：should_<预期行为>_when_<条件>
示例：
  should_returnUser_when_validIdGiven
  should_throwException_when_duplicateName
  should_returnEmptyList_when_noDataMatches
  should_saveEntity_when_allFieldsValid
```

### 2.3 前端测试文件命名

```
格式：<ComponentName>.spec.ts
示例：
  OrgWorkbench.spec.ts
  UserList.spec.ts
  LoginForm.spec.ts
```

### 2.4 前端测试用例命名

```
格式：should <预期行为> when <条件>
示例：
  should render KPI cards when data is loaded
  should show error message when API call fails
  should emit submit event when form is valid
```

---

## 3. 测试结构：Given/When/Then

所有测试方法必须使用 Given/When/Then 三段式结构，以注释明确划分。

### 3.1 后端示例

```java
@Test
void should_returnActiveUsers_when_statusFilterApplied() {
    // Given — 准备测试数据与前置条件
    User activeUser = User.builder().status("active").name("张三").build();
    User inactiveUser = User.builder().status("inactive").name("李四").build();
    when(userMapper.selectList(any(LambdaQueryWrapper.class)))
        .thenReturn(List.of(activeUser));

    // When — 执行被测方法
    List<UserVO> result = userService.listByStatus("active");

    // Then — 验证结果
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getName()).isEqualTo("张三");
    verify(userMapper).selectList(any(LambdaQueryWrapper.class));
}
```

### 3.2 前端示例

```typescript
it('should render KPI cards when data is loaded', async () => {
    // Given — 准备 Mock 数据与组件挂载
    const mockData = { companyCount: 3, departmentCount: 24 };
    server.use(
        http.get('/api/org/workbench', () =>
            HttpResponse.json({ code: 200, data: mockData })
        )
    );

    // When — 挂载组件并等待渲染
    const wrapper = mount(OrgWorkbench);
    await flushPromises();

    // Then — 验证渲染结果
    expect(wrapper.text()).toContain('3');
    expect(wrapper.text()).toContain('24');
});
```

---

## 4. 测试文件目录结构

### 4.1 后端

```
src/test/java/com/erp/
├── module/
│   ├── org/
│   │   ├── service/
│   │   │   └── OrgCompanyServiceTest.java
│   │   ├── controller/
│   │   │   └── OrgCompanyControllerTest.java
│   │   └── mapper/
│   │       └── OrgCompanyMapperTest.java
│   ├── product/
│   │   └── ...
│   └── ...
├── common/
│   └── base/
│       └── BaseServiceTest.java
└── factory/
    └── TestDataFactory.java
```

### 4.2 前端

```
src/__tests__/
├── views/
│   └── org/
│       └── OrgWorkbench.spec.ts
├── components/
│   └── KpiCard.spec.ts
├── composables/
│   └── useOrg.spec.ts
└── api/
    └── org.spec.ts
```

---

## 5. 测试数据规范

- 测试数据统一使用 `TEST_` 前缀标记，便于识别
- 使用 Builder 模式或工厂方法构造测试对象
- 不依赖生产数据库数据，所有数据在测试中自给自足

```java
// 测试数据工厂示例
public class TestDataFactory {
    public static final String TEST_COMPANY_NAME = "TEST_测试公司";
    public static final String TEST_DEPT_NAME = "TEST_测试部门";

    public static OrgCompany createTestCompany() {
        OrgCompany company = new OrgCompany();
        company.setName(TEST_COMPANY_NAME);
        company.setCode("TEST_001");
        return company;
    }
}
```

---

## 6. 覆盖率门禁

| 层级 | 最低覆盖率 | 测量维度 |
|------|:--------:|---------|
| Service 层 | >= 80% | 行覆盖 + 分支覆盖 |
| Controller 层 | >= 70% | 行覆盖 |
| Mapper 层 | >= 60% | 行覆盖 |
| 前端组件 | >= 70% | 语句覆盖 + 分支覆盖 |

CI 构建时执行覆盖率检查，低于门禁值则构建失败。

---

## 7. Mock 策略

### 7.1 后端

- Service 层测试：Mock Mapper 层，使用 `@ExtendWith(MockitoExtension.class)`
- Controller 层测试：Mock Service 层，使用 `@WebMvcTest` + `@MockBean`
- Mapper 层测试：使用 H2 内存数据库 + `@MybatisPlusTest`
- 集成测试：使用 Testcontainers 拉起真实 PostgreSQL

### 7.2 前端

- 组件测试：使用 `@vue/test-utils` mount，Mock 子组件用 `stubs`
- API 调用：使用 MSW (Mock Service Worker) 拦截网络请求
- Composable：使用 `renderHook` 或直接调用并断言返回值
- Store：使用 `setActivePinia(createPinia())` 创建隔离实例

---

## 8. 测试运行命令

```bash
# 后端 — 运行全部测试
mvn test

# 后端 — 运行指定模块测试
mvn test -pl erp-module-org

# 后端 — 生成覆盖率报告
mvn test jacoco:report

# 前端 — 运行全部测试
pnpm test

# 前端 — 监视模式
pnpm test:watch

# 前端 — 生成覆盖率报告
pnpm test:coverage
```

---

> 本文档为测试规范指南，所有开发人员必须遵守。CI 门禁将强制执行覆盖率要求。
