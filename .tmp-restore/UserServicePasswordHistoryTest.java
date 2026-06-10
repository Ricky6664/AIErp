package com.erp.system.service.impl;

import com.erp.auth.entity.AuthPasswordPolicy;
import com.erp.auth.service.AuthPasswordPolicyService;
import com.erp.common.exception.BusinessException;
import com.erp.system.entity.SysUser;
import com.erp.system.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 历史密码校验功能验证测试.
 *
 * @author AI
 * @since 2026-06-04
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("历史密码校验")
class UserServicePasswordHistoryTest {

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthPasswordPolicyService passwordPolicyService;

    private UserServiceImpl userService;

    private SysUser testUser;
    private static final Long USER_ID = 1L;
    private static final String OLD_PASSWORD = "OldPass123!";
    private static final String NEW_PASSWORD = "NewPass456!";

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(passwordPolicyService);
        ReflectionTestUtils.setField(userService, "baseMapper", userMapper);

        testUser = new SysUser();
        testUser.setId(USER_ID);
        testUser.setUsername("testuser");
        testUser.setPasswordHash(BCrypt.hashpw(OLD_PASSWORD, BCrypt.gensalt()));
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());

        lenient().when(passwordPolicyService.getCurrentPolicy()).thenReturn(new AuthPasswordPolicy());
    }

    @Nested
    @DisplayName("核心功能 — 历史密码拒绝")
    class HistoryRejection {

        @Test
        @DisplayName("新密码与历史密码1匹配时应拒绝")
        void shouldRejectWhenNewPasswordMatchesHistoryEntry1() {
            String hashedNewPassword = BCrypt.hashpw(NEW_PASSWORD, BCrypt.gensalt());
            List<String> history = Arrays.asList(
                    hashedNewPassword,
                    BCrypt.hashpw("OldHist1!", BCrypt.gensalt()),
                    BCrypt.hashpw("OldHist2!", BCrypt.gensalt())
            );

            when(userMapper.selectById(USER_ID)).thenReturn(testUser);
            when(userMapper.selectPasswordHistory(USER_ID, 3)).thenReturn(history);

            assertThatThrownBy(() ->
                    userService.changePassword(USER_ID, OLD_PASSWORD, NEW_PASSWORD)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("code", 30006);

            verify(userMapper, never()).insertPasswordHistory(anyLong(), anyString());
        }

        @Test
        @DisplayName("新密码与历史密码2匹配时应拒绝")
        void shouldRejectWhenNewPasswordMatchesHistoryEntry2() {
            String hashedNewPassword = BCrypt.hashpw(NEW_PASSWORD, BCrypt.gensalt());
            List<String> history = Arrays.asList(
                    BCrypt.hashpw("OldHist1!", BCrypt.gensalt()),
                    hashedNewPassword,
                    BCrypt.hashpw("OldHist2!", BCrypt.gensalt())
            );

            when(userMapper.selectById(USER_ID)).thenReturn(testUser);
            when(userMapper.selectPasswordHistory(USER_ID, 3)).thenReturn(history);

            assertThatThrownBy(() ->
                    userService.changePassword(USER_ID, OLD_PASSWORD, NEW_PASSWORD)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("code", 30006);

            verify(userMapper, never()).insertPasswordHistory(anyLong(), anyString());
        }

        @Test
        @DisplayName("新密码与历史密码3匹配时应拒绝")
        void shouldRejectWhenNewPasswordMatchesHistoryEntry3() {
            String hashedNewPassword = BCrypt.hashpw(NEW_PASSWORD, BCrypt.gensalt());
            List<String> history = Arrays.asList(
                    BCrypt.hashpw("OldHist1!", BCrypt.gensalt()),
                    BCrypt.hashpw("OldHist2!", BCrypt.gensalt()),
                    hashedNewPassword
            );

            when(userMapper.selectById(USER_ID)).thenReturn(testUser);
            when(userMapper.selectPasswordHistory(USER_ID, 3)).thenReturn(history);

            assertThatThrownBy(() ->
                    userService.changePassword(USER_ID, OLD_PASSWORD, NEW_PASSWORD)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("code", 30006);

            verify(userMapper, never()).insertPasswordHistory(anyLong(), anyString());
        }

        @Test
        @DisplayName("历史密码拒绝时不应插入新的密码历史记录")
        void shouldNotInsertHistoryOnRejection() {
            String hashedNewPassword = BCrypt.hashpw(NEW_PASSWORD, BCrypt.gensalt());
            List<String> history = Collections.singletonList(hashedNewPassword);

            when(userMapper.selectById(USER_ID)).thenReturn(testUser);
            when(userMapper.selectPasswordHistory(USER_ID, 3)).thenReturn(history);

            try {
                userService.changePassword(USER_ID, OLD_PASSWORD, NEW_PASSWORD);
            } catch (BusinessException ignored) {
                // expected
            }

            verify(userMapper, never()).insertPasswordHistory(anyLong(), anyString());
            verify(userMapper, never()).updateById(any(SysUser.class));
        }
    }

    @Nested
    @DisplayName("核心功能 — 历史密码通过")
    class HistoryAcceptance {

        @Test
        @DisplayName("新密码与历史密码均不匹配时应接受")
        void shouldAcceptWhenNewPasswordIsUnique() {
            List<String> history = Arrays.asList(
                    BCrypt.hashpw("OldHist1!", BCrypt.gensalt()),
                    BCrypt.hashpw("OldHist2!", BCrypt.gensalt()),
                    BCrypt.hashpw("OldHist3!", BCrypt.gensalt())
            );

            when(userMapper.selectById(USER_ID)).thenReturn(testUser);
            when(userMapper.selectPasswordHistory(USER_ID, 3)).thenReturn(history);

            userService.changePassword(USER_ID, OLD_PASSWORD, NEW_PASSWORD);

            verify(userMapper).insertPasswordHistory(eq(USER_ID), anyString());
            verify(userMapper).updateById(any(SysUser.class));
        }

        @Test
        @DisplayName("密码修改成功后应记录到密码历史表")
        void shouldInsertPasswordHistoryOnSuccess() {
            List<String> history = Collections.singletonList(
                    BCrypt.hashpw("SomeOldPass1!", BCrypt.gensalt())
            );

            when(userMapper.selectById(USER_ID)).thenReturn(testUser);
            when(userMapper.selectPasswordHistory(USER_ID, 3)).thenReturn(history);

            userService.changePassword(USER_ID, OLD_PASSWORD, NEW_PASSWORD);

            verify(userMapper).insertPasswordHistory(eq(USER_ID), anyString());
        }

        @Test
        @DisplayName("密码修改成功后应更新用户记录(密码哈希、过期时间)")
        void shouldUpdateUserOnSuccess() {
            List<String> history = Collections.emptyList();

            when(userMapper.selectById(USER_ID)).thenReturn(testUser);
            when(userMapper.selectPasswordHistory(USER_ID, 3)).thenReturn(history);

            userService.changePassword(USER_ID, OLD_PASSWORD, NEW_PASSWORD);

            verify(userMapper).updateById(argThat(user ->
                    user instanceof SysUser && USER_ID.equals(((SysUser) user).getId())
            ));
        }
    }

    @Nested
    @DisplayName("边界条件 — 空历史或不足3条")
    class BoundaryConditions {

        @Test
        @DisplayName("用户无历史密码记录时应接受任何有效新密码")
        void shouldAcceptWhenNoHistory() {
            when(userMapper.selectById(USER_ID)).thenReturn(testUser);
            when(userMapper.selectPasswordHistory(USER_ID, 3)).thenReturn(Collections.emptyList());

            userService.changePassword(USER_ID, OLD_PASSWORD, NEW_PASSWORD);

            verify(userMapper).insertPasswordHistory(eq(USER_ID), anyString());
        }

        @Test
        @DisplayName("selectPasswordHistory返回null时应安全处理")
        void shouldHandleNullHistory() {
            when(userMapper.selectById(USER_ID)).thenReturn(testUser);
            when(userMapper.selectPasswordHistory(USER_ID, 3)).thenReturn(null);

            userService.changePassword(USER_ID, OLD_PASSWORD, NEW_PASSWORD);

            verify(userMapper).insertPasswordHistory(eq(USER_ID), anyString());
        }

        @Test
        @DisplayName("仅有1条历史密码且不匹配时应接受")
        void shouldAcceptWithSingleHistoryEntry() {
            List<String> history = Collections.singletonList(
                    BCrypt.hashpw("SingleOldPass1!", BCrypt.gensalt())
            );

            when(userMapper.selectById(USER_ID)).thenReturn(testUser);
            when(userMapper.selectPasswordHistory(USER_ID, 3)).thenReturn(history);

            userService.changePassword(USER_ID, OLD_PASSWORD, NEW_PASSWORD);

            verify(userMapper).insertPasswordHistory(eq(USER_ID), anyString());
        }

        @Test
        @DisplayName("仅有1条历史密码且匹配时应拒绝")
        void shouldRejectWithSingleMatchingHistoryEntry() {
            String hashedNewPassword = BCrypt.hashpw(NEW_PASSWORD, BCrypt.gensalt());
            List<String> history = Collections.singletonList(hashedNewPassword);

            when(userMapper.selectById(USER_ID)).thenReturn(testUser);
            when(userMapper.selectPasswordHistory(USER_ID, 3)).thenReturn(history);

            assertThatThrownBy(() ->
                    userService.changePassword(USER_ID, OLD_PASSWORD, NEW_PASSWORD)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("code", 30006);
        }

        @Test
        @DisplayName("仅有2条历史密码且均不匹配时应接受")
        void shouldAcceptWithTwoHistoryEntries() {
            List<String> history = Arrays.asList(
                    BCrypt.hashpw("Hist1!", BCrypt.gensalt()),
                    BCrypt.hashpw("Hist2!", BCrypt.gensalt())
            );

            when(userMapper.selectById(USER_ID)).thenReturn(testUser);
            when(userMapper.selectPasswordHistory(USER_ID, 3)).thenReturn(history);

            userService.changePassword(USER_ID, OLD_PASSWORD, NEW_PASSWORD);

            verify(userMapper).insertPasswordHistory(eq(USER_ID), anyString());
        }
    }

    @Nested
    @DisplayName("边界条件 — 仅检查最近3条")
    class HistoryLimit {

        @Test
        @DisplayName("selectPasswordHistory传入参数为3")
        void shouldQueryExactlyThreeHistoryEntries() {
            when(userMapper.selectById(USER_ID)).thenReturn(testUser);
            when(userMapper.selectPasswordHistory(USER_ID, 3)).thenReturn(Collections.emptyList());

            userService.changePassword(USER_ID, OLD_PASSWORD, NEW_PASSWORD);

            verify(userMapper).selectPasswordHistory(USER_ID, 3);
        }

        @Test
        @DisplayName("只要最近3条不匹配就应接受(即使更早的密码匹配)")
        void shouldAcceptWhenMatchingBeyondLastThree() {
            List<String> history = Arrays.asList(
                    BCrypt.hashpw("Recent1!", BCrypt.gensalt()),
                    BCrypt.hashpw("Recent2!", BCrypt.gensalt()),
                    BCrypt.hashpw("Recent3!", BCrypt.gensalt())
            );

            when(userMapper.selectById(USER_ID)).thenReturn(testUser);
            when(userMapper.selectPasswordHistory(USER_ID, 3)).thenReturn(history);

            userService.changePassword(USER_ID, OLD_PASSWORD, NEW_PASSWORD);

            verify(userMapper).insertPasswordHistory(eq(USER_ID), anyString());
        }
    }

    @Nested
    @DisplayName("异常场景 — 参数校验")
    class ParameterValidation {

        @Test
        @DisplayName("用户ID为null时应抛出PARAM_MISSING")
        void shouldThrowWhenUserIdNull() {
            assertThatThrownBy(() ->
                    userService.changePassword(null, OLD_PASSWORD, NEW_PASSWORD)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("code", 30002);

            verify(userMapper, never()).selectPasswordHistory(anyLong(), anyInt());
        }

        @Test
        @DisplayName("旧密码为空时应抛出PARAM_MISSING")
        void shouldThrowWhenOldPasswordEmpty() {
            assertThatThrownBy(() ->
                    userService.changePassword(USER_ID, "", NEW_PASSWORD)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("code", 30002);

            verify(userMapper, never()).selectPasswordHistory(anyLong(), anyInt());
        }

        @Test
        @DisplayName("新密码长度不足6位时应拒绝")
        void shouldRejectShortPassword() {
            assertThatThrownBy(() ->
                    userService.changePassword(USER_ID, OLD_PASSWORD, "Ab1!")
            )
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("code", 30001);

            verify(userMapper, never()).selectPasswordHistory(anyLong(), anyInt());
        }

        @Test
        @DisplayName("新密码与当前密码相同时应拒绝")
        void shouldRejectSameAsCurrentPassword() {
            when(userMapper.selectById(USER_ID)).thenReturn(testUser);

            assertThatThrownBy(() ->
                    userService.changePassword(USER_ID, OLD_PASSWORD, OLD_PASSWORD)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("code", 30006);

            verify(userMapper, never()).selectPasswordHistory(anyLong(), anyInt());
        }

        @Test
        @DisplayName("用户不存在时应抛出DATA_NOT_FOUND")
        void shouldThrowWhenUserNotFound() {
            when(userMapper.selectById(USER_ID)).thenReturn(null);

            assertThatThrownBy(() ->
                    userService.changePassword(USER_ID, OLD_PASSWORD, NEW_PASSWORD)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("code", 50002);
        }

        @Test
        @DisplayName("旧密码错误时应抛出PASSWORD_ERROR")
        void shouldThrowWhenOldPasswordWrong() {
            when(userMapper.selectById(USER_ID)).thenReturn(testUser);

            assertThatThrownBy(() ->
                    userService.changePassword(USER_ID, "WrongOldPass1!", NEW_PASSWORD)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("code", 20006);
        }
    }

    @Nested
    @DisplayName("密码重置 — 历史记录")
    class PasswordReset {

        @Test
        @DisplayName("重置密码后应记录到密码历史表")
        void shouldInsertHistoryOnReset() {
            when(userMapper.selectById(USER_ID)).thenReturn(testUser);

            userService.resetPasswordAndReturn(USER_ID);

            verify(userMapper).insertPasswordHistory(eq(USER_ID), anyString());
        }

        @Test
        @DisplayName("重置密码后应更新用户密码和过期时间")
        void shouldUpdatePasswordOnReset() {
            when(userMapper.selectById(USER_ID)).thenReturn(testUser);

            String newPassword = userService.resetPasswordAndReturn(USER_ID);

            assertThat(newPassword).isNotNull();
            assertThat(newPassword.length()).isGreaterThanOrEqualTo(8);
            verify(userMapper).updateById(any(SysUser.class));
        }

        @Test
        @DisplayName("重置不存在的用户时应抛出异常")
        void shouldThrowWhenResettingNonexistentUser() {
            when(userMapper.selectById(USER_ID)).thenReturn(null);

            assertThatThrownBy(() ->
                    userService.resetPasswordAndReturn(USER_ID)
            )
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("code", 50002);

            verify(userMapper, never()).insertPasswordHistory(anyLong(), anyString());
        }
    }
}
