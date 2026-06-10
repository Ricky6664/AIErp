package com.erp.auth.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 认证配置工作台聚合数据 VO.
 *
 * @author AI
 * @since 2026-06-04
 */
@Data
public class AuthConfigWorkbenchVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long totalAuthMethods;

    private Long enabledAuthMethods;

    private Long totalPasswordPolicies;

    private Long enabledPasswordPolicies;

    private Long onlineDeviceCount;

    private Long todayLoginSuccessCount;

    private Long todayLoginFailCount;

    private Long ssoConfigCount;

    private List<LoginMethodDistVO> loginMethodDistribution;

    private List<DailyLoginStatVO> dailyLoginStats;

    private List<RecentLoginVO> recentLogins;

    @Data
    public static class LoginMethodDistVO implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String loginMethod;

        private Long count;
    }

    @Data
    public static class DailyLoginStatVO implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String loginDate;

        private Long successCount;

        private Long failCount;
    }

    @Data
    public static class RecentLoginVO implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String username;

        private String loginTime;

        private String ip;

        private String status;
    }
}
