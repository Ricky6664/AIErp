package com.erp.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色 VO.
 *
 * @author AI
 * @since 2026-06-03
 */
@Data
public class SysRoleVO {

    @Data
    public static class ListVO {
        private Long id;

        private String roleCode;

        private String roleName;

        private String roleDesc;

        private String dataScope;

        private String dataScopeName;

        private Boolean isEnabled;

        private Integer sortOrder;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
    }

    @Data
    public static class DetailVO {
        private Long id;

        private String roleCode;

        private String roleName;

        private String roleDesc;

        private String dataScope;

        private String dataScopeName;

        private Boolean isEnabled;

        private Integer sortOrder;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;
    }
}
