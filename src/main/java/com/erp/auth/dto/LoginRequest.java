package com.erp.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 登录请求DTO.
 *
 * @author AI
 * @since 2026-06-03
 */
@Schema(description = "登录请求")
@Data
public class LoginRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "用户名", example = "admin")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "密码", example = "123456")
    @NotBlank(message = "密码不能为空")
    private String password;

    @Schema(description = "验证码")
    private String captchaCode;

    @Schema(description = "验证码Key")
    private String captchaKey;
}
