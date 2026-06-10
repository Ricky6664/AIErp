package com.erp.system.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 用户管理工作台聚合数据 VO.
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
public class UserWorkbenchVO {

    @JsonProperty("userTotal")
    private Long userTotal;

    @JsonProperty("roleTotal")
    private Long roleTotal;

    @JsonProperty("onlineCount")
    private Long onlineCount;

    @JsonProperty("todayLoginCount")
    private Long todayLoginCount;

    @JsonProperty("loginTrend")
    private List<LoginTrendVO> loginTrend;

    @JsonProperty("roleDistribution")
    private List<RoleDistVO> roleDistribution;

    @JsonProperty("recentLogins")
    private List<RecentLoginVO> recentLogins;

    @Data
    public static class LoginTrendVO {
        private String date;
        private Long count;
    }

    @Data
    public static class RoleDistVO {
        private String roleName;
        private Long userCount;
    }

    @Data
    public static class RecentLoginVO {
        private String username;
        private String loginTime;
        private String ip;
        private String status;
    }
}
