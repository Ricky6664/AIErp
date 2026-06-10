package com.erp.auth.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("auth_online_device")
public class AuthOnlineDevice implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long tenantId;

    @TableField("user_id")
    private Long userId;

    @TableField("device_type")
    private String deviceType;

    @TableField("session_token_id")
    private String sessionTokenId;

    @TableField("status")
    private String status;

    @TableField("last_active_time")
    private LocalDateTime lastActiveTime;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
