package com.erp.module.message.config;

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

@SpringBootTest(classes = {MessagePushProperties.class})
@ActiveProfiles("test")
@DisplayName("MessagePushProperties 配置属性验证")
class MessagePushPropertiesTest {

    @Autowired
    private MessagePushProperties properties;

    // ======================== Bean注入验证 ========================

    @Test
    @DisplayName("MessagePushProperties Bean应成功注入")
    void shouldAutowiredProperties() {
        assertNotNull(properties);
    }

    @Test
    @DisplayName("所有嵌套配置对象应非空")
    void shouldHaveNestedConfigObjects() {
        assertNotNull(properties.getMethods());
        assertNotNull(properties.getFrequency());
        assertNotNull(properties.getAuto());
        assertNotNull(properties.getLimits());
        assertNotNull(properties.getPagination());
        assertNotNull(properties.getSort());
    }

    // ======================== Methods默认值验证 ========================

    @Test
    @DisplayName("站内信推送默认启用")
    void shouldHaveDefaultStationEnabled() {
        assertTrue(properties.getMethods().isStation());
    }

    @Test
    @DisplayName("邮件推送默认启用")
    void shouldHaveDefaultEmailEnabled() {
        assertTrue(properties.getMethods().isEmail());
    }

    @Test
    @DisplayName("短信推送默认禁用")
    void shouldHaveDefaultSmsDisabled() {
        assertFalse(properties.getMethods().isSms());
    }

    // ======================== Frequency默认值验证 ========================

    @Test
    @DisplayName("默认推送频率为daily")
    void shouldHaveDefaultFrequency() {
        assertEquals("daily", properties.getFrequency().getDefaultFrequency());
    }

    // ======================== Auto默认值验证 ========================

    @Test
    @DisplayName("自动推送定时任务默认禁用")
    void shouldHaveDefaultAutoEnabledDisabled() {
        assertFalse(properties.getAuto().isEnabled());
    }

    @Test
    @DisplayName("自动推送cron表达式默认为每天9点")
    void shouldHaveDefaultCron() {
        assertEquals("0 0 9 * * ?", properties.getAuto().getCron());
    }

    // ======================== Limits默认值验证 ========================

    @Test
    @DisplayName("每天最大推送次数默认为5")
    void shouldHaveDefaultMaxPerDay() {
        assertEquals(5, properties.getLimits().getMaxPerDay());
    }

    @Test
    @DisplayName("同一接收人推送冷却时间默认为30分钟")
    void shouldHaveDefaultCooldownMinutes() {
        assertEquals(30, properties.getLimits().getCooldownMinutes());
    }

    // ======================== Pagination默认值验证 ========================

    @Test
    @DisplayName("默认每页条数为10")
    void shouldHaveDefaultPageSize() {
        assertEquals(10, properties.getPagination().getDefaultPageSize());
    }

    @Test
    @DisplayName("最大每页条数为50")
    void shouldHaveDefaultMaxPageSize() {
        assertEquals(50, properties.getPagination().getMaxPageSize());
    }

    // ======================== Sort默认值验证 ========================

    @Test
    @DisplayName("默认排序字段为createTime")
    void shouldHaveDefaultSortField() {
        assertEquals("createTime", properties.getSort().getDefaultField());
    }

    @Test
    @DisplayName("默认排序方向为desc")
    void shouldHaveDefaultSortOrder() {
        assertEquals("desc", properties.getSort().getDefaultOrder());
    }

    // ======================== 综合绑定验证 ========================

    @Test
    @DisplayName("application.yml中message.push配置应全部正确绑定")
    void shouldBindAllPropertiesFromApplicationYml() {
        assertAll("message.push config binding",
            // Methods
            () -> assertTrue(properties.getMethods().isStation(),
                "methods.station"),
            () -> assertTrue(properties.getMethods().isEmail(),
                "methods.email"),
            () -> assertFalse(properties.getMethods().isSms(),
                "methods.sms"),
            // Frequency
            () -> assertEquals("daily", properties.getFrequency().getDefaultFrequency(),
                "frequency.default-frequency"),
            // Auto
            () -> assertFalse(properties.getAuto().isEnabled(),
                "auto.enabled"),
            () -> assertEquals("0 0 9 * * ?", properties.getAuto().getCron(),
                "auto.cron"),
            // Limits
            () -> assertEquals(5, properties.getLimits().getMaxPerDay(),
                "limits.max-per-day"),
            () -> assertEquals(30, properties.getLimits().getCooldownMinutes(),
                "limits.cooldown-minutes"),
            // Pagination
            () -> assertEquals(10, properties.getPagination().getDefaultPageSize(),
                "pagination.default-page-size"),
            () -> assertEquals(50, properties.getPagination().getMaxPageSize(),
                "pagination.max-page-size"),
            // Sort
            () -> assertEquals("createTime", properties.getSort().getDefaultField(),
                "sort.default-field"),
            () -> assertEquals("desc", properties.getSort().getDefaultOrder(),
                "sort.default-order")
        );
    }

    // ======================== @Validated 校验验证 ========================

    private final Validator validator;

    MessagePushPropertiesTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("maxPerDay=0时应触发校验错误")
    void shouldRejectZeroMaxPerDay() {
        MessagePushProperties.Limits limits = new MessagePushProperties.Limits();
        limits.setMaxPerDay(0);
        limits.setCooldownMinutes(30);
        var violations = validator.validate(limits);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("max-per-day")));
    }

    @Test
    @DisplayName("maxPerDay=1时应通过校验")
    void shouldAcceptValidMaxPerDay() {
        MessagePushProperties.Limits limits = new MessagePushProperties.Limits();
        limits.setMaxPerDay(1);
        limits.setCooldownMinutes(30);
        var violations = validator.validate(limits);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("cooldownMinutes=0时应触发校验错误")
    void shouldRejectZeroCooldownMinutes() {
        MessagePushProperties.Limits limits = new MessagePushProperties.Limits();
        limits.setMaxPerDay(1);
        limits.setCooldownMinutes(0);
        var violations = validator.validate(limits);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("cooldown-minutes")));
    }

    @Test
    @DisplayName("cooldownMinutes为负数时应触发校验错误")
    void shouldRejectNegativeCooldownMinutes() {
        MessagePushProperties.Limits limits = new MessagePushProperties.Limits();
        limits.setMaxPerDay(1);
        limits.setCooldownMinutes(-1);
        var violations = validator.validate(limits);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("cooldown-minutes")));
    }

    @Test
    @DisplayName("defaultPageSize=4时应触发校验错误(小于最小值5)")
    void shouldRejectDefaultPageSizeBelowMin() {
        MessagePushProperties.Pagination pagination = new MessagePushProperties.Pagination();
        pagination.setDefaultPageSize(4);
        pagination.setMaxPageSize(50);
        var violations = validator.validate(pagination);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("default-page-size")));
    }

    @Test
    @DisplayName("defaultPageSize=101时应触发校验错误(大于最大值100)")
    void shouldRejectDefaultPageSizeAboveMax() {
        MessagePushProperties.Pagination pagination = new MessagePushProperties.Pagination();
        pagination.setDefaultPageSize(101);
        pagination.setMaxPageSize(50);
        var violations = validator.validate(pagination);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("default-page-size")));
    }

    @Test
    @DisplayName("defaultPageSize=5且10时应通过校验")
    void shouldAcceptValidPageSize() {
        MessagePushProperties.Pagination pagination = new MessagePushProperties.Pagination();
        pagination.setDefaultPageSize(10);
        pagination.setMaxPageSize(50);
        var violations = validator.validate(pagination);
        assertTrue(violations.isEmpty());
    }

    // ======================== 配置属性路径验证 ========================

    @Test
    @DisplayName("@ConfigurationProperties前缀应为message.push")
    void shouldHaveCorrectPrefix() {
        ConfigurationProperties annotation = MessagePushProperties.class
            .getAnnotation(ConfigurationProperties.class);
        assertNotNull(annotation);
        assertEquals("message.push", annotation.prefix());
    }
}
