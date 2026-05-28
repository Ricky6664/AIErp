# P1-009-002-002-001-002 编写Controller+Service

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P1-009-002-002-001-002 |
| 任务名称 | 编写Controller+Service |
| 所属模块 | P1-009 |
| 优先级 | P1 |
| 任务类型 | Service服务层 |

## 二、任务目标

实现委外管理模块Controller层与Service接口定义，包含路由注册、参数绑定、统一响应封装、权限注解配置，确保RESTful契约完整

## 三、前置依赖

### 3.1 前置任务

- P1-009-002-002-001 后端开发（父任务）
- P1-009-002-002-001-001 编写未委外SQL（前序兄弟任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用
| 规范文档名 | 引用原因 |
|-----------|---------|
| 全局规范-项目架构与开发约束 | 项目架构、技术约束与任务依赖关系 |
| 全局规范-前端代码规范 | 前端代码开发规范约束 |
| 全局规范-末端任务文档编写规范 | 末端任务文档格式与内容规范约束 |
| 全局规范-AI开发执行手册 | AI开发执行流程与规范约束 |

## 五、详细开发规格
> **📦 本任务模块上下文**（来源：P1-009模块开发指南）
> - 本模块涉及数据表：sub_notice, sub_notice_detail, sub_notice_change, sub_picking, sub_picking_detail, sub_over_picking, sub_over_picking_detail, sub_return, sub_return_detail, sub_receipt
> - 本模块涉及API：/api/subcontract/unproduced, /api/subcontract/unproduced/export, /api/subcontract/notice, /api/subcontract/notice-change, /api/subcontract/kanban
> - 本模块业务规则：委外与生产结构对称：委外管理的单据结构与生产管理高度对称（通知→领料→入库→退库→工序移交接收），但委外管理关联供应商维度，生产管理关联生产部门维度。; 加工费管理：委外通知单明细包含加工单价和加工费字段，入库时自动汇总加工费，为后续应付管理和成本核算提供数据依据。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 核心功能
1. 定义委外管理模块Controller类，使用@RestController + @RequestMapping("/api/subcontract")
2. 实现CRUD方法：GET(分页/详情)、POST(新增)、PUT(修改)、DELETE(删除)
3. 统一响应：返回Result<T>，包含code/message/data
4. 权限注解：@RequiresPermission配置菜单权限

### 5.2 数据交互
1. 入参使用@RequestBody接收DTO，使用@Valid触发JSR303校验
2. 出参使用VO对象，通过BeanUtils或MapStruct转换
3. 分页参数：pageNum(默认1)、pageSize(默认20)、排序字段

### 5.3 关键逻辑
1. Controller层只做参数校验与响应封装，不包含业务逻辑
2. Service层方法命名遵循规范：list/get/create/update/delete
3. 异常由全局ExceptionHandler捕获，返回统一错误格式

### 5.4 验证
1. Swagger UI能正常展示所有接口文档
2. 参数校验失败时返回明确错误信息
3. 接口响应格式与契约一致

## 六、交付物清单
| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/views/subcontract/ | 前端页面目录 |
| 2 | src/api/subcontract/ | 前端API目录 |

## 七、验收标准
| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | 页面渲染正确 | 人工检查 |
| 2 | 交互逻辑正常 | 手动验证 |
| 3 | 无Hydration错误 | 控制台检查 |

## 八、易错警示
> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 注意多租户隔离（tenant_id）贯穿SQL/业务逻辑/缓存

> ⚠️ 确保逻辑删除字段（is_deleted）正确处理，避免数据'复活'
