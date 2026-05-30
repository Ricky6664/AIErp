package com.erp.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解.
 *
 * <p>标注在方法上, 配合 OperLogAspect 切面自动记录操作日志.</p>
 *
 * @author AI
 * @since 2026-05-30
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperLog {

    /** 所属模块. */
    String module() default "";

    /** 操作类型: 增/删/改/查/导出/导入. */
    String action() default "";

    /** 操作描述. */
    String description() default "";

    /** 是否保存请求数据, 默认 true. */
    boolean saveRequestData() default true;

    /** 是否保存响应数据, 默认 false(查询结果数据量大). */
    boolean saveResponseData() default false;

    /** 是否保存异常堆栈, 默认 false(避免超过数据库字段长度). */
    boolean isSaveErrorTrace() default false;
}
