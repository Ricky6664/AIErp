# P2-003-001-004-002-002 编写CarryForwardServiceImpl实现类（@Service+executeCarryForward方法按结转类型分别处理：①损益结转=查询所有收入类科目余额(贷方余额)+费用类科目余额(借方余额)→结转至"本年利润"科目②成本结转=生产成本/制造费用结转至"库存商品"③增值税结转=销项税额/进项税额/进项税额转出/已交税金各明细科目余额结转至"未交增值税"④所得税结转=所得税费用结转至"本年利润"⑤利润分配结转=本年利润结转至"利润分配-未分配利润"+每种结转生成对应结转凭证+previewCarryForward预览：同计算逻辑返回试算平衡表+reverseCarryForward反结转：删除结转记录+冲销凭证+@Transactional事务管理）

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-003-001-004-002-002 |
| 任务名称 | 编写CarryForwardServiceImpl实现类（@Service+executeCarryForward方法按结转类型分别处理：①损益结转=查询所有收入类科目余额(贷方余额)+费用类科目余额(借方余额)→结转至"本年利润"科目②成本结转=生产成本/制造费用结转至"库存商品"③增值税结转=销项税额/进项税额/进项税额转出/已交税金各明细科目余额结转至"未交增值税"④所得税结转=所得税费用结转至"本年利润"⑤利润分配结转=本年利润结转至"利润分配-未分配利润"+每种结转生成对应结转凭证+previewCarryForward预览：同计算逻辑返回试算平衡表+reverseCarryForward反结转：删除结转记录+冲销凭证+@Transactional事务管理） |
| 所属模块 | P2-003 |
| 优先级 | P2 |
| 任务类型 | Service服务层 |

## 二、任务目标

实现CarryForwardServiceImpl：extends ServiceImplX；@Service+@Transactional事务；DTO↔Entity转换(MapStruct)；业务校验+编码生成+持久化+操作日志

## 三、前置依赖

### 3.1 前置任务

- P2-003-001-004-002 损益结转Service开发（父任务）
- P2-003-001-004-002-001 编写CarryForwardService接口（前序兄弟任务）

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

> **📦 本任务模块上下文**（来源：P2-003模块开发指南）
> - 本模块涉及数据表：period_close(期末结账), period_close_log(结账日志), voucher(凭证), voucher_detail(凭证明细)
> - 本模块涉及API：/api/period/close, /api/period/voucher
> - 本模块业务规则：期末结账需校验所有单据已审核；凭证生成需遵循借贷平衡；反结账需检查后续期间是否已结账
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 ServiceImpl

```java
@Slf4j
@Service
public class CarryForwardServiceImpl extends ServiceImplX<CarryForwardMapper, CarryForwardEntity> implements CarryForwardService {
    @Autowired private CarryForwardMapper carryForwardMapper;
    @Autowired private CodeGeneratorService codeGeneratorService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CarryForwardVO create(CarryForwardCreateDTO dto) {
        validateUnique(dto.getCode(), null);
        CarryForwardEntity entity = MapStructConverter.INSTANCE.toEntity(dto);
        entity.setCode(codeGeneratorService.generate("period.carry_forward"));
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
| 1 | src/main/java/com/erp/period/service/impl/CarryForwardServiceImpl.java | CarryForwardService实现 |


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
