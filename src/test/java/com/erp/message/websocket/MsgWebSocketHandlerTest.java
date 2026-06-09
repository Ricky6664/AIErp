package com.erp.message.websocket;

import com.erp.module.message.service.IMsgMessageService;
import com.erp.module.message.websocket.MsgWebSocketHandler;
import com.erp.system.entity.SysRole;
import com.erp.system.service.SysRoleService;
import com.erp.system.service.UserRoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("MsgWebSocketHandler 单元测试")
class MsgWebSocketHandlerTest {

    @Mock
    private IMsgMessageService messageService;
    @Mock
    private SysRoleService roleService;
    @Mock
    private UserRoleService userRoleService;

    private ObjectMapper objectMapper;
    private MsgWebSocketHandler handler;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        handler = new MsgWebSocketHandler(messageService, roleService, userRoleService, objectMapper);
    }

    @AfterEach
    void tearDown() {
        var field = getSessionMap();
        field.clear();
    }

    @SuppressWarnings("unchecked")
    private ConcurrentHashMap<Long, WebSocketSession> getSessionMap() {
        try {
            var f = MsgWebSocketHandler.class.getDeclaredField("USER_SESSION_MAP");
            f.setAccessible(true);
            return (ConcurrentHashMap<Long, WebSocketSession>) f.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private WebSocketSession mockSession(Long userId, Map<String, Object> attrs) {
        WebSocketSession session = mock(WebSocketSession.class);
        lenient().when(session.getAttributes()).thenReturn(attrs != null ? attrs : new ConcurrentHashMap<>());
        lenient().when(session.isOpen()).thenReturn(true);
        return session;
    }

    private void invokeHandleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Method m = MsgWebSocketHandler.class.getDeclaredMethod("handleTextMessage",
                WebSocketSession.class, TextMessage.class);
        m.setAccessible(true);
        m.invoke(handler, session, message);
    }

    // ==================== afterConnectionEstablished ====================

    @Nested
    @DisplayName("afterConnectionEstablished - 连接建立")
    class ConnectionEstablishedTests {

        @Test
        @DisplayName("有效userId → 注册会话成功")
        void shouldRegisterSessionWhenValidUserId() throws Exception {
            Map<String, Object> attrs = new ConcurrentHashMap<>();
            attrs.put("userId", 100L);
            WebSocketSession session = mockSession(100L, attrs);

            handler.afterConnectionEstablished(session);

            assertEquals(1, getSessionMap().size());
            assertNotNull(getSessionMap().get(100L));
            assertNotNull(attrs.get("lastHeartbeat"));
        }

        @Test
        @DisplayName("userId为null → 关闭连接(NOT_ACCEPTABLE)")
        void shouldCloseWhenUserIdNull() throws Exception {
            Map<String, Object> attrs = new ConcurrentHashMap<>();
            WebSocketSession session = mockSession(null, attrs);

            handler.afterConnectionEstablished(session);

            verify(session).close(CloseStatus.NOT_ACCEPTABLE);
            assertEquals(0, getSessionMap().size());
        }

        @Test
        @DisplayName("多设备登录 → 旧连接关闭，新连接替换")
        void shouldReplaceOldSessionOnMultiDevice() throws Exception {
            WebSocketSession oldSession = mock(WebSocketSession.class);
            when(oldSession.getAttributes()).thenReturn(new ConcurrentHashMap<>());
            when(oldSession.isOpen()).thenReturn(true);
            getSessionMap().put(100L, oldSession);

            Map<String, Object> attrs = new ConcurrentHashMap<>();
            attrs.put("userId", 100L);
            WebSocketSession newSession = mockSession(100L, attrs);

            handler.afterConnectionEstablished(newSession);

            verify(oldSession).close(CloseStatus.NORMAL);
            assertEquals(1, getSessionMap().size());
            assertSame(newSession, getSessionMap().get(100L));
        }
    }

    // ==================== handleTextMessage ====================

    @Nested
    @DisplayName("handleTextMessage - 消息处理/心跳")
    class HandleTextMessageTests {

        @Test
        @DisplayName("ping消息 → 回复pong，更新心跳时间")
        void shouldReplyPongOnPing() throws Exception {
            Map<String, Object> attrs = new ConcurrentHashMap<>();
            attrs.put("userId", 100L);
            attrs.put("lastHeartbeat", 0L);
            WebSocketSession session = mockSession(100L, attrs);

            invokeHandleTextMessage(session, new TextMessage("{\"type\":\"ping\"}"));

            verify(session).sendMessage(argThat(msg -> {
                String payload = ((TextMessage) msg).getPayload();
                return payload.contains("\"type\":\"pong\"");
            }));
            assertTrue((Long) attrs.get("lastHeartbeat") > 0);
        }

        @Test
        @DisplayName("非ping消息 → 不回复pong，仅日志")
        void shouldNotReplyOnNonPing() throws Exception {
            Map<String, Object> attrs = new ConcurrentHashMap<>();
            attrs.put("userId", 100L);
            WebSocketSession session = mockSession(100L, attrs);

            invokeHandleTextMessage(session, new TextMessage("{\"type\":\"text\",\"content\":\"hello\"}"));

            verify(session, never()).sendMessage(any(TextMessage.class));
        }
    }

    // ==================== afterConnectionClosed ====================

    @Nested
    @DisplayName("afterConnectionClosed - 连接关闭")
    class ConnectionClosedTests {

        @Test
        @DisplayName("正常关闭 → 从映射中移除会话")
        void shouldRemoveSessionOnClose() {
            Map<String, Object> attrs = new ConcurrentHashMap<>();
            attrs.put("userId", 100L);
            WebSocketSession session = mockSession(100L, attrs);
            getSessionMap().put(100L, session);

            handler.afterConnectionClosed(session, CloseStatus.NORMAL);

            assertEquals(0, getSessionMap().size());
        }

        @Test
        @DisplayName("userId为null → 无操作")
        void shouldNoOpWhenUserIdNull() {
            WebSocketSession session = mock(WebSocketSession.class);
            when(session.getAttributes()).thenReturn(new ConcurrentHashMap<>());

            handler.afterConnectionClosed(session, CloseStatus.NORMAL);

            assertEquals(0, getSessionMap().size());
        }

        @Test
        @DisplayName("会话已不在映射中 → 无异常")
        void shouldNotThrowWhenSessionNotInMap() {
            Map<String, Object> attrs = new ConcurrentHashMap<>();
            attrs.put("userId", 999L);
            WebSocketSession session = mockSession(999L, attrs);

            assertDoesNotThrow(() ->
                    handler.afterConnectionClosed(session, CloseStatus.NORMAL));
        }
    }

    // ==================== pushToUser ====================

    @Nested
    @DisplayName("pushToUser - 按用户推送")
    class PushToUserTests {

        @Test
        @DisplayName("用户在线 → 推送消息成功")
        void shouldPushWhenUserOnline() throws Exception {
            Map<String, Object> attrs = new ConcurrentHashMap<>();
            attrs.put("userId", 100L);
            WebSocketSession session = mockSession(100L, attrs);
            getSessionMap().put(100L, session);

            String message = "{\"title\":\"新消息\"}";
            handler.pushToUser(100L, message);

            verify(session).sendMessage(any(TextMessage.class));
        }

        @Test
        @DisplayName("用户离线 → 降级处理(日志记录)")
        void shouldDegradeWhenUserOffline() {
            assertDoesNotThrow(() -> handler.pushToUser(999L, "离线消息"));
        }

        @Test
        @DisplayName("用户在线但Session已关闭 → 降级处理")
        void shouldDegradeWhenSessionClosed() throws Exception {
            WebSocketSession session = mock(WebSocketSession.class);
            when(session.isOpen()).thenReturn(false);
            getSessionMap().put(100L, session);

            assertDoesNotThrow(() -> handler.pushToUser(100L, "消息"));
            verify(session, never()).sendMessage(any(TextMessage.class));
        }
    }

    // ==================== pushToRole ====================

    @Nested
    @DisplayName("pushToRole - 按角色推送")
    class PushToRoleTests {

        @Test
        @DisplayName("角色存在且有成员 → 逐用户推送")
        void shouldPushToRoleMembers() throws Exception {
            SysRole role = new SysRole();
            role.setId(10L);
            role.setRoleCode("ADMIN");

            // Manually mock MyBatis-Plus chain: lambdaQuery().eq().one()
            var chain = mock(com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper.class);
            SysRoleService roleSvc = mock(SysRoleService.class);
            when(roleSvc.lambdaQuery()).thenReturn(chain);
            when(chain.eq(any(), anyString())).thenReturn(chain);
            when(chain.one()).thenReturn(role);

            UserRoleService userRoleSvc = mock(UserRoleService.class);
            when(userRoleSvc.getUserIdsByRoleId(10L)).thenReturn(List.of(100L, 200L));

            MsgWebSocketHandler h = new MsgWebSocketHandler(messageService, roleSvc, userRoleSvc, objectMapper);

            Map<String, Object> attrs1 = new ConcurrentHashMap<>();
            WebSocketSession s1 = mockSession(100L, attrs1);
            getSessionMap().put(100L, s1);

            Map<String, Object> attrs2 = new ConcurrentHashMap<>();
            WebSocketSession s2 = mockSession(200L, attrs2);
            getSessionMap().put(200L, s2);

            h.pushToRole("ADMIN", "角色消息");

            verify(s1).sendMessage(any(TextMessage.class));
            verify(s2).sendMessage(any(TextMessage.class));
        }

        @Test
        @DisplayName("角色编码不存在 → 记录警告")
        void shouldWarnWhenRoleNotFound() {
            var chain = mock(com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper.class);
            SysRoleService roleSvc = mock(SysRoleService.class);
            when(roleSvc.lambdaQuery()).thenReturn(chain);
            when(chain.eq(any(), anyString())).thenReturn(chain);
            when(chain.one()).thenReturn(null);

            MsgWebSocketHandler h = new MsgWebSocketHandler(messageService, roleSvc, userRoleService, objectMapper);

            assertDoesNotThrow(() -> h.pushToRole("UNKNOWN", "消息"));
        }
    }

    // ==================== broadcastAll ====================

    @Nested
    @DisplayName("broadcastAll - 全局广播")
    class BroadcastAllTests {

        @Test
        @DisplayName("有在线用户 → 所有用户收到消息")
        void shouldBroadcastToAllOnline() throws Exception {
            Map<String, Object> attrs1 = new ConcurrentHashMap<>();
            WebSocketSession s1 = mockSession(1L, attrs1);
            getSessionMap().put(1L, s1);

            Map<String, Object> attrs2 = new ConcurrentHashMap<>();
            WebSocketSession s2 = mockSession(2L, attrs2);
            getSessionMap().put(2L, s2);

            handler.broadcastAll("广播消息");

            verify(s1).sendMessage(any(TextMessage.class));
            verify(s2).sendMessage(any(TextMessage.class));
        }

        @Test
        @DisplayName("无在线用户 → 无异常")
        void shouldNotThrowWhenNoOnlineUsers() {
            assertDoesNotThrow(() -> handler.broadcastAll("广播"));
        }

        @Test
        @DisplayName("部分Session已关闭 → 仅推送给在线的")
        void shouldSkipClosedSessions() throws Exception {
            WebSocketSession open = mockSession(1L, new ConcurrentHashMap<>());
            getSessionMap().put(1L, open);

            WebSocketSession closed = mock(WebSocketSession.class);
            when(closed.isOpen()).thenReturn(false);
            getSessionMap().put(2L, closed);

            handler.broadcastAll("广播消息");

            verify(open).sendMessage(any(TextMessage.class));
            verify(closed, never()).sendMessage(any(TextMessage.class));
        }
    }

    // ==================== heartbeatCheck ====================

    @Nested
    @DisplayName("heartbeatCheck - 心跳检测")
    class HeartbeatCheckTests {

        @Test
        @DisplayName("心跳正常 → 保持连接")
        void shouldKeepAliveWhenHeartbeatNormal() throws Exception {
            Map<String, Object> attrs = new ConcurrentHashMap<>();
            attrs.put("userId", 100L);
            attrs.put("lastHeartbeat", System.currentTimeMillis());
            WebSocketSession session = mockSession(100L, attrs);
            getSessionMap().put(100L, session);

            handler.heartbeatCheck();

            assertEquals(1, getSessionMap().size());
            verify(session, never()).close(any(CloseStatus.class));
        }

        @Test
        @DisplayName("心跳超时(>90秒) → 断开连接")
        void shouldCloseOnHeartbeatTimeout() throws Exception {
            Map<String, Object> attrs = new ConcurrentHashMap<>();
            attrs.put("userId", 100L);
            attrs.put("lastHeartbeat", System.currentTimeMillis() - 100_000);
            WebSocketSession session = mockSession(100L, attrs);
            getSessionMap().put(100L, session);

            handler.heartbeatCheck();

            verify(session).close(CloseStatus.SESSION_NOT_RELIABLE);
            assertEquals(0, getSessionMap().size());
        }

        @Test
        @DisplayName("lastHeartbeat为null → 不作处理(仅检查非null)")
        void shouldNotCloseWhenNoHeartbeatRecord() throws Exception {
            Map<String, Object> attrs = new ConcurrentHashMap<>();
            attrs.put("userId", 100L);
            WebSocketSession session = mockSession(100L, attrs);
            getSessionMap().put(100L, session);

            handler.heartbeatCheck();

            verify(session, never()).close(any(CloseStatus.class));
        }
    }
}
