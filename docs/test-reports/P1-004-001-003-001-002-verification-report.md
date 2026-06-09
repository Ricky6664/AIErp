# P1-004-001-003-001-002 验证功能 — 测试报告

> **任务编号**：P1-004-001-003-001-002
> **任务名称**：验证功能
> **测试日期**：2026-06-09
> **测试人员**：AI Worker W10
> **测试环境**：JDK17 + Maven + JUnit5 + Mockito + MockedStatic(StpUtil)

---

## 一、测试概况

| 指标 | 数值 |
|------|------|
| 测试类 | BizDocCopyServiceVerificationTest |
| 测试方法总数 | 10 |
| 通过 | 10 |
| 失败 | 0 |
| 错误 | 0 |
| 跳过 | 0 |
| 涉及被测类 | BizDocCopyService, BizDocRelationCoreService |

---

## 二、验证清单执行结果

| 序号 | 验证项 | 测试方法 | 结果 |
|:---:|--------|---------|:---:|
| 1 | 正常流程-新增 | shouldCopyDocumentAndReturnRelationId | ✅ |
| 2 | 正常流程-查询 | shouldReturnCopyRelations | ✅ |
| 3 | 正常流程-查询(空) | shouldReturnEmptyWhenNoCopyRelations | ✅ |
| 4 | 正常流程-回滚 | shouldRollbackCopySuccessfully | ✅ |
| 5 | 边界-空值(copy允许qty=0) | shouldAcceptZeroQtyForCopy | ✅ |
| 6 | 边界-非copy类型 | shouldRejectNonCopyRelationType | ✅ |
| 7 | 边界-push类型拒复制 | shouldRejectPushTypeForCopy | ✅ |
| 8 | 边界-回滚非copy类型 | shouldRejectRollbackNonCopyRelation | ✅ |
| 9 | 异常-不存在 | shouldThrowNotFoundOnRollbackNonexistent | ✅ |
| 10 | 带明细ID复制 | shouldCopyWithDetailIds | ✅ |

---

## 三、验证详细记录

### 3.1 正常流程

**新增复制关联**
- 传入完整copy DTO → 返回关联关系ID(400L)，数据库记录字段正确
- 传入qty=0的copy DTO → 接受（copy允许零数量）
- 传入带sourceDetailId/targetDetailId的DTO → 明细字段正确保存

**查询复制关联**
- 按源单据类型和ID查询 → 返回正确copy类型关联列表
- 查询不存在的单据 → 返回空列表

**回滚复制关联**
- 回滚有效copy关联关系 → 删除成功，selectById(2次) + deleteById(1次)验证通过

### 3.2 边界条件

| 场景 | 预期 | 实际 | 状态 |
|------|------|------|:---:|
| relationType=import | PARAM_INVALID, 不执行insert | PARAM_INVALID(30001), insert未调用 | ✅ |
| relationType=push | PARAM_INVALID, 不执行insert | PARAM_INVALID(30001), insert未调用 | ✅ |
| 回滚push类型关联 | DATA_STATUS_INVALID, 不执行delete | DATA_STATUS_INVALID(40002), delete未调用 | ✅ |
| 回滚不存在的ID | DATA_NOT_FOUND, 不执行delete | DATA_NOT_FOUND(50002), delete未调用 | ✅ |

### 3.3 关于乐观锁并发测试

乐观锁并发控制由BaseEntity的`@Version`字段和MyBatis-Plus内置机制实现，需集成测试环境（真实数据库 + 多线程）验证。本单元测试层已验证Service层正确委托核心服务、参数校验和异常处理。

### 3.4 已知限制

`getCopyableTargetTypes`方法使用LambdaQueryWrapper的`.select()`和`.groupBy()`方法引用，依赖MyBatis-Plus TableInfo lambda缓存，无法在纯Mockito单元测试中验证，需数据库集成测试覆盖。该方法的业务逻辑（空列表处理、distinct去重）为简单流操作，风险较低。

---

## 四、测试覆盖率（服务层）

| 被测类 | 方法 | 覆盖 |
|--------|------|:---:|
| BizDocCopyService | copyDocument | ✅ |
| BizDocCopyService | rollbackCopy | ✅ |
| BizDocCopyService | queryCopyRelations | ✅ |
| BizDocCopyService | getCopyableTargetTypes | ⚠️ 集成测试 |
| BizDocCopyService | validateCopyRules(private) | ✅ (间接) |
| BizDocRelationCoreService | createRelation | ✅ (间接) |
| BizDocRelationCoreService | deleteRelation | ✅ (间接) |

---

## 五、总结

- 10/10 测试通过，编译无错误
- 正常流程（新增/查询/回滚）全部覆盖
- 边界条件（非copy类型拒绝、回滚类型校验）全部覆盖
- 异常场景（不存在ID）覆盖
- 1个方法因MyBatis-Plus lambda缓存限制需集成测试覆盖，已记录
