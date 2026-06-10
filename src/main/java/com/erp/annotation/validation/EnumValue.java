package com.erp.annotation.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 枚举值校验注解.
 *
 * <p>校验字段值是否在指定枚举类的合法值范围内. 支持通过 {@link #method()} 指定
 * 获取枚举值的方法(默认使用 {@code getValue} 方法).</p>
 *
 * <p>使用示例:
 * <pre>{@code
 * public class OrderDto {
 *     @EnumValue(enumClass = OrderStatusEnum.class, method = "getCode",
 *                message = "{validation.enum-value.invalid}")
 *     private Integer status;
 * }
 * }</pre>
 * </p>
 *
 * <p>null 值不校验(如需必填, 请配合 {@code @NotNull} 使用).</p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Documented
@Constraint(validatedBy = EnumValueValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(EnumValue.List.class)
public @interface EnumValue {

    /**
     * 枚举类.
     *
     * @return 要校验的枚举类
     */
    Class<? extends Enum<?>> enumClass();

    /**
     * 获取枚举值的方法名.
     *
     * <p>默认使用 {@code getValue} 方法. 该方法必须是无参 public 方法,
     * 返回值类型需与待校验字段类型兼容.</p>
     *
     * @return 方法名
     */
    String method() default "getValue";

    String message() default "{validation.enum-value.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 支持同一字段上使用多个 @EnumValue 注解.
     */
    @Documented
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        EnumValue[] value();
    }
}
