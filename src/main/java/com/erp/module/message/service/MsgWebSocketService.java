package com.erp.module.message.service;

/**
 * WebSocket消息推送Service接口.
 *
 * @author AI
 */
public interface MsgWebSocketService {

    void pushToUser(Long userId, String message);

    void pushToRole(String roleCode, String message);

    void broadcastAll(String message);
}
