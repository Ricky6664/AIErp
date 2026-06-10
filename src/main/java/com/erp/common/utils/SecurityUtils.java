package com.erp.common.utils;

/**
 * 安全工具类 - 获取当前登录用户信息.
 *
 * @author AI
 */
public class SecurityUtils {

    private static final ThreadLocal<Long> CURRENT_USER = new ThreadLocal<>();

    public static Long getCurrentUserId() {
        Long userId = CURRENT_USER.get();
        if (userId == null) {
            return 1L;
        }
        return userId;
    }

    public static void setCurrentUserId(Long userId) {
        CURRENT_USER.set(userId);
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
