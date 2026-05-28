# P2-007-001-007-001-001 定义Controller类+@RequestMapping路径+注入Service

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-007-001-007-001-001 |
| 任务名称 | 定义Controller类+@RequestMapping路径+注入Service |
| 所属模块 | P2-007 |
| 优先级 | P2 |
| 任务类型 | Service服务层 |

## 二、任务目标

定义AfterSaleController类：@RestController+@RequestMapping("/api/oa/{resource}")+注入Service+@Tag Knife4j分组

## 三、前置依赖

### 3.1 前置任务

- P2-007-001-007-001 接修单Entity/Mapper/Service/Controller开发（父任务）

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

> **📦 本任务模块上下文**（来源：P2-007模块开发指南）
> - 本模块涉及数据表：参见模块开发指南
> - 本模块涉及API：/api/after-sale/order, /api/after-sale/remote-service, /api/after-sale/onsite-service, /api/after-sale/return, /api/after-sale/exchange
> - 本模块业务规则：本模块无模块级专属约束，遵循全局规范。售后退货单审核后库存自动增加且退款金额联动应收；售后换货单审核后旧品入库与新品出库同步执行并处理差价；维修领料单关联维修单执行领料出库与库存扣减。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 Controller类

```java
@Slf4j
@RestController
@RequestMapping("/api/aftersale/aftersale")
@Tag(name = "售后管理-AfterSale管理")
public class AfterSaleController {

    @Autowired private AfterSaleService afterSaleService;

    @PostMapping("/create")
    @Operation(summary = "创建AfterSale")
    public Result<AfterSaleVO> create(@RequestBody @Valid AfterSaleCreateDTO dto) {
        return Result.success(afterSaleService.create(dto));
    }

    @PutMapping("/update")
    @Operation(summary = "修改AfterSale")
    public Result<Boolean> update(@RequestBody @Valid AfterSaleUpdateDTO dto) {
        return Result.success(afterSaleService.update(dto));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除AfterSale")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(afterSaleService.delete(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询AfterSale")
    public Result<Page<AfterSaleListVO>> page(AfterSaleQueryDTO query) {
        return Result.success(afterSaleService.getPage(query));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "AfterSale详情")
    public Result<AfterSaleVO> detail(@PathVariable Long id) {
        return Result.success(afterSaleService.getDetail(id));
    }
}
```

### 5.2 规范
- 路径/aftersale/{resource}全小写-分隔
- 统一Result<T>
- @Tag/@Operation完整
- @Valid触发校验
## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/aftersale/controller/AfterSaleController.java | AfterSaleController |


## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | @RequestMapping符合/api/oa/{resource} | 启动测试 |
| 2 | Service注入 | 启动测试 |
| 3 | @Tag Knife4j | doc.html |


## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE

> ⚠️ 路径符合/api/oa/{resource}规范

> ⚠️ OA资源: schedule/announcement/meeting/meeting-room/vehicle/leave/news
