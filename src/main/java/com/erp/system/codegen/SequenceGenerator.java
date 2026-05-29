package com.erp.system.codegen;

/**
 * 序列号生成器接口 —— 为编码引擎提供全局唯一序列号.
 *
 * @author AI
 * @since 2026-05-29
 */
public interface SequenceGenerator {

    long next(String ruleCode);
}
