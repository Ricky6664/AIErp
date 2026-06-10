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

@SpringBootTest(classes = {ApprovalDelegateProperties.class})
@ActiveProfiles("test")
@DisplayName("ApprovalDelegateProperties 配置属性验证")
class ApprovalDelegatePropertiesTest {

    @Autowired
    private ApprovalDelegateProperties properties;

    // ======================== Bean注入验证 ========================

    @Test
    @DisplayName("ApprovalDelegateProperties Bean应成功注入")
    void shouldAutowiredProperties() {
        assertNotNull(properties);
    }

    @Test
    @DisplayName("嵌套配置对象应非空")
    void shouldHaveNestedConfigObjects() {
        assertNotNull(properties.getNotification());
    }

    // ======================== 默认值绑定验证 ========================

    @Test
    @DisplayName("审批委托功能默认启用")
    void shouldHaveDefaultEnabled() {
        assertTrue(properties.isEnabled());
    }

    @Test
    @DisplayName("单次委托最大天数默认为30")
    void shouldHaveDefaultMaxDelegateDays() {
        assertEquals(30, properties.getMaxDelegateDays());
    }

    @Test
    @DisplayName("委托到期自动撤销默认启用")
    void shouldHaveDefaultAutoRevoke() {
        assertTrue(properties.isAutoRevoke());
    }

    @Test
    @DisplayName("委托生效通知被委托人默认启用")
    void shouldHaveDefaultNotificationEnabled() {
        assertTrue(properties.getNotification().isEnabled());
    }

    @Test
    @DisplayName("被委托人再次委托默认禁用")
    void shouldHaveDefaultAllowRedelegateDisabled() {
        assertFalse(properties.isAllowRedelegate());
    }

    // ======================== 综合绑定验证 ========================

    @Test
    @DisplayName("application.yml中approval.delegate配置应全部正确绑定")
    void shouldBindAllPropertiesFromApplicationYml() {
        assertAll("approval.delegate config binding",
            () -> assertTrue(properties.isEnabled(),
                "enabled"),
            () -> assertEquals(30, properties.getMaxDelegateDays(),
                "max-delegate-days"),
            () -> assertTrue(properties.isAutoRevoke(),
                "auto-revoke"),
            () -> assertTrue(properties.getNotification().isEnabled(),
                "notification.enabled"),
            () -> assertFalse(properties.isAllowRedelegate(),
                "allow-redelegate")
        );
    }

    // ======================== @Validated 校验验证 ========================

    private final Validator validator;

    ApprovalDelegatePropertiesTest() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    @Test
    @DisplayName("maxDelegateDays=0时应触发校验错误")
    void shouldRejectZeroMaxDelegateDays() {
        ApprovalDelegateProperties delegate = new ApprovalDelegateProperties();
        delegate.setMaxDelegateDays(0);
        var violations = validator.validate(delegate);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("max-delegate-days")));
    }

    @Test
    @DisplayName("maxDelegateDays=1时应通过校验")
    void shouldAcceptValidMaxDelegateDays() {
        ApprovalDelegateProperties delegate = new ApprovalDelegateProperties();
        delegate.setMaxDelegateDays(1);
        var violations = validator.validate(delegate);
        assertTrue(violations.isEmpty());
    }

    @Test
    @DisplayName("maxDelegateDays为负数时应触发校验错误")
    void shouldRejectNegativeMaxDelegateDays() {
        ApprovalDelegateProperties delegate = new ApprovalDelegateProperties();
        delegate.setMaxDelegateDays(-1);
        var violations = validator.validate(delegate);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("max-delegate-days")));
    }

    // ======================== 配置属性路径验证 ========================

    @Test
    @DisplayName("@ConfigurationProperties前缀应为approval.delegate")
    void shouldHaveCorrectPrefix() {
        ConfigurationProperties annotation = ApprovalDelegateProperties.class
            .getAnnotation(ConfigurationProperties.class);
        assertNotNull(annotation);
        assertEquals("approval.delegate", annotation.prefix());
    }
}
