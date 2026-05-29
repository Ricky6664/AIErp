package com.erp.annotation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * 中国大陆身份证号校验器.
 *
 * <p>校验规则:
 * <ul>
 *   <li>15位旧版: 6位地区码 + 6位日期(yyMMdd) + 3位顺序码</li>
 *   <li>18位新版: 6位地区码 + 8位日期(yyyyMMdd) + 3位顺序码 + 1位校验码(0-9或X/x)</li>
 * </ul>
 * </p>
 *
 * <p>null 值返回 true(不做校验), 如需必填请配合 {@code @NotBlank} 使用.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
public class IdCardValidator implements ConstraintValidator<IdCard, String> {

    /**
     * 15位旧版身份证号正则.
     */
    private static final Pattern ID_CARD_15 = Pattern.compile(
            "^\\d{6}\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}$");

    /**
     * 18位新版身份证号正则.
     */
    private static final Pattern ID_CARD_18 = Pattern.compile(
            "^\\d{6}\\d{4}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$");

    @Override
    public void initialize(IdCard constraintAnnotation) {
        // 无需初始化
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null 值不校验(由 @NotBlank 负责非空校验)
        if (value == null || value.isEmpty()) {
            return true;
        }
        return ID_CARD_15.matcher(value).matches()
                || ID_CARD_18.matcher(value).matches();
    }
}
