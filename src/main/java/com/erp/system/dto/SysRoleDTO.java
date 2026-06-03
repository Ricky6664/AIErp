package com.erp.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 角色 DTO.
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
public class SysRoleDTO {

    @Data
    public static class CreateDTO {
        @NotBlank(message = "角色编码不能为空")
        private String roleCode;

        @NotBlank(message = "角色名称不能为空")
        private String roleName;

        private String roleDesc;

        private String dataScope;

        private Boolean isEnabled;

        private Integer sortOrder;
    }

    @Data
    public static class UpdateDTO {
        @NotNull(message = "角色ID不能为空")
        private Long id;

        private String roleCode;

        private String roleName;

        private String roleDesc;

        private String dataScope;

        private Boolean isEnabled;

        private Integer sortOrder;
    }

    @Data
    public static class QueryDTO {
        private String roleCode;

        private String roleName;

        private Boolean isEnabled;

        private Integer pageNum = 1;

        private Integer pageSize = 10;
    }
}
