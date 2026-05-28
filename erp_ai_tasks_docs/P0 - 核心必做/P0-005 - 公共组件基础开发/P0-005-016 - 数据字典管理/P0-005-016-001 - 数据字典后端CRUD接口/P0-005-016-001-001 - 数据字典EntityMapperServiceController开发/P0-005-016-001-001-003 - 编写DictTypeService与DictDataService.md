# P0-005-016-001-001-003 编写DictTypeService+DictDataService（主从CRUD+dict_type_code唯一性校验+缓存@Cacheable("sys_dict")+缓存刷新@CacheEvict+批量启用/禁用）

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-005-016-001-001-003 |
| 任务名称 | 编写DictTypeService+DictDataService（主从CRUD+dict_type_code唯一性校验+缓存@Cacheable("sys_dict")+缓存刷新@CacheEvict+批量启用/禁用） |
| 所属模块 | P0-005 |
| 优先级 | P0 |
| 任务类型 | Service服务层 |

## 二、任务目标

完成编写DictTypeService+DictDataService（主从CRUD+dict_type_code唯一性校验+缓存@Cacheable("sys_dict")+缓存刷新@CacheEvict+批量启用/禁用）的开发工作，确保功能完整、质量达标

## 三、前置依赖

### 3.1 前置任务

- P0-005-016-001-001 数据字典Entity/Mapper/Service/Controller开发（父任务）
- P0-005-016-001-001-002 编写DictTypeMapper+DictDataMapper（前序兄弟任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用

| 规范文档名 | 引用原因 |
|-----------|---------|
| 全局规范-项目架构与开发约束 | 项目架构、技术约束与任务依赖关系 |
| 全局规范-后端代码规范 | 后端代码开发规范约束 |
| 全局规范-末端任务文档编写规范 | 末端任务文档结构与内容规范要求 |
| 全局规范-AI开发执行手册 | AI辅助开发流程与执行规范要求 |

## 五、详细开发规格

### 5.1 Service接口
继承IService<Entity>，声明CRUD方法 + 业务方法（如refreshCache, getByCode等）

### 5.2 ServiceImpl实现
继承ServiceImpl<Mapper, Entity>，实现核心业务逻辑：
- 数据校验（唯一性dict_code/param_key、状态、关联性）
- 事务管理（@Transactional(rollbackFor=Exception.class)）
- Redis缓存同步（增删改后刷新dict:{dict_code}/param:{param_key}缓存）
- 操作日志记录

### 5.3 业务校验
唯一性校验 + 状态流转校验 + 关联数据完整性校验

### 5.4 集成点
缓存管理(Redis) + 编码生成 + 操作日志记录

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | erp-system-module/src/main/java/com/erp/system/dict/service/ | Service接口定义 |
| 2 | erp-system-module/src/main/java/com/erp/system/dict/service/impl/ | ServiceImpl实现类 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | Service接口方法签名完整，CRUD+业务方法齐全 | 代码审查 |
| 2 | ServiceImpl业务逻辑正确，缓存同步逻辑已实现 | 单元测试 |
| 3 | 唯一性校验+状态校验+关联数据校验完备 | 集成测试 |
| 4 | @Transactional事务注解位置正确，无大事务问题 | 代码审查 |
| 5 | 异常统一抛出BusinessException，错误码规范 | 接口测试 |

## 八、易错警示

> ⚠️ @Transactional要加在ServiceImpl方法上而非接口上，避免大事务（只包含写操作）

> ⚠️ 字典/参数修改后必须同步刷新Redis缓存（删除旧Key或发布消息通知其他节点刷新）

> ⚠️ 批量操作使用saveBatch/updateBatchById而非循环单条操作，避免N+1性能问题
