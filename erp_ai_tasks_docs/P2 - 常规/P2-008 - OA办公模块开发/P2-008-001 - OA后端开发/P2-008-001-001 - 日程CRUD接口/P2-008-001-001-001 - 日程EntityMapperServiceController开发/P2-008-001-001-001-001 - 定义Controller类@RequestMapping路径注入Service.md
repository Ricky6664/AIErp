# P2-008-001-001-001-001 定义Controller类+@RequestMapping路径+注入Service

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-008-001-001-001-001 |
| 任务名称 | 定义Controller类+@RequestMapping路径+注入Service |
| 所属模块 | P2-008 |
| 优先级 | P2 |
| 任务类型 | Service服务层 |

## 二、任务目标

定义OaScheduleController类：@RestController+@RequestMapping("/api/oa/schedule")+注入OaScheduleService+@Tag(name="OA办公-日程管理") Knife4j分组；实现标准CRUD五个端点(POST /create, PUT /update, DELETE /delete/{id}, GET /page, GET /detail/{id})以及日程资源专属端点GET /calendar（月视图按日期分组）

## 三、前置依赖

### 3.1 前置任务

- P2-008-001-001-001 日程Entity/Mapper/Service/Controller开发（父任务）

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
> - 数据表：oa_schedule（日程表）
> - 专属字段：title, schedule_date, start_time, end_time, location, remind_flag(0不提醒/1提前5分钟/2提前15分钟/3提前30分钟/4提前1小时), remind_minutes, color(日历分类色)
> - 公共字段：id, created_by, created_at, updated_by, updated_at, is_deleted, tenant_id, version
> - API基础路径：/api/oa/schedule
> - 技术栈：Spring Boot 3.4.x + MyBatis-Plus 3.5.5 + JDK 17 + PostgreSQL 15+ + Sa-Token 1.39 + Knife4j 4.x

### 5.1 Controller类

```java
@Slf4j
@RestController
@RequestMapping("/api/oa/schedule")
@Tag(name = "OA办公-日程管理")
public class OaScheduleController {

    @Autowired private OaScheduleService oaScheduleService;

    @PostMapping("/create")
    @Operation(summary = "创建日程")
    public Result<OaScheduleVO> create(@RequestBody @Valid OaScheduleCreateDTO dto) {
        return Result.success(oaScheduleService.create(dto));
    }

    @PutMapping("/update")
    @Operation(summary = "修改日程")
    public Result<Boolean> update(@RequestBody @Valid OaScheduleUpdateDTO dto) {
        return Result.success(oaScheduleService.update(dto));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除日程")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(oaScheduleService.delete(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询日程")
    public Result<Page<OaScheduleListVO>> page(OaScheduleQueryDTO query) {
        return Result.success(oaScheduleService.getPage(query));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "日程详情")
    public Result<OaScheduleVO> detail(@PathVariable Long id) {
        return Result.success(oaScheduleService.getDetail(id));
    }

    @GetMapping("/calendar")
    @Operation(summary = "日程月视图", description = "按年月查询日程日历视图")
    public Result<Map<String, List<OaScheduleVO>>> calendar(
            @RequestParam @Parameter(description = "年份") Integer year,
            @RequestParam @Parameter(description = "月份") Integer month) {
        return Result.success(oaScheduleService.getCalendarView(year, month));
    }
}
```

### 5.2 规范约束
- 路径 `/api/oa/schedule` 全小写，多单词用 `-` 分隔
- 统一 `Result<T>` 包装返回
- `@Tag` / `@Operation` 注解完整
- `@Valid` 触发 DTO 参数校验
- 注入 Service 为 `OaScheduleService`，禁止使用泛型 OaService

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/oa/controller/OaScheduleController.java | 日程Controller类 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | @RequestMapping 值为 `/api/oa/schedule` | 启动后访问 Knife4j 验证 |
| 2 | 注入 `OaScheduleService` 且启动无报错 | mvn spring-boot:run |
| 3 | @Tag(name="OA办公-日程管理") 在 doc.html 正确分组展示 | 访问 /doc.html |
| 4 | 标准 CRUD 5 个端点均已注册 | Knife4j 接口列表 |
| 5 | 日程专属端点已注册 | Knife4j 接口列表 |

## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE

> ⚠️ @RequestMapping 路径必须为 `/api/oa/schedule`，禁止使用 `/api/oa/oa`

> ⚠️ 类名必须为 `OaScheduleController`，禁止使用泛型 `OaController`

> ⚠️ Service 注入必须为 `OaScheduleService`，禁止使用 `OaService`

> ⚠️ [日程] remind_flag 枚举 0~4 需在文档中注明含义

> ⚠️ [日程] calendar 接口参数 year/month 需校验范围
