package com.erp.approval.config;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ApprovalUrgeProperties.class})
@ActiveProfiles("test")
@DisplayName("ApprovalUrgeProperties 配置属性验证")
class ApprovalUrgePropertiesTest {

    @Autowired
    private ApprovalUrgeProperties properties;

    // ======================== Bean注入验证 ========================

    @Test
    @DisplayName("ApprovalUrgeProperties Bean应成功注入")
    void shouldAutowiredProperties() {
        assertNotNull(properties);
    }

    @Test
    @DisplayName("嵌套配置对象应非空")
    void shouldHaveNestedConfigObjects() {
        assertNotNull(properties.getMethods());
        assertNotNull(properties.getFrequency());
        assertNotNull(properties.getAuto());
        assertNotNull(properties.getLimits());
    }

    // ======================== 默认值绑定验证 ========================

    @Test
    @DisplayName("站内信催办默认启用")
    void shouldHaveDefaultStationEnabled() {
        assertTrue(properties.getMethods().isStation());
    }

    @Test
    @DisplayName("邮件催办默认启用")
    void shouldHaveDefaultEmailEnabled() {
        assertTrue(properties.getMethods().isEmail());
    }

    @Test
    @DisplayName("短信催办默认禁用")
    void shouldHaveDefaultSmsDisabled() {
        assertFalse(properties.getMethods().isSms());
    }

    @Test
    @DisplayName("默认催办频率为daily")
    void shouldHaveDefaultFrequency() {
        assertEquals("daily", properties.getFrequency().getDefaultFrequency());
    }

    @Test
    @DisplayName("自动催办定时任务默认禁用")
    void shouldHaveDefaultAutoEnabledFalse() {
        assertFalse(properties.getAuto().isEnabled());
    }

    @Test
    @DisplayName("自动催办默认cron为每天9点")
    void shouldHaveDefaultCron() {
        assertEquals("0 0 9 * * ?", properties.getAuto().getCron());
    }

    @Test
    @DisplayName("每天最大催办次数默认为3")
    void shouldHaveDefaultMaxPerDay() {
        assertEquals(3, properties.getLimits().getMaxPerDay());
    }

    @Test
    @DisplayName("催办冷却时间默认为24小时")
    void shouldHaveDefaultCooldownHours() {
        assertEquals(24, properties.getLimits().getCooldownHours());
    }

    // ======================== 综合绑定验证 ========================

    @Test
    @DisplayName("application.yml中approval.urge配置应全部正确绑定")
    void shouldBindAllPropertiesFromApplicationYml() {
        assertAll("approval.urge config binding",
            () -> assertTrue(properties.getMethods().isStation(),
                "methods.station"),
            () -> assertTrue(properties.getMethods().isEmail(),
                "methods.email"),
            () -> assertFalse(properties.getMethods().isSms(),
                "methods.sms"),
            () -> assertEquals("daily", properties.getFrequency().getDefaultFrequency(),
                "frequency.default-frequency"),
            () -> assertFalse(properties.getAuto().isEnabled(),
                "auto.enabled"),
            () -> assertEquals("0 0 9 * * ?", properties.getAuto().getCron(),
                "auto.cron"),
            () -> assertEquals(3, properties.getLimits().getMaxPerDay(),
                "limits.max-per-day"),
            () -> assertEquals(24, properties.getLimits().getCooldownHours(),
                "limits.cooldown-hours")
        );
    }

    // ======================== @Validated 校验验证 ========================

    private final Validator validator;

    ApprovalUrgePropertiesTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("maxPerDay=0时应触发校验错误")
    void shouldRejectZeroMaxPerDay() {
        ApprovalUrgeProperties.Limits limits = new ApprovalUrgeProperties.Limits();
        limits.setMaxPerDay(0);
        limits.setCooldownHours(24);
        var violations = validator.validate(limits);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("max-per-day")));
    }

    @Test
    @DisplayName("maxPerDay=1且cooldownHours=1时应通过校验")
    void shouldAcceptValidLimits() {
        ApprovalUrgeProperties.Limits limits = new ApprovalUrgeProperties.Limits();
        limits.setMaxPerDay(1);
        limits.setCooldownHours(1);
        var violations = validator.validate(limits);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("cooldownHours=0时应触发校验错误")
    void shouldRejectZeroCooldownHours() {
        ApprovalUrgeProperties.Limits limits = new ApprovalUrgeProperties.Limits();
        limits.setMaxPerDay(3);
        limits.setCooldownHours(0);
        var violations = validator.validate(limits);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("cooldown-hours")));
    }

    // ======================== 配置属性路径验证 ========================

    @Test
    @DisplayName("@ConfigurationProperties前缀应为approval.urge")
    void shouldHaveCorrectPrefix() {
        ConfigurationProperties annotation = ApprovalUrgeProperties.class
            .getAnnotation(ConfigurationProperties.class);
        assertNotNull(annotation);
        assertEquals("approval.urge", annotation.prefix());
    }
}
