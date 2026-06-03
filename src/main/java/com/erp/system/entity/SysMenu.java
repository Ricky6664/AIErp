package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 菜单表实体.
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_menu")
public class SysMenu extends BaseEntity {

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

    @TableField(exist = false)
    private List<SysMenu> children;
}
