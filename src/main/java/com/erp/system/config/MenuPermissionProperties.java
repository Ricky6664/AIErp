package com.erp.system.config;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 菜单权限配置页属性.
 *
 * <p>绑定 application.yml 中 menu-permission.* 配置项, 提供类型安全的配置访问.
 * 在应用启动时自动执行 @Validated 校验, 配置错误会导致启动失败.</p>
 *
 * <p>配置项说明:
 * <ul>
 *   <li>menu-permission.menu-tree.auto-expand-level — 菜单树默认展开层级，默认 2（展开到第2层）</li>
 *   <li>menu-permission.menu-tree.show-disabled — 是否在权限树中显示已禁用的菜单，默认 false</li>
 *   <li>menu-permission.menu-tree.check-strictly — 父子节点是否独立勾选，默认 false（关联勾选）</li>
 *   <li>menu-permission.assignment.confirm-before-save — 保存权限分配前是否弹确认框，默认 true</li>
 * </ul>
 * </p>
 *
 * @author AI
 * @since 2026-06-04
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "menu-permission")
public class MenuPermissionProperties {

    private MenuTree menuTree = new MenuTree();

    private Assignment assignment = new Assignment();

    @Data
    public static class MenuTree {

        @Min(value = 1, message = "menu-permission.menu-tree.auto-expand-level 必须大于 0")
        private int autoExpandLevel = 2;

        private boolean showDisabled = false;

        private boolean checkStrictly = false;
    }

    @Data
    public static class Assignment {

        private boolean confirmBeforeSave = true;
    }
}
