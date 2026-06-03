package com.erp.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 登录响应VO.
 *
 * @author AI
 * @since 2026-06-03
 */
@Schema(description = "登录响应")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Sa-Token值")
    private String token;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像URL")
    private String avatar;

    @Schema(description = "菜单权限树")
    private List<MenuTreeNode> menuTree;

    @Schema(description = "按钮权限标识列表")
    private List<String> permissions;

    /**
     * 菜单树节点.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuTreeNode implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private Long id;

        private String name;

        private String path;

        private List<MenuTreeNode> children;
    }
}
