package com.erp;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * ERP AI 智能管理系统 - Spring Boot 主启动类.
 *
 * <p>提供应用入口, 配置组件扫描、Mapper 扫描和 AOP 代理暴露.</p>
 *
 * @author AI
 * @since 2026-05-28
 */
@SpringBootApplication(scanBasePackages = "com.erp")
@MapperScan("com.erp.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class ErpAiApplication {

    /**
     * 应用入口方法.
     *
     * <p>JVM 推荐参数: -Xms512m -Xmx1024m (已在 pom.xml spring-boot-maven-plugin 中配置).</p>
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(ErpAiApplication.class, args);
    }

}
