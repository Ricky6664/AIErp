package com.erp.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * IP地址工具类.
 *
 * @author AI
 * @since 2026-06-03
 */
public final class IpAddressUtil {

    private static final String UNKNOWN = "unknown";

    private IpAddressUtil() {
    }

    /**
     * 获取客户端真实IP地址.
     *
     * <p>解析优先级: X-Forwarded-For &gt; X-Real-IP &gt; Proxy-Client-IP
     * &gt; WL-Proxy-Client-IP &gt; request.getRemoteAddr().
     * X-Forwarded-For 包含多级代理时取第一个非 unknown 的 IP.</p>
     *
     * @param request HTTP请求
     * @return 客户端真实IP
     */
    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (isValidIp(ip)) {
            int commaIdx = ip.indexOf(',');
            if (commaIdx > 0) {
                ip = ip.substring(0, commaIdx).trim();
            }
            return ip;
        }
        ip = request.getHeader("X-Real-IP");
        if (isValidIp(ip)) {
            return ip;
        }
        ip = request.getHeader("Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (isValidIp(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    private static boolean isValidIp(String ip) {
        return ip != null && !ip.isBlank() && !UNKNOWN.equalsIgnoreCase(ip);
    }
}
