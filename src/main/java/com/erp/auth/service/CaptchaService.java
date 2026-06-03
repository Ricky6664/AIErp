package com.erp.auth.service;

import com.erp.auth.exception.CaptchaException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 图形验证码服务.
 *
 * <p>验证码存储在Redis中, TTL=5分钟, 校验通过后立即删除（一次性消费）.</p>
 *
 * @author AI
 * @since 2026-06-03
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private static final String CAPTCHA_PREFIX = "captcha:";
    private static final long CAPTCHA_TTL = 5;
    private static final TimeUnit CAPTCHA_TTL_UNIT = TimeUnit.MINUTES;

    private final StringRedisTemplate redisTemplate;

    /**
     * 生成图形验证码并存储到Redis.
     *
     * @return captchaKey 验证码标识
     */
    public String generateCaptcha() {
        String captchaKey = UUID.randomUUID().toString().replace("-", "");
        String captchaCode = generateRandomCode(4);
        String redisKey = CAPTCHA_PREFIX + captchaKey;
        redisTemplate.opsForValue().set(redisKey, captchaCode, CAPTCHA_TTL, CAPTCHA_TTL_UNIT);
        log.debug("验证码已生成: key={}", captchaKey);
        return captchaKey;
    }

    /**
     * 校验验证码, 校验通过后立即删除（一次性消费）.
     *
     * @param captchaKey  验证码标识
     * @param captchaCode 用户输入的验证码
     * @throws CaptchaException 验证码错误或已过期
     */
    public void verifyCaptcha(String captchaKey, String captchaCode) {
        if (captchaKey == null || captchaKey.isBlank()) {
            throw new CaptchaException();
        }
        if (captchaCode == null || captchaCode.isBlank()) {
            throw new CaptchaException();
        }
        String redisKey = CAPTCHA_PREFIX + captchaKey;
        String storedCode = redisTemplate.opsForValue().get(redisKey);
        if (storedCode == null) {
            throw new CaptchaException();
        }
        if (!storedCode.equalsIgnoreCase(captchaCode)) {
            throw new CaptchaException();
        }
        redisTemplate.delete(redisKey);
        log.debug("验证码校验通过并已删除: key={}", captchaKey);
    }

    private String generateRandomCode(int length) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return sb.toString();
    }
}
