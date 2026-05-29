package com.erp.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解.
 *
 * <p>标注在方法上, 自动记录操作日志(通过AOP切面实现).</p>
 *
 * @author AI
 * @since 2026-05-30
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperLog {

    /** 操作描述, 默认取方法上的 @Operation summary. */
    String value() default "";

    /** 操作类型: CREATE/UPDATE/DELETE/QUERY/EXPORT/IMPORT/OTHER */
    String type() default "OTHER";

    /** 是否记录请求参数, 默认 true. */
    boolean recordParams() default true;

    /** 是否记录响应结果, 默认 false(查询结果数据量大). */
    boolean recordResult() default false;
}
