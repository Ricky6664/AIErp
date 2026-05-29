package com.erp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.sql.DataSource;

/**
 * HikariCP 数据源配置类.
 *
 * <p>通过 @Bean 方式注册 HikariDataSource, 显式控制连接池参数.
 * Spring Boot 检测到此 Bean 后将退避自动配置的数据源.</p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Configuration
@Validated
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    // ---- 数据源基础配置 ----
    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    // ---- HikariCP 连接池配置 (从 application-{profile}.yml 读取, 支持 /actuator/env 验证) ----
    @Min(1)
    @Max(50)
    @Value("${spring.datasource.hikari.minimum-idle:5}")
    private int minimumIdle;

    @Min(1)
    @Max(20)
    @Value("${spring.datasource.hikari.maximum-pool-size:20}")
    private int maximumPoolSize;

    @Min(10000)
    @Value("${spring.datasource.hikari.idle-timeout:300000}")
    private long idleTimeout;

    @Min(30000)
    @Value("${spring.datasource.hikari.max-lifetime:1200000}")
    private long maxLifetime;

    @Min(1000)
    @Value("${spring.datasource.hikari.connection-timeout:30000}")
    private long connectionTimeout;

    @Value("${spring.datasource.hikari.pool-name:ErpHikariPool}")
    private String poolName;

    @Min(0)
    @Value("${spring.datasource.hikari.leak-detection-threshold:60000}")
    private long leakDetectionThreshold;

    /**
     * 注册 HikariCP 数据源 Bean.
     *
     * @return HikariDataSource 实例
     */
    @Bean
    public DataSource dataSource() {
        log.info("HikariCP DataSource initializing: poolName={}, maxPoolSize={}, minIdle={}, "
                        + "idleTimeout={}ms, maxLifetime={}ms, connectionTimeout={}ms, leakDetectionThreshold={}ms",
                poolName, maximumPoolSize, minimumIdle,
                idleTimeout, maxLifetime, connectionTimeout, leakDetectionThreshold);

        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(minimumIdle);
        config.setMaximumPoolSize(maximumPoolSize);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);
        config.setConnectionTimeout(connectionTimeout);
        config.setPoolName(poolName);
        config.setLeakDetectionThreshold(leakDetectionThreshold);

        log.info("HikariCP DataSource registered: poolName={}, maxPoolSize={}, minIdle={}",
                config.getPoolName(), config.getMaximumPoolSize(), config.getMinimumIdle());

        return new HikariDataSource(config);
    }
}
