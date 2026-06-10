package com.erp.auth.service;

import com.erp.auth.exception.CaptchaException;
import com.erp.auth.vo.CaptchaVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
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
     * 生成图形验证码(含Base64图片).
     *
     * @return CaptchaVO 含captchaKey和base64图片
     */
    public CaptchaVO generateCaptchaImage() {
        String captchaKey = UUID.randomUUID().toString().replace("-", "");
        String captchaCode = generateRandomCode(4);
        String redisKey = CAPTCHA_PREFIX + captchaKey;
        redisTemplate.opsForValue().set(redisKey, captchaCode, CAPTCHA_TTL, CAPTCHA_TTL_UNIT);
        String base64Image = drawCaptchaImage(captchaCode);
        log.info("验证码: key={}, code={}", captchaKey, captchaCode);
        return CaptchaVO.builder()
                .captchaKey(captchaKey)
                .captchaImage(base64Image)
                .build();
    }

    private String drawCaptchaImage(String code) {
        int width = 120;
        int height = 40;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // 背景
            g.setColor(new Color(240, 240, 240));
            g.fillRect(0, 0, width, height);

            // 干扰线 5条
            for (int i = 0; i < 5; i++) {
                g.setColor(new Color(randomInt(50, 180), randomInt(50, 180), randomInt(50, 180)));
                g.drawLine(randomInt(0, width), randomInt(0, height),
                        randomInt(0, width), randomInt(0, height));
            }

            // 噪点 100个
            for (int i = 0; i < 100; i++) {
                g.setColor(new Color(randomInt(100, 200), randomInt(100, 200), randomInt(100, 200)));
                g.fillRect(randomInt(0, width), randomInt(0, height), 1, 1);
            }

            // 文字 每个字符随机颜色、位置偏移、旋转
            Font font = new Font("Arial", Font.BOLD, 22);
            g.setFont(font);
            for (int i = 0; i < code.length(); i++) {
                g.setColor(new Color(randomInt(20, 120), randomInt(20, 120), randomInt(20, 120)));
                int x = 10 + i * 26 + randomInt(-3, 3);
                int y = 28 + randomInt(-3, 3);
                double angle = (randomInt(-30, 30) * Math.PI) / 180.0;
                g.rotate(angle, x, y);
                g.drawString(String.valueOf(code.charAt(i)), x, y);
                g.rotate(-angle, x, y);
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("验证码图片生成失败", e);
        } finally {
            g.dispose();
        }
    }

    private int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
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

    /**
     * [DEV] 获取验证码文本（仅开发环境使用）.
     */
    public String getCaptchaCode(String captchaKey) {
        String redisKey = CAPTCHA_PREFIX + captchaKey;
        String storedCode = redisTemplate.opsForValue().get(redisKey);
        return storedCode != null ? storedCode : "已过期";
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
