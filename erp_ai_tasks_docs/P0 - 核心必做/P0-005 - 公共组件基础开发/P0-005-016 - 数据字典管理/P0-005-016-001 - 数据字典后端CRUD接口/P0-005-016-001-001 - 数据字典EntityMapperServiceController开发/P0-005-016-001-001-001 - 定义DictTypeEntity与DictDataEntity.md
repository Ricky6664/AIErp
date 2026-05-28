# P0-005-016-001-001-001 定义DictTypeEntity（@TableName"sys_dict_type"+dict_type_code唯一+dict_type_name+status+排序+remark+@TableLogic+@Version）+DictDataEntity

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-005-016-001-001-001 |
| 任务名称 | 定义DictTypeEntity（@TableName"sys_dict_type"+dict_type_code唯一+dict_type_name+status+排序+remark+@TableLogic+@Version）+DictDataEntity |
| 所属模块 | P0-005 |
| 优先级 | P0 |
| 任务类型 | Entity/DTO/VO数据模型 |

## 二、任务目标

定义数据字典管理模块的Entity实体类，映射sys_dict_type/sys_dict_item/sys_param等表结构，包含@TableId(type=ASSIGN_ID)主键策略、@TableField字段映射、@TableLogic逻辑删除、@Version乐观锁、Lombok注解，确保字段与数据库DDL一一对应

## 三、前置依赖

### 3.1 前置任务

- P0-005-016-001-001 数据字典Entity/Mapper/Service/Controller开发（父任务）

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
| 全局规范-数据库规范 | 数据库字段映射与模型规范约束 |

## 五、详细开发规格

### 5.1 Entity类
@TableName("sys_xxx") + @TableId(type=IdType.ASSIGN_ID) + 字段映射(@TableField) + 逻辑删除(@TableLogic) + 乐观锁(@Version) + @Data/@Builder
sys_dict_type关键字段：dict_name(varchar100), dict_code(varchar50), remark(varchar500), enable_flag(boolean)
sys_dict_item关键字段：dict_type_id(bigint), item_label(varchar100), item_value(varchar100), sort_no(int), enable_flag(boolean)
sys_param关键字段：param_name(varchar100), param_key(varchar50), param_value(text), param_type(varchar30), category(varchar30), remark(varchar500), is_system(boolean), is_readonly(boolean)

### 5.2 DTO类
CreateDTO(@NotBlank/@NotNull/@Size校验注解) + UpdateDTO(@NotNull id字段) + QueryDTO(分页参数 + 筛选条件)

### 5.3 VO类
ListVO(列表展示字段) + DetailVO(完整字段) + @JsonFormat日期格式 + 字典翻译字段

## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | erp-system-module/src/main/java/com/erp/system/dict/entity/ | Entity实体类 |
| 2 | erp-system-module/src/main/java/com/erp/system/dict/dto/ | DTO数据传输对象 |
| 3 | erp-system-module/src/main/java/com/erp/system/dict/vo/ | VO视图对象 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | Entity字段与sys_dict_type/sys_dict_item/sys_param表DDL一一对应 | 人工比对DDL与Entity |
| 2 | @TableName/@TableId(type=ASSIGN_ID)/@TableLogic/@Version注解完整 | 代码审查 |
| 3 | DTO校验注解(@NotBlank/@NotNull/@Size)与业务规则匹配 | 单元测试 |
| 4 | VO的@JsonFormat日期格式和字典翻译字段正确 | 接口联调验证 |
| 5 | 编译通过无警告，Serializable接口已实现 | Maven编译 |

## 八、易错警示

> ⚠️ sys_dict_type.dict_code和sys_param.param_key字段需添加UNIQUE约束，防止重复编码

> ⚠️ 字典项(sys_dict_item)的item_value存储字符串类型，数值型也需以字符串形式存储

> ⚠️ @Version乐观锁字段需在ServiceImpl中正确配置OptimisticLockerInnerInterceptor
