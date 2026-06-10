package com.erp.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.system.entity.SysCodeRule;
import com.erp.system.service.SysCodeRuleService;
import com.erp.system.vo.SysCodeRuleVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 编码规则 Service 实现.
 *
 * @author AI
 * @since 2026-05-29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysCodeRuleServiceImpl extends SysCodeRuleService {

    private final StringRedisTemplate redisTemplate;

    private static final String LOCK_KEY_PREFIX = "code:lock:";
    private static final String CACHE_KEY_PREFIX = "code:cache:";
    private static final String SEQ_KEY_PREFIX = "code:seq:";
    private static final long LOCK_TIMEOUT_SECONDS = 10;
    private static final int MAX_RETRY = 3;
    private static final long RETRY_DELAY_MS = 100;

    // ========== 编码预览 ==========

    @Override
    public String preview(Long ruleId) {
        SysCodeRule rule = getBaseMapper().selectById(ruleId);
        if (rule == null) {
            throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "编码规则不存在: id=" + ruleId);
        }
        List<SysCodeRuleVO.SegmentVO> segments = querySegmentsByRuleId(ruleId);
        if (segments == null || segments.isEmpty()) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "编码规则未配置编码段");
        }
        return buildCode(rule, segments, true);
    }

    // ========== 编码生成（分布式锁 + 重试） ==========

    @Override
    public String generate(String ruleCode) {
        String lockKey = LOCK_KEY_PREFIX + ruleCode;
        for (int i = 0; i < MAX_RETRY; i++) {
            Boolean locked = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, "1", LOCK_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(locked)) {
                try {
                    SysCodeRule rule = getBaseMapper().selectOne(
                            new LambdaQueryWrapper<SysCodeRule>()
                                    .eq(SysCodeRule::getRuleCode, ruleCode));
                    if (rule == null) {
                        throw new BusinessException(ErrorCode.DATA_NOT_FOUND, "编码规则不存在: " + ruleCode);
                    }
                    if (rule.getIsEnabled() == null || rule.getIsEnabled() != 1) {
                        throw new BusinessException(ErrorCode.DATA_STATUS_INVALID, "编码规则未启用: " + ruleCode);
                    }
                    List<SysCodeRuleVO.SegmentVO> segments = querySegmentsByRuleId(rule.getId());
                    if (segments == null || segments.isEmpty()) {
                        throw new BusinessException(ErrorCode.BUSINESS_ERROR, "编码规则未配置编码段: " + ruleCode);
                    }
                    String code = buildCode(rule, segments, false);
                    log.info("编码生成成功: ruleCode={}, code={}", ruleCode, code);
                    return code;
                } finally {
                    redisTemplate.delete(lockKey);
                }
            }
            try {
                Thread.sleep(RETRY_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BusinessException(ErrorCode.BUSINESS_ERROR, "编码生成被中断", e);
            }
        }
        throw new BusinessException(ErrorCode.OPERATION_TOO_FREQUENT, "编码生成失败，请稍后重试");
    }

    // ========== 缓存刷新 ==========

    @Override
    public void refreshCache(String ruleCode) {
        String cacheKey = CACHE_KEY_PREFIX + ruleCode;
        redisTemplate.delete(cacheKey);
        String seqKey = SEQ_KEY_PREFIX + ruleCode;
        redisTemplate.delete(seqKey);
        log.info("编码规则缓存与序列已清除: ruleCode={}", ruleCode);
    }

    // ========== 编码构建核心逻辑 ==========

    /**
     * 构建编码字符串.
     *
     * @param rule     编码规则
     * @param segments 编码段列表（已按 segmentOrder 排序）
     * @param preview  true-预览模式（序列段使用占位值），false-正式生成（序列段使用Redis INCR）
     * @return 编码字符串
     */
    private String buildCode(SysCodeRule rule, List<SysCodeRuleVO.SegmentVO> segments, boolean preview) {
        StringBuilder sb = new StringBuilder();
        String separator = rule.getSeparator() != null ? rule.getSeparator() : "";
        for (int i = 0; i < segments.size(); i++) {
            SysCodeRuleVO.SegmentVO seg = segments.get(i);
            String value = resolveSegmentValue(rule, seg, preview);
            sb.append(value);
            if (i < segments.size() - 1 && !separator.isEmpty()) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    /**
     * 解析单个编码段的值.
     *
     * @param rule    编码规则
     * @param seg     编码段配置
     * @param preview 是否预览模式
     * @return 段值字符串
     */
    private String resolveSegmentValue(SysCodeRule rule, SysCodeRuleVO.SegmentVO seg, boolean preview) {
        Integer segmentType = seg.getSegmentType();
        if (segmentType == null) {
            return "";
        }
        switch (segmentType) {
            case 1: // 固定段
                return seg.getSegmentValue() != null ? seg.getSegmentValue() : "";
            case 2: { // 日期段
                String format = seg.getSegmentFormat() != null ? seg.getSegmentFormat() : "yyyyMMdd";
                return LocalDate.now().format(DateTimeFormatter.ofPattern(format));
            }
            case 3: { // 序列段
                if (preview) {
                    int digits = seg.getSegmentLength() != null ? seg.getSegmentLength() : 4;
                    return String.format("%0" + digits + "d", 1);
                }
                String seqKey = SEQ_KEY_PREFIX + rule.getRuleCode();
                Long seq = redisTemplate.opsForValue().increment(seqKey);
                int digits = seg.getSegmentLength() != null ? seg.getSegmentLength() : 4;
                return String.format("%0" + digits + "d", seq != null ? seq : 1);
            }
            case 4: // 自定义变量段
                return seg.getSegmentValue() != null ? seg.getSegmentValue() : "";
            default:
                return "";
        }
    }
}
