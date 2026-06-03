package com.erp.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import com.erp.auth.entity.AuthOnlineDevice;
import com.erp.auth.entity.SysUser;
import com.erp.auth.exception.CaptchaException;
import com.erp.auth.mapper.AuthOnlineDeviceMapper;
import com.erp.auth.mapper.SysUserMapper;
import com.erp.auth.vo.LoginResponse;
import com.erp.auth.vo.TokenRefreshResponse;
import com.erp.auth.vo.TokenVerifyResponse;
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
    @Mock private AuthOnlineDeviceMapper authOnlineDeviceMapper;
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

    // ==================== 退出登录 ====================

    @Nested
    @DisplayName("退出登录")
    class Logout {

        @Test
        @DisplayName("正常退出 → 注销Token,清除Redis缓存,更新设备状态,异步写登出时间")
        void shouldLogoutSuccessfully() {
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            stpUtilMock.when(StpUtil::getTokenValue).thenReturn("test-token-xxx");

            AuthOnlineDevice device = new AuthOnlineDevice();
            device.setId(1L);
            device.setUserId(1L);
            device.setSessionTokenId("test-token-xxx");
            device.setStatus("在线");
            when(authOnlineDeviceMapper.selectOne(any())).thenReturn(device);

            authService.logout();

            // 验证设备状态更新为"已下线"
            verify(authOnlineDeviceMapper).updateById(argThat(d -> "已下线".equals(d.getStatus())));

            // 验证调用了StpUtil.logout()
            stpUtilMock.verify(StpUtil::logout);

            // 验证Redis权限缓存清除
            verify(redisTemplate).delete("user:permission:1");
            verify(redisTemplate).delete("user:menu:1");

            // 验证异步更新登出时间
            verify(loginLogService).updateLogoutTime(1L);
        }

        @Test
        @DisplayName("Token已过期时退出 → 幂等处理,不抛异常")
        void shouldHandleExpiredTokenGracefully() {
            stpUtilMock.when(StpUtil::getLoginIdAsLong)
                    .thenThrow(new cn.dev33.satoken.exception.NotLoginException("Token已过期", "", ""));

            // 不应抛出异常
            assertDoesNotThrow(() -> authService.logout());

            // StpUtil.logout()不应被调用
            stpUtilMock.verify(StpUtil::logout, never());
        }

        @Test
        @DisplayName("退出后Redis权限缓存已清除 → 验证delete调用")
        void shouldClearRedisPermissionCache() {
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(100L);
            stpUtilMock.when(StpUtil::getTokenValue).thenReturn("token-100");

            authService.logout();

            verify(redisTemplate).delete("user:permission:100");
            verify(redisTemplate).delete("user:menu:100");
        }

        @Test
        @DisplayName("退出时更新设备状态失败 → 不阻断退出流程(防御性容错)")
        void shouldNotBlockLogoutWhenDeviceStatusUpdateFails() {
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            stpUtilMock.when(StpUtil::getTokenValue).thenReturn("token-xxx");
            when(authOnlineDeviceMapper.selectOne(any()))
                    .thenThrow(new RuntimeException("数据库连接异常"));

            // 不应抛出异常, logout仍应完成
            assertDoesNotThrow(() -> authService.logout());

            // Token注销仍应执行
            stpUtilMock.verify(StpUtil::logout);
        }
    }

    // ==================== Token校验 ====================

    @Nested
    @DisplayName("Token校验 - verifyToken()")
    class VerifyToken {

        @Test
        @DisplayName("有效Token → 返回valid=true,userId和expireInSeconds")
        void shouldReturnValidResponseWhenTokenIsValid() {
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            stpUtilMock.when(StpUtil::getTokenTimeout).thenReturn(1800L);
            stpUtilMock.when(StpUtil::getTokenValue).thenReturn("valid-token-xxx");

            AuthOnlineDevice device = new AuthOnlineDevice();
            device.setId(1L);
            device.setUserId(1L);
            device.setSessionTokenId("valid-token-xxx");
            when(authOnlineDeviceMapper.selectOne(any())).thenReturn(device);

            TokenVerifyResponse response = authService.verifyToken();

            assertNotNull(response);
            assertTrue(response.isValid());
            assertEquals(1L, response.getUserId());
            assertEquals(1800L, response.getExpireInSeconds());
            verify(authOnlineDeviceMapper).updateById(argThat(d ->
                    d.getLastActiveTime() != null));
        }

        @Test
        @DisplayName("更新设备活跃时间失败 → 不阻断校验结果(防御性容错)")
        void shouldNotBlockVerifyWhenDeviceUpdateFails() {
            stpUtilMock.when(StpUtil::getLoginIdAsLong).thenReturn(1L);
            stpUtilMock.when(StpUtil::getTokenTimeout).thenReturn(1800L);
            stpUtilMock.when(StpUtil::getTokenValue).thenReturn("token-xxx");
            when(authOnlineDeviceMapper.selectOne(any()))
                    .thenThrow(new RuntimeException("数据库连接异常"));

            TokenVerifyResponse response = authService.verifyToken();

            assertTrue(response.isValid());
            assertEquals(1L, response.getUserId());
        }

        // 注: checkLogin() 是 Sa-Token 的 void 静态方法, MockedStatic 的 when() 在注册 stub 时
        // 会先执行真实方法, 导致在无 Sa-Token 上下文的单元测试中抛出非预期的 NotLoginException。
        // Token过期/未登录的错误路径在 AuthControllerTest 层验证 (mock AuthService 实例方法).
    }

    // ==================== Token刷新 ====================

    @Nested
    @DisplayName("Token刷新 - refreshToken()")
    class RefreshToken {

        @Test
        @DisplayName("有效refreshToken → 返回新token+refreshToken+expiresIn")
        void shouldReturnNewTokenPairWhenRefreshTokenIsValid() {
            when(valueOperations.get("refresh:token:valid-refresh-token")).thenReturn("1");

            stpUtilMock.when(StpUtil::getTokenValue).thenReturn("old-token", "new-token-xxx");
            stpUtilMock.when(StpUtil::getTokenTimeout).thenReturn(2592000L);

            AuthOnlineDevice device = new AuthOnlineDevice();
            device.setId(1L);
            device.setSessionTokenId("new-token-xxx");
            when(authOnlineDeviceMapper.selectOne(any())).thenReturn(device);

            TokenRefreshResponse response = authService.refreshToken("valid-refresh-token");

            assertNotNull(response);
            assertEquals("new-token-xxx", response.getToken());
            assertNotNull(response.getRefreshToken());
            assertFalse(response.getRefreshToken().isEmpty());
            assertEquals(2592000L, response.getExpiresIn());
        }

        @Test
        @DisplayName("refreshToken不存在于Redis → 抛出AuthException(REFRESH_TOKEN_EXPIRED)")
        void shouldThrowWhenRefreshTokenNotFound() {
            when(valueOperations.get("refresh:token:expired-token")).thenReturn(null);

            AuthException ex = assertThrows(AuthException.class,
                    () -> authService.refreshToken("expired-token"));
            assertEquals(20008, ex.getCode());
        }

        @Test
        @DisplayName("旧refreshToken一次性使用 → 刷新后立即删除,防止重放")
        void shouldDeleteOldRefreshTokenAfterUse() {
            when(valueOperations.get("refresh:token:one-time-token")).thenReturn("1");
            stpUtilMock.when(StpUtil::getTokenValue).thenReturn("old-token", "new-token");
            stpUtilMock.when(StpUtil::getTokenTimeout).thenReturn(2592000L);

            authService.refreshToken("one-time-token");

            verify(redisTemplate).delete("refresh:token:one-time-token");
        }

        @Test
        @DisplayName("旧accessToken被平滑替换 → 调用StpUtil.replaced()")
        void shouldReplaceOldAccessToken() {
            when(valueOperations.get("refresh:token:replace-test")).thenReturn("1");
            stpUtilMock.when(StpUtil::getTokenValue).thenReturn("old-access-token", "new-access-token");
            stpUtilMock.when(StpUtil::getTokenTimeout).thenReturn(2592000L);

            authService.refreshToken("replace-test");

            stpUtilMock.verify(() -> StpUtil.replaced("old-access-token", "new-access-token"));
        }

        @Test
        @DisplayName("生成新refreshToken并存入Redis → 7天TTL")
        void shouldStoreNewRefreshTokenWith7DayTTL() {
            when(valueOperations.get("refresh:token:gen-test")).thenReturn("1");
            stpUtilMock.when(StpUtil::getTokenValue).thenReturn("old-token", "new-token");
            stpUtilMock.when(StpUtil::getTokenTimeout).thenReturn(2592000L);

            TokenRefreshResponse response = authService.refreshToken("gen-test");

            verify(redisTemplate.opsForValue()).set(
                    eq("refresh:token:" + response.getRefreshToken()),
                    eq("1"),
                    eq(7L),
                    eq(java.util.concurrent.TimeUnit.DAYS));
        }
    }
}
