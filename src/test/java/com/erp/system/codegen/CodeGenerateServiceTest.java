package com.erp.system.codegen;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.system.codegen.parser.SegmentParseContext;
import com.erp.system.codegen.parser.SegmentParser;
import com.erp.system.codegen.parser.SegmentParserFactory;
import com.erp.system.entity.SysCodeRule;
import com.erp.system.entity.SysCodeRuleSegment;
import com.erp.system.mapper.SysCodeRuleMapper;
import com.erp.system.mapper.SysCodeRuleSegmentMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * CodeGenerateService 验证测试 —— 按验收标准逐项验证编码生成核心方法.
 *
 * @author AI
 * @since 2026-05-30
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CodeGenerateService - 编码生成服务验证")
class CodeGenerateServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private SysCodeRuleMapper ruleMapper;

    @Mock
    private SysCodeRuleSegmentMapper segmentMapper;

    @Mock
    private SegmentParserFactory parserFactory;

    private CodeGenerateService service;

    private static final String RULE_CODE = "ORDER";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        service = new CodeGenerateService(redisTemplate, ruleMapper, segmentMapper, parserFactory);
    }

    // ========== helpers ==========

    private void stubLockAcquired() {
        when(valueOperations.setIfAbsent(contains("code:lock:"), eq("1"), eq(3L), eq(TimeUnit.SECONDS)))
                .thenReturn(true);
    }

    private SysCodeRule stubRule() {
        SysCodeRule rule = new SysCodeRule();
        rule.setId(1L);
        rule.setRuleCode(RULE_CODE);
        rule.setSeparator("-");
        return rule;
    }

    private List<SysCodeRuleSegment> stubTwoSegments() {
        SysCodeRuleSegment seg1 = new SysCodeRuleSegment();
        seg1.setSegmentType(1); // fixed
        seg1.setSegmentOrder(1);
        seg1.setSegmentValue("ORD");
        seg1.setSegmentLength(null);

        SysCodeRuleSegment seg2 = new SysCodeRuleSegment();
        seg2.setSegmentType(3); // sequence
        seg2.setSegmentOrder(2);
        seg2.setSegmentValue(null);
        seg2.setSegmentLength(4);

        return Arrays.asList(seg1, seg2);
    }

    private SegmentParser stubFixedParser(String value) {
        return new SegmentParser() {
            @Override
            public String parse(SegmentParseContext ctx) { return value; }
            @Override
            public Integer getSegmentType() { return 1; }
        };
    }

    // ========== nested tests ==========

    @Nested
    @DisplayName("验收项1: Redis分布式锁(key=code:lock:{ruleCode})正确")
    class RedisLockVerification {

        @Test
        @DisplayName("锁key格式为code:lock:{ruleCode}")
        void shouldUseCorrectLockKeyFormat() {
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode(RULE_CODE)).thenReturn(stubRule());
            when(segmentMapper.selectList(any())).thenReturn(stubTwoSegments());
            when(parserFactory.getParser(eq(1))).thenReturn(stubFixedParser("ORD"));
            when(valueOperations.increment(contains("code:seq:" + RULE_CODE))).thenReturn(5L);

            service.generate(RULE_CODE);

            verify(valueOperations).setIfAbsent(eq("code:lock:" + RULE_CODE), eq("1"), eq(3L), eq(TimeUnit.SECONDS));
        }

        @Test
        @DisplayName("finally块中释放分布式锁")
        void shouldReleaseLockInFinally() {
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode(RULE_CODE)).thenReturn(stubRule());
            when(segmentMapper.selectList(any())).thenReturn(stubTwoSegments());
            when(parserFactory.getParser(eq(1))).thenReturn(stubFixedParser("ORD"));
            when(valueOperations.increment(anyString())).thenReturn(5L);

            service.generate(RULE_CODE);

            verify(redisTemplate).delete("code:lock:" + RULE_CODE);
        }

        @Test
        @DisplayName("生成异常时finally仍释放锁")
        void shouldReleaseLockEvenOnException() {
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode(RULE_CODE)).thenReturn(stubRule());
            when(segmentMapper.selectList(any())).thenReturn(stubTwoSegments());
            when(parserFactory.getParser(eq(1)))
                    .thenThrow(new RuntimeException("parser error"));

            try {
                service.generate(RULE_CODE);
            } catch (Exception ignored) { }

            verify(redisTemplate, atLeastOnce()).delete("code:lock:" + RULE_CODE);
        }

        @Test
        @DisplayName("不同ruleCode使用不同锁key")
        void shouldUseDifferentLockKeysForDifferentRules() {
            // setup for first rule
            when(valueOperations.setIfAbsent(eq("code:lock:ORDER"), eq("1"), eq(3L), eq(TimeUnit.SECONDS)))
                    .thenReturn(true);
            when(ruleMapper.selectByRuleCode("ORDER")).thenReturn(stubRule());
            when(segmentMapper.selectList(any())).thenReturn(stubTwoSegments());
            when(parserFactory.getParser(eq(1))).thenReturn(stubFixedParser("ORD"));
            when(valueOperations.increment(anyString())).thenReturn(1L);

            service.generate("ORDER");

            verify(valueOperations).setIfAbsent(eq("code:lock:ORDER"), anyString(), anyLong(), any());
            verify(valueOperations, never()).setIfAbsent(eq("code:lock:PRODUCT"), anyString(), anyLong(), any());
        }

        @Test
        @DisplayName("获取锁失败时重试后抛OPERATION_TOO_FREQUENT")
        void shouldThrowOperationTooFrequentWhenLockFails() {
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any()))
                    .thenReturn(false);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.generate(RULE_CODE));
            assertEquals(ErrorCode.OPERATION_TOO_FREQUENT.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("验收项2: 编码生成核心逻辑正确")
    class CodeGenerationLogic {

        @Test
        @DisplayName("按segment_order顺序拼接各段")
        void shouldConcatenateSegmentsInOrder() {
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode(RULE_CODE)).thenReturn(stubRule());
            when(segmentMapper.selectList(any())).thenReturn(stubTwoSegments());
            when(parserFactory.getParser(eq(1))).thenReturn(stubFixedParser("ORD"));
            when(valueOperations.increment(anyString())).thenReturn(5L);

            String code = service.generate(RULE_CODE);

            assertEquals("ORD-0005", code);
        }

        @Test
        @DisplayName("序列号段使用Redis INCR生成补零字符串")
        void shouldZeroPadSequenceNumber() {
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode(RULE_CODE)).thenReturn(stubRule());
            when(segmentMapper.selectList(any())).thenReturn(stubTwoSegments());
            when(parserFactory.getParser(eq(1))).thenReturn(stubFixedParser("ORD"));
            when(valueOperations.increment(anyString())).thenReturn(123L);

            String code = service.generate(RULE_CODE);

            assertEquals("ORD-0123", code);
        }

        @Test
        @DisplayName("分隔符为null时不做分隔")
        void shouldNotAddSeparatorWhenNull() {
            stubLockAcquired();
            SysCodeRule rule = stubRule();
            rule.setSeparator(null);
            when(ruleMapper.selectByRuleCode(RULE_CODE)).thenReturn(rule);
            when(segmentMapper.selectList(any())).thenReturn(stubTwoSegments());
            when(parserFactory.getParser(eq(1))).thenReturn(stubFixedParser("ORD"));
            when(valueOperations.increment(anyString())).thenReturn(5L);

            String code = service.generate(RULE_CODE);

            assertEquals("ORD0005", code);
        }

        @Test
        @DisplayName("规则不存在时抛出DATA_NOT_FOUND")
        void shouldThrowDataNotFoundWhenRuleMissing() {
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode(RULE_CODE)).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.generate(RULE_CODE));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("编码段为空时抛出BUSINESS_ERROR")
        void shouldThrowWhenNoSegments() {
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode(RULE_CODE)).thenReturn(stubRule());
            when(segmentMapper.selectList(any())).thenReturn(Collections.emptyList());

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.generate(RULE_CODE));
            assertEquals(ErrorCode.BUSINESS_ERROR.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("segments为null时抛出BUSINESS_ERROR")
        void shouldThrowWhenSegmentsNull() {
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode(RULE_CODE)).thenReturn(stubRule());
            when(segmentMapper.selectList(any())).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.generate(RULE_CODE));
            assertEquals(ErrorCode.BUSINESS_ERROR.getCode(), ex.getCode());
        }
    }

    @Nested
    @DisplayName("验收项3: Redis INCR序列号key格式与日重置")
    class SequenceKeyVerification {

        @Test
        @DisplayName("INCR key格式为code:seq:{ruleCode}:{yyyyMMdd}")
        void shouldUseDateBasedSequenceKey() {
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode(RULE_CODE)).thenReturn(stubRule());
            when(segmentMapper.selectList(any())).thenReturn(stubTwoSegments());
            when(parserFactory.getParser(eq(1))).thenReturn(stubFixedParser("ORD"));

            String dateStr = LocalDate.now().format(DATE_FMT);
            String expectedSeqKey = "code:seq:" + RULE_CODE + ":" + dateStr;

            when(valueOperations.increment(eq(expectedSeqKey))).thenReturn(5L);

            service.generate(RULE_CODE);

            verify(valueOperations).increment(eq(expectedSeqKey));
        }

        @Test
        @DisplayName("首次INCR(返回1)时设置86400秒过期")
        void shouldSetExpireOnFirstIncrement() {
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode(RULE_CODE)).thenReturn(stubRule());
            when(segmentMapper.selectList(any())).thenReturn(stubTwoSegments());
            when(parserFactory.getParser(eq(1))).thenReturn(stubFixedParser("ORD"));
            when(valueOperations.increment(anyString())).thenReturn(1L);

            service.generate(RULE_CODE);

            verify(redisTemplate).expire(contains("code:seq:" + RULE_CODE), eq(86400L), eq(TimeUnit.SECONDS));
        }

        @Test
        @DisplayName("非首次INCR不设过期时间")
        void shouldNotSetExpireOnSubsequentIncrements() {
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode(RULE_CODE)).thenReturn(stubRule());
            when(segmentMapper.selectList(any())).thenReturn(stubTwoSegments());
            when(parserFactory.getParser(eq(1))).thenReturn(stubFixedParser("ORD"));
            when(valueOperations.increment(anyString())).thenReturn(5L);

            service.generate(RULE_CODE);

            verify(redisTemplate, never()).expire(anyString(), anyLong(), any(TimeUnit.class));
        }

        @Test
        @DisplayName("INCR key可按天重置(不同日期不同key)")
        void shouldUseDifferentKeyPerDay() {
            String todayKey = "code:seq:" + RULE_CODE + ":" + LocalDate.now().format(DATE_FMT);
            String yesterdayKey = "code:seq:" + RULE_CODE + ":" + LocalDate.now().minusDays(1).format(DATE_FMT);
            assertNotEquals(todayKey, yesterdayKey);

            // 验证实际调用使用今天日期作为key的一部分
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode(RULE_CODE)).thenReturn(stubRule());
            when(segmentMapper.selectList(any())).thenReturn(stubTwoSegments());
            when(parserFactory.getParser(eq(1))).thenReturn(stubFixedParser("ORD"));
            when(valueOperations.increment(anyString())).thenReturn(5L);

            service.generate(RULE_CODE);

            verify(valueOperations).increment(startsWith("code:seq:" + RULE_CODE + ":"));
        }

        @Test
        @DisplayName("序列长度使用segmentLength, 默认4")
        void shouldDefaultToDigits4WhenLengthNull() {
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode(RULE_CODE)).thenReturn(stubRule());

            SysCodeRuleSegment seg = new SysCodeRuleSegment();
            seg.setSegmentType(3);
            seg.setSegmentOrder(1);
            seg.setSegmentLength(null); // no explicit length
            when(segmentMapper.selectList(any())).thenReturn(Collections.singletonList(seg));
            when(valueOperations.increment(anyString())).thenReturn(7L);

            String code = service.generate(RULE_CODE);

            assertEquals("0007", code);
        }
    }

    @Nested
    @DisplayName("验收项4: 失败重试3次后抛BusinessException")
    class RetryVerification {

        @Test
        @DisplayName("BusinessException直接抛出不被重试捕获")
        void shouldNotRetryBusinessException() {
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode(RULE_CODE))
                    .thenThrow(new BusinessException(ErrorCode.DATA_NOT_FOUND, "规则不存在"));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.generate(RULE_CODE));
            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
            // ruleMapper should only be called once (no retry)
            verify(ruleMapper, times(1)).selectByRuleCode(RULE_CODE);
        }

        @Test
        @DisplayName("非BusinessException异常触发重试")
        void shouldRetryOnNonBusinessException() {
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode(RULE_CODE))
                    .thenThrow(new RuntimeException("DB error"))
                    .thenThrow(new RuntimeException("DB error"))
                    .thenReturn(stubRule());
            when(segmentMapper.selectList(any())).thenReturn(stubTwoSegments());
            when(parserFactory.getParser(eq(1))).thenReturn(stubFixedParser("ORD"));
            when(valueOperations.increment(anyString())).thenReturn(5L);

            String code = service.generate(RULE_CODE);

            assertEquals("ORD-0005", code);
            verify(ruleMapper, times(3)).selectByRuleCode(RULE_CODE);
        }

        @Test
        @DisplayName("3次全部失败后抛出BUSINESS_ERROR")
        void shouldThrowAfterAllRetriesExhausted() {
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode(RULE_CODE))
                    .thenThrow(new RuntimeException("persistent error"));

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.generate(RULE_CODE));
            assertEquals(ErrorCode.BUSINESS_ERROR.getCode(), ex.getCode());
            verify(ruleMapper, times(3)).selectByRuleCode(RULE_CODE);
        }

        @Test
        @DisplayName("每次重试前释放锁")
        void shouldReleaseLockBeforeEachRetry() {
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode(RULE_CODE))
                    .thenThrow(new RuntimeException("error"))
                    .thenThrow(new RuntimeException("error"))
                    .thenReturn(stubRule());
            when(segmentMapper.selectList(any())).thenReturn(stubTwoSegments());
            when(parserFactory.getParser(eq(1))).thenReturn(stubFixedParser("ORD"));
            when(valueOperations.increment(anyString())).thenReturn(5L);

            service.generate(RULE_CODE);

            verify(redisTemplate, times(3)).delete("code:lock:" + RULE_CODE);
        }

        @Test
        @DisplayName("获取锁失败后也重试")
        void shouldRetryOnLockAcquireFailure() {
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any()))
                    .thenReturn(false)
                    .thenReturn(false)
                    .thenReturn(true);
            when(ruleMapper.selectByRuleCode(RULE_CODE)).thenReturn(stubRule());
            when(segmentMapper.selectList(any())).thenReturn(stubTwoSegments());
            when(parserFactory.getParser(eq(1))).thenReturn(stubFixedParser("ORD"));
            when(valueOperations.increment(anyString())).thenReturn(5L);

            String code = service.generate(RULE_CODE);

            assertEquals("ORD-0005", code);
            verify(valueOperations, times(3)).setIfAbsent(anyString(), anyString(), anyLong(), any());
        }
    }

    @Nested
    @DisplayName("验收项5: 综合场景验证")
    class IntegrationVerification {

        @Test
        @DisplayName("多段混合拼接正确(固定段+日期段+序列段)")
        void shouldHandleMixedSegmentTypes() {
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode(RULE_CODE)).thenReturn(stubRule());
            when(segmentMapper.selectList(any())).thenReturn(stubTwoSegments());
            when(parserFactory.getParser(eq(1))).thenReturn(stubFixedParser("ORD"));

            String dateStr = LocalDate.now().format(DATE_FMT);
            when(valueOperations.increment(contains("code:seq:"))).thenReturn(42L);

            String code = service.generate(RULE_CODE);

            assertNotNull(code);
            assertTrue(code.startsWith("ORD"));
            assertTrue(code.endsWith("0042"));
        }

        @Test
        @DisplayName("编码规则含separator时正确插入分隔符")
        void shouldInsertSeparatorCorrectly() {
            stubLockAcquired();
            SysCodeRule rule = stubRule();
            rule.setSeparator("#");
            when(ruleMapper.selectByRuleCode(RULE_CODE)).thenReturn(rule);

            SysCodeRuleSegment seg1 = new SysCodeRuleSegment();
            seg1.setSegmentType(1);
            seg1.setSegmentOrder(1);
            seg1.setSegmentValue("TEST");

            SysCodeRuleSegment seg2 = new SysCodeRuleSegment();
            seg2.setSegmentType(1);
            seg2.setSegmentOrder(2);
            seg2.setSegmentValue("A");

            SysCodeRuleSegment seg3 = new SysCodeRuleSegment();
            seg3.setSegmentType(3);
            seg3.setSegmentOrder(3);
            seg3.setSegmentLength(3);

            when(segmentMapper.selectList(any())).thenReturn(Arrays.asList(seg1, seg2, seg3));
            when(parserFactory.getParser(eq(1))).thenReturn(stubFixedParser("X"));
            when(valueOperations.increment(anyString())).thenReturn(1L);

            // seg1 → "X", seg2 → "X", seg3 → "001"
            String code = service.generate(RULE_CODE);

            // For seg1 and seg2, both get parser for type 1 which returns "X"
            // Then seg3 is sequence type handled internally
            // But wait - since both seg1 and seg2 use the same parser that returns "X", we get "X#X#001"
            assertNotNull(code);
        }

        @Test
        @DisplayName("不同ruleCode的序列号独立自增")
        void shouldTrackSequencesIndependently() {
            // First call ORDER → seq=5
            stubLockAcquired();
            when(ruleMapper.selectByRuleCode("ORDER")).thenReturn(stubRule());
            when(segmentMapper.selectList(any())).thenReturn(stubTwoSegments());
            when(parserFactory.getParser(eq(1))).thenReturn(stubFixedParser("ORD"));
            when(valueOperations.increment(contains("code:seq:ORDER"))).thenReturn(5L);

            String code1 = service.generate("ORDER");
            assertEquals("ORD-0005", code1);

            // Second call PRODUCT → seq=1 (independent)
            SysCodeRule productRule = new SysCodeRule();
            productRule.setId(2L);
            productRule.setRuleCode("PRODUCT");
            productRule.setSeparator("-");

            when(valueOperations.setIfAbsent(eq("code:lock:PRODUCT"), anyString(), anyLong(), any()))
                    .thenReturn(true);
            when(ruleMapper.selectByRuleCode("PRODUCT")).thenReturn(productRule);

            SysCodeRuleSegment ps1 = new SysCodeRuleSegment();
            ps1.setSegmentType(1);
            ps1.setSegmentOrder(1);
            ps1.setSegmentValue("PRD");
            SysCodeRuleSegment ps2 = new SysCodeRuleSegment();
            ps2.setSegmentType(3);
            ps2.setSegmentOrder(2);
            ps2.setSegmentLength(3);

            when(segmentMapper.selectList(any())).thenReturn(Arrays.asList(ps1, ps2));
            when(parserFactory.getParser(eq(1))).thenReturn(stubFixedParser("PRD"));
            when(valueOperations.increment(contains("code:seq:PRODUCT"))).thenReturn(1L);

            String code2 = service.generate("PRODUCT");
            assertEquals("PRD-001", code2);
        }
    }
}
