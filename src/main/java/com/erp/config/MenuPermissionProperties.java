package com.erp.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 菜单权限配置属性.
 *
 * <p>绑定 application.yml 中 auth.menu-permission 配置段,
 * 为前端菜单权限配置页提供类型安全的配置项读取.</p>
 *
 * @author AI
 * @since 2026-06-04
 */
@Component
@Validated
@ConfigurationProperties(prefix = "auth.menu-permission")
public class MenuPermissionProperties {

    /** 菜单权限树最大层级深度, 超出层级的节点不返回 */
    @Min(1)
    @Max(10)
    private int treeMaxDepth = 5;

    /** 是否启用菜单权限缓存 */
    private boolean cacheEnabled = true;

    /** 菜单权限缓存 TTL（秒） */
    @Positive
    private long cacheTtlSeconds = 1800;

    /** 新菜单项的默认权限类型（view/add/edit/delete/audit） */
    private String defaultPermissionType = "view";

    /** 是否在菜单权限树中自动包含父节点路径 */
    private boolean includeParentPath = true;

    public int getTreeMaxDepth() {
        return treeMaxDepth;
    }

    public void setTreeMaxDepth(int treeMaxDepth) {
        this.treeMaxDepth = treeMaxDepth;
    }

    public boolean isCacheEnabled() {
        return cacheEnabled;
    }

    public void setCacheEnabled(boolean cacheEnabled) {
        this.cacheEnabled = cacheEnabled;
    }

    public long getCacheTtlSeconds() {
        return cacheTtlSeconds;
    }

    public void setCacheTtlSeconds(long cacheTtlSeconds) {
        this.cacheTtlSeconds = cacheTtlSeconds;
    }

    public String getDefaultPermissionType() {
        return defaultPermissionType;
    }

    public void setDefaultPermissionType(String defaultPermissionType) {
        this.defaultPermissionType = defaultPermissionType;
    }

    public boolean isIncludeParentPath() {
        return includeParentPath;
    }

    public void setIncludeParentPath(boolean includeParentPath) {
        this.includeParentPath = includeParentPath;
    }
}
