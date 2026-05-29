package com.erp.annotation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 非空列表校验注解.
 *
 * <p>校验 {@link java.util.Collection} 类型字段不为 null 且不为空(至少包含一个元素).</p>
 *
 * <p>使用示例:
 * <pre>{@code
 * public class BatchDeleteDto {
 *     @NotEmptyList(message = "{validation.not-empty-list.invalid}")
 *     private List<Long> ids;
 * }
 * }</pre>
 * </p>
 *
 * <p>与 {@code @NotEmpty} 的区别: 本注解专用于 {@link java.util.Collection} 类型,
 * 语义更明确, 且支持 i18n 消息键.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Documented
@Constraint(validatedBy = NotEmptyListValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmptyList {

    String message() default "{validation.not-empty-list.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
