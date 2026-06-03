package com.erp.auth.exception;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.AuthException;

/**
 * 验证码异常(验证码错误或已过期).
 *
 * @author AI
 * @since 2026-06-03
 */
public class CaptchaException extends AuthException {

    public CaptchaException() {
        super(ErrorCode.CAPTCHA_ERROR);
    }
}
