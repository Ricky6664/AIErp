package com.erp.common.exception;

/**
 * 错误码统一接口.
 *
 * <p>所有错误码枚举类必须实现此接口, 保证错误码和错误消息的访问方式统一.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
public interface IErrorCode {

    /**
     * 获取错误码.
     *
     * @return 错误码(数字)
     */
    int getCode();

    /**
     * 获取错误消息.
     *
     * @return 错误消息(中文描述)
     */
    String getMessage();
}
