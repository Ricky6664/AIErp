package com.erp.auth.config;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 认证模块配置属性.
 *
 * <p>绑定 application.yml 中 auth.* 配置项, 提供类型安全的配置访问.
 * 在应用启动时自动执行 @Validated 校验, 配置错误会导致启动失败.</p>
 *
 * <p>配置项说明:
 * <ul>
 *   <li>auth.captcha.ttl-minutes — 验证码 Redis 缓存有效期（分钟），默认 5</li>
 *   <li>auth.captcha.code-length — 验证码字符长度，默认 4</li>
 *   <li>auth.login-security.max-attempts — 最大登录失败次数，超过后锁定，默认 5</li>
 *   <li>auth.login-security.lock-minutes — 登录失败锁定时间（分钟），默认 15</li>
 *   <li>auth.user-info.default-avatar — 用户未设置头像时的默认头像 URL</li>
 *   <li>auth.user-info.menu-tree-max-depth — 用户信息接口返回的菜单权限树最大深度，默认 5</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    private Captcha captcha = new Captcha();

    private LoginSecurity loginSecurity = new LoginSecurity();

    private UserInfo userInfo = new UserInfo();

    @Data
    public static class Captcha {

        @Min(value = 1, message = "auth.captcha.ttl-minutes 必须大于 0")
        private int ttlMinutes = 5;

        @Min(value = 4, message = "auth.captcha.code-length 至少为 4")
        private int codeLength = 4;
    }

    @Data
    public static class LoginSecurity {

        @Min(value = 1, message = "auth.login-security.max-attempts 必须大于 0")
        private int maxAttempts = 5;

        @Min(value = 1, message = "auth.login-security.lock-minutes 必须大于 0")
        private int lockMinutes = 15;
    }

    @Data
    public static class UserInfo {

        private String defaultAvatar = "";

        @Min(value = 1, message = "auth.user-info.menu-tree-max-depth 必须大于 0")
        private int menuTreeMaxDepth = 5;
    }
}
