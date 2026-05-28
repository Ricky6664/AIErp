# P2-001-001-003-003-002 编写ExpenseBudgetServiceImpl实现类（@Service+主从表事务保存createWithDetail+预算编号自动生成调用SerialNumberService.generate("YSB")+唯一性校验：同一预算年度+期间+部门+费用项目组合不可重复+同期预算汇总金额校验：明细金额合计=主表总金额+预算扣减deductBudget方法：扣减已用金额并更新剩余金额=预算金额-已用金额+预算释放releaseBudget方法：业务单据驳回时恢复预算余额+上期预算复制copyFromLastPeriod方法：复制上期预算结构并更新年度+@Transactional事务管理）

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P2-001-001-003-003-002 |
| 任务名称 | 编写ExpenseBudgetServiceImpl实现类（@Service+主从表事务保存createWithDetail+预算编号自动生成调用SerialNumberService.generate("YSB")+唯一性校验：同一预算年度+期间+部门+费用项目组合不可重复+同期预算汇总金额校验：明细金额合计=主表总金额+预算扣减deductBudget方法：扣减已用金额并更新剩余金额=预算金额-已用金额+预算释放releaseBudget方法：业务单据驳回时恢复预算余额+上期预算复制copyFromLastPeriod方法：复制上期预算结构并更新年度+@Transactional事务管理） |
| 所属模块 | P2-001 |
| 优先级 | P2 |
| 任务类型 | Service服务层 |

## 二、任务目标

实现ExpenseBudgetServiceImpl：extends ServiceImplX；@Service+@Transactional事务；DTO↔Entity转换(MapStruct)；业务校验+编码生成+持久化+操作日志

## 三、前置依赖

### 3.1 前置任务

- P2-001-001-003-003 费用预算Service开发（父任务）
- P2-001-001-003-003-001 编写ExpenseBudgetService接口（前序兄弟任务）

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

> **📦 本任务模块上下文**（来源：P2-001模块开发指南）
> - 本模块涉及数据表：expense_reimbursement(费用报销单), expense_reimbursement_detail(报销明细), expense_application(费用申请单), expense_application_detail(申请明细), expense_budget(费用预算单), expense_budget_detail(预算明细)
> - 本模块涉及API：/api/expense/claim, /api/expense/application, /api/expense/budget, /api/expense/budget/execution
> - 本模块业务规则：费用报销支持从费用申请下推生成；预算校验采用硬约束，超预算禁止提交；报销金额自动汇总明细行金额；审核流转支持多级审批
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 ServiceImpl

```java
@Slf4j
@Service
public class ExpenseBudgetServiceImpl extends ServiceImplX<ExpenseBudgetMapper, ExpenseBudgetEntity> implements ExpenseBudgetService {
    @Autowired private ExpenseBudgetMapper expenseBudgetMapper;
    @Autowired private CodeGeneratorService codeGeneratorService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExpenseBudgetVO create(ExpenseBudgetCreateDTO dto) {
        validateUnique(dto.getCode(), null);
        ExpenseBudgetEntity entity = MapStructConverter.INSTANCE.toEntity(dto);
        entity.setCode(codeGeneratorService.generate("expense.expense_budget"));
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
| 1 | src/main/java/com/erp/expense/service/impl/ExpenseBudgetServiceImpl.java | ExpenseBudgetService实现 |


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
