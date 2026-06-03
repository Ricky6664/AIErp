package com.erp.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@Schema(description = "Token刷新响应")
public class TokenRefreshResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "新的访问令牌", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "新的刷新令牌", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String refreshToken;

    @Schema(description = "Token有效秒数", example = "1800")
    private long expiresIn;
}
