package com.erp.system.codegen.parser;

import org.springframework.stereotype.Component;

/**
 * 固定段解析器 —— 直接返回配置的固定字符串.
 *
 * @author AI
 * @since 2026-05-29
 */
@Component
public class FixedSegmentParser implements SegmentParser {

    @Override
    public String parse(SegmentParseContext context) {
        return context.getSegmentValue() != null ? context.getSegmentValue() : "";
    }

    @Override
    public Integer getSegmentType() {
        return 1;
    }
}
