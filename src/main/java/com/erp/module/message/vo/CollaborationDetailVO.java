package com.erp.module.message.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 协作讨论详情VO.
 *
 * @author AI
 */
@Data
public class CollaborationDetailVO {

    private Long id;

    private String topic;

    private String category;

    private String content;

    private Long creatorId;

    private String creatorName;

    private Boolean isClosed;

    private LocalDateTime closeTime;

    private LocalDateTime createTime;

    private List<ReplyTreeVO> replies;

    private List<ParticipantVO> participants;
}
