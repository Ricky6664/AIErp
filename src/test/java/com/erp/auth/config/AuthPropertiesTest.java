package com.erp.auth.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {AuthProperties.class})
@EnableConfigurationProperties(AuthProperties.class)
@ActiveProfiles("test")
@DisplayName("AuthProperties 配置属性验证")
class AuthPropertiesTest {

    @Autowired
    private AuthProperties authProperties;

    // ======================== Bean注入验证 ========================

    @Test
    @DisplayName("AuthProperties Bean应成功注入")
    void shouldAutowiredAuthProperties() {
        assertNotNull(authProperties);
    }

    @Test
    @DisplayName("嵌套配置对象应非空")
    void shouldHaveNestedConfigObjects() {
        assertNotNull(authProperties.getCaptcha());
        assertNotNull(authProperties.getLoginSecurity());
        assertNotNull(authProperties.getUserInfo());
    }

    // ======================== 默认值绑定验证 ========================

    @Test
    @DisplayName("验证码默认TTL为5分钟")
    void shouldHaveDefaultCaptchaTtl() {
        assertEquals(5, authProperties.getCaptcha().getTtlMinutes());
    }

    @Test
    @DisplayName("验证码默认长度为4")
    void shouldHaveDefaultCaptchaCodeLength() {
        assertEquals(4, authProperties.getCaptcha().getCodeLength());
    }

    @Test
    @DisplayName("登录安全默认最大尝试次数为5")
    void shouldHaveDefaultMaxAttempts() {
        assertEquals(5, authProperties.getLoginSecurity().getMaxAttempts());
    }

    @Test
    @DisplayName("登录安全默认锁定时间为15分钟")
    void shouldHaveDefaultLockMinutes() {
        assertEquals(15, authProperties.getLoginSecurity().getLockMinutes());
    }

    @Test
    @DisplayName("用户信息默认头像为空字符串")
    void shouldHaveDefaultAvatar() {
        assertEquals("", authProperties.getUserInfo().getDefaultAvatar());
    }

    @Test
    @DisplayName("菜单树默认最大深度为5")
    void shouldHaveDefaultMenuTreeMaxDepth() {
        assertEquals(5, authProperties.getUserInfo().getMenuTreeMaxDepth());
    }

    // ======================== 综合绑定验证 ========================

    @Test
    @DisplayName("application.yml中auth配置应全部正确绑定")
    void shouldBindAllPropertiesFromApplicationYml() {
        assertAll("auth config binding",
            () -> assertEquals(5, authProperties.getCaptcha().getTtlMinutes(),
                "captcha.ttl-minutes"),
            () -> assertEquals(4, authProperties.getCaptcha().getCodeLength(),
                "captcha.code-length"),
            () -> assertEquals(5, authProperties.getLoginSecurity().getMaxAttempts(),
                "login-security.max-attempts"),
            () -> assertEquals(15, authProperties.getLoginSecurity().getLockMinutes(),
                "login-security.lock-minutes"),
            () -> assertEquals("", authProperties.getUserInfo().getDefaultAvatar(),
                "user-info.default-avatar"),
            () -> assertEquals(5, authProperties.getUserInfo().getMenuTreeMaxDepth(),
                "user-info.menu-tree-max-depth")
        );
    }

    // ======================== @Validated 校验验证 ========================

    private final Validator validator;

    AuthPropertiesTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("ttlMinutes=0时应触发校验错误")
    void shouldRejectZeroTtl() {
        AuthProperties.Captcha captcha = new AuthProperties.Captcha();
        captcha.setTtlMinutes(0);
        var violations = validator.validate(captcha);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("ttl-minutes")));
    }

    @Test
    @DisplayName("ttlMinutes=1且codeLength=4时应通过校验")
    void shouldAcceptValidCaptcha() {
        AuthProperties.Captcha captcha = new AuthProperties.Captcha();
        captcha.setTtlMinutes(1);
        captcha.setCodeLength(4);
        var violations = validator.validate(captcha);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("codeLength=3时应触发校验错误")
    void shouldRejectTooShortCode() {
        AuthProperties.Captcha captcha = new AuthProperties.Captcha();
        captcha.setTtlMinutes(5);
        captcha.setCodeLength(3);
        var violations = validator.validate(captcha);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("code-length")));
    }

    @Test
    @DisplayName("maxAttempts=0时应触发校验错误")
    void shouldRejectZeroMaxAttempts() {
        AuthProperties.LoginSecurity ls = new AuthProperties.LoginSecurity();
        ls.setMaxAttempts(0);
        ls.setLockMinutes(15);
        var violations = validator.validate(ls);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("max-attempts")));
    }

    @Test
    @DisplayName("lockMinutes=0时应触发校验错误")
    void shouldRejectZeroLockMinutes() {
        AuthProperties.LoginSecurity ls = new AuthProperties.LoginSecurity();
        ls.setMaxAttempts(5);
        ls.setLockMinutes(0);
        var violations = validator.validate(ls);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("lock-minutes")));
    }

    @Test
    @DisplayName("menuTreeMaxDepth=0时应触发校验错误")
    void shouldRejectZeroMenuDepth() {
        AuthProperties.UserInfo ui = new AuthProperties.UserInfo();
        ui.setMenuTreeMaxDepth(0);
        var violations = validator.validate(ui);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("menu-tree-max-depth")));
    }
}
