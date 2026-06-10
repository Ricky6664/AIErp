package com.erp.common.aspect;

import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.erp.common.annotation.RequirePermission;
import com.erp.common.enums.LogicEnum;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * PermissionAspect + @RequirePermission 集成测试.
 *
 * <p>任务: P0-001-004-002-002-003 集成测试验证</p>
 *
 * @author AI
 * @since 2026-05-29
 */
@SpringBootTest
class PermissionAspectIntegrationTest {

    private static final long TEST_USER_ID = 10001L;

    @Autowired
    private TestPermissionService testService;

    @BeforeEach
    void setUp() {
        TestStpInterface.clearPermissions();
        StpUtil.login(TEST_USER_ID);
    }

    @AfterEach
    void tearDown() {
        StpUtil.logout();
        TestStpInterface.clearPermissions();
    }

    static void grantPermission(String permission) {
        TestStpInterface.addPermission(TEST_USER_ID, permission);
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary
        StringRedisTemplate stringRedisTemplate() {
            StringRedisTemplate template = mock(StringRedisTemplate.class);
            ListOperations<String, String> listOps = mock(ListOperations.class);
            doReturn(null).when(listOps).range(anyString(), anyLong(), anyLong());
            doReturn(listOps).when(template).opsForList();
            return template;
        }

        @Bean
        @Primary
        StpInterface testStpInterface() {
            return new TestStpInterface();
        }

        @Bean
        TestPermissionService testPermissionService() {
            return new TestPermissionService();
        }
    }

    public static class TestStpInterface implements StpInterface {

        private static final Set<String> permissions = new HashSet<>();

        static void addPermission(long userId, String permission) {
            permissions.add(userId + ":" + permission);
        }

        static void clearPermissions() {
            permissions.clear();
        }

        @Override
        public List<String> getPermissionList(Object loginId, String loginType) {
            String prefix = loginId + ":";
            List<String> result = new ArrayList<>();
            for (String p : permissions) {
                if (p.startsWith(prefix)) {
                    result.add(p.substring(prefix.length()));
                }
            }
            return result;
        }

        @Override
        public List<String> getRoleList(Object loginId, String loginType) {
            return Collections.emptyList();
        }
    }

    public static class TestPermissionService {

        @RequirePermission({"user:read", "user:write"})
        public String andOperation() {
            return "and-success";
        }

        @RequirePermission({"order:create", "order:delete"})
        public String andMultiPermission() {
            return "and-multi-success";
        }

        @RequirePermission(value = {"user:read", "admin:read"}, logic = LogicEnum.OR)
        public String orOperation() {
            return "or-success";
        }

        @RequirePermission(value = {"inventory:in", "inventory:out"}, logic = LogicEnum.OR)
        public String orMultiPermission() {
            return "or-multi-success";
        }

        @RequirePermission("class:perm")
        public String classAnnotationMethod() {
            return "class-annotation-success";
        }

        public String noAnnotation() {
            return "no-annotation-success";
        }

        @RequirePermission({})
        public String emptyPermissions() {
            return "empty-success";
        }

        @RequirePermission("single:perm")
        public String singlePermission() {
            return "single-success";
        }
    }

    // ========== AND 逻辑测试 ==========

    @Test
    @DisplayName("AND: 用户持有所有权限 → 方法正常执行")
    void and_allPermissions_allowAccess() {
        grantPermission("user:read");
        grantPermission("user:write");
        assertEquals("and-success", testService.andOperation());
    }

    @Test
    @DisplayName("AND: 用户缺少任一权限 → 抛出 NotPermissionException")
    void and_missingOnePermission_throwsException() {
        grantPermission("user:read");
        assertThrows(NotPermissionException.class, () -> testService.andOperation());
    }

    @Test
    @DisplayName("AND: 用户无任何权限 → 抛出 NotPermissionException")
    void and_noPermissions_throwsException() {
        assertThrows(NotPermissionException.class, () -> testService.andOperation());
    }

    @Test
    @DisplayName("AND: 多权限全部持有 → 通过")
    void and_allMultiPermissions_allowAccess() {
        grantPermission("order:create");
        grantPermission("order:delete");
        assertEquals("and-multi-success", testService.andMultiPermission());
    }

    @Test
    @DisplayName("AND: 多权限部分持有 → 抛出异常")
    void and_partialMultiPermissions_throwsException() {
        grantPermission("order:create");
        assertThrows(NotPermissionException.class, () -> testService.andMultiPermission());
    }

    // ========== OR 逻辑测试 ==========

    @Test
    @DisplayName("OR: 用户持有第一个权限 → 通过")
    void or_firstPermission_allowAccess() {
        grantPermission("user:read");
        assertEquals("or-success", testService.orOperation());
    }

    @Test
    @DisplayName("OR: 用户持有第二个权限 → 通过")
    void or_secondPermission_allowAccess() {
        grantPermission("admin:read");
        assertEquals("or-success", testService.orOperation());
    }

    @Test
    @DisplayName("OR: 用户持有所有权限 → 通过")
    void or_allPermissions_allowAccess() {
        grantPermission("user:read");
        grantPermission("admin:read");
        assertEquals("or-success", testService.orOperation());
    }

    @Test
    @DisplayName("OR: 用户无任何权限 → 抛出 NotPermissionException")
    void or_noPermissions_throwsException() {
        assertThrows(NotPermissionException.class, () -> testService.orOperation());
    }

    @Test
    @DisplayName("OR: 多权限持有任一 → 通过")
    void or_oneOfMultiPermissions_allowAccess() {
        grantPermission("inventory:out");
        assertEquals("or-multi-success", testService.orMultiPermission());
    }

    @Test
    @DisplayName("OR: 多权限全无 → 抛出异常")
    void or_noMultiPermissions_throwsException() {
        assertThrows(NotPermissionException.class, () -> testService.orMultiPermission());
    }

    // ========== 单权限测试 ==========

    @Test
    @DisplayName("单权限: 持有权限 → 通过")
    void singlePermission_hasPermission_allowAccess() {
        grantPermission("single:perm");
        assertEquals("single-success", testService.singlePermission());
    }

    @Test
    @DisplayName("单权限: 无权限 → 抛出异常")
    void singlePermission_noPermission_throwsException() {
        assertThrows(NotPermissionException.class, () -> testService.singlePermission());
    }

    // ========== 放行场景 ==========

    @Test
    @DisplayName("放行: 无 @RequirePermission 注解 → 直接放行")
    void bypass_noAnnotation_allowAccess() {
        assertEquals("no-annotation-success", testService.noAnnotation());
    }

    @Test
    @DisplayName("放行: 空权限数组 → 直接放行")
    void bypass_emptyPermissions_allowAccess() {
        assertEquals("empty-success", testService.emptyPermissions());
    }

    // ========== 注解生效测试 ==========

    @Test
    @DisplayName("注解: 方法级 @RequirePermission 生效且被 AOP 拦截")
    void annotation_methodLevel_intercepted() {
        grantPermission("class:perm");
        assertEquals("class-annotation-success", testService.classAnnotationMethod());
    }

    @Test
    @DisplayName("注解: 方法级注解缺少权限时抛出异常")
    void annotation_methodLevel_missingPermission_throwsException() {
        assertThrows(NotPermissionException.class, () -> testService.classAnnotationMethod());
    }

    // ========== NotPermissionException 验证 ==========

    @Test
    @DisplayName("异常: 抛出 NotPermissionException 类型正确")
    void exception_correctExceptionType() {
        assertThrows(NotPermissionException.class, () -> testService.andOperation());
    }

    @Test
    @DisplayName("异常: 权限不足时方法体不被执行")
    void exception_methodBodyNotExecuted() {
        try {
            testService.andOperation();
            fail("Should have thrown NotPermissionException");
        } catch (NotPermissionException e) {
            assertNotNull(e.getMessage());
        }
    }
}
