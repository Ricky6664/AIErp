# P2-008-001-005-001-001 定义Controller类+@RequestMapping路径+注入Service

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-008-001-005-001-001 |
| 任务名称 | 定义Controller类+@RequestMapping路径+注入Service |
| 所属模块 | P2-008 |
| 优先级 | P2 |
| 任务类型 | Service服务层 |

## 二、任务目标

定义OaVehicleController类：@RestController+@RequestMapping("/api/oa/vehicle")+注入OaVehicleService+@Tag(name="OA办公-用车管理") Knife4j分组；实现标准CRUD五个端点(POST /create, PUT /update, DELETE /delete/{id}, GET /page, GET /detail/{id})以及用车资源专属端点POST /{id}/approve（审批）、POST /{id}/dispatch（派车）

## 三、前置依赖

### 3.1 前置任务

- P2-008-001-005-001 用车Entity/Mapper/Service/Controller开发（父任务）

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
> - 数据表：oa_vehicle（用车表）
> - 专属字段：purpose, applicant_id, car_id, start_time, end_time, destination, passenger_count, status(0待审/1已批/2已派/3已还)
> - 公共字段：id, created_by, created_at, updated_by, updated_at, is_deleted, tenant_id, version
> - API基础路径：/api/oa/vehicle
> - 技术栈：Spring Boot 3.4.x + MyBatis-Plus 3.5.5 + JDK 17 + PostgreSQL 15+ + Sa-Token 1.39 + Knife4j 4.x

### 5.1 Controller类

```java
@Slf4j
@RestController
@RequestMapping("/api/oa/vehicle")
@Tag(name = "OA办公-用车管理")
public class OaVehicleController {

    @Autowired private OaVehicleService oaVehicleService;

    @PostMapping("/create")
    @Operation(summary = "创建用车")
    public Result<OaVehicleVO> create(@RequestBody @Valid OaVehicleCreateDTO dto) {
        return Result.success(oaVehicleService.create(dto));
    }

    @PutMapping("/update")
    @Operation(summary = "修改用车")
    public Result<Boolean> update(@RequestBody @Valid OaVehicleUpdateDTO dto) {
        return Result.success(oaVehicleService.update(dto));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除用车")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(oaVehicleService.delete(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询用车")
    public Result<Page<OaVehicleListVO>> page(OaVehicleQueryDTO query) {
        return Result.success(oaVehicleService.getPage(query));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "用车详情")
    public Result<OaVehicleVO> detail(@PathVariable Long id) {
        return Result.success(oaVehicleService.getDetail(id));
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "审批用车申请", description = "审批用车申请，通过后状态变为approved")
    public Result<Boolean> approve(@PathVariable Long id) {
        return Result.success(oaVehicleService.approve(id));
    }

    @PostMapping("/{id}/dispatch")
    @Operation(summary = "派车", description = "为已审批的用车申请分配车辆，状态变为dispatched")
    public Result<Boolean> dispatch(@PathVariable Long id,
            @RequestParam @Parameter(description = "分配车辆ID") Long carId) {
        return Result.success(oaVehicleService.dispatch(id, carId));
    }
}
```

### 5.2 规范约束
- 路径 `/api/oa/vehicle` 全小写，多单词用 `-` 分隔
- 统一 `Result<T>` 包装返回
- `@Tag` / `@Operation` 注解完整
- `@Valid` 触发 DTO 参数校验
- 注入 Service 为 `OaVehicleService`，禁止使用泛型 OaService

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/oa/controller/OaVehicleController.java | 用车Controller类 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | @RequestMapping 值为 `/api/oa/vehicle` | 启动后访问 Knife4j 验证 |
| 2 | 注入 `OaVehicleService` 且启动无报错 | mvn spring-boot:run |
| 3 | @Tag(name="OA办公-用车管理") 在 doc.html 正确分组展示 | 访问 /doc.html |
| 4 | 标准 CRUD 5 个端点均已注册 | Knife4j 接口列表 |
| 5 | 用车专属端点已注册 | Knife4j 接口列表 |

## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE

> ⚠️ @RequestMapping 路径必须为 `/api/oa/vehicle`，禁止使用 `/api/oa/oa`

> ⚠️ 类名必须为 `OaVehicleController`，禁止使用泛型 `OaController`

> ⚠️ Service 注入必须为 `OaVehicleService`，禁止使用 `OaService`

> ⚠️ [用车] 审批流程集成 P1-002 审批模块

> ⚠️ [用车] dispatch 时必须校验车辆时段不冲突
