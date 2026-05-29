package com.erp.system.codegen;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.system.codegen.parser.SegmentParseContext;
import com.erp.system.codegen.parser.SegmentParser;
import com.erp.system.codegen.parser.SegmentParserFactory;
import com.erp.system.entity.SysCodeRule;
import com.erp.system.entity.SysCodeRuleSegment;
import com.erp.system.mapper.SysCodeRuleMapper;
import com.erp.system.mapper.SysCodeRuleSegmentMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 编码生成服务 —— 获取分布式锁后按规则生成真实编码, 消耗序列号.
 *
 * @author AI
 * @since 2026-05-30
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CodeGenerateService {

    private final StringRedisTemplate redisTemplate;
    private final SysCodeRuleMapper ruleMapper;
    private final SysCodeRuleSegmentMapper segmentMapper;
    private final SegmentParserFactory parserFactory;

    private static final String LOCK_KEY_PREFIX = "code:lock:";
    private static final String SEQ_KEY_PREFIX = "code:seq:";
    private static final long LOCK_TIMEOUT_SECONDS = 3;
    private static final long SEQ_EXPIRE_SECONDS = 86400;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_BASE_DELAY_MS = 50;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 根据规则编码生成真实编码.
     *
     * @param ruleCode 编码规则编码
     * @return 生成的编码字符串
     */
    public String generate(String ruleCode) {
        String lockKey = LOCK_KEY_PREFIX + ruleCode;
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            Boolean locked = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "1", LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(locked)) {
                try {
                    return doGenerate(ruleCode);
                } catch (BusinessException e) {
                    throw e;
                } catch (Exception e) {
                    if (attempt == MAX_RETRIES - 1) {
                        throw new BusinessException(ErrorCode.BUSINESS_ERROR, "编码生成失败: " + ruleCode);
                    }
                    log.warn("编码生成失败, 重试: ruleCode={}, attempt={}", ruleCode, attempt + 1, e);
                    sleepBeforeRetry(attempt);
                } finally {
                    redisTemplate.delete(lockKey);
                }
            } else if (attempt < MAX_RETRIES - 1) {
                sleepBeforeRetry(attempt);
            }
        }
        throw new BusinessException(ErrorCode.OPERATION_TOO_FREQUENT, "获取分布式锁失败: " + ruleCode);
    }

    private String doGenerate(String ruleCode) {
        SysCodeRule rule = loadRule(ruleCode);
        List<SysCodeRuleSegment> segments = loadSegments(rule.getId());
        String separator = rule.getSeparator() != null ? rule.getSeparator() : "";

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < segments.size(); i++) {
            SysCodeRuleSegment seg = segments.get(i);
            String value;
            if (seg.getSegmentType() != null && seg.getSegmentType() == 3) {
                value = genSequence(ruleCode, seg.getSegmentLength());
            } else {
                value = parseNonSequence(seg, ruleCode);
            }
            sb.append(value);
            if (i < segments.size() - 1 && !separator.isEmpty()) {
                sb.append(separator);
            }
        }

        String code = sb.toString();
        log.info("编码生成完成: ruleCode={}, code={}", ruleCode, code);
        return code;
    }

    private SysCodeRule loadRule(String ruleCode) {
        SysCodeRule rule = ruleMapper.selectByRuleCode(ruleCode);
        if (rule == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "编码规则不存在: " + ruleCode);
        }
        return rule;
    }

    private List<SysCodeRuleSegment> loadSegments(Long ruleId) {
        List<SysCodeRuleSegment> segments = segmentMapper.selectList(
                new LambdaQueryWrapper<SysCodeRuleSegment>()
                        .eq(SysCodeRuleSegment::getRuleId, ruleId)
                        .orderByAsc(SysCodeRuleSegment::getSegmentOrder));
        if (segments == null || segments.isEmpty()) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "编码规则未配置编码段");
        }
        return segments;
    }

    private String parseNonSequence(SysCodeRuleSegment seg, String ruleCode) {
        SegmentParseContext ctx = SegmentParseContext.builder()
                .segmentType(seg.getSegmentType())
                .segmentValue(seg.getSegmentValue())
                .segmentFormat(seg.getSegmentFormat())
                .segmentLength(seg.getSegmentLength())
                .ruleCode(ruleCode)
                .preview(false)
                .build();
        SegmentParser parser = parserFactory.getParser(seg.getSegmentType());
        return parser.parse(ctx);
    }

    private String genSequence(String ruleCode, Integer length) {
        int digits = length != null ? length : 4;
        String dateStr = LocalDate.now().format(DATE_FMT);
        String seqKey = SEQ_KEY_PREFIX + ruleCode + ":" + dateStr;
        Long seq = redisTemplate.opsForValue().increment(seqKey);
        if (seq != null && seq == 1) {
            redisTemplate.expire(seqKey, SEQ_EXPIRE_SECONDS, TimeUnit.SECONDS);
        }
        long val = seq != null ? seq : 1;
        return String.format("%0" + digits + "d", val);
    }

    private void sleepBeforeRetry(int attempt) {
        try {
            Thread.sleep(RETRY_BASE_DELAY_MS * (attempt + 1));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.CACHE_ERROR);
        }
    }
}
