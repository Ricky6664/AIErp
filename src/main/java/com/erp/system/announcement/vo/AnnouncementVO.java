package com.erp.system.announcement.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告VO.
 *
 * @author AI
 */
@Data
public class AnnouncementVO {

    private Long id;

    private String title;

    private String content;

    private String announcementType;

    private LocalDateTime publishTime;

    private Boolean isTop;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String creatorName;
}
