package com.erp.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 密码策略配置页属性.
 *
 * <p>绑定 application.yml 中 password-policy.* 配置项, 提供类型安全的配置访问.
 * 在应用启动时自动执行 @Validated 校验, 配置错误会导致启动失败.</p>
 *
 * <p>配置项说明:
 * <ul>
 *   <li>password-policy.page.default-page-size — 分页默认每页条数, 默认 15</li>
 *   <li>password-policy.page.default-sort-field — 默认排序字段, 默认 updateTime</li>
 *   <li>password-policy.page.show-disabled — 列表中是否显示已禁用的策略, 默认 true</li>
 *   <li>password-policy.form.policy-name-min-length — 策略名称最小长度, 默认 2</li>
 *   <li>password-policy.form.policy-name-max-length — 策略名称最大长度, 默认 50</li>
 *   <li>password-policy.password.min-length — 密码最小长度, 默认 8</li>
 *   <li>password-policy.password.max-length — 密码最大长度, 默认 32</li>
 *   <li>password-policy.password.require-uppercase — 是否必须包含大写字母, 默认 true</li>
 *   <li>password-policy.password.require-lowercase — 是否必须包含小写字母, 默认 true</li>
 *   <li>password-policy.password.require-digit — 是否必须包含数字, 默认 true</li>
 *   <li>password-policy.password.require-special-char — 是否必须包含特殊字符, 默认 true</li>
 *   <li>password-policy.password.min-special-char-count — 最少特殊字符数量, 默认 1</li>
 *   <li>password-policy.lockout.max-attempts — 最大登录失败次数, 默认 5</li>
 *   <li>password-policy.lockout.lock-duration-minutes — 锁定持续时间(分钟), 默认 30</li>
 *   <li>password-policy.lockout.reset-duration-minutes — 失败计数重置时间(分钟), 默认 10</li>
 *   <li>password-policy.expire.password-expire-days — 密码过期天数(0=永不过期), 默认 90</li>
 *   <li>password-policy.expire.remind-before-days — 过期前提醒天数, 默认 7</li>
 *   <li>password-policy.history.max-history-count — 历史密码不可重复次数, 默认 3</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-06-05
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "password-policy")
public class PasswordPolicyProperties {

    private Page page = new Page();

    private Form form = new Form();

    private Password password = new Password();

    private Lockout lockout = new Lockout();

    private Expire expire = new Expire();

    private History history = new History();

    @Data
    public static class Page {

        private int defaultPageSize = 15;

        private String defaultSortField = "updateTime";

        private boolean showDisabled = true;
    }

    @Data
    public static class Form {

        private int policyNameMinLength = 2;

        private int policyNameMaxLength = 50;
    }

    @Data
    public static class Password {

        private int minLength = 8;

        private int maxLength = 32;

        private boolean requireUppercase = true;

        private boolean requireLowercase = true;

        private boolean requireDigit = true;

        private boolean requireSpecialChar = true;

        private int minSpecialCharCount = 1;
    }

    @Data
    public static class Lockout {

        private int maxAttempts = 5;

        private int lockDurationMinutes = 30;

        private int resetDurationMinutes = 10;
    }

    @Data
    public static class Expire {

        private int passwordExpireDays = 90;

        private int remindBeforeDays = 7;
    }

    @Data
    public static class History {

        private int maxHistoryCount = 3;
    }
}
