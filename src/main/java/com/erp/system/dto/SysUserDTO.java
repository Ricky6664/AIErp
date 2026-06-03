package com.erp.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户 DTO.
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
public class SysUserDTO {

    @Data
    public static class CreateDTO {
        @NotBlank(message = "用户名不能为空")
        private String username;

        @NotBlank(message = "密码不能为空")
        private String password;

        private Long employeeId;

        private String realName;

        private String nickname;

        private String avatar;

        private String email;

        private String mobile;

        private String gender;

        private String status;

        private Boolean isLocked;

        private LocalDateTime lockedUntil;

        private Long ownerDeptId;

        private Long ownerId;
    }

    @Data
    public static class UpdateDTO {
        @NotNull(message = "用户ID不能为空")
        private Long id;

        private String username;

        private String realName;

        private String nickname;

        private String avatar;

        private String email;

        private String mobile;

        private String gender;

        private String status;

        private Boolean isLocked;

        private LocalDateTime lockedUntil;

        private Long ownerDeptId;

        private Long ownerId;
    }

    @Data
    public static class QueryDTO {
        private String username;

        private String realName;

        private String email;

        private String mobile;

        private String gender;

        private String status;

        private Boolean isLocked;

        private Integer pageNum = 1;

        private Integer pageSize = 10;
    }

    @Data
    public static class ResetPwdDTO {
        @NotNull(message = "用户ID不能为空")
        private Long userId;

        @NotBlank(message = "新密码不能为空")
        private String newPassword;
    }
}
