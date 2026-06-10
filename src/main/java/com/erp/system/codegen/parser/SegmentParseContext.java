package com.erp.system.codegen.parser;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 段解析上下文.
 *
 * @author AI
 * @since 2026-05-29
 */
@Data
@Builder
public class SegmentParseContext {

    private Integer segmentType;

    private String segmentValue;

    private String segmentFormat;

    private Integer segmentLength;

    private String ruleCode;

    private boolean preview;

    private Map<String, String> variables;
}
