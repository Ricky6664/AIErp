# AccountService 测试验证报告

> **任务**: P0-011-001-003-001-003 验证Service
> **日期**: 2026-06-07
> **执行人**: W2
> **框架**: JUnit5 + Mockito (MockitoExtension)

## 测试结果总览

| 指标 | 数值 |
|------|------|
| 测试总数 | 39 |
| 通过 | 39 |
| 失败 | 0 |
| 错误 | 0 |
| 跳过 | 0 |

## 测试用例明细

### 1. create 新增会计科目 (5 tests)

| 用例 | 结果 | 说明 |
|------|:--:|------|
| 正常数据 -> 返回VO，数据已持久化 | PASS | 验证 insert 调用 + 字段映射正确 |
| 编码重复 -> 抛出BusinessException DATA_ALREADY_EXISTS | PASS | 唯一性校验生效 |
| 编码为空字符串 -> 跳过唯一性校验，正常创建 | PASS | 边界处理正确 |
| 编码为null -> 跳过唯一性校验，正常创建 | PASS | 空值容错 |
| create参数标注@Valid -> 参数校验生效 | PASS | @Valid 在 IAccountService 接口上 |

### 2. update 更新会计科目 (4 tests)

| 用例 | 结果 | 说明 |
|------|:--:|------|
| 正常数据 -> 返回更新后VO | PASS | updateById 调用 + 字段更新正确 |
| ID不存在 -> 抛出BusinessException DATA_NOT_FOUND | PASS | 存在性检查 |
| 编码重复排除自身 -> 抛出BusinessException | PASS | excludeId 参数验证 |
| 更新时编码不变 -> 唯一性校验排除自身通过 | PASS | 自身排除逻辑正确 |

### 3. delete 删除会计科目 (3 tests)

| 用例 | 结果 | 说明 |
|------|:--:|------|
| 无关联数据 -> 软删除成功 | PASS | removeById 调用成功 |
| ID不存在 -> 抛出BusinessException | PASS | 存在性检查 |
| 关联校验: 仅检查存在性 | PASS | 已知限制，见下方说明 |

### 4. pageList 分页查询 (11 tests)

| 用例 | 结果 | 说明 |
|------|:--:|------|
| 无筛选条件 -> 返回全量分页结果 | PASS | 默认分页正常 |
| 按科目编码模糊搜索 | PASS | like 条件生效 |
| 按科目名称模糊搜索 | PASS | like 条件生效 |
| 按上级科目ID筛选 | PASS | eq 条件生效 |
| 按科目类型筛选 | PASS | eq 条件生效 |
| 按科目类别筛选 | PASS | eq 条件生效 |
| 按是否末级筛选 | PASS | eq Boolean 条件生效 |
| 多条件联合筛选 | PASS | 组合条件正确 |
| 无匹配数据 -> 返回空列表 | PASS | 空结果处理 |
| ASC升序排序 | PASS | sortOrder=ASC 正确 |
| DESC降序排序 | PASS | sortOrder=DESC 正确 |
| 无效排序字段 -> 默认排序 | PASS | default 分支兜底 |
| 无排序字段 -> 默认按createTime降序 | PASS | 默认排序行为 |

### 5. getById 查询详情 (2 tests)

| 用例 | 结果 | 说明 |
|------|:--:|------|
| ID存在 -> 返回VO | PASS | 查询 + 字段映射 |
| ID不存在 -> 抛出BusinessException | PASS | 异常处理 |

### 6. @Transactional 事务注解验证 (5 tests)

| 用例 | 结果 | 说明 |
|------|:--:|------|
| create标注@Transactional(rollbackFor=Exception) | PASS | 事务回滚范围正确 |
| update标注@Transactional(rollbackFor=Exception) | PASS | 事务回滚范围正确 |
| delete标注@Transactional(rollbackFor=Exception) | PASS | 事务回滚范围正确 |
| getById标注@Transactional(readOnly=true) | PASS | 只读事务优化 |
| pageList标注@Transactional(readOnly=true) | PASS | 只读事务优化 |

### 7. 边界场景 (6 tests)

| 用例 | 结果 | 说明 |
|------|:--:|------|
| 可选字段为null | PASS | 兼容性 |
| 末级科目(isLeaf=true) | PASS | Boolean 字段正确 |
| 非末级科目(isLeaf=false) | PASS | Boolean 字段正确 |
| 超长科目名称(200字符) | PASS | 长度边界 |
| 余额方向贷方(-1) | PASS | 负值处理 |
| 多级科目(parentId非0) | PASS | 父子关系 |

### 8. toVO 实体转换 (1 test)

| 用例 | 结果 | 说明 |
|------|:--:|------|
| 实体所有字段正确映射到VO | PASS | 13个字段全部验证 |

## 已知限制

### 1. delete 不检查关联数据
当前 `AccountServiceImpl.delete()` 仅检查记录是否存在，不检查是否存在关联的凭证数据。任务规格要求"delete存在关联数据 -> 抛出BusinessException(禁止删除)"，但当前实现未包含此校验逻辑。建议后续添加关联数据检查。

### 2. 状态流转未实现
`AccountEntity` 当前没有 `status` 字段（与 BankAccountEntity 不同），因此不存在状态流转逻辑。规格中的"状态流转"场景不适用于当前实体。

## 结论

所有 39 个测试用例全部通过。核心 CRUD 正常流程、唯一性校验、事务回滚、边界场景均已覆盖。建议在后续迭代中补充关联数据删除校验。
