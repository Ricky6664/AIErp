package com.erp.common.result;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.IErrorCode;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应包装类(RT).
 *
 * <p>全系统所有API接口的统一返回结构. 前端拦截器和状态管理均按此结构解析.</p>
 *
 * <p>字段说明:
 * <ul>
 *   <li>{@code code} — 状态码, 0=成功, 其他=失败(业务错误码由 ErrorCode 枚举定义)</li>
 *   <li>{@code message} — 响应消息, 成功时为 "success", 失败时为具体错误描述</li>
 *   <li>{@code data} — 响应数据(泛型), 失败时为 null</li>
 *   <li>{@code timestamp} — 响应时间戳(毫秒)</li>
 * </ul>
 * </p>
 *
 * <p>使用示例:
 * <pre>{@code
 * return RT.ok(data);
 * return RT.ok();
 * return RT.fail(ErrorCode.BUSINESS_ERROR);
 * return RT.fail(40001, "订单已审核, 不可修改");
 * }</pre>
 * </p>
 *
 * @param <T> 响应数据类型
 * @author AI
 * @since 2026-05-29
 */
@Schema(description = "统一响应包装对象")
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RT<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 状态码: 200=成功, 其他=失败(业务错误码由 ErrorCode 枚举定义) */
    @Schema(description = "状态码: 0=成功, 其他=失败", example = "0")
    private int code;

    /** 响应消息: 成功时="success", 失败时=具体错误描述 */
    @Schema(description = "响应消息: 成功时为success, 失败时为具体错误描述", example = "success")
    private String message;

    /** 响应数据: 泛型, 失败时可为 null */
    @Schema(description = "响应数据, 失败时为null")
    private T data;

    /** 响应时间戳(毫秒) */
    @Schema(description = "响应时间戳(毫秒)", example = "1716940800000")
    private long timestamp;

    // ========== 成功 ==========

    /**
     * 成功响应(带数据).
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功响应
     */
    public static <T> RT<T> ok(T data) {
        return new RT<>(0, "success", data, System.currentTimeMillis());
    }

    /**
     * 成功响应(无数据).
     *
     * @param <T> 数据类型
     * @return 成功响应
     */
    public static <T> RT<T> ok() {
        return ok(null);
    }

    /**
     * 成功响应(自定义消息).
     *
     * @param message 消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return 成功响应
     */
    public static <T> RT<T> ok(String message, T data) {
        return new RT<>(0, message, data, System.currentTimeMillis());
    }

    // ========== 失败 ==========

    /**
     * 失败响应(指定错误码和消息).
     *
     * @param code    错误码
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 失败响应
     */
    public static <T> RT<T> fail(int code, String message) {
        return new RT<>(code, message, null, System.currentTimeMillis());
    }

    /**
     * 失败响应(使用 IErrorCode 枚举).
     *
     * @param errorCode 错误码枚举
     * @param <T>       数据类型
     * @return 失败响应
     */
    public static <T> RT<T> fail(IErrorCode errorCode) {
        return fail(errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 失败响应(使用 IErrorCode 枚举 + 自定义详情).
     *
     * @param errorCode 错误码枚举
     * @param detail    详情(追加到消息后)
     * @param <T>       数据类型
     * @return 失败响应
     */
    public static <T> RT<T> fail(IErrorCode errorCode, String detail) {
        String msg = detail == null || detail.isEmpty()
                ? errorCode.getMessage()
                : errorCode.getMessage() + ": " + detail;
        return fail(errorCode.getCode(), msg);
    }

    /**
     * 服务端错误(code=500).
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 失败响应
     */
    public static <T> RT<T> error(String message) {
        return fail(500, message);
    }

    /**
     * 参数错误(code=400).
     *
     * @param message 错误消息
     * @param <T>     数据类型
     * @return 失败响应
     */
    public static <T> RT<T> paramError(String message) {
        return fail(400, message);
    }

    /**
     * 未认证(code=401).
     *
     * @param <T> 数据类型
     * @return 失败响应
     */
    public static <T> RT<T> unauthorized() {
        return fail(401, "未登录或登录已过期");
    }

    /**
     * 无权限(code=403).
     *
     * @param <T> 数据类型
     * @return 失败响应
     */
    public static <T> RT<T> forbidden() {
        return fail(403, "无操作权限");
    }

    /**
     * 判断是否成功.
     *
     * @return true=成功(code=200)
     */
    public boolean isSuccess() {
        return this.code == 0;
    }

    /**
     * 链式设置响应数据.
     *
     * <p>支持链式调用: {@code RT.ok().data(xxx)}</p>
     *
     * @param data 响应数据
     * @return 当前实例(链式调用)
     */
    public RT<T> data(T data) {
        this.data = data;
        return this;
    }
}
