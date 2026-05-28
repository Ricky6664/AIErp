# P2-003-001-002-002-001 编写PeriodReverseCloseController类

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-003-001-002-002-001 |
| 任务名称 | 编写PeriodReverseCloseController类 |
| 所属模块 | P2-003 |
| 优先级 | P2 |
| 任务类型 | Controller接口层 |

## 二、任务目标

编写PeriodReverseCloseController类：@RestController+@RequestMapping("/api/period/periodreverseclose")；@Tag(name="期末处理-PeriodReverseClose管理")Knife4j分组；注入PeriodReverseCloseService；统一返回Result<T>

## 三、前置依赖

### 3.1 前置任务

- P2-003-001-002-002 反结账Controller开发（父任务）

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

> **📦 本任务模块上下文**（来源：P2-003模块开发指南）
> - 本模块涉及数据表：参见模块开发指南
> - 本模块涉及API：/api/fin/period/close, /api/fin/period/reverse, /api/fin/exchange, /api/fin/carryforward
> - 本模块业务规则：本模块无模块级专属约束，遵循全局规范。期末结转类型包含：损益结转、成本结转、增值税结转、所得税结转、利润分配结转五种。结账/反结账操作必须由财务主管权限执行，同一期间不可并发结账。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 Controller类

```java
@Slf4j
@RestController
@RequestMapping("/api/period/periodreverseclose")
@Tag(name = "期末处理-PeriodReverseClose管理")
public class PeriodReverseCloseController {

    @Autowired private PeriodReverseCloseService periodReverseCloseService;

    @PostMapping("/create")
    @Operation(summary = "创建PeriodReverseClose")
    public Result<PeriodReverseCloseVO> create(@RequestBody @Valid PeriodReverseCloseCreateDTO dto) {
        return Result.success(periodReverseCloseService.create(dto));
    }

    @PutMapping("/update")
    @Operation(summary = "修改PeriodReverseClose")
    public Result<Boolean> update(@RequestBody @Valid PeriodReverseCloseUpdateDTO dto) {
        return Result.success(periodReverseCloseService.update(dto));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除PeriodReverseClose")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(periodReverseCloseService.delete(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询PeriodReverseClose")
    public Result<Page<PeriodReverseCloseListVO>> page(PeriodReverseCloseQueryDTO query) {
        return Result.success(periodReverseCloseService.getPage(query));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "PeriodReverseClose详情")
    public Result<PeriodReverseCloseVO> detail(@PathVariable Long id) {
        return Result.success(periodReverseCloseService.getDetail(id));
    }
}
```

### 5.2 规范
- 路径/period/{resource}全小写-分隔
- 统一Result<T>
- @Tag/@Operation完整
- @Valid触发校验
## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/period/controller/PeriodReverseCloseController.java | PeriodReverseCloseController |


## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | @RequestMapping路径正确 | 启动测试 |
| 2 | @Tag Knife4j | doc.html |
| 3 | Service注入正确 | 启动测试 |
| 4 | 统一Result<T> | 接口调用 |


## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE

> ⚠️ 路径符合/api/{module}/{resource}规范复数形式

> ⚠️ @RequestBody配合@Valid触发校验

> ⚠️ Knife4j @Tag/@Operation/@Parameter完整
