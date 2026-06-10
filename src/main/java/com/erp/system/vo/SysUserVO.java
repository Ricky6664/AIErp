package com.erp.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户 VO.
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
public class SysUserVO {

    @Data
    public static class ListVO {
        private Long id;

        private String username;

        private String realName;

        private String nickname;

        private String avatar;

        private String email;

        private String mobile;

        private String gender;

        private String status;

        private String statusName;

        private Boolean isLocked;

        private String employeeName;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime lastLoginAt;

        private String lastLoginIp;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
    }

    @Data
    public static class DetailVO {
        private Long id;

        private String username;

        private Long employeeId;

        private String realName;

        private String nickname;

        private String avatar;

        private String email;

        private String mobile;

        private String gender;

        private String status;

        private String statusName;

        private Boolean isLocked;

        private LocalDateTime lockedUntil;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime lastLoginAt;

        private String lastLoginIp;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime pwdResetAt;

        private Long ownerDeptId;

        private Long ownerId;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;

        private List<String> roleNames;
    }
}
