package com.erp.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 在线登录用户视图对象.
 *
 * @author AI
 * @since 2026-05-29
 */
@Schema(description = "在线登录用户视图对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginUserVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "会话Token值")
    private String token;

    @Schema(description = "登录ID(用户ID)")
    private String loginId;

    @Schema(description = "登录设备类型")
    private String loginDevice;

    @Schema(description = "登录时间")
    private String loginTime;

    @Schema(description = "最后活跃时间")
    private String lastActiveTime;
}
