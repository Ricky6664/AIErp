package com.erp.module.message.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 楼中楼回复树形VO.
 *
 * @author AI
 */
@Data
public class ReplyTreeVO {

    private Long id;

    private Long collaborationId;

    private String replyContent;

    private Long replierId;

    private String replierName;

    private Long parentReplyId;

    private LocalDateTime createTime;

    private List<ReplyTreeVO> childReplies;
}
