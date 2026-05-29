package com.erp.system.codegen;

import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.BusinessException;
import com.erp.system.entity.SysCodeRule;
import com.erp.system.mapper.SysCodeRuleMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * SequenceGenerator 验证测试.
 *
 * @author AI
 * @since 2026-05-29
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SequenceGenerator - 序列号生成器验证")
class SequenceGeneratorTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private SysCodeRuleMapper sysCodeRuleMapper;

    private SequenceGenerator generator;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        generator = new SequenceGenerator(redisTemplate, applicationContext);
    }

    @Nested
    @DisplayName("验收项1: Redis分布式锁key=code:lock:{ruleCode}正确")
    class RedisLockKeyVerification {

        @Test
        @DisplayName("锁key格式为code:lock:{ruleCode}")
        void shouldUseCorrectLockKeyFormat() {
            String expectedLockKey = "code:lock:RULE001";
            when(valueOperations.setIfAbsent(eq(expectedLockKey), anyString(), eq(10L), eq(TimeUnit.SECONDS)))
                    .thenReturn(true);
            when(valueOperations.increment(anyString())).thenReturn(5L);
            when(redisTemplate.delete(eq(expectedLockKey))).thenReturn(true);

            long result = generator.next("RULE001");

            assertEquals(5L, result);
            verify(valueOperations).setIfAbsent(eq(expectedLockKey), eq("1"), eq(10L), eq(TimeUnit.SECONDS));
            verify(redisTemplate).delete(eq(expectedLockKey));
        }

        @Test
        @DisplayName("INCR key格式为code:seq:{ruleCode}:{yyyyMMdd}")
        void shouldUseCorrectIncrKeyFormat() {
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String expectedSeqKey = "code:seq:RULE001:" + dateStr;

            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any()))
                    .thenReturn(true);
            when(valueOperations.increment(eq(expectedSeqKey))).thenReturn(1L);
            when(redisTemplate.delete(anyString())).thenReturn(true);

            generator.next("RULE001");

            verify(valueOperations).increment(eq(expectedSeqKey));
        }

        @Test
        @DisplayName("首次INCR时设置86400秒过期")
        void shouldSetExpireOnFirstIncrement() {
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any()))
                    .thenReturn(true);
            when(valueOperations.increment(anyString())).thenReturn(1L);
            when(redisTemplate.expire(anyString(), eq(86400L), eq(TimeUnit.SECONDS))).thenReturn(true);
            when(redisTemplate.delete(anyString())).thenReturn(true);

            generator.next("RULE001");

            verify(redisTemplate).expire(contains("code:seq:RULE001"), eq(86400L), eq(TimeUnit.SECONDS));
        }

        @Test
        @DisplayName("非首次INCR不设置过期时间")
        void shouldNotSetExpireOnSubsequentIncrements() {
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any()))
                    .thenReturn(true);
            when(valueOperations.increment(anyString())).thenReturn(5L);
            when(redisTemplate.delete(anyString())).thenReturn(true);

            generator.next("RULE001");

            verify(redisTemplate, never()).expire(anyString(), anyLong(), any(TimeUnit.class));
        }

        @Test
        @DisplayName("finally块中释放分布式锁")
        void shouldReleaseLockInFinally() {
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any()))
                    .thenReturn(true);
            when(valueOperations.increment(anyString())).thenThrow(new RuntimeException("Redis error"));
            when(redisTemplate.delete(anyString())).thenReturn(true);
            when(applicationContext.getBean(SysCodeRuleMapper.class)).thenReturn(sysCodeRuleMapper);
            when(sysCodeRuleMapper.selectByRuleCode(anyString())).thenReturn(null);

            try {
                generator.next("RULE001");
            } catch (BusinessException ignored) {
            }

            verify(redisTemplate).delete(contains("code:lock:RULE001"));
        }

        @Test
        @DisplayName("分布式锁获取失败时抛出OPERATION_TOO_FREQUENT")
        void shouldThrowWhenLockAcquireFails() {
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any()))
                    .thenReturn(false);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> generator.next("RULE001"));
            assertEquals(ErrorCode.OPERATION_TOO_FREQUENT.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("不同ruleCode使用不同的锁key避免相互阻塞")
        void shouldUseDifferentLockKeysForDifferentRules() {
            String lockKeyA = "code:lock:ORDER";
            String lockKeyB = "code:lock:PRODUCT";

            when(valueOperations.setIfAbsent(eq(lockKeyA), anyString(), anyLong(), any()))
                    .thenReturn(true);
            when(valueOperations.increment(anyString())).thenReturn(1L);
            when(redisTemplate.delete(anyString())).thenReturn(true);

            generator.next("ORDER");

            verify(valueOperations).setIfAbsent(eq(lockKeyA), anyString(), anyLong(), any());
            verify(valueOperations, never()).setIfAbsent(eq(lockKeyB), anyString(), anyLong(), any());
        }
    }

    @Nested
    @DisplayName("验收项2: Redis INCR原子自增性能验证")
    class RedisIncrPerformanceVerification {

        @Test
        @DisplayName("连续多次INCR返回递增序列")
        void shouldReturnIncreasingSequence() {
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any()))
                    .thenReturn(true);
            when(valueOperations.increment(anyString())).thenReturn(1L, 2L, 3L, 10L);
            when(redisTemplate.delete(anyString())).thenReturn(true);

            assertEquals(1L, generator.next("RULE001"));
            assertEquals(2L, generator.next("RULE001"));
            assertEquals(3L, generator.next("RULE001"));
            assertEquals(10L, generator.next("RULE001"));
        }

        @Test
        @DisplayName("getNext返回指定位数补零字符串")
        void shouldReturnZeroPaddedString() {
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any()))
                    .thenReturn(true);
            when(valueOperations.increment(anyString())).thenReturn(5L);
            when(redisTemplate.delete(anyString())).thenReturn(true);

            String result = generator.getNext("RULE001", 6);

            assertEquals("000005", result);
        }
    }

    @Nested
    @DisplayName("验收项3: 失败重试3次后抛BusinessException")
    class DbFallbackRetryVerification {

        @Test
        @DisplayName("Redis异常时降级到DB乐观锁")
        void shouldFallbackToDbWhenRedisFails() {
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any()))
                    .thenReturn(true);
            when(valueOperations.increment(anyString())).thenThrow(new RuntimeException("Redis down"));
            when(redisTemplate.delete(anyString())).thenReturn(true);
            when(applicationContext.getBean(SysCodeRuleMapper.class)).thenReturn(sysCodeRuleMapper);

            SysCodeRule rule = mockRule(1L, "RULE001", 10L);
            when(sysCodeRuleMapper.selectByRuleCode("RULE001")).thenReturn(rule);
            when(sysCodeRuleMapper.updateCurrentVersion(eq(1L), eq(11L), eq(10L))).thenReturn(1);

            long result = generator.next("RULE001");

            assertEquals(11L, result);
            verify(sysCodeRuleMapper).updateCurrentVersion(1L, 11L, 10L);
        }

        @Test
        @DisplayName("DB乐观锁冲突时重试最多3次")
        void shouldRetryOnOptimisticLockConflict() {
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any()))
                    .thenReturn(true);
            when(valueOperations.increment(anyString())).thenThrow(new RuntimeException("Redis down"));
            when(redisTemplate.delete(anyString())).thenReturn(true);
            when(applicationContext.getBean(SysCodeRuleMapper.class)).thenReturn(sysCodeRuleMapper);

            SysCodeRule rule = mockRule(1L, "RULE001", 0L);
            when(sysCodeRuleMapper.selectByRuleCode("RULE001")).thenReturn(rule);
            when(sysCodeRuleMapper.updateCurrentVersion(eq(1L), eq(1L), eq(0L)))
                    .thenReturn(0, 0, 1);

            long result = generator.next("RULE001");

            assertEquals(1L, result);
            verify(sysCodeRuleMapper, times(3)).updateCurrentVersion(anyLong(), anyLong(), anyLong());
        }

        @Test
        @DisplayName("DB重试3次全部失败后抛出OPERATION_TOO_FREQUENT")
        void shouldThrowAfter3DbRetries() {
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any()))
                    .thenReturn(true);
            when(valueOperations.increment(anyString())).thenThrow(new RuntimeException("Redis down"));
            when(redisTemplate.delete(anyString())).thenReturn(true);
            when(applicationContext.getBean(SysCodeRuleMapper.class)).thenReturn(sysCodeRuleMapper);

            SysCodeRule rule = mockRule(1L, "RULE001", 0L);
            when(sysCodeRuleMapper.selectByRuleCode("RULE001")).thenReturn(rule);
            when(sysCodeRuleMapper.updateCurrentVersion(anyLong(), anyLong(), anyLong())).thenReturn(0);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> generator.next("RULE001"));

            assertEquals(ErrorCode.OPERATION_TOO_FREQUENT.getCode(), ex.getCode());
            assertTrue(ex.getMessage().contains("操作过于频繁"));
            verify(sysCodeRuleMapper, times(3)).updateCurrentVersion(anyLong(), anyLong(), anyLong());
        }

        @Test
        @DisplayName("DB降级时编码规则不存在抛出DATA_NOT_FOUND")
        void shouldThrowWhenRuleNotFoundInDb() {
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any()))
                    .thenReturn(true);
            when(valueOperations.increment(anyString())).thenThrow(new RuntimeException("Redis down"));
            when(redisTemplate.delete(anyString())).thenReturn(true);
            when(applicationContext.getBean(SysCodeRuleMapper.class)).thenReturn(sysCodeRuleMapper);
            when(sysCodeRuleMapper.selectByRuleCode("RULE001")).thenReturn(null);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> generator.next("RULE001"));

            assertEquals(ErrorCode.DATA_NOT_FOUND.getCode(), ex.getCode());
        }

        @Test
        @DisplayName("DB降级currentValue为null时从0开始")
        void shouldStartFromZeroWhenCurrentValueNull() {
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any()))
                    .thenReturn(true);
            when(valueOperations.increment(anyString())).thenThrow(new RuntimeException("Redis down"));
            when(redisTemplate.delete(anyString())).thenReturn(true);
            when(applicationContext.getBean(SysCodeRuleMapper.class)).thenReturn(sysCodeRuleMapper);

            SysCodeRule rule = mockRule(1L, "RULE001", null);
            when(sysCodeRuleMapper.selectByRuleCode("RULE001")).thenReturn(rule);
            when(sysCodeRuleMapper.updateCurrentVersion(eq(1L), eq(1L), isNull())).thenReturn(1);

            long result = generator.next("RULE001");

            assertEquals(1L, result);
        }
    }

    @Nested
    @DisplayName("综合验证")
    class IntegrationVerification {

        @Test
        @DisplayName("Redis INCR原子操作线程安全（模拟并发自增100次）")
        void shouldBeThreadSafeWithAtomicIncrement() {
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any()))
                    .thenReturn(true);
            Long[] values = new Long[100];
            for (int i = 0; i < values.length; i++) {
                values[i] = (long) (i + 1);
            }
            when(valueOperations.increment(anyString())).thenReturn(values[0], java.util.Arrays.copyOfRange(values, 1, values.length));
            when(redisTemplate.delete(anyString())).thenReturn(true);

            for (int i = 0; i < 100; i++) {
                long seq = generator.next("RULE001");
                assertEquals(i + 1, seq);
            }
        }

        @Test
        @DisplayName("BusinessException直接从Redis路径抛出不被DB降级捕获")
        void shouldNotCatchBusinessExceptionFromRedis() {
            when(valueOperations.setIfAbsent(anyString(), anyString(), anyLong(), any()))
                    .thenReturn(true);
            when(valueOperations.increment(anyString()))
                    .thenThrow(new BusinessException(ErrorCode.BUSINESS_ERROR));
            when(redisTemplate.delete(anyString())).thenReturn(true);

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> generator.next("RULE001"));

            assertEquals(ErrorCode.BUSINESS_ERROR.getCode(), ex.getCode());
            verify(applicationContext, never()).getBean(any(Class.class));
        }
    }

    private SysCodeRule mockRule(Long id, String ruleCode, Long currentValue) {
        SysCodeRule rule = new SysCodeRule();
        rule.setId(id);
        rule.setRuleCode(ruleCode);
        rule.setCurrentValue(currentValue);
        return rule;
    }
}
