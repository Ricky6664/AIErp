# P1-005-001-002-004-001 定义Controller类+@RequestMapping路径+注入Service

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-005-001-002-004-001 |
| 任务名称 | 定义Controller类+@RequestMapping路径+注入Service |
| 所属模块 | P1-005 |
| 优先级 | P1 |
| 任务类型 | Service服务层 |

## 二、任务目标

定义SaleOrderController类，@RestController+@RequestMapping("/api/sale/order")，注入ISaleOrderService，声明GET/POST/PUT/DELETE端点方法


## 三、前置依赖

### 3.1 前置任务

- P1-005-001-002-004 销售订单Controller开发（父任务）

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


> **📦 本任务模块上下文**（来源：P1-005模块开发指南）
> - 本模块涉及数据表：sale_quotation, sale_quotation_detail, sale_order, sale_order_detail, sale_order_detail_serial, sale_order_change, sale_delivery_notice, sale_delivery_notice_detail, sale_outbound, sale_outbound_detail
> - 本模块涉及API：/api/sale/order, /api/sale/order, /api/sale/order-change, /api/sale/unshipped, /api/sale/unshipped/export
> - 本模块业务规则：单据流转路径：报价→订单→发货通知→出库→退货→对账，流转路径由系统参数控制（如sale.order_flow_path），参数缺失时提示用户。强制流转选项允许跳过中间环节。; 金额计算引擎：所有销售单据的金额计算（单价×数量+税率+折扣=价税合计）统一调用金额计算引擎，确保精度一致（金额保留4位小数）。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。
### 5.1 Controller类定义
```java
@RestController
@RequestMapping("/api/sale/order")
@RequiredArgsConstructor
@Tag(name = "SaleQuotation管理")
public class SaleOrderController {
    private final ISaleOrderService saleService;

    @GetMapping
    @Operation(summary = "分页查询列表")
    public R<Page<SaleQuotationListVO>> page(SaleQuotationQueryDTO query) {
        return R.ok(saleService.pageList(query));
    }

    @PostMapping
    @Operation(summary = "新增")
    public R<Long> create(@Valid @RequestBody SaleQuotationCreateDTO dto) {
        return R.ok(saleService.create(dto));
    }
}
```


## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/sale/controller/SaleOrderController.java | Controller类 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | SaleOrderController的@RestController+@RequestMapping("/api/sale/order")正确 | 代码检查 |
| 2 | Service注入正确(@RequiredArgsConstructor) | 代码检查 |
| 3 | Swagger @Tag注解已添加 | 代码检查 |
| 4 | 编译通过无警告 | mvn compile |
## 八、易错警示

> ⚠️ @RequestMapping路径与API文档一致，使用复数名词(如/api/sale/orders)

> ⚠️ Service注入使用@RequiredArgsConstructor+final字段，勿用@Autowired

> ⚠️ Controller仅做参数接收+校验+Service调用+响应包装，不含业务逻辑

> ⚠️ 返回值统一使用R<T>包装
