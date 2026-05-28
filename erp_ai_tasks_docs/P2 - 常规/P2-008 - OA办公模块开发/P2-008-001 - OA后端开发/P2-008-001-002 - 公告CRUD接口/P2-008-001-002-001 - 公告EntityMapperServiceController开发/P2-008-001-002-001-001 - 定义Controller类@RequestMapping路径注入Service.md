# P2-008-001-002-001-001 定义Controller类+@RequestMapping路径+注入Service

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-008-001-002-001-001 |
| 任务名称 | 定义Controller类+@RequestMapping路径+注入Service |
| 所属模块 | P2-008 |
| 优先级 | P2 |
| 任务类型 | Service服务层 |

## 二、任务目标

定义OaAnnouncementController类：@RestController+@RequestMapping("/api/oa/announcement")+注入OaAnnouncementService+@Tag(name="OA办公-公告管理") Knife4j分组；实现标准CRUD五个端点(POST /create, PUT /update, DELETE /delete/{id}, GET /page, GET /detail/{id})以及公告资源专属端点POST /{id}/publish（发布）、POST /{id}/withdraw（撤回）

## 三、前置依赖

### 3.1 前置任务

- P2-008-001-002-001 公告Entity/Mapper/Service/Controller开发（父任务）

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
> - 数据表：oa_announcement（公告表）
> - 专属字段：title, content(TEXT), publish_date, expire_date, is_top(置顶标记), status(0草稿/1已发布/2已过期)
> - 公共字段：id, created_by, created_at, updated_by, updated_at, is_deleted, tenant_id, version
> - API基础路径：/api/oa/announcement
> - 技术栈：Spring Boot 3.4.x + MyBatis-Plus 3.5.5 + JDK 17 + PostgreSQL 15+ + Sa-Token 1.39 + Knife4j 4.x

### 5.1 Controller类

```java
@Slf4j
@RestController
@RequestMapping("/api/oa/announcement")
@Tag(name = "OA办公-公告管理")
public class OaAnnouncementController {

    @Autowired private OaAnnouncementService oaAnnouncementService;

    @PostMapping("/create")
    @Operation(summary = "创建公告")
    public Result<OaAnnouncementVO> create(@RequestBody @Valid OaAnnouncementCreateDTO dto) {
        return Result.success(oaAnnouncementService.create(dto));
    }

    @PutMapping("/update")
    @Operation(summary = "修改公告")
    public Result<Boolean> update(@RequestBody @Valid OaAnnouncementUpdateDTO dto) {
        return Result.success(oaAnnouncementService.update(dto));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除公告")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(oaAnnouncementService.delete(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询公告")
    public Result<Page<OaAnnouncementListVO>> page(OaAnnouncementQueryDTO query) {
        return Result.success(oaAnnouncementService.getPage(query));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "公告详情")
    public Result<OaAnnouncementVO> detail(@PathVariable Long id) {
        return Result.success(oaAnnouncementService.getDetail(id));
    }

    @PostMapping("/{id}/publish")
    @Operation(summary = "发布公告", description = "将草稿公告发布，设置发布日期并更新状态")
    public Result<Boolean> publish(@PathVariable Long id) {
        return Result.success(oaAnnouncementService.publish(id));
    }

    @PostMapping("/{id}/withdraw")
    @Operation(summary = "撤回公告", description = "将已发布公告撤回为草稿状态")
    public Result<Boolean> withdraw(@PathVariable Long id) {
        return Result.success(oaAnnouncementService.withdraw(id));
    }
}
```

### 5.2 规范约束
- 路径 `/api/oa/announcement` 全小写，多单词用 `-` 分隔
- 统一 `Result<T>` 包装返回
- `@Tag` / `@Operation` 注解完整
- `@Valid` 触发 DTO 参数校验
- 注入 Service 为 `OaAnnouncementService`，禁止使用泛型 OaService

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/oa/controller/OaAnnouncementController.java | 公告Controller类 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | @RequestMapping 值为 `/api/oa/announcement` | 启动后访问 Knife4j 验证 |
| 2 | 注入 `OaAnnouncementService` 且启动无报错 | mvn spring-boot:run |
| 3 | @Tag(name="OA办公-公告管理") 在 doc.html 正确分组展示 | 访问 /doc.html |
| 4 | 标准 CRUD 5 个端点均已注册 | Knife4j 接口列表 |
| 5 | 公告专属端点已注册 | Knife4j 接口列表 |

## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE

> ⚠️ @RequestMapping 路径必须为 `/api/oa/announcement`，禁止使用 `/api/oa/oa`

> ⚠️ 类名必须为 `OaAnnouncementController`，禁止使用泛型 `OaController`

> ⚠️ Service 注入必须为 `OaAnnouncementService`，禁止使用 `OaService`

> ⚠️ [公告] 状态流转 draft->published->expired 不可跳跃

> ⚠️ [公告] is_top 置顶公告需配合前端排序展示
