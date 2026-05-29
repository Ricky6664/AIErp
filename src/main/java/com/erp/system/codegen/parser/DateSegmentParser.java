package com.erp.system.codegen.parser;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日期段解析器 —— 使用 DateTimeFormatter 按指定格式格式化当前日期.
 *
 * <p>支持格式: yyyyMMdd / yyMMdd / yyyy-MM-dd / yyMM / MMdd 等标准日期模式,
 * 默认使用 yyyyMMdd. 格式化器缓存以提升并发性能.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Component
public class DateSegmentParser implements SegmentParser {

    private static final String DEFAULT_FORMAT = "yyyyMMdd";
    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

    static {
        FORMATTER_CACHE.put(DEFAULT_FORMAT, DateTimeFormatter.ofPattern(DEFAULT_FORMAT));
    }

    @Override
    public String parse(SegmentParseContext context) {
        String format = context.getSegmentFormat();
        if (format == null || format.isBlank()) {
            format = DEFAULT_FORMAT;
        }
        DateTimeFormatter formatter = FORMATTER_CACHE.computeIfAbsent(format, f -> {
            try {
                return DateTimeFormatter.ofPattern(f);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ErrorCode.PARAM_FORMAT_ERROR);
            }
        });
        return LocalDate.now().format(formatter);
    }

    @Override
    public Integer getSegmentType() {
        return 2;
    }
}
