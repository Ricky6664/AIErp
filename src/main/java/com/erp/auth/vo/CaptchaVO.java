package com.erp.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 验证码响应VO.
 *
 * @author AI
 * @since 2026-06-04
 */
@Schema(description = "验证码响应")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "验证码标识(UUID)", example = "550e8400e29b41d4a716446655440000")
    private String captchaKey;

    @Schema(description = "验证码图片(Base64编码)", example = "data:image/png;base64,iVBOR...")
    private String captchaImage;
}
