package com.erp.approval.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 审批催办配置属性.
 *
 * <p>绑定 application.yml 中 approval.urge.* 配置项, 提供类型安全的配置访问.</p>
 *
 * <p>配置项说明:
 * <ul>
 *   <li>approval.urge.methods.station — 是否启用站内信催办，默认 true</li>
 *   <li>approval.urge.methods.email — 是否启用邮件催办，默认 true</li>
 *   <li>approval.urge.methods.sms — 是否启用短信催办，默认 false</li>
 *   <li>approval.urge.frequency.default-frequency — 默认催办频率(daily/weekly)，默认 daily</li>
 *   <li>approval.urge.auto.enabled — 是否启用自动催办定时任务，默认 false</li>
 *   <li>approval.urge.auto.cron — 自动催办cron表达式，默认 0 0 9 * * ?(每天9点)</li>
 *   <li>approval.urge.limits.max-per-day — 每天最大催办次数，默认 3</li>
 *   <li>approval.urge.limits.cooldown-hours — 同一审批催办冷却时间(小时)，默认 24</li>
 *   <li>approval.urge.pagination.default-page-size — 催办记录默认每页条数(5-100)，默认 10</li>
 *   <li>approval.urge.pagination.max-page-size — 催办记录最大每页条数，默认 50</li>
 *   <li>approval.urge.sort.default-field — 默认排序字段，默认 createTime</li>
 *   <li>approval.urge.sort.default-order — 默认排序方向(asc/desc)，默认 desc</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-06-09
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "approval.urge")
public class ApprovalUrgeProperties {

    private Methods methods = new Methods();

    private Frequency frequency = new Frequency();

    private Auto auto = new Auto();

    private Limits limits = new Limits();

    private Pagination pagination = new Pagination();

    private Sort sort = new Sort();

    @Data
    public static class Methods {

        private boolean station = true;

        private boolean email = true;

        private boolean sms = false;
    }

    @Data
    public static class Frequency {

        private String defaultFrequency = "daily";
    }

    @Data
    public static class Auto {

        private boolean enabled = false;

        private String cron = "0 0 9 * * ?";
    }

    @Data
    public static class Limits {

        @Min(value = 1, message = "approval.urge.limits.max-per-day 必须大于 0")
        private int maxPerDay = 3;

        @Min(value = 1, message = "approval.urge.limits.cooldown-hours 必须大于 0")
        private int cooldownHours = 24;
    }

    @Data
    public static class Pagination {

        @Min(value = 5, message = "approval.urge.pagination.default-page-size 最小为 5")
        @Max(value = 100, message = "approval.urge.pagination.default-page-size 最大为 100")
        private int defaultPageSize = 10;

        private int maxPageSize = 50;
    }

    @Data
    public static class Sort {

        private String defaultField = "createTime";

        private String defaultOrder = "desc";
    }
}
