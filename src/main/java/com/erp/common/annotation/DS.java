package com.erp.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 多数据源切换注解.
 *
 * <p>标注在类或方法上, 指定使用的数据源名称.
 * 未标注时默认使用 master 数据源.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DS {

    /**
     * 数据源名称, 默认 master.
     */
    String value() default "master";
}
