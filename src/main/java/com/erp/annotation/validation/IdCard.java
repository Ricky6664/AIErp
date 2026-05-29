package com.erp.annotation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 中国大陆身份证号校验注解.
 *
 * <p>校验规则: 支持15位旧版和18位新版身份证号格式.</p>
 *
 * <p>使用示例:
 * <pre>{@code
 * public class UserDto {
 *     @IdCard(message = "身份证号格式不正确")
 *     private String idCard;
 * }
 * }</pre>
 * </p>
 *
 * <p>null 值不校验(如需必填, 请配合 {@code @NotBlank} 使用).</p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Documented
@Constraint(validatedBy = IdCardValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IdCard {

    String message() default "身份证号格式不正确";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
