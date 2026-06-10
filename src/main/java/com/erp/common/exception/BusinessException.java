package com.erp.common.exception;

/**
 * 业务异常.
 *
 * <p>用于业务逻辑不满足预期时抛出的异常, 例如:
 * 数据不存在、状态不允许操作、业务规则校验失败等场景.</p>
 *
 * <p>示例用法:
 * <pre>{@code
 * throw new BusinessException(ErrorCode.BUSINESS_ERROR);
 * throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "用户ID: " + userId);
 * throw new BusinessException(ErrorCode.BUSINESS_ERROR, cause);
 * }</pre>
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
public class BusinessException extends BaseException {

    /**
     * 构造方法 - 使用错误码枚举(静态消息).
     *
     * @param errorCode 错误码枚举
     */
    public BusinessException(IErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 构造方法 - 使用错误码枚举 + 参数化消息.
     *
     * @param errorCode 错误码枚举
     * @param args      消息占位符参数
     */
    public BusinessException(IErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    /**
     * 构造方法 - 使用原始异常.
     *
     * @param errorCode 错误码枚举
     * @param cause     原始异常
     */
    public BusinessException(IErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
