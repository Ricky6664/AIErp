package com.erp.auth.service;

import com.erp.auth.exception.CaptchaException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CaptchaService 单元测试")
class CaptchaServiceTest {

    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private CaptchaService captchaService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Nested
    @DisplayName("验证码生成")
    class GenerateCaptcha {

        @Test
        @DisplayName("生成验证码 → 返回非空key且存储到Redis")
        void shouldGenerateCaptchaAndStoreInRedis() {
            String key = captchaService.generateCaptcha();

            assertNotNull(key);
            assertFalse(key.isBlank());
            assertFalse(key.contains("-"), "key不应包含横线");
            verify(valueOperations).set(startsWith("captcha:" + key), anyString(), eq(5L), eq(TimeUnit.MINUTES));
        }

        @Test
        @DisplayName("生成验证码 → Key为32位十六进制字符串")
        void shouldGenerate32CharHexKey() {
            String key = captchaService.generateCaptcha();

            assertEquals(32, key.length());
            assertTrue(key.matches("[0-9a-f]{32}"), "key应为32位十六进制");
        }
    }

    @Nested
    @DisplayName("验证码校验成功")
    class VerifySuccess {

        @Test
        @DisplayName("验证码匹配 → 校验通过并删除Redis记录(一次性消费)")
        void shouldVerifyAndDeleteWhenCorrect() {
            when(valueOperations.get("captcha:key123")).thenReturn("ABCD");

            captchaService.verifyCaptcha("key123", "ABCD");

            verify(redisTemplate).delete("captcha:key123");
        }

        @Test
        @DisplayName("验证码大小写不敏感校验")
        void shouldVerifyCaseInsensitive() {
            when(valueOperations.get("captcha:key123")).thenReturn("ABCD");

            assertDoesNotThrow(() -> captchaService.verifyCaptcha("key123", "abcd"));
            verify(redisTemplate).delete("captcha:key123");
        }
    }

    @Nested
    @DisplayName("验证码校验失败")
    class VerifyFailure {

        @Test
        @DisplayName("验证码不匹配 → 抛出CaptchaException且不删除Redis")
        void shouldThrowExceptionWhenCodeMismatch() {
            when(valueOperations.get("captcha:key123")).thenReturn("ABCD");

            assertThrows(CaptchaException.class,
                    () -> captchaService.verifyCaptcha("key123", "WRONG"));
            verify(redisTemplate, never()).delete(anyString());
        }

        @Test
        @DisplayName("验证码已过期(Redis中不存在) → 抛出CaptchaException")
        void shouldThrowExceptionWhenExpired() {
            when(valueOperations.get("captcha:key123")).thenReturn(null);

            assertThrows(CaptchaException.class,
                    () -> captchaService.verifyCaptcha("key123", "ABCD"));
        }

        @Test
        @DisplayName("captchaKey为null → 抛出CaptchaException")
        void shouldThrowExceptionWhenKeyIsNull() {
            assertThrows(CaptchaException.class,
                    () -> captchaService.verifyCaptcha(null, "ABCD"));
        }

        @Test
        @DisplayName("captchaKey为空字符串 → 抛出CaptchaException")
        void shouldThrowExceptionWhenKeyIsBlank() {
            assertThrows(CaptchaException.class,
                    () -> captchaService.verifyCaptcha("   ", "ABCD"));
        }

        @Test
        @DisplayName("captchaCode为null → 抛出CaptchaException")
        void shouldThrowExceptionWhenCodeIsNull() {
            assertThrows(CaptchaException.class,
                    () -> captchaService.verifyCaptcha("key123", null));
        }

        @Test
        @DisplayName("captchaCode为空字符串 → 抛出CaptchaException")
        void shouldThrowExceptionWhenCodeIsBlank() {
            assertThrows(CaptchaException.class,
                    () -> captchaService.verifyCaptcha("key123", ""));
        }
    }

    @Nested
    @DisplayName("一次性消费验证")
    class OneTimeConsumption {

        @Test
        @DisplayName("校验通过后再次校验 → 第二次抛出CaptchaException(因为已删除)")
        void shouldFailOnSecondVerificationBecauseDeleted() {
            when(valueOperations.get("captcha:key123")).thenReturn("ABCD").thenReturn(null);

            assertDoesNotThrow(() -> captchaService.verifyCaptcha("key123", "ABCD"));

            assertThrows(CaptchaException.class,
                    () -> captchaService.verifyCaptcha("key123", "ABCD"));
        }
    }
}
