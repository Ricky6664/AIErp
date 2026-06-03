package com.erp.auth.controller;

import com.erp.auth.dto.LoginRequest;
import com.erp.auth.dto.TokenRefreshRequest;
import com.erp.auth.service.AuthService;
import com.erp.auth.vo.LoginResponse;
import com.erp.auth.vo.TokenRefreshResponse;
import com.erp.auth.vo.TokenVerifyResponse;
import com.erp.common.enums.ErrorCode;
import com.erp.common.exception.AuthException;
import com.erp.common.exception.BusinessException;
import com.erp.common.result.RT;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController 单元测试")
class AuthControllerTest {

    @Mock private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private LoginRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new LoginRequest();
        validRequest.setUsername("admin");
        validRequest.setPassword("123456");
        validRequest.setCaptchaCode("ABCD");
        validRequest.setCaptchaKey("key123");
    }

    @Nested
    @DisplayName("POST /auth/login 成功")
    class LoginSuccess {

        @Test
        @DisplayName("有效请求 → 返回RT.ok包装的LoginResponse")
        void shouldReturnOkWithLoginResponse() {
            LoginResponse mockResponse = LoginResponse.builder()
                    .token("token-xxx")
                    .userId(1L)
                    .username("admin")
                    .nickname("管理员")
                    .avatar("/avatar.png")
                    .menuTree(Collections.emptyList())
                    .permissions(Collections.emptyList())
                    .build();
            when(authService.login(eq("admin"), eq("123456"), eq("ABCD"), eq("key123"), any(HttpServletRequest.class)))
                    .thenReturn(mockResponse);

            BindingResult bindingResult = new BeanPropertyBindingResult(validRequest, "loginRequest");
            RT<LoginResponse> result = authController.login(validRequest, bindingResult, mock(HttpServletRequest.class));

            assertTrue(result.isSuccess());
            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals("admin", result.getData().getUsername());
            assertEquals("token-xxx", result.getData().getToken());
        }
    }

    @Nested
    @DisplayName("POST /auth/login 参数校验失败")
    class ValidationFailure {

        @Test
        @DisplayName("用户名为空 → 返回paramError")
        void shouldReturnParamErrorWhenUsernameIsBlank() {
            LoginRequest req = new LoginRequest();
            req.setUsername("");
            req.setPassword("123456");
            BindingResult br = new BeanPropertyBindingResult(req, "loginRequest");
            br.rejectValue("username", "NotBlank", "用户名不能为空");

            RT<LoginResponse> result = authController.login(req, br, mock(HttpServletRequest.class));

            assertFalse(result.isSuccess());
            assertEquals(400, result.getCode());
            assertEquals("用户名不能为空", result.getMessage());
        }

        @Test
        @DisplayName("密码为空 → 返回paramError")
        void shouldReturnParamErrorWhenPasswordIsBlank() {
            LoginRequest req = new LoginRequest();
            req.setUsername("admin");
            req.setPassword("");
            BindingResult br = new BeanPropertyBindingResult(req, "loginRequest");
            br.rejectValue("password", "NotBlank", "密码不能为空");

            RT<LoginResponse> result = authController.login(req, br, mock(HttpServletRequest.class));

            assertFalse(result.isSuccess());
            assertEquals(400, result.getCode());
            assertEquals("密码不能为空", result.getMessage());
        }

        @Test
        @DisplayName("无具体字段错误时 → 返回默认参数校验失败消息")
        void shouldReturnDefaultMessageWhenNoFieldError() {
            BindingResult br = new BeanPropertyBindingResult(validRequest, "loginRequest");
            br.reject("global.error");

            RT<LoginResponse> result = authController.login(validRequest, br, mock(HttpServletRequest.class));

            assertFalse(result.isSuccess());
            assertEquals(400, result.getCode());
            assertEquals("参数校验失败", result.getMessage());
        }
    }

    @Nested
    @DisplayName("POST /auth/login 业务异常传播")
    class ExceptionPropagation {

        @Test
        @DisplayName("AuthService抛出AuthException → 由全局异常处理器处理(不在此层捕获)")
        void shouldPropagateAuthException() {
            BindingResult br = new BeanPropertyBindingResult(validRequest, "loginRequest");
            when(authService.login(anyString(), anyString(), anyString(), anyString(), any(HttpServletRequest.class)))
                    .thenThrow(new AuthException(ErrorCode.PASSWORD_ERROR));

            assertThrows(AuthException.class,
                    () -> authController.login(validRequest, br, mock(HttpServletRequest.class)));
        }
    }

    // ==================== 退出登录 ====================

    @Nested
    @DisplayName("POST /auth/logout")
    class Logout {

        @Test
        @DisplayName("正常退出 → 返回RT.ok并调用authService.logout()")
        void shouldReturnOkOnLogout() {
            doNothing().when(authService).logout();

            RT<Void> result = authController.logout();

            assertTrue(result.isSuccess());
            assertEquals(200, result.getCode());
            assertEquals("退出成功", result.getMessage());
            assertNull(result.getData());
            verify(authService).logout();
        }

        @Test
        @DisplayName("重复退出(幂等) → authService内部容错,仍返回成功")
        void shouldReturnOkOnRepeatedLogout() {
            doNothing().when(authService).logout();

            RT<Void> result1 = authController.logout();
            RT<Void> result2 = authController.logout();

            assertTrue(result1.isSuccess());
            assertTrue(result2.isSuccess());
            verify(authService, times(2)).logout();
        }
    }

    // ==================== Token校验 ====================

    @Nested
    @DisplayName("GET /auth/token/verify")
    class VerifyToken {

        @Test
        @DisplayName("有效Token → 返回RT.ok包装的TokenVerifyResponse")
        void shouldReturnOkWithTokenVerifyResponse() {
            TokenVerifyResponse mockResponse = new TokenVerifyResponse(true, 1L, 1800L);
            when(authService.verifyToken()).thenReturn(mockResponse);

            RT<TokenVerifyResponse> result = authController.verifyToken();

            assertTrue(result.isSuccess());
            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertTrue(result.getData().isValid());
            assertEquals(1L, result.getData().getUserId());
            assertEquals(1800L, result.getData().getExpireInSeconds());
        }

        @Test
        @DisplayName("Token过期 → AuthException由全局异常处理器处理")
        void shouldPropagateAuthExceptionOnExpiredToken() {
            when(authService.verifyToken())
                    .thenThrow(new AuthException(ErrorCode.TOKEN_EXPIRED));

            assertThrows(AuthException.class,
                    () -> authController.verifyToken());
        }
    }

    // ==================== Token刷新 ====================

    @Nested
    @DisplayName("POST /auth/token/refresh")
    class RefreshToken {

        @Test
        @DisplayName("有效refreshToken → 返回RT.ok包装的TokenRefreshResponse")
        void shouldReturnOkWithTokenRefreshResponse() {
            TokenRefreshRequest request = new TokenRefreshRequest();
            request.setRefreshToken("valid-refresh-token");

            TokenRefreshResponse mockResponse = TokenRefreshResponse.builder()
                    .token("new-access-token")
                    .refreshToken("new-refresh-token")
                    .expiresIn(2592000L)
                    .build();
            when(authService.refreshToken("valid-refresh-token")).thenReturn(mockResponse);

            BindingResult br = new BeanPropertyBindingResult(request, "tokenRefreshRequest");
            RT<TokenRefreshResponse> result = authController.refreshToken(request, br);

            assertTrue(result.isSuccess());
            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertEquals("new-access-token", result.getData().getToken());
            assertEquals("new-refresh-token", result.getData().getRefreshToken());
            assertEquals(2592000L, result.getData().getExpiresIn());
        }

        @Test
        @DisplayName("refreshToken为空 → 返回paramError")
        void shouldReturnParamErrorWhenRefreshTokenIsBlank() {
            TokenRefreshRequest request = new TokenRefreshRequest();
            request.setRefreshToken("");
            BindingResult br = new BeanPropertyBindingResult(request, "tokenRefreshRequest");
            br.rejectValue("refreshToken", "NotBlank", "refreshToken不能为空");

            RT<TokenRefreshResponse> result = authController.refreshToken(request, br);

            assertFalse(result.isSuccess());
            assertEquals(400, result.getCode());
            assertEquals("refreshToken不能为空", result.getMessage());
        }

        @Test
        @DisplayName("refreshToken无效 → AuthException由全局异常处理器处理")
        void shouldPropagateAuthExceptionOnInvalidRefreshToken() {
            TokenRefreshRequest request = new TokenRefreshRequest();
            request.setRefreshToken("expired-token");
            BindingResult br = new BeanPropertyBindingResult(request, "tokenRefreshRequest");

            when(authService.refreshToken("expired-token"))
                    .thenThrow(new AuthException(ErrorCode.REFRESH_TOKEN_EXPIRED));

            assertThrows(AuthException.class,
                    () -> authController.refreshToken(request, br));
        }
    }
}
