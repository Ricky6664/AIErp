package com.erp.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Jackson 序列化配置.
 *
 * <p>解决 Sa-Token 使用 Redis 存储 Session 时的序列化问题:
 * <ul>
 *   <li>禁用 FAIL_ON_SELF_REFERENCES, 避免 Session 对象自引用导致序列化异常</li>
 *   <li>注册 JavaTimeModule 并指定 LocalDateTime 格式为 yyyy-MM-dd HH:mm:ss</li>
 *   <li>序列化时忽略 null 值, 减少 Redis 存储量</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            // 避免 Session 对象循环引用导致的序列化异常
            builder.featuresToDisable(SerializationFeature.FAIL_ON_SELF_REFERENCES);
            // 序列化时忽略 null 值
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            // 注册 JavaTimeModule 并指定 LocalDateTime 序列化格式
            JavaTimeModule javaTimeModule = new JavaTimeModule();
            javaTimeModule.addSerializer(LocalDateTime.class,
                    new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            builder.modules(javaTimeModule);
        };
    }
}
