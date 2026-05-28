# P1-007-002-012-002-001 定义Controller类+@RequestMapping路径+注入Service

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-007-002-012-002-001 |
| 任务名称 | 定义Controller类+@RequestMapping路径+注入Service |
| 所属模块 | P1-007 |
| 优先级 | P1 |
| 任务类型 | Service服务层 |

## 二、任务目标

定义WmsInventoryController类，@RestController+@RequestMapping("/api/inventory/stock")，注入IWmsInventoryService，声明GET/POST/PUT/DELETE端点方法


## 三、前置依赖

### 3.1 前置任务

- P1-007-002-012-002 序列号跟踪Service+Controller开发（父任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用


| 规范文档名 | 引用原因 |
|-----------|---------|
| 全局规范-项目架构与开发约束 | 项目架构、技术约束与任务依赖关系 |
| 全局规范-末端任务文档编写规范 | 末端任务文档结构与内容规范要求 |
| 全局规范-AI开发执行手册 | AI开发执行流程与质量规范 |
| 全局规范-后端代码规范 | 后端代码开发规范约束 |

## 五、详细开发规格


> **📦 本任务模块上下文**（来源：P1-007模块开发指南）
> - 本模块涉及数据表：inv_warehouse, inv_location, inv_other_outbound, inv_other_outbound_detail, inv_other_inbound, inv_other_inbound_detail, inv_stocktake, inv_stocktake_detail, inv_transfer, inv_transfer_detail
> - 本模块涉及API：/api/inventory/stock-engine, /api/inventory/available-qty, /api/inventory/occupied-qty, /api/inventory/intransit-qty, /api/inventory/cost
> - 本模块业务规则：库存增减原子操作：inv_stock表行级锁+数量增减+版本号乐观锁，所有出入库单据审核时必须通过库存引擎Service执行增减操作，确保库存数据一致性。; 成本计算方法：系统参数inv.cost_method控制全局成本方法（移动加权平均/先进先出/手工指定），按商品可覆盖成本方法。入库时重新加权平均单价，出库时按当前均价或批次顺序匹配成本。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。
### 5.1 Controller类定义
```java
@RestController
@RequestMapping("/api/inventory/stock")
@RequiredArgsConstructor
@Tag(name = "WmsInventory管理")
public class WmsInventoryController {
    private final IWmsInventoryService wmsService;

    @GetMapping
    @Operation(summary = "分页查询列表")
    public R<Page<WmsInventoryListVO>> page(WmsInventoryQueryDTO query) {
        return R.ok(wmsService.pageList(query));
    }

    @PostMapping
    @Operation(summary = "新增")
    public R<Long> create(@Valid @RequestBody WmsInventoryCreateDTO dto) {
        return R.ok(wmsService.create(dto));
    }
}
```


## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/wms/controller/WmsInventoryController.java | Controller类 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | WmsInventoryController的@RestController+@RequestMapping("/api/wms/inventory")正确 | 代码检查 |
| 2 | Service注入正确(@RequiredArgsConstructor) | 代码检查 |
| 3 | Swagger @Tag注解已添加 | 代码检查 |
| 4 | 编译通过无警告 | mvn compile |
## 八、易错警示

> ⚠️ @RequestMapping路径与API文档一致，使用复数名词(如/api/sale/orders)

> ⚠️ Service注入使用@RequiredArgsConstructor+final字段，勿用@Autowired

> ⚠️ Controller仅做参数接收+校验+Service调用+响应包装，不含业务逻辑

> ⚠️ 返回值统一使用R<T>包装
