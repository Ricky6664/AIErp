package com.erp.module.message.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 单据沟通列表VO.
 *
 * @author AI
 */
@Data
public class DiscussionListVO {

    private Long id;

    private String businessType;

    private Long businessId;

    private Long senderId;

    private String senderName;

    private String senderAvatar;

    private String content;

    private Long parentId;

    private String attachment;

    private LocalDateTime createTime;
}
