package com.erp.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.erp.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户表实体.
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
public class SysUser extends BaseEntity {

    private String username;

    @TableField(select = false)
    private String passwordHash;

    private Long employeeId;

    private String realName;

    private String nickname;

    private String avatar;

    private String email;

    private String mobile;

    private String gender;

    private String status;

    private LocalDateTime lastLoginAt;

    private String lastLoginIp;

    private LocalDateTime pwdResetAt;

    private Boolean isLocked;

    private LocalDateTime lockedUntil;
}
