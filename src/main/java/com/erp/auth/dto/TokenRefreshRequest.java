package com.erp.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "Token刷新请求")
public class TokenRefreshRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "refreshToken不能为空")
    @Schema(description = "刷新令牌", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String refreshToken;
}
