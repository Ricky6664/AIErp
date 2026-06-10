package com.erp.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户表实体.
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long tenantId;

    private String username;

    @TableField("password_hash")
    private String passwordHash;

    private Long employeeId;

    private String realName;

    private String nickname;

    private String avatar;

    private String email;

    private String mobile;

    private String gender;

    private String status;

    @TableField("last_login_at")
    private LocalDateTime lastLoginAt;

    @TableField("last_login_ip")
    private String lastLoginIp;

    @TableField("pwd_reset_at")
    private LocalDateTime pwdResetAt;

    @TableField("password_expire_date")
    private java.time.LocalDate passwordExpireDate;

    @TableField("is_locked")
    private Boolean isLocked;

    @TableField("locked_until")
    private LocalDateTime lockedUntil;

    // 扩展字段
    private String extStr1;
    private String extStr2;
    private String extStr3;
    private String extStr4;
    private String extStr5;
    private String extStr6;
    private String extStr7;
    private String extStr8;
    private String extStr9;
    private String extStr10;

    // 通用字段
    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;

    @TableField("created_by")
    private Long createdBy;

    @TableField("updated_by")
    private Long updatedBy;

    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;

    @TableField("owner_dept_id")
    private Long ownerDeptId;

    @TableField("owner_id")
    private Long ownerId;

    @Version
    private Integer version;
}
