package com.erp.common.annotation;

import com.erp.common.enums.LogicEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解.
 *
 * <p>标注在类或方法上, 声明访问所需的权限码.
 * 标注在类上时, 对该类所有方法生效; 同时标注类和方法时, 方法级注解优先.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 权限码数组.
     */
    String[] value();

    /**
     * 权限码之间的逻辑关系, 默认 AND(所有权限都必须通过).
     */
    LogicEnum logic() default LogicEnum.AND;
}
