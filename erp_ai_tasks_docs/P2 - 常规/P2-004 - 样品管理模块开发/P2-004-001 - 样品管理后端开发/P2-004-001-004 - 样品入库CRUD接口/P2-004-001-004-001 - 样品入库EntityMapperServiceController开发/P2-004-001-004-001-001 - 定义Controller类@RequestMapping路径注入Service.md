# P2-004-001-004-001-001 定义Controller类+@RequestMapping路径+注入Service

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-004-001-004-001-001 |
| 任务名称 | 定义Controller类+@RequestMapping路径+注入Service |
| 所属模块 | P2-004 |
| 优先级 | P2 |
| 任务类型 | Service服务层 |

## 二、任务目标

定义SampleController类：@RestController+@RequestMapping("/api/sample/{resource}")+注入Service+@Tag Knife4j分组

## 三、前置依赖

### 3.1 前置任务

- P2-004-001-004-001 样品入库Entity/Mapper/Service/Controller开发（父任务）

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

> **📦 本任务模块上下文**（来源：P2-004模块开发指南）
> - 本模块涉及数据表：参见模块开发指南
> - 本模块涉及API：/api/sample/application, /api/sample/shipment, /api/sample/purchase, /api/sample/inbound, /api/sample/return
> - 本模块业务规则：本模块无模块级专属约束，遵循全局规范。样品发货审核后自动扣减库存并关联出库单；样品归还审核后自动增加库存并执行出库冲回。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 Controller类

```java
@Slf4j
@RestController
@RequestMapping("/api/sample/sample")
@Tag(name = "样品管理-Sample管理")
public class SampleController {

    @Autowired private SampleService sampleService;

    @PostMapping("/create")
    @Operation(summary = "创建Sample")
    public Result<SampleVO> create(@RequestBody @Valid SampleCreateDTO dto) {
        return Result.success(sampleService.create(dto));
    }

    @PutMapping("/update")
    @Operation(summary = "修改Sample")
    public Result<Boolean> update(@RequestBody @Valid SampleUpdateDTO dto) {
        return Result.success(sampleService.update(dto));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除Sample")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(sampleService.delete(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询Sample")
    public Result<Page<SampleListVO>> page(SampleQueryDTO query) {
        return Result.success(sampleService.getPage(query));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "Sample详情")
    public Result<SampleVO> detail(@PathVariable Long id) {
        return Result.success(sampleService.getDetail(id));
    }
}
```

### 5.2 规范
- 路径/sample/{resource}全小写-分隔
- 统一Result<T>
- @Tag/@Operation完整
- @Valid触发校验
## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/sample/controller/SampleController.java | SampleController |


## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | @RequestMapping符合/api/sample/{resource} | 启动测试 |
| 2 | Service注入 | 启动测试 |
| 3 | @Tag Knife4j | doc.html |


## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE

> ⚠️ 路径符合/api/sample/{resource}规范

