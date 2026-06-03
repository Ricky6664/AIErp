package com.erp.system.vo;

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

    private Long totalUsers;

    private Long onlineUsers;

    private Long newUsersThisMonth;

    private List<RoleDistVO> roleDistribution;

    private List<DeptDistVO> deptDistribution;

    @Data
    public static class RoleDistVO {
        private String roleName;
        private Long userCount;
    }

    @Data
    public static class DeptDistVO {
        private String deptName;
        private Long userCount;
    }
}
