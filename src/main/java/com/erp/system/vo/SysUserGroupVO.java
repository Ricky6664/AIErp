package com.erp.system.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户组 VO.
 *
 * @author AI
 * @since 2026-06-04
 */
@Data
public class SysUserGroupVO {

    @Data
    public static class ListVO {
        private Long id;

        private String groupCode;

        private String groupName;

        private String groupDesc;

        private Long memberCount;

        private Boolean isEnabled;

        private Integer sortOrder;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;
    }

    @Data
    public static class DetailVO {
        private Long id;

        private String groupCode;

        private String groupName;

        private String groupDesc;

        private Boolean isEnabled;

        private Integer sortOrder;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime createTime;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime updateTime;
    }
}
