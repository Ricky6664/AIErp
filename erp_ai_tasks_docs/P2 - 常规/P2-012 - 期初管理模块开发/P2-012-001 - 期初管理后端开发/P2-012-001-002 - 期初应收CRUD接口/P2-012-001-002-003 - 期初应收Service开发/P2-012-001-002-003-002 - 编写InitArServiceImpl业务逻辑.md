# P2-012-001-002-003-002 编写InitArServiceImpl实现类（@Service+期初编号自动生成调用SerialNumberService.generate("QCAR")+唯一性校验：同客户+来源订单号不可重复+approve审核通过后writeToAr方法：①在应收款表ar_receivable中插入应收记录②应收金额=期初金额，已收金额=0，未收金额=应收金额③记录应收款流水④已发生业务往来的客户期初数据不可修改+@Transactional事务管理）

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-012-001-002-003-002 |
| 任务名称 | 编写InitArServiceImpl实现类（@Service+期初编号自动生成调用SerialNumberService.generate("QCAR")+唯一性校验：同客户+来源订单号不可重复+approve审核通过后writeToAr方法：①在应收款表ar_receivable中插入应收记录②应收金额=期初金额，已收金额=0，未收金额=应收金额③记录应收款流水④已发生业务往来的客户期初数据不可修改+@Transactional事务管理） |
| 所属模块 | P2-012 |
| 优先级 | P2 |
| 任务类型 | Service服务层 |

## 二、任务目标

编写InitService接口：声明page(QueryDTO)/getById/create/update/delete等业务方法；编写ServiceImpl实现类：含唯一性校验+状态流转校验+关联数据完整性校验+编码引擎调用+操作日志记录，@Transactional事务注解加在写操作方法

## 三、前置依赖

### 3.1 前置任务

- P2-012-001-002-003 期初应收Service开发（父任务）
- P2-012-001-002-003-001 编写InitArService接口（前序兄弟任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用


| 规范文档名 | 引用原因 |
|-----------|---------|
| 全局规范-项目架构与开发约束 | 项目架构、技术约束与任务依赖关系 |
| 全局规范-末端任务文档编写规范 | 末端任务文档标准化模板与质量要求 |
| 全局规范-AI开发执行手册 | AI任务解读与执行流程规范 |
| 全局规范-后端代码规范 | 后端代码开发规范约束 |
| 全局规范-API接口规范 | API接口设计与RESTful规范约束 |
## 五、详细开发规格

> **📦 本任务模块上下文**（来源：P2-012模块开发指南）
> - 本模块涉及数据表：init_inventory(期初库存表), init_inventory_detail(期初库存明细表), init_ar(期初应收表), init_ap(期初应付表), init_ar_invoice(期初应收发票表)
> - 本模块涉及API：/api/init/inventory/*, /api/init/ar/*, /api/init/ap/*, /api/init/ar-invoice/*, /api/init/ap-invoice/*
> - 本模块业务规则：期初数据审核通过后自动写入对应业务表（库存表/应收表/应付表/资产卡片表/科目余额表）；已启用（已存在业务单据）的仓库/客户/供应商/科目不可修改期初数据；期初科目余额必须通过试算平衡校验（全部借方合计=全部贷方合计）方可提交；期初固定资产支持四种折旧方法公式自动计算月折旧额。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 Service接口方法清单

| 方法签名 | 功能 | 事务 |
|---------|------|------|
| PageResult<InitListVO> page(InitQueryDTO query) | 分页查询 | 无 |
| InitDetailVO getById(Long id) | 详情查询 | 无 |
| Long create(InitCreateDTO dto) | 新增 | @Transactional |
| void update(Long id, InitUpdateDTO dto) | 修改 | @Transactional |
| void delete(Long id) | 软删除 | @Transactional |

### 5.2 业务校验规则

| 校验项 | 校验时机 | 校验逻辑 | 错误提示 |
|--------|---------|---------|---------|
| 唯一性校验 | 新增/修改 | 查询is_deleted=false下同名记录 | "名称已存在" |
| 状态流转校验 | 修改/删除 | 检查当前状态是否允许操作 | "当前状态不允许此操作" |
| 关联数据检查 | 删除 | 查询关联表是否有引用 | "存在关联数据，无法删除" |

### 5.3 集成调用

| 调用目标 | 调用方式 | 调用时机 |
|---------|---------|---------|
| 编码引擎 | codeGenerateService.generate("init_inventory") | 新增时 |
| 操作日志 | @OperLog注解 | 新增/修改/删除 |

### 5.4 验证

1. Service接口方法签名完整
2. ServiceImpl业务逻辑+校验+事务正确
3. 编码引擎/操作日志集成正确

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/init/service/InitService.java | Service接口 |
| 2 | src/main/java/com/erp/init/service/impl/InitServiceImpl.java | Service实现类 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | Service接口方法签名完整（page/getById/create/update/delete） | 代码review |
| 2 | 唯一性校验查询is_deleted=false | 代码review |
| 3 | 写操作方法含@Transactional注解 | 代码review |
| 4 | 编码引擎调用正确（幂等） | 单元测试 |
| 5 | @OperLog操作日志注解存在 | 代码review |

## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id条件

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE而非DELETE

> ⚠️ 事务边界要准确——@Transactional加在Service方法而非Controller

> ⚠️ 避免大事务——查询操作尽量移出事务，仅写操作在事务内

> ⚠️ 异常统一抛出BusinessException——由GlobalExceptionHandler统一处理

> ⚠️ 编码生成调用要幂等——失败重试不重复生成编码

> ⚠️ [期初管理模块] 期初数据审核通过后会自动写入对应业务表，审核操作不可撤销，需在审核前做完整校验

> ⚠️ [期初管理模块] 已启用（存在业务单据）的仓库/客户/供应商/科目禁止修改期初数据，修改前需检查业务单据引用

> ⚠️ [期初管理模块] 期初科目余额必须通过试算平衡校验：全部借方合计=全部贷方合计，否则禁止提交
