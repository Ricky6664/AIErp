# 全局规范-API接口规范

> 本文档定义全系统API接口设计规范，所有前后端接口开发必须严格遵守。

---

## 1. RESTful URL规则

| 操作类型 | URL格式 | HTTP方法 | 说明 |
|---------|--------|---------|------|
| 列表查询 | /api/{module}/{resource} | GET | 分页查询 |
| 详情查询 | /api/{module}/{resource}/{id} | GET | 按ID查询 |
| 新增 | /api/{module}/{resource} | POST | 新增记录 |
| 修改 | /api/{module}/{resource}/{id} | PUT | 按ID修改 |
| 删除 | /api/{module}/{resource}/{id} | DELETE | 软删除 |
| 审核/反审 | /api/{module}/{resource}/{id}/audit | POST | 状态流转 |
| 引入 | /api/{module}/{resource}/import | POST | 源单引入 |
| 下推 | /api/{module}/{resource}/push | POST | 下推生成 |
| 批量操作 | /api/{module}/{resource}/batch | POST | 批量处理 |

**URL规则要点：**

- URL全部小写，多词使用连字符（kebab-case），如`/api/sale/order-detail`
- URL以`/api/`为统一前缀
- `{module}`为业务模块标识（如sale/purchase/inventory），`{resource}`为资源标识（如order/warehouse）
- RESTful风格：GET读、POST写、PUT改、DELETE删
- 特殊操作（审核/下推/批量）使用POST方法，URL末尾追加操作名

---

## 2. HTTP方法约定与特殊操作

### 标准CRUD

| 操作 | HTTP方法 | URL | 请求体 | 响应 |
|------|---------|-----|--------|------|
| 分页列表 | GET | /api/{module}/{resource}?page=1&pageSize=20 | 无 | R<PageResult<T>> |
| 详情 | GET | /api/{module}/{resource}/{id} | 无 | R<T> |
| 新增 | POST | /api/{module}/{resource} | DTO JSON | R<Long>（返回新ID） |
| 修改 | PUT | /api/{module}/{resource}/{id} | DTO JSON | R<Void> |
| 删除 | DELETE | /api/{module}/{resource}/{id} | 无 | R<Void> |

### 特殊操作

| 操作 | HTTP方法 | URL | 请求体 | 说明 |
|------|---------|-----|--------|------|
| 审核 | POST | /api/{module}/{resource}/{id}/audit | AuditDTO | 审核/反审状态流转 |
| 下推 | POST | /api/{module}/{resource}/push | PushDTO | 源单下推生成下游单据 |
| 引入 | POST | /api/{module}/{resource}/import | ImportDTO | 从源单引入数据 |
| 批量删除 | POST | /api/{module}/{resource}/batch | {ids: [...]} | 批量软删除 |
| 批量审核 | POST | /api/{module}/{resource}/batch/audit | {ids: [...], approved: true} | 批量审核/反审 |

---

## 3. R<T> — 通用响应类（非分页场景）

全系统所有API接口统一使用以下响应结构，前端拦截器和状态管理均按此结构解析。

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<T> implements Serializable {
    /** 状态码：200=成功，400=参数错误，401=未认证，403=无权限，500=服务端错误 */
    private int code;
    /** 响应消息：成功时="success"，失败时=具体错误描述 */
    private String message;
    /** 响应数据：泛型，可为null */
    private T data;
    /** 响应时间戳（毫秒） */
    private long timestamp;

    // 静态工厂方法
    public static <T> R<T> ok(T data) { return new R<>(200, "success", data, System.currentTimeMillis()); }
    public static <T> R<T> ok() { return ok(null); }
    public static <T> R<T> fail(int code, String message) { return new R<>(code, message, null, System.currentTimeMillis()); }
    public static <T> R<T> error(String message) { return fail(500, message); }
    public static <T> R<T> paramError(String message) { return fail(400, message); }
    public static <T> R<T> unauthorized() { return fail(401, "未登录或登录已过期"); }
    public static <T> R<T> forbidden() { return fail(403, "无操作权限"); }
}
```

---

## 4. PageResult<T> — 分页响应类

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> implements Serializable {
    /** 总记录数 */
    private long total;
    /** 当前页数据列表 */
    private List<T> records;
    /** 当前页码 */
    private long pageNum;
    /** 每页大小 */
    private long pageSize;
    /** 总页数 */
    private long totalPages;

    public static <T> PageResult<T> of(long total, List<T> records, long pageNum, long pageSize) {
        long totalPages = pageSize > 0 ? (total + pageSize - 1) / pageSize : 0;
        return new PageResult<>(total, records, pageNum, pageSize, totalPages);
    }
}
```

---

## 5. 分页接口响应组合

分页查询接口使用 `R<PageResult<T>>` 组合，示例：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "total": 1500,
    "records": [...],
    "pageNum": 1,
    "pageSize": 20,
    "totalPages": 75
  },
  "timestamp": 1716500000000
}
```

---

## 6. 错误码体系

### 标准HTTP错误码

| 错误码 | 含义 | 触发场景 | R<T>工厂方法 |
|--------|------|---------|-------------|
| 200 | 成功 | 请求处理成功 | `R.ok(data)` |
| 400 | 参数错误 | 请求参数校验失败、业务规则校验失败 | `R.paramError(message)` |
| 401 | 未认证 | 未登录或登录已过期 | `R.unauthorized()` |
| 403 | 无权限 | 当前用户无操作权限 | `R.forbidden()` |
| 500 | 服务端错误 | 未预期的系统异常 | `R.error(message)` |

### 业务错误码命名规则

业务错误码使用**6位数字**，格式为`{模块编号}{序号}`，范围为`100001~999999`：

| 模块 | 错误码范围 | 示例 |
|------|-----------|------|
| 通用/系统 | 100001~100999 | 100001=参数校验失败 |
| CRM模块 | 101001~101999 | 101001=客户编码已存在 |
| SRM模块 | 102001~102999 | 102001=供应商编码已存在 |
| 商品模块 | 103001~103999 | 103001=商品编码已存在 |
| 销售模块 | 108001~108999 | 108001=订单已审核不可修改 |
| 采购模块 | 109001~109999 | 109001=采购单已关闭 |
| 库存模块 | 110001~110999 | 110001=库存不足 |
| 财务模块 | 113001~113999 | 113001=会计期间已结账 |

**使用方式：**

```java
// 抛出业务异常，全局异常处理器自动转换为R<T>响应
throw new BusinessException(108001, "订单已审核，不可修改");
// 全局异常处理器返回：{"code": 108001, "message": "订单已审核，不可修改", "data": null, "timestamp": ...}
```

### 前端错误处理约定

| code范围 | 前端处理方式 |
|---------|------------|
| 200 | 正常处理data |
| 400 | ElMessage.error提示错误信息 |
| 401 | 跳转登录页，清除本地Token |
| 403 | ElMessage.warning提示无权限 |
| 500 | ElMessage.error提示系统错误 |
| 100001~999999 | ElMessage.error提示业务错误信息 |
