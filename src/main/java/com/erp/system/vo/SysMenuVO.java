package com.erp.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 菜单 VO.
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
public class SysMenuVO {

    @Data
    public static class ListVO {
        private Long id;

        private Long parentId;

        private String menuName;

        private String menuType;

        private String menuTypeName;

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

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
    }

    @Data
    public static class DetailVO {
        private Long id;

        private Long parentId;

        private String menuName;

        private String menuType;

        private String menuTypeName;

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

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;
    }
}
