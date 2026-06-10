package com.erp.module.message.config;

import com.erp.module.message.websocket.MsgWebSocketHandler;
import com.erp.module.message.websocket.WebSocketAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket配置类 — 注册WebSocket端点、认证拦截器.
 *
 * @author AI
 */
@Configuration
@EnableWebSocket
@EnableScheduling
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final MsgWebSocketHandler msgWebSocketHandler;
    private final WebSocketAuthInterceptor authInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(msgWebSocketHandler, "/api/msg/ws/connect")
                .addInterceptors(authInterceptor)
                .setAllowedOrigins("*");
    }
}
