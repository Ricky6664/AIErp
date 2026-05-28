# P2-002-001-004-003-001 编写AssetDisposalService接口

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-002-001-004-003-001 |
| 任务名称 | 编写AssetDisposalService接口 |
| 所属模块 | P2-002 |
| 优先级 | P2 |
| 任务类型 | Service服务层 |

## 二、任务目标

定义AssetDisposalService接口：extends IServiceX<AssetDisposalEntity>；CRUD方法(create/update/delete/getDetail/page)+业务方法(submit/checkBudget等)

## 三、前置依赖

### 3.1 前置任务

- P2-002-001-004-003 资产清理处置Service开发（父任务）

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

### 5.1 Service接口

```java
public interface AssetDisposalService extends IServiceX<AssetDisposalEntity> {
    AssetDisposalVO create(AssetDisposalCreateDTO dto);
    Boolean update(AssetDisposalUpdateDTO dto);
    Boolean delete(Long id);
    AssetDisposalVO getDetail(Long id);
    Page<AssetDisposalListVO> getPage(AssetDisposalQueryDTO query);
    Boolean submit(Long id);
}
```

### 5.2 规范
- 继承IServiceX
- 业务方法参数DTO，返回VO
- 命名：create/update/delete/getXxx/pageXxx
## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/asset/service/AssetDisposalService.java | AssetDisposalService接口 |


## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | extends IServiceX | IDE检查 |
| 2 | CRUD方法签名完整 | IDE检查 |
| 3 | 业务方法声明完整 | 对照任务目标 |


## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE

> ⚠️ 方法参数DTO返回VO，勿暴露Entity
