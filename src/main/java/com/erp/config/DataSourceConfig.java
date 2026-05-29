package com.erp.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    /**
     * 注册 HikariCP 数据源 Bean.
     *
     * @return HikariDataSource 实例
     */
    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName(driverClassName);
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(5);
        config.setMaximumPoolSize(20);
        config.setIdleTimeout(300000);
        config.setMaxLifetime(1200000);
        config.setConnectionTimeout(30000);
        config.setPoolName("ErpHikariPool");
        config.setLeakDetectionThreshold(60000);

        log.info("HikariCP DataSource registered: poolName={}, maxPoolSize={}, minIdle={}",
                config.getPoolName(), config.getMaximumPoolSize(), config.getMinimumIdle());

        return new HikariDataSource(config);
    }
}
