# P2-002-001-002-004-001 编写DepreciationController类

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-002-001-002-004-001 |
| 任务名称 | 编写DepreciationController类 |
| 所属模块 | P2-002 |
| 优先级 | P2 |
| 任务类型 | Controller接口层 |

## 二、任务目标

编写DepreciationController类：@RestController+@RequestMapping("/api/asset/depreciation")；@Tag(name="固定资产-Depreciation管理")Knife4j分组；注入DepreciationService；统一返回Result<T>

## 三、前置依赖

### 3.1 前置任务

- P2-002-001-002-004 折旧计算Controller开发（父任务）

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

> **📦 本任务模块上下文**（来源：P2-002模块开发指南）
> - 本模块涉及数据表：参见模块开发指南
> - 本模块涉及API：/api/fa/asset, /api/fa/depreciation, /api/fa/change, /api/fa/disposal
> - 本模块业务规则：本模块无模块级专属约束，遵循全局规范。折旧方法支持：平均年限法、双倍余额递减法、年数总和法、工作量法四种。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 Controller类

```java
@Slf4j
@RestController
@RequestMapping("/api/asset/depreciation")
@Tag(name = "固定资产-Depreciation管理")
public class DepreciationController {

    @Autowired private DepreciationService depreciationService;

    @PostMapping("/create")
    @Operation(summary = "创建Depreciation")
    public Result<DepreciationVO> create(@RequestBody @Valid DepreciationCreateDTO dto) {
        return Result.success(depreciationService.create(dto));
    }

    @PutMapping("/update")
    @Operation(summary = "修改Depreciation")
    public Result<Boolean> update(@RequestBody @Valid DepreciationUpdateDTO dto) {
        return Result.success(depreciationService.update(dto));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除Depreciation")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(depreciationService.delete(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询Depreciation")
    public Result<Page<DepreciationListVO>> page(DepreciationQueryDTO query) {
        return Result.success(depreciationService.getPage(query));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "Depreciation详情")
    public Result<DepreciationVO> detail(@PathVariable Long id) {
        return Result.success(depreciationService.getDetail(id));
    }
}
```

### 5.2 规范
- 路径/asset/{resource}全小写-分隔
- 统一Result<T>
- @Tag/@Operation完整
- @Valid触发校验
## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/asset/controller/DepreciationController.java | DepreciationController |


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
