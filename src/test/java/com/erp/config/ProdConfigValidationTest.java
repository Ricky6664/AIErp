package com.erp.config;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 生产环境配置(application-prod.yml)验证测试.
 *
 * <p>验证生产环境配置的完整性和安全性:</p>
 * <ul>
 *   <li>YAML 语法正确, 可正常加载</li>
 *   <li>server.port = 8080</li>
 *   <li>所有敏感配置通过环境变量注入, 无硬编码明文</li>
 *   <li>日志级别为 WARN, 禁止 DEBUG/INFO</li>
 *   <li>Swagger/Knife4j 文档已关闭</li>
 * </ul>
 *
 * @author AI
 * @since 2026-05-29
 */
class ProdConfigValidationTest {

    private static Map<String, Object> config;

    @BeforeAll
    static void loadConfig() {
        Yaml yaml = new Yaml();
        try (InputStream is = ProdConfigValidationTest.class
                .getClassLoader()
                .getResourceAsStream("application-prod.yml")) {
            assertNotNull(is, "application-prod.yml 必须存在于 classpath 中");
            config = yaml.load(is);
            assertNotNull(config, "application-prod.yml 加载后不能为空");
        } catch (Exception e) {
            fail("加载 application-prod.yml 失败: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("server.port 必须为 8080")
    void serverPortShouldBe8080() {
        Map<String, Object> server = getMap("server");
        assertNotNull(server, "server 配置节点不能为空");
        assertEquals(8080, server.get("port"), "生产环境 server.port 必须为 8080");
    }

    @Test
    @DisplayName("数据库密码必须通过环境变量 ${DB_PASSWORD} 注入")
    void dbPasswordShouldUseEnvVar() {
        Map<String, Object> spring = getMap("spring");
        assertNotNull(spring, "spring 配置节点不能为空");
        Map<String, Object> datasource = getMap(spring, "datasource");
        assertNotNull(datasource, "spring.datasource 配置节点不能为空");

        String password = String.valueOf(datasource.get("password"));
        assertTrue(password.contains("${DB_PASSWORD}"),
                "数据库密码必须使用环境变量 ${DB_PASSWORD}, 实际值: " + password);
        assertFalse(password.matches(".*[a-zA-Z0-9]{3,}.*") && !password.contains("${"),
                "数据库密码禁止硬编码明文");
    }

    @Test
    @DisplayName("数据库URL和用户名必须通过环境变量注入")
    void dbUrlAndUsernameShouldUseEnvVar() {
        Map<String, Object> spring = getMap("spring");
        Map<String, Object> datasource = getMap(spring, "datasource");
        assertNotNull(datasource, "spring.datasource 配置节点不能为空");

        String url = String.valueOf(datasource.get("url"));
        String username = String.valueOf(datasource.get("username"));
        assertTrue(url.contains("${DB_URL}"),
                "数据库URL必须使用环境变量 ${DB_URL}, 实际值: " + url);
        assertTrue(username.contains("${DB_USERNAME}"),
                "数据库用户名必须使用环境变量 ${DB_USERNAME}, 实际值: " + username);
    }

    @Test
    @DisplayName("Redis密码必须通过环境变量 ${REDIS_PASSWORD} 注入")
    void redisPasswordShouldUseEnvVar() {
        Map<String, Object> spring = getMap("spring");
        Map<String, Object> data = getMap(spring, "data");
        assertNotNull(data, "spring.data 配置节点不能为空");
        Map<String, Object> redis = getMap(data, "redis");
        assertNotNull(redis, "spring.data.redis 配置节点不能为空");

        String password = String.valueOf(redis.get("password"));
        assertTrue(password.contains("${REDIS_PASSWORD}"),
                "Redis密码必须使用环境变量 ${REDIS_PASSWORD}, 实际值: " + password);
    }

    @Test
    @DisplayName("JWT密钥必须通过环境变量 ${JWT_SECRET} 注入")
    void jwtSecretShouldUseEnvVar() {
        Map<String, Object> spring = getMap("spring");
        Map<String, Object> jwt = getMap(spring, "jwt");
        assertNotNull(jwt, "spring.jwt 配置节点不能为空");

        String secret = String.valueOf(jwt.get("secret"));
        assertTrue(secret.contains("${JWT_SECRET}"),
                "JWT密钥必须使用环境变量 ${JWT_SECRET}, 实际值: " + secret);
    }

    @Test
    @DisplayName("日志级别 com.erp 必须为 WARN")
    void comErpLoggingShouldBeWarn() {
        Map<String, Object> logging = getMap("logging");
        assertNotNull(logging, "logging 配置节点不能为空");
        Map<String, Object> level = getMap(logging, "level");
        assertNotNull(level, "logging.level 配置节点不能为空");

        assertEquals("WARN", level.get("com.erp"),
                "生产环境 com.erp 日志级别必须为 WARN");
    }

    @Test
    @DisplayName("禁止使用 DEBUG/INFO/TRACE 日志级别")
    void noDebugOrInfoLogLevels() {
        Map<String, Object> logging = getMap("logging");
        assertNotNull(logging, "logging 配置节点不能为空");
        Map<String, Object> level = getMap(logging, "level");
        assertNotNull(level, "logging.level 配置节点不能为空");

        List<String> forbiddenLevels = List.of("DEBUG", "INFO", "TRACE");
        for (Map.Entry<String, Object> entry : level.entrySet()) {
            String logLevel = String.valueOf(entry.getValue());
            assertFalse(forbiddenLevels.contains(logLevel.toUpperCase()),
                    String.format("生产环境禁止使用 %s 日志级别, 但 %s=%s",
                            logLevel, entry.getKey(), logLevel));
        }
    }

    @Test
    @DisplayName("Swagger/Knife4j 文档必须在生产环境关闭")
    void swaggerShouldBeDisabled() {
        Map<String, Object> springdoc = getMap("springdoc");
        assertNotNull(springdoc, "springdoc 配置节点不能为空");

        Map<String, Object> apiDocs = getMap(springdoc, "api-docs");
        assertNotNull(apiDocs, "springdoc.api-docs 配置节点不能为空");
        assertEquals(false, apiDocs.get("enabled"),
                "生产环境 springdoc.api-docs.enabled 必须为 false");

        Map<String, Object> swaggerUi = getMap(springdoc, "swagger-ui");
        assertNotNull(swaggerUi, "springdoc.swagger-ui 配置节点不能为空");
        assertEquals(false, swaggerUi.get("enabled"),
                "生产环境 springdoc.swagger-ui.enabled 必须为 false");
    }

    @Test
    @DisplayName("MyBatis-Plus 生产环境日志实现必须为 NoLoggingImpl")
    void mybatisPlusLoggingShouldBeNoLogging() {
        Map<String, Object> mybatisPlus = getMap("mybatis-plus");
        assertNotNull(mybatisPlus, "mybatis-plus 配置节点不能为空");
        Map<String, Object> configuration = getMap(mybatisPlus, "configuration");
        assertNotNull(configuration, "mybatis-plus.configuration 配置节点不能为空");

        String logImpl = String.valueOf(configuration.get("log-impl"));
        assertTrue(logImpl.contains("NoLoggingImpl"),
                "生产环境 MyBatis-Plus 日志实现必须为 NoLoggingImpl, 实际值: " + logImpl);
    }

    @Test
    @DisplayName("生产环境配置文件不包含任何明文密码或密钥")
    void noHardcodedSecrets() {
        Yaml yaml = new Yaml();
        String yamlStr = yaml.dump(config);

        // 检查常见的硬编码模式
        String[] forbiddenPatterns = {
                "password: [a-zA-Z0-9]",
                "secret: [a-zA-Z0-9]",
                "apiKey:",
                "api_key:"
        };
        for (String pattern : forbiddenPatterns) {
            assertFalse(yamlStr.matches("(?s).*" + pattern + ".*"),
                    "生产环境配置中发现疑似硬编码敏感信息: " + pattern);
        }
    }

    // ---- 辅助方法 ----

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(String key) {
        Object value = config.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getMap(Map<String, Object> parent, String key) {
        if (parent == null) {
            return null;
        }
        Object value = parent.get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return null;
    }
}
