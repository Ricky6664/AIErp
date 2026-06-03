package com.erp.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录日志表实体.
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
@TableName("sys_login_log")
public class SysLoginLog implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long tenantId;

    @TableField("user_id")
    private Long userId;

    @TableField("login_time")
    private LocalDateTime loginTime;

    @TableField("ip_address")
    private String ipAddress;

    @TableField("browser")
    private String browser;

    @TableField("os")
    private String os;

    @TableField("login_method")
    private String loginMethod;

    @TableField("status")
    private String status;

    @TableField("fail_reason")
    private String failReason;

    @TableField("created_at")
    private LocalDateTime createdAt;
}
