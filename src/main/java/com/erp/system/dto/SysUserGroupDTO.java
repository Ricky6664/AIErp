package com.erp.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 用户组 DTO.
 *
 * @author AI
 * @since 2026-06-04
 */
@Data
public class SysUserGroupDTO {

    @Data
    public static class CreateDTO {
        @NotBlank(message = "组编码不能为空")
        private String groupCode;

        @NotBlank(message = "组名称不能为空")
        private String groupName;

        private String groupDesc;

        private Boolean isEnabled;

        private Integer sortOrder;
    }

    @Data
    public static class UpdateDTO {
        private String groupCode;

        private String groupName;

        private String groupDesc;

        private Boolean isEnabled;

        private Integer sortOrder;
    }

    @Data
    public static class StatusDTO {
        @NotNull(message = "启用状态不能为空")
        private Boolean isEnabled;
    }
}
