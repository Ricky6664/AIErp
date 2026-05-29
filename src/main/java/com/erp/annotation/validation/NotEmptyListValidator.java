package com.erp.annotation.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Collection;

/**
 * 非空列表校验器.
 *
 * <p>校验 {@link Collection} 类型字段不为 null 且不为空(至少包含一个元素).</p>
 *
 * <p>支持的类型:
 * <ul>
 *   <li>{@link java.util.List}</li>
 *   <li>{@link java.util.Set}</li>
 *   <li>其他 {@link Collection} 实现</li>
 * </ul>
 * </p>
 *
 * <p>null 值返回 false(列表为空或 null 均校验失败).</p>
 *
 * @author AI
 * @since 2026-05-29
 */
public class NotEmptyListValidator implements ConstraintValidator<NotEmptyList, Collection<?>> {

    @Override
    public void initialize(NotEmptyList constraintAnnotation) {
        // 无需初始化
    }

    @Override
    public boolean isValid(Collection<?> value, ConstraintValidatorContext context) {
        // null 或空集合均校验失败
        return value != null && !value.isEmpty();
    }
}
