package com.erp.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Token校验响应")
public class TokenVerifyResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "Token是否有效", example = "true")
    private boolean valid;

    @Schema(description = "用户ID", example = "1")
    private Long userId;

    @Schema(description = "剩余有效秒数", example = "1800")
    private long expireInSeconds;
}
