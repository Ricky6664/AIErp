package com.erp.util;

/**
 * User-Agent解析工具类.
 *
 * @author AI
 * @since 2026-06-03
 */
public final class UserAgentUtil {

    private UserAgentUtil() {
    }

    /**
     * 从User-Agent解析浏览器名称与版本.
     *
     * @param userAgent HTTP User-Agent头
     * @return 浏览器名称与版本, 无法识别时返回 "Unknown"
     */
    public static String parseBrowser(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "Unknown";
        }
        // Edge (must check before Chrome)
        if (userAgent.contains("Edg/") || userAgent.contains("Edge/")) {
            return "Edge " + extractVersion(userAgent, "Edg/", "Edge/");
        }
        // Chrome (exclude Edge which also contains "Chrome/")
        if (userAgent.contains("Chrome/") && !userAgent.contains("Edg/")) {
            return "Chrome " + extractVersion(userAgent, "Chrome/");
        }
        // Firefox
        if (userAgent.contains("Firefox/")) {
            return "Firefox " + extractVersion(userAgent, "Firefox/");
        }
        // Safari (must check after Chrome since Chrome UA also contains "Safari/")
        if (userAgent.contains("Safari/") && !userAgent.contains("Chrome/")) {
            return "Safari " + extractVersion(userAgent, "Version/");
        }
        // Opera
        if (userAgent.contains("OPR/") || userAgent.contains("Opera/")) {
            return "Opera " + extractVersion(userAgent, "OPR/", "Opera/");
        }
        // IE
        if (userAgent.contains("MSIE ") || userAgent.contains("Trident/")) {
            String version = userAgent.contains("rv:")
                    ? extractVersion(userAgent, "rv:")
                    : extractVersion(userAgent, "MSIE ");
            return "IE " + version;
        }
        return "Unknown";
    }

    /**
     * 从User-Agent解析操作系统.
     *
     * @param userAgent HTTP User-Agent头
     * @return 操作系统名称, 无法识别时返回 "Unknown"
     */
    public static String parseOs(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "Unknown";
        }
        if (userAgent.contains("Windows NT 10.0")) {
            return "Windows 10";
        }
        if (userAgent.contains("Windows NT 6.3")) {
            return "Windows 8.1";
        }
        if (userAgent.contains("Windows NT 6.1")) {
            return "Windows 7";
        }
        if (userAgent.contains("Windows")) {
            return "Windows";
        }
        if (userAgent.contains("Mac OS X") || userAgent.contains("Macintosh")) {
            return "macOS";
        }
        if (userAgent.contains("Linux") && !userAgent.contains("Android")) {
            return "Linux";
        }
        if (userAgent.contains("Android")) {
            return "Android";
        }
        if (userAgent.contains("iPhone") || userAgent.contains("iPad") || userAgent.contains("iOS")) {
            return "iOS";
        }
        return "Unknown";
    }

    private static String extractVersion(String ua, String... prefixes) {
        for (String prefix : prefixes) {
            int idx = ua.indexOf(prefix);
            if (idx >= 0) {
                idx += prefix.length();
                int end = idx;
                while (end < ua.length()) {
                    char c = ua.charAt(end);
                    if (c == '.' || c == '_' || (c >= '0' && c <= '9')) {
                        end++;
                    } else {
                        break;
                    }
                }
                if (end > idx) {
                    return ua.substring(idx, end).replace('_', '.');
                }
            }
        }
        return "";
    }
}
