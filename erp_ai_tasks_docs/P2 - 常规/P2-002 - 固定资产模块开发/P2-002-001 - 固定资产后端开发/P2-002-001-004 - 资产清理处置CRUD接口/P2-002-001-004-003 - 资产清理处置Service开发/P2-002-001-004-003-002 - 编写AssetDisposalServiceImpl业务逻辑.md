# P2-002-001-004-003-002 编写AssetDisposalServiceImpl实现类（@Service+处置编号自动生成调用SerialNumberService.generate("ZCCZ")+create创建：选择资产后自动获取卡片原值/累计折旧/净值+损益计算：处置损益=处置收入-处置费用-资产净值+approve审核通过后executeDisposal方法：①更新资产卡片状态为已处置/已报废②清理累计折旧③生成处置凭证：出售→借-银行存款(处置收入)/借-累计折旧/贷-固定资产(原值)/借或贷-资产处置损益，报废→借-累计折旧/借-营业外支出/贷-固定资产，捐赠→借-累计折旧/借-营业外支出/贷-固定资产，盘亏→借-累计折旧/借-待处理财产损溢/贷-固定资产+@Transactional事务管理）

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-002-001-004-003-002 |
| 任务名称 | 编写AssetDisposalServiceImpl实现类（@Service+处置编号自动生成调用SerialNumberService.generate("ZCCZ")+create创建：选择资产后自动获取卡片原值/累计折旧/净值+损益计算：处置损益=处置收入-处置费用-资产净值+approve审核通过后executeDisposal方法：①更新资产卡片状态为已处置/已报废②清理累计折旧③生成处置凭证：出售→借-银行存款(处置收入)/借-累计折旧/贷-固定资产(原值)/借或贷-资产处置损益，报废→借-累计折旧/借-营业外支出/贷-固定资产，捐赠→借-累计折旧/借-营业外支出/贷-固定资产，盘亏→借-累计折旧/借-待处理财产损溢/贷-固定资产+@Transactional事务管理） |
| 所属模块 | P2-002 |
| 优先级 | P2 |
| 任务类型 | Service服务层 |

## 二、任务目标

实现AssetDisposalServiceImpl：extends ServiceImplX；@Service+@Transactional事务；DTO↔Entity转换(MapStruct)；业务校验+编码生成+持久化+操作日志

## 三、前置依赖

### 3.1 前置任务

- P2-002-001-004-003 资产清理处置Service开发（父任务）
- P2-002-001-004-003-001 编写AssetDisposalService接口（前序兄弟任务）

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
public class AssetDisposalServiceImpl extends ServiceImplX<AssetDisposalMapper, AssetDisposalEntity> implements AssetDisposalService {
    @Autowired private AssetDisposalMapper assetDisposalMapper;
    @Autowired private CodeGeneratorService codeGeneratorService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AssetDisposalVO create(AssetDisposalCreateDTO dto) {
        validateUnique(dto.getCode(), null);
        AssetDisposalEntity entity = MapStructConverter.INSTANCE.toEntity(dto);
        entity.setCode(codeGeneratorService.generate("asset.asset_disposal"));
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
| 1 | src/main/java/com/erp/asset/service/impl/AssetDisposalServiceImpl.java | AssetDisposalService实现 |


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
