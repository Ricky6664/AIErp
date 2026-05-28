# P2-003-001-003-002-002 编写ExchangeGainLossServiceImpl实现类（@Service+计算方法：①查询所有外币科目期末余额SELECT account_id,currency_id,SUM(debit)-SUM(credit) foreign_balance FROM fin_voucher_detail WHERE period_id<=? AND currency_id IS NOT NULL GROUP BY account_id,currency_id HAVING foreign_balance<>0②遍历外币科目：原币余额=外币余额×原汇率，新币余额=外币余额×期末汇率，汇兑差额=新币余额-原币余额③若外币余额>0（借方余额），汇兑差额>0→借-外币科目/贷-汇兑收益，汇兑差额<0→借-汇兑损失/贷-外币科目；若外币余额<0（贷方余额），方向相反④生成调汇记录+自动生成汇兑损益凭证+previewExchangeGainLoss方法：同计算逻辑但不保存，返回预览结果供确认+reverseExchangeGainLoss反调汇：删除调汇记录+冲销汇兑损益凭证+@Transactional事务管理）

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-003-001-003-002-002 |
| 任务名称 | 编写ExchangeGainLossServiceImpl实现类（@Service+计算方法：①查询所有外币科目期末余额SELECT account_id,currency_id,SUM(debit)-SUM(credit) foreign_balance FROM fin_voucher_detail WHERE period_id<=? AND currency_id IS NOT NULL GROUP BY account_id,currency_id HAVING foreign_balance<>0②遍历外币科目：原币余额=外币余额×原汇率，新币余额=外币余额×期末汇率，汇兑差额=新币余额-原币余额③若外币余额>0（借方余额），汇兑差额>0→借-外币科目/贷-汇兑收益，汇兑差额<0→借-汇兑损失/贷-外币科目；若外币余额<0（贷方余额），方向相反④生成调汇记录+自动生成汇兑损益凭证+previewExchangeGainLoss方法：同计算逻辑但不保存，返回预览结果供确认+reverseExchangeGainLoss反调汇：删除调汇记录+冲销汇兑损益凭证+@Transactional事务管理） |
| 所属模块 | P2-003 |
| 优先级 | P2 |
| 任务类型 | Entity/DTO/VO数据模型 |

## 二、任务目标

实现ExchangeGainLossServiceImpl：extends ServiceImplX；@Service+@Transactional事务；DTO↔Entity转换(MapStruct)；业务校验+编码生成+持久化+操作日志

## 三、前置依赖

### 3.1 前置任务

- P2-003-001-003-002 期末调汇Service开发（父任务）
- P2-003-001-003-002-001 编写ExchangeGainLossService接口（前序兄弟任务）

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
public class ExchangeGainLossServiceImpl extends ServiceImplX<ExchangeGainLossMapper, ExchangeGainLossEntity> implements ExchangeGainLossService {
    @Autowired private ExchangeGainLossMapper exchangeGainLossMapper;
    @Autowired private CodeGeneratorService codeGeneratorService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExchangeGainLossVO create(ExchangeGainLossCreateDTO dto) {
        validateUnique(dto.getCode(), null);
        ExchangeGainLossEntity entity = MapStructConverter.INSTANCE.toEntity(dto);
        entity.setCode(codeGeneratorService.generate("period.exchange_gain_loss"));
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
| 1 | src/main/java/com/erp/period/service/impl/ExchangeGainLossServiceImpl.java | ExchangeGainLossService实现 |


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
