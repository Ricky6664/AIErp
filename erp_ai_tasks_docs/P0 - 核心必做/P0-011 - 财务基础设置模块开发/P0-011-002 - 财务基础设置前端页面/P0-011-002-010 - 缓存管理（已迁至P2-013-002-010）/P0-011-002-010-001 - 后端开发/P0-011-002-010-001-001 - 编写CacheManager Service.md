# P0-011-002-010-001-001 编写CacheManager Service

## 一、任务标识

| 属性 | 值 |
|------|-----|
| 任务编号 | P0-011-002-010-001-001 |
| 任务名称 | 编写CacheManager Service |
| 所属模块 | P0-011 |
| 优先级 | P0 |
| 任务类型 | Service服务层 |

## 二、任务目标

编写缓存管理CacheManager Service，封装RedisTemplate操作，支持SCAN遍历Key列表(禁止KEYS)、Key模式匹配查询、TTL查询、Value读取(JSON反序列化)、指定Key/模式清除缓存，并记录操作日志

## 三、前置依赖

### 3.1 前置任务

- P0-011-002-010-001 后端开发（父任务）

### 3.2 前置资源

- 项目代码仓库已就绪
- 开发环境已搭建（JDK17 + Maven + PostgreSQL + Redis）

## 四、关联规范引用
| 规范文档名 | 引用原因 |
|-----------|---------|
| 全局规范-项目架构与开发约束 | 项目架构、技术约束与任务依赖关系 |
| 全局规范-末端任务文档编写规范 | 末端任务文档结构与编写要求 |
| 全局规范-AI开发执行手册 | AI开发执行流程与质量要求 |
| 全局规范-后端代码规范 | 后端代码开发规范约束 |

## 五、详细开发规格
> **📦 本任务模块上下文**（来源：finance模块开发指南）
> - 本模块涉及数据表：fin_currency_rate, fin_bank_account, fin_account, fin_voucher_word, fin_accounting_period
> - 本模块涉及API：/api/finance/currency-rate, /api/finance/bank-account, /api/finance/account, /api/finance/voucher-word, /api/finance/accounting-period, /api/finance/workbench
> - 本模块业务规则：币种编码全局唯一; 汇率日期不得晚于当前日期+30天; 银行账号全局唯一; 会计科目编码按级次规则(4-2-2-2)全局唯一; 末级科目标记新增修改时自动计算; 会计期间不可重叠; 科目余额表凭证审核时自动更新
>
> 💡 开发本任务时，请结合上述模块上下文理解业务场景和数据关系。

### 5.1 Service接口
```java
public interface ICacheManagerService {
    List<CacheKeyVO> scanKeys(String pattern, int count);
    Long getKeyTTL(String key);
    String getKeyValue(String key);
    void deleteByKey(String key);
    void deleteByPattern(String pattern);
    CacheStatsVO getCacheStats();
}
```

### 5.2 核心实现
- RedisTemplate.execute + SCAN命令遍历Key(禁止KEYS)
- Value读取：opsForValue().get() + JSON序列化
- TTL查询：getExpire()
- 删除：delete()

### 5.3 操作日志
- 写操作(删除/清除)记录sys_operate_log(操作人/时间/Key)

### 5.4 安全约束
- 禁止KEYS命令(生产环境性能风险)
- 批量删除最大1000个Key


## 六、交付物清单

| 序号 | 文件路径 | 说明 |
|:---:|---------|------|
| 1 | erp-system-module/src/main/java/com/erp/system/cache/service/ICacheManagerService.java | 缓存管理Service接口 |
| 2 | erp-system-module/src/main/java/com/erp/system/cache/service/impl/CacheManagerServiceImpl.java | Service实现类 |

## 七、验收标准

| 序号 | 检查项 | 验证方法 |
|:---:|--------|--------|
| 1 | SCAN遍历Key列表正确 | 接口测试 |
| 2 | Value读取(JSON反序列化)正确 | 接口测试 |
| 3 | TTL查询返回正确 | 接口测试+Redis CLI |
| 4 | 按Key/模式删除正确 | 接口测试+Redis CLI |
| 5 | 操作日志已记录 | 数据库查询 |
| 6 | 未使用KEYS命令(仅SCAN) | 代码审查 |

## 八、易错警示

> ⚠️ 严禁在生产环境使用KEYS命令，必须使用SCAN遍历

> ⚠️ SCAN的COUNT参数不是精确返回数量，是提示Redis每次迭代返回的近似数量

> ⚠️ 批量删除要限制最大数量(如1000)，避免一次删除过多Key导致Redis阻塞

> ⚠️ Value反序列化要try-catch，部分Key的Value可能不是JSON格式

> ⚠️ 缓存操作日志要记录操作人、操作时间、操作Key，便于审计
