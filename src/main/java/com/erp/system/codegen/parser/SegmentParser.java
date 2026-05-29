package com.erp.system.codegen.parser;

/**
 * 段解析器接口 —— 根据编码段配置解析生成段值.
 *
 * @author AI
 * @since 2026-05-29
 */
public interface SegmentParser {

    String parse(SegmentParseContext context);

    Integer getSegmentType();
}
