# ERP AI 测试命名规范

> 版本：V1.0
> 创建日期：2026-06-06
> 适用范围：ERP AI 项目全部测试代码

---

## 1. 总则

本规范定义项目中所有测试代码的命名规则、文件结构和代码组织方式。严格的命名规范确保：
- 测试代码可读性一致，任何开发者都能快速理解测试意图
- 测试报告易于检索和分类
- CI 工具可基于命名规则自动筛选和分组测试

---

## 2. 后端测试命名规范

### 2.1 测试类命名

| 规则 | 说明 |
|------|------|
| 格式 | `<被测类名>Test` |
| 位置 | `src/test/java/com/erp/` 下与被测类相同的包路径 |
| 可见性 | 默认包级私有（package-private），不添加 `public` 修饰符 |

**正确示例：**

```java
// 被测类: com.erp.module.org.service.OrgCompanyService
// 测试类: com.erp.module.org.service.OrgCompanyServiceTest
class OrgCompanyServiceTest {
    // ...
}

// 被测类: com.erp.module.product.controller.ProductController
// 测试类: com.erp.module.product.controller.ProductControllerTest
class ProductControllerTest {
    // ...
}
```

**错误示例：**

```java
// ❌ 使用 Test 前缀而非后缀
class TestOrgCompanyService { }

// ❌ 使用 Tests 复数形式
class OrgCompanyServiceTests { }

// ❌ 省略被测类名
class OrgServiceTest { }
```

### 2.2 测试方法命名

| 规则 | 说明 |
|------|------|
| 格式 | `should_<预期行为>_when_<条件>` |
| 分隔符 | 使用下划线 `_` 分隔语义段 |
| 语言 | 英文驼峰式的语义描述，不包含拼音或中文 |
| 注解 | 必须添加 `@Test` 和 `@DisplayName` |

**正确示例：**

```java
@Test
@DisplayName("应在有效ID时返回用户")
void should_returnUser_when_validIdGiven() {
    // ...
}

@Test
@DisplayName("应在重复名称时抛出业务异常")
void should_throwBusinessException_when_duplicateName() {
    // ...
}

@Test
@DisplayName("应在无匹配数据时返回空列表")
void should_returnEmptyList_when_noDataMatches() {
    // ...
}

@Test
@DisplayName("应在所有字段有效时成功保存")
void should_saveSuccessfully_when_allFieldsValid() {
    // ...
}
```

**错误示例：**

```java
// ❌ 不规范的命名
void testSave() { }
void testFindById() { }
void saveTest() { }

// ❌ 过于简单，不表达行为
void shouldWork() { }
void shouldBeOk() { }
```

### 2.3 @DisplayName 规范

每个测试方法必须添加 `@DisplayName` 注解，使用中文简洁描述测试意图：

```java
@Test
@DisplayName("应在有效ID时返回用户")
void should_returnUser_when_validIdGiven() { }

@Test
@DisplayName("应在重复名称时抛出业务异常")
void should_throwBusinessException_when_duplicateName() { }
```

---

## 3. 前端测试命名规范

### 3.1 测试文件命名

| 规则 | 说明 |
|------|------|
| 格式 | `<ComponentName>.spec.ts` |
| 位置 | `src/__tests__/` 下按模块组织 |
| 扩展名 | `.spec.ts`（非 `.test.ts`） |

**正确示例：**

```
src/__tests__/
├── views/org/
│   ├── OrgWorkbench.spec.ts
│   ├── OrgCompanyList.spec.ts
│   └── OrgDepartmentTree.spec.ts
├── components/
│   ├── KpiCard.spec.ts
│   └── DataTable.spec.ts
└── composables/
    └── useOrg.spec.ts
```

**错误示例：**

```
// ❌ 使用 .test.ts
OrgWorkbench.test.ts

// ❌ 使用 Spec 后缀
OrgWorkbenchSpec.ts

// ❌ 文件名与组件名不一致
workbench.spec.ts
```

### 3.2 describe 块命名

| 规则 | 说明 |
|------|------|
| 格式 | 被测组件/函数名 |
| 语言 | 英文 |

```typescript
describe('OrgWorkbench', () => {
    // 组件测试
});

describe('useOrg', () => {
    // composable 测试
});
```

### 3.3 it 用例命名

| 规则 | 说明 |
|------|------|
| 格式 | `should <预期行为> when <条件>` |
| 语言 | 英文 |

**正确示例：**

```typescript
it('should render KPI cards when data is loaded', async () => {
    // ...
});

it('should show error message when API call fails', async () => {
    // ...
});

it('should emit submit event when form is valid', async () => {
    // ...
});

it('should disable button when user lacks permission', () => {
    // ...
});
```

**错误示例：**

```typescript
// ❌ 不规范的命名
it('test render', () => { });
it('renders correctly', () => { });
it('works', () => { });
```

---

## 4. Given/When/Then 三段式结构

所有测试必须使用 Given/When/Then 三段式，以注释 `// Given`、`// When`、`// Then` 明确划分。

### 4.1 后端三段式

```java
@Test
@DisplayName("应在有效ID时返回用户")
void should_returnUser_when_validIdGiven() {
    // Given — 准备测试数据与前置条件
    Long userId = 1L;
    User mockUser = new User();
    mockUser.setId(userId);
    mockUser.setName("张三");
    when(userMapper.selectById(userId)).thenReturn(mockUser);

    // When — 执行被测方法
    UserVO result = userService.getById(userId);

    // Then — 验证结果
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("张三");
    verify(userMapper).selectById(userId);
}
```

### 4.2 前端三段式

```typescript
it('should render KPI cards when data is loaded', async () => {
    // Given — 准备 Mock 数据与组件挂载
    const mockData = {
        companyCount: 3,
        departmentCount: 24,
    };
    server.use(
        http.get('/api/org/workbench', () =>
            HttpResponse.json({ code: 200, data: mockData })
        )
    );

    // When — 挂载组件并等待异步渲染
    const wrapper = mount(OrgWorkbench, {
        global: { plugins: [pinia, router] }
    });
    await flushPromises();

    // Then — 验证渲染结果
    expect(wrapper.findAll('.kpi-card')).toHaveLength(4);
    expect(wrapper.text()).toContain('3');
    expect(wrapper.text()).toContain('24');
});
```

### 4.3 三段式规则

| 阶段 | 职责 | 可包含 |
|------|------|--------|
| Given | 准备前置条件和测试数据 | 创建对象、Mock 设置、数据库初始化 |
| When | 执行被测行为 | 只调用一次被测方法 |
| Then | 验证结果 | assertThat/expect/verify 断言 |

**规则：**
- 每个阶段必须使用独立注释行分隔
- Given 可以有多行，When 通常只有一行（被测方法调用），Then 可以有多个断言
- 不要将 Given 的准备工作混入 When 阶段
- 不要在 Then 阶段执行额外的方法调用（除了 getter）

---

## 5. 测试数据命名

- 常量名使用 `TEST_` 前缀，全大写加下划线
- 测试方法中的局部变量使用描述性名称，包含 `mock`、`test`、`expected` 等前缀

```java
// 常量
private static final String TEST_COMPANY_NAME = "TEST_测试科技有限公司";
private static final String TEST_DEPT_CODE = "TEST_DEPT_001";
private static final Long TEST_INVALID_ID = -1L;

// 局部变量
User mockUser = createMockUser();
List<UserVO> expectedList = List.of(userVO1, userVO2);
String testName = "TEST_新部门";
```

---

## 6. 覆盖率报告中的测试分类

CI 覆盖率报告按以下方式展示测试统计：

```
模块                         测试数   通过   失败   覆盖率
erp-module-org               45      45     0      85%
  ├─ OrgCompanyServiceTest   12      12     0      88%
  ├─ OrgDeptServiceTest      8       8      0      82%
  ├─ OrgCompanyControllerTest 6       6      0      75%
  └─ ...
```

---

## 7. 检查清单

在创建或修改测试时，确认以下项目：

- [ ] 测试类名以被测类名 + `Test` 结尾
- [ ] 测试方法名遵循 `should_xxx_when_yyy` 格式
- [ ] 每个测试方法有 `@DisplayName` 中文描述
- [ ] 使用 Given/When/Then 三段式注释分隔
- [ ] 每个测试方法只测一个行为
- [ ] 测试数据使用 `TEST_` 前缀
- [ ] 不依赖测试执行顺序（每个测试独立）
- [ ] Mock 设置完整，不产生不必要的外部调用
- [ ] 断言明确，不依赖隐式行为

---

> 本文档由测试基础模块（P0-014）制定，所有模块的测试代码必须遵循。Code Review 时将检查命名规范遵从性。
