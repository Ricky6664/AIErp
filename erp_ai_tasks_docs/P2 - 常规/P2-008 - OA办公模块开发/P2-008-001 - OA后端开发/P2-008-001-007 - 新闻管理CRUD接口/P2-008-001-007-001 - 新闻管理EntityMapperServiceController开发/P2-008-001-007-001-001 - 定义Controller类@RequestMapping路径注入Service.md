# P2-008-001-007-001-001 定义Controller类+@RequestMapping路径+注入Service

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-008-001-007-001-001 |
| 任务名称 | 定义Controller类+@RequestMapping路径+注入Service |
| 所属模块 | P2-008 |
| 优先级 | P2 |
| 任务类型 | Service服务层 |

## 二、任务目标

定义OaNewsController类：@RestController+@RequestMapping("/api/oa/news")+注入OaNewsService+@Tag(name="OA办公-新闻管理") Knife4j分组；实现标准CRUD五个端点(POST /create, PUT /update, DELETE /delete/{id}, GET /page, GET /detail/{id})以及新闻资源专属端点POST /{id}/publish（发布）、POST /{id}/offline（下线）

## 三、前置依赖

### 3.1 前置任务

- P2-008-001-007-001 新闻管理Entity/Mapper/Service/Controller开发（父任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用

| 规范文档名 | 引用原因 |
|-----------|---------|
| 全局规范-项目架构与开发约束 | 项目架构、技术约束与任务依赖关系 |
| 全局规范-末端任务文档编写规范 | 末端任务文档格式与内容规范约束 |
| 全局规范-AI开发执行手册 | AI辅助开发流程与执行标准 |
| 全局规范-后端代码规范 | 后端代码开发规范约束 |
| 全局规范-API接口规范 | API接口设计与RESTful规范约束 |

## 五、详细开发规格

> **本任务模块上下文**
> - 数据表：oa_news（新闻表）
> - 专属字段：title, category(company/industry/notice), content(富文本HTML), cover_image(MinIO URL), is_top, publish_date, status(0草稿/1已发布/2已下线)
> - 公共字段：id, created_by, created_at, updated_by, updated_at, is_deleted, tenant_id, version
> - API基础路径：/api/oa/news
> - 技术栈：Spring Boot 3.4.x + MyBatis-Plus 3.5.5 + JDK 17 + PostgreSQL 15+ + Sa-Token 1.39 + Knife4j 4.x

### 5.1 Controller类

```java
@Slf4j
@RestController
@RequestMapping("/api/oa/news")
@Tag(name = "OA办公-新闻管理")
public class OaNewsController {

    @Autowired private OaNewsService oaNewsService;

    @PostMapping("/create")
    @Operation(summary = "创建新闻")
    public Result<OaNewsVO> create(@RequestBody @Valid OaNewsCreateDTO dto) {
        return Result.success(oaNewsService.create(dto));
    }

    @PutMapping("/update")
    @Operation(summary = "修改新闻")
    public Result<Boolean> update(@RequestBody @Valid OaNewsUpdateDTO dto) {
        return Result.success(oaNewsService.update(dto));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除新闻")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(oaNewsService.delete(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询新闻")
    public Result<Page<OaNewsListVO>> page(OaNewsQueryDTO query) {
        return Result.success(oaNewsService.getPage(query));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "新闻详情")
    public Result<OaNewsVO> detail(@PathVariable Long id) {
        return Result.success(oaNewsService.getDetail(id));
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "发布新闻", description = "将草稿新闻发布上线")
    public Result<Boolean> publish(@PathVariable Long id) {
        return Result.success(oaNewsService.publish(id));
    }

    @PostMapping("/{id}/offline")
    @Operation(summary = "下线新闻", description = "将已发布新闻下线")
    public Result<Boolean> offline(@PathVariable Long id) {
        return Result.success(oaNewsService.offline(id));
    }
}
```

### 5.2 规范约束
- 路径 `/api/oa/news` 全小写，多单词用 `-` 分隔
- 统一 `Result<T>` 包装返回
- `@Tag` / `@Operation` 注解完整
- `@Valid` 触发 DTO 参数校验
- 注入 Service 为 `OaNewsService`，禁止使用泛型 OaService

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/oa/controller/OaNewsController.java | 新闻Controller类 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | @RequestMapping 值为 `/api/oa/news` | 启动后访问 Knife4j 验证 |
| 2 | 注入 `OaNewsService` 且启动无报错 | mvn spring-boot:run |
| 3 | @Tag(name="OA办公-新闻管理") 在 doc.html 正确分组展示 | 访问 /doc.html |
| 4 | 标准 CRUD 5 个端点均已注册 | Knife4j 接口列表 |
| 5 | 新闻专属端点已注册 | Knife4j 接口列表 |

## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE

> ⚠️ @RequestMapping 路径必须为 `/api/oa/news`，禁止使用 `/api/oa/oa`

> ⚠️ 类名必须为 `OaNewsController`，禁止使用泛型 `OaController`

> ⚠️ Service 注入必须为 `OaNewsService`，禁止使用 `OaService`

> ⚠️ [新闻] content 富文本必须 XSS 过滤（Jsoup.clean）

> ⚠️ [新闻] cover_image URL 来自 MinIO 上传，不可直接接收文件
