package com.erp.common.exception;

/**
 * 权限异常.
 *
 * <p>用于用户权限校验失败的场景, 例如:
 * 无操作权限、账号被锁定、角色不匹配等.</p>
 *
 * <p>示例用法:
 * <pre>{@code
 * throw new PermissionException(ErrorCode.FORBIDDEN);
 * throw new PermissionException(ErrorCode.ACCOUNT_LOCKED);
 * }</pre>
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
public class PermissionException extends BaseException {

    /**
     * 构造方法 - 使用错误码枚举(静态消息).
     *
     * @param errorCode 错误码枚举
     */
    public PermissionException(IErrorCode errorCode) {
        super(errorCode);
    }

    /**
     * 构造方法 - 使用错误码枚举 + 参数化消息.
     *
     * @param errorCode 错误码枚举
     * @param args      消息占位符参数
     */
    public PermissionException(IErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }

    /**
     * 构造方法 - 使用原始异常.
     *
     * @param errorCode 错误码枚举
     * @param cause     原始异常
     */
    public PermissionException(IErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
