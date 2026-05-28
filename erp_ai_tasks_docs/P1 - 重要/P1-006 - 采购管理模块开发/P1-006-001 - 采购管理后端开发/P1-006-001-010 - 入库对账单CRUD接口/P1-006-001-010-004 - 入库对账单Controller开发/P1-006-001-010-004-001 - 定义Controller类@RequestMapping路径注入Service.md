# P1-006-001-010-004-001 定义Controller类+@RequestMapping路径+注入Service

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-006-001-010-004-001 |
| 任务名称 | 定义Controller类+@RequestMapping路径+注入Service |
| 所属模块 | P1-006 |
| 优先级 | P1 |
| 任务类型 | Service服务层 |

## 二、任务目标

定义PurchaseOrderController类，@RestController+@RequestMapping("/api/purchase/inquiry")，注入IPurchaseOrderService，声明GET/POST/PUT/DELETE端点方法


## 三、前置依赖

### 3.1 前置任务

- P1-006-001-010-004 入库对账单Controller开发（父任务）

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


> **📦 本任务模块上下文**（来源：P1-006模块开发指南）
> - 本模块涉及数据表：pur_inquiry, pur_inquiry_detail, pur_requisition, pur_requisition_detail, pur_order, pur_order_detail, pur_order_change, pur_inbound, pur_inbound_detail, pur_inbound_return
> - 本模块涉及API：/api/purchase/inquiry, /api/purchase/requisition, /api/purchase/unbuy, /api/purchase/unbuy/export, /api/purchase/order
> - 本模块业务规则：单据流转路径：询价→请购→采购订单→入库→退货→对账，流转路径由系统参数控制（如purchase.order_flow_path），参数缺失时提示用户。强制流转选项允许跳过中间环节。; 金额计算引擎：所有采购单据的金额计算（不含税单价×数量+税率=含税金额）统一调用金额计算引擎，确保精度一致（金额保留4位小数，单价保留6位小数）。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。
### 5.1 Controller类定义
```java
@RestController
@RequestMapping("/api/purchase/inquiry")
@RequiredArgsConstructor
@Tag(name = "PurchaseOrder管理")
public class PurchaseOrderController {
    private final IPurchaseOrderService purchaseService;

    @GetMapping
    @Operation(summary = "分页查询列表")
    public R<Page<PurchaseOrderListVO>> page(PurchaseOrderQueryDTO query) {
        return R.ok(purchaseService.pageList(query));
    }

    @PostMapping
    @Operation(summary = "新增")
    public R<Long> create(@Valid @RequestBody PurchaseOrderCreateDTO dto) {
        return R.ok(purchaseService.create(dto));
    }
}
```


## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/purchase/controller/PurchaseOrderController.java | Controller类 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | PurchaseOrderController的@RestController+@RequestMapping("/api/purchase/order")正确 | 代码检查 |
| 2 | Service注入正确(@RequiredArgsConstructor) | 代码检查 |
| 3 | Swagger @Tag注解已添加 | 代码检查 |
| 4 | 编译通过无警告 | mvn compile |
## 八、易错警示

> ⚠️ @RequestMapping路径与API文档一致，使用复数名词(如/api/sale/orders)

> ⚠️ Service注入使用@RequiredArgsConstructor+final字段，勿用@Autowired

> ⚠️ Controller仅做参数接收+校验+Service调用+响应包装，不含业务逻辑

> ⚠️ 返回值统一使用R<T>包装
