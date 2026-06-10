package com.erp.common.exception;

/**
 * 认证异常.
 *
 * <p>用于用户认证相关场景的异常, 例如:
 * 未登录、登录过期、Token无效、密码错误等.</p>
 *
 * <p>示例用法:
 * <pre>{@code
 * throw new AuthException(ErrorCode.UNAUTHORIZED);
 * throw new AuthException(ErrorCode.TOKEN_EXPIRED);
 * throw new AuthException(ErrorCode.PASSWORD_ERROR);
 * }</pre>
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
public class AuthException extends BaseException {

    /**
     * 构造方法 - 使用错误码枚举(静态消息).
     *
     * @param errorCode 错误码枚举
     */
    public AuthException(IErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 构造方法 - 使用错误码枚举 + 参数化消息.
     *
     * @param errorCode 错误码枚举
     * @param args      消息占位符参数
     */
    public AuthException(IErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    /**
     * 构造方法 - 使用原始异常.
     *
     * @param errorCode 错误码枚举
     * @param cause     原始异常
     */
    public AuthException(IErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
