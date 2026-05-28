# 全局规范-性能基准与SLA规范

> 本文档定义ERP系统性能基准与SLA指标，涵盖API响应时间目标、前端性能预算、数据库查询限制、分页规则、并发目标、缓存要求及性能测试规范，所有模块开发与上线必须严格遵守。

---

## 1. API响应时间目标

### 1.1 响应时间分级

| 操作类型 | p50目标 | p95目标 | p99目标 | 超时阈值 | 说明 |
|---------|--------|--------|--------|---------|------|
| 单条CRUD | < 100ms | < 200ms | < 500ms | 5s | 单条记录的增删改查 |
| 列表查询（分页） | < 200ms | < 500ms | < 1s | 10s | 含条件筛选+分页 |
| 树形查询 | < 300ms | < 800ms | < 1.5s | 10s | 部门树/分类树/BOM树 |
| 复杂报表 | < 1s | < 3s | < 5s | 30s | 多维度统计报表 |
| 文件上传（100MB） | < 5s | < 10s | < 20s | 120s | 网络传输+存储写入 |
| 批量操作（1000条） | < 10s | < 30s | < 60s | 300s | 批量导入/审核/状态变更 |
| 登录认证 | < 200ms | < 500ms | < 1s | 5s | 含密码校验+Token生成 |
| 权限查询 | < 50ms | < 100ms | < 200ms | 2s | 菜单/按钮/数据权限 |

### 1.2 超时配置

```yaml
# application.yml — 超时配置
spring:
  mvc:
    async:
      request-timeout: 30000   # 异步请求超时30秒

server:
  tomcat:
    connection-timeout: 10000  # 连接超时10秒

# RestTemplate/WebClient超时
http:
  client:
    connect-timeout: 5000      # 连接超时5秒
    read-timeout: 30000        # 读取超时30秒
    write-timeout: 30000       # 写入超时30秒
```

### 1.3 慢请求告警

```java
/**
 * 慢请求拦截器 — 记录超过阈值的请求
 */
@Component
public class SlowRequestInterceptor implements HandlerInterceptor {

    private static final long SLOW_THRESHOLD_MS = 1000; // 1秒
    private static final long CRITICAL_THRESHOLD_MS = 5000; // 5秒

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;
        String uri = request.getRequestURI();

        if (duration >= CRITICAL_THRESHOLD_MS) {
            log.error("【严重慢请求】{} {}ms | {} | params={}",
                uri, duration, request.getMethod(), request.getQueryString());
            alertService.sendSlowRequestAlert(uri, duration, "CRITICAL");
        } else if (duration >= SLOW_THRESHOLD_MS) {
            log.warn("【慢请求】{} {}ms | {} | params={}",
                uri, duration, request.getMethod(), request.getQueryString());
        }

        // 上报指标到Micrometer
        metricsService.recordRequestDuration(uri, request.getMethod(), duration);
    }
}
```

---

## 2. 前端性能预算

### 2.1 核心Web Vitals指标

| 指标 | 目标值 | 度量方式 | 说明 |
|------|--------|---------|------|
| FCP (First Contentful Paint) | < 1.5s | Lighthouse | 首次内容绘制时间 |
| LCP (Largest Contentful Paint) | < 2.5s | Lighthouse | 最大内容绘制时间 |
| TTI (Time to Interactive) | < 3.5s | Lighthouse | 可交互时间 |
| CLS (Cumulative Layout Shift) | < 0.1 | Lighthouse | 累积布局偏移 |
| FID (First Input Delay) | < 100ms | Lighthouse | 首次输入延迟 |
| TBT (Total Blocking Time) | < 200ms | Lighthouse | 总阻塞时间 |

### 2.2 打包体积预算

| 资源类型 | 体积上限（gzipped） | 说明 |
|---------|-------------------|------|
| 单路由chunk | < 300KB | 每个页面级路由的JS chunk |
| 初始加载JS | < 500KB | 首屏所需的vendor + app JS |
| 初始加载CSS | < 100KB | 首屏所需样式 |
| Element Plus按需 | < 200KB | 仅引入使用到的组件 |
| 第三方库总计 | < 1MB | 所有第三方依赖gzipped总计 |
| 单张图片 | < 500KB | 列表缩略图 < 50KB |

### 2.3 Vite构建优化配置

```typescript
// vite.config.ts — 性能优化配置
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],

  build: {
    // 分包策略
    rollupOptions: {
      output: {
        manualChunks: {
          'vendor-vue': ['vue', 'vue-router', 'pinia'],
          'vendor-element': ['element-plus'],
          'vendor-utils': ['axios', 'dayjs', 'lodash-es'],
          'vendor-charts': ['echarts'],
        },
        // 单chunk体积告警阈值
        chunkSizeWarningLimit: 300,
      },
    },
    // 压缩方式
    minify: 'terser',
    terserOptions: {
      compress: {
        drop_console: true,  // 生产环境移除console
        drop_debugger: true,
      },
    },
    // CSS代码分割
    cssCodeSplit: true,
    // sourcemap（生产关闭）
    sourcemap: false,
  },

  // 依赖预构建优化
  optimizeDeps: {
    include: ['vue', 'vue-router', 'pinia', 'axios', 'element-plus'],
  },
})
```

### 2.4 路由懒加载

```typescript
// src/router/index.ts — 所有路由必须懒加载
const router = createRouter({
  routes: [
    {
      path: '/sale',
      component: () => import('@/layouts/MainLayout.vue'),
      children: [
        {
          path: 'order',
          // 路由懒加载 — 按需加载chunk
          component: () => import('@/views/sale/order/index.vue'),
          meta: { title: '销售订单' },
        },
        {
          path: 'order/:id',
          component: () => import('@/views/sale/order/detail.vue'),
          meta: { title: '订单详情' },
        },
      ],
    },
  ],
})
```

### 2.5 性能监控

```typescript
// src/utils/performance.ts — 前端性能指标上报
import { onMounted } from 'vue'

export function usePerformanceMonitor(pageName: string) {
  onMounted(() => {
    // 使用PerformanceObserver监听Web Vitals
    if ('PerformanceObserver' in window) {
      // LCP监听
      const lcpObserver = new PerformanceObserver((entryList) => {
        const entries = entryList.getEntries()
        const lastEntry = entries[entries.length - 1]
        reportMetric(pageName, 'LCP', lastEntry.startTime)
      })
      lcpObserver.observe({ type: 'largest-contentful-paint', buffered: true })

      // CLS监听
      let clsValue = 0
      const clsObserver = new PerformanceObserver((entryList) => {
        for (const entry of entryList.getEntries()) {
          if (!(entry as any).hadRecentInput) {
            clsValue += (entry as any).value
          }
        }
        reportMetric(pageName, 'CLS', clsValue)
      })
      clsObserver.observe({ type: 'layout-shift', buffered: true })
    }
  })
}

function reportMetric(page: string, metric: string, value: number) {
  // 上报到监控平台
  navigator.sendBeacon('/api/system/metrics/frontend', JSON.stringify({
    page, metric, value, timestamp: Date.now(),
  }))
}
```

---

## 3. 数据库查询限制

### 3.1 查询性能红线

| 指标 | 限制 | 说明 |
|------|------|------|
| 单条查询执行时间 | < 100ms | 超过则需优化SQL或添加索引 |
| 单次JOIN表数量 | ≤ 5张 | 超过应拆分为多次查询或引入中间表 |
| 无分页查询最大行数 | 10,000行 | 超过必须使用分页或流式查询 |
| IN子句最大元素数 | 1,000个 | 超过应使用临时表或分批查询 |
| 单事务最大影响行数 | 10,000行 | 超过应分批次提交 |
| 慢查询阈值 | 500ms | PostgreSQL log_min_duration_statement=500 |

### 3.2 连接池配置

```yaml
# application.yml — HikariCP连接池配置
spring:
  datasource:
    hikari:
      minimum-idle: 5                    # 最小空闲连接数
      maximum-pool-size: 20              # 最大连接数（单服务实例）
      idle-timeout: 600000               # 空闲连接超时（10分钟）
      max-lifetime: 1800000              # 连接最大存活时间（30分钟）
      connection-timeout: 30000          # 获取连接超时（30秒）
      validation-timeout: 5000           # 连接校验超时（5秒）
      leak-detection-threshold: 60000    # 连接泄漏检测阈值（60秒）
```

**连接池容量规划：**

| 部署规模 | 服务实例数 | 每实例连接数 | 总连接数 | 说明 |
|---------|-----------|------------|---------|------|
| 小型（单节点） | 1 | 20 | 20 | 总连接数 ≤ 数据库max_connections的50% |
| 中型（3节点） | 3 | 15 | 45 | 预留50%连接给运维/备份 |
| 大型（5+节点） | 5 | 10 | 50 | 考虑使用PgBouncer连接池中间件 |

### 3.3 SQL优化规范

```java
// 错误：SELECT * 查询所有字段
List<SaleOrder> orders = baseMapper.selectList(
    new LambdaQueryWrapper<SaleOrder>().eq(SaleOrder::getStatus, "draft")
);

// 正确：仅查询需要的字段
List<SaleOrderVO> orders = baseMapper.selectList(
    new LambdaQueryWrapper<SaleOrder>()
        .select(SaleOrder::getId, SaleOrder::getOrderNo,
                SaleOrder::getOrderDate, SaleOrder::getStatus,
                SaleOrder::getTotalAmount)
        .eq(SaleOrder::getStatus, "draft")
).stream().map(this::toVO).collect(Collectors.toList());
```

```sql
-- 错误：无索引的模糊查询
SELECT * FROM sale_order WHERE order_no LIKE '%2025%';

-- 正确：前缀匹配（可利用索引）
SELECT * FROM sale_order WHERE order_no LIKE '2025%';

-- 正确：使用全文检索（需要时引入pg_trgm或Elasticsearch）
SELECT * FROM sale_order WHERE order_no ILIKE '%2025%';
-- 需创建GIN索引：CREATE INDEX idx_order_no_trgm ON sale_order USING gin(order_no gin_trgm_ops);
```

### 3.4 N+1查询防护

```java
// 错误：N+1查询（1次主查询 + N次明细查询）
List<SaleOrder> orders = orderMapper.selectList(wrapper);
for (SaleOrder order : orders) {
    List<SaleOrderDetail> details = detailMapper.selectList(
        new LambdaQueryWrapper<SaleOrderDetail>()
            .eq(SaleOrderDetail::getOrderId, order.getId())
    );
    order.setDetails(details);
}

// 正确：批量查询 + 内存关联
List<SaleOrder> orders = orderMapper.selectList(wrapper);
List<Long> orderIds = orders.stream().map(SaleOrder::getId).collect(Collectors.toList());
List<SaleOrderDetail> allDetails = detailMapper.selectList(
    new LambdaQueryWrapper<SaleOrderDetail>()
        .in(SaleOrderDetail::getOrderId, orderIds)
);
Map<Long, List<SaleOrderDetail>> detailMap = allDetails.stream()
    .collect(Collectors.groupingBy(SaleOrderDetail::getOrderId));
for (SaleOrder order : orders) {
    order.setDetails(detailMap.getOrDefault(order.getId(), Collections.emptyList()));
}
```

---

## 4. 分页规则

### 4.1 分页参数规范

| 参数 | 默认值 | 最大值 | 说明 |
|------|--------|--------|------|
| page | 1 | 无上限 | 当前页码（从1开始） |
| pageSize | 20 | 200 | 每页条数，最大200 |
| sortField | created_at | 白名单内 | 排序字段，必须使用白名单 |
| sortOrder | desc | asc/desc | 排序方向 |

### 4.2 分页参数校验

```java
/**
 * 分页参数强制校验
 */
public static void validatePagination(int page, int pageSize) {
    if (page < 1) {
        throw new ParamException(200001, "页码必须大于等于1");
    }
    if (pageSize < 1 || pageSize > 200) {
        throw new ParamException(200001, "每页条数必须在1~200之间");
    }
}
```

### 4.3 大结果集分页策略

| 数据量级 | 分页方式 | 实现方法 | 说明 |
|---------|---------|---------|------|
| ≤ 100,000行 | OFFSET分页 | `LIMIT offset, size` | 传统分页，简单直接 |
| > 100,000行 | 游标分页 | `WHERE id > lastId LIMIT size` | 避免深分页性能问题 |
| 无限滚动 | 游标分页 | 前端滚动加载，传递lastId | 适合移动端/列表流 |
| 数据导出 | 流式查询 | `stream()` + 分批写入 | 不使用分页，逐行处理 |

### 4.4 深分页优化

```java
// 错误：深分页（OFFSET过大导致全表扫描）
// SELECT * FROM sale_order ORDER BY id LIMIT 20 OFFSET 100000;

// 正确：游标分页（利用主键索引）
public PageResult<SaleOrderVO> cursorPage(Long lastId, int pageSize) {
    List<SaleOrder> list = baseMapper.selectList(
        new LambdaQueryWrapper<SaleOrder>()
            .gt(lastId != null, SaleOrder::getId, lastId)
            .orderByAsc(SaleOrder::getId)
            .last("LIMIT " + pageSize)
    );

    Long nextCursor = list.isEmpty() ? null : list.get(list.size() - 1).getId();
    return PageResult.cursor(list.stream().map(this::toVO).collect(Collectors.toList()),
                             nextCursor);
}
```

```java
// 正确：覆盖索引优化深分页
// 先查ID（利用覆盖索引），再根据ID列表查完整数据
public PageResult<SaleOrderVO> optimizedPage(int page, int pageSize) {
    int offset = (page - 1) * pageSize;

    // Step 1: 仅查ID（覆盖索引，极快）
    List<Long> ids = baseMapper.selectObjs(
        new LambdaQueryWrapper<SaleOrder>()
            .select(SaleOrder::getId)
            .orderByDesc(SaleOrder::getCreatedAt)
            .last("LIMIT " + pageSize + " OFFSET " + offset)
    ).stream().map(id -> (Long) id).collect(Collectors.toList());

    if (ids.isEmpty()) {
        return PageResult.empty();
    }

    // Step 2: 根据ID列表精确查询
    List<SaleOrder> records = baseMapper.selectBatchIds(ids);
    return PageResult.of(records.stream().map(this::toVO).collect(Collectors.toList()),
                         page, pageSize, totalCount);
}
```

---

## 5. 并发用户目标

### 5.1 并发容量规划

| 指标 | 目标值 | 说明 |
|------|--------|------|
| 单租户并发用户 | 500 | 同时在线操作用户数 |
| 系统总并发用户 | 5,000 | 所有租户总计 |
| 单接口QPS | 200 | 单实例单接口每秒请求数 |
| 系统总QPS | 2,000 | 集群总计 |
| 事务成功率 | ≥ 99.9% | 非功能性失败 < 0.1% |

### 5.2 容量规划参考

| 部署规模 | 后端实例 | 数据库配置 | Redis配置 | 支撑并发 |
|---------|---------|-----------|----------|---------|
| 小型 | 2 × 4C8G | 8C32G | 4C8G | 1,000并发 |
| 中型 | 4 × 8C16G | 16C64G | 8C16G | 3,000并发 |
| 大型 | 8+ × 8C16G | 32C128G+主从 | 16C32G+集群 | 5,000+并发 |

### 5.3 Tomcat线程池配置

```yaml
# application.yml — Tomcat线程池
server:
  tomcat:
    threads:
      min-spare: 20          # 最小工作线程数
      max: 200               # 最大工作线程数
    max-connections: 8192     # 最大连接数
    accept-count: 100        # 等待队列长度
```

---

## 6. 批量操作限制

### 6.1 批量操作限额

| 操作类型 | 单次上限 | 说明 |
|---------|---------|------|
| 批量导入 | 10,000行/文件 | 超过5000行使用异步导入 |
| 批量导出 | 100,000行 | 超过10000行使用SXSSFWorkbook流式导出 |
| 批量删除 | 1,000条 | 前端限制单次选择数量 |
| 批量审核 | 500条 | 含业务校验，处理较重 |
| 批量状态变更 | 500条 | 含状态机流转 |
| 批量打印 | 200条 | 生成PDF消耗资源 |
| IN查询参数 | 1,000个 | 超过需分批查询 |

### 6.2 批量操作超时控制

```java
/**
 * 批量操作超时控制
 */
@PostMapping("/batch")
@Timeout(value = 300, unit = TimeUnit.SECONDS) // 5分钟超时
public R<BatchResult> batchOperation(@RequestBody BatchOperationDTO dto) {
    // 校验批量操作上限
    if (dto.getIds().size() > 1000) {
        throw new ParamException(200001, "单次批量操作不能超过1000条");
    }
    return R.ok(service.batchProcess(dto));
}
```

---

## 7. 缓存要求

### 7.1 热数据缓存清单

| 数据类型 | 缓存必要性 | TTL | 更新策略 |
|---------|-----------|-----|---------|
| 字典数据 | 必须缓存 | 24h | 修改时主动失效 |
| 系统参数 | 必须缓存 | 12h | 修改时主动失效 |
| 用户权限/菜单 | 必须缓存 | 2h | 权限变更时清除 |
| 商品主数据 | 必须缓存 | 6h | 修改时主动失效 |
| 实时库存 | 建议缓存 | 1h | 变动时更新（以DB为准） |
| 客户/供应商主数据 | 建议缓存 | 4h | 修改时主动失效 |
| 报表统计数据 | 按需缓存 | 5min~1h | 定时刷新 |
| 列表查询结果 | 不缓存 | - | 实时查询（保证一致性） |

### 7.2 缓存命中率要求

| 缓存类型 | 目标命中率 | 说明 |
|---------|-----------|------|
| 字典/系统参数 | ≥ 99% | 极少变更，命中率应极高 |
| 用户权限 | ≥ 95% | 偶尔变更 |
| 商品主数据 | ≥ 90% | 高频读取 |
| 库存数据 | ≥ 80% | 高频读写，缓存作为辅助 |

---

## 8. 监控指标

### 8.1 Spring Boot Actuator配置

```yaml
# application.yml — Actuator监控端点
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
      base-path: /actuator
  endpoint:
    health:
      show-details: when-authorized
  metrics:
    tags:
      application: ${spring.application.name}
    export:
      prometheus:
        enabled: true
```

### 8.2 关键指标清单

| 指标分类 | 指标名 | 告警阈值 | 说明 |
|---------|--------|---------|------|
| **应用层** | http.server.requests (p95) | > 2s | API响应时间 |
| | http.server.requests (error rate) | > 1% | 错误率 |
| | jvm.memory.used | > 80% heap | JVM内存使用率 |
| | jvm.gc.pause | > 200ms | GC暂停时间 |
| | jvm.threads.live | > 300 | 活跃线程数 |
| **数据库层** | hikaricp.connections.active | > 15 (max=20) | 活跃连接数 |
| | hikaricp.connections.pending | > 5 | 等待连接数 |
| | jdbc.query.duration (p95) | > 500ms | 查询耗时 |
| **缓存层** | cache.gets (hit rate) | < 80% | 缓存命中率 |
| | redis.command.duration (p95) | > 50ms | Redis命令耗时 |
| **消息队列** | rabbitmq.queue.depth | > 10000 | 消息积压量 |
| | rabbitmq.consumer.rate | < 0 (停滞) | 消费速率 |

### 8.3 Micrometer自定义指标

```java
@Component
public class BusinessMetrics {

    private final MeterRegistry meterRegistry;

    // 订单创建计数器
    private final Counter orderCreatedCounter;

    // 订单处理耗时
    private final Timer orderProcessTimer;

    public BusinessMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
        this.orderCreatedCounter = Counter.builder("erp.order.created")
            .description("订单创建数量")
            .tag("type", "sale")
            .register(meterRegistry);
        this.orderProcessTimer = Timer.builder("erp.order.process.duration")
            .description("订单处理耗时")
            .tag("type", "sale")
            .register(meterRegistry);
    }

    public void recordOrderCreated() {
        orderCreatedCounter.increment();
    }

    public <T> T timedOrderProcess(Supplier<T> action) {
        return orderProcessTimer.record(action);
    }
}
```

---

## 9. 性能测试规范

### 9.1 JMeter脚本规范

每个核心业务流程必须有对应的JMeter性能测试脚本：

| 测试脚本 | 覆盖接口 | 并发用户数 | 持续时间 |
|---------|---------|-----------|---------|
| login.jmx | 登录接口 | 100 | 5分钟 |
| order_crud.jmx | 订单CRUD全流程 | 200 | 10分钟 |
| query_list.jmx | 列表查询（多种筛选条件） | 300 | 10分钟 |
| import_export.jmx | 导入导出 | 50 | 10分钟 |
| mixed_scenario.jmx | 混合场景（登录+查询+下单） | 500 | 30分钟 |

### 9.2 性能测试场景

| 场景 | 并发用户 | 操作混合比 | 预期结果 |
|------|---------|-----------|---------|
| 基准测试 | 100 | 单接口 | p95 < 目标值 |
| 负载测试 | 500 | 混合操作 | p95 < 目标值 × 2 |
| 压力测试 | 1000 | 混合操作 | 系统不崩溃，错误率 < 5% |
| 稳定性测试 | 300 | 混合操作 | 持续运行4小时无内存泄漏 |
| 峰值测试 | 1500 | 混合操作 | 限流生效，系统可恢复 |

### 9.3 新功能性能测试清单

新功能上线前，必须完成以下性能验证：

| 检查项 | 验证方法 | 通过标准 |
|--------|---------|---------|
| 接口响应时间 | JMeter压测 | p95 < 目标值 |
| SQL执行计划 | EXPLAIN ANALYZE | 无全表扫描，耗时 < 100ms |
| 索引有效性 | EXPLAIN | 命中预期索引 |
| 内存占用 | JVM监控 | 无内存泄漏，GC正常 |
| 连接池使用 | HikariCP指标 | 无连接泄漏，空闲连接正常 |
| 缓存命中率 | Redis指标 | 热数据缓存命中 > 80% |
| 前端加载 | Lighthouse | LCP < 2.5s，TTI < 3.5s |
| 打包体积 | Vite分析 | 单chunk < 300KB gzipped |
| 并发安全 | 多线程压测 | 无数据竞争，结果一致 |

---

## 10. 数据库监控配置

### 10.1 PostgreSQL慢查询日志

```sql
-- postgresql.conf — 慢查询配置
log_min_duration_statement = 500     -- 记录超过500ms的查询
log_statement = 'none'               -- 不记录所有SQL（性能优先）
log_line_prefix = '%t [%p]: [%l-1] ' -- 日志前缀格式
log_lock_waits = on                  -- 记录锁等待
log_temp_files = 0                   -- 记录所有临时文件使用
log_checkpoints = on                 -- 记录检查点
```

### 10.2 索引使用监控

```sql
-- 查找未使用的索引（应定期清理）
SELECT
    schemaname,
    relname AS table_name,
    indexrelname AS index_name,
    idx_scan AS times_used,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_stat_user_indexes
WHERE idx_scan = 0
    AND indexrelname NOT LIKE '%pkey%'
    AND indexrelname NOT LIKE '%unique%'
ORDER BY pg_relation_size(indexrelid) DESC;
```

### 10.3 连接数监控

```sql
-- 查看当前连接使用情况
SELECT
    state,
    count(*) AS connection_count,
    round(count(*) * 100.0 / (SELECT setting::int FROM pg_settings WHERE name = 'max_connections'), 1) AS percentage
FROM pg_stat_activity
GROUP BY state
ORDER BY count(*) DESC;
```

---

## 11. SLA等级定义

### 11.1 服务等级目标

| SLA等级 | 可用性目标 | 月度最大停机时间 | 适用模块 |
|---------|-----------|----------------|---------|
| P0（核心） | 99.95% | 21.6分钟 | 登录认证、订单处理、库存操作 |
| P1（重要） | 99.9% | 43.2分钟 | 报表查询、导入导出、审批流程 |
| P2（一般） | 99.5% | 3.6小时 | 数据同步、通知推送、定时任务 |
| P3（次要） | 99% | 7.2小时 | 日志归档、历史数据清理 |

### 11.2 降级策略

| 故障场景 | 降级措施 | 影响范围 |
|---------|---------|---------|
| Redis不可用 | 绕过缓存，直查DB | 响应变慢，功能不受影响 |
| MinIO不可用 | 文件上传/下载暂停 | 文件相关功能不可用 |
| RabbitMQ不可用 | 消息暂存DB，恢复后重发 | 异步任务延迟 |
| 数据库主节点故障 | 自动切换到从节点（只读） | 写入操作暂停 |
| XXL-JOB不可用 | 定时任务暂停 | 不影响在线业务 |

---

## 12. 检查清单

新功能上线性能评审必须逐项确认：

| 检查项 | 通过标准 |
|--------|---------|
| API响应时间 | p95 < 对应类型的目标值 |
| SQL执行计划 | EXPLAIN ANALYZE无全表扫描 |
| 分页规范 | 默认20条/页，最大200条/页 |
| 深分页处理 | > 10万行使用游标分页或覆盖索引 |
| N+1查询 | 无N+1查询，使用批量查询+内存关联 |
| JOIN表数量 | ≤ 5张 |
| 缓存使用 | 热数据已缓存，TTL合理 |
| 连接池 | HikariCP配置合理，无连接泄漏 |
| 前端FCP | < 1.5s |
| 前端LCP | < 2.5s |
| 路由懒加载 | 所有路由使用动态import |
| 打包体积 | 单chunk < 300KB gzipped |
| 性能测试 | JMeter脚本覆盖核心路径 |
| 监控指标 | Actuator + Prometheus指标已配置 |
| 慢请求告警 | 拦截器已配置，阈值合理 |
| 降级策略 | 中间件故障时有降级方案 |
