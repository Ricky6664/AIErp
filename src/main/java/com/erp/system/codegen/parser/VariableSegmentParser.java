package com.erp.system.codegen.parser;

import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 自定义变量段解析器 —— 从上下文变量 Map 中读取对应 key 的值.
 *
 * @author AI
 * @since 2026-05-29
 */
@Component
public class VariableSegmentParser implements SegmentParser {

    @Override
    public String parse(SegmentParseContext context) {
        String key = context.getSegmentValue();
        if (key == null || key.isEmpty()) {
            return "";
        }
        Map<String, String> variables = context.getVariables();
        if (variables != null && variables.containsKey(key)) {
            return variables.get(key);
        }
        return "";
    }

    @Override
    public Integer getSegmentType() {
        return 4;
    }
}
