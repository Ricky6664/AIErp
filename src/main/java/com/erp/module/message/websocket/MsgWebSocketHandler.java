package com.erp.module.message.websocket;

import com.erp.module.message.service.IMsgMessageService;
import com.erp.system.service.SysRoleService;
import com.erp.system.service.UserRoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket消息推送处理器 — 连接生命周期管理、按用户/角色/广播推送、心跳检测.
 *
 * @author AI
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class MsgWebSocketHandler extends TextWebSocketHandler {

    /** 用户会话映射：userId -> WebSocketSession（ConcurrentHashMap保证线程安全） */
    private static final ConcurrentHashMap<Long, WebSocketSession> USER_SESSION_MAP = new ConcurrentHashMap<>();
    /** 心跳超时时间（90秒） */
    private static final long HEARTBEAT_TIMEOUT_MS = 90_000;
    /** 心跳间隔（30秒） */
    private static final long HEARTBEAT_INTERVAL_MS = 30_000;

    private final IMsgMessageService messageService;
    private final SysRoleService roleService;
    private final UserRoleService userRoleService;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }
        // 注册会话，同一用户多设备登录时保留最新连接
        WebSocketSession oldSession = USER_SESSION_MAP.put(userId, session);
        if (oldSession != null && oldSession.isOpen()) {
            oldSession.close(CloseStatus.NORMAL);
        }
        session.getAttributes().put("lastHeartbeat", System.currentTimeMillis());
        log.info("[WS] 用户{}连接成功，当前在线用户数: {}", userId, USER_SESSION_MAP.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        // 心跳处理
        if ("{\"type\":\"ping\"}".equals(payload)) {
            session.getAttributes().put("lastHeartbeat", System.currentTimeMillis());
            session.sendMessage(new TextMessage("{\"type\":\"pong\"}"));
            return;
        }
        log.debug("[WS] 收到消息: {}", payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            USER_SESSION_MAP.remove(userId, session);
            log.info("[WS] 用户{}断开连接，status={}", userId, status);
        }
    }

    /** 按用户推送消息 */
    public void pushToUser(Long userId, Object message) {
        WebSocketSession session = USER_SESSION_MAP.get(userId);
        if (session != null && session.isOpen()) {
            try {
                String json = objectMapper.writeValueAsString(message);
                synchronized (session) {
                    session.sendMessage(new TextMessage(json));
                }
            } catch (IOException e) {
                log.error("[WS] 推送给用户{}失败", userId, e);
            }
        } else {
            // 离线降级：消息由调用方通过MsgMessageService存入msg_message表
            log.info("[WS] 用户{}离线，消息降级存储", userId);
        }
    }

    /** 按角色推送消息 */
    public void pushToRole(String roleCode, Object message) {
        var role = roleService.lambdaQuery()
                .eq(com.erp.system.entity.SysRole::getRoleCode, roleCode)
                .one();
        if (role == null) {
            log.warn("[WS] 角色编码{}不存在", roleCode);
            return;
        }
        java.util.List<Long> userIds = userRoleService.getUserIdsByRoleId(role.getId());
        userIds.forEach(uid -> pushToUser(uid, message));
    }

    /** 全局广播 */
    public void broadcastAll(Object message) {
        USER_SESSION_MAP.values().forEach(session -> {
            if (session.isOpen()) {
                try {
                    String json = objectMapper.writeValueAsString(message);
                    synchronized (session) {
                        session.sendMessage(new TextMessage(json));
                    }
                } catch (IOException e) {
                    log.error("[WS] 广播失败", e);
                }
            }
        });
    }

    /** 定时心跳检测（每30秒执行） */
    @Scheduled(fixedRate = HEARTBEAT_INTERVAL_MS)
    public void heartbeatCheck() {
        long now = System.currentTimeMillis();
        USER_SESSION_MAP.forEach((userId, session) -> {
            Long lastHb = (Long) session.getAttributes().get("lastHeartbeat");
            if (lastHb != null && (now - lastHb) > HEARTBEAT_TIMEOUT_MS) {
                try {
                    session.close(CloseStatus.SESSION_NOT_RELIABLE);
                } catch (IOException e) {
                    /* ignore */
                }
                USER_SESSION_MAP.remove(userId);
                log.info("[WS] 用户{}心跳超时，断开连接", userId);
            }
        });
    }
}
