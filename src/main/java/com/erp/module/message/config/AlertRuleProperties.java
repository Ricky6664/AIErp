package com.erp.module.message.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 业务预警规则配置属性.
 *
 * <p>绑定 application.yml 中 message.alert-rule.* 配置项, 提供类型安全的配置访问.</p>
 *
 * <p>配置项说明:
 * <ul>
 *   <li>message.alert-rule.methods.station — 是否启用站内信预警通知，默认 true</li>
 *   <li>message.alert-rule.methods.email — 是否启用邮件预警通知，默认 true</li>
 *   <li>message.alert-rule.methods.sms — 是否启用短信预警通知，默认 false</li>
 *   <li>message.alert-rule.evaluation.enabled — 是否启用自动预警评估定时任务，默认 false</li>
 *   <li>message.alert-rule.evaluation.cron — 自动预警评估cron表达式，默认 0 *&#47;5 * * * ?(每5分钟)</li>
 *   <li>message.alert-rule.evaluation.max-rules-per-batch — 每批评估最大规则数，默认 50</li>
 *   <li>message.alert-rule.evaluation.max-conditions-per-rule — 单规则最大条件表达式数，默认 10</li>
 *   <li>message.alert-rule.threshold.default-value — 默认预警阈值，默认 0.00</li>
 *   <li>message.alert-rule.pagination.default-page-size — 预警规则默认每页条数(5-100)，默认 10</li>
 *   <li>message.alert-rule.pagination.max-page-size — 预警规则最大每页条数，默认 50</li>
 *   <li>message.alert-rule.sort.default-field — 默认排序字段，默认 createTime</li>
 *   <li>message.alert-rule.sort.default-order — 默认排序方向(asc/desc)，默认 desc</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-06-09
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "message.alert-rule")
public class AlertRuleProperties {

    private Methods methods = new Methods();

    private Evaluation evaluation = new Evaluation();

    private Threshold threshold = new Threshold();

    private Pagination pagination = new Pagination();

    private Sort sort = new Sort();

    @Data
    public static class Methods {

        private boolean station = true;

        private boolean email = true;

        private boolean sms = false;
    }

    @Data
    public static class Evaluation {

        private boolean enabled = false;

        private String cron = "0 */5 * * * ?";

        @Min(value = 1, message = "message.alert-rule.evaluation.max-rules-per-batch 必须大于 0")
        private int maxRulesPerBatch = 50;

        @Min(value = 1, message = "message.alert-rule.evaluation.max-conditions-per-rule 必须大于 0")
        @Max(value = 50, message = "message.alert-rule.evaluation.max-conditions-per-rule 最大为 50")
        private int maxConditionsPerRule = 10;
    }

    @Data
    public static class Threshold {

        private java.math.BigDecimal defaultValue = java.math.BigDecimal.ZERO;
    }

    @Data
    public static class Pagination {

        @Min(value = 5, message = "message.alert-rule.pagination.default-page-size 最小为 5")
        @Max(value = 100, message = "message.alert-rule.pagination.default-page-size 最大为 100")
        private int defaultPageSize = 10;

        private int maxPageSize = 50;
    }

    @Data
    public static class Sort {

        private String defaultField = "createTime";

        private String defaultOrder = "desc";
    }
}
