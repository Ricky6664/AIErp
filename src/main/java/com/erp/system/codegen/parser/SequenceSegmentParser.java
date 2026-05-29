package com.erp.system.codegen.parser;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.system.codegen.SequenceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 序列段解析器 —— 调用 SequenceGenerator 获取序列值并按 segment_length 左补零.
 *
 * <p>序列号生成失败时最多重试 3 次, 每次重试间隔递增, 全部失败后抛出 BusinessException.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Component
@RequiredArgsConstructor
public class SequenceSegmentParser implements SegmentParser {

    private static final int MAX_RETRIES = 3;
    private static final long RETRY_BASE_DELAY_MS = 50L;

    private final SequenceGenerator sequenceGenerator;

    @Override
    public String parse(SegmentParseContext context) {
        int digits = context.getSegmentLength() != null ? context.getSegmentLength() : 4;
        if (context.isPreview()) {
            return String.format("%0" + digits + "d", 1);
        }
        long seq = nextWithRetry(context.getRuleCode());
        return String.format("%0" + digits + "d", seq);
    }

    private long nextWithRetry(String ruleCode) {
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                return sequenceGenerator.next(ruleCode);
            } catch (Exception e) {
                if (attempt == MAX_RETRIES - 1) {
                    throw new BusinessException(ErrorCode.CACHE_ERROR);
                }
                try {
                    Thread.sleep(RETRY_BASE_DELAY_MS * (attempt + 1));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new BusinessException(ErrorCode.CACHE_ERROR);
                }
            }
        }
        throw new BusinessException(ErrorCode.CACHE_ERROR);
    }

    @Override
    public Integer getSegmentType() {
        return 3;
    }
}
