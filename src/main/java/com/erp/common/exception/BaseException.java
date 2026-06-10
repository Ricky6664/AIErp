package com.erp.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 自定义异常基类(抽象类).
 *
 * <p>所有业务自定义异常的根类, 继承 {@link RuntimeException},
 * 统一携带错误码({@link IErrorCode})、错误消息和可选数据载荷.</p>
 *
 * <p>支持两种消息模式:
 * <ul>
 *   <li>静态消息: 直接使用 {@link IErrorCode#getMessage()}</li>
 *   <li>参数化消息: 使用占位符 {@code {}} 配合 {@code args} 进行格式化</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Getter
public abstract class BaseException extends RuntimeException {

    /** 错误码 */
    private final int code;

    /** 错误消息(已格式化) */
    private final String msg;

    /** 附加数据(可选, 用于向前端传递额外上下文, 通过 {@link #setData(Object)} 设置) */
    @Setter
    private Object data;

    /**
     * 构造方法 - 使用错误码枚举(静态消息).
     *
     * @param errorCode 错误码枚举
     */
    protected BaseException(IErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.msg = errorCode.getMessage();
    }

    /**
     * 构造方法 - 使用错误码枚举 + 参数化消息.
     *
     * <p>消息中的 {@code {}} 占位符将按顺序被 {@code args} 替换.</p>
     *
     * @param errorCode 错误码枚举
     * @param args      消息占位符参数
     */
    protected BaseException(IErrorCode errorCode, Object... args) {
        super(formatMessage(errorCode.getMessage(), args));
        this.code = errorCode.getCode();
        this.msg = formatMessage(errorCode.getMessage(), args);
    }

    /**
     * 构造方法 - 使用原始异常.
     *
     * @param errorCode 错误码枚举
     * @param cause     原始异常
     */
    protected BaseException(IErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.code = errorCode.getCode();
        this.msg = errorCode.getMessage();
    }

    /**
     * 消息占位符格式化.
     *
     * <p>将消息模板中的 {@code {}} 按顺序替换为 {@code args} 中的值.</p>
     *
     * @param template 消息模板(含 {@code {}} 占位符)
     * @param args     替换参数
     * @return 格式化后的消息
     */
    private static String formatMessage(String template, Object... args) {
        if (args == null || args.length == 0) {
            return template;
        }
        String result = template;
        for (Object arg : args) {
            int idx = result.indexOf("{}");
            if (idx >= 0) {
                result = result.substring(0, idx) + String.valueOf(arg) + result.substring(idx + 2);
            }
        }
        return result;
    }
}
