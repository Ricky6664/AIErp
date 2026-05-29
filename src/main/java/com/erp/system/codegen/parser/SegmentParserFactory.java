package com.erp.system.codegen.parser;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 段解析器工厂 —— 根据 segment_type 路由到对应的段解析器实现.
 *
 * @author AI
 * @since 2026-05-29
 */
@Component
public class SegmentParserFactory {

    private final Map<Integer, SegmentParser> parserMap;

    public SegmentParserFactory(List<SegmentParser> parsers) {
        this.parserMap = parsers.stream()
                .collect(Collectors.toMap(SegmentParser::getSegmentType, Function.identity()));
    }

    public SegmentParser getParser(Integer segmentType) {
        SegmentParser parser = parserMap.get(segmentType);
        if (parser == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID);
        }
        return parser;
    }
}
