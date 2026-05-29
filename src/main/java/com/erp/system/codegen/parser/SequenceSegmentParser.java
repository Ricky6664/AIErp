package com.erp.system.codegen.parser;

import com.erp.system.codegen.SequenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 序列段解析器 —— 调用 SequenceGenerator 获取序列值并按 segment_length 左补零.
 *
 * @author AI
 * @since 2026-05-29
 */
@Component
@RequiredArgsConstructor
public class SequenceSegmentParser implements SegmentParser {

    private final SequenceGenerator sequenceGenerator;

    @Override
    public String parse(SegmentParseContext context) {
        int digits = context.getSegmentLength() != null ? context.getSegmentLength() : 4;
        if (context.isPreview()) {
            return String.format("%0" + digits + "d", 1);
        }
        long seq = sequenceGenerator.next(context.getRuleCode());
        return String.format("%0" + digits + "d", seq);
    }

    @Override
    public Integer getSegmentType() {
        return 3;
    }
}
