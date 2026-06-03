package com.erp.auth.controller;

import com.erp.auth.dto.LoginRequest;
import com.erp.auth.dto.TokenRefreshRequest;
import com.erp.auth.service.AuthService;
import com.erp.auth.vo.LoginResponse;
import com.erp.auth.vo.TokenRefreshResponse;
import com.erp.auth.vo.TokenVerifyResponse;
import com.erp.common.result.RT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器.
 *
 * @author AI
 * @since 2026-06-03
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "登录/登出/Token刷新/用户信息等认证接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户登录.
     */
    @Operation(summary = "用户登录", description = "通过用户名+密码+验证码登录, 返回Token和用户权限信息")
    @PostMapping("/login")
    public RT<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest,
                                    BindingResult bindingResult,
                                    HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "参数校验失败";
            return RT.paramError(msg);
        }
        LoginResponse response = authService.login(
                loginRequest.getUsername(),
                loginRequest.getPassword(),
                loginRequest.getCaptchaCode(),
                loginRequest.getCaptchaKey(),
                request);
        return RT.ok(response);
    }

    /**
     * 退出登录.
     */
    @Operation(summary = "退出登录", description = "注销当前Token, 清除会话和权限缓存")
    @PostMapping("/logout")
    public RT<Void> logout() {
        authService.logout();
        return RT.ok("退出成功", null);
    }

    /**
     * Token校验.
     */
    @Operation(summary = "Token校验", description = "校验当前Token有效性, 有效则自动续期并返回用户ID和剩余有效秒数")
    @GetMapping("/token/verify")
    public RT<TokenVerifyResponse> verifyToken() {
        TokenVerifyResponse response = authService.verifyToken();
        return RT.ok(response);
    }

    /**
     * Token刷新.
     */
    @Operation(summary = "Token刷新", description = "使用refreshToken换取新的accessToken+refreshToken对, 旧refreshToken一次性失效")
    @PostMapping("/token/refresh")
    public RT<TokenRefreshResponse> refreshToken(@Valid @RequestBody TokenRefreshRequest request,
                                                   BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            String msg = bindingResult.getFieldError() != null
                    ? bindingResult.getFieldError().getDefaultMessage()
                    : "参数校验失败";
            return RT.paramError(msg);
        }
        TokenRefreshResponse response = authService.refreshToken(request.getRefreshToken());
        return RT.ok(response);
    }
}
