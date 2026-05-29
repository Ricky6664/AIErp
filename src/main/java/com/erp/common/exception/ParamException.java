package com.erp.common.exception;

/**
 * 参数异常.
 *
 * <p>用于请求参数校验失败的场景, 例如:
 * 参数缺失、格式不正确、值超出范围、类型不匹配、参数重复等.</p>
 *
 * <p>示例用法:
 * <pre>{@code
 * throw new ParamException(ErrorCode.PARAM_INVALID);
 * throw new ParamException(ErrorCode.PARAM_MISSING, "username");
 * throw new ParamException(ErrorCode.PARAM_FORMAT_ERROR, "email", "xxx@yy");
 * }</pre>
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
public class ParamException extends BaseException {

    /**
     * 构造方法 - 使用错误码枚举(静态消息).
     *
     * @param errorCode 错误码枚举
     */
    public ParamException(IErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 构造方法 - 使用错误码枚举 + 参数化消息.
     *
     * @param errorCode 错误码枚举
     * @param args      消息占位符参数
     */
    public ParamException(IErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    /**
     * 构造方法 - 使用原始异常.
     *
     * @param errorCode 错误码枚举
     * @param cause     原始异常
     */
    public ParamException(IErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
