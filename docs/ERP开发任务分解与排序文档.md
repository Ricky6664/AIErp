# ERP开发任务分解与优先级排序文档

> **版本**：V11.0（AI自动化开发执行版 - 开发前最终全量核查修正版）  
> **基于**：《ERP开发详细设计文档V4.3》  
> **定位**：专属服务于AI自动化开发执行，所有内容均为AI开发适配优化，可直接指导AI分步落地开发  
> **说明**：本版本在V10.1基础上完成开发前最终全量核查修正：①修正4处页面类型编号错误（P0-006组织中心P01→P02、P0-008客户联系人P07→P06、P0-007 BOM成本P04→P10、P0-012薪资管理P04→P03+补P06）；②补全P0-007/P0-009/P0-012共25个P07/P06录入页遗漏；③补全P0-006工作台前端页面+P0-008客户附件录入页；④更新任务统计  
> **统计**：P0模块14个 + P1模块15个 + P2模块17个 = 总计46个一级任务模块。各层级任务统计详见第五章。

# 第零章 开发约束与规范总纲

> 本章集中沉淀V4.3设计文档中的核心约束、规范、原则与方法论，作为全项目开发执行的刚性准绳。所有任务节点在执行时必须遵守本章约束，不得违背。本章为**全局最高优先级AI开发约束**，作为全文所有开发任务的统一执行提示词与标准依据，全文任务必须无条件遵守本章规范。

---

## 0.1 整体项目开发总规范

### 0.1.1 技术栈强制约束

> 以下技术栈与设计文档第二章2.1节完全对齐，所有版本号均为锁定版本，开发执行时不得偏离。

#### 0.1.1.1 前端技术栈（Web端）
| 技术层 | 技术选型 | 版本 | 说明 |
|--------|---------|------|------|
| 框架 | Vue 3 | 3.5.x | Composition API + `<script setup>`语法，响应式系统精准更新 |
| 构建工具 | Vite | 6.x | 启动速度<500ms，HMR秒更新，Rollup构建产物体积小 |
| 语言 | TypeScript | 5.x | 全量类型安全，与Vue3/Element Plus天然兼容 |
| **UI组件库** | **Element Plus** | **2.8.x** | 国内ERP/中后台事实标准，组件覆盖100%ERP场景，支持浅色/深色模式 |
| 图标库 | Element Plus Icons | 最新 | 官方配套图标库，风格统一，支持按需引入 |
| 样式方案 | Tailwind CSS + SCSS | 4.x | Element Plus组件使用SCSS变量定制主题色，Tailwind仅用于布局/间距/排版原子类；深色模式统一通过Element Plus的dark class切换 |
| 状态管理(全局) | Pinia | 2.x | Vue3官方状态管理，管理用户信息/权限/主题/全局配置 |
| 组合式工具库 | VueUse | 最新 | 防抖/节流/响应式/浏览器API等工具函数 |
| 状态管理(服务端) | Vue Query(TanStack Query) + Axios | 最新 | 服务端状态管理（缓存/重试/后台刷新）+Axios请求 |
| **高级表格** | **Vxe Table** | **4.x** | 百万数据虚拟滚动、单元格合并/公式/下拉填充、导入导出Excel、固定列/树形/可编辑表格 |
| 图表组件 | ECharts | 5.x | 30+种图表类型，大数据渲染性能强，支持深色主题 |
| 大屏适配 | v-scale-screen + CSS clamp() | 最新 | 1920/2K/4K等比缩放 |
| 路由 | Vue Router | 4.x | 懒加载、路由守卫、权限控制、动态菜单 |
| HTTP客户端 | Axios | 1.x | 拦截器/取消请求/上传进度/错误处理 |
| 富文本 | TinyMCE Vue3 | 7.x | 开源版功能完整，适合OA通知、公告、备注编辑 |
| Excel导入 | xlsx + Vxe Table | 最新 | 前端解析Excel，支持复杂表头、批量导入校验 |
| Excel导出 | Vxe Table 内置导出 | 内置 | 一键导出带样式、合并单元格、多Sheet |
| 打印 | print-js | 最新 | 单据打印、报表打印、条码打印通用方案 |
| 国际化(i18n) | vue-i18n@next | 9.x | 多语言切换、动态语言包 |
| 代码规范 | ESLint + Prettier | 最新 | 统一代码风格，保证企业项目可维护性 |
| 测试 | Vitest | 最新 | Vite生态原生支持，轻量快速 |

#### 0.1.1.2 前端技术栈（移动端）
| 技术层 | 技术选型 | 版本 | 说明 |
|--------|---------|------|------|
| 跨端框架 | UniApp (Vue 3) | 3.x | 一套代码编译：微信小程序+iOS+Android+H5 |
| UI框架 | uView Plus | 3.x | UniApp生态最稳定、企业级首选 |
| 状态管理 | Pinia | 2.x | 与Web端通用 |
| 请求封装 | luch-request | 最新 | UniApp专用请求库 |
| 图表 | ucharts | 最新 | 跨端图表，支持深色模式 |
| 扫码 | uni.scanCode | 内置 | 原生扫码 |
| 定位 | uni.getLocation | 内置 | 原生定位，适合外勤/巡检/仓库 |
| 推送 | 极光推送 + 微信订阅消息 | - | 双通道推送 |
| 国际化 | vue-i18n@next | 9.x | 与Web端统一配置 |
| 离线存储 | uni.setStorage + SQLite | - | 离线单据/盘点/提交 |

#### 0.1.1.3 后端技术栈
| 技术层 | 技术选型 | 版本 | 说明 |
|--------|---------|------|------|
| 语言 | Java | 17 LTS | 企业ERP标准，生态最全 |
| 框架 | Spring Boot | 3.4.x | 自动配置、稳定无坑 |
| ORM | MyBatis-Plus | 3.5.5 | 单表CRUD零代码、复杂SQL可控、多租户/软删除/乐观锁内置 |
| 权限 | Sa-Token | 1.38+ | 注解鉴权、多端登录 |
| 接口文档 | SpringDoc | 2.x | OpenAPI 3 |
| 参数校验 | Jakarta Validation | - | 注解校验 |
| 任务调度 | XXL-JOB | 2.4.x | 分布式定时任务 |
| 消息队列 | RabbitMQ | 3.12+ | 业务异步：单据流转、批量任务、跨模块事件通知 |
| WebSocket | Spring Boot + STOMP | 内置 | 消息订阅模式 |
| 文件处理 | EasyExcel | 3.3.x | 大数据Excel导入导出 |
| PDF | OpenPDF | 2.x | 单据/报表PDF生成（LGPL/MPL）；HTML-to-PDF搭配OpenHTMLtoPDF(LGPL) |
| 打印 | Lodop | - | 仅用于需要套打的条码/票据场景；常规打印统一使用前端print-js |
| 日志 | SLF4J + Logback | - | 标准日志 |
| 数据库迁移 | Flyway | 10.x | 版本化SQL，多环境统一 |
| 工具类 | Hutool | 5.8.x | 工具库 |
| 异常处理 | 全局异常处理器 | 内置 | 统一返回结构 |

#### 0.1.1.4 外部集成SDK
| 集成系统 | 技术选型 | 版本 | 说明 |
|---------|---------|------|------|
| 微信开放平台 | weixin-java-miniapp (WxJava) | 4.6.x | 小程序登录、模板消息、公众号 |
| 微信支付 | weixin-java-pay (WxJava) | 4.6.x | API v3、签名自动管理 |
| 阿里云短信 | Aliyun Java SDK (Dysmsapi) | 2.x | 短信验证码、业务通知 |
| 电子发票 | 诺诺/百望云 API | - | 销项发票开具、进项发票OCR识别 |
| 快递查询 | 快递鸟/快递100 API | - | 物流轨迹查询、自动订阅推送 |

#### 0.1.1.5 数据库与缓存
| 技术层 | 技术选型 | 版本 | 说明 |
|--------|---------|------|------|
| 关系数据库 | PostgreSQL | 15+ | JSON支持极强、复杂查询强、多租户、GIS |
| 缓存 | Redis | 7.x | 分布式锁、缓存、限流、Pub/Sub（基础设施层，与RabbitMQ业务异步互补） |
| 搜索引擎 | Elasticsearch | 8.x | 全文检索、单据高级搜索（私有化部署；SaaS模式改用OpenSearch） |
| 对象存储 | MinIO | RELEASE.2024-11-07 | 私有化存储，S3协议兼容 |
| 向量数据库 | pgvector | - | 基于PostgreSQL，无需额外部署 |

#### 0.1.1.6 AI能力技术栈
| 技术层 | 技术选型 | 版本 | 说明 |
|--------|---------|------|------|
| 大模型接入 | OpenAI兼容API | - | 通义千问、豆包、DeepSeek、Kimi无缝切换 |
| 结构化输出 | JSON Schema | - | 强制AI输出JSON，前端可直接解析 |
| OCR | PaddleOCR | latest | 发票、单据、条码识别 |
| RAG框架 | LangChain4j | latest | Java原生AI框架，与Spring Boot无缝集成 |

#### 0.1.1.7 基础设施与DevOps
| 技术层 | 技术选型 | 说明 |
|--------|---------|------|
| 容器化 | Docker + Docker Compose | 简化部署，环境一致性 |
| 编排 | Kubernetes（可选） | 大规模SaaS部署 |
| CI/CD | GitLab CI / GitHub Actions | 自动化构建、测试、部署 |
| 监控 | Prometheus + Grafana | 指标采集与可视化 |
| 日志 | ELK (Elasticsearch+Logstash+Kibana) | 集中式日志管理 |
| 链路追踪 | SkyWalking | 性能瓶颈定位 |
| 配置中心 | Nacos | 仅Kubernetes大规模SaaS部署时使用；单体部署使用Spring Boot原生配置 |

### 0.1.2 开发执行核心原则
| 原则 | 说明 |
|------|------|
| 依赖前置 | 被依赖的模块必须先开发，依赖方必须后开发 |
| 先基础后业务 | 先完成框架、数据库、认证、公共组件，再做业务模块 |
| 先底层后上层 | 先完成后端接口/服务层，再完成前端页面/展示层 |
| 先公共后业务 | 先完成通用引擎（编码引擎/审核引擎/流转引擎），再做业务单据 |
| 后端先行 | 每个模块内部，后端（建表→Entity→Mapper→Service→Controller）先于前端（页面→组件→交互） |
| 单任务原子性 | 每个最末级任务为一个最小不可拆分的开发执行单元，AI单次可独立完成 |

### 0.1.3 单模块开发内部顺序规范
每个业务模块的开发严格按以下顺序执行：
```
1. 数据库建表（CREATE TABLE + 索引 + 约束）
2. 后端Entity/DTO/VO定义
3. 后端Mapper层（MyBatis-Plus Mapper + XML映射）
4. 后端Service层（业务逻辑 + 校验 + 联动 + 计算）
5. 后端Controller层（RESTful API接口）
6. 后端单元可运行验证（接口可调通）
7. 前端页面路由注册 + 菜单数据配置
8. 前端页面开发（基于页面类型基座组件 + 字段配置渲染）
9. 前后端联调验证
```

### 0.1.4 任务颗粒度标准
最末级任务（叶子节点）必须满足以下全部条件：
1. **单次可完成**：AI在单次执行上下文中可独立完成，无需跨上下文
2. **边界明确**：有清晰的输入（依赖项）和输出（交付物）
3. **可验证**：有明确的验证标准（接口可调通/页面可渲染/功能可操作）
4. **无交叉**：与其他叶子任务无功能重叠或逻辑耦合
5. **原子性**：不可进一步拆分为更小的有意义的开发单元

### 0.1.5 代码规范通用要求
- 标点符号**全部半角**，禁止中文全角标点（字符串内中文文本除外）
- 所有函数参数（含回调、事件处理器）**必须标注类型**，禁止隐式any
- 所有组件/函数/类型**使用前必须import**，禁止假设全局可用
- 括号`(){}[]`和模板字符串必须**成对闭合**

---

## 0.2 后端技术框架开发规范

### 0.2.1 项目结构规范
```
src/main/java/com/xxx/erp/
├── config/           # 配置类（MyBatis-Plus/Sa-Token/Redis/全局异常）
├── common/           # 公共基础（R响应类/分页类/枚举/常量/工具）
├── modules/          # 业务模块（按模块分子包）
│   ├── crm/          # 客户管理模块
│   │   ├── controller/
│   │   ├── service/
│   │   ├── mapper/
│   │   ├── entity/
│   │   ├── dto/
│   │   └── vo/
│   ├── srm/          # 供应商管理模块
│   ├── prod/         # 商品管理模块
│   ├── org/          # 组织架构模块
│   ├── hrm/          # 人力资源模块
│   ├── purchase/     # 采购管理模块
│   ├── sale/         # 销售管理模块
│   ├── inv/          # 库存管理模块
│   ├── produce/      # 生产管理模块
│   ├── subcontract/  # 委外管理模块
│   ├── ar/           # 应收管理模块
│   ├── ap/           # 应付管理模块
│   ├── voucher/      # 凭证管理模块
│   ├── ledger/       # 账簿查询模块
│   ├── report/       # 财务报表模块
│   ├── cost/         # 成本核算模块
│   ├── expense/      # 费用管理模块
│   ├── asset/        # 固定资产模块
│   ├── sample/       # 样品管理模块
│   ├── borrow/       # 借用管理模块
│   ├── lease/        # 租赁管理模块
│   ├── aftersale/    # 售后管理模块
│   ├── oa/           # OA办公模块
│   ├── ai/           # AI助手模块
│   ├── template/     # 模板中心模块
│   ├── sys/          # 系统管理模块
│   ├── ops/          # 运维管理模块
│   └── i18n/         # 多语言模块
├── engine/           # 通用引擎（编码/审核/流转/消息）
└── ApiApplication.java
```

### 0.2.2 API设计规范
| 操作类型 | URL格式 | 说明 |
|---------|--------|------|
| 列表查询 | GET /api/{module}/{resource} | 分页查询 |
| 详情查询 | GET /api/{module}/{resource}/{id} | 按ID查询 |
| 新增 | POST /api/{module}/{resource} | 新增记录 |
| 修改 | PUT /api/{module}/{resource}/{id} | 按ID修改 |
| 删除 | DELETE /api/{module}/{resource}/{id} | 软删除 |
| 审核/反审 | POST /api/{module}/{resource}/{id}/audit | 状态流转 |
| 引入 | POST /api/{module}/{resource}/import | 源单引入 |
| 下推 | POST /api/{module}/{resource}/push | 下推生成 |
| 批量操作 | POST /api/{module}/{resource}/batch | 批量处理 |

### 0.2.2.1 统一响应结构规范（R<T> + PageResult<T>）

> 全系统所有API接口统一使用以下响应结构，前端拦截器和状态管理均按此结构解析。

**R&lt;T&gt; — 通用响应类（非分页场景）**

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<T> implements Serializable {
    /** 状态码：200=成功，400=参数错误，401=未认证，403=无权限，500=服务端错误 */
    private int code;
    /** 响应消息：成功时="success"，失败时=具体错误描述 */
    private String message;
    /** 响应数据：泛型，可为null */
    private T data;
    /** 响应时间戳（毫秒） */
    private long timestamp;

    // 静态工厂方法
    public static <T> R<T> ok(T data) { return new R<>(200, "success", data, System.currentTimeMillis()); }
    public static <T> R<T> ok() { return ok(null); }
    public static <T> R<T> fail(int code, String message) { return new R<>(code, message, null, System.currentTimeMillis()); }
    public static <T> R<T> error(String message) { return fail(500, message); }
    public static <T> R<T> paramError(String message) { return fail(400, message); }
    public static <T> R<T> unauthorized() { return fail(401, "未登录或登录已过期"); }
    public static <T> R<T> forbidden() { return fail(403, "无操作权限"); }
}
```

**PageResult&lt;T&gt; — 分页响应类**

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    /** 总记录数 */
    private long total;
    /** 当前页数据列表 */
    private List<T> records;
    /** 当前页码 */
    private long pageNum;
    /** 每页大小 */
    private long pageSize;
    /** 总页数 */
    private long totalPages;

    public static <T> PageResult<T> of(long total, List<T> records, long pageNum, long pageSize) {
        long totalPages = pageSize > 0 ? (total + pageSize - 1) / pageSize : 0;
        return new PageResult<>(total, records, pageNum, pageSize, totalPages);
    }
}
```

**分页接口响应组合**：分页查询接口使用 `R<PageResult<T>>` 组合，示例：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1500,
    "records": [...],
    "pageNum": 1,
    "pageSize": 20,
    "totalPages": 75
  },
  "timestamp": 1716500000000
}
```

### 0.2.3 Controller层规范
```java
@RestController
@RequestMapping("/api/sale/order")
@Tag(name = "销售订单", description = "销售订单管理接口")
@RequiredArgsConstructor
public class SaleOrderController {
    private final SaleOrderService saleOrderService;
    
    @GetMapping
    @Operation(summary = "分页查询销售订单列表")
    public R<PageResult<SaleOrderListVO>> list(SaleOrderQueryDTO query) {
        return R.ok(saleOrderService.getPage(query));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "查询销售订单详情")
    public R<SaleOrderDetailVO> getById(@PathVariable Long id) {
        return R.ok(saleOrderService.getDetailById(id));
    }
    
    @PostMapping
    @Operation(summary = "新增销售订单")
    public R<Long> create(@Validated @RequestBody SaleOrderCreateDTO dto) {
        return R.ok(saleOrderService.create(dto));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "修改销售订单")
    public R<Void> update(@PathVariable Long id, @Validated @RequestBody SaleOrderUpdateDTO dto) {
        saleOrderService.update(id, dto);
        return R.ok();
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "删除销售订单")
    public R<Void> delete(@PathVariable Long id) {
        saleOrderService.delete(id);
        return R.ok();
    }
    
    @PostMapping("/{id}/audit")
    @Operation(summary = "审核/反审销售订单")
    public R<Void> audit(@PathVariable Long id, @RequestBody AuditDTO dto) {
        saleOrderService.audit(id, dto);
        return R.ok();
    }
}
```

### 0.2.4 Service层规范
```java
@Service
@RequiredArgsConstructor
public class SaleOrderServiceImpl extends ServiceImpl<SaleOrderMapper, SaleOrder> implements SaleOrderService {
    private final SaleOrderDetailService detailService;
    private final CodeGeneratorService codeGeneratorService;
    private final AuditService auditService;
    
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long create(SaleOrderCreateDTO dto) {
        // 1. 主表保存
        SaleOrder order = BeanUtil.copyProperties(dto, SaleOrder.class);
        order.setBillNo(codeGeneratorService.generate("SALE_ORDER"));
        order.setBillStatus(BillStatus.DRAFT.getCode());
        save(order);
        
        // 2. 从表保存
        List<SaleOrderDetail> details = BeanUtil.copyToList(dto.getDetails(), SaleOrderDetail.class);
        details.forEach(d -> d.setOrderId(order.getId()));
        detailService.saveBatch(details);
        
        // 3. 关联处理（库存占用、应收款等）
        
        return order.getId();
    }
}
```

### 0.2.5 Mapper层规范
```java
@Mapper
public interface SaleOrderMapper extends BaseMapperX<SaleOrder> {
    // 自定义复杂查询方法
    List<SaleOrderStatisticsVO> statisticsByCustomer(@Param("customerId") Long customerId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
```

### 0.2.6 Entity层规范
```java
@Data
@TableName("sale_order")
public class SaleOrder {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private String billNo;
    private LocalDate billDate;
    private Integer billStatus;
    private Integer auditStatus;
    private Long customerId;
    private String customerName;
    // ... 业务字段
    
    // 通用必含字段
    private Long tenantId;
    private Long creatorId;
    private LocalDateTime createTime;
    private Long updaterId;
    private LocalDateTime updateTime;
    @TableLogic
    private Boolean isDeleted;
    private Integer version;
}
```

### 0.2.7 DTO/VO层规范
```java
// 创建DTO
@Data
public class SaleOrderCreateDTO {
    @NotBlank(message = "客户ID不能为空")
    private Long customerId;
    
    @NotNull(message = "明细不能为空")
    @Size(min = 1, message = "明细至少1行")
    private List<SaleOrderDetailDTO> details;
    
    // ... 其他字段
}

// 列表VO
@Data
public class SaleOrderListVO {
    private Long id;
    private String billNo;
    private String billStatus;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate billDate;
    private String customerName;
    private BigDecimal totalAmount;
    
    // 字典翻译字段
    private String billStatusName;
}

// 详情VO
@Data
public class SaleOrderDetailVO {
    private Long id;
    // ... 主表字段
    private List<SaleOrderDetailItemVO> details;
}
```

### 0.2.8 事务管理规范
- 单据保存（主表+从表）使用`@Transactional`数据库事务
- 审核操作需在事务内完成状态变更+库存/财务联动
- 编码生成需在事务内完成序号递增（Redis原子操作或数据库行级锁）

---

## 0.3 前端技术框架开发规范

### 0.3.1 项目结构规范
```
src/
├── api/              # API请求层（按模块分文件）
├── assets/           # 静态资源
├── components/       # 公共组件
│   ├── page-types/   # 15种页面类型基座组件
│   ├── inputs/       # 20种录入组件
│   ├── selectors/    # 7大体系业务选择器
│   └── business/     # 业务增强组件
├── composables/      # 组合式函数
├── layouts/          # 布局组件
├── router/           # 路由配置
├── stores/           # Pinia状态管理
├── styles/           # 全局样式
├── utils/            # 工具函数
└── views/            # 页面视图（按模块分目录）
```

### 0.3.2 API调用规范
```typescript
// api/sale/order.ts
import apiClient from '@/utils/request';

export interface SaleOrderQuery {
  page: number;
  pageSize: number;
  billNo?: string;
  customerId?: number;
  startDate?: string;
  endDate?: string;
}

export interface SaleOrderListVO {
  id: number;
  billNo: string;
  billStatus: string;
  billDate: string;
  customerName: string;
  totalAmount: number;
}

// 分页查询
export function getSaleOrderPage(params: SaleOrderQuery) {
  return apiClient.get<PageResult<SaleOrderListVO>>('/api/sale/order', { params });
}

// 详情查询
export function getSaleOrderById(id: number) {
  return apiClient.get<SaleOrderDetailVO>(`/api/sale/order/${id}`);
}

// 新增
export function createSaleOrder(data: SaleOrderCreateDTO) {
  return apiClient.post<number>('/api/sale/order', data);
}

// 修改
export function updateSaleOrder(id: number, data: SaleOrderUpdateDTO) {
  return apiClient.put<void>(`/api/sale/order/${id}`, data);
}

// 删除
export function deleteSaleOrder(id: number) {
  return apiClient.delete<void>(`/api/sale/order/${id}`);
}

// 审核
export function auditSaleOrder(id: number, data: AuditDTO) {
  return apiClient.post<void>(`/api/sale/order/${id}/audit`, data);
}
```

### 0.3.3 组件开发规范
```vue
<!-- 使用 <script setup> 语法 -->
<template>
  <div class="page-container">
    <!-- 搜索条件区 -->
    <el-card class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="单据编号">
          <el-input v-model="queryParams.billNo" placeholder="请输入单据编号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 数据表格区 -->
    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>销售订单列表</span>
          <div class="header-buttons">
            <el-button type="primary" @click="handleAdd" v-permission="'sale:order:add'">新增</el-button>
          </div>
        </div>
      </template>
      <vxe-table :data="tableData" :loading="loading">
        <vxe-column type="seq" title="序号" width="60" />
        <vxe-column field="billNo" title="单据编号" />
        <vxe-column field="billStatus" title="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.billStatus)">{{ row.billStatusName }}</el-tag>
          </template>
        </vxe-column>
        <!-- 更多列 -->
      </vxe-table>
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        @current-change="loadData"
        @size-change="loadData"
      />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { getSaleOrderPage, deleteSaleOrder } from '@/api/sale/order';

// 响应式状态
const loading = ref(false);
const tableData = ref<SaleOrderListVO[]>([]);
const total = ref(0);

// 查询参数
const queryParams = reactive<SaleOrderQuery>({
  page: 1,
  pageSize: 20,
  billNo: '',
});

// 加载数据
const loadData = async () => {
  loading.value = true;
  try {
    const { data } = await getSaleOrderPage(queryParams);
    tableData.value = data.records;
    total.value = data.total;
  } finally {
    loading.value = false;
  }
};

// 初始化
onMounted(() => {
  loadData();
});
</script>
```

### 0.3.4 状态管理规范（Pinia）
```typescript
// stores/user.ts
import { defineStore } from 'pinia';
import { getUserInfo, logout } from '@/api/auth';

export const useUserStore = defineStore('user', {
  state: () => ({
    token: '',
    userInfo: null as UserInfo | null,
    permissions: [] as string[],
    roles: [] as string[],
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    hasPermission: (state) => (permission: string) => state.permissions.includes(permission),
  },
  
  actions: {
    async login(credentials: LoginDTO) {
      const { data } = await loginApi(credentials);
      this.token = data.token;
    },
    
    async fetchUserInfo() {
      const { data } = await getUserInfo();
      this.userInfo = data.user;
      this.permissions = data.permissions;
      this.roles = data.roles;
    },
    
    async logout() {
      await logout();
      this.$reset();
    },
  },
  
  persist: true, // 持久化
});
```

### 0.3.5 类型安全规范
- 所有API请求/响应定义TypeScript类型
- 所有组件Props定义类型
- 所有数据表字段定义类型
- **禁止使用any类型**

---

## 0.4 数据层开发规范

### 0.4.1 通用必含字段
所有业务表必须包含以下公共字段，由MyBatis-Plus自动填充：

| 字段名 | 数据类型 | 默认中文标题 | 说明 |
|--------|---------|-------------|------|
| id | BIGINT | 主键ID | 雪花算法 |
| tenant_id | BIGINT | 租户ID | 多租户隔离字段，查询默认注入，索引首列 |
| created_at | TIMESTAMP | 创建时间 | 审计字段，不可修改 |
| updated_at | TIMESTAMP | 更新时间 | 审计字段，每次修改自动更新 |
| created_by | BIGINT | 创建人ID | 审计字段，不可修改 |
| updated_by | BIGINT | 更新人ID | 审计字段，每次修改自动更新 |
| is_deleted | BOOLEAN | 是否删除 | 软删除标记，查询默认过滤is_deleted=false |
| owner_dept_id | BIGINT | 归属部门ID | 数据权限字段 |
| owner_id | BIGINT | 归属人ID | 数据权限字段，默认=created_by |
| version | INT | 版本号 | 乐观锁版本号 |

### 0.4.2 单据主表特有字段
所有单据主表（销售单、采购单、出入库单等）必须额外包含：

| 字段名 | 数据类型 | 默认中文标题 | 说明 |
|--------|---------|-------------|------|
| bill_no | VARCHAR(32) | 单据编号 | 唯一约束需含is_deleted字段 |
| bill_date | DATE | 单据日期 | 默认当天 |
| bill_status | SMALLINT | 单据状态 | 0=草稿/1=待审/2=审核中/3=已审核/4=已关闭/5=已作废 |
| audit_status | SMALLINT | 审核状态 | 0=未提交/1=待审/2=已审核/3=已驳回 |
| attachment_count | INT | 附件数 | 默认0 |
| source_bill_no | VARCHAR(32) | 来源单号 | 下推来源 |
| source_bill_type | VARCHAR(32) | 来源单类型 | 下推来源类型 |

### 0.4.3 明细从表商品冗余字段+快照原则
所有单据明细从表包含商品信息冗余字段，下推时源单商品信息快照写入不可修改：

| 字段名 | 数据类型 | 默认中文标题 | 说明 |
|--------|---------|-------------|------|
| product_code | VARCHAR(50) | 商品编码 | 下单时商品编码快照 |
| product_name | VARCHAR(100) | 商品名称 | 下单时商品名称快照 |
| model | VARCHAR(100) | 型号 | 下单时型号快照 |
| spec | VARCHAR(100) | 规格 | 下单时规格快照 |
| brand | VARCHAR(50) | 品牌 | 下单时品牌快照 |
| unit_id | BIGINT | 单位ID | 下单时所选单位ID |
| product_unit | VARCHAR(30) | 单位名称 | 下单时所选单位中文名 |
| qty | DECIMAL(18,8) | 数量 | 按所选单位的数量 |
| is_multi_unit | BOOLEAN | 是否多单位 | 商品是否启用多单位 |
| conversion_rate | DECIMAL(18,8) | 转换比例 | 所选单位与基础单位的转换比例 |
| base_unit_id | BIGINT | 基础单位ID | 商品基础单位ID |
| base_qty | DECIMAL(18,8) | 换算数量 | 基础单位数量，库存增减/成本核算/统计汇总统一使用此值 |
| customer_product_code | VARCHAR(50) | 客户料号 | 销售链路单据冗余，从报价单沿链路自动传递 |
| supplier_product_code | VARCHAR(50) | 供应商料号 | 采购链路单据冗余，用于追溯供应商原始物料编码 |
| aux_attr1~10 | VARCHAR(100) | 辅助属性值1~10 | 动态扩展列，用于存储颜色/尺码/批次属性等辅助属性 |
| attr_json | JSONB | 辅助属性完整JSON | 辅助属性结构化存储，含属性名+属性值键值对 |

**快照约束**：下推时源单商品字段自动填充，下推后只读不可修改，变更商品时重新覆盖

**客户料号传递链路**：报价单 → 销售订单 → 发货通知 → 出库单 → 退货单 → 对账单 → 发票（销售链路自动传递）

**供应商料号传递链路**：询价单 → 请购单 → 采购订单 → 入库单 → 退货单 → 对账单（采购链路自动传递）

### 0.4.4 数据类型约定

> **核心原则**：单价、数量、金额、转换率等数值字段的小数位数**不硬编码**，由系统参数统一控制。数据库物理字段统一使用`DECIMAL(18,8)`（最大精度8位），实际显示和输入精度由系统参数动态控制。

| 业务含义 | 数据库物理类型 | 默认精度 | 控制参数 | 说明 |
|----------|--------------|---------|---------|------|
| 金额/合计 | DECIMAL(18,8) | 2位 | `system.decimal_places_amount` | 最高8位，录入/显示精度由参数控制 |
| 单价 | DECIMAL(18,8) | 4位 | `sale.decimal_places_price` 或 `purchase.decimal_places_price` | 最高8位，销售/采购可独立配置 |
| 数量 | DECIMAL(18,8) | 6位 | `system.decimal_places_qty` | 最高8位，适用于所有单据明细行 |
| 转换率 | DECIMAL(18,8) | 6位 | `system.decimal_places_qty` | 最高8位，多单位换算比例 |
| 税率 | DECIMAL(5,4) | 2位 | — | 百分比，如13.00，最高4位小数 |
| 百分比 | DECIMAL(5,4) | 2位 | — | 百分比值，最高4位小数 |
| 编码 | VARCHAR(50) | — | — | 业务编码统一50位 |
| 名称 | VARCHAR(128) | — | — | 业务名称统一128位 |
| 备注 | VARCHAR(512) | — | — | 备注统一512位 |
| 状态 | SMALLINT | — | — | 枚举值，0起 |
| 日期 | DATE | — | — | 仅日期 |
| 时间 | TIMESTAMP | — | — | 日期+时间 |
| 布尔 | BOOLEAN | — | — | true/false |

**精度控制规则**：
- 前端录入组件根据系统参数精度值控制`precision`属性（见0.5.3节录入组件规范）
- 后端校验时按参数值截断/四舍五入（见0.2.4 Service层规范）
- 金额=数量×单价时，先以最大精度(8位)计算乘积，再按金额精度参数四舍五入
- 税额=金额×税率时，税率固定2位，乘积后按金额精度参数截断
- 合计=金额+税额（及其他费用），尾差由合计行补齐

### 0.4.5 base_qty核心地位
- `base_qty`为计量基准数量，所有数量计算以base_qty为准
- 公式：qty x rate = base_qty（数量 x 换算率 = 基准数量）
- 数量比较/汇总/库存计算均使用base_qty
- 前端展示qty但后端校验/统计必须用base_qty

### 0.4.6 唯一约束需包含is_deleted字段
软删除场景下，bill_no等业务唯一约束必须包含is_deleted字段：
```sql
UNIQUE(tenant_id, bill_no, is_deleted)
```
否则删除后无法重建同编号

### 0.4.7 多租户隔离规范
- 所有表含tenant_id BIGINT NOT NULL字段
- 所有查询WHERE自动注入tenant_id（MyBatis-Plus TenantLineInnerInterceptor）
- 唯一约束包含tenant_id字段
- 数据隔离粒度：行级隔离
- 租户间数据不可见

### 0.4.8 库位/批次/序列号三表存储规范
库位、批次、序列号三者各自独立建子表，均采用**一行对多行**模式：

| 辅助属性 | 存储方式 | 关系 | 子表名 | 说明 |
|---------|---------|------|--------|------|
| 仓库ID | 直接存明细行 | 一对一 | - | 每条明细行必填一个仓库 |
| 库位 | 独立子表 | 一对多 | doc_detail_location | 是否必填由is_location_manage控制 |
| 批次 | 独立子表 | 一对多 | doc_detail_batch | 是否必填由is_batch_manage控制 |
| 序列号 | 独立子表 | 一对多 | doc_detail_serial | 是否必填由is_serial_manage控制；记录数=主表qty |

**三表独立性**：各自独立、互不耦合，可单独开启/关闭

---

## 0.5 公共组件与基础配置开发规范

### 0.5.1 15种页面类型组件规范
| 编号 | 类型名称 | 典型场景 | 组件名 |
|------|---------|---------|--------|
| P01 | 首页 | 系统入口仪表盘 | HomePage |
| P02 | 工作台 | 各模块工作台 | WorkbenchPage |
| P03 | 主从列表页 | 订单/单据列表 | MasterDetailListPage |
| P04 | 单一列表页 | 基础档案列表 | SingleListPage |
| P05 | 树形列表页 | 分类/部门/科目 | TreeListPage |
| P06 | 主从表单页 | 订单/单据录入 | MasterDetailFormPage |
| P07 | 单一表单页 | 基础档案录入 | SingleFormPage |
| P08 | 看板页 | 生产/库存看板 | DashboardPage |
| P09 | 查询页 | 未XX清单/库存查询 | QueryPage |
| P10 | 报表页 | 成本/财务报表 | ReportPage |
| P11 | 大屏页 | 数据大屏 | ScreenPage |
| P12 | 画像页 | 客户画像/供应商画像 | ProfilePage |
| P13 | 配置页 | 系统配置与权限配置 | ConfigPage |
| P14 | AI对话页 | AI智能中心 | AIChatPage |
| P15 | 设计器页 | 报表设计/审批设计 | DesignerPage |

### 0.5.2 页面类型组件开发原则
- 所有页面类型（P01-P15）必须开发为独立可复用的页面类型组件
- 每个功能点页面通过配置驱动渲染，不单独开发页面
- 组件开发遵循Element Plus风格和规范，使用`<script setup>`语法
- 所有交互组件必须支持v-model受控模式
- 所有录入组件必须支持:disabled只读模式切换

### 0.5.3 录入组件规范
| 类型 | 组件 | 说明 |
|------|------|------|
| 文本 | el-input | 单行文本 |
| 多行文本 | el-input type="textarea" | 多行文本 |
| 数字 | el-input-number | 数字输入 |
| 金额 | AmountInput | 金额输入（格式化显示） |
| 日期 | el-date-picker | 日期选择 |
| 日期时间 | el-date-picker type="datetime" | 日期时间选择 |
| 时间 | el-time-picker | 时间选择 |
| 下拉选择 | el-select | 下拉选择 |
| 级联选择 | el-cascader | 级联选择 |
| 开关 | el-switch | 开关 |
| 复选框 | el-checkbox | 复选框 |
| 单选框 | el-radio-group | 单选框组 |
| 文件上传 | el-upload | 文件上传 |
| 图片上传 | ImageUpload | 图片上传 |
| 富文本 | TinyMCE | 富文本编辑器（与设计文档2.1.1选型TinyMCE Vue3 7.x一致） |
| 字典选择 | DictSelect | 字典下拉选择 |
| 树形选择 | el-tree-select | 树形下拉选择 |
| 弹窗选择 | DialogSelect | 弹窗选择器 |
| 地区选择 | RegionCascader | 省市区选择 |
| 话术选择 | ScriptSelect | 话术模板选择 |

### 0.5.3.1 field_type到前端组件映射表

> 以下映射表为字段配置驱动的核心依据。数据库字段配置表中的field_type值决定前端渲染哪个录入组件。所有模块的录入页（P06/P07）和表格单元格编辑均按此映射渲染。

| field_type值 | 映射组件 | 适用数据类型 | 说明 |
|-------------|---------|------------|------|
| text | el-input | varchar/text | 单行文本输入 |
| textarea | el-input(type=textarea) | text | 多行文本输入 |
| number | el-input-number | int/decimal | 数字输入，支持min/max/step |
| amount | AmountInput | decimal(18,4) | 金额专用，千分位+币种符号 |
| date | el-date-picker(type=date) | date | 日期选择 |
| datetime | el-date-picker(type=datetime) | timestamp | 日期时间选择 |
| time | el-time-picker | time | 时间选择 |
| select | el-select | varchar/int | 固定选项下拉 |
| cascader | el-cascader | varchar/int | 级联下拉（如地区/分类） |
| switch | el-switch | boolean | 布尔开关 |
| checkbox | el-checkbox-group | varchar/jsonb | 多选复选框组 |
| radio | el-radio-group | varchar/int | 单选按钮组 |
| upload | el-upload | varchar | 文件上传（返回URL） |
| image | ImageUpload | varchar | 图片上传+预览 |
| richtext | TinyMCE | text | 富文本编辑器（与设计文档2.1.1选型TinyMCE Vue3 7.x一致） |
| dict | DictSelect | varchar | 字典数据源下拉 |
| tree_select | el-tree-select | bigint | 树形下拉选择（如上级分类/父级科目） |
| dialog_select | DialogSelect | bigint | 弹窗选择器（如客户/供应商/商品） |
| region | RegionCascader | varchar | 省市区三级联动 |
| script | ScriptSelect | varchar | 话术模板选择 |
| password | el-input(type=password) | varchar | 密码输入 |
| email | el-input(type=email) | varchar | 邮箱输入（含格式校验） |
| phone | el-input + 校验 | varchar | 手机号输入（含格式校验） |
| color | el-color-picker | varchar | 颜色选择器 |
| icon | IconSelector | varchar | 图标选择器 |
| json | JsonEditor | jsonb | JSON编辑器 |

> **使用规则**：每个字段的field_type在数据视图字段配置表(sys_data_view_field)和字段权限配置中统一管理。前端渲染时通过Composable函数`useFieldRender(fieldType)`获取对应组件。新增组件类型需同时更新此表和P0-005-010的录入组件注册表。

### 0.5.4 7大体系业务选择器规范
| 选择器 | 功能 | 返回数据 |
|--------|------|---------|
| CustomerSelector | 客户选择器 | 客户ID+名称+编码 |
| SupplierSelector | 供应商选择器 | 供应商ID+名称+编码 |
| ProductSelector | 商品选择器 | 商品ID+名称+编码+规格+单位 |
| WarehouseSelector | 仓库选择器 | 仓库ID+名称+编码 |
| EmployeeSelector | 员工选择器 | 员工ID+姓名+编码 |
| DepartmentSelector | 部门选择器 | 部门ID+名称+编码 |
| AccountSelector | 结算账户选择器 | 账户ID+名称+编码 |

---

## 0.6 权限体系开发规范

### 0.6.1 权限四维模型
| 维度 | 说明 | 实现方式 |
|------|------|---------|
| 功能权限 | 菜单+按钮级 | 基于RBAC角色授权 |
| 数据权限 | 行级数据过滤 | 基于数据权限方案（本人/本部门/本部门及下属/全部） |
| 字段权限 | 字段级可见/可编辑/隐藏 | 基于字段权限方案 |
| API权限 | 接口级访问控制 | @SaCheckPermission注解 |

### 0.6.2 角色继承与互斥角色
- 角色继承树：子角色自动拥有父角色权限
- 互斥角色约束：同一用户不可同时拥有互斥角色
- 互斥示例：采购员与审批人互斥、出纳与会计互斥、制单人与审核人互斥

### 0.6.3 数据权限方案配置
- 数据权限方案可复用于多角色
- 方案维度：本人/本部门/本部门及下属/自定义/全部
- 方案到角色绑定（多对多）
- SQL注入拦截：自动在查询SQL中追加数据范围条件

### 0.6.4 字段权限方案配置
- 字段权限方案：按角色控制字段可见/可编辑/隐藏
- 方案到角色+字段到权限映射
- 前端根据字段权限控制UI组件显隐/禁用

---

## 0.7 通用引擎开发规范

### 0.7.1 编码引擎规范
| 规则项 | 说明 |
|--------|------|
| 编码规则 | 前缀+日期段+序号段+自定义段 |
| 流水号重置规则 | 按年/月/日/不重置 |
| 编码预览 | 新建单据时实时预览编码 |
| 编码唯一性保证 | Redis原子递增或数据库行级锁 |
| 并发控制 | SELECT ... FOR UPDATE |
| 编码回退规则 | 删除单据不回退序号 |

### 0.7.2 审核引擎规范
| 状态值 | 状态名称 | 允许操作 |
|--------|---------|---------|
| 0 | 草稿 | 编辑、删除、提交 |
| 1 | 待审 | 撤回 |
| 2 | 审核中 | - |
| 3 | 已审核 | 反审（需权限）、下推 |
| 4 | 已关闭 | - |
| 5 | 已作废 | 撤销作废 |

### 0.7.3 流转引擎规范
| 功能 | 说明 |
|------|------|
| 下推定义 | 源单 -> 目标单映射配置 |
| 字段映射规则 | 含辅助属性映射 |
| 数量字段映射 | 源单数量 -> 目标单数量 |
| 下推前校验规则 | 状态/数量/关联校验 |
| 下推后源单状态更新 | 更新已下推数量/状态 |
| 下推关系记录 | 可追溯 |
| 反审核时关联下推单检查 | 存在下推单则不允许反审 |

### 0.7.4 审批引擎规范
| 审批人类型 | 说明 |
|-----------|------|
| 指定人 | 指定具体用户 |
| 角色 | 指定角色内任意用户 |
| 部门主管 | 发起人所在部门主管 |
| 发起人上级 | 发起人直属上级 |
| 表单字段值 | 表单中用户字段 |

**会签/或签规则**：
- 会签：所有审批人都通过才通过
- 或签：任一审批人通过即通过
- 会签通过率可配置

---

## 0.8 系统参数配置规范

### 0.8.1 参数读取优先级规则
```
模块级总开关(如inv.batch_manage) → 商品控制策略(商品级开关) → 单据级参数
```

### 0.8.2 数值精度参数优先级规则
```
模块级参数(sale.decimal_places_price) → 全局默认参数(system.decimal_places_price)
```
- 若模块级参数未配置（为空），则使用全局默认参数
- 所有精度参数值上限为8，超过8时自动截断为8
- 数据库物理字段统一使用decimal(18,8)

### 0.8.3 系统预置参数清单（必须实现）
**库存管理参数（category=inventory）**：
| 参数键 | 参数名称 | 默认值 | 说明 |
|--------|---------|--------|------|
| inv.batch_manage | 是否启用批次管理 | true | 批次管理总开关 |
| inv.serial_manage | 是否启用序列号管理 | true | 序列号管理总开关 |
| inv.negative_stock | 是否允许负库存 | false | 出库校验库存 |
| inv.auto_stock_out | 销售出库是否允许直接新建 | true | |
| inv.auto_stock_in | 采购入库是否允许直接新建 | true | |
| inv.cost_method | 库存成本核算方法 | weighted_avg | weighted_avg/fifo/manual |
| inv.sync_cost_on_stock_in | 入库时是否同步更新成本 | true | |

**CRM管理参数（category=crm）**：
| 参数键 | 参数名称 | 默认值 | 说明 |
|--------|---------|--------|------|
| crm.customer_name_unique | 客户名称是否允许重复 | false | |
| crm.customer_code_auto | 客户编码是否自动生成 | true | |
| crm.opportunity_required | 是否强制关联商机 | false | |

**销售管理参数（category=sale）**：
| 参数键 | 参数名称 | 默认值 | 说明 |
|--------|---------|--------|------|
| sale.auto_confirm | 销售订单是否自动确认 | false | |
| sale.stock_deduct_mode | 销售库存扣减时机 | on_stock_out | on_order/on_stock_out/on_ship |
| sale.price_allow_modify | 是否允许修改销售价格 | true | |
| sale.decimal_places_price | 销售单价小数位数 | 4 | 最高8位 |
| sale.decimal_places_qty | 销售数量小数位数 | 6 | 最高8位 |

**财务管理参数（category=finance）**：
| 参数键 | 参数名称 | 默认值 | 说明 |
|--------|---------|--------|------|
| finance.auto_voucher | 单据审核是否自动生成凭证 | false | |
| finance.ar_source | 应收款产生依据 | sale_order | sale_order/stock_out/stock_out_reconcile |
| finance.ap_source | 应付款产生依据 | purchase_order | purchase_order/stock_in/stock_in_reconcile |
| finance.fiscal_year_start | 会计年度起始月 | 1 | 1-12 |

**审批流程参数（category=approval）**：
| 参数键 | 参数名称 | 默认值 | 说明 |
|--------|---------|--------|------|
| approval.enabled | 是否启用审批流程 | true | |
| approval.allow_withdraw | 是否允许撤回已提交审批 | true | |

---

## 0.9 菜单与路由配置规范

### 0.9.1 菜单类型
| 类型 | 说明 |
|------|------|
| 目录 | 仅作为分组，不关联页面 |
| 菜单 | 关联具体页面，点击打开页面 |
| 按钮 | 页面内操作按钮权限 |

### 0.9.2 页面打开形式
| 形式 | 说明 |
|------|------|
| 全局标签页 | 在主布局区以标签页形式打开 |
| 弹窗(小) | 400px宽度弹窗 |
| 弹窗(中) | 600px宽度弹窗 |
| 弹窗(大) | 800px宽度弹窗 |
| 抽屉 | 右侧抽屉形式 |
| 新窗口 | 浏览器新窗口打开 |

### 0.9.3 菜单权限标识规范
```
模块:资源:操作
示例：sale:order:add（销售订单新增）
示例：sale:order:edit（销售订单编辑）
示例：sale:order:delete（销售订单删除）
示例：sale:order:audit（销售订单审核）
```

---

## 0.10 前端开发约束清单

### 0.10.1 Hydration错误预防
- 严禁在JSX渲染逻辑中直接使用typeof window、Date.now()、Math.random()
- 必须使用'use client'并配合useEffect + useState确保动态内容仅在客户端挂载后渲染
- 严禁非法HTML嵌套（如`<p>`嵌套`<div>`）

### 0.10.2 Web性能优化规范
- 使用CSS class切换代替逐属性修改样式
- 使用transform/opacity做动画，不用left/top/width/height
- DOM读写分离，避免布局抖动
- 频繁变化元素加will-change: transform
- 动画用requestAnimationFrame
- 首屏LCP图片加fetchpriority="high"，禁止懒加载
- 非首屏图片加loading="lazy"
- 长列表使用虚拟滚动

### 0.10.3 外部资源域名适配
- 中国大陆环境使用官方.cn域
- Google Fonts CSS使用fonts.googleapis.cn

---

## 0.11 字段配置规则约束（对应设计文档第十章）

> 所有业务字段的可视化配置能力均须遵守以下规则。字段配置按录入场景分为**表单型**与**表格型**两大类，通用规则两者共享。

### 0.11.1 通用配置规则（表单型与表格型共用）

| 规则类别 | 核心约束 |
|---------|---------|
| 基础标识 | 字段显示名称（支持多语言）、字段编码（前端唯一标识）、数据库字段名（后端映射）、显示排序、分组归属、placeholder、帮助提示、对齐方式（数值右对齐/其余左对齐）、状态显隐（新增/编辑/详情分别配置）、隐藏字段（后台静默存值前端不展示） |
| 录入权限与校验 | 必填（前端+后端双重）、条件必填（联动规则）、最大/最小长度、数值最大/最小值、小数位数、自定义正则、跨字段联合校验（如结束日期≥开始日期）、唯一性校验、禁止编辑（按状态条件）、字段级权限（可见/可编辑/隐藏）、数据录入范围权限 |
| 数据源绑定 | 绑定组件类型（8.1-8.4已封装组件）、字典编码绑定、数据过滤条件、默认排序、严格字典匹配/非严格自由录入、多选最大数量限制、编码规则绑定（sys_code_rule关联） |
| 默认值与自动赋值 | 固定静态默认值、登录人信息默认值（操作员/部门/仓库/公司）、系统日期/时间自动赋值、单据编号自动生成、引用其他字段值、条件默认值 |
| 联动带出 | 选择后自动带出目标字段列表、带出字段映射关系（源→目标）、覆盖策略（始终覆盖/仅空值填充/用户确认后覆盖）、多级联动、跨层级联动（主表→明细行带出；明细行→主表反向带出**仅在明细行只有一行时生效**，多行时不触发反向带出） |
| 自动计算 | 开启/关闭开关、触发条件（依赖字段值变更时）、公式配置（四则运算+业务函数：求和/取整/四舍五入/税率/折扣）、结果小数位数、计算后锁定、反向计算（改金额反算单价）、触发时机（即时/失焦/提交时）、手动修改后策略（重算覆盖/保留手动值）、循环计算防护（依赖有向图检测环路） |
| 显隐联动 | 值等于/不等于指定值显隐、多条件组合（AND/OR）、状态联动（审核后隐藏）、值保留策略（隐藏时清空/保留）、显隐与必填优先级（隐藏时自动豁免必填）、隐藏字段提交规则（不随表单提交，除非显式勾选"隐藏字段参与提交"） |
| 数据联动过滤 | A字段选择值→B字段数据源自动过滤、跨层级过滤（主表选仓库→明细只显示该仓库库位）、多字段组合过滤（AND/OR） |
| 单据流转专属 | 引入时是否自动带入、下推时是否自动携带、字段映射规则（支持一对多）、是否允许覆盖已有数据、流转中是否禁止修改 |
| 移动端适配 | 移动端是否显示、组件简化展示模式 |
| 日志与追溯 | 字段值变更是否记录操作日志、是否允许查看修改历史 |

### 0.11.2 表单型录入字段专属配置

| 规则类别 | 核心约束 |
|---------|---------|
| 表单布局 | 栅格占位列数（1/2/3/4列）、标签宽度（自适应/固定值）、标签位置（顶部/左侧/右侧）、是否独占一行、分组折叠（默认折叠/展开） |
| 表单状态联动 | 新增状态行为（可编辑/只读/隐藏）、编辑状态行为、详情状态（统一只读）、审核后字段锁定（审核通过→指定字段只读→反审恢复）、作废后全部字段锁定 |
| 表单增强校验 | 条件必填联动、跨字段联合校验、后端异步校验、校验失败提示方式（红字/弹窗/行内图标） |
| 表单专属交互 | 字段变更事件触发、表单字段与明细表格联动、字段值变更确认、字段清空联动 |

### 0.11.3 表格型录入字段专属配置

| 规则类别 | 核心约束 |
|---------|---------|
| 列布局与显示 | 列宽度（固定/最小/自适应）、列固定（左固定/右固定/不固定）、单元格格式化（数值千分位+小数位+货币符号、百分比%、日期自定义格式、布尔图标/文字）、超出处理（省略+Tooltip/自动换行）、列标题说明提示 |
| 单元格编辑控制 | 单元格级只读条件、编辑模式（行内/弹窗/下拉）、编辑触发方式（单击/双击/F2）、空值展示文案 |
| 行级操作与校验 | 新增行默认赋值、行复制新增、行删除前置校验、行级必填校验、行级整行校验、行级编辑权限控制 |
| 联动带出扩展 | 同行联动带出、带出触发行级计算、带出值与行状态联动 |
| 自动计算扩展 | 行级计算（当前行范围）、列级汇总（当前列所有行）、跨行计算、主表与明细表格汇总联动 |
| 统计汇总 | 是否参与合计、合计方式（求和/平均/最大/最小/计数）、条件合计、合计展示别名、合计行位置（底部固定/非固定）、多级合计（分组小计+总计） |
| 单元格合并与样式 | 单元格合并规则开关、条件样式、悬浮提示文案 |

---
---

# 第一章 优先级定义与开发顺序规范

## 1.1 优先级体系定义

| 优先级 | 含义 | 判定标准 | 示例 |
|--------|------|---------|------|
| **P0** | 核心必做 | 系统骨架、公共基础设施、核心基础数据模块；无此前置则后续全部业务无法运行 | 框架搭建、数据库建表、认证权限、公共组件、组织架构、商品管理、客户管理、供应商管理、仓库管理、财务基础、HRM |
| **P1** | 重要 | 核心业务链路模块；构成ERP主业务闭环的关键环节 | 审批流程、消息管理、销售管理、采购管理、库存管理、生产管理、委外管理、应收管理、应付管理、凭证管理、账簿查询、财务报表、成本核算 |
| **P2** | 常规 | 扩展业务、辅助功能、优化体验模块；在核心链路跑通后逐步补充 | 费用管理、固定资产、期末处理、样品/借用/租赁/售后、OA办公、AI助手、模板中心、报表中心、移动端、期初管理、系统管理完善、运维、多语言、首页仪表盘 |

## 1.2 任务编码规范

- **格式**：`{优先级}-{一级序号}-{二级序号}-{三级序号}-...`
- **层级无限延伸**：支持 `P0-001`、`P0-001-001`、`P0-001-001-001`、`P0-001-001-001-001` 等无限层级细分
- **编号唯一**：每个任务编号全局唯一，不可复用
- **边界清晰**：每个最末级任务为一个最小不可拆分的开发执行单元，AI单次可独立完成

## 1.3 开发顺序核心原则

| 原则 | 说明 |
|------|------|
| 依赖前置 | 被依赖的模块必须先开发，依赖方必须后开发 |
| 先基础后业务 | 先完成框架、数据库、认证、公共组件，再做业务模块 |
| 先底层后上层 | 先完成后端接口/服务层，再完成前端页面/展示层 |
| 先公共后业务 | 先完成通用引擎（编码引擎/审核引擎/流转引擎），再做业务单据 |
| 先通用后定制 | 先完成通用组件/通用功能，再做各模块定制化业务 |
| 后端先行 | 每个模块内部，后端（建表→Entity→Mapper→Service→Controller）先于前端（页面→组件→交互） |

## 1.4 单模块开发内部顺序规范

每个业务模块的开发严格按以下顺序执行：

```
1. 数据库建表（CREATE TABLE + 索引 + 约束）
2. 后端Entity/DTO/VO定义
3. 后端Mapper层（MyBatis-Plus Mapper + XML映射）
4. 后端Service层（业务逻辑 + 校验 + 联动 + 计算）
5. 后端Controller层（RESTful API接口）
6. 后端单元可运行验证（接口可调通）
7. 前端页面路由注册 + 菜单数据配置
8. 前端页面开发（基于页面类型基座组件 + 字段配置渲染）
9. 前后端联调验证
```

## 1.5 任务颗粒度标准

最末级任务（叶子节点）必须满足以下全部条件：

1. **单次可完成**：AI在单次执行上下文中可独立完成，无需跨上下文
2. **边界明确**：有清晰的输入（依赖项）和输出（交付物）
3. **可验证**：有明确的验证标准（接口可调通/页面可渲染/功能可操作）
4. **无交叉**：与其他叶子任务无功能重叠或逻辑耦合
5. **原子性**：不可进一步拆分为更小的有意义的开发单元

---

### 1.6 四级任务标准拆分规则

为适配AI单次执行容量限制，所有三级任务按以下标准规则拆分为四级叶子节点：

| 三级任务类型 | 四级拆分规则 | 示例 |
|---|---|---|
| **后端CRUD接口** | 001-Entity+Mapper定义 → 002-Service层业务逻辑 → 003-Controller层接口 → 004-接口自测验证 | P1-005-001-001 → P1-005-001-001-001~004 |
| **后端引擎/服务** | 001-核心逻辑实现 → 002-配置管理 → 003-集成对接 → 004-自测验证 | P1-007-001-001 → P1-007-001-001-001~004 |
| **后端聚合查询** | 001-SQL编写+Mapper → 002-Service聚合逻辑 → 003-Controller接口 → 004-自测验证 | P1-005-001-004 → P1-005-001-004-001~004 |
| **前端列表页** | 001-搜索条件区 → 002-数据表格区+分页 → 003-操作按钮区+批量操作 → 004-页面联调验证 | P1-005-002-001 → P1-005-002-001-001~004 |
| **前端主从列表页** | 001-主表搜索+列表区 → 002-从表标签页区 → 003-操作按钮+批量操作 → 004-页面联调验证 | P1-005-002-002 → P1-005-002-002-001~004 |
| **前端表单页** | 001-主表字段录入区 → 002-从表明细行编辑区 → 003-选择器对接+计算逻辑 → 004-表单联调验证 | P1-005-002-003 → P1-005-002-003-001~004 |
| **前端主从表单页** | 001-主表字段区 → 002-从表明细行区 → 003-库位/批次/序列号子表区(如有) → 004-选择器对接+金额计算 → 005-审核/流转按钮集成 → 006-表单联调验证 | P1-005-002-005 → P1-005-002-005-001~006 |
| **前端查询页** | 001-查询条件区 → 002-查询结果表格区 → 003-导出+操作按钮 → 004-页面联调验证 | P1-005-002-007 → P1-005-002-007-001~004 |
| **前端报表页** | 001-报表条件区 → 002-报表数据表格/图表区 → 003-导出+打印 → 004-页面联调验证 | P1-005-002-018 → P1-005-002-018-001~004 |
| **前端配置页** | 001-配置表单区 → 002-配置预览+校验 → 003-配置保存+生效 → 004-页面联调验证 | P1-002-002-001 → P1-002-002-001-001~004 |
| **前端设计器页** | 001-组件面板+画布区 → 002-属性配置面板 → 003-数据绑定+预览 → 004-保存+版本管理 → 005-设计器联调验证 | P2-010-002-004 → P2-010-002-004-001~005 |
| **建表任务** | 001-建表SQL编写 → 002-初始化数据SQL → 003-执行+验证 | P0-003-001 → P0-003-001-001~003 |
| **种子数据** | 001-种子数据JSON编写 → 002-种子数据导入接口 → 003-导入执行+验证 | P0-006-003-001 → P0-006-003-001-001~003 |

> **规则**：每个四级叶子节点应为一个AI单次可完成的最小执行单元，预计代码量50-200行，执行时间5-20分钟。若某个四级节点仍超出此范围，可在开发执行时进一步拆分。
>
> **模板选择规则（V8补充）**：
> - **"实现核心逻辑"模板**：适用于所有后端Service/Controller开发、前端页面开发（P01-P12/P14）、前端组件开发、聚合查询接口开发
> - **"编写配置项"模板**：仅适用于P13配置页和系统配置类（@Configuration）开发。判断标准：任务产出物是yml配置项或@Configuration类时使用此模板
> - **"编写Entity类"模板**：适用于Entity/DTO/VO定义任务
> - **"编写Mapper"模板**：适用于MyBatis-Plus Mapper接口与XML映射开发
> - **常见误用**：前端页面（P04/P06/P07/P02等）不是配置页，应使用"实现核心逻辑"模板，不可使用"编写配置项"模板

---

# 第二章 P0级任务分解（核心必做）

## P0-001 后端项目框架搭建

> **依赖**：无  
> **概述**：搭建Spring Boot + MyBatis-Plus后端项目骨架，配置全局基础设施

### P0-001-001 项目初始化与基础配置
- P0-001-001-001 Spring Boot项目创建
  - P0-001-001-001-001 Spring Initializr项目生成（JDK17 + Spring Boot 3.x + Maven）
    - P0-001-001-001-001-001 执行Spring Initializr生成项目（指定JDK17+Spring Boot 3.x+Maven+指定包名）
    - P0-001-001-001-001-002 验证项目可启动（mvn compile+启动main方法+端口监听+健康检查）
  - P0-001-001-001-002 项目包结构创建（controller/service/mapper/entity/dto/vo/config/common/enum包）
    - P0-001-001-001-002-001 创建基础包目录（controller/service/mapper/entity/dto/vo/config/common/enum）
    - P0-001-001-001-002-002 创建模块子包（按业务模块分子包+package-info.java+模块标准结构）
    - P0-001-001-001-002-003 验证包结构完整性（编译通过+无空包+所有模块可引用）
  - P0-001-001-001-003 主启动类配置（@SpringBootApplication + @MapperScan + @EnableScheduling）
    - P0-001-001-001-003-001 编写启动类main方法（@SpringBootApplication+@MapperScan+@EnableScheduling+组件扫描路径）
    - P0-001-001-001-003-002 配置启动参数（JVM参数+环境变量+profile激活+端口指定+时区设置）
- P0-001-001-002 pom.xml依赖配置
  - P0-001-001-002-001 核心依赖引入（MyBatis-Plus / Sa-Token / PostgreSQL驱动 / Lombok / Hutool）
    - P0-001-001-002-001-001 添加核心依赖坐标（MyBatis-Plus/Sa-Token/PostgreSQL/Lombok/Hutool+版本号指定）
    - P0-001-001-002-001-002 验证依赖可用（mvn compile+项目启动+核心Bean注入成功）
  - P0-001-001-002-002 开发工具依赖引入（Knife4j / Spring Boot DevTools / Spring Boot Actuator）
    - P0-001-001-002-002-001 添加开发工具依赖坐标（Knife4j/DevTools/Actuator+scope=runtime/test标记）
    - P0-001-001-002-002-002 配置开发工具参数（Knife4j增强/DevTools热部署/Actuator端点暴露）
    - P0-001-001-002-002-003 验证工具可用（Swagger UI访问/热部署生效/Actuator端点返回数据）
  - P0-001-001-002-003 依赖版本统一管理（dependencyManagement + 版本属性变量）
    - P0-001-001-002-003-001 定义dependencyManagement区块（BOM引入+版本属性变量<properties>）
    - P0-001-001-002-003-002 统一各依赖版本号（Spring Boot/MyBatis-Plus/Sa-Token等核心依赖版本锁定）
    - P0-001-001-002-003-003 验证依赖冲突（mvn dependency:tree+冲突解决+版本一致性检查）
- P0-001-001-003 application.yml多环境配置
  - P0-001-001-003-001 application-dev.yml开发环境配置（数据库连接/Redis连接/服务端口8080/日志DEBUG）
    - P0-001-001-003-001-001 编写dev环境配置（PostgreSQL连接+Redis连接+端口8080+日志DEBUG+热部署开关）
    - P0-001-001-003-001-002 验证配置生效（启动dev profile+数据库连接+Redis连接+日志级别确认）
    - P0-001-001-003-001-003 配置安全项检查（敏感信息环境变量化+非硬编码密码+连接池参数合理性）
  - P0-001-001-003-001-004 application-test.yml测试环境配置（独立测试数据库/Redis连接/独立端口8081/日志DEBUG/自动化测试数据源）
    - P0-001-001-003-001-004-001 编写test环境配置（独立测试数据库连接+独立Redis db1+端口8081+日志DEBUG+测试profile激活）
    - P0-001-001-003-001-004-002 验证test环境配置（启动test profile+测试数据库连接+Redis隔离验证+测试数据初始化）
  - P0-001-001-003-001-005 application-staging.yml预生产环境配置（性能测试数据库/Redis连接/端口8082/日志INFO/监控端点暴露）
    - P0-001-001-003-001-005-001 编写staging环境配置（预生产数据库连接池+Redis连接+端口8082+日志INFO+Actuator端点全开）
    - P0-001-001-003-001-005-002 验证staging环境配置（启动staging profile+数据库连接验证+监控端点可访问+性能基线确认）
  - P0-001-001-003-002 application-prod.yml生产环境配置（数据库连接池优化/Redis连接/服务端口/日志INFO）
    - P0-001-001-003-002-001 编写prod环境配置（连接池优化+Redis连接+端口+日志INFO+安全加固）
    - P0-001-001-003-002-002 验证配置生效（启动prod profile+生产参数验证+性能指标确认）
    - P0-001-001-003-002-003 配置安全加固（密码环境变量+SSL配置+Actuator端点限制+超时设置）
  - P0-001-001-003-003 公共配置抽取（application.yml含MyBatis-Plus/Sa-Token/文件上传/编码规则通用配置）
    - P0-001-001-003-003-001 编写公共配置项（MyBatis-Plus全局+Sa-Token+文件上传+编码规则+分页默认值）
      - P0-001-001-003-003-001-001 配置MyBatis-Plus全局项（逻辑删除字段+自动填充+分页插件+乐观锁插件）
      - P0-001-001-003-003-001-002 配置Sa-Token全局项（Token有效期+活动刷新+并发登录策略+排他登录）
      - P0-001-001-003-003-001-003 配置文件上传+编码规则+分页默认值（上传大小限制+编码缓存+分页默认20条）
    - P0-001-001-003-003-002 抽取公共配置到application.yml（多环境共享+各profile仅覆盖差异项）
    - P0-001-001-003-003-003 验证配置继承正确（dev/prod继承公共配置+各环境差异生效+无遗漏项）
- P0-001-001-004 日志框架配置
  - P0-001-001-004-001 logback-spring.xml配置（控制台输出+按模块分文件输出+滚动策略+保留天数）
    - P0-001-001-004-001-001 编写logback-spring.xml（console appender+file appender+按模块分文件+滚动策略+保留天数）
    - P0-001-001-004-001-002 配置日志格式与输出（JSON格式+请求追踪ID+业务模块标记+异常堆栈完整输出）
    - P0-001-001-004-001-003 验证日志输出（各模块日志文件生成+滚动策略生效+磁盘空间预估）
  - P0-001-001-004-002 日志级别配置（开发环境DEBUG/生产环境INFO/第三方框架WARN）
    - P0-001-001-004-002-001 配置springProfile日志级别（dev=DEBUG+prod=INFO+第三方框架=WARN+SQL日志控制）
    - P0-001-001-004-002-002 配置动态日志级别（Actuator端点+运行时调整+业务包精细级别控制）
    - P0-001-001-004-002-003 验证日志级别生效（各环境级别确认+业务日志可见+框架日志不干扰）
- P0-001-001-005 MyBatis-Plus全局配置
  - P0-001-001-005-001 MybatisPlusConfig配置类（分页插件+自动填充处理器+逻辑删除全局值+乐观锁插件）
    - P0-001-001-005-001-001 定义@Configuration配置类+@Bean注册方式
    - P0-001-001-005-001-002 配置属性绑定（@ConfigurationProperties+yaml属性映射+默认值）
    - P0-001-001-005-001-003 配置校验与启动检查（参数合法性校验+启动时必要配置检查）
  - P0-001-001-005-002 自动填充处理器实现（MetaObjectHandler：创建时间/更新时间/创建人/更新人自动填充）
    - P0-001-001-005-002-001 实现insertFill方法（创建时间LocalDateTime+创建人userId+逻辑删除默认值+乐观锁版本）
    - P0-001-001-005-002-002 实现updateFill方法（更新时间+更新人+字段set策略：仅null时填充）
    - P0-001-001-005-002-003 验证自动填充（新增/修改操作+字段自动赋值+时间精度确认+用户ID获取正确）

### P0-001-002 全局异常处理与统一响应
- P0-001-002-001 全局异常处理器
  - P0-001-002-001-001 错误码枚举定义（BusinessException/ParamException/AuthException等错误码+错误消息）
    - P0-001-002-001-001-001 定义错误码枚举类（code+message+各业务模块错误码常量）
    - P0-001-002-001-001-002 定义错误码分类体系（系统级/参数级/业务级/认证级分组）
  - P0-001-002-001-002 自定义异常类开发（BusinessException/ParamException/AuthException/ForbiddenException）
    - P0-001-002-001-002-001 定义异常类（extends RuntimeException+错误码+消息+构造方法）
    - P0-001-002-001-002-002 实现自定义异常类异常处理逻辑（捕获+分类+响应包装+日志记录）
  - P0-001-002-001-003 全局异常处理器开发（@RestControllerAdvice + 各类异常捕获 + 统一R响应包装）
    - P0-001-002-001-003-001 定义Controller类+@RequestMapping路径+注入Service
    - P0-001-002-001-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
    - P0-001-002-001-003-003 补充接口文档注解（@Operation+@Schema+示例值）
- P0-001-002-002 统一响应包装类
  - P0-001-002-002-001 R<T>统一响应类开发（code/msg/data + 静态工厂方法ok/fail/error + 常用快捷方法）
    - P0-001-002-002-001-001 定义泛型类结构（code/msg/data+泛型T+序列化）
    - P0-001-002-002-001-002 实现静态工厂方法（ok/fail/error+链式调用）
    - P0-001-002-002-001-003 编写单元测试（成功/失败/空数据/分页响应验证）
  - P0-001-002-002-002 分页响应类开发（PageResult<T>含total/records/pageNum/pageSize）
    - P0-001-002-002-002-001 定义泛型类结构（code/msg/data+泛型T+序列化）
    - P0-001-002-002-002-002 实现静态工厂方法（ok/fail/error+链式调用）
    - P0-001-002-002-002-003 编写单元测试（成功/失败/空数据/分页响应验证）
- P0-001-002-003 参数校验框架集成
  - P0-001-002-003-001 Hibernate Validator依赖引入与全局校验处理器（MethodArgumentNotValidException捕获+字段级错误信息提取）
    - P0-001-002-003-001-001 引入Hibernate Validator依赖+配置MethodArgumentNotValidException全局捕获处理器
    - P0-001-002-003-001-002 验证字段级校验（@NotBlank/@NotNull/@Size等注解生效+错误信息字段级返回）
  - P0-001-002-003-002 自定义校验注解开发（@DictValue字典值校验/@Phone手机号校验/@IdCard身份证校验）
    - P0-001-002-003-002-001 定义注解元数据（@Target+@Retention+属性声明+默认值）
    - P0-001-002-003-002-002 实现注解处理器（AOP切面/BeanPostProcessor+注解解析+逻辑执行）

### P0-001-003 数据库连接与基础DAO层
- P0-001-003-001 PostgreSQL数据源配置
  - P0-001-003-001-001 HikariCP连接池配置（最小/最大连接数/连接超时/空闲超时/最大生命周期）
    - P0-001-003-001-001-001 定义@Configuration配置类+@Bean注册方式
    - P0-001-003-001-001-002 配置属性绑定（@ConfigurationProperties+yaml属性映射+默认值）
    - P0-001-003-001-001-003 配置校验与启动检查（参数合法性校验+启动时必要配置检查）
  - P0-001-003-001-002 多数据源支持预留（AbstractRoutingDataSource + DynamicDataSource注解占位）
    - P0-001-003-001-002-001 定义注解元数据（@Target+@Retention+属性声明+默认值）
    - P0-001-003-001-002-002 实现注解处理器（AOP切面/BeanPostProcessor+注解解析+逻辑执行）
- P0-001-003-002 MyBatis-Plus通用Mapper基类封装
  - P0-001-003-002-001 BaseMapperX开发（扩展BaseMapper：selectOneByQuery/selectListByQuery/selectPageByQuery/insertOrUpdateBatch）
    - P0-001-003-002-001-001 定义Mapper接口（extends BaseMapperX+@Mapper+自定义方法声明）
    - P0-001-003-002-001-002 编写XML映射文件（resultMap+自定义SQL片段+动态条件）
  - P0-001-003-002-002 通用查询条件构造器封装（QueryWrapperX含模糊查询/范围查询/排序快捷方法）
    - P0-001-003-002-002-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
    - P0-001-003-002-002-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
    - P0-001-003-002-002-003 实现通用查询条件构造查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P0-001-003-003 通用Service基类封装
  - P0-001-003-003-001 IServiceX开发（扩展IService：saveOrUpdateBatchExt/getPage/selectByCondition快捷方法）
    - P0-001-003-003-001-001 定义Service接口（extends IServiceX+业务方法声明）
    - P0-001-003-003-001-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
    - P0-001-003-003-001-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
  - P0-001-003-003-002 通用CRUD Service模板（标准CRUD+编码生成+审核状态流转+操作日志集成）
    - P0-001-003-003-002-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
    - P0-001-003-003-002-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
    - P0-001-003-003-002-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）

### P0-001-004 Sa-Token认证框架集成
- P0-001-004-001 Sa-Token核心依赖与配置
  - P0-001-004-001-001 Sa-Token依赖引入与sa-token.yml配置（Token风格/有效期/Activity超时/并发登录策略）
    - P0-001-004-001-001-001 定义@Configuration配置类+@Bean注册方式
    - P0-001-004-001-001-002 配置属性绑定（@ConfigurationProperties+yaml属性映射+默认值）
    - P0-001-004-001-001-003 配置校验与启动检查（参数合法性校验+启动时必要配置检查）
  - P0-001-004-001-002 StpInterface接口实现（获取用户权限列表+获取用户角色列表+缓存策略）
    - P0-001-004-001-002-001 定义接口路由与方法签名
    - P0-001-004-001-002-002 实现接口逻辑（参数处理+Service调用+响应包装）
- P0-001-004-002 Sa-Token拦截器注册
  - P0-001-004-002-001 登录校验拦截器注册（SaInterceptor + 排除路径配置/登录/验证码等公共接口）
    - P0-001-004-002-001-001 定义接口路由与方法签名
    - P0-001-004-002-001-002 实现接口逻辑（参数处理+Service调用+响应包装）
  - P0-001-004-002-002 权限校验注解开发（@RequiresPermission + @RequiresRole 自定义注解+AOP切面）
    - P0-001-004-002-002-001 定义切面/拦截器注册方式（@Aspect/@Component+切入点表达式）
    - P0-001-004-002-002-002 实现核心处理逻辑（前置/后置/环绕通知+业务处理+异常处理）
    - P0-001-004-002-002-003 集成测试验证（触发切面/拦截器+验证效果+异常场景）
- P0-001-004-003 Sa-Token与Redis集成
  - P0-001-004-003-001 sa-token-redis-jackson依赖引入与配置（Redis连接/序列化方式/Key前缀）
    - P0-001-004-003-001-001 定义@Configuration配置类+@Bean注册方式
    - P0-001-004-003-001-002 配置属性绑定（@ConfigurationProperties+yaml属性映射+默认值）
    - P0-001-004-003-001-003 配置校验与启动检查（参数合法性校验+启动时必要配置检查）
  - P0-001-004-003-002 会话管理服务开发（在线用户查询/强制下线/会话超时续期）
    - P0-001-004-003-002-001 实现在线用户查询（Sa-Token会话列表+分页+按用户名/IP/登录时间筛选）
    - P0-001-004-003-002-002 实现强制下线+会话超时续期（Sa-Token踢出会话+超时自动续期+并发安全）
    - P0-001-004-003-002-003 验证会话管理（查询在线用户+强制下线生效+会话超时续期+并发安全）

### P0-001-005 编码引擎基础服务
- P0-001-005-001 编码规则配置表建表
  - P0-001-005-001-001 sys_code_rule主表DDL（规则编码/规则名称/编码类型/当前序列号/重置周期/状态）
    - P0-001-005-001-001-001 编写CREATE TABLE sys_code_rule主语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-001-005-001-001-002 添加主键与索引约束
  - P0-001-005-001-002 sys_code_rule_segment从表DDL（规则ID/段序号/段类型/段值/固定值/日期格式/序列长度/起始值/步长）
    - P0-001-005-001-002-001 编写CREATE TABLE DDL（字段+类型+默认值+非空+注释）
    - P0-001-005-001-002-002 添加索引与约束
  - P0-001-005-001-003 索引与约束创建（规则编码唯一索引+从表外键+主表状态索引）
    - P0-001-005-001-003-001 编写CREATE INDEX语句（规则编码唯一索引+从表外键索引+主表状态索引）
    - P0-001-005-001-003-002 执行DDL脚本（CREATE INDEX+ALTER TABLE ADD CONSTRAINT+验证执行成功）
    - P0-001-005-001-003-003 验证索引与约束（唯一性约束生效+外键级联+查询性能提升验证）
- P0-001-005-002 编码规则Entity/Mapper/Service开发
  - P0-001-005-002-001 SysCodeRule + SysCodeRuleSegment Entity/DTO/VO定义
    - P0-001-005-002-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
    - P0-001-005-002-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
    - P0-001-005-002-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
  - P0-001-005-002-002 SysCodeRuleMapper + SysCodeRuleSegmentMapper开发（含主从联合查询）
    - P0-001-005-002-002-001 定义Mapper接口（extends BaseMapperX+自定义方法声明）
    - P0-001-005-002-002-002 编写XML映射（resultMap+主从联合查询SQL+动态条件）
    - P0-001-005-002-002-003 编写自定义查询方法（分页查询/条件查询/统计查询SQL）
  - P0-001-005-002-003 SysCodeRuleService开发（主从CRUD + 编码预览 + 编码生成调用入口）
    - P0-001-005-002-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
    - P0-001-005-002-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
    - P0-001-005-002-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P0-001-005-003 编码生成引擎核心逻辑
  - P0-001-005-003-001 段解析器开发（固定段/日期段/序列段/自定义变量段解析+拼接）
    - P0-001-005-003-001-001 实现固定段/日期段/序列段/自定义变量段解析器（段类型判断+参数提取+格式化输出）
    - P0-001-005-003-001-002 实现段解析器核心逻辑（段类型路由→固定段/日期段/序列段/自定义段分别解析→拼接格式化）
    - P0-001-005-003-001-003 验证各段解析（固定段原样输出+日期段格式化+序列段占位+自定义变量替换）
  - P0-001-005-003-002 序列号生成器开发（Redis INCR分布式自增+按重置周期自动重置+序列号补零+步长支持）
    - P0-001-005-003-002-001 实现Redis INCR分布式自增（按重置周期key设计+自增+补零格式化+步长支持）
    - P0-001-005-003-002-002 实现序列号自增逻辑（Redis INCRBY+按周期设计key+自增后补零+步长INCRBY+并发安全）
    - P0-001-005-003-002-003 验证序列号生成（并发安全+按周期重置+补零长度+步长递增正确）
  - P0-001-005-003-003 编码预览功能开发（根据规则配置预览下一个编码+不消耗序列号）
    - P0-001-005-003-003-001 实现编码预览逻辑（根据规则配置解析各段+拼接预览编码+不消耗序列号）
    - P0-001-005-003-003-002 实现编码预览核心逻辑（按规则解析各段→拼接预览编码→只读不消耗序列号→返回预览字符串）
    - P0-001-005-003-003-003 验证编码预览（预览不消耗序列号+格式正确+规则变更预览同步更新）
  - P0-001-005-003-004 编码生成功能开发（消耗序列号+返回已生成编码+并发安全保证）
    - P0-001-005-003-004-001 实现编码生成逻辑（消耗序列号+返回已生成编码+Redis事务保证并发安全+重复编码检测）
    - P0-001-005-003-004-002 实现编码生成核心逻辑（Redis事务INCR→消耗序列号→拼接编码→并发无重复→返回编码）
    - P0-001-005-003-004-003 验证编码生成（并发无重复+序列号递增+周期重置+编码格式正确）
- P0-001-005-004 编码规则管理Controller接口
  - P0-001-005-004-001 编码规则CRUD接口（POST/PUT/DELETE/GET + 主从保存/更新/删除）
    - P0-001-005-004-001-001 定义接口路由与方法签名（@PostMapping/@PutMapping/@DeleteMapping/@GetMapping）
    - P0-001-005-004-001-002 实现新增/修改/删除方法（参数接收+校验+Service调用+响应包装）
    - P0-001-005-004-001-003 实现查询方法（分页+条件+Service调用+响应包装）
  - P0-001-005-004-002 编码预览接口（GET /code-rule/preview?ruleCode=xxx）
    - P0-001-005-004-002-001 定义接口路由与方法签名
    - P0-001-005-004-002-002 实现接口逻辑（参数处理+Service调用+响应包装）
  - P0-001-005-004-003 编码生成接口（POST /code-rule/generate?ruleCode=xxx + 幂等性保证）
    - P0-001-005-004-003-001 定义接口路由与方法签名
    - P0-001-005-004-003-002 实现接口逻辑（参数处理+Service调用+响应包装）

### P0-001-006 数据视图引擎基础服务
- P0-001-006-001 数据视图配置表建表
  - P0-001-006-001-001 sys_data_view主表DDL（视图编码/视图名称/主表名/查询SQL模板/状态）
    - P0-001-006-001-001-001 编写CREATE TABLE sys_data_view主语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-001-006-001-001-002 添加主键与索引约束
  - P0-001-006-001-002 sys_data_view_field从表DDL（视图ID/字段名/字段标签/字段类型/查询方式/排序号/显隐）
    - P0-001-006-001-002-001 编写CREATE TABLE DDL（字段+类型+默认值+非空+注释）
    - P0-001-006-001-002-002 添加索引与约束
  - P0-001-006-001-003 索引与约束创建（视图编码唯一索引+从表外键）
    - P0-001-006-001-003-001 编写CREATE INDEX语句（视图编码唯一索引+从表外键索引）
    - P0-001-006-001-003-002 执行DDL脚本（CREATE INDEX+ALTER TABLE ADD CONSTRAINT+验证执行成功）
    - P0-001-006-001-003-003 验证索引与约束（唯一性+外键级联+查询性能）
- P0-001-006-002 数据视图Entity/Mapper/Service开发
  - P0-001-006-002-001 SysDataView + SysDataViewField Entity/DTO/VO定义
    - P0-001-006-002-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
    - P0-001-006-002-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
    - P0-001-006-002-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
  - P0-001-006-002-002 SysDataViewMapper + SysDataViewFieldMapper开发（含主从联合查询）
    - P0-001-006-002-002-001 定义Mapper接口（extends BaseMapperX+自定义方法声明）
    - P0-001-006-002-002-002 编写XML映射（resultMap+主从联合查询SQL+动态条件）
    - P0-001-006-002-002-003 编写自定义查询方法（分页查询/条件查询/统计查询SQL）
  - P0-001-006-002-003 SysDataViewService开发（主从CRUD + 动态查询调用入口）
    - P0-001-006-002-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
    - P0-001-006-002-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
    - P0-001-006-002-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P0-001-006-003 数据视图动态查询引擎
  - P0-001-006-003-001 SQL动态构建器开发（根据视图字段配置动态构建SELECT/FROM/JOIN/WHERE子句）
    - P0-001-006-003-001-001 实现SQL动态构建（解析视图字段配置→构建SELECT列+FROM主表+JOIN关联表+WHERE条件）
    - P0-001-006-003-001-002 实现SQL构建核心逻辑（解析视图字段→SELECT列→FROM主表→JOIN关联→WHERE条件拼接+参数化）
    - P0-001-006-003-001-003 验证SQL构建（单表/多表JOIN/子查询/条件组合/SQL注入防护）
  - P0-001-006-003-002 查询条件解析器开发（等于/模糊/范围/IN/日期区间等查询方式解析+参数绑定+SQL注入防护）
    - P0-001-006-003-002-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
    - P0-001-006-003-002-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
    - P0-001-006-003-002-003 实现查询条件解析器开查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
  - P0-001-006-003-003 分页排序执行器开发（动态ORDER BY+物理分页+总数查询+结果集映射）
    - P0-001-006-003-003-001 实现分页排序执行（动态ORDER BY+LIMIT/OFFSET物理分页+COUNT总数查询+结果集映射到VO）
    - P0-001-006-003-003-002 实现分页排序核心逻辑（动态ORDER BY+LIMIT/OFFSET分页+COUNT总数查询+结果集映射到VO）
    - P0-001-006-003-003-003 验证分页排序（分页参数正确+总数准确+排序字段有效+大数据量性能）
- P0-001-006-004 数据视图管理Controller接口
  - P0-001-006-004-001 视图CRUD接口（POST/PUT/DELETE/GET + 主从保存/更新/删除）
    - P0-001-006-004-001-001 定义接口路由与方法签名（@PostMapping/@PutMapping/@DeleteMapping/@GetMapping）
    - P0-001-006-004-001-002 实现新增/修改/删除方法（参数接收+校验+Service调用+响应包装）
    - P0-001-006-004-001-003 实现查询方法（分页+条件+Service调用+响应包装）
  - P0-001-006-004-002 动态查询接口（POST /data-view/query?viewCode=xxx + 查询条件JSON + 分页参数）
    - P0-001-006-004-002-001 定义接口路由与方法签名（@GetMapping+查询参数）
    - P0-001-006-004-002-002 实现查询逻辑（参数接收+Service调用+响应包装）

### P0-001-007 系统参数服务基础
- P0-001-007-001 系统参数表建表
  - P0-001-007-001-001 sys_param表DDL（参数编码/参数名称/参数值/参数类型/参数分类/排序号/状态/备注）
    - P0-001-007-001-001-001 编写CREATE TABLE DDL（字段+类型+默认值+非空+注释）
    - P0-001-007-001-001-002 添加索引与约束
  - P0-001-007-001-002 索引与约束创建（参数编码唯一索引+分类索引）
    - P0-001-007-001-002-001 编写CREATE INDEX语句（参数编码唯一索引+分类索引）
    - P0-001-007-001-002-002 执行DDL脚本（CREATE INDEX参数编码唯一+分类索引+验证执行成功）
    - P0-001-007-001-002-003 验证索引与约束（唯一性+分类查询性能+约束生效）
- P0-001-007-002 SysParamService开发
  - P0-001-007-002-001 SysParam Entity/Mapper/Service开发（基础CRUD + 参数缓存Map+自动刷新）
    - P0-001-007-002-001-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
    - P0-001-007-002-001-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
    - P0-001-007-002-001-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
  - P0-001-007-002-002 参数类型转换方法（getParamValueAsString/getParamValueAsBoolean/getParamValueAsInt/getParamValueAsLong）
    - P0-001-007-002-002-001 实现基础类型转换方法（getString/getBoolean/getInt/getLong+空值默认+类型转换异常处理）
    - P0-001-007-002-002-002 实现高级转换方法（getBigDecimal/getLocalDate/getList+格式校验+异常提示）
  - P0-001-007-002-003 参数缓存刷新机制（@PostConstruct初始化加载+Redis发布订阅缓存同步+手动刷新接口）
    - P0-001-007-002-003-001 定义接口路由与方法签名
    - P0-001-007-002-003-002 实现接口逻辑（参数处理+Service调用+响应包装）
- P0-001-007-003 系统参数Controller接口
  - P0-001-007-003-001 参数按分类查询接口（GET /param/list-by-group?group=xxx）
    - P0-001-007-003-001-001 定义接口路由与方法签名（@GetMapping+查询参数）
    - P0-001-007-003-001-002 实现查询逻辑（参数接收+Service调用+响应包装）
  - P0-001-007-003-002 参数批量更新接口（PUT /param/batch-update + 参数值校验+缓存自动刷新）
    - P0-001-007-003-002-001 定义接口路由与方法签名
    - P0-001-007-003-002-002 实现接口逻辑（参数处理+Service调用+响应包装）

### P0-001-008 公共基础服务
- P0-001-008-001 通用文件上传/下载服务
  - P0-001-008-001-001 文件上传服务开发（本地存储+对象存储双模式+文件大小限制+类型限制+文件MD5校验）
    - P0-001-008-001-001-001 实现文件上传核心逻辑（MultipartFile接收+大小校验+类型白名单+MD5去重+本地/对象存储双写）
    - P0-001-008-001-001-002 实现文件上传核心逻辑（MultipartFile接收→大小校验→类型白名单→MD5计算→本地/对象存储双写）
    - P0-001-008-001-001-003 验证文件上传（大小超限拒绝+类型拒绝+MD5去重+存储路径正确+数据库记录创建）
  - P0-001-008-001-002 文件下载服务开发（根据文件ID/路径下载+断点续传支持+下载权限校验）
    - P0-001-008-001-002-001 实现文件下载逻辑（根据文件ID查询路径+权限校验+流式输出+断点续传Range支持）
    - P0-001-008-001-002-002 实现文件下载核心逻辑（文件ID查路径→权限校验→流式输出→断点续传Range支持→文件名编码）
    - P0-001-008-001-002-003 验证文件下载（权限校验+大文件断点续传+文件名编码+Content-Type正确）
  - P0-001-008-001-003 文件预览服务开发（图片/PDF在线预览+Office文档在线预览占位）
    - P0-001-008-001-003-001 实现文件预览逻辑（图片直接输出+PDF内嵌预览+Office文档占位提示+预览权限校验）
    - P0-001-008-001-003-002 实现文件预览核心逻辑（图片缩略图输出→PDF内嵌→Office占位→预览权限校验→Content-Type设置）
    - P0-001-008-001-003-003 验证文件预览（图片缩略图+PDF内嵌+Office占位+无权限拒绝访问）
- P0-001-008-002 操作日志AOP切面
  - P0-001-008-002-001 @OperLog注解定义（模块/操作类型/描述/是否保存请求参数/是否保存响应参数）
    - P0-001-008-002-001-001 定义注解元数据（@Target+@Retention+属性声明+默认值）
    - P0-001-008-002-001-002 实现注解处理器（AOP切面/BeanPostProcessor+注解解析+逻辑执行）
  - P0-001-008-002-002 OperLogAspect切面开发（@AfterReturning/@AfterThrowing + 异步记录到sys_operation_log）
    - P0-001-008-002-002-001 定义切面/拦截器注册方式（@Aspect/@Component+切入点表达式）
    - P0-001-008-002-002-002 实现核心处理逻辑（前置/后置/环绕通知+业务处理+异常处理）
    - P0-001-008-002-002-003 集成测试验证（触发切面/拦截器+验证效果+异常场景）
  - P0-001-008-002-003 操作日志查询接口（条件查询+分页+操作详情查看）
    - P0-001-008-002-003-001 定义接口路由与方法签名（@GetMapping+查询参数）
    - P0-001-008-002-003-002 实现查询逻辑（参数接收+Service调用+响应包装）
- P0-001-008-003 通用导入/导出服务
  - P0-001-008-003-001 EasyExcel依赖引入与通用导出工具类（动态表头+数据行写入+样式配置+合并单元格）
    - P0-001-008-003-001-001 实现导出逻辑（查询条件+API+文件流+文件名处理）
    - P0-001-008-003-001-002 导出异常处理（大数据量分页+超时+失败重试提示）
  - P0-001-008-003-002 通用导入工具类（模板下载+文件上传+数据校验+错误数据导出+导入结果汇总）
    - P0-001-008-003-002-001 实现导出功能（API+文件流下载+动态文件名+进度提示）
    - P0-001-008-003-002-002 实现导入功能（上传+API+结果反馈+错误数据下载）
  - P0-001-008-003-003 导入模板管理接口（模板定义+字段映射+校验规则+模板文件存储）
    - P0-001-008-003-003-001 定义导入接口路由（@PostMapping+文件上传参数）
    - P0-001-008-003-003-002 实现导入逻辑（文件解析+数据校验+批量写入+错误数据返回）
- P0-001-008-004 通用树形结构工具类
  - P0-001-008-004-001 TreeUtil开发（列表转树/树转列表/排序/遍历/查找/过滤/路径获取）
    - P0-001-008-004-001-001 定义函数签名与类型（参数+返回+泛型约束）
    - P0-001-008-004-001-002 实现核心处理逻辑（输入校验+数据处理+边界+返回）

### P0-001-009 通用单据公共从表服务
- P0-001-009-001 doc_detail_location库位子表
  - P0-001-009-001-001 doc_detail_location表DDL（主表ID/商品ID/仓库ID/库位ID/数量+索引约束）
    - P0-001-009-001-001-001 编写CREATE INDEX语句（唯一索引/普通索引+CONCURRENTLY防锁表）
    - P0-001-009-001-001-002 编写ALTER TABLE ADD CONSTRAINT语句（外键/CHECK/触发器占位）
  - P0-001-009-001-002 DocDetailLocation Entity/Mapper开发
    - P0-001-009-001-002-001 定义Mapper接口（extends BaseMapperX+@Mapper+自定义方法声明）
    - P0-001-009-001-002-002 编写XML映射文件（resultMap+自定义SQL片段+动态条件）
- P0-001-009-002 doc_detail_batch批次子表
  - P0-001-009-002-001 doc_detail_batch表DDL（主表ID/商品ID/仓库ID/批次号/生产日期/有效期+数量+索引约束）
    - P0-001-009-002-001-001 编写CREATE INDEX语句（唯一索引/普通索引+CONCURRENTLY防锁表）
    - P0-001-009-002-001-002 编写ALTER TABLE ADD CONSTRAINT语句（外键/CHECK/触发器占位）
  - P0-001-009-002-002 DocDetailBatch Entity/Mapper开发
    - P0-001-009-002-002-001 定义Mapper接口（extends BaseMapperX+@Mapper+自定义方法声明）
    - P0-001-009-002-002-002 编写XML映射文件（resultMap+自定义SQL片段+动态条件）
- P0-001-009-003 doc_detail_serial序列号子表
  - P0-001-009-003-001 doc_detail_serial表DDL（主表ID/商品ID/仓库ID/序列号+索引约束）
    - P0-001-009-003-001-001 编写CREATE INDEX语句（唯一索引/普通索引+CONCURRENTLY防锁表）
    - P0-001-009-003-001-002 编写ALTER TABLE ADD CONSTRAINT语句（外键/CHECK/触发器占位）
  - P0-001-009-003-002 DocDetailSerial Entity/Mapper开发
    - P0-001-009-003-002-001 定义Mapper接口（extends BaseMapperX+@Mapper+自定义方法声明）
    - P0-001-009-003-002-002 编写XML映射文件（resultMap+自定义SQL片段+动态条件）
- P0-001-009-004 通用单据从表服务
  - P0-001-009-004-001 DetailSubTableService开发（库位/批次/序列号从表批量保存+按主表ID查询+数量合计校验）
    - P0-001-009-004-001-001 定义Service接口（extends IServiceX+业务方法声明）
    - P0-001-009-004-001-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
    - P0-001-009-004-001-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）

---

## P0-002 前端项目框架搭建

> **依赖**：无  
> **概述**：搭建Vue3 + Vite + TypeScript + Element Plus + Vxe Table前端项目骨架

### P0-002-001 项目初始化与基础配置
- P0-002-001-001 Vite + Vue3 + TypeScript项目创建
  - P0-002-001-001-001 Vite项目初始化（pnpm create vite + Vue3 + TypeScript模板）
    - P0-002-001-001-001-001 执行pnpm create vite初始化项目（选择Vue3+TypeScript模板+指定项目名）
    - P0-002-001-001-001-002 验证项目可运行（pnpm dev+Vite启动+页面渲染+热更新生效）
  - P0-002-001-001-002 ESLint + Prettier配置（代码规范+格式化规则+保存自动格式化）
    - P0-002-001-001-002-001 安装ESLint+Prettier依赖+编写.eslintrc.cjs+.prettierrc配置（规则定义+保存格式化）
    - P0-002-001-001-002-002 配置VSCode设置（settings.json+保存自动格式化+ESLint自动修复）
    - P0-002-001-001-002-003 验证代码规范（lint通过+格式化一致+提交时自动检查）
  - P0-002-001-001-003 Husky + lint-staged配置（Git提交前代码检查+提交信息规范）
    - P0-002-001-001-003-001 安装Husky+lint-staged依赖+编写.husky/pre-commit脚本+配置lint-staged文件匹配规则
    - P0-002-001-001-003-002 配置commitlint（提交信息规范feat/fix/docs/style/refactor/test/chore+scope+subject格式）
    - P0-002-001-001-003-003 验证Git钩子（不规范代码被拦截+不规范提交信息被拒绝+修复后可提交）
- P0-002-001-002 核心依赖安装配置
  - P0-002-001-002-001 Element Plus安装与按需引入配置（unplugin-vue-components + unplugin-auto-import）
    - P0-002-001-002-001-001 安装Element Plus依赖+配置unplugin-vue-components+unplugin-auto-import按需引入
    - P0-002-001-002-001-002 配置主题定制（CSS变量覆盖+暗色模式适配+中文语言包引入）
    - P0-002-001-002-001-003 验证组件库可用（按需引入无全量包+主题定制生效+中文显示正确）
  - P0-002-001-002-002 Vxe Table安装与全局配置（表格组件+表单组件+工具函数）
    - P0-002-001-002-002-001 定义函数签名与类型（参数+返回+泛型约束）
    - P0-002-001-002-002-002 实现核心处理逻辑（输入校验+数据处理+边界+返回）
  - P0-002-001-002-003 状态管理与请求层依赖（Pinia + Axios + VueRouter + TailwindCSS）
    - P0-002-001-002-003-001 定义实例与基础配置（baseURL/超时/请求头+请求拦截器Token注入）
    - P0-002-001-002-003-002 响应拦截逻辑（错误码401/403/500+业务异常弹窗+Token刷新）
    - P0-002-001-002-003-003 请求管理（CancelToken+并发队列+重复请求拦截）
- P0-002-001-003 Vite配置
  - P0-002-001-003-001 vite.config.ts开发（代理配置/API前缀/路径别名/构建优化/环境变量注入）
    - P0-002-001-003-001-001 编写vite.config.ts（proxy代理/API前缀/api自动路径别名/构建优化/环境变量注入）
    - P0-002-001-003-001-002 验证Vite配置（代理转发正确+路径别名解析+构建产物优化+环境变量读取）
    - P0-002-001-003-001-003 验证HMR与构建（热更新正常+生产构建无报错+chunk大小合理）
  - P0-002-001-003-002 环境变量文件创建（.env.dev/.env.prod/.env.local 含API_BASE_URL/APP_TITLE等）
    - P0-002-001-003-002-001 编写.env.dev/.env.prod/.env.local（API_BASE_URL/APP_TITLE/上传地址等变量）
    - P0-002-001-003-002-002 在代码中使用环境变量（import.meta.env.VITE_XXX+类型声明env.d.ts）
    - P0-002-001-003-002-003 验证环境切换（dev/prod API地址不同+本地覆盖.env.local生效+敏感变量前缀VITE_）
- P0-002-001-004 TypeScript全局类型声明
  - P0-002-001-004-001 全局类型声明文件（*.vue模块声明/env.d.ts环境变量声明/扩展类型声明）
    - P0-002-001-004-001-001 编写*.vue模块声明（declare module '*.vue'+Component类型）
    - P0-002-001-004-001-002 编写env.d.ts环境变量声明（ImportMetaEnv+VITE_变量类型声明）
    - P0-002-001-004-001-003 编写扩展类型声明（全局类型扩展+第三方库类型补全+业务通用类型）

### P0-002-002 路由与布局系统
- P0-002-002-001 Vue Router配置
  - P0-002-002-001-001 静态路由表定义（登录页/404/首页等公共路由+路由守卫占位）
    - P0-002-002-001-001-001 定义路由配置项（path/component/name/meta+权限标识）
    - P0-002-002-001-001-002 实现路由注册与守卫（动态注册+权限校验+标题+缓存标记）
  - P0-002-002-001-002 动态路由加载机制（根据后端菜单权限数据动态注册路由+路由守卫权限校验）
    - P0-002-002-001-002-001 定义前端路由配置（path/component/name/meta+权限标识）
    - P0-002-002-001-002-002 配置后端菜单数据（名称/路径/图标/排序/权限标识/父级ID+SQL）
  - P0-002-002-001-003 路由守卫开发（beforeEach：Token校验+权限校验+动态路由加载+标题设置）
    - P0-002-002-001-003-001 定义路由配置项（path/component/name/meta+权限标识）
    - P0-002-002-001-003-002 实现路由注册与守卫（动态注册+权限校验+标题+缓存标记）
- P0-002-002-002 主布局组件开发
  - P0-002-002-002-001 AdminLayout布局组件（顶部导航栏+侧边栏+标签页栏+内容区+底部状态栏布局骨架）
    - P0-002-002-002-001-001 实现布局容器结构（el-container嵌套+flex布局+响应式断点+slot插槽）
    - P0-002-002-002-001-002 实现布局状态管理（侧边栏折叠/展开+标签页联动+全屏切换+布局持久化）
    - P0-002-002-002-001-003 布局样式与动画（折叠过渡动画+固定定位+溢出处理+最小宽度）
  - P0-002-002-002-002 顶部导航栏组件（系统Logo+面包屑+全局搜索+消息铃铛+用户头像下拉+全屏切换）
    - P0-002-002-002-002-001 导航栏布局结构（Logo+面包屑+全局搜索+消息铃铛+用户头像+全屏切换）
    - P0-002-002-002-002-002 各功能区域交互（面包屑路由联动+全局搜索弹窗+消息下拉+用户菜单）
    - P0-002-002-002-002-003 导航栏响应式适配（折叠模式+搜索框收缩+下拉菜单适配移动端）
  - P0-002-002-002-003 侧边栏组件（菜单渲染+折叠展开+高亮当前路由+无限层级递归）
    - P0-002-002-002-003-001 定义前端路由配置（path/component/name/meta+权限标识）
    - P0-002-002-002-003-002 配置后端菜单数据（名称/路径/图标/排序/权限标识/父级ID+SQL）
- P0-002-002-003 标签页管理
  - P0-002-002-003-001 标签页状态管理（Pinia store：打开/关闭/缓存/切换/右键菜单操作）
    - P0-002-002-003-001-001 定义Pinia store（state: visitedViews/cachedViews+actions声明）
    - P0-002-002-003-001-002 实现标签页操作方法（addView/delView/delOthers/delAll/更新标题）
    - P0-002-002-003-001-003 实现右键菜单（关闭/关闭其他/关闭全部/刷新+指令式调用）
  - P0-002-002-003-002 标签页组件开发（标签页渲染+关闭按钮+右键菜单：关闭其他/关闭全部/刷新当前）
    - P0-002-002-003-002-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-002-002-003-002-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-002-002-003-002-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-002-002-003-003 标签页缓存策略（keep-alive缓存+最大缓存数+活跃标签页管理）
    - P0-002-002-003-003-001 实现keep-alive缓存策略（include动态列表+最大缓存数限制+LRU淘汰）
    - P0-002-002-003-003-002 实现缓存刷新机制（exclude临时排除+重新渲染触发+缓存状态调试）
- P0-002-002-004 侧边栏菜单渲染
  - P0-002-002-004-001 菜单数据获取与转换（后端菜单树数据→前端路由菜单格式转换+图标映射）
    - P0-002-002-004-001-001 定义前端路由配置（path/component/name/meta+权限标识）
    - P0-002-002-004-001-002 配置后端菜单数据（名称/路径/图标/排序/权限标识/父级ID+SQL）
  - P0-002-002-004-002 侧边栏菜单组件（支持无限层级/图标/折叠/高亮/外链/隐藏菜单）
    - P0-002-002-004-002-001 实现菜单渲染引擎（递归组件+无限层级+图标+外链+隐藏项+badge）
      - P0-002-002-004-002-001-001 实现递归菜单组件（MenuItem→children递归渲染+无限层级支持+v-for嵌套）
      - P0-002-002-004-002-001-002 实现菜单图标+外链（图标渲染+外链target=_blank+隐藏项不渲染+badge红点）
      - P0-002-002-004-002-001-003 实现菜单权限过滤（根据用户权限过滤菜单项+无权限菜单不渲染+缓存过滤结果）
    - P0-002-002-004-002-002 实现菜单交互（展开/折叠+高亮联动路由+点击跳转+折叠状态持久化）
    - P0-002-002-004-002-003 实现菜单权限过滤（后端菜单数据+v-permission过滤+动态路由注册）

### P0-002-003 状态管理与请求层
- P0-002-003-001 Pinia Store体系搭建
  - P0-002-003-001-001 userStore开发（用户信息/Token/权限列表/角色列表状态管理+getInfo/logout actions）
    - P0-002-003-001-001-001 定义State类型与初始值（interface+reactive+持久化字段）
    - P0-002-003-001-001-002 实现Actions（异步API+状态更新+错误处理+loading）
    - P0-002-003-001-001-003 实现Getters（派生计算+缓存+类型安全）
  - P0-002-003-001-002 appStore开发（侧边栏折叠/设备类型/主题/标签页列表/全局配置状态管理）
    - P0-002-003-001-002-001 定义State类型与初始值（interface+reactive+持久化字段）
    - P0-002-003-001-002-002 实现Actions（异步API+状态更新+错误处理+loading）
    - P0-002-003-001-002-003 实现Getters（派生计算+缓存+类型安全）
  - P0-002-003-001-003 permissionStore开发（路由列表/菜单树/按钮权限集合+generateRoutes动态路由生成）
    - P0-002-003-001-003-001 定义前端路由配置（path/component/name/meta+权限标识）
    - P0-002-003-001-003-002 配置后端菜单数据（名称/路径/图标/排序/权限标识/父级ID+SQL）
  - P0-002-003-001-004 paramStore开发（系统参数缓存/按分类获取参数+初始化加载）
    - P0-002-003-001-004-001 定义State类型与初始值（interface+reactive+持久化字段）
    - P0-002-003-001-004-002 实现Actions（异步API+状态更新+错误处理+loading）
    - P0-002-003-001-004-003 实现Getters（派生计算+缓存+类型安全）
- P0-002-003-002 Axios封装
  - P0-002-003-002-001 Axios实例创建（baseURL/超时/请求头默认值+请求拦截器：Token注入+Loading控制）
    - P0-002-003-002-001-001 定义切面/拦截器注册方式（@Aspect/@Component+切入点表达式）
    - P0-002-003-002-001-002 实现核心处理逻辑（前置/后置/环绕通知+业务处理+异常处理）
    - P0-002-003-002-001-003 集成测试验证（触发切面/拦截器+验证效果+异常场景）
  - P0-002-003-002-002 响应拦截器开发（统一错误码处理：401跳登录/403无权限/500服务端错误/业务异常弹窗）
    - P0-002-003-002-002-001 定义切面/拦截器注册方式（@Aspect/@Component+切入点表达式）
    - P0-002-003-002-002-002 实现核心处理逻辑（前置/后置/环绕通知+业务处理+异常处理）
    - P0-002-003-002-002-003 集成测试验证（触发切面/拦截器+验证效果+异常场景）
  - P0-002-003-002-003 Token刷新机制（401时自动刷新Token+并发请求队列+刷新失败清除登录态）
    - P0-002-003-002-003-001 实现401拦截与Token刷新请求（refreshToken API+请求队列暂存+重试）
    - P0-002-003-002-003-002 实现并发请求处理（刷新期间请求排队+刷新成功后批量重发+失败清除登录态）
      - P0-002-003-002-003-002-001 实现请求排队机制（Token刷新期间新请求→存入待重发队列+不重复触发刷新）
      - P0-002-003-002-003-002-002 实现刷新后批量重发（Token刷新成功→遍历待重发队列→替换新Token→逐个重发请求）
      - P0-002-003-002-003-002-003 实现刷新失败处理（Token刷新失败→清除登录态→跳转登录页+提示会话过期）
  - P0-002-003-002-004 请求取消机制（CancelToken封装+路由切换时自动取消pending请求）
    - P0-002-003-002-004-001 定义路由配置项（path/component/name/meta+权限标识）
    - P0-002-003-002-004-002 实现路由注册与守卫（动态注册+权限校验+标题+缓存标记）
- P0-002-003-003 API层统一封装规范
  - P0-002-003-003-001 API模块文件规范定义（按后端模块分文件/接口方法命名：list/getById/add/update/delete）
    - P0-002-003-003-001-001 定义接口路由与方法签名
    - P0-002-003-003-001-002 实现接口逻辑（参数处理+Service调用+响应包装）
  - P0-002-003-003-002 请求/响应类型定义规范（统一R<T>泛型响应类型+分页请求/响应类型+各模块DTO类型）
    - P0-002-003-003-002-001 定义泛型类结构（code/msg/data+泛型T+序列化）
    - P0-002-003-003-002-002 实现静态工厂方法（ok/fail/error+链式调用）
    - P0-002-003-003-002-003 编写单元测试（成功/失败/空数据/分页响应验证）

### P0-002-004 前端工具层
- P0-002-004-001 通用工具函数库
  - P0-002-004-001-001 日期工具（parseTime/formatDate/dateRange/相对时间/常用日期快捷选项）
    - P0-002-004-001-001-001 实现基础日期方法（parseTime/formatDate/dateRange/相对时间）
    - P0-002-004-001-001-002 实现快捷选项生成（近7天/近30天/本月/上月/本季度/自定义快捷）
  - P0-002-004-001-002 数字与金额工具（moneyFormat/thousandSeparator/精度计算/百分比格式化）
    - P0-002-004-001-002-001 实现数字格式化方法（千分位/小数精度/百分比/moneyFormat）
    - P0-002-004-001-002-002 实现精度计算方法（add/sub/mul/div大数计算+银行家舍入+舍入模式）
  - P0-002-004-001-003 对象与数组工具（deepClone/deepMerge/arrayToTree/treeToArray/uniqueArray）
    - P0-002-004-001-003-001 实现对象方法（deepClone/deepMerge/pick/omit+循环引用处理）
    - P0-002-004-001-003-002 实现数组方法（arrayToTree/treeToArray/uniqueArray/flatten+类型安全）
  - P0-002-004-001-004 防抖节流工具（debounce/throttle/once/beforeAfter钩子）
    - P0-002-004-001-004-001 实现debounce/throttle（leading/trailing配置+取消方法+this绑定）
    - P0-002-004-001-004-002 实现高级工具（once/beforeAfter钩子+单例模式+执行计数）
- P0-002-004-002 全局指令注册
  - P0-002-004-002-001 v-permission指令（按钮级别权限控制：传入权限标识+无权限时移除DOM元素）
    - P0-002-004-002-001-001 定义指令钩子函数（mounted/updated/unmounted+绑定值解析）
    - P0-002-004-002-001-002 实现指令逻辑（DOM操作+权限判断+事件绑定+清理）
  - P0-002-004-002-002 v-debounce指令（按钮防抖：点击后延迟+loading状态+可配置延迟时间）
    - P0-002-004-002-002-001 定义指令钩子函数（mounted/updated/unmounted+绑定值解析）
    - P0-002-004-002-002-002 实现指令逻辑（DOM操作+权限判断+事件绑定+清理）
  - P0-002-004-002-003 v-copy指令（一键复制文本到剪贴板+复制成功提示）
    - P0-002-004-002-003-001 定义指令钩子函数（mounted/updated/unmounted+绑定值解析）
    - P0-002-004-002-003-002 实现指令逻辑（DOM操作+权限判断+事件绑定+清理）
- P0-002-004-003 消息通知封装
  - P0-002-004-003-001 ElMessage/ElMessageBox二次封装（统一图标/时长/确认文案+Promise化调用）
    - P0-002-004-003-001-001 定义封装函数签名（参数类型+返回类型+默认配置）
    - P0-002-004-003-001-002 实现封装逻辑（统一图标/时长/文案+Promise化+类型重载）
  - P0-002-004-003-002 ElNotification封装（成功/警告/错误/信息通知+可配置持续时间和位置）
    - P0-002-004-003-002-001 定义封装函数签名（参数类型+返回类型+默认配置）
    - P0-002-004-003-002-002 实现封装逻辑（统一图标/时长/文案+Promise化+类型重载）

### P0-002-005 国际化基础
- P0-002-005-001 Vue I18n配置
  - P0-002-005-001-001 I18n实例创建与插件注册（createI18n+默认中文+lazy loading语言包机制）
    - P0-002-005-001-001-001 定义语言包结构（JSON格式+命名空间+TypeScript类型）
    - P0-002-005-001-001-002 编写中文词条（通用词汇/状态/校验提示/模块词汇映射）
  - P0-002-005-001-002 语言切换机制（appStore持久化+Element Plus locale联动+切换后页面刷新）
    - P0-002-005-001-002-001 定义State类型与初始值（interface+reactive+持久化字段）
    - P0-002-005-001-002-002 实现Actions（异步API+状态更新+错误处理+loading）
    - P0-002-005-001-002-003 实现Getters（派生计算+缓存+类型安全）
- P0-002-005-002 中文语言包初始化
  - P0-002-005-002-001 通用词汇定义（按钮：新增/修改/删除/查询/导出/导入/确认/取消/重置等）
    - P0-002-005-002-001-001 实现导出功能（API+文件流下载+动态文件名+进度提示）
    - P0-002-005-002-001-002 实现导入功能（上传+API+结果反馈+错误数据下载）
  - P0-002-005-002-002 状态文本定义（启用/禁用/草稿/审核中/已审核/已关闭/已完成等）
    - P0-002-005-002-002-001 编写状态文本词条（启用/禁用/草稿/审核中/已审核/已关闭/已完成+Tag颜色映射）
    - P0-002-005-002-002-002 实现状态文本渲染函数（字典翻译+Tag颜色+国际化key映射）
    - P0-002-005-002-002-003 验证状态展示（各状态标签颜色正确+翻译准确+未知状态兜底显示）
  - P0-002-005-002-003 校验提示定义（必填/格式错误/长度超限/数值范围等通用校验消息）
    - P0-002-005-002-003-001 编写校验提示词条（必填/格式错误/长度超限/数值范围+字段名占位符${label}）
    - P0-002-005-002-003-002 实现校验提示渲染函数（字段名替换+多规则聚合+错误行高亮）
    - P0-002-005-002-003-003 验证校验提示（各规则提示文案正确+字段名替换+多错误聚合展示）

### P0-002-006 样式体系
- P0-002-006-001 全局CSS变量定义
  - P0-002-006-001-001 颜色变量定义（主色/辅色/成功/警告/危险/信息/文字色/背景色/边框色系列）
    - P0-002-006-001-001-001 编写:root CSS变量（主色/辅色/成功/警告/危险/信息/文字色/背景色/边框色系列+tailwind.config映射）
    - P0-002-006-001-001-002 配置Tailwind自定义颜色（extend.colors引用CSS变量+暗色模式变量覆盖）
    - P0-002-006-001-001-003 验证颜色系统（各组件颜色一致+暗色模式切换+品牌色渲染正确）
  - P0-002-006-001-002 间距与尺寸变量定义（padding/margin/圆角/阴影/字体大小/行高系列）
    - P0-002-006-001-002-001 编写间距/尺寸CSS变量（padding/margin/圆角/阴影/字体大小/行高+tailwind.extend映射）
    - P0-002-006-001-002-002 配置Tailwind自定义间距/圆角/阴影（extend引用CSS变量+Design Token值对齐）
    - P0-002-006-001-002-003 验证间距系统（组件间距一致+响应式断点+圆角/阴影视觉统一）
- P0-002-006-002 Tailwind CSS主题扩展
  - P0-002-006-002-001 tailwind.config.ts主题扩展（colors/spacing/fontSize/borderRadius映射ERP设计Token）
    - P0-002-006-002-001-001 定义变量/扩展配置（:root变量+tailwind.config+设计Token值）
    - P0-002-006-002-001-002 验证样式效果（组件一致性+暗色模式+响应式断点）
- P0-002-006-003 Element Plus主题定制
  - P0-002-006-003-001 CSS变量覆盖（--el-color-primary等系列变量覆盖+组件圆角/间距微调）
    - P0-002-006-003-001-001 定义变量/扩展配置（:root变量+tailwind.config+设计Token值）
    - P0-002-006-003-001-002 验证样式效果（组件一致性+暗色模式+响应式断点）
- P0-002-006-004 通用工具样式类
  - P0-002-006-004-001 布局工具类（flex居中/左右对齐/网格布局/固定高度滚动区）
    - P0-002-006-004-001-001 编写SCSS工具类（.flex-center/.flex-between/.grid-auto-fill/.fixed-scroll等布局工具类+响应式断点适配）
    - P0-002-006-004-001-002 验证布局工具类（各组件引用正确+响应式生效+浏览器兼容）
  - P0-002-006-004-002 文本与装饰工具类（文本截断/省略号/文字颜色/背景色/动画过渡）
    - P0-002-006-004-002-001 编写SCSS工具类（.text-ellipsis/.text-truncate-{n}/.text-{color}/.bg-{color}/.transition-{type}等）
    - P0-002-006-004-002-002 验证文本装饰工具类（截断生效+颜色正确+动画流畅+暗色模式适配）
- P0-002-006-005 性能优化基础设施
  - P0-002-006-005-001 虚拟滚动指令开发（v-virtual-scroll：基于IntersectionObserver+滚动容器高度计算+dom回收复用）
    - P0-002-006-005-001-001 编写v-virtual-scroll指令（IntersectionObserver监听+可视区域计算+dom池管理+滚动事件节流）
    - P0-002-006-005-001-002 验证虚拟滚动（大数据列表渲染<50个dom节点+滚动流畅60fps+行高自适应）
  - P0-002-006-005-002 图片懒加载指令开发（v-lazy-img：IntersectionObserver+加载占位图+加载失败降级）
    - P0-002-006-005-002-001 编写v-lazy-img指令（IntersectionObserver监听+data-src替换+占位图+失败重试+降级默认图）
    - P0-002-006-005-002-002 验证图片懒加载（首屏仅加载可视区图片+滚动加载+加载失败降级+loading态）
  - P0-002-006-005-003 Vite构建优化配置（代码分割+Tree Shaking+资源压缩+Gzip+CDN外部化）
    - P0-002-006-005-003-001 配置vite.config.ts构建优化（manualChunks分包+terser压缩+assetsInlineLimit+rollupOptions外部化）
    - P0-002-006-005-003-002 验证构建优化（dist体积<5MB+gzip<1.5MB+首屏加载<3s+Lighthouse>90分）
- P0-002-006-006 外部资源域适配
  - P0-002-006-006-001 Google Fonts等外部CSS国内CDN替代（fonts.googleapis.cn + 国内镜像回退）
    - P0-002-006-006-001-001 编写外部资源域适配配置（环境变量VITE_EXTERNAL_CDN_BASE+国内CDN首选+国外CDN回退+加载超时切换）
    - P0-002-006-006-001-002 验证CDN切换（国内环境使用国内CDN+国外环境使用原CDN+超时自动切换+字体正常加载）

---

## P0-003 数据库基础架构搭建

> **依赖**：P0-001  
> **概述**：创建数据库、公共字段基座、全部数据表

### P0-003-001 数据库与Schema创建
- P0-003-001-001 创建ERP数据库（CREATE DATABASE + Schema + 字符集UTF8 + 时区设置）
  - P0-003-001-001-001 执行创建脚本（CREATE DATABASE+Schema+字符集+时区设置）
    - P0-003-001-001-001-001 编写CREATE DATABASE语句（数据库名+字符集UTF8+排序规则+时区设置）
    - P0-003-001-001-001-002 执行DDL并验证（数据库创建成功+字符集正确+可连接）
- P0-003-001-002 数据库通用规范配置（命名规范注释/默认值约束/CHECK约束）
  - P0-003-001-002-001 编写规范定义文档+DDL（字段命名+默认值+约束+注释规范）
    - P0-003-001-002-001-001 编写公共字段DDL（id/creator_id/create_time/updater_id/update_time/deleted/tenant_id/version）
    - P0-003-001-002-001-002 编写默认值与约束（默认值表达式+非空约束+CHECK约束+注释规范）
    - P0-003-001-002-001-003 验证规范（字段命名一致+默认值生效+约束正确）

### P0-003-002 公共字段基座表创建
- P0-003-002-001 公共字段规范定义（id/creator_id/create_time/updater_id/update_time/deleted/tenant_id/version/status/remark）
  - P0-003-002-001-001 编写规范定义文档+DDL（字段命名+默认值+约束+注释规范）
    - P0-003-002-001-001-001 编写公共字段DDL（id/creator_id/create_time/updater_id/update_time/deleted/tenant_id/version）
    - P0-003-002-001-001-002 编写默认值与约束（默认值表达式+非空约束+CHECK约束+注释规范）
    - P0-003-002-001-001-003 验证规范（字段命名一致+默认值生效+约束正确）
- P0-003-002-002 系统核心表建表（sys_user + sys_role + sys_menu + sys_user_role + sys_user_dept + sys_role_menu + sys_role_data_scope + sys_role_field_permission + sys_user_group + sys_user_group_member）
  - P0-003-002-002-001 编写系统核心表DDL（CREATE TABLE 系统核心+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-002-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-002-002-001-002 编写系统核心表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-002-002-001-003 验证编写系统核心表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-002-003 认证相关表建表（sys_login_log + sys_auth_config + sys_password_policy + auth_online_device + auth_sso_config + auth_oauth2_config）
  - P0-003-002-003-001 编写认证相关表DDL（CREATE TABLE 认证相关+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-002-003-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-002-003-001-002 编写认证相关表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-002-003-001-003 验证编写认证相关表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-002-004 系统管理表建表（sys_param + sys_dict_type + sys_dict_data + sys_code_rule + sys_code_rule_segment + sys_operation_log + sys_data_view + sys_data_view_field + sys_notice + sys_doc_config）
  - P0-003-002-004-001 编写系统管理表DDL（CREATE TABLE 系统管理+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-002-004-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-002-004-001-002 编写系统管理表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-002-004-001-003 验证编写系统管理表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-002-005 移动端相关表建表（sys_mobile_menu）
  - P0-003-002-005-001 编写移动端相关表DDL（CREATE TABLE 移动端相关+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-002-005-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-002-005-001-002 编写移动端相关表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-002-005-001-003 验证编写移动端相关表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
  - P0-003-002-006 单据主表特有字段规范（单据编号bill_no+单据日期bill_date+单据状态bill_status+审核状态audit_status+备注remark+附件数attachment_count+来源单号source_bill_no+来源单类型source_bill_type）
    - P0-003-002-006-001 编写单据主表字段规范DDL+注释（bill_no唯一+bill_date默认当天+bill_status枚举+audit_status枚举+来源单关联）
      - P0-003-002-006-001-001 编写DDL+约束（bill_no VARCHAR(32) NOT NULL UNIQUE含is_deleted+bill_status SMALLINT DEFAULT 0+枚举注释）
  - P0-003-002-007 明细从表商品冗余字段+快照原则（product_code+product_name+product_spec+product_unit+unit_id+辅助属性字段组，下推时源单商品信息快照写入不可修改）
    - P0-003-002-007-001 编写明细表商品冗余字段规范DDL+快照逻辑约束
      - P0-003-002-007-001-001 编写DDL（product_code/product_name/product_spec/product_unit+辅助属性aux_字段组+注释"快照字段不可修改"）
      - P0-003-002-007-001-002 编写快照约束说明（下推时源单商品字段自动填充+下推后只读+变更商品重新覆盖）
  - P0-003-002-008 多租户隔离规范（tenant_id字段必含+所有查询WHERE自动注入tenant_id+唯一约束包含tenant_id+数据隔离粒度：行级隔离+租户间数据不可见）
    - P0-003-002-008-001 编写多租户隔离DDL规范+MyBatis拦截器+唯一约束规范
      - P0-003-002-008-001-001 编写DDL规范（所有表含tenant_id BIGINT NOT NULL+索引idx_tenant_id+唯一约束含tenant_id）
      - P0-003-002-008-001-002 编写MyBatis-Plus TenantLineInnerInterceptor（SQL自动注入WHERE tenant_id=?+Insert自动填充tenant_id）
  - P0-003-002-009 明细表辅助属性存储规范（aux_attr1~aux_attr10动态扩展列+attr_json辅助属性JSON+辅助属性值来源product_aux_attr表+检索+展示）
    - P0-003-002-009-001 编写辅助属性DDL+存储规范+查询映射
      - P0-003-002-009-001-001 编写DDL（aux_attr1~10 VARCHAR+attr_json JSONB+字段注释"辅助属性值，来源product_aux_attr"）
  - P0-003-002-010 数据类型约定（金额DECIMAL(18,4)+数量DECIMAL(18,4)+单价DECIMAL(18,4)+税率DECIMAL(5,2)+百分比DECIMAL(5,2)+编码VARCHAR(32)+名称VARCHAR(128)+备注VARCHAR(512)+状态SMALLINT+日期DATE+时间TIMESTAMP+布尔SMALLINT(0/1)）
    - P0-003-002-010-001 编写数据类型规范文档+DDL模板+CHECK约束
      - P0-003-002-010-001-001 编写规范文档（各业务字段类型映射+精度要求+CHECK约束+字段命名统一后缀）
  - P0-003-002-011 base_qty核心地位（base_qty为计量基准数量，所有数量计算以base_qty为准+qty×rate=base_qty+数量比较/汇总/库存计算均使用base_qty+前端展示qty但后端校验/统计必须用base_qty）
    - P0-003-002-011-001 编写base_qty核心规范+计算逻辑+校验规则
      - P0-003-002-011-001-001 编写规范（base_qty=qty×rate+所有库存/统计/比较使用base_qty+前端转换显示qty）
  - P0-003-002-012 唯一约束需包含is_deleted字段（软删除场景下bill_no等业务唯一约束必须包含is_deleted+否则删除后无法重建同编号+UNIQUE(tenant_id, bill_no, is_deleted)）
    - P0-003-002-012-001 编写唯一约束规范+DDL模板+已有约束修正
      - P0-003-002-012-001-001 编写规范（所有UNIQUE约束追加is_deleted字段+给出修正DDL模板）
  - P0-003-002-013 数值精度参数优先级规则（系统参数>单据参数>默认值+四舍五入/截断/向上取整策略可配+金额=数量×单价先乘后截+税额=金额×税率+合计=金额+税额+尾差处理规则）
    - P0-003-002-013-001 编写精度优先级规范+尾差处理规则+参数配置
      - P0-003-002-013-001-001 编写精度规则（优先级链+四舍五入策略+先乘后截+尾差归入最后一行+参数键定义）

### P0-003-003 组织架构表建表
- P0-003-003-001 org_company公司表建表
  - P0-003-003-001-001 编写org_company公司表DDL（CREATE TABLE org_company公司+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-003-001-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-003-001-001-002 编写org_company公司表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-003-001-001-003 验证编写org_company公司表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-003-002 org_department部门表建表
  - P0-003-003-002-001 编写org_department部门表DDL（CREATE TABLE org_department部门+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-003-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-003-002-001-002 编写org_department部门表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-003-002-001-003 验证编写org_department部门表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-003-003 org_position岗位表建表
  - P0-003-003-003-001 编写org_position岗位表DDL（CREATE TABLE org_position岗位+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-003-003-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-003-003-001-002 编写org_position岗位表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-003-003-001-003 验证编写org_position岗位表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-003-004 org_employee员工表建表
  - P0-003-003-004-001 编写org_employee员工表DDL（CREATE TABLE org_employee员工+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-003-004-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-003-004-001-002 编写org_employee员工表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-003-004-001-003 验证编写org_employee员工表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）

### P0-003-004 商品管理表建表
- P0-003-004-001 prod_product_class商品分类表建表
  - P0-003-004-001-001 编写prod_product_class商品分类表DDL（CREATE TABLE prod_product_class商品分类+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-004-001-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-001-001-002 编写prod_product_class商品分类表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-004-001-001-003 验证编写prod_product_class商品分类表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-004-002 prod_product商品主表建表
  - P0-003-004-002-001 编写prod_product商品主表DDL（CREATE TABLE prod_product商品主+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-004-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-002-001-002 编写prod_product商品主表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-004-002-001-003 验证编写prod_product商品主表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-004-003 prod_product_unit商品多单位表建表
  - P0-003-004-003-001 编写prod_product_unit商品多单位表DDL（CREATE TABLE prod_product_unit商品多单位+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-004-003-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-003-001-002 编写prod_product_unit商品多单位表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-004-003-001-003 验证编写prod_product_unit商品多单位表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-004-004 prod_product_control商品控制策略表建表
  - P0-003-004-004-001 编写prod_product_control商品控制策略表DDL（CREATE TABLE prod_product_control商品控制策略+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-004-004-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-004-001-002 编写prod_product_control商品控制策略表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-004-004-001-003 验证编写prod_product_control商品控制策略表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-004-005 prod_product_safety_stock商品安全库存表建表
  - P0-003-004-005-001 编写prod_product_safety_stock商品安全库存表DDL（CREATE TABLE prod_product_safety_stock商品安全库存+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-004-005-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-005-001-002 编写prod_product_safety_stock商品安全库存表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-004-005-001-003 验证编写prod_product_safety_stock商品安全库存表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-004-006 prod_product_attachment商品附件表建表
  - P0-003-004-006-001 编写prod_product_attachment商品附件表DDL（CREATE TABLE prod_product_attachment商品附件+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-004-006-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-006-001-002 编写prod_product_attachment商品附件表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-004-006-001-003 验证编写prod_product_attachment商品附件表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-004-007 prod_product_standard_price商品标准价表建表
  - P0-003-004-007-001 编写prod_product_standard_price商品标准价表DDL（CREATE TABLE prod_product_standard_price商品标准价+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-004-007-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-007-001-002 编写prod_product_standard_price商品标准价表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-004-007-001-003 验证编写prod_product_standard_price商品标准价表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-004-008 prod_product_purchase_price商品购价核定表建表
  - P0-003-004-008-001 编写prod_product_purchase_price商品购价核定表DDL（CREATE TABLE prod_product_purchase_price商品购价核定+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-004-008-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-008-001-002 编写prod_product_purchase_price商品购价核定表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-004-008-001-003 验证编写prod_product_purchase_price商品购价核定表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-004-009 prod_product_sale_price商品销价核定表建表
  - P0-003-004-009-001 编写prod_product_sale_price商品销价核定表DDL（CREATE TABLE prod_product_sale_price商品销价核定+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-004-009-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-009-001-002 编写prod_product_sale_price商品销价核定表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-004-009-001-003 验证编写prod_product_sale_price商品销价核定表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-004-010 prod_product_competitor商品竞品表建表
  - P0-003-004-010-001 编写prod_product_competitor商品竞品表DDL（CREATE TABLE prod_product_competitor商品竞品+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-004-010-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-010-001-002 编写prod_product_competitor商品竞品表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-004-010-001-003 验证编写prod_product_competitor商品竞品表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-004-011 prod_product_bom + prod_product_bom_detail商品BOM主从表建表
  - P0-003-004-011-001 编写prod_product_bom_detail商品BOM主从表DDL（CREATE TABLE prod_product_bom_detail商品BOM主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-004-011-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-011-001-002 编写prod_product_bom_detail商品BOM主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-004-011-001-003 验证编写prod_product_bom_detail商品BOM主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-004-012 prod_product_process + prod_product_process_param + prod_product_process_price商品工序主从表建表
  - P0-003-004-012-001 编写prod_product_process_price商品工序主从表DDL（CREATE TABLE prod_product_process_price商品工序主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-004-012-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-012-001-002 编写prod_product_process_price商品工序主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-004-012-001-003 验证编写prod_product_process_price商品工序主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-004-013 prod_standard_process标准工序表建表
  - P0-003-004-013-001 编写prod_standard_process标准工序表DDL（CREATE TABLE prod_standard_process标准工序+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-004-013-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-013-001-002 编写prod_standard_process标准工序表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-004-013-001-003 验证编写prod_standard_process标准工序表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-004-014 prod_product_price商品价格表建表
  - P0-003-004-014-001 编写prod_product_price商品价格表DDL（价格类型/价格/币种/起订量/生效日期/失效日期）
    - P0-003-004-014-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-014-001-002 编写prod_product_price表索引与约束（主键PK+商品+价格类型唯一索引UK+业务索引IDX）
    - P0-003-004-014-001-003 验证DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整）
- P0-003-004-015 prod_product_attribute商品属性表 + prod_product_attribute_value商品属性值表建表
  - P0-003-004-015-001 编写prod_product_attribute + prod_product_attribute_value表DDL（属性名/属性值/排序号）
    - P0-003-004-015-001-001 编写CREATE TABLE语句（主从表字段定义+数据类型+非空约束+注释）
    - P0-003-004-015-001-002 编写索引与约束（主键PK+属性名唯一索引+外键关联+业务索引IDX）
    - P0-003-004-015-001-003 验证DDL（CREATE TABLE成功+字段类型正确+约束生效）
- P0-003-004-016 prod_product_spec商品规格表建表
  - P0-003-004-016-001 编写prod_product_spec商品规格表DDL（规格名称/规格值/排序号）
    - P0-003-004-016-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-016-001-002 编写索引与约束（主键PK+商品+规格名唯一索引UK+业务索引IDX）
    - P0-003-004-016-001-003 验证DDL（CREATE TABLE成功+字段类型正确+约束生效）
- P0-003-004-017 prod_product_barcode商品条码表建表
  - P0-003-004-017-001 编写prod_product_barcode商品条码表DDL（条码类型/条码值/是否默认/单位ID关联）
    - P0-003-004-017-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-017-001-002 编写索引与约束（主键PK+条码值唯一索引UK+商品索引IDX）
    - P0-003-004-017-001-003 验证DDL（CREATE TABLE成功+字段类型正确+约束生效）
- P0-003-004-018 prod_product_image商品图片表建表
  - P0-003-004-018-001 编写prod_product_image商品图片表DDL（图片URL/图片类型/排序号/是否主图）
    - P0-003-004-018-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-018-001-002 编写索引与约束（主键PK+商品索引IDX+主图标记索引）
    - P0-003-004-018-001-003 验证DDL（CREATE TABLE成功+字段类型正确+约束生效）
- P0-003-004-019 prod_product_relation商品关联表建表
  - P0-003-004-019-001 编写prod_product_relation商品关联表DDL（关联商品ID/关联类型/双向标记）
    - P0-003-004-019-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-019-001-002 编写索引与约束（主键PK+商品+关联商品唯一索引UK+业务索引IDX）
    - P0-003-004-019-001-003 验证DDL（CREATE TABLE成功+字段类型正确+约束生效）
- P0-003-004-020 prod_product_tag商品标签关联表建表
  - P0-003-004-020-001 编写prod_product_tag商品标签关联表DDL（标签定义ID/标签值/排序号）
    - P0-003-004-020-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-020-001-002 编写索引与约束（主键PK+商品+标签唯一索引UK+业务索引IDX）
    - P0-003-004-020-001-003 验证DDL（CREATE TABLE成功+字段类型正确+约束生效）
- P0-003-004-021 prod_serial_template序列号模板表建表
  - P0-003-004-021-001 编写prod_serial_template序列号模板表DDL（模板编码/模板名称/前缀/序列号长度/起始值/步长/重置周期）
    - P0-003-004-021-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-021-001-002 编写索引与约束（主键PK+模板编码唯一索引UK+状态索引IDX）
    - P0-003-004-021-001-003 验证DDL（CREATE TABLE成功+字段类型正确+约束生效）
- P0-003-004-022 prod_product_other商品其他信息表建表
  - P0-003-004-022-001 编写prod_product_other商品其他信息表DDL（HS编码/hs_code+条码/barcode+产地/origin+毛重/gross_weight+净重/net_weight+体积/volume+包装单位/packing_unit+认证/certification+保质期/shelf_life+存储条件/storage_condition）
    - P0-003-004-022-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-004-022-001-002 编写索引与约束（主键PK+商品ID唯一索引UK+HS编码索引IDX+条码索引IDX）
    - P0-003-004-022-001-003 验证DDL（CREATE TABLE成功+字段类型正确+约束生效）

### P0-003-005 CRM客户管理表建表
- P0-003-005-001 crm_customer_class客户分类表建表
  - P0-003-005-001-001 编写crm_customer_class客户分类表DDL（CREATE TABLE crm_customer_class客户分类+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-005-001-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-005-001-001-002 编写crm_customer_class客户分类表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-005-001-001-003 验证编写crm_customer_class客户分类表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-005-002 crm_tag_definition CRM标签定义表建表
  - P0-003-005-002-001 编写CRM标签定义表DDL（CREATE TABLE CRM标签定义+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-005-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-005-002-001-002 编写CRM标签定义表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-005-002-001-003 验证编写CRM标签定义表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-005-003 crm_customer客户主表建表
  - P0-003-005-003-001 编写crm_customer客户主表DDL（CREATE TABLE crm_customer客户主+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-005-003-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-005-003-001-002 编写crm_customer客户主表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-005-003-001-003 验证编写crm_customer客户主表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-005-004 crm_customer_contact + crm_contact_comm客户联系人表建表
  - P0-003-005-004-001 编写crm_contact_comm客户联系人表DDL（CREATE TABLE crm_contact_comm客户联系人+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-005-004-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-005-004-001-002 编写crm_contact_comm客户联系人表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-005-004-001-003 验证编写crm_contact_comm客户联系人表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-005-005 crm_customer_address客户地址表建表
  - P0-003-005-005-001 编写crm_customer_address客户地址表DDL（CREATE TABLE crm_customer_address客户地址+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-005-005-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-005-005-001-002 编写crm_customer_address客户地址表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-005-005-001-003 验证编写crm_customer_address客户地址表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-005-006 crm_customer_tag_rel客户标签关联表建表
  - P0-003-005-006-001 编写crm_customer_tag_rel客户标签关联表DDL（CREATE TABLE crm_customer_tag_rel客户标签关联+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-005-006-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-005-006-001-002 编写crm_customer_tag_rel客户标签关联表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-005-006-001-003 验证编写crm_customer_tag_rel客户标签关联表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-005-007 crm_customer_attachment客户附件表建表
  - P0-003-005-007-001 编写crm_customer_attachment客户附件表DDL（CREATE TABLE crm_customer_attachment客户附件+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-005-007-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-005-007-001-002 编写crm_customer_attachment客户附件表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-005-007-001-003 验证编写crm_customer_attachment客户附件表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-005-008 crm_customer_evaluation客户评价表建表
  - P0-003-005-008-001 编写crm_customer_evaluation客户评价表DDL（CREATE TABLE crm_customer_evaluation客户评价+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-005-008-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-005-008-001-002 编写crm_customer_evaluation客户评价表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-005-008-001-003 验证编写crm_customer_evaluation客户评价表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-005-009 crm_customer_finance客户财务配置表建表
  - P0-003-005-009-001 编写crm_customer_finance客户财务配置表DDL（CREATE TABLE crm_customer_finance客户财务配置+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-005-009-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-005-009-001-002 编写crm_customer_finance客户财务配置表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-005-009-001-003 验证编写crm_customer_finance客户财务配置表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-005-010 crm_opportunity客户机会表建表
  - P0-003-005-010-001 编写crm_opportunity客户机会表DDL（CREATE TABLE crm_opportunity客户机会+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-005-010-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-005-010-001-002 编写crm_opportunity客户机会表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-005-010-001-003 验证编写crm_opportunity客户机会表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-005-011 crm_project客户项目表建表
  - P0-003-005-011-001 编写crm_project客户项目表DDL（CREATE TABLE crm_project客户项目+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-005-011-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-005-011-001-002 编写crm_project客户项目表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-005-011-001-003 验证编写crm_project客户项目表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）

### P0-003-006 SRM供应商管理表建表
- P0-003-006-001 srm_supplier_class供应商分类表建表
  - P0-003-006-001-001 编写srm_supplier_class供应商分类表DDL（CREATE TABLE srm_supplier_class供应商分类+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-006-001-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-006-001-001-002 编写srm_supplier_class供应商分类表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-006-001-001-003 验证编写srm_supplier_class供应商分类表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-006-002 srm_tag_definition SRM标签定义表建表
  - P0-003-006-002-001 编写SRM标签定义表DDL（CREATE TABLE SRM标签定义+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-006-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-006-002-001-002 编写SRM标签定义表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-006-002-001-003 验证编写SRM标签定义表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-006-003 srm_supplier供应商主表建表
  - P0-003-006-003-001 编写srm_supplier供应商主表DDL（CREATE TABLE srm_supplier供应商主+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-006-003-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-006-003-001-002 编写srm_supplier供应商主表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-006-003-001-003 验证编写srm_supplier供应商主表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-006-004 srm_supplier_contact + srm_supplier_comm供应商联系人表建表
  - P0-003-006-004-001 编写srm_supplier_comm供应商联系人表DDL（CREATE TABLE srm_supplier_comm供应商联系人+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-006-004-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-006-004-001-002 编写srm_supplier_comm供应商联系人表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-006-004-001-003 验证编写srm_supplier_comm供应商联系人表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-006-005 srm_supplier_address供应商地址表建表
  - P0-003-006-005-001 编写srm_supplier_address供应商地址表DDL（CREATE TABLE srm_supplier_address供应商地址+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-006-005-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-006-005-001-002 编写srm_supplier_address供应商地址表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-006-005-001-003 验证编写srm_supplier_address供应商地址表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-006-006 srm_supplier_tag_rel供应商标签关联表建表
  - P0-003-006-006-001 编写srm_supplier_tag_rel供应商标签关联表DDL（CREATE TABLE srm_supplier_tag_rel供应商标签关联+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-006-006-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-006-006-001-002 编写srm_supplier_tag_rel供应商标签关联表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-006-006-001-003 验证编写srm_supplier_tag_rel供应商标签关联表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-006-007 srm_supplier_attachment供应商附件表建表
  - P0-003-006-007-001 编写srm_supplier_attachment供应商附件表DDL（CREATE TABLE srm_supplier_attachment供应商附件+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-006-007-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-006-007-001-002 编写srm_supplier_attachment供应商附件表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-006-007-001-003 验证编写srm_supplier_attachment供应商附件表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-006-008 srm_supplier_evaluation供应商评价表建表
  - P0-003-006-008-001 编写srm_supplier_evaluation供应商评价表DDL（CREATE TABLE srm_supplier_evaluation供应商评价+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-006-008-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-006-008-001-002 编写srm_supplier_evaluation供应商评价表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-006-008-001-003 验证编写srm_supplier_evaluation供应商评价表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-006-009 srm_supplier_finance供应商财务配置表建表
  - P0-003-006-009-001 编写srm_supplier_finance供应商财务配置表DDL（CREATE TABLE srm_supplier_finance供应商财务配置+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-006-009-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-006-009-001-002 编写srm_supplier_finance供应商财务配置表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-006-009-001-003 验证编写srm_supplier_finance供应商财务配置表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）

### P0-003-007 仓库库存表建表
- P0-003-007-001 inv_warehouse仓库定义表建表
  - P0-003-007-001-001 编写inv_warehouse仓库定义表DDL（CREATE TABLE inv_warehouse仓库定义+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-007-001-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-007-001-001-002 编写inv_warehouse仓库定义表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-007-001-001-003 验证编写inv_warehouse仓库定义表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-007-002 inv_location库位管理表建表
  - P0-003-007-002-001 编写inv_location库位管理表DDL（CREATE TABLE inv_location库位管理+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-007-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-007-002-001-002 编写inv_location库位管理表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-007-002-001-003 验证编写inv_location库位管理表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-007-003 inv_stock库存实时表建表
  - P0-003-007-003-001 编写inv_stock库存实时表DDL（CREATE TABLE inv_stock库存实时+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-007-003-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-007-003-001-002 编写inv_stock库存实时表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-007-003-001-003 验证编写inv_stock库存实时表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-007-004 inv_stock_location库位库存表建表
  - P0-003-007-004-001 编写inv_stock_location库位库存表DDL（CREATE TABLE inv_stock_location库位库存+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-007-004-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-007-004-001-002 编写inv_stock_location库位库存表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-007-004-001-003 验证编写inv_stock_location库位库存表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-007-005 inv_other_outbound + inv_other_outbound_detail其他出库主从表建表
  - P0-003-007-005-001 编写inv_other_outbound_detail其他出库主从表DDL（CREATE TABLE inv_other_outbound_detail其他出库主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-007-005-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-007-005-001-002 编写inv_other_outbound_detail其他出库主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-007-005-001-003 验证编写inv_other_outbound_detail其他出库主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-007-006 inv_other_inbound + inv_other_inbound_detail其他入库主从表建表
  - P0-003-007-006-001 编写inv_other_inbound_detail其他入库主从表DDL（CREATE TABLE inv_other_inbound_detail其他入库主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-007-006-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-007-006-001-002 编写inv_other_inbound_detail其他入库主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-007-006-001-003 验证编写inv_other_inbound_detail其他入库主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-007-007 inv_stocktaking + inv_stocktaking_detail盘点主从表建表
  - P0-003-007-007-001 编写inv_stocktaking_detail盘点主从表DDL（CREATE TABLE inv_stocktaking_detail盘点主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-007-007-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-007-007-001-002 编写inv_stocktaking_detail盘点主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-007-007-001-003 验证编写inv_stocktaking_detail盘点主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-007-008 inv_transfer + inv_transfer_detail调拨主从表建表
  - P0-003-007-008-001 编写inv_transfer_detail调拨主从表DDL（CREATE TABLE inv_transfer_detail调拨主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-007-008-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-007-008-001-002 编写inv_transfer_detail调拨主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-007-008-001-003 验证编写inv_transfer_detail调拨主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-007-009 inv_loss + inv_loss_detail报损主从表建表
  - P0-003-007-009-001 编写inv_loss_detail报损主从表DDL（CREATE TABLE inv_loss_detail报损主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-007-009-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-007-009-001-002 编写inv_loss_detail报损主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-007-009-001-003 验证编写inv_loss_detail报损主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-007-010 inv_overflow + inv_overflow_detail报溢主从表建表
  - P0-003-007-010-001 编写inv_overflow_detail报溢主从表DDL（CREATE TABLE inv_overflow_detail报溢主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-007-010-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-007-010-001-002 编写inv_overflow_detail报溢主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-007-010-001-003 验证编写inv_overflow_detail报溢主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-007-011 inv_assembly + inv_assembly_detail组装主从表建表
  - P0-003-007-011-001 编写inv_assembly_detail组装主从表DDL（CREATE TABLE inv_assembly_detail组装主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-007-011-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-007-011-001-002 编写inv_assembly_detail组装主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-007-011-001-003 验证编写inv_assembly_detail组装主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-007-012 inv_disassembly + inv_disassembly_detail拆卸主从表建表
  - P0-003-007-012-001 编写inv_disassembly_detail拆卸主从表DDL（CREATE TABLE inv_disassembly_detail拆卸主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-007-012-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-007-012-001-002 编写inv_disassembly_detail拆卸主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-007-012-001-003 验证编写inv_disassembly_detail拆卸主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）

### P0-003-008 财务基础表建表
- P0-003-008-001 fin_currency_rate币种汇率表建表
  - P0-003-008-001-001 编写fin_currency_rate币种汇率表DDL（CREATE TABLE fin_currency_rate币种汇率+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-008-001-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-008-001-001-002 编写fin_currency_rate币种汇率表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-008-001-001-003 验证编写fin_currency_rate币种汇率表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-008-002 fin_bank_account银行账户表建表
  - P0-003-008-002-001 编写fin_bank_account银行账户表DDL（CREATE TABLE fin_bank_account银行账户+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-008-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-008-002-001-002 编写fin_bank_account银行账户表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-008-002-001-003 验证编写fin_bank_account银行账户表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-008-003 fin_account会计科目表建表
  - P0-003-008-003-001 编写fin_account会计科目表DDL（CREATE TABLE fin_account会计科目+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-008-003-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-008-003-001-002 编写fin_account会计科目表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-008-003-001-003 验证编写fin_account会计科目表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-008-004 fin_voucher_word凭证字表建表
  - P0-003-008-004-001 编写fin_voucher_word凭证字表DDL（CREATE TABLE fin_voucher_word凭证字+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-008-004-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-008-004-001-002 编写fin_voucher_word凭证字表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-008-004-001-003 验证编写fin_voucher_word凭证字表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-008-005 fin_accounting_period会计期间表建表
  - P0-003-008-005-001 编写fin_accounting_period会计期间表DDL（CREATE TABLE fin_accounting_period会计期间+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-008-005-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-008-005-001-002 编写fin_accounting_period会计期间表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-008-005-001-003 验证编写fin_accounting_period会计期间表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）

### P0-003-009 HRM人力资源表建表
- P0-003-009-001 hrm_employee员工档案表建表
  - P0-003-009-001-001 编写hrm_employee员工档案表DDL（CREATE TABLE hrm_employee员工档案+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-009-001-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-009-001-001-002 编写hrm_employee员工档案表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-009-001-001-003 验证编写hrm_employee员工档案表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-009-002 hrm_recruitment招聘管理表建表
  - P0-003-009-002-001 编写hrm_recruitment招聘管理表DDL（CREATE TABLE hrm_recruitment招聘管理+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-009-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-009-002-001-002 编写hrm_recruitment招聘管理表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-009-002-001-003 验证编写hrm_recruitment招聘管理表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-009-003 hrm_attendance考勤管理表建表
  - P0-003-009-003-001 编写hrm_attendance考勤管理表DDL（CREATE TABLE hrm_attendance考勤管理+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-009-003-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-009-003-001-002 编写hrm_attendance考勤管理表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-009-003-001-003 验证编写hrm_attendance考勤管理表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-009-004 hrm_salary薪资管理表建表
  - P0-003-009-004-001 编写hrm_salary薪资管理表DDL（CREATE TABLE hrm_salary薪资管理+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-009-004-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-009-004-001-002 编写hrm_salary薪资管理表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-009-004-001-003 验证编写hrm_salary薪资管理表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）

### P0-003-010 销售管理表建表
- P0-003-010-001 sale_quotation + sale_quotation_detail报价单主从表建表
  - P0-003-010-001-001 编写sale_quotation_detail报价单主从表DDL（CREATE TABLE sale_quotation_detail报价单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-010-001-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-010-001-001-002 编写sale_quotation_detail报价单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-010-001-001-003 验证编写sale_quotation_detail报价单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-010-002 sale_order + sale_order_detail + sale_order_detail_serial销售订单主从表建表
  - P0-003-010-002-001 编写sale_order_detail_serial销售订单主从表DDL（CREATE TABLE sale_order_detail_serial销售订单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-010-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-010-002-001-002 编写sale_order_detail_serial销售订单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-010-002-001-003 验证编写sale_order_detail_serial销售订单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-010-003 sale_order_change销售订单变更记录表建表
  - P0-003-010-003-001 编写sale_order_change销售订单变更记录表DDL（CREATE TABLE sale_order_change销售订单变更记录+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-010-003-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-010-003-001-002 编写sale_order_change销售订单变更记录表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-010-003-001-003 验证编写sale_order_change销售订单变更记录表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-010-004 sale_delivery_notice + sale_delivery_notice_detail发货通知单主从表建表
  - P0-003-010-004-001 编写sale_delivery_notice_detail发货通知单主从表DDL（CREATE TABLE sale_delivery_notice_detail发货通知单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-010-004-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-010-004-001-002 编写sale_delivery_notice_detail发货通知单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-010-004-001-003 验证编写sale_delivery_notice_detail发货通知单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-010-005 sale_outbound + sale_outbound_detail出库单主从表建表
  - P0-003-010-005-001 编写sale_outbound_detail出库单主从表DDL（CREATE TABLE sale_outbound_detail出库单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-010-005-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-010-005-001-002 编写sale_outbound_detail出库单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-010-005-001-003 验证编写sale_outbound_detail出库单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-010-006 sale_outbound_return + sale_outbound_return_detail出库退货单主从表建表
  - P0-003-010-006-001 编写sale_outbound_return_detail出库退货单主从表DDL（CREATE TABLE sale_outbound_return_detail出库退货单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-010-006-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-010-006-001-002 编写sale_outbound_return_detail出库退货单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-010-006-001-003 验证编写sale_outbound_return_detail出库退货单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-010-007 sale_reconciliation + sale_reconciliation_detail出库对账单主从表建表
  - P0-003-010-007-001 编写sale_reconciliation_detail出库对账单主从表DDL（CREATE TABLE sale_reconciliation_detail出库对账单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-010-007-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-010-007-001-002 编写sale_reconciliation_detail出库对账单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-010-007-001-003 验证编写sale_reconciliation_detail出库对账单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）

### P0-003-011 采购管理表建表
- P0-003-011-001 purchase_inquiry + purchase_inquiry_detail询价单主从表建表
  - P0-003-011-001-001 编写purchase_inquiry_detail询价单主从表DDL（CREATE TABLE purchase_inquiry_detail询价单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-011-001-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-011-001-001-002 编写purchase_inquiry_detail询价单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-011-001-001-003 验证编写purchase_inquiry_detail询价单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-011-002 purchase_requisition + purchase_requisition_detail请购单主从表建表
  - P0-003-011-002-001 编写purchase_requisition_detail请购单主从表DDL（CREATE TABLE purchase_requisition_detail请购单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-011-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-011-002-001-002 编写purchase_requisition_detail请购单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-011-002-001-003 验证编写purchase_requisition_detail请购单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-011-003 purchase_order + purchase_order_detail采购订单主从表建表
  - P0-003-011-003-001 编写purchase_order_detail采购订单主从表DDL（CREATE TABLE purchase_order_detail采购订单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-011-003-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-011-003-001-002 编写purchase_order_detail采购订单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-011-003-001-003 验证编写purchase_order_detail采购订单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-011-004 purchase_order_change采购订单变更记录表建表
  - P0-003-011-004-001 编写purchase_order_change采购订单变更记录表DDL（CREATE TABLE purchase_order_change采购订单变更记录+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-011-004-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-011-004-001-002 编写purchase_order_change采购订单变更记录表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-011-004-001-003 验证编写purchase_order_change采购订单变更记录表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-011-005 purchase_inbound + purchase_inbound_detail入库单主从表建表
  - P0-003-011-005-001 编写purchase_inbound_detail入库单主从表DDL（CREATE TABLE purchase_inbound_detail入库单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-011-005-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-011-005-001-002 编写purchase_inbound_detail入库单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-011-005-001-003 验证编写purchase_inbound_detail入库单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-011-006 purchase_inbound_return + purchase_inbound_return_detail入库退货单主从表建表
  - P0-003-011-006-001 编写purchase_inbound_return_detail入库退货单主从表DDL（CREATE TABLE purchase_inbound_return_detail入库退货单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-011-006-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-011-006-001-002 编写purchase_inbound_return_detail入库退货单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-011-006-001-003 验证编写purchase_inbound_return_detail入库退货单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-011-007 purchase_reconciliation + purchase_reconciliation_detail入库对账单主从表建表
  - P0-003-011-007-001 编写purchase_reconciliation_detail入库对账单主从表DDL（CREATE TABLE purchase_reconciliation_detail入库对账单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-011-007-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-011-007-001-002 编写purchase_reconciliation_detail入库对账单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-011-007-001-003 验证编写purchase_reconciliation_detail入库对账单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）

### P0-003-012 生产管理表建表
- P0-003-012-001 prod_notice + prod_notice_detail生产通知单主从表建表
  - P0-003-012-001-001 编写prod_notice_detail生产通知单主从表DDL（CREATE TABLE prod_notice_detail生产通知单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-012-001-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-012-001-001-002 编写prod_notice_detail生产通知单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-012-001-001-003 验证编写prod_notice_detail生产通知单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-012-002 prod_notice_change生产通知变更记录表建表
  - P0-003-012-002-001 编写prod_notice_change生产通知变更记录表DDL（CREATE TABLE prod_notice_change生产通知变更记录+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-012-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-012-002-001-002 编写prod_notice_change生产通知变更记录表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-012-002-001-003 验证编写prod_notice_change生产通知变更记录表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-012-003 prod_pick + prod_pick_detail生产领料单主从表建表
  - P0-003-012-003-001 编写prod_pick_detail生产领料单主从表DDL（CREATE TABLE prod_pick_detail生产领料单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-012-003-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-012-003-001-002 编写prod_pick_detail生产领料单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-012-003-001-003 验证编写prod_pick_detail生产领料单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-012-004 prod_over_pick + prod_over_pick_detail生产超领单主从表建表
  - P0-003-012-004-001 编写prod_over_pick_detail生产超领单主从表DDL（CREATE TABLE prod_over_pick_detail生产超领单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-012-004-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-012-004-001-002 编写prod_over_pick_detail生产超领单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-012-004-001-003 验证编写prod_over_pick_detail生产超领单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-012-005 prod_return + prod_return_detail生产退料单主从表建表
  - P0-003-012-005-001 编写prod_return_detail生产退料单主从表DDL（CREATE TABLE prod_return_detail生产退料单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-012-005-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-012-005-001-002 编写prod_return_detail生产退料单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-012-005-001-003 验证编写prod_return_detail生产退料单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-012-006 prod_stock_in + prod_stock_in_detail生产入库单主从表建表
  - P0-003-012-006-001 编写prod_stock_in_detail生产入库单主从表DDL（CREATE TABLE prod_stock_in_detail生产入库单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-012-006-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-012-006-001-002 编写prod_stock_in_detail生产入库单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-012-006-001-003 验证编写prod_stock_in_detail生产入库单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-012-007 prod_stock_return + prod_stock_return_detail生产退库单主从表建表
  - P0-003-012-007-001 编写prod_stock_return_detail生产退库单主从表DDL（CREATE TABLE prod_stock_return_detail生产退库单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-012-007-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-012-007-001-002 编写prod_stock_return_detail生产退库单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-012-007-001-003 验证编写prod_stock_return_detail生产退库单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-012-008 prod_process_handover生产工序移交单表建表
  - P0-003-012-008-001 编写prod_process_handover生产工序移交单表DDL（CREATE TABLE prod_process_handover生产工序移交单+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-012-008-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-012-008-001-002 编写prod_process_handover生产工序移交单表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-012-008-001-003 验证编写prod_process_handover生产工序移交单表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-012-009 prod_process_receive生产工序接收单表建表
  - P0-003-012-009-001 编写prod_process_receive生产工序接收单表DDL（CREATE TABLE prod_process_receive生产工序接收单+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-012-009-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-012-009-001-002 编写prod_process_receive生产工序接收单表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-012-009-001-003 验证编写prod_process_receive生产工序接收单表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）

### P0-003-013 委外管理表建表
- P0-003-013-001 sub_notice + sub_notice_detail委外通知单主从表建表
  - P0-003-013-001-001 编写sub_notice_detail委外通知单主从表DDL（CREATE TABLE sub_notice_detail委外通知单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-013-001-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-013-001-001-002 编写sub_notice_detail委外通知单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-013-001-001-003 验证编写sub_notice_detail委外通知单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-013-002 sub_notice_change委外通知变更记录表建表
  - P0-003-013-002-001 编写sub_notice_change委外通知变更记录表DDL（CREATE TABLE sub_notice_change委外通知变更记录+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-013-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-013-002-001-002 编写sub_notice_change委外通知变更记录表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-013-002-001-003 验证编写sub_notice_change委外通知变更记录表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-013-003 sub_pick + sub_pick_detail委外领料单主从表建表
  - P0-003-013-003-001 编写sub_pick_detail委外领料单主从表DDL（CREATE TABLE sub_pick_detail委外领料单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-013-003-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-013-003-001-002 编写sub_pick_detail委外领料单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-013-003-001-003 验证编写sub_pick_detail委外领料单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-013-004 sub_stock_in + sub_stock_in_detail委外入库单主从表建表
  - P0-003-013-004-001 编写sub_stock_in_detail委外入库单主从表DDL（CREATE TABLE sub_stock_in_detail委外入库单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-013-004-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-013-004-001-002 编写sub_stock_in_detail委外入库单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-013-004-001-003 验证编写sub_stock_in_detail委外入库单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-013-005 sub_stock_return委外退库单表建表
  - P0-003-013-005-001 编写sub_stock_return委外退库单表DDL（CREATE TABLE sub_stock_return委外退库单+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-013-005-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-013-005-001-002 编写sub_stock_return委外退库单表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-013-005-001-003 验证编写sub_stock_return委外退库单表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）

### P0-003-014 应收应付表建表
- P0-003-014-001 ar_receipt + ar_receipt_detail收款单主从表建表
  - P0-003-014-001-001 编写ar_receipt_detail收款单主从表DDL（CREATE TABLE ar_receipt_detail收款单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-014-001-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-014-001-001-002 编写ar_receipt_detail收款单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-014-001-001-003 验证编写ar_receipt_detail收款单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-014-002 ar_receipt_write_off + ar_receipt_write_off_detail收款核销单主从表建表
  - P0-003-014-002-001 编写ar_receipt_write_off_detail收款核销单主从表DDL（CREATE TABLE ar_receipt_write_off_detail收款核销单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-014-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-014-002-001-002 编写ar_receipt_write_off_detail收款核销单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-014-002-001-003 验证编写ar_receipt_write_off_detail收款核销单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-014-003 ar_invoice + ar_invoice_detail销项发票主从表建表
  - P0-003-014-003-001 编写ar_invoice_detail销项发票主从表DDL（CREATE TABLE ar_invoice_detail销项发票主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-014-003-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-014-003-001-002 编写ar_invoice_detail销项发票主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-014-003-001-003 验证编写ar_invoice_detail销项发票主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-014-004 ar_invoice_write_off销项发票核销单表建表
  - P0-003-014-004-001 编写ar_invoice_write_off销项发票核销单表DDL（CREATE TABLE ar_invoice_write_off销项发票核销单+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-014-004-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-014-004-001-002 编写ar_invoice_write_off销项发票核销单表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-014-004-001-003 验证编写ar_invoice_write_off销项发票核销单表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-014-005 ar_other_receipt其他应收单表建表
  - P0-003-014-005-001 编写ar_other_receipt其他应收单表DDL（CREATE TABLE ar_other_receipt其他应收单+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-014-005-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-014-005-001-002 编写ar_other_receipt其他应收单表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-014-005-001-003 验证编写ar_other_receipt其他应收单表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-014-006 ap_payment + ap_payment_detail付款单主从表建表
  - P0-003-014-006-001 编写ap_payment_detail付款单主从表DDL（CREATE TABLE ap_payment_detail付款单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-014-006-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-014-006-001-002 编写ap_payment_detail付款单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-014-006-001-003 验证编写ap_payment_detail付款单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-014-007 ap_payment_write_off + ap_payment_write_off_detail付款核销单主从表建表
  - P0-003-014-007-001 编写ap_payment_write_off_detail付款核销单主从表DDL（CREATE TABLE ap_payment_write_off_detail付款核销单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-014-007-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-014-007-001-002 编写ap_payment_write_off_detail付款核销单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-014-007-001-003 验证编写ap_payment_write_off_detail付款核销单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-014-008 ap_invoice + ap_invoice_detail进项发票主从表建表
  - P0-003-014-008-001 编写ap_invoice_detail进项发票主从表DDL（CREATE TABLE ap_invoice_detail进项发票主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-014-008-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-014-008-001-002 编写ap_invoice_detail进项发票主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-014-008-001-003 验证编写ap_invoice_detail进项发票主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-014-009 ap_invoice_write_off进项发票核销单表建表
  - P0-003-014-009-001 编写ap_invoice_write_off进项发票核销单表DDL（CREATE TABLE ap_invoice_write_off进项发票核销单+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-014-009-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-014-009-001-002 编写ap_invoice_write_off进项发票核销单表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-014-009-001-003 验证编写ap_invoice_write_off进项发票核销单表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-014-010 ap_other_payment其他应付单表建表
  - P0-003-014-010-001 编写ap_other_payment其他应付单表DDL（CREATE TABLE ap_other_payment其他应付单+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-014-010-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-014-010-001-002 编写ap_other_payment其他应付单表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-014-010-001-003 验证编写ap_other_payment其他应付单表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）

### P0-003-015 凭证与账簿表建表
- P0-003-015-001 fin_voucher + fin_voucher_detail凭证主从表建表
  - P0-003-015-001-001 编写fin_voucher_detail凭证主从表DDL（CREATE TABLE fin_voucher_detail凭证主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-015-001-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-015-001-001-002 编写fin_voucher_detail凭证主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-015-001-001-003 验证编写fin_voucher_detail凭证主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-015-002 fin_voucher_template + fin_voucher_template_detail凭证模板主从表建表
  - P0-003-015-002-001 编写fin_voucher_template_detail凭证模板主从表DDL（CREATE TABLE fin_voucher_template_detail凭证模板主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-015-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-015-002-001-002 编写fin_voucher_template_detail凭证模板主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-015-002-001-003 验证编写fin_voucher_template_detail凭证模板主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）

### P0-003-016 扩展业务表建表
- P0-003-016-001 exp_expense + exp_expense_detail费用报销主从表建表
  - P0-003-016-001-001 编写exp_expense_detail费用报销主从表DDL（CREATE TABLE exp_expense_detail费用报销主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-016-001-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-016-001-001-002 编写exp_expense_detail费用报销主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-016-001-001-003 验证编写exp_expense_detail费用报销主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-016-002 exp_budget费用预算表建表
  - P0-003-016-002-001 编写exp_budget费用预算表DDL（CREATE TABLE exp_budget费用预算+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-016-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-016-002-001-002 编写exp_budget费用预算表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-016-002-001-003 验证编写exp_budget费用预算表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-016-003 fa_asset固定资产卡片表建表
  - P0-003-016-003-001 编写fa_asset固定资产卡片表DDL（CREATE TABLE fa_asset固定资产卡片+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-016-003-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-016-003-001-002 编写fa_asset固定资产卡片表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-016-003-001-003 验证编写fa_asset固定资产卡片表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-016-004 fa_depreciation资产折旧表建表
  - P0-003-016-004-001 编写fa_depreciation资产折旧表DDL（CREATE TABLE fa_depreciation资产折旧+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-016-004-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-016-004-001-002 编写fa_depreciation资产折旧表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-016-004-001-003 验证编写fa_depreciation资产折旧表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-016-005 fa_change资产变动表建表
  - P0-003-016-005-001 编写fa_change资产变动表DDL（CREATE TABLE fa_change资产变动+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-016-005-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-016-005-001-002 编写fa_change资产变动表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-016-005-001-003 验证编写fa_change资产变动表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-016-006 fa_disposal资产清理处置表建表
  - P0-003-016-006-001 编写fa_disposal资产清理处置表DDL（CREATE TABLE fa_disposal资产清理处置+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-016-006-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-016-006-001-002 编写fa_disposal资产清理处置表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-016-006-001-003 验证编写fa_disposal资产清理处置表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-016-007 sample_apply + sample_apply_detail样品申请主从表建表
  - P0-003-016-007-001 编写sample_apply_detail样品申请主从表DDL（CREATE TABLE sample_apply_detail样品申请主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-016-007-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-016-007-001-002 编写sample_apply_detail样品申请主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-016-007-001-003 验证编写sample_apply_detail样品申请主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-016-008 sample_delivery样品发货表建表
  - P0-003-016-008-001 编写sample_delivery样品发货表DDL（CREATE TABLE sample_delivery样品发货+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-016-008-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-016-008-001-002 编写sample_delivery样品发货表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-016-008-001-003 验证编写sample_delivery样品发货表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-016-009 sample_return样品归还表建表
  - P0-003-016-009-001 编写sample_return样品归还表DDL（CREATE TABLE sample_return样品归还+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-016-009-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-016-009-001-002 编写sample_return样品归还表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-016-009-001-003 验证编写sample_return样品归还表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-016-010 borrow_apply + borrow_apply_detail借用申请主从表建表
  - P0-003-016-010-001 编写borrow_apply_detail借用申请主从表DDL（CREATE TABLE borrow_apply_detail借用申请主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-016-010-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-016-010-001-002 编写borrow_apply_detail借用申请主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-016-010-001-003 验证编写borrow_apply_detail借用申请主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-016-011 borrow_out借用出库表建表
  - P0-003-016-011-001 编写borrow_out借用出库表DDL（CREATE TABLE borrow_out借用出库+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-016-011-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-016-011-001-002 编写borrow_out借用出库表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-016-011-001-003 验证编写borrow_out借用出库表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-016-012 borrow_return借用归还表建表
  - P0-003-016-012-001 编写borrow_return借用归还表DDL（CREATE TABLE borrow_return借用归还+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-016-012-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-016-012-001-002 编写borrow_return借用归还表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-016-012-001-003 验证编写borrow_return借用归还表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-016-013 lease_contract租赁合同表建表
  - P0-003-016-013-001 编写lease_contract租赁合同表DDL（CREATE TABLE lease_contract租赁合同+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-016-013-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-016-013-001-002 编写lease_contract租赁合同表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-016-013-001-003 验证编写lease_contract租赁合同表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-016-014 lease_bill租赁计费表建表
  - P0-003-016-014-001 编写lease_bill租赁计费表DDL（CREATE TABLE lease_bill租赁计费+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-016-014-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-016-014-001-002 编写lease_bill租赁计费表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-016-014-001-003 验证编写lease_bill租赁计费表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-016-015 after_sale + after_sale_detail售后单主从表建表
  - P0-003-016-015-001 编写after_sale_detail售后单主从表DDL（CREATE TABLE after_sale_detail售后单主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-016-015-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-016-015-001-002 编写after_sale_detail售后单主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-016-015-001-003 验证编写after_sale_detail售后单主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-016-016 after_sale_return售后退货表建表
  - P0-003-016-016-001 编写after_sale_return售后退货表DDL（CREATE TABLE after_sale_return售后退货+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-016-016-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-016-016-001-002 编写after_sale_return售后退货表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-016-016-001-003 验证编写after_sale_return售后退货表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-016-017 after_sale_exchange售后换货表建表
  - P0-003-016-017-001 编写after_sale_exchange售后换货表DDL（CREATE TABLE after_sale_exchange售后换货+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-016-017-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-016-017-001-002 编写after_sale_exchange售后换货表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-016-017-001-003 验证编写after_sale_exchange售后换货表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）

### P0-003-017 审批流程表建表
- P0-003-017-001 approval_definition审批定义表建表
  - P0-003-017-001-001 编写approval_definition审批定义表DDL（CREATE TABLE approval_definition审批定义+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-017-001-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-017-001-001-002 编写approval_definition审批定义表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-017-001-001-003 验证编写approval_definition审批定义表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-017-002 approval_instance审批实例表建表
  - P0-003-017-002-001 编写approval_instance审批实例表DDL（CREATE TABLE approval_instance审批实例+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-017-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-017-002-001-002 编写approval_instance审批实例表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-017-002-001-003 验证编写approval_instance审批实例表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-017-003 approval_record审批记录表建表
  - P0-003-017-003-001 编写approval_record审批记录表DDL（CREATE TABLE approval_record审批记录+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-017-003-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-017-003-001-002 编写approval_record审批记录表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-017-003-001-003 验证编写approval_record审批记录表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）

### P0-003-018 消息管理表建表
- P0-003-018-001 msg_message消息表建表
  - P0-003-018-001-001 编写msg_message消息表DDL（CREATE TABLE msg_message消息+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-018-001-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-018-001-001-002 编写msg_message消息表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-018-001-001-003 验证编写msg_message消息表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-018-002 msg_template消息模板表建表
  - P0-003-018-002-001 编写msg_template消息模板表DDL（CREATE TABLE msg_template消息模板+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-018-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-018-002-001-002 编写msg_template消息模板表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-018-002-001-003 验证编写msg_template消息模板表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-018-003 msg_type消息类型表建表
  - P0-003-018-003-001 编写msg_type消息类型表DDL（CREATE TABLE msg_type消息类型+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-018-003-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-018-003-001-002 编写msg_type消息类型表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-018-003-001-003 验证编写msg_type消息类型表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-018-004 msg_todo单据待办表建表
  - P0-003-018-004-001 编写msg_todo单据待办表DDL（CREATE TABLE msg_todo单据待办+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-018-004-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-018-004-001-002 编写msg_todo单据待办表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-018-004-001-003 验证编写msg_todo单据待办表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-018-005 msg_push_config消息推送配置表建表
  - P0-003-018-005-001 编写msg_push_config消息推送配置表DDL（CREATE TABLE msg_push_config消息推送配置+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-018-005-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-018-005-001-002 编写msg_push_config消息推送配置表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-018-005-001-003 验证编写msg_push_config消息推送配置表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）

### P0-003-019 OA/运维/多语言/模板等辅助表建表
- P0-003-019-001 oa_schedule日程表建表
  - P0-003-019-001-001 编写oa_schedule日程表DDL（CREATE TABLE oa_schedule日程+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-019-001-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-019-001-001-002 编写oa_schedule日程表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-019-001-001-003 验证编写oa_schedule日程表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-019-002 oa_announcement公告表建表
  - P0-003-019-002-001 编写oa_announcement公告表DDL（CREATE TABLE oa_announcement公告+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-019-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-019-002-001-002 编写oa_announcement公告表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-019-002-001-003 验证编写oa_announcement公告表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-019-003 oa_meeting会议表建表
  - P0-003-019-003-001 编写oa_meeting会议表DDL（CREATE TABLE oa_meeting会议+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-019-003-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-019-003-001-002 编写oa_meeting会议表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-019-003-001-003 验证编写oa_meeting会议表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-019-004 oa_meeting_room会议室表建表
  - P0-003-019-004-001 编写oa_meeting_room会议室表DDL（CREATE TABLE oa_meeting_room会议室+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-019-004-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-019-004-001-002 编写oa_meeting_room会议室表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-019-004-001-003 验证编写oa_meeting_room会议室表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-019-005 oa_vehicle用车表建表
  - P0-003-019-005-001 编写oa_vehicle用车表DDL（CREATE TABLE oa_vehicle用车+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-019-005-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-019-005-001-002 编写oa_vehicle用车表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-019-005-001-003 验证编写oa_vehicle用车表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-019-006 ops_scheduled_task定时任务表建表
  - P0-003-019-006-001 编写ops_scheduled_task定时任务表DDL（CREATE TABLE ops_scheduled_task定时任务+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-019-006-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-019-006-001-002 编写ops_scheduled_task定时任务表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-019-006-001-003 验证编写ops_scheduled_task定时任务表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-019-007 ops_task_log任务执行日志表建表
  - P0-003-019-007-001 编写ops_task_log任务执行日志表DDL（CREATE TABLE ops_task_log任务执行日志+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-019-007-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-019-007-001-002 编写ops_task_log任务执行日志表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-019-007-001-003 验证编写ops_task_log任务执行日志表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-019-008 ops_backup数据备份表建表
  - P0-003-019-008-001 编写ops_backup数据备份表DDL（CREATE TABLE ops_backup数据备份+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-019-008-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-019-008-001-002 编写ops_backup数据备份表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-019-008-001-003 验证编写ops_backup数据备份表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-019-009 ops_alert运维告警表建表
  - P0-003-019-009-001 编写ops_alert运维告警表DDL（CREATE TABLE ops_alert运维告警+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-019-009-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-019-009-001-002 编写ops_alert运维告警表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-019-009-001-003 验证编写ops_alert运维告警表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-019-010 i18n_language语言定义表建表
  - P0-003-019-010-001 编写i18n_language语言定义表DDL（CREATE TABLE i18n_language语言定义+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-019-010-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-019-010-001-002 编写i18n_language语言定义表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-019-010-001-003 验证编写i18n_language语言定义表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-019-011 i18n_translation_key + i18n_translation_value翻译主从表建表
  - P0-003-019-011-001 编写i18n_translation_value翻译主从表DDL（CREATE TABLE i18n_translation_value翻译主从+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-019-011-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-019-011-001-002 编写i18n_translation_value翻译主从表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-019-011-001-003 验证编写i18n_translation_value翻译主从表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-019-012 tpl_template模板表建表
  - P0-003-019-012-001 编写tpl_template模板表DDL（CREATE TABLE tpl_template模板+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-019-012-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-019-012-001-002 编写tpl_template模板表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-019-012-001-003 验证编写tpl_template模板表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-019-013 tpl_template_class模板分类表建表
  - P0-003-019-013-001 编写tpl_template_class模板分类表DDL（CREATE TABLE tpl_template_class模板分类+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-019-013-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-019-013-001-002 编写tpl_template_class模板分类表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-019-013-001-003 验证编写tpl_template_class模板分类表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-019-014 tpl_print_template打印模板表建表
  - P0-003-019-014-001 编写tpl_print_template打印模板表DDL（CREATE TABLE tpl_print_template打印模板+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-019-014-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-019-014-001-002 编写tpl_print_template打印模板表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-019-014-001-003 验证编写tpl_print_template打印模板表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-019-015 rpt_report报表表建表
  - P0-003-019-015-001 编写rpt_report报表表DDL（CREATE TABLE rpt_report报表+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-019-015-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-019-015-001-002 编写rpt_report报表表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-019-015-001-003 验证编写rpt_report报表表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-019-016 rpt_report_class报表分类表建表
  - P0-003-019-016-001 编写rpt_report_class报表分类表DDL（CREATE TABLE rpt_report_class报表分类+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-019-016-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-019-016-001-002 编写rpt_report_class报表分类表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-019-016-001-003 验证编写rpt_report_class报表分类表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-019-017 cost_calculate成本核算表建表
  - P0-003-019-017-001 编写cost_calculate成本核算表DDL（CREATE TABLE cost_calculate成本核算+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-019-017-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-019-017-001-002 编写cost_calculate成本核算表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-019-017-001-003 验证编写cost_calculate成本核算表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）

### P0-003-020 期初管理表建表
- P0-003-020-001 init_stock期初库存表建表
  - P0-003-020-001-001 编写init_stock期初库存表DDL（CREATE TABLE init_stock期初库存+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-020-001-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-020-001-001-002 编写init_stock期初库存表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-020-001-001-003 验证编写init_stock期初库存表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-020-002 init_ar期初应收表建表
  - P0-003-020-002-001 编写init_ar期初应收表DDL（CREATE TABLE init_ar期初应收+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-020-002-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-020-002-001-002 编写init_ar期初应收表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-020-002-001-003 验证编写init_ar期初应收表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-020-003 init_ap期初应付表建表
  - P0-003-020-003-001 编写init_ap期初应付表DDL（CREATE TABLE init_ap期初应付+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-020-003-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-020-003-001-002 编写init_ap期初应付表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-020-003-001-003 验证编写init_ap期初应付表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-020-004 init_fixed_asset期初固定资产表建表
  - P0-003-020-004-001 编写init_fixed_asset期初固定资产表DDL（CREATE TABLE init_fixed_asset期初固定资产+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-020-004-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-020-004-001-002 编写init_fixed_asset期初固定资产表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-020-004-001-003 验证编写init_fixed_asset期初固定资产表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）
- P0-003-020-005 init_account_balance期初科目余额表建表
  - P0-003-020-005-001 编写init_account_balance期初科目余额表DDL（CREATE TABLE init_account_balance期初科目余额+字段定义+数据类型+默认值+非空约束+注释）
    - P0-003-020-005-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P0-003-020-005-001-002 编写init_account_balance期初科目余额表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P0-003-020-005-001-003 验证编写init_account_balance期初科目余额表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）

### P0-003-021 系统初始化数据
- P0-003-021-001 超级管理员账号与默认角色初始化
  - P0-003-021-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-003-021-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-003-021-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-003-021-002 系统预置参数初始化（全部参数清单见设计文档11.35.1，共12个分类约60个参数）
  - P0-003-021-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-003-021-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-003-021-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-003-021-003 系统预置数据字典初始化
  - P0-003-021-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-003-021-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-003-021-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-003-021-004 系统预置编码规则初始化
  - P0-003-021-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-003-021-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-003-021-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-003-021-005 系统预置菜单数据初始化（39个一级模块菜单+283个子功能点菜单）
  - P0-003-021-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-003-021-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-003-021-005-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-003-021-006 系统预置数据视图初始化
  - P0-003-021-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-003-021-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-003-021-006-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P0-003-022 Flyway数据库迁移管理

> **说明**：Flyway提供版本化的数据库变更管理，确保多环境数据库Schema一致性，支持迁移脚本的自动执行与回滚。

- P0-003-022-001 Flyway依赖引入与基础配置
  - P0-003-022-001-001 Flyway Maven依赖引入（flyway-core + flyway-database-postgresql + spring-boot集成）
    - P0-003-022-001-001-001 添加Flyway依赖（pom.xml/flayway-core+postgresql+spring-boot-starter整合）
    - P0-003-022-001-001-002 验证Flyway可用（启动自动迁移+版本表创建+迁移历史记录）
  - P0-003-022-001-002 Flyway基础配置（application.yml：enabled/locations/baseline-on-migrate/clean-disabled）
    - P0-003-022-001-002-001 配置Flyway参数（开启自动迁移+指定脚本路径+禁止clean+baseline策略）
    - P0-003-022-001-002-002 验证配置生效（迁移脚本自动执行+版本表baseline+clean阻止确认）
- P0-003-022-002 迁移脚本目录结构与命名规范
  - P0-003-022-002-001 创建迁移脚本目录（src/main/resources/db/migration + 版本号命名规则V1.0.0__xxx.sql）
    - P0-003-022-002-001-001 创建目录结构（db/migration目录+README说明+版本号前缀规范+描述命名规则）
    - P0-003-022-002-001-002 编写命名规范文档（V<major>.<minor>.<patch>__<description>.sql+回滚脚本R__前缀+可重复脚本R__前缀）
  - P0-003-022-002-002 初始化基线脚本（V1.0.0__init_schema.sql：整合P0-003-001~P0-003-020全部DDL）
    - P0-003-022-002-002-001 编写基线迁移脚本（整合全部建表DDL+索引+约束+注释，按模块分组+事务控制）
    - P0-003-022-002-002-002 编写种子数据脚本（V1.0.1__init_data.sql：系统预置参数+数据字典+编码规则+菜单数据+视图配置）
- P0-003-022-003 多环境迁移策略
  - P0-003-022-003-001 dev环境自动迁移配置（启动时自动执行迁移+失败快速终止+基线初始化）
    - P0-003-022-003-001-001 配置dev环境Flyway（自动迁移+baseline-on-migrate=true+失败fast-fail）
    - P0-003-022-003-001-002 验证dev迁移（启动自动执行+版本表正确+数据初始完整）
  - P0-003-022-003-002 test/staging/prod环境迁移配置（手动触发或CI集成+迁移前备份+验证步骤）
    - P0-003-022-003-002-001 配置生产环境Flyway（手动迁移+迁移前备份检查+validate-on-migrate=true）
    - P0-003-022-003-002-002 编写迁移SOP文档（备份→执行→验证→回滚步骤+异常处理流程）

---

## P0-004 认证与权限基础开发

> **依赖**：P0-001、P0-003  
> **概述**：完成登录认证、用户管理、角色权限的完整后端服务与前端页面

### P0-004-001 登录认证后端开发
- P0-004-001-001 登录接口（用户名+密码登录，Sa-Token签发Token，返回用户信息+权限标识）
  - P0-004-001-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-001-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-001-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-001-002 退出登录接口（Sa-Token注销Token）
  - P0-004-001-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-001-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-001-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-001-003 Token校验与刷新接口（Sa-Token心跳续期+双Token刷新机制）
  - P0-004-001-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-001-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-001-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-001-004 获取当前用户信息接口（含菜单权限树+按钮权限标识列表+字段权限配置）
  - P0-004-001-004-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P0-004-001-004-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P0-004-001-004-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P0-004-001-005 登录日志记录（登录成功/失败异步写入sys_login_log）
  - P0-004-001-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-001-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-001-005-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P0-004-002 用户管理后端开发
- P0-004-002-001 sys_user Entity/DTO/VO定义
  - P0-004-002-001-001 编写Entity/DTO/VO类
    - P0-004-002-001-001-001 编写Entity类（@TableName+@TableId+@TableField+字段+注解+Lombok）
    - P0-004-002-001-001-002 编写DTO/VO类（请求DTO+响应VO+@Valid注解+字段校验+MapStruct转换）
    - P0-004-002-001-001-003 验证实体类（字段完整+注解正确+MapStruct转换+序列化）
- P0-004-002-002 UserMapper开发（含自定义SQL：按部门/角色/状态查询）
  - P0-004-002-002-001 编写SQL+验证
    - P0-004-002-002-001-001 编写SQL语句（表关联+筛选条件+聚合函数+排序+分页）
    - P0-004-002-002-001-002 验证编写SQL（数据正确+性能合理+索引命中+边界处理）
- P0-004-002-003 UserService开发（CRUD + 软删除 + 密码BCrypt加密 + 唯一性校验）
  - P0-004-002-003-001 Service接口定义+实现类编写
    - P0-004-002-003-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-004-002-003-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-004-002-003-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-004-002-004 用户角色分配Service（sys_user_role批量绑定/解绑）
  - P0-004-002-004-001 Service接口定义+实现类编写
    - P0-004-002-004-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-004-002-004-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-004-002-004-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-004-002-005 用户部门关联Service（sys_user_dept批量绑定/解绑）
  - P0-004-002-005-001 Service接口定义+实现类编写
    - P0-004-002-005-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-004-002-005-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-004-002-005-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-004-002-006 用户组CRUD Service + 成员管理Service
  - P0-004-002-006-001 Service接口定义+实现类编写
    - P0-004-002-006-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-004-002-006-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-004-002-006-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-004-002-007 密码修改与重置Service（旧密码校验/管理员强制重置/密码策略校验）
  - P0-004-002-007-001 Service接口定义+实现类编写
    - P0-004-002-007-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-004-002-007-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-004-002-007-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-004-002-008 UserController开发（全部用户管理RESTful接口）
  - P0-004-002-008-001 Controller类编写+接口定义
    - P0-004-002-008-001-001 编写Controller类（@RestController+@RequestMapping+注入Service+统一返回Result）
    - P0-004-002-008-001-002 编写接口方法（GET/POST/PUT/DELETE+@RequestBody/@PathVariable+参数校验+Swagger注解）
    - P0-004-002-008-001-003 验证Controller（接口调用成功+参数校验+权限校验+返回格式正确）
- P0-004-002-009 用户管理工作台聚合数据接口（DV_USRWB001：用户总数/活跃用户数/待审批权限申请数/最近登录统计）
  - P0-004-002-009-001 实现核心逻辑（多维度聚合SQL+用户活跃度统计+权限申请待办汇总）
    - P0-004-002-009-001-001 编写工作台聚合SQL（sys_user COUNT+active_status统计+sys_login_log近期登录+待审批申请数）
    - P0-004-002-009-001-002 验证功能（各维度统计数据正确+空数据场景+性能合理）

### P0-004-003 权限配置后端开发
- P0-004-003-001 角色CRUD Service（sys_role含角色编码唯一性+角色状态管理）
  - P0-004-003-001-001 Service接口定义+实现类编写
    - P0-004-003-001-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-004-003-001-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-004-003-001-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-004-003-002 菜单权限配置Service（sys_role_menu批量绑定/解绑+按角色查询菜单树）
  - P0-004-003-002-001 Service接口定义+实现类编写
    - P0-004-003-002-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-004-003-002-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-004-003-002-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-004-003-003 数据权限配置Service（sys_role_data_scope按角色绑定数据范围）
  - P0-004-003-003-001 Service接口定义+实现类编写
    - P0-004-003-003-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-004-003-003-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-004-003-003-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-004-003-004 字段权限配置Service（sys_role_field_permission按角色+菜单绑定字段权限）
  - P0-004-003-004-001 Service接口定义+实现类编写
    - P0-004-003-004-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-004-003-004-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-004-003-004-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-004-003-005 按钮权限校验Service（Sa-Token权限标识匹配+缓存）
  - P0-004-003-005-001 Service接口定义+实现类编写
    - P0-004-003-005-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-004-003-005-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-004-003-005-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-004-003-006 权限配置Controller开发（全部权限管理RESTful接口）
  - P0-004-003-006-001 Controller类编写+接口定义
    - P0-004-003-006-001-001 编写Controller类（@RestController+@RequestMapping+注入Service+统一返回Result）
    - P0-004-003-006-001-002 编写接口方法（GET/POST/PUT/DELETE+@RequestBody/@PathVariable+参数校验+Swagger注解）
    - P0-004-003-006-001-003 验证Controller（接口调用成功+参数校验+权限校验+返回格式正确）
- P0-004-003-007 角色继承与互斥角色（角色继承树：子角色自动拥有父角色权限+互斥角色约束：同一用户不可同时拥有互斥角色+如采购员与审批人互斥+出纳与会计互斥）
  - P0-004-003-007-001 后端开发（sys_role_inheritance表+sys_role_exclusion表+继承解析Service+互斥校验）
    - P0-004-003-007-001-001 编写DDL+Entity/Mapper/Service/Controller（继承关系CRUD+互斥约束CRUD+权限继承计算+互斥校验）
- P0-004-003-008 数据权限方案配置（数据权限方案：按部门/本人/部门及下属/全部+方案可复用于多角色+sys_data_permission_scheme表+scheme_detail表+方案→角色绑定）
  - P0-004-003-008-001 后端开发（数据权限方案CRUD+方案明细+方案→角色绑定+数据范围解析）
    - P0-004-003-008-001-001 编写DDL+Entity/Mapper/Service/Controller（方案CRUD+明细+绑定+解析+SQL注入拦截）
- P0-004-003-009 字段权限方案配置（字段权限方案：按角色控制字段可见/可编辑/隐藏+方案→角色+字段→权限+sys_field_permission_scheme表+scheme_detail表）
  - P0-004-003-009-001 后端开发（字段权限方案CRUD+方案明细+方案→角色绑定+字段权限解析）
    - P0-004-003-009-001-001 编写DDL+Entity/Mapper/Service/Controller（方案CRUD+明细+绑定+解析+前端字段控制API）

### P0-004-004 菜单管理后端开发
- P0-004-004-001 菜单CRUD Service（sys_menu含树形排序+is_authorized平台授权字段）
  - P0-004-004-001-001 Service接口定义+实现类编写
    - P0-004-004-001-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-004-004-001-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-004-004-001-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-004-004-002 移动端菜单CRUD Service（sys_mobile_menu）
  - P0-004-004-002-001 Service接口定义+实现类编写
    - P0-004-004-002-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-004-004-002-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-004-004-002-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-004-004-003 菜单树加载Service（按角色权限过滤+平台类型过滤）
  - P0-004-004-003-001 Service接口定义+实现类编写
    - P0-004-004-003-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-004-004-003-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-004-004-003-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-004-004-004 MenuController开发
  - P0-004-004-004-001 Controller类编写+接口定义
    - P0-004-004-004-001-001 编写Controller类（@RestController+@RequestMapping+注入Service+统一返回Result）
    - P0-004-004-004-001-002 编写接口方法（GET/POST/PUT/DELETE+@RequestBody/@PathVariable+参数校验+Swagger注解）
    - P0-004-004-004-001-003 验证Controller（接口调用成功+参数校验+权限校验+返回格式正确）

### P0-004-005 认证配置后端开发
- P0-004-005-001 认证方式配置Service（密码/短信/OAuth2/单点登录方式存入sys_auth_config）
  - P0-004-005-001-001 Service接口定义+实现类编写
    - P0-004-005-001-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-004-005-001-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-004-005-001-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-004-005-002 密码策略配置Service（密码长度/复杂度/有效期/历史密码存入sys_password_policy）
  - P0-004-005-002-001 Service接口定义+实现类编写
    - P0-004-005-002-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-004-005-002-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-004-005-002-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-004-005-003 AuthConfigController开发
  - P0-004-005-003-001 Controller类编写+接口定义
    - P0-004-005-003-001-001 编写Controller类（@RestController+@RequestMapping+注入Service+统一返回Result）
    - P0-004-005-003-001-002 编写接口方法（GET/POST/PUT/DELETE+@RequestBody/@PathVariable+参数校验+Swagger注解）
    - P0-004-005-003-001-003 验证Controller（接口调用成功+参数校验+权限校验+返回格式正确）
- P0-004-005-004 在线设备查询Service（auth_online_device：按用户/设备类型/状态查询在线会话+Sa-Token会话列表同步+强制下线）
  - P0-004-005-004-001 Service接口定义+实现类编写
    - P0-004-005-004-001-001 编写OnlineDeviceService（查询在线设备列表+设备类型/OS/浏览器/IP/登录时间/最后活跃时间+Sa-Token getSessionById同步+管理员强制踢出）
    - P0-004-005-004-001-002 编写ServiceImpl实现类（Sa-Token StpUtil.searchSessionId遍历+auth_online_device表读写+设备信息解析+踢出后状态更新）
    - P0-004-005-004-001-003 验证Service（在线列表正确+设备信息完整+强制踢出生效+状态同步）
- P0-004-005-005 认证配置工作台聚合数据接口（认证方式数量/在线用户数/SSO配置数/OAuth2 Provider数/登录失败统计）
  - P0-004-005-005-001 实现核心逻辑（多维度聚合SQL+认证配置概览+在线统计）
    - P0-004-005-005-001-001 编写工作台聚合SQL（sys_auth_config COUNT+auth_online_device活跃统计+auth_sso_config/auth_oauth2_config配置数+近7天登录失败趋势）
    - P0-004-005-005-001-002 验证功能（各维度统计数据正确+空数据场景+性能合理）

### P0-004-006 前端登录页面开发
- P0-004-006-001 登录页UI开发（用户名/密码/验证码/记住我/布局样式）
  - P0-004-006-001-001 前端页面开发（组件编写+数据绑定+交互逻辑）
    - P0-004-006-001-001-001 编写页面组件（template+搜索区+表格区+操作区+弹窗+表单组件）
    - P0-004-006-001-001-002 编写数据绑定与交互逻辑（API调用+数据绑定+事件处理+路由跳转）
    - P0-004-006-001-001-003 验证前端页面开发页面（渲染正确+交互响应+数据绑定+空状态/加载态）
- P0-004-006-002 登录逻辑开发（调用登录接口/Token存储到localStorage/用户信息存入userStore/路由跳转）
  - P0-004-006-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-006-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-006-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-006-003 退出登录逻辑开发（清除Token/清除Store/跳转登录页）
  - P0-004-006-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-006-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-006-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-006-004 Token刷新逻辑开发（Axios拦截器401自动刷新/并发请求队列）
  - P0-004-006-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-006-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-006-004-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P0-004-007 前端权限体系集成
- P0-004-007-001 路由守卫集成（登录校验+动态菜单加载+权限校验+白名单）
  - P0-004-007-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-007-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-007-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-007-002 usePermission组合式函数（hasBtnPermission按钮权限判断/hasFieldPermission字段权限判断）
  - P0-004-007-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-007-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-007-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-007-003 侧边栏菜单动态渲染（根据permissionStore菜单树渲染+权限过滤）
  - P0-004-007-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-007-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-007-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-007-004 系统参数前端加载（启动时调用/api/system/params一次性拉取存入paramStore供全局使用）
  - P0-004-007-004-001 前端页面开发（组件编写+数据绑定+交互逻辑）
    - P0-004-007-004-001-001 编写页面组件（template+搜索区+表格区+操作区+弹窗+表单组件）
    - P0-004-007-004-001-002 编写数据绑定与交互逻辑（API调用+数据绑定+事件处理+路由跳转）
    - P0-004-007-004-001-003 验证前端页面开发页面（渲染正确+交互响应+数据绑定+空状态/加载态）

### P0-004-008 用户管理前端页面
- P0-004-008-000 用户管理工作台（P02：用户总数统计卡片+活跃用户统计+待审批权限申请+最近登录趋势图）
  - P0-004-008-000-001 实现核心逻辑（P02工作台布局+统计卡片+快捷入口+异常处理）
    - P0-004-008-000-001-001 编写核心代码（P02工作台+统计卡片渲染+快捷入口跳转+日志记录）
    - P0-004-008-000-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-008-001 用户管理列表页（P04单一列表页：查询区+操作区+表格+分页）
  - P0-004-008-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-008-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-008-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-008-002 新增/编辑用户表单页（P07单一表单页：基本信息+角色分配+部门关联）
  - P0-004-008-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-008-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-008-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-008-003 用户角色分配弹窗（穿梭框：左侧可选角色/右侧已分配角色）
  - P0-004-008-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-008-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-008-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-008-004 用户部门关联弹窗（树形选择+部门人员列表）
  - P0-004-008-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-008-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-008-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-008-005 用户组管理列表页（P04）
  - P0-004-008-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-008-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-008-005-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-008-006 用户组新增/编辑表单页（P07 + 成员选择）
  - P0-004-008-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-008-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-008-006-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P0-004-009 权限配置前端页面
- P0-004-009-000 权限配置工作台页（P02：角色数量/用户数量/菜单数量统计卡片+权限覆盖率+数据权限分布图+快捷配置入口）
  - P0-004-009-000-001 工作台KPI卡片区（角色数/用户数/菜单数/权限覆盖率+点击下钻）
    - P0-004-009-000-001-001 KPI卡片组件开发（数值展示+点击下钻路由）
  - P0-004-009-000-002 工作台图表区（权限分布+数据权限方案分布+快捷配置入口）
    - P0-004-009-000-002-001 ECharts图表组件开发（饼图+柱状图+响应式）
  - P0-004-009-000-003 工作台联调验证
    - P0-004-009-000-003-001 工作台全流程联调（数据加载+下钻+刷新+异常处理）
- P0-004-009-001 角色管理列表页（P04）
  - P0-004-009-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-009-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-009-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-009-002 新增/编辑角色表单页（P07）
  - P0-004-009-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-009-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-009-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-009-003 菜单权限配置页（P13左右分栏：左侧角色树+右侧菜单权限矩阵）
  - P0-004-009-003-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P0-004-009-003-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P0-004-009-003-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P0-004-009-004 数据权限配置页（P13左右分栏：左侧角色树+右侧数据范围配置）
  - P0-004-009-004-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P0-004-009-004-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P0-004-009-004-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P0-004-009-005 字段权限配置页（P13左右分栏：左侧角色树+右侧字段权限矩阵）
  - P0-004-009-005-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P0-004-009-005-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P0-004-009-005-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P0-004-009-006 菜单管理页面（P05树形列表页：左侧菜单树+右侧菜单编辑表单）
  - P0-004-009-006-001 前端页面开发（组件编写+数据绑定+交互逻辑）
    - P0-004-009-006-001-001 编写页面组件（template+搜索区+表格区+操作区+弹窗+表单组件）
    - P0-004-009-006-001-002 编写数据绑定与交互逻辑（API调用+数据绑定+事件处理+路由跳转）
    - P0-004-009-006-001-003 验证前端页面开发页面（渲染正确+交互响应+数据绑定+空状态/加载态）

### P0-004-010 认证配置前端页面
- P0-004-010-000 认证配置工作台（P02：认证方式数量统计卡片+在线用户数+SSO/OAuth2配置概览+登录失败趋势图）
  - P0-004-010-000-001 实现核心逻辑（P02工作台布局+统计卡片+快捷入口+异常处理）
    - P0-004-010-000-001-001 编写核心代码（P02工作台+统计卡片渲染+快捷入口跳转+日志记录）
    - P0-004-010-000-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-010-001 认证方式配置页（P13配置页）
  - P0-004-010-001-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P0-004-010-001-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P0-004-010-001-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P0-004-010-002 密码策略配置页（P13配置页）
  - P0-004-010-002-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P0-004-010-002-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P0-004-010-002-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P0-004-010-003 登录日志列表页（P04只读列表页）
  - P0-004-010-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-010-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-010-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-010-004 在线设备列表页（P04：用户/设备类型/OS/浏览器/IP/登录时间/最后活跃/状态+强制下线操作按钮）
  - P0-004-010-004-001 实现核心逻辑（在线设备列表渲染+搜索筛选+强制下线操作+状态实时更新）
    - P0-004-010-004-001-001 编写核心代码（P04列表页+设备信息表格+搜索条件+强制下线确认弹窗+WebSocket状态推送）
    - P0-004-010-004-001-002 验证功能（列表渲染正确+搜索生效+强制下线操作+状态同步）

### P0-004-011 登录安全增强

> **说明**：登录安全属于P0认证基础设施，应在系统上线前完成而非推迟到P2。

- P0-004-011-001 图形验证码生成与校验
  - P0-004-011-001-001 图形验证码生成接口（GET /api/auth/captcha：生成4位随机字符+干扰线+噪点+Base64图片返回+Redis缓存key）
    - P0-004-011-001-001-001 实现验证码生成（随机字符串+Java AWT/BufferedImage生成+Base64编码+Redis存储+TLL 5min）
    - P0-004-011-001-001-002 实现验证码校验（从Redis获取+比对+一次性消费删除+大小写不敏感）
    - P0-004-011-001-001-003 验证图形验证码（生成图片可读+校验正确+过期失效+一次性消费）
  - P0-004-011-001-002 登录页集成图形验证码（登录表单增加验证码输入框+点击刷新+登录接口增加captchaKey+captchaCode参数）
    - P0-004-011-001-002-001 前端登录页验证码组件（el-image展示+el-input输入+点击刷新+倒计时禁用）
    - P0-004-011-001-002-002 后端登录接口改造（校验验证码→校验用户名密码→签发Token→验证码消费删除）
- P0-004-011-002 密码强度策略
  - P0-004-011-002-001 密码强度校验器（最小8位+大小写字母+数字+特殊字符四选三+禁止连续3位相同字符+禁止与用户名相同）
    - P0-004-011-002-001-001 实现密码强度校验工具类（正则组合校验+强度评分+错误提示汇总）
    - P0-004-011-002-001-002 前端密码强度指示器（PasswordStrengthBar：强度颜色条+弱/中/强/极强+实时提示）
  - P0-004-011-002-002 密码过期策略（默认90天过期+过期前7天提醒+首次登录强制修改密码+历史密码不可重复使用5次）
    - P0-004-011-002-002-001 实现密码过期逻辑（last_password_update字段+过期天数系统参数+登录时检查+强制修改页跳转）
    - P0-004-011-002-002-002 实现历史密码校验（sys_user_password_history表+修改密码时检查最近5次+旧密码不可重复）
- P0-004-011-003 登录失败锁定
  - P0-004-011-003-001 登录失败计数器（Redis计数器+连续失败5次锁定15分钟+锁定期间拒绝登录+成功后重置计数）
    - P0-004-011-003-001-001 实现登录失败计数与锁定（Redis key设计login:fail:<username>+INCR计数+TTL 15min+达到阈值拒绝）
    - P0-004-011-003-001-002 实现锁定状态查询与解锁（管理员手动解锁接口+锁定状态通知用户+登录页剩余锁定时间提示）
- P0-004-011-004 短信验证码（预留）

### P0-004-012 SSO/OAuth2配置管理

> **说明**：为对接第三方系统的单点登录需求提供基础配置能力。初期实现配置管理，具体协议对接可在P2阶段按需扩展。

- P0-004-012-001 OAuth2配置表建表（auth_oauth2_config：client_id/client_secret/redirect_uri/authorization_uri/token_uri/user_info_uri/scopes/enabled）
  - P0-004-012-001-001 编写CREATE TABLE DDL（字段+类型+默认值+非空+注释）
  - P0-004-012-001-002 添加索引与约束
- P0-004-012-001a SSO配置表建表（auth_sso_config：sso_name/sso_type(SAML/OAuth2/CAS)/idp_url/sp_entity_id/sso_url/slo_url/certificate/enable_flag）
  - P0-004-012-001a-001 编写CREATE TABLE auth_sso_config DDL（字段+类型+默认值+非空+注释）
  - P0-004-012-001a-002 添加索引与约束（sso_name唯一索引+enable_flag索引）
- P0-004-012-002 SSO配置CRUD接口
  - P0-004-012-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-004-012-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-004-012-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-004-012-003 SSO配置管理页（P13配置页：OAuth2 Provider配置表单+SSO协议选择+回调URL预览+联调验证）
  - P0-004-012-003-001 配置表单区（Provider名称/client_id/client_secret/redirect_uri/authorization_uri/token_uri/user_info_uri/scopes多选/enabled开关）
    - P0-004-012-003-001-001 实现配置表单UI（el-form+分组折叠+Provider选择下拉+DingTalk/WeChat/Feishu预设模板+自定义Provider）
    - P0-004-012-003-001-002 实现回调URL自动生成与预览（redirect_uri=baseUrl+/api/auth/oauth2/callback/{provider}+一键复制）
  - P0-004-012-003-002 配置预览+校验（OAuth2授权流程模拟+token获取测试+用户信息映射校验）
    - P0-004-012-003-002-001 实现OAuth2测试连接按钮（点击触发授权流程模拟+返回token解析结果+用户信息字段映射验证）
  - P0-004-012-003-003 配置保存+生效（保存到auth_oauth2_config表+刷新Sa-Token OAuth2配置缓存）
    - P0-004-012-003-003-001 实现保存逻辑（POST API+配置JSON序列化+缓存刷新通知）
  - P0-004-012-003-004 SSO配置页联调验证（表单提交+OAuth2测试连接+配置缓存生效+错误提示）

---

## P0-005 公共组件基础开发

> **依赖**：P0-002  
> **概述**：开发15种页面类型基座组件、通用录入组件、ERP业务选择组件、通用业务功能组件、组合式函数，为所有业务页面提供统一渲染基座

### P0-005-001 常用查询区组件（QueryPanel.vue）
- P0-005-001-001 多字段模糊查询区（上部分：或关系匹配多字段，搜索框+搜索字段下拉选择）
  - P0-005-001-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-001-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-001-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-001-002 常用字段查询区（下部分：且关系组合筛选，根据字段配置动态渲染筛选条件）
  - P0-005-001-002-001 实现核心逻辑（动态筛选条件渲染+字段配置驱动+组合查询+异常处理）
    - P0-005-001-002-001-001 编写核心代码（动态筛选条件渲染+字段配置解析+组合查询逻辑+日志记录）
    - P0-005-001-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-001-003 查询区折叠/展开控制（超过3行自动折叠/展开切换按钮）
  - P0-005-001-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-001-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-001-003-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P0-005-002 功能操作区组件（ActionBar.vue）
- P0-005-002-001 列表页功能操作区（左右分排按钮布局：左侧主操作+右侧扩展操作）
  - P0-005-002-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-002-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-002-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-002-002 表单页功能操作区（左右分排按钮布局：左侧保存/提交+右侧关闭）
  - P0-005-002-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-002-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-002-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-002-003 头部扩展工具栏组件（铺满/刷新/格式设置/行高调整下拉面板）
  - P0-005-002-003-001 实现核心逻辑（工具栏按钮组+下拉面板+铺满切换+刷新触发+异常处理）
    - P0-005-002-003-001-001 编写核心代码（工具栏布局+铺满/刷新事件+格式/行高下拉面板+日志记录）
    - P0-005-002-003-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P0-005-003 列表数据表格组件（ListTable.vue）
- P0-005-003-001 Vxe Table基础封装（基于vxe-table组件封装、统一默认配置、事件代理）
  - P0-005-003-001-001 实现核心逻辑（vxe-table封装+默认配置注入+事件代理+插槽透传+异常处理）
    - P0-005-003-001-001-001 编写核心代码（vxe-table二次封装+默认props/events/slots+列配置绑定+日志记录）
    - P0-005-003-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-003-002 列排序功能（服务端排序+客户端排序双模式）
  - P0-005-003-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-003-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-003-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-003-003 列搜索筛选功能（按字段类型差异化筛选条件：文本模糊/数字范围/日期范围/字典勾选）
  - P0-005-003-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-003-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-003-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-003-004 列格式自定义设置（显隐/顺序拖拽/列宽调整/固定列设置+持久化到localStorage）
  - P0-005-003-004-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P0-005-003-004-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P0-005-003-004-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P0-005-003-005 行高度/字体大小7档设置（紧凑/较小/默认/较大/宽松/超大/超宽松）
  - P0-005-003-005-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P0-005-003-005-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P0-005-003-005-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P0-005-003-006 一键初始化/一键清空搜索排序（重置为默认列格式+清除搜索条件+清除排序）
  - P0-005-003-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-003-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-003-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-003-007 表格合计列配置（接收后端计算返回的合计行数据并展示）
  - P0-005-003-007-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P0-005-003-007-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P0-005-003-007-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P0-005-003-008 基础标配功能（选中行高亮/省略Tooltip/斑马纹/纵向边框/前端分页/服务端分页切换）
  - P0-005-003-008-001 前端页面开发（组件编写+数据绑定+交互逻辑）
    - P0-005-003-008-001-001 编写页面组件（template+搜索区+表格区+操作区+弹窗+表单组件）
    - P0-005-003-008-001-002 编写数据绑定与交互逻辑（API调用+数据绑定+事件处理+路由跳转）
    - P0-005-003-008-001-003 验证前端页面开发页面（渲染正确+交互响应+数据绑定+空状态/加载态）

### P0-005-004 录入数据表格组件（EntryTable.vue）
- P0-005-004-001 Vxe Table可编辑封装（基于vxe-table edit模式、单元格编辑态/展示态切换）
  - P0-005-004-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-004-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-004-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-004-002 行拖拽改序（SortableJS集成+行号自动重排）
  - P0-005-004-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-004-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-004-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-004-003 列格式自定义设置（同ListTable列格式设置逻辑复用）
  - P0-005-004-003-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P0-005-004-003-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P0-005-004-003-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P0-005-004-004 行高度/字体大小7档设置（同ListTable）
  - P0-005-004-004-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P0-005-004-004-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P0-005-004-004-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P0-005-004-005 表格合计列配置（前端实时计算合计行，根据列配置的合计公式）
  - P0-005-004-005-001 前端页面开发（组件编写+数据绑定+交互逻辑）
    - P0-005-004-005-001-001 编写页面组件（template+搜索区+表格区+操作区+弹窗+表单组件）
    - P0-005-004-005-001-002 编写数据绑定与交互逻辑（API调用+数据绑定+事件处理+路由跳转）
    - P0-005-004-005-001-003 验证前端页面开发页面（渲染正确+交互响应+数据绑定+空状态/加载态）
- P0-005-004-006 录入组件单元格渲染引擎（根据字段配置的field_type自动渲染对应录入组件）
  - P0-005-004-006-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P0-005-004-006-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P0-005-004-006-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P0-005-004-007 详情只读态自动转禁用（根据页面状态将全部编辑组件切换为只读文本展示）
  - P0-005-004-007-001 前端页面开发（组件编写+数据绑定+交互逻辑）
    - P0-005-004-007-001-001 编写页面组件（template+搜索区+表格区+操作区+弹窗+表单组件）
    - P0-005-004-007-001-002 编写数据绑定与交互逻辑（API调用+数据绑定+事件处理+路由跳转）
    - P0-005-004-007-001-003 验证前端页面开发页面（渲染正确+交互响应+数据绑定+空状态/加载态）

### P0-005-005 关联信息区组件（RelatedInfoArea.vue）
- P0-005-005-001 左侧分组导航栏（点击分组展开/折叠子项）
  - P0-005-005-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-005-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-005-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-005-002 右侧标签页容器（多标签页切换+标签页懒加载）
  - P0-005-005-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-005-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-005-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-005-003 标签页显隐权限配置支持（根据字段权限控制标签页显示/隐藏）
  - P0-005-005-003-001 实现核心逻辑（字段权限读取+标签页显隐控制+动态渲染+异常处理）
    - P0-005-005-003-001-001 编写核心代码（权限判断逻辑+标签页v-if控制+权限变更响应+日志记录）
    - P0-005-005-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-005-004 数据刷新机制（单击主行刷新当前标签页/切换标签页自动刷新）
  - P0-005-005-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-005-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-005-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-005-005 头部扩展工具栏（新增按钮/放大查看按钮）
  - P0-005-005-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-005-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-005-005-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P0-005-006 明细从表区域组件（DetailTableArea.vue）
- P0-005-006-001 纯标签页模式容器（多个从表标签页切换+懒加载）
  - P0-005-006-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-006-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-006-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-006-002 头部扩展工具栏（添加x行/铺满按钮）
  - P0-005-006-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-006-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-006-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-006-003 区域铺满切换（主表单铺满/明细铺满双模式切换）
  - P0-005-006-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-006-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-006-003-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P0-005-007 主表单录入区组件（MasterForm.vue）
- P0-005-007-001 表单字段渲染器（FormField.vue：根据字段配置自动渲染录入组件+标签+校验提示）
  - P0-005-007-001-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P0-005-007-001-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P0-005-007-001-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P0-005-007-002 表单布局配置（栅格占位/标签宽度/标签位置/独占一行/分组折叠面板）
  - P0-005-007-002-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P0-005-007-002-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P0-005-007-002-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P0-005-007-003 表单校验引擎（必填/条件必填/正则/跨字段联合/唯一性/后端异步校验规则链）
  - P0-005-007-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-007-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-007-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-007-004 表单联动引擎（联动带出值/自动计算/显隐联动/数据联动过滤）
  - P0-005-007-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-007-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-007-004-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P0-005-008 标签页容器组件（TabPageContainer.vue）
- P0-005-008-001 通用标签页切换容器（支持动态标签页+标签页插槽+懒加载）
  - P0-005-008-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-008-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-008-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-008-002 标签页权限控制集成（根据权限配置过滤标签页）
  - P0-005-008-002-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P0-005-008-002-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P0-005-008-002-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）

### P0-005-009 15种页面类型基座组件
- P0-005-009-001 PageP01Dashboard（首页/仪表盘：拖拽网格布局+图表组件+KPI卡片插槽）
  - P0-005-009-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-009-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-009-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-009-002 PageP02Workbench（工作台：网格+列表混合布局插槽）
  - P0-005-009-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-009-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-009-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-009-003 PageP03MasterList（主从列表页：查询区+主列表区+关联信息区+铺满切换）
  - P0-005-009-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-009-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-009-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-009-004 PageP04SimpleList（单一列表页：查询区+主列表区）
  - P0-005-009-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-009-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-009-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-009-005 PageP05TreeList（树形列表页：左侧树+右侧列表+联动选中）
  - P0-005-009-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-009-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-009-005-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-009-006 PageP06MasterForm（主从表单页：功能操作区+主表单+明细从表+铺满切换）
  - P0-005-009-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-009-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-009-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-009-007 PageP07SimpleForm（单一表单页：功能操作区+主表单）
  - P0-005-009-007-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-009-007-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-009-007-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-009-008 PageP08Kanban（看板页：泳道卡片+拖拽流转）
  - P0-005-009-008-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-009-008-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-009-008-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-009-009 PageP09Query（查询页：查询条件+只读结果表格）
  - P0-005-009-009-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-009-009-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-009-009-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-009-010 PageP10Report（报表页：条件区+报表展示区+穿透钻取）
  - P0-005-009-010-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-009-010-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-009-010-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-009-011 PageP11Screen（大屏页：深色主题+图表网格+定时刷新）
  - P0-005-009-011-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-009-011-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-009-011-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-009-012 PageP12Profile（画像页：概览区+多维度分析区）
  - P0-005-009-012-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-009-012-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-009-012-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-009-013 PageP13Config（配置页：表单式+左右分栏式两种变体）
  - P0-005-009-013-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P0-005-009-013-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P0-005-009-013-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P0-005-009-014 PageP14AIDialog（AI对话页：对话区+结果展示区+流式SSE输出）
  - P0-005-009-014-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-009-014-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-009-014-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-009-015 PageP15Designer（设计器页：工具栏+组件面板+画布+属性面板）
  - P0-005-009-015-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-009-015-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-009-015-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P0-005-010 基础通用录入组件（20种）
- P0-005-010-001 单行文本输入框
  - P0-005-010-001-001 组件封装开发（ElInput封装+maxLength限制+placeholder提示+清除按钮+前后缀插槽）
    - P0-005-010-001-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-001-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-001-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-001-002 字段配置集成（field_type映射+校验规则绑定+联动规则绑定）
    - P0-005-010-001-002-001 实现联动规则解析器（配置JSON解析+触发字段监听+动作类型判断）
      - P0-005-010-001-002-001-001 解析联动规则配置JSON（触发字段+触发条件+动作类型+目标字段+动作参数）
      - P0-005-010-001-002-001-002 监听触发字段变化（watch触发字段→读取当前值→匹配触发条件）
      - P0-005-010-001-002-001-003 执行联动动作（值联动→赋值目标字段 / 显隐联动→设置目标字段visible / 必填联动→设置required）
    - P0-005-010-001-002-002 实现联动执行引擎（值带出→字段赋值/过滤→选项过滤/显隐→字段切换）
- P0-005-010-002 多行文本输入框
  - P0-005-010-002-001 组件封装开发（ElInput type=textarea封装+autoSize自适应高度+最大行数限制）
    - P0-005-010-002-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-002-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-002-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-002-002 字段配置集成
    - P0-005-010-002-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-002-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-002-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-002-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-002-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-002-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-002-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-002-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-002-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-002-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-002-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）
- P0-005-010-003 整数录入框
  - P0-005-010-003-001 组件封装开发（ElInputNumber封装+整数校验+千分位可选+步长配置+min/max约束）
    - P0-005-010-003-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-003-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-003-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-003-002 字段配置集成
    - P0-005-010-003-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-003-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-003-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-003-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-003-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-003-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-003-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-003-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-003-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-003-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-003-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）
- P0-005-010-004 小数/金额录入框
  - P0-005-010-004-001 组件封装开发（ElInputNumber封装+精度配置+千分位+前缀后缀+四舍五入策略）
    - P0-005-010-004-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-004-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-004-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-004-002 字段配置集成
    - P0-005-010-004-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-004-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-004-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-004-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-004-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-004-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-004-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-004-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-004-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-004-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-004-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）
- P0-005-010-005 百分比录入框
  - P0-005-010-005-001 组件封装开发（ElInputNumber封装+%后缀+0~100范围校验+小数位配置）
    - P0-005-010-005-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-005-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-005-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-005-002 字段配置集成
    - P0-005-010-005-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-005-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-005-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-005-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-005-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-005-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-005-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-005-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-005-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-005-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-005-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）
- P0-005-010-006 日期选择器
  - P0-005-010-006-001 组件封装开发（ElDatePicker封装+格式配置+快捷选项：今天/本周/本月/本季/本年）
    - P0-005-010-006-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-006-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-006-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-006-002 字段配置集成
    - P0-005-010-006-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-006-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-006-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-006-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-006-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-006-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-006-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-006-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-006-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-006-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-006-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）
- P0-005-010-007 时间选择器
  - P0-005-010-007-001 组件封装开发（ElTimePicker封装+格式配置+固定时间选项）
    - P0-005-010-007-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-007-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-007-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-007-002 字段配置集成
    - P0-005-010-007-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-007-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-007-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-007-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-007-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-007-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-007-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-007-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-007-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-007-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-007-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）
- P0-005-010-008 日期时间选择器
  - P0-005-010-008-001 组件封装开发（ElDatePicker type=datetime封装+日期时间联动）
    - P0-005-010-008-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-008-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-008-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-008-002 字段配置集成
    - P0-005-010-008-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-008-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-008-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-008-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-008-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-008-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-008-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-008-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-008-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-008-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-008-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）
- P0-005-010-009 年月选择器
  - P0-005-010-009-001 组件封装开发（ElDatePicker type=month封装+快捷选项：本月/上月/本季/本年）
    - P0-005-010-009-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-009-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-009-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-009-002 字段配置集成
    - P0-005-010-009-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-009-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-009-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-009-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-009-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-009-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-009-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-009-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-009-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-009-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-009-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）
- P0-005-010-010 开关组件
  - P0-005-010-010-001 组件封装开发（ElSwitch封装+变更事件+活跃/非活跃文案配置）
    - P0-005-010-010-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-010-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-010-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-010-002 字段配置集成
    - P0-005-010-010-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-010-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-010-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-010-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-010-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-010-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-010-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-010-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-010-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-010-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-010-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）
- P0-005-010-011 单选组件
  - P0-005-010-011-001 组件封装开发（ElRadioGroup封装+字典数据源/静态数据源+按钮样式/传统样式切换）
    - P0-005-010-011-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-011-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-011-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-011-002 字段配置集成
    - P0-005-010-011-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-011-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-011-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-011-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-011-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-011-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-011-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-011-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-011-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-011-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-011-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）
- P0-005-010-012 多选组件
  - P0-005-010-012-001 组件封装开发（ElCheckboxGroup封装+字典数据源/静态数据源+全选/反选）
    - P0-005-010-012-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-012-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-012-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-012-002 字段配置集成
    - P0-005-010-012-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-012-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-012-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-012-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-012-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-012-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-012-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-012-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-012-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-012-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-012-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）
- P0-005-010-013 树形选择组件
  - P0-005-010-013-001 组件封装开发（ElTreeSelect封装+异步加载+搜索过滤+多选模式+懒加载子节点）
    - P0-005-010-013-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-013-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-013-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-013-002 字段配置集成
    - P0-005-010-013-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-013-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-013-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-013-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-013-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-013-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-013-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-013-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-013-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-013-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-013-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）
- P0-005-010-014 附件上传组件
  - P0-005-010-014-001 组件封装开发（ElUpload封装+多文件+大小限制+类型限制+预览+下载+删除）
    - P0-005-010-014-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-014-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-014-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-014-002 字段配置集成
    - P0-005-010-014-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-014-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-014-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-014-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-014-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-014-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-014-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-014-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-014-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-014-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-014-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）
- P0-005-010-015 图片上传组件
  - P0-005-010-015-001 组件封装开发（ElUpload封装+图片预览+裁剪+压缩+拖拽排序+最大数量限制）
    - P0-005-010-015-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-015-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-015-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-015-002 字段配置集成
    - P0-005-010-015-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-015-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-015-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-015-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-015-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-015-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-015-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-015-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-015-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-015-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-015-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）
- P0-005-010-016 富文本编辑组件
  - P0-005-010-016-001 组件封装开发（TinyMCE封装+工具栏配置+图片上传集成+内容过滤XSS）
    - P0-005-010-016-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-016-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-016-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-016-002 字段配置集成
    - P0-005-010-016-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-016-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-016-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-016-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-016-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-016-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-016-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-016-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-016-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-016-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-016-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）
- P0-005-010-017 颜色选择组件
  - P0-005-010-017-001 组件封装开发（ElColorPicker封装+预设色板+自定义输入+透明度）
    - P0-005-010-017-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-017-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-017-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-017-002 字段配置集成
    - P0-005-010-017-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-017-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-017-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-017-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-017-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-017-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-017-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-017-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-017-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-017-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-017-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）
- P0-005-010-018 星级评分组件
  - P0-005-010-018-001 组件封装开发（ElRate封装+半星+只读+自定义图标+评分描述文案）
    - P0-005-010-018-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-018-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-018-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-018-002 字段配置集成
    - P0-005-010-018-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-018-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-018-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-018-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-018-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-018-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-018-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-018-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-018-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-018-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-018-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）
- P0-005-010-019 标签输入组件
  - P0-005-010-019-001 组件封装开发（ElSelect multiple+allowCreate+tag拖拽排序+最大标签数限制）
    - P0-005-010-019-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-019-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-019-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-019-002 字段配置集成
    - P0-005-010-019-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-019-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-019-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-019-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-019-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-019-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-019-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-019-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-019-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-019-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-019-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）
- P0-005-010-020 密码输入框
  - P0-005-010-020-001 组件封装开发（ElInput type=password封装+显示/隐藏切换+强度指示器+校验规则）
    - P0-005-010-020-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-010-020-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-010-020-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-010-020-002 字段配置集成
    - P0-005-010-020-002-001 字段配置读取与渲染（从字段配置API加载→动态渲染字段组件+校验规则+显隐联动）
      - P0-005-010-020-002-001-001 调用字段配置API获取当前单据的字段配置列表（字段编码+字段类型+显隐规则+校验规则+默认值）
      - P0-005-010-020-002-001-002 动态渲染字段组件（根据字段类型渲染Input/Select/DatePicker/NumberInput等+绑定v-model）
      - P0-005-010-020-002-001-003 执行字段校验规则（required/pattern/range/custom校验+实时校验+提交校验+错误提示）
      - P0-005-010-020-002-001-004 执行字段显隐联动（监听触发字段变化→根据显隐规则计算目标字段visible/hidden状态）
    - P0-005-010-020-002-002 字段值保存与校验（表单提交→字段配置校验规则执行→保存字段值+扩展字段映射）
      - P0-005-010-020-002-002-001 收集表单字段值（遍历动态字段→收集字段编码+字段值→构建字段值Map）
      - P0-005-010-020-002-002-002 执行字段校验规则（required+pattern+range+custom校验→不通过阻止提交+提示错误）
      - P0-005-010-020-002-002-003 保存主表字段值（标准字段→主表INSERT/UPDATE+乐观锁版本号）
      - P0-005-010-020-002-002-004 保存扩展字段值（扩展字段→ext_field_value表UPSERT+字段编码+业务ID+字段值）
    - P0-005-010-020-002-003 验证字段配置集成（字段动态渲染正确+校验生效+值保存正确+扩展字段关联）

### P0-005-011 字典类下拉组件（3种）
- P0-005-011-001 通用字典下拉选择器（根据dict_type加载字典数据+严格模式禁止手输/非严格模式允许手输）
  - P0-005-011-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-011-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-011-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-011-002 字典多选下拉组件（ElSelect multiple+字典数据源）
  - P0-005-011-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-011-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-011-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-005-011-003 字典级联下拉组件（ElCascader+父子字典关联+懒加载）
  - P0-005-011-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-005-011-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-005-011-003-001-002 验证功能（核心用例通过+边界处理+异常降级）

  - P0-005-011-022 客户分类选择器（树形弹窗+搜索+选中回填分类编码/名称+支持多级展开）
    - P0-005-011-022-001 选择器弹窗开发（el-tree树形+搜索+单选+确认/取消+回填）
      - P0-005-011-022-001-001 实现弹窗UI+树形数据加载（分类树API+懒加载+搜索过滤+选中回填）
      - P0-005-011-022-001-002 验证选择器（树形展开+搜索+选中回填+清空联动）
  - P0-005-011-023 供应商分类选择器（树形弹窗+搜索+选中回填分类编码/名称+多级展开）
    - P0-005-011-023-001 选择器弹窗开发（el-tree树形+搜索+单选+确认/取消+回填）
      - P0-005-011-023-001-001 实现弹窗UI+树形数据加载+回填+验证
  - P0-005-011-024 商品分类选择器（树形弹窗+搜索+选中回填分类编码/名称+多级展开）
    - P0-005-011-024-001 选择器弹窗开发（el-tree树形+搜索+单选+确认/取消+回填）
      - P0-005-011-024-001-001 实现弹窗UI+树形数据加载+回填+验证
  - P0-005-011-025 库存状态选择器（下拉+状态列表：可用/冻结/在检/待检/报废+颜色标识+回填）
    - P0-005-011-025-001 选择器开发（el-select下拉+状态选项+颜色Tag+回填）
      - P0-005-011-025-001-001 实现下拉+状态选项+颜色标识+回填+验证
  - P0-005-011-026 会计科目选择器（树形弹窗+科目编码+科目名称+科目层级+余额方向+搜索）
    - P0-005-011-026-001 选择器弹窗开发（el-tree+科目树+搜索+选中回填科目编码/名称）
      - P0-005-011-026-001-001 实现科目树弹窗+懒加载+搜索+回填+验证
  - P0-005-011-027 单号快捷调取器（输入单号前缀→模糊搜索→下拉匹配→选中跳转单据详情页）
    - P0-005-011-027-001 调取器开发（el-autocomplete+模糊搜索API+选中跳转路由）
      - P0-005-011-027-001-001 实现模糊搜索+下拉匹配+选中跳转+验证
  - P0-005-011-028 资产卡片选择器（弹窗+资产列表+按分类/编号/名称搜索+选中回填资产编码/名称）
    - P0-005-011-028-001 选择器弹窗开发（vxe-table列表+搜索+分页+选中回填）
      - P0-005-011-028-001-001 实现弹窗+列表+搜索+回填+验证
  - P0-005-011-029 资产分类选择器（树形弹窗+搜索+选中回填分类编码/名称）
    - P0-005-011-029-001 选择器弹窗开发（el-tree+搜索+回填）
      - P0-005-011-029-001-001 实现弹窗+树形+搜索+回填+验证
  - P0-005-011-030 会议室选择器（弹窗+会议室列表+容量/设备/楼层筛选+可用时段显示+选中回填）
    - P0-005-011-030-001 选择器弹窗开发（列表+筛选+可用时段+回填）
      - P0-005-011-030-001-001 实现弹窗+列表+时段+回填+验证
  - P0-005-011-031 车辆选择器（弹窗+车辆列表+车牌号/车型/座位数+可用状态+选中回填）
    - P0-005-011-031-001 选择器弹窗开发（列表+状态筛选+回填）
      - P0-005-011-031-001-001 实现弹窗+列表+状态+回填+验证
  - P0-005-011-032 成本中心选择器（弹窗+成本中心树形+搜索+选中回填编码/名称）
    - P0-005-011-032-001 选择器弹窗开发（el-tree+搜索+回填）
      - P0-005-011-032-001-001 实现弹窗+树形+搜索+回填+验证
  - P0-005-011-033 审批人/审批组选择器（弹窗+按部门树+人员列表+角色列表+多选+选中回填）
    - P0-005-011-033-001 选择器弹窗开发（部门树+人员列表Tab+角色列表Tab+多选+回填）
      - P0-005-011-033-001-001 实现部门树+人员/角色Tab+多选+回填+验证
  - P0-005-011-034 费用项目选择器（弹窗+费用项目列表+按分类筛选+选中回填编码/名称）
    - P0-005-011-034-001 选择器弹窗开发（列表+分类筛选+回填）
      - P0-005-011-034-001-001 实现弹窗+列表+分类+回填+验证

### P0-005-012 ERP业务档案选择组件（7大体系）
- P0-005-012-001 客户体系选择组件
  - P0-005-012-001-001 客户选择器弹窗（P03主从列表弹窗：客户列表+客户详情关联信息+双击选中带回）
    - P0-005-012-001-001-001 弹窗容器与列表布局（el-dialog+vxe-table+搜索条件+分页）
    - P0-005-012-001-001-002 客户数据加载与搜索（API调用+客户分类过滤+关键字搜索+双击选中）
    - P0-005-012-001-001-003 选中回填逻辑（双击/确认按钮→emit选中数据→父组件回填字段）
  - P0-005-012-001-002 客户分类树选择组件（ElTreeSelect+分类懒加载+选中分类过滤客户）
    - P0-005-012-001-002-001 实现ElTreeSelect组件封装（懒加载+异步数据+选中回显+搜索过滤）
    - P0-005-012-001-002-002 实现分类联动（选中分类→过滤下级列表+清空重置+默认展开）
  - P0-005-012-001-003 客户联系人选择器（ElSelect+按客户ID过滤联系人+姓名/电话/职位展示）
    - P0-005-012-001-003-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-001-003-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
- P0-005-012-002 供应商体系选择组件
  - P0-005-012-002-001 供应商选择器弹窗（P03主从列表弹窗：供应商列表+详情+双击选中带回）
    - P0-005-012-002-001-001 弹窗容器与列表布局（el-dialog+vxe-table+搜索+分页）
    - P0-005-012-002-001-002 供应商数据加载与搜索（API+分类过滤+搜索+双击选中）
    - P0-005-012-002-001-003 选中回填逻辑（双击/确认→emit→父组件回填）
  - P0-005-012-002-002 供应商分类树选择组件
    - P0-005-012-002-002-001 实现ElTreeSelect组件封装（懒加载+异步数据+选中回显+搜索过滤）
    - P0-005-012-002-002-002 实现分类联动（选中分类→过滤下级列表+清空重置+默认展开）
  - P0-005-012-002-003 供应商联系人选择器
    - P0-005-012-002-003-001 实现供应商联系人选择器弹窗（联系人列表+按姓名/电话搜索+选中回填联系人ID/名称/电话/邮箱）
    - P0-005-012-002-003-002 验证选择器（供应商切换→联系人联动刷新+选中回填正确+清空联动）
- P0-005-012-003 物料商品体系选择组件
  - P0-005-012-003-001 商品选择器弹窗（P03主从列表弹窗：商品列表+商品详情+多规格+双击选中带回+支持多选批量带回）
    - P0-005-012-003-001-001 弹窗容器与列表布局（el-dialog+vxe-table+搜索+分页+多选支持）
    - P0-005-012-003-001-002 商品数据加载与搜索（API+分类过滤+规格选择+搜索+双击/多选）
    - P0-005-012-003-001-003 多选批量回填逻辑（确认→emit多条数据→父组件批量新增明细行）
  - P0-005-012-003-002 商品分类树选择组件（ElTreeSelect+多级分类懒加载）
    - P0-005-012-003-002-001 实现ElTreeSelect组件封装（懒加载+异步数据+选中回显+搜索过滤）
    - P0-005-012-003-002-002 实现分类联动（选中分类→过滤下级列表+清空重置+默认展开）
  - P0-005-012-003-003 计量单位选择器（ElSelect+单位组+主单位/副单位切换+换算率展示）
    - P0-005-012-003-003-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-003-003-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
- P0-005-012-004 仓库库存体系选择组件
  - P0-005-012-004-001 仓库选择器（ElSelect+按公司过滤+仓库类型标识+仓库状态过滤）
    - P0-005-012-004-001-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-004-001-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
  - P0-005-012-004-002 库位选择器（ElSelect+按仓库ID过滤库位+库位类型标识）
    - P0-005-012-004-002-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-004-002-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
  - P0-005-012-004-003 批次选择器（ElSelect+按商品+仓库过滤批次+生产日期+有效期展示）
    - P0-005-012-004-003-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-004-003-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
  - P0-005-012-004-004 批次拆分组件（Dialog弹窗：选择多个批次+各批次分配数量+总量校验）
    - P0-005-012-004-004-001 Dialog弹窗布局（批次列表vxe-table+数量分配列+合计校验行）
    - P0-005-012-004-004-002 批次选择与分配交互（选择批次+输入分配数量+总量校验+确认回填）
  - P0-005-012-004-005 序列号选择器（ElSelect+按商品+仓库过滤序列号+序列号扫码输入）
    - P0-005-012-004-005-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-004-005-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
  - P0-005-012-004-006 序列号批量录入组件（Dialog弹窗：扫码枪批量录入+重复校验+数量校验）
    - P0-005-012-004-006-001 Dialog弹窗布局（多行文本框/表格输入+校验提示+确认取消按钮）
    - P0-005-012-004-006-002 批量处理逻辑（文本解析/行拆分+重复校验+数量校验+确认回填）
  - P0-005-012-004-007 库存状态展示组件（可用量/在途量/占用量/安全库存实时查询Popover）
    - P0-005-012-004-007-001 Popover组件布局（可用量/在途量/占用量/安全库存四维度展示）
    - P0-005-012-004-007-002 实时查询逻辑（商品+仓库维度API调用+缓存+防抖+数值色标）
- P0-005-012-005 组织人员体系选择组件
  - P0-005-012-005-001 公司选择器（ElSelect+公司层级+公司状态过滤）
    - P0-005-012-005-001-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-005-001-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
  - P0-005-012-005-002 部门树选择器（ElTreeSelect+按公司过滤+部门树懒加载）
    - P0-005-012-005-002-001 实现ElTreeSelect组件封装（懒加载+异步数据+选中回显+搜索过滤）
    - P0-005-012-005-002-002 实现分类联动（选中分类→过滤下级列表+清空重置+默认展开）
  - P0-005-012-005-003 职员选择器（ElSelect+按部门过滤+姓名/工号/岗位展示+支持多选）
    - P0-005-012-005-003-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-005-003-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
  - P0-005-012-005-004 岗位选择器（ElSelect+按部门过滤+岗位名称+岗位编码展示）
    - P0-005-012-005-004-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-005-004-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
- P0-005-012-006 财务往来体系选择组件
  - P0-005-012-006-001 结算方式选择器（ElSelect+结算方式编码+名称+天数展示）
    - P0-005-012-006-001-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-006-001-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
  - P0-005-012-006-002 收支项目选择器（ElSelect+收支项目树+项目类型标识）
    - P0-005-012-006-002-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-006-002-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
  - P0-005-012-006-003 币种选择器（ElSelect+币种编码+名称+汇率展示）
    - P0-005-012-006-003-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-006-003-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
  - P0-005-012-006-004 税率选择器（ElSelect+税率值+税种类型展示）
    - P0-005-012-006-004-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-006-004-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
  - P0-005-012-006-005 结算账户选择器（ElSelect+账户名称+账号+币种展示）
    - P0-005-012-006-005-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-006-005-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
  - P0-005-012-006-006 会计科目树选择器（ElTreeSelect+科目编码+名称+层级+按账套过滤）
    - P0-005-012-006-006-001 实现ElTreeSelect组件封装（懒加载+异步数据+选中回显+搜索过滤）
    - P0-005-012-006-006-002 实现分类联动（选中分类→过滤下级列表+清空重置+默认展开）
  - P0-005-012-006-007 会计期间选择器（ElSelect+年度+期间+状态：已开账/已结账/未开账）
    - P0-005-012-006-007-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-006-007-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
- P0-005-012-007 生产工序体系选择组件
  - P0-005-012-007-001 工序选择器（ElSelect+工序编码+名称+标准工时展示）
    - P0-005-012-007-001-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-007-001-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
  - P0-005-012-007-002 工艺路线选择器（ElSelect+路线编码+名称+工序步骤展示）
    - P0-005-012-007-002-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-007-002-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
  - P0-005-012-007-003 生产线选择器（ElSelect+生产线编码+名称+产能展示）
    - P0-005-012-007-003-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-007-003-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
  - P0-005-012-007-004 BOM选择器（ElSelect+BOM版本+物料清单+用量展示）
    - P0-005-012-007-004-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-007-004-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）
  - P0-005-012-007-005 班组选择器（ElSelect+班组编码+名称+所属车间展示）
    - P0-005-012-007-005-001 实现ElSelect封装组件（远程搜索+按条件过滤+自定义模板渲染）
    - P0-005-012-007-005-002 实现选中回填与联动（选中→emit→父组件字段赋值+清空联动）

### P0-005-013 通用业务功能组件
- P0-005-013-001 通用单据引入组件（DocImportDialog.vue）
  - P0-005-013-001-001 引入弹窗UI开发（源单类型选择Tab+源单列表+明细行勾选表格+数量可修改列+确认/取消按钮）
    - P0-005-013-001-001-001 明细行vxe-table定义（列配置+可编辑列+行号+增删行+合计行）
    - P0-005-013-001-001-002 明细行编辑交互（单元格编辑+下拉+弹出选择器+行增删复制）
    - P0-005-013-001-001-003 明细行自动计算（数量×单价=金额+税率+折扣+合计汇总）
  - P0-005-013-001-002 引入逻辑开发（源单查询接口调用+已引入过滤+数量校验+自动填充目标单字段映射）
    - P0-005-013-001-002-001 定义接口路由与方法签名（@GetMapping+查询参数）
    - P0-005-013-001-002-002 实现查询逻辑（参数接收+Service调用+响应包装）
- P0-005-013-002 通用单据下推组件（DocPushDialog.vue）
  - P0-005-013-002-001 下推弹窗UI开发（目标单据类型选择+自动携带数据预览+确认/取消按钮）
    - P0-005-013-002-001-001 实现下推弹窗UI（目标单据类型选择+自动携带数据预览表格+确认/取消按钮）
    - P0-005-013-002-001-002 验证下推弹窗（单据类型选择+数据预览正确+确认跳转目标单据编辑页）
    - P0-005-013-002-001-003 验证下推弹窗完整流程（选择类型→预览→确认→目标单据数据正确）
  - P0-005-013-002-002 下推逻辑开发（目标单字段映射+数量拆分+下推后源单状态更新）
    - P0-005-013-002-002-001 实现下推逻辑（源单字段映射→目标单字段赋值+数量拆分+下推后源单状态更新为已下推）
      - P0-005-013-002-002-001-001 实现源单→目标单字段映射（配置映射关系→源单字段值赋值到目标单对应字段+自定义映射规则）
      - P0-005-013-002-002-001-002 实现数量拆分逻辑（源单数量→目标单数量+可拆分+已下推数量校验+剩余可下推数量计算）
      - P0-005-013-002-002-001-003 实现下推后源单状态更新（下推成功→源单状态更新为已下推/部分下推+下推关联记录写入）
    - P0-005-013-002-002-002 验证下推逻辑（字段映射正确+数量拆分准确+源单状态更新+下推关联记录正确）
    - P0-005-013-002-002-003 验证逻辑（主流程通过+边界处理+异常回滚+数据一致性）
- P0-005-013-003 通用单据复制组件（DocCopyDialog.vue）
  - P0-005-013-003-001 复制弹窗UI开发（源单搜索选择+主表数据预览+明细行勾选+确认/取消按钮）
    - P0-005-013-003-001-001 明细行vxe-table定义（列配置+可编辑列+行号+增删行+合计行）
    - P0-005-013-003-001-002 明细行编辑交互（单元格编辑+下拉+弹出选择器+行增删复制）
    - P0-005-013-003-001-003 明细行自动计算（数量×单价=金额+税率+折扣+合计汇总）
  - P0-005-013-003-002 复制逻辑开发（源单数据读取+编码重新生成+复制后进入编辑态）
    - P0-005-013-003-002-001 实现复制逻辑（源单数据读取→编码重新生成→复制后进入编辑态→清除审核状态）
    - P0-005-013-003-002-002 验证复制功能（数据完整复制+新编码生成+审核状态清除+关联单据不复制）
    - P0-005-013-003-002-003 验证逻辑（主流程通过+边界处理+异常回滚+数据一致性）
- P0-005-013-004 通用草稿保存/读取组件
  - P0-005-013-004-001 草稿保存逻辑（自动定时草稿：30秒自动保存+手动保存草稿按钮）
    - P0-005-013-004-001-001 实现草稿保存方法（localStorage/setInterval 30s+手动保存按钮+序列化）
    - P0-005-013-004-001-002 实现草稿冲突检测（多标签页共享+版本比对+覆盖提示）
  - P0-005-013-004-002 草稿读取逻辑（进入编辑页时检查草稿+草稿列表弹窗+草稿恢复+草稿删除）
    - P0-005-013-004-002-001 实现草稿检测（编辑页加载时检查+草稿列表弹窗+预览对比）
    - P0-005-013-004-002-002 实现草稿恢复（选择草稿→表单回填→草稿删除→恢复确认提示）
- P0-005-013-005 通用审核/反审组件（AuditDialog.vue）
  - P0-005-013-005-001 审核弹窗UI开发（审核意见文本框+审核通过/驳回按钮+批量审核勾选）
    - P0-005-013-005-001-001 审核按钮组渲染（提交/审核/驳回/撤回/关闭+按状态权限显隐）
    - P0-005-013-005-001-002 审核流程对话框（审批意见+审批节点+签字+流程图）
    - P0-005-013-005-001-003 审核API调用与状态刷新（审核接口+刷新状态+操作反馈）
  - P0-005-013-005-002 审核逻辑开发（审核状态流转+审核记录写入+反审权限校验+反审逻辑）
    - P0-005-013-005-002-001 审核按钮组渲染（提交/审核/驳回/撤回/关闭+按状态权限显隐）
    - P0-005-013-005-002-002 审核流程对话框（审批意见+审批节点+签字+流程图）
    - P0-005-013-005-002-003 审核API调用与状态刷新（审核接口+刷新状态+操作反馈）
- P0-005-013-006 通用作废/撤销作废组件（VoidDialog.vue）
  - P0-005-013-006-001 作废弹窗UI开发（作废原因必填文本框+确认作废按钮+撤销作废按钮）
    - P0-005-013-006-001-001 实现作废弹窗UI（作废原因必填文本框+确认作废按钮+撤销作废按钮+警告色提示）
    - P0-005-013-006-001-002 验证作废弹窗（原因必填校验+确认后状态变更+撤销恢复成功）
    - P0-005-013-006-001-003 验证UI渲染（布局正确+交互响应+数据绑定+空状态/加载态）
  - P0-005-013-006-002 作废逻辑开发（作废状态流转+作废后下游单据检查+撤销作废恢复）
    - P0-005-013-006-002-001 实现作废逻辑（作废状态流转→已审核单据不可直接删除→作废后下游单据检查→撤销作废恢复）
      - P0-005-013-006-002-001-001 实现作废状态流转（已审核→已作废+作废原因记录+作废时间+操作人记录）
      - P0-005-013-006-002-001-002 实现下游单据检查（检查下游是否存在未完成单据+存在则禁止作废+提示下游单据编号）
      - P0-005-013-006-002-001-003 实现撤销作废恢复（已作废→已审核+清除作废原因+操作日志+权限控制）
    - P0-005-013-006-002-002 验证作废逻辑（状态流转正确+下游单据存在时禁止作废+撤销作废恢复+操作日志记录）
    - P0-005-013-006-002-003 验证逻辑（主流程通过+边界处理+异常回滚+数据一致性）
- P0-005-013-007 通用单据追溯组件（DocTraceDialog.vue）
  - P0-005-013-007-001 追溯弹窗UI开发（上下游单据链路树形展示+节点单据类型+单据编号+状态标签）
    - P0-005-013-007-001-001 实现追溯弹窗UI（上下游单据链路树形展示+节点单据类型图标+编号+状态标签+点击跳转）
    - P0-005-013-007-001-002 验证追溯弹窗（树形链路完整+节点信息正确+点击跳转详情页）
    - P0-005-013-007-001-003 验证UI渲染（布局正确+交互响应+数据绑定+空状态/加载态）
  - P0-005-013-007-002 追溯逻辑开发（递归查询上下游单据+点击节点跳转单据详情）
    - P0-005-013-007-002-001 实现追溯逻辑（递归查询上游来源单据+递归查询下游目标单据+构建树形数据结构）
      - P0-005-013-007-002-001-001 递归查询上游来源单据（当前单据→上游来源单据→上游的上游→直到最源头）
      - P0-005-013-007-002-001-002 递归查询下游目标单据（当前单据→下游目标单据→下游的下游→直到最终单据）
      - P0-005-013-007-002-001-003 构建树形数据结构（上下游合并→父子关系构建→循环引用检测→层级排序）
      - P0-005-013-007-002-001-004 节点点击跳转（点击树节点→根据单据类型→跳转到对应单据详情页+高亮当前节点）
    - P0-005-013-007-002-002 验证追溯逻辑（上下游链路完整+多级追溯正确+循环引用检测+性能合理）
    - P0-005-013-007-002-003 验证逻辑（主流程通过+边界处理+异常回滚+数据一致性）
- P0-005-013-008 通用模板打印组件（PrintDialog.vue）
  - P0-005-013-008-001 打印弹窗UI开发（打印模板选择+打印份数+打印机选择+打印预览区域）
    - P0-005-013-008-001-001 实现打印弹窗UI（打印模板选择下拉+打印份数输入+打印机选择+预览区域+确认打印按钮）
    - P0-005-013-008-001-002 验证打印弹窗（模板选择+份数输入+预览渲染+打印执行）
    - P0-005-013-008-001-003 验证UI渲染（布局正确+交互响应+数据绑定+空状态/加载态）
  - P0-005-013-008-002 打印逻辑开发（模板渲染+预览生成+打印执行+打印记录）
    - P0-005-013-008-002-001 实现打印逻辑（模板渲染+预览HTML生成+调用浏览器打印+记录打印次数和时间）
      - P0-005-013-008-002-001-001 实现模板渲染（选择模板→替换占位变量→生成打印HTML+样式隔离）
      - P0-005-013-008-002-001-002 实现预览+打印执行（预览区域iframe加载+调用window.print+打印份数控制）
      - P0-005-013-008-002-001-003 实现打印记录（记录打印时间+打印人+打印份数+打印模板+业务单据关联）
    - P0-005-013-008-002-002 验证打印逻辑（模板渲染正确+预览与实际一致+打印记录写入）
    - P0-005-013-008-002-003 验证逻辑（主流程通过+边界处理+异常回滚+数据一致性）
- P0-005-013-009 通用Excel导出组件（ExportDialog.vue）
  - P0-005-013-009-001 导出弹窗UI开发（导出字段勾选+导出格式选择xlsx/csv+导出按钮）
    - P0-005-013-009-001-001 实现导出逻辑（查询条件+API+文件流+文件名处理）
    - P0-005-013-009-001-002 导出异常处理（大数据量分页+超时+失败重试提示）
  - P0-005-013-009-002 导出逻辑开发（字段映射+EasyExcel生成+文件下载+大数据量异步导出）
    - P0-005-013-009-002-001 实现导出逻辑（查询条件+API+文件流+文件名处理）
    - P0-005-013-009-002-002 导出异常处理（大数据量分页+超时+失败重试提示）
- P0-005-013-010 通用导入模板下载+数据导入组件（ImportDialog.vue）
  - P0-005-013-010-001 导入弹窗UI开发（模板下载按钮+文件上传区+校验结果表格+错误数据导出按钮）
    - P0-005-013-010-001-001 vxe-table列定义（字段映射+列宽+排序+对齐+固定列+显隐）
    - P0-005-013-010-001-002 数据加载与分页（API调用+分页参数+数据赋值+Loading）
    - P0-005-013-010-001-003 表格格式化渲染（字典翻译+日期+金额+状态色标+操作列按钮）
  - P0-005-013-010-002 导入逻辑开发（模板生成+文件解析+数据校验+正确数据入库+错误数据标记+错误数据导出）
    - P0-005-013-010-002-001 实现导出功能（API+文件流下载+动态文件名+进度提示）
    - P0-005-013-010-002-002 实现导入功能（上传+API+结果反馈+错误数据下载）
- P0-005-013-011 通用审批流程展示组件
  - P0-005-013-011-001 审批流程图UI开发（审批节点横向流程图+当前节点高亮+已审节点绿色+未审节点灰色）
    - P0-005-013-011-001-001 实现审批流程图UI（审批节点横向流程图+当前节点高亮黄色+已审节点绿色+未审节点灰色+审批人头像）
    - P0-005-013-011-001-002 验证流程图展示（节点颜色正确+当前节点高亮+审批人信息显示）
    - P0-005-013-011-001-003 验证UI渲染（布局正确+交互响应+数据绑定+空状态/加载态）
  - P0-005-013-011-002 审批记录列表开发（审批人/审批时间/审批意见/审批结果列表+按时间排序）
    - P0-005-013-011-002-001 实现审批记录列表（审批人+审批时间+审批意见+审批结果+按时间倒序排列）
    - P0-005-013-011-002-002 验证审批记录（记录完整+时间排序+意见展示+结果标签颜色正确）
    - P0-005-013-011-002-003 验证功能（核心用例通过+边界场景+异常降级+日志可追溯）
- P0-005-013-012 通用备注留言组件
  - P0-005-013-012-001 备注区UI开发（备注文本框+留言历史时间线列表+@提及输入框）
    - P0-005-013-012-001-001 实现备注区UI（备注文本框+留言历史时间线列表+@提及输入框+表情选择器）
    - P0-005-013-012-001-002 验证备注区UI（文本框可编辑+时间线列表渲染+@提及下拉选择）
    - P0-005-013-012-001-003 验证UI渲染（布局正确+交互响应+数据绑定+空状态/加载态）
  - P0-005-013-012-002 留言逻辑开发（留言提交+@提及人员通知+留言附件上传）
    - P0-005-013-012-002-001 实现留言逻辑（留言提交+@提及人员站内通知+留言附件上传+留言按时间排序）
      - P0-005-013-012-002-001-001 实现留言提交（留言内容+附件+@提及人员+提交API+留言ID生成）
      - P0-005-013-012-002-001-002 实现@提及人员通知（解析@人员→站内消息通知+消息类型：提及+跳转到留言位置）
      - P0-005-013-012-002-001-003 实现留言附件上传+时间排序（附件上传到文件服务+留言按创建时间倒序+分页加载）
    - P0-005-013-012-002-002 验证留言功能（提交成功+@人员收到通知+附件上传下载+排序正确）
    - P0-005-013-012-002-003 验证逻辑（主流程通过+边界处理+异常回滚+数据一致性）
- P0-005-013-013 通用附件关联组件
  - P0-005-013-013-001 附件区UI开发（附件上传按钮+附件列表+附件预览+附件下载+附件删除）
    - P0-005-013-013-001-001 实现附件区UI（附件上传按钮+附件列表含文件名/大小/上传人/时间+预览/下载/删除操作）
    - P0-005-013-013-001-002 验证附件区UI（上传按钮+列表渲染+操作按钮显隐权限控制）
    - P0-005-013-013-001-003 验证UI渲染（布局正确+交互响应+数据绑定+空状态/加载态）
  - P0-005-013-013-002 附件逻辑开发（文件上传+按业务单据ID关联+文件大小统计+预览打开）
    - P0-005-013-013-002-001 实现附件逻辑（文件上传API+按业务单据ID关联+文件大小统计+预览弹窗打开+删除确认）
      - P0-005-013-013-002-001-001 实现文件上传API（MultipartFile→文件服务→返回文件ID+按业务单据ID关联+批量上传）
      - P0-005-013-013-002-001-002 实现文件大小统计+预览（文件大小汇总+预览弹窗+图片/PDF内嵌预览）
      - P0-005-013-013-002-001-003 实现删除确认+权限（删除前确认弹窗+删除权限校验+逻辑删除+关联记录清理）
    - P0-005-013-013-002-002 验证附件功能（上传成功+单据关联正确+预览可用+删除生效+大小统计准确）
    - P0-005-013-013-002-003 验证逻辑（主流程通过+边界处理+异常回滚+数据一致性）
- P0-005-013-014 通用消息通知弹窗组件
  - P0-005-013-014-001 消息铃铛UI开发（右上角铃铛图标+未读数量Badge+点击弹出消息面板）
    - P0-005-013-014-001-001 实现消息铃铛UI（右上角铃铛图标+未读数量Badge红点+点击弹出消息面板）
    - P0-005-013-014-001-002 验证铃铛UI（Badge数量正确+面板弹出+已读后Badge更新）
    - P0-005-013-014-001-003 验证UI渲染（布局正确+交互响应+数据绑定+空状态/加载态）
  - P0-005-013-014-002 消息面板开发（消息分类Tab：通知/待办/预警+消息列表+一键已读+跳转详情）
    - P0-005-013-014-002-001 实现消息面板（消息分类Tab：通知/待办/预警+消息列表+一键已读+点击跳转详情页）
    - P0-005-013-014-002-002 验证消息面板（Tab切换+列表加载+已读标记+跳转正确+空数据提示）
    - P0-005-013-014-002-003 验证功能（核心用例通过+边界场景+异常降级+日志可追溯）
- P0-005-013-015 通用数据比对组件
  - P0-005-013-015-001 比对UI开发（左右分栏：变更前/变更后+差异字段高亮+字段名称标签）
    - P0-005-013-015-001-001 实现比对UI（左右分栏布局：变更前/变更后+差异字段高亮红色+字段名称标签+变更类型标识）
    - P0-005-013-015-001-002 验证比对UI（左右分栏正确+差异高亮+字段对齐+变更类型标识清晰）
    - P0-005-013-015-001-003 验证UI渲染（布局正确+交互响应+数据绑定+空状态/加载态）
  - P0-005-013-015-002 比对逻辑开发（数据快照对比+差异字段提取+变更类型标识：新增/修改/删除）
    - P0-005-013-015-002-001 实现比对逻辑（数据快照对比+差异字段提取+变更类型标识：新增字段绿色/修改字段黄色/删除字段红色）
      - P0-005-013-015-002-001-001 实现数据快照对比（加载变更前/后快照→逐字段比对→差异字段列表）
      - P0-005-013-015-002-001-002 实现差异字段提取（字段名+变更前值+变更后值+变更类型标识：新增/修改/删除）
      - P0-005-013-015-002-001-003 实现比对结果渲染（新增字段绿色+修改字段黄色+删除字段红色+字段名标签+值对比）
    - P0-005-013-015-002-002 验证比对逻辑（差异字段准确识别+变更类型正确+快照数据完整）
    - P0-005-013-015-002-003 验证逻辑（主流程通过+边界处理+异常回滚+数据一致性）
- P0-005-013-016 编码自动生成组件（CodeGenerator.vue）
  - P0-005-013-016-001 编码组件UI开发（编码预览展示+手工输入/自动生成切换+编码规则配置入口）
    - P0-005-013-016-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-013-016-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-013-016-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-013-016-002 编码生成逻辑开发（调用后端编码预览接口+保存时编码确认+手工编码唯一性校验）
    - P0-005-013-016-002-001 定义接口路由与方法签名
    - P0-005-013-016-002-002 实现接口逻辑（参数处理+Service调用+响应包装）

### P0-005-014 业务增强组件
- P0-005-014-001 物料自由配方组件
  - P0-005-014-001-001 配方UI开发（BOM物料行表格+替代料折叠行+用量计算列+添加/删除行按钮）
    - P0-005-014-001-001-001 实现配方UI（BOM物料行vxe-table+替代料折叠行+用量计算列+添加/删除行按钮+合计行）
    - P0-005-014-001-001-002 实现配方计算逻辑（BOM层级展开+用量汇总+替代料替换选择+损耗率计算+用量校验）
    - P0-005-014-001-001-003 验证配方计算（层级展开正确+用量汇总准确+替代料替换+损耗率计算正确）
      - P0-005-014-001-001-003-001 实现替代料优先级排序（替代料按优先级排序+首选替代料默认选中+手动切换替代料）
      - P0-005-014-001-001-003-002 实现替代料替换逻辑（原物料→替代料→用量不变+单位换算+价格重新计算）
      - P0-005-014-001-001-003-003 实现损耗率计算（最终用量=基础用量×(1+损耗率)+四舍五入+损耗率来源：物料主数据/BOM行）
  - P0-005-014-001-002 配方计算逻辑开发（BOM层级展开+用量汇总+替代料替换+损耗率计算）
    - P0-005-014-001-002-001 实现BOM层级展开逻辑（递归解析BOM树+层级编号+物料用量汇总+层级深度校验）
      - P0-005-014-001-002-001-001 递归解析BOM树（根据根物料ID→查BOM子件→递归查子件的BOM→直到叶子节点）
      - P0-005-014-001-002-001-002 生成层级编号（根节点=1, 子节点=1.1/1.2, 孙节点=1.1.1/1.1.2... 层级深度限制10层）
      - P0-005-014-001-002-001-003 汇总物料用量（各层级用量×父级用量→累计总用量+损耗率叠加+替代料选择）
      - P0-005-014-001-002-001-004 层级深度校验（超过10层报错+循环引用检测+BOM不存在提示）
    - P0-005-014-001-002-002 实现替代料替换+损耗率计算（替代料优先级排序+替换选择+损耗率叠加+最终用量=基础用量×(1+损耗率)）
      - P0-005-014-001-002-002-001 实现替代料优先级排序（替代料按优先级排序+首选替代料默认选中+手动切换替代料）
      - P0-005-014-001-002-002-002 实现替代料替换逻辑（原物料→替代料→用量不变+单位换算+价格重新计算）
      - P0-005-014-001-002-002-003 实现损耗率计算（最终用量=基础用量×(1+损耗率)+四舍五入+损耗率来源：物料主数据/BOM行）
    - P0-005-014-001-002-003 验证逻辑（主流程通过+边界处理+异常回滚+数据一致性）
- P0-005-014-002 费用分摊比例组件
  - P0-005-014-002-001 分摊UI开发（分摊规则表格：分摊对象+分摊比例+分摊金额+自动计算合计行）
    - P0-005-014-002-001-001 实现分摊UI（分摊规则表格：分摊对象+分摊比例+分摊金额+自动计算合计行+尾差显示）
    - P0-005-014-002-001-002 实现分摊计算逻辑（按比例自动分配金额+尾差处理归入最大项+总额校验=100%）
    - P0-005-014-002-001-003 验证分摊计算（比例分配准确+尾差处理合理+总额100%校验通过）
  - P0-005-014-002-002 分摊计算逻辑开发（按比例自动分配金额+尾差处理+总额校验=100%）
    - P0-005-014-002-002-001 实现按比例分配逻辑（各分摊对象金额=总额×比例+四舍五入保留2位+分配结果列表）
    - P0-005-014-002-002-002 实现尾差处理+总额校验（分配总额与原总额差值归入最大分摊项+校验分配总额=100%+超出提示）
    - P0-005-014-002-002-003 验证逻辑（主流程通过+边界处理+异常回滚+数据一致性）
- P0-005-014-003 地区省市区三级选择组件
  - P0-005-014-003-001 组件封装开发（ElCascader+省市区数据源+懒加载区级+选中值格式：省/市/区编码）
    - P0-005-014-003-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-014-003-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-014-003-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-014-003-002 数据源维护（省市区JSON数据+热更新+地区编码映射）
    - P0-005-014-003-002-001 维护省市区JSON数据文件（国家统计局最新编码+三层级映射+编码→名称索引+数据缓存到Redis）
    - P0-005-014-003-002-002 验证数据源（三级联动正确+编码映射准确+新增区划数据可热更新）
- P0-005-014-004 备注快捷话术选择组件
  - P0-005-014-004-001 组件封装开发（话术模板下拉+分类筛选+一键填充备注文本框+常用话术置顶）
    - P0-005-014-004-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-014-004-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-014-004-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-014-004-002 话术管理逻辑（话术CRUD+话术分类+使用频次排序）
    - P0-005-014-004-002-001 实现话术CRUD方法（新增/编辑/删除/查询+分类管理+使用频次排序）
    - P0-005-014-004-002-002 实现话术快速使用（快捷键呼出+搜索+选择插入+使用计数+1）
- P0-005-014-005 预警数值设置组件
  - P0-005-014-005-001 组件封装开发（预警阈值输入：上限值/下限值+预警条件表达式编辑器+预警级别选择）
    - P0-005-014-005-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-014-005-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-014-005-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-014-005-002 预警计算逻辑（阈值比较+条件表达式解析+预警触发通知）
    - P0-005-014-005-002-001 实现阈值比较方法（当前值与上下限比较+条件表达式解析引擎）
    - P0-005-014-005-002-002 实现预警触发（超阈值→通知消息推送+预警记录写入+预警级别标记）
- P0-005-014-006 批次有效期展示组件
  - P0-005-014-006-001 组件封装开发（批次信息行：生产日期+有效期+剩余天数+过期状态高亮色标）
    - P0-005-014-006-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-014-006-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-014-006-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-014-006-002 有效期计算逻辑（剩余天数实时计算+临期预警7/30/90天色标+已过期红色标记）
    - P0-005-014-006-002-001 实现阈值比较方法（当前值与上下限比较+条件表达式解析引擎）
    - P0-005-014-006-002-002 实现预警触发（超阈值→通知消息推送+预警记录写入+预警级别标记）
- P0-005-014-007 库存可用量实时展示组件
  - P0-005-014-007-001 组件封装开发（Popover弹出层：可用量/在途量/占用量/安全库存分项展示+库存状态色标）
    - P0-005-014-007-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
    - P0-005-014-007-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
    - P0-005-014-007-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）
  - P0-005-014-007-002 实时查询逻辑（商品+仓库维度实时查询+库存数据缓存+防抖刷新）
    - P0-005-014-007-002-001 实现查询方法（商品+仓库维度API+请求参数构建+响应解析）
    - P0-005-014-007-002-002 实现缓存与防抖（localStorage短时缓存+300ms防抖+Loading状态）
    - P0-005-014-009 字段配置UI设计器（可视化规则配置：字段联动规则设计器+计算公式编辑器+校验规则配置器+显隐规则配置器+规则优先级显示+租户/角色/用户级切换+规则预览+规则模拟执行）
      - P0-005-014-009-001 设计器组件开发（拖拽式规则配置+字段选择+操作符选择+值来源选择+逻辑组合+预览面板）
        - P0-005-014-009-001-001 实现联动规则设计器（源字段→触发条件→目标字段→执行动作+可视化连线+多规则排序）
        - P0-005-014-009-001-002 实现计算公式编辑器（字段变量+运算符+函数+语法校验+实时预览+模拟执行）
        - P0-005-014-009-001-003 实现校验规则配置器（字段+规则类型+参数+错误消息+优先级+启用/禁用）
    - P0-005-014-010 规则优先级体系（租户级规则>角色级规则>用户级规则+同级别按优先级字段排序+规则冲突时高优先级覆盖+规则生效范围：全局/模块/页面/字段+规则版本管理）
      - P0-005-014-010-001 优先级解析引擎开发（规则加载+优先级排序+冲突检测+生效范围过滤+版本管理）
        - P0-005-014-010-001-001 实现规则优先级解析（加载所有规则→按scope过滤→按priority排序→按level覆盖→返回最终规则集）
    - P0-005-014-011 字段默认值规则（新建单据时字段自动填充：按优先级租户>角色>用户+默认值类型：固定值/当前日期/当前用户/当前部门/上游字段值/SQL查询值+默认值触发时机：新建/复制/下推）
      - P0-005-014-011-001 默认值规则引擎开发（规则CRUD+优先级解析+默认值计算+多触发时机+前端联动）
        - P0-005-014-011-001-001 实现默认值规则（规则配置+优先级+计算引擎：固定值/日期/用户/部门/上游值/SQL+触发时机）
    - P0-005-014-012 字段只读规则（按角色/状态/条件控制字段只读：审批中不可修改+已审核不可修改+特定角色只读+条件触发只读+只读时UI禁用+提示原因）
      - P0-005-014-012-001 只读规则引擎开发（规则CRUD+条件解析+状态联动+前端字段禁用）
        - P0-005-014-012-001-001 实现只读规则（按角色/状态/条件控制+解析引擎+前端字段禁用API+只读原因提示）

### P0-005-015 组合式函数（Composables）
- P0-005-015-001 usePageState（页面状态管理）
  - P0-005-015-001-001 状态切换逻辑（新增/编辑/详情三种状态+状态切换方法+状态守卫）
    - P0-005-015-001-001-001 实现状态管理方法（新增/编辑/详情切换+状态守卫+字段可编辑性联动）
    - P0-005-015-001-001-002 实现状态UI联动（详情态禁用+编辑态启用+按钮显隐+标题切换）
  - P0-005-015-001-002 数据脏检测逻辑（深度对比原始数据与当前数据+isDirty标识+离开确认弹窗）
    - P0-005-015-001-002-001 实现深度对比方法（原始数据JSON vs 当前数据+递归比较+isDirty标识）
    - P0-005-015-001-002-002 实现离开拦截（beforeRouteLeave+isDirty判断+确认弹窗+放弃/保存选择）
- P0-005-015-002 useTableConfig（表格配置管理）
  - P0-005-015-002-001 列配置加载逻辑（从数据视图接口获取列配置+列元数据解析+默认列格式生成）
    - P0-005-015-002-001-001 定义接口路由与方法签名
    - P0-005-015-002-001-002 实现接口逻辑（参数处理+Service调用+响应包装）
  - P0-005-015-002-002 格式设置持久化逻辑（列显隐/顺序/宽度/固定列保存到localStorage+恢复加载）
    - P0-005-015-002-002-001 实现localStorage读写（列显隐/顺序/宽度/固定列序列化存储+恢复加载）
    - P0-005-015-002-002-002 实现用户级格式存储（后端API保存+按用户+页面维度隔离+导入导出）
- P0-005-015-003 useFieldConfig（字段配置管理）
  - P0-005-015-003-001 联动规则引擎（字段值变更→联动带出值/联动过滤/联动显隐触发链执行）
    - P0-005-015-003-001-001 实现联动规则解析器（配置JSON解析+触发字段监听+动作类型判断）
      - P0-005-015-003-001-001-001 解析联动规则配置JSON（触发字段+触发条件+动作类型+目标字段+动作参数）
      - P0-005-015-003-001-001-002 监听触发字段变化（watch触发字段→读取当前值→匹配触发条件）
      - P0-005-015-003-001-001-003 执行联动动作（值联动→赋值目标字段 / 显隐联动→设置目标字段visible / 必填联动→设置required）
    - P0-005-015-003-001-002 实现联动执行引擎（值带出→字段赋值/过滤→选项过滤/显隐→字段切换）
  - P0-005-015-003-002 计算规则引擎（公式解析+依赖字段监听+自动计算触发+精度处理）
    - P0-005-015-003-002-001 实现公式解析器（表达式词法分析+变量替换+运算符优先级+函数调用）
    - P0-005-015-003-002-002 实现自动计算触发（依赖字段watch+计算执行+精度处理+循环依赖检测）
  - P0-005-015-003-003 显隐规则引擎（条件表达式解析+字段显隐动态切换+布局自动重排）
    - P0-005-015-003-003-001 实现条件表达式解析（逻辑运算符+比较运算符+字段值取值+括号嵌套）
    - P0-005-015-003-003-002 实现显隐动态切换（条件变更→字段显隐+布局重排+表单校验联动）
  - P0-005-015-003-004 校验规则引擎（必填/正则/跨字段/唯一性/后端异步校验链执行+错误信息聚合）
    - P0-005-015-003-004-001 实现校验链执行器（必填→正则→跨字段→唯一性→后端异步 顺序执行）
    - P0-005-015-003-004-002 实现错误信息聚合（多规则错误收集+字段级展示+全局汇总提示）
      - P0-005-015-003-004-002-001 收集多规则校验错误（遍历所有字段→收集校验失败项→字段编码+错误信息+规则类型）
      - P0-005-015-003-004-002-002 字段级错误展示（在字段下方显示该字段的校验错误+红色边框+错误图标）
      - P0-005-015-003-004-002-003 全局错误汇总提示（页面顶部错误汇总条+点击定位到错误字段+错误总数统计）
- P0-005-015-004 usePermission（权限判断）
  - P0-005-015-004-001 按钮权限判断（hasBtnPermission：从permissionStore获取按钮权限标识+判断方法）
    - P0-005-015-004-001-001 定义State类型与初始值（interface+reactive+持久化字段）
    - P0-005-015-004-001-002 实现Actions（异步API+状态更新+错误处理+loading）
    - P0-005-015-004-001-003 实现Getters（派生计算+缓存+类型安全）
  - P0-005-015-004-002 字段权限判断（hasFieldPermission：从字段权限配置获取+字段只读/隐藏/可见判断）
    - P0-005-015-004-002-001 实现hasFieldPermission方法（从字段权限配置API获取+字段只读/隐藏/可见判断+v-permission指令集成）
      - P0-005-015-004-002-001-001 实现hasFieldPermission方法（从字段权限配置API获取当前用户字段权限+缓存+角色判断）
      - P0-005-015-004-002-001-002 实现字段权限渲染逻辑（只读→组件disabled+隐藏→v-if=false+可见→正常渲染+权限变更即时生效）
      - P0-005-015-004-002-001-003 实现v-permission指令集成（自定义指令v-permission:readonly/v-permission:hidden+自动绑定）
    - P0-005-015-004-002-002 实现字段权限联动（只读→禁用组件+隐藏→移除DOM+可见→正常渲染+权限变更即时生效）
    - P0-005-015-004-002-003 验证字段权限（不同角色看到不同字段+只读不可编辑+隐藏不可见+权限缓存生效）
  - P0-005-015-004-003 数据权限判断（hasDataPermission：数据范围校验+组织维度过滤）
    - P0-005-015-004-003-001 实现hasDataPermission方法（当前用户数据范围+组织维度匹配+权限判断）
    - P0-005-015-004-003-002 实现数据过滤集成（SQL级数据过滤+前端控件禁用+操作按钮权限联动）
- P0-005-015-005 useDataView（数据视图加载）
  - P0-005-015-005-001 视图字段元数据加载（调用视图配置接口+字段元数据解析+列配置映射）
    - P0-005-015-005-001-001 定义接口路由与方法签名
    - P0-005-015-005-001-002 实现接口逻辑（参数处理+Service调用+响应包装）
  - P0-005-015-005-002 动态查询逻辑（查询条件构建+分页参数+排序参数+请求发送+响应解析）
    - P0-005-015-005-002-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
    - P0-005-015-005-002-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
    - P0-005-015-005-002-003 实现动态查询逻辑查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P0-005-015-006 useCodeRule（编码规则生成）
  - P0-005-015-006-001 编码预览逻辑（调用后端编码预览接口+编码展示+手工/自动切换）
    - P0-005-015-006-001-001 定义接口路由与方法签名
    - P0-005-015-006-001-002 实现接口逻辑（参数处理+Service调用+响应包装）
  - P0-005-015-006-002 编码确认逻辑（保存时编码确认+手工编码唯一性校验+编码占用处理）
    - P0-005-015-006-002-001 实现编码确认对话框（编码预览+手工修改+唯一性校验+确认/取消）
    - P0-005-015-006-002-002 实现编码冲突处理（占用提示+自动递增+手工编码保存逻辑）

### P0-005-016 数据字典管理（V10.0从P2-013迁入，为P1业务模块提供字典管理界面）

> **依赖**：P0-001（后端框架含Redis缓存）+P0-002（前端框架）
> **概述**：提供数据字典类型+字典数据的管理界面，P1业务模块开发时可直接使用字典下拉组件绑定字典源

#### P0-005-016-001 数据字典后端CRUD接口
- P0-005-016-001-001 数据字典Entity/Mapper/Service/Controller开发（sys_dict_type+sys_dict_data主从CRUD+缓存自动刷新）
  - P0-005-016-001-001-001 定义DictTypeEntity（@TableName"sys_dict_type"+dict_type_code唯一+dict_type_name+status+排序+remark+@TableLogic+@Version）+DictDataEntity（@TableName"sys_dict_data"+dict_type关联+dict_value+dict_label+排序+status+css_class）
  - P0-005-016-001-001-002 编写DictTypeMapper+DictDataMapper（extends BaseMapperX+自定义查询方法）
  - P0-005-016-001-001-003 编写DictTypeService+DictDataService（主从CRUD+dict_type_code唯一性校验+缓存@Cacheable("sys_dict")+缓存刷新@CacheEvict+批量启用/禁用）
  - P0-005-016-001-001-004 编写DictController（@RestController+@RequestMapping"/api/system/dict"+字典类型CRUD+字典数据CRUD+按dict_type_code查询字典数据列表+缓存手动刷新+导入/导出）
  - P0-005-016-001-001-005 验证数据字典接口（CRUD+唯一性+缓存命中+缓存刷新+批量操作+导入导出）

#### P0-005-016-002 数据字典管理前端页面
- P0-005-016-002-001 数据字典类型列表页（P04：字典编码+字典名称+状态+排序+搜索+批量启用/禁用+导入/导出）
  - P0-005-016-002-001-001 实现搜索区+列表表格+操作按钮+联调验证
- P0-005-016-002-002 新增/编辑字典类型表单页（P07）
  - P0-005-016-002-002-001 实现表单（编码+名称+状态+排序+备注+联调验证）
- P0-005-016-002-003 字典数据列表页（P04：按字典类型过滤+字典值+字典标签+排序+状态+CSS样式类）
  - P0-005-016-002-003-001 实现搜索区+列表表格+字典类型联动过滤+联调验证
- P0-005-016-002-004 新增/编辑字典数据表单页（P07）
  - P0-005-016-002-004-001 实现表单（字典类型下拉+字典值+标签+排序+状态+CSS类+联调验证）

### P0-005-017 系统参数管理（V10.0从P2-013迁入，为P1业务模块提供参数管理界面）

> **依赖**：P0-001（后端框架含Redis缓存）+P0-002（前端框架）
> **概述**：提供系统参数的分组管理界面，P1业务模块开发时可通过参数编码读取系统参数值

#### P0-005-017-001 系统参数后端CRUD接口
- P0-005-017-001-001 系统参数Entity/Mapper/Service/Controller开发（按分类分组CRUD+参数缓存刷新）
  - P0-005-017-001-001-001 定义SysParamEntity（@TableName"sys_param"+param_group参数分组+param_code参数编码唯一+param_name参数名称+param_value参数值+param_type值类型string/number/boolean/json+sort排序+remark+@TableLogic+@Version）
  - P0-005-017-001-001-002 编写SysParamMapper（extends BaseMapperX+selectByGroup按分组查询+selectByCode按编码查询+selectGroups查询所有分组列表）
  - P0-005-017-001-001-003 编写SysParamService（CRUD+param_code唯一性校验+缓存@Cacheable("sys_param")+缓存刷新@CacheEvict+getParamValue按编码获取参数值+getParamGroup获取分组参数列表+参数值类型校验string/number/boolean/json）
  - P0-005-017-001-001-004 编写SysParamController（@RestController+@RequestMapping"/api/system/param"+参数CRUD+按分组查询+缓存手动刷新+导入/导出）
  - P0-005-017-001-001-005 验证系统参数接口（CRUD+唯一性+缓存+类型校验+分组查询）

#### P0-005-017-002 系统参数管理前端页面
- P0-005-017-002-001 系统参数配置页（P13左右分栏：左侧参数分组树+右侧参数列表+按分组筛选+新增/编辑/删除+缓存刷新按钮）
  - P0-005-017-002-001-001 实现左右分栏布局（el-aside分组树+el-main参数列表+分组选择联动过滤+联调验证）
- P0-005-017-002-002 新增/编辑系统参数表单页（P07弹窗）
  - P0-005-017-002-002-001 实现表单（参数分组下拉+编码+名称+值+值类型选择+排序+备注+类型联动校验+联调验证）

---

## P0-006 组织架构模块开发

> **依赖**：P0-001~P0-005全部完成  
> **概述**：完成组织架构全功能开发，为后续所有业务模块提供公司、部门、岗位、员工基础数据支撑

### P0-006-001 组织架构后端开发
- P0-006-001-001 公司CRUD接口（org_company：Entity/Mapper/Service/Controller + 公司编码唯一性校验）
  - P0-006-001-001-001 Service接口定义+实现类编写
    - P0-006-001-001-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-006-001-001-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-006-001-001-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-006-001-002 部门CRUD接口（org_department：Entity/Mapper/Service/Controller + 树形结构+部门编码唯一性）
  - P0-006-001-002-001 Service接口定义+实现类编写
    - P0-006-001-002-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-006-001-002-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-006-001-002-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-006-001-003 岗位CRUD接口（org_position：Entity/Mapper/Service/Controller + 所属部门关联）
  - P0-006-001-003-001 Service接口定义+实现类编写
    - P0-006-001-003-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-006-001-003-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-006-001-003-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-006-001-004 组织架构工作台聚合数据接口（公司数量/部门数量/岗位数量/在职人数统计）
  - P0-006-001-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-006-001-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-006-001-004-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P0-006-002 组织架构前端页面
- P0-006-002-000 组织架构工作台页（P02工作台：公司/部门/岗位KPI卡片+快捷操作+待办列表+组织概览图表）
  - P0-006-002-000-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-006-002-000-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-006-002-000-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-006-002-001 组织中心首页（P02工作台：组织架构图可视化+统计数据卡片+公司/部门/岗位概览）
  - P0-006-002-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-006-002-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-006-002-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-006-002-002 公司管理列表页（P04：查询区+操作区+列表区）
  - P0-006-002-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-006-002-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-006-002-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-006-002-003 新增/编辑公司表单页（P07：公司基本信息+负责人+联系方式）
  - P0-006-002-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-006-002-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-006-002-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-006-002-004 部门管理树形列表页（P05：左侧部门树+右侧部门列表+联动选中）
  - P0-006-002-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-006-002-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-006-002-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-006-002-005 新增/编辑部门表单页（P07：部门基本信息+上级部门选择+负责人选择）
  - P0-006-002-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-006-002-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-006-002-005-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-006-002-006 岗位管理列表页（P04：查询区+操作区+列表区）
  - P0-006-002-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-006-002-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-006-002-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-006-002-007 新增/编辑岗位表单页（P07：岗位基本信息+所属部门选择）
  - P0-006-002-007-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-006-002-007-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-006-002-007-001-002 验证功能（核心用例通过+边界处理+异常降级）

---

## P0-007 商品管理模块开发

> **依赖**：P0-006（组织架构提供公司/部门数据）  
> **概述**：完成商品全功能开发，为后续销售、采购、库存、生产等所有业务模块提供商品基础数据支撑

### P0-007-001 商品管理后端开发
- P0-007-001-001 商品分类CRUD接口（prod_product_class：Entity/Mapper/Service/Controller + 树形结构+分类编码唯一性）
  - P0-007-001-001-001 Service接口定义+实现类编写
    - P0-007-001-001-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-007-001-001-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-007-001-001-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-007-001-002 商品主表CRUD接口（prod_product：Entity/Mapper/Service/Controller + 商品编码唯一性+参数判重逻辑）
  - P0-007-001-002-001 Service接口定义+实现类编写
    - P0-007-001-002-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-007-001-002-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-007-001-002-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-007-001-003 商品多单位CRUD接口（prod_product_unit：Entity/Mapper/Service/Controller + 基本单位标识+转换率校验）
  - P0-007-001-003-001 Service接口定义+实现类编写
    - P0-007-001-003-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-007-001-003-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-007-001-003-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-007-001-004 商品控制策略CRUD接口（prod_product_control：Entity/Mapper/Service/Controller + 控制策略字段完整性校验）
  - P0-007-001-004-001 Service接口定义+实现类编写
    - P0-007-001-004-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-007-001-004-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-007-001-004-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-007-001-005 商品安全库存CRUD接口（prod_product_safety_stock：Entity/Mapper/Service/Controller + 仓库维度唯一性）
  - P0-007-001-005-001 Service接口定义+实现类编写
    - P0-007-001-005-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-007-001-005-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-007-001-005-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-007-001-006 商品附件管理接口（prod_product_attachment：上传/下载/删除/列表查询）
  - P0-007-001-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-001-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-001-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-001-007 商品标准价CRUD接口（prod_product_standard_price：Entity/Mapper/Service/Controller）
  - P0-007-001-007-001 Service接口定义+实现类编写
    - P0-007-001-007-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-007-001-007-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-007-001-007-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-007-001-008 商品购价核定CRUD接口（prod_product_purchase_price：Entity/Mapper/Service/Controller + 供应商维度唯一性）
  - P0-007-001-008-001 Service接口定义+实现类编写
    - P0-007-001-008-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-007-001-008-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-007-001-008-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-007-001-009 商品销价核定CRUD接口（prod_product_sale_price：Entity/Mapper/Service/Controller + 客户维度唯一性）
  - P0-007-001-009-001 Service接口定义+实现类编写
    - P0-007-001-009-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-007-001-009-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-007-001-009-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-007-001-010 商品竞品CRUD接口（prod_product_competitor：Entity/Mapper/Service/Controller）
  - P0-007-001-010-001 Service接口定义+实现类编写
    - P0-007-001-010-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-007-001-010-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-007-001-010-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-007-001-011 商品BOM CRUD接口（prod_product_bom + prod_product_bom_detail：主从CRUD + BOM层级校验+用量校验）
  - P0-007-001-011-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-001-011-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-001-011-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-001-012 商品BOM成本计算接口（按BOM展开逐层计算材料成本+工序加工成本）
  - P0-007-001-012-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-001-012-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-001-012-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-001-013 商品工序CRUD接口（prod_product_process + prod_product_process_param + prod_product_process_price：主从CRUD）
  - P0-007-001-013-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-001-013-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-001-013-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-001-014 标准工序CRUD接口（prod_standard_process：Entity/Mapper/Service/Controller）
  - P0-007-001-014-001 Service接口定义+实现类编写
    - P0-007-001-014-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-007-001-014-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-007-001-014-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-007-001-015 商品管理工作台聚合数据接口（商品总数/分类数量/低库存预警/最近添加统计）
  - P0-007-001-015-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-001-015-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-001-015-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-001-016 商品其他信息CRUD接口（prod_product_other：Entity/Mapper/Service/Controller + 商品ID关联校验 + HS编码/条码唯一性校验）
  - P0-007-001-016-001 Service接口定义+实现类编写
    - P0-007-001-016-001-001 编写Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-007-001-016-001-002 编写ServiceImpl实现类（HS编码校验+条码校验+商品关联+Mapper调用+事务管理+异常处理）
    - P0-007-001-016-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）

> **V8补充 — 商品扩展表CRUD（P0-003-004-014~022 DDL + P0-007-001-017~026 后端 + P0-007-002-024~038 前端）**
> **V10.0标注**：以下8个功能点为V8版本补充的**增强扩展项**，超出设计文档V4.3第11.5节基线（基线19个功能点）。扩展项基于设计文档第13章数据表定义，为商品管理提供更完整的能力。**全部保留开发**——其中商品图片管理(P0-007-002-032)为用户明确要求保留项。开发执行时：19个基线功能点优先开发，8个扩展项作为增强在基线完成后开发。
> 扩展项清单：prod_product_price(商品价格) / prod_product_attribute+value(属性) / prod_product_spec(规格) / prod_product_barcode(条码) / prod_product_image(图片) / prod_product_relation(关联) / prod_product_tag(标签) / prod_serial_template(序列号模板)

- P0-007-001-017 商品价格CRUD接口（prod_product_price：Entity/Mapper/Service/Controller + 价格类型+币种+生效期校验）
  - P0-007-001-017-001 Service接口定义+实现类编写
    - P0-007-001-017-001-001 编写Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-007-001-017-001-002 编写ServiceImpl实现类（价格类型校验+有效期冲突检测+Mapper调用+事务管理）
    - P0-007-001-017-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-007-001-018 商品属性与属性值CRUD接口（prod_product_attribute主表+prod_product_attribute_value从表：主从CRUD + 属性名唯一性校验）
  - P0-007-001-018-001 Service接口定义+实现类编写
    - P0-007-001-018-001-001 编写Service接口（主从CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-007-001-018-001-002 编写ServiceImpl实现类（主从保存+属性名唯一性+从表批量维护+事务管理）
    - P0-007-001-018-001-003 验证Service（单元测试+主从CRUD正确+事务回滚+异常处理）
- P0-007-001-019 商品规格CRUD接口（prod_product_spec：Entity/Mapper/Service/Controller + 规格名唯一性校验）
  - P0-007-001-019-001 Service接口定义+实现类编写
    - P0-007-001-019-001-001 编写Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-007-001-019-001-002 编写ServiceImpl实现类（规格名校验+Mapper调用+事务管理+异常处理）
    - P0-007-001-019-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-007-001-020 商品条码CRUD接口（prod_product_barcode：Entity/Mapper/Service/Controller + 条码值全局唯一性校验）
  - P0-007-001-020-001 Service接口定义+实现类编写
    - P0-007-001-020-001-001 编写Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-007-001-020-001-002 编写ServiceImpl实现类（条码唯一性校验+默认条码切换+Mapper调用+事务管理）
    - P0-007-001-020-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-007-001-021 商品图片CRUD接口（prod_product_image：上传/排序/删除/设为主图 + 文件存储集成）
  - P0-007-001-021-001 实现核心逻辑（代码编写+文件存储+排序逻辑+异常处理）
    - P0-007-001-021-001-001 编写核心代码（图片上传+排序管理+主图设置+文件存储集成+日志记录）
    - P0-007-001-021-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-001-022 商品关联CRUD接口（prod_product_relation：双向关联管理 + 关联类型校验）
  - P0-007-001-022-001 Service接口定义+实现类编写
    - P0-007-001-022-001-001 编写Service接口（CRUD方法签名+关联类型枚举+双向同步逻辑+@Transactional注解）
    - P0-007-001-022-001-002 编写ServiceImpl实现类（双向关联同步+关联类型校验+不可自关联+事务管理）
    - P0-007-001-022-001-003 验证Service（单元测试+双向同步正确+事务回滚+异常处理）
- P0-007-001-023 商品标签关联CRUD接口（prod_product_tag：批量绑定/解绑 + 标签定义关联校验）
  - P0-007-001-023-001 实现核心逻辑（批量绑定+解绑+标签校验+异常处理）
    - P0-007-001-023-001-001 编写核心代码（批量绑定/解绑+标签定义校验+去重+日志记录）
    - P0-007-001-023-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-001-024 序列号模板CRUD接口（prod_serial_template：Entity/Mapper/Service/Controller + 模板编码唯一性）
  - P0-007-001-024-001 Service接口定义+实现类编写
    - P0-007-001-024-001-001 编写Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-007-001-024-001-002 编写ServiceImpl实现类（模板编码唯一性+序列号预览+Mapper调用+事务管理）
    - P0-007-001-024-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-007-001-025 商品扩展表前端页面补充（P0-007-002-018至P0-007-002-027，对应后端P0-007-001-017至P0-007-001-024）
  > 前端页面任务在P0-007-002节末尾补充，每张扩展表对应一个列表页(P04)+表单页(P07)组合
  - P0-007-001-025-001 本任务为后端标记节点，实际前端开发见P0-007-002-018~027
    - P0-007-001-025-001-001 确认后端接口全部可调通+Swagger文档完整
    - P0-007-001-025-001-002 向前端任务移交接口契约（DTO/VO/接口路径/校验规则）

### P0-007-002 商品管理前端页面
- P0-007-002-001 商品管理工作台（P02：统计卡片+快捷入口+最近商品列表）
  - P0-007-002-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-002 商品分类树形列表页（P05：左侧分类树+右侧分类列表）
  - P0-007-002-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-003 新增/编辑商品分类表单页（P07）
  - P0-007-002-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-004 标准工序列表页（P04）
  - P0-007-002-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-005 商品中心主从列表页（P03：关联信息区含基本信息/多单位/安全库存/附件/BOM/标准价等标签页）
  - P0-007-002-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-005-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-006 新增/编辑商品中心主从表单页（P06：从表区含多单位/安全库存）
  - P0-007-002-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-007 商品基本信息列表页（P04）
  - P0-007-002-007-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-007-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-007-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-008 新增/编辑商品基本信息表单页（P07）
  - P0-007-002-008-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-008-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-008-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-009 商品多单位列表页（P04）
  - P0-007-002-009-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-009-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-009-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-010 商品控制策略列表页（P04）
  - P0-007-002-010-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-010-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-010-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-011 商品安全库存列表页（P04）
  - P0-007-002-011-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-011-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-011-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-012 商品附件列表页（P04）
  - P0-007-002-012-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-012-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-012-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-013 商品其他信息列表页（P04）
  - P0-007-002-013-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-013-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-013-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-014 商品标准价列表页（P04）
  - P0-007-002-014-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-014-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-014-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-015 商品购价核定列表页（P04）
  - P0-007-002-015-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-015-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-015-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-016 商品销价核定列表页（P04）
  - P0-007-002-016-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-016-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-016-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-017 商品竞品列表页（P04）
  - P0-007-002-017-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-017-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-017-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-018 商品BOM列表页（P04）
  - P0-007-002-018-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-018-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-018-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-019 新增/编辑商品BOM主从表单页（P06：含BOM明细从表+物料选择+用量录入）
  - P0-007-002-019-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-019-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-019-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-020 商品BOM成本报表页（P10报表页：BOM成本条件筛选+成本明细报表+穿透钻取）
  - P0-007-002-020-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-020-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-020-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-021 商品工序列表页（P04）
  - P0-007-002-021-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-021-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-021-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-022 商品工序参数列表页（P04）
  - P0-007-002-022-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-022-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-022-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-023 商品工序加工价格列表页（P04）
  - P0-007-002-023-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-023-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-023-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-024 商品价格列表页（P04）
  - P0-007-002-024-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-024-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-024-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-025 新增/编辑商品价格表单页（P07）
  - P0-007-002-025-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-025-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-025-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-026 商品属性与属性值列表页（P04）
  - P0-007-002-026-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-026-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-026-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-027 新增/编辑商品属性与属性值主从表单页（P06：含属性值从表编辑）
  - P0-007-002-027-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-027-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-027-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-028 商品规格列表页（P04）
  - P0-007-002-028-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-028-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-028-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-029 新增/编辑商品规格表单页（P07）
  - P0-007-002-029-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-029-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-029-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-030 商品条码列表页（P04）
  - P0-007-002-030-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-030-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-030-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-031 新增/编辑商品条码表单页（P07）
  - P0-007-002-031-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-031-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-031-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-032 商品图片管理页（P04 + 图片上传/预览/排序/设为主图）
  - P0-007-002-032-001 实现核心逻辑（图片上传+预览+排序拖拽+主图设置+异常处理）
    - P0-007-002-032-001-001 编写核心代码（图片上传/预览+拖拽排序+主图切换+日志记录）
    - P0-007-002-032-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-033 商品关联管理页（P04 + 双向关联选择+关联类型配置）
  - P0-007-002-033-001 实现核心逻辑（商品选择器+关联类型+双向同步+异常处理）
    - P0-007-002-033-001-001 编写核心代码（关联商品选择+关联类型+双向同步显示+日志记录）
    - P0-007-002-033-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-034 商品标签关联页（P04 + 批量标签绑定/解绑）
  - P0-007-002-034-001 实现核心逻辑（标签选择器+批量绑定+解绑+异常处理）
    - P0-007-002-034-001-001 编写核心代码（标签多选+批量绑定/解绑+刷新+日志记录）
    - P0-007-002-034-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-035 序列号模板列表页（P04）
  - P0-007-002-035-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-035-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-035-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-036 新增/编辑序列号模板表单页（P07 + 序列号预览）
  - P0-007-002-036-001 实现核心逻辑（模板配置+序列号预览+异常处理）
    - P0-007-002-036-001-001 编写核心代码（模板表单+序列号预览实时刷新+日志记录）
    - P0-007-002-036-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-037 商品其他信息列表页（P04）
  - P0-007-002-037-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-037-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-037-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-038 新增/编辑商品其他信息表单页（P07：含HS编码/条码/产地/重量/体积/认证/保质期字段）
  - P0-007-002-038-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-038-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-038-001-002 验证功能（核心用例通过+边界处理+异常降级）

> **V11.0补全**：以下13个P07独立录入页对应设计文档11.5节各子功能点的"自有录入页"定义，为各商品子功能提供独立菜单入口的新增/编辑能力（亦可通过商品中心P06从表标签页操作）。

- P0-007-002-039 新增/编辑标准工序表单页（P07：工序编码+工序名称+工时+备注）
  - P0-007-002-039-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-039-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-039-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-040 新增/编辑商品多单位表单页（P07：主单位+辅助单位+换算率）
  - P0-007-002-040-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-040-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-040-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-041 新增/编辑商品控制策略表单页（P07：采购/销售/库存/生产策略配置）
  - P0-007-002-041-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-041-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-041-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-042 新增/编辑商品安全库存表单页（P07：最低库存+最高库存+预警值+补货点）
  - P0-007-002-042-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-042-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-042-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-043 新增/编辑商品附件表单页（P07：附件上传+附件描述+关联商品）
  - P0-007-002-043-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-043-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-043-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-044 新增/编辑商品其他信息表单页（P07：产地+保质期+存储条件+备注）
  - P0-007-002-044-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-044-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-044-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-045 新增/编辑商品标准价表单页（P07：标准价+币种+生效期+含税/不含税）
  - P0-007-002-045-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-045-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-045-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-046 新增/编辑商品购价核定表单页（P07：采购价+供应商+币种+生效期+MOQ）
  - P0-007-002-046-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-046-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-046-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-047 新增/编辑商品销价核定表单页（P07：销售价+客户分类+币种+折扣区间）
  - P0-007-002-047-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-047-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-047-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-048 新增/编辑商品竞品表单页（P07：竞品名称+竞品价格+竞品厂商+对比分析）
  - P0-007-002-048-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-048-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-048-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-049 新增/编辑商品工序表单页（P07：工序选择+排序号+工时+是否关键工序）
  - P0-007-002-049-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-049-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-049-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-050 新增/编辑商品工序参数表单页（P07：参数名称+参数类型+标准值+上下限）
  - P0-007-002-050-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-050-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-050-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-007-002-051 新增/编辑商品工序加工价格表单页（P07：工序+加工单价+币种+生效期）
  - P0-007-002-051-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-007-002-051-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-007-002-051-001-002 验证功能（核心用例通过+边界处理+异常降级）

---

## P0-008 CRM客户管理模块开发

> **依赖**：P0-006（组织架构提供部门/员工数据）、P0-007（商品管理提供商品数据）  
> **概述**：完成CRM客户全功能开发，为销售模块提供客户基础数据支撑

### P0-008-001 CRM后端开发
- P0-008-001-001 客户分类CRUD接口（crm_customer_class：Entity/Mapper/Service/Controller + 树形结构）
  - P0-008-001-001-001 Service接口定义+实现类编写
    - P0-008-001-001-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-008-001-001-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-008-001-001-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-008-001-002 CRM标签定义CRUD接口（crm_tag_definition：Entity/Mapper/Service/Controller）
  - P0-008-001-002-001 Service接口定义+实现类编写
    - P0-008-001-002-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-008-001-002-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-008-001-002-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-008-001-003 客户主表CRUD接口（crm_customer：Entity/Mapper/Service/Controller + 客户名称唯一性参数控制）
  - P0-008-001-003-001 Service接口定义+实现类编写
    - P0-008-001-003-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-008-001-003-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-008-001-003-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-008-001-004 客户联系人CRUD接口（crm_customer_contact + crm_contact_comm：主从CRUD + 联系方式唯一性参数控制）
  - P0-008-001-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-001-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-001-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-001-005 客户地址CRUD接口（crm_customer_address：Entity/Mapper/Service/Controller）
  - P0-008-001-005-001 Service接口定义+实现类编写
    - P0-008-001-005-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-008-001-005-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-008-001-005-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-008-001-006 客户标签关联CRUD接口（crm_customer_tag_rel：批量绑定/解绑）
  - P0-008-001-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-001-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-001-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-001-007 客户附件CRUD接口（crm_customer_attachment：上传/下载/删除/列表查询）
  - P0-008-001-007-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-001-007-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-001-007-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-001-008 客户评价CRUD接口（crm_customer_evaluation：Entity/Mapper/Service/Controller）
  - P0-008-001-008-001 Service接口定义+实现类编写
    - P0-008-001-008-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-008-001-008-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-008-001-008-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-008-001-009 客户财务配置CRUD接口（crm_customer_finance：Entity/Mapper/Service/Controller）
  - P0-008-001-009-001 Service接口定义+实现类编写
    - P0-008-001-009-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-008-001-009-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-008-001-009-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-008-001-010 客户机会CRUD接口（crm_opportunity：Entity/Mapper/Service/Controller + 阶段流转）
  - P0-008-001-010-001 Service接口定义+实现类编写
    - P0-008-001-010-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-008-001-010-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-008-001-010-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-008-001-011 客户项目CRUD接口（crm_project：Entity/Mapper/Service/Controller）
  - P0-008-001-011-001 Service接口定义+实现类编写
    - P0-008-001-011-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-008-001-011-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-008-001-011-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-008-001-012 客户画像聚合数据接口（客户基本信息+交易统计+跟进记录+标签画像）
  - P0-008-001-012-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-001-012-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-001-012-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-001-013 CRM工作台聚合数据接口（客户总数/本月新增/跟进中/成交转化率统计）
  - P0-008-001-013-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-001-013-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-001-013-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P0-008-002 CRM前端页面
- P0-008-002-001 CRM工作台（P02：统计卡片+快捷入口+待跟进客户+最近联系）
  - P0-008-002-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-002 客户中心主从列表页（P03：关联信息区含联系人/地址/标签/附件/评价/财务/机会/项目等9个标签页）
  - P0-008-002-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-003 新增/编辑客户中心主从表单页（P06：从表区含联系人/财务配置）
  - P0-008-002-003-001 实现核心逻辑（主表单+从表标签页+选择器对接+表单校验+异常处理）
    - P0-008-002-003-001-001 编写核心代码（P06主从表单页+客户选择器+联系人从表+财务配置从表+表单联动+日志记录）
    - P0-008-002-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-004 客户分类树形列表页（P05）
  - P0-008-002-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-005 新增/编辑客户分类表单页（P07）
  - P0-008-002-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-005-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-006 标签定义列表页（P04）
  - P0-008-002-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-007 新增/编辑标签定义表单页（P07）
  - P0-008-002-007-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-007-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-007-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-008 客户基本信息列表页（P04）
  - P0-008-002-008-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-008-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-008-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-009 新增/编辑客户基本信息表单页（P07）
  - P0-008-002-009-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-009-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-009-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-010 客户联系人列表页（P04）
  - P0-008-002-010-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-010-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-010-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-011 新增/编辑客户联系人表单页（P06主从表单：联系人基本信息+联系方式从表crm_contact_comm）
  - P0-008-002-011-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-011-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-011-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-012 客户地址列表页（P04）
  - P0-008-002-012-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-012-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-012-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-013 新增/编辑客户地址表单页（P07）
  - P0-008-002-013-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-013-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-013-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-014 客户标签列表页（P04）
  - P0-008-002-014-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-014-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-014-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-015 新增/编辑客户标签表单页（P07）
  - P0-008-002-015-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-015-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-015-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-016 客户画像页（P12：客户概览+交易统计+标签画像+跟进历史）
  - P0-008-002-016-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-016-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-016-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-017 客户附件列表页（P04）
  - P0-008-002-017-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-017-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-017-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-017a 新增/编辑客户附件表单页（P07：附件上传+附件描述+关联客户）
  - P0-008-002-017a-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-017a-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-017a-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-018 客户评价列表页（P04）
  - P0-008-002-018-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-018-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-018-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-019 新增/编辑客户评价表单页（P07）
  - P0-008-002-019-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-019-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-019-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-020 客户财务配置列表页（P04）
  - P0-008-002-020-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-020-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-020-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-021 新增/编辑客户财务配置表单页（P07）
  - P0-008-002-021-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-021-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-021-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-022 客户机会列表页（P04）
  - P0-008-002-022-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-022-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-022-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-023 新增/编辑客户机会表单页（P07）
  - P0-008-002-023-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-023-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-023-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-024 客户项目列表页（P04）
  - P0-008-002-024-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-024-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-024-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-008-002-025 新增/编辑客户项目表单页（P07）
  - P0-008-002-025-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-008-002-025-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-008-002-025-001-002 验证功能（核心用例通过+边界处理+异常降级）

---

## P0-009 SRM供应商管理模块开发

> **依赖**：P0-006（组织架构）、P0-007（商品管理）  
> **概述**：完成SRM供应商全功能开发，为采购模块提供供应商基础数据支撑

### P0-009-001 SRM后端开发
- P0-009-001-001 供应商分类CRUD接口（srm_supplier_class：Entity/Mapper/Service/Controller + 树形结构）
  - P0-009-001-001-001 Service接口定义+实现类编写
    - P0-009-001-001-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-009-001-001-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-009-001-001-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-009-001-002 SRM标签定义CRUD接口（srm_tag_definition：Entity/Mapper/Service/Controller）
  - P0-009-001-002-001 Service接口定义+实现类编写
    - P0-009-001-002-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-009-001-002-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-009-001-002-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-009-001-003 供应商主表CRUD接口（srm_supplier：Entity/Mapper/Service/Controller + 供应商名称唯一性参数控制）
  - P0-009-001-003-001 Service接口定义+实现类编写
    - P0-009-001-003-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-009-001-003-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-009-001-003-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-009-001-004 供应商联系人CRUD接口（srm_supplier_contact + srm_supplier_comm：主从CRUD + 联系方式唯一性参数控制）
  - P0-009-001-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-001-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-001-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-001-005 供应商地址CRUD接口（srm_supplier_address：Entity/Mapper/Service/Controller）
  - P0-009-001-005-001 Service接口定义+实现类编写
    - P0-009-001-005-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-009-001-005-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-009-001-005-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-009-001-006 供应商标签关联CRUD接口（srm_supplier_tag_rel：批量绑定/解绑）
  - P0-009-001-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-001-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-001-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-001-007 供应商附件CRUD接口（srm_supplier_attachment：上传/下载/删除/列表查询）
  - P0-009-001-007-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-001-007-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-001-007-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-001-008 供应商评价CRUD接口（srm_supplier_evaluation：Entity/Mapper/Service/Controller）
  - P0-009-001-008-001 Service接口定义+实现类编写
    - P0-009-001-008-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-009-001-008-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-009-001-008-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-009-001-009 供应商财务配置CRUD接口（srm_supplier_finance：Entity/Mapper/Service/Controller）
  - P0-009-001-009-001 Service接口定义+实现类编写
    - P0-009-001-009-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-009-001-009-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-009-001-009-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-009-001-010 供应商画像聚合数据接口（供应商基本信息+采购统计+评价+标签画像）
  - P0-009-001-010-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-001-010-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-001-010-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-001-011 SRM工作台聚合数据接口（供应商总数/本月新增/待评审/合格率统计）
  - P0-009-001-011-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-001-011-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-001-011-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-001-012 供应商品类CRUD接口（srm_supplier_category：Entity/Mapper/Service/Controller + 供应商关联校验）
  - P0-009-001-012-001 Service接口定义+实现类编写
    - P0-009-001-012-001-001 编写Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-009-001-012-001-002 编写ServiceImpl实现类（品类关联供应商校验+Mapper调用+事务管理+异常处理）
    - P0-009-001-012-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-009-001-013 供应商准入CRUD接口（srm_supplier_qualification：Entity/Mapper/Service/Controller + 准入状态流转+附件关联）
  - P0-009-001-013-001 Service接口定义+实现类编写
    - P0-009-001-013-001-001 编写Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-009-001-013-001-002 编写ServiceImpl实现类（准入状态流转+附件关联+Mapper调用+事务管理+异常处理）
    - P0-009-001-013-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）

### P0-009-002 SRM前端页面
- P0-009-002-001 SRM工作台（P02）
  - P0-009-002-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-002 供应商中心主从列表页（P03：关联信息区含7个标签页）
  - P0-009-002-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-003 新增/编辑供应商中心主从表单页（P06）
  - P0-009-002-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-004 供应商分类树形列表页（P05）
  - P0-009-002-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-005 新增/编辑供应商分类表单页（P07）
  - P0-009-002-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-005-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-006 标签定义列表页（P04）
  - P0-009-002-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-007 供应商基本信息列表页（P04）
  - P0-009-002-007-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-007-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-007-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-008 供应商联系人列表页（P04）
  - P0-009-002-008-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-008-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-008-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-009 供应商地址列表页（P04）
  - P0-009-002-009-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-009-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-009-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-010 供应商标签列表页（P04）
  - P0-009-002-010-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-010-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-010-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-011 供应商画像页（P12）
  - P0-009-002-011-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-011-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-011-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-012 供应商附件列表页（P04）
  - P0-009-002-012-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-012-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-012-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-013 供应商评价列表页（P04）
  - P0-009-002-013-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-013-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-013-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-014 供应商财务配置列表页（P04）
  - P0-009-002-014-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-014-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-014-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-015 供应商品类列表页（P04）
  - P0-009-002-015-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-015-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-015-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-016 新增/编辑供应商品类表单页（P07）
  - P0-009-002-016-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-016-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-016-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-017 供应商准入列表页（P04）
  - P0-009-002-017-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-017-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-017-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-018 新增/编辑供应商准入表单页（P07 + 附件上传）
  - P0-009-002-018-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-018-001-001 编写核心代码（准入表单+附件上传+状态流转+日志记录）
    - P0-009-002-018-001-002 验证功能（核心用例通过+边界处理+异常降级）

> **V11.0补全**：以下8个P07独立录入页对应设计文档11.4节各子功能点的"自有录入页"定义，为SRM各子功能提供独立菜单入口的新增/编辑能力（与CRM模块P0-008对齐）。

- P0-009-002-019 新增/编辑标签定义表单页（P07：标签名称+标签分组+标签颜色+排序号）
  - P0-009-002-019-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-019-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-019-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-020 新增/编辑供应商基本信息表单页（P07：供应商编码+名称+统一社会信用代码+法人+注册资本+企业类型）
  - P0-009-002-020-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-020-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-020-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-021 新增/编辑供应商联系人表单页（P07：联系人姓名+手机+邮箱+职位+角色）
  - P0-009-002-021-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-021-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-021-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-022 新增/编辑供应商地址表单页（P07：省市区+详细地址+邮编+联系人+电话）
  - P0-009-002-022-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-022-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-022-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-023 新增/编辑供应商标签表单页（P07：标签选择+关联供应商+备注）
  - P0-009-002-023-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-023-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-023-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-024 新增/编辑供应商附件表单页（P07：附件上传+附件描述+关联供应商）
  - P0-009-002-024-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-024-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-024-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-025 新增/编辑供应商评价表单页（P07：评价维度+评分+评价内容+评价日期）
  - P0-009-002-025-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-025-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-025-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-009-002-026 新增/编辑供应商财务配置表单页（P07：结算方式+付款条件+税率+发票类型+银行账户）
  - P0-009-002-026-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-009-002-026-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-009-002-026-001-002 验证功能（核心用例通过+边界处理+异常降级）

---

## P0-010 仓库与库位管理开发

> **依赖**：P0-006、P0-007  
> **概述**：完成仓库定义、库位管理，为库存及所有出入库业务提供基础数据支撑

### P0-010-001 仓库库位后端开发
- P0-010-001-000 仓库管理工作台聚合数据接口（仓库总数+库位总数+库存预警数+本月出入库汇总+仓库利用率）
  - P0-010-001-000-001 Service接口定义+实现类编写（聚合统计SQL+缓存@Cacheable）
    - P0-010-001-000-001-001 编写工作台聚合SQL（仓库数+库位数+预警数+出入库汇总+利用率）
    - P0-010-001-000-001-002 编写ServiceImpl实现类（聚合查询+缓存+空数据处理）
    - P0-010-001-000-001-003 验证Service（数据正确+缓存命中+性能合理）
- P0-010-001-001 仓库定义CRUD接口（inv_warehouse：Entity/Mapper/Service/Controller + 仓库编码唯一性）
  - P0-010-001-001-001 Service接口定义+实现类编写
    - P0-010-001-001-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-010-001-001-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-010-001-001-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-010-001-002 库位管理CRUD接口（inv_location：Entity/Mapper/Service/Controller + 所属仓库关联+库位编码唯一性）
  - P0-010-001-002-001 Service接口定义+实现类编写
    - P0-010-001-002-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-010-001-002-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-010-001-002-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）

### P0-010-002 仓库库位前端页面
- P0-010-002-000 仓库管理工作台页（P02：仓库总数/库位总数/库存预警数/出入库汇总KPI卡片+仓库利用率图表+快捷操作）
  - P0-010-002-000-001 工作台KPI卡片区（仓库数/库位数/预警数/本月出入库金额+点击下钻）
    - P0-010-002-000-001-001 KPI卡片组件开发（数值展示+同比环比+点击下钻路由）
  - P0-010-002-000-002 工作台图表区（仓库利用率+出入库趋势+预警分布）
    - P0-010-002-000-002-001 ECharts图表组件开发（柱状图+折线图+饼图+响应式）
  - P0-010-002-000-003 工作台联调验证
    - P0-010-002-000-003-001 工作台全流程联调（数据加载+下钻+刷新+异常处理）
- P0-010-002-001 仓库定义列表页（P04）
  - P0-010-002-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-010-002-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-010-002-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-010-002-002 新增/编辑仓库定义表单页（P07）
  - P0-010-002-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-010-002-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-010-002-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-010-002-003 库位管理列表页（P04）
  - P0-010-002-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-010-002-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-010-002-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-010-002-004 新增/编辑库位管理表单页（P07）
  - P0-010-002-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-010-002-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-010-002-004-001-002 验证功能（核心用例通过+边界处理+异常降级）

---

## P0-011 财务基础设置模块开发

> **依赖**：P0-006、P0-007  
> **概述**：完成币种汇率、银行账户、会计科目、凭证字等财务基础数据，为后续所有财务模块提供基础支撑

### P0-011-001 财务基础设置后端开发
- P0-011-001-001 币种汇率CRUD接口（fin_currency_rate：Entity/Mapper/Service/Controller + 币种编码唯一性+汇率日期有效性）
  - P0-011-001-001-001 Service接口定义+实现类编写
    - P0-011-001-001-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-011-001-001-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-011-001-001-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-011-001-002 银行账户CRUD接口（fin_bank_account：Entity/Mapper/Service/Controller + 账号唯一性）
  - P0-011-001-002-001 Service接口定义+实现类编写
    - P0-011-001-002-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-011-001-002-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-011-001-002-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-011-001-003 会计科目CRUD接口（fin_account：Entity/Mapper/Service/Controller + 树形结构+科目编码唯一性+科目级次校验）
  - P0-011-001-003-001 Service接口定义+实现类编写
    - P0-011-001-003-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-011-001-003-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-011-001-003-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-011-001-004 凭证字CRUD接口（fin_voucher_word：Entity/Mapper/Service/Controller）
  - P0-011-001-004-001 Service接口定义+实现类编写
    - P0-011-001-004-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-011-001-004-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-011-001-004-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-011-001-005 会计期间CRUD接口（fin_accounting_period：Entity/Mapper/Service/Controller + 期间不可重叠校验）
  - P0-011-001-005-001 Service接口定义+实现类编写
    - P0-011-001-005-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-011-001-005-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-011-001-005-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-011-001-006 财务基础设置工作台聚合数据接口（配置项状态概览/各子模块数据统计）
  - P0-011-001-006-001 实现核心逻辑（聚合查询SQL+多表统计+数据处理+异常处理）
    - P0-011-001-006-001-001 编写核心代码（多表聚合SQL+统计计算+数据组装+日志记录）
    - P0-011-001-006-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P0-011-002 财务基础设置前端页面
- P0-011-002-001 财务基础设置工作台（P02：统计卡片+各子模块快捷入口+配置状态概览）
  - P0-011-002-001-001 实现核心逻辑（P02工作台布局+统计卡片+快捷入口+异常处理）
    - P0-011-002-001-001-001 编写核心代码（P02工作台+统计卡片渲染+快捷入口跳转+日志记录）
    - P0-011-002-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-011-002-002 币种汇率列表页（P04）
  - P0-011-002-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-011-002-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-011-002-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-011-002-003 新增/编辑币种汇率表单页（P07）
  - P0-011-002-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-011-002-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-011-002-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-011-002-004 银行账户列表页（P04）
  - P0-011-002-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-011-002-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-011-002-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-011-002-005 新增/编辑银行账户表单页（P07）
  - P0-011-002-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-011-002-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-011-002-005-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-011-002-006 会计科目管理树形列表页（P05：左侧科目树+右侧科目列表）
  - P0-011-002-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-011-002-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-011-002-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-011-002-007 新增/编辑会计科目表单页（P07）
  - P0-011-002-007-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-011-002-007-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-011-002-007-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-011-002-008 凭证字管理列表页（P04）
  - P0-011-002-008-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-011-002-008-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-011-002-008-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-011-002-009 新增/编辑凭证字表单页（P07）
> **[已移除 — 与P2-013-002-010重复，保留P2-013版本]**
<!--     - P0-011-002-010 缓存管理（Redis缓存查看+按Key前缀搜索+缓存清除+统计+预热）
      - P0-011-002-010-001 后端开发（CacheManager：SCAN搜索+Value查看+TTL+删除+批量删除+统计）
        - P0-011-002-010-001-001 编写CacheManager Service（RedisTemplate封装+SCAN+Value+TTL+删除+操作日志）
        - P0-011-002-010-001-002 编写Controller+权限控制（@RequiresPermissions("system:cache:manage")）
        - P0-011-002-010-001-003 验证缓存管理（搜索+Value+清除+统计+权限控制）
      - P0-011-002-010-002 前端开发（搜索框+Key列表+Value展示+清除按钮+统计面板）
        - P0-011-002-010-002-001 实现缓存管理页面（Key搜索+列表+Value JSON高亮+清除+统计卡片） -->
> **[已移除 — 与P2-013-002-011重复，保留P2-013版本]**
<!--     - P0-011-002-011 系统公告管理（公告CRUD+定时发布+置顶+已读未读+富文本+范围控制）
      - P0-011-002-011-001 后端开发（sys_notice表：CRUD+定时发布+置顶+已读记录+类型+范围）
        - P0-011-002-011-001-001 编写DDL+Entity/Mapper（公告ID+标题+内容富文本+类型+发布时间+置顶+状态）
        - P0-011-002-011-001-002 编写Service+Controller（CRUD+定时发布Task+置顶排序+已读标记+未读查询）
        - P0-011-002-011-001-003 验证公告管理（CRUD+定时发布+置顶+已读+范围控制）
      - P0-011-002-011-002 前端开发（公告管理页+详情弹窗+富文本编辑器+未读Badge）
        - P0-011-002-011-002-001 实现公告管理页（P04列表+P02编辑+富文本+定时发布+置顶开关）
        - P0-011-002-011-002-002 实现公告通知弹窗（登录后弹窗+未读公告+已读标记+详情跳转） -->
  - P0-011-002-009-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-011-002-009-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-011-002-009-001-002 验证功能（核心用例通过+边界处理+异常降级）

---

## P0-012 HRM人力资源管理模块开发

> **依赖**：P0-006（组织架构提供部门/岗位数据）  
> **概述**：完成员工档案（hrm_employee）基础数据管理，为全系统提供人员基础数据支撑。招聘管理、考勤管理、薪资管理属HR运营功能，不构成其他业务模块的前置依赖，建议后续迭代中拆分至独立P2 HR运营模块。
> **V8说明**：当前招聘/考勤/薪资仍在P0-012中保留建表和CRUD任务，但开发优先级上可在P1核心业务完成后再进行，不影响其他模块的依赖关系。

### P0-012-001 HRM后端开发
- P0-012-001-001 员工档案CRUD接口（hrm_employee：Entity/Mapper/Service/Controller + 工号唯一性+部门岗位关联）
  - P0-012-001-001-001 Service接口定义+实现类编写
    - P0-012-001-001-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-012-001-001-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-012-001-001-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-012-001-002 招聘管理CRUD接口（hrm_recruitment：Entity/Mapper/Service/Controller）
  - P0-012-001-002-001 Service接口定义+实现类编写
    - P0-012-001-002-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-012-001-002-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-012-001-002-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-012-001-003 考勤管理CRUD接口（hrm_attendance：Entity/Mapper/Service/Controller）
  - P0-012-001-003-001 Service接口定义+实现类编写
    - P0-012-001-003-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-012-001-003-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-012-001-003-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-012-001-004 薪资管理CRUD接口（hrm_salary：Entity/Mapper/Service/Controller）
  - P0-012-001-004-001 Service接口定义+实现类编写
    - P0-012-001-004-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P0-012-001-004-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P0-012-001-004-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P0-012-001-005 HRM工作台聚合数据接口
  - P0-012-001-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-012-001-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-012-001-005-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P0-012-002 HRM前端页面
- P0-012-002-001 HRM工作台（P02）
  - P0-012-002-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-012-002-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-012-002-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-012-002-002 员工中心主从列表页（P03：关联信息区含员工档案标签页）
  - P0-012-002-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-012-002-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-012-002-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-012-002-003 新增/编辑员工中心主从表单页（P06）
  - P0-012-002-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-012-002-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-012-002-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-012-002-004 员工档案列表页（P04）
  - P0-012-002-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-012-002-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-012-002-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-012-002-005 招聘管理列表页（P04）
  - P0-012-002-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-012-002-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-012-002-005-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-012-002-006 考勤管理列表页（P04）
  - P0-012-002-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-012-002-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-012-002-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-012-002-007 薪资管理主从列表页（P03：薪资列表+薪资明细标签页+查询区+操作区）
  - P0-012-002-007-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-012-002-007-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-012-002-007-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-012-002-008 新增/编辑薪资管理主从表单页（P06主从表单：薪资主表+薪资明细hrm_salary_detail从表）
  - P0-012-002-008-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-012-002-008-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-012-002-008-001-002 验证功能（核心用例通过+边界处理+异常降级）

> **V11.0补全**：以下3个P07独立录入页对应设计文档11.7节员工档案/招聘管理/考勤管理的"自有录入页"定义（薪资管理P06已在上方P0-012-002-008补全）。

- P0-012-002-009 新增/编辑员工档案表单页（P07：员工基本信息+学历+工作经历+合同信息+紧急联系人）
  - P0-012-002-009-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-012-002-009-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-012-002-009-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-012-002-010 新增/编辑招聘管理表单页（P07：招聘岗位+需求人数+招聘渠道+面试安排+录用状态）
  - P0-012-002-010-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-012-002-010-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-012-002-010-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P0-012-002-011 新增/编辑考勤管理表单页（P07：员工+考勤日期+打卡时间+工时+考勤类型+加班时长）
  - P0-012-002-011-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P0-012-002-011-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P0-012-002-011-001-002 验证功能（核心用例通过+边界处理+异常降级）

---

## P0-013 部署与DevOps基础

> **依赖**：P0-001、P0-002  
> **概述**：搭建Docker容器化环境、Nginx反向代理、CI/CD流水线，确保开发/测试/生产环境一致性与自动化部署能力

### P0-013-001 Docker容器化
- P0-013-001-001 Dockerfile编写（后端Spring Boot多阶段构建：maven编译+openjdk21运行镜像+分层缓存优化）
  - P0-013-001-001-001 编写后端Dockerfile（maven:3.9-eclipse-temurin-21构建+openjdk21-jre-slim运行+jar分层+非root用户）
  - P0-013-001-001-002 编写前端Dockerfile（node:20-alpine构建+nginx:alpine运行+多阶段+gzip压缩+CSP头）
- P0-013-001-002 docker-compose.yml编排（PostgreSQL+Redis+MinIO+Elasticsearch+后端+前端+Nginx统一服务编排）
  - P0-013-001-002-001 编写docker-compose服务编排（各服务+健康检查+网络+卷挂载+环境变量+.env文件）
  - P0-013-001-002-002 验证docker-compose编排（docker compose up+所有服务启动+健康检查通过+服务间通信正常）
- P0-013-001-003 .env环境变量文件（多环境变量模板：.env.dev/.env.staging/.env.prod+数据库密码/密钥/API Key外部化）
- P0-013-001-004 .dockerignore文件编写（排除node_modules/target/.git/logs/本地配置文件）

### P0-013-002 Nginx反向代理与静态资源
- P0-013-002-001 nginx.conf主配置（upstream后端集群+前端SPA静态资源+API反向代理+WebSocket代理+gzip压缩+缓存策略）
- P0-013-002-002 多环境Nginx配置（dev/staging/prod独立配置+SSL证书路径+域名绑定+CORS头+CSP安全头）
- P0-013-002-003 健康检查端点配置（/actuator/health代理+后端健康检查+故障转移upstream配置）

### P0-013-003 CI/CD流水线
- P0-013-003-001 GitHub Actions / GitLab CI基础流水线（代码检出→编译构建→单元测试→Docker镜像构建→推送镜像仓库）
  - P0-013-003-001-001 编写CI配置文件（.github/workflows/ci.yml或.gitlab-ci.yml+触发条件+Job定义+缓存策略）
  - P0-013-003-001-002 验证CI流水线（push触发+构建成功+测试通过+镜像推送+流水线报告）
- P0-013-003-002 代码质量门禁（ESLint前端检查+Checkstyle/SpotBugs后端检查+SonarQube集成占位）
- P0-013-003-003 自动化部署脚本（deploy.sh：拉取镜像→备份数据库→滚动更新→健康检查→失败自动回滚）

---

## P0-014 测试基础模块

> **依赖**：P0-001、P0-002  
> **概述**：搭建后端与前端测试框架，建立测试规范与模板，确保测试覆盖率达到项目质量标准

### P0-014-001 后端测试框架搭建
- P0-014-001-001 JUnit 5 + Mockito测试依赖引入（pom.xml：junit-jupiter/mockito-core/mockito-junit-jupiter/assertj/h2-test-db）
- P0-014-001-002 单元测试基类开发（BaseServiceTest：@SpringBootTest+@Testcontainers PostgreSQL+@MockBean外部依赖+测试数据工厂）
  - P0-014-001-002-001 编写BaseServiceTest基类（@SpringBootTest+Testcontainers配置+通用Mock方法+断言工具+事务回滚）
  - P0-014-001-002-002 编写测试数据工厂（TestDataFactory：buildEntity/buildDTO/buildPage/buildUser通用方法+各模块扩展工厂）
- P0-014-001-003 Service层测试模板（@ExtendWith(MockitoExtension)+Mock依赖+given/when/then模式+覆盖率目标≥80%）
- P0-014-001-004 Controller层测试模板（@WebMvcTest+MockMvc+Mock Service+JSON断言+异常场景覆盖）
- P0-014-001-005 Mapper层测试模板（@MybatisPlusTest+@Sql初始化数据+H2内存数据库+SQL正确性验证）
- P0-014-001-006 集成测试模板（@SpringBootTest+Testcontainers+真实数据库+全链路接口测试+事务隔离）

### P0-014-002 前端测试框架搭建
- P0-014-002-001 Vitest测试依赖引入（vitest/@vue/test-utils/happy-dom+配置vitest.config.ts+覆盖率工具c8/istanbul）
- P0-014-002-002 组件单元测试模板（mount组件+props传参+事件触发+DOM断言+快照测试）
- P0-014-002-003 Composable测试模板（renderComposable+响应式状态验证+异步操作测试+边界条件）
- P0-014-002-004 API Mock策略（MSW或vitest mock配置+接口响应模拟+错误场景覆盖+Loading状态测试）
- P0-014-002-005 前端覆盖率配置（c8覆盖率报告+阈值≥70%+CI集成+覆盖率徽章）

### P0-014-003 测试规范与基础设施
- P0-014-003-001 测试命名规范（类名：<Target>Test+方法名：should_<行为>_when_<条件>+given/when/then三段式注释）
- P0-014-003-002 测试覆盖率要求文档（Service≥80%/Controller≥70%/Mapper≥60%/前端组件≥70%+覆盖率报告集成CI）
- P0-014-003-003 测试数据隔离策略（@Transactional自动回滚+Testcontainers独立数据库+测试数据前缀标记+并行测试安全）

---

# 第三章 P1级任务分解（重要）

## P1-001 通用单据审核引擎开发

> **依赖**：P0-004（权限体系）、P0-005（公共组件含审核组件）  
> **概述**：开发通用单据审核/反审/作废/撤销作废引擎，为所有业务单据提供统一的审核状态流转能力

### P1-001-001 审核引擎后端开发
- P1-001-001-001 通用审核接口（单据审核：状态从草稿→已审核+审核人/审核时间记录+审核意见存储）
  - P1-001-001-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-001-001-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-001-001-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-001-001-002 通用反审接口（单据反审：状态从已审核→草稿+反审人/反审时间+校验下游单据是否已审核）
  - P1-001-001-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-001-001-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-001-001-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-001-001-003 通用作废接口（单据作废：状态→已作废+作废原因+校验无下游单据关联）
  - P1-001-001-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-001-001-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-001-001-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-001-001-004 通用撤销作废接口（单据撤销作废：状态→草稿）
  - P1-001-001-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-001-001-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-001-001-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-001-001-005 审核引擎与审批流程集成（approval.enabled参数判断：=false直接审核通过，=true走审批流程）
  - P1-001-001-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-001-001-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-001-001-005-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-001-001-006 审核引擎与系统参数联动（sale.auto_confirm/purchase.auto_confirm等自动确认参数）
  - P1-001-001-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-001-001-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-001-001-006-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P1-001-002 审核引擎前端集成
- P1-001-002-001 审核按钮状态联动（根据单据状态动态显示审核/反审/作废/撤销作废按钮）
  - P1-001-002-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-001-002-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-001-002-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-001-002-002 审核弹窗集成（审核意见输入+确认审核/反审/作废弹窗）
  - P1-001-002-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-001-002-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-001-002-002-001-002 验证功能（核心用例通过+边界处理+异常降级）

---

## P1-002 审批流程模块开发

> **依赖**：P1-001（审核引擎）、P1-003（消息管理——审批催办/通知依赖消息推送能力）  
> **概述**：完成审批流程定义、审批实例与审批记录，为所有单据审核提供流程支撑。审批催办功能（站内信/邮件/短信）依赖P1-003消息管理模块的消息推送通道。

### P1-002-001 审批流程后端开发
- P1-002-001-001 审批定义CRUD接口（approval_definition：Entity/Mapper/Service/Controller + 审批节点JSON配置）
  - P1-002-001-001-001 Service接口定义+实现类编写
    - P1-002-001-001-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P1-002-001-001-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P1-002-001-001-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P1-002-001-002 审批实例管理接口（approval_instance：提交审批时创建实例+当前节点记录）
  - P1-002-001-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-002-001-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-002-001-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-002-001-003 审批记录管理接口（approval_record：审批操作记录+审批意见+附件）
  - P1-002-001-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-002-001-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-002-001-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-002-001-004 我的审批查询接口（待审/已审/待提交列表+按单据类型/时间筛选）
  - P1-002-001-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-002-001-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-002-001-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-002-001-005 审批统计接口（各单据类型审批效率+平均审批时长+驳回率）
  - P1-002-001-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-002-001-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-002-001-005-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-002-001-006 审批流程运行时引擎（提交→审核→驳回→转办→加签→撤回状态机）
  - P1-002-001-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-002-001-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-002-001-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-002-001-007 审批催办接口（approval_urge：催办记录+催办方式（站内信/邮件/短信）+催办频率配置+自动催办定时任务）
  - P1-002-001-007-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P1-002-001-007-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P1-002-001-007-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P1-002-001-008 审批委托接口（approval_delegate：委托规则配置+委托人指定+起止时间+自动转交逻辑）
  - P1-002-001-008-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P1-002-001-008-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P1-002-001-008-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P1-002-001-009 预置审批流程配置接口（按单据类型预置默认审批流程+支持多级+条件分支+自动加载）
  - P1-002-001-009-001 编写预置流程数据初始化SQL（各单据类型2-3级审批节点+审批人类型+条件分支配置）
    - P1-002-001-009-001-001 编写预置流程SQL INSERT（销售/采购/报销等单据+审批节点+审批人+条件分支）
    - P1-002-001-009-001-002 实现预置流程加载逻辑（首次使用自动加载+可自定义修改+覆盖预置）
    - P1-002-001-009-001-003 验证预置流程（加载正确+节点完整+条件分支生效+可修改覆盖）
- P1-002-001-010 审批日志查询接口（全量审批操作日志+按单据/审批人/时间筛选+操作类型+耗时统计）
  - P1-002-001-010-001 编写审批日志查询Service+Controller
    - P1-002-001-010-001-001 编写日志查询SQL（approval_record全量+按单据ID/审批人/时间/操作类型筛选+分页）
    - P1-002-001-010-001-002 编写统计SQL（按操作类型GROUP BY+平均审批耗时+驳回率+通过率）
    - P1-002-001-010-001-003 验证查询接口（数据完整+筛选正确+统计准确+性能合理）
- P1-002-001-011 审批工作台聚合数据接口（待审/已审/待提交数量+平均审批时长+超时预警数+审批效率趋势）

### P1-002-002 审批流程前端页面
- P1-002-002-001 审批定义配置页（P13/P15设计器：审批节点拖拽编排+条件分支配置）
  - P1-002-002-001-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P1-002-002-001-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P1-002-002-001-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P1-002-002-002 审批实例列表页（P04）
  - P1-002-002-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-002-002-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-002-002-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-002-002-003 审批记录列表页（P04）
  - P1-002-002-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-002-002-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-002-002-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-002-002-004 我的审批列表页（P04：待审/已审/待提交标签页切换）
  - P1-002-002-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-002-002-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-002-002-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-002-002-005 审批统计页（P10报表页）
  - P1-002-002-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-002-002-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-002-002-005-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-002-002-006 审批催办配置页（P13配置页：催办方式+催办频率+自动催办开关）
  - P1-002-002-006-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P1-002-002-006-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P1-002-002-006-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P1-002-002-007 审批委托配置页（P13配置页：委托规则+委托人选择+起止时间设置）
  - P1-002-002-007-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P1-002-002-007-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P1-002-002-007-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P1-002-002-008 预置审批流程配置页（P13配置页：按单据类型预置默认审批流程+多级+条件分支）
  - P1-002-002-008-001 预置流程配置前端开发（流程模板列表+节点编辑+条件分支可视化+保存/加载）
    - P1-002-002-008-001-001 实现预置流程配置页面（流程模板选择+节点拖拽+条件分支+保存）
    - P1-002-002-008-001-002 验证预置流程配置（加载正确+节点完整+条件分支生效+可修改覆盖）
- P1-002-002-009 审批日志查询页（P04列表页：全量审批操作日志+按单据/审批人/时间筛选+耗时统计）
  - P1-002-002-009-001 审批日志查询前端开发（搜索条件区+日志列表+统计图表）
    - P1-002-002-009-001-001 实现审批日志查询页（搜索区+日志表格+操作类型筛选+耗时统计+导出）
    - P1-002-002-009-001-002 验证查询页（数据完整+筛选正确+统计准确+联调通过）
- P1-002-002-010 审批工作台页（P02工作台：待审/已审/待提交数量+平均审批时长+超时预警+审批效率趋势图）
  - P1-002-002-010-001 工作台KPI卡片区（待审数/已审数/超时数/平均审批时长+点击下钻）
    - P1-002-002-010-001-001 KPI卡片组件开发（数值展示+同比环比+点击下钻路由）
  - P1-002-002-010-002 工作台图表区（审批效率趋势+各单据类型审批分布+超时预警列表）
    - P1-002-002-010-002-001 ECharts图表组件开发（折线图+饼图+列表+响应式+交互事件）
  - P1-002-002-010-003 工作台待办区+联调验证
    - P1-002-002-010-003-001 工作台全流程联调（数据加载+下钻+刷新+异常处理）

---

## P1-003 消息管理模块开发

> **依赖**：P0-001（WebSocket配置）、P0-004（用户管理）  
> **概述**：完成消息中心、消息模板、单据待办、消息推送，为系统提供实时通知能力

### P1-003-001 消息管理后端开发
- P1-003-001-000 消息管理工作台聚合数据接口（未读消息数+待办数+业务预警数+今日消息趋势+各类型消息分布）
  - P1-003-001-000-001 Service接口定义+实现类编写（聚合统计SQL+WebSocket实时推送+缓存）
    - P1-003-001-000-001-001 编写工作台聚合SQL（未读数+待办数+预警数+趋势+分布）
    - P1-003-001-000-001-002 编写ServiceImpl实现类（聚合查询+缓存+WebSocket推送+空数据处理）
    - P1-003-001-000-001-003 验证Service（数据正确+缓存命中+WebSocket推送+性能合理）
- P1-003-001-001 消息中心CRUD接口（msg_message：Entity/Mapper/Service/Controller + 已读/未读状态管理）
  - P1-003-001-001-001 Service接口定义+实现类编写
    - P1-003-001-001-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P1-003-001-001-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P1-003-001-001-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P1-003-001-002 消息模板CRUD接口（msg_template：Entity/Mapper/Service/Controller + 模板变量替换）
  - P1-003-001-002-001 Service接口定义+实现类编写
    - P1-003-001-002-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P1-003-001-002-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P1-003-001-002-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P1-003-001-003 消息类型CRUD接口（msg_type：Entity/Mapper/Service/Controller）
  - P1-003-001-003-001 Service接口定义+实现类编写
    - P1-003-001-003-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P1-003-001-003-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P1-003-001-003-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P1-003-001-004 单据待办管理接口（msg_todo：单据审核/审批自动创建待办+已办标记）
  - P1-003-001-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-003-001-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-003-001-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-003-001-005 消息推送配置CRUD接口（msg_push_config：推送方式/推送时间/推送对象配置）
  - P1-003-001-005-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P1-003-001-005-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P1-003-001-005-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P1-003-001-006 WebSocket消息推送服务（新消息实时推送+待办提醒推送+连接管理）
  - P1-003-001-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-003-001-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-003-001-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-003-001-007 业务预警规则CRUD接口（msg_alert_rule：Entity/Mapper/Service/Controller + 预警条件表达式+预警阈值+预警频率+触发动作配置）
  - P1-003-001-007-001 Service接口定义+实现类编写
    - P1-003-001-007-001-001 编写接口定义Service接口（CRUD方法签名+参数DTO+返回VO+@Transactional注解）
    - P1-003-001-007-001-002 编写ServiceImpl实现类（业务逻辑+Mapper调用+事务管理+异常处理）
    - P1-003-001-007-001-003 验证Service（单元测试+业务逻辑正确+事务回滚+异常处理）
- P1-003-001-008 业务预警触发引擎（定时扫描预警规则+条件匹配+自动生成预警消息+推送通知）
  - P1-003-001-008-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-003-001-008-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-003-001-008-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-003-001-009 单据沟通CRUD接口（msg_doc_comment：单据维度留言+@提及+回复+附件+消息通知联动）
  - P1-003-001-009-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-003-001-009-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-003-001-009-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-003-001-010 协作讨论CRUD接口（msg_discussion + msg_discussion_reply：讨论主题+回复+参与者+消息通知联动）
  - P1-003-001-010-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-003-001-010-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-003-001-010-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P1-003-002 消息管理前端页面
- P1-003-002-000 消息管理工作台页（P02工作台：未读消息数/待办数/业务预警数KPI卡片+消息趋势图+消息类型分布饼图+最新待办列表+WebSocket实时更新）
  - P1-003-002-000-001 工作台KPI卡片区（未读数/待办数/预警数+点击下钻+WebSocket实时更新）
    - P1-003-002-000-001-001 KPI卡片组件开发（数值展示+实时刷新+点击下钻路由）
  - P1-003-002-000-002 工作台图表区（消息趋势折线图+消息类型分布饼图+待办列表）
    - P1-003-002-000-002-001 ECharts图表组件开发（折线图+饼图+列表+响应式+WebSocket实时数据）
  - P1-003-002-000-003 工作台联调验证
    - P1-003-002-000-003-001 工作台全流程联调（数据加载+WebSocket推送+下钻+刷新+异常处理）
- P1-003-002-001 消息中心列表页（P04：全部/未读/已读标签页）
  - P1-003-002-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-003-002-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-003-002-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-003-002-002 消息模板列表页（P04）
  - P1-003-002-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-003-002-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-003-002-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-003-002-003 消息类型列表页（P04）
  - P1-003-002-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-003-002-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-003-002-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-003-002-004 单据待办列表页（P04）
  - P1-003-002-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-003-002-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-003-002-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-003-002-005 消息推送配置页（P13）
  - P1-003-002-005-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P1-003-002-005-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P1-003-002-005-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P1-003-002-006 全局消息通知弹窗（实时WebSocket推送+消息铃铛+未读数量角标）
  - P1-003-002-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-003-002-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-003-002-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-003-002-007 业务预警规则配置页（P13：预警条件编辑器+阈值设置+触发动作配置）
  - P1-003-002-007-001 编写配置项（配置文件+配置类+默认值+环境区分）
    - P1-003-002-007-001-001 编写配置文件/类（yml配置项+@Configuration+@Value/@ConfigurationProperties绑定）
    - P1-003-002-007-001-002 验证编写配置项配置（配置加载正确+环境区分+默认值+变更生效）
- P1-003-002-008 业务预警看板页（P08看板页：预警列表+预警状态标签+快捷处理）
  - P1-003-002-008-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-003-002-008-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-003-002-008-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-003-002-009 单据沟通组件（单据详情页内嵌留言区+@提及+回复+附件上传）
- P1-003-002-010 协作讨论列表页（P04：讨论主题+参与人+最新回复）
- P1-003-002-011 协作讨论详情页（P09查询页：讨论内容+回复列表+参与人管理）

---

## P1-004 通用单据流转引擎开发

> **依赖**：P0-005（公共组件含引入/下推组件）、P1-001（审核引擎）  
> **概述**：开发通用单据引入/下推/复制/追溯引擎，为所有业务单据链路流转提供统一能力

### P1-004-001 流转引擎后端开发
- P1-004-001-001 通用单据引入服务（源单查询+明细行勾选+自动填充目标单主表/明细数据）
  - P1-004-001-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-004-001-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-004-001-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-004-001-002 通用单据下推服务（源单状态更新+目标单自动创建+关联关系记录）
  - P1-004-001-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-004-001-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-004-001-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-004-001-003 通用单据复制服务（源单深拷贝+单据编号重新生成+状态重置为草稿）
  - P1-004-001-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-004-001-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-004-001-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-004-001-004 通用单据追溯服务（根据单据ID上下游递归查询+形成追溯链路树）
  - P1-004-001-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-004-001-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-004-001-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-004-001-005 单据关联关系表建表+管理服务（doc_relation记录上下游单据关联）
  - P1-004-001-005-001 编写单据关联关系表DDL（CREATE TABLE 单据关联关系+字段定义+数据类型+默认值+非空约束+注释）
    - P1-004-001-005-001-001 编写CREATE TABLE语句（字段名+数据类型+长度+默认值+非空约束+字段注释）
    - P1-004-001-005-001-002 编写单据关联关系表索引与约束（主键PK+唯一索引UK+业务索引IDX+CHECK约束+外键）
    - P1-004-001-005-001-003 验证编写单据关联关系表DDL（CREATE TABLE成功+字段类型正确+约束生效+注释完整+索引创建）

### P1-004-002 流转引擎前端集成
- P1-004-002-001 引入弹窗集成（源单列表+明细行勾选+确认引入）
  - P1-004-002-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-004-002-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-004-002-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-004-002-002 下推弹窗集成（目标单类型选择+自动携带数据预览+确认下推）
  - P1-004-002-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-004-002-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-004-002-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-004-002-003 复制弹窗集成（源单确认+目标单预览+确认复制）
  - P1-004-002-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-004-002-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-004-002-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-004-002-004 追溯弹窗集成（追溯链路树形展示+单据详情查看跳转）
  - P1-004-002-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-004-002-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-004-002-004-001-002 验证功能（核心用例通过+边界处理+异常降级）

---

## P1-005 销售管理模块开发

> **依赖**：P0-007（商品）、P0-008（客户）、P0-010（仓库）、P0-011（财务基础）、P1-001（审核引擎）、P1-004（流转引擎）  
> **概述**：完成销售全链路业务：报价→订单→发货通知→出库→退货→对账→统计

### P1-005-001 销售管理后端开发

#### P1-005-001-001 报价单CRUD接口
- P1-005-001-001-001 报价单Entity/DTO/VO定义（sale_quotation主表+sale_quotation_detail从表字段映射）
  - P1-005-001-001-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-005-001-001-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-005-001-001-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-005-001-001-002 报价单Mapper开发（主从联合查询+按客户/状态/时间条件查询）
  - P1-005-001-001-002-001 定义Mapper接口（extends BaseMapperX+自定义方法声明）
  - P1-005-001-001-002-002 编写XML映射（resultMap+主从联合查询SQL+动态条件）
  - P1-005-001-001-002-003 编写自定义查询方法（分页查询/条件查询/统计查询SQL）
- P1-005-001-001-003 报价单Service开发（主从CRUD+编码自动生成+金额计算引擎调用+审核状态流转+操作日志）
  - P1-005-001-001-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-005-001-001-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-005-001-001-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-005-001-001-004 报价单Controller开发（RESTful接口+参数校验+权限注解+Swagger文档）
  - P1-005-001-001-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-005-001-001-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-005-001-001-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-005-001-002 销售订单CRUD接口
- P1-005-001-002-001 销售订单Entity/DTO/VO定义（sale_order主表+sale_order_detail从表+sale_order_detail_serial序列号子表）
  - P1-005-001-002-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-005-001-002-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-005-001-002-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-005-001-002-002 销售订单Mapper开发（主从联合查询+序列号子表查询+按客户/状态/时间条件查询）
  - P1-005-001-002-002-001 定义Mapper接口（extends BaseMapperX+自定义方法声明）
  - P1-005-001-002-002-002 编写XML映射（resultMap+主从联合查询SQL+动态条件）
  - P1-005-001-002-002-003 编写自定义查询方法（分页查询/条件查询/统计查询SQL）
- P1-005-001-002-003 销售订单Service开发（主从CRUD+编码自动生成+金额计算+序列号子表处理+审核流转+操作日志）
  - P1-005-001-002-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-005-001-002-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-005-001-002-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-005-001-002-004 销售订单Controller开发（RESTful接口+参数校验+权限注解+Swagger文档）
  - P1-005-001-002-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-005-001-002-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-005-001-002-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-005-001-003 销售订单变更记录接口
- P1-005-001-003-001 销售订单变更Entity/Mapper开发（sale_order_change变更前后内容对比字段）
  - P1-005-001-003-001-001 定义Mapper接口（extends BaseMapperX+@Mapper+自定义方法声明）
  - P1-005-001-003-001-002 编写XML映射文件（resultMap+自定义SQL片段+动态条件）
- P1-005-001-003-002 销售订单变更Service开发（变更时自动对比前后差异+变更原因必填+变更记录查询）
  - P1-005-001-003-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-005-001-003-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-005-001-003-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-005-001-003-003 销售订单变更Controller开发（变更记录查询接口+变更详情对比接口）
  - P1-005-001-003-003-001 定义Controller类+@RequestMapping路径
  - P1-005-001-003-003-002 实现查询接口方法（参数接收+分页+Service调用+响应包装）
  - P1-005-001-003-003-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-005-001-004 未发货通知清单聚合查询接口
- P1-005-001-004-001 未发货通知SQL编写（sale_order_detail LEFT JOIN sale_delivery_notice_detail按未发货数量聚合）
  - P1-005-001-004-001-001 编写未发货通知SQL（sale_order_detail LEFT JOIN sale_delivery_notice_detail按未发货数量聚合+分页支持）
  - P1-005-001-004-001-002 验证SQL执行（数据正确+性能合理+索引命中+空结果处理）
  - P1-005-001-004-001-003 验证SQL执行（数据正确+性能合理+索引命中+空结果兜底）
- P1-005-001-004-002 未发货通知Service聚合逻辑（按订单/客户/商品维度汇总未发货数量）
  - P1-005-001-004-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-005-001-004-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-005-001-004-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-005-001-004-003 未发货通知Controller接口（条件查询+分页+导出）
  - P1-005-001-004-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-005-001-004-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-005-001-004-003-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-005-001-005 发货通知单CRUD接口
- P1-005-001-005-001 发货通知单Entity/DTO/VO定义（sale_delivery_notice主表+detail从表）
  - P1-005-001-005-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-005-001-005-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-005-001-005-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-005-001-005-002 发货通知单Mapper开发（主从联合查询+来源订单关联查询）
  - P1-005-001-005-002-001 定义Mapper接口（extends BaseMapperX+自定义方法声明）
  - P1-005-001-005-002-002 编写XML映射（resultMap+主从联合查询SQL+动态条件）
  - P1-005-001-005-002-003 编写自定义查询方法（分页查询/条件查询/统计查询SQL）
- P1-005-001-005-003 发货通知单Service开发（主从CRUD+编码自动生成+来源订单引入+审核流转+操作日志）
  - P1-005-001-005-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-005-001-005-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-005-001-005-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-005-001-005-004 发货通知单Controller开发（RESTful接口+参数校验+权限注解+Swagger文档）
  - P1-005-001-005-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-005-001-005-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-005-001-005-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-005-001-006 未出库清单聚合查询接口
- P1-005-001-006-001 未出库SQL编写（发货通知明细LEFT JOIN出库明细按未出库数量聚合）
  - P1-005-001-006-001-001 编写未出库SQL（发货通知明细LEFT JOIN出库明细按未出库数量聚合+分页支持）
  - P1-005-001-006-001-002 验证SQL执行（数据正确+性能合理+索引命中）
  - P1-005-001-006-001-003 验证SQL执行（数据正确+性能合理+索引命中+空结果兜底）
- P1-005-001-006-002 未出库Service聚合逻辑（按通知单/客户/商品维度汇总）
  - P1-005-001-006-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-005-001-006-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-005-001-006-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-005-001-006-003 未出库Controller接口（条件查询+分页+导出）
  - P1-005-001-006-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-005-001-006-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-005-001-006-003-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-005-001-007 出库单CRUD接口
- P1-005-001-007-001 出库单Entity/DTO/VO定义（sale_outbound主表+detail从表+doc_detail_location/batch/serial三表）
  - P1-005-001-007-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-005-001-007-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-005-001-007-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-005-001-007-002 出库单Mapper开发（主从联合查询+库位/批次/序列号子表查询）
  - P1-005-001-007-002-001 定义Mapper接口（extends BaseMapperX+自定义方法声明）
  - P1-005-001-007-002-002 编写XML映射（resultMap+主从联合查询SQL+动态条件）
  - P1-005-001-007-002-003 编写自定义查询方法（分页查询/条件查询/统计查询SQL）
- P1-005-001-007-003 出库单Service开发（主从CRUD+编码自动生成+库位/批次/序列号三表处理+出库库存扣减+审核流转）
  - P1-005-001-007-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-005-001-007-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-005-001-007-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-005-001-007-004 出库单Controller开发（RESTful接口+参数校验+权限注解+Swagger文档）
  - P1-005-001-007-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-005-001-007-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-005-001-007-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-005-001-008 出库退货单CRUD接口
- P1-005-001-008-001 出库退货单Entity/DTO/VO定义（sale_outbound_return主表+detail从表）
  - P1-005-001-008-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-005-001-008-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-005-001-008-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-005-001-008-002 出库退货单Mapper开发（主从联合查询+来源出库单关联查询）
  - P1-005-001-008-002-001 定义Mapper接口（extends BaseMapperX+自定义方法声明）
  - P1-005-001-008-002-002 编写XML映射（resultMap+主从联合查询SQL+动态条件）
  - P1-005-001-008-002-003 编写自定义查询方法（分页查询/条件查询/统计查询SQL）
- P1-005-001-008-003 出库退货单Service开发（主从CRUD+编码自动生成+退货库存增加+审核流转+操作日志）
  - P1-005-001-008-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-005-001-008-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-005-001-008-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-005-001-008-004 出库退货单Controller开发（RESTful接口+参数校验+权限注解+Swagger文档）
  - P1-005-001-008-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-005-001-008-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-005-001-008-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-005-001-009 未对账清单聚合查询接口
- P1-005-001-009-001 未对账SQL编写（出库明细LEFT JOIN对账明细按未对账金额聚合）
  - P1-005-001-009-001-001 编写未对账SQL（出库明细LEFT JOIN对账明细按未对账金额聚合+分页支持）
  - P1-005-001-009-001-002 验证SQL执行（数据正确+金额精度+索引命中）
  - P1-005-001-009-001-003 验证SQL执行（数据正确+性能合理+索引命中+空结果兜底）
- P1-005-001-009-002 未对账Service聚合逻辑（按出库单/客户维度汇总）
  - P1-005-001-009-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-005-001-009-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-005-001-009-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-005-001-009-003 未对账Controller接口（条件查询+分页+导出）
  - P1-005-001-009-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-005-001-009-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-005-001-009-003-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-005-001-010 出库对账单CRUD接口
- P1-005-001-010-001 出库对账单Entity/DTO/VO定义（sale_reconciliation主表+detail从表）
  - P1-005-001-010-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-005-001-010-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-005-001-010-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-005-001-010-002 出库对账单Mapper开发（主从联合查询+来源出库单关联查询）
  - P1-005-001-010-002-001 定义Mapper接口（extends BaseMapperX+自定义方法声明）
  - P1-005-001-010-002-002 编写XML映射（resultMap+主从联合查询SQL+动态条件）
  - P1-005-001-010-002-003 编写自定义查询方法（分页查询/条件查询/统计查询SQL）
- P1-005-001-010-003 出库对账单Service开发（主从CRUD+编码自动生成+来源出库单引入+审核流转+操作日志）
  - P1-005-001-010-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-005-001-010-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-005-001-010-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-005-001-010-004 出库对账单Controller开发（RESTful接口+参数校验+权限注解+Swagger文档）
  - P1-005-001-010-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-005-001-010-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-005-001-010-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-005-001-011 销售统计接口
- P1-005-001-011-001 销售统计SQL编写（按客户/商品/业务员/时间维度汇总统计）
  - P1-005-001-011-001-001 编写销售统计SQL（按客户/商品/业务员/时间维度GROUP BY汇总+金额合计+同比环比计算）
  - P1-005-001-011-001-002 验证SQL执行（维度正确+金额汇总准确+同比环比计算正确）
  - P1-005-001-011-001-003 验证SQL执行（数据正确+性能合理+索引命中+空结果兜底）
- P1-005-001-011-002 销售统计Service开发（多维度聚合+同比环比计算+趋势分析）
  - P1-005-001-011-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-005-001-011-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-005-001-011-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-005-001-011-003 销售统计Controller接口（条件查询+分页+导出）
  - P1-005-001-011-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-005-001-011-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-005-001-011-003-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-005-001-012 销售毛利分析接口
- P1-005-001-012-001 销售毛利SQL编写（销售收入-销售成本-税额按多维度分析）
  - P1-005-001-012-001-001 编写销售毛利SQL（销售收入-销售成本-税额按多维度分析+毛利率计算）
  - P1-005-001-012-001-002 验证SQL执行（成本匹配准确+毛利计算正确+维度汇总一致）
  - P1-005-001-012-001-003 验证SQL执行（数据正确+性能合理+索引命中+空结果兜底）
- P1-005-001-012-002 销售毛利Service开发（毛利计算+毛利率+毛利趋势+排名分析）
  - P1-005-001-012-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-005-001-012-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-005-001-012-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-005-001-012-003 销售毛利Controller接口（条件查询+分页+导出）
  - P1-005-001-012-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-005-001-012-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-005-001-012-003-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-005-001-013 销售单据流转逻辑
- P1-005-001-013-001 报价→订单流转逻辑（报价单下推销售订单+报价明细携带+报价单状态更新）
  - P1-005-001-013-001-001 实现下推方法（源单据→目标单据字段映射+明细携带+源单状态更新）
  - P1-005-001-013-001-002 实现流转路径控制（系统参数判断流转路径+参数缺失提示+强制流转选项）
  - P1-005-001-013-001-003 实现流转校验（源单状态校验+已下推数量校验+关联完整性校验）
- P1-005-001-013-002 订单→发货→出库流转逻辑（订单下推发货通知+发货通知下推出库单+系统参数控制流转路径）
  - P1-005-001-013-002-001 实现下推方法（源单据→目标单据字段映射+明细携带+源单状态更新）
  - P1-005-001-013-002-002 实现流转路径控制（系统参数判断流转路径+参数缺失提示+强制流转选项）
  - P1-005-001-013-002-003 实现流转校验（源单状态校验+已下推数量校验+关联完整性校验）
- P1-005-001-013-003 出库→退货→对账流转逻辑（出库单引入退货+出库单下推对账单+系统参数控制）
  - P1-005-001-013-003-001 实现下推方法（源单据→目标单据字段映射+明细携带+源单状态更新）
  - P1-005-001-013-003-002 实现流转路径控制（系统参数判断流转路径+参数缺失提示+强制流转选项）
  - P1-005-001-013-003-003 实现流转校验（源单状态校验+已下推数量校验+关联完整性校验）
- P1-005-001-013-004 流转引擎集成（引入/下推组件后端Service对接+单据关联关系记录）
  - P1-005-001-013-004-001 定义Service接口（核心方法声明+配置管理方法声明）
  - P1-005-001-013-004-002 实现核心业务逻辑（主流程+分支处理+异常处理+并发控制）
  - P1-005-001-013-004-003 实现配置管理（配置CRUD+缓存加载+配置校验+默认值）
  - P1-005-001-013-004-004 集成对接（上下游服务交互+事件通知+操作日志记录）

#### P1-005-001-014 销售管理工作台聚合数据接口
- P1-005-001-014-001 工作台SQL编写（今日订单/本月销售额/待发货/待对账/Top客户统计）
  - P1-005-001-014-001-001 编写工作台SQL（今日订单数+本月销售额+待发货+待对账+Top10客户统计）
  - P1-005-001-014-001-002 验证SQL执行（统计准确+实时性+并发安全）
  - P1-005-001-014-001-003 验证SQL执行（数据正确+性能合理+索引命中+空结果兜底）
- P1-005-001-014-002 工作台Service开发（多维度数据聚合+趋势数据+预警数据）
  - P1-005-001-014-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-005-001-014-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-005-001-014-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-005-001-014-003 工作台Controller接口
  - P1-005-001-014-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-005-001-014-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-005-001-014-003-003 补充接口文档注解（@Operation+@Schema+示例值）

### P1-005-002 销售管理前端页面

#### P1-005-002-001 销售管理工作台
- P1-005-002-001-001 工作台KPI卡片区（今日订单/本月销售额/待发货/待对账四卡片）
  - P1-005-002-001-001-001 KPI卡片组件开发（数值展示+同比环比+趋势mini图+点击下钻路由）
  - P1-005-002-001-001-002 KPI数据API对接（统计查询API+数据计算+Loading+空数据处理）
- P1-005-002-001-002 工作台图表区（销售趋势折线图+Top客户柱状图+商品销售饼图）
  - P1-005-002-001-002-001 ECharts图表组件开发（折线图/柱状图/饼图+响应式+交互事件+图例）
  - P1-005-002-001-002-002 图表数据API对接（统计API+数据转换+时间范围切换+自动刷新）
- P1-005-002-001-003 工作台待办区（待发货列表+待对账列表+快捷操作按钮）
  - P1-005-002-001-003-001 顶部操作栏布局（新增/修改/删除/导出+v-permission权限控制）
  - P1-005-002-001-003-002 批量操作逻辑（多选+批量删除/审核+确认弹窗+选中校验）
    - P1-005-002-001-003-002-001 实现多选逻辑（vxe-table checkbox多选+全选+跨页选中保持+选中行数据收集）
    - P1-005-002-001-003-002-002 实现选中校验（至少选1条+已审核不可删除+草稿才可删除+不符合条件提示）
    - P1-005-002-001-003-002-003 实现批量操作确认弹窗（操作类型+影响数量提示+确认/取消按钮+二次确认）
    - P1-005-002-001-003-002-004 实现批量操作执行（批量调用API+进度提示+成功/失败统计+列表刷新）
  - P1-005-002-001-003-003 操作事件处理（按钮点击→弹窗/路由+参数传递+回调刷新）
- P1-005-002-001-004 工作台联调验证（API对接+数据刷新+图表交互）
  - P1-005-002-001-004-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-005-002-001-004-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-005-002-001-004-003 工作台异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-005-002-002 报价单主从列表页
- P1-005-002-002-001 主表搜索条件区（客户选择+日期范围+单据状态+单据编号搜索）
  - P1-005-002-002-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-005-002-002-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-005-002-002-001-003 实现主表搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-005-002-002-002 主表数据表格+从表明细标签页区（主表列表+点击行展开从表明细）
  - P1-005-002-002-002-001 vxe-table列定义（字段映射+列宽+排序+对齐+固定列+显隐）
  - P1-005-002-002-002-002 数据加载与分页（API调用+分页参数+数据赋值+Loading）
  - P1-005-002-002-002-003 表格格式化渲染（字典翻译+日期+金额+状态色标+操作列按钮）
- P1-005-002-002-003 操作按钮区（新增/编辑/审核/反审/作废/下推销售订单/复制/打印）
  - P1-005-002-002-003-001 顶部操作栏布局（新增/修改/删除/导出+v-permission权限控制）
  - P1-005-002-002-003-002 批量操作逻辑（多选+批量删除/审核+确认弹窗+选中校验）
    - P1-005-002-002-003-002-001 实现多选逻辑（vxe-table checkbox多选+全选+跨页选中保持+选中行数据收集）
    - P1-005-002-002-003-002-002 实现选中校验（至少选1条+已审核不可删除+草稿才可删除+不符合条件提示）
    - P1-005-002-002-003-002-003 实现批量操作确认弹窗（操作类型+影响数量提示+确认/取消按钮+二次确认）
    - P1-005-002-002-003-002-004 实现批量操作执行（批量调用API+进度提示+成功/失败统计+列表刷新）
  - P1-005-002-002-003-003 操作事件处理（按钮点击→弹窗/路由+参数传递+回调刷新）
- P1-005-002-002-004 列表页联调验证（API对接+分页+操作按钮状态联动）
  - P1-005-002-002-004-001 顶部操作栏布局（新增/修改/删除/导出+v-permission权限控制）
  - P1-005-002-002-004-002 批量操作逻辑（多选+批量删除/审核+确认弹窗+选中校验）
    - P1-005-002-002-004-002-001 实现多选逻辑（vxe-table checkbox多选+全选+跨页选中保持+选中行数据收集）
    - P1-005-002-002-004-002-002 实现选中校验（至少选1条+已审核不可删除+草稿才可删除+不符合条件提示）
    - P1-005-002-002-004-002-003 实现批量操作确认弹窗（操作类型+影响数量提示+确认/取消按钮+二次确认）
    - P1-005-002-002-004-002-004 实现批量操作执行（批量调用API+进度提示+成功/失败统计+列表刷新）
  - P1-005-002-002-004-003 操作事件处理（按钮点击→弹窗/路由+参数传递+回调刷新）

#### P1-005-002-003 新增/编辑报价单主从表单页
- P1-005-002-003-001 主表字段录入区（客户选择器+日期+备注+附件上传）
  - P1-005-002-003-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-005-002-003-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-005-002-003-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-005-002-003-002 从表明细行编辑区（商品选择器+数量+单价+折扣+税率+金额计算）
  - P1-005-002-003-002-001 明细行vxe-table定义（列配置+可编辑列+行号+增删行+合计行）
  - P1-005-002-003-002-002 明细行编辑交互（单元格编辑+下拉+弹出选择器+行增删复制）
  - P1-005-002-003-002-003 明细行自动计算（数量×单价=金额+税率+折扣+合计汇总）
- P1-005-002-003-003 选择器对接+金额自动计算逻辑（客户选择器+商品选择器+税率联动+行金额/合计金额自动计算）
  - P1-005-002-003-003-001 商品选择器对接（弹出列表+搜索+多选+回填字段）
  - P1-005-002-003-003-002 客户/供应商/仓库选择器对接（弹出+搜索+回填）
  - P1-005-002-003-003-003 金额计算逻辑（单价×数量+税率+折扣+合计汇总）
- P1-005-002-003-004 审核/流转按钮集成（审核/反审按钮+下推销售订单弹窗+单据追溯弹窗）
  - P1-005-002-003-004-001 审核按钮组渲染（提交/审核/驳回/撤回/关闭+按状态权限显隐）
  - P1-005-002-003-004-002 审核流程对话框（审批意见+审批节点+签字+流程图）
  - P1-005-002-003-004-003 审核API调用与状态刷新（审核接口+刷新状态+操作反馈）
- P1-005-002-003-005 表单联调验证（API对接+表单校验+保存/提交+操作日志）
  - P1-005-002-003-005-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-005-002-003-005-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-005-002-003-005-003 表单联调验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-005-002-004 销售订单主从列表页
- P1-005-002-004-001 主表搜索条件区（客户+日期+状态+编号+业务员搜索）
  - P1-005-002-004-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-005-002-004-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-005-002-004-001-003 实现主表搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-005-002-004-002 主表数据表格+从表明细标签页区
  - P1-005-002-004-002-001 vxe-table列定义（字段映射+列宽+排序+对齐+固定列+显隐）
  - P1-005-002-004-002-002 数据加载与分页（API调用+分页参数+数据赋值+Loading）
  - P1-005-002-004-002-003 表格格式化渲染（字典翻译+日期+金额+状态色标+操作列按钮）
- P1-005-002-004-003 操作按钮区（新增/编辑/审核/变更/下推发货通知/复制/打印）
  - P1-005-002-004-003-001 顶部操作栏布局（新增/修改/删除/导出+v-permission权限控制）
  - P1-005-002-004-003-002 批量操作逻辑（多选+批量删除/审核+确认弹窗+选中校验）
    - P1-005-002-004-003-002-001 实现多选逻辑（vxe-table checkbox多选+全选+跨页选中保持+选中行数据收集）
    - P1-005-002-004-003-002-002 实现选中校验（至少选1条+已审核不可删除+草稿才可删除+不符合条件提示）
    - P1-005-002-004-003-002-003 实现批量操作确认弹窗（操作类型+影响数量提示+确认/取消按钮+二次确认）
    - P1-005-002-004-003-002-004 实现批量操作执行（批量调用API+进度提示+成功/失败统计+列表刷新）
  - P1-005-002-004-003-003 操作事件处理（按钮点击→弹窗/路由+参数传递+回调刷新）
- P1-005-002-004-004 列表页联调验证
  - P1-005-002-004-004-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-005-002-004-004-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-005-002-004-004-003 列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-005-002-005 新增/编辑销售订单主从表单页
- P1-005-002-005-001 主表字段录入区（客户+日期+业务员+备注+附件）
  - P1-005-002-005-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-005-002-005-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-005-002-005-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-005-002-005-002 从表明细行编辑区（商品+数量+单价+折扣+税率+交货日期+序列号录入）
  - P1-005-002-005-002-001 明细行vxe-table定义（列配置+可编辑列+行号+增删行+合计行）
  - P1-005-002-005-002-002 明细行编辑交互（单元格编辑+下拉+弹出选择器+行增删复制）
  - P1-005-002-005-002-003 明细行自动计算（数量×单价=金额+税率+折扣+合计汇总）
- P1-005-002-005-003 序列号子表区（序列号扫描/批量导入+序列号数量校验+序列号状态显示）
  - P1-005-002-005-003-001 序列号子表vxe-table定义（序列号列+批量录入+行操作）
  - P1-005-002-005-003-002 序列号批量录入弹窗（批量输入+自动拆行+重复校验+数量校验）
- P1-005-002-005-004 选择器对接+金额自动计算逻辑
  - P1-005-002-005-004-001 商品选择器对接（弹出列表+搜索+多选+回填字段）
  - P1-005-002-005-004-002 客户/供应商/仓库选择器对接（弹出+搜索+回填）
  - P1-005-002-005-004-003 金额计算逻辑（单价×数量+税率+折扣+合计汇总）
- P1-005-002-005-005 审核/变更/流转按钮集成（审核+变更记录+下推发货通知+引入报价单+追溯）
  - P1-005-002-005-005-001 审核按钮组渲染（提交/审核/驳回/撤回/关闭+按状态权限显隐）
  - P1-005-002-005-005-002 审核流程对话框（审批意见+审批节点+签字+流程图）
  - P1-005-002-005-005-003 审核API调用与状态刷新（审核接口+刷新状态+操作反馈）
- P1-005-002-005-006 表单联调验证
  - P1-005-002-005-006-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-005-002-005-006-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-005-002-005-006-003 表单联调验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-005-002-006 销售订单变更记录列表页
- P1-005-002-006-001 搜索条件区（订单编号+变更时间+变更人搜索）
  - P1-005-002-006-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-005-002-006-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-005-002-006-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-005-002-006-002 变更记录数据表格区（变更前后对比+变更原因+变更人/时间）
  - P1-005-002-006-002-001 vxe-table列定义（字段映射+列宽+排序+对齐+固定列+显隐）
  - P1-005-002-006-002-002 数据加载与分页（API调用+分页参数+数据赋值+Loading）
  - P1-005-002-006-002-003 表格格式化渲染（字典翻译+日期+金额+状态色标+操作列按钮）
- P1-005-002-006-003 列表页联调验证
  - P1-005-002-006-003-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-005-002-006-003-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-005-002-006-003-003 列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-005-002-007 未发货通知清单页
- P1-005-002-007-001 查询条件区（订单编号+客户+商品+日期范围搜索）
  - P1-005-002-007-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-005-002-007-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-005-002-007-001-003 实现查询条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-005-002-007-002 查询结果表格区（订单明细+已发货数+未发货数+发货进度百分比）
  - P1-005-002-007-002-001 vxe-table列定义（字段映射+列宽+排序+对齐+固定列+显隐）
  - P1-005-002-007-002-002 数据加载与分页（API调用+分页参数+数据赋值+Loading）
  - P1-005-002-007-002-003 表格格式化渲染（字典翻译+日期+金额+状态色标+操作列按钮）
- P1-005-002-007-003 快捷操作区（快捷下推发货通知+导出）
  - P1-005-002-007-003-001 实现导出逻辑（查询条件+API+文件流+文件名处理）
  - P1-005-002-007-003-002 导出异常处理（大数据量分页+超时+失败重试提示）
- P1-005-002-007-004 查询页联调验证
  - P1-005-002-007-004-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-005-002-007-004-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-005-002-007-004-003 查询页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-005-002-008 发货通知单主从列表页
- P1-005-002-008-001 主表搜索条件区（客户+日期+状态+来源订单搜索）
  - P1-005-002-008-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-005-002-008-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-005-002-008-001-003 实现主表搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-005-002-008-002 主表数据表格+从表明细区
  - P1-005-002-008-002-001 vxe-table列定义（字段映射+列宽+排序+对齐+固定列+显隐）
  - P1-005-002-008-002-002 数据加载与分页（API调用+分页参数+数据赋值+Loading）
  - P1-005-002-008-002-003 表格格式化渲染（字典翻译+日期+金额+状态色标+操作列按钮）
- P1-005-002-008-003 操作按钮区（新增/编辑/审核/下推出库单/打印）
  - P1-005-002-008-003-001 顶部操作栏布局（新增/修改/删除/导出+v-permission权限控制）
  - P1-005-002-008-003-002 批量操作逻辑（多选+批量删除/审核+确认弹窗+选中校验）
    - P1-005-002-008-003-002-001 实现多选逻辑（vxe-table checkbox多选+全选+跨页选中保持+选中行数据收集）
    - P1-005-002-008-003-002-002 实现选中校验（至少选1条+已审核不可删除+草稿才可删除+不符合条件提示）
    - P1-005-002-008-003-002-003 实现批量操作确认弹窗（操作类型+影响数量提示+确认/取消按钮+二次确认）
    - P1-005-002-008-003-002-004 实现批量操作执行（批量调用API+进度提示+成功/失败统计+列表刷新）
  - P1-005-002-008-003-003 操作事件处理（按钮点击→弹窗/路由+参数传递+回调刷新）
- P1-005-002-008-004 列表页联调验证
  - P1-005-002-008-004-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-005-002-008-004-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-005-002-008-004-003 列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-005-002-009 新增/编辑发货通知单主从表单页
- P1-005-002-009-001 主表字段录入区（客户+日期+发货方式+备注）
  - P1-005-002-009-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-005-002-009-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-005-002-009-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-005-002-009-002 从表明细行编辑区（引入销售订单明细+发货数量+交货日期）
  - P1-005-002-009-002-001 明细行vxe-table定义（列配置+可编辑列+行号+增删行+合计行）
  - P1-005-002-009-002-002 明细行编辑交互（单元格编辑+下拉+弹出选择器+行增删复制）
  - P1-005-002-009-002-003 明细行自动计算（数量×单价=金额+税率+折扣+合计汇总）
- P1-005-002-009-003 引入销售订单弹窗对接（源单选择+明细行勾选+自动填充）
  - P1-005-002-009-003-001 明细行vxe-table定义（列配置+可编辑列+行号+增删行+合计行）
  - P1-005-002-009-003-002 明细行编辑交互（单元格编辑+下拉+弹出选择器+行增删复制）
  - P1-005-002-009-003-003 明细行自动计算（数量×单价=金额+税率+折扣+合计汇总）
- P1-005-002-009-004 表单联调验证
  - P1-005-002-009-004-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-005-002-009-004-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-005-002-009-004-003 表单联调验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-005-002-010 未出库清单页
- P1-005-002-010-001 查询条件区
  - P1-005-002-010-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-005-002-010-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-005-002-010-001-003 实现查询条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-005-002-010-002 查询结果表格区（发货通知明细+已出库数+未出库数）
  - P1-005-002-010-002-001 vxe-table列定义（字段映射+列宽+排序+对齐+固定列+显隐）
  - P1-005-002-010-002-002 数据加载与分页（API调用+分页参数+数据赋值+Loading）
  - P1-005-002-010-002-003 表格格式化渲染（字典翻译+日期+金额+状态色标+操作列按钮）
- P1-005-002-010-003 快捷操作区（快捷下推出库单+导出）
  - P1-005-002-010-003-001 实现导出逻辑（查询条件+API+文件流+文件名处理）
  - P1-005-002-010-003-002 导出异常处理（大数据量分页+超时+失败重试提示）
- P1-005-002-010-004 查询页联调验证
  - P1-005-002-010-004-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-005-002-010-004-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-005-002-010-004-003 查询页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-005-002-011 出库单主从列表页
- P1-005-002-011-001 主表搜索条件区（客户+日期+状态+仓库搜索）
  - P1-005-002-011-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-005-002-011-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-005-002-011-001-003 实现主表搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-005-002-011-002 主表数据表格+从表明细区
  - P1-005-002-011-002-001 vxe-table列定义（字段映射+列宽+排序+对齐+固定列+显隐）
  - P1-005-002-011-002-002 数据加载与分页（API调用+分页参数+数据赋值+Loading）
  - P1-005-002-011-002-003 表格格式化渲染（字典翻译+日期+金额+状态色标+操作列按钮）
- P1-005-002-011-003 操作按钮区（新增/编辑/审核/下推对账单/打印）
  - P1-005-002-011-003-001 顶部操作栏布局（新增/修改/删除/导出+v-permission权限控制）
  - P1-005-002-011-003-002 批量操作逻辑（多选+批量删除/审核+确认弹窗+选中校验）
    - P1-005-002-011-003-002-001 实现多选逻辑（vxe-table checkbox多选+全选+跨页选中保持+选中行数据收集）
    - P1-005-002-011-003-002-002 实现选中校验（至少选1条+已审核不可删除+草稿才可删除+不符合条件提示）
    - P1-005-002-011-003-002-003 实现批量操作确认弹窗（操作类型+影响数量提示+确认/取消按钮+二次确认）
    - P1-005-002-011-003-002-004 实现批量操作执行（批量调用API+进度提示+成功/失败统计+列表刷新）
  - P1-005-002-011-003-003 操作事件处理（按钮点击→弹窗/路由+参数传递+回调刷新）
- P1-005-002-011-004 列表页联调验证
  - P1-005-002-011-004-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-005-002-011-004-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-005-002-011-004-003 列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-005-002-012 新增/编辑出库单主从表单页
- P1-005-002-012-001 主表字段录入区（客户+仓库+出库日期+备注）
  - P1-005-002-012-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-005-002-012-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-005-002-012-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-005-002-012-002 从表明细行编辑区（商品+数量+单价+金额）
  - P1-005-002-012-002-001 明细行vxe-table定义（列配置+可编辑列+行号+增删行+合计行）
  - P1-005-002-012-002-002 明细行编辑交互（单元格编辑+下拉+弹出选择器+行增删复制）
  - P1-005-002-012-002-003 明细行自动计算（数量×单价=金额+税率+折扣+合计汇总）
- P1-005-002-012-003 库位/批次/序列号子表录入区（库位选择+批号新增/选择+序列号扫描/导入）
  - P1-005-002-012-003-001 库位子表vxe-table定义（仓库/库位/数量+可编辑+行操作）
  - P1-005-002-012-003-002 库位弹窗选择器对接（仓库选择→库位联动→数量分配→校验）
- P1-005-002-012-004 引入发货通知弹窗对接+金额计算
  - P1-005-002-012-004-001 实现引入弹窗组件（el-dialog+源单列表+搜索+分页+多选）
  - P1-005-002-012-004-002 实现引入数据回填（选中源单→明细行拆分映射+金额计算+源单标记已引入）
- P1-005-002-012-005 审核/流转按钮集成（审核+下推对账单+引入发货通知+追溯）
  - P1-005-002-012-005-001 审核按钮组渲染（提交/审核/驳回/撤回/关闭+按状态权限显隐）
  - P1-005-002-012-005-002 审核流程对话框（审批意见+审批节点+签字+流程图）
  - P1-005-002-012-005-003 审核API调用与状态刷新（审核接口+刷新状态+操作反馈）
- P1-005-002-012-006 表单联调验证
  - P1-005-002-012-006-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-005-002-012-006-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-005-002-012-006-003 表单联调验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-005-002-013 出库退货单主从列表页
- P1-005-002-013-001 搜索条件区+数据表格区
  - P1-005-002-013-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-005-002-013-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-005-002-013-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-005-002-013-002 操作按钮区（新增/编辑/审核/打印）
  - P1-005-002-013-002-001 顶部操作栏布局（新增/修改/删除/导出+v-permission权限控制）
  - P1-005-002-013-002-002 批量操作逻辑（多选+批量删除/审核+确认弹窗+选中校验）
    - P1-005-002-013-002-002-001 实现多选逻辑（vxe-table checkbox多选+全选+跨页选中保持+选中行数据收集）
    - P1-005-002-013-002-002-002 实现选中校验（至少选1条+已审核不可删除+草稿才可删除+不符合条件提示）
    - P1-005-002-013-002-002-003 实现批量操作确认弹窗（操作类型+影响数量提示+确认/取消按钮+二次确认）
    - P1-005-002-013-002-002-004 实现批量操作执行（批量调用API+进度提示+成功/失败统计+列表刷新）
  - P1-005-002-013-002-003 操作事件处理（按钮点击→弹窗/路由+参数传递+回调刷新）
- P1-005-002-013-003 列表页联调验证
  - P1-005-002-013-003-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-005-002-013-003-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-005-002-013-003-003 列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-005-002-014 新增/编辑出库退货单主从表单页
- P1-005-002-014-001 主表字段+从表明细行区（引入出库单明细+退货数量+退货原因）
  - P1-005-002-014-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-005-002-014-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-005-002-014-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-005-002-014-002 引入出库单弹窗对接
  - P1-005-002-014-002-001 实现引入弹窗组件（el-dialog+源单列表+搜索+分页+多选）
  - P1-005-002-014-002-002 实现引入数据回填（选中源单→明细行拆分映射+金额计算+源单标记已引入）
- P1-005-002-014-003 表单联调验证
  - P1-005-002-014-003-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-005-002-014-003-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-005-002-014-003-003 表单联调验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-005-002-015 未对账清单页
- P1-005-002-015-001 查询条件区+查询结果表格区
  - P1-005-002-015-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-005-002-015-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-005-002-015-001-003 实现查询条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-005-002-015-002 快捷操作区（快捷下推对账单+导出）
  - P1-005-002-015-002-001 实现导出逻辑（查询条件+API+文件流+文件名处理）
  - P1-005-002-015-002-002 导出异常处理（大数据量分页+超时+失败重试提示）
- P1-005-002-015-003 查询页联调验证
  - P1-005-002-015-003-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-005-002-015-003-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-005-002-015-003-003 查询页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-005-002-016 出库对账单主从列表页
- P1-005-002-016-001 搜索条件区+数据表格区
  - P1-005-002-016-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-005-002-016-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-005-002-016-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-005-002-016-002 操作按钮区（新增/编辑/审核/打印）
  - P1-005-002-016-002-001 顶部操作栏布局（新增/修改/删除/导出+v-permission权限控制）
  - P1-005-002-016-002-002 批量操作逻辑（多选+批量删除/审核+确认弹窗+选中校验）
    - P1-005-002-016-002-002-001 实现多选逻辑（vxe-table checkbox多选+全选+跨页选中保持+选中行数据收集）
    - P1-005-002-016-002-002-002 实现选中校验（至少选1条+已审核不可删除+草稿才可删除+不符合条件提示）
    - P1-005-002-016-002-002-003 实现批量操作确认弹窗（操作类型+影响数量提示+确认/取消按钮+二次确认）
    - P1-005-002-016-002-002-004 实现批量操作执行（批量调用API+进度提示+成功/失败统计+列表刷新）
  - P1-005-002-016-002-003 操作事件处理（按钮点击→弹窗/路由+参数传递+回调刷新）
- P1-005-002-016-003 列表页联调验证
  - P1-005-002-016-003-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-005-002-016-003-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-005-002-016-003-003 列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-005-002-017 新增/编辑出库对账单主从表单页
- P1-005-002-017-001 主表字段+从表明细行区（引入出库单明细+对账数量+对账金额）
  - P1-005-002-017-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-005-002-017-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-005-002-017-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-005-002-017-002 引入出库单弹窗对接
  - P1-005-002-017-002-001 实现引入弹窗组件（el-dialog+源单列表+搜索+分页+多选）
  - P1-005-002-017-002-002 实现引入数据回填（选中源单→明细行拆分映射+金额计算+源单标记已引入）
- P1-005-002-017-003 表单联调验证
  - P1-005-002-017-003-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-005-002-017-003-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-005-002-017-003-003 表单联调验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-005-002-018 销售统计页
- P1-005-002-018-001 报表条件区（时间范围+客户+商品+业务员维度选择）
  - P1-005-002-018-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-005-002-018-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-005-002-018-001-003 实现报表条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-005-002-018-002 报表数据表格/图表区（汇总表+明细表+趋势图+排名图）
  - P1-005-002-018-002-001 vxe-table列定义（字段映射+列宽+排序+对齐+固定列+显隐）
  - P1-005-002-018-002-002 数据加载与分页（API调用+分页参数+数据赋值+Loading）
  - P1-005-002-018-002-003 表格格式化渲染（字典翻译+日期+金额+状态色标+操作列按钮）
- P1-005-002-018-003 导出+打印区
  - P1-005-002-018-003-001 实现导出逻辑（查询条件+API+文件流+文件名处理）
  - P1-005-002-018-003-002 导出异常处理（大数据量分页+超时+失败重试提示）
- P1-005-002-018-004 报表页联调验证
  - P1-005-002-018-004-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-005-002-018-004-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-005-002-018-004-003 报表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-005-002-019 销售毛利分析页
- P1-005-002-019-001 报表条件区（时间+客户+商品+业务员维度+成本方法选择）
  - P1-005-002-019-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-005-002-019-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-005-002-019-001-003 实现报表条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-005-002-019-002 报表数据表格/图表区（毛利汇总+毛利率+毛利趋势+排名）
  - P1-005-002-019-002-001 vxe-table列定义（字段映射+列宽+排序+对齐+固定列+显隐）
  - P1-005-002-019-002-002 数据加载与分页（API调用+分页参数+数据赋值+Loading）
  - P1-005-002-019-002-003 表格格式化渲染（字典翻译+日期+金额+状态色标+操作列按钮）
- P1-005-002-019-003 导出+打印区
  - P1-005-002-019-003-001 实现导出逻辑（查询条件+API+文件流+文件名处理）
  - P1-005-002-019-003-002 导出异常处理（大数据量分页+超时+失败重试提示）
- P1-005-002-019-004 报表页联调验证
  - P1-005-002-019-004-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-005-002-019-004-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-005-002-019-004-003 报表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

---

## P1-006 采购管理模块开发

> **依赖**：P0-007（商品）、P0-009（供应商）、P0-010（仓库）、P0-011（财务基础）、P1-001（审核引擎）、P1-004（流转引擎）  
> **概述**：完成采购全链路业务：询价→请购→采购订单→入库→退货→对账→统计

### P1-006-001 采购管理后端开发

#### P1-006-001-001 询价单CRUD接口
- P1-006-001-001-001 询价单Entity/DTO/VO定义
  - P1-006-001-001-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-006-001-001-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-006-001-001-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-006-001-001-002 询价单Mapper开发（主从联合查询+按供应商/状态/时间条件查询）
  - P1-006-001-001-002-001 定义Mapper接口（extends BaseMapperX+自定义方法声明）
  - P1-006-001-001-002-002 编写XML映射（resultMap+主从联合查询SQL+动态条件）
  - P1-006-001-001-002-003 编写自定义查询方法（分页查询/条件查询/统计查询SQL）
- P1-006-001-001-003 询价单Service开发（主从CRUD+编码自动生成+审核流转+操作日志）
  - P1-006-001-001-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-006-001-001-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-006-001-001-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-006-001-001-004 询价单Controller开发
  - P1-006-001-001-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-006-001-001-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-006-001-001-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-006-001-002 请购单CRUD接口
- P1-006-001-002-001 请购单Entity/DTO/VO定义
  - P1-006-001-002-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-006-001-002-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-006-001-002-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-006-001-002-002 请购单Mapper开发
  - P1-006-001-002-002-001 定义Mapper接口（extends BaseMapperX+@Mapper+自定义方法声明）
  - P1-006-001-002-002-002 编写XML映射文件（resultMap+自定义SQL片段+动态条件）
- P1-006-001-002-003 请购单Service开发（主从CRUD+编码自动生成+审核流转+操作日志）
  - P1-006-001-002-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-006-001-002-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-006-001-002-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-006-001-002-004 请购单Controller开发
  - P1-006-001-002-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-006-001-002-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-006-001-002-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-006-001-003 未采购清单聚合查询接口
- P1-006-001-003-001 未采购SQL编写（请购明细LEFT JOIN采购明细按未采购数量聚合）
  - P1-006-001-003-001-001 编写未采购SQL（请购明细LEFT JOIN采购明细按未采购数量聚合+分页支持）
  - P1-006-001-003-001-002 验证SQL执行（数据正确+性能合理+索引命中）
  - P1-006-001-003-001-003 验证SQL执行（数据正确+性能合理+索引命中+空结果兜底）
- P1-006-001-003-002 未采购Service聚合逻辑
  - P1-006-001-003-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-006-001-003-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-006-001-003-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-006-001-003-003 未采购Controller接口
  - P1-006-001-003-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-006-001-003-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-006-001-003-003-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-006-001-004 采购订单CRUD接口
- P1-006-001-004-001 采购订单Entity/DTO/VO定义（purchase_order主表+detail从表）
  - P1-006-001-004-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-006-001-004-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-006-001-004-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-006-001-004-002 采购订单Mapper开发（主从联合查询+按供应商/状态/时间条件查询）
  - P1-006-001-004-002-001 定义Mapper接口（extends BaseMapperX+自定义方法声明）
  - P1-006-001-004-002-002 编写XML映射（resultMap+主从联合查询SQL+动态条件）
  - P1-006-001-004-002-003 编写自定义查询方法（分页查询/条件查询/统计查询SQL）
- P1-006-001-004-003 采购订单Service开发（主从CRUD+编码自动生成+金额计算+审核流转+操作日志）
  - P1-006-001-004-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-006-001-004-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-006-001-004-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-006-001-004-004 采购订单Controller开发
  - P1-006-001-004-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-006-001-004-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-006-001-004-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-006-001-005 采购订单变更记录接口
- P1-006-001-005-001 采购订单变更Entity/Mapper开发
  - P1-006-001-005-001-001 定义Mapper接口（extends BaseMapperX+@Mapper+自定义方法声明）
  - P1-006-001-005-001-002 编写XML映射文件（resultMap+自定义SQL片段+动态条件）
- P1-006-001-005-002 采购订单变更Service开发（变更对比+变更原因+变更记录查询）
  - P1-006-001-005-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-006-001-005-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-006-001-005-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-006-001-005-003 采购订单变更Controller开发
  - P1-006-001-005-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-006-001-005-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-006-001-005-003-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-006-001-006 未入库清单聚合查询接口
- P1-006-001-006-001 未入库SQL编写
  - P1-006-001-006-001-001 编写未入库SQL（采购明细LEFT JOIN入库明细按未入库数量聚合+分页支持）
  - P1-006-001-006-001-002 验证SQL执行（数据正确+性能合理+索引命中+空结果处理）
  - P1-006-001-006-001-003 验证SQL执行（数据正确+性能合理+索引命中+空结果兜底）
- P1-006-001-006-002 未入库Service聚合逻辑
  - P1-006-001-006-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-006-001-006-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-006-001-006-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-006-001-006-003 未入库Controller接口
  - P1-006-001-006-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-006-001-006-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-006-001-006-003-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-006-001-007 入库单CRUD接口
- P1-006-001-007-001 入库单Entity/DTO/VO定义（purchase_inbound主表+detail从表+doc_detail三表）
  - P1-006-001-007-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-006-001-007-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-006-001-007-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-006-001-007-002 入库单Mapper开发（主从联合查询+库位/批次/序列号子表查询）
  - P1-006-001-007-002-001 定义Mapper接口（extends BaseMapperX+自定义方法声明）
  - P1-006-001-007-002-002 编写XML映射（resultMap+主从联合查询SQL+动态条件）
  - P1-006-001-007-002-003 编写自定义查询方法（分页查询/条件查询/统计查询SQL）
- P1-006-001-007-003 入库单Service开发（主从CRUD+编码自动生成+库位/批次/序列号三表处理+入库库存增加+审核流转）
  - P1-006-001-007-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-006-001-007-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-006-001-007-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-006-001-007-004 入库单Controller开发
  - P1-006-001-007-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-006-001-007-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-006-001-007-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-006-001-008 入库退货单CRUD接口
- P1-006-001-008-001 入库退货单Entity/DTO/VO定义
  - P1-006-001-008-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-006-001-008-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-006-001-008-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-006-001-008-002 入库退货单Mapper开发
  - P1-006-001-008-002-001 定义Mapper接口（extends BaseMapperX+@Mapper+自定义方法声明）
  - P1-006-001-008-002-002 编写XML映射文件（resultMap+自定义SQL片段+动态条件）
- P1-006-001-008-003 入库退货单Service开发（主从CRUD+退货库存扣减+审核流转）
  - P1-006-001-008-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-006-001-008-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-006-001-008-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-006-001-008-004 入库退货单Controller开发
  - P1-006-001-008-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-006-001-008-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-006-001-008-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-006-001-009 未对账清单聚合查询接口
- P1-006-001-009-001 未对账SQL编写
  - P1-006-001-009-001-001 编写未对账SQL（入库明细LEFT JOIN对账明细按未对账金额聚合+分页支持）
  - P1-006-001-009-001-002 验证SQL执行（数据正确+金额精度+索引命中）
  - P1-006-001-009-001-003 验证SQL执行（数据正确+性能合理+索引命中+空结果兜底）
- P1-006-001-009-002 未对账Service聚合逻辑
  - P1-006-001-009-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-006-001-009-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-006-001-009-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-006-001-009-003 未对账Controller接口
  - P1-006-001-009-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-006-001-009-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-006-001-009-003-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-006-001-010 入库对账单CRUD接口
- P1-006-001-010-001 入库对账单Entity/DTO/VO定义
  - P1-006-001-010-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-006-001-010-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-006-001-010-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-006-001-010-002 入库对账单Mapper开发
  - P1-006-001-010-002-001 定义Mapper接口（extends BaseMapperX+@Mapper+自定义方法声明）
  - P1-006-001-010-002-002 编写XML映射文件（resultMap+自定义SQL片段+动态条件）
- P1-006-001-010-003 入库对账单Service开发（主从CRUD+来源入库单引入+审核流转）
  - P1-006-001-010-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-006-001-010-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-006-001-010-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-006-001-010-004 入库对账单Controller开发
  - P1-006-001-010-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-006-001-010-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-006-001-010-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-006-001-011 采购统计接口
- P1-006-001-011-001 采购统计SQL编写（按供应商/商品/采购员/时间维度汇总）
  - P1-006-001-011-001-001 编写采购统计SQL（按供应商/商品/采购员/时间维度GROUP BY汇总+金额合计）
  - P1-006-001-011-001-002 验证SQL执行（维度正确+金额汇总准确+性能合理）
  - P1-006-001-011-001-003 验证SQL执行（数据正确+性能合理+索引命中+空结果兜底）
- P1-006-001-011-002 采购统计Service开发
  - P1-006-001-011-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-006-001-011-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-006-001-011-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-006-001-011-003 采购统计Controller接口
  - P1-006-001-011-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-006-001-011-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-006-001-011-003-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-006-001-012 采购单据流转逻辑
- P1-006-001-012-001 询价→请购→采购流转逻辑
  - P1-006-001-012-001-001 实现下推方法（源单据→目标单据字段映射+明细携带+源单状态更新）
  - P1-006-001-012-001-002 实现流转路径控制（系统参数判断流转路径+参数缺失提示+强制流转选项）
  - P1-006-001-012-001-003 实现流转校验（源单状态校验+已下推数量校验+关联完整性校验）
- P1-006-001-012-002 采购→入库→退货→对账流转逻辑
  - P1-006-001-012-002-001 实现下推方法（源单据→目标单据字段映射+明细携带+源单状态更新）
  - P1-006-001-012-002-002 实现流转路径控制（系统参数判断流转路径+参数缺失提示+强制流转选项）
  - P1-006-001-012-002-003 实现流转校验（源单状态校验+已下推数量校验+关联完整性校验）
- P1-006-001-012-003 流转引擎集成
  - P1-006-001-012-003-001 集成流转引擎（下推→采购入库+下推→采购对账+下推→采购退货 按系统参数判断路径）
    - P1-006-001-012-003-001-001 实现下推→采购入库路径（采购订单明细→创建入库单+字段映射+数量拆分+状态更新）
    - P1-006-001-012-003-001-002 实现下推→采购对账/退货路径（采购入库→对账单+采购订单→退货单+系统参数判断路径）
    - P1-006-001-012-003-001-003 验证流转路径（各路径下推正确+参数控制生效+源单状态更新+数据映射完整）
  - P1-006-001-012-003-002 验证流转路径（下推入库/对账/退货路径正确+参数控制生效+源单状态更新）
  - P1-006-001-012-003-003 验证流转路径（下推路径正确+状态更新+数据映射完整）

#### P1-006-001-013 采购管理工作台聚合数据接口
- P1-006-001-013-001 工作台SQL编写
  - P1-006-001-013-001-001 编写工作台SQL（今日采购额+本月采购额+待入库+待对账+Top10供应商统计）
  - P1-006-001-013-001-002 验证SQL执行（统计准确+实时性+并发安全）
  - P1-006-001-013-001-003 验证SQL执行（数据正确+性能合理+索引命中+空结果兜底）
- P1-006-001-013-002 工作台Service开发
  - P1-006-001-013-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-006-001-013-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-006-001-013-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-006-001-013-003 工作台Controller接口
  - P1-006-001-013-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-006-001-013-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-006-001-013-003-003 补充接口文档注解（@Operation+@Schema+示例值）

### P1-006-002 采购管理前端页面

#### P1-006-002-001 采购管理工作台
- P1-006-002-001-001 工作台KPI卡片区
  - P1-006-002-001-001-001 KPI卡片组件开发（数值展示+同比环比+趋势mini图+点击下钻路由）
  - P1-006-002-001-001-002 KPI数据API对接（统计查询API+数据计算+Loading+空数据处理）
- P1-006-002-001-002 工作台图表区
  - P1-006-002-001-002-001 ECharts图表组件开发（折线图/柱状图/饼图+响应式+交互事件+图例）
  - P1-006-002-001-002-002 图表数据API对接（统计API+数据转换+时间范围切换+自动刷新）
- P1-006-002-001-003 工作台待办区
  - P1-006-002-001-003-001 待办列表组件开发（待审核/待处理/已超期分类+数量徽标+点击跳转）
  - P1-006-002-001-003-002 待办数据API对接（待办统计API+列表查询+实时刷新+消息推送联动）
- P1-006-002-001-004 工作台联调验证
  - P1-006-002-001-004-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-006-002-001-004-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-006-002-001-004-003 工作台异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-006-002-002 询价单主从列表页
- P1-006-002-002-001 搜索条件区
  - P1-006-002-002-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-006-002-002-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-006-002-002-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-006-002-002-002 主表数据表格+从表明细区
  - P1-006-002-002-002-001 vxe-table列定义（字段映射+列宽+排序+对齐+固定列+显隐）
  - P1-006-002-002-002-002 数据加载与分页（API调用+分页参数+数据赋值+Loading）
  - P1-006-002-002-002-003 表格格式化渲染（字典翻译+日期+金额+状态色标+操作列按钮）
- P1-006-002-002-003 操作按钮区（新增/编辑/审核/下推请购/打印）
  - P1-006-002-002-003-001 顶部操作栏布局（新增/修改/删除/导出+v-permission权限控制）
  - P1-006-002-002-003-002 批量操作逻辑（多选+批量删除/审核+确认弹窗+选中校验）
    - P1-006-002-002-003-002-001 实现多选逻辑（vxe-table checkbox多选+全选+跨页选中保持+选中行数据收集）
    - P1-006-002-002-003-002-002 实现选中校验（至少选1条+已审核不可删除+草稿才可删除+不符合条件提示）
    - P1-006-002-002-003-002-003 实现批量操作确认弹窗（操作类型+影响数量提示+确认/取消按钮+二次确认）
    - P1-006-002-002-003-002-004 实现批量操作执行（批量调用API+进度提示+成功/失败统计+列表刷新）
  - P1-006-002-002-003-003 操作事件处理（按钮点击→弹窗/路由+参数传递+回调刷新）
- P1-006-002-002-004 列表页联调验证
  - P1-006-002-002-004-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-006-002-002-004-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-006-002-002-004-003 列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-006-002-003 新增/编辑询价单主从表单页
- P1-006-002-003-001 主表字段录入区
  - P1-006-002-003-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-006-002-003-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-006-002-003-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-006-002-003-002 从表明细行编辑区
  - P1-006-002-003-002-001 明细行vxe-table定义（列配置+可编辑列+行号+增删行+合计行）
  - P1-006-002-003-002-002 明细行编辑交互（单元格编辑+下拉+弹出选择器+行增删复制）
  - P1-006-002-003-002-003 明细行自动计算（数量×单价=金额+税率+折扣+合计汇总）
- P1-006-002-003-003 选择器对接
  - P1-006-002-003-003-001 商品选择器对接（弹出列表+搜索+多选+回填字段）
  - P1-006-002-003-003-002 客户/供应商/仓库选择器对接（弹出+搜索+回填）
  - P1-006-002-003-003-003 金额计算逻辑（单价×数量+税率+折扣+合计汇总）
- P1-006-002-003-004 表单联调验证
  - P1-006-002-003-004-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-006-002-003-004-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-006-002-003-004-003 表单联调验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-006-002-004 请购单主从列表页
- P1-006-002-004-001 搜索条件区+数据表格区
  - P1-006-002-004-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-006-002-004-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-006-002-004-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-006-002-004-002 操作按钮区（新增/编辑/审核/下推采购订单/打印）
  - P1-006-002-004-002-001 顶部操作栏布局（新增/修改/删除/导出+v-permission权限控制）
  - P1-006-002-004-002-002 批量操作逻辑（多选+批量删除/审核+确认弹窗+选中校验）
    - P1-006-002-004-002-002-001 实现多选逻辑（vxe-table checkbox多选+全选+跨页选中保持+选中行数据收集）
    - P1-006-002-004-002-002-002 实现选中校验（至少选1条+已审核不可删除+草稿才可删除+不符合条件提示）
    - P1-006-002-004-002-002-003 实现批量操作确认弹窗（操作类型+影响数量提示+确认/取消按钮+二次确认）
    - P1-006-002-004-002-002-004 实现批量操作执行（批量调用API+进度提示+成功/失败统计+列表刷新）
  - P1-006-002-004-002-003 操作事件处理（按钮点击→弹窗/路由+参数传递+回调刷新）
- P1-006-002-004-003 列表页联调验证
  - P1-006-002-004-003-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-006-002-004-003-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-006-002-004-003-003 列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-006-002-005 新增/编辑请购单主从表单页
- P1-006-002-005-001 主表字段+从表明细行区
  - P1-006-002-005-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-006-002-005-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-006-002-005-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-006-002-005-002 表单联调验证
  - P1-006-002-005-002-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-006-002-005-002-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-006-002-005-002-003 表单联调验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-006-002-006 未采购清单页
- P1-006-002-006-001 查询条件区
  - P1-006-002-006-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-006-002-006-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-006-002-006-001-003 实现查询条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-006-002-006-002 查询结果表格区
  - P1-006-002-006-002-001 vxe-table列定义（字段映射+列宽+排序+对齐+固定列+显隐）
  - P1-006-002-006-002-002 数据加载与分页（API调用+分页参数+数据赋值+Loading）
  - P1-006-002-006-002-003 表格格式化渲染（字典翻译+日期+金额+状态色标+操作列按钮）
- P1-006-002-006-003 快捷操作区（下推采购订单+导出）
  - P1-006-002-006-003-001 实现导出逻辑（查询条件+API+文件流+文件名处理）
  - P1-006-002-006-003-002 导出异常处理（大数据量分页+超时+失败重试提示）
- P1-006-002-006-004 查询页联调验证
  - P1-006-002-006-004-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-006-002-006-004-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-006-002-006-004-003 查询页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-006-002-007 采购订单主从列表页
- P1-006-002-007-001 搜索条件区
  - P1-006-002-007-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-006-002-007-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-006-002-007-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-006-002-007-002 主表数据表格+从表明细区
  - P1-006-002-007-002-001 vxe-table列定义（字段映射+列宽+排序+对齐+固定列+显隐）
  - P1-006-002-007-002-002 数据加载与分页（API调用+分页参数+数据赋值+Loading）
  - P1-006-002-007-002-003 表格格式化渲染（字典翻译+日期+金额+状态色标+操作列按钮）
- P1-006-002-007-003 操作按钮区（新增/编辑/审核/变更/下推入库/打印）
  - P1-006-002-007-003-001 顶部操作栏布局（新增/修改/删除/导出+v-permission权限控制）
  - P1-006-002-007-003-002 批量操作逻辑（多选+批量删除/审核+确认弹窗+选中校验）
    - P1-006-002-007-003-002-001 实现多选逻辑（vxe-table checkbox多选+全选+跨页选中保持+选中行数据收集）
    - P1-006-002-007-003-002-002 实现选中校验（至少选1条+已审核不可删除+草稿才可删除+不符合条件提示）
    - P1-006-002-007-003-002-003 实现批量操作确认弹窗（操作类型+影响数量提示+确认/取消按钮+二次确认）
    - P1-006-002-007-003-002-004 实现批量操作执行（批量调用API+进度提示+成功/失败统计+列表刷新）
  - P1-006-002-007-003-003 操作事件处理（按钮点击→弹窗/路由+参数传递+回调刷新）
- P1-006-002-007-004 列表页联调验证
  - P1-006-002-007-004-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-006-002-007-004-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-006-002-007-004-003 列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-006-002-008 新增/编辑采购订单主从表单页
- P1-006-002-008-001 主表字段录入区
  - P1-006-002-008-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-006-002-008-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-006-002-008-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-006-002-008-002 从表明细行编辑区（商品+数量+单价+税率+金额计算）
  - P1-006-002-008-002-001 明细行vxe-table定义（列配置+可编辑列+行号+增删行+合计行）
  - P1-006-002-008-002-002 明细行编辑交互（单元格编辑+下拉+弹出选择器+行增删复制）
  - P1-006-002-008-002-003 明细行自动计算（数量×单价=金额+税率+折扣+合计汇总）
- P1-006-002-008-003 引入请购单弹窗对接+金额计算
  - P1-006-002-008-003-001 实现引入弹窗组件（el-dialog+源单列表+搜索+分页+多选）
  - P1-006-002-008-003-002 实现引入数据回填（选中源单→明细行拆分映射+金额计算+源单标记已引入）
- P1-006-002-008-004 审核/变更/流转按钮集成
  - P1-006-002-008-004-001 审核按钮组渲染（提交/审核/驳回/撤回/关闭+按状态权限显隐）
  - P1-006-002-008-004-002 审核流程对话框（审批意见+审批节点+签字+流程图）
  - P1-006-002-008-004-003 审核API调用与状态刷新（审核接口+刷新状态+操作反馈）
- P1-006-002-008-005 表单联调验证
  - P1-006-002-008-005-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-006-002-008-005-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-006-002-008-005-003 表单联调验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-006-002-009 采购订单变更记录列表页
- P1-006-002-009-001 搜索条件区+数据表格区
  - P1-006-002-009-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-006-002-009-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-006-002-009-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-006-002-009-002 列表页联调验证
  - P1-006-002-009-002-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-006-002-009-002-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-006-002-009-002-003 列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-006-002-010 未入库清单页
- P1-006-002-010-001 查询条件区+结果表格区
  - P1-006-002-010-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-006-002-010-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-006-002-010-001-003 实现查询条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-006-002-010-002 快捷操作区（下推入库单+导出）
  - P1-006-002-010-002-001 实现导出逻辑（查询条件+API+文件流+文件名处理）
  - P1-006-002-010-002-002 导出异常处理（大数据量分页+超时+失败重试提示）
- P1-006-002-010-003 查询页联调验证
  - P1-006-002-010-003-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-006-002-010-003-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-006-002-010-003-003 查询页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-006-002-011 入库单主从列表页
- P1-006-002-011-001 搜索条件区+数据表格区
  - P1-006-002-011-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-006-002-011-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-006-002-011-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-006-002-011-002 操作按钮区（新增/编辑/审核/下推对账/打印）
  - P1-006-002-011-002-001 顶部操作栏布局（新增/修改/删除/导出+v-permission权限控制）
  - P1-006-002-011-002-002 批量操作逻辑（多选+批量删除/审核+确认弹窗+选中校验）
    - P1-006-002-011-002-002-001 实现多选逻辑（vxe-table checkbox多选+全选+跨页选中保持+选中行数据收集）
    - P1-006-002-011-002-002-002 实现选中校验（至少选1条+已审核不可删除+草稿才可删除+不符合条件提示）
    - P1-006-002-011-002-002-003 实现批量操作确认弹窗（操作类型+影响数量提示+确认/取消按钮+二次确认）
    - P1-006-002-011-002-002-004 实现批量操作执行（批量调用API+进度提示+成功/失败统计+列表刷新）
  - P1-006-002-011-002-003 操作事件处理（按钮点击→弹窗/路由+参数传递+回调刷新）
- P1-006-002-011-003 列表页联调验证
  - P1-006-002-011-003-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-006-002-011-003-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-006-002-011-003-003 列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-006-002-012 新增/编辑入库单主从表单页
- P1-006-002-012-001 主表字段录入区
  - P1-006-002-012-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-006-002-012-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-006-002-012-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-006-002-012-002 从表明细行编辑区
  - P1-006-002-012-002-001 明细行vxe-table定义（列配置+可编辑列+行号+增删行+合计行）
  - P1-006-002-012-002-002 明细行编辑交互（单元格编辑+下拉+弹出选择器+行增删复制）
  - P1-006-002-012-002-003 明细行自动计算（数量×单价=金额+税率+折扣+合计汇总）
- P1-006-002-012-003 库位/批次/序列号子表录入区
  - P1-006-002-012-003-001 库位子表vxe-table定义（仓库/库位/数量+可编辑+行操作）
  - P1-006-002-012-003-002 库位弹窗选择器对接（仓库选择→库位联动→数量分配→校验）
- P1-006-002-012-004 引入采购订单弹窗对接
  - P1-006-002-012-004-001 实现引入弹窗组件（el-dialog+源单列表+搜索+分页+多选）
  - P1-006-002-012-004-002 实现引入数据回填（选中源单→明细行拆分映射+金额计算+源单标记已引入）
- P1-006-002-012-005 表单联调验证
  - P1-006-002-012-005-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-006-002-012-005-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-006-002-012-005-003 表单联调验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-006-002-013 入库退货单主从列表页
- P1-006-002-013-001 搜索条件区+数据表格区
  - P1-006-002-013-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-006-002-013-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-006-002-013-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-006-002-013-002 列表页联调验证
  - P1-006-002-013-002-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-006-002-013-002-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-006-002-013-002-003 列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-006-002-014 新增/编辑入库退货单主从表单页
- P1-006-002-014-001 主表字段+从表明细区（引入入库单明细+退货数量+原因）
  - P1-006-002-014-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-006-002-014-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-006-002-014-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-006-002-014-002 引入入库单弹窗对接
  - P1-006-002-014-002-001 实现引入弹窗组件（el-dialog+源单列表+搜索+分页+多选）
  - P1-006-002-014-002-002 实现引入数据回填（选中源单→明细行拆分映射+金额计算+源单标记已引入）
- P1-006-002-014-003 表单联调验证
  - P1-006-002-014-003-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-006-002-014-003-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-006-002-014-003-003 表单联调验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-006-002-015 未对账清单页
- P1-006-002-015-001 查询条件区+结果表格区
  - P1-006-002-015-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-006-002-015-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-006-002-015-001-003 实现查询条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-006-002-015-002 快捷操作区（下推对账单+导出）
  - P1-006-002-015-002-001 实现导出逻辑（查询条件+API+文件流+文件名处理）
  - P1-006-002-015-002-002 导出异常处理（大数据量分页+超时+失败重试提示）
- P1-006-002-015-003 查询页联调验证
  - P1-006-002-015-003-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-006-002-015-003-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-006-002-015-003-003 查询页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-006-002-016 入库对账单主从列表页
- P1-006-002-016-001 搜索条件区+数据表格区
  - P1-006-002-016-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-006-002-016-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-006-002-016-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-006-002-016-002 列表页联调验证
  - P1-006-002-016-002-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-006-002-016-002-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-006-002-016-002-003 列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-006-002-017 新增/编辑入库对账单主从表单页
- P1-006-002-017-001 主表字段+从表明细区（引入入库单明细+对账数量+金额）
  - P1-006-002-017-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-006-002-017-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-006-002-017-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-006-002-017-002 引入入库单弹窗对接
  - P1-006-002-017-002-001 实现引入弹窗组件（el-dialog+源单列表+搜索+分页+多选）
  - P1-006-002-017-002-002 实现引入数据回填（选中源单→明细行拆分映射+金额计算+源单标记已引入）
- P1-006-002-017-003 表单联调验证
  - P1-006-002-017-003-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-006-002-017-003-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-006-002-017-003-003 表单联调验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-006-002-018 采购统计页
- P1-006-002-018-001 报表条件区
  - P1-006-002-018-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-006-002-018-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-006-002-018-001-003 实现报表条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-006-002-018-002 报表数据表格/图表区
  - P1-006-002-018-002-001 vxe-table列定义（字段映射+列宽+排序+对齐+固定列+显隐）
  - P1-006-002-018-002-002 数据加载与分页（API调用+分页参数+数据赋值+Loading）
  - P1-006-002-018-002-003 表格格式化渲染（字典翻译+日期+金额+状态色标+操作列按钮）
- P1-006-002-018-003 导出+打印区
  - P1-006-002-018-003-001 实现导出逻辑（查询条件+API+文件流+文件名处理）
  - P1-006-002-018-003-002 导出异常处理（大数据量分页+超时+失败重试提示）
- P1-006-002-018-004 报表页联调验证
  - P1-006-002-018-004-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-006-002-018-004-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-006-002-018-004-003 报表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）
    - P1-006-002-008 应收/应付产生依据参数配置（参数键：ar_ap_generate_source+选项：按出库单产生/按销售开票产生/按验收单产生+按租户配置+产生时机：审核时自动产生/手动产生+参数变更影响说明+参数校验）

---

## P1-007 库存管理模块开发

> **依赖**：P0-007（商品）、P0-010（仓库库位）、P1-005（销售出库）、P1-006（采购入库）  
> **概述**：完成库存全功能：其他出入库、盘点、调拨、报损报溢、组装拆卸、库存查询、预警、成本查询

### P1-007-001 库存引擎后端开发

#### P1-007-001-001 库存出入库引擎核心
- P1-007-001-001-001 库存增减原子操作Service（inv_stock表行级锁+数量增减+版本号乐观锁）
  - P1-007-001-001-001-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-007-001-001-001-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-007-001-001-001-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-007-001-001-002 可用量计算Service（可用量=现存量-占用量+在途量，按仓库+商品+库位维度计算）
  - P1-007-001-001-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-007-001-001-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-007-001-001-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-007-001-001-003 占用量计算Service（待审核出库单占用+销售订单预留占用）
  - P1-007-001-001-003-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-007-001-001-003-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-007-001-001-003-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-007-001-001-004 在途量计算Service（在途采购+在途调拨的在途数量汇总）
  - P1-007-001-001-004-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-007-001-001-004-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-007-001-001-004-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-007-001-001-005 库存实时表更新Service（出入库后异步刷新inv_stock聚合表）
  - P1-007-001-001-005-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-007-001-001-005-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-007-001-001-005-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）

#### P1-007-001-002 库存成本计算引擎
- P1-007-001-002-001 移动加权平均成本计算Service（入库时重新加权平均单价+出库时按当前均价出库）
  - P1-007-001-002-001-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-007-001-002-001-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-007-001-002-001-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-007-001-002-002 先进先出成本计算Service（出库时按批次入库时间先后顺序匹配成本）
  - P1-007-001-002-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-007-001-002-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-007-001-002-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-007-001-002-003 手工指定成本Service（出库时人工指定成本价+成本录入校验）
  - P1-007-001-002-003-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-007-001-002-003-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-007-001-002-003-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-007-001-002-004 成本方法切换策略Service（系统参数控制全局成本方法+按商品覆盖成本方法）
  - P1-007-001-002-004-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-007-001-002-004-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-007-001-002-004-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）

#### P1-007-001-003 批次/序列号更新引擎
- P1-007-001-003-001 批次记录自动创建/更新Service（入库时自动创建批次+出库时扣减批次数量）
  - P1-007-001-003-001-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-007-001-003-001-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-007-001-003-001-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-007-001-003-002 序列号状态变更Service（入库时序列号状态→在库+出库时→已出库）
  - P1-007-001-003-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-007-001-003-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-007-001-003-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-007-001-003-003 批次有效期预警Service（近效期/过效期批次自动预警+预警通知推送）
  - P1-007-001-003-003-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-007-001-003-003-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-007-001-003-003-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）

#### P1-007-001-004 库存预警引擎
- P1-007-001-004-001 安全库存预警检测Service（低于安全库存自动生成预警记录+预警级别计算）
  - P1-007-001-004-001-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-007-001-004-001-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-007-001-004-001-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-007-001-004-002 预警通知推送Service（预警记录→消息中心通知+预警列表接口）
  - P1-007-001-004-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-007-001-004-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-007-001-004-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）

### P1-007-002 库存管理后端开发

#### P1-007-002-001 其他出库单CRUD接口
- P1-007-002-001-001 其他出库单Entity/DTO/VO定义
  - P1-007-002-001-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-007-002-001-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-007-002-001-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-007-002-001-002 其他出库单Mapper开发
  - P1-007-002-001-002-001 定义Mapper接口（extends BaseMapperX+@Mapper+自定义方法声明）
  - P1-007-002-001-002-002 编写XML映射文件（resultMap+自定义SQL片段+动态条件）
- P1-007-002-001-003 其他出库单Service开发（主从CRUD+库位/批次/序列号三表处理+库存扣减+审核流转）
  - P1-007-002-001-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-007-002-001-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-007-002-001-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-007-002-001-004 其他出库单Controller开发
  - P1-007-002-001-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-007-002-001-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-007-002-001-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-007-002-002 其他入库单CRUD接口
- P1-007-002-002-001 其他入库单Entity/DTO/VO定义
  - P1-007-002-002-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-007-002-002-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-007-002-002-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-007-002-002-002 其他入库单Mapper开发
  - P1-007-002-002-002-001 定义Mapper接口（extends BaseMapperX+@Mapper+自定义方法声明）
  - P1-007-002-002-002-002 编写XML映射文件（resultMap+自定义SQL片段+动态条件）
- P1-007-002-002-003 其他入库单Service开发（主从CRUD+库位/批次/序列号三表处理+库存增加+审核流转）
  - P1-007-002-002-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-007-002-002-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-007-002-002-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-007-002-002-004 其他入库单Controller开发
  - P1-007-002-002-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-007-002-002-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-007-002-002-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-007-002-003 盘点单CRUD接口
- P1-007-002-003-001 盘点单Entity/DTO/VO定义
  - P1-007-002-003-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-007-002-003-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-007-002-003-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-007-002-003-002 盘点单Mapper开发
  - P1-007-002-003-002-001 定义Mapper接口（extends BaseMapperX+@Mapper+自定义方法声明）
  - P1-007-002-003-002-002 编写XML映射文件（resultMap+自定义SQL片段+动态条件）
- P1-007-002-003-003 盘点单Service开发（主从CRUD+盘盈盘亏计算+盘盈自动生成报溢单+盘亏自动生成报损单）
  - P1-007-002-003-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-007-002-003-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-007-002-003-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-007-002-003-004 盘点单Controller开发
  - P1-007-002-003-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-007-002-003-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-007-002-003-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-007-002-004 调拨单CRUD接口
- P1-007-002-004-001 调拨单Entity/DTO/VO定义
  - P1-007-002-004-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-007-002-004-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-007-002-004-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-007-002-004-002 调拨单Mapper开发
  - P1-007-002-004-002-001 定义Mapper接口（extends BaseMapperX+@Mapper+自定义方法声明）
  - P1-007-002-004-002-002 编写XML映射文件（resultMap+自定义SQL片段+动态条件）
- P1-007-002-004-003 调拨单Service开发（主从CRUD+调出仓扣减+调入仓增加+审核流转）
  - P1-007-002-004-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-007-002-004-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-007-002-004-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-007-002-004-004 调拨单Controller开发
  - P1-007-002-004-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-007-002-004-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-007-002-004-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-007-002-005 报损单CRUD接口
- P1-007-002-005-001 报损单Entity/DTO/VO定义+Mapper开发
  - P1-007-002-005-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-007-002-005-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-007-002-005-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-007-002-005-002 报损单Service开发（主从CRUD+库存扣减+审核流转）
  - P1-007-002-005-002-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-007-002-005-002-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-007-002-005-002-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-007-002-005-003 报损单Controller开发
  - P1-007-002-005-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-007-002-005-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-007-002-005-003-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-007-002-006 报溢单CRUD接口
- P1-007-002-006-001 报溢单Entity/DTO/VO定义+Mapper开发
  - P1-007-002-006-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-007-002-006-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-007-002-006-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-007-002-006-002 报溢单Service开发（主从CRUD+库存增加+审核流转）
  - P1-007-002-006-002-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-007-002-006-002-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-007-002-006-002-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-007-002-006-003 报溢单Controller开发
  - P1-007-002-006-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-007-002-006-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-007-002-006-003-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-007-002-007 组装单CRUD接口
- P1-007-002-007-001 组装单Entity/DTO/VO定义
  - P1-007-002-007-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-007-002-007-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-007-002-007-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-007-002-007-002 组装单Mapper开发
  - P1-007-002-007-002-001 定义Mapper接口（extends BaseMapperX+@Mapper+自定义方法声明）
  - P1-007-002-007-002-002 编写XML映射文件（resultMap+自定义SQL片段+动态条件）
- P1-007-002-007-003 组装单Service开发（主从CRUD+子件扣减+组合件增加+BOM校验）
  - P1-007-002-007-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-007-002-007-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-007-002-007-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-007-002-007-004 组装单Controller开发
  - P1-007-002-007-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-007-002-007-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-007-002-007-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-007-002-008 拆卸单CRUD接口
- P1-007-002-008-001 拆卸单Entity/DTO/VO定义
  - P1-007-002-008-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-007-002-008-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-007-002-008-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-007-002-008-002 拆卸单Mapper开发
  - P1-007-002-008-002-001 定义Mapper接口（extends BaseMapperX+@Mapper+自定义方法声明）
  - P1-007-002-008-002-002 编写XML映射文件（resultMap+自定义SQL片段+动态条件）
- P1-007-002-008-003 拆卸单Service开发（主从CRUD+组合件扣减+子件增加+BOM校验）
  - P1-007-002-008-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-007-002-008-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-007-002-008-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-007-002-008-004 拆卸单Controller开发
  - P1-007-002-008-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-007-002-008-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-007-002-008-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-007-002-009 库存查询接口
- P1-007-002-009-001 库存查询SQL编写（inv_stock聚合查询+多维度筛选）
  - P1-007-002-009-001-001 编写库存查询SQL（inv_stock聚合查询+多维度筛选+库存状态计算+分页支持）
  - P1-007-002-009-001-002 验证SQL执行（聚合正确+筛选高效+库存状态准确）
  - P1-007-002-009-001-003 验证SQL执行（数据正确+性能合理+索引命中+空结果兜底）
- P1-007-002-009-002 库存查询Service开发（可用量+现存量+占用量+在途量展示）
  - P1-007-002-009-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-007-002-009-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-007-002-009-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-007-002-009-003 库存查询Controller开发
  - P1-007-002-009-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-007-002-009-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-007-002-009-003-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-007-002-010 库位库存查询接口
- P1-007-002-010-001 库位库存SQL+Service+Controller开发
  - P1-007-002-010-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-007-002-010-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-007-002-010-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-007-002-011 批次跟踪查询接口
- P1-007-002-011-001 批次跟踪SQL编写（批次出入库记录+批次流转链路）
  - P1-007-002-011-001-001 编写批次跟踪SQL（批次出入库记录+批次流转链路+按批次号查询）
  - P1-007-002-011-001-002 验证SQL执行（链路完整+排序正确+性能合理）
  - P1-007-002-011-001-003 验证SQL执行（数据正确+性能合理+索引命中+空结果兜底）
- P1-007-002-011-002 批次跟踪Service+Controller开发
  - P1-007-002-011-002-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-007-002-011-002-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-007-002-011-002-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-007-002-012 序列号跟踪查询接口
- P1-007-002-012-001 序列号跟踪SQL编写（序列号出入库记录+序列号流转链路）
  - P1-007-002-012-001-001 编写序列号跟踪SQL（序列号出入库记录+序列号流转链路+按序列号查询）
  - P1-007-002-012-001-002 验证SQL执行（链路完整+排序正确+性能合理）
  - P1-007-002-012-001-003 验证SQL执行（数据正确+性能合理+索引命中+空结果兜底）
- P1-007-002-012-002 序列号跟踪Service+Controller开发
  - P1-007-002-012-002-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-007-002-012-002-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-007-002-012-002-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-007-002-013 库存预警接口
- P1-007-002-013-001 库存预警列表SQL+Service+Controller开发
  - P1-007-002-013-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-007-002-013-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-007-002-013-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-007-002-014 库存成本查询接口
- P1-007-002-014-001 库存成本SQL编写（商品库存成本+成本明细+成本方法标识）
  - P1-007-002-014-001-001 编写库存成本SQL（商品库存成本+成本明细+成本方法标识+成本调整记录）
  - P1-007-002-014-001-002 验证SQL执行（成本计算正确+方法标识一致+调整记录完整）
  - P1-007-002-014-001-003 验证SQL执行（数据正确+性能合理+索引命中+空结果兜底）
- P1-007-002-014-002 库存成本Service+Controller开发
  - P1-007-002-014-002-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-007-002-014-002-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-007-002-014-002-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-007-002-015 库存管理工作台聚合数据接口
- P1-007-002-015-001 工作台SQL+Service+Controller开发
  - P1-007-002-015-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-007-002-015-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-007-002-015-001-003 补充接口文档注解（@Operation+@Schema+示例值）

### P1-007-003 库存管理前端页面

#### P1-007-003-001 库存管理工作台
- P1-007-003-001-001 工作台KPI卡片区（总库存金额/本月出入库/预警数/待盘点）
  - P1-007-003-001-001-001 KPI卡片组件开发（数值展示+同比环比+趋势mini图+点击下钻路由）
  - P1-007-003-001-001-002 KPI数据API对接（统计查询API+数据计算+Loading+空数据处理）
- P1-007-003-001-002 工作台图表区（库存趋势+出入库对比+预警分布）
  - P1-007-003-001-002-001 ECharts图表组件开发（折线图/柱状图/饼图+响应式+交互事件+图例）
  - P1-007-003-001-002-002 图表数据API对接（统计API+数据转换+时间范围切换+自动刷新）
- P1-007-003-001-003 工作台待办区+联调验证
  - P1-007-003-001-003-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-007-003-001-003-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-007-003-001-003-003 工作台待办区异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-007-003-002 其他出库单主从列表页
- P1-007-003-002-001 搜索条件区+数据表格区+操作按钮区
  - P1-007-003-002-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-007-003-002-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-007-003-002-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-007-003-002-002 列表页联调验证
  - P1-007-003-002-002-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-007-003-002-002-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-007-003-002-002-003 列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-007-003-003 新增/编辑其他出库单主从表单页
- P1-007-003-003-001 主表字段+从表明细+库位/批次/序列号子表区
  - P1-007-003-003-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-007-003-003-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-007-003-003-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-007-003-003-002 表单联调验证
  - P1-007-003-003-002-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-007-003-003-002-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-007-003-003-002-003 表单联调验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-007-003-004 其他入库单主从列表页
- P1-007-003-004-001 搜索条件区+数据表格区+操作按钮区
  - P1-007-003-004-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-007-003-004-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-007-003-004-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-007-003-004-002 列表页联调验证
  - P1-007-003-004-002-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-007-003-004-002-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-007-003-004-002-003 列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-007-003-005 新增/编辑其他入库单主从表单页
- P1-007-003-005-001 主表字段+从表明细+库位/批次/序列号子表区
  - P1-007-003-005-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-007-003-005-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-007-003-005-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-007-003-005-002 表单联调验证
  - P1-007-003-005-002-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-007-003-005-002-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-007-003-005-002-003 表单联调验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-007-003-006 盘点单主从列表页
- P1-007-003-006-001 搜索条件区+数据表格区+操作按钮区（含生成报损/报溢单按钮）
  - P1-007-003-006-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-007-003-006-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-007-003-006-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-007-003-006-002 列表页联调验证
  - P1-007-003-006-002-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-007-003-006-002-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-007-003-006-002-003 列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-007-003-007 新增/编辑盘点单主从表单页
- P1-007-003-007-001 主表字段+从表明细区（系统库存+实盘数量+盘盈盘亏自动计算）
  - P1-007-003-007-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-007-003-007-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-007-003-007-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-007-003-007-002 表单联调验证
  - P1-007-003-007-002-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-007-003-007-002-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-007-003-007-002-003 表单联调验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-007-003-008 调拨单主从列表页
- P1-007-003-008-001 搜索条件区+数据表格区+操作按钮区
  - P1-007-003-008-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-007-003-008-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-007-003-008-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-007-003-008-002 列表页联调验证
  - P1-007-003-008-002-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-007-003-008-002-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-007-003-008-002-003 列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-007-003-009 新增/编辑调拨单主从表单页
- P1-007-003-009-001 主表字段+从表明细区（调出仓+调入仓+调拨数量）
  - P1-007-003-009-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-007-003-009-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-007-003-009-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-007-003-009-002 表单联调验证
  - P1-007-003-009-002-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-007-003-009-002-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-007-003-009-002-003 表单联调验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-007-003-010 报损单主从列表页
- P1-007-003-010-001 搜索条件区+数据表格区+列表页联调验证
  - P1-007-003-010-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-007-003-010-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-007-003-010-001-003 实现搜索条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-007-003-010a 新增/编辑报损单主从表单页（P06：主表单+明细从表+库位/批次/序列号子表）
- P1-007-003-010a-001 主表字段+从表明细区（商品选择+仓库选择+库位/批次/序列号+报损数量+报损原因）
  - P1-007-003-010a-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-007-003-010a-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-007-003-010a-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-007-003-010a-002 表单联调验证
  - P1-007-003-010a-002-001 新增/编辑流程联调+库存扣减验证+审核流转

#### P1-007-003-011 报溢单主从列表页
- P1-007-003-011-001 搜索条件区+数据表格区+列表页联调验证
  - P1-007-003-011-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-007-003-011-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-007-003-011-001-003 实现搜索条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-007-003-011a 新增/编辑报溢单主从表单页（P06：主表单+明细从表+库位/批次/序列号子表）
- P1-007-003-011a-001 主表字段+从表明细区（商品选择+仓库选择+库位/批次/序列号+报溢数量+报溢原因）
  - P1-007-003-011a-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-007-003-011a-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-007-003-011a-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-007-003-011a-002 表单联调验证
  - P1-007-003-011a-002-001 新增/编辑流程联调+库存增加验证+审核流转

#### P1-007-003-012 组装单主从列表页
- P1-007-003-012-001 搜索条件区+数据表格区+列表页联调验证
  - P1-007-003-012-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-007-003-012-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-007-003-012-001-003 实现搜索条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-007-003-012a 新增/编辑组装单主从表单页（P06：主表单+子件明细区+组合件明细区）
- P1-007-003-012a-001 主表字段+子件从表区（BOM自动展开+子件仓库/库位/批次+扣减数量）+组合件从表区（组合件仓库/库位+入库数量）
  - P1-007-003-012a-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-007-003-012a-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-007-003-012a-001-003 主表联动逻辑（BOM选择→自动展开子件+数量自动计算+库存校验）
- P1-007-003-012a-002 表单联调验证
  - P1-007-003-012a-002-001 新增/编辑流程联调+BOM展开验证+子件扣减+组合件增加+审核流转

#### P1-007-003-013 拆卸单主从列表页
- P1-007-003-013-001 搜索条件区+数据表格区+列表页联调验证
  - P1-007-003-013-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-007-003-013-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-007-003-013-001-003 实现搜索条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-007-003-013a 新增/编辑拆卸单主从表单页（P06：主表单+组合件明细区+子件明细区）
- P1-007-003-013a-001 主表字段+组合件从表区（组合件仓库/库位/批次+拆卸数量）+子件从表区（BOM自动展开+子件入库仓库/库位+数量）
  - P1-007-003-013a-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-007-003-013a-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-007-003-013a-001-003 主表联动逻辑（BOM选择→自动展开子件+数量自动计算+库存校验）
- P1-007-003-013a-002 表单联调验证
  - P1-007-003-013a-002-001 新增/编辑流程联调+BOM展开验证+组合件扣减+子件增加+审核流转

#### P1-007-003-014 库存查询页
- P1-007-003-014-001 查询条件区（仓库+商品+分类+品牌多维度筛选）
  - P1-007-003-014-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-007-003-014-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-007-003-014-001-003 实现查询条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-007-003-014-002 查询结果表格区（现存量+可用量+占用量+在途量）
  - P1-007-003-014-002-001 vxe-table列定义（字段映射+列宽+排序+对齐+固定列+显隐）
  - P1-007-003-014-002-002 数据加载与分页（API调用+分页参数+数据赋值+Loading）
  - P1-007-003-014-002-003 表格格式化渲染（字典翻译+日期+金额+状态色标+操作列按钮）
- P1-007-003-014-003 查询页联调验证
  - P1-007-003-014-003-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-007-003-014-003-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-007-003-014-003-003 查询页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-007-003-015 库位库存查询页
- P1-007-003-015-001 查询条件区+结果表格区+联调验证
  - P1-007-003-015-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-007-003-015-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-007-003-015-001-003 实现查询条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-007-003-016 批次跟踪查询页
- P1-007-003-016-001 查询条件区+结果表格区（批次流转链路）+联调验证
  - P1-007-003-016-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-007-003-016-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-007-003-016-001-003 实现查询条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-007-003-017 序列号跟踪查询页
- P1-007-003-017-001 查询条件区+结果表格区（序列号流转链路）+联调验证
  - P1-007-003-017-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-007-003-017-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-007-003-017-001-003 实现查询条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-007-003-018 库存预警页
- P1-007-003-018-001 预警列表+预警级别标识+快捷补货操作+联调验证
  - P1-007-003-018-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-007-003-018-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-007-003-018-001-003 预警列表异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-007-003-019 库存成本查询页
- P1-007-003-019-001 查询条件区+成本表格区（成本价+成本方法标识）+联调验证
  - P1-007-003-019-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-007-003-019-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-007-003-019-001-003 实现查询条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

---

## P1-008 生产管理模块开发

> **依赖**：P0-007（商品含BOM/工序）、P0-008（客户）、P0-010（仓库）、P1-007（库存引擎）  
> **概述**：完成生产全链路：生产通知→MRP→领料→入库→工序移交→进度查询→统计

### P1-008-001 生产管理后端开发

#### P1-008-001-001 未生产清单聚合查询接口
- P1-008-001-001-001 未生产SQL编写+Service+Controller开发
  - P1-008-001-001-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-001-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-001-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-008-001-002 生产通知单CRUD接口
- P1-008-001-002-001 生产通知单Entity/DTO/VO定义
  - P1-008-001-002-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-008-001-002-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-008-001-002-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-008-001-002-002 生产通知单Mapper开发
  - P1-008-001-002-002-001 定义Mapper接口（extends BaseMapperX+@Mapper+自定义方法声明）
  - P1-008-001-002-002-002 编写XML映射文件（resultMap+自定义SQL片段+动态条件）
- P1-008-001-002-003 生产通知单Service开发（主从CRUD+BOM自动展开+编码自动生成+审核流转）
  - P1-008-001-002-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-008-001-002-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-008-001-002-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-008-001-002-004 生产通知单Controller开发
  - P1-008-001-002-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-002-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-002-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-008-001-003 生产通知变更记录接口
- P1-008-001-003-001 变更Entity/Mapper/Service/Controller开发
  - P1-008-001-003-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-003-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-003-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-008-001-004 MRP运算接口
- P1-008-001-004-001 MRP需求分析Service（销售订单+生产通知需求汇总）
  - P1-008-001-004-001-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-008-001-004-001-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-008-001-004-001-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-008-001-004-002 BOM展开Service（多级BOM递归展开+用量计算）
  - P1-008-001-004-002-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-008-001-004-002-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-008-001-004-002-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-008-001-004-003 库存可用量检查Service（现有库存+在途+占用→净需求计算）
  - P1-008-001-004-003-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-008-001-004-003-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-008-001-004-003-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-008-001-004-004 计划订单建议Service（净需求→采购建议+生产建议）
  - P1-008-001-004-004-001 定义Service接口（extends IServiceX+业务方法声明）
  - P1-008-001-004-004-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P1-008-001-004-004-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）
- P1-008-001-004-005 MRP运算Controller+运算结果存储
  - P1-008-001-004-005-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-004-005-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-004-005-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-008-001-005 生产看板数据接口
- P1-008-001-005-001 看板SQL编写+Service+Controller开发
  - P1-008-001-005-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-005-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-005-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-008-001-006 生产未领料清单聚合查询接口
- P1-008-001-006-001 未领料SQL+Service+Controller开发
  - P1-008-001-006-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-006-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-006-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-008-001-007 生产领料单CRUD接口
- P1-008-001-007-001 生产领料单Entity/DTO/VO定义
  - P1-008-001-007-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P1-008-001-007-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P1-008-001-007-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P1-008-001-007-002 生产领料单Mapper开发
  - P1-008-001-007-002-001 定义Mapper接口（extends BaseMapperX+@Mapper+自定义方法声明）
  - P1-008-001-007-002-002 编写XML映射文件（resultMap+自定义SQL片段+动态条件）
- P1-008-001-007-003 生产领料单Service开发（主从CRUD+库存扣减+BOM用量校验+超领校验+库位/批次/序列号三表处理）
  - P1-008-001-007-003-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P1-008-001-007-003-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P1-008-001-007-003-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P1-008-001-007-004 生产领料单Controller开发
  - P1-008-001-007-004-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-007-004-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-007-004-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-008-001-008 生产超领单CRUD接口
- P1-008-001-008-001 超领单Entity/Mapper/Service/Controller开发（超领原因必填+库存扣减）
  - P1-008-001-008-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-008-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-008-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-008-001-009 生产退料单CRUD接口
- P1-008-001-009-001 退料单Entity/Mapper/Service/Controller开发（库存增加）
  - P1-008-001-009-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-009-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-009-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-008-001-010 生产未入库清单聚合查询接口
- P1-008-001-010-001 未入库SQL+Service+Controller开发
  - P1-008-001-010-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-010-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-010-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-008-001-011 生产入库单CRUD接口
- P1-008-001-011-001 生产入库单Entity/Mapper/Service/Controller开发（库存增加）
  - P1-008-001-011-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-011-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-011-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-008-001-012 生产退库单CRUD接口
- P1-008-001-012-001 生产退库单Entity/Mapper/Service/Controller开发（库存扣减）
  - P1-008-001-012-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-012-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-012-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-008-001-013 来料入库单CRUD接口
- P1-008-001-013-001 来料入库单Entity/Mapper/Service/Controller开发
  - P1-008-001-013-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-013-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-013-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-008-001-014 来料退库单CRUD接口
- P1-008-001-014-001 来料退库单Entity/Mapper/Service/Controller开发
  - P1-008-001-014-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-014-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-014-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-008-001-015 生产工序移交单CRUD接口
- P1-008-001-015-001 工序移交单Entity/Mapper/Service/Controller开发
  - P1-008-001-015-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-015-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-015-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-008-001-016 生产工序接收单CRUD接口
- P1-008-001-016-001 工序接收单Entity/Mapper/Service/Controller开发
  - P1-008-001-016-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-016-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-016-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-008-001-017 生产进度查询接口
- P1-008-001-017-001 生产进度SQL编写（按通知单维度：完成率+工序进度+领料进度+入库进度）
  - P1-008-001-017-001-001 编写生产进度SQL（按通知单维度：完成率+工序进度+领料进度+入库进度+逾期标记）
  - P1-008-001-017-001-002 验证SQL执行（进度计算准确+逾期标记正确+性能合理）
  - P1-008-001-017-001-003 验证SQL执行（数据正确+性能合理+索引命中+空结果兜底）
- P1-008-001-017-002 生产进度Service+Controller开发
  - P1-008-001-017-002-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-017-002-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-017-002-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-008-001-018 生产统计接口
- P1-008-001-018-001 生产统计SQL+Service+Controller开发
  - P1-008-001-018-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-018-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-018-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P1-008-001-019 生产管理工作台聚合数据接口
- P1-008-001-019-001 工作台SQL+Service+Controller开发
  - P1-008-001-019-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P1-008-001-019-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P1-008-001-019-001-003 补充接口文档注解（@Operation+@Schema+示例值）

### P1-008-002 生产管理前端页面

#### P1-008-002-001 生产管理工作台
- P1-008-002-001-001 工作台KPI卡片区+图表区+待办区+联调验证
  - P1-008-002-001-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-008-002-001-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-008-002-001-001-003 工作台异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-008-002-002 未生产清单页
- P1-008-002-002-001 查询条件区+结果表格区+快捷下推生产通知+联调验证
  - P1-008-002-002-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-008-002-002-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-008-002-002-001-003 实现查询条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-008-002-003 生产通知单主从列表页
- P1-008-002-003-001 搜索条件区+主表数据表格+从表明细区+操作按钮区
  - P1-008-002-003-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-008-002-003-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-008-002-003-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
- P1-008-002-003-002 列表页联调验证
  - P1-008-002-003-002-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-008-002-003-002-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-008-002-003-002-003 列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-008-002-004 新增/编辑生产通知单主从表单页
- P1-008-002-004-001 主表字段录入区
  - P1-008-002-004-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-008-002-004-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-008-002-004-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-008-002-004-002 BOM展开明细区（选择产品后自动展开BOM子件+用量+预计用料）
  - P1-008-002-004-002-001 实现BOM展开逻辑（选择产品→API查询BOM→递归展开子件+用量+层级缩进）
  - P1-008-002-004-002-002 实现预计用料计算（BOM子件用量×生产数量→预计用料合计+替代料展示）
- P1-008-002-004-003 选择器对接+审核/流转按钮集成+表单联调验证
  - P1-008-002-004-003-001 商品选择器对接（弹出列表+搜索+多选+回填字段）
  - P1-008-002-004-003-002 客户/供应商/仓库选择器对接（弹出+搜索+回填）
  - P1-008-002-004-003-003 金额计算逻辑（单价×数量+税率+折扣+合计汇总）

#### P1-008-002-005 生产通知变更记录列表页
- P1-008-002-005-001 搜索条件区+数据表格区+联调验证
  - P1-008-002-005-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-008-002-005-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-008-002-005-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-008-002-006 MRP运算页
- P1-008-002-006-001 MRP参数配置区（运算范围+时间范围+忽略安全库存等参数）
  - P1-008-002-006-001-001 实现MRP参数配置区（运算范围选择+时间范围+忽略安全库存+批量规划参数）
    - P1-008-002-006-001-001-001 实现运算范围选择（指定仓库/物料分类/物料范围+全选+反选+模糊搜索）
    - P1-008-002-006-001-001-002 实现时间范围配置（计划开始日期+计划结束日期+日期范围校验+默认当月）
    - P1-008-002-006-001-001-003 实现高级参数（忽略安全库存+批量规划+最小采购量+采购提前期+允许提前期+库存扣减方式）
  - P1-008-002-006-001-002 验证功能（核心用例通过+边界场景+异常降级）
  - P1-008-002-006-001-003 验证功能完整性（核心用例+边界场景+集成联调+异常降级）
- P1-008-002-006-002 MRP运算执行区（运算按钮+进度条+运算结果展示）
  - P1-008-002-006-002-001 实现MRP运算触发（运算按钮+参数校验+后端运算API调用+进度轮询）
  - P1-008-002-006-002-002 实现运算进度展示（进度条+阶段提示+运算日志+取消运算）
- P1-008-002-006-003 MRP结果区（采购建议列表+生产建议列表+快捷下推）
  - P1-008-002-006-003-001 实现结果列表展示（采购建议+生产建议双Tab+数据表格+分页）
  - P1-008-002-006-003-002 实现快捷下推（选中建议→下推采购订单/生产工单+参数映射+批量下推）
- P1-008-002-006-004 MRP运算页联调验证
  - P1-008-002-006-004-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-008-002-006-004-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-008-002-006-004-003 运算页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-008-002-007 生产看板页
- P1-008-002-007-001 看板卡片区（在制数/待领料/待入库/逾期数）
  - P1-008-002-007-001-001 看板卡片组件开发（在制数/待领料/待入库/逾期数+颜色标识+点击筛选）
  - P1-008-002-007-001-002 看板数据API对接（实时统计API+定时刷新+数据为0隐藏+异常告警）
- P1-008-002-007-002 看板图表区（工序进度甘特图+产量趋势图）
  - P1-008-002-007-002-001 ECharts图表组件开发（折线图/柱状图/饼图+响应式+交互事件+图例）
  - P1-008-002-007-002-002 图表数据API对接（统计API+数据转换+时间范围切换+自动刷新）
- P1-008-002-007-003 看板联调验证
  - P1-008-002-007-003-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-008-002-007-003-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-008-002-007-003-003 看板联调验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-008-002-008 未领料清单页
- P1-008-002-008-001 查询条件区+结果表格区+快捷下推领料单+联调验证
  - P1-008-002-008-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-008-002-008-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-008-002-008-001-003 实现查询条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-008-002-009 生产领料单主从列表页
- P1-008-002-009-001 搜索条件区+数据表格区+操作按钮区+联调验证
  - P1-008-002-009-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-008-002-009-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-008-002-009-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-008-002-010 新增/编辑生产领料单主从表单页
- P1-008-002-010-001 主表字段+从表明细区（引入生产通知+BOM用量校验+库位/批次/序列号录入）
  - P1-008-002-010-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P1-008-002-010-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P1-008-002-010-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）
- P1-008-002-010-002 表单联调验证
  - P1-008-002-010-002-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P1-008-002-010-002-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P1-008-002-010-002-003 表单联调验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P1-008-002-011 生产超领单主从列表页
- P1-008-002-011-001 搜索条件区+数据表格区+联调验证
  - P1-008-002-011-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-008-002-011-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-008-002-011-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-008-002-012 生产退料单主从列表页
- P1-008-002-012-001 搜索条件区+数据表格区+联调验证
  - P1-008-002-012-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-008-002-012-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-008-002-012-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-008-002-013 未入库清单页
- P1-008-002-013-001 查询条件区+结果表格区+快捷下推入库单+联调验证
  - P1-008-002-013-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-008-002-013-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-008-002-013-001-003 实现查询条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-008-002-014 生产入库单主从列表页
- P1-008-002-014-001 搜索条件区+数据表格区+操作按钮区+联调验证
  - P1-008-002-014-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-008-002-014-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-008-002-014-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-008-002-015 生产退库单主从列表页
- P1-008-002-015-001 搜索条件区+数据表格区+联调验证
  - P1-008-002-015-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-008-002-015-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-008-002-015-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-008-002-016 来料入库单主从列表页
- P1-008-002-016-001 搜索条件区+数据表格区+联调验证
  - P1-008-002-016-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-008-002-016-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-008-002-016-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-008-002-017 来料退库单主从列表页
- P1-008-002-017-001 搜索条件区+数据表格区+联调验证
  - P1-008-002-017-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-008-002-017-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-008-002-017-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-008-002-018 生产工序移交单列表页
- P1-008-002-018-001 搜索条件区+数据表格区+联调验证
  - P1-008-002-018-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-008-002-018-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-008-002-018-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-008-002-019 生产工序接收单列表页
- P1-008-002-019-001 搜索条件区+数据表格区+联调验证
  - P1-008-002-019-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-008-002-019-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-008-002-019-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-008-002-020 生产进度查询页
- P1-008-002-020-001 查询条件区+进度表格区（完成率+工序进度+领料/入库进度）+联调验证
  - P1-008-002-020-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-008-002-020-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-008-002-020-001-003 实现查询条件区查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P1-008-002-021 生产统计页
- P1-008-002-021-001 报表条件区+报表数据区+导出打印区+联调验证
  - P1-008-002-021-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P1-008-002-021-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P1-008-002-021-001-003 实现报表条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

---

## P1-009 委外管理模块开发

> **依赖**：P0-007（商品含BOM）、P0-009（供应商）、P0-010（仓库）、P1-007（库存引擎）  
> **概述**：完成委外全链路：委外通知→领料→入库→工序移交→统计

### P1-009-001 委外管理后端开发
- P1-009-001-001 未委外清单聚合查询接口
  - P1-009-001-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-001-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-001-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-001-002 委外通知单CRUD接口（sub_notice + sub_notice_detail：主从CRUD + BOM展开+编码自动生成）
  - P1-009-001-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-001-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-001-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-001-003 委外通知变更记录接口
  - P1-009-001-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-001-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-001-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-001-004 委外看板数据接口
  - P1-009-001-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-001-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-001-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-001-005 委外未领料清单聚合查询接口
  - P1-009-001-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-001-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-001-005-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-001-006 委外领料单CRUD接口（sub_pick + sub_pick_detail：主从CRUD + 库存扣减）
  - P1-009-001-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-001-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-001-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-001-007 委外超领单CRUD接口
  - P1-009-001-007-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-001-007-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-001-007-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-001-008 委外退料单CRUD接口
  - P1-009-001-008-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-001-008-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-001-008-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-001-009 委外未入库清单聚合查询接口
  - P1-009-001-009-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-001-009-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-001-009-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-001-010 委外入库单CRUD接口（sub_stock_in + sub_stock_in_detail：主从CRUD + 库存增加）
  - P1-009-001-010-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-001-010-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-001-010-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-001-011 委外退库单CRUD接口
  - P1-009-001-011-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-001-011-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-001-011-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-001-012 委外工序移交单CRUD接口
  - P1-009-001-012-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-001-012-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-001-012-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-001-013 委外工序接收单CRUD接口
  - P1-009-001-013-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-001-013-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-001-013-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-001-014 委外统计接口
  - P1-009-001-014-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-001-014-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-001-014-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-001-015 委外管理工作台聚合数据接口
  - P1-009-001-015-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-001-015-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-001-015-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P1-009-002 委外管理前端页面
- P1-009-002-001 委外管理工作台（P02）
  - P1-009-002-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-002-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-002-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-002-002 委外未生产清单（P04：按请购单维度聚合未委外数量+按供应商/物料筛选+导出）
  - P1-009-002-002-001 后端开发（未委外数量SQL+Controller+Service+Mapper）
    - P1-009-002-002-001-001 编写未委外SQL（请购明细LEFT JOIN委外明细+未委外数量=请购数量-已委外数量+分页）
    - P1-009-002-002-001-002 编写Controller+Service（查询接口+筛选+分页+导出支持）
    - P1-009-002-002-001-003 验证后端开发SQL（数据正确+未委外数量准确+性能合理）
  - P1-009-002-002-002 前端开发（P04查询页：搜索区+数据表格+合计行+导出）
    - P1-009-002-002-002-001 实现搜索区（供应商选择+物料选择+日期范围+查询/重置）
    - P1-009-002-002-002-002 实现数据表格+合计行+导出（请购单号+物料+未委外数量+单位+需求日期）
- P1-009-002-003 委外看板页（P08看板页）
  - P1-009-002-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-002-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-002-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-002-004 委外领料单主从列表页（P03）
  - P1-009-002-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-002-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-002-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-002-005 委外入库单主从列表页（P03）
  - P1-009-002-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-002-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-002-005-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-002-006 委外统计页（P10报表页）
  - P1-009-002-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-009-002-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-009-002-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-009-002-007 其余委外单据前端页面（退料/超领/退库/工序移交/接收，参照生产管理页面结构）
  - P1-009-002-007-001 前端页面开发（组件编写+数据绑定+交互逻辑）
    - P1-009-002-007-001-001 编写页面组件（template+搜索区+表格区+操作区+弹窗+表单组件）
    - P1-009-002-007-001-002 编写数据绑定与交互逻辑（API调用+数据绑定+事件处理+路由跳转）
    - P1-009-002-007-001-003 验证前端页面开发页面（渲染正确+交互响应+数据绑定+空状态/加载态）

---

## P1-010 应收管理模块开发

> **依赖**：P1-005（销售出库/对账）、P0-011（财务基础）  
> **概述**：完成应收全链路：应收款→应开票→收款→核销→发票→账龄分析

### P1-010-001 应收管理后端开发
- P1-010-001-001 应收款自动生成接口（根据finance.ar_source参数，从销售订单/出库单/对账单生成应收款记录）
  - P1-010-001-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-001-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-001-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-001-002 应开票自动生成接口（根据finance.invoice_ar_source参数生成应开票记录）
  - P1-010-001-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-001-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-001-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-001-003 收款单CRUD接口（ar_receipt + ar_receipt_detail：主从CRUD + 编码自动生成）
  - P1-010-001-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-001-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-001-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-001-004 收款核销单CRUD接口（ar_receipt_write_off + ar_receipt_write_off_detail：主从CRUD + 核销金额校验+应收款已核销金额更新）
  - P1-010-001-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-001-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-001-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-001-005 销项发票CRUD接口（ar_invoice + ar_invoice_detail：主从CRUD）
  - P1-010-001-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-001-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-001-005-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-001-006 销项发票核销单CRUD接口
  - P1-010-001-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-001-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-001-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-001-007 其他应收单CRUD接口
  - P1-010-001-007-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-001-007-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-001-007-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-001-008 其他收款单CRUD接口
  - P1-010-001-008-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-001-008-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-001-008-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-001-009 应收账龄分析接口（按客户/账龄区间汇总应收款+已收+未收金额）
  - P1-010-001-009-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-001-009-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-001-009-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-001-010 应收管理工作台聚合数据接口
  - P1-010-001-010-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-001-010-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-001-010-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P1-010-002 应收管理前端页面
- P1-010-002-001 应收管理工作台（P02）
  - P1-010-002-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-002-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-002-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-002-002 应收款列表页（P04）
  - P1-010-002-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-002-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-002-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-002-003 应开票列表页（P04）
  - P1-010-002-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-002-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-002-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-002-004 收款单主从列表页（P03）
  - P1-010-002-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-002-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-002-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-002-005 新增/编辑收款单主从表单页（P06）
  - P1-010-002-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-002-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-002-005-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-002-006 收款核销单主从列表页（P03）
  - P1-010-002-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-002-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-002-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-002-007 销项发票主从列表页（P03）
  - P1-010-002-007-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-002-007-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-002-007-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-002-008 销项发票核销单列表页（P04）
  - P1-010-002-008-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-002-008-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-002-008-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-002-009 其他应收单列表页（P04）
  - P1-010-002-009-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-002-009-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-002-009-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-002-010 其他收款单列表页（P04）
  - P1-010-002-010-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-002-010-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-002-010-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-010-002-011 应收账龄分析页（P10报表页）
  - P1-010-002-011-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-010-002-011-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-010-002-011-001-002 验证功能（核心用例通过+边界处理+异常降级）

---

## P1-011 应付管理模块开发

> **依赖**：P1-006（采购入库/对账）、P1-009（委外）、P0-011（财务基础）  
> **概述**：完成应付全链路：应付款→应付票→付款→核销→发票→账龄分析

### P1-011-001 应付管理后端开发
- P1-011-001-001 应付款自动生成接口（根据finance.ap_source参数）
  - P1-011-001-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-001-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-001-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-001-002 应付票自动生成接口（根据finance.invoice_ap_source参数）
  - P1-011-001-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-001-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-001-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-001-003 付款单CRUD接口（ap_payment + ap_payment_detail：主从CRUD + 编码自动生成）
  - P1-011-001-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-001-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-001-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-001-004 付款核销单CRUD接口（ap_payment_write_off + ap_payment_write_off_detail：主从CRUD + 核销金额校验）
  - P1-011-001-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-001-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-001-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-001-005 进项发票CRUD接口（ap_invoice + ap_invoice_detail：主从CRUD）
  - P1-011-001-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-001-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-001-005-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-001-006 进项发票核销单CRUD接口
  - P1-011-001-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-001-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-001-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-001-007 其他应付单CRUD接口
  - P1-011-001-007-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-001-007-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-001-007-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-001-008 其他付款单CRUD接口
  - P1-011-001-008-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-001-008-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-001-008-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-001-009 应付账龄分析接口
  - P1-011-001-009-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-001-009-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-001-009-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-001-010 应付管理工作台聚合数据接口
  - P1-011-001-010-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-001-010-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-001-010-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P1-011-002 应付管理前端页面
- P1-011-002-001 应付管理工作台（P02）
  - P1-011-002-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-002-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-002-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-002-002 应付款列表页（P04）
  - P1-011-002-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-002-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-002-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-002-003 应付票列表页（P04）
  - P1-011-002-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-002-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-002-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-002-004 付款单主从列表页（P03）
  - P1-011-002-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-002-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-002-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-002-005 新增/编辑付款单主从表单页（P06）
  - P1-011-002-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-002-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-002-005-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-002-006 付款核销单主从列表页（P03）
  - P1-011-002-006-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-002-006-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-002-006-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-002-007 进项发票主从列表页（P03）
  - P1-011-002-007-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-002-007-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-002-007-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-002-008 进项发票核销单列表页（P04）
  - P1-011-002-008-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-002-008-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-002-008-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-002-009 其他应付单列表页（P04）
  - P1-011-002-009-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-002-009-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-002-009-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-002-010 其他付款单列表页（P04）
  - P1-011-002-010-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-002-010-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-002-010-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-011-002-011 应付账龄分析页（P10报表页）
  - P1-011-002-011-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-011-002-011-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-011-002-011-001-002 验证功能（核心用例通过+边界处理+异常降级）

---

## P1-012 凭证管理模块开发

> **依赖**：P0-011（会计科目/凭证字）、P1-010（应收）、P1-011（应付）  
> **概述**：完成凭证录入、凭证模板、自动生成凭证

### P1-012-001 凭证管理后端开发
- P1-012-001-001 凭证CRUD接口（fin_voucher + fin_voucher_detail：主从CRUD + 借贷平衡校验+编码自动生成）
  - P1-012-001-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-012-001-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-012-001-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-012-001-002 凭证模板CRUD接口（fin_voucher_template + fin_voucher_template_detail：主从CRUD）
  - P1-012-001-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-012-001-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-012-001-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-012-001-003 凭证自动生成接口（根据finance.auto_voucher参数，单据审核后自动按模板生成凭证）
  - P1-012-001-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-012-001-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-012-001-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-012-001-004 凭证过账/红冲接口（过账标记+红冲自动生成反向凭证）
  - P1-012-001-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-012-001-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-012-001-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-012-001-005 凭证管理工作台聚合数据接口
  - P1-012-001-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-012-001-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-012-001-005-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P1-012-002 凭证管理前端页面
- P1-012-002-001 凭证管理工作台（P02）
  - P1-012-002-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-012-002-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-012-002-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-012-002-002 凭证主从列表页（P03）
  - P1-012-002-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-012-002-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-012-002-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-012-002-003 新增/编辑凭证主从表单页（P06：借方/贷方/金额三栏式录入+借贷平衡实时校验）
  - P1-012-002-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-012-002-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-012-002-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-012-002-004 凭证模板列表页（P04）
  - P1-012-002-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-012-002-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-012-002-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-012-002-005 新增/编辑凭证模板表单页（P07）
  - P1-012-002-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-012-002-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-012-002-005-001-002 验证功能（核心用例通过+边界处理+异常降级）

---

## P1-013 账簿查询模块开发

> **依赖**：P1-012（凭证数据）  
> **概述**：完成总账、明细账、余额表、多栏账查询

### P1-013-001 账簿查询后端开发
- P1-013-001-001 总账查询接口（按科目汇总各期借方/贷方/余额+支持科目树下钻）
  - P1-013-001-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-013-001-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-013-001-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-013-001-002 明细账查询接口（按科目+期间查询凭证明细+借方/贷方/余额三栏式）
  - P1-013-001-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-013-001-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-013-001-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-013-001-003 余额表查询接口（科目余额汇总+期初/本期借方/本期贷方/期末余额）
  - P1-013-001-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-013-001-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-013-001-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-013-001-004 多栏账查询接口（多栏明细展开+按科目下级栏目展开）
  - P1-013-001-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-013-001-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-013-001-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-013-001-005 账簿查询工作台聚合数据接口
  - P1-013-001-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-013-001-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-013-001-005-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P1-013-002 账簿查询前端页面
- P1-013-002-001 账簿查询工作台（P02）
  - P1-013-002-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-013-002-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-013-002-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-013-002-002 总账页（P10报表页：科目树+报表数据+下钻明细账）
  - P1-013-002-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-013-002-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-013-002-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-013-002-003 明细账页（P10报表页：按科目分页借方/贷方/余额三栏式）
  - P1-013-002-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-013-002-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-013-002-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-013-002-004 余额表页（P10报表页：科目余额汇总+下钻）
  - P1-013-002-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-013-002-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-013-002-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-013-002-005 多栏账页（P10报表页：多栏明细展开）
  - P1-013-002-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-013-002-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-013-002-005-001-002 验证功能（核心用例通过+边界处理+异常降级）

---

## P1-014 财务报表模块开发

> **依赖**：P1-013（账簿数据）  
> **概述**：完成资产负债表、利润表、现金流量表

### P1-014-001 财务报表后端开发
- P1-014-001-001 资产负债表数据接口（按科目余额汇总+报表项目取数公式计算）
  - P1-014-001-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-014-001-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-014-001-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-014-001-002 利润表数据接口（损益类科目本期/累计发生额+报表项目取数公式计算）
  - P1-014-001-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-014-001-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-014-001-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-014-001-003 现金流量表数据接口（现金类科目明细+流量分类归集+报表项目取数公式计算）
  - P1-014-001-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-014-001-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-014-001-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-014-001-004 财务报表工作台聚合数据接口
  - P1-014-001-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-014-001-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-014-001-004-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P1-014-002 财务报表前端页面
- P1-014-002-001 财务报表工作台（P02）
  - P1-014-002-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-014-002-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-014-002-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-014-002-002 资产负债表页（P10报表页：固定行列表格+单元格钻取到总账/明细账）
  - P1-014-002-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-014-002-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-014-002-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-014-002-003 利润表页（P10报表页）
  - P1-014-002-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-014-002-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-014-002-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-014-002-004 现金流量表页（P10报表页）
  - P1-014-002-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-014-002-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-014-002-004-001-002 验证功能（核心用例通过+边界处理+异常降级）

---

## P1-015 成本核算模块开发

> **依赖**：P1-005（销售出库）、P1-007（库存成本）、P1-008（生产）、P1-009（委外）  
> **概述**：完成销售订单/出库/生产/委外成本核算与库存成本调整

### P1-015-001 成本核算后端开发
- P1-015-001-000 成本核算工作台聚合数据接口（本月销售成本/生产成本/委外成本汇总+成本趋势+毛利概览+成本异常预警）
  - P1-015-001-000-001 Service接口定义+实现类编写（聚合统计SQL+缓存）
    - P1-015-001-000-001-001 编写工作台聚合SQL（销售成本+生产成本+委外成本汇总+趋势+毛利概览）
    - P1-015-001-000-001-002 编写ServiceImpl实现类（聚合查询+缓存+空数据处理）
    - P1-015-001-000-001-003 验证Service（数据正确+缓存命中+性能合理）
- P1-015-001-001 销售订单成本核算接口（按销售订单维度汇总出库成本+毛利计算）
  - P1-015-001-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-015-001-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-015-001-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-015-001-002 销售出库成本核算接口（按出库单明细行计算成本+成本方法按inv.cost_method参数）
  - P1-015-001-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-015-001-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-015-001-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-015-001-003 生产成本核算接口（材料成本+人工成本+制造费用归集+分摊计算）
  - P1-015-001-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-015-001-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-015-001-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-015-001-004 委外成本核算接口（材料成本+加工费归集计算）
  - P1-015-001-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-015-001-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-015-001-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-015-001-005 库存成本调整接口（手工调整+系统自动调整+调整凭证生成）
  - P1-015-001-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-015-001-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-015-001-005-001-002 验证功能（核心用例通过+边界处理+异常降级）

### P1-015-002 成本核算前端页面
- P1-015-002-000 成本核算工作台页（P02工作台：销售成本/生产成本/委外成本KPI卡片+成本趋势图+毛利概览+成本异常预警列表）
  - P1-015-002-000-001 工作台KPI卡片区（本月销售成本/生产成本/委外成本/毛利率+点击下钻）
    - P1-015-002-000-001-001 KPI卡片组件开发（数值展示+同比环比+点击下钻路由）
  - P1-015-002-000-002 工作台图表区（成本趋势折线图+成本构成饼图+毛利趋势柱状图）
    - P1-015-002-000-002-001 ECharts图表组件开发（折线图+饼图+柱状图+响应式+交互事件）
  - P1-015-002-000-003 工作台联调验证
    - P1-015-002-000-003-001 工作台全流程联调（数据加载+下钻+刷新+异常处理）
- P1-015-002-001 销售订单成本核算列表页（P04）
  - P1-015-002-001-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-015-002-001-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-015-002-001-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-015-002-002 销售出库成本核算列表页（P04）
  - P1-015-002-002-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-015-002-002-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-015-002-002-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-015-002-003 生产成本核算列表页（P04）
  - P1-015-002-003-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-015-002-003-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-015-002-003-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-015-002-004 委外成本核算列表页（P04）
  - P1-015-002-004-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-015-002-004-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-015-002-004-001-002 验证功能（核心用例通过+边界处理+异常降级）
- P1-015-002-005 库存成本调整列表页（P04）
  - P1-015-002-005-001 实现核心逻辑（代码编写+数据处理+异常处理）
    - P1-015-002-005-001-001 编写核心代码（主流程+数据处理+计算逻辑+日志记录）
    - P1-015-002-005-001-002 验证功能（核心用例通过+边界处理+异常降级）

---

# 第四章 P2级任务分解（常规）

## P2-001 费用管理模块开发

> **依赖**：P0-011（财务基础）、P1-001（审核引擎）  
> **概述**：完成费用报销与费用预算

### P2-001-001 费用管理后端开发

#### P2-001-001-001 费用报销CRUD接口
- P2-001-001-001-001 费用报销Entity/DTO/VO定义（exp_expense主表+exp_expense_detail从表）
  - P2-001-001-001-001-001 编写ExpenseEntity类（@TableName"exp_expense"+@TableId报销ID+报销编号expense_no+报销日期expense_date+报销部门dept_id+报销人reimburse_user_id+报销金额合计total_amount+币种currency_id+关联申请ID apply_id+付款方式pay_method+收款账户bank_account_id+发票附件attachment_url+状态status+制单人+制单日期+@TableLogic逻辑删除+@Version乐观锁）
  - P2-001-001-001-001-002 编写ExpenseDetailEntity类（@TableName"exp_expense_detail"+@TableId明细ID+报销ID expense_id+费用项目expense_item_id+报销金额amount+发票号invoice_no+发票日期invoice_date+发票类型invoice_type+备注remark）
  - P2-001-001-001-001-003 编写ExpenseCreateDTO（@NotBlank报销部门+@NotNull报销日期+@NotEmpty明细列表+@DecimalMin("0.01")金额>0+付款方式+关联申请号可选+发票信息可选+校验规则：报销金额合计=明细金额合计+关联申请时校验申请状态已审核+预算关联校验）
  - P2-001-001-001-001-004 编写ExpenseUpdateDTO（@NotNull报销ID+同CreateDTO校验+已审核报销单不可修改）
  - P2-001-001-001-001-005 编写ExpenseQueryDTO（报销编号+报销部门+报销人+状态+日期范围+关联申请编号查询条件）
  - P2-001-001-001-001-006 编写ExpenseVO/ExpenseListVO（列表VO含报销编号/报销部门名称/报销人姓名/报销金额合计/币种名称/状态/日期+详情VO含明细列表+发票附件+审核记录+@JsonFormat日期格式+字典翻译）
- P2-001-001-001-002 费用报销Mapper开发
  - P2-001-001-001-002-001 编写ExpenseMapper接口（extends BaseMapperX<ExpenseEntity>+@Mapper+selectByExpenseNo按编号查询+selectPageByCondition条件分页+selectByApplyId按申请ID查询关联报销+selectByDeptPeriod按部门+期间汇总报销金额+selectByBudgetId按预算查询已报销金额）
  - P2-001-001-001-002-002 编写ExpenseDetailMapper接口（extends BaseMapperX<ExpenseDetailEntity>+@Mapper+selectByExpenseId按报销ID查明细+deleteByExpenseId按报销ID删除明细+batchInsert批量插入明细+sumByExpenseItem按费用项目汇总报销金额）
  - P2-001-001-001-002-003 编写ExpenseMapper.xml（resultMap含主从嵌套映射+条件查询SQL含编号/部门/日期/状态动态where+部门报销汇总SQL含GROUP BY dept_id,period_id+预算关联查询SQL含SUM已报销金额）
  - P2-001-001-001-002-004 编写ExpenseDetailMapper.xml（resultMap映射+按报销ID查询+按费用项目汇总+批量插入SQL）
- P2-001-001-001-003 费用报销Service开发（主从CRUD+编码自动生成+费用预算校验+审核流转）
  - P2-001-001-001-003-001 编写ExpenseService接口（extends IServiceX<ExpenseEntity>+create/draftCreate创建报销草稿+update修改+delete删除+submit提交审核+approve审核通过+reject驳回+checkBudget预算校验+generateVoucher生成凭证方法声明）
  - P2-001-001-001-003-002 编写ExpenseServiceImpl实现类（@Service+报销编号自动生成调用SerialNumberService.generate("BX")+createWithDetail主从事务保存+关联费用申请时自动带出申请明细+checkBudget预算校验：查询费用项目对应预算余额=预算金额-已申请未报销金额-已报销金额，校验报销金额<=预算余额+审核通过approve后：①更新关联费用申请状态为"已报销"②扣减对应费用预算的已用金额remaining_amount-=报销金额③生成费用报销凭证：借-费用科目/贷-其他应付款或银行存款+关联申请时自动下推+不关联申请时直接报销+@Transactional事务管理）
  - P2-001-001-001-003-003 业务校验逻辑（报销编号唯一性+报销金额合计=明细金额合计+关联费用申请时校验申请已审核通过且未全额报销+预算余额实时校验+报销金额>0+发票附件校验（按要求必填/选填）+已审核报销单不可修改/删除+付款方式与收款账户一致性）
- P2-001-001-001-004 费用报销Controller开发
  - P2-001-001-001-004-001 编写ExpenseController类（@RestController+@RequestMapping"/api/expense/claim"+@Tag"费用报销"+注入ExpenseService）
  - P2-001-001-001-004-002 实现POST /create创建报销接口（@RequestBody ExpenseCreateDTO+@Valid校验+从费用申请下推时自动带出数据+返回Result<ExpenseVO>）
  - P2-001-001-001-004-003 实现PUT /update修改报销接口（@RequestBody ExpenseUpdateDTO+@Valid+仅草稿状态可修改+返回Result<Void>）
  - P2-001-001-001-004-004 实现DELETE /delete/{id}删除报销接口（@PathVariable id+仅草稿状态可删除+返回Result<Void>）
  - P2-001-001-001-004-005 实现GET /page分页查询接口（ExpenseQueryDTO条件+返回Result<PageResult<ExpenseListVO>>含报销金额合计列）
  - P2-001-001-001-004-006 实现GET /detail/{id}详情查询接口（@PathVariable id+主从表组装+返回Result<ExpenseVO>含明细/发票附件/审核记录/关联申请信息）
  - P2-001-001-001-004-007 实现POST /submit/{id}提交审核接口（@PathVariable id+预算校验+调用审核引擎+返回Result<Void>）
  - P2-001-001-001-004-008 实现POST /checkBudget预算校验接口（@RequestBody BudgetCheckDTO+返回Result<BudgetCheckVO>含预算余额/可报销金额/超预算提示）
  - P2-001-001-001-004-009 实现POST /pushFromApply从申请下推接口（@RequestParam applyId+校验申请状态+自动生成报销草稿+返回Result<ExpenseVO>）
  - P2-001-001-001-004-010 补充Knife4j接口文档注解（@Operation描述+@Parameter参数说明+@Schema示例值）
  - P2-001-001-001-004-011 验证费用报销接口（单元测试：CRUD全流程+从申请下推+预算校验+审核流转+凭证生成+金额合计校验+异常场景处理）

#### P2-001-001-002 费用申请CRUD接口
- P2-001-001-002-001 费用申请Entity/DTO/VO定义（expense_application主表+expense_application_detail从表）
  - P2-001-001-002-001-001 编写ExpenseApplicationEntity类（@TableName"expense_application"+@TableId申请ID+申请编号exp_apply_no+申请日期apply_date+申请部门dept_id+申请人apply_user_id+申请金额apply_amount+币种currency_id+预算关联budget_id+用途说明purpose+状态status+@TableLogic逻辑删除）
  - P2-001-001-002-001-002 编写ExpenseApplicationDetailEntity类（@TableName"expense_application_detail"+@TableId明细ID+申请ID关联apply_id+费用项目expense_item_id+申请金额amount+备注remark）
  - P2-001-001-002-001-003 编写ExpenseApplicationCreateDTO（@NotBlank申请部门+@NotNull申请日期+@NotEmpty明细列表+@DecimalMin申请金额>0）
  - P2-001-001-002-001-004 编写ExpenseApplicationUpdateDTO（@NotNull申请ID+同CreateDTO校验规则）
  - P2-001-001-002-001-005 编写ExpenseApplicationQueryDTO（申请编号+申请部门+申请人+状态+日期范围查询条件）
  - P2-001-001-002-001-006 编写ExpenseApplicationVO/ExpenseApplicationListVO（列表VO含申请编号/申请部门名称/申请人姓名/申请金额/状态/日期+@JsonFormat日期格式+字典翻译字段）
- P2-001-001-002-002 费用申请Mapper开发
  - P2-001-001-002-002-001 编写ExpenseApplicationMapper接口（extends BaseMapperX<ExpenseApplicationEntity>+@Mapper+selectByApplyNo按编号查询+selectPageByCondition条件分页+selectByBudgetId按预算查询+selectPendingByUser按用户查询待审核列表）
  - P2-001-001-002-002-002 编写ExpenseApplicationDetailMapper接口（extends BaseMapperX<ExpenseApplicationDetailEntity>+@Mapper+selectByApplyId按申请ID查明细+deleteByApplyId按申请ID删除明细+batchInsert批量插入明细）
  - P2-001-001-002-002-003 编写ExpenseApplicationMapper.xml（resultMap映射含主表+detail从表嵌套+条件查询SQL含部门/状态/日期范围动态where+预算关联子查询）
  - P2-001-001-002-002-004 编写ExpenseApplicationDetailMapper.xml（resultMap映射+按申请ID查询SQL+批量插入SQL）
- P2-001-001-002-003 费用申请Service开发（主从CRUD+编码自动生成+预算控制校验+申请→报销下推）
  - P2-001-001-002-003-001 编写ExpenseApplicationService接口（extends IServiceX<ExpenseApplicationEntity>+create/draftCreate创建草稿+update修改+delete删除+submit提交审核+approve审核通过+reject驳回+pushToExpenseClaim下推到报销+checkBudget预算校验方法声明）
  - P2-001-001-002-003-002 编写ExpenseApplicationServiceImpl实现类（@Service+主从表事务保存createWithDetail+编码自动生成调用SerialNumberService.generate("SQF")+预算校验checkBudget方法：查询费用项目对应预算余额+申请金额≤预算余额校验+超预算时抛出BusinessException含预算余额提示+审核后预算扣减逻辑deductBudget+下推到报销pushToExpenseClaim生成报销单草稿+@Transactional事务管理）
  - P2-001-001-002-003-003 业务校验逻辑（申请编号唯一性校验+申请日期必须在当前会计期间内+预算余额实时计算逻辑：预算金额-已申请未报销金额-已报销金额=可用余额+部门费用额度校验+提交审核时状态校验open→pending）
- P2-001-001-002-004 费用申请Controller开发
  - P2-001-001-002-004-001 编写ExpenseApplicationController类（@RestController+@RequestMapping"/api/expense/application"+@Tag"费用申请"+注入ExpenseApplicationService）
  - P2-001-001-002-004-002 实现POST /create创建申请接口（@RequestBody ExpenseApplicationCreateDTO+@Valid参数校验+Service调用+返回Result<ExpenseApplicationVO>）
  - P2-001-001-002-004-003 实现PUT /update修改申请接口（@RequestBody ExpenseApplicationUpdateDTO+@Valid校验+Service调用+返回Result<Void>）
  - P2-001-001-002-004-004 实现DELETE /delete/{id}删除申请接口（@PathVariable id+状态校验仅允许open/草稿状态删除+Service调用+返回Result<Void>）
  - P2-001-001-002-004-005 实现GET /page分页查询接口（ExpenseApplicationQueryDTO条件+@PageDefault分页参数+Service调用+返回Result<PageResult<ExpenseApplicationListVO>>)
  - P2-001-001-002-004-006 实现GET /detail/{id}详情查询接口（@PathVariable id+主从表数据组装+返回Result<ExpenseApplicationVO>含明细列表）
  - P2-001-001-002-004-007 实现POST /submit/{id}提交审核接口（@PathVariable id+状态校验+调用审核引擎+返回Result<Void>）
  - P2-001-001-002-004-008 实现POST /checkBudget预算校验接口（@RequestBody BudgetCheckDTO含费用项目+金额+部门+返回Result<BudgetCheckVO>含预算余额/是否超预算/超预算金额）
  - P2-001-001-002-004-009 补充Knife4j接口文档注解（@Operation描述+@Parameter参数说明+@Schema示例值）
  - P2-001-001-002-004-010 验证费用申请接口（单元测试：CRUD全流程+预算校验+超预算拦截+下推报销+审核流转+异常场景处理）

#### P2-001-001-003 费用预算CRUD接口
- P2-001-001-003-001 费用预算Entity/DTO/VO定义（expense_budget主表+expense_budget_detail从表）
  - P2-001-001-003-001-001 编写ExpenseBudgetEntity类（@TableName"expense_budget"+@TableId预算ID+预算编号budget_no+预算年度budget_year+预算期间budget_period+部门dept_id+总预算金额total_amount+已用金额used_amount+剩余金额remaining_amount+状态status+制单人+制单日期+@TableLogic逻辑删除）
  - P2-001-001-003-001-002 编写ExpenseBudgetDetailEntity类（@TableName"expense_budget_detail"+@TableId明细ID+预算ID budget_id+费用项目expense_item_id+预算金额budget_amount+已用金额used_amount+剩余金额remaining_amount+备注remark）
  - P2-001-001-003-001-003 编写ExpenseBudgetCreateDTO（@NotBlank预算年度+@NotBlank预算期间+@NotNull部门+@NotEmpty明细列表+@DecimalMin预算金额>0+预算期间+部门+费用项目维度唯一性校验注解@BudgetUnique）
  - P2-001-001-003-001-004 编写ExpenseBudgetUpdateDTO（@NotNull预算ID+同CreateDTO校验规则）
  - P2-001-001-003-001-005 编写ExpenseBudgetQueryDTO（预算编号+预算年度+预算期间+部门+费用项目+状态查询条件）
  - P2-001-001-003-001-006 编写ExpenseBudgetVO/ExpenseBudgetListVO（列表VO含预算编号/年度/期间/部门名称/总金额/已用金额/剩余金额/执行率+@JsonFormat日期格式+字典翻译）
- P2-001-001-003-002 费用预算Mapper开发
  - P2-001-001-003-002-001 编写ExpenseBudgetMapper接口（extends BaseMapperX<ExpenseBudgetEntity>+@Mapper+selectByBudgetNo按编号查询+selectByPeriodAndDept按期间部门查询+selectPageByCondition条件分页+selectByUniqueKey唯一性校验查询（期间+部门+费用项目））
  - P2-001-001-003-002-002 编写ExpenseBudgetDetailMapper接口（extends BaseMapperX<ExpenseBudgetDetailEntity>+@Mapper+selectByBudgetId按预算ID查明细+sumByBudgetId汇总预算明细金额+updateUsedAmount更新已用金额）
  - P2-001-001-003-002-003 编写ExpenseBudgetMapper.xml（resultMap映射含主表+detail嵌套+条件查询SQL含年度/期间/部门/状态动态where+唯一性校验查询SQL含期间+部门+费用项目三字段组合）
  - P2-001-001-003-002-004 编写ExpenseBudgetDetailMapper.xml（resultMap映射+按预算ID分组汇总SQL+更新已用金额SQL）
- P2-001-001-003-003 费用预算Service开发（主从CRUD+预算期间+部门+费用项目维度唯一性+预算执行联动）
  - P2-001-001-003-003-001 编写ExpenseBudgetService接口（extends IServiceX<ExpenseBudgetEntity>+create/draftCreate创建+update修改+delete删除+getByPeriodAndDept按期间部门查询+getBudgetBalance查询预算余额+deductBudget预算扣减+releaseBudget预算释放+copyFromLastPeriod从上期复制预算方法声明）
  - P2-001-001-003-003-002 编写ExpenseBudgetServiceImpl实现类（@Service+主从表事务保存createWithDetail+预算编号自动生成调用SerialNumberService.generate("YSB")+唯一性校验：同一预算年度+期间+部门+费用项目组合不可重复+同期预算汇总金额校验：明细金额合计=主表总金额+预算扣减deductBudget方法：扣减已用金额并更新剩余金额=预算金额-已用金额+预算释放releaseBudget方法：业务单据驳回时恢复预算余额+上期预算复制copyFromLastPeriod方法：复制上期预算结构并更新年度+@Transactional事务管理）
  - P2-001-001-003-003-003 业务校验逻辑（预算期间唯一性校验（年度+期间+部门+费用项目四维度唯一）+预算金额精度校验（保留两位小数）+预算期间不可跨年+已执行预算不可删除+budget_no唯一性校验+预算期间必须为已定义的会计期间）
- P2-001-001-003-004 费用预算Controller开发
  - P2-001-001-003-004-001 编写ExpenseBudgetController类（@RestController+@RequestMapping"/api/expense/budget"+@Tag"费用预算"+注入ExpenseBudgetService）
  - P2-001-001-003-004-002 实现POST /create创建预算接口（@RequestBody ExpenseBudgetCreateDTO+@Valid校验+唯一性校验+Service调用+返回Result<ExpenseBudgetVO>）
  - P2-001-001-003-004-003 实现PUT /update修改预算接口（@RequestBody ExpenseBudgetUpdateDTO+@Valid校验+已执行预算不可修改明细+Service调用+返回Result<Void>）
  - P2-001-001-003-004-004 实现DELETE /delete/{id}删除预算接口（@PathVariable id+状态校验仅允许草稿状态删除+Service调用+返回Result<Void>）
  - P2-001-001-003-004-005 实现GET /page分页查询接口（ExpenseBudgetQueryDTO条件+Service调用+返回Result<PageResult<ExpenseBudgetListVO>>含执行率字段）
  - P2-001-001-003-004-006 实现GET /detail/{id}详情查询接口（@PathVariable id+主从表数据组装+标注已用/剩余+返回Result<ExpenseBudgetVO>含明细列表）
  - P2-001-001-003-004-007 实现POST /copyFromLastPeriod从上期复制接口（@RequestBody CopyBudgetDTO含源期间+目标期间+部门+Service调用+返回Result<ExpenseBudgetVO>）
  - P2-001-001-003-004-008 补充Knife4j接口文档注解（@Operation描述+@Parameter参数说明+@Schema示例值）
  - P2-001-001-003-004-009 验证费用预算接口（单元测试：CRUD全流程+唯一性校验+预算扣减+预算释放+上期复制+金额精度+异常场景处理）

#### P2-001-001-004 费用预算执行率查询接口
- P2-001-001-004-001 预算执行率查询Service开发
  - P2-001-001-004-001-001 编写ExpenseBudgetExecutionRateVO（执行率VO含预算编号/部门名称/费用项目名称/预算金额/已申请金额/已报销金额/已用金额合计/剩余金额/执行率百分比+@JsonFormat格式）
  - P2-001-001-004-001-002 编写ExpenseBudgetExecutionService接口（getExecutionRate按期间部门查询执行率+getExecutionRateByBudget按预算ID查询+getExecutionTrend按月份查询执行趋势+getDepartmentComparison部门间预算执行对比方法声明）
  - P2-001-001-004-001-003 编写ExpenseBudgetExecutionServiceImpl实现类（@Service+执行率计算逻辑：已用金额=SUM(已申请金额)+SUM(已报销金额)，剩余金额=预算金额-已用金额，执行率=已用金额/预算金额×100%+按费用项目GROUP BY汇总+按部门GROUP BY汇总+按期间GROUP BY趋势计算+缓存@Cacheable("budgetExecution")+@Transactional(readOnly=true)）
  - P2-001-001-004-001-004 编写执行率查询SQL（两张核心SQL：①执行率汇总SQL=SELECT budget_id,budget_no,dept_id,expense_item_id,SUM(budget_amount) budget,SUM(used_amount) used,SUM(remaining_amount) remaining,ROUND(SUM(used_amount)/SUM(budget_amount)*100,2) rate FROM expense_budget LEFT JOIN相关申请/报销表GROUP BY预算维度；②月度趋势SQL=按月GROUP BY+累计执行率+同比环比计算）
  - P2-001-001-004-001-005 验证执行率计算（预算金额与已用金额数据一致性+执行率精度+多维度拆分正确+缓存命中验证）
- P2-001-001-004-002 预算执行率Controller开发
  - P2-001-001-004-002-001 编写ExpenseBudgetExecutionController类（@RestController+@RequestMapping"/api/expense/budget/execution"+@Tag"费用预算执行率"+注入ExpenseBudgetExecutionService）
  - P2-001-001-004-002-002 实现GET /rate查询执行率接口（@RequestParam期间+部门+费用项目可选+返回Result<List<ExpenseBudgetExecutionRateVO>>按执行率降序排列）
  - P2-001-001-004-002-003 实现GET /rate/{budgetId}按预算查询接口（@PathVariable budgetId+返回Result<ExpenseBudgetExecutionRateVO>含明细费用项目的执行率）
  - P2-001-001-004-002-004 实现GET /trend查询执行趋势接口（@RequestParam部门+年度+返回Result<List<ExecutionTrendVO>>含12个月执行率趋势数据）
  - P2-001-001-004-002-005 实现GET /departmentComparison部门对比接口（@RequestParam期间+返回Result<List<DepartmentComparisonVO>>各部门执行率排名+超预算预警标记）
  - P2-001-001-004-002-006 补充Knife4j接口文档注解（@Operation描述+@Parameter参数说明+@Schema示例值）
  - P2-001-001-004-002-007 验证接口功能（执行率数据正确+多维查询准确+趋势数据连续+对比排名合理+性能压测）

### P2-001-002 费用管理前端页面

#### P2-001-002-000 费用管理工作台页（P02：本月报销金额/待审申请/预算执行率KPI卡片+费用趋势图+预算预警列表）
- P2-001-002-000-001 工作台KPI卡片区+图表区+待办区+联调验证
  - P2-001-002-000-001-001 KPI卡片+图表+待办列表+快捷入口+联调

#### P2-001-002-001 费用申请单主从列表页
- P2-001-002-001-001 搜索区+主表列表+从表标签页区+操作按钮+批量操作+联调验证
  - P2-001-002-001-001-001 实现搜索（申请编号+申请部门+日期范围+状态）+主表vxe-table+从表明细+操作按钮

#### P2-001-002-002 新增/编辑费用申请主从表单页
- P2-001-002-002-001 主表字段+从表明细区（费用项目选择+申请金额+用途说明）+预算校验提示+联调验证
  - P2-001-002-002-001-001 实现表单（申请部门+日期+从表费用项目+申请金额+预算余额实时提示+提交审核）

#### P2-001-002-003 费用报销主从列表页
- P2-001-002-003-001 搜索条件区+主表数据表格+从表明细区+操作按钮区+联调验证
  - P2-001-002-003-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-001-002-003-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-001-002-003-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-001-002-004 新增/编辑费用报销主从表单页
- P2-001-002-004-001 主表字段+从表明细区（费用项目选择+金额+发票附件）+预算校验提示+联调验证
  - P2-001-002-004-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P2-001-002-004-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P2-001-002-004-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）

#### P2-001-002-005 费用预算列表页
- P2-001-002-005-001 搜索条件区+数据表格区+联调验证
  - P2-001-002-005-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-001-002-005-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-001-002-005-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-001-002-006 费用预算执行率页
- P2-001-002-006-001 报表条件区+执行率表格+图表区+联调验证
  - P2-001-002-006-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-001-002-006-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-001-002-006-001-003 实现报表条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
  - P2-001-002-007 费用统计（P10报表页：按费用项目/部门/人员/时间维度+预算对比+趋势图）
    - P2-001-002-007-001 后端开发（多维统计SQL+预算对比查询+趋势数据接口）
      - P2-001-002-007-001-001 编写统计SQL（按费用项目/部门/人员/时间GROUP BY+SUM申请+SUM报销金额）
      - P2-001-002-007-001-002 编写预算对比SQL（费用预算-实际费用=差异+执行率=实际/预算×100%）
      - P2-001-002-007-001-003 验证后端开发SQL（统计正确+预算对比准确+性能合理）
    - P2-001-002-007-002 前端开发（P10报表页：筛选区+统计表格+趋势图+预算对比图）
      - P2-001-002-007-002-001 实现筛选区+统计表格（维度切换+SUM列+合计行+导出）
      - P2-001-002-007-002-002 实现趋势图+预算对比图（ECharts+柱状图+导出）

---

## P2-002 固定资产模块开发

> **依赖**：P0-011（财务基础含会计科目）  
> **概述**：完成固定资产卡片、折旧、变动、处置

### P2-002-001 固定资产后端开发

#### P2-002-001-001 固定资产卡片CRUD接口
- P2-002-001-001-001 固定资产卡片Entity/DTO/VO定义（fa_asset主表）
  - P2-002-001-001-001-001 编写AssetEntity类（@TableName"fa_asset"+@TableId资产ID+资产编号asset_no+资产名称asset_name+资产分类category_id+规格型号spec+计量单位unit+数量quantity+取得日期acquisition_date+原值original_value+残值率residual_rate+残值residual_value+使用年限useful_life+折旧方法depreciation_method+月折旧额monthly_depreciation+累计折旧accumulated_depreciation+净值net_value+使用部门dept_id+存放位置location+保管人custodian_id+资产状态status+启用日期start_use_date+@TableLogic逻辑删除+@Version乐观锁版本号）
  - P2-002-001-001-001-002 编写AssetCardCreateDTO（@NotBlank资产编号+@NotBlank资产名称+@NotNull资产分类+@NotNull取得日期+@DecimalMin("0.01")原值>0+@DecimalMin("0")残值率0-100+@NotNull使用年限>0+@NotBlank折旧方法+@NotNull使用部门+校验规则：残值=原值×残值率+月折旧额根据折旧方法公式自动计算）
  - P2-002-001-001-001-003 编写AssetCardUpdateDTO（@NotNull资产ID+同CreateDTO校验规则+已折旧资产不可修改原值和使用年限）
  - P2-002-001-001-001-004 编写AssetCardQueryDTO（资产编号+资产名称+资产分类+使用部门+资产状态+取得日期范围+保管人+折旧方法查询条件）
  - P2-002-001-001-001-005 编写AssetCardVO/AssetCardListVO（列表VO含资产编号/资产名称/资产分类名称/原值/累计折旧/净值/使用部门名称/状态+@JsonFormat日期格式+字典翻译字段+折旧方法名称翻译+资产状态标签颜色）
- P2-002-001-001-002 固定资产卡片Mapper开发
  - P2-002-001-001-002-001 编写AssetMapper接口（extends BaseMapperX<AssetEntity>+@Mapper+selectByAssetNo按编号查询+selectPageByCondition条件分页+selectByDept按部门查询+selectByCategory按分类查询+selectDepreciableAssets查询应折旧资产列表（状态=使用中+净值>残值+当期未折旧））
  - P2-002-001-001-002-002 编写AssetMapper.xml（resultMap映射+条件查询SQL含编号/名称/分类/部门/状态动态where+应折旧资产查询SQL含状态过滤+折旧月份过滤子查询）
- P2-002-001-001-003 固定资产卡片Service开发（CRUD+编码自动生成+折旧方法配置+增减变动联动）
  - P2-002-001-001-003-001 编写AssetService接口（extends IServiceX<AssetEntity>+create/draftCreate创建+update修改+delete删除+dispose处置+change变动+calculateDepreciation计算折旧+getAssetStats资产统计方法声明）
  - P2-002-001-001-003-002 编写AssetServiceImpl实现类（@Service+资产编号自动生成调用SerialNumberService.generate("ZC")+CRUD事务实现+折旧参数配置方法configDepreciation：根据折旧方法（平均年限法/双倍余额递减法/年数总和法/工作量法）计算月折旧额+公式实现：平均年限法=(原值-残值)/使用年限/12，双倍余额递减法=2/使用年限×期初净值，最后两年转为平均年限法，年数总和法=(原值-残值)×尚可使用年限/年数总和+月折旧额计算并保存+增减变动联动：变动单审核后自动更新资产卡片原值/累计折旧/净值+处置单审核后自动标记资产状态为已处置+@Transactional事务管理）
  - P2-002-001-001-003-003 业务校验逻辑（资产编号唯一性校验+启用日期不可早于取得日期+已折旧资产修改原值时需校验折旧重新计算+处置/报废状态资产不可变动+资产分类与科目联动校验+同一资产不可重复处置）
- P2-002-001-001-004 固定资产卡片Controller开发
  - P2-002-001-001-004-001 编写AssetController类（@RestController+@RequestMapping"/api/fa/asset"+@Tag"固定资产卡片"+注入AssetService）
  - P2-002-001-001-004-002 实现POST /create创建卡片接口（@RequestBody AssetCardCreateDTO+@Valid校验+自动计算残值/月折旧额+Service调用+返回Result<AssetCardVO>）
  - P2-002-001-001-004-003 实现PUT /update修改卡片接口（@RequestBody AssetCardUpdateDTO+@Valid校验+折旧方法变更时自动重算月折旧额+Service调用+返回Result<Void>）
  - P2-002-001-001-004-004 实现DELETE /delete/{id}删除卡片接口（@PathVariable id+仅允许草稿状态删除+未开始使用的资产可删除+返回Result<Void>）
  - P2-002-001-001-004-005 实现GET /page分页查询接口（AssetCardQueryDTO条件+Service调用+返回Result<PageResult<AssetCardListVO>>含净值计算列）
  - P2-002-001-001-004-006 实现GET /detail/{id}详情查询接口（@PathVariable id+返回Result<AssetCardVO>含折旧参数配置/累计折旧/净值/使用期间+折旧记录列表）
  - P2-002-001-001-004-007 补充Knife4j接口文档注解（@Operation描述+@Parameter参数说明+@Schema示例值）
  - P2-002-001-001-004-008 验证固定资产卡片接口（单元测试：CRUD全流程+折旧参数配置+月折旧额自动计算+折旧方法切换重算+异常场景处理）

#### P2-002-001-002 资产折旧计算接口
- P2-002-001-002-001 折旧计算Entity/DTO/VO定义（fa_depreciation主表+fa_depreciation_detail从表）
  - P2-002-001-002-001-001 编写DepreciationEntity类（@TableName"fa_depreciation"+@TableId折旧记录ID+折旧编号depreciation_no+会计期间period_id+折旧日期depreciation_date+折旧资产数量+折旧金额合计total_amount+状态status+制单人+制单日期+@TableLogic逻辑删除）
  - P2-002-001-002-001-002 编写DepreciationDetailEntity类（@TableName"fa_depreciation_detail"+@TableId明细ID+折旧记录ID depreciation_id+资产ID asset_id+折旧方法method+原值original_value+残值residual_value+月折旧额monthly_amount+累计折旧accumulated+本期折旧current_amount+净值net_value+备注）
  - P2-002-001-002-001-003 编写DepreciationExecuteDTO（@NotNull会计期间+资产分类可选+部门可选+折旧方法可选+全选/部分资产折旧标志）
- P2-002-001-002-002 折旧计算Mapper开发
  - P2-002-001-002-002-001 编写DepreciationMapper接口（extends BaseMapperX<DepreciationEntity>+@Mapper+selectByPeriod按期间查询+selectByAssetId按资产查折旧历史+selectLastDepreciation查询上期折旧记录）
  - P2-002-001-002-002-002 编写DepreciationDetailMapper接口（extends BaseMapperX<DepreciationDetailEntity>+@Mapper+selectByDepreciationId按折旧记录查明细+sumByAssetId汇总资产累计折旧+sumByPeriod按期间汇总折旧金额）
  - P2-002-001-002-002-003 编写DepreciationMapper.xml（resultMap含主从嵌套映射+按期间/资产查询SQL+上期折旧记录查询SQL含LAG窗口函数）
- P2-002-001-002-003 折旧计算Service开发（按折旧方法自动计算+折旧凭证生成）
  - P2-002-001-002-003-001 编写DepreciationService接口（extends IServiceX<DepreciationEntity>+executeDepreciation执行折旧计算+previewDepreciation预览折旧+reverseDepreciation反折旧+generateVoucher生成折旧凭证方法声明）
  - P2-002-001-002-003-002 编写DepreciationServiceImpl实现类（@Service+executeDepreciation执行方法：①查询当期应折旧资产列表（状态=使用中+净值>残值+当期未折旧）②遍历资产按折旧方法计算本期折旧额：平均年限法=月折旧额（固定值），双倍余额递减法=2/剩余使用年限×期初净值（判断是否转为平均年限法），年数总和法=(原值-残值)×剩余使用年限/年数总和，工作量法=单位折旧额×本期工作量③生成折旧记录主从表④更新资产卡片累计折旧+净值⑤自动生成折旧凭证：借-折旧费用科目/贷-累计折旧科目+凭证金额=本期折旧合计+previewDepreciation预览方法：同执行逻辑但不保存，返回折旧明细供预览+reverseDepreciation反折旧方法：删除折旧记录+恢复资产净值+冲销折旧凭证+@Transactional事务管理）
  - P2-002-001-002-003-003 业务校验逻辑（当期已执行折旧不允许重复执行+已结账期间不允许执行折旧+折旧金额必须≥0+凭证生成成功才算折旧完成+反折旧前校验凭证未被过账）
  - P2-002-001-002-003-004 验证折旧计算Service（单元测试：四种折旧方法计算正确性+月折旧额精度+累计折旧累加正确+残值保护+反折旧恢复验证+凭证生成正确+性能测试（大批量资产折旧））
- P2-002-001-002-004 折旧计算Controller开发
  - P2-002-001-002-004-001 编写DepreciationController类（@RestController+@RequestMapping"/api/fa/depreciation"+@Tag"资产折旧计算"+注入DepreciationService）
  - P2-002-001-002-004-002 实现POST /preview预览折旧接口（@RequestBody DepreciationExecuteDTO+返回Result<List<DepreciationDetailVO>>预览折旧明细列表含每项资产折旧金额）
  - P2-002-001-002-004-003 实现POST /execute执行折旧接口（@RequestBody DepreciationExecuteDTO+@Valid校验+返回Result<DepreciationVO>含折旧汇总信息）
  - P2-002-001-002-004-004 实现POST /reverse/{id}反折旧接口（@PathVariable id+状态校验+返回Result<Void>）
  - P2-002-001-002-004-005 实现GET /page折旧历史查询接口（期间+资产条件+Service调用+返回Result<PageResult<DepreciationListVO>>）
  - P2-002-001-002-004-006 补充Knife4j接口文档注解（@Operation描述+@Parameter参数说明+@Schema示例值）
  - P2-002-001-002-004-007 验证折旧接口（单元测试：预览→执行→反折旧全流程+不同折旧方法正确性+凭证生成校验+期间重复执行拦截）

#### P2-002-001-003 资产变动CRUD接口
- P2-002-001-003-001 资产变动Entity/DTO/VO定义（fa_asset_change主表）
  - P2-002-001-003-001-001 编写AssetChangeEntity类（@TableName"fa_asset_change"+@TableId变动ID+变动编号change_no+资产ID asset_id+变动类型change_type（原值增加/原值减少/使用年限变更/残值率变更/部门转移/使用人变更/折旧方法变更）+变动日期change_date+变动前值before_value+变动后值after_value+变动原因change_reason+关联凭证ID voucher_id+状态status+制单人+制单日期+@TableLogic逻辑删除）
  - P2-002-001-003-001-002 编写AssetChangeCreateDTO（@NotNull资产ID+@NotBlank变动类型+@NotNull变动日期+@NotBlank变动前值+@NotBlank变动后值+变动前后值不可相同校验@FieldNotEqual+变动后折旧重算标志）
  - P2-002-001-003-001-003 编写AssetChangeVO（详情VO含变动编号/资产编号/资产名称/变动类型名称/变动前后值对比/变动原因/关联凭证信息/制单人/+日期格式+字典翻译）
- P2-002-001-003-002 资产变动Mapper开发
  - P2-002-001-003-002-001 编写AssetChangeMapper接口（extends BaseMapperX<AssetChangeEntity>+@Mapper+selectByAssetId按资产查变动历史+selectByType按变动类型查询+selectPageByCondition条件分页+selectByVoucherId按凭证查变动记录）
  - P2-002-001-003-002-002 编写AssetChangeMapper.xml（resultMap映射+条件查询SQL含资产编号/变动类型/部门/日期范围动态where+按资产GROUP BY变动次数统计）
- P2-002-001-003-003 资产变动Service开发（变动类型+变动前后值记录+凭证生成+资产卡片联动更新）
  - P2-002-001-003-003-001 编写AssetChangeService接口（extends IServiceX<AssetChangeEntity>+create创建变动+update修改变动+delete删除变动+submit提交审核+approve审核通过执行变动+generateVoucher生成变动凭证方法声明）
  - P2-002-001-003-003-002 编写AssetChangeServiceImpl实现类（@Service+变动编号自动生成调用SerialNumberService.generate("ZCB")+create创建：记录变动前值beforeValue=资产卡片当前值+审核通过approve后执行applyChange方法：①原值增加/减少→更新资产卡片原值+重算月折旧额+重算净值②使用年限变更→重算月折旧额③残值率变更→重算残值+重算月折旧额④部门转移→更新使用部门⑤折旧方法变更→按新方法重算月折旧额+generateVoucher凭证生成：原值变动→借-固定资产/贷-营业外收入等+折旧方法变更→仅更新卡片参数不生成凭证+@Transactional事务管理）
  - P2-002-001-003-003-003 业务校验逻辑（变动类型与变动前后值一致性校验+变动日期不可早于资产启用日期+已处置/已报废资产不可变动+变动审核后不可修改+原值变动不可为负数+同一资产同一天不可重复做原值变动）
- P2-002-001-003-004 资产变动Controller开发
  - P2-002-001-003-004-001 编写AssetChangeController类（@RestController+@RequestMapping"/api/fa/change"+@Tag"资产变动"+注入AssetChangeService）
  - P2-002-001-003-004-002 实现POST /create创建变动接口（@RequestBody AssetChangeCreateDTO+@Valid校验+变动前值自动从资产卡片获取+返回Result<AssetChangeVO>）
  - P2-002-001-003-004-003 实现PUT /update修改变动接口（仅草稿状态可修改+返回Result<Void>）
  - P2-002-001-003-004-004 实现DELETE /delete/{id}删除变动接口（仅草稿状态可删除+返回Result<Void>）
  - P2-002-001-003-004-005 实现GET /page分页查询接口（AssetChangeQueryDTO条件+返回Result<PageResult<AssetChangeListVO>>含变动前后值对比列）
  - P2-002-001-003-004-006 实现GET /detail/{id}详情查询接口（@PathVariable id+返回Result<AssetChangeVO>含变动前后值对比+关联凭证+资产卡片当前信息）
  - P2-002-001-003-004-007 实现POST /submit/{id}提交审核接口（@PathVariable id+状态校验+调用审核引擎+返回Result<Void>）
  - P2-002-001-003-004-008 补充Knife4j接口文档注解（@Operation描述+@Parameter参数说明+@Schema示例值）
  - P2-002-001-003-004-009 验证资产变动接口（单元测试：各变动类型CRUD+审核通过后卡片联动更新+月折旧额重算+凭证生成+异常场景处理）

#### P2-002-001-004 资产清理处置CRUD接口
- P2-002-001-004-001 资产清理处置Entity/DTO/VO定义（fa_asset_disposal主表+fa_asset_disposal_detail从表）
  - P2-002-001-004-001-001 编写AssetDisposalEntity类（@TableName"fa_asset_disposal"+@TableId处置ID+处置编号disposal_no+处置日期disposal_date+处置方式disposal_type（出售/报废/捐赠/盘亏）+处置资产数量+原值合计+累计折旧合计+净值合计+处置收入disposal_income+处置费用disposal_expense+处置损益disposal_gain_loss+关联凭证ID voucher_id+状态status+制单人+制单日期+@TableLogic逻辑删除）
  - P2-002-001-004-001-002 编写AssetDisposalDetailEntity类（@TableName"fa_asset_disposal_detail"+@TableId明细ID+处置ID disposal_id+资产ID asset_id+原值original_value+累计折旧accumulated_depreciation+净值net_value+处置收入+处置费用+处置损益+备注remark）
  - P2-002-001-004-001-003 编写AssetDisposalCreateDTO（@NotNull资产选择列表+@NotNull处置方式+@NotNull处置日期+处置收入(出售时必填)+处置费用可选+处置损益自动计算=处置收入-处置费用-净值）
  - P2-002-001-004-001-004 编写AssetDisposalVO（详情VO含处置编号/处置方式名称/资产列表/原值合计/累计折旧合计/净值合计/处置收入/处置费用/处置损益/关联凭证+@JsonFormat日期格式）
- P2-002-001-004-002 资产清理处置Mapper开发
  - P2-002-001-004-002-001 编写AssetDisposalMapper接口（extends BaseMapperX<AssetDisposalEntity>+@Mapper+selectByDisposalNo按编号查询+selectPageByCondition条件分页+selectByAssetId按资产查处置记录+selectByType按处置方式统计）
  - P2-002-001-004-002-002 编写AssetDisposalDetailMapper接口（extends BaseMapperX<AssetDisposalDetailEntity>+@Mapper+selectByDisposalId按处置ID查明细+batchInsert批量插入+sumByType按处置方式汇总）
  - P2-002-001-004-002-003 编写AssetDisposalMapper.xml（resultMap含主从嵌套映射+条件查询SQL含日期/处置方式/资产部门动态where+处置统计SQL按方式汇总金额）
- P2-002-001-004-003 资产清理处置Service开发（处置方式+处置收入+凭证生成+资产状态更新）
  - P2-002-001-004-003-001 编写AssetDisposalService接口（extends IServiceX<AssetDisposalEntity>+create创建处置+update修改处置+delete删除处置+submit提交审核+approve审核通过执行处置+generateVoucher生成处置凭证+calculateGainLoss计算处置损益方法声明）
  - P2-002-001-004-003-002 编写AssetDisposalServiceImpl实现类（@Service+处置编号自动生成调用SerialNumberService.generate("ZCCZ")+create创建：选择资产后自动获取卡片原值/累计折旧/净值+损益计算：处置损益=处置收入-处置费用-资产净值+approve审核通过后executeDisposal方法：①更新资产卡片状态为已处置/已报废②清理累计折旧③生成处置凭证：出售→借-银行存款(处置收入)/借-累计折旧/贷-固定资产(原值)/借或贷-资产处置损益，报废→借-累计折旧/借-营业外支出/贷-固定资产，捐赠→借-累计折旧/借-营业外支出/贷-固定资产，盘亏→借-累计折旧/借-待处理财产损溢/贷-固定资产+@Transactional事务管理）
  - P2-002-001-004-003-003 业务校验逻辑（已处置/已报废资产不可重复处置+处置日期不可早于资产取得日期+出售方式必须填写处置收入+处置损益自动计算精度校验+资产卡片与明细数据一致性校验+凭证科目映射校验）
- P2-002-001-004-004 资产清理处置Controller开发
  - P2-002-001-004-004-001 编写AssetDisposalController类（@RestController+@RequestMapping"/api/fa/disposal"+@Tag"资产清理处置"+注入AssetDisposalService）
  - P2-002-001-004-004-002 实现POST /create创建处置接口（@RequestBody AssetDisposalCreateDTO+@Valid校验+自动计算处置损益+返回Result<AssetDisposalVO>）
  - P2-002-001-004-004-003 实现PUT /update修改处置接口（仅草稿状态可修改+返回Result<Void>）
  - P2-002-001-004-004-004 实现DELETE /delete/{id}删除处置接口（仅草稿状态可删除+返回Result<Void>）
  - P2-002-001-004-004-005 实现GET /page分页查询接口（AssetDisposalQueryDTO条件+返回Result<PageResult<AssetDisposalListVO>>含处置损益列）
  - P2-002-001-004-004-006 实现GET /detail/{id}详情查询接口（@PathVariable id+返回Result<AssetDisposalVO>含资产明细列表+损益计算明细+关联凭证信息）
  - P2-002-001-004-004-007 实现POST /submit/{id}提交审核接口（@PathVariable id+状态校验+调用审核引擎+返回Result<Void>）
  - P2-002-001-004-004-008 补充Knife4j接口文档注解（@Operation描述+@Parameter参数说明+@Schema示例值）
  - P2-002-001-004-004-009 验证资产处置接口（单元测试：各处置方式CRUD+损益计算正确+凭证生成+卡片状态更新+资产不可重复处置+异常场景处理）

### P2-002-002 固定资产前端页面

#### P2-002-002-000 固定资产工作台页（P02：资产总数/资产总值/本月折旧/待处置资产KPI卡片+资产分布图+折旧趋势）
- P2-002-002-000-001 工作台KPI卡片区+图表区+待办区+联调验证
  - P2-002-002-000-001-001 KPI卡片+图表+待办列表+快捷入口+联调

#### P2-002-002-001 固定资产卡片列表页
- P2-002-002-001-001 搜索条件区+数据表格区+操作按钮区+联调验证
  - P2-002-002-001-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-002-002-001-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-002-002-001-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-002-002-002 新增/编辑固定资产卡片表单页
- P2-002-002-002-001 基本信息区+折旧配置区+附属设备区+联调验证
  - P2-002-002-002-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-002-002-002-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-002-002-002-001-003 基本信息区异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-002-002-003 资产折旧列表页
- P2-002-002-003-001 搜索条件区+折旧数据表格区+折旧执行按钮+联调验证
  - P2-002-002-003-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-002-002-003-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-002-002-003-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-002-002-004 资产变动列表页
- P2-002-002-004-001 搜索条件区+数据表格区+联调验证
  - P2-002-002-004-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-002-002-004-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-002-002-004-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-002-002-005 资产清理处置列表页
- P2-002-002-005-001 搜索条件区+数据表格区+联调验证
  - P2-002-002-005-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-002-002-005-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-002-002-005-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）
  - P2-002-002-006 资产统计（P10报表页：按分类/部门/状态维度+折旧趋势+增减变动表）
    - P2-002-002-006-001 后端开发（资产统计SQL+折旧趋势SQL+增减变动SQL）
      - P2-002-002-006-001-001 编写资产统计SQL（按分类/部门/状态GROUP BY+原值+累计折旧+净值）
      - P2-002-002-006-001-002 编写折旧趋势SQL（按月份+SUM月折旧额+累计折旧+折旧方法分布）
      - P2-002-002-006-001-003 验证后端开发SQL（统计正确+折旧趋势准确+性能合理）
    - P2-002-002-006-002 前端开发（P10报表页：筛选+统计表+折旧趋势图+增减变动表）
      - P2-002-002-006-002-001 实现筛选区+统计表格（分类/部门/状态维度+原值/折旧/净值+合计行）
      - P2-002-002-006-002-002 实现折旧趋势图+增减变动表（ECharts折线图+增减变动列+导出）

---

## P2-003 期末处理模块开发

> **依赖**：P1-012（凭证）、P1-013（账簿）、P1-014（报表）  
> **概述**：完成期末结账/反结账、期末调汇、期末结转

### P2-003-001 期末处理后端开发

#### P2-003-001-001 期末结账接口
- P2-003-001-001-001 结账记录Entity/DTO/VO定义（fin_period_close）
  - P2-003-001-001-001-001 编写PeriodCloseEntity类（@TableName"fin_period_close"+@TableId结账ID+会计年度fiscal_year+会计期间period_id+结账状态close_status+结账日期close_date+结账人closed_by+凭证总数voucher_count+已过账数posted_count+下一期间开放标志next_opened+备注remark+@TableLogic逻辑删除）
  - P2-003-001-001-001-002 编写PeriodCloseExecuteDTO（@NotNull会计期间+校验通过标志check_passed+结账操作类型close_type）
  - P2-003-001-001-001-003 编写PeriodCloseCheckResultVO（校验结果VO含校验通过项列表/失败项列表/失败原因详细说明/状态/时间+@JsonFormat日期格式）
  - P2-003-001-001-001-004 编写PeriodCloseResultVO（操作结果VO含结账期间/结账时间/下一期间信息/结账后期间状态+@JsonFormat日期格式）
- P2-003-001-001-002 结账校验Service开发
  - P2-003-001-001-002-001 编写PeriodCloseCheckService接口（checkBeforeClose结账前全面校验+checkVouchersPosted校验凭证已全部过账+checkProfitLossBalance校验损益科目余额为0+checkInventorySettled校验库存已全部结算+checkSubLedgerBalanced校验明细账与总账平衡+checkBankReconciled校验银行对账已完成+getCheckResult获取校验结果详情方法声明）
  - P2-003-001-001-002-002 编写PeriodCloseCheckServiceImpl实现类（@Service+checkBeforeClose方法：①查询当期所有凭证SELECT COUNT(*) FROM fin_voucher WHERE period_id=? AND status!='posted'，若有未过账凭证返回失败项"尚有N张凭证未过账"②查询损益科目余额SELECT SUM(balance) FROM fin_account_balance WHERE period_id=? AND account_type IN ('INCOME','EXPENSE')，若余额≠0返回失败项"损益科目尚有余额未结转"③库存校验：查询库存模块当期是否有未结算出入库单④明细账与总账平衡校验：按科目分别SUM明细账与总账发生额，差异≠0返回失败项⑤返回CheckResultVO含checkItems列表每项标通过/失败+失败原因+@Transactional(readOnly=true)）
- P2-003-001-001-003 结账执行Service开发
  - P2-003-001-001-003-001 编写PeriodCloseService接口（executeClose执行结账+executeReverseClose执行反结账+getCloseHistory查询结账历史+getCurrentPeriodStatus查询当前期间状态方法声明）
  - P2-003-001-001-003-002 编写PeriodCloseServiceImpl实现类（@Service+executeClose方法：①再次执行结账前校验②更新会计期间表fin_accounting_period设置is_closed=true③创建结账记录fin_period_close④计算并结转本年利润科目余额⑤打开下一会计期间设置is_open=true⑥生成结账操作日志+executeReverseClose反结账方法：①校验下一期间无任何凭证及业务单据②恢复当期会计期间is_closed=false③更新结账记录状态为已反结账④恢复上一期间开放+@Transactional事务管理+乐观锁防并发）
  - P2-003-001-001-003-003 业务校验逻辑（已结账期间不可重复结账+未通过结账前校验不可执行结账+反结账前校验下一期间无数据+结账/反结账操作必须由财务主管权限执行+同一期间不可并发结账）
- P2-003-001-001-004 期末结账Controller开发
  - P2-003-001-001-004-001 编写PeriodCloseController类（@RestController+@RequestMapping"/api/fin/period/close"+@Tag"期末结账"+注入PeriodCloseCheckService及PeriodCloseService）
  - P2-003-001-001-004-002 实现POST /check校验接口（@RequestBody PeriodCloseExecuteDTO+返回Result<PeriodCloseCheckResultVO>含所有校验项通过/失败明细）
  - P2-003-001-001-004-003 实现POST /execute执行结账接口（@RequestBody PeriodCloseExecuteDTO+@Valid+先校验后执行+返回Result<PeriodCloseResultVO>）
  - P2-003-001-001-004-004 实现POST /reverse反结账接口（@RequestBody PeriodCloseExecuteDTO+反结账校验+返回Result<PeriodCloseResultVO>）
  - P2-003-001-001-004-005 实现GET /history结账历史查询接口（返回Result<List<PeriodCloseHistoryVO>>含历史结账/反结账记录）
  - P2-003-001-001-004-006 补充Knife4j接口文档注解（@Operation描述+@Parameter参数说明+@Schema示例值）
  - P2-003-001-001-004-007 验证期末结账接口（单元测试：校验场景（未过账凭证/损益未结转/库存未结算）+正常结账流程+反结账流程+重复结账拦截+权限校验+并发控制）

#### P2-003-001-002 期末反结账接口
- P2-003-001-002-001 反结账校验Service开发
  - P2-003-001-002-001-001 编写PeriodReverseCloseCheckService接口（checkBeforeReverseClose反结账前校验+checkNextPeriodNoVoucher校验下一期无凭证+checkNextPeriodNoBizDoc校验下一期无业务单据+getCheckResult获取校验结果方法声明）
  - P2-003-001-002-001-002 编写PeriodReverseCloseCheckServiceImpl实现类（@Service+checkBeforeReverseClose方法：①查询下一期间凭证SELECT COUNT(*) FROM fin_voucher WHERE period_id=?，若count>0返回失败"下一期间已有N张凭证，不可反结账"②查询下一期间业务单据统计：采购入库单/销售出库单/应收单/应付单/收款单/付款单/资产变动单等，任一类>0返回失败③查询下一期间是否已执行折旧/调汇/结转④返回CheckResultVO+@Transactional(readOnly=true)）
- P2-003-001-002-002 反结账Controller开发
  - P2-003-001-002-002-001 编写PeriodReverseCloseController类（@RestController+@RequestMapping"/api/fin/period/reverse"+@Tag"期末反结账"+注入PeriodReverseCloseCheckService及PeriodCloseService）
  - P2-003-001-002-002-002 实现POST /check反结账校验接口（@RequestBody PeriodCloseExecuteDTO+返回Result<PeriodCloseCheckResultVO>）
  - P2-003-001-002-002-003 实现POST /execute执行反结账接口（@RequestBody PeriodCloseExecuteDTO+@Valid+先校验后执行+返回Result<PeriodCloseResultVO>）
  - P2-003-001-002-002-004 补充Knife4j接口文档注解（@Operation描述+@Parameter参数说明+@Schema示例值）
  - P2-003-001-002-002-005 验证反结账接口（单元测试：正常反结账+下一期有凭证拦截+下一期有业务单据拦截+反结账后期间状态恢复+权限校验）

#### P2-003-001-003 期末调汇接口
- P2-003-001-003-001 期末调汇Entity/DTO/VO定义（fin_exchange_gain_loss）
  - P2-003-001-003-001-001 编写ExchangeGainLossEntity类（@TableName"fin_exchange_gain_loss"+@TableId调汇ID+调汇编号exchange_no+会计期间period_id+调汇日期exchange_date+调汇汇率exchange_rate+调汇前余额before_balance+调汇后余额after_balance+汇兑损益金额gain_loss_amount+关联凭证ID voucher_id+状态status+备注remark+@TableLogic逻辑删除）
  - P2-003-001-003-001-002 编写ExchangeGainLossDetailEntity类（@TableName"fin_exchange_gain_loss_detail"+@TableId明细ID+调汇IDexchange_id+科目IDaccount_id+币种currency_id+外币余额foreign_balance+原汇率original_rate+新汇率new_rate+本币原金额original_amount+本币新金额new_amount+汇兑差额difference+备注remark）
  - P2-003-001-003-001-003 编写ExchangeGainLossExecuteDTO（@NotNull会计期间+@NotNull期末汇率+币种可选（默认所有外币科目）+调汇日期+试算标志preview_flag）
  - P2-003-001-003-001-004 编写ExchangeGainLossVO（结果VO含调汇编号/币种/外币余额合计/调汇前本币合计/调汇后本币合计/汇兑损益合计/关联凭证号+明细列表+@JsonFormat日期格式）
- P2-003-001-003-002 期末调汇Service开发（外币科目按期末汇率重新计算+汇兑损益凭证自动生成）
  - P2-003-001-003-002-001 编写ExchangeGainLossService接口（previewExchangeGainLoss预览调汇结果+executeExchangeGainLoss执行调汇+reverseExchangeGainLoss反调汇+generateVoucher生成汇兑损益凭证+getHistory查询调汇历史方法声明）
  - P2-003-001-003-002-002 编写ExchangeGainLossServiceImpl实现类（@Service+计算方法：①查询所有外币科目期末余额SELECT account_id,currency_id,SUM(debit)-SUM(credit) foreign_balance FROM fin_voucher_detail WHERE period_id<=? AND currency_id IS NOT NULL GROUP BY account_id,currency_id HAVING foreign_balance<>0②遍历外币科目：原币余额=外币余额×原汇率，新币余额=外币余额×期末汇率，汇兑差额=新币余额-原币余额③若外币余额>0（借方余额），汇兑差额>0→借-外币科目/贷-汇兑收益，汇兑差额<0→借-汇兑损失/贷-外币科目；若外币余额<0（贷方余额），方向相反④生成调汇记录+自动生成汇兑损益凭证+previewExchangeGainLoss方法：同计算逻辑但不保存，返回预览结果供确认+reverseExchangeGainLoss反调汇：删除调汇记录+冲销汇兑损益凭证+@Transactional事务管理）
  - P2-003-001-003-002-003 业务校验逻辑（汇兑损益凭证科目必须已定义+期末汇率必须大于0+已结账期间不可执行调汇+同一期间不可重复调汇+调汇后外币科目本币余额=外币余额×新汇率，本币余额必须一致）
- P2-003-001-003-003 期末调汇Controller开发
  - P2-003-001-003-003-001 编写ExchangeGainLossController类（@RestController+@RequestMapping"/api/fin/exchange"+@Tag"期末调汇"+注入ExchangeGainLossService）
  - P2-003-001-003-003-002 实现POST /preview预览调汇接口（@RequestBody ExchangeGainLossExecuteDTO+返回Result<ExchangeGainLossPreviewVO>含各科目汇兑损益明细）
  - P2-003-001-003-003-003 实现POST /execute执行调汇接口（@RequestBody ExchangeGainLossExecuteDTO+@Valid+返回Result<ExchangeGainLossVO>含凭证信息）
  - P2-003-001-003-003-004 实现POST /reverse/{id}反调汇接口（@PathVariable id+校验未结账+返回Result<Void>）
  - P2-003-001-003-003-005 实现GET /history调汇历史查询接口（期间+币种条件+返回Result<PageResult<ExchangeGainLossListVO>>）
  - P2-003-001-003-003-006 补充Knife4j接口文档注解（@Operation描述+@Parameter参数说明+@Schema示例值）
  - P2-003-001-003-003-007 验证调汇接口（单元测试：预览+执行+反调汇全流程+汇兑损益计算正确（资产类/负债类方向不同）+凭证生成验证+重复调汇拦截）

#### P2-003-001-004 期末结转接口
- P2-003-001-004-001 期末结转Entity/DTO/VO定义（fin_period_carry_forward）
  - P2-003-001-004-001-001 编写PeriodCarryForwardEntity类（@TableName"fin_period_carry_forward"+@TableId结转ID+结转编号carry_no+会计期间period_id+结转类型carry_type（损益结转/成本结转/增值税结转/所得税结转/利润分配结转）+结转金额carry_amount+转出科目from_account_id+转入科目to_account_id+关联凭证ID voucher_id+状态status+备注remark+@TableLogic逻辑删除）
  - P2-003-001-004-001-002 编写CarryForwardExecuteDTO（@NotNull会计期间+@NotNull结转类型（多选）+试算标志preview_flag）
  - P2-003-001-004-001-003 编写CarryForwardVO（结果VO含结转类型/转出科目/转入科目/结转金额/生成的凭证号+@JsonFormat日期格式）
- P2-003-001-004-002 损益结转Service开发（收入/费用科目余额转利润+自动生成结转凭证）
  - P2-003-001-004-002-001 编写CarryForwardService接口（previewCarryForward预览结转+executeCarryForward执行结转+reverseCarryForward反结转+generateVoucher生成结转凭证+getHistory查询结转历史方法声明）
  - P2-003-001-004-002-002 编写CarryForwardServiceImpl实现类（@Service+executeCarryForward方法按结转类型分别处理：①损益结转=查询所有收入类科目余额(贷方余额)+费用类科目余额(借方余额)→结转至"本年利润"科目②成本结转=生产成本/制造费用结转至"库存商品"③增值税结转=销项税额/进项税额/进项税额转出/已交税金各明细科目余额结转至"未交增值税"④所得税结转=所得税费用结转至"本年利润"⑤利润分配结转=本年利润结转至"利润分配-未分配利润"+每种结转生成对应结转凭证+previewCarryForward预览：同计算逻辑返回试算平衡表+reverseCarryForward反结转：删除结转记录+冲销凭证+@Transactional事务管理）
  - P2-003-001-004-002-003 业务校验逻辑（已结账期间不可执行结转+结转前所有业务凭证必须已过账+同一期间同类型结转不可重复+结转凭证金额必须不为0+年度结转必须在12月结账后进行）
- P2-003-001-004-003 期末结转Controller开发
  - P2-003-001-004-003-001 编写CarryForwardController类（@RestController+@RequestMapping"/api/fin/carryforward"+@Tag"期末结转"+注入CarryForwardService）
  - P2-003-001-004-003-002 实现POST /preview预览结转接口（@RequestBody CarryForwardExecuteDTO+返回Result<CarryForwardPreviewVO>含各类型结转金额/分录预览）
  - P2-003-001-004-003-003 实现POST /execute执行结转接口（@RequestBody CarryForwardExecuteDTO+@Valid+返回Result<List<CarryForwardVO>>含每个结转类型的凭证信息）
  - P2-003-001-004-003-004 实现POST /reverse/{id}反结转接口（@PathVariable id+校验+返回Result<Void>）
  - P2-003-001-004-003-005 实现GET /history结转历史查询接口（期间+类型条件+返回Result<PageResult<CarryForwardListVO>>）
  - P2-003-001-004-003-006 补充Knife4j接口文档注解（@Operation描述+@Parameter参数说明+@Schema示例值）
  - P2-003-001-004-003-007 验证结转接口（单元测试：各结转类型（损益/成本/增值税/所得税/利润分配）正确性+结转金额计算+凭证生成+反结转+重复结转拦截+年度结转）

### P2-003-002 期末处理前端页面

#### P2-003-002-000 期末处理工作台页（P02：当前会计期间/已结账期间数/待调汇笔数KPI+期末处理进度+快捷操作）
- P2-003-002-000-001 工作台KPI卡片区+图表区+待办区+联调验证
  - P2-003-002-000-001-001 KPI卡片+图表+待办列表+快捷入口+联调

#### P2-003-002-001 期末结账页
- P2-003-002-001-001 期间选择+结账校验状态+结账执行按钮+联调验证
  - P2-003-002-001-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-003-002-001-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-003-002-001-001-003 期间选择异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-003-002-002 期末调汇页
- P2-003-002-002-001 调汇参数配置+调汇执行+调汇结果展示+联调验证
  - P2-003-002-002-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-003-002-002-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-003-002-002-001-003 调汇参数配置异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-003-002-003 期末结转页
- P2-003-002-003-001 结转参数配置+结转执行+结转凭证展示+联调验证
  - P2-003-002-003-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-003-002-003-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-003-002-003-001-003 结转参数配置异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

---

## P2-004 样品管理模块开发

> **依赖**：P0-007（商品）、P0-008（客户）、P0-010（仓库）
> **概述**：完成样品申请→发货→归还全链路

### P2-004-001 样品管理后端开发

#### P2-004-001-001 样品申请CRUD接口
- P2-004-001-001-001 样品申请Entity/DTO/VO定义+Mapper开发（sample_apply+sample_apply_detail）
  - P2-004-001-001-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P2-004-001-001-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P2-004-001-001-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P2-004-001-001-002 样品申请Service开发（主从CRUD+编码自动生成+审核流转）
  - P2-004-001-001-002-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P2-004-001-001-002-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P2-004-001-001-002-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P2-004-001-001-003 样品申请Controller开发
  - P2-004-001-001-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-004-001-001-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-004-001-001-003-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-004-001-002 样品发货CRUD接口
- P2-004-001-002-001 样品发货Entity/Mapper/Service/Controller开发（审核后自动扣减库存+出库单关联）
  - P2-004-001-002-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-004-001-002-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-004-001-002-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-004-001-003 样品采购CRUD接口
- P2-004-001-003-001 样品采购Entity/Mapper/Service/Controller开发（主从CRUD+采购入库关联）
  - P2-004-001-003-001-001 定义Controller类（@RestController+@RequestMapping+@Tag+注入Service）
  - P2-004-001-003-001-002 实现新增接口（POST+@RequestBody+参数校验+Service调用+响应包装）
  - P2-004-001-003-001-003 实现修改/删除接口（PUT/DELETE+参数校验+Service调用+响应包装）
  - P2-004-001-003-001-004 实现查询接口（GET+分页+条件查询+Service调用+响应包装）
  - P2-004-001-003-001-005 补充Knife4j接口文档注解（@Operation+@Schema+示例值）

#### P2-004-001-004 样品入库CRUD接口
- P2-004-001-004-001 样品入库Entity/Mapper/Service/Controller开发（审核后自动增加库存+采购单关联+入库单关联）
  - P2-004-001-004-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-004-001-004-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-004-001-004-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-004-001-005 样品归还CRUD接口
- P2-004-001-005-001 样品归还Entity/Mapper/Service/Controller开发（审核后自动增加库存+出库冲回）
  - P2-004-001-005-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-004-001-005-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-004-001-005-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-004-001-006 样品库存查询接口
- P2-004-001-006-001 样品库存查询Service+Controller开发（商品+仓库维度+库存余量+在途+借出统计）
  - P2-004-001-006-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-004-001-006-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-004-001-006-001-003 补充接口文档注解（@Operation+@Schema+示例值）

### P2-004-002 样品管理前端页面

#### P2-004-002-000 样品管理工作台页（P02：样品库存数/借用中/待采购KPI+样品流转趋势+逾期归还预警）
- P2-004-002-000-001 工作台KPI卡片区+图表区+待办区+联调验证
  - P2-004-002-000-001-001 KPI卡片+图表+待办列表+快捷入口+联调

#### P2-004-002-001 样品申请主从列表页
- P2-004-002-001-001 搜索条件区+主表数据表格+从表明细区+操作按钮区+联调验证
  - P2-004-002-001-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-004-002-001-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-004-002-001-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-004-002-002 新增/编辑样品申请主从表单页
- P2-004-002-002-001 主表字段+从表明细区+客户选择器+商品选择器+联调验证
  - P2-004-002-002-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P2-004-002-002-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P2-004-002-002-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）

#### P2-004-002-003 样品发货列表页
- P2-004-002-003-001 搜索条件区+数据表格区+联调验证
  - P2-004-002-003-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-004-002-003-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-004-002-003-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-004-002-004 样品采购主从列表页
- P2-004-002-004-001 搜索条件区+主表数据表格+从表明细区+联调验证
  - P2-004-002-004-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-004-002-004-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-004-002-004-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-004-002-005 样品入库列表页
- P2-004-002-005-001 搜索条件区+数据表格区+联调验证
  - P2-004-002-005-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-004-002-005-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-004-002-005-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-004-002-006 样品归还列表页
- P2-004-002-006-001 搜索条件区+数据表格区+联调验证
  - P2-004-002-006-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-004-002-006-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-004-002-006-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-004-002-007 样品库存查询页
- P2-004-002-007-001 报表条件区+库存数据表格+统计区+联调验证
  - P2-004-002-007-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-004-002-007-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-004-002-007-001-003 实现报表条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

---

## P2-005 借用管理模块开发

> **依赖**：P0-007（商品）、P0-010（仓库）
> **概述**：完成借用申请→出库→归还+借入→归还全链路

### P2-005-001 借用管理后端开发

#### P2-005-001-000 借用申请单CRUD接口（borrow_application：设计文档11.24.2要求，借用出库的前置申请单据）
- P2-005-001-000-001 借用申请单Entity/DTO/VO定义+Mapper开发（borrow_application主表+borrow_application_detail从表）
  - P2-005-001-000-001-001 定义Entity类（@TableName"borrow_application"+@TableId+申请编号+申请人+借用日期+预计归还日期+借用事由+状态+逻辑删除）
  - P2-005-001-000-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+校验注解）
  - P2-005-001-000-001-003 定义VO类（列表VO/详情VO+日期格式+字典翻译）
- P2-005-001-000-002 借用申请单Service开发（主从CRUD+编码自动生成+审核+下推借用出库单）
  - P2-005-001-000-002-001 定义Service接口+实现ServiceImpl（CRUD+审核后自动下推借用出库单+状态流转）
  - P2-005-001-000-002-002 业务校验逻辑（申请编号唯一性+预计归还日期>借用日期+审核状态校验）
- P2-005-001-000-003 借用申请单Controller开发
  - P2-005-001-000-003-001 定义Controller类+@RequestMapping路径+实现CRUD+审核+下推接口

#### P2-005-001-001 借用出库单CRUD接口
- P2-005-001-001-001 借用出库单Entity/DTO/VO定义+Mapper开发（borrow_apply+borrow_apply_detail）
  - P2-005-001-001-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P2-005-001-001-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P2-005-001-001-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P2-005-001-001-002 借用出库单Service开发（主从CRUD+编码自动生成+审核+扣减库存）
  - P2-005-001-001-002-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P2-005-001-001-002-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P2-005-001-001-002-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P2-005-001-001-003 借用出库单Controller开发
  - P2-005-001-001-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-005-001-001-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-005-001-001-003-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-005-001-002 借用归还单CRUD接口
- P2-005-001-002-001 借用归还单Entity/Mapper/Service/Controller开发（审核后自动增加库存）
  - P2-005-001-002-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-005-001-002-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-005-001-002-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-005-001-003 借入单CRUD接口
- P2-005-001-003-001 借入单Entity/Mapper/Service/Controller开发（主从CRUD+编码自动生成+供应商关联）
  - P2-005-001-003-001-001 定义Controller类（@RestController+@RequestMapping+@Tag+注入Service）
  - P2-005-001-003-001-002 实现新增接口（POST+@RequestBody+参数校验+Service调用+响应包装）
  - P2-005-001-003-001-003 实现修改/删除接口（PUT/DELETE+参数校验+Service调用+响应包装）
  - P2-005-001-003-001-004 实现查询接口（GET+分页+条件查询+Service调用+响应包装）
  - P2-005-001-003-001-005 补充Knife4j接口文档注解（@Operation+@Schema+示例值）

#### P2-005-001-004 借入归还单CRUD接口
- P2-005-001-004-001 借入归还单Entity/Mapper/Service/Controller开发（审核后自动扣减库存+供应商归还确认）
  - P2-005-001-004-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-005-001-004-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-005-001-004-001-003 补充接口文档注解（@Operation+@Schema+示例值）

### P2-005-002 借用管理前端页面

#### P2-005-002-000 借用管理工作台页（P02：借用中数量/逾期未还数/本月借用金额KPI+借用趋势图+逾期预警列表）
- P2-005-002-000-001 工作台KPI卡片区+图表区+联调验证
  - P2-005-002-000-001-001 KPI卡片+借用趋势图+逾期预警列表+联调

#### P2-005-002-000a 借用申请单主从列表页（P03）
- P2-005-002-000a-001 搜索条件区+主表数据表格+从表明细区+操作按钮区+联调验证
  - P2-005-002-000a-001-001 搜索表单布局+搜索字段组件+搜索交互

#### P2-005-002-000b 新增/编辑借用申请单主从表单页（P06）
- P2-005-002-000b-001 主表字段+从表明细区+商品选择器+预计归还日期+借用事由+联调验证
  - P2-005-002-000b-001-001 主表表单布局+字段组件渲染+联动逻辑+提交审核后下推借用出库单

#### P2-005-002-001 借用出库单主从列表页
- P2-005-002-001-001 搜索条件区+主表数据表格+从表明细区+操作按钮区+联调验证
  - P2-005-002-001-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-005-002-001-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-005-002-001-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-005-002-002 新增/编辑借用出库单主从表单页
- P2-005-002-002-001 主表字段+从表明细区+商品选择器+仓库选择器+联调验证
  - P2-005-002-002-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P2-005-002-002-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P2-005-002-002-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）

#### P2-005-002-003 借用归还单列表页
- P2-005-002-003-001 搜索条件区+数据表格区+联调验证
  - P2-005-002-003-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-005-002-003-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-005-002-003-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-005-002-004 借入单主从列表页
- P2-005-002-004-001 搜索条件区+主表数据表格+从表明细区+联调验证
  - P2-005-002-004-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-005-002-004-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-005-002-004-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-005-002-005 新增/编辑借入单主从表单页
- P2-005-002-005-001 主表字段+从表明细区+供应商选择器+联调验证
  - P2-005-002-005-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P2-005-002-005-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P2-005-002-005-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）

#### P2-005-002-006 借入归还单列表页
- P2-005-002-006-001 搜索条件区+数据表格区+联调验证
  - P2-005-002-006-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-005-002-006-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-005-002-006-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

---

## P2-006 租赁管理模块开发

> **依赖**：P0-008（客户）、P0-007（商品）
> **概述**：完成租赁合同→出库→计费→收款→归还+租入→归还全链路

### P2-006-001 租赁管理后端开发

#### P2-006-001-001 租赁合同CRUD接口
- P2-006-001-001-001 租赁合同Entity/DTO/VO定义+Mapper开发（lease_contract+lease_contract_detail）
  - P2-006-001-001-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P2-006-001-001-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P2-006-001-001-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P2-006-001-001-002 租赁合同Service开发（主从CRUD+编码自动生成+合同状态流转）
  - P2-006-001-001-002-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P2-006-001-001-002-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P2-006-001-001-002-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P2-006-001-001-003 租赁合同Controller开发
  - P2-006-001-001-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-006-001-001-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-006-001-001-003-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-006-001-002 租赁出库单CRUD接口
- P2-006-001-002-001 租赁出库单Entity/Mapper/Service/Controller开发（审核后自动扣减库存+合同关联）
  - P2-006-001-002-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-006-001-002-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-006-001-002-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-006-001-003 租赁计费CRUD接口
- P2-006-001-003-001 租赁计费Entity/Mapper/Service/Controller开发（合同到期自动生成计费单+应收款联动）
  - P2-006-001-003-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-006-001-003-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-006-001-003-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-006-001-004 租赁收款单CRUD接口
- P2-006-001-004-001 租赁收款单Entity/Mapper/Service/Controller开发（收款确认+收款核销+结算账户联动）
  - P2-006-001-004-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-006-001-004-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-006-001-004-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-006-001-005 租赁归还单CRUD接口
- P2-006-001-005-001 租赁归还单Entity/Mapper/Service/Controller开发（审核后自动增加库存+合同状态更新）
  - P2-006-001-005-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-006-001-005-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-006-001-005-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-006-001-006 租入单CRUD接口
- P2-006-001-006-001 租入单Entity/Mapper/Service/Controller开发（主从CRUD+供应商关联+租入合同关联）
  - P2-006-001-006-001-001 定义Controller类（@RestController+@RequestMapping+@Tag+注入Service）
  - P2-006-001-006-001-002 实现新增接口（POST+@RequestBody+参数校验+Service调用+响应包装）
  - P2-006-001-006-001-003 实现修改/删除接口（PUT/DELETE+参数校验+Service调用+响应包装）
  - P2-006-001-006-001-004 实现查询接口（GET+分页+条件查询+Service调用+响应包装）
  - P2-006-001-006-001-005 补充Knife4j接口文档注解（@Operation+@Schema+示例值）

#### P2-006-001-007 租入归还单CRUD接口
- P2-006-001-007-001 租入归还单Entity/Mapper/Service/Controller开发（审核后自动扣减库存+租入合同状态更新）
  - P2-006-001-007-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-006-001-007-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-006-001-007-001-003 补充接口文档注解（@Operation+@Schema+示例值）

### P2-006-002 租赁管理前端页面

#### P2-006-002-000 租赁管理工作台页（P02：租赁合同数/本月应收租金/逾期租金KPI+合同到期预警+收款进度）
- P2-006-002-000-001 工作台KPI卡片区+图表区+待办区+联调验证
  - P2-006-002-000-001-001 KPI卡片+图表+待办列表+快捷入口+联调

#### P2-006-002-001 租赁合同主从列表页
- P2-006-002-001-001 搜索条件区+主表数据表格+从表明细区+操作按钮区+联调验证
  - P2-006-002-001-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-006-002-001-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-006-002-001-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-006-002-002 新增/编辑租赁合同主从表单页
- P2-006-002-002-001 主表字段+从表明细区+客户选择器+商品选择器+联调验证
  - P2-006-002-002-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P2-006-002-002-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P2-006-002-002-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）

#### P2-006-002-003 租赁出库单列表页
- P2-006-002-003-001 搜索条件区+数据表格区+联调验证
  - P2-006-002-003-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-006-002-003-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-006-002-003-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-006-002-004 租赁计费列表页
- P2-006-002-004-001 搜索条件区+数据表格区+联调验证
  - P2-006-002-004-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-006-002-004-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-006-002-004-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-006-002-005 租赁收款单列表页
- P2-006-002-005-001 搜索条件区+数据表格区+联调验证
  - P2-006-002-005-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-006-002-005-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-006-002-005-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-006-002-006 租赁归还单列表页
- P2-006-002-006-001 搜索条件区+数据表格区+联调验证
  - P2-006-002-006-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-006-002-006-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-006-002-006-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-006-002-007 租入单主从列表页
- P2-006-002-007-001 搜索条件区+主表数据表格+从表明细区+联调验证
  - P2-006-002-007-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-006-002-007-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-006-002-007-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-006-002-008 租入归还单列表页
- P2-006-002-008-001 搜索条件区+数据表格区+联调验证
  - P2-006-002-008-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-006-002-008-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-006-002-008-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

---

## P2-007 售后管理模块开发

> **依赖**：P0-007（商品）、P0-008（客户）、P0-010（仓库）、P1-001（审核引擎）
> **概述**：完成售后全链路：售后单→远程/上门服务→退货/换货/维修→送修/接修→客户回访

### P2-007-001 售后管理后端开发

#### P2-007-001-001 售后单CRUD接口
- P2-007-001-001-001 售后单Entity/DTO/VO定义+Mapper开发（after_sale+after_sale_detail）
  - P2-007-001-001-001-001 定义Entity类（@TableName+@TableId+字段映射+@TableField+逻辑删除注解）
  - P2-007-001-001-001-002 定义DTO类（CreateDTO/UpdateDTO/QueryDTO+@NotBlank/@NotNull校验注解）
  - P2-007-001-001-001-003 定义VO类（列表VO/详情VO+@JsonFormat日期格式+字典翻译字段）
- P2-007-001-001-002 售后单Service开发（主从CRUD+编码自动生成+销售订单关联+审核流转）
  - P2-007-001-001-002-001 定义Service接口（extends IServiceX+CRUD方法声明+业务方法声明）
  - P2-007-001-001-002-002 实现ServiceImpl（extends ServiceImpl+@Service+CRUD方法实现）
  - P2-007-001-001-002-003 业务校验逻辑（唯一性校验+状态校验+关联数据校验）
- P2-007-001-001-003 售后单Controller开发
  - P2-007-001-001-003-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-007-001-001-003-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-007-001-001-003-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-007-001-002 远程服务单CRUD接口
- P2-007-001-002-001 远程服务单Entity/Mapper/Service/Controller开发（远程协助记录+服务时长+服务评价）
  - P2-007-001-002-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-007-001-002-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-007-001-002-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-007-001-003 上门服务单CRUD接口
- P2-007-001-003-001 上门服务单Entity/Mapper/Service/Controller开发（服务人员指派+服务时间+交通费用+服务评价）
  - P2-007-001-003-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-007-001-003-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-007-001-003-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-007-001-004 售后退货单CRUD接口
- P2-007-001-004-001 售后退货单Entity/Mapper/Service/Controller开发（审核后库存增加+退款金额联动应收）
  - P2-007-001-004-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-007-001-004-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-007-001-004-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-007-001-005 售后换货单CRUD接口
- P2-007-001-005-001 售后换货单Entity/Mapper/Service/Controller开发（审核后旧品入库+新品出库+差价处理）
  - P2-007-001-005-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-007-001-005-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-007-001-005-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-007-001-006 售后维修单CRUD接口
- P2-007-001-006-001 售后维修单Entity/Mapper/Service/Controller开发（维修方案+维修费用+维修领料关联）
  - P2-007-001-006-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-007-001-006-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-007-001-006-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-007-001-007 接修单CRUD接口
- P2-007-001-007-001 接修单Entity/Mapper/Service/Controller开发（客户送修登记+故障描述+维修人员分配）
  - P2-007-001-007-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-007-001-007-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-007-001-007-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-007-001-008 接修返回单CRUD接口
- P2-007-001-008-001 接修返回单Entity/Mapper/Service/Controller开发（维修完成返回客户+返回物流信息）
  - P2-007-001-008-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-007-001-008-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-007-001-008-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-007-001-009 送修单CRUD接口
- P2-007-001-009-001 送修单Entity/Mapper/Service/Controller开发（内部送外维修+送修供应商+送修物流信息）
  - P2-007-001-009-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-007-001-009-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-007-001-009-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-007-001-010 送修返回单CRUD接口
- P2-007-001-010-001 送修返回单Entity/Mapper/Service/Controller开发（外部维修返回+返回验收+维修费用确认）
  - P2-007-001-010-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-007-001-010-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-007-001-010-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-007-001-011 客户回访单CRUD接口
- P2-007-001-011-001 客户回访单Entity/Mapper/Service/Controller开发（回访方式+满意度评价+回访内容+下次回访计划）
  - P2-007-001-011-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-007-001-011-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-007-001-011-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-007-001-012 保修规则配置CRUD接口
- P2-007-001-012-001 保修规则配置Entity/Mapper/Service/Controller开发（保修期限+保修范围+保修条件配置）
  - P2-007-001-012-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-007-001-012-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-007-001-012-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-007-001-013 维修领料单CRUD接口
- P2-007-001-013-001 维修领料单Entity/Mapper/Service/Controller开发（关联维修单+领料出库+库存扣减）
  - P2-007-001-013-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-007-001-013-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-007-001-013-001-003 补充接口文档注解（@Operation+@Schema+示例值）

### P2-007-002 售后管理前端页面

#### P2-007-002-000 售后管理工作台页（P02：待处理售后单/维修中/本月完工数KPI+售后类型分布+客户满意度趋势）
- P2-007-002-000-001 工作台KPI卡片区+图表区+待办区+联调验证
  - P2-007-002-000-001-001 KPI卡片+图表+待办列表+快捷入口+联调

#### P2-007-002-001 售后单主从列表页
- P2-007-002-001-001 搜索条件区+主表数据表格+从表明细区+操作按钮区+联调验证
  - P2-007-002-001-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-007-002-001-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-007-002-001-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-007-002-002 新增/编辑售后单主从表单页
- P2-007-002-002-001 主表字段+从表明细区+客户选择器+商品选择器+销售订单关联+联调验证
  - P2-007-002-002-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P2-007-002-002-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P2-007-002-002-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）

#### P2-007-002-003 远程服务单列表页
- P2-007-002-003-001 搜索条件区+数据表格区+联调验证
  - P2-007-002-003-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-007-002-003-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-007-002-003-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-007-002-004 上门服务单列表页
- P2-007-002-004-001 搜索条件区+数据表格区+联调验证
  - P2-007-002-004-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-007-002-004-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-007-002-004-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-007-002-005 售后退货单列表页
- P2-007-002-005-001 搜索条件区+数据表格区+联调验证
  - P2-007-002-005-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-007-002-005-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-007-002-005-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-007-002-006 售后换货单列表页
- P2-007-002-006-001 搜索条件区+数据表格区+联调验证
  - P2-007-002-006-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-007-002-006-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-007-002-006-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-007-002-007 售后维修单列表页
- P2-007-002-007-001 搜索条件区+数据表格区+联调验证
  - P2-007-002-007-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-007-002-007-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-007-002-007-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-007-002-008 接修单列表页
- P2-007-002-008-001 搜索条件区+数据表格区+联调验证
  - P2-007-002-008-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-007-002-008-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-007-002-008-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-007-002-009 接修返回单列表页
- P2-007-002-009-001 搜索条件区+数据表格区+联调验证
  - P2-007-002-009-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-007-002-009-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-007-002-009-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-007-002-010 送修单列表页
- P2-007-002-010-001 搜索条件区+数据表格区+联调验证
  - P2-007-002-010-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-007-002-010-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-007-002-010-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-007-002-011 送修返回单列表页
- P2-007-002-011-001 搜索条件区+数据表格区+联调验证
  - P2-007-002-011-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-007-002-011-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-007-002-011-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-007-002-012 客户回访单列表页
- P2-007-002-012-001 搜索条件区+数据表格区+联调验证
  - P2-007-002-012-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-007-002-012-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-007-002-012-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-007-002-013 保修规则配置页
- P2-007-002-013-001 规则列表区+新增/编辑表单+联调验证
  - P2-007-002-013-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-007-002-013-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-007-002-013-001-003 规则列表区异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-007-002-014 维修领料单列表页
- P2-007-002-014-001 搜索条件区+数据表格区+联调验证
  - P2-007-002-014-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-007-002-014-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-007-002-014-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

---

## P2-008 OA办公模块开发

> **依赖**：P0-004（用户/权限）、P1-002（审批流程）
> **概述**：完成日程、公告、会议、用车、请假、新闻等OA办公功能

### P2-008-001 OA后端开发

#### P2-008-001-001 日程CRUD接口
- P2-008-001-001-001 日程Entity/Mapper/Service/Controller开发（日程提醒+重复规则）
  - P2-008-001-001-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-008-001-001-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-008-001-001-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-008-001-002 公告CRUD接口
- P2-008-001-002-001 公告Entity/Mapper/Service/Controller开发（发布/撤回+已读统计）
  - P2-008-001-002-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-008-001-002-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-008-001-002-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-008-001-003 会议CRUD接口
- P2-008-001-003-001 会议Entity/Mapper/Service/Controller开发（会议室冲突校验+参会人通知）
  - P2-008-001-003-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-008-001-003-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-008-001-003-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-008-001-004 会议室CRUD接口
- P2-008-001-004-001 会议室Entity/Mapper/Service/Controller开发（占用状态查询）
  - P2-008-001-004-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-008-001-004-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-008-001-004-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-008-001-005 用车CRUD接口
- P2-008-001-005-001 用车Entity/Mapper/Service/Controller开发（车辆冲突校验+审批关联）
  - P2-008-001-005-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-008-001-005-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-008-001-005-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-008-001-006 请假管理CRUD接口
- P2-008-001-006-001 请假管理Entity/Mapper/Service/Controller开发（假期余额校验+审批流程关联+请假天数自动计算）
  - P2-008-001-006-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-008-001-006-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-008-001-006-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-008-001-007 新闻管理CRUD接口
- P2-008-001-007-001 新闻管理Entity/Mapper/Service/Controller开发（新闻分类+发布/撤回+置顶+浏览量统计）
  - P2-008-001-007-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-008-001-007-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-008-001-007-001-003 补充接口文档注解（@Operation+@Schema+示例值）

### P2-008-002 OA前端页面

#### P2-008-002-000 OA办公工作台页（P02：今日日程/待审请假/未读公告/会议安排KPI+日程日历+待办列表）
- P2-008-002-000-001 工作台KPI卡片区+图表区+待办区+联调验证
  - P2-008-002-000-001-001 KPI卡片+图表+待办列表+快捷入口+联调

#### P2-008-002-001 日程管理页
- P2-008-002-001-001 日/周/月视图切换+日程卡片+新增/编辑弹窗+联调验证
  - P2-008-002-001-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-008-002-001-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-008-002-001-001-003 月视图切换异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-008-002-002 公告管理列表页
- P2-008-002-002-001 搜索条件区+数据表格区+已读统计+联调验证
  - P2-008-002-002-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-008-002-002-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-008-002-002-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-008-002-003 新增/编辑公告表单页
- P2-008-002-003-001 公告内容编辑+发布范围+附件上传+联调验证
  - P2-008-002-003-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-008-002-003-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-008-002-003-001-003 公告内容编辑异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-008-002-004 会议管理列表页
- P2-008-002-004-001 搜索条件区+数据表格区+联调验证
  - P2-008-002-004-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-008-002-004-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-008-002-004-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-008-002-005 会议室管理列表页
- P2-008-002-005-001 搜索条件区+数据表格区+占用状态+联调验证
  - P2-008-002-005-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-008-002-005-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-008-002-005-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-008-002-006 用车管理列表页
- P2-008-002-006-001 搜索条件区+数据表格区+联调验证
  - P2-008-002-006-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-008-002-006-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-008-002-006-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-008-002-007 请假申请列表页
- P2-008-002-007-001 搜索条件区+数据表格区+审批状态+联调验证
  - P2-008-002-007-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-008-002-007-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-008-002-007-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-008-002-008 新增/编辑请假申请表单页
- P2-008-002-008-001 假期余额展示+日期选择+天数自动计算+联调验证
  - P2-008-002-008-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-008-002-008-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-008-002-008-001-003 假期余额展示异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-008-002-009 新闻管理列表页
- P2-008-002-009-001 搜索条件区+数据表格区+分类筛选+置顶标记+联调验证
  - P2-008-002-009-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-008-002-009-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-008-002-009-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-008-002-010 新增/编辑新闻表单页
- P2-008-002-010-001 富文本编辑+分类选择+封面图上传+联调验证
  - P2-008-002-010-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-008-002-010-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-008-002-010-001-003 富文本编辑异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

---

## P2-009 AI助手模块开发

> **依赖**：P0-005（P14 AI对话页组件）
> **概述**：完成AI对话、智能问答、数据智能分析、对话式录入/查询/分析/审核

### P2-009-001 AI助手后端开发

#### P2-009-001-001 AI对话接口
- P2-009-001-001-001 LLM API对接+上下文管理+流式SSE响应开发
  - P2-009-001-001-001-001 实现LLM API对接（HTTP客户端+上下文消息管理+流式SSE响应解析+重试+超时+Token计数）
    - P2-009-001-001-001-001-001 实现HTTP客户端（POST /chat/completions+请求头Authorization+请求体构建+超时60s）
    - P2-009-001-001-001-001-002 实现流式SSE响应解析（ReadableStream读取+data:前缀解析+[DONE]结束+增量拼接）
    - P2-009-001-001-001-001-003 实现重试+超时+Token计数（3次重试+指数退避+Token使用量统计+剩余Token提示）
  - P2-009-001-001-001-002 实现上下文管理（对话历史存储+上下文窗口裁剪+系统提示词注入+多轮对话ID）
  - P2-009-001-001-001-003 验证LLM对接（API调用成功+流式输出渲染+上下文保持+异常降级）

#### P2-009-001-002 智能问答接口
- P2-009-001-002-001 ERP知识库RAG检索增强生成开发
  - P2-009-001-002-001-001 实现RAG检索增强（文档向量化+向量存储+语义检索+检索结果注入LLM上下文）
    - P2-009-001-002-001-001-001 实现文档向量化（文本分块+调用Embedding API+向量维度对齐+批量向量化）
    - P2-009-001-002-001-001-002 实现向量存储与索引（向量数据库写入+HNSW索引+元数据存储+相似度阈值0.7）
    - P2-009-001-002-001-001-003 实现语义检索+结果注入（用户问题向量化→相似度检索Top5→拼接至LLM上下文system消息）
  - P2-009-001-002-001-002 实现ERP知识库构建（业务文档/表结构/API文档导入+分块+向量化+索引构建）
  - P2-009-001-002-001-003 验证RAG检索（语义检索命中率+检索结果相关性+知识库更新实时性）

#### P2-009-001-003 数据智能分析接口
- P2-009-001-003-001 自然语言→SQL→查询结果→自然语言解读全链路开发
  - P2-009-001-003-001-001 实现自然语言→SQL转换（LLM解析用户意图→生成SQL+SQL安全校验+执行+结果集获取）
    - P2-009-001-003-001-001-001 实现意图识别+SQL生成（LLM解析用户意图→匹配数据表→生成参数化SQL+字段映射）
    - P2-009-001-003-001-001-002 实现SQL安全校验（只允许SELECT+禁止DROP/DELETE/UPDATE+行数限制+超时限制）
    - P2-009-001-003-001-001-003 实现SQL执行+结果解析（安全SQL执行+结果集转JSON+字段名翻译+空结果友好提示）
  - P2-009-001-003-001-002 实现SQL结果→自然语言解读（结果集传递给LLM+生成人类可读的解读文本+格式化展示）
  - P2-009-001-003-001-003 验证查询链路（自然语言→SQL准确+SQL安全+执行结果→解读准确）

#### P2-009-001-004 对话式智能录入接口
- P2-009-001-004-001 AI解析自然语言→自动填充单据字段→单据草稿生成开发
  - P2-009-001-004-001-001 实现AI解析自然语言→单据字段映射（意图识别→提取实体→匹配单据字段→自动填充）
    - P2-009-001-004-001-001-001 实现意图识别+实体提取（识别单据类型+提取字段实体+解析数量/日期/金额等）
    - P2-009-001-004-001-001-002 实现字段匹配与填充（提取的实体→匹配单据字段编码→自动填充字段值+不确定项提示确认）
    - P2-009-001-004-001-001-003 实现草稿创建与返回（填充字段→创建草稿单据→返回草稿ID+编辑页URL+用户可修改后保存）
  - P2-009-001-004-001-002 实现单据草稿生成（填充字段→创建草稿单据→返回草稿ID→用户可在编辑页继续完善）
  - P2-009-001-004-001-003 验证智能录入（自然语言→字段映射准确+草稿创建成功+用户可编辑保存）

#### P2-009-001-005 对话式智能查询接口
- P2-009-001-005-001 自然语言→多维度数据查询→结果汇总+图表建议开发
  - P2-009-001-005-001-001 实现自然语言→多维度数据查询（意图解析→多维度SQL→执行→结果聚合）
    - P2-009-001-005-001-001-001 实现意图解析→多维度SQL（LLM生成多维度查询SQL+维度识别+指标计算）
    - P2-009-001-005-001-001-002 实现结果聚合+图表建议（查询结果→汇总文本→推荐图表类型：柱状/折线/饼图）
    - P2-009-001-005-001-001-003 实现前端自动渲染图表（接收图表类型+数据→ECharts配置→自动渲染+交互式切换）
  - P2-009-001-005-001-002 实现结果汇总+图表建议（数据→汇总文本→推荐图表类型→前端自动渲染图表）
  - P2-009-001-005-001-003 验证智能查询（多维度查询正确+汇总文本准确+图表推荐合理）

#### P2-009-001-006 对话式智能分析汇总接口
- P2-009-001-006-001 自然语言→多维数据聚合分析→趋势/对比/占比分析+自然语言解读开发
  - P2-009-001-006-001-001 实现自然语言→多维数据聚合分析（LLM生成分析SQL→趋势/对比/占比计算→分析结果）
    - P2-009-001-006-001-001-001 实现分析SQL生成（LLM生成趋势/对比/占比分析SQL+时间维度+分组维度）
    - P2-009-001-006-001-001-002 实现趋势/对比/占比计算（查询结果→同比/环比/占比计算→百分比格式化）
    - P2-009-001-006-001-001-003 实现自然语言解读+可视化建议（分析结果→LLM生成解读文本+可视化图表推荐）
  - P2-009-001-006-001-002 实现趋势/对比/占比分析+自然语言解读（分析结果→LLM生成解读文本+可视化建议）
  - P2-009-001-006-001-003 验证智能分析（趋势分析正确+对比准确+占比计算正确+解读文本合理）

#### P2-009-001-007 对话式单据审核接口
- P2-009-001-007-001 AI辅助审核→单据合规性检查+异常检测+审核建议+风险提示开发
  - P2-009-001-007-001-001 定义异常类（extends RuntimeException+错误码+消息+构造方法）
  - P2-009-001-007-001-002 实现辅助审核异常处理逻辑（捕获+分类+响应包装+日志记录）

### P2-009-002 AI助手前端页面

#### P2-009-002-000 AI助手工作台页（P02：今日对话数/智能录入次数/知识库文档数KPI+使用趋势+热门提示词）
- P2-009-002-000-001 工作台KPI卡片区+图表区+待办区+联调验证
  - P2-009-002-000-001-001 KPI卡片+图表+待办列表+快捷入口+联调

#### P2-009-002-001 AI对话页
- P2-009-002-001-001 对话区+流式打字机输出+代码高亮+图表渲染+联调验证
  - P2-009-002-001-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-009-002-001-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-009-002-001-001-003 对话区异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-009-002-002 智能问答入口
- P2-009-002-002-001 全局搜索栏集成AI问答+联调验证
  - P2-009-002-002-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-009-002-002-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-009-002-002-001-003 全局搜索栏集异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-009-002-003 数据智能分析页
- P2-009-002-003-001 P14对话区+图表展示区+联调验证
  - P2-009-002-003-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-009-002-003-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-009-002-003-001-003 对话区异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-009-002-004 对话式智能录入页
- P2-009-002-004-001 P14集成于单据表单页：AI输入区+字段自动填充+确认提交+联调验证
  - P2-009-002-004-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-009-002-004-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-009-002-004-001-003 集成于单据表异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-009-002-005 对话式智能查询页
- P2-009-002-005-001 P14集成于列表页：AI查询区+结果展示+筛选条件自动生成+联调验证
  - P2-009-002-005-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-009-002-005-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-009-002-005-001-003 集成于列表页异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-009-002-006 对话式智能分析汇总页
- P2-009-002-006-001 P14对话区+多维图表+数据钻取+结论摘要+联调验证
  - P2-009-002-006-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-009-002-006-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-009-002-006-001-003 对话区异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-009-002-007 对话式单据审核页
- P2-009-002-007-001 P14集成于审批页：AI审核建议+异常高亮+风险提示展示+联调验证
  - P2-009-002-007-001-001 定义异常类（extends RuntimeException+错误码+消息+构造方法）
  - P2-009-002-007-001-002 实现集成于审批页异常处理逻辑（捕获+分类+响应包装+日志记录）
    - P2-009-002-008 AI配置管理（LLM供应商/模型/API Key/温度/最大Token/超时+多模型切换+热更新）
      - P2-009-002-008-001 后端开发（ai_config表：CRUD+加密存储+运行时热更新+多供应商适配）
        - P2-009-002-008-001-001 编写DDL+Entity/Mapper（供应商+模型+API Key加密+温度+Token+超时+默认标识）
        - P2-009-002-008-001-002 编写Service+Controller（CRUD+切换+加密解密+热更新+供应商适配层）
        - P2-009-002-008-001-003 验证配置管理（CRUD+加密+切换+热更新不重启）
      - P2-009-002-008-002 前端开发（P13配置页：供应商选择+模型列表+API Key脱敏+测试连接）
        - P2-009-002-008-002-001 实现配置表单（供应商下拉+模型选择+API Key+温度滑块+Token输入+测试按钮）
        - P2-009-002-008-002-002 实现测试连接（发送测试请求+成功/失败+延迟+模型信息回显）
    - P2-009-002-009 AI提示词模板管理（预置模板CRUD+变量占位符+模板分类+预览+导入导出）
      - P2-009-002-009-001 后端开发（ai_prompt_template表：CRUD+变量解析+渲染+预置数据）
        - P2-009-002-009-001-001 编写DDL+Entity/Mapper（模板名+分类+内容+变量${xxx}+排序+启用标识）
        - P2-009-002-009-001-002 编写Service+Controller（CRUD+变量提取+渲染+预置SQL初始化）
        - P2-009-002-009-001-003 验证模板管理（CRUD+变量提取+渲染+预置加载）
      - P2-009-002-009-002 前端开发（P04列表+P02编辑：模板列表+分类Tab+编辑器+变量提示）
        - P2-009-002-009-002-001 实现模板列表页（分类Tab+卡片列表+搜索+增删改预览）
        - P2-009-002-009-002-002 实现模板编辑页（模板名+分类+内容textarea+变量高亮+预览渲染）
    - P2-009-002-011 AI知识库管理（文档导入+向量化+语义检索+知识库CRUD+文档切片+检索测试）
      - P2-009-002-011-001 后端开发（知识库CRUD+文档上传+切片+Embedding向量化+向量检索+重排序）
        - P2-009-002-011-001-001 编写知识库DDL+Entity/Mapper（知识库ID+名称+描述+文档数+向量维度+Embedding模型）
        - P2-009-002-011-001-002 编写文档上传+切片Service（PDF/Word/Excel解析+文本切片+Overlap+元数据）
        - P2-009-002-011-001-003 编写向量化+检索Service（Embedding API调用+向量存储pgvector+余弦相似度检索+Top-K+重排序）
        - P2-009-002-011-001-004 验证知识库（CRUD+上传+切片+向量化+检索+重排序）
      - P2-009-002-011-002 前端开发（知识库列表+文档管理+检索测试+配置）
        - P2-009-002-011-002-001 实现知识库列表页（CRUD+文档列表+切片预览+检索测试+配置页）
    - P2-009-002-010 AI对话中心（P14对话页：对话列表+对话输入+AI流式回复+对话历史+上下文管理+多轮对话）
      - P2-009-002-010-001 后端开发（对话Session管理+消息存储+LLM流式调用+上下文窗口管理）
        - P2-009-002-010-001-001 编写对话Session Service（创建/列表/删除+消息CRUD+上下文token限制+历史截断）
        - P2-009-002-010-001-002 编写LLM流式调用Service（SSE流式输出+上下文拼接+RAG检索增强+工具调用）
        - P2-009-002-010-001-003 验证对话中心（多轮对话+流式输出+上下文管理+历史记录）
      - P2-009-002-010-002 前端开发（P14对话页：消息列表+输入框+流式渲染+Markdown+代码高亮）
        - P2-009-002-010-002-001 实现对话页（消息气泡+流式渲染+Markdown-it+代码高亮+输入框+发送按钮+历史列表）

---

## P2-010 模板中心模块开发

> **依赖**：P0-005（公共组件含打印组件）
> **概述**：完成模板管理、模板分类、打印模板、导入/导出模板、模板设计器

### P2-010-001 模板中心后端开发

#### P2-010-001-001 模板CRUD接口
- P2-010-001-001-001 模板Entity/Mapper/Service/Controller开发（模板内容JSON存储）
  - P2-010-001-001-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-010-001-001-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-010-001-001-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-010-001-002 模板分类CRUD接口
- P2-010-001-002-001 模板分类Entity/Mapper/Service/Controller开发（树形结构）
  - P2-010-001-002-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-010-001-002-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-010-001-002-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-010-001-003 打印模板CRUD接口
- P2-010-001-003-001 打印模板Entity/Mapper/Service/Controller开发（模板文件上传存储）
  - P2-010-001-003-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-010-001-003-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-010-001-003-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-010-001-004 导入模板CRUD接口
- P2-010-001-004-001 导入模板Entity/Mapper/Service/Controller开发（导入字段映射配置+校验规则配置+模板文件存储）
  - P2-010-001-004-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-010-001-004-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-010-001-004-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-010-001-005 导出模板CRUD接口
- P2-010-001-005-001 导出模板Entity/Mapper/Service/Controller开发（导出字段配置+导出格式配置+模板文件存储）
  - P2-010-001-005-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-010-001-005-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-010-001-005-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-010-001-006 模板设计器引擎接口
- P2-010-001-006-001 模板组件库查询+模板保存+模板预览+模板版本管理开发
  - P2-010-001-006-001-001 定义组件props/emits（属性+类型+默认值+事件声明）
  - P2-010-001-006-001-002 实现组件模板结构（template+scoped样式+slot+响应式）
  - P2-010-001-006-001-003 实现组件逻辑（computed+methods+watch+生命周期+emit）

### P2-010-002 模板中心前端页面

#### P2-010-002-000 模板中心工作台页（P02：打印模板数/导入模板数/导出模板数KPI+模板使用排行+最近编辑模板）
- P2-010-002-000-001 工作台KPI卡片区+图表区+待办区+联调验证
  - P2-010-002-000-001-001 KPI卡片+图表+待办列表+快捷入口+联调

#### P2-010-002-001 模板管理列表页
- P2-010-002-001-001 搜索条件区+数据表格区+分类筛选+联调验证
  - P2-010-002-001-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-010-002-001-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-010-002-001-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-010-002-002 模板分类树形列表页
- P2-010-002-002-001 树形分类区+新增/编辑/删除操作+联调验证
  - P2-010-002-002-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-010-002-002-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-010-002-002-001-003 树形分类区异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-010-002-003 打印模板管理列表页
- P2-010-002-003-001 搜索条件区+数据表格区+联调验证
  - P2-010-002-003-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-010-002-003-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-010-002-003-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-010-002-004 打印模板设计器页
- P2-010-002-004-001 拖拽式组件编排+属性配置+数据绑定+预览打印+联调验证
  - P2-010-002-004-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-010-002-004-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-010-002-004-001-003 拖拽式组件编异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-010-002-005 导入模板管理列表页
- P2-010-002-005-001 搜索条件区+数据表格区+联调验证
  - P2-010-002-005-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-010-002-005-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-010-002-005-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-010-002-006 导入模板配置页
- P2-010-002-006-001 字段映射配置+校验规则配置+模板下载+联调验证
  - P2-010-002-006-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-010-002-006-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-010-002-006-001-003 字段映射配置异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-010-002-007 导出模板管理列表页
- P2-010-002-007-001 搜索条件区+数据表格区+联调验证
  - P2-010-002-007-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-010-002-007-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-010-002-007-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-010-002-008 导出模板配置页
- P2-010-002-008-001 导出字段选择+格式配置+模板下载+联调验证
  - P2-010-002-008-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-010-002-008-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-010-002-008-001-003 导出字段选择异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-010-002-009 模板设计器页
- P2-010-002-009-001 组件面板+画布+属性面板+预览+版本管理+联调验证
  - P2-010-002-009-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-010-002-009-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-010-002-009-001-003 组件面板异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

---

## P2-011 报表中心模块开发

> **依赖**：P0-005（P10报表页组件）、各业务模块
> **概述**：完成分业务报表+自定义报表+报表设计器

### P2-011-001 报表中心后端开发

#### P2-011-001-001 报表CRUD接口
- P2-011-001-001-001 报表Entity/Mapper/Service/Controller开发（报表配置JSON存储）
  - P2-011-001-001-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-011-001-001-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-011-001-001-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-011-001-002 报表分类CRUD接口
- P2-011-001-002-001 报表分类Entity/Mapper/Service/Controller开发（树形结构）
  - P2-011-001-002-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-011-001-002-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-011-001-002-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-011-001-003 报表数据动态查询接口
- P2-011-001-003-001 根据报表配置的SQL模板+参数绑定+动态执行+分页返回开发
  - P2-011-001-003-001-001 报表查询条件区（日期+组织+商品+客户等筛选）
  - P2-011-001-003-001-002 报表数据展示（多维列+合计+小计+格式高亮+图表）
  - P2-011-001-003-001-003 报表导出打印（Excel+打印预览+PDF+大数据分页）

#### P2-011-001-004 采购报表数据接口
- P2-011-001-004-001 采购汇总表+采购明细表+采购价格分析+供应商采购统计SQL+Service开发
  - P2-011-001-004-001-001 定义Service接口（extends IServiceX+业务方法声明）
  - P2-011-001-004-001-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P2-011-001-004-001-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）

#### P2-011-001-005 销售报表数据接口
- P2-011-001-005-001 销售汇总表+销售明细表+客户销售统计+销售趋势分析SQL+Service开发（注：销售毛利分析已由P1-005-001-012覆盖，此处不重复）
  - P2-011-001-005-001-001 定义Service接口（extends IServiceX+业务方法声明）
  - P2-011-001-005-001-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P2-011-001-005-001-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）

#### P2-011-001-006 库存报表数据接口
- P2-011-001-006-001 库存余额表+库存收发存汇总+库存周转率+库存账龄+安全库存预警SQL+Service开发
  - P2-011-001-006-001-001 定义Service接口（extends IServiceX+业务方法声明）
  - P2-011-001-006-001-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P2-011-001-006-001-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）

#### P2-011-001-007 财务报表聚合查看入口
- P2-011-001-007-001 报表中心视角下的财务报表聚合查看（调用P1-014已有三大报表接口+报表中心统一入口封装+跨模块报表聚合展示）
  - P2-011-001-007-001-001 定义Service接口（聚合调用P1-014资产负债表/利润表/现金流量表接口+统一数据格式封装）
  - P2-011-001-007-001-002 实现ServiceImpl（报表中心聚合入口+缓存策略+多期对比数据组装）
  - P2-011-001-007-001-003 验证聚合接口（数据与P1-014一致+缓存生效+性能合理）
  > **说明**：资产负债表/利润表/现金流量表的取数逻辑已由P1-014财务报表模块完整实现，科目余额表/辅助核算报表已由P1-013账簿查询模块覆盖。本任务仅提供报表中心视角的统一聚合查看入口，不重复实现取数逻辑。

#### P2-011-001-008 生产报表数据接口
- P2-011-001-008-001 生产完工统计+生产成本分析+工序效率统计+材料消耗分析SQL+Service开发
  - P2-011-001-008-001-001 定义Service接口（extends IServiceX+业务方法声明）
  - P2-011-001-008-001-002 实现ServiceImpl（核心业务逻辑+数据校验+联动计算）
  - P2-011-001-008-001-003 业务辅助方法（状态流转+编码生成调用+操作日志集成）

#### P2-011-001-009 自定义报表引擎接口
- P2-011-001-009-001 报表字段配置+数据源配置+查询条件配置+计算列配置+图表配置开发
  - P2-011-001-009-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-011-001-009-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-011-001-009-001-003 实现报表字段配置查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

### P2-011-002 报表中心前端页面

#### P2-011-002-000 报表中心工作台页（P02：报表总数/收藏报表数/本月查看次数KPI+常用报表快捷入口+数据大屏入口）
- P2-011-002-000-001 工作台KPI卡片区+图表区+待办区+联调验证
  - P2-011-002-000-001-001 KPI卡片+图表+待办列表+快捷入口+联调

#### P2-011-002-001 报表管理列表页
- P2-011-002-001-001 搜索条件区+数据表格区+分类筛选+联调验证
  - P2-011-002-001-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-011-002-001-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-011-002-001-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-011-002-002 报表分类树形列表页
- P2-011-002-002-001 树形分类区+操作按钮+联调验证
  - P2-011-002-002-001-001 顶部操作栏布局（新增/修改/删除/导出+v-permission权限控制）
  - P2-011-002-002-001-002 批量操作逻辑（多选+批量删除/审核+确认弹窗+选中校验）
    - P2-011-002-002-001-002-001 实现多选逻辑（vxe-table checkbox多选+全选+跨页选中保持+选中行数据收集）
    - P2-011-002-002-001-002-002 实现选中校验（至少选1条+已审核不可删除+草稿才可删除+不符合条件提示）
    - P2-011-002-002-001-002-003 实现批量操作确认弹窗（操作类型+影响数量提示+确认/取消按钮+二次确认）
    - P2-011-002-002-001-002-004 实现批量操作执行（批量调用API+进度提示+成功/失败统计+列表刷新）
  - P2-011-002-002-001-003 操作事件处理（按钮点击→弹窗/路由+参数传递+回调刷新）

#### P2-011-002-003 自定义报表设计器页
- P2-011-002-003-001 字段配置+数据源配置+查询条件+计算列+图表配置+预览+联调验证
  - P2-011-002-003-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-011-002-003-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-011-002-003-001-003 实现字段配置查询页搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-011-002-004 报表查看页
- P2-011-002-004-001 根据报表配置动态渲染条件区+结果区+联调验证
  - P2-011-002-004-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-011-002-004-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-011-002-004-001-003 根据报表配置异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-011-002-005 采购报表页
- P2-011-002-005-001 采购汇总/明细/价格分析/供应商统计Tab+联调验证
  - P2-011-002-005-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-011-002-005-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-011-002-005-001-003 采购汇总异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-011-002-006 销售报表页
- P2-011-002-006-001 销售汇总/明细/毛利分析/客户统计/趋势分析Tab+联调验证
  - P2-011-002-006-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-011-002-006-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-011-002-006-001-003 销售汇总异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-011-002-007 库存报表页
- P2-011-002-007-001 库存余额/收发存/周转率/账龄/安全库存预警Tab+联调验证
  - P2-011-002-007-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-011-002-007-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-011-002-007-001-003 库存余额异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-011-002-008 财务报表页
- P2-011-002-008-001 资产负债表/利润表/现金流量表/科目余额/辅助核算Tab+联调验证
  - P2-011-002-008-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-011-002-008-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-011-002-008-001-003 资产负债表异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-011-002-009 生产报表页
- P2-011-002-009-001 完工统计/成本分析/工序效率/材料消耗Tab+联调验证
  - P2-011-002-009-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-011-002-009-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-011-002-009-001-003 完工统计异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）
    - P2-011-002-010 报表目录管理（分类树形目录+排序+权限控制+目录CRUD+报表归属）
      - P2-011-002-010-001 后端开发（report_catalog树形CRUD+排序+报表归属+权限校验）
        - P2-011-002-010-001-001 编写DDL+Entity/Mapper/Service/Controller（目录ID+父ID+名称+排序+层级路径）
        - P2-011-002-010-001-002 验证目录管理（树形CRUD+排序+归属+权限校验）
      - P2-011-002-010-002 前端开发（树形目录组件+拖拽排序+右键菜单+报表归属）
        - P2-011-002-010-002-001 实现目录树（el-tree+懒加载+拖拽+右键菜单+报表数量Badge）
    - P2-011-002-011 报表收藏功能（用户收藏+收藏列表+取消收藏+排序+快速访问）
      - P2-011-002-011-001 后端开发（report_favorite：收藏CRUD+列表+排序+用户隔离）
        - P2-011-002-011-001-001 编写DDL+Entity/Mapper/Service/Controller（用户ID+报表ID+时间+排序+唯一约束）
      - P2-011-002-011-002 前端开发（收藏按钮+收藏列表+快速访问入口）
        - P2-011-002-011-002-001 实现收藏功能（星标按钮+收藏列表+取消+拖拽排序+工作台快捷入口）
    - P2-011-002-012 数据大屏（P11大屏页：设计文档11.33.11要求，基于report_screen_config配置表+全屏数据可视化展示）
      - P2-011-002-012-001 后端开发（数据大屏配置CRUD+大屏数据聚合接口+定时刷新支持）
        - P2-011-002-012-001-001 编写report_screen_config表DDL+Entity/Mapper/Service/Controller（大屏名称+编码+主题dark/light+分辨率1920x1080/2560x1440/3840x2160+layout_config JSONB布局配置+refresh_interval自动刷新秒数+enable_flag启用标志）
        - P2-011-002-012-001-002 编写大屏数据聚合Service（根据layout_config中配置的图表组件列表+各组件关联的数据视图编号+批量调用数据视图引擎查询+组装大屏数据包返回+@Cacheable缓存+缓存过期按refresh_interval控制）
        - P2-011-002-012-001-003 编写大屏Controller（@RestController+@RequestMapping"/api/report/screen"+大屏配置CRUD+大屏数据查询+大屏启用/禁用+预览接口）
        - P2-011-002-012-001-004 验证大屏后端（配置CRUD正确+数据聚合准确+缓存命中+刷新间隔生效+性能合理）
      - P2-011-002-012-002 前端开发（P11大屏页：深色主题+v-scale-screen自适应+图表网格布局+定时刷新+全屏展示）
        - P2-011-002-012-002-001 大屏布局容器开发（v-scale-screen组件+1920x1080/2K/4K自适应缩放+深色主题CSS+CSS clamp()响应式+网格布局Grid/Flex+组件拖拽配置位置）
        - P2-011-002-012-002-002 大屏图表组件开发（ECharts深色主题+折线图/柱状图/饼图/仪表盘/地图/数字翻牌器+数据绑定+定时刷新setInterval+loading状态+异常兜底）
        - P2-011-002-012-002-003 大屏配置器开发（P15设计器页：大屏列表+新增大屏+布局编辑拖拽组件+数据源选择绑定+刷新间隔配置+主题选择+分辨率选择+预览/发布）
        - P2-011-002-012-002-004 大屏展示页开发（全屏展示+根据layout_config动态渲染图表组件+数据加载+自动刷新+全屏切换+ESC退出+联调验证）

---

## P2-012 期初管理模块开发

> **依赖**：P0-007（商品）、P0-010（仓库）、P1-010（应收）、P1-011（应付）、P1-012（凭证）
> **概述**：完成期初库存、期初应收、期初应付、期初应开票、期初应收票、期初固定资产、期初科目余额

### P2-012-001 期初管理后端开发

#### P2-012-001-001 期初库存CRUD接口
- P2-012-001-001-001 期初库存Entity/DTO/VO定义（init_inventory主表+init_inventory_detail从表）
  - P2-012-001-001-001-001 编写InitInventoryEntity类（@TableName"init_inventory"+@TableId期初库存ID+期初编号init_no+仓库warehouse_id+启用日期enable_date+状态status+备注remark+@TableLogic逻辑删除）
  - P2-012-001-001-001-002 编写InitInventoryDetailEntity类（@TableName"init_inventory_detail"+@TableId明细ID+期初库存ID init_id+商品product_id+批号batch_no+生产日期production_date+有效期至expiry_date+数量quantity+单价unit_price+金额amount+备注remark）
  - P2-012-001-001-001-003 编写InitInventoryCreateDTO（@NotNull仓库+@NotNull启用日期+@NotEmpty明细列表+校验规则：仓库+商品维度唯一性（同仓库同商品不可重复）+@DecimalMin数量>0+单价>=0+金额=数量×单价自动计算校验）
  - P2-012-001-001-001-004 编写InitInventoryQueryDTO（仓库+商品+日期范围查询条件）
  - P2-012-001-001-001-005 编写InitInventoryVO/InitInventoryListVO（列表VO含期初编号/仓库名称/商品名称/批号/数量/单价/金额/日期+@JsonFormat日期格式+字典翻译）
- P2-012-001-001-002 期初库存Mapper开发
  - P2-012-001-001-002-001 编写InitInventoryMapper接口（extends BaseMapperX<InitInventoryEntity>+@Mapper+selectByWarehouse按仓库查询+selectPageByCondition条件分页+selectByUniqueKey唯一性校验查询（仓库+商品））
  - P2-012-001-001-002-002 编写InitInventoryDetailMapper接口（extends BaseMapperX<InitInventoryDetailEntity>+@Mapper+selectByInitId按期初ID查明细+deleteByInitId按期初ID删除明细+batchInsert批量插入明细+sumByWarehouse商品按仓库汇总）
  - P2-012-001-001-002-003 编写InitInventoryMapper.xml（resultMap含主从嵌套+条件查询SQL含仓库/商品/日期动态where+唯一性校验SQL（warehouse_id+product_id组合））
- P2-012-001-001-003 期初库存Service开发（仓库+商品维度唯一性+库存写入）
  - P2-012-001-001-003-001 编写InitInventoryService接口（extends IServiceX<InitInventoryEntity>+create/draftCreate创建+update修改+delete删除+submit提交审核+approve审核通过执行期初写入+writeToInventory库存写入方法声明）
  - P2-012-001-001-003-002 编写InitInventoryServiceImpl实现类（@Service+期初编号自动生成调用SerialNumberService.generate("QCKC")+唯一性校验：同一仓库+商品的组合不可重复+approve审核通过后writeToInventory方法：①在库存主表inventory中创建或更新该仓库+商品的库存记录②库存数量=期初数量③库存金额=期初金额④记录库存流水inventory_transaction类型=期初导入⑤已启用（已存在业务单据）的仓库不可修改期初+@Transactional事务管理）
  - P2-012-001-001-003-003 业务校验逻辑（仓库+商品维度唯一性+期初数量>=0+金额精度两位小数+已启用仓库不可修改期初+期初导入前确认库存表无该仓库商品记录+批号管理商品必须填写批号信息）
- P2-012-001-001-004 期初库存Controller开发
  - P2-012-001-001-004-001 编写InitInventoryController类（@RestController+@RequestMapping"/api/init/inventory"+@Tag"期初库存"+注入InitInventoryService）
  - P2-012-001-001-004-002 实现POST /create创建接口（@RequestBody InitInventoryCreateDTO+@Valid校验+唯一性校验+返回Result<InitInventoryVO>）
  - P2-012-001-001-004-003 实现PUT /update修改接口（@RequestBody InitInventoryCreateDTO+仅草稿状态可修改+返回Result<Void>）
  - P2-012-001-001-004-004 实现DELETE /delete/{id}删除接口（仅草稿状态可删除+返回Result<Void>）
  - P2-012-001-001-004-005 实现GET /page分页查询接口（InitInventoryQueryDTO条件+返回Result<PageResult<InitInventoryListVO>>）
  - P2-012-001-001-004-006 实现GET /detail/{id}详情查询接口（@PathVariable id+主从表组装+返回Result<InitInventoryVO>）
  - P2-012-001-001-004-007 实现POST /submit/{id}提交审核接口（@PathVariable id+调用审核引擎+返回Result<Void>）
  - P2-012-001-001-004-008 补充Knife4j接口文档注解（@Operation描述+@Parameter参数说明+@Schema示例值）
  - P2-012-001-001-004-009 验证期初库存接口（单元测试：CRUD全流程+唯一性校验+审核后库存写入验证+已启用仓库拦截+金额计算精度+批号校验）

#### P2-012-001-002 期初应收CRUD接口
- P2-012-001-002-001 期初应收Entity/DTO/VO定义（init_ar）
  - P2-012-001-002-001-001 编写InitArEntity类（@TableName"init_ar"+@TableId期初应收ID+期初编号init_no+客户customer_id+应收日期ar_date+币种currency_id+应收金额ar_amount+已收金额received_amount+未收金额unreceived_amount+销售订单号（来源追溯）source_order_no+应收类型ar_type（货款/运费/其他）+账期天数credit_days+到期日due_date+备注remark+@TableLogic逻辑删除）
  - P2-012-001-002-001-002 编写InitArCreateDTO（@NotNull客户+@NotNull应收日期+@DecimalMin("0.01")应收金额>0+币种+应收类型+账期天数+到期日自动计算=应收日期+账期天数+校验规则：客户维度汇总不允许同一客户同一订单号重复）
  - P2-012-001-002-001-003 编写InitArQueryDTO（客户+应收日期范围+应收类型+币种查询条件）
  - P2-012-001-002-001-004 编写InitArVO/InitArListVO（列表VO含期初编号/客户名称/应收日期/币种名称/应收金额/已收金额/未收金额/到期日/应收类型+@JsonFormat日期格式+字典翻译）
- P2-012-001-002-002 期初应收Mapper开发
  - P2-012-001-002-002-001 编写InitArMapper接口（extends BaseMapperX<InitArEntity>+@Mapper+selectByCustomer按客户查询+selectPageByCondition条件分页+selectByUniqueKey唯一性校验（客户+来源订单号）+sumByCustomer按客户汇总应收金额）
  - P2-012-001-002-002-002 编写InitArMapper.xml（resultMap映射+条件查询SQL含客户/日期/类型动态where+客户汇总SQL：SELECT customer_id,SUM(ar_amount),SUM(received_amount),SUM(unreceived_amount) GROUP BY customer_id）
- P2-012-001-002-003 期初应收Service开发（客户维度汇总+应收款写入）
  - P2-012-001-002-003-001 编写InitArService接口（extends IServiceX<InitArEntity>+create/draftCreate创建+update修改+delete删除+submit提交审核+approve审核通过写入+writeToAr应收款写入方法声明）
  - P2-012-001-002-003-002 编写InitArServiceImpl实现类（@Service+期初编号自动生成调用SerialNumberService.generate("QCAR")+唯一性校验：同客户+来源订单号不可重复+approve审核通过后writeToAr方法：①在应收款表ar_receivable中插入应收记录②应收金额=期初金额，已收金额=0，未收金额=应收金额③记录应收款流水④已发生业务往来的客户期初数据不可修改+@Transactional事务管理）
  - P2-012-001-002-003-003 业务校验逻辑（客户维度+来源订单号唯一性+应收金额>0+到期日>=应收日期+已发生业务客户不可修改期初+币种必须为已定义币种）
- P2-012-001-002-004 期初应收Controller开发
  - P2-012-001-002-004-001 编写InitArController类（@RestController+@RequestMapping"/api/init/ar"+@Tag"期初应收"+注入InitArService）
  - P2-012-001-002-004-002 实现POST /create创建接口（@RequestBody InitArCreateDTO+@Valid校验+返回Result<InitArVO>）
  - P2-012-001-002-004-003 实现PUT /update修改接口（@RequestBody InitArCreateDTO+仅草稿状态+返回Result<Void>）
  - P2-012-001-002-004-004 实现DELETE /delete/{id}删除接口（仅草稿状态+返回Result<Void>）
  - P2-012-001-002-004-005 实现GET /page分页查询接口（InitArQueryDTO条件+返回Result<PageResult<InitArListVO>>含汇总行）
  - P2-012-001-002-004-006 实现GET /detail/{id}详情查询接口（@PathVariable id+返回Result<InitArVO>）
  - P2-012-001-002-004-007 实现POST /submit/{id}提交审核接口（@PathVariable id+调用审核引擎+返回Result<Void>）
  - P2-012-001-002-004-008 补充Knife4j接口文档注解（@Operation描述+@Parameter参数说明+@Schema示例值）
  - P2-012-001-002-004-009 验证期初应收接口（单元测试：CRUD全流程+唯一性校验+审核后写入应收款表+金额精度+到期日计算+已启用客户拦截）

#### P2-012-001-003 期初应付CRUD接口
- P2-012-001-003-001 期初应付Entity/DTO/VO定义（init_ap）
  - P2-012-001-003-001-001 编写InitApEntity类（@TableName"init_ap"+@TableId期初应付ID+期初编号init_no+供应商supplier_id+应付日期ap_date+币种currency_id+应付金额ap_amount+已付金额paid_amount+未付金额unpaid_amount+采购订单号（来源追溯）source_order_no+应付类型ap_type（货款/运费/其他）+账期天数credit_days+到期日due_date+备注remark+@TableLogic逻辑删除）
  - P2-012-001-003-001-002 编写InitApCreateDTO（@NotNull供应商+@NotNull应付日期+@DecimalMin("0.01")应付金额>0+币种+应付类型+账期天数+到期日自动计算+校验规则：供应商维度汇总不允许同一供应商同一订单号重复）
  - P2-012-001-003-001-003 编写InitApQueryDTO（供应商+应付日期范围+应付类型+币种查询条件）
  - P2-012-001-003-001-004 编写InitApVO/InitApListVO（列表VO含期初编号/供应商名称/应付日期/币种名称/应付金额/已付金额/未付金额/到期日/应付类型+@JsonFormat日期格式+字典翻译）
- P2-012-001-003-002 期初应付Mapper开发
  - P2-012-001-003-002-001 编写InitApMapper接口（extends BaseMapperX<InitApEntity>+@Mapper+selectBySupplier按供应商查询+selectPageByCondition条件分页+selectByUniqueKey唯一性校验+sumBySupplier按供应商汇总）
  - P2-012-001-003-002-002 编写InitApMapper.xml（resultMap映射+条件查询SQL含供应商/日期/类型动态where+供应商汇总SQL）
- P2-012-001-003-003 期初应付Service开发（供应商维度汇总+应付款写入）
  - P2-012-001-003-003-001 编写InitApService接口（extends IServiceX<InitApEntity>+create/draftCreate创建+update修改+delete删除+submit提交审核+approve审核通过写入+writeToAp应付款写入方法声明）
  - P2-012-001-003-003-002 编写InitApServiceImpl实现类（@Service+期初编号自动生成调用SerialNumberService.generate("QCAP")+唯一性校验：同供应商+来源订单号不可重复+approve后writeToAp方法：①在应付款表ap_payable中插入应付记录②应付金额=期初金额，已付=0，未付=应付③记录应付款流水+已发生业务的供应商期初不可修改+@Transactional事务管理）
  - P2-012-001-003-003-003 业务校验逻辑（供应商维度+来源订单号唯一性+应付金额>0+到期日>=应付日期+已启用供应商不可修改+币种校验）
- P2-012-001-003-004 期初应付Controller开发
  - P2-012-001-003-004-001 编写InitApController类（@RestController+@RequestMapping"/api/init/ap"+@Tag"期初应付"+注入InitApService）
  - P2-012-001-003-004-002 实现POST /create创建接口（@RequestBody InitApCreateDTO+@Valid校验+返回Result<InitApVO>）
  - P2-012-001-003-004-003 实现PUT /update修改接口（@RequestBody InitApCreateDTO+仅草稿状态+返回Result<Void>）
  - P2-012-001-003-004-004 实现DELETE /delete/{id}删除接口（仅草稿状态+返回Result<Void>）
  - P2-012-001-003-004-005 实现GET /page分页查询接口（InitApQueryDTO条件+返回Result<PageResult<InitApListVO>>含汇总行）
  - P2-012-001-003-004-006 实现GET /detail/{id}详情查询接口（返回Result<InitApVO>）
  - P2-012-001-003-004-007 实现POST /submit/{id}提交审核接口（调用审核引擎+返回Result<Void>）
  - P2-012-001-003-004-008 补充Knife4j接口文档注解
  - P2-012-001-003-004-009 验证期初应付接口（单元测试：CRUD全流程+唯一性校验+审核后写入应付款表+到期日计算+已启用供应商拦截）

#### P2-012-001-004 期初应开票CRUD接口
- P2-012-001-004-001 期初应开票Entity/DTO/VO定义（init_ar_invoice）
  - P2-012-001-004-001-001 编写InitArInvoiceEntity类（@TableName"init_ar_invoice"+@TableId期初应开票ID+期初编号init_no+客户customer_id+商品product_id+规格型号spec+数量quantity+单价unit_price+金额amount+税率tax_rate+税额tax_amount+价税合计total_amount+已开票金额invoiced_amount+未开票金额uninvoiced_amount+来源单据类型source_type+来源单据号source_no+备注remark+@TableLogic逻辑删除）
  - P2-012-001-004-001-002 编写InitArInvoiceCreateDTO（@NotNull客户+@NotNull商品+@DecimalMin("0.01")金额>0+@DecimalMin("0")税率+税额自动计算=金额×税率+价税合计自动计算=金额+税额+校验规则：客户+商品+来源单据号维度唯一性）
  - P2-012-001-004-001-003 编写InitArInvoiceQueryDTO（客户+商品+来源类型查询条件）
  - P2-012-001-004-001-004 编写InitArInvoiceVO/InitArInvoiceListVO（列表VO含期初编号/客户名称/商品名称/金额/税额/价税合计/已开票/未开票+@JsonFormat格式+字典翻译）
- P2-012-001-004-002 期初应开票Mapper开发
  - P2-012-001-004-002-001 编写InitArInvoiceMapper接口（extends BaseMapperX<InitArInvoiceEntity>+@Mapper+selectByCustomer按客户查询+selectPageByCondition条件分页+selectByUniqueKey唯一性校验+sumByCustomer按客户汇总价税合计）
  - P2-012-001-004-002-002 编写InitArInvoiceMapper.xml（resultMap映射+条件查询SQL+客户汇总SQL含金额/税额/价税合计GROUP BY）
- P2-012-001-004-003 期初应开票Service开发（客户+商品维度+待开票金额+税额）
  - P2-012-001-004-003-001 编写InitArInvoiceService接口（extends IServiceX<InitArInvoiceEntity>+create/draftCreate创建+update修改+delete删除+submit提交审核+approve审核通过写入+writeToArInvoice应开票写入方法声明）
  - P2-012-001-004-003-002 编写InitArInvoiceServiceImpl实现类（@Service+期初编号自动生成调用SerialNumberService.generate("QCYKP")+税额自动计算：tax_amount=amount×tax_rate+价税合计自动计算：total_amount=amount+tax_amount+approve后writeToArInvoice方法：在应开票表ar_invoice中插入记录，已开票=0，未开票=价税合计+已发生开票业务的客户期初不可修改+@Transactional事务管理）
  - P2-012-001-004-003-003 业务校验逻辑（客户+商品+来源单据号唯一性+金额>0+税率>=0+税额计算精度（两位小数）+已启用客户不可修改）
- P2-012-001-004-004 期初应开票Controller开发
  - P2-012-001-004-004-001 编写InitArInvoiceController类（@RestController+@RequestMapping"/api/init/ar-invoice"+@Tag"期初应开票"+注入InitArInvoiceService）
  - P2-012-001-004-004-002 实现标准CRUD接口（create/update/delete/page/detail/submit同应收模式）
  - P2-012-001-004-004-003 补充Knife4j接口文档注解
  - P2-012-001-004-004-004 验证期初应开票接口（单元测试：CRUD全流程+税额自动计算+价税合计+唯一性校验+审核写入应开票表）

#### P2-012-001-005 期初应收票CRUD接口
- P2-012-001-005-001 期初应收票Entity/DTO/VO定义（init_ap_invoice）
  - P2-012-001-005-001-001 编写InitApInvoiceEntity类（@TableName"init_ap_invoice"+@TableId期初应收票ID+期初编号init_no+供应商supplier_id+商品product_id+规格型号spec+数量quantity+单价unit_price+金额amount+税率tax_rate+税额tax_amount+价税合计total_amount+已收票金额received_invoice_amount+未收票金额unreceived_invoice_amount+来源单据类型source_type+来源单据号source_no+备注remark+@TableLogic逻辑删除）
  - P2-012-001-005-001-002 编写InitApInvoiceCreateDTO（@NotNull供应商+@NotNull商品+金额>0+税率+税额自动计算+价税合计自动计算+供应商+商品+来源单据号唯一性校验）
  - P2-012-001-005-001-003 编写InitApInvoiceVO/InitApInvoiceListVO（列表VO含供应商名称/商品名称/金额/税额/价税合计/已收票/未收票+格式+翻译）
- P2-012-001-005-002 期初应收票Mapper开发
  - P2-012-001-005-002-001 编写InitApInvoiceMapper接口（extends BaseMapperX<InitApInvoiceEntity>+@Mapper+selectBySupplier按供应商查询+selectPageByCondition条件分页+selectByUniqueKey唯一性校验+sumBySupplier按供应商汇总）
  - P2-012-001-005-002-002 编写InitApInvoiceMapper.xml（resultMap+条件查询SQL+供应商汇总含价税合计GROUP BY）
- P2-012-001-005-003 期初应收票Service开发（供应商+商品维度+待收票金额+税额）
  - P2-012-001-005-003-001 编写InitApInvoiceService接口（extends IServiceX<InitApInvoiceEntity>+标准CRUD+审核+writeToApInvoice方法声明）
  - P2-012-001-005-003-002 编写InitApInvoiceServiceImpl实现类（@Service+期初编号自动生成调用SerialNumberService.generate("QCYSP")+税额自动计算+价税合计自动计算+approve后writeToApInvoice方法：在应收票表ap_invoice中插入记录+已启用供应商不可修改+@Transactional事务管理）
  - P2-012-001-005-003-003 业务校验逻辑（供应商+商品+来源单据号唯一性+金额>0+税率>=0+税额精度+已启用供应商不可修改）
- P2-012-001-005-004 期初应收票Controller开发
  - P2-012-001-005-004-001 编写InitApInvoiceController类（@RestController+@RequestMapping"/api/init/ap-invoice"+@Tag"期初应收票"+注入InitApInvoiceService）
  - P2-012-001-005-004-002 实现标准CRUD接口（create/update/delete/page/detail/submit）
  - P2-012-001-005-004-003 补充Knife4j接口文档注解
  - P2-012-001-005-004-004 验证期初应收票接口（单元测试：CRUD全流程+税额价税合计计算+审核写入应收票表+已启用供应商拦截）

#### P2-012-001-006 期初固定资产CRUD接口
- P2-012-001-006-001 期初固定资产Entity/DTO/VO定义（init_fa_asset）
  - P2-012-001-006-001-001 编写InitFaAssetEntity类（@TableName"init_fa_asset"+@TableId期初资产ID+期初编号init_no+资产编号asset_no+资产名称asset_name+资产分类category_id+规格型号spec+计量单位unit+数量quantity+取得日期acquisition_date+原值original_value+残值率residual_rate+残值residual_value+使用年限useful_life+已使用年限used_years+累计折旧accumulated_depreciation+期初净值net_value+折旧方法depreciation_method+月折旧额monthly_depreciation+使用部门dept_id+存放位置location+保管人custodian_id+备注remark+@TableLogic逻辑删除）
  - P2-012-001-006-001-002 编写InitFaAssetCreateDTO（@NotBlank资产编号+@NotBlank资产名称+@NotNull资产分类+@NotNull取得日期+@DecimalMin("0.01")原值>0+残值率0-100+@NotNull使用年限+@NotNull已使用年限+@NotBlank折旧方法+期初净值自动计算=原值-累计折旧+已使用年限<使用年限校验）
  - P2-012-001-006-001-003 编写InitFaAssetVO（详情VO含资产编号/资产名称/分类名称/原值/累计折旧/期初净值/折旧方法/使用部门/取得日期+@JsonFormat格式+字典翻译）
- P2-012-001-006-002 期初固定资产Mapper开发
  - P2-012-001-006-002-001 编写InitFaAssetMapper接口（extends BaseMapperX<InitFaAssetEntity>+@Mapper+selectByAssetNo按编号查询+selectPageByCondition条件分页+selectByCategory按分类汇总原值折旧+selectByDept按部门汇总）
  - P2-012-001-006-002-002 编写InitFaAssetMapper.xml（resultMap+条件查询+分类汇总GROUP BY含SUM(original_value) SUM(accumulated_depreciation) SUM(net_value)）
- P2-012-001-006-003 期初固定资产Service开发（资产卡片写入）
  - P2-012-001-006-003-001 编写InitFaAssetService接口（extends IServiceX<InitFaAssetEntity>+create/draftCreate创建+update修改+delete删除+submit提交审核+approve审核通过写入+writeToFaAsset资产卡片写入方法声明）
  - P2-012-001-006-003-002 编写InitFaAssetServiceImpl实现类（@Service+期初编号自动生成调用SerialNumberService.generate("QCZC")+净值自动计算：net_value=original_value-accumulated_depreciation+月折旧额按折旧方法自动计算+与P2-002相同的四种折旧方法公式+approve后writeToFaAsset方法：①在资产卡片表fa_asset中创建资产记录②原值/累计折旧/净值/月折旧额等字段直接写入③资产状态设置为"使用中"+资产编号唯一性校验：不能与已存在资产卡片编号重复+@Transactional事务管理）
  - P2-012-001-006-003-003 业务校验逻辑（资产编号唯一性（包括已存在资产卡片）+已使用年限<使用年限+期初净值=原值-累计折旧且>=0+取得日期不可晚于当前日期+折旧方法必须为系统支持的方法+已存在资产的部门不可修改期初）
- P2-012-001-006-004 期初固定资产Controller开发
  - P2-012-001-006-004-001 编写InitFaAssetController类（@RestController+@RequestMapping"/api/init/fa-asset"+@Tag"期初固定资产"+注入InitFaAssetService）
  - P2-012-001-006-004-002 实现POST /create创建接口（@RequestBody InitFaAssetCreateDTO+@Valid+净值自动计算+返回Result<InitFaAssetVO>）
  - P2-012-001-006-004-003 实现PUT /update修改接口（@RequestBody InitFaAssetCreateDTO+仅草稿+返回Result<Void>）
  - P2-012-001-006-004-004 实现DELETE /delete/{id}删除接口（仅草稿状态+返回Result<Void>）
  - P2-012-001-006-004-005 实现GET /page分页查询接口（InitFaAssetQueryDTO条件+返回Result<PageResult<InitFaAssetListVO>>含分类/部门汇总统计）
  - P2-012-001-006-004-006 实现GET /detail/{id}详情查询接口（返回Result<InitFaAssetVO>含折旧计算详情）
  - P2-012-001-006-004-007 实现POST /submit/{id}提交审核接口（调用审核引擎+返回Result<Void>）
  - P2-012-001-006-004-008 补充Knife4j接口文档注解
  - P2-012-001-006-004-009 验证期初固定资产接口（单元测试：CRUD全流程+净值自动计算+折旧方法公式验证+资产编号冲突检测+审核后卡片写入+已使用年限校验）

#### P2-012-001-007 期初科目余额CRUD接口
- P2-012-001-007-001 期初科目余额Entity/DTO/VO定义（init_account_balance）
  - P2-012-001-007-001-001 编写InitAccountBalanceEntity类（@TableName"init_account_balance"+@TableId期初余额ID+期初编号init_no+会计年度fiscal_year+会计期间period_id+科目IDaccount_id+科目编码account_code+科目名称account_name+币种currency_id+期初借方余额debit_balance+期初贷方余额credit_balance+期初余额方向balance_direction（借/贷）+辅助核算类型auxiliary_type+辅助核算值auxiliary_value+备注remark+@TableLogic逻辑删除）
  - P2-012-001-007-001-002 编写InitAccountBalanceCreateDTO（@NotNull会计年度+@NotNull会计期间+@NotNull科目+@DecimalMin("0")期初借方金额>=0+@DecimalMin("0")期初贷方金额>=0+币种+辅助核算可选+校验规则：借方贷方至少一方为0（同一科目不可同时有借有贷）+科目+辅助核算维度唯一性+试算平衡校验（全部借方合计=全部贷方合计））
  - P2-012-001-007-001-003 编写InitAccountBalanceVO/InitAccountBalanceListVO（列表VO含科目编码/科目名称/期初借方/期初贷方/余额方向/币种+@JsonFormat格式+字典翻译）
  - P2-012-001-007-001-004 编写TrialBalanceVO（试算平衡VO含借方合计/贷方合计/差额/是否平衡标志+不平衡科目列表）
- P2-012-001-007-002 期初科目余额Mapper开发
  - P2-012-001-007-002-001 编写InitAccountBalanceMapper接口（extends BaseMapperX<InitAccountBalanceEntity>+@Mapper+selectByAccount按科目查询+selectByPeriod按期间查询+selectPageByCondition条件分页+sumDebitCredit按期间汇总借贷+selectByUniqueKey唯一性校验（科目+辅助核算）+selectTrialBalance试算平衡查询）
  - P2-012-001-007-002-002 编写InitAccountBalanceMapper.xml（resultMap映射+条件查询SQL含科目/期间/辅助核算动态where+试算平衡SQL：SELECT SUM(debit_balance) total_debit,SUM(credit_balance) total_credit FROM init_account_balance WHERE fiscal_year=? AND period_id=?）
- P2-012-001-007-003 期初科目余额Service开发（科目维度汇总+余额写入+试算平衡）
  - P2-012-001-007-003-001 编写InitAccountBalanceService接口（extends IServiceX<InitAccountBalanceEntity>+create/draftCreate创建+batchImport批量导入+update修改+delete删除+submit提交审核+approve审核通过写入+writeToAccountBalance写入科目余额表+checkTrialBalance试算平衡校验+getTrialBalanceResult获取试算结果方法声明）
  - P2-012-001-007-003-002 编写InitAccountBalanceServiceImpl实现类（@Service+期初编号自动生成调用SerialNumberService.generate("QCYE")+checkTrialBalance方法：SELECT SUM(debit_balance),SUM(credit_balance) FROM init_account_balance WHERE period_id=?，若SUM(debit)!=SUM(credit)返回不平衡及差额+batchImport批量导入方法：解析Excel/CSV文件+逐行校验+全部行校验通过后批量插入+beforeSubmit提交前自动试算平衡校验，不平衡不可提交+approve后writeToAccountBalance方法：①在科目余额表fin_account_balance中按科目写入期初余额②余额方向=借方余额-贷方余额③记录余额导入日志+@Transactional事务管理）
  - P2-012-001-007-003-003 业务校验逻辑（试算平衡（全部借方合计=全部贷方合计）+科目编码必须为已定义科目+同一科目+辅助核算维度唯一+借方贷方不可同时>0+已启用（已存在凭证）科目不可修改期初+币种校验+期初余额金额精度两位小数）
- P2-012-001-007-004 期初科目余额Controller开发
  - P2-012-001-007-004-001 编写InitAccountBalanceController类（@RestController+@RequestMapping"/api/init/account-balance"+@Tag"期初科目余额"+注入InitAccountBalanceService）
  - P2-012-001-007-004-002 实现POST /create创建接口（@RequestBody InitAccountBalanceCreateDTO+@Valid+自动试算平衡校验+返回Result<InitAccountBalanceVO>）
  - P2-012-001-007-004-003 实现POST /batchImport批量导入接口（@RequestParam MultipartFile+Excel解析+返回Result<BatchImportResultVO>含成功/失败行数+失败明细）
  - P2-012-001-007-004-004 实现PUT /update修改接口（仅草稿状态+返回Result<Void>）
  - P2-012-001-007-004-005 实现DELETE /delete/{id}删除接口（仅草稿状态+返回Result<Void>）
  - P2-012-001-007-004-006 实现GET /page分页查询接口（InitAccountBalanceQueryDTO条件+返回Result<PageResult<InitAccountBalanceListVO>>）
  - P2-012-001-007-004-007 实现GET /trialBalance试算平衡查询接口（@RequestParam periodId+返回Result<TrialBalanceVO>含借贷合计+差额+平衡标志）
  - P2-012-001-007-004-008 实现POST /submit/{id}提交审核接口（校验试算平衡+调用审核引擎+返回Result<Void>）
  - P2-012-001-007-004-009 补充Knife4j接口文档注解
  - P2-012-001-007-004-010 验证期初科目余额接口（单元测试：CRUD全流程+试算平衡校验+批量导入+审核写入+借贷规则+已启用科目拦截+余额精度+币种校验）

### P2-012-002 期初管理前端页面

#### P2-012-002-000 期初管理工作台页（P02：期初库存金额/期初应收/期初应付/期初科目余额KPI+初始化进度+数据校验状态）
- P2-012-002-000-001 工作台KPI卡片区+图表区+待办区+联调验证
  - P2-012-002-000-001-001 KPI卡片+图表+待办列表+快捷入口+联调

#### P2-012-002-001 期初库存列表页
- P2-012-002-001-001 搜索条件区+数据表格区+联调验证
  - P2-012-002-001-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-012-002-001-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-012-002-001-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-012-002-002 期初应收列表页
- P2-012-002-002-001 搜索条件区+数据表格区+联调验证
  - P2-012-002-002-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-012-002-002-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-012-002-002-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-012-002-003 期初应付列表页
- P2-012-002-003-001 搜索条件区+数据表格区+联调验证
  - P2-012-002-003-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-012-002-003-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-012-002-003-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-012-002-004 期初应开票列表页
- P2-012-002-004-001 搜索条件区+数据表格区+联调验证
  - P2-012-002-004-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-012-002-004-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-012-002-004-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-012-002-005 期初应收票列表页
- P2-012-002-005-001 搜索条件区+数据表格区+联调验证
  - P2-012-002-005-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-012-002-005-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-012-002-005-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-012-002-006 期初固定资产列表页
- P2-012-002-006-001 搜索条件区+数据表格区+联调验证
  - P2-012-002-006-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-012-002-006-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-012-002-006-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-012-002-007 期初科目余额列表页
- P2-012-002-007-001 搜索条件区+数据表格区+联调验证
  - P2-012-002-007-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-012-002-007-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-012-002-007-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

---

## P2-013 系统管理完善模块开发

> **依赖**：P0-004（认证权限基础）
> **概述**：完成系统管理工作台、数据字典管理、编码规则管理、数据视图管理、单据配置、操作日志查询、登录认证增强等

### P2-013-001 系统管理后端开发

#### P2-013-001-000 系统管理工作台聚合数据接口
- P2-013-001-000-001 工作台聚合SQL编写（DV_SYS_WB001：操作日志数+登录日志数+系统公告数+数据字典数+编码规则数+缓存命中率统计）
  - P2-013-001-000-001-001 编写聚合SQL（sys_operation_log COUNT+sys_login_log COUNT+sys_notice COUNT+sys_dict_type COUNT+sys_code_rule COUNT+Redis INFO统计）
  - P2-013-001-000-001-002 编写Service+Controller（@RestController+@RequestMapping"/api/system/dashboard"+聚合数据返回+缓存5分钟）
  - P2-013-001-000-001-003 验证工作台接口（各维度统计数据正确+缓存生效+性能合理）

#### P2-013-001-001 数据字典CRUD接口
> **V10.0已迁移至P0-005-016**：数据字典后端CRUD已迁移至P0-005-016-001（阶段四执行），为P1业务模块提供字典管理界面。P2阶段此处仅保留数据字典增强功能（如字典导入模板管理、字典数据批量导入导出增强、字典引用查询等）。
- P2-013-001-001-001 数据字典Entity/Mapper/Service/Controller开发（sys_dict_type+sys_dict_data主从CRUD+缓存自动刷新）
  - P2-013-001-001-001-001 定义Controller类（@RestController+@RequestMapping+@Tag+注入Service）
  - P2-013-001-001-001-002 实现新增接口（POST+@RequestBody+参数校验+Service调用+响应包装）
  - P2-013-001-001-001-003 实现修改/删除接口（PUT/DELETE+参数校验+Service调用+响应包装）
  - P2-013-001-001-001-004 实现查询接口（GET+分页+条件查询+Service调用+响应包装）
  - P2-013-001-001-001-005 补充Knife4j接口文档注解（@Operation+@Schema+示例值）

#### P2-013-001-002 编码规则CRUD接口
- P2-013-001-002-001 编码规则Entity/Mapper/Service/Controller开发（主从CRUD+编码预览+编码生成调用）
  - P2-013-001-002-001-001 定义Controller类（@RestController+@RequestMapping+@Tag+注入Service）
  - P2-013-001-002-001-002 实现新增接口（POST+@RequestBody+参数校验+Service调用+响应包装）
  - P2-013-001-002-001-003 实现修改/删除接口（PUT/DELETE+参数校验+Service调用+响应包装）
  - P2-013-001-002-001-004 实现查询接口（GET+分页+条件查询+Service调用+响应包装）
  - P2-013-001-002-001-005 补充Knife4j接口文档注解（@Operation+@Schema+示例值）

#### P2-013-001-003 数据视图CRUD接口
- P2-013-001-003-001 数据视图Entity/Mapper/Service/Controller开发（主从CRUD+动态查询调用）
  - P2-013-001-003-001-001 定义Controller类（@RestController+@RequestMapping+@Tag+注入Service）
  - P2-013-001-003-001-002 实现新增接口（POST+@RequestBody+参数校验+Service调用+响应包装）
  - P2-013-001-003-001-003 实现修改/删除接口（PUT/DELETE+参数校验+Service调用+响应包装）
  - P2-013-001-003-001-004 实现查询接口（GET+分页+条件查询+Service调用+响应包装）
  - P2-013-001-003-001-005 补充Knife4j接口文档注解（@Operation+@Schema+示例值）

#### P2-013-001-004 操作日志查询接口
- P2-013-001-004-001 操作日志查询Service+Controller开发（条件查询+分页+操作详情查看）
  - P2-013-001-004-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-013-001-004-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-013-001-004-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-013-001-005 系统参数管理接口
> **V10.0已迁移至P0-005-017**：系统参数后端CRUD已迁移至P0-005-017-001（阶段四执行），为P1业务模块提供参数管理界面。P2阶段此处仅保留系统参数增强功能（如参数变更审计日志、参数分组层级嵌套、参数版本管理等）。
- P2-013-001-005-001 系统参数Entity/Mapper/Service/Controller开发（按分类分组CRUD+参数缓存刷新）
  - P2-013-001-005-001-001 定义Controller类（@RestController+@RequestMapping+@Tag+注入Service）
  - P2-013-001-005-001-002 实现新增接口（POST+@RequestBody+参数校验+Service调用+响应包装）
  - P2-013-001-005-001-003 实现修改/删除接口（PUT/DELETE+参数校验+Service调用+响应包装）
  - P2-013-001-005-001-004 实现查询接口（GET+分页+条件查询+Service调用+响应包装）
  - P2-013-001-005-001-005 补充Knife4j接口文档注解（@Operation+@Schema+示例值）

#### P2-013-001-006 登录认证增强接口
> **已迁移至P0**：本任务所含的验证码生成、密码强度校验、登录失败锁定等基础安全功能已迁移至 P0-004-011（登录安全增强）和 P0-004-012（SSO/OAuth2配置管理）。P2阶段仅保留系统管理配置页面的联调验证。

#### P2-013-001-007 缓存管理后端接口
- P2-013-001-007-001 CacheManager Service+Controller开发（Redis SCAN搜索+Value查看+TTL查询+单条/批量删除+缓存统计+Key前缀过滤）
  - P2-013-001-007-001-001 编写CacheManager Service（RedisTemplate封装+SCAN按前缀搜索+Value JSON序列化+TTL获取+删除操作+操作日志记录）
  - P2-013-001-007-001-002 编写Controller（@RestController+@RequestMapping"/api/system/cache"+权限控制@RequiresPermissions("system:cache:manage")+搜索/查看/删除/统计端点）
  - P2-013-001-007-001-003 验证缓存管理接口（搜索准确+Value正确+TTL准确+删除生效+统计正确+权限拦截）

#### P2-013-001-008 系统公告后端接口
- P2-013-001-008-001 sys_notice表Entity/Mapper/Service/Controller开发（CRUD+定时发布+置顶排序+已读标记+发布范围控制）
  - P2-013-001-008-001-001 编写Entity（sys_notice：公告ID+标题+内容富文本+公告类型+发布时间+置顶标志+状态+发布范围JSON+已读列表）
  - P2-013-001-008-001-002 编写Service+Controller（CRUD+定时发布通过XXL-JOB调度+置顶排序逻辑+已读/未读标记+范围过滤+富文本存储）
  - P2-013-001-008-001-003 验证公告管理接口（CRUD正常+定时发布触发正确+置顶排序正确+已读状态更新+范围过滤生效）

#### P2-013-001-009 单据配置CRUD接口
- P2-013-001-009-001 单据配置Entity/Mapper/Service/Controller开发（sys_doc_config：单据类型编码+名称+模块编码+列表显示列JSONB+表单字段JSONB+流转规则JSONB+模板绑定+启用状态）
  - P2-013-001-009-001-001 编写Entity（sys_doc_config：doc_type/doc_name/module_code/list_columns(JSONB)/form_fields(JSONB)/flow_rules(JSONB)/print_template_id/import_template_id/export_template_id/enable_flag/remark）
  - P2-013-001-009-001-002 编写Service+Controller（CRUD+doc_type唯一性校验+按模块查询+JSONB字段存取+联调验证）
  - P2-013-001-009-001-003 验证单据配置接口（CRUD正常+JSONB字段序列化正确+唯一性校验生效+模板绑定查询正确）

### P2-013-002 系统管理前端页面

#### P2-013-002-000 系统管理工作台（P02）
- P2-013-002-000-001 工作台页面开发（统计卡片：操作日志数+登录日志数+系统公告数+数据字典数+快捷入口：字典管理+编码规则+单据配置+缓存管理）
  - P2-013-002-000-001-001 编写P02工作台页面（统计卡片渲染+快捷入口跳转+趋势图+异常处理）
  - P2-013-002-000-001-002 联调验证（API对接+数据刷新+卡片交互）

#### P2-013-002-001 数据字典管理页
> **V10.0已迁移至P0-005-016-002**：数据字典管理前端页面已迁移至P0-005-016-002（阶段四执行）。P2阶段此处仅保留数据字典增强页面（字典引用查询、字典使用统计、字典批量维护增强）。
- P2-013-002-001-001 左侧字典类型+右侧字典数据+CRUD操作+联调验证
  - P2-013-002-001-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-013-002-001-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-013-002-001-001-003 左侧字典类型异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-013-002-002 编码规则管理列表页
- P2-013-002-002-001 搜索条件区+数据表格区+联调验证
  - P2-013-002-002-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-013-002-002-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-013-002-002-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-013-002-003 新增/编辑编码规则主从表单页
- P2-013-002-003-001 主表字段+编码段从表配置+编码预览+联调验证
  - P2-013-002-003-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P2-013-002-003-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P2-013-002-003-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）

#### P2-013-002-004 数据视图管理列表页
- P2-013-002-004-001 搜索条件区+数据表格区+联调验证
  - P2-013-002-004-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-013-002-004-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-013-002-004-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-013-002-005 新增/编辑数据视图主从表单页
- P2-013-002-005-001 主表字段+视图字段从表配置+联调验证
  - P2-013-002-005-001-001 主表表单布局（el-form+el-row/el-col+分组折叠+labelWidth）
  - P2-013-002-005-001-002 主表字段组件渲染（el-input/el-select/el-date+校验规则）
  - P2-013-002-005-001-003 主表联动逻辑（字段变更联动+自动计算+字典级联+默认值填充）

#### P2-013-002-006 操作日志列表页
- P2-013-002-006-001 搜索条件区+数据表格区（只读）+联调验证
  - P2-013-002-006-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-013-002-006-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-013-002-006-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-013-002-007 系统参数配置页
> **V10.0已迁移至P0-005-017-002**：系统参数配置前端页面已迁移至P0-005-017-002（阶段四执行）。P2阶段此处仅保留系统参数增强页面（参数变更审计、参数版本管理、参数分组层级嵌套配置）。
- P2-013-002-007-001 按分类分组+参数值编辑+联调验证
  - P2-013-002-007-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-013-002-007-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-013-002-007-001-003 按分类分组异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-013-002-008 登录页
> **已迁移至P0**：登录安全相关功能（验证码、密码强度、登录锁定）已迁移至 P0-004-011 和 P0-004-012。P2阶段仅保留登录页面的系统管理端联调验证。

#### P2-013-002-009 密码修改页
- P2-013-002-009-001 旧密码验证+新密码强度校验+确认密码+联调验证
  - P2-013-002-009-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-013-002-009-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-013-002-009-001-003 旧密码验证异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-013-002-010 缓存管理（Redis缓存查看+按Key前缀搜索+缓存清除+统计+预热）
> **后端对应**：P2-013-001-007
- P2-013-002-010-001 前端开发（搜索框+Key列表+Value展示+清除按钮+统计面板）
  - P2-013-002-010-001-001 实现缓存管理页面（Key搜索+列表+Value JSON高亮+清除+统计卡片）

#### P2-013-002-011 系统公告管理（公告CRUD+定时发布+置顶+已读未读+富文本+范围控制）
> **后端对应**：P2-013-001-008
- P2-013-002-011-001 前端开发（公告管理页+详情弹窗+富文本编辑器+未读Badge）
  - P2-013-002-011-001-001 实现公告管理页（P04列表+P02编辑+富文本+定时发布+置顶开关）
  - P2-013-002-011-001-002 实现公告通知弹窗（登录后弹窗+未读公告+已读标记+详情跳转）

#### P2-013-002-012 单据配置列表页（P04）
- P2-013-002-012-001 搜索条件区+数据表格区（单据类型+模块编码+启用状态筛选）+联调验证
  - P2-013-002-012-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-013-002-012-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-013-002-012-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+字段联动过滤）

#### P2-013-002-013 新增/编辑单据配置表单页（P07）
- P2-013-002-013-001 主表字段表单+JSONB配置编辑器（列表列配置/表单字段配置/流转规则配置可视化编辑）+联调验证
  - P2-013-002-013-001-001 主表表单布局（el-form+el-row/el-col+单据类型+模块选择+启用开关+模板绑定下拉）
  - P2-013-002-013-001-002 JSONB配置可视化编辑器（列表列配置拖拽排序+表单字段配置+流转规则条件配置）
  - P2-013-002-013-001-003 联调验证（表单提交+JSONB序列化正确+模板绑定查询+唯一性校验）

---

## P2-014 运维管理模块开发

> **依赖**：P0-001（后端框架）
> **概述**：完成定时任务、任务日志、数据备份、运维告警、服务器监控、在线用户管理

### P2-014-001 运维管理后端开发

#### P2-014-001-001 定时任务CRUD接口
- P2-014-001-001-001 定时任务Entity/Mapper/Service/Controller开发（XXL-JOB集成+任务手动触发）
  - P2-014-001-001-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-014-001-001-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-014-001-001-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-014-001-002 任务执行日志查询接口
- P2-014-001-002-001 任务执行日志查询Service+Controller开发（条件查询+分页）
  - P2-014-001-002-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-014-001-002-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-014-001-002-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-014-001-003 数据备份CRUD接口
- P2-014-001-003-001 数据备份Entity/Mapper/Service/Controller开发（备份执行+备份文件下载+恢复）
  - P2-014-001-003-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-014-001-003-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-014-001-003-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-014-001-004 运维告警CRUD接口
- P2-014-001-004-001 运维告警Entity/Mapper/Service/Controller开发（告警规则+告警触发+告警通知）
  - P2-014-001-004-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-014-001-004-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-014-001-004-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-014-001-005 服务器监控数据接口
- P2-014-001-005-001 CPU+内存+磁盘+JVM+数据库连接池实时采集+历史趋势Service+Controller开发
  - P2-014-001-005-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-014-001-005-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-014-001-005-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-014-001-006 在线用户管理接口
- P2-014-001-006-001 在线用户列表+强制下线+会话信息查看+登录IP/设备/时间开发
  - P2-014-001-006-001-001 实现在线用户列表查询+强制下线+会话信息查看+登录IP/设备/时间展示
    - P2-014-001-006-001-001-001 实现在线用户列表查询（Sa-Token会话列表+分页+按用户名/IP/时间筛选）
    - P2-014-001-006-001-001-002 实现强制下线（踢出指定用户会话+前端Token失效+实时通知）
    - P2-014-001-006-001-001-003 实现会话信息查看（登录IP+设备+浏览器+登录时间+最后活跃时间）
  - P2-014-001-006-001-002 验证功能（核心用例通过+边界场景+异常降级）
  - P2-014-001-006-001-003 验证功能完整性（核心用例+边界场景+集成联调+异常降级）

### P2-014-002 运维管理前端页面

#### P2-014-002-000 运维管理工作台页（P02：定时任务数/服务器状态/告警数/在线用户数KPI+系统健康度+告警列表+快捷操作）
- P2-014-002-000-001 工作台KPI卡片区+图表区+待办区+联调验证
  - P2-014-002-000-001-001 KPI卡片+图表+待办列表+快捷入口+联调

#### P2-014-002-000a 模块授权管理页（P13配置页：模块列表+授权状态+授权期限+启用/停用）
- P2-014-002-000a-001 模块授权配置表单+联调验证
  - P2-014-002-000a-001-001 模块列表+授权状态+授权期限+启用/停用+联调

#### P2-014-002-001 定时任务管理列表页
- P2-014-002-001-001 搜索条件区+数据表格区+手动触发按钮+联调验证
  - P2-014-002-001-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-014-002-001-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-014-002-001-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-014-002-002 任务执行日志列表页
- P2-014-002-002-001 搜索条件区+数据表格区+联调验证
  - P2-014-002-002-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-014-002-002-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-014-002-002-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-014-002-003 数据备份列表页
- P2-014-002-003-001 搜索条件区+数据表格区+备份执行+文件下载+联调验证
  - P2-014-002-003-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-014-002-003-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-014-002-003-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-014-002-004 运维告警列表页
- P2-014-002-004-001 搜索条件区+数据表格区+联调验证
  - P2-014-002-004-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-014-002-004-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-014-002-004-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-014-002-005 服务器监控页
- P2-014-002-005-001 CPU/内存/磁盘实时曲线+JVM状态+数据库连接池+告警状态+联调验证
  - P2-014-002-005-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-014-002-005-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-014-002-005-001-003 内存异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-014-002-006 在线用户管理列表页
- P2-014-002-006-001 在线用户列表+登录信息+强制下线操作+联调验证
  - P2-014-002-006-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-014-002-006-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-014-002-006-001-003 在线用户列表异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）
    - P2-014-002-007 服务监控（Spring Boot Actuator+JVM指标+线程池+HTTP统计+健康检查+告警阈值）
      - P2-014-002-007-001 后端开发（Actuator集成+指标采集+健康检查+告警阈值+指标API）
        - P2-014-002-007-001-001 集成Actuator（/health+/metrics+/info端点+安全配置+自定义指标）
        - P2-014-002-007-001-002 编写监控指标API（JVM/CPU/线程池/HTTP统计/DB连接池/GC+时间范围查询）
        - P2-014-002-007-001-003 编写告警阈值配置（阈值CRUD+超阈值告警+通知推送）
      - P2-014-002-007-002 前端开发（监控仪表盘：实时图表+健康灯+告警配置）
        - P2-014-002-007-002-001 实现监控仪表盘（ECharts实时图表+JVM/线程/请求+健康灯+告警配置）
    - P2-014-002-008 数据恢复（备份文件选择+恢复校验+恢复执行+恢复验证+日志+回滚）
      - P2-014-002-008-001 后端开发（恢复Service：备份列表+MD5校验+pg_restore+进度+日志+回滚）
        - P2-014-002-008-001-001 编写恢复Service（备份列表+校验+执行+进度+日志+权限控制+二次确认）
        - P2-014-002-008-001-002 编写Controller+权限（@RequiresPermissions("ops:recovery:execute")+日志）
      - P2-014-002-008-002 前端开发（恢复页面：备份列表+恢复按钮+进度条+日志+确认弹窗）
        - P2-014-002-008-002-001 实现恢复页面（备份列表+恢复按钮+进度条+日志+二次确认弹窗）

---

## P2-015 多语言模块开发

> **依赖**：P0-002（前端框架含i18n基础）
> **概述**：完成多语言翻译管理、语言切换、菜单/表格/字典/文案多语言配置

### P2-015-001 多语言后端开发

#### P2-015-001-001 语言定义CRUD接口
- P2-015-001-001-001 语言定义Entity/Mapper/Service/Controller开发
  - P2-015-001-001-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-015-001-001-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-015-001-001-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-015-001-002 翻译键值CRUD接口
- P2-015-001-002-001 翻译键值Entity/Mapper/Service/Controller开发（主从CRUD）
  - P2-015-001-002-001-001 定义Controller类（@RestController+@RequestMapping+@Tag+注入Service）
  - P2-015-001-002-001-002 实现新增接口（POST+@RequestBody+参数校验+Service调用+响应包装）
  - P2-015-001-002-001-003 实现修改/删除接口（PUT/DELETE+参数校验+Service调用+响应包装）
  - P2-015-001-002-001-004 实现查询接口（GET+分页+条件查询+Service调用+响应包装）
  - P2-015-001-002-001-005 补充Knife4j接口文档注解（@Operation+@Schema+示例值）

#### P2-015-001-003 翻译数据导出接口
- P2-015-001-003-001 按语言导出全部翻译键值对为JSON/Excel开发
  - P2-015-001-003-001-001 实现导出逻辑（查询条件+API+文件流+文件名处理）
  - P2-015-001-003-001-002 导出异常处理（大数据量分页+超时+失败重试提示）

#### P2-015-001-004 翻译数据导入接口
- P2-015-001-004-001 按语言批量导入翻译键值对+增量更新开发
  - P2-015-001-004-001-001 实现导入逻辑（上传+模板校验+数据解析+API批量调用）
  - P2-015-001-004-001-002 导入结果处理（成功数+失败行+错误导出+导入报告）

#### P2-015-001-005 菜单多语言CRUD接口
- P2-015-001-005-001 菜单名称按语言翻译+菜单多语言缓存+前端菜单加载时按语言返回开发
  - P2-015-001-005-001-001 实现菜单名称按语言翻译+菜单多语言Redis缓存+前端菜单加载时按当前语言返回
    - P2-015-001-005-001-001-001 实现菜单名称按语言翻译（菜单ID+语言编码→翻译文本+缺失翻译回退默认语言）
    - P2-015-001-005-001-001-002 实现菜单多语言Redis缓存（按语言编码缓存菜单树+翻译变更时清除缓存+TTL 24h）
    - P2-015-001-005-001-001-003 实现前端菜单加载时按语言返回（请求头Accept-Language→后端按语言返回菜单+切换语言重新加载）
  - P2-015-001-005-001-002 验证功能（核心用例通过+边界场景+异常降级）
  - P2-015-001-005-001-003 验证功能完整性（核心用例+边界场景+集成联调+异常降级）

#### P2-015-001-006 表格列多语言CRUD接口
- P2-015-001-006-001 表格列标题按语言翻译+视图配置加载时按语言返回列标题开发
  - P2-015-001-006-001-001 实现表格列标题按语言翻译+视图配置加载时按当前语言返回列标题
    - P2-015-001-006-001-001-001 实现表格列标题按语言翻译（列编码+语言编码→翻译列标题+缺失回退默认语言）
    - P2-015-001-006-001-001-002 实现视图配置加载时按语言返回（视图配置API+Accept-Language→返回翻译后列标题）
  - P2-015-001-006-001-002 验证功能（核心用例通过+边界场景+异常降级）
  - P2-015-001-006-001-003 验证功能完整性（核心用例+边界场景+集成联调+异常降级）

#### P2-015-001-007 字典多语言CRUD接口
- P2-015-001-007-001 字典项名称按语言翻译+字典加载时按语言返回字典项名称开发
  - P2-015-001-007-001-001 实现字典项名称按语言翻译+字典加载时按当前语言返回字典项名称+缓存
    - P2-015-001-007-001-001-001 实现字典项名称按语言翻译（字典编码+项值+语言编码→翻译文本+缺失回退）
    - P2-015-001-007-001-001-002 实现字典加载时按语言返回（字典API+Accept-Language→返回翻译后字典项+前端缓存）
  - P2-015-001-007-001-002 验证功能（核心用例通过+边界场景+异常降级）
  - P2-015-001-007-001-003 验证功能完整性（核心用例+边界场景+集成联调+异常降级）

#### P2-015-001-008 系统文案配置CRUD接口
- P2-015-001-008-001 系统提示语/按钮文字/错误消息按语言翻译+文案缓存+前端文案加载接口开发
  - P2-015-001-008-001-001 定义接口路由与方法签名
  - P2-015-001-008-001-002 实现接口逻辑（参数处理+Service调用+响应包装）

### P2-015-002 多语言前端页面

#### P2-015-002-000 多语言管理工作台页（P02：已配语言数/翻译覆盖率/待翻译条目数KPI+翻译进度+各模块覆盖率）
- P2-015-002-000-001 工作台KPI卡片区+图表区+待办区+联调验证
  - P2-015-002-000-001-001 KPI卡片+图表+待办列表+快捷入口+联调

#### P2-015-002-001 语言定义列表页
- P2-015-002-001-001 搜索条件区+数据表格区+联调验证
  - P2-015-002-001-001-001 搜索表单布局（el-form+el-row/el-col栅格+labelWidth+展开收起）
  - P2-015-002-001-001-002 搜索字段组件渲染（el-input/el-select/el-date-picker+默认值）
  - P2-015-002-001-001-003 实现搜索条件区搜索交互（搜索/重置+回车搜索+日期快捷选择+字段联动过滤）

#### P2-015-002-002 翻译管理列表页
- P2-015-002-002-001 语言切换标签页+翻译键值对编辑+联调验证
  - P2-015-002-002-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-015-002-002-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-015-002-002-001-003 语言切换标签异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-015-002-003 全局语言切换集成
- P2-015-002-003-001 顶部导航栏语言选择器+切换后全页面刷新翻译+联调验证
  - P2-015-002-003-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-015-002-003-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-015-002-003-001-003 顶部导航栏语异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-015-002-004 菜单多语言配置页
- P2-015-002-004-001 菜单树+各语言翻译编辑+联调验证
  - P2-015-002-004-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-015-002-004-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-015-002-004-001-003 菜单树异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-015-002-005 表格列多语言配置页
- P2-015-002-005-001 表格列列表+各语言列标题翻译编辑+联调验证
  - P2-015-002-005-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-015-002-005-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-015-002-005-001-003 表格列列表异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-015-002-006 字典多语言配置页
- P2-015-002-006-001 字典类型选择+字典项各语言翻译编辑+联调验证
  - P2-015-002-006-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-015-002-006-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-015-002-006-001-003 字典类型选择异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-015-002-007 系统文案配置页
- P2-015-002-007-001 文案分类+各语言文案内容编辑+预览+联调验证
  - P2-015-002-007-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-015-002-007-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-015-002-007-001-003 文案分类异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）
    - P2-015-002-008 多语言导入导出（按语言导出翻译Excel/JSON+批量导入+校验+覆盖率统计）
      - P2-015-002-008-001 后端开发（导出：按语言/模块导出+导入：Excel解析+校验+批量更新+覆盖率）
        - P2-015-002-008-001-001 编写导出Service（按语言+模块查询翻译→构建Excel/JSON+模板下载）
        - P2-015-002-008-001-002 编写导入Service（Excel上传→解析→校验（必填+长度+编码存在性）→批量更新+错误报告）
        - P2-015-002-008-001-003 编写翻译覆盖率统计（已翻译/总词条×100%+按模块统计+缺失列表）
      - P2-015-002-008-002 前端开发（导入导出页面+模板下载+进度+错误报告+覆盖率面板）
        - P2-015-002-008-002-001 实现导入导出页面（语言选择+导出按钮+上传+校验结果+覆盖率进度条）
    - P2-015-002-009 表格多语言配置（按数据视图+列维度展示表格翻译+批量编辑列标题翻译+复用i18n_translation表category=table）
      - P2-015-002-009-001 后端开发（表格翻译CRUD+按视图+列维度查询+批量更新+复用i18n_translation）
        - P2-015-002-009-001-001 编写表格翻译Service（category=table+视图ID+列名+多语言值+批量更新+按视图查询）
        - P2-015-002-009-001-002 验证表格多语言（CRUD+批量编辑+按视图查询+复用i18n表）
      - P2-015-002-009-002 前端开发（P01主从列表页+P07批量编辑弹窗：视图选择+列标题翻译+批量保存）
        - P2-015-002-009-002-001 实现表格多语言页（视图下拉+列标题列表+批量编辑+保存+预览效果）

---

## P2-016 首页仪表盘模块开发

> **依赖**：P0-005（P01仪表盘组件）
> **概述**：完成系统首页仪表盘配置、KPI卡片、图表展示

### P2-016-001 首页仪表盘后端开发

#### P2-016-001-001 仪表盘配置CRUD接口
- P2-016-001-001-001 仪表盘配置Entity/Mapper/Service/Controller开发（布局JSON+组件配置JSON+用户个性化配置存储）
  - P2-016-001-001-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-016-001-001-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-016-001-001-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-016-001-002 KPI数据聚合接口
- P2-016-001-002-001 KPI数据聚合Service+Controller开发（销售/采购/库存/财务/生产多维KPI汇总）
  - P2-016-001-002-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-016-001-002-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-016-001-002-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-016-001-003 图表数据聚合接口
- P2-016-001-003-001 图表数据聚合Service+Controller开发（按时间维度/分类维度的趋势数据+对比数据）
  - P2-016-001-003-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-016-001-003-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-016-001-003-001-003 补充接口文档注解（@Operation+@Schema+示例值）

### P2-016-002 首页仪表盘前端页面

#### P2-016-002-001 首页仪表盘页
- P2-016-002-001-001 拖拽网格布局+KPI卡片+多类型图表+实时刷新+联调验证
  - P2-016-002-001-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-016-002-001-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-016-002-001-001-003 拖拽网格布局异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-016-002-002 仪表盘配置页
- P2-016-002-002-001 组件拖拽+属性配置+布局调整+联调验证
  - P2-016-002-002-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-016-002-002-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-016-002-002-001-003 组件拖拽异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

---

## P2-017 移动端适配模块开发

> **依赖**：P0-004（移动端菜单）、各业务模块前端完成
> **概述**：完成移动端首页、审批、消息、报表、单据操作、关键业务查询

### P2-017-001 移动端后端开发

#### P2-017-001-001 移动端菜单CRUD接口
- P2-017-001-001-001 移动端菜单Entity/Mapper/Service/Controller开发
  - P2-017-001-001-001-001 定义Controller类+@RequestMapping路径+注入Service
  - P2-017-001-001-001-002 实现接口方法（参数接收+校验+Service调用+响应包装）
  - P2-017-001-001-001-003 补充接口文档注解（@Operation+@Schema+示例值）

#### P2-017-001-002 移动端审批接口
- P2-017-001-002-001 待审/已审列表+审批操作+审批意见+审批催办+审批委托开发
  - P2-017-001-002-001-001 实现待审/已审列表+审批操作+审批意见+审批催办+审批委托功能
    - P2-017-001-002-001-001-001 实现待审/已审列表（Tab切换+分页+按类型筛选+状态标签+审批人/时间显示）
    - P2-017-001-002-001-001-002 实现审批操作（同意/拒绝按钮+审批意见输入+签名+附件+操作后刷新列表）
    - P2-017-001-002-001-001-003 实现审批催办+委托（催办通知发送+委托设置弹窗+委托人选择+委托时间段）
  - P2-017-001-002-001-002 验证功能（核心用例通过+边界场景+异常降级）
  - P2-017-001-002-001-003 验证功能完整性（核心用例+边界场景+集成联调+异常降级）

#### P2-017-001-003 移动端消息推送接口
- P2-017-001-003-001 APNs/FCM集成+消息模板+推送策略开发
  - P2-017-001-003-001-001 实现APNs/FCM集成+消息模板配置+推送策略（即时/定时/批量）
  - P2-017-001-003-001-002 验证功能（核心用例通过+边界场景+异常降级）
  - P2-017-001-003-001-003 验证功能完整性（核心用例+边界场景+集成联调+异常降级）

#### P2-017-001-004 移动端关键业务查询接口
- P2-017-001-004-001 销售订单/采购订单/库存查询/应收应付统计开发
  - P2-017-001-004-001-001 实现移动端核心业务：销售订单/采购订单/库存查询/应收应付统计页面
    - P2-017-001-004-001-001-001 实现移动端销售订单页面（订单列表+详情+状态标签+搜索+快捷创建）
    - P2-017-001-004-001-001-002 实现移动端采购订单+库存查询页面（采购列表+库存查询+扫码查库存+实时库存数）
    - P2-017-001-004-001-001-003 实现移动端应收应付统计页面（应收/应付金额+逾期标记+趋势图+筛选维度）
  - P2-017-001-004-001-002 验证功能（核心用例通过+边界场景+异常降级）
  - P2-017-001-004-001-003 验证功能完整性（核心用例+边界场景+集成联调+异常降级）

#### P2-017-001-005 移动端报表数据接口
- P2-017-001-005-001 KPI概览+销售报表+采购报表+库存报表+财务报表开发（适配移动端图表数据格式）
  - P2-017-001-005-001-001 报表查询条件区（日期+组织+商品+客户等筛选）
  - P2-017-001-005-001-002 报表数据展示（多维列+合计+小计+格式高亮+图表）
  - P2-017-001-005-001-003 报表导出打印（Excel+打印预览+PDF+大数据分页）

#### P2-017-001-006 移动端单据操作接口
- P2-017-001-006-001 单据新增/编辑/提交/审核+离线模式支持+数据同步开发
  - P2-017-001-006-001-001 实现移动端单据新增/编辑/提交/审核+离线模式支持+网络恢复后数据同步
  - P2-017-001-006-001-002 验证功能（核心用例通过+边界场景+异常降级）
  - P2-017-001-006-001-003 验证功能完整性（核心用例+边界场景+集成联调+异常降级）

### P2-017-002 移动端前端页面

#### P2-017-002-000 移动端管理工作台页（P02：移动用户数/移动版本/推送成功率KPI+版本分布+推送统计+移动菜单配置快捷入口）
- P2-017-002-000-001 工作台KPI卡片区+图表区+待办区+联调验证
  - P2-017-002-000-001-001 KPI卡片+图表+待办列表+快捷入口+联调

#### P2-017-002-000a 移动基础配置页（P13配置页：AppID/AppSecret/推送通道/离线策略+设计文档11.32.1要求）
- P2-017-002-000a-001 移动基础配置表单+联调验证
  - P2-017-002-000a-001-001 AppID+AppSecret+推送通道+离线策略配置+联调

#### P2-017-002-000b 移动版本管理页（P04列表页：版本号+强制更新+更新说明+发布时间+设计文档11.32.2b要求）
- P2-017-002-000b-001 版本管理列表+新增/编辑表单+联调验证
  - P2-017-002-000b-001-001 版本号+强制更新+更新说明+发布时间+联调

#### P2-017-002-001 移动端首页
- P2-017-002-001-001 H5适配：快捷功能入口+待办提醒+KPI卡片+联调验证
  - P2-017-002-001-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-017-002-001-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-017-002-001-001-003 适配异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-017-002-002 移动端审批列表页
- P2-017-002-002-001 待审/已审/待提交标签页+审批操作按钮+联调验证
  - P2-017-002-002-001-001 顶部操作栏布局（新增/修改/删除/导出+v-permission权限控制）
  - P2-017-002-002-001-002 批量操作逻辑（多选+批量删除/审核+确认弹窗+选中校验）
    - P2-017-002-002-001-002-001 实现多选逻辑（vxe-table checkbox多选+全选+跨页选中保持+选中行数据收集）
    - P2-017-002-002-001-002-002 实现选中校验（至少选1条+已审核不可删除+草稿才可删除+不符合条件提示）
    - P2-017-002-002-001-002-003 实现批量操作确认弹窗（操作类型+影响数量提示+确认/取消按钮+二次确认）
    - P2-017-002-002-001-002-004 实现批量操作执行（批量调用API+进度提示+成功/失败统计+列表刷新）
  - P2-017-002-002-001-003 操作事件处理（按钮点击→弹窗/路由+参数传递+回调刷新）

#### P2-017-002-003 移动端审批详情页
- P2-017-002-003-001 单据信息+审批流程图+审批意见输入+审批操作+联调验证
  - P2-017-002-003-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-017-002-003-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-017-002-003-001-003 单据信息异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-017-002-004 移动端消息中心页
- P2-017-002-004-001 消息列表+未读标记+消息详情+联调验证
  - P2-017-002-004-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-017-002-004-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-017-002-004-001-003 消息列表异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-017-002-005 移动端关键业务查询页
- P2-017-002-005-001 销售/采购/库存/财务Tab+联调验证
  - P2-017-002-005-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-017-002-005-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-017-002-005-001-003 销售异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-017-002-006 移动端报表页
- P2-017-002-006-001 KPI概览+图表展示+数据钻取+联调验证
  - P2-017-002-006-001-001 新增流程联调（表单提交→API→成功反馈→列表刷新→关闭弹窗）
  - P2-017-002-006-001-002 编辑流程联调（加载数据→回填→修改提交→成功反馈→列表刷新）
  - P2-017-002-006-001-003 概览异常流程联调（校验失败提示+接口错误处理+重复提交防护+loading状态）

#### P2-017-002-007 移动端单据操作页
- P2-017-002-007-001 单据新增表单+明细行编辑+拍照上传+扫码输入+离线暂存+联调验证
  - P2-017-002-007-001-001 明细行vxe-table定义（列配置+可编辑列+行号+增删行+合计行）
  - P2-017-002-007-001-002 明细行编辑交互（单元格编辑+下拉+弹出选择器+行增删复制）
  - P2-017-002-007-001-003 明细行自动计算（数量×单价=金额+税率+折扣+合计汇总）
    - P2-017-002-008 移动端配置管理（功能模块开关+页面布局配置+离线策略+推送策略）
      - P2-017-002-008-001 后端开发（mobile_config表：模块开关+布局JSON+离线策略+推送策略CRUD）
        - P2-017-002-008-001-001 编写DDL+Entity/Mapper/Service/Controller（配置项编码+值JSON+开关+策略）
        - P2-017-002-008-001-002 验证配置管理（CRUD+加载+缓存+实时生效）
      - P2-017-002-008-002 前端开发（P13配置页：模块Toggle+布局预览+策略配置）
        - P2-017-002-008-002-001 实现配置页面（模块开关+布局配置+策略选择+保存+实时生效）

---

# 第五章 开发顺序总览与依赖矩阵

## 5.1 整体开发阶段划分

| 阶段 | 包含任务编号 | 阶段目标 | 前置阶段 |
|------|------------|---------|---------|
| **阶段一：基础设施** | P0-001 + P0-002 + P0-013 | 后端框架+前端框架+Docker/Nginx/CI/CD搭建完成 | 无 |
| **阶段二：数据库建表** | P0-003 | 全部数据表创建完成+Flyway迁移+初始化数据写入 | 阶段一 |
| **阶段三：认证权限** | P0-004 | 登录认证+用户管理+权限体系+登录安全可用 | 阶段一+二 |
| **阶段四：测试与公共组件** | P0-005 + P0-014 | 15种页面类型+全部录入/选择/业务组件+前后端测试框架可用+数据字典管理界面+系统参数管理界面 | 阶段一 |
| **阶段五：基础数据** | P0-006~P0-012 | 组织/商品/客户/供应商/仓库/财务/HRM全部可用 | 阶段二+三+四 |
| **阶段六：通用引擎** | P1-001~P1-004 | 审核/审批/消息/流转引擎全部可用 | 阶段三+四 |
| **阶段七：核心业务** | P1-005~P1-009 | 销售/采购/库存/生产/委外核心链路可用 | 阶段五+六 |
| **阶段八：财务业务** | P1-010~P1-015 | 应收/应付/凭证/账簿/报表/成本可用 | 阶段七 |
| **阶段九：扩展业务** | P2-001~P2-007 | 费用/资产/期末/样品/借用/租赁/售后可用 | 阶段八 |
| **阶段十：系统完善** | P2-008~P2-017 | OA/AI/模板/报表/期初/运维/多语言/仪表盘/移动端可用 | 阶段八 |

## 5.2 核心依赖链路图

```
P0-001(后端框架) ─┬→ P0-003(数据库) ─→ P0-004(认证权限) ─→ P0-006(组织架构) ─┬→ P0-007(商品) ─┬→ P1-005(销售) ─┬→ P1-010(应收) ─→ P1-012(凭证)
                  │                                                                       │                │                                  │
P0-002(前端框架) ─┤                                                   P0-005(公共组件) ─┤                ├→ P1-006(采购) ─┬→ P1-011(应付) ─→ P1-013(账簿) ─→ P1-014(报表)
                  │                                                                       │                │
                  ├→ P1-001(审核引擎) ─→ P1-002(审批流程) ─────────────→ P1-004(流转引擎) ─┤                ├→ P1-007(库存) ─→ P1-015(成本)
                  │                                                                       │                │
                  ├→ P1-003(消息管理) ──────────────────────────────────────────────────────┤                ├→ P1-008(生产)
                  │                                                                       │                │
                  └→ P0-008(客户) ─────────────────────────────────────────────────────────┘                └→ P1-009(委外)
                                                                                         │
                  P0-009(供应商) ───────────────────────────────────────────────────────────┘
                  P0-010(仓库) ─────────────────────────────────────────────────────────────┘
                  P0-011(财务基础) ──────────────────────────────────────────────────────────┘
                  P0-012(HRM) ─────────────────────────────────────────────────────────────┘
```

## 5.3 模块间依赖关系矩阵

| 被依赖模块 ↓ / 依赖模块 → | P0-006组织 | P0-007商品 | P0-008客户 | P0-009供应商 | P0-010仓库 | P0-011财务 | P0-012HRM | P1-001审核 | P1-003消息 | P1-004流转 | P1-005销售 | P1-006采购 | P1-007库存 |
|---|---|---|---|---|---|---|---|---|---|---|---|---|---|
| **P1-005 销售** | ✓ | ✓ | ✓ | | ✓ | ✓ | | ✓ | | ✓ | - | | |
| **P1-006 采购** | ✓ | ✓ | | ✓ | ✓ | ✓ | | ✓ | | ✓ | | - | |
| **P1-007 库存** | | ✓ | | | ✓ | | | ✓ | | | ✓ | ✓ | - |
| **P1-008 生产** | | ✓ | | | ✓ | | | ✓ | | | | | ✓ |
| **P1-009 委外** | | ✓ | | ✓ | ✓ | | | ✓ | | | | | ✓ |
| **P1-010 应收** | | | ✓ | | | ✓ | | | | | ✓ | | |
| **P1-011 应付** | | | | ✓ | | ✓ | | | | | | ✓ | |
| **P1-012 凭证** | | | | | | ✓ | | | | | | | |
| **P1-015 成本** | | ✓ | | | | ✓ | | | | | ✓ | | ✓ |

## 5.4 P0级任务执行顺序详排

```
第1步：P0-001-001 后端项目初始化与基础配置
第2步：P0-001-002 全局异常处理与统一响应
第3步：P0-001-003 数据库连接与基础DAO层
第4步：P0-001-004 Sa-Token认证框架集成
第5步：P0-001-005 编码引擎基础服务
第6步：P0-001-006 数据视图引擎基础服务
第7步：P0-001-007 系统参数服务基础
第8步：P0-001-008 公共基础服务（文件上传/操作日志/导入导出/树形工具）
第9步：P0-001-009 通用单据公共从表服务（doc_detail三表）
  ─── 以上可与前端并行 ───
第10步：P0-002-001 前端项目初始化与基础配置
第11步：P0-002-002 路由与布局系统
第12步：P0-002-003 状态管理与请求层
第13步：P0-002-004 前端工具层
第14步：P0-002-005 国际化基础
第15步：P0-002-006 样式体系
  ─── DevOps与测试基础 ───
第16步：P0-013-001 Docker容器化配置
第17步：P0-013-002 Nginx反向代理与静态资源配置
第18步：P0-014-001 后端测试框架搭建（JUnit 5 + Mockito + Testcontainers）
第19步：P0-014-002 前端测试框架搭建（Vitest + Vue Test Utils）
第20步：P0-014-003 测试规范与基础设施
  ─── 数据库建表 ───
第21步：P0-003-001~P0-003-021 全部数据表创建
第22步：P0-003-022 Flyway迁移脚本管理与初始化数据
  ─── 认证权限 ───
第23步：P0-004-001~P0-004-012 认证与权限完整开发（含登录安全+SSO/OAuth2配置）
  ─── 公共组件 ───
第24步：P0-005-001~P0-005-017 全部公共组件开发（含数据字典管理P0-005-016+系统参数管理P0-005-017）
  ─── 基础数据模块 ───
第25步：P0-006 组织架构模块
第26步：P0-007 商品管理模块
第27步：P0-008 CRM客户管理模块
第28步：P0-009 SRM供应商管理模块
第29步：P0-010 仓库与库位管理
第30步：P0-011 财务基础设置
第31步：P0-012 HRM人力资源管理
```

## 5.5 P1级任务执行顺序详排

```
第32步：P1-001 通用单据审核引擎
第33步：P1-002 审批流程模块
第34步：P1-003 消息管理模块
第35步：P1-004 通用单据流转引擎
第36步：P1-005 销售管理模块
第37步：P1-006 采购管理模块
第38步：P1-007 库存管理模块
第39步：P1-008 生产管理模块
第40步：P1-009 委外管理模块
第41步：P1-010 应收管理模块
第42步：P1-011 应付管理模块
第43步：P1-012 凭证管理模块
第44步：P1-013 账簿查询模块
第45步：P1-014 财务报表模块
第46步：P1-015 成本核算模块
```

## 5.6 P2级任务执行顺序详排

```
第47步：P2-013 系统管理完善（工作台/数据字典/编码规则/数据视图/单据配置/操作日志/缓存管理/公告）
第48步：P2-010 模板中心（提前：为P1业务模块提供打印/导入/导出模板管理支撑）
第49步：P2-001 费用管理
第50步：P2-002 固定资产
第51步：P2-012 期初管理
第52步：P2-003 期末处理
第53步：P2-004 样品管理
第54步：P2-005 借用管理
第55步：P2-006 租赁管理
第56步：P2-007 售后管理
第57步：P2-008 OA办公
第58步：P2-016 首页仪表盘
第59步：P2-011 报表中心
第60步：P2-009 AI助手
第61步：P2-015 多语言
第62步：P2-014 运维管理
第63步：P2-017 移动端适配
```

## 5.7 任务统计总览

| 优先级 | 一级任务数 | 二级任务数 | 三级任务数 | 四级任务数（叶子节点） | 占比 |
|--------|-----------|-----------|-----------|----------------------|------|
| **P0** | 14 | 60 | 313 | 1144 | 43.5% |
| **P1** | 15 | 30 | 237 | 935 | 35.6% |
| **P2** | 17 | 43 | 139 | 546 | 20.8% |
| **合计** | **46** | **133** | **689** | **2625** | **100%** |

> **说明**：叶子节点数量为四级任务统计值。V11.0较V10.1净增27个三级任务（P0-006工作台1个+P0-007录入页13个+P0-008客户附件1个+P0-009录入页8个+P0-012录入页4个）、27个叶子节点，修正4处页面类型编号错误。累计V9.0→V11.0净增71个三级任务、152个叶子节点。在实际细化各子任务详细开发需求时，可能进一步拆分。最终精确数量以细化补充后的文档为准。

---

# 附录A 术语表

| 术语 | 说明 |
|------|------|
| 主从表 | 主表（单头）+ 从表（明细行），如销售订单(sale_order) + 销售订单明细(sale_order_detail) |
| doc_detail三表 | doc_detail_location + doc_detail_batch + doc_detail_serial，出入库单据的库位/批次/序列号公共从表 |
| 编码引擎 | 根据编码规则自动生成单据编号的服务，支持日期变量/自增序列/手工切换 |
| 数据视图引擎 | 根据视图配置动态构建SQL查询的服务，支持动态JOIN/WHERE/分页/排序 |
| 审核引擎 | 通用单据审核/反审/作废/撤销作废状态流转服务 |
| 流转引擎 | 通用单据引入/下推/复制/追溯服务 |
| 页面类型 | P01~P15共15种标准化页面布局基座 |
| 录入组件 | 20种基础输入组件 + 3种字典组件 + 7大体系业务选择器 |
| 业务增强组件 | 7种ERP特色增强组件（BOM配方/费用分摊/地区/话术/预警/批次有效期/库存可用量） |
| Composable | Vue3组合式函数，封装页面状态/表格配置/字段配置/权限/数据视图/编码规则等可复用逻辑 |

---

# 附录B 文档审查修正记录与已知遗留问题

## B.1 本次审查（V3.1定稿审查）已完成修正

| 序号 | 修正项 | 涉及文档 | 说明 |
|------|--------|---------|------|
| 1 | 版本号统一 | 设计文档 | 版本头 V2.0 → V4.3，与文件名一致 |
| 2 | ID生成策略统一 | 设计文档 | 7.1.1自增主键 → 雪花算法（IdType.ASSIGN_ID），与任务文档0.2.6一致 |
| 3 | 单据字段命名标准化 | 设计文档 | order_no → bill_no，order_date → bill_date，status → bill_status + audit_status，与任务文档0.4.2一致 |
| 4 | 商品快照字段对齐 | 任务文档 | 0.4.3扩展为完整字段定义（含model/spec/brand/qty/base_qty/客户料号/供应商料号/aux_attr/attr_json），与设计文档7.1.3一致 |
| 5 | 缺失外键修复 | 设计文档 | sale_quotation_detail.quotation_id、sale_order_detail.order_id 补充 |
| 6 | org_employee表定义 | 设计文档 | 新增11.6.6员工档案章节，含完整表结构定义 |
| 7 | 章节编号冲突修复 | 任务文档 | 重复的1.4号段修正为1.6（四级任务标准拆分规则） |
| 8 | 委外模块任务结构修正 | 任务文档 | P1-009重复的"委外未生产清单"任务去重，嵌套层级修正 |
| 9 | 错位任务移除 | 任务文档 | P1-005-002-020"入库未对账清单"移除（P1-006已有正确任务） |
| 10 | 全局规范文件补全 | 任务目录 | 新增4个缺失的全局规范文件：引擎服务、系统参数、菜单路由、性能安全 |

## B.2 已知遗留问题（V8处理进展）

| 序号 | 问题 | 涉及文档 | 严重程度 | V8处理状态 |
|------|------|---------|---------|-----------|
| 1 | 10个商品模块表字段定义缺失 | 设计文档11.5 | 中 | ✅已验证：设计文档V4.3已包含完整字段定义，任务文档V9已补充DDL+CRUD+前端页面 |
| 2 | srm_supplier_category/srm_supplier_qualification | 设计文档 | 中 | ✅已修复：设计文档V4.3已补充11.4.13和11.4.14完整设计，任务文档V9已补充前后端任务 |
| 3 | fin_account_balance表 | 设计文档11.22.6 | 高 | ✅已验证：设计文档V4.3已包含完整字段定义，含7个字段 |
| 4 | 第12章数据视图汇总不完整 | 设计文档 | 低 | 📋 335+视图以第11章各功能点"数据视图清单"为准，第12章为编码索引 |
| 5 | 第13章数据表汇总不完整 | 设计文档 | 低 | 📋 270+表以第11章各功能点"数据表清单"为准，第13章为编码索引 |
| 6 | Flyway数据库迁移任务未纳入P0 | 任务文档 | ✅已修复 | V3.1→V8：P0-003-022 Flyway数据库迁移管理 |
| 7 | DevOps/部署任务未纳入P0 | 任务文档 | ✅已修复 | V3.1→V8：P0-013 部署与DevOps基础 |
| 8 | 测试基础设施任务未纳入P0 | 任务文档 | ✅已修复 | V3.1→V8：P0-014 测试基础模块 |
| 9 | 登录安全功能位于P2而非P0 | 任务文档 | ✅已修复 | V3.1→V8：迁移至P0-004-011/012 |
| 10 | P2任务粒度偏粗 | 任务文档 | 低 | 📋 P2部分叶子任务待细化至文件级 |
| 11 | 第11/12章视图编码体系不一致 | 设计文档 | 低 | 📋 建议后续统一为语义化编码（DV_模块缩写+功能缩写+序号） |

## B.3 V9.0修正记录（开发前最终全量核查）

| 序号 | 修正项 | 类型 | 说明 |
|------|--------|------|------|
| 1 | 基准版本对齐 | 文档级 | 基准文档引用从V7更新为V4.3，补全V3.5~V4.1新增内容 |
| 2 | 删除P0-004-001-008编号规则 | 结构错误 | 与P0-001-005编码引擎完全重叠且表名(sys_number_rule vs sys_code_rule)不一致，统一使用P0-001-005 |
| 3 | P0-004-003-010/011/012层级提升 | 结构错误 | 从P0-004-003-006下6级嵌套提升为与001~006同级的独立三级任务，重编号为007/008/009 |
| 4 | 补全单据配置(11.35.4) | 严重遗漏 | P2-013新增后端P2-013-001-009(sys_doc_config)+前端P2-013-002-012/013 |
| 5 | 补全在线设备(11.40.3) | 严重遗漏 | P0-003新增auth_online_device DDL+P0-004-005新增后端查询+P0-004-010新增前端列表页 |
| 6 | 补全用户管理工作台(11.38.0) | 严重遗漏 | P0-004-002新增聚合接口+P0-004-008新增P02工作台页 |
| 7 | 补全系统管理工作台(11.35.0) | 严重遗漏 | P2-013新增聚合接口+P02工作台页 |
| 8 | 补全认证配置工作台(11.40.0) | 严重遗漏 | P0-004-005新增聚合接口+P0-004-010新增P02工作台页 |
| 9 | 补全SSO配置表DDL(11.40.4) | 严重遗漏 | P0-004-012新增auth_sso_config建表任务 |
| 10 | 数据字典/参数管理拆分至P0 | 优先级 | 从P2-013拆出数据字典管理和系统参数管理到P0-005后执行，为P1业务模块提供管理界面 |
| 11 | 模板中心排序提前 | 优先级 | P2-010从第58步提前至第48步(费用管理之后) |
| 12 | P2-011财务报表消歧 | 功能重叠 | P2-011-001-007改为聚合查看入口，不重复P1-014取数逻辑；删除科目余额表/辅助核算(已由P1-013覆盖) |
| 13 | P2-011销售报表消歧 | 功能重叠 | P2-011-001-005删除销售毛利分析(已由P1-005覆盖) |

## B.4 V10.0修正记录（开发前最终二轮全量核查）

| 序号 | 修正项 | 类型 | 说明 |
|------|--------|------|------|
| 1 | 富文本组件选型纠偏 | 技术冲突 | 0.5.3.1/0.5.4映射表WangEditor→TinyMCE，与设计文档2.1.1选型TinyMCE Vue3 7.x对齐（P0-005-010-016已正确使用TinyMCE，仅映射表有误） |
| 2 | P1-004-002-011错位任务移除 | 结构错误 | 出库未对账清单误置于P1-004流转引擎前端下，该功能属于P1-005销售管理（P1-005-001-009后端+P1-005-002-015前端已完整覆盖） |
| 3 | P1-002审批流程嵌套层级修复（后端） | 结构错误 | P1-002-001-009预置审批流程、P1-002-001-010审批日志查询从P1-002-001-008审批委托的子级提升为同级独立任务；新增P1-002-001-011审批工作台聚合数据接口 |
| 4 | P1-002审批流程嵌套层级修复（前端） | 结构错误 | P1-002-002-008预置审批流程配置、P1-002-002-009审批日志查询从P1-002-002-007审批委托配置页的子级提升为同级独立任务；新增P1-002-002-010审批工作台页 |
| 5 | 补全P0-010仓库工作台 | 严重遗漏 | 设计文档11.10.1工作台缺失：后端新增P0-010-001-000聚合数据接口+前端新增P0-010-002-000 P02工作台页 |
| 6 | 补全P0-004-009权限配置工作台 | 严重遗漏 | 设计文档11.39.0工作台缺失：前端新增P0-004-009-000 P02工作台页 |
| 7 | 补全P1-002审批工作台 | 严重遗漏 | 设计文档11.27.0工作台缺失：后端P1-002-001-011+前端P1-002-002-010 |
| 8 | 补全P1-003消息工作台 | 严重遗漏 | 设计文档11.29.0工作台缺失：后端P1-003-001-000聚合数据接口（含WebSocket实时推送）+前端P1-003-002-000 P02工作台页 |
| 9 | 补全P1-015成本工作台 | 严重遗漏 | 设计文档11.15.0工作台缺失：后端P1-015-001-000+前端P1-015-002-000 |
| 10 | 补全P1-007四个P06表单页 | 功能遗漏 | 库存管理报损单(P1-007-003-010a)、报溢单(P1-007-003-011a)、组装单(P1-007-003-012a)、拆卸单(P1-007-003-013a)前端P06主从表单页缺失，仅有P03列表页 |
| 11 | 补全P2-005借用申请单 | 严重遗漏 | 设计文档11.24.2借用申请单缺失（与借用出库单11.24.3为不同单据）：后端新增P2-005-001-000 CRUD+前端新增P2-005-002-000a列表页+P2-005-002-000b表单页 |
| 12 | 批量补全P2级14个模块工作台 | 系统性遗漏 | P2-001~P2-012、P2-014、P2-015、P2-017共14个P2模块缺失工作台（设计文档11.x.0要求），逐模块补全后端聚合接口+前端P02工作台页 |
| 13 | 补全P2-014模块授权管理页 | 功能遗漏 | 设计文档11.36.9模块授权缺失：前端新增P2-014-002-000a P13配置页 |
| 14 | 补全P2-017移动基础配置+版本管理 | 功能遗漏 | 设计文档11.32.1移动基础配置+11.32.2b移动版本管理缺失：前端新增P2-017-002-000a配置页+P2-017-002-000b列表页 |

### B.4.1 已知遗留问题（V10.0标记 → V10.1全部已处理）

> ✅ V10.1已处理全部5项遗留问题，详见B.5修正记录。以下为原始标记记录（已解决）。

| 序号 | 问题 | 原严重程度 | V10.1处理状态 |
|------|------|---------|-------------|
| 1 | 0.1.1技术栈约束表仅覆盖设计文档2.1技术栈的~14% | 中 | ✅已补全：0.1.1扩展为7个子表（Web前端/移动端/后端/外部SDK/数据库/AI/DevOps），覆盖全部70+技术选型及精确版本号 |
| 2 | 第零章对设计文档第十章字段配置规则覆盖不足 | 中 | ✅已补全：新增0.11节（3个子节），完整沉淀通用规则11类+表单型4类+表格型7类字段配置约束 |
| 3 | B.3 Fix#10数据字典/系统参数拆分至P0尚未在正文落地 | 中 | ✅已落地：新增P0-005-016(数据字典管理后端+前端)+P0-005-017(系统参数管理后端+前端)；P2-013对应4处任务标记为"已迁移至P0-005"，P2阶段仅保留增强功能 |
| 4 | P0-007含8个超出设计文档11.5基线的扩展功能点 | 低 | ✅已标注：V8补充说明更新为"增强扩展项"，明确19个基线优先+8个扩展增强的开发顺序，商品图片管理页(P0-007-002-032)用户明确要求保留 |
| 5 | P2-011数据大屏仅有P0-005-009-011组件基座 | 低 | ✅已补全：P2-011-002-012新增完整数据大屏任务（后端配置CRUD+数据聚合接口+前端P11大屏页深色主题+v-scale-screen自适应+图表网格+配置器+定时刷新） |

---

## B.5 V10.1修正记录（遗留问题清零）

| 序号 | 修正项 | 类型 | 说明 |
|------|--------|------|------|
| 1 | 0.1.1技术栈约束表全量扩展 | 覆盖率 | 从10行简表扩展为7个子表（0.1.1.1~0.1.1.7），完整覆盖设计文档2.1全部70+技术选型，含精确版本号、用途说明、许可证约束 |
| 2 | 新增0.11节字段配置规则约束 | 规范补全 | 对应设计文档第十章，新增0.11.1通用规则（11类）、0.11.2表单型专属（4类）、0.11.3表格型专属（7类），确保全模块字段实现一致性 |
| 3 | P0-005-016数据字典管理任务新增 | 迁移落地 | 后端P0-005-016-001（sys_dict_type+sys_dict_data主从CRUD+缓存）+前端P0-005-016-002（字典类型列表+字典数据列表+新增/编辑表单共4个页面） |
| 4 | P0-005-017系统参数管理任务新增 | 迁移落地 | 后端P0-005-017-001（sys_param CRUD+分组查询+缓存+类型校验）+前端P0-005-017-002（左右分栏配置页+新增/编辑弹窗共2个页面） |
| 5 | P2-013对应任务迁移标记 | 迁移标记 | P2-013-001-001(数据字典后端)、P2-013-001-005(系统参数后端)、P2-013-002-001(数据字典前端)、P2-013-002-007(系统参数前端)共4处标记为"V10.0已迁移至P0-005"，P2阶段仅保留增强功能 |
| 6 | P0-007扩展功能点显式标注 | 标注对齐 | V8补充说明更新为"增强扩展项"，明确19基线+8扩展的优先级关系，保留商品图片管理页 |
| 7 | P2-011-002-012数据大屏任务补全 | 功能补全 | 后端（report_screen_config DDL+大屏配置CRUD+数据聚合Service+大屏Controller）+前端（P11大屏布局容器+v-scale-screen自适应+ECharts深色图表组件+大屏配置器P15+大屏展示页+定时刷新） |

---

## B.6 V11.0修正记录（开发前最终全量核查 — 三轮核查修正）

> **核查方法**：以设计文档V4.3为基准，对39个一级模块344个功能点逐条比对任务文档V10.1，覆盖展示页类型、录入页类型、页面编号、前后端任务完整性四个维度。

| 序号 | 修正项 | 类型 | 涉及模块 | 说明 |
|------|--------|------|---------|------|
| 1 | P0-006-002-001页面类型编号修正 | 页面类型错误 | P0-006 | 组织中心首页从P01仪表盘修正为P02工作台（设计文档11.6.2明确为P02工作台） |
| 2 | P0-008-002-011页面类型修正 | 页面类型错误 | P0-008 | 客户联系人表单从P07单一表单修正为P06主从表单（设计文档11.3.6含crm_contact_comm联系方式从表） |
| 3 | P0-007-002-020页面类型修正 | 页面类型错误 | P0-007 | 商品BOM成本从P04列表页修正为P10报表页（设计文档11.5.16明确为P10报表页，含条件筛选+穿透钻取） |
| 4 | P0-012-002-007页面类型修正+P06表单补全 | 页面类型错误+遗漏 | P0-012 | 薪资管理展示页从P04修正为P03主从列表页，新增P0-012-002-008 P06主从表单页（含hrm_salary_detail薪资明细从表），与设计文档11.7.6对齐 |
| 5 | P0-006-002-000组织架构工作台前端补全 | 前端页面遗漏 | P0-006 | 设计文档11.6.1定义了"组织架构工作台"(P02)，后端聚合接口P0-006-001-004已有但前端P02页面缺失，新增P0-006-002-000 |
| 6 | P0-008-002-017a客户附件录入页补全 | 前端页面遗漏 | P0-008 | 设计文档11.3.10定义了客户附件"自有录入页"(P07)，仅有P04列表页缺P07表单，新增P0-008-002-017a |
| 7 | P0-007商品管理13个P07录入页补全 | 系统性遗漏 | P0-007 | 设计文档11.5节13个子功能点（标准工序/多单位/控制策略/安全库存/附件/其他信息/标准价/购价核定/销价核定/竞品/工序/工序参数/工序加工价格）缺少独立P07录入页，新增P0-007-002-039~051共13个前端任务 |
| 8 | P0-009 SRM供应商管理8个P07录入页补全 | 系统性遗漏 | P0-009 | 设计文档11.4节8个子功能点（标签定义/基本信息/联系人/地址/标签/附件/评价/财务配置）缺少独立P07录入页，新增P0-009-002-019~026共8个前端任务 |
| 9 | P0-012 HRM 3个P07录入页补全 | 系统性遗漏 | P0-012 | 设计文档11.7节3个子功能点（员工档案/招聘管理/考勤管理）缺少独立P07录入页，新增P0-012-002-009~011共3个前端任务 |
| 10 | 任务统计数据更新 | 统计修正 | 全局 | P0三级任务286→313（+27）、四级任务1117→1144（+27）；全局三级662→689、四级2598→2625 |