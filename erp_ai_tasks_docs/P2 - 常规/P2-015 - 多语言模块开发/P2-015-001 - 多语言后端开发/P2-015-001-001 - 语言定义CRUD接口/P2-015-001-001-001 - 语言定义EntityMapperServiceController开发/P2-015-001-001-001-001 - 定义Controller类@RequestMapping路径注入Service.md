# P2-015-001-001-001-001 定义Controller类+@RequestMapping路径+注入Service

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-015-001-001-001-001 |
| 任务名称 | 定义Controller类+@RequestMapping路径+注入Service |
| 所属模块 | P2-015 |
| 优先级 | P2 |
| 任务类型 | Service服务层 |

## 二、任务目标

定义I18nController类：@RestController+@RequestMapping("/api/i18n/language")类级路径，@Tag(name="多语言管理") Knife4j分组注解，@Autowired注入I18nService，类级@SaCheckPermission权限前缀

## 三、前置依赖

### 3.1 前置任务

- P2-015-001-001-001 语言定义Entity/Mapper/Service/Controller开发（父任务）

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

> **📦 本任务模块上下文**（来源：P2-015模块开发指南）
> - 本模块涉及数据表：参见模块开发指南
> - 本模块涉及API：/api/i18n/language, /api/i18n/translation, /api/i18n/export, /api/i18n/import, /api/i18n/menu
> - 本模块业务规则：本模块无模块级专属约束，遵循全局国际化开发规范。菜单/表格列/字典多语言数据按语言编码缓存至Redis（TTL 24h），翻译变更时自动清除缓存；前端菜单与字典加载时根据请求头Accept-Language返回对应语言翻译；缺失翻译时自动回退默认语言；翻译导入支持增量更新与覆盖率统计；表格多语言配置复用i18n_translation表（category=table）。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 Controller类定义

```java
@RestController
@RequestMapping("/api/i18n/language")
@Tag(name = "多语言管理")
@SaCheckPermission("i18n")
public class I18nController {

    @Autowired
    private I18nService i18nService;
}
```

### 5.2 类级注解

- @RestController：声明为REST控制器
- @RequestMapping("/api/i18n/language")：类级路径，符合 /api/{module}/{resource} 规范
- @Tag(name="多语言管理")：Knife4j分组
- @SaCheckPermission：Sa-Token权限前缀

### 5.3 验证

1. @RequestMapping路径符合RESTful规范（复数形式）
2. @Tag/@SaCheckPermission注解完整
3. Service注入正确

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/i18n/controller/I18nController.java | Controller类 |

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

> ⚠️ [多语言模块] i18n key命名规范：{module}.{page}.{component}.{field}，例如sys.user.list.title

> ⚠️ [多语言模块] 翻译加载使用懒加载策略，首次请求某语言翻译时才加载并缓存

> ⚠️ [多语言模块] 翻译Key全局唯一性校验，新增时检查是否已存在相同key+language_code的记录
