# P2-012-001-004-003-001 编写InitArInvoiceService接口

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-012-001-004-003-001 |
| 任务名称 | 编写InitArInvoiceService接口 |
| 所属模块 | P2-012 |
| 优先级 | P2 |
| 任务类型 | Entity/DTO/VO数据模型 |

## 二、任务目标

定义编写InitArInvoiceService接口：Entity/DTO/VO三类模型完整定义，字段与数据库表一一对应，校验注解完备，展示字段含字典翻译

## 三、前置依赖

### 3.1 前置任务

- P2-012-001-004-003 期初应开票Service开发（父任务）

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
| 全局规范-数据库规范 | 数据库字段映射与模型规范约束 |
## 五、详细开发规格

> **📦 本任务模块上下文**（来源：P2-012模块开发指南）
> - 本模块涉及数据表：参见模块开发指南
> - 本模块涉及API：/api/init/inventory, /api/init/ar, /api/init/ap, /api/init/ar-invoice, /api/init/ap-invoice
> - 本模块业务规则：本模块无模块级专属约束，遵循全局规范。期初数据审核通过后自动写入对应业务表（库存表/应收表/应付表/资产卡片表/科目余额表）；已启用（已存在业务单据）的仓库/客户/供应商/科目不可修改期初数据；期初科目余额必须通过试算平衡校验（全部借方合计=全部贷方合计）方可提交；期初固定资产支持四种折旧方法公式自动计算月折旧额。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 ListVO定义

| 字段名 | 类型 | 注解 | 说明 |
|--------|------|------|------|
| id | Long | - | 主键 |
| createdAt | LocalDateTime | @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss") | 创建时间 |
| (字典字段)Name | String | - | 字典翻译字段 |

### 5.2 DetailVO定义

含全部字段 + 关联对象（如明细列表List<DetailVO>）

### 5.3 验证

1. @JsonFormat日期格式正确
2. 字典翻译字段命名xxxName
3. 编译通过无警告

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/init/entity/InitArInvoiceService接口Entity.java | Entity实体类 |
| 2 | src/main/java/com/erp/init/dto/InitArInvoiceService接口CreateDTO.java | DTO类 |
| 3 | src/main/java/com/erp/init/vo/InitArInvoiceService接口ListVO.java | VO类 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | 日期字段@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss") | 代码review |
| 2 | 字典翻译字段命名xxxName | 代码review |
| 3 | ListVO仅含列表必要字段 | 代码review |
| 4 | DetailVO含全部字段+关联对象 | 代码review |
| 5 | 编译通过无警告 | mvn compile |

## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id条件

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE而非DELETE

> ⚠️ @TableField映射勿遗漏——所有业务字段必须显式声明@TableField

> ⚠️ LocalDateTime类型勿用Date——日期时间统一使用LocalDateTime

> ⚠️ Entity必须implements Serializable——Redis缓存需要序列化

> ⚠️ @Version乐观锁字段仅声明不手动赋值——MyBatis-Plus自动处理

> ⚠️ [期初管理模块] 期初数据审核通过后会自动写入对应业务表，审核操作不可撤销，需在审核前做完整校验

> ⚠️ [期初管理模块] 已启用（存在业务单据）的仓库/客户/供应商/科目禁止修改期初数据，修改前需检查业务单据引用

> ⚠️ [期初管理模块] 期初科目余额必须通过试算平衡校验：全部借方合计=全部贷方合计，否则禁止提交
