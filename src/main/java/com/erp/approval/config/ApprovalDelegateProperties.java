package com.erp.approval.config;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 审批委托配置属性.
 *
 * <p>绑定 application.yml 中 approval.delegate.* 配置项, 提供类型安全的配置访问.</p>
 *
 * <p>配置项说明:
 * <ul>
 *   <li>approval.delegate.enabled — 是否启用审批委托功能，默认 true</li>
 *   <li>approval.delegate.max-delegate-days — 单次委托最大天数，默认 30</li>
 *   <li>approval.delegate.auto-revoke — 委托到期是否自动撤销，默认 true</li>
 *   <li>approval.delegate.notification.enabled — 委托生效时是否通知被委托人，默认 true</li>
 *   <li>approval.delegate.allow-redelegate — 是否允许被委托人再次委托，默认 false</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-06-09
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "approval.delegate")
public class ApprovalDelegateProperties {

    private boolean enabled = true;

    @Min(value = 1, message = "approval.delegate.max-delegate-days 必须大于 0")
    private int maxDelegateDays = 30;

    private boolean autoRevoke = true;

    private Notification notification = new Notification();

    private boolean allowRedelegate = false;

    @Data
    public static class Notification {

        private boolean enabled = true;
    }
}
