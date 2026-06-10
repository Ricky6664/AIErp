package com.erp.module.message.websocket;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket握手认证拦截器 — 从请求参数中提取token并验证用户身份.
 *
 * @author AI
 */
@Component
@Slf4j
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            String token = httpRequest.getParameter("token");
            if (token == null || token.isEmpty()) {
                log.warn("[WS] 握手失败：缺少token参数");
                return false;
            }
            try {
                Object loginId = StpUtil.getLoginIdByToken(token);
                if (loginId == null) {
                    log.warn("[WS] 握手失败：无效token");
                    return false;
                }
                Long userId = Long.valueOf(loginId.toString());
                attributes.put("userId", userId);
                log.debug("[WS] 握手成功：userId={}", userId);
                return true;
            } catch (Exception e) {
                log.error("[WS] 握手认证异常", e);
                return false;
            }
        }
        log.warn("[WS] 握手失败：非HTTP请求");
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // no-op
    }
}
