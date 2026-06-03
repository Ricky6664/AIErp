package com.erp.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 菜单 DTO.
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
public class SysMenuDTO {

    @Data
    public static class CreateDTO {
        private Long parentId;

        @NotBlank(message = "菜单名称不能为空")
        private String menuName;

        @NotBlank(message = "菜单类型不能为空")
        private String menuType;

        private String permissionCode;

        private String routePath;

        private String routeName;

        private String componentPath;

        private String icon;

        private Integer sortOrder;

        private Boolean isVisible;

        private Boolean isEnabled;

        private Boolean isKeepAlive;

        private Boolean isExternalLink;

        private String externalUrl;
    }

    @Data
    public static class UpdateDTO {
        @NotNull(message = "菜单ID不能为空")
        private Long id;

        private Long parentId;

        private String menuName;

        private String menuType;

        private String permissionCode;

        private String routePath;

        private String routeName;

        private String componentPath;

        private String icon;

        private Integer sortOrder;

        private Boolean isVisible;

        private Boolean isEnabled;

        private Boolean isKeepAlive;

        private Boolean isExternalLink;

        private String externalUrl;
    }

    @Data
    public static class QueryDTO {
        private String menuName;

        private String menuType;

        private Long parentId;

        private Boolean isVisible;

        private Boolean isEnabled;

        private Integer pageNum = 1;

        private Integer pageSize = 10;
    }
}
