# P2-002-001-003-003-002 编写AssetChangeServiceImpl实现类（@Service+变动编号自动生成调用SerialNumberService.generate("ZCB")+create创建：记录变动前值beforeValue=资产卡片当前值+审核通过approve后执行applyChange方法：①原值增加/减少→更新资产卡片原值+重算月折旧额+重算净值②使用年限变更→重算月折旧额③残值率变更→重算残值+重算月折旧额④部门转移→更新使用部门⑤折旧方法变更→按新方法重算月折旧额+generateVoucher凭证生成：原值变动→借-固定资产/贷-营业外收入等+折旧方法变更→仅更新卡片参数不生成凭证+@Transactional事务管理）

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-002-001-003-003-002 |
| 任务名称 | 编写AssetChangeServiceImpl实现类（@Service+变动编号自动生成调用SerialNumberService.generate("ZCB")+create创建：记录变动前值beforeValue=资产卡片当前值+审核通过approve后执行applyChange方法：①原值增加/减少→更新资产卡片原值+重算月折旧额+重算净值②使用年限变更→重算月折旧额③残值率变更→重算残值+重算月折旧额④部门转移→更新使用部门⑤折旧方法变更→按新方法重算月折旧额+generateVoucher凭证生成：原值变动→借-固定资产/贷-营业外收入等+折旧方法变更→仅更新卡片参数不生成凭证+@Transactional事务管理） |
| 所属模块 | P2-002 |
| 优先级 | P2 |
| 任务类型 | Entity/DTO/VO数据模型 |

## 二、任务目标

实现AssetChangeServiceImpl：extends ServiceImplX；@Service+@Transactional事务；DTO↔Entity转换(MapStruct)；业务校验+编码生成+持久化+操作日志

## 三、前置依赖

### 3.1 前置任务

- P2-002-001-003-003 资产变动Service开发（父任务）
- P2-002-001-003-003-001 编写AssetChangeService接口（前序兄弟任务）

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
> - 本模块涉及数据表：asset_card(固定资产卡片), depreciation(折旧计算单), depreciation_detail(折旧明细), asset_change(资产变动单), asset_disposal(资产处置单)
> - 本模块涉及API：/api/asset/card, /api/asset/depreciation, /api/asset/change, /api/asset/disposal
> - 本模块业务规则：折旧计算支持直线线法/双倍余额递减法/年数总和法；资产变动需重新计算折旧；处置资产需先完成当月折旧
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 ServiceImpl

```java
@Slf4j
@Service
public class AssetChangeServiceImpl extends ServiceImplX<AssetChangeMapper, AssetChangeEntity> implements AssetChangeService {
    @Autowired private AssetChangeMapper assetChangeMapper;
    @Autowired private CodeGeneratorService codeGeneratorService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssetChangeVO create(AssetChangeCreateDTO dto) {
        validateUnique(dto.getCode(), null);
        AssetChangeEntity entity = MapStructConverter.INSTANCE.toEntity(dto);
        entity.setCode(codeGeneratorService.generate("asset.asset_change"));
        entity.setStatus(0);
        save(entity);
        saveDetails(entity.getId(), dto.getDetails());
        return getDetail(entity.getId());
    }
}
```

### 5.2 关键
- @Transactional(rollbackFor=Exception.class)
- DTO→Entity用MapStruct
- 编号CodeGeneratorService生成
- 异常BusinessException(ErrorCode)
- AOP操作日志
## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | src/main/java/com/erp/asset/service/impl/AssetChangeServiceImpl.java | AssetChangeService实现 |


## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|---------|
| 1 | @Service注解 | 启动测试 |
| 2 | @Transactional(rollbackFor=Exception.class) | 回滚测试 |
| 3 | MapStruct转换 | 代码审查 |
| 4 | BusinessException异常 | 异常测试 |
| 5 | 编号CodeGeneratorService | 创建测试 |
| 6 | 公共字段自动填充 | 查数据库 |


## 八、易错警示

> ⚠️ 代码提交前确保无敏感信息硬编码（密码/密钥/token）

> ⚠️ 多租户隔离(tenant_id)必须正确——所有SQL查询需自动注入tenant_id

> ⚠️ 逻辑删除字段(is_deleted)正确处理——查询追加is_deleted=false，删除使用UPDATE

> ⚠️ 事务边界准确避免大事务

> ⚠️ 异常统一BusinessException

> ⚠️ 编码生成调用幂等

> ⚠️ DTO→Entity用MapStruct避免BeanUtils性能问题

> ⚠️ 批量用saveBatch/updateBatchById
