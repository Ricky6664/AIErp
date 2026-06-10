package com.erp.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 数据权限配置页属性.
 *
 * <p>绑定 application.yml 中 data-permission.* 配置项, 提供类型安全的配置访问.
 * 在应用启动时自动执行 @Validated 校验, 配置错误会导致启动失败.</p>
 *
 * <p>配置项说明:
 * <ul>
 *   <li>data-permission.data-scope.default-scope — 默认数据权限范围，默认 SELF（仅本人）</li>
 *   <li>data-permission.data-scope.show-self — 是否显示"仅本人"选项，默认 true</li>
 *   <li>data-permission.data-scope.show-dept — 是否显示"本部门"选项，默认 true</li>
 *   <li>data-permission.data-scope.show-company — 是否显示"本公司"选项，默认 true</li>
 *   <li>data-permission.data-scope.show-all — 是否显示"全部"选项，默认 false</li>
 *   <li>data-permission.rule.cache-ttl-seconds — 数据权限规则缓存 TTL（秒），默认 1800</li>
 *   <li>data-permission.rule.field-level-enabled — 是否启用字段级权限，默认 false</li>
 *   <li>data-permission.rule.default-deny — 规则匹配失败时是否默认拒绝，默认 true</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-06-04
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "data-permission")
public class DataPermissionProperties {

    private DataScope dataScope = new DataScope();

    private Rule rule = new Rule();

    @Data
    public static class DataScope {

        private String defaultScope = "SELF";

        private boolean showSelf = true;

        private boolean showDept = true;

        private boolean showCompany = true;

        private boolean showAll = false;
    }

    @Data
    public static class Rule {

        private long cacheTtlSeconds = 1800;

        private boolean fieldLevelEnabled = false;

        private boolean defaultDeny = true;
    }
}
