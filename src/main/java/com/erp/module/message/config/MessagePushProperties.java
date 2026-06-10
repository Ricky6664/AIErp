package com.erp.module.message.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 消息推送配置属性.
 *
 * <p>绑定 application.yml 中 message.push.* 配置项, 提供类型安全的配置访问.</p>
 *
 * <p>配置项说明:
 * <ul>
 *   <li>message.push.methods.station — 是否启用站内信推送，默认 true</li>
 *   <li>message.push.methods.email — 是否启用邮件推送，默认 true</li>
 *   <li>message.push.methods.sms — 是否启用短信推送，默认 false</li>
 *   <li>message.push.frequency.default-frequency — 默认推送频率(daily/weekly/monthly)，默认 daily</li>
 *   <li>message.push.auto.enabled — 是否启用自动推送定时任务，默认 false</li>
 *   <li>message.push.auto.cron — 自动推送cron表达式，默认 0 0 9 * * ?(每天9点)</li>
 *   <li>message.push.limits.max-per-day — 每天最大推送次数，默认 5</li>
 *   <li>message.push.limits.cooldown-minutes — 同一接收人推送冷却时间(分钟)，默认 30</li>
 *   <li>message.push.pagination.default-page-size — 推送配置默认每页条数(5-100)，默认 10</li>
 *   <li>message.push.pagination.max-page-size — 推送配置最大每页条数，默认 50</li>
 *   <li>message.push.sort.default-field — 默认排序字段，默认 createTime</li>
 *   <li>message.push.sort.default-order — 默认排序方向(asc/desc)，默认 desc</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-06-09
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "message.push")
public class MessagePushProperties {

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

        @Min(value = 1, message = "message.push.limits.max-per-day 必须大于 0")
        private int maxPerDay = 5;

        @Min(value = 1, message = "message.push.limits.cooldown-minutes 必须大于 0")
        private int cooldownMinutes = 30;
    }

    @Data
    public static class Pagination {

        @Min(value = 5, message = "message.push.pagination.default-page-size 最小为 5")
        @Max(value = 100, message = "message.push.pagination.default-page-size 最大为 100")
        private int defaultPageSize = 10;

        private int maxPageSize = 50;
    }

    @Data
    public static class Sort {

        private String defaultField = "createTime";

        private String defaultOrder = "desc";
    }
}
