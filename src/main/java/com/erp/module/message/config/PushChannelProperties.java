package com.erp.module.message.config;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 推送通道配置属性.
 *
 * <p>绑定 application.yml 中 message.push.channel.* 配置项, 提供类型安全的配置访问.</p>
 *
 * <p>配置项说明:
 * <ul>
 *   <li>message.push.channel.max-channels-per-tenant — 每租户最大推送通道数, 默认 5</li>
 *   <li>message.push.channel.default-type — 默认推送通道类型(station/email/sms), 默认 station</li>
 *   <li>message.push.channel.retry.max-retries — 推送失败最大重试次数, 默认 3</li>
 *   <li>message.push.channel.retry.interval-seconds — 重试间隔(秒), 默认 60</li>
 *   <li>message.push.channel.retry.backoff-multiplier — 退避倍数, 默认 2.0</li>
 *   <li>message.push.channel.batch.size — 批量推送每批大小, 默认 100</li>
 *   <li>message.push.channel.batch.interval-ms — 批次间隔(毫秒), 默认 500</li>
 *   <li>message.push.channel.batch.max-queue-size — 推送队列最大容量, 默认 10000</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-06-09
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "message.push.channel")
public class PushChannelProperties {

    @Min(value = 1, message = "message.push.channel.max-channels-per-tenant 必须大于 0")
    private int maxChannelsPerTenant = 5;

    private String defaultType = "station";

    private Retry retry = new Retry();

    private Batch batch = new Batch();

    @Data
    public static class Retry {

        @Min(value = 0, message = "message.push.channel.retry.max-retries 不能为负数")
        private int maxRetries = 3;

        @Min(value = 1, message = "message.push.channel.retry.interval-seconds 必须大于 0")
        private int intervalSeconds = 60;

        private double backoffMultiplier = 2.0;
    }

    @Data
    public static class Batch {

        @Min(value = 1, message = "message.push.channel.batch.size 必须大于 0")
        private int size = 100;

        @Min(value = 0, message = "message.push.channel.batch.interval-ms 不能为负数")
        private int intervalMs = 500;

        @Min(value = 1, message = "message.push.channel.batch.max-queue-size 必须大于 0")
        private int maxQueueSize = 10000;
    }
}
