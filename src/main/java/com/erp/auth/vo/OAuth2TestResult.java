package com.erp.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * OAuth2连接测试结果VO.
 *
 * @author AI
 * @since 2026-06-05
 */
@Schema(description = "OAuth2连接测试结果")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2TestResult implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "连接是否成功", example = "true")
    private boolean success;

    @Schema(description = "HTTP状态码", example = "200")
    private int statusCode;

    @Schema(description = "响应时间(毫秒)", example = "235")
    private long responseTime;

    @Schema(description = "结果描述信息", example = "授权URL可访问")
    private String message;

    @Schema(description = "实际测试的URL", example = "https://open.work.weixin.qq.com/oauth2/authorize")
    private String testUrl;
}
