# P2-013-001-007-001-001 编写CacheManager Service

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-013-001-007-001-001 |
| 任务名称 | 编写CacheManager Service |
| 所属模块 | P2-013 |
| 优先级 | P2 |
| 任务类型 | Service服务层 |

## 二、任务目标

编写SysService接口：声明page(QueryDTO)/getById/create/update/delete等业务方法；编写ServiceImpl实现类：含唯一性校验+状态流转校验+关联数据完整性校验+编码引擎调用+操作日志记录，@Transactional事务注解加在写操作方法

## 三、前置依赖

### 3.1 前置任务

- P2-013-001-007-001 CacheManager Service+Controller开发（父任务）

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

> **📦 本任务模块上下文**（来源：P2-013模块开发指南）
> - 本模块涉及数据表：参见模块开发指南
> - 本模块涉及API：/api/system/dashboard, /api/system/dict, /api/system/code-rule, /api/system/data-view, /api/system/operation-log
> - 本模块业务规则：本模块部分功能已在V10.0版本中迁移至P0阶段：; 数据字典管理：后端CRUD已迁移至P0-005-016-001，前端页面已迁移至P0-005-016-002。P2阶段仅保留数据字典增强功能（字典导入模板管理、字典数据批量导入导出增强、字典引用查询）。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 Service接口方法清单

| 方法签名 | 功能 | 事务 |
|---------|------|------|
| PageResult<SysListVO> page(SysQueryDTO query) | 分页查询 | 无 |
| SysDetailVO getById(Long id) | 详情查询 | 无 |
| Long create(SysCreateDTO dto) | 新增 | @Transactional |
| void update(Long id, SysUpdateDTO dto) | 修改 | @Transactional |
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
| 编码引擎 | codeGenerateService.generate("sys_param") | 新增时 |
| 操作日志 | @OperLog注解 | 新增/修改/删除 |

### 5.4 验证

1. Service接口方法签名完整
2. ServiceImpl业务逻辑+校验+事务正确
3. 编码引擎/操作日志集成正确

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/sys/service/SysService.java | Service接口 |
| 2 | src/main/java/com/erp/sys/service/impl/SysServiceImpl.java | Service实现类 |

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

> ⚠️ [系统管理模块] 系统参数优先级链：租户级参数 > 全局默认参数，查询时先查租户级再回退全局

> ⚠️ [系统管理模块] 参数变更后仅对新操作生效，不影响已有业务单据，需在参数值中记录生效时间

> ⚠️ [系统管理模块] 字典数据缓存至Redis（TTL 24h），字典项增删改时必须清除对应dict_type的缓存
