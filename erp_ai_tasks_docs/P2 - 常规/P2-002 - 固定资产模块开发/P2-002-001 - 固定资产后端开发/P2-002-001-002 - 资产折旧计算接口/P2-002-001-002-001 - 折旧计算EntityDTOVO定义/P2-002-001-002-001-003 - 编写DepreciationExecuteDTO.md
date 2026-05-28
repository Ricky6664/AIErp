# P2-002-001-002-001-003 编写DepreciationExecuteDTO

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-002-001-002-001-003 |
| 任务名称 | 编写DepreciationExecuteDTO |
| 所属模块 | P2-002 |
| 优先级 | P2 |
| 任务类型 | Entity/DTO/VO数据模型 |

## 二、任务目标

完成编写DepreciationExecuteDTO的开发工作，确保功能完整、质量达标、符合固定资产模块规范

## 三、前置依赖

### 3.1 前置任务

- P2-002-001-002-001 折旧计算Entity/DTO/VO定义（父任务）
- P2-002-001-002-001-002 编写DepreciationDetailEntity类（前序兄弟任务）

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

## 五、详细开发规格

> **📦 本任务模块上下文**（来源：P2-002模块开发指南）
> - 本模块涉及数据表：参见模块开发指南
> - 本模块涉及API：/api/fa/asset, /api/fa/depreciation, /api/fa/change, /api/fa/disposal
> - 本模块业务规则：本模块无模块级专属约束，遵循全局规范。折旧方法支持：平均年限法、双倍余额递减法、年数总和法、工作量法四种。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 核心功能
1. 按业务场景定义XxxCreateDTO/XxxUpdateDTO/XxxQueryDTO/XxxExecuteDTO
2. 使用@NotNull/@NotBlank/@Size等JSR380注解校验必填项与长度
3. 金额字段使用BigDecimal，日期字段使用LocalDate/LocalDateTime
4. 嵌套明细使用List<XxxDetailDTO>并加@Valid级联校验
5. 使用@Schema注解补充Knife4j字段描述

### 5.2 数据交互
1. Controller接收前端JSON请求，@RequestBody反序列化为DTO
2. @Valid触发JSR380校验，校验失败返回400+字段错误明细
3. DTO通过BeanUtils.copyProperties或MapStruct转换为Entity
4. QueryDTO用于封装查询条件，支持分页参数(pageNum/pageSize)
5. 批量操作DTO内含List<DetailDTO>明细列表

### 5.3 关键逻辑
1. JSR380注解精确校验：@NotNull对象，@NotBlank字符串
2. 自定义校验注解处理复杂规则(如日期范围、金额区间)
3. 分组校验@Validated(Create.class/Update.class)区分新增/修改
4. 嵌套DTO加@Valid级联校验，明细不能为空
5. 日期格式@JsonFormat统一yyyy-MM-dd或yyyy-MM-dd HH:mm:ss

### 5.4 验证
1. DTO类编译通过，字段类型正确
2. JSR380校验注解生效，空值/越界返回明确错误
3. 分组校验Create/Update区分正确
4. 嵌套DTO @Valid级联校验生效
5. @Schema注解在Knife4j文档正确展示
## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/asset/... | 相关源代码 |


## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | 功能完整符合目标 | 对照Section 2 |
| 2 | 编译/运行无错 | mvn compile |
| 3 | 符合规范 | 代码审查 |
| 4 | 单元测试通过 | mvn test |
| 5 | Knife4j完整 | doc.html |


## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE

> ⚠️ @TableField勿遗漏

> ⚠️ 事务边界准确避免大事务

> ⚠️ 异常统一BusinessException
