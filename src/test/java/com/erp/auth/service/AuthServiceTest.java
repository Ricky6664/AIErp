package com.erp.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.auth.entity.SysUser;
import com.erp.auth.exception.CaptchaException;
import com.erp.auth.mapper.SysUserMapper;
import com.erp.auth.vo.LoginResponse;
import com.erp.common.exception.AuthException;
import com.erp.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AuthService 单元测试")
class AuthServiceTest {

    @Mock private SysUserMapper sysUserMapper;
    @Mock private CaptchaService captchaService;
    @Mock private LoginLogService loginLogService;
    @Mock private StringRedisTemplate redisTemplate;
    @Mock private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private AuthService authService;

    private SysUser validUser;
    private MockedStatic<StpUtil> stpUtilMock;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // Mock Sa-Token 静态方法
        stpUtilMock = mockStatic(StpUtil.class);
        stpUtilMock.when(() -> StpUtil.login(anyLong())).then(invocation -> null);
        stpUtilMock.when(StpUtil::getTokenValue).thenReturn("mock-token-xxx");

        validUser = new SysUser();
        validUser.setId(1L);
        validUser.setUsername("admin");
        validUser.setPasswordHash(BCrypt.hashpw("123456", BCrypt.gensalt()));
        validUser.setNickname("管理员");
        validUser.setStatus("active");
        validUser.setAvatar("/avatar.png");
    }

    @AfterEach
    void tearDown() {
        if (stpUtilMock != null) {
            stpUtilMock.close();
        }
    }

    private HttpServletRequest mockRequest(String remoteAddr, String... headers) {
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getRemoteAddr()).thenReturn(remoteAddr != null ? remoteAddr : "127.0.0.1");
        return req;
    }

    // ==================== 登录成功 ====================

    @Nested
    @DisplayName("登录成功场景")
    class LoginSuccess {

        @Test
        @DisplayName("正确用户名密码验证码 → 登录成功返回token和用户信息")
        void shouldLoginSuccessfullyWithValidCredentials() {
            HttpServletRequest req = mockRequest("127.0.0.1");
            when(sysUserMapper.selectByUsername("admin")).thenReturn(validUser);
            doNothing().when(captchaService).verifyCaptcha(anyString(), anyString());

            LoginResponse response = authService.login("admin", "123456", "ABCD", "key123", req);

            assertNotNull(response);
            assertEquals(1L, response.getUserId());
            assertEquals("admin", response.getUsername());
            assertEquals("管理员", response.getNickname());
            assertEquals("/avatar.png", response.getAvatar());
            assertEquals("mock-token-xxx", response.getToken());
            assertNotNull(response.getMenuTree());
            assertNotNull(response.getPermissions());
            verify(sysUserMapper).updateById(validUser);
        }

        @Test
        @DisplayName("用户无昵称时 → 使用username作为nickname")
        void shouldFallbackToUsernameWhenNicknameIsNull() {
            validUser.setNickname(null);
            HttpServletRequest req = mockRequest("127.0.0.1");
            when(sysUserMapper.selectByUsername("admin")).thenReturn(validUser);
            doNothing().when(captchaService).verifyCaptcha(anyString(), anyString());

            LoginResponse response = authService.login("admin", "123456", "ABCD", "key123", req);

            assertEquals("admin", response.getNickname());
        }

        @Test
        @DisplayName("登录成功后清除失败计数和锁定状态")
        void shouldClearFailCountOnSuccessfulLogin() {
            HttpServletRequest req = mockRequest("127.0.0.1");
            when(sysUserMapper.selectByUsername("admin")).thenReturn(validUser);
            doNothing().when(captchaService).verifyCaptcha(anyString(), anyString());

            authService.login("admin", "123456", "ABCD", "key123", req);

            verify(redisTemplate).delete("login:fail:admin");
            verify(redisTemplate).delete("login:lock:admin");
        }
    }

    // ==================== 密码错误 ====================

    @Nested
    @DisplayName("登录失败 - 密码错误")
    class PasswordError {

        @Test
        @DisplayName("密码错误 → 抛出AuthException且增加失败计数")
        void shouldThrowAuthExceptionWhenPasswordWrong() {
            HttpServletRequest req = mockRequest("127.0.0.1");
            when(sysUserMapper.selectByUsername("admin")).thenReturn(validUser);
            doNothing().when(captchaService).verifyCaptcha(anyString(), anyString());
            when(valueOperations.increment("login:fail:admin")).thenReturn(1L);
            when(redisTemplate.expire(anyString(), anyLong(), any())).thenReturn(true);

            AuthException ex = assertThrows(AuthException.class,
                    () -> authService.login("admin", "wrongPassword", "ABCD", "key123", req));
            assertEquals(20006, ex.getCode());
            verify(valueOperations).increment("login:fail:admin");
        }
    }

    // ==================== 用户不存在 ====================

    @Nested
    @DisplayName("登录失败 - 用户不存在")
    class UserNotFound {

        @Test
        @DisplayName("用户不存在 → 抛出AuthException且增加失败计数")
        void shouldThrowAuthExceptionWhenUserNotFound() {
            HttpServletRequest req = mockRequest("127.0.0.1");
            when(sysUserMapper.selectByUsername("ghost")).thenReturn(null);
            doNothing().when(captchaService).verifyCaptcha(anyString(), anyString());
            when(valueOperations.increment("login:fail:ghost")).thenReturn(1L);
            when(redisTemplate.expire(anyString(), anyLong(), any())).thenReturn(true);

            assertThrows(AuthException.class,
                    () -> authService.login("ghost", "password", "ABCD", "key123", req));
        }
    }

    // ==================== 账号状态 ====================

    @Nested
    @DisplayName("登录失败 - 账号状态异常")
    class AccountStatus {

        @Test
        @DisplayName("账号被禁用 → 抛出BusinessException(错误码20005)")
        void shouldThrowBusinessExceptionWhenAccountDisabled() {
            validUser.setStatus("disabled");
            HttpServletRequest req = mockRequest("127.0.0.1");
            when(sysUserMapper.selectByUsername("admin")).thenReturn(validUser);
            doNothing().when(captchaService).verifyCaptcha(anyString(), anyString());

            BusinessException ex = assertThrows(BusinessException.class,
                    () -> authService.login("admin", "123456", "ABCD", "key123", req));
            assertEquals(20005, ex.getCode());
        }
    }

    // ==================== 验证码 ====================

    @Nested
    @DisplayName("登录失败 - 验证码")
    class CaptchaFailure {

        @Test
        @DisplayName("验证码错误 → 抛出CaptchaException")
        void shouldThrowCaptchaExceptionWhenCaptchaWrong() {
            HttpServletRequest req = mockRequest("127.0.0.1");
            doThrow(new CaptchaException()).when(captchaService).verifyCaptcha(anyString(), anyString());

            assertThrows(CaptchaException.class,
                    () -> authService.login("admin", "123456", "WRONG", "key123", req));
            verify(sysUserMapper, never()).selectByUsername(anyString());
        }
    }

    // ==================== 锁定机制 ====================

    @Nested
    @DisplayName("登录锁定机制")
    class LockMechanism {

        @Test
        @DisplayName("账号锁定后 → 直接抛出AuthException不校验密码")
        void shouldThrowAuthExceptionWhenAccountLocked() {
            HttpServletRequest req = mockRequest("127.0.0.1");
            when(valueOperations.get("login:lock:admin")).thenReturn("1");

            assertThrows(AuthException.class,
                    () -> authService.login("admin", "123456", "ABCD", "key123", req));
            verify(sysUserMapper, never()).selectByUsername(anyString());
        }

        @Test
        @DisplayName("连续失败5次 → 锁定账号")
        void shouldLockAccountAfter5ConsecutiveFailures() {
            HttpServletRequest req = mockRequest("127.0.0.1");
            when(sysUserMapper.selectByUsername("admin")).thenReturn(validUser);
            doNothing().when(captchaService).verifyCaptcha(anyString(), anyString());
            when(valueOperations.increment("login:fail:admin")).thenReturn(5L);
            when(redisTemplate.expire(anyString(), anyLong(), any())).thenReturn(true);

            assertThrows(AuthException.class,
                    () -> authService.login("admin", "wrongPwd", "ABCD", "key123", req));

            verify(valueOperations).set(eq("login:lock:admin"), eq("1"), eq(15L), any());
        }
    }

    // ==================== IP获取 ====================

    @Nested
    @DisplayName("IP获取")
    class ClientIp {

        @Test
        @DisplayName("X-Forwarded-For头存在时 → 优先使用")
        void shouldUseXForwardedForWhenPresent() {
            HttpServletRequest req = mock(HttpServletRequest.class);
            when(req.getHeader("X-Forwarded-For")).thenReturn("192.168.1.100");
            when(req.getRemoteAddr()).thenReturn("127.0.0.1");
            when(sysUserMapper.selectByUsername("admin")).thenReturn(validUser);
            doNothing().when(captchaService).verifyCaptcha(anyString(), anyString());

            authService.login("admin", "123456", "ABCD", "key123", req);

            verify(sysUserMapper).updateById(argThat(u -> "192.168.1.100".equals(u.getLastLoginIp())));
        }

        @Test
        @DisplayName("X-Forwarded-For含多IP → 取第一个")
        void shouldTakeFirstIpWhenMultipleInXForwardedFor() {
            HttpServletRequest req = mock(HttpServletRequest.class);
            when(req.getHeader("X-Forwarded-For")).thenReturn("10.0.0.1, 10.0.0.2, 10.0.0.3");
            when(req.getRemoteAddr()).thenReturn("127.0.0.1");
            when(sysUserMapper.selectByUsername("admin")).thenReturn(validUser);
            doNothing().when(captchaService).verifyCaptcha(anyString(), anyString());

            authService.login("admin", "123456", "ABCD", "key123", req);

            verify(sysUserMapper).updateById(argThat(u -> "10.0.0.1".equals(u.getLastLoginIp())));
        }
    }
}
