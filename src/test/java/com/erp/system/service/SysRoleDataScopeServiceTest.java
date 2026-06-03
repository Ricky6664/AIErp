package com.erp.system.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.erp.common.exception.BusinessException;
import com.erp.system.entity.SysRoleDataScope;
import com.erp.system.mapper.SysRoleDataScopeMapper;
import com.erp.system.mapper.UserMapper;
import com.erp.system.service.impl.SysRoleDataScopeServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("SysRoleDataScopeService 单元测试")
class SysRoleDataScopeServiceTest {

    @Mock
    private SysRoleDataScopeMapper sysRoleDataScopeMapper;

    @Mock
    private UserMapper userMapper;

    @Spy
    private SysRoleDataScopeServiceImpl service;

    private MockedStatic<StpUtil> stpUtilMock;

    @BeforeEach
    void setUp() {
        stpUtilMock = mockStatic(StpUtil.class);
        ReflectionTestUtils.setField(service, "baseMapper", sysRoleDataScopeMapper);
        ReflectionTestUtils.setField(service, "userMapper", userMapper);
    }

    @AfterEach
    void tearDown() {
        if (stpUtilMock != null) {
            stpUtilMock.close();
        }
    }

    private SysRoleDataScope buildScope(Long id, Long roleId, String scopeType, String deptIds) {
        SysRoleDataScope scope = new SysRoleDataScope();
        scope.setId(id);
        scope.setRoleId(roleId);
        scope.setScopeType(scopeType);
        scope.setDeptIds(deptIds);
        return scope;
    }

    // ---- getByRoleId ----

    @Nested
    @DisplayName("getByRoleId 方法")
    class GetByRoleId {

        @Test
        @DisplayName("roleId 为 null 时返回空列表")
        void shouldReturnEmptyListWhenRoleIdIsNull() {
            List<SysRoleDataScope> result = service.getByRoleId(null);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("正常返回角色数据权限列表")
        void shouldReturnScopesByRoleId() {
            Long roleId = 1L;
            SysRoleDataScope scope1 = buildScope(1L, roleId, "DEPT", "1,2,3");
            SysRoleDataScope scope2 = buildScope(2L, roleId, "ALL", null);
            doReturn(Arrays.asList(scope1, scope2)).when(service)
                    .list(any(LambdaQueryWrapper.class));

            List<SysRoleDataScope> result = service.getByRoleId(roleId);

            assertEquals(2, result.size());
            assertEquals("DEPT", result.get(0).getScopeType());
            assertEquals("ALL", result.get(1).getScopeType());
        }

        @Test
        @DisplayName("角色无数据权限时返回空列表")
        void shouldReturnEmptyListWhenNoScopes() {
            doReturn(Collections.emptyList()).when(service)
                    .list(any(LambdaQueryWrapper.class));

            List<SysRoleDataScope> result = service.getByRoleId(99L);

            assertTrue(result.isEmpty());
        }
    }

    // ---- saveRoleDataScopes ----

    @Nested
    @DisplayName("saveRoleDataScopes 方法")
    class SaveRoleDataScopes {

        @Test
        @DisplayName("roleId 为 null 时抛出 BusinessException")
        void shouldThrowWhenRoleIdIsNull() {
            BusinessException ex = assertThrows(BusinessException.class,
                    () -> service.saveRoleDataScopes(null, Collections.emptyList()));
            assertNotNull(ex);
            assertEquals(30002, ex.getCode());
        }

        @Test
        @DisplayName("正常保存数据权限并踢出在线用户")
        void shouldSaveScopesAndKickoutUsers() {
            Long roleId = 1L;
            SysRoleDataScope scope = buildScope(null, null, "DEPT", "1,2");
            List<SysRoleDataScope> scopes = Collections.singletonList(scope);

            doReturn(true).when(service).remove(any(LambdaQueryWrapper.class));
            doReturn(true).when(service).saveBatch(anyList());
            when(userMapper.selectUserIdsByRoleId(roleId)).thenReturn(Arrays.asList(100L, 200L));

            assertDoesNotThrow(() -> service.saveRoleDataScopes(roleId, scopes));

            assertEquals(roleId, scope.getRoleId());
            verify(service).remove(any(LambdaQueryWrapper.class));
            verify(service).saveBatch(anyList());
            stpUtilMock.verify(() -> StpUtil.kickout(100L));
            stpUtilMock.verify(() -> StpUtil.kickout(200L));
        }

        @Test
        @DisplayName("scopes 为空时仅删除旧数据")
        void shouldOnlyRemoveWhenScopesIsEmpty() {
            Long roleId = 1L;
            doReturn(true).when(service).remove(any(LambdaQueryWrapper.class));
            when(userMapper.selectUserIdsByRoleId(roleId)).thenReturn(Collections.emptyList());

            assertDoesNotThrow(() -> service.saveRoleDataScopes(roleId, Collections.emptyList()));

            verify(service).remove(any(LambdaQueryWrapper.class));
            verify(service, never()).saveBatch(anyList());
        }

        @Test
        @DisplayName("scopes 为 null 时不执行插入")
        void shouldNotInsertWhenScopesIsNull() {
            Long roleId = 1L;
            doReturn(true).when(service).remove(any(LambdaQueryWrapper.class));
            when(userMapper.selectUserIdsByRoleId(roleId)).thenReturn(Collections.emptyList());

            assertDoesNotThrow(() -> service.saveRoleDataScopes(roleId, null));

            verify(service).remove(any(LambdaQueryWrapper.class));
            verify(service, never()).saveBatch(anyList());
        }

        @Test
        @DisplayName("踢出用户失败时不抛异常")
        void shouldNotThrowWhenKickoutFails() {
            Long roleId = 1L;
            SysRoleDataScope scope = buildScope(null, null, "ALL", null);
            List<SysRoleDataScope> scopes = Collections.singletonList(scope);

            doReturn(true).when(service).remove(any(LambdaQueryWrapper.class));
            doReturn(true).when(service).saveBatch(anyList());
            when(userMapper.selectUserIdsByRoleId(roleId)).thenReturn(Arrays.asList(100L));
            stpUtilMock.when(() -> StpUtil.kickout(100L))
                    .thenThrow(new RuntimeException("用户不在线"));

            assertDoesNotThrow(() -> service.saveRoleDataScopes(roleId, scopes));
        }
    }

    // ---- deleteByRoleId ----

    @Nested
    @DisplayName("deleteByRoleId 方法")
    class DeleteByRoleId {

        @Test
        @DisplayName("roleId 为 null 时直接返回不操作")
        void shouldReturnWhenRoleIdIsNull() {
            assertDoesNotThrow(() -> service.deleteByRoleId(null));
            verify(service, never()).remove(any(LambdaQueryWrapper.class));
        }

        @Test
        @DisplayName("正常按角色ID删除数据权限")
        void shouldDeleteByRoleId() {
            Long roleId = 1L;
            doReturn(true).when(service).remove(any(LambdaQueryWrapper.class));

            assertDoesNotThrow(() -> service.deleteByRoleId(roleId));

            verify(service).remove(any(LambdaQueryWrapper.class));
        }
    }

    // ---- getScopeType ----

    @Nested
    @DisplayName("getScopeType 方法")
    class GetScopeType {

        @Test
        @DisplayName("roleId 为 null 时返回 null")
        void shouldReturnNullWhenRoleIdIsNull() {
            assertNull(service.getScopeType(null));
        }

        @Test
        @DisplayName("角色无数据权限时返回 null")
        void shouldReturnNullWhenNoScopes() {
            doReturn(Collections.emptyList()).when(service)
                    .list(any(LambdaQueryWrapper.class));

            assertNull(service.getScopeType(1L));
        }

        @Test
        @DisplayName("返回第一条数据权限的 scopeType")
        void shouldReturnFirstScopeType() {
            Long roleId = 1L;
            SysRoleDataScope scope = buildScope(1L, roleId, "DEPT_AND_CHILD", "1,2");
            doReturn(Collections.singletonList(scope)).when(service)
                    .list(any(LambdaQueryWrapper.class));

            String scopeType = service.getScopeType(roleId);

            assertEquals("DEPT_AND_CHILD", scopeType);
        }
    }
}
