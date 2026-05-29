package com.erp.system.codegen.parser;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 日期段解析器 —— 使用 DateTimeFormatter 按指定格式格式化当前日期.
 *
 * @author AI
 * @since 2026-05-29
 */
@Component
public class DateSegmentParser implements SegmentParser {

    @Override
    public String parse(SegmentParseContext context) {
        String format = context.getSegmentFormat() != null ? context.getSegmentFormat() : "yyyyMMdd";
        return LocalDate.now().format(DateTimeFormatter.ofPattern(format));
    }

    @Override
    public Integer getSegmentType() {
        return 2;
    }
}
