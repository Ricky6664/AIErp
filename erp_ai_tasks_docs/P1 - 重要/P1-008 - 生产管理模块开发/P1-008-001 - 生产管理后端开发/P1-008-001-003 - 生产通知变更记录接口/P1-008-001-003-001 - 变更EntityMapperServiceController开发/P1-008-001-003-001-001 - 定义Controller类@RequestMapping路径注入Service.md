# P1-008-001-003-001-001 定义Controller类+@RequestMapping路径+注入Service

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-008-001-003-001-001 |
| 任务名称 | 定义Controller类+@RequestMapping路径+注入Service |
| 所属模块 | P1-008 |
| 优先级 | P1 |
| 任务类型 | Service服务层 |

## 二、任务目标

定义ProdWorkOrderController类，@RestController+@RequestMapping("/api/production/unproduced")，注入IProdWorkOrderService，声明GET/POST/PUT/DELETE端点方法


## 三、前置依赖

### 3.1 前置任务

- P1-008-001-003-001 变更Entity/Mapper/Service/Controller开发（父任务）

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


> **📦 本任务模块上下文**（来源：P1-008模块开发指南）
> - 本模块涉及数据表：prod_notice, prod_notice_detail, prod_notice_change, prod_mrp_config, prod_mrp_result, prod_picking, prod_picking_detail, prod_over_picking, prod_over_picking_detail, prod_return
> - 本模块涉及API：/api/production/unproduced, /api/production/unproduced/export, /api/production/notice, /api/production/notice-change, /api/production/mrp/config
> - 本模块业务规则：BOM自动展开：生产通知单明细选择产品后，系统根据BOM（来自P0-007商品管理）自动递归展开子件，展示预计用料（BOM子件用量×生产数量），支持多级BOM与替代料展示。; MRP运算引擎：MRP运算包含四个核心步骤——需求分析（销售订单+生产通知需求汇总）、BOM展开（多级递归+用量计算）、库存可用量检查（现有库存+在途+占用→净需求）、计划订单建议（净需求→采购建议/生产建议/委外建议）。运...
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。
### 5.1 Controller类定义
```java
@RestController
@RequestMapping("/api/production/unproduced")
@RequiredArgsConstructor
@Tag(name = "ProdWorkOrder管理")
public class ProdWorkOrderController {
    private final IProdWorkOrderService prodService;

    @GetMapping
    @Operation(summary = "分页查询列表")
    public R<Page<ProdWorkOrderListVO>> page(ProdWorkOrderQueryDTO query) {
        return R.ok(prodService.pageList(query));
    }

    @PostMapping
    @Operation(summary = "新增")
    public R<Long> create(@Valid @RequestBody ProdWorkOrderCreateDTO dto) {
        return R.ok(prodService.create(dto));
    }
}
```


## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/production/controller/ProdWorkOrderController.java | Controller类 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | ProdWorkOrderController的@RestController+@RequestMapping("/api/prod/work-order")正确 | 代码检查 |
| 2 | Service注入正确(@RequiredArgsConstructor) | 代码检查 |
| 3 | Swagger @Tag注解已添加 | 代码检查 |
| 4 | 编译通过无警告 | mvn compile |
## 八、易错警示

> ⚠️ @RequestMapping路径与API文档一致，使用复数名词(如/api/sale/orders)

> ⚠️ Service注入使用@RequiredArgsConstructor+final字段，勿用@Autowired

> ⚠️ Controller仅做参数接收+校验+Service调用+响应包装，不含业务逻辑

> ⚠️ 返回值统一使用R<T>包装
