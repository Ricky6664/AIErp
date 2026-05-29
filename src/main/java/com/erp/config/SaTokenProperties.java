package com.erp.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * Sa-Token 配置属性绑定.
 *
 * <p>绑定 application.yml 中 sa-token.* 配置项, 提供类型安全的配置访问.
 * 在应用启动时自动执行 @Validated 校验, 配置错误会导致启动失败.
 * </p>
 *
 * @author AI
 * @since 2026-05-29
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "sa-token")
public class SaTokenProperties {

    /** Token 名称（也是 cookie 名称） */
    @NotBlank(message = "sa-token.token-name 不能为空")
    private String tokenName = "satoken";

    /** Token 有效期（秒），默认 30 天 = 2592000 */
    @Min(value = 1, message = "sa-token.timeout 必须大于 0")
    private long timeout = 2592000;

    /** Token 临时有效期（秒），无操作超过此时间则过期，默认 30 分钟 = 1800 */
    @Min(value = 1, message = "sa-token.active-timeout 必须大于 0")
    private long activeTimeout = 1800;

    /** 是否允许同一账号并发登录 */
    private boolean isConcurrent = true;

    /** 多人登录同一账号时是否共用 Token */
    private boolean isShare = true;

    /** Token 风格 */
    private String tokenStyle = "uuid";

    /** 是否输出操作日志 */
    private boolean isLog = false;

    /** 排除拦截的路径（逗号分隔） */
    private String excludePaths = "/api/auth/login,/api/auth/logout,/doc.html,/v3/api-docs/**";
}
