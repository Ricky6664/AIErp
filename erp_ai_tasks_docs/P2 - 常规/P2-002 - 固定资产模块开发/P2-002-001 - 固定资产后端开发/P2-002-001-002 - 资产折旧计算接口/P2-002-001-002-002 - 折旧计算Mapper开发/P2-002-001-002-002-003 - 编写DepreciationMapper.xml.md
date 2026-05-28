# P2-002-001-002-002-003 编写DepreciationMapper.xml

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-002-001-002-002-003 |
| 任务名称 | 编写DepreciationMapper.xml |
| 所属模块 | P2-002 |
| 优先级 | P2 |
| 任务类型 | Mapper数据访问层 |

## 二、任务目标

编写DepreciationMapper.xml：resultMap定义；自定义SQL(主从JOIN/动态条件<if>/<choose>)；分页使用MyBatis-Plus Page；#{}参数绑定防注入

## 三、前置依赖

### 3.1 前置任务

- P2-002-001-002-002 折旧计算Mapper开发（父任务）
- P2-002-001-002-002-002 编写DepreciationDetailMapper接口（前序兄弟任务）

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
| 全局规范-数据库规范 | 数据库字段映射与模型规范约束 |

## 五、详细开发规格

> **📦 本任务模块上下文**（来源：P2-002模块开发指南）
> - 本模块涉及数据表：参见模块开发指南
> - 本模块涉及API：/api/fa/asset, /api/fa/depreciation, /api/fa/change, /api/fa/disposal
> - 本模块业务规则：本模块无模块级专属约束，遵循全局规范。折旧方法支持：平均年限法、双倍余额递减法、年数总和法、工作量法四种。
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 Mapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.xxx.asset.mapper.DepreciationMapper">
    <resultMap id="BaseResultMap" type="com.xxx.asset.entity.DepreciationEntity">
        <id column="id" property="id"/>
        <result column="code" property="code"/>
    </resultMap>

    <select id="selectPageByCondition" resultMap="BaseResultMap">
        SELECT t.*, d.name AS dept_name
        FROM asset_depreciation t
        LEFT JOIN sys_dept d ON t.dept_id = d.id
        WHERE t.is_deleted = 0 AND t.tenant_id = #{tenantId}
        <if test="query.code != null and query.code != ''">
            AND t.code LIKE CONCAT('%', #{query.code}, '%')
        </if>
        <if test="query.status != null">AND t.status = #{query.status}</if>
        ORDER BY t.create_time DESC
    </select>
</mapper>
```

### 5.2 规范
- namespace与Mapper全类名一致
- #{}参数绑定(禁止${})
- 动态<if>/<choose>
- tenant_id+is_deleted条件必加
## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/resources/mapper/asset/DepreciationMapper.xml | DepreciationMapper XML |


## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | namespace与全类名一致 | 启动测试 |
| 2 | resultMap完整 | 查询测试 |
| 3 | #{}绑定(非${}) | 代码审查 |
| 4 | is_deleted=0条件 | 查询测试 |
| 5 | tenant_id多租户 | 查询测试 |
| 6 | 分页正常 | 分页测试 |


## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE

> ⚠️ namespace与Mapper全类名一致

> ⚠️ #{}绑定，禁止${}

> ⚠️ is_deleted=0必加

> ⚠️ tenant_id多租户必加

> ⚠️ <if test="name != null and name != ''">双判空
