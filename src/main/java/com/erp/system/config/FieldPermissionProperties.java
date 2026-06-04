
package com.erp.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 字段权限配置页属性.
 *
 * <p>绑定 application.yml 中 field-permission.* 配置项, 提供类型安全的配置访问.
 * 在应用启动时自动执行 @Validated 校验, 配置错误会导致启动失败.</p>
 *
 * <p>配置项说明:
 * <ul>
 *   <li>field-permission.table-tree.auto-expand-level — 数据表树默认展开层级，默认 2</li>
 *   <li>field-permission.table-tree.show-system-tables — 是否显示系统内部表，默认 false</li>
 *   <li>field-permission.field-rule.default-mode — 字段默认权限模式（EDITABLE/READONLY/HIDDEN），默认 EDITABLE</li>
 *   <li>field-permission.field-rule.cache-enabled — 是否启用字段权限缓存，默认 true</li>
 *   <li>field-permission.field-rule.cache-ttl-seconds — 字段权限缓存 TTL（秒），默认 1800</li>
 *   <li>field-permission.field-rule.confirm-before-save — 保存字段权限前是否弹确认框，默认 true</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-06-04
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "field-permission")
public class FieldPermissionProperties {

    private TableTree tableTree = new TableTree();

    private FieldRule fieldRule = new FieldRule();

    @Data
    public static class TableTree {

        private int autoExpandLevel = 2;

        private boolean showSystemTables = false;
    }

    @Data
    public static class FieldRule {

        private String defaultMode = "EDITABLE";

        private boolean cacheEnabled = true;

        private long cacheTtlSeconds = 1800;

        private boolean confirmBeforeSave = true;
    }
}
