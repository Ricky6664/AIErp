package com.erp.annotation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 枚举值校验器.
 *
 * <p>通过反射调用枚举实例的指定方法获取合法值集合, 判断待校验值是否在该集合中.</p>
 *
 * <p>校验流程:
 * <ol>
 *   <li>初始化时通过反射获取枚举实例的指定方法, 收集所有合法值</li>
 *   <li>校验时判断字段值是否在合法值集合中</li>
 * </ol>
 * </p>
 *
 * <p>null 值返回 true(不做校验), 如需必填请配合 {@code @NotNull} 使用.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {

    /**
     * 合法值集合(初始化时从枚举实例中提取).
     */
    private final Set<Object> validValues = new HashSet<>();

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        Class<? extends Enum<?>> enumClass = constraintAnnotation.enumClass();
        String methodName = constraintAnnotation.method();

        Enum<?>[] enumConstants = enumClass.getEnumConstants();
        if (enumConstants == null || enumConstants.length == 0) {
            return;
        }

        try {
            Method method = enumClass.getMethod(methodName);
            for (Enum<?> enumConstant : enumConstants) {
                validValues.add(method.invoke(enumConstant));
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "无法从枚举类 " + enumClass.getName() + " 中通过方法 " + methodName + " 获取值", e);
        }
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        // null 值不校验(由 @NotNull 负责非空校验)
        if (value == null) {
            return true;
        }
        return validValues.contains(value);
    }
}
