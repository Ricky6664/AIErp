package com.erp.common.enums;

/**
 * 权限校验逻辑运算符枚举.
 *
 * <p>用于 {@code @RequirePermission} 注解, 指定多个权限码之间的逻辑关系.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
public enum LogicEnum {

    /** 所有权限都必须通过 */
    AND,

    /** 任一权限通过即可 */
    OR
}
