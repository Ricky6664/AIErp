package com.erp.annotation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * 中国大陆手机号校验器.
 *
 * <p>校验规则: 11位数字, 以1开头, 第二位为3-9.</p>
 *
 * <p>null 值返回 true(不做校验), 如需必填请配合 {@code @NotBlank} 使用.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
public class PhoneValidator implements ConstraintValidator<Phone, String> {

    /**
     * 中国大陆手机号正则: 1开头, 第二位3-9, 共11位数字.
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    @Override
    public void initialize(Phone constraintAnnotation) {
        // 无需初始化
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null 值不校验(由 @NotBlank 负责非空校验)
        if (value == null || value.isEmpty()) {
            return true;
        }
        return PHONE_PATTERN.matcher(value).matches();
    }
}
