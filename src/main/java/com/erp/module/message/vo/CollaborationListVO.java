package com.erp.module.message.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 协作讨论列表VO.
 *
 * @author AI
 */
@Data
public class CollaborationListVO {

    private Long id;

    private String topic;

    private String category;

    private Long creatorId;

    private String creatorName;

    private Integer replyCount;

    private Boolean isClosed;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
