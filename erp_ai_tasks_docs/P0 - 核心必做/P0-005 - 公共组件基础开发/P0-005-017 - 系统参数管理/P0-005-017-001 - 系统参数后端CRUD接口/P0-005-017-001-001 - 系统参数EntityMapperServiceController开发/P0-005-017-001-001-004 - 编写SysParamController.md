# P0-005-017-001-001-004 编写SysParamController

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-005-017-001-001-004 |
| 任务名称 | 编写SysParamController |
| 所属模块 | P0-005 |
| 优先级 | P0 |
| 任务类型 | Controller接口层 |

## 二、任务目标

开发系统参数管理模块的Controller类，使用@RestController+@RequestMapping注册RESTful接口，注入Service层，配置权限注解，统一R<T>/PageResult<T>响应包装，编写Swagger文档注解

## 三、前置依赖

### 3.1 前置任务

- P0-005-017-001-001 系统参数Entity/Mapper/Service/Controller开发（父任务）
- P0-005-017-001-001-003 编写SysParamService（CRUD+param_code唯一性校验+缓存@Cacheable("sys_param")+缓存刷新@CacheEvict+getParamValue按编码获取参数值+getParamGroup获取分组参数列表+参数值类型校验string/number/boolean/json）（前序兄弟任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用


| 规范文档名 | 引用原因 |
|-----------|---------|
| 全局规范-项目架构与开发约束 | 项目架构、技术约束与任务依赖关系 |
| 全局规范-后端代码规范 | 后端代码开发规范约束 |
| 全局规范-API接口规范 | API接口设计与RESTful规范约束 |
| 全局规范-末端任务文档编写规范 | 末端任务文档结构与内容规范要求 |
| 全局规范-AI开发执行手册 | AI辅助开发流程与执行规范要求 |
## 五、详细开发规格


### 5.1 接口清单
GET(列表/详情) + POST(新增) + PUT(修改) + DELETE(软删除) + POST(批量操作)

### 5.2 权限配置
@SaCheckPermission("模块:资源:操作") 权限标识注解

### 5.3 统一响应
R<T> + PageResult<T>包装，全局异常处理器(GlobalExceptionHandler)兜底

### 5.4 接口文档
@Operation(summary="") + @Schema(description="") + 示例值

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | erp-system-module/src/main/java/com/erp/system/param/controller/ | Controller类 |
| 2 | erp-system-module/src/main/java/com/erp/system/param/controller/ | Swagger接口文档注解 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | RESTful接口URL符合/api/{module}/{resource}规范 | 接口测试 |
| 2 | 统一R<T>/PageResult<T>响应包装正确 | 接口测试 |
| 3 | @Valid参数校验生效，BindingResult处理正确 | 接口测试 |
| 4 | 权限注解配置正确，越权访问被拦截 | 权限测试 |
| 5 | Swagger @Operation/@Schema文档注解完整 | Swagger UI验证 |

## 八、易错警示

> ⚠️ 接口路径必须符合/api/{module}/{resource}规范，资源名使用复数形式

> ⚠️ 分页参数默认值要合理（pageNum默认1，pageSize默认20，最大100）

> ⚠️ 导出接口要限制最大导出条数，防止OOM（建议单次不超过10000条）
