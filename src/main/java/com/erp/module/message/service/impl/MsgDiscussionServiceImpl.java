package com.erp.module.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.result.PageResult;
import com.erp.common.utils.SecurityUtils;
import com.erp.module.message.dto.DiscussionCreateDTO;
import com.erp.module.message.dto.DiscussionQueryDTO;
import com.erp.module.message.dto.MsgMessageCreateDTO;
import com.erp.module.message.entity.MsgDiscussionEntity;
import com.erp.module.message.mapper.MsgDiscussionMapper;
import com.erp.module.message.service.IMsgMessageService;
import com.erp.module.message.service.MsgDiscussionService;
import com.erp.module.message.service.MsgWebSocketService;
import com.erp.module.message.util.MentionParser;
import com.erp.module.message.vo.DiscussionListVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 单据沟通Service实现.
 *
 * @author AI
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MsgDiscussionServiceImpl implements MsgDiscussionService {

    private final MsgDiscussionMapper discussionMapper;
    private final IMsgMessageService messageService;
    private final MsgWebSocketService webSocketService;
    private final MentionParser mentionParser;

    @Override
    public PageResult<DiscussionListVO> pageByDoc(DiscussionQueryDTO query) {
        LambdaQueryWrapper<MsgDiscussionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MsgDiscussionEntity::getBusinessType, query.getBusinessType());
        wrapper.eq(MsgDiscussionEntity::getBusinessId, query.getBusinessId());
        wrapper.orderByAsc(MsgDiscussionEntity::getCreateTime);

        int pageNum = query.getPageNum() != null ? query.getPageNum() : 1;
        int pageSize = query.getPageSize() != null ? query.getPageSize() : 20;
        IPage<MsgDiscussionEntity> page = discussionMapper.selectPage(
                new Page<>(pageNum, pageSize), wrapper);

        List<DiscussionListVO> records = page.getRecords().stream()
                .map(this::toVO)
                .collect(Collectors.toList());

        return PageResult.of(records, page.getTotal(), pageNum, pageSize);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(DiscussionCreateDTO dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();

        MsgDiscussionEntity entity = new MsgDiscussionEntity();
        entity.setBusinessType(dto.getBusinessType());
        entity.setBusinessId(dto.getBusinessId());
        entity.setSenderId(currentUserId);
        entity.setContent(dto.getContent());
        entity.setParentId(dto.getParentId() != null ? dto.getParentId() : 0L);
        entity.setAttachment(dto.getAttachment());
        discussionMapper.insert(entity);

        Set<Long> notifiedUsers = new HashSet<>();

        List<Long> mentionedUserIds = mentionParser.parseMentionedUsers(dto.getContent());
        for (Long mentionedUserId : mentionedUserIds) {
            if (!mentionedUserId.equals(currentUserId)) {
                sendNotification("有人在单据沟通中@了您",
                        buildMentionContent(dto), mentionedUserId);
                webSocketService.pushToUser(mentionedUserId,
                        buildPushPayload(entity, "mention"));
                notifiedUsers.add(mentionedUserId);
            }
        }

        if (entity.getParentId() != null && entity.getParentId() > 0) {
            MsgDiscussionEntity parentMsg = discussionMapper.selectById(entity.getParentId());
            if (parentMsg != null
                    && !parentMsg.getSenderId().equals(currentUserId)
                    && !notifiedUsers.contains(parentMsg.getSenderId())) {
                sendNotification("有人回复了您的单据沟通",
                        buildReplyContent(dto), parentMsg.getSenderId());
                webSocketService.pushToUser(parentMsg.getSenderId(),
                        buildPushPayload(entity, "reply"));
            }
        }

        log.info("[单据沟通] 用户{}在{}({})新增留言, id={}",
                currentUserId, dto.getBusinessType(), dto.getBusinessId(), entity.getId());
        return entity.getId();
    }

    @Override
    public List<DiscussionListVO> getReplies(Long parentId) {
        LambdaQueryWrapper<MsgDiscussionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MsgDiscussionEntity::getParentId, parentId);
        wrapper.orderByAsc(MsgDiscussionEntity::getCreateTime);
        List<MsgDiscussionEntity> replies = discussionMapper.selectList(wrapper);
        return replies.stream().map(this::toVO).collect(Collectors.toList());
    }

    private void sendNotification(String title, String content, Long receiverId) {
        MsgMessageCreateDTO msgDto = new MsgMessageCreateDTO();
        msgDto.setMessageTitle(title);
        msgDto.setMessageContent(content);
        msgDto.setReceiverId(receiverId);
        msgDto.setSourceType("discussion");
        messageService.create(msgDto);
    }

    private String buildMentionContent(DiscussionCreateDTO dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return "用户" + currentUserId + "在单据沟通中@了您：" + dto.getContent();
    }

    private String buildReplyContent(DiscussionCreateDTO dto) {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        return "用户" + currentUserId + "回复了您的留言：" + dto.getContent();
    }

    private String buildPushPayload(MsgDiscussionEntity entity, String action) {
        return "{\"action\":\"" + action + "\",\"discussionId\":" + entity.getId()
                + ",\"businessType\":\"" + entity.getBusinessType() + "\""
                + ",\"businessId\":" + entity.getBusinessId() + "}";
    }

    private DiscussionListVO toVO(MsgDiscussionEntity entity) {
        DiscussionListVO vo = new DiscussionListVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
