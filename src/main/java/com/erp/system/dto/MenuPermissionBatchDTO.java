package com.erp.system.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 菜单权限批量绑定DTO.
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
public class MenuPermissionBatchDTO {

    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    @NotEmpty(message = "菜单权限列表不能为空")
    private List<MenuPermissionItem> menuPermissions;

    @Data
    public static class MenuPermissionItem {
        @NotNull(message = "菜单ID不能为空")
        private Long menuId;

        private List<String> permissionTypes;
    }
}
